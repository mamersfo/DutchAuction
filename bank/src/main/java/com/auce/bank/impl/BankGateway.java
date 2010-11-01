package com.auce.bank.impl;

import static com.auce.bank.util.Constants.DEFAULT_FIELD_SEPARATOR;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.bank.AccountType;
import com.auce.bank.Bank;
import com.auce.bank.BankFacade;
import com.auce.bank.Cancellation;
import com.auce.bank.Instruction;
import com.auce.bank.Opening;
import com.auce.bank.Payment;
import com.auce.bank.Statement;
import com.auce.bank.Status;
import com.auce.bank.Event.Type;
import com.auce.bank.util.Gateway;
import com.auce.bank.util.Mapper;
import com.auce.bank.util.Request;
import com.auce.bank.util.Response;
import com.auce.bank.util.StatementWriter;
import com.auce.bank.util.StringResponse;
import com.auce.bank.util.Utilities;

public class BankGateway implements Gateway<String>, Mapper<Instruction>
{
	final static private Logger LOGGER = LoggerFactory.getLogger( BankGateway.class );
	
	private BankFacade				bank;
	private Pattern					pattern;
	private Map<String,InetAddress>	addresses;
	
	public BankGateway( BankFacade bank )
	{
		this.bank = bank;
		
		this.pattern = Pattern.compile(
			"^" +
			"(CANCELLATION,[1-9][0-9]{8})|" +
			"(OPENING,(CHECKING|SAVINGS),\"[^,]*\",\"[^,]*\")|" +
			"(PAYMENT,[1-9][0-9]{8},[1-9][0-9]{8},\"[^,]*\",\\d+)|" +
			"(STATEMENT,[1-9][0-9]{8})" +
			"$"
		);
		
		this.addresses = new HashMap<String,InetAddress>();
	}
	
	public boolean isValidRequest( Request<String> request )
	{
		boolean result = false;
	
		if ( request != null && request.getInetAddress() != null )
		{
			if ( request.getEntity() != null )
			{
				Matcher matcher = this.pattern.matcher( request.getEntity() );
				
				result = matcher.matches();
			}
		}
		
		return result;
	}
	
	public Response<String> handleRequest( Request<String> request )
	{
		StringResponse response = new StringResponse();
		
		LOGGER.debug( ">> REQUEST from {}: {}", request.getInetAddress(), request.getEntity() );
		
		if ( this.isValidRequest( request ) == false )
		{
			response.setStatus( Response.Status.BAD_REQUEST );
		}
		else
		{
			Instruction instruction = this.mapFromString( request.getEntity() );
			
			if ( instruction == null )
			{
				response.setStatus( Response.Status.BAD_REQUEST );
			}
			else
			{
				if ( instruction instanceof Opening )
				{
					Opening opening = (Opening)instruction;
					
					this.bank.processOpening( opening );
					
					if ( Status.EXECUTED == opening.getStatus() )
					{
						response.setStatus( Response.Status.CREATED );
						
						String accountNumber = opening.getAccountNumber();
						
						response.append( accountNumber );
						
						this.addresses.put( accountNumber, request.getInetAddress() );
					}
					else
					{
						response.setStatus( Response.Status.FORBIDDEN );
						
						response.append( opening.getStatus().toString() );
					}
				}
				else
				{
					InetAddress expectedInetAddress = 
						this.addresses.get( instruction.getAccountNumber() );
					
					if ( expectedInetAddress == null ||
						 expectedInetAddress.equals( request.getInetAddress() ) == false )
					{
						LOGGER.info(
							"Not authenticated: {}, instruction: {}",
							request.getInetAddress(),
							request.getEntity() );
						
						response.setStatus( Response.Status.FORBIDDEN );
					}
					else
					{
						if ( instruction instanceof Cancellation )
						{
							Cancellation cancellation = (Cancellation)instruction;
							
							this.bank.processCancellation( cancellation );
							
							if ( Status.EXECUTED == cancellation.getStatus() )
							{
								response.setStatus( Response.Status.NO_CONTENT );
								
								String accountNumber = cancellation.getAccountNumber();
								
								this.addresses.remove( accountNumber );
							}
							else
							{
								response.setStatus( Response.Status.CONFLICT );
							}
						}						
						else if ( instruction instanceof Payment )
						{
							Payment payment = (Payment)instruction;
							
							this.bank.processPayment( payment );
							
							if ( Status.EXECUTED == payment.getStatus() )
							{
								response.setStatus( Response.Status.NO_CONTENT );
							}
							else
							{
								response.setStatus( Response.Status.FORBIDDEN );
								response.append( payment.getStatus().toString() );
							}
						}
						else if ( instruction instanceof Statement )
						{
							Statement statement = (Statement)instruction;
							
							this.bank.processStatement( statement );
							
							if ( Status.EXECUTED == statement.getStatus() )
							{
								response.setStatus( Response.Status.OK );
								
								StatementWriter writer = new FixedStatementWriter();
								
								response.append( writer.write( statement ) );
							}
							else if ( Status.NOT_FOUND == statement.getStatus() )
							{
								response.setStatus( Response.Status.NOT_FOUND );
							}
						}
						else
						{
							response.setStatus( Response.Status.NOT_IMPLEMENTED );
						}
					}
				}
			}
		}
		
		LOGGER.debug( "<< RESPONSE: {}", response.getStatus() );
		LOGGER.debug( "<< RESPONSE: {}", response.getBody() );

		return response;
	}

