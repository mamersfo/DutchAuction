
package com.auce.market;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.entity.Product;
import com.auce.auction.entity.Stock;
import com.auce.auction.entity.Trader;
import com.auce.auction.event.InvalidEventException;
import com.auce.auction.event.Offer;
import com.auce.auction.event.Sale;
import com.auce.bank.Account;
import com.auce.bank.Bank;

public class Buyer implements Runnable
{
	final static protected Logger LOGGER = LoggerFactory.getLogger( Buyer.class );

	private final Market	market;
	private final Bank		bank;
	private final Account	account;
	private final Offer		offer;

	public Buyer( Market market, Offer offer )
	{
		this.market = market;
		this.bank = market.getBank();
		this.account = market.getAccount();
		this.offer = offer;
	}

	public void run ()
	{
		if ( this.offer != null )
		{
			try
			{
				Product product = this.offer.getProduct();
				
				Trader trader = this.offer.getTrader();
	
				Stock stock = trader.findStock( product, false );
	
				if ( stock == null )
				{
					throw new IllegalStateException( 
						"trader " + trader.getId() + 
						" has no stock for product " + product.getId() );
				}
	
				int quantity = this.offer.getQuantity();
	
				if ( quantity > stock.getQuantity() )
				{
					throw new InvalidEventException( 
						offer, "offered quantity " + 
						offer.getQuantity() + " exceeds stock quantity " + 
						stock.getQuantity() );
				}
				
				Quoter quoter = this.market.getQuoter( product );
				
				int price = quoter.getPrice();
	
				Sale sale = new Sale( 
					trader, 
					product, 
					price, 
					quantity 
				);
	
				trader.addSale( sale );
	
				this.market.send( sale );
				
				// do payment
				
				this.bank.doPayment( 
					offer.getAccountNumber(), 
					this.account.getAccountNumber(), 
					offer.getReferenceNumber(), 
					price * quantity 
				);
				
				// adjust price, going down
				quoter.setMu( quoter.getMu() - quantity * 0.00001);
			}
			catch( Exception e )
			{
				// logger.error( e.getMessage() );
			}
		}
	}
}
