package com.auce.bank;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public abstract class AbstractAccount implements Account
{	
	private final String 			accountNumber;
	private String 					nameOfHolder;
	private String 					cityOfHolder;
	private long					balance;
	private List<Mutation>			mutations;
	
	public AbstractAccount( final String accountNumber )
	{
		this.accountNumber = accountNumber;
		
		this.mutations = new ArrayList<Mutation>();	
	}

	public String getAccountNumber ()
	{
		return accountNumber;
	}
	
	public String getNameOfHolder ()
	{
		return nameOfHolder;
	}

	public void setNameOfHolder ( final String nameOfHolder )
	{
		this.nameOfHolder = nameOfHolder;
	}

	public String getCityOfHolder ()
	{
		return cityOfHolder;
	}

	public void setCityOfHolder ( final String cityOfHolder )
	{
		this.cityOfHolder = cityOfHolder;
	}	

	public long getBalance ()
	{
		return this.balance;
	}
	
	public void setBalance( final long balance )
	{
		this.balance = balance;
	}
	
	public void performDeposit( final Deposit deposit )
	{
		if ( deposit.getAccountNumber().equals( this.accountNumber ) == false )
		{
			this.mutations.add( deposit );
			
			this.balance += deposit.getAmount();
		}
	}
	
	public int getNumberOfMutations()
	{
		return this.mutations.size();
	}
	
	public Mutation getLastMutation()
	{
		return this.mutations.get( this.mutations.size() - 1 );
	}

	protected void addMutation( final Mutation mutation )
	{
		this.mutations.add( mutation );
	}
	
	public List<Mutation> listMutations()
	{
		final LinkedList<Mutation> result = new LinkedList<Mutation>();
		
		final ListIterator<Mutation> iterator = 
			this.mutations.listIterator( this.mutations.size() );
		
		while ( iterator.hasPrevious() )
		{
			final Mutation next = iterator.previous();

			if ( next.isViewed() )
			{
				break;
			}
			
			next.setViewed( true );
			
			result.add( next );
		}
		
		return result;
	}
	
	@Override
	public boolean equals ( final Object obj )
	{
		boolean result = false;
		
		if ( obj instanceof Account )
		{
			result = this.accountNumber.equals( ((Account)obj).getAccountNumber() );
		}
		
		return result;
	}
	
	@Override
	public String toString ()
	{
		final StringBuilder sb = new StringBuilder( this.getClass().getSimpleName() );
		
		sb.append( " { " );
		sb.append( "accountNumber=" + this.accountNumber + ", " );
		sb.append( "nameOfHolder=\"" + this.nameOfHolder + "\", " );
		sb.append( "cityOfHolder=\"" + this.cityOfHolder + "\", " );
		sb.append( "balance=" + this.balance + ", " );
		sb.append( "mutations=" + this.mutations.size() );
		sb.append( " }" );
		
		return sb.toString();
	}
}
