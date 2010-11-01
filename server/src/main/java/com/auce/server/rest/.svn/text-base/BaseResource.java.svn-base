package com.auce.server.rest;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Resource;

import com.auce.auction.repository.Repository;

public class BaseResource extends Resource
{
	public BaseResource( Context context, Request request, Response response )
	{
		super( context, request, response );
	}

	public RestApplication getApplication()
	{
		return (RestApplication)this.getContext()
			.getAttributes().get( Application.KEY );
	}

	public Repository getRepository()
	{
		return this.getApplication().getRepository();
	}		
}
