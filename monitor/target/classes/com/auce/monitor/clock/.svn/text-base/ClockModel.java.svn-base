package com.auce.monitor.clock;

import java.awt.Color;

import javax.swing.table.TableModel;

import com.auce.auction.entity.AuctionListener;
import com.auce.auction.entity.Clock;
import com.auce.auction.entity.Product;
import com.auce.auction.event.Purchase;
import com.auce.auction.event.Run;

public interface ClockModel extends TableModel, AuctionListener
{
	public Clock getClock();
	public Product getProduct();
	public Color getColor();
	
	public Run getRun();
	public void setRun( Run auction );
	
	public int getValue();
	public void setValue( int value );
	
	public Purchase getPurchase();
	public void setPurchase( Purchase purchase );
		
	public void addClockModelListener( ClockModelListener listener );
	public void removeClockModelListener( ClockModelListener listener );
}
