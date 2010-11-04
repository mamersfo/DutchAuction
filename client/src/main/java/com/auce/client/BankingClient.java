package com.auce.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.entity.Stock;
import com.auce.auction.event.Event;
import com.auce.auction.event.Purchase;
import com.auce.auction.repository.Repository;
import com.auce.bank.Account;
import com.auce.bank.Payment;
import com.auce.bank.Status;
import com.auce.client.bank.Bank;
import com.auce.client.bank.BankListener;
import com.auce.client.bank.Mutation;
import com.auce.client.bank.Withdrawal;
import com.auce.util.multicast.MulticastChannel;

public abstract class BankingClient extends AbstractClient implements BankListener
{
	final static private Logger LOGGER = LoggerFactory.getLogger( BankingClient.class );
	
	private final Bank		bank;

	public BankingClient( Repository repository, Bank bank )
	{
		super( repository );

		this.bank = bank;
		this.bank.addBankListener( this );
	}
	
	public Bank getBank()
	{
		return this.bank;
	}	
	
	public void openAccount()
	{
		this.bank.openAccount( this.trader.getId(), System.getProperty( "trader.city" ) );		
	}
	
	public void handleAccount( Account account )
	{
		this.trader.setAccount( account );
	}
	
	public void handlePayment( Payment payment )
	{
		if ( Status.EXECUTED != payment.getStatus() )
		{
			LOGGER.error( "PAYMENT: {},{},{},{},{}", 
				new Object[] {
					payment.getDebitAccountNumber(),
					payment.getCreditAccountNumber(),
					payment.getDescription(),
					payment.getAmount(),
					payment.getStatus()
				});
		}
	}
	
	public void handleMutation( Mutation mutation )
	{
		LOGGER.info( "{}: {},{},{},{}",
			new Object[] {
				mutation.getClass().getSimpleName().toUpperCase(),
				mutation.getAccountNumber(),
				mutation.getNameOfHolder(),
				mutation.getDescription(),
				mutation.getAmount() 
			});

		if ( mutation instanceof Withdrawal )
		{
			String referenceNumber = mutation.getDescription();
			
			Purchase purchase = this.purchases.get( referenceNumber );
			
			if ( purchase != null )
			{
				this.purchases.remove( referenceNumber );
				
				this.trader.addPurchase( purchase );
				
				Stock stock = this.trader.findStock( purchase.getProduct(), false );
				
				if ( stock != null )
				{
					LOGGER.info( "in STOCK: {},{}", 
						stock.getProduct().getId(),
						stock.getQuantity() );
				}
			}			
		}
	}	
	
	protected void handleEvent( MulticastChannel source, Event event )
	{
		super.handleEvent( source, event );
		
		if ( event != null && event instanceof Purchase && this.bank.isDirectDebit() == false )
		{
			// no direct debit, need to do payment ourselves
			
			Purchase purchase = (Purchase)event;
			
			this.bank.doPayment( 
				purchase.getAccountNumber(),
				purchase.getReferenceNumber(),
				purchase.getPrice() * purchase.getQuantity() );
		}
	}
}
