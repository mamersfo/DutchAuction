
package com.auce.bank.rest;

import static com.auce.bank.util.Constants.BANK_HTTP_PORT_KEY;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.Protocol;

import com.auce.bank.BankFacade;
import com.auce.bank.impl.BankGateway;
import com.auce.bank.impl.BankOfCollections;
import com.auce.bank.util.Utilities;

public class BankApplication extends Application
{
	private final BankFacade 	facade;
	private final BankGateway	gateway;

	public BankApplication( Context context )
	{
		super( context );

		this.facade = new BankFacade( new BankOfCollections() );
		
		this.gateway = new BankGateway( this.facade );
	}
	
	public BankFacade getFacade()
	{
		return this.facade;
	}
	
	public BankGateway getGateway()
	{
		return this.gateway;
	}

	@Override
	public Restlet createRoot ()
	{
		Router router = new Router( getContext() );
		router.attach( "/account/{accountNumber}", AccountResource.class );
		return router;
	}

	public static void main ( String[] args )
	{
		try
		{
			Utilities.loadProperties( "bank.properties" );
			int httpPort = Integer.parseInt( System.getProperty( BANK_HTTP_PORT_KEY ) );

			Component component = new Component();
			component.getServers().add( Protocol.HTTP, httpPort );
			BankApplication app = new BankApplication( component.getContext() );
			component.getDefaultHost().attachDefault( app );
			component.start();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
}
