package com.auce.auction.entity;

import com.auce.auction.event.Purchase;
import com.auce.util.Identity;

public class Lot extends Entity implements Identity
{
	final static public String PREFIX = "L";
	
	protected String			id;
	protected Supplier			supplier;
	protected Product			product;
	protected int				quantity;
	
	public Lot( String id )
	{
		this.id = id;
	}
	
	public Lot( String id, Supplier supplier, Product product )
	{
		this( id, supplier, product, 100 );
	}
	
	public Lot( String id, Supplier supplier, Product product, int quantity )
	{
		this.id = id;
		this.supplier = supplier;
		this.product = product;
		this.quantity = quantity;
	}

	public String getId()
	{
		return this.id;
	}
		
	public Supplier getSupplier ()
	{
		return supplier;
	}
	
	public void setSupplier( Supplier supplier )
	{
		this.supplier = supplier;
	}

	public Product getProduct ()
	{
		return product;
	}
	
	public void setProduct( Product product )
	{
		this.product = product;
	}
	
	public int getQuantity ()
	{
		return quantity;
	}
	
	public void setQuantity( int quantity )
	{
		this.quantity = quantity;
	}
	
	// Business Rules
	
	public void handlePurchase( Purchase purchase )
	{
		this.quantity -= purchase.getQuantity();
	}
}
