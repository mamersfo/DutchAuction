package com.auce.market;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.entity.Lot;
import com.auce.auction.entity.Product;
import com.auce.auction.entity.Trader;
import com.auce.auction.event.Bid;
import com.auce.auction.event.Event;
import com.auce.auction.event.EventMapper;
import com.auce.auction.event.Offer;
import com.auce.auction.event.Quote;
import com.auce.auction.repository.Repository;
import com.auce.bank.AbstractAccount;
import com.auce.bank.Account;
import com.auce.bank.AccountType;
import com.auce.bank.Bank;
import com.auce.bank.Instruction;
import com.auce.bank.Opening;
import com.auce.bank.util.Observer;
import com.auce.util.component.ComponentSupport;
import com.auce.util.multicast.MulticastChannel;
import com.auce.util.multicast.MulticastChannelEvent;
import com.auce.util.multicast.MulticastChannelListener;

public class Market extends ComponentSupport implements MulticastChannelListener, Observer<Instruction>
{
	final static protected Logger LOGGER = LoggerFactory.getLogger( Market.class );
	
	private final Bank				bank;
	private final Account			account;
	private final Repository		repository;
	private final EventMapper		mapper;
	protected MulticastChannel		channel;
	protected Map<String,Quoter>	quoters;
	
	public Market( String id, Repository repository, Bank bank )
	{
		super( id );
		
		this.repository = repository;
		this.mapper = new EventMapper( this.repository );

		this.bank = bank;
		
		String accountNumber = bank.issueAccountNumber();
		bank.openAccount( AccountType.CHECKING, accountNumber, "Market", "Rotterdam" );		
		this.account = bank.findAccount( accountNumber );
		((AbstractAccount)account).setBalance( Long.MAX_VALUE );	
		
		try
		{
			this.period = Integer.parseInt( System.getProperty( "market.period" ) );
			
			this.quoters = new HashMap<String,Quoter>();
	
			// channel
			
			this.channel = new MulticastChannel( this.id );
			this.channel.setGroup( InetAddress.getByName( System.getProperty( "market.address" ) ) );
			this.channel.setPort( Integer.parseInt( System.getProperty( "market.port" ) ) );
			this.channel.setTtl( Integer.parseInt( System.getProperty( "market.ttl" ) ) );
			this.channel.addChannelListener( this );
		}
		catch( UnknownHostException e )
		{
			e.printStackTrace();
		}
	}
	
	public Bank getBank()
	{
		return this.bank;
	}
	
	public Account getAccount()
	{
		return this.account;
	}
	
	public void start()
	{
		if ( this.repository == null )
		{
			throw new IllegalStateException( "repository not initialized" );
		}
		
		LOGGER.info( "starting {} with id {}", 
			this.getClass().getSimpleName(), this.id );

		this.channel.start();

		super.start();
	}
	
	public MulticastChannel getChannel ()
	{
		return channel;
	}
	
	public void send( Event event )
	{
		try
		{
			String message = this.mapper.write( event );
			
			this.channel.sendMessage( this.id, message );
		}
		catch( Exception e )
		{
			LOGGER.error( e.getMessage() );
		}
	}
	
	@Override
	protected void beforeRunning ()
	{
		if ( this.repository == null )
		{
			throw new IllegalStateException( "missing dependency: repository" );
		}
		
		Product[] products = this.repository.listProducts();
		
		for ( int i=0; i < products.length; i++ )
		{
			String productId = products[i].getId();
			
			String values = System.getProperty( "market.price." + productId );
			
			LOGGER.info( "product: {}, values: {}", productId, values );
			
			String[] params = values.split( "," );			
			
			Quoter generator = 
				new Quoter( 
					products[i], 						// product 
					Double.parseDouble( params[0] ),    // price
					Double.parseDouble( params[1] ),    // drift
					Double.parseDouble( params[2] )     // volatility
				);

			this.quoters.put( products[i].getId(), generator );
		}		
	}

	@Override
	protected void poll ()
	{
		try
		{
			StringBuilder sb = new StringBuilder();

			for ( Quoter generator : this.quoters.values() )
			{
				Quote quote = generator.getNextQuote2();
				
				sb.append( this.mapper.write( quote ) );
				
				sb.append( "\n" );
			}
			
			String msg = sb.toString();
			
			this.channel.sendMessage( this.id, msg );
		}
		catch ( IOException e )
		{
			LOGGER.error( e.getMessage(), e );
		}
	}

	public Repository getRepository ()
	{
		return repository;
	}

	public Quoter getQuoter ( Product product )
	{
		return this.quoters.get( product.getId() );
	}
	
	public void addOffer( Offer offer )
	{
		// adjust market price, market price goes down on offer

		Product product = offer.getProduct();
		if ( product == null ) return;
		
		Quoter quoter = this.getQuoter( product );
		if ( quoter == null ) return;

		quoter.setMu( quoter.getMu() - offer.getQuantity() * 0.00001 );
	}
	
	public void addBid ( Bid bid )
	{
		// adjust market price, market price goes up on bid
		
		if ( bid == null ) return;
		
		Lot lot = bid.getLot();
		if ( lot == null ) return;
			
		Product product = lot.getProduct();
		if ( product == null ) return;
		
		Quoter quoter = this.getQuoter( product );
		if ( quoter == null ) return;

		quoter.setMu( quoter.getMu() + bid.getQuantity() * 0.00001 );
	}

	@Override
	protected void afterRunning() 
	{
		// TODO Auto-generated method stub  	
	}

	public void handleEvent ( MulticastChannelEvent messageEvent )
	{
		String sender = messageEvent.getSender();
		
		if ( this.id.equals( sender ) ) return;
		
		Trader trader = this.repository.findTrader( sender );
		
		if ( trader == null )
		{
			trader = new Trader( sender ) ;
			
			String accountNumber = this.bank.issueAccountNumber();
			this.bank.openAccount( AccountType.CHECKING, accountNumber, sender, "Rotterdam" );		
			Account account = this.bank.findAccount(accountNumber);
			((AbstractAccount)account).setBalance( Long.parseLong( System.getProperty( "bank.start.amount" ) ) );
			trader.setAccount( account );
			
			this.repository.addTrader( trader );			
		}	
		
		for ( String text : messageEvent.lines() )
		{
			try
			{
				Event event =  this.mapper.read( sender, text );
				
				if ( event != null )
				{
					if ( event instanceof Offer )
					{
						Offer offer = (Offer)event;
						
						this.addOffer( offer );
						
						Buyer buyer = new Buyer( this, offer );
						
						new Thread( buyer ).start();
					}
				}
			}
			catch( Exception e )
			{
				LOGGER.error( e.getMessage(), e );
			}
		}
	}
	
	public void addEvent( Instruction instruction ) 
	{
		if ( instruction instanceof Opening )
		{
			Opening opening = (Opening)instruction;
			
			if ( AccountType.CHECKING == opening.getAccountType() && com.auce.bank.Status.EXECUTED == opening.getStatus() )
			{
				String id = opening.getNameOfHolder();
				
				String accountNumber = opening.getAccountNumber();

				Account account = this.bank.findAccount( accountNumber );
				
				if ( account != null )
				{
					long amount = 0;	
					
					Trader trader = this.repository.findTrader( id );
					
					if ( trader == null )
					{
						trader = new Trader( id );
						
						this.repository.addTrader( trader );
						
						amount = Long.parseLong( System.getProperty( "bank.start.amount" ) );					
					}
					else
					{
						amount = trader.getAccount().getBalance();						
					}
					
					trader.setAccount( account );
	
					this.bank.doPayment( 
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
}
