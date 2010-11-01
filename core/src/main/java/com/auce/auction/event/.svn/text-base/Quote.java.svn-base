package com.auce.auction.event;

import com.auce.auction.entity.Product;


public class Quote extends Event
{
	protected Product	product;
	protected int		price;
	
	public Quote( Product product, int price )
	{
		this.product = product;
		this.price = price;
	}
	
	public Type getType()
	{
		return Type.QUOTE;
	}

	public Product getProduct()
	{
		return product;
	}

	public int getPrice ()
	{
		return price;
	}
}
