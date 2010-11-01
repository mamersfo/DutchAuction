
package com.auce.market;

import java.util.Random;

import com.auce.auction.entity.Product;
import com.auce.auction.event.Quote;

public class Quoter
{
	final static protected double MIN = 10;
	final static protected double MAX = 130;
	
	final static protected Random RANDOM = new Random();
	
	/*
	 * A geometric Brownian motion with drift mu and volatility sigma is a stochastic 
	 * process that can model the price of asset. The parameter mu models the percentage 
	 * drift. If mu = 0.10, then we expect the asset to increase by 10% each year. 
	 * The parameter sigma models the percentage volatility. If mu = 0.20, then the 
	 * standard deviation of the asset price over one year is roughly 20% of the current 
	 * asset price. 
	 */

	protected Product	product;
	protected double	price;
	protected double	drift;		
	protected double	volatility;
	
	public Quoter( Product product, double value, double drift, double volatility )
	{
		this.product = product;
		this.price = value;
		this.drift = drift;
		this.volatility = volatility;
	}

	public double getMu ()
	{
		return drift;
	}

	public void setMu ( double drift )
	{
		this.drift = drift;
	}

	public double getSigma ()
	{
		return volatility;
	}

	public void setSigma ( double volatility )
	{
		this.volatility = volatility;
	}
	
	public int getPrice()
	{
		return (int)Math.round( this.price );
	}

	public Quote getNextQuote1()
	{
		// Price(t) = Price(t-1) * exp(mu + 0.5 * sigma * Z) 
		// mu = drift: slow systematic movement in the same direction
		// sigma = volatility: a measure of the uncertainty of the price of an asset
		
		double Z = RANDOM.nextGaussian();

		this.price = ( this.price * Math.exp( this.drift + 0.5 * this.volatility * Z ) );
		
		if ( this.price < MIN )
		{
			this.price = MIN;
			
			this.drift = 0.01;
		}
		else if ( this.price > MAX )
		{
			this.price = MAX;
			
			this.drift = -0.0;
		}

		Quote quote = new Quote( this.product, (int)Math.round( this.price ) );
		
		return quote;
	}
	
	public Quote getNextQuote2 ()
	{
		// Price(t) = Price(t-1) * exp((mu-sigma^2/2) + sigma * Z) 
		// mu = drift: slow systematic movement in the same direction
		// sigma = volatility: a measure of the uncertainty of the price of an asset

		double Z = RANDOM.nextGaussian();
		
		this.price = ( this.price * Math.exp( 
			( this.drift - Math.pow( this.volatility, 2.0 ) / 2.0  ) + this.volatility * Z ) );
		
		if ( this.price < MIN )
		{
			this.price = MIN;
			
			this.drift = 0.01;
		}
		else if ( this.price > MAX )
		{
			this.price = MAX;
			
			this.drift = -0.0;
		}
		
		Quote quote = new Quote( this.product, (int)Math.round( this.price ) );
		
		return quote;
	}
}
