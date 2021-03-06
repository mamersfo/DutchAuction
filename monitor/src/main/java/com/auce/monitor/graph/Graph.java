package com.auce.monitor.graph;

import java.awt.AlphaComposite;



import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Graph extends JComponent implements GraphModelListener
{
	private static final long	serialVersionUID	= -3861270211356134057L;
	
	final static protected Logger LOGGER = LoggerFactory.getLogger( Graph.class );
	final static protected int OFFSET = 25;
	final static protected int INCREMENT = 3;
	final static protected Stroke GRID_STROKE = new BasicStroke();
	
	protected BufferedImage	image;
	protected GraphModel	model;
	protected Stroke		lineStroke;
	
	public Graph( GraphModel model )
	{
		this.setModel( model );
		
		this.lineStroke = new BasicStroke( 2 );
	}
	
	public void setStroke( Stroke stroke )
	{
		this.lineStroke = stroke;
	}
	
	public void setModel( GraphModel model )
	{
		this.model = model;
		
		this.model.addGraphModelListener( this );
	}
	
	public void paintComponent ( Graphics graphics )
	{
		Graphics2D g2 = (Graphics2D)graphics;

		g2.setRenderingHint( 
			RenderingHints.KEY_ANTIALIASING, 
			RenderingHints.VALUE_ANTIALIAS_ON );

		int w = this.getWidth();
		int h = this.getHeight();
		
		if ( this.image == null || this.image.getWidth() != w || this.image.getHeight() != h )
		{
			GraphicsConfiguration gc = g2.getDeviceConfiguration();

			this.image = gc.createCompatibleImage( w, h, Transparency.TRANSLUCENT );

			Graphics2D g = this.image.createGraphics();
			
			// start
			
			g.fillRect( 0, 0, w, h );
			
			g.setComposite( AlphaComposite.Clear );
			g.setColor( this.getBackground() );
			g.fillRect( 0, 0, w, h );

			g.setComposite( AlphaComposite.Src );
			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			g.setColor( Color.WHITE );
			g.fillRect( 0, 0, w, w );
			
			g.setComposite( AlphaComposite.SrcAtop );
			g.setPaint( new GradientPaint( w / 2, 0, Color.GRAY, 0, h, Color.WHITE ) );
			g.fillRect( 0, 0, w, h );			
			
			/* grid */
			
			g.setStroke( GRID_STROKE );
			
			for ( int i = 0; i < ( h - 10 ); i += 10 )
			{
				if ( i % 20 == 0 )
				{
					if ( i > 0 )
					{
						g.setColor( Color.BLACK );
						
						g.drawString( 
							"" + i, 
							0, 
							h - i + 3 
						);
					}
					
					g.drawLine( 
						OFFSET, 
						h - i, 
						w, 
						h - i 
					);
				}
				else
				{
					g.setColor( Color.GRAY );
					
					g.drawLine( 
						OFFSET, 
						h - i, 
						w, 
						h - i 
					);
				}
			}
			
			// title
			
			String text = this.model.getTitle();
			
			Font font = g.getFont().deriveFont( Font.BOLD );

			g.setFont( font );
			
			Rectangle2D r = font.getStringBounds( text, g.getFontRenderContext() );
			
			int x = OFFSET + (int)( ( w - OFFSET ) / 2 ) - (int)( r.getWidth() / 2.0 );
			
			int y = 10 + (int)( r.getHeight() / 3.0 );
			
			g.setColor( Color.WHITE );
			
			g.drawString( text, x, y );
			
			
			// finish
			
			g.dispose();

			g.drawImage( this.image, 0, 0, null );			
		}
		
		g2.drawImage( this.image, 0, 0, this );		
		
		int x1 = OFFSET;

		g2.setColor( this.model.getColor() );
		
		g2.setStroke( this.lineStroke );

		List<Integer> values = this.model.values();
		
		Iterator<Integer> iterator = null;

		int index = (int)Math.floor( ( w - OFFSET ) / INCREMENT );
		
		// logger.info( "index: {}", index );

		if ( index > 0 && values.size() > index )
		{
			iterator = values.listIterator( values.size() - index );
		}
		else
		{
			iterator = values.listIterator();
		}
		
		if ( iterator != null && iterator.hasNext() )
		{
			int y1 = h - iterator.next();

			while ( iterator.hasNext() )
			{
				int x2 = x1 + INCREMENT;
				
				int y2 = h - iterator.next();
				
				g2.drawLine( x1, y1, x2, y2 );
				
				x1 = x2;
				
				y1 = y2;
			}
		}		
	}

	public void graphModelChanged ()
	{
		this.repaint();
	}
}
