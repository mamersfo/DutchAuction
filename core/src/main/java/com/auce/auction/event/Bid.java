package com.auce.auction.event;

import com.auce.auction.entity.Lot;
import com.auce.auction.entity.Trader;

public class Bid extends Event
{
	private final Trader	trader;
	private final Lot		lot;
	private int				price;		// TODO remove - clock price prevails
	private int				quantity;
	
	public Bid( Trader trader, Lot lot, int price, int quantity )
	{
		this.trader = trader;
		this.lot = lot;
		this.price = price;
		this.quantity = quantity;
	}
	
	public Type getType()
	{
		return Type.BID;
	}
	
	public Trader getTrader()
	{
		return this.trader;
	}

	public Lot getLot()
	{
		return this.lot;
	}

	public int getQuantity ()
	{
		return quantity;
	}
	
	public void setQuantity( int quantity )
	{
		this.quantity = quantity;
	}
	
	public int getPrice()
	{
		return this.price;
	}
	
	public void setPrice( int price )
	{
		this.price = price;
	}
	
	@Override
	public String toString ()
	{
		StringBuilder sb = new StringBuilder( this.getClass().getSimpleName() );
		
		sb.append( " {" );
		sb.append( "trader=" + this.trader.getId() + ", " );
		sb.append( "lot=" + this.lot.getId() + ", " );
		sb.append( "price=" + this.price + ", " );
		sb.append( "quantity=" + this.quantity );
		sb.append( " }" );
		
		return sb.toString();
	}
}
