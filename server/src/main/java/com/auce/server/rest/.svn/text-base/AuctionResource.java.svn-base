package com.auce.server.rest;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import com.auce.auction.entity.Clock;
import com.auce.auction.event.Run;
import com.auce.util.StringUtils;

public class AuctionResource extends BaseResource
{
	public AuctionResource( Context context, Request request, Response response )
	{
		super( context, request, response );
		
		this.getVariants().add( new Variant( MediaType.TEXT_XML ) );
	}
	
	@Override
	public Representation getRepresentation ( Variant variant )
	{
		Representation result = null;
		
		if ( MediaType.TEXT_XML.equals( variant.getMediaType() ) )
		{
			StringBuilder sb = new StringBuilder( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
			
			sb.append( "\r\n" );
			
			sb.append( "<Auction>" );
			
			sb.append( "\r\n" );
			
			Clock[] clocks = this.getRepository().listClocks();
			
			for ( int i=0; i < clocks.length; i++ )
			{
				Run[] runs = clocks[i].listRuns();
				
				for ( int j=0; j < runs.length; j++ )
				{
					StringUtils.appendToXml( runs[j], sb );
					
					sb.append( "\r\n" );
				}
			}
			
			sb.append( "</Auction>" );
			
			sb.append( "\r\n" );

			result = new StringRepresentation( sb.toString(), MediaType.TEXT_XML );	
		}
		
		return result;
	}
}
