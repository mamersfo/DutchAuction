package com.auce.auction.event;

import com.auce.auction.entity.Product;
import com.auce.auction.entity.Trader;

public class Offer extends Event
{
	private final Trader	trader;
	private final Product	product;
	private final int		quantity;
	private final String	accountNumber;
	private final String	referenceNumber;

	public Offer( 
		Trader trader, Product product, int quantity,
		String accountNumber, String referenceNumber )
	{
		this.trader = trader;
		this.product = product;
		this.quantity = quantity;
		this.accountNumber = accountNumber;
		this.referenceNumber = referenceNumber;
	}
	
	public Type getType()
	{
		return Event.Type.OFFER;
	}
	
	public Trader getTrader()
	{
		return this.trader;
	}

	public Product getProduct ()
	{
		return product;
	}

	public int getQuantity ()
	{
		return quantity;
	}
	
	public String getAccountNumber ()
	{
		return accountNumber;
	}

	public String getReferenceNumber ()
	{
		return referenceNumber;
	}
	
	@Override
	public String toString ()
	{
		StringBuilder sb = new StringBuilder( this.getClass().getSimpleName() );
		
		sb.append( " { " );
		sb.append( "trader=" + this.trader.getId() + ", " );
		sb.append( "product=" + this.product.getId() + ", " );
		sb.append( "quantity=" + this.quantity + ", " );
		sb.append( "accountNumber=" + this.accountNumber + ", " );
		sb.append( "referenceNumber=" + this.referenceNumber );
		sb.append( " }" );
		
		return sb.toString();
	}
}
