package com.auce.auction.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.auce.auction.dao.ProductDao;
import com.auce.auction.entity.EntityExistsException;
import com.auce.auction.entity.IllegalEntityException;
import com.auce.auction.entity.Product;

public class ProductDaoMemoryImpl implements ProductDao
{
	protected Map<String,Product> products;
	
	public ProductDaoMemoryImpl()
	{
		this.products = new HashMap<String,Product>();
	}
	
	public int count()
	{
		return this.products.size();
	}
	
	public Product[] list()
	{
		Collection<Product> collection = this.products.values();
		
		return (Product[])collection.toArray( new Product[ collection.size() ] );
	}
	
	public Product find( String id )
	{
		Product result = this.products.get( id );
		
		if ( result == null ) throw new com.auce.auction.entity.EntityNotFoundException( Product.class, id );
		
		return result;
	}

	public void add ( Product product )
	{
		if ( product == null || product.getId() == null )
		{
			throw new IllegalEntityException( Product.class, product );
		}
		
		if ( this.products.containsKey( product.getId() ) )
		{
			throw new EntityExistsException( Product.class, product.getId() );
		}
		
		this.products.put( product.getId(), product );
	}
}
