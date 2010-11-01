
package com.auce.auction.event;

import com.auce.auction.entity.Product;
import com.auce.auction.entity.Trader;


public class Sale extends Event
{
	protected Trader	trader;
	protected Product	product;
	protected int		price;
	protected int		quantity;

	public Sale( Trader trader, Product product, int price, int quantity )
	{
		this.trader = trader;
		this.product = product;
		this.price = price;
		this.quantity = quantity;
	}
	
	public Type getType()
	{
		return Type.SALE;
	}
	
	public Trader getTrader()
	{
		return this.trader;
	}

	public Product getProduct ()
	{
		return this.product;
	}

	public int getPrice ()
	{
		return price;
	}

	public int getQuantity ()
	{
		return quantity;
	}
	
	public int getValue()
	{
		return this.price * this.quantity;
	}

	public Trader trader ()
	{
		return trader;
	}
}
