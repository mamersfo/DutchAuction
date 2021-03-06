package com.auce.bank;

import java.util.ArrayList;
import java.util.List;

public class Statement extends Instruction
{
	private String			accountNumber;
	private List<Mutation>	mutations;
	private long			balance;
	
	public Statement()
	{
		this( null );
	}
	
	public Statement( final String accountNumber )
	{
		super();
		
		this.accountNumber = accountNumber;
		this.mutations = new ArrayList<Mutation>();
	}

	public Type getType ()
	{
		return Event.Type.STATEMENT;
	}
	
	public String getAccountNumber ()
	{
		return accountNumber;
	}

	public void setAccountNumber ( final String accountNumber )
	{
		this.accountNumber = accountNumber;
	}

	public long getBalance ()
	{
		return balance;
	}

	public void setBalance ( final long balance )
	{
		this.balance = balance;
	}
	
	public void addMutations( final List<Mutation> mutation )
	{
		this.mutations.addAll( mutation );
	}
	
	public List<Mutation> listMutations()
	{
		return this.mutations;
	}

	@Override
	public String toString ()
	{
		final StringBuilder sb = new StringBuilder( this.getClass().getSimpleName() );
		
		sb.append( " { " );
		sb.append( "accountNumber=" + this.accountNumber + ", " );
		sb.append( "mutations=" + this.mutations.size() + ", " );
		sb.append( "balance=" + this.balance + ", " );
		sb.append( "date=" + this.getDate() + ", " );
		sb.append( "status=" + this.getStatus() ); 
		sb.append( " }" );
		
		return sb.toString();
	}
}
