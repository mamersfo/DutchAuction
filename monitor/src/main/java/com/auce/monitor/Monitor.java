package com.auce.monitor;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.entity.AuctionProxy;
import com.auce.auction.entity.Clock;
import com.auce.auction.entity.Entity;
import com.auce.auction.entity.Product;
import com.auce.auction.entity.Trader;
import com.auce.auction.repository.Repository;
import com.auce.auction.repository.RepositoryListener;
import com.auce.auction.repository.impl.RepositoryMemoryImpl;
import com.auce.monitor.clock.ClockFace;
import com.auce.monitor.clock.ClockModel;
import com.auce.monitor.graph.Graph;
import com.auce.monitor.graph.GraphModel;
import com.auce.monitor.trader.TraderModel;
import com.auce.monitor.trader.TraderPanel;
import com.auce.util.Utilities;

public class Monitor extends JFrame implements RepositoryListener
{
	private static final long serialVersionUID = -6197471409200302104L;
	
	final static protected Logger LOGGER = LoggerFactory.getLogger( Monitor.class );
	
	protected AuctionProxy	auction;
	protected Controller	controller;
	protected JPanel		clockPanel;
	protected JPanel		marketPanel;
	protected JPanel		tradePanel;
	protected JPanel		centerPanel;
	
	public Monitor( String title )
	{
		super( title );
		
		try
		{
			Repository repository = new RepositoryMemoryImpl();
			repository.addRepositoryListener( this );
			
			this.auction = new AuctionProxy( repository );
			this.auction.start();

			this.controller = new Controller( repository );

			// Panels
			
			this.marketPanel = new JPanel(  new GridLayout( 1, 7, 8, 8 ) );
			this.marketPanel.setPreferredSize( new Dimension( -1, 140 ) );

			this.clockPanel = new JPanel( new GridLayout( 1, 2, 8, 8) );		
			this.clockPanel.setPreferredSize( new Dimension( -1, 400 ) );
			
			this.tradePanel = new JPanel( new GridLayout( 1, -1, 8, 8 ) );
			this.tradePanel.setPreferredSize( new Dimension( -1, 200 ) );
			
			// Layout
			
			this.setLayout( new BorderLayout( 4, 4 ) );
			
			this.getContentPane().add( this.marketPanel, BorderLayout.NORTH );

			this.centerPanel = new JPanel(new BorderLayout());
			
			this.centerPanel.add( this.clockPanel, BorderLayout.CENTER );

			JSplitPane splitPane = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, this.centerPanel, this.tradePanel );

			this.getContentPane().add( splitPane, BorderLayout.CENTER );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public AuctionProxy getAuction()
	{
		return this.auction;
	}
	
	public Controller getController()
	{
		return controller;
	}

	public void repositoryChanged ( Entity entity )
	{
		if ( entity instanceof Clock )
		{
			this.getContentPane().invalidate();
			this.centerPanel.invalidate();

			Clock clock = (Clock)entity;
			ClockModel clockModel = this.controller.addClock( clock );
			this.auction.addAuctionListener( clockModel );

			this.clockPanel.invalidate();
			ClockFace clockFace = new ClockFace( clockModel );
			this.clockPanel.add( clockFace );
			this.clockPanel.validate();
			
			// List
			
			JTable table = new JTable( clockModel );
			table.setBackground( null );
			JScrollPane scrollPane = new JScrollPane( table );
			scrollPane.setPreferredSize( new Dimension( 140, -1 ) );
			
			if ( "C1".equals( clock.getId() ) )
			{
				this.centerPanel.add( scrollPane, BorderLayout.WEST );
			}
			else if ( "C2".equals( clock.getId() ) )
			{
				this.centerPanel.add( scrollPane, BorderLayout.EAST );
			}
			
			this.centerPanel.validate();
			
			this.getContentPane().validate();
		}
		else if ( entity instanceof Product )
		{
			this.marketPanel.invalidate();

			Product product = (Product)entity;
			
			GraphModel model = this.controller.addProduct( product );
			
			this.marketPanel.add( new Graph( model ) );
			
			this.marketPanel.validate();			
		}
		else if ( entity instanceof Trader )
		{
			LOGGER.info( "added trader: {}", ((Trader) entity).getId() );
			
			this.tradePanel.invalidate();
			
			TraderModel model = this.controller.addTrader( (Trader)entity );
			
			TraderPanel panel = new TraderPanel( model );
			
			this.tradePanel.add( panel );
			
			this.tradePanel.validate();
		}
		else if ( entity instanceof Product )
		{
			LOGGER.info( "added product: {}", entity );
		}
	}
	
	public static void main ( String[] args )
	{
		try
		{
			Utilities.loadProperties( "monitor.conf" );

			final Monitor frame = new Monitor( System.getProperty( "monitor.title" ) );
			
			// frame
			
			frame.setSize( 
				Integer.parseInt(  System.getProperty( "monitor.window.width", "1024" ) ), 
				Integer.parseInt(  System.getProperty( "monitor.window.height", "760" ) ) );
			
			frame.addWindowListener( new WindowAdapter()
			{
				public void windowClosing ( WindowEvent e )
				{
					frame.dispose();
				}

				public void windowClosed( WindowEvent e )
				{
					System.exit( 0 );
				}
			});
			
			frame.setVisible( true );			
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}	
}
