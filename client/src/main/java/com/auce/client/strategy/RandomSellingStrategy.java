package com.auce.client.strategy;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.entity.Product;
import com.auce.auction.entity.Stock;
import com.auce.auction.entity.Trader;
import com.auce.auction.event.Offer;
import com.auce.auction.event.Quote;
import com.auce.auction.event.Sale;
import com.auce.bank.Account;
import com.auce.client.Client;
import com.auce.util.Utilities;

public class RandomSellingStrategy implements SellingStrategy
{
	final static protected Logger LOGGER = 
		LoggerFactory.getLogger( RandomSellingStrategy.class );
	
	private final Client		client;
	private final Set<Product>	locks;
	
	public RandomSellingStrategy( Client client )
	{
		this.client = client;
		this.locks = new HashSet<Product>();
	}
	
	protected void lock( Product product )
	{
		this.locks.add( product );
	}
	
	protected void unlock( Product product )
	{
		this.locks.remove( product );
	}
	
	protected boolean isLocked( Product product )
	{
		return this.locks.contains( product );
	}
	
	public void handleSale( Sale sale )
	{
		this.unlock( sale.getProduct() );
	}
	
	public Offer doOffer( Quote quote )
	{
		Offer result = null;
		
		Product product = quote.getProduct();
		
		if ( this.isLocked( product ) == false )
		{
			Trader trader = this.client.getTrader();
			
			Stock stock = trader.findStock( product, false );
			
			if ( stock != null )
			{
				int qty = stock.getQuantity();
				
				String accountNumber = "123456789";
				
				Account account = trader.getAccount();
				
				if ( account != null )
				{
					accountNumber = account.getAccountNumber();
				}
				
				if ( qty > 0 )
				{
					this.lock( product );

					result = new Offer( 
						trader, 
						product, 
						qty,
						accountNumber,
						Long.toString( Utilities.generateLong( 5 ) )
					);
				}
			}			
		}
		
		return result;
	}
}
