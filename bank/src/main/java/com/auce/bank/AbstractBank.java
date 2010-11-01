package com.auce.bank;

import java.util.Date;

import com.auce.bank.util.Utilities;

public abstract class AbstractBank implements Bank
{
	public AbstractBank()
	{
	}
		
	public Status openAccount( 
			final AccountType accountType, 
			final String accountNumber,
			final String nameOfHolder, 
			final String cityOfHolder )
	{
		Status status = null;
		
		if ( accountType == null || accountNumber == null || 
			 nameOfHolder == null || cityOfHolder == null )
		{
			status = Status.NOT_COMPLETE;
		}
		else if ( Utilities.isValidAccountNumber( accountNumber ) == false )
		{
			status = Status.NOT_VALID;
		}
		else if ( this.findAccount( accountNumber ) != null )
		{
			status = Status.NOT_ALLOWED;
		}
		else
		{
			Account account = null;
			
			if ( AccountType.CHECKING == accountType )
			{
				account = new CheckingAccount( accountNumber );
				
			}
			else if ( AccountType.SAVINGS == accountType )
			{
				account = new SavingsAccount( accountNumber );
			}			
			
			((AbstractAccount)account).setNameOfHolder( nameOfHolder );
			
			((AbstractAccount)account).setCityOfHolder( cityOfHolder );	
			
			this.addAccount( account );
			
			status = Status.EXECUTED;
		}
		
		return status;
	}
	
	public Status cancelAccount( final String accountNumber )
	{
		Status status = null;
		
		if ( accountNumber == null )
		{
			status = Status.NOT_COMPLETE;
		}
		else if ( Utilities.isValidAccountNumber( accountNumber ) == false )
		{
			status = Status.NOT_VALID;
		}
		else
		{
			Account account = this.findAccount( accountNumber );
			
			if ( account == null )
			{
				status = Status.NOT_FOUND;
			}
			else if ( account.getBalance() != 0 )
			{
				status = Status.NOT_ALLOWED;
			}
			else
			{
				this.removeAccount( account );
				
				status = Status.EXECUTED;
			}
		}
		
		return status;
	}
	
	public Status doPayment( 
			final String debitAccountNumber, 
			final String creditAccountNumber, 
			final String description,
			final long amount )
	{
		Status status = null;
		
		/* Check input */
		
		if ( debitAccountNumber == null || creditAccountNumber == null )
		{
			status = Status.NOT_COMPLETE;
		}
		else
		{
			/* Check validity */
			
			if ( amount <= 0 ||
				 Utilities.isValidAccountNumber( debitAccountNumber ) == false ||
				 Utilities.isValidAccountNumber( creditAccountNumber ) == false )
			{
				status = Status.NOT_VALID;
			}
			else
			{
				/* Verify existence */
				
				Account debitAccount = this.findAccount( debitAccountNumber );
				
				Account creditAccount = this.findAccount( creditAccountNumber );
		
				if ( debitAccount == null || creditAccount == null )
				{
					status = Status.NOT_FOUND;
				}
				else
				{
					/*  Authorize */
					
					if ( creditAccount.isWithdrawalAuthorized( amount ) == false )
					{
						status = Status.NOT_AUTHORIZED;
					}
					else
					{
						Date date = new Date();
						
						creditAccount.performWithdrawal(
							new Withdrawal( 
								date, 
								debitAccount, 
								description, 
								amount ) );
				
						debitAccount.performDeposit(
							new Deposit( 
								date, 
								creditAccount, 
								description, 
								amount ) );
						
						status = Status.EXECUTED;
					}
				}
			}
		}
		
		return status;
	}
		
	public String issueAccountNumber()
	{
		String result = null;
		
		while ( result == null )
		{
			result = Utilities.randomAccountNumber();
			
			if ( result != null && this.findAccount( result ) == null )
			{
				break;
			}
			
			result = null;
		}
		
		return result;
	}	
	
	protected abstract void addAccount( final Account account );
	protected abstract void removeAccount( final Account account );
}
