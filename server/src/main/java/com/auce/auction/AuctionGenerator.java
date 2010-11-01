package com.auce.auction;

import com.auce.auction.entity.Clock;
import com.auce.auction.entity.Lot;
import com.auce.auction.entity.Product;
import com.auce.auction.entity.Supplier;
import com.auce.auction.event.Run;
import com.auce.auction.repository.Repository;
import com.auce.util.Sequence;

public class AuctionGenerator
{
	static public Run createRandomRun( Clock clock, Repository repository )
	{
		Run result = null;
		
		Lot lot = getRandomLot( repository );
		
		if ( lot != null )
		{
			result = new Run( 
				Sequence.getInstance().next( Run.PREFIX ), clock, lot );
		}
		
		return result;
	}
	
	static private Supplier getRandomSupplier( Repository repository )
	{
		Supplier[] suppliers = repository.listSuppliers();

		if ( suppliers.length > 0 )
		{
			int index = (int)Math.floor( Math.random() * suppliers.length );
			
			return suppliers[index];
		}

		return null;
	}		
	
	static private Product getRandomProduct ( Repository repository )
	{
		Product[] products = repository.listProducts();

		if ( products.length > 0 )
		{
			int index = (int)Math.floor( Math.random() * products.length );
			
			return products[index];
		}

		return null;
	}
	
	static public Lot getRandomLot( Repository repository )
	{
		Supplier supplier = getRandomSupplier( repository );
		
		Product product = getRandomProduct( repository );
		
		if ( supplier != null && product != null )
		{
			Lot lot = new Lot( 
				Sequence.getInstance().next( Lot.PREFIX ), 
				supplier, 
				product, 
				100 );
			
			try
			{
				repository.addLot( lot );
			}
			catch( Exception e )
			{
			}
			
			return lot;
		}
		
		return null;
	}	
}
