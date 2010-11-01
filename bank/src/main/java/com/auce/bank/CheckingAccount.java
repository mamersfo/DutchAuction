package com.auce.bank;


public class CheckingAccount extends AbstractAccount
{
	public CheckingAccount( final String accountNumber )
	{
		super( accountNumber );		
	}
	
	public AccountType getAccountType()
	{
		return AccountType.CHECKING;
	}
	
	public boolean isWithdrawalAuthorized( final long amount )
	{
		return ( this.getBalance() - amount >= 0 );
	}
	
	public void performWithdrawal( final Withdrawal withdrawal )
	{
		if ( withdrawal.getAccountNumber().equals( this.getAccountNumber() ) == false )
		{
			long amount = withdrawal.getAmount();
			
			if ( this.isWithdrawalAuthorized( amount ) )
			{
				this.addMutation( withdrawal );
				
				this.setBalance( this.getBalance() - amount );
			}
		}
	}
}
