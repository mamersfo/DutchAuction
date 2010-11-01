package com.auce.client;

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
import com.auce.bank.Account;
import com.auce.bank.Payment;
import com.auce.bank.Status;
import com.auce.client.bank.Bank;
import com.auce.client.bank.BankListener;
import com.auce.client.bank.Mutation;
import com.auce.client.bank.Withdrawal;
import com.auce.client.strategy.PurchasingStrategy;
import com.auce.client.strategy.SellingStrategy;
import com.auce.util.multicast.MulticastChannel;
import com.auce.util.multicast.MulticastChannelEvent;
import com.auce.util.multicast.MulticastChannelListener;

public abstract class AbstractClient 
	implements Client, MulticastChannelListener, BankListener
{
	protected Logger				logger; 
	protected Trader				trader;
	protected Bank					bank;
	protected Repository			repository;
	protected PurchasingStrategy	purchasingStrategy;
	protected SellingStrategy		sellingStrategy;
	protected EventMapper			mapper;
	protected long					magicNumber;
	protected Map<String,Purchase>	purchases;
	private final boolean			directDebit;
	
	public AbstractClient( Repository repository, Bank bank )
	{
		this.purchases = new HashMap<String,Purchase>();
		
		this.directDebit = Boolean.parseBoolean( 
			System.getProperty( "auction.direct.debit" ) );

		String traderId = System.getProperty( "trader.id" );
		
		this.logger = LoggerFactory.getLogger( 
			this.getClass().getSimpleName() + "-" + traderId );		

		this.trader = new Trader( traderId );

		this.repository = repository;
		this.repository.addRepositoryListener( this );
		this.repository.addTrader( trader );			

		this.mapper = new EventMapper( this.repository );

		this.bank = bank;
		this.bank.addBankListener( this );
		this.bank.openAccount( trader.getId(), System.getProperty( "trader.city" ) );
	}	
	
	public PurchasingStrategy getPurchasingStrategy ()
	{
		return this.purchasingStrategy;
	}

	public SellingStrategy getSellingStrategy ()
	{
		return this.sellingStrategy;
	}
	
	public Bank getBank()
	{
		return this.bank;
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
	
	public void handleAccount( Account account )
	{
		trader.setAccount( account );
	}
	
	public void handlePayment( Payment payment )
	{
		if ( Status.EXECUTED != payment.getStatus() )
		{
			logger.error( "PAYMENT: {},{},{},{},{}", 
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
		logger.info( "{}: {},{},{},{}",
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
				
				Stock stock = this.trader.findStock( 
					purchase.getProduct(), false );
				
				if ( stock != null )
				{
					logger.info( "in STOCK: {},{}", 
						stock.getProduct().getId(),
						stock.getQuantity() );
				}
			}			
		}
	}

	public void handleEvent ( MulticastChannelEvent messageEvent )
	{
		String sender = messageEvent.getSender();
		
		if ( sender.equals( this.trader.getId() ) ) return;
		
		MulticastChannel channel = (MulticastChannel)messageEvent.getSource(); 

		for ( String text : messageEvent.lines() )
		{		
			try
			{
				Event event = this.mapper.read( sender, text );
				
				if ( event instanceof Tick )
				{
					Tick tick = (Tick)event;
					
					Bid bid = this.purchasingStrategy.doBid( tick );
					
					if ( bid != null )
					{
						String string = this.mapper.write( bid ) ;
						
						channel.sendMessage( this.trader.getId(), string );
						
						logger.info( "BID: {}, price={}, qty={}", 
							new Object[] {
								bid.getLot().getProduct().getId(),
								bid.getPrice(),
								bid.getQuantity()
							});
					}
				}
				else if ( event instanceof Purchase )
				{
					Purchase purchase = (Purchase)event;
					
					if ( this.trader.equals( purchase.getTrader() ) )
					{
						logger.info( "PURCHASE: {}, price={}, qty={}", 
							new Object[] { 
								purchase.getLot().getProduct().getId(),
								purchase.getPrice(),
								purchase.getQuantity()
							});
						
						String referenceNumber = purchase.getReferenceNumber();
						
						this.purchases.put( referenceNumber, purchase );
						
						if ( this.directDebit == false )
						{
							// no direct debit, need to do payment ourselves
							
							this.bank.doPayment( 
								purchase.getAccountNumber(),
								referenceNumber,
								purchase.getPrice() * purchase.getQuantity() );
						}
						else
						{
							// server will perform direct debit payment
						}
					}
				}
				else if ( event instanceof Quote )
				{
					Quote quote = (Quote)event;
					
					Offer offer = this.sellingStrategy.doOffer( quote );
					
					if ( offer != null )
					{
						String message = this.mapper.write( offer );
						
						channel.sendMessage( this.trader.getId(), message );
						
						logger.info( "OFFER: {}, qty={}, ref={}",
							new Object[] {
								offer.getProduct().getId(), 
								offer.getQuantity(),
								offer.getReferenceNumber()
							}
						);
					}
				}
				else if ( event instanceof Sale )
				{
					Sale sale = (Sale)event;
					
					if ( this.trader.equals( sale.getTrader() ) )
					{
						logger.info( "SALE: {}, price={}, qty={}", new Object[] {
							sale.getProduct().getId(),
							sale.getPrice(),
							sale.getQuantity()
						});
						
						this.trader.addSale( sale );
						
						Stock stock = this.trader.findStock( 
							sale.getProduct(), false );
						
						if ( stock != null )
						{
							logger.info( "in STOCK: {},{}", 
								stock.getProduct().getId(),
								stock.getQuantity() );
						}
					}
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
				
//				logger.error( 
//					"{} with {} ({})", 
//					new Object[] {
//						e.getClass().getSimpleName(), text, e.getMessage()
//					});
			}
		}
	}
	
	public void repositoryChanged ( Entity entity )
	{
		if ( entity instanceof Clock )
		{
			Clock clock = (Clock)entity;
			
			logger.info( "adding clock: {}", clock.getId() );
			
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