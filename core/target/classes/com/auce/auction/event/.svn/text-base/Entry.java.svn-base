package com.auce.auction.event;

public class Entry extends Event
{
	private String	traderId;
	
	public Entry( String traderId )
	{
		this.traderId = traderId;
	}
	
	public Type getType()
	{
		return Type.ENTRY;
	}
	
	public String getTraderId()
	{
		return this.traderId;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder( this.getClass().getSimpleName() );
		
		sb.append( " { " );
		sb.append( "traderId=" + traderId );
		sb.append( " }" );
		
		return sb.toString();
	}
}
