package com.auce.monitor.graph;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import com.auce.auction.entity.Product;
import com.auce.monitor.util.ColorUtil;

public class PriceGraphModel implements GraphModel
{
	protected Product					product;
	protected Color						color;
	protected List<GraphModelListener>	listeners;
	protected List<Integer>				values;
	protected int						capacity;
	
	public PriceGraphModel( Product product, int capacity )
	{
		this.product = product;
		
		this.color = ColorUtil.fromString( this.product.getColor() );
		
		this.capacity = capacity;

		this.listeners = new LinkedList<GraphModelListener>();
		
		this.values = new LinkedList<Integer>();
	}

	public void addGraphModelListener ( GraphModelListener listener )
	{
		this.listeners.add( listener );
	}

	public Color getColor ()
	{
		return this.color;
	}

	public String getTitle ()
	{
		return this.product.getId();
	}
	
	public List<Integer> values ()
	{
		return this.values;
	}
	
	public void add( Integer value )
	{
		this.values.add( value );
		
		while ( this.values.size() > this.capacity )
		{
			this.values.remove( 0 );
		}
		
		this.fireGraphModelChanged();
	}
	
	protected void fireGraphModelChanged()
	{
		for ( GraphModelListener listener : this.listeners )
		{
			listener.graphModelChanged();
		}
	}	
}
