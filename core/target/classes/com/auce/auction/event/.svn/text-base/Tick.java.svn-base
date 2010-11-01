package com.auce.auction.event;

import com.auce.auction.entity.Lot;


public class Tick extends Event
{
	protected Lot	lot;
	protected int	value;
	
	public Tick( Lot lot, int value )
	{
		this.lot = lot;
		this.value = value;
	}
	
	public Type getType()
	{
		return Type.TICK;
	}

	public Lot getLot()
	{
		return lot;
	}

	public int getValue ()
	{
		return value;
	}
}
