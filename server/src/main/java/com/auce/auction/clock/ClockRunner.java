package com.auce.auction.clock;

import java.io.IOException;

import com.auce.auction.Auction;
import com.auce.auction.AuctionGenerator;
import com.auce.auction.clock.event.ClockListener;
import com.auce.auction.clock.event.ClockStopEvent;
import com.auce.auction.clock.event.ClockTickEvent;
import com.auce.auction.entity.Clock;
import com.auce.auction.entity.Lot;
import com.auce.auction.entity.Trader;
import com.auce.auction.event.Bid;
import com.auce.auction.event.Event;
import com.auce.auction.event.EventMapper;
import com.auce.auction.event.Purchase;
import com.auce.auction.event.Run;
import com.auce.auction.event.Tick;
import com.auce.auction.repository.Repository;
import com.auce.util.component.ComponentSupport;
import com.auce.util.multicast.MulticastChannel;
import com.auce.util.multicast.MulticastChannelEvent;
import com.auce.util.multicast.MulticastChannelListener;

public class ClockRunner extends ComponentSupport 
	implements ClockListener, MulticastChannelListener
{
	protected final Auction		auction;
	protected final Clock		clock;
	protected final Repository	repository;
	protected EventMapper		mapper;
	protected MulticastChannel	channel;
	protected ClockRunTask		task;
	// protected Market			market;

	public ClockRunner( Auction auction, Clock clock, Repository repository )
	{
		super( clock.getId() );
		
		this.auction = auction;
		this.clock = clock;
		this.repository = repository;
		this.mapper = new EventMapper( this.repository );
	
		this.period = Integer.parseInt( System.getProperty( "auction.period" ) );

		this.channel = new MulticastChannel( "clock-" + clock.getId() );
		this.channel.setGroup( clock.resolveInetAddress() );
		this.channel.setPort( clock.getPort() );
		this.channel.setTtl( Integer.parseInt( System.getProperty( "auction.ttl" ) ) );
		this.channel.addChannelListener( this );
		this.channel.start();
	}
	
	protected void beforeRunning ()
	{
		if ( this.repository == null )
		{
			throw new IllegalStateException( "repository not initialized" );
		}
		
		logger.info( "running {} with id {}", 
			this.getClass().getSimpleName(), this.id );
	}
	
	protected void poll ()
	{
		if ( this.channel.isConnected() == false ) return;
	
		if ( this.clock.getRunCount() < auction.getMaxRuns() )
		{
			Run run = AuctionGenerator.createRandomRun( this.clock, this.repository );
			
			if ( run != null )
			{
				this.clock.addRun( run );
			}
		}
		
		if ( this.task == null )
		{
			Run run = this.clock.pollRun();
			
			if ( run != null )
			{
				try
				{
					this.task = new ClockRunTask( run );
					
					String message = this.mapper.write( run );
					
					logger.debug( "starting {}", message );
					
					this.channel.sendMessage( this.clock.getId(), message );
					
					this.task.addClockListener( this );
					
					this.task.start();
				}
				catch( IOException e )
				{
					logger.error( e.getMessage(), e );
				}
			}
		}
	}

	public void handleClockTickEvent ( ClockTickEvent event )
	{
		try
		{
			Run run = event.getRun();
			
			Tick tick = new Tick( run.getLot(), event.getValue() );
			
			this.channel.sendMessage( this.clock.getId(), this.mapper.write( tick ) );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	public void handleClockStopEvent( ClockStopEvent event )
	{
		logger.debug( "run {} stopped", event.getRun() );
		
		if ( this.task != null )
		{
			this.task.removeClockListener( this );
			
			this.task = null;
		}
	}

	public void handleEvent ( MulticastChannelEvent messageEvent )
	{
		String sender = messageEvent.getSender();
		
		if ( this.clock.getId().equals( sender ) ) return;
		
		for ( String text : messageEvent.lines() )
		{
			try
			{
				Event event = this.mapper.read( sender, text );

				if ( event != null && event instanceof Bid )
				{
					Bid bid = (Bid)event;
					
					logger.debug( "bid from {}: {}", sender, text );
					
					// provide bid feedback to Market in order to
					// adjust market price (market price goes up upon bid)
					
					this.auction.getMarket().addBid( bid );
					
					if ( this.task == null )
					{
						// clock is no longer running
						continue;
					}
					
					int clockPrice = this.task.getValue();
					
					if ( clockPrice == 0 )
					{
						// run has ended
						continue;
					}
					
					Run run = this.task.getRun();
					
					Lot lot = run.getLot();
					
					if ( lot.equals( bid.getLot() ) == false )
					{
						// bid shall match running auction

						throw new IllegalStateException( 
							"expected lotId " + lot.getId() + 
							", got " + bid.getLot().getId() );
					}
					
					int bidPrice = bid.getPrice();
					
					if ( bidPrice <=0 || bidPrice > 100 )
					{
						// illegal bid price
						continue;
					}					
					
					int bidQuantity = bid.getQuantity();
					
					int runQuantity = run.getLot().getQuantity();
					
					if ( bidQuantity <= 0 || bidQuantity > runQuantity )
					{
						// bid quantity should be greater than zero and 
						// less than or equal to offered quantity

						throw new IllegalStateException( 
							"expected 0 > bid quantity > " + 
							runQuantity + ", got " + bidQuantity );
					}
					
					// check funds
					
					Trader trader = this.repository.findTrader( sender );

					int purchaseValue = bidQuantity * clockPrice;
					
					if ( trader.getAccount().getBalance() < purchaseValue ) 
					{
						throw new IllegalStateException(
							"trader " + trader.getId() + 
							" has insufficient funds for purchase value " + 
							purchaseValue );
					} 

					// we have a winner, stop the task -- no more bidding
						
					this.task.stop();
					
					// send the purchase message
					
					String referenceNumber = String.format(
						"%1$s%2$s-%3$s-%4$d/%5$d", 
						lot.getId(), 
						run.getId(),
						lot.getProduct().getId(),
						bidQuantity, 
						clockPrice
					);
					
					Purchase purchase = new Purchase( 
						trader, 
						lot, 
						clockPrice,
						bidQuantity,
						this.auction.getAccount().getAccountNumber(),
						referenceNumber
					);
					
					String message = this.mapper.write( purchase );
					
					this.channel.sendMessage( this.clock.getId(), message );
					
					this.auction.addPurchase( purchase );					
				}
			}
			catch( Exception e )
			{
				logger.info( e.getMessage() );
			}
		}
	}

	/*
	public void setMarket ( Market market )
	{
		this.market = market;
	}
	 */

	@Override
	protected void afterRunning() {
		// TODO Auto-generated method stub
		
	}
}
