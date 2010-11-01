package com.auce.bank.util;

import static com.auce.bank.util.Constants.DOT;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.auce.bank.rest.BankApplication;

public class Utilities
{
	final static public int ACCOUNT_NUMBER_LENGTH = 9;	

	static public long generateLong( int numDigits )
	{
		String result = 
			Integer.toString( 
				(int)(Math.round( Math.random() * 8 ) + 1 ) );

		while ( result.length() < numDigits )
		{
			result += Integer.toString( (int)(Math.round( Math.random() * 9 ) ) );
		}
		
		return Long.parseLong( result );
	}

	static public boolean isValidAccountNumber( String number )
	{
		boolean result = false;
		
		if ( number != null && 
			 number.length() == ACCOUNT_NUMBER_LENGTH && 
			 number.startsWith( "0" ) == false )
		{
			int sum = 0;
			
			for ( int i=0; i < ACCOUNT_NUMBER_LENGTH; i++ )
			{
				char c = number.charAt( i );
				
				if ( Character.isDigit( c ) )
				{
					int next = Integer.parseInt( c + "" );
					
					sum += ( next * ( ACCOUNT_NUMBER_LENGTH - i ) );
				}
				else
				{
					sum = 0;
					
					break;
				}
			}
			
			if ( sum > 0 ) result = ( sum % 11 == 0 );
		}
		
		return result;
	}			
	
	static public String randomAccountNumber()
	{
		long number = Utilities.generateLong( ACCOUNT_NUMBER_LENGTH );
		
		String result = Long.toString( number );
		
		while ( Utilities.isValidAccountNumber( result ) == false )
		{
			result = Long.toString( ++number );
		}
		
		return result;
	}
	
	static public String toUnquotedString( String string )
	{
		String result = string;
		
		if ( string.startsWith( Constants.DOUBLE_QUOTE ) &&
			 string.endsWith( Constants.DOUBLE_QUOTE ) )
		{
			result = string.substring( 1, string.length() - 1 );
		}
		
		return result;
	}
	
	static public String toQuotedString( String string )
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( Constants.DOUBLE_QUOTE );
		sb.append( string );
		sb.append( Constants.DOUBLE_QUOTE );
		
		return sb.toString();
	}	
	
	static public String toDottedAccountNumber( String accountNumber )
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( accountNumber.substring( 0, 2 ) );
		sb.append( DOT );
		sb.append( accountNumber.substring( 2, 4 ) );
		sb.append( DOT );
		sb.append( accountNumber.substring( 4, 6 ) );
		sb.append( DOT );
		sb.append( accountNumber.substring( 6, 9 ) );
		
		return sb.toString();
	}
	
	static public void loadProperties ( String filename )
	{
		InputStream is = null;

		try
		{
			is = BankApplication.class.getClassLoader().getResourceAsStream( filename );
			Properties properties = new Properties( System.getProperties() );
			properties.load( is );
			System.setProperties( properties );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			if ( is != null )
			{
				try
				{
					is.close();
				}
				catch ( IOException e )
				{
				}
			}
		}
	}	
}
