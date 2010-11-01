
package com.auce.bank;

import java.util.List;

public interface Account
{
	/* Properties */
	
	public AccountType getAccountType();
	public String getAccountNumber();
	public String getNameOfHolder();
	public String getCityOfHolder();
	public long getBalance();
	
	/* Authorization */
	
	public boolean isWithdrawalAuthorized( long amount );

	/* Mutations */
	
	public void performWithdrawal( Withdrawal withdrawal );
	public void performDeposit( Deposit deposit );
	
	public Mutation getLastMutation();
	public int getNumberOfMutations();
	public List<Mutation> listMutations();
}
