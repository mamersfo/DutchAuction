package com.auce.auction.event;

import com.auce.auction.entity.Clock;
import com.auce.auction.entity.Lot;
import com.auce.util.Identity;

public class Run extends Event implements Identity
{
	final static public String PREFIX = "R";
	
	protected String	id;
	protected Clock		clock;
	protected Lot		lot;
	
	@Override
	public String toString ()
	{
		StringBuilder sb = new StringBuilder( 
			this.getClass().getSimpleName() + " {" );
		
		sb.append( "id=" + this.id + ", " );
		sb.append( "clock=" + this.clock.getId() + ", " );
		sb.append( "lot=" + this.lot.getId() + ", " );
		sb.append( "product=" + this.lot.getProduct().getId() + " }" );
		
		return sb.toString();
	}
	
	public Run( String id )
	{
		this.id = id;
	}
	
	public Run( String id, Clock clock, Lot lot )
	{
		this.id = id;
		this.clock = clock;
		this.lot = lot;
	}
	
	public Type getType()
	{
		return Type.RUN;
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public Clock getClock()
	{
		return clock;
	}
	
	public void setClock( Clock clock )
	{
		this.clock = clock;
	}

	public Lot getLot ()
	{
		return lot;
	}
	
	public void setLot( Lot lot )
	{
		this.lot = lot;
	}
}
