package com.auce.auction.clock;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.auce.auction.clock.event.ClockListener;
import com.auce.auction.clock.event.ClockStopEvent;
import com.auce.auction.clock.event.ClockTickEvent;
import com.auce.auction.entity.Clock;
import com.auce.auction.event.Run;

public class ClockRunTask extends TimerTask
{
	protected Run					run;
	protected Timer					timer;
	protected int					value;
	protected List<ClockListener>	listeners;
	protected List<ClockListener>	invalidatedListeners;;
	
	public ClockRunTask( Run auction )
	{
		this.run = auction;
		this.timer = new Timer();
		this.value = Clock.MAX_VALUE;
		this.listeners = new LinkedList<ClockListener>();
		this.invalidatedListeners = new LinkedList<ClockListener>();
	}
	
	public void start()
	{
		this.timer.schedule( this, 
			Integer.parseInt( System.getProperty( "auction.clock.timer.delay" ) ), 
			Integer.parseInt( System.getProperty( "auction.clock.timer.interval" ) )
		);
	}
	
	public void stop()
	{
		this.cancel();
		
		this.fireClockStopEvent();
	}
	
	public void addClockListener( ClockListener listener )
	{
		this.listeners.add( listener );
	}
	
	public void removeClockListener( ClockListener listener )
	{
		this.invalidatedListeners.add( listener );
	}
	
	protected void fireClockTickEvent()
	{
		ClockTickEvent event = new ClockTickEvent( this, this.value );
	
		for ( ClockListener listener : this.invalidatedListeners ) 
		{
			this.listeners.remove( listener );
	    }
		
		this.invalidatedListeners.clear();
		
		for ( ClockListener listener : this.listeners )
		{
			listener.handleClockTickEvent( event );
		}
	}
	
	protected void fireClockStopEvent()
	{
		ClockStopEvent event = new ClockStopEvent( this, this.value );
		
		for ( ClockListener listener : this.invalidatedListeners ) 
		{
			this.listeners.remove( listener );
	    }
		
		this.invalidatedListeners.clear();

		for ( ClockListener listener : this.listeners )
		{
			listener.handleClockStopEvent( event );
		}
	}

	public int getValue()
	{
		return this.value;
	}

	public Run getRun()
	{
		return this.run;
	}
	
	public void run ()
	{
		this.fireClockTickEvent();

		if ( this.value == 0 )
		{
			this.stop();
		}
		else
		{
			this.value -= 1;
		}				
	}
}
