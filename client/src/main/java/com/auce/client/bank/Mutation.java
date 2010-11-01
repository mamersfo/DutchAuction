
package com.auce.client.bank;

public abstract class Mutation
{
	private String 	time;
	private String 	accountNumber;
	private String 	nameOfHolder;
	private String 	description;
	private long 	amount;
	
	public String getTime ()
	{
		return time;
	}

	public void setTime ( String time )
	{
		this.time = time;
	}

	public String getAccountNumber ()
	{
		return accountNumber;
	}

	public void setAccountNumber ( String accountNumber )
	{
		this.accountNumber = accountNumber;
	}

	public String getDescription ()
	{
		return description;
	}

	public void setDescription ( String description )
	{
		this.description = description;
	}

	public long getAmount ()
	{
		return amount;
	}

	public void setAmount ( long amount )
	{
		this.amount = amount;
	}

	public String getNameOfHolder ()
	{
		return nameOfHolder;
	}

	public void setNameOfHolder ( String nameOfHolder )
	{
		this.nameOfHolder = nameOfHolder;
	}
	
	@Override
	public String toString ()
	{
		StringBuilder sb = new StringBuilder( this.getClass().getSimpleName() + " { " );
		
		sb.append( "time=" + this.time + ", " );
		sb.append( "accountNumber=" + this.accountNumber + ", " );
		sb.append( "nameOfHolder=" + this.nameOfHolder + ", " );
		sb.append( "description=" + this.description + ", " );
		sb.append( "amount=" + this.amount + " }" );
		
		return sb.toString();
	}	
}
