
package com.auce.auction.repository.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.dao.ClockDao;
import com.auce.auction.dao.LotDao;
import com.auce.auction.dao.ProductDao;
import com.auce.auction.dao.SupplierDao;
import com.auce.auction.dao.TraderDao;
import com.auce.auction.dao.impl.ClockDaoMemoryImpl;
import com.auce.auction.dao.impl.LotDaoMemoryImpl;
import com.auce.auction.dao.impl.ProductDaoMemoryImpl;
import com.auce.auction.dao.impl.SupplierDaoMemoryImpl;
import com.auce.auction.dao.impl.TraderDaoMemoryImpl;
import com.auce.auction.entity.Clock;
import com.auce.auction.entity.Entity;
import com.auce.auction.entity.EntityExistsException;
import com.auce.auction.entity.IllegalEntityException;
import com.auce.auction.entity.Lot;
import com.auce.auction.entity.Product;
import com.auce.auction.entity.Supplier;
import com.auce.auction.entity.Trader;
import com.auce.auction.repository.Repository;
import com.auce.auction.repository.RepositoryListener;

public class RepositoryMemoryImpl implements Repository
{
	final static protected Logger LOGGER = 
		LoggerFactory.getLogger( RepositoryMemoryImpl.class );

	protected List<RepositoryListener>	listeners;
	protected ClockDao					clockDao;
	protected ProductDao				productDao;
	protected SupplierDao				supplierDao;
	protected LotDao					lotDao;
	protected TraderDao					traderDao;
	protected Boolean					lock;

	public RepositoryMemoryImpl()
	{
		this.lock = Boolean.TRUE;
		this.listeners = new LinkedList<RepositoryListener>();

		this.clockDao = new ClockDaoMemoryImpl();
		this.productDao = new ProductDaoMemoryImpl();
		this.supplierDao = new SupplierDaoMemoryImpl();
		this.lotDao = new LotDaoMemoryImpl();
		this.traderDao = new TraderDaoMemoryImpl();
	}

	// Products

	public int countProducts ()
	{
		return this.productDao.count();
	}

	public Product findProduct ( String productId )
	{
		return this.productDao.find( productId );
	}

	public Product[] listProducts ()
	{
		return (Product[])this.productDao.list();
	}

	public void addProduct ( Product product ) throws EntityExistsException, IllegalEntityException
	{
		this.productDao.add( product );
		this.fireRepositoryChanged( product );
	}

	// Clock

	public int countClocks ()
	{
		return this.clockDao.count();
	}

	public void addClock ( Clock clock ) throws EntityExistsException, IllegalEntityException
	{
		this.clockDao.add( clock );
		this.fireRepositoryChanged( clock );
	}

	public Clock[] listClocks ()
	{
		return (Clock[])this.clockDao.list();
	}

	public Clock findClock ( String id )
	{
		return this.clockDao.find( id );
	}

	// Lot

	public Lot findLot ( String lotId )
	{
		return this.lotDao.find( lotId );
	}

	public void addLot ( Lot lot ) throws EntityExistsException, IllegalEntityException
	{
		this.lotDao.add( lot );
		this.fireRepositoryChanged( lot );
	}

	// Traders

	public Trader findTrader ( String traderId )
	{
		return this.traderDao.find( traderId );
	}

	public void addTrader ( Trader trader ) throws EntityExistsException, IllegalEntityException
	{
		this.traderDao.add( trader );
		
		this.fireRepositoryChanged( trader );
	}
	
	public void updateTrader( Trader trader )
	{
		this.traderDao.update( trader );
	}
	
	public int countTraders()
	{
		return this.traderDao.count();
	}
	
	public Trader[] listTraders()
	{
		return (Trader[])this.traderDao.list();
	}

	// Supplier

	public int countSuppliers ()
	{
		return this.supplierDao.count();
	}

	public Supplier[] listSuppliers ()
	{
		return (Supplier[])this.supplierDao.list();
	}

	public Supplier findSupplier ( String id )
	{
		return this.supplierDao.find( id );
	}

	public void addSupplier ( Supplier supplier ) throws EntityExistsException, IllegalEntityException
	{
		this.supplierDao.add( supplier );
		this.fireRepositoryChanged( supplier );
	}

