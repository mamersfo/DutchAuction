package com.auce.client.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.entity.AuctionListener;
import com.auce.auction.entity.Lot;
import com.auce.auction.event.Bid;
import com.auce.auction.event.Run;
import com.auce.auction.event.Tick;
import com.auce.client.Client;
import com.auce.client.bank.Bank;

public class RandomPurchasingStrategy implements PurchasingStrategy, AuctionListener
{
	final static protected Logger LOGGER = LoggerFactory.getLogger( RandomPurchasingStrategy.class );
	
	private final Client		client;
	protected Map<String,Bid>	bids;
	protected Random			random;
	
	public RandomPurchasingStrategy( Client client )
	{
		this.client = client;
		this.bids = new HashMap<String,Bid>();
		this.random = new Random();
	}
	
	public Bid doBid( Tick tick )
	{
		Bid result = null;
		
		Lot lot = tick.getLot();
		
		Bid bid = this.findBid( lot );
		
		if ( bid != null )
		{
			if ( bid.getQuantity() > 0 )
			{
				if ( tick.getValue() <= bid.getPrice() )
				{
					long amount = bid.getPrice() * bid.getQuantity();
					
					Bank bank = this.client.getBank();
					
					if ( bank.getBalance() > amount )
					{
						result = bid;
					}
					else
					{
						LOGGER.debug( "not enough money, got {}, need {}",
							bank.getBalance(), amount );
					}
				}
			}
			else
			{
				LOGGER.debug( "bid quantity is less than 0" );
			}
		}
		else
		{
			LOGGER.debug( "no bid found for " + lot );
		}
		
		return result;
	}
	
	public Bid findBid( Lot lot )
	{
		return this.bids.get( lot.getId() );
	}
	
	public void removeBid( Lot lot )
	{
		this.bids.remove( lot.getId() );
	}
	
	public void addBid( Bid bid )
	{
		this.bids.put( bid.getLot().getId(), bid );
	}
	
	public void handleRun( Run run )
	{	
		Lot lot = run.getLot();
		
		Bid bid = this.findBid( lot );
		
		if ( bid != null )
		{
			if ( lot.getQuantity() <= 0 )
			{
				this.removeBid( lot );
			}
			else
			{
				int qty = 0;
				
				if ( lot.getQuantity() < 50 ) qty = lot.getQuantity();
				else qty = this.random.nextInt( lot.getQuantity() );
				
				bid.setQuantity( qty );
			}
		}
		else if ( lot.getQuantity() > 0 )
		{
			int qty = 0;
			
			if ( lot.getQuantity() < 50 ) qty = lot.getQuantity();
			else qty = this.random.nextInt( lot.getQuantity() );

			bid = new Bid( 
				this.client.getTrader(), 
				lot, 
				this.random.nextInt( 99 ) + 1, 
				qty 
			);
			
			this.addBid( bid );
		}		
	}

	public void auctionsChanged ()
	{
	}
}
