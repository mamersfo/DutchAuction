package com.auce.client;

import com.auce.auction.entity.Trader;
import com.auce.auction.repository.Repository;
import com.auce.auction.repository.RepositoryListener;
import com.auce.client.bank.Bank;
import com.auce.client.strategy.PurchasingStrategy;
import com.auce.client.strategy.SellingStrategy;

public interface Client extends RepositoryListener
{
	public Bank getBank();
	public Trader getTrader();
	public Repository getRepository();
	public PurchasingStrategy getPurchasingStrategy();
	public SellingStrategy getSellingStrategy();
}
