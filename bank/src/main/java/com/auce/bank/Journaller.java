package com.auce.bank;

import static com.auce.bank.util.Constants.DEFAULT_DATE_FORMAT;

import static com.auce.bank.util.Constants.DEFAULT_FIELD_SEPARATOR;

import java.io.BufferedWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.bank.util.Mapper;
import com.auce.bank.util.Observer;
import com.auce.bank.util.Utilities;

public class Journaller implements Observer<Instruction>, Mapper<Instruction>
{
	final static protected Logger LOGGER = LoggerFactory.getLogger( Journaller.class );

	final private BufferedWriter	writer;
	
	public Journaller( final Writer writer )
	{
		this.writer = new BufferedWriter( writer );
	}
	
	public void addEvent( final Instruction instruction )
	{
		if ( instruction != null )
		{
			try
			{
				this.writer.write( this.mapToString( instruction ) );
				this.writer.newLine();
				this.writer.flush();
			}
			catch( Exception e )
			{
				LOGGER.error( 
					"error writing " + instruction.getClass().getSimpleName() +
					": " + e.getClass().getSimpleName() +
					( e.getMessage() != null ? ", " + e.getMessage() : "" ) );
			}
		}
	}

	public Instruction mapFromString ( final String string )
	{
		return null;
	}

	public String mapToString ( final Instruction i )
	{
		StringBuilder sb = new StringBuilder();

		/* Field 1: Date */
		sb.append( DEFAULT_DATE_FORMAT.format( i.getDate() ) );

		/* Field2: Instruction Type */
		sb.append( DEFAULT_FIELD_SEPARATOR );
		sb.append( i.getType().toString() );
				
		if ( i instanceof Cancellation )
		{
			Cancellation c = (Cancellation)i;
			
			sb.append( DEFAULT_FIELD_SEPARATOR );
			sb.append( c.getAccountNumber() );
		}
		else if ( i instanceof Opening )
		{
			Opening o = (Opening)i;
			
			sb.append( DEFAULT_FIELD_SEPARATOR );
			sb.append( o.getAccountType().toString() );
			sb.append( DEFAULT_FIELD_SEPARATOR );
			sb.append( o.getAccountNumber() );
			sb.append( DEFAULT_FIELD_SEPARATOR );
			sb.append( Utilities.toQuotedString( o.getNameOfHolder() ) );
			sb.append( DEFAULT_FIELD_SEPARATOR );
			sb.append( Utilities.toQuotedString( o.getCityOfHolder() ) );
			sb.append( DEFAULT_FIELD_SEPARATOR );
			sb.append( Long.toString( o.getBalance() ) );
		}
		else if ( i instanceof Payment )
		{
			Payment p = (Payment)i;
			
			sb.append( DEFAULT_FIELD_SEPARATOR );
			sb.append( p.getDebitAccountNumber() );
			sb.append( DEFAULT_FIELD_SEPARATOR );
			sb.append( p.getCreditAccountNumber() );
			sb.append( DEFAULT_FIELD_SEPARATOR );
			sb.append( Utilities.toQuotedString( p.getDescription() ) );
			sb.append( DEFAULT_FIELD_SEPARATOR );
			sb.append( Long.toString( p.getAmount() ) );
		}
		
		/* Last field: status */
		
		sb.append( DEFAULT_FIELD_SEPARATOR );
		sb.append( i.getStatus().toString() );
		
		return sb.toString();
	}
}
