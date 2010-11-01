package com.auce.bank.util;


import static com.auce.bank.util.Constants.LINEFEED;

public class StringResponse implements Response<String>
{
	private Status			status;
	private StringBuilder	body;
	
	public StringResponse()
	{
		this.body = new StringBuilder();
	}

	public Status getStatus ()
	{
		return this.status;
	}

	public void setStatus( Status status )
	{
		this.status = status;
	}

	public String getBody ()
	{
		return this.body.toString();
	}
	
	public void append( String string )
	{
		if ( this.body.length() > 0 )
		{
			this.body.append( LINEFEED );
		}
		
		this.body.append( string );
	}
	
	@Override
	public String toString ()
	{
		StringBuilder sb = new StringBuilder( this.getClass().getSimpleName() );
		
		sb.append( " { " );
		sb.append( "status=" + this.status + ", " );
		sb.append( "body=" + this.body );
		sb.append( " }" );
		
		return sb.toString();
	}
}
