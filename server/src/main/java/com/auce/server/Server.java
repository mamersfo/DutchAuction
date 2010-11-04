package com.auce.server;

import org.restlet.Component;

import org.restlet.data.Protocol;

import com.auce.auction.Auction;
import com.auce.auction.clock.ClockRunner;
import com.auce.auction.entity.Clock;
import com.auce.auction.repository.Repository;
import com.auce.auction.repository.impl.RepositoryCsvImpl;
import com.auce.bank.BankFacade;
import com.auce.bank.impl.BankGateway;
import com.auce.bank.impl.BankOfCollections;
import com.auce.bank.socket.SocketServer;
import com.auce.market.Market;
import com.auce.server.rest.RestApplication;
import com.auce.util.Utilities;

public class Server
{	
	private Auction			auction;
	private BankGateway		bank;
	private Repository		repository;
	private Market			market;

	public Server()
	{
		this.repository = new RepositoryCsvImpl();

		BankFacade bankFacade = new BankFacade( new BankOfCollections() );
		
		this.bank = new BankGateway( bankFacade );		
		
		this.market = new Market( System.getProperty( "market.id" ), this.repository, bankFacade );
		
		this.auction = new Auction( System.getProperty( "market.id" ), this.repository, this.market, bankFacade );

		bankFacade.addObserver( this.market );
	}
	
	public void runService() throws Exception
	{
		Component component = new Component();
		component.getServers().add( Protocol.HTTP, Integer.parseInt( System.getProperty( "auction.port" ) ) );
		RestApplication app = new RestApplication( component.getContext() );
		
		app.setRepository( this.repository );
		app.setBankGateway( this.bank );
		
		component.getDefaultHost().attachDefault( app );
		component.start();		
	}
	
	public void runAuction() throws Exception
	{
		this.auction.start();		
	}
	
	public void runBank() throws Exception
	{
		int bankPort = Integer.parseInt( System.getProperty( "bank.port" ) );
		
		( new SocketServer( bankPort, this.bank ) ).start();
	}
	
	public void runMarket() throws Exception
	{
		this.market.start();
	}
	
	public void runClocks()
	{
		Clock[] clocks = this.repository.listClocks();
		
		for ( int i=0; i < clocks.length; i++ )
		{
			ClockRunner clockRunner = new ClockRunner( this.auction, clocks[i], this.repository, this.bank.getBank() );
			
			clockRunner.start();		
		}
	}
	
	public static void main ( String[] args )
	{	
		try
		{
			Utilities.loadProperties( "server.conf" );
						
			Server server = new Server();
			
			server.runMarket();
			
			// server.runAuction();
			
			// server.runBank();
			
			server.runClocks();
			
			server.runService();
		}
		catch( Exception e )
		{
			e.printStackTrace();
			
			System.exit( 0 );
		}
	}
}
