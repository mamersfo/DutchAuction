package com.auce.auction.entity;

import com.auce.util.Identity;
import com.auce.util.StringUtils;

public abstract class Entity
{
	public boolean equals( Object object )
	{
		boolean result = false;
		
		if ( object != null )
		{
			if ( object.getClass().equals( this.getClass() ) )
			{
				if ( object instanceof Identity )
				{
					result = ((Identity)this).getId()
						.equals( ((Identity)object).getId() );
				}
			}
		}
		
		return result;
	}
	
	public String toXml()
	{
		return StringUtils.toXml( this );
	}
	
	/*
	public String toString()
	{
		return StringUtils.toString( this );
	}
	 */
}
