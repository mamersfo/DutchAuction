package com.auce.monitor.trader;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;

public class TraderPanel extends JComponent implements TraderModelListener
{
	private static final long	serialVersionUID	= -6546982237620047319L;
	
	protected Header		header;
	protected TraderModel	model;
	protected JButton		traderButton;
	
	public TraderPanel( TraderModel model )
	{
		this.model = model;
		this.model.addTraderModelListener( this );

		this.setLayout( new BorderLayout( 4, 4 ) );
		
		this.header = new Header( model.getTrader().getId() );
		this.header.setBackground( Color.DARK_GRAY );
		this.header.setForeground( Color.WHITE );
		
		this.add( header, BorderLayout.NORTH );
		
		this.traderButton = new JButton( "" );
		this.traderButton.setFont( this.traderButton.getFont().deriveFont( 16f ) );
		this.add( this.traderButton, BorderLayout.CENTER );
	}

	public void traderModelChanged ()
	{
		this.traderButton.setText( this.model.getDump() );		
	}
	
	public class Header extends JComponent
	{
		private static final long	serialVersionUID	= 1817806258944274670L;
		
		protected String		text;
		protected int			profit;
		protected BufferedImage image;
		protected Font			font;
		
		public Header( String text )
		{
			this.text = text;
			this.setPreferredSize( new Dimension( -1, 24 ) );
			this.font = new Font( "SansSerif", Font.BOLD, 20 );
		}
		
		public void setFont( Font font )
		{
			this.font = font;
		}
		
		public void setText( String string )
		{
			this.text = string;
			this.repaint();
		}
		
		public void paintComponent ( Graphics graphics )
		{
			Graphics2D g2 = (Graphics2D)graphics;
			
			g2.setRenderingHint( 
				RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON );
			
			g2.setFont( this.font );
			
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
				
				g.setPaint( 
					new GradientPaint( 
						0, h, this.getBackground(), 
						(int)( w * 0.9 ) , 0, Color.LIGHT_GRAY 
					) 
				);
				
				g.fillRect( 0, 0, w, h );			
				
				// finish
				
				g.dispose();

				g.drawImage( this.image, 0, 0, null );			
			}
			
			g2.drawImage( this.image, 0, 0, this );		
			
			g2.setColor( this.getForeground() );
			
			g2.drawString( text, 5, h-5 );
		}		
	}
}
