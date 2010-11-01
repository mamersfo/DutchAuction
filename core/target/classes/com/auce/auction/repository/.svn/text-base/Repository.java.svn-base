package com.auce.auction.repository;

import com.auce.auction.entity.Clock;
import com.auce.auction.entity.Lot;
import com.auce.auction.entity.Product;
import com.auce.auction.entity.Supplier;
import com.auce.auction.entity.Trader;

public interface Repository
{
	// Listeners
	public void addRepositoryListener( RepositoryListener l );
	public void removeRepositoryListener( RepositoryListener l );

	// Traders
	public Trader[] listTraders();
	public Trader findTrader( String id );
	public void addTrader( Trader trader );
	public void updateTrader ( Trader trader );
	
	// Suppliers
	public int countSuppliers();
	public Supplier[] listSuppliers();
	public Supplier findSupplier( String id );
	public void addSupplier( Supplier supplier );
	
	// Products
	public int countProducts();
	public Product[] listProducts();
	public Product findProduct( String id );
	public void addProduct ( Product product );
	
	// Clocks
	public int countClocks();
	public Clock[] listClocks();
	public Clock findClock( String clockId );
	public void addClock( Clock clock );

	// Lots
	public Lot findLot( String lotId );
	public void addLot( Lot lot );
}
