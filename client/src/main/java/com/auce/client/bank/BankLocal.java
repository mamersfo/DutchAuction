package com.auce.client.bank;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankLocal implements Bank
{
	final static private Logger LOGGER = LoggerFactory.getLogger( BankLocal.class );
	
	private long				balance;
	private List<BankListener>	listeners;
	final private boolean		isDirectDebit;
	private Boolean				lock = Boolean.TRUE;
	
	public BankLocal()
	{
		this.balance = Integer.parseInt( System.getProperty( "bank.start.balance" ) );
		
		this.listeners = new ArrayList<BankListener>();
		
		this.isDirectDebit = Boolean.parseBoolean( System.getProperty( "bank.direct.debit" ) );		
	}

	public void openAccount(String nameOfHolder, String cityOfHolder) 
	{
	}

	public void doPayment( String accountNumber, String description, long amount )
	{
	}

	public long getBalance() 
	{
		return this.balance;
	}
	
	public void deposit( long amount )
	{
		synchronized( this.lock )
		{
			long before = this.balance;
			
			this.balance += amount;
			
			// LOGGER.info( "deposit() - before={}, amount={}, after={}", new Object[] { before, amount, balance } );
						
			this.lock.notify();
		}
	}
	
	public void withdraw( long amount )
	{
		synchronized( this.lock )
		{
			long before = this.balance;
			
			this.balance -= amount;
			
			// LOGGER.info( "withdraw() - before={}, amount={}, after={}", new Object[] { before, amount, balance } );
 
			this.lock.notify();
		}
	}

	public void getStatement() 
	{
	}

	public void addBankListener( BankListener listener )
	{
		this.listeners.add( listener );
	}
	
	public void removeBankListener( BankListener listener )
	{
		this.listeners.remove( listener );
	}

	public boolean isDirectDebit() 
	{
		return this.isDirectDebit;
	}
}
