
package com.auce.auction.entity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.auce.auction.event.Run;
import com.auce.util.Identity;
import com.auce.util.SystemException;

public class Clock extends Entity implements Identity
{
	final static public int MAX_VALUE = 100;
	final static public int MIN_VALUE = 0;

	protected String		id;
	protected String		group;
	protected int			port;
	protected InetAddress	inetAddress;
	protected Queue<Run>	runs;

	public Clock( String id )
	{
		this( id, null, -1 );
	}

	public Clock( String id, String group, int port )
	{
		this.id = id;
		this.group = group;
		this.port = port;
		this.runs = new ConcurrentLinkedQueue<Run>();
	}

	public String getId ()
	{
		return id;
	}

	public String getGroup ()
	{
		return this.group;
	}

	public InetAddress resolveInetAddress ()
	{
		InetAddress result = null;

		try
		{
			result = InetAddress.getByName( group );
		}
		catch ( UnknownHostException e )
		{
			throw new SystemException( e );
		}

		if ( result != null )
		{
			if ( result.isMulticastAddress() == false )
			{
				throw new IllegalStateException( result + " is not a multicastAddress" );
			}
		}

		return result;
	}

	public int getPort ()
	{
		return port;
	}

	public Run[] listRuns ()
	{
		return (Run[])this.runs.toArray( new Run[this.runs.size()] );
	}

	public void removeAllRuns ()
	{
		this.runs.clear();
	}

	public Run pollRun ()
	{
		return this.runs.poll();
	}

	public void addRun ( Run run )
	{
		this.runs.add( run );
	}

	public int getRunCount ()
	{
		return this.runs.size();
	}
}
