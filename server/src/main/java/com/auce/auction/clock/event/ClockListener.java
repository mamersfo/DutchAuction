package com.auce.auction.clock.event;


public interface ClockListener
{
	public void handleClockTickEvent( ClockTickEvent event );
	public void handleClockStopEvent( ClockStopEvent event );
}
