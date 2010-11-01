package com.auce.bank.impl;

import com.auce.bank.AbstractBank;
import com.auce.bank.Account;


/**
 * 
 * Demonstrates how to subclass an abstract class (by using the @extends keyword).
 * This class demonstrates the concept of polymorphism, by implementing abstract
 * methods addAcount(), removeAccount(), and findAccount(), using an array for 
 * keeping track of @Account instances. Implements a private method that is
 * invisible to subclasses.
 * 
 * Lab1: Created as Bank class with Account array. Declared publics methods 
 * findAccount(), openAccount(), closeAccount(), and processPayment() and
 * protected methods addAccount(), removeAccount() and issueAccountNumber().
 * Lab2: Implemented public and protected methods from Lab1, added and
 * implemented private method indexOfAccount().
 * Lab3: Class renamed to FirstBankOfArrays, and subsumed as subclass as 
 * @AbstractBank and moved to com.auce.bank.core.impl package. Moved methods
 * openAccount(), processPayment(), issueAccountNumber() to @AbstractBank.
 * @author amersfoorthm
 *
 */
public class FirstBankOfArrays extends AbstractBank
{
	private Account[]	accounts;
	
	public FirstBankOfArrays()
	{
		this.accounts = new Account[100];
	}

	/**
	 * Returns the @Account that matches @accountNumber.
	 * @param accountNumber
	 * @return
	 * @since Lab01, implemented since Lab02
	 */
	public Account findAccount( String accountNumber )
	{
		Account result = null;
		
		int index = this.indexOfAccount( accountNumber );
		
		if ( index != -1 )
		{
			result = this.accounts[index];
		}
		
		return result;
	}

	/**
	 * (non-Javadoc)
	 * Convenience method for retrieving accounts by account number.
	 * @param accountNumber
	 * @return index of account matched by accountNumber
	 * @since Lab02
	 */
	private int indexOfAccount( String accountNumber )
	{
		int index = -1;
		
		if ( accountNumber != null )
		{
			for ( int i=0; i < this.accounts.length; i++ )
			{
				if ( this.accounts[i] != null )
				{
					if ( accountNumber.equals( this.accounts[i].getAccountNumber() ) )
					{
						index = i;
						
						break;
					}
				}
			}		
		}
		
		return index;
	}		
	
	/* (non-Javadoc)
	 * @see com.auce.bank.core.AbstractBank#addAccount(com.auce.bank.core.Account)
	 * @since Lab01, implemented since Lab02
	 */
	protected void addAccount( Account account )
	{
		for ( int i=0; i < this.accounts.length; i++ )
		{
			if ( this.accounts[i] == null )
			{
				this.accounts[i] = account;
				
				break;
			}
		}		
	}	
	
	/* (non-Javadoc)
	 * @see com.auce.bank.core.AbstractBank#removeAccount(com.auce.bank.core.Account)
	 * @since Lab01, implemented since Lab02
	 */
	protected void removeAccount( Account account )
	{
		int index = this.indexOfAccount( account.getAccountNumber() );
		if ( index != -1 ) this.accounts[index] = null;
	}
}
