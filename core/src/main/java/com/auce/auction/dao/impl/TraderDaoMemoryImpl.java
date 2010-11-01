package com.auce.auction.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.auce.auction.dao.TraderDao;
import com.auce.auction.entity.EntityExistsException;
import com.auce.auction.entity.IllegalEntityException;
import com.auce.auction.entity.Trader;

public class TraderDaoMemoryImpl implements TraderDao
{
	protected Map<String,Trader>		traders;
	
	public TraderDaoMemoryImpl()
	{
		this.traders = new HashMap<String,Trader>();
	}
	
	public void update( Trader trader )
	{
		this.traders.put( trader.getId(), trader );
	}

	public void add ( Trader trader ) throws EntityExistsException, IllegalEntityException
	{
		if ( trader == null || trader.getId() == null )
		{
			throw new IllegalEntityException( Trader.class, trader );
		}
		
		if ( this.traders.containsKey( trader.getId() ) )
		{
			throw new EntityExistsException( Trader.class, trader.getId() );
		}
		
		this.traders.put( trader.getId(), trader );
	}

	public int count ()
	{
		return this.traders.size();
	}

	public Trader find ( String id )
	{
		Trader result = this.traders.get( id );
		// if ( result == null ) throw new EntityNotFoundException( Trader.class, id );
		return result;
	}

	public Trader[] list ()
	{
		Collection<Trader> collection = this.traders.values();
		return collection.toArray( new Trader[ collection.size() ] );
	}

}
