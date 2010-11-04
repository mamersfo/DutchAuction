package com.auce.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.entity.Clock;
import com.auce.auction.entity.Entity;
import com.auce.auction.entity.Stock;
import com.auce.auction.entity.Trader;
import com.auce.auction.event.Bid;
import com.auce.auction.event.Event;
import com.auce.auction.event.EventMapper;
import com.auce.auction.event.Offer;
import com.auce.auction.event.Purchase;
import com.auce.auction.event.Quote;
import com.auce.auction.event.Sale;
import com.auce.auction.event.Tick;
import com.auce.auction.repository.Repository;
import com.auce.client.strategy.PurchasingStrategy;
import com.auce.client.strategy.SellingStrategy;
import com.auce.util.multicast.MulticastChannel;
import com.auce.util.multicast.MulticastChannelEvent;
import com.auce.util.multicast.MulticastChannelListener;

public abstract class AbstractClient implements Client, MulticastChannelListener
{
	final static private Logger LOGGER = LoggerFactory.getLogger( AbstractClient.class );
	
	protected Trader				trader;
	protected Repository			repository;
	protected PurchasingStrategy	purchasingStrategy;
	protected SellingStrategy		sellingStrategy;
	protected EventMapper			mapper;
	protected Map<String,Purchase>	purchases;
	
	public AbstractClient( Repository repository )
	{
		String traderId = System.getProperty( "trader.id" );
		this.trader = new Trader( traderId );		

		this.repository = repository;
		this.repository.addRepositoryListener( this );
		this.repository.addTrader( trader );			
          
		this.mapper = new EventMapper( this.repository );
		this.purchases = new HashMap<String,Purchase>();
	}	
	
	public PurchasingStrategy getPurchasingStrategy ()
	{
		return this.purchasingStrategy;
	}

	public SellingStrategy getSellingStrategy ()
	{
		return this.sellingStrategy;
	}
	
	public Repository getRepository()
	{
		return this.repository;
	}
	
	public Trader getTrader()
	{
		return this.trader;
	}

	public void setPurchasingStrategy( PurchasingStrategy strategy )
	{
		this.purchasingStrategy = strategy;
	}
	
	public void setSellingStrategy( SellingStrategy strategy )
	{
		this.sellingStrategy = strategy;
	}
	
	protected void handleEvent( MulticastChannel source, Tick tick )
	{
		Bid bid = this.purchasingStrategy.doBid( tick );
		
		if ( bid != null )
		{
			String string = this.mapper.write( bid ) ;
			
			try
			{
				source.sendMessage( this.trader.getId(), string );
				
				LOGGER.info( "BID: {}, price={}, qty={}", 
					new Object[] {
						bid.getLot().getProduct().getId(),
						bid.getPrice(),
						bid.getQuantity()
					});
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}		
	}
	
	protected void handleEvent( MulticastChannel source, Purchase purchase )
	{
		if ( this.trader.equals( purchase.getTrader() ) )
		{
			LOGGER.info( "PURCHASE: {}, price={}, qty={}", 
				new Object[] { 
					purchase.getLot().getProduct().getId(),
					purchase.getPrice(),
					purchase.getQuantity()
				});
			
			String referenceNumber = purchase.getReferenceNumber();
			
			this.purchases.put( referenceNumber, purchase );	
		}	
	}
	
	protected void handleEvent( MulticastChannel source, Quote quote )
	{		
		Offer offer = this.sellingStrategy.doOffer( quote );
		
		if ( offer != null )
		{
			String message = this.mapper.write( offer );
			
			try
			{
				source.sendMessage( this.trader.getId(), message );
				
				LOGGER.info( "OFFER: {}, qty={}, ref={}",
					new Object[] {
						offer.getProduct().getId(), 
						offer.getQuantity(),
						offer.getReferenceNumber()
					}
				);
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}		
	}
	
	protected void handleEvent( MulticastChannel source, Sale sale )
	{
		if ( this.trader.equals( sale.getTrader() ) )
		{
			LOGGER.info( "SALE: {}, price={}, qty={}", new Object[] {
				sale.getProduct().getId(),
				sale.getPrice(),
				sale.getQuantity()
			});
			
			this.trader.addSale( sale );
			
			Stock stock = this.trader.findStock( 
				sale.getProduct(), false );
			
			if ( stock != null )
			{
				LOGGER.info( "in STOCK: {},{}", 
					stock.getProduct().getId(),
					stock.getQuantity() );
			}
		}
	}
	
	protected void handleEvent( MulticastChannel source, Event event )
	{
		if ( event instanceof Tick )
		{
			this.handleEvent( source, (Tick)event );
		}
		else if ( event instanceof Purchase )
		{
			this.handleEvent( source, (Purchase)event );
		}
		else if ( event instanceof Quote )
		{
			this.handleEvent( source, (Quote)event );					
		}
		else if ( event instanceof Sale )
		{
			this.handleEvent( source, (Sale)event );					
		}
	}
	
	public void handleEvent ( MulticastChannelEvent messageEvent )
	{
		String sender = messageEvent.getSender();
		
		if ( sender.equals( this.trader.getId() ) ) return;
		
		for ( String text : messageEvent.lines() )
		{		
			try
			{
				Event event = this.mapper.read( sender, text );

				this.handleEvent( messageEvent.getSource(), event );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}
	
	public void repositoryChanged ( Entity entity )
	{
		if ( entity instanceof Clock )
		{
			Clock clock = (Clock)entity;
			
			LOGGER.info( "adding clock: {}", clock.getId() );
			
			MulticastChannel channel = 
				new MulticastChannel( "client-" + clock.getId() );
			
			channel.addChannelListener( this );
			channel.setGroup( clock.resolveInetAddress() );
			channel.setPort( clock.getPort() );
			channel.setTtl( 1 );
			
			channel.start();			
		}
		else
		{
			// logger.info( "added {}", entity );
		}
	}
}
