package com.auce.monitor.clock;

import java.util.EventObject;

import com.auce.auction.entity.Lot;

public class ClockModelEvent extends EventObject
{
	private static final long serialVersionUID = -8267755414334547780L;
	
	public ClockModelEvent( ClockModel clockModel )
	{
		super( clockModel );
	}
	
	public int getValue()
	{
		return ((ClockModel)this.source).getValue();
	}
	
	public Lot getLot()
	{
		return ((ClockModel)this.source).getRun().getLot();
	}
}
