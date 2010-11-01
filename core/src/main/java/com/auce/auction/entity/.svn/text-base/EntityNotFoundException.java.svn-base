package com.auce.auction.entity;


public class EntityNotFoundException extends EntityException
{
	private static final long serialVersionUID = 7790649890570416996L;
	
	protected String	id;
	
	public EntityNotFoundException( Class<? extends Entity> type, String id )
	{
		super( type );
		
		this.id = id;
	}

	public String getMessage()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( this.type.getSimpleName() );
		sb.append( "[" );
		sb.append( this.id );
		sb.append( "] not found" );
		
		return sb.toString();
	}
}
