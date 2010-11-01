package com.auce.monitor.clock;

public interface ClockModelListener
{
	public void clockValueChanged( ClockModelEvent event );
	public void clockAuctionChanged( ClockModelEvent event );
}
