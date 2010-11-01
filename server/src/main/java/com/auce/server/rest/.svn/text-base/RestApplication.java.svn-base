package com.auce.server.rest;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Router;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.auce.auction.repository.Repository;
import com.auce.bank.impl.BankGateway;

public class RestApplication extends Application
{
	private Repository		repository;
	private BankGateway		bankGateway;
	
	public RestApplication( Context context )
	{
		super( context );
		
		SLF4JBridgeHandler.install();
	}
	
	public Repository getRepository()
	{
		return this.repository;
	}
	
	public void setRepository( Repository repository )
	{
		this.repository = repository;
	}
	
	@Override
	public Restlet createRoot ()
	{
		Router router = new Router( getContext() );
		
		router.attach( "/auction", AuctionResource.class );
		router.attach( "/trader/{traderId}", TraderResource.class );
		router.attach( "/account/{accountNumber}", AccountResource.class );
		
		return router;
	}

	public BankGateway getBankGateway ()
	{
		return bankGateway;
	}

	public void setBankGateway ( BankGateway gateway )
	{
		this.bankGateway = gateway;
	}
}
