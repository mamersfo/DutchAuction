package com.auce.auction.entity;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.event.Run;
import com.auce.auction.repository.Repository;
import com.auce.util.component.ComponentSupport;

public class AuctionProxy extends ComponentSupport
{
	private static final long serialVersionUID = -3578719716874133867L;
	
	final static protected Logger TIMER = LoggerFactory.getLogger( AuctionProxy.class );
	
	protected Repository				repository;
	protected List<AuctionListener>		listeners;
	protected URL						serverUrl;
	
	public AuctionProxy( Repository repository )
		throws MalformedURLException
	{
		super( "auction" );
		
		this.repository = repository;
		this.listeners = new LinkedList<AuctionListener>();
		this.serverUrl = new URL( System.getProperty( "auction.url" ) );
		this.period = Integer.parseInt( System.getProperty( "auction.period" ) );
	}
	
	public URL getServerUrl()
	{
		return this.serverUrl;
	}
	
	public void setServerUrl( URL url )
	{
		this.serverUrl = url;
	}
	
	public void addAuctionListener( AuctionListener l )
	{
		this.listeners.add( l );
	}
	
	public void removeAuctionListener( AuctionListener l )
	{
		this.listeners.remove( l );
	}
	
	protected Entity transform ( Element element )
	{
		Entity result = null;

		String name = element.getName();
		
		if ( "Run".equals( name ) )
		{
			String auctionId = element.attribute( "Id" ).getText();

			Clock clock = (Clock)this.transform( element.element( "Clock" ) );
			
			Lot lot = (Lot)this.transform( element.element( "Lot" ) );

			Run auction = new Run( auctionId, clock, lot );

			clock.addRun( auction );

			result = auction;
			
			for ( AuctionListener l : this.listeners )
			{
				l.handleRun( auction );
			}
		}
		else if ( "Clock".equals( name ) )
		{
			String clockId = element.attribute( "Id" ).getText();

			Clock clock = null;
			
			try
			{
				clock = this.repository.findClock( clockId );
			}
			catch( EntityNotFoundException e )
			{
				String group = element.element( "Group" ).getText();

				int port = Integer.parseInt( element.element( "Port" ).getText() );

				clock = new Clock( clockId, group, port );

				this.repository.addClock( clock );
			}

			result = clock;
		}
		else if ( "Lot".equals( name ) )
		{
			String lotId = element.attribute( "Id" ).getText();
			
			int quantity = Integer.parseInt( element.element( "Quantity" ).getText() );

			Lot lot = null;
			
			try
			{
				lot = this.repository.findLot( lotId );
			}
			catch( EntityNotFoundException e )
			{
				Product product = (Product)this.transform( element.element( "Product" ) );
				Supplier supplier = (Supplier)this.transform( element.element( "Supplier" ) );
				lot = new Lot( lotId, supplier, product );
				this.repository.addLot( lot );
			}

			lot.setQuantity( quantity );

			result = lot;
		}
		else if ( "Product".equals( name ) )
		{
			String productId = element.attribute( "Id" ).getText();

			Product product = null;
			
			try
			{
				product = this.repository.findProduct( productId );
			}
			catch( EntityNotFoundException e )
			{
				product = new Product( 
					productId, 
					element.element( "Name" ).getText(), 
					element.element( "Color" ).getText() );

				this.repository.addProduct( product );
			}

			result = product;
		}
		else if ( "Supplier".equals( name ) )
		{
			String supplierId = element.attribute( "Id" ).getText();

			Supplier supplier = null;
			
			try
			{
				supplier = this.repository.findSupplier( supplierId );
			}
			catch( EntityNotFoundException e )
			{
				supplier = new Supplier( supplierId, element.element( "Name" ).getText() );

				this.repository.addSupplier( supplier );
			}

			result = supplier;
		}

		return result;
	}	
	
	@SuppressWarnings("unchecked")
	public void poll()
	{
		InputStream inputStream = null;
		
		try
		{
			Clock[] clocks = this.repository.listClocks();
			
			for ( int i=0; i < clocks.length; i++ )
			{
				clocks[i].removeAllRuns();
			}

			inputStream = this.serverUrl.openStream();
			
			SAXReader reader = new SAXReader();
			
			Document document = reader.read( inputStream );
			
			List<Element> elements = 
				(List<Element>)document.getRootElement().elements();
			
			for ( Element element: elements )
			{
				try
				{
					this.transform( element );
				}
				catch( Exception e )
				{
					TIMER.error( e.getMessage(), e );
				}
			}

			for ( AuctionListener l : this.listeners )
			{
				l.auctionsChanged();
			}
		}
		catch ( ConnectException e )
		{
			// logger.error( e.getMessage(), e );
		}
		catch ( IOException e )
		{
			TIMER.error( e.getMessage(), e );
		}
		catch ( DocumentException e )
		{
			TIMER.error( e.getMessage(), e );
		}
		catch( Exception e )
		{
			TIMER.error( e.getMessage(), e );
		}
		finally
		{
			if ( inputStream != null )
			{
				try
				{
					inputStream.close();
				}
				catch( IOException e )
				{
				}
			}
		}
	}

	@Override
	protected void afterRunning() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeRunning() {
		// TODO Auto-generated method stub
		
	}
}
