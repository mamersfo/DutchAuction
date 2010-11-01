package com.auce.auction.entity;

import java.util.LinkedList;
import java.util.List;

import com.auce.auction.event.Purchase;
import com.auce.auction.event.Sale;

public class Stock extends Entity
{
	protected Product			product;
	protected int				quantity;
	protected List<Purchase>	purchases;
	protected List<Sale>		sales;
	
	public Stock( Product product )
	{
		this.product = product;
		this.purchases = new LinkedList<Purchase>();
		this.sales = new LinkedList<Sale>();
	}
	
	public Product getProduct()
	{
		return this.product;
	}
	
	public int getQuantity()
	{
		return quantity;
	}
	
	public int getPurchaseQuantity()
	{
		int result = 0;
		
		for ( Purchase p : this.purchases )
		{
			result += ( p.getQuantity() );
		}
		
		return result;
	}
	
	public int getPurchaseValue()
	{
		int result = 0;
		
		for ( Purchase p : this.purchases )
		{
			result += p.getValue();
		}
		
		return result;
	}
	
	public int getSalesQuantity()
	{
		int result = 0;
		
		for ( Sale s : this.sales )
		{
			result += ( s.getQuantity() );
		}
		
		return result;
	}
	
	public int getSalesValue()
	{
		int result = 0;
		
		for ( Sale s : this.sales )
		{
			result += s.getValue();
		}
		
		return result;
	}
	
	// Business Rules

	public void updateWithPurchase( Purchase purchase )
	{
		this.purchases.add( purchase );
		
		this.quantity += purchase.getQuantity();
	}
	
	public void updateWithSale( Sale sale )
	{
		this.sales.add( sale );
	
		this.quantity -= sale.getQuantity();
	}
	
	@Override
	public String toString ()
	{
		StringBuilder sb = new StringBuilder( 
			this.getClass().getSimpleName() + " { " );
		
		sb.append( "product=" + this.product.getId() + ", " );
		sb.append( "quantity=" + this.quantity + " }" );
		
		return sb.toString();
	}
}
