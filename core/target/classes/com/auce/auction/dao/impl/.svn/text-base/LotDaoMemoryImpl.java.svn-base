package com.auce.auction.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.auce.auction.dao.LotDao;
import com.auce.auction.entity.EntityExistsException;
import com.auce.auction.entity.EntityNotFoundException;
import com.auce.auction.entity.IllegalEntityException;
import com.auce.auction.entity.Lot;

public class LotDaoMemoryImpl implements LotDao
{
	protected Map<String,Lot>	lots;
	protected int				index;
	
	public LotDaoMemoryImpl()
	{
		this.index = 1;
		this.lots = new HashMap<String,Lot>();
	}

	public Lot find( String lotId )
	{
		Lot result = this.lots.get( lotId );
		if ( result == null ) throw new EntityNotFoundException( Lot.class, lotId );
		return result;
	}
	
	public Lot create()
	{
		StringBuilder sb = new StringBuilder( "L" );
		sb.append( Integer.toString( this.index++ ) );
		Lot result = new Lot( sb.toString() );
		return result;
	}
	
	public void add( Lot lot )
	{
		if ( lot == null || lot.getId() == null )
		{
			throw new IllegalEntityException( Lot.class, lot );
		}
		
		if ( this.lots.containsKey( lot.getId() ) )
		{
			throw new EntityExistsException( Lot.class, lot.getId() );
		}
		
		this.lots.put( lot.getId(), lot );
	}

	public int count ()
	{
		return this.lots.size();
	}

	public Lot[] list ()
	{
		Collection<Lot> collection = this.lots.values();
		return collection.toArray( new Lot[ collection.size() ] );
	}
}
