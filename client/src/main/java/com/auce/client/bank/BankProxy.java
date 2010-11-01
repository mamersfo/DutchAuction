package com.auce.client.bank;

import static com.auce.util.Constants.LINE_SEPARATOR;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.auce.bank.Account;
import com.auce.bank.AccountType;
import com.auce.bank.CheckingAccount;
import com.auce.bank.Opening;
import com.auce.bank.Payment;
import com.auce.bank.Statement;
import com.auce.bank.Status;
import com.auce.util.SocketClient;
import com.auce.util.component.ComponentSupport;

public class BankProxy extends ComponentSupport implements Bank 
{
	private SocketClient		socketClient;
	private long				balance;
	private List<BankListener>	listeners;
	private Account				account;
	
	public BankProxy()
	{
		super( "proxy" );
		
		this.socketClient = new SocketClient(
			System.getProperty( "bank.host" ),
			Integer.parseInt( System.getProperty( "bank.port" ) ),
			Integer.parseInt( System.getProperty( "bank.timeout" ) )
		);
		
		super.setPeriod( Integer.parseInt( System.getProperty( "bank.period" ) ) );
		
		this.listeners = new ArrayList<BankListener>();
	}
	
	public void addBankListener( BankListener listener )
	{
		this.listeners.add( listener );
	}
	
	public void removeBankListener( BankListener listener )
	{
		this.listeners.remove( listener );
	}
	
	public long getBalance()
	{
		return this.balance;
	}
	
	public void getStatement()
	{
		if ( this.account == null )
		{
			logger.warn( "getStatement() - no account" );
			
			return;
		}
		
		final Statement statement = new Statement( 
			this.account.getAccountNumber() );
		
		BankInstructionRunner runner = new BankInstructionRunner(
			statement, this.socketClient, new SocketClient.Callback() 
			{
				public void handleResponse ( String request, String response )
				{
					String[] items = response.split( LINE_SEPARATOR );
					
					SocketStatus socketStatus = SocketStatus.INTERNAL_SERVER_ERROR;
						
					try
					{
						socketStatus = SocketStatus.valueOf( items[0] );
					}
					catch( IllegalArgumentException e )
					{
						logger.warn( "getStatement() - error parsing response: '{}'", response );
						logger.warn( e.getMessage(), e );
					}

					if ( SocketStatus.OK == socketStatus )
					{
						statement.setStatus( Status.EXECUTED );
						
						List<Mutation> mutations = new LinkedList<Mutation>();

						Mutation mutation = null;
						
						for ( int i=1; i < items.length; i++ )
						{
							String line = items[i];
							
							String time = line.substring( 0, 10 ).trim();
							String text = line.substring( 10, 40 ).trim();
							String debit = line.substring( 40, 50 ).trim();
							String credit = line.substring( 50, 60 ).trim();

							if ( time.length() > 0 )
							{
								double value = 0;
								
								if ( "Balance".equalsIgnoreCase( text ) )
								{
									if ( debit.length() > 0 )
									{
										// should not happen
										value = Double.parseDouble( debit );
										balance = (long)( value * -100 );
									}
									else if ( credit.length() > 0 )
									{
										value = Double.parseDouble( credit );
										balance = (long)( value * 100 );
									}
								}
								else
								{
									if ( debit.length() > 0 )
									{
										// withdrawal
										mutation = new Withdrawal();
										
										value = Double.parseDouble( debit );
									}
									else if ( credit.length() > 0 )
									{
										// deposit
										mutation = new Deposit();

										value = Double.parseDouble( credit );
									}
								
									mutation.setTime( time );
									mutation.setAccountNumber( text );
									mutation.setAmount( (long)( value * 100 ) );
									mutations.add( mutation );
								}
							}
							else // info line
							{
								if ( mutation.getNameOfHolder() == null )
								{
									mutation.setNameOfHolder( text );
								}
								else
								{
									mutation.setDescription( text );
								}
							}
						}
						
						for ( Mutation m : mutations )
						{
							for ( BankListener listener : listeners )
							{
								listener.handleMutation( m );
							}							
						}
					}
					else if ( SocketStatus.NOT_FOUND == socketStatus )
					{
						statement.setStatus( Status.NOT_FOUND );
					}
				}
			});
		
		runner.start();		
	}
	
	public void openAccount( String nameOfHolder, String cityOfHolder )
	{
		if ( this.account != null )
		{
			throw new IllegalStateException(
				"openAccount() - account already exists" );
		}
		
		final Opening opening = new Opening(
			AccountType.CHECKING,
			null,
			nameOfHolder,
			cityOfHolder
		);
		
		BankInstructionRunner runner = new BankInstructionRunner(
			opening, this.socketClient, new SocketClient.Callback() 
			{
				public void handleResponse ( String request, String response )
				{
					String[] items = response.split( LINE_SEPARATOR );
					
					SocketStatus socketStatus = SocketStatus.valueOf( items[0] );
					
					if ( SocketStatus.CREATED == socketStatus )
					{
						opening.setStatus( Status.EXECUTED );
						
						CheckingAccount checkingAccount = 
							new CheckingAccount( items[1] );
							
						checkingAccount.setNameOfHolder( opening.getNameOfHolder() );
						
						checkingAccount.setCityOfHolder( opening.getCityOfHolder() );
						
						account = checkingAccount;
					}
					else if ( SocketStatus.FORBIDDEN == socketStatus )
					{
						opening.setStatus( Status.valueOf( items[1] ) );
					}
										
					for ( BankListener listener : listeners )
					{
						listener.handleAccount( account );
					}
				}
			});
		
		runner.start();
	}

	public void doPayment( String accountNumber, String description, long amount )
	{
		if ( this.account == null )
		{
			throw new IllegalStateException(
				"doPayment() - no account" );
		}
			
		final Payment payment = new Payment(
			accountNumber,
			this.account.getAccountNumber(),
			description,
			amount );

		BankInstructionRunner runner = new BankInstructionRunner(
			payment, this.socketClient, new SocketClient.Callback() 
			{
				public void handleResponse ( String request, String response )
				{
					payment.setDate( new Date() );

					String[] items = response.split( "\n" );
					
					SocketStatus socketStatus = SocketStatus.valueOf( items[0] );
					
					if ( SocketStatus.NO_CONTENT == socketStatus )
					{
						payment.setStatus( Status.EXECUTED );
					}
					else if ( SocketStatus.FORBIDDEN == socketStatus )
					{
						payment.setStatus( Status.valueOf( items[1] ) );
					}

					for ( BankListener listener : listeners )
					{
						listener.handlePayment( payment );
					}
				}
			});
		
		runner.start();
	}

	@Override
	protected void poll ()
	{
		this.getStatement();
	}
	
	public enum SocketStatus
	{
		OK (200),	
		CREATED (201),
		ACCEPTED (202),
		NO_CONTENT (204),
		BAD_REQUEST (400),
		UNAUTHORIZED (401),
		FORBIDDEN (403),
		NOT_FOUND( 404),
		CONFLICT (409),
		INTERNAL_SERVER_ERROR (500),
		NOT_IMPLEMENTED (501),
		SERVICE_UNAVAILABLE (503);
		
		private int	code;
		
		private SocketStatus( int code )
		{
			this.code = code;
		}
		
		public int getCode()
		{
			return this.code;
		}
	}

	@Override
	protected void afterRunning() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeRunning() {
		// TODO Auto-generated method stub
		
	}	
}
