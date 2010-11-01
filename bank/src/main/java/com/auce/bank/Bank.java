package com.auce.bank;




public interface Bank
{
	public Account findAccount( final String accountNumber );
	
	public Status openAccount( 
			final AccountType accountType, 
			final String accountNumber,
			final String nameOfHolder, 
			final String cityOfHolder );

	public Status cancelAccount( final String accountNumber );

	public Status doPayment( 
			final String debitAccountNumber, 
			final String creditAccountNumber, 
			final String description,
			final long amounts );

	public String issueAccountNumber();	
}
