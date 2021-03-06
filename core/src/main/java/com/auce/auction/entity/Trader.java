package com.auce.auction.entity;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.auce.auction.event.Purchase;
import com.auce.auction.event.Sale;
import com.auce.bank.Account;
import com.auce.util.Identity;

public class Trader extends Entity implements Identity
{
	protected String		id;
	protected List<Stock>	inventory;
	protected Account		account;
	
	public Trader( String id )
	{
		this.id = id;
		
		this.inventory = new ArrayList<Stock>();
	}
	
	public Account getAccount ()
	{
		return this.account;
	}
	
	public void setAccount( Account account )
	{
		this.account = account;
	}

	public String getId()
	{
		return this.id;
	}

	public List<Stock> getInventory()
	{
		return this.inventory;
	}
	
	public void addStock( Stock stock )
	{
		this.inventory.add( stock );
	}
	
	public Stock findStock( Product product, boolean createIfNeeded )
	{
		Stock result = null;
		
		if ( product != null )
		{
			for ( Stock stock : this.inventory )
			{
				if ( product.equals( stock.getProduct() ) )
				{
					result = stock;
					break;
				}
			}
		}
		
		if ( result == null )
		{
			result = new Stock( product );
			
			this.addStock( result );
		}
		
		return result;
	}
	
	public void addPurchase ( Purchase purchase )
	{	
		Lot lot = purchase.getLot();
		
		if ( lot != null )
		{
			Product product = lot.getProduct();
			
			Stock stock = this.findStock( product, true );
			
			stock.updateWithPurchase( purchase );
		}
		else
		{
			throw new IllegalArgumentException( "expected Purchase.lot" );
		}
	}

	public void addSale ( Sale sale )
	{
		Stock stock = this.findStock( sale.getProduct(), true );
		
		stock.updateWithSale( sale );
	}

	public int getProfit ()
	{
		int purchaseValue = 0;
		int salesValue = 0;
		
		for ( Stock s : this.inventory )
		{
			purchaseValue += s.getPurchaseValue();
			salesValue += s.getSalesValue();
		}
		
		return salesValue - purchaseValue;
	}
	
	public void dump ( PrintWriter writer )
	{
		StringBuffer buf = new StringBuffer();

		buf.append("<html>\n<body>\n<B>\n");
		
		buf.append( this.getId() );

		buf.append( " has " );

		buf.append( (long)( this.account.getBalance() / 100.00 ) );
		
		for ( Stock s : this.inventory )
		{
			buf.append("\n<BR>");
			buf.append( s.getProduct().getId() );
			buf.append( " PQ:" );
			buf.append( s.getPurchaseQuantity() );
			buf.append( " SQ:" );
			buf.append( s.getSalesQuantity() );
			buf.append("</BR>");
		}
		
		buf.append("\n</B>\n</body>\n</html>\n");

		writer.println( buf.toString() );
	}	
}
