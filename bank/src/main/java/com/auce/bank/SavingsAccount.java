package com.auce.bank;

public class SavingsAccount extends AbstractAccount
{
	public SavingsAccount( final String accountNumber )
	{
		super( accountNumber );
	}
	
	public AccountType getAccountType()
	{
		return AccountType.SAVINGS;
	}	
	
	public boolean isWithdrawalAuthorized( final long amount )
	{
		return false;
	}
	
	public void performWithdrawal( final Withdrawal withdrawal )
	{
	}
}
