package com.auce.auction.event;

import com.auce.auction.entity.Lot;
import com.auce.auction.entity.Product;
import com.auce.auction.entity.Trader;

public class Purchase extends Event
{
	protected Lot		lot;
	protected Trader	trader;
	protected int		price;
	protected int		quantity;
	protected String	accountNumber;
	protected String	referenceNumber;

	public Purchase( 
		Trader trader, 
		Lot lot, 
		int price, 
		int quantity, 
		String accountNumber,
		String referenceNumber )
	{
		this.trader = trader;
		this.lot = lot;
		this.price = price;
		this.quantity = quantity;
		this.accountNumber = accountNumber;
		this.referenceNumber = referenceNumber;
	}
	
	@Override
	public String toString ()
	{
		StringBuilder sb = new StringBuilder( this.getClass().getSimpleName() + " { " );
		
		if ( trader != null ) sb.append( "trader=" + trader.getId() + ", " );
		if ( lot != null ) sb.append( "lot=" + lot.getId() + ", " );
		sb.append( "price=" + price + ", " );
		sb.append( "quantity=" + quantity + ", " );
		sb.append( "accountNumber=" + accountNumber + ", " );
		sb.append( "referenceNumber=" + referenceNumber + " }" );
		
		return sb.toString();
	}
	
	public Type getType()
	{
		return Type.PURCHASE;
	}
	
	public Product getProduct()
	{
		Product result = null;
		
		if ( this.lot != null )
		{
			result = this.lot.getProduct();
		}
		
		return result;
	}

	public Lot getLot()
	{
		return this.lot;
	}
	
	public Trader getTrader()
	{
		return this.trader;
	}

	public int getPrice ()
	{
		return price;
	}

	public int getQuantity ()
	{
		return this.quantity;
	}
	
	public int getValue()
	{
		return ( this.price * this.quantity );
	}
	
	public String getAccountNumber ()
	{
		return accountNumber;
	}

	public String getReferenceNumber ()
	{
		return referenceNumber;
	}
}