	/*

	public Event deserialize ( String text )
	{
		if ( text == null )
		{
			throw new IllegalArgumentException( "expected string argument" );
		}

		text = text.trim();

		if ( text.length() == 0 )
		{
			throw new IllegalArgumentException( "expected non-zero length string argument" );
		}

		Event result = null;

		if ( text.startsWith( Banking.BALANCE ) || text.startsWith( Banking.TRANSFER ) )
		{
			// Banking Protocol

			String[] items = text.split( "( |\t|\r|\n)+" );

			if ( text.startsWith( Banking.BALANCE ) )
			{
				result = new BalanceRequest( items[1] );
			}
			else if ( text.startsWith( Banking.TRANSFER ) )
			{
				result = new Transfer( items[1], items[2], Integer.parseInt( items[3] ) );
			}
		}
		else if ( text.startsWith( Trading.PURCHASE ) || text.startsWith( Trading.SALE ) )
		{
			// Trading Protocol, purchases and sales

			String[] items = text.split( "," );

			String traderId = items[1];

			Trader trader = null;
			
			try
			{
				trader = this.findTrader( traderId );
			}
			catch( EntityNotFoundException e )
			{
				trader = new Trader( items[1] );
				this.addTrader( trader );
			}
			
			if ( text.startsWith( Trading.PURCHASE ) )
			{
				Lot lot = this.findLot( items[2] );
				int price = Integer.parseInt( items[3] );
				int quantity = Integer.parseInt( items[4] );
				result = new Purchase( trader, lot, price, quantity );
			}			
			else if ( text.startsWith( Trading.SALE ) )
			{
				Product product = this.findProduct( items[2] );
				int price = Integer.parseInt( items[3] );
				int quantity = Integer.parseInt( items[4] );
				result = new Sale( trader, product, price, quantity );
			}
			
		}
		else if ( text.startsWith( Trading.OFFER ) || text.startsWith( Trading.QUOTE ) )
		{
			// Trading Protocol, quotes and offers

			String[] items = text.split( "," );

			if ( text.startsWith( Trading.OFFER ) )
			{
				Product product = this.findProduct( items[1] );
				int quantity = Integer.parseInt( items[2] );
				result = new Offer( product, quantity );
			}

			else if ( text.startsWith( Trading.QUOTE ) )
			{
				Product product = this.findProduct( items[1] );
				int price = Integer.parseInt( items[2] );
				result = new Quote( product, price );
			}
		}
		else if ( text.startsWith( Messaging.MESSAGE ) || text.startsWith( Messaging.LOGON ) || text.startsWith( Messaging.LOGOFF ) )
		{
			// Messaging Protocol

			if ( text.startsWith( Messaging.MESSAGE ) )
			{
				result = new Message( text.substring( Messaging.MESSAGE.length() ).trim() );
			}
			else if ( text.startsWith( Messaging.LOGON ) )
			{
				result = new Logon( text.substring( Messaging.LOGON.length() ).trim() );
			}
			else if ( text.startsWith( Messaging.LOGOFF ) )
			{
				result = new Logoff( text.substring( Messaging.LOGOFF.length() ).trim() );
			}
		}
		else
		{
			// Auction Protocol

			String[] items = text.split( "," );

			if ( text.startsWith( Auctioning.AUCTION ) )
			{
				String auctionId = items[1];
				Clock clock = this.findClock( items[2] );
				Lot lot = this.findLot( items[3] );
				result = new Auction( auctionId, clock, lot );
			}
			else if ( text.startsWith( Auctioning.BID ) )
			{
				String lotId = items[1];
				int price = Integer.parseInt( items[2] );
				int quantity = Integer.parseInt( items[3] );
				Lot lot = this.findLot( lotId );
				result = new Bid( lot, price, quantity );
			}
			else if ( text.startsWith( Auctioning.PRICE ) )
			{
				String lotId = items[1];
				int value = Integer.parseInt( items[2] );
				Lot lot = this.findLot( lotId );
				result = new Price( lot, value );
			}
		}

		return result;
	}

	public String serialize ( Event event )
	{
		StringBuilder sb = new StringBuilder();

		if ( event instanceof Auctioning )
		{
			if ( event instanceof Auction )
			{
				Auction auction = (Auction)event;
				sb.append( Auctioning.AUCTION );
				sb.append( Constants.COMMA );
				sb.append( auction.getId() );
				sb.append( Constants.COMMA );
				sb.append( auction.getClock().getId() );
				sb.append( Constants.COMMA );
				sb.append( auction.getLot().getId() );
			}
			else if ( event instanceof Bid )
			{
				Bid bid = (Bid)event;
				sb.append( Auctioning.BID );
				sb.append( Constants.COMMA );
				sb.append( bid.getLot().getId() );
				sb.append( Constants.COMMA );
				sb.append( bid.getPrice() );
				sb.append( Constants.COMMA );
				sb.append( bid.getQuantity() );
			}
			else if ( event instanceof Price )
			{
				Price price = (Price)event;
				sb.append( Auctioning.PRICE );
				sb.append( Constants.COMMA );
				sb.append( price.getLot().getId() );
				sb.append( Constants.COMMA );
				sb.append( price.getValue() );
			}
		}
		else if ( event instanceof Banking )
		{
			if ( event instanceof BalanceRequest )
			{
				BalanceRequest balance = (BalanceRequest)event;
				sb.append( Banking.BALANCE );
				sb.append( Constants.SPACE );
				sb.append( balance.getAccountNumber() );
			}
			else if ( event instanceof Transfer )
			{
				Transfer transfer = (Transfer)event;
				sb.append( Banking.TRANSFER );
				sb.append( Constants.SPACE );
				sb.append( transfer.getDebitAccountNumber() );
				sb.append( Constants.SPACE );
				sb.append( transfer.getCreditAccountNumber() );
				sb.append( Constants.SPACE );
				sb.append( transfer.getAmount() );
			}
		}
		else if ( event instanceof Messaging )
		{
			if ( event instanceof Message )
			{
				Message message = (Message)event;
				sb.append( Messaging.MESSAGE );
				sb.append( Constants.SPACE );
				sb.append( message.getText() );
			}
			else if ( event instanceof Logon )
			{
				Logon logon = (Logon)event;
				sb.append( Messaging.LOGON );
				sb.append( Constants.SPACE );
				sb.append( logon.getUserId() );
			}
			else if ( event instanceof Logoff )
			{
				Logoff logoff = (Logoff)event;
				sb.append( Messaging.LOGON );
				sb.append( Constants.SPACE );
				sb.append( logoff.getUserId() );
			}
		}
		else if ( event instanceof Trading )
		{
			if ( event instanceof Purchase )
			{
				Purchase purchase = (Purchase)event;

				sb.append( Trading.PURCHASE );
				sb.append( Constants.COMMA );
				sb.append( purchase.getTrader().getId() );
				sb.append( Constants.COMMA );
				sb.append( purchase.getLot().getId() );
				sb.append( Constants.COMMA );
				sb.append( purchase.getPrice() );
				sb.append( Constants.COMMA );
				sb.append( purchase.getQuantity() );
			}
			else if ( event instanceof Offer )
			{
				Offer offer = (Offer)event;

				sb.append( Trading.OFFER );
				sb.append( Constants.COMMA );
				sb.append( offer.getProduct().getId() );
				sb.append( Constants.COMMA );
				sb.append( offer.getQuantity() );
			}
			else if ( event instanceof Quote )
			{
				Quote quote = (Quote)event;
				sb.append( Trading.QUOTE );
				sb.append( Constants.COMMA );
				sb.append( quote.getProduct().getId() );
				sb.append( Constants.COMMA );
				sb.append( quote.getPrice() );
			}
			else if ( event instanceof Sale )
			{
				Sale sale = (Sale)event;
				sb.append( Trading.SALE );
				sb.append( Constants.COMMA );
				sb.append( sale.trader().getId() );
				sb.append( Constants.COMMA );
				sb.append( sale.getProduct().getId() );
				sb.append( Constants.COMMA );
				sb.append( sale.getPrice() );
				sb.append( Constants.COMMA );
				sb.append( sale.getQuantity() );
			}
		}

		return sb.toString();
	}
	 */

	public void addRepositoryListener ( RepositoryListener l )
	{
		this.listeners.add( l );
	}

	public void removeRepositoryListener ( RepositoryListener l )
	{
		this.listeners.remove( l );
	}

	protected void fireRepositoryChanged ( Entity entity )
	{
		for ( RepositoryListener l : this.listeners )
		{
			l.repositoryChanged( entity );
		}
	}

	protected InputStream openStream ( String entity ) throws IOException
	{
		StringBuilder sb = new StringBuilder();

		sb.append( "com/auce/entity/" );
		sb.append( entity );
		sb.append( ".csv" );

		String path = sb.toString();

		// logger.info( "reading {}", path );

		return ClassLoader.getSystemResourceAsStream( path );
	}
}
