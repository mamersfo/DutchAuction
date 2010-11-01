package com.auce.monitor.clock;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.entity.Clock;
import com.auce.auction.entity.Lot;
import com.auce.auction.entity.Product;
import com.auce.auction.event.Purchase;
import com.auce.auction.event.Run;
import com.auce.monitor.util.ColorUtil;

public class DefaultClockModel implements ClockModel
{
	final static protected Logger LOGGER = LoggerFactory.getLogger( DefaultClockModel.class );
	
	final static protected String[] COLUMN_NAMES = {
		"Lot",
		"Prod",
		"Qty"
	};
	
	final static protected Class<?>[] COLUMN_CLASSES = {
		String.class,
		String.class,
		Integer.class
	};

	protected Clock						clock;
	protected Run						run;
	protected Color						color;
	protected Run[]						schedule;
	protected int						value;
	protected Purchase					purchase;
	protected List<ClockModelListener>	clockModelListeners;
	protected List<TableModelListener>	tableModelListeners;
	
	public DefaultClockModel( Clock clock )
	{
		this.clock = clock;
		this.schedule = new Run[0];
		this.clockModelListeners = new LinkedList<ClockModelListener>();
		this.tableModelListeners = new LinkedList<TableModelListener>();
	}
	
	public void fireClockValueUpdated()
	{
		ClockModelEvent event = new ClockModelEvent( this );
		
		for ( ClockModelListener listener : this.clockModelListeners )
		{
			listener.clockValueChanged( event );
		}
	}
	
	public void fireClockAuctionUpdated()
	{
		ClockModelEvent event = new ClockModelEvent( this );
		
		for ( ClockModelListener listener : this.clockModelListeners )
		{
			listener.clockAuctionChanged( event );
		}
	}

	public void addClockModelListener ( ClockModelListener listener )
	{
		this.clockModelListeners.add( listener );
	}

	public void removeClockModelListener ( ClockModelListener listener )
	{
		this.clockModelListeners.remove( listener );
	}

	public int getValue ()
	{
		return this.value;
	}
	
	public void setValue( int value )
	{
		this.value = value;
		
		this.fireClockValueUpdated();
	}

	public Run getRun ()
	{
		return run;
	}

	public void setRun ( Run run )
	{
		this.run = run;
		
		this.color = null;

		if ( this.run != null )
		{
			Lot lot= this.run.getLot();
			
			if ( lot != null )
			{
				Product product = lot.getProduct();
				
				if ( product != null )
				{
					this.color = ColorUtil.fromString( product.getColor() );
				}
			}
		}
		
		if ( this.color == null ) this.color = Color.BLACK;
		
		this.value = 100;
		
		this.purchase = null;
		
		this.fireClockAuctionUpdated();
	}

	public Clock getClock()
	{
		return this.clock;
	}

	public Product getProduct()
	{
		Product product = null;
		
		if ( this.run != null )
		{
			Lot lot = this.run.getLot();
			
			if ( lot != null ) product = lot.getProduct();
		}
		
		return product;
	}
	
	public Color getColor()
	{
		return this.color;
	}
	
	public Purchase getPurchase ()
	{
		return this.purchase;
	}

	public void setPurchase ( Purchase purchase )
	{
		this.purchase = purchase;
		
		this.fireClockAuctionUpdated();
	}
	
	/*** TableModel methods ***/

	public void addTableModelListener ( TableModelListener l )
	{
		this.tableModelListeners.add( l );
	}

	public Class<?> getColumnClass ( int columnIndex )
	{
		return COLUMN_CLASSES[columnIndex];
	}

	public int getColumnCount ()
	{
		return COLUMN_NAMES.length;
	}

	public String getColumnName ( int columnIndex )
	{
		return COLUMN_NAMES[columnIndex];
	}

	public int getRowCount ()
	{
		int result = 0;
		
		if ( this.schedule != null )
		{
			result = this.schedule.length;
		}
		
		return result;
	}

	public Object getValueAt ( int rowIndex, int columnIndex )
	{
		Object result = null;
		
		Run auction = this.schedule[rowIndex];
		
		Lot lot = auction.getLot();
		
		Product product = null;
		
		if ( lot != null ) product = lot.getProduct();
		
		switch( columnIndex )
		{
			case 0:
				// LotId
				if ( lot != null ) result = lot.getId();
				break;
			case 1:
				// Product name
				if ( product != null ) result = product.getId();
				break;
			case 2:
				// Auction Units
				if ( lot != null ) result = lot.getQuantity();
				break;
			default:
				break;
		}
		
		return result;	
	}

	public boolean isCellEditable ( int rowIndex, int columnIndex )
	{
		return false;
	}

	public void removeTableModelListener ( TableModelListener l )
	{
		this.tableModelListeners.remove( l );
	}

	public void setValueAt ( Object value, int rowIndex, int columnIndex )
	{
	}
	
	// AuctioneerListener

	public void auctionsChanged ()
	{
		this.schedule = this.clock.listRuns();
		
		TableModelEvent event = new TableModelEvent( this );
		
		for ( TableModelListener l : this.tableModelListeners )
		{
			l.tableChanged( event );
		}
	}

	public void handleRun ( Run run )
	{
	}
}
