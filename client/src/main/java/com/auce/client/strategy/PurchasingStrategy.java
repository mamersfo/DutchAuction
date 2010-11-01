package com.auce.client.strategy;

import com.auce.auction.event.Bid;
import com.auce.auction.event.Run;
import com.auce.auction.event.Tick;

public interface PurchasingStrategy
{
	public void handleRun( Run run );	
	public Bid doBid( Tick tick );
}
