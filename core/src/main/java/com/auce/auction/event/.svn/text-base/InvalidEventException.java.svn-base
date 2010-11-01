package com.auce.auction.event;


public class InvalidEventException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7497477655554710175L;
	
	protected Event event;
	
	public InvalidEventException( Event event, String reason )
	{
		super( reason );
		
		this.event = event;
	}

	public String getMessage()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( this.getClass().getSimpleName() );
		sb.append( ": " );
		sb.append( event.toString() );
		sb.append( ", " );
		sb.append( super.getMessage() );
		
		return sb.toString();
	}
}
