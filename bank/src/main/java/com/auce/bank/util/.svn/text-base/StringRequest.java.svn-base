package com.auce.bank.util;

import java.net.InetAddress;


public class StringRequest implements Request<String>
{
	private InetAddress		inetAddress;
	private String			entity;
	
	public StringRequest( InetAddress inetAddress, String entity )
	{
		this.inetAddress = inetAddress;

		this.entity = entity;
	}

	public InetAddress getInetAddress ()
	{
		return this.inetAddress;
	}

	public String getEntity ()
	{
		return this.entity;
	}
	
	@Override
	public String toString ()
	{
		StringBuilder sb = new StringBuilder( this.getClass().getSimpleName() );
		
		sb.append( " { " );
		sb.append( "inetAddress=" + this.inetAddress + ", " );
		sb.append( "entity=" + this.entity );
		sb.append( " }" );
		
		return sb.toString();
	}
}
