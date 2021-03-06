
package com.auce.monitor;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.entity.Clock;
import com.auce.auction.entity.Product;
import com.auce.auction.entity.Trader;
import com.auce.auction.event.Bid;
import com.auce.auction.event.Event;
import com.auce.auction.event.EventMapper;
import com.auce.auction.event.Offer;
import com.auce.auction.event.Quote;
import com.auce.auction.event.Run;
import com.auce.auction.event.Tick;
import com.auce.auction.repository.Repository;
import com.auce.monitor.clock.ClockModel;
import com.auce.monitor.clock.DefaultClockModel;
import com.auce.monitor.graph.PriceGraphModel;
import com.auce.monitor.trader.TraderModel;
import com.auce.util.multicast.MulticastChannel;
import com.auce.util.multicast.MulticastChannelEvent;
import com.auce.util.multicast.MulticastChannelListener;

public class Controller implements MulticastChannelListener
{
	final static protected Logger LOGGER = LoggerFactory.getLogger( Controller.class );

	final static protected int DEFAULT_CAPACITY	= 50;

	protected Repository						repository;
	protected Map<String,ClockModel>			clockModels;
	protected Map<String,MulticastChannel>		clockChannels;
	protected Map<String,TraderModel>			traderModels;
	protected HashMap<String,PriceGraphModel>	graphModel;
	protected MulticastChannel					marketChannel;
	protected EventMapper						mapper;

	public Controller( Repository repository )
	{
		this.repository = repository;
		this.mapper = new EventMapper( this.repository );

		this.clockModels = new HashMap<String,ClockModel>();
		this.traderModels = new HashMap<String,TraderModel>();
		this.clockChannels = new HashMap<String,MulticastChannel>();
		this.graphModel = new HashMap<String,PriceGraphModel>();
		
		try
		{
			this.marketChannel = new MulticastChannel( System.getProperty( "market.id" ) + "-client" );
			this.marketChannel.setGroup( InetAddress.getByName( System.getProperty( "market.group" ) ) );
			this.marketChannel.setPort( Integer.parseInt( System.getProperty( "market.port" ) ) );
			this.marketChannel.setPeriod( Integer.parseInt( System.getProperty( "market.period" )) );
			this.marketChannel.addChannelListener( this );		
			this.marketChannel.start();
		}
		catch( Exception e )
		{
			LOGGER.error( e.getMessage(), e );
		}
	}

	public TraderModel addTrader ( Trader trader )
	{
		TraderModel model = new TraderModel( trader );
		
		this.traderModels.put( trader.getId(), model );
		
		return model;
	}

	public ClockModel addClock ( Clock clock )
	{
		ClockModel result = new DefaultClockModel( clock );
		
		String clockId = clock.getId();

		this.clockModels.put( clockId, result );

		try
		{
			MulticastChannel channel = new MulticastChannel( clockId + "-client" );
			
			channel.addChannelListener( this );
			channel.setGroup( clock.resolveInetAddress() );
			channel.setPort( clock.getPort() );
			channel.setTtl( 1 );

			this.clockChannels.put( clockId, channel );

			channel.start();
		}
		catch ( Exception e )
		{
			LOGGER.error( "Error starting channel: {}", clockId );
		}

		return result;
	}

	public ClockModel getClockModel ( String clockId )
	{
		return this.clockModels.get( clockId );
	}

	public void handleEvent ( MulticastChannelEvent channelEvent )
	{
		String sender = channelEvent.getSender();
		
		for ( String line : channelEvent.lines() )
		{
			Event event = this.mapper.read( sender, line );
			
			if ( event != null )
			{			
				if ( event instanceof Tick )
				{
					Tick price = (Tick)event;
					
					ClockModel clockModel = this.clockModels.get( sender );
					
					clockModel.setValue( price.getValue() );
				}
				else if ( event instanceof Quote )
				{
					Quote quote = (Quote)event;
					
					Product product = quote.getProduct();
					
					if ( product != null )
					{
						PriceGraphModel model = 
							this.graphModel.get( product.getId() );
						
						if ( model != null )
						{
							model.add( quote.getPrice() );
						}
					}
				}			
				else if ( event instanceof Run )
				{
					Run run = (Run)event;
					
					if ( run.getLot() == null )
					{
						LOGGER.info( "could not find lot for auction: {}", run.getId() );
					}
					
					ClockModel clockModel = this.clockModels.get( run.getClock().getId() );
					
					if ( clockModel != null )
					{
						clockModel.setRun( run );
					}
					else 
					{
						LOGGER.error( "clock not found: {}", run.getClock().getId() );
					}
				}
				else if ( event instanceof Bid || event instanceof Offer )
				{
					if ( this.repository.findTrader( sender ) == null )
					{
						this.repository.addTrader( new Trader( sender ) );
					}		
				}
			}
		}
	}
	
	public PriceGraphModel addProduct( Product product )
	{
		PriceGraphModel model = 
			new PriceGraphModel( product, DEFAULT_CAPACITY );

		this.graphModel.put( product.getId(), model );
		
		return model;
	}
	
	public MulticastChannel getMarketChannel()
	{
		return this.marketChannel;
	}
	
	public Collection<PriceGraphModel> listModels()
	{
		return this.graphModel.values();
	}
}
