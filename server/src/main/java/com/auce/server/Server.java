package com.auce.server;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.Auction;
import com.auce.auction.clock.ClockRunner;
import com.auce.auction.entity.Clock;
import com.auce.auction.entity.Trader;
import com.auce.auction.event.Entry;
import com.auce.auction.repository.Repository;
import com.auce.auction.repository.impl.RepositoryCsvImpl;
import com.auce.bank.AbstractAccount;
import com.auce.bank.Account;
import com.auce.bank.AccountType;
import com.auce.bank.Bank;
import com.auce.bank.BankFacade;
import com.auce.bank.Instruction;
import com.auce.bank.Opening;
import com.auce.bank.impl.BankGateway;
import com.auce.bank.impl.BankOfCollections;
import com.auce.bank.socket.SocketServer;
import com.auce.bank.util.Observer;
import com.auce.market.Market;
import com.auce.server.rest.RestApplication;
import com.auce.util.Utilities;

public class Server implements Observer<Instruction>
{	
	final static private Logger LOGGER = LoggerFactory.getLogger( Server.class );
	
	private Auction			auction;
	private	Account			account;
	private BankGateway		bankGateway;
	private Repository		repository;
	private Market			market;

	public Server()
	{
		// Repository
		this.repository = new RepositoryCsvImpl();

		// Bank
		BankFacade bank = new BankFacade( new BankOfCollections() );
		this.bankGateway = new BankGateway( bank );		
		
		String accountNumber = bank.issueAccountNumber();
		bank.openAccount( AccountType.CHECKING, accountNumber, "Masterclass", "Rotterdam" );		
		this.account = bank.findAccount( accountNumber );
		((AbstractAccount)account).setBalance( Long.MAX_VALUE );	

		// Market
		this.market = new Market( System.getProperty( "market.id" ), bank );
		market.setPeriod( Integer.parseInt( System.getProperty( "market.period" ) ) );
		market.setRepository( this.repository );

		// Auction
		this.auction = new Auction( "Auction", this.repository, this.market, bank );
		
		// Add bank observer
		bank.addObserver( this );
	}
	
	public void runService() throws Exception
	{
		Component component = new Component();

		component.getServers().add( 
			Protocol.HTTP, 
			Integer.parseInt( System.getProperty( "auction.port" ) ) );
		
		RestApplication app = new RestApplication( component.getContext() );
		
		app.setRepository( this.repository );
		app.setBankGateway( this.bankGateway );
		
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
		SocketServer bankServer = new SocketServer( bankPort, this.bankGateway );
		bankServer.start();		
	}
	
	public void runMarket() throws Exception
	{
		market.start();
	}
	
	public void runClocks()
	{
		Clock[] clocks = this.repository.listClocks();
		
		for ( int i=0; i < clocks.length; i++ )
		{
			ClockRunner runner = 
				new ClockRunner( 
					this.auction, 
					clocks[i],
					this.repository
				);
			
			// runner.setMarket( this.market );
			
			runner.start();
		}
	}
	
	public void addEvent ( Instruction instruction )
	{
		if ( instruction instanceof Opening )
		{
			Opening opening = (Opening)instruction;
			
			if ( AccountType.CHECKING == opening.getAccountType() &&
				 com.auce.bank.Status.EXECUTED == opening.getStatus() )
			{
				String id = opening.getNameOfHolder();
				
				String accountNumber = opening.getAccountNumber();

				Bank bank = this.bankGateway.getBank();
				
				Account account = bank.findAccount( accountNumber );
				
				if ( account != null )
				{
					long amount = 0;	
					
					Trader trader = this.repository.findTrader( id );
					
					if ( trader == null )
					{
						trader = new Trader( id );
						
						this.repository.addTrader( trader );

						Entry entry = new Entry( id );
						this.market.send( entry );
						LOGGER.info( "sent entry: {}", entry );
						
						amount = Long.parseLong( 
							System.getProperty( "bank.start.amount" ) );					
					}
					else
					{
						amount = trader.getAccount().getBalance();						
					}
					
					trader.setAccount( account );
	
					bank.doPayment( 
						accountNumber, 
						this.account.getAccountNumber(), 
						"welcome " + id, 
						amount
					);
					
					LOGGER.info( 
						"added trader: {} with account number: {}, balance: {}", 
						new Object[] { id, accountNumber, amount } );
				}
				else
				{
					LOGGER.error( "account not found: {}", accountNumber );
				}
			}
		}
	}
	
	public static void main ( String[] args )
	{	
		try
		{
			Utilities.loadProperties( "server.conf" );
						
			Server server = new Server();
			
			server.runMarket();
			server.runAuction();
			server.runBank();
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
