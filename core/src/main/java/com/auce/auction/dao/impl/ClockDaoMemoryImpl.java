package com.auce.auction.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.auce.auction.dao.ClockDao;
import com.auce.auction.entity.Clock;
import com.auce.auction.entity.EntityExistsException;
import com.auce.auction.entity.EntityNotFoundException;
import com.auce.auction.entity.IllegalEntityException;

public class ClockDaoMemoryImpl implements ClockDao
{
	protected Map<String,Clock>		clocks;
	
	public ClockDaoMemoryImpl()
	{
		this.clocks = new HashMap<String,Clock>();
	}
	
	public int count()
	{
		return this.clocks.size();
	}
	
	public Clock[] list()
	{
		Collection<Clock> collection = this.clocks.values();
		return (Clock[])collection.toArray( new Clock[ collection.size() ] );
	}
	
	public Clock find( String id )
	{
		Clock result = this.clocks.get( id );
		if ( result == null ) throw new EntityNotFoundException( Clock.class, id );
		return result;
	}
	
	public void add( Clock clock )
	{
		if ( clock == null || clock.getId() == null )
		{
			throw new IllegalEntityException( Clock.class, clock );
		}
		
		if ( this.clocks.containsKey( clock.getId() ) )
		{
			throw new EntityExistsException( Clock.class, clock.getId() );
		}
		
		this.clocks.put( clock.getId(), clock );
	}
}
