package com.auce.server.rest;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import com.auce.auction.entity.Trader;

public class TraderResource extends BaseResource
{
	private String	traderId;

	public TraderResource( Context context, Request request, Response response )
	{
		super( context, request, response );
		
		this.getVariants().add( new Variant( MediaType.TEXT_HTML ) );
		
		this.traderId = 
			(String)this.getRequest().getAttributes().get( "traderId" );
	}
	
	@Override
	public Representation getRepresentation ( Variant variant )
	{
		Representation result = null;
		
		StringWriter writer = new StringWriter();
		
		Trader trader = this.getRepository().findTrader( traderId );
		
		if ( trader != null )
		{
			trader.dump( new PrintWriter( writer ) );
		
			this.getResponse().setStatus( Status.SUCCESS_OK );
		
			result = new StringRepresentation(
				writer.toString(), MediaType.TEXT_XML );
		}
		else
		{
			this.getResponse().setStatus( Status.CLIENT_ERROR_NOT_FOUND );

			result = new StringRepresentation(
				"trader not found: " + this.traderId, MediaType.TEXT_PLAIN );
		}
		
		return result;
	}
}
