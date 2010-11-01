package com.auce.auction.entity;


public class IllegalEntityException extends EntityException
{
	private static final long	serialVersionUID	= -4619737220577393821L;

	protected Entity					entity;
	
	public IllegalEntityException( Class<? extends Entity> type, Entity entity )
	{
		super( type );
		
		this.entity = entity;
	}
	
	public String getMessage()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( "illegal " );
		sb.append( this.type.getSimpleName() );
		sb.append( ": " );
		sb.append( this.entity );
		
		return sb.toString();
	}
}