	public Bank getBank ()
	{
		return this.bank;
	}

	public Instruction mapFromString ( String input )
	{
		Instruction result = null;
		
		if ( input != null && input.length() > 0 && 
			 input.contains( DEFAULT_FIELD_SEPARATOR ) )
		{
			String[] items = input.split( DEFAULT_FIELD_SEPARATOR );
			
			if ( items.length >= 1 )
			{
				Type type = null;
				
				try
				{
					type = Type.valueOf( items[0] );
				}
				catch( IllegalArgumentException e )
				{
					LOGGER.error( "Type not found: {}", items[0] );
				}
				
				if ( Type.CANCELLATION == type )
				{
					Cancellation c = new Cancellation();
					c.setAccountNumber( items[1] );	
					result = c;
				}
				else if ( Type.OPENING == type )
				{
					Opening o = new Opening();
					
					for ( int i=1; i < items.length; i++ )
					{
						switch( i )
						{
							case 1:
								o.setAccountType( AccountType.valueOf( items[i] ) );
								break;
							case 2:
								o.setNameOfHolder( Utilities.toUnquotedString( items[i] ) );
								break;
							case 3:
								o.setCityOfHolder( Utilities.toUnquotedString( items[i] ) );
								break;
							default:
								break;
						}
					}

					result = o;
				}
				else if ( Type.PAYMENT == type )
				{
					Payment p = new Payment();

					for ( int i=1; i < items.length; i++ )
					{
						switch( i )
						{
							case 1:
								p.setDebitAccountNumber( items[i] );
								break;
							case 2:
								p.setCreditAccountNumber( items[i] );
								break;
							case 3:
								p.setDescription( Utilities.toUnquotedString( items[i] ) );
								break;
							case 4:
								p.setAmount( Long.parseLong( items[i] ) );
								break;
						}
					}
					
					result = p;
				}
				else if ( Type.STATEMENT == type )
				{
					Statement s = new Statement();
					s.setAccountNumber( items[1] );
					result = s;
				}
			}
		}
		
		return result;
	}

	public String mapToString ( Instruction i )
	{
		String result = null;
		
		if ( i instanceof Cancellation )
		{
			result = String.format( "%1$s,%2$s", 
				i.getType().toString(),
				((Cancellation)i).getAccountNumber()
			);
		}
		else if ( i instanceof Opening )
		{
			Opening o = (Opening)i;
			
			result = String.format( "%1$s,%2$s,\"%3$s\",\"%4$s\"",
				o.getType().toString(),
				o.getAccountType(),
				o.getNameOfHolder(),
				o.getCityOfHolder()
			);
		}
		else if ( i instanceof Payment )
		{
			Payment p = (Payment)i;
			
			result = String.format( "%1$s,%2$s,%3$s,\"%4$s\",%5$d",
				p.getType().toString(),
				p.getDebitAccountNumber(),
				p.getCreditAccountNumber(),
				p.getDescription(),
				p.getAmount()
			);
		}
		else if ( i instanceof Statement )
		{
			Statement s = (Statement)i;
			
			result = String.format( "%1$s,%2$s",
				s.getType().toString(),
				s.getAccountNumber()
			);
		}

		return result;
	}
}
