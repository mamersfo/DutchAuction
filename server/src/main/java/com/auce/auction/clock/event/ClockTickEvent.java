package com.auce.auction.clock.event;

import com.auce.auction.clock.ClockRunTask;

public class ClockTickEvent extends ClockEvent
{
	private static final long	serialVersionUID	= -6243061173950404540L;

	public ClockTickEvent( ClockRunTask task, int value )
	{
		super( task, value );
	}
}
