
package com.auce.auction.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.dao.SupplierDao;
import com.auce.auction.entity.EntityExistsException;
import com.auce.auction.entity.EntityNotFoundException;
import com.auce.auction.entity.IllegalEntityException;
import com.auce.auction.entity.Supplier;

public class SupplierDaoMemoryImpl implements SupplierDao
{
	final static protected Logger LOGGER = LoggerFactory.getLogger( SupplierDaoMemoryImpl.class );
	
	protected Map<String,Supplier> suppliers;

	public SupplierDaoMemoryImpl()
	{
		this.suppliers = new HashMap<String,Supplier>();
	}
	
	public int count()
	{
		return this.suppliers.size();
	}
	
	public Supplier[] list()
	{
		Collection<Supplier> collection = this.suppliers.values();
		return (Supplier[])collection.toArray( new Supplier[ collection.size() ] );
	}
	
	public Supplier find( String id )
	{
		Supplier result = this.suppliers.get( id );
		if ( result == null ) throw new EntityNotFoundException( Supplier.class, id );
		return result;
	}
	
	public void add( Supplier supplier )
	{
		if ( supplier == null || supplier.getId() == null )
		{
			throw new IllegalEntityException( Supplier.class, supplier );
		}
		
		if ( this.suppliers.containsKey( supplier.getId() ) )
		{
			throw new EntityExistsException( Supplier.class, supplier.getId() );
		}
		
		this.suppliers.put( supplier.getId(), supplier );
	}
}
