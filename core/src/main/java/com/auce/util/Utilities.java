package com.auce.util;

import java.io.IOException;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.bank.rest.BankApplication;

public class Utilities
{
	final static protected Logger LOGGER = LoggerFactory.getLogger( Utilities.class );

	static private Calendar today = null;
	
	static public Date today()
	{
		if ( today == null )
		{
			today = Calendar.getInstance();
			
			today.set( Calendar.AM_PM, Calendar.AM );
			today.set( Calendar.HOUR, 0 );
			today.set( Calendar.MINUTE, 0 );
			today.set( Calendar.SECOND, 0 );
			today.set( Calendar.MILLISECOND, 0 );
		}
		
		return today.getTime();
	}
	
	static public void loadProperties ( String filename )
	{
		InputStream is = null;

		try
		{
			ClassLoader classLoader = BankApplication.class.getClassLoader();
			LOGGER.debug( "loading properties from {}", classLoader.getResource( filename ) );
			is = classLoader.getResourceAsStream( filename );
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
}
