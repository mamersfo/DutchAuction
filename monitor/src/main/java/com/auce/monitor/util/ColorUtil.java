package com.auce.monitor.util;

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.util.SystemException;

public class ColorUtil
{
	final static protected Logger LOGGER = LoggerFactory.getLogger( ColorUtil.class );
	
	static public String toString( Color color )
	{
		StringBuilder sb = new StringBuilder();
		
		String s = Integer.toString( color.getRed() );
		if ( s.length() == 1 ) sb.append( "0" );
		sb.append( s );
		
		s = Integer.toString( color.getGreen() );
		if ( s.length() == 1 ) sb.append( "0" );
		sb.append( s );

		s = Integer.toString( color.getBlue() );
		if ( s.length() == 1 ) sb.append( "0" );
		sb.append( s );

		return sb.toString();
	}
	
	static public Color fromString( String string )
	{
		Color result = null;
		
		try
		{
			String r = string.substring( 0, 2 );
			String g = string.substring( 2, 4 );
			String b = string.substring( 4, 6 );
			
			result = new Color( 
				Integer.parseInt( r, 16 ),
				Integer.parseInt( g, 16 ),
				Integer.parseInt( b, 16 )
			);
		}
		catch( Exception e )
		{
			LOGGER.error( string + ": " + e.getMessage() );
			
			throw new SystemException( e );
		}
		
		return result;
	}
}
