package com.auce.bank;

import java.util.Date;

public abstract class Mutation
{
	private final Date		date;
	private final String	accountNumber;
	private final String	nameOfHolder;
	private final String	cityOfHolder;
	private final String	description;
	private final long		amount;
	private boolean			viewed;
	
	public Mutation( final Date date, final String accountNumber, final String nameOfHolder, final String cityOfHolder, final String description, final long amount )
	{
		this.date = date;
		this.accountNumber = accountNumber;
		this.nameOfHolder = nameOfHolder;
		this.cityOfHolder = cityOfHolder;
		this.description = description;
		this.amount = amount;
	}

	public Date getDate ()
	{
		return date;
	}

	public String getAccountNumber ()
	{
		return accountNumber;
	}

	public String getNameOfHolder ()
	{
		return nameOfHolder;
	}

	public String getCityOfHolder ()
	{
		return cityOfHolder;
	}

	public String getDescription ()
	{
		return description;
	}

	public long getAmount ()
	{
		return amount;
	}
	
	public boolean isViewed()
	{
		return this.viewed;
	}
	
	public void setViewed( boolean flag )
	{
		this.viewed = flag;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder( this.getClass().getSimpleName() );
		
		sb.append( " { " );
		sb.append( "date=" + this.date + ", " );
		sb.append( "accountNumber=" + this.accountNumber + ", " );
		sb.append( "nameOfHolder=" + this.nameOfHolder + ", " );
		sb.append( "cityOfHolder=" + this.cityOfHolder + ", " );
		sb.append( "description=" + this.description + ", " );
		sb.append( "amount=" + this.amount + ", " );
		sb.append( "viewed=" + this.viewed );
		sb.append( " }" );

		return sb.toString();
	}	
}
