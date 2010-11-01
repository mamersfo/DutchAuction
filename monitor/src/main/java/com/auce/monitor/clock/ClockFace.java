
package com.auce.monitor.clock;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.entity.Lot;
import com.auce.auction.entity.Product;
import com.auce.auction.entity.Supplier;
import com.auce.auction.entity.Trader;
import com.auce.auction.event.Purchase;
import com.auce.auction.event.Run;

public class ClockFace extends JComponent implements ClockModelListener
{
	private static final long	serialVersionUID	= -4656800951220278621L;

	private static final double	TWO_PI				= 2.0 * Math.PI;
	private static final int	MAX_VALUE			= 100;

	private int					diameter;
	private int					centerX;
	private int					centerY;
	private BufferedImage		image;
	protected int				tickSize;
	protected ClockModel		model;
	protected Logger			logger; 
	protected Font				smallFont;
	protected Font				largeFont;

	public ClockFace( ClockModel model )
	{
		this.logger = LoggerFactory.getLogger( 
			this.getClass().getSimpleName() + "[" + model.getClock().getId() + "]" );
		
		this.model = model;
		
		this.model.addClockModelListener( this );
		
		this.tickSize = 10;
	}

	public ClockModel getModel ()
	{
		return this.model;
	}

	public void paintComponent ( Graphics graphics )
	{
		Graphics2D g2 = (Graphics2D)graphics;

		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

		int w = getWidth();
		int h = getHeight();

		diameter = ( ( w < h ) ? w : h );

		this.tickSize = (int)( 0.02 * diameter );

		centerX = diameter / 2;
		centerY = diameter / 2;

		if ( this.image == null || this.image.getWidth() != w || this.image.getHeight() != h )
		{
			int fontSize = (int)( diameter / 20.0 );
			this.smallFont = new Font( "SansSerif", Font.BOLD, fontSize );

			fontSize = (int)( diameter / 7.0 );
			this.largeFont = new Font( "SansSerif", Font.BOLD, fontSize );

			GraphicsConfiguration gc = g2.getDeviceConfiguration();
			this.image = gc.createCompatibleImage( w, h, Transparency.TRANSLUCENT );
			Graphics2D g = this.image.createGraphics();

			g.setComposite( AlphaComposite.Clear );
			g.fillRect( 0, 0, w, h );
			g.setComposite( AlphaComposite.Src );
			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			g.setColor( Color.WHITE );
			g.fillOval( 0, 0, diameter, diameter );
			g.setComposite( AlphaComposite.SrcAtop );

			Color color = Color.LIGHT_GRAY;
			
			g.setPaint( new GradientPaint( 0, 0, color, 0, h, Color.WHITE ) );

			g.fillOval( 0, 0, diameter, diameter );

			int radius = diameter / 2;

			for ( int i = 0; i < MAX_VALUE; i++ )
			{
				drawTick( g, i / (double)MAX_VALUE, radius - this.tickSize, null );

				if ( i % 5 == 0 )
				{
					drawTick( g, i / (double)MAX_VALUE, radius - (int)( this.tickSize * 2.5 ), Color.DARK_GRAY );
				}
			}

			if ( this.model.getRun() != null )
			{
				this.drawInfo( g );
			}

			g.dispose();

			g2.drawImage( this.image, 0, 0, null );
		}
		else
		{
			g2.drawImage( this.image, null, 0, 0 );
		}

		this.drawValue( g2 );
	}

	private void drawText ( Graphics2D g2, String text, int position )
	{
		Rectangle2D r = this.smallFont.getStringBounds( text, g2.getFontRenderContext() );

		g2.drawString( text, centerX - (int)( (double)r.getWidth() / 2.0 ), centerY + (int)( (double)r.getHeight() / 2.0 ) + (int)( position * r.getHeight() ) );
	}

	private void drawInfo ( Graphics2D g2 )
	{
		g2.setColor( Color.DARK_GRAY );

		g2.setFont( this.smallFont );

		Run auction = this.model.getRun();
		
		Lot lot = auction.getLot();
		
		if ( lot != null )
		{
			Supplier supplier = lot.getSupplier();
			
			if ( supplier != null )
			{
				this.drawText( g2, supplier.getName(), -3 );
			}
			
			Product product = lot.getProduct();
			
			if ( product != null )
			{
				StringBuilder sb = new StringBuilder();
				sb.append( Integer.toString( lot.getQuantity() ) );
				sb.append( " units " );
				sb.append( auction.getLot().getProduct().getName() );
				this.drawText( g2, sb.toString(), -4 );
	
			}
			
			StringBuilder sb = new StringBuilder();
			sb = new StringBuilder();
			sb.append( "Partij " );
			sb.append( lot.getId() );
			this.drawText( g2, sb.toString(), -5 );
		}

		Purchase purchase = this.model.getPurchase();

		if ( purchase != null )
		{
			String traderId = "unknown";
			
			Trader trader = purchase.getTrader();
			
			if ( trader != null )
			{
				traderId = purchase.getTrader().getId();
			}
			
			this.drawText( g2, traderId, 3 );
			
			this.drawText( g2, purchase.getQuantity() + " units", 4 );
		}
		// else if ( this.model.getValue() == 0 )
		// {
		// this.drawText( g2, "doorgedraaid", 3 );
		// this.drawText( g2, auction.getUnits() + " units", 4 );
		// }
	}

	private void drawValue ( Graphics2D g2 )
	{
		int value = this.model.getValue();

		String text = Integer.toString( value );
		
		g2.setColor( this.model.getColor() );

		g2.setFont( this.largeFont );

		Rectangle2D r = this.largeFont.getStringBounds( text, g2.getFontRenderContext() );

		g2.drawString( text, 
			centerX - (int)( (double)r.getWidth() / 2.0 ), 
			centerY + (int)( (double)r.getHeight() / 4.0 ) );

		int handMax = this.diameter / 2;

		double percent = ( (double)value ) / (double)MAX_VALUE;

		drawTick( g2, percent, handMax - this.tickSize, Color.RED );
	}

	private void drawTick ( Graphics2D g2, double percent, int minRadius, Color color )
	{
		double radians = ( 0.5 - percent ) * TWO_PI;

		double sine = Math.sin( radians );

		double cosine = Math.cos( radians );

		int dxmin = centerX + (int)( minRadius * sine );

		int dymin = centerY + (int)( minRadius * cosine );

		if ( color != null )
		{
			g2.setColor( color );

			g2.fillOval( dxmin - 5, dymin - 5, tickSize + 1, tickSize + 1 );
		}
		else
		{
			g2.setColor( Color.WHITE );

			g2.fillOval( dxmin - 5, dymin - 5, tickSize + 1, tickSize + 1 );

			g2.setColor( Color.BLACK );

			g2.drawOval( dxmin - 5, dymin - 5, tickSize, tickSize );
		}
	}

	public void clockValueChanged ( ClockModelEvent event )
	{
		Run auction = this.model.getRun();

		if ( auction != null )
		{
			if ( this.model.getValue() == 0 )
			{
				this.image = null;
			}
		}

		this.repaint();
	}

	public void clockAuctionChanged ( ClockModelEvent event )
	{
		if ( this.model.getRun() != null )
		{
			this.image = null;

			this.repaint();
		}
	}
}
