package com.auce.auction.entity;


public abstract class EntityException extends RuntimeException
{
	protected Class<? extends Entity>	type;
	
	public EntityException( Class<? extends Entity> type )
	{
		this.type = type;
	}
	
	public Class<? extends Entity> getType()
	{
		return this.type;
	}
}
