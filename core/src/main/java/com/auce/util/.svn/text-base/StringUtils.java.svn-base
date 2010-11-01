package com.auce.util;

import java.lang.reflect.Method;

import com.auce.auction.entity.Entity;

public class StringUtils
{
	final static private Object[] NOARGS = new Object[0];
	
	static public String toXml( Object target )
	{
		if ( target == null ) 
		{
			throw new IllegalArgumentException( "expected target argument" );
		}

		StringBuilder sb = new StringBuilder();
		
		appendToXml( target, sb );
		
		return sb.toString();
	}
	
	static public void appendToXml ( Object target, StringBuilder sb )
	{
		if ( target == null ) return;
		
		String entityName = target.getClass().getSimpleName();
		
		sb.append( "<" );
		sb.append( entityName );
		
		if ( target instanceof Identity )
		{
			sb.append( " Id=\"" );
			sb.append( ((Identity)target).getId() );
			sb.append( "\"" );
		}
		
		sb.append( ">" );
		
		Method[] methods = target.getClass().getDeclaredMethods();
		
		for ( int i=0; i < methods.length; i++ )
		{
			String name = methods[i].getName();
			
			if ( name.startsWith( "get" ) )
			{
				name = name.substring( 3 );
				
				if ( name.equals( "Class" ) ) continue;
				if ( name.equals( "Id" ) ) continue;
				
				try
				{
					Object value = methods[i].invoke( target, NOARGS );
					
					if ( value != null )
					{
						if ( value instanceof Entity )
						{
							appendToXml( ((Entity)value), sb );
						}
						else
						{
							sb.append( "<" );
							sb.append( name );
							sb.append( ">" );
							sb.append( value );
							sb.append( "</" );
							sb.append( name );
							sb.append( ">" );
						}
					}
					else
					{
						sb.append( "<" );
						sb.append( name );
						sb.append( "/>" );
					}
				}
				catch( Exception e )
				{
				}
			}
		}		
		
		sb.append( "</" );
		sb.append( entityName );
		sb.append( ">" );
	}	

	static public String toString( Object target )
	{
		if ( target == null ) 
		{
			throw new IllegalArgumentException( "expected target argument" );
		}
		
		StringBuilder sb = new StringBuilder();
		
		Method[] methods = target.getClass().getDeclaredMethods();
		
		for ( int i=0; i < methods.length; i++ )
		{
			String name = methods[i].getName();
			
			if ( name.startsWith( "get" ) )
			{
				if ( name.equals( "getClass" ) ) continue;
				if ( name.equals( "getId" ) ) continue;
				
				try
				{
					Object value = methods[i].invoke( target, NOARGS );
					
					if ( sb.length() == 0 )
					{
						sb.append( target.getClass().getSimpleName() );
						
						if ( target instanceof Identity )
						{
							sb.append( "[" );
							sb.append( ((Identity)target).getId() );
							sb.append( "]" );
						}
						
						sb.append( " { " );
					}
					else
					{
						sb.append( ", " );
					}
					
					sb.append( name.substring( 3 ) );
					sb.append( "=" );
					sb.append( value );
				}
				catch( Exception e )
				{
				}
			}
		}
		
		sb.append( " }" );
				
		return sb.toString();
	}
}
