package com.auce.auction.event;

import com.auce.auction.entity.Entity;

public abstract class Event extends Entity
{
	public enum Type
	{
		TWEET,
		BID,
		OFFER,
		PURCHASE,
		QUOTE,
		RUN,
		SALE,
		TICK
	}
	
	public abstract Type getType();
}
