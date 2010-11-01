package com.auce.util;

import java.util.HashMap;

public class Sequence
{
	final static protected Sequence SINGLETON = new Sequence();
	
	protected HashMap<String,Integer>	data;
	protected Boolean					lock;
	
	public Sequence()
	{
		this.data = new HashMap<String,Integer>();
		
		this.lock = Boolean.TRUE;
	}
	
	public String next( String prefix )
	{
		String result = null;
		
		synchronized( this.lock )
		{
			StringBuilder sb = new StringBuilder( prefix );

			Integer current = this.data.get( prefix );
			
			if ( current == null )
			{
				current = 1;
			}
			else
			{
				current += 1;
			}
			
			this.data.put( prefix, current );
			
			sb.append( Integer.toString( current ) );
			
			result = sb.toString();

			this.lock.notify();
		}
		
		return result;
	}
	
	static public Sequence getInstance()
	{
		return SINGLETON;
	}
}
