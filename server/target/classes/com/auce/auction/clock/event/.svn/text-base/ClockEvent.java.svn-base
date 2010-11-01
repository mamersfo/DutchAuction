
package com.auce.auction.clock.event;

import java.util.EventObject;

import com.auce.auction.clock.ClockRunTask;
import com.auce.auction.event.Run;

public abstract class ClockEvent extends EventObject
{
	private static final long	serialVersionUID	= 8943083114542119691L;

	protected Run				run;
	protected int				value;

	public ClockEvent( ClockRunTask task, int value )
	{
		super( task );

		this.run = task.getRun();

		this.value = value;
	}

	public Run getRun ()
	{
		return this.run;
	}

	public int getValue ()
	{
		return this.value;
	}
}
