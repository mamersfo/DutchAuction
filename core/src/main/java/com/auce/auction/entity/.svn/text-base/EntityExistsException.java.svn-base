package com.auce.auction.entity;


public class EntityExistsException extends EntityException
{
	private static final long	serialVersionUID	= 8728502063464735429L;

	protected String	id;
	
	public EntityExistsException( Class<? extends Entity> type, String id )
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
		sb.append( "] exists" );
		
		return sb.toString();
	}
}
