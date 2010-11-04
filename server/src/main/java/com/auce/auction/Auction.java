package com.auce.auction;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import com.auce.auction.entity.Clock;
import com.auce.auction.entity.Lot;
import com.auce.auction.entity.Trader;
import com.auce.auction.event.Purchase;
import com.auce.auction.event.Run;
import com.auce.auction.repository.Repository;
import com.auce.bank.Account;
import com.auce.bank.AccountType;
import com.auce.bank.BankFacade;
import com.auce.bank.Deposit;
import com.auce.bank.Mutation;
import com.auce.market.Market;
import com.auce.util.Sequence;
import com.auce.util.component.ComponentSupport;

public class Auction extends ComponentSupport
{
	private final Repository			repository;
	private final Market				market;
	private final BankFacade			bank;
	private final Account				account;
	private final int					maxRuns;
	private final Map<String,Purchase>	purchases;
	
	public Auction( String id, Repository repository, Market market, BankFacade bank )
	{
		super( id );
		
		this.repository = repository;
		
		this.market = market;
		
		this.bank = bank;
		String accountNumber = this.bank.issueAccountNumber();
		this.bank.openAccount( AccountType.CHECKING, accountNumber, id, "Westland" );
		this.account = bank.findAccount( accountNumber );
		
		this.maxRuns = Integer.parseInt( System.getProperty( "auction.max.runs" ) );
		
		this.period = Integer.parseInt( System.getProperty( "auction.period" ) );
		
		this.purchases = new HashMap<String,Purchase>();
	}
	
	public Market getMarket()
	{
		return this.market;
	}
	
	public void addPurchase( Purchase purchase )
	{
		logger.debug( "addPurchase() - {}", purchase.getReferenceNumber() );
		
		if ( this.bank.isDirectDebit() )
		{
			Trader trader = purchase.getTrader();
			
			// payment
			this.bank.doPayment( 
				this.account.getAccountNumber(), 
				trader.getAccount().getAccountNumber(), 
				purchase.getReferenceNumber(), 
				purchase.getValue() );
			
			// add to trader stock
			trader.addPurchase( purchase );
			
			// re-auction if needed
			Lot lot = purchase.getLot();
			lot.handlePurchase( purchase );
			this.doAuction( lot );
		}
		else
		{
			this.purchases.put( purchase.getReferenceNumber(), purchase );
		}
	}
	
	public void doAuction( Lot lot )
	{
		if ( lot.getQuantity() > 0 )
		{	
			Clock[] clocks = this.repository.listClocks();
			
			int index = (int)Math.round( Math.random() * ( clocks.length - 1 ) );
			
			Clock clock = clocks[index];
			
			Run run = new Run( 
				Sequence.getInstance().next( Run.PREFIX ), 
				clock, 
				lot 
			);

			clock.addRun( run );
		}
	}
	
	public void settlePurchase( Purchase purchase, Deposit deposit )
	{
		String referenceNumber = purchase.getReferenceNumber();
		long purchaseAmount = purchase.getValue();
		long depositAmount = deposit.getAmount();
		
		logger.debug( "settlePurchase() - {}, {}, {}", 
			new Object[] {
				referenceNumber, 
				deposit.getAccountNumber(),
				depositAmount
			}
		);
		
		if ( depositAmount >= purchaseAmount )
		{
			// remove purchase from standing orders
			this.purchases.remove( referenceNumber );
			
			// add product to trader stock
			purchase.getTrader().addPurchase( purchase );
			
			// add purchase to lot
			Lot lot = purchase.getLot();
			lot.handlePurchase( purchase );
			if ( lot.getQuantity() > 0 ) this.doAuction( lot );
						
			if ( depositAmount > purchaseAmount )
			{
				// pay back difference
				
				this.bank.doPayment( 
					deposit.getAccountNumber(), 
					this.account.getAccountNumber(), 
					referenceNumber, 
					depositAmount - purchaseAmount
				);
			}
		}
		else if ( depositAmount > 0 )
		{
			// payment not sufficient, pay back full deposit amount
			
			this.bank.doPayment( 
				deposit.getAccountNumber(), 
				this.account.getAccountNumber(), 
				referenceNumber, 
				depositAmount
			);
			
			// re-auction the lot
			
			this.doAuction( purchase.getLot() );
		}
	}
	
	public int getMaxRuns()
	{
		return this.maxRuns;
	}
	
	public Account getAccount()
	{
		return this.account;
	}
	
	@Override
	protected void poll ()
	{
		if ( this.bank.isDirectDebit() == false )
		{
			List<Mutation> mutations = this.account.listMutations();
			
			for ( Mutation mutation : mutations )
			{
				if ( mutation instanceof Deposit )
				{
					String referenceNumber = mutation.getDescription();
					
					Purchase purchase = this.purchases.get( referenceNumber );
					
					if ( purchase != null )
					{
						this.settlePurchase( purchase, (Deposit)mutation );
					}
				}
			}
		}
	}

	@Override
	protected void afterRunning() 
	{
		// TODO Auto-generated method stub	
	}

	@Override
	protected void beforeRunning() 
	{
		// TODO Auto-generated method stub	
	}
}
