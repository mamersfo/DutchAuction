package com.auce.auction.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.entity.EntityNotFoundException;
import com.auce.auction.repository.Repository;
import com.auce.util.Mapper;

import com.auce.auction.event.Event.Type;

public class EventMapper implements Mapper<Event>
{
	final static protected Logger LOGGER = LoggerFactory.getLogger( EventMapper.class );
	
	protected Repository	repository;
	
	public EventMapper( Repository repository )
	{
		this.repository = repository;
	}
	
	public Event read( final String sender, String text )
	{
		Event result = null;
		
		if ( sender == null )
		{
			throw new IllegalArgumentException( "expected sender argument" );			
		}
		
		if ( text == null )
		{
			throw new IllegalArgumentException( "expected text argument" );
		}

		final String input = text.trim();

		if ( input.length() == 0 )
		{
			throw new IllegalArgumentException( 
				"expected non-zero length text argument" );
		}
		
		Type type = null;
		
		try
		{			
			String[] items = input.split( "," );
			
			type = Type.valueOf( items[0] );

			switch( type )
			{
				case BID:
					result = new Bid( 
						this.repository.findTrader( sender ),		// trader
						this.repository.findLot( items[1] ), 		// lot
						Integer.parseInt( items[2] ), 				// price
						Integer.parseInt( items[3] )				// quantity
					);
					break;
				case OFFER:
					result = new Offer( 
						this.repository.findTrader( sender ),		// trader
						this.repository.findProduct( items[1] ), 	// product
						Integer.parseInt( items[2] ), 				// quantity
						items[3],									// accountNumber
						items[4]									// referenceNumber
					);
					break;
				case PURCHASE:
					result = new Purchase( 
						this.repository.findTrader( items[1] ), 	// trader
						this.repository.findLot( items[2] ), 		// lot
						Integer.parseInt( items[3] ), 				// price
						Integer.parseInt( items[4] ), 				// quantity
						items[5], 									// accountNumber
						items[6] 									// referenceNumber
					);
					break;
				case QUOTE:
					result = new Quote( 
						this.repository.findProduct( items[1] ), 	// product
						Integer.parseInt( items[2] ) 				// price
					);
					break;
				case RUN:
					result = new Run( 
						items[1], 									// runId
						this.repository.findClock( items[2] ), 		// clock
						this.repository.findLot( items[3] ) 		// lot
					);
					break;
				case SALE:
					result = new Sale( 
						this.repository.findTrader( items[1] ), 	// trader
						this.repository.findProduct( items[2] ), 	// product
						Integer.parseInt( items[3] ), 				// price
						Integer.parseInt( items[4] ) 				// quantity
					);
					break;
				case TICK:
					result = new Tick( 
						this.repository.findLot( items[1] ), 		// lot
						Integer.parseInt( items[2] ) 				// value
					);			
					break;
				default:
					break;
			}			
		}
		catch( EntityNotFoundException e )
		{
			LOGGER.debug( "Event {}: {}", type, e.getMessage() );
		}		

		return result;		
	}
	
	public String write( Event event )
	{
		String result = null;

		if ( event instanceof Run )
		{
			Run run = (Run)event;
			
			result = String.format( "%1$s,%2$s,%3$s,%4$s",
				run.getType(), 
				run.getId(), 
				run.getClock().getId(), 
				run.getLot().getId() 
			);
		}
		else if ( event instanceof Bid )
		{
			Bid bid = (Bid)event;
			
			result = String.format( "%1$s,%2$s,%3$d,%4$d",
				bid.getType(), 
				bid.getLot().getId(), 
				bid.getPrice(), 
				bid.getQuantity() 
			);
		}
		else if ( event instanceof Tick )
		{
			Tick tick = (Tick)event;
			
			result = String.format( "%1$s,%2$s,%3$d", 
				tick.getType(), 
				tick.getLot().getId(), 
				tick.getValue() 
			);
		}
		else if ( event instanceof Purchase )
		{
			Purchase purchase = (Purchase)event;
			
			result = String.format( "%1$s,%2$s,%3$s,%4$d,%5$d,%6$s,%7$s",
				purchase.getType(),
				purchase.getTrader().getId(),
				purchase.getLot().getId(),
				purchase.getPrice(),
				purchase.getQuantity(),
				purchase.getAccountNumber(),
				purchase.getReferenceNumber()
			);
		}
		else if ( event instanceof Offer )
		{
			Offer offer = (Offer)event;
			
			result = String.format( "%1$s,%2$s,%3$d,%4$s,%5$s", 
				offer.getType(),
				offer.getProduct().getId(),
				offer.getQuantity(),
				offer.getAccountNumber(),
				offer.getReferenceNumber()
			);
		}
		else if ( event instanceof Quote )
		{
			Quote quote = (Quote)event;
			
			result = String.format( "%1$s,%2$s,%3$d", 
				quote.getType(),
				quote.getProduct().getId(),
				quote.getPrice()
			);
		}
		else if ( event instanceof Sale )
		{
			Sale sale = (Sale)event;
			
			result = String.format( "%1$s,%2$s,%3$s,%4$d,%5$d",
				sale.getType(),
				sale.trader().getId(),
				sale.getProduct().getId(),
				sale.getPrice(),
				sale.getQuantity()
			);
		}

		return result;
	}	
}
