package com.auce.bank;

import java.util.Date;

public class Withdrawal extends Mutation
{
	public Withdrawal(
		final Date date,
		final Account account,
		final String description,
		final long amount )
	{
		super( date, 
			account.getAccountNumber(),
			account.getNameOfHolder(), 
			account.getCityOfHolder(), 
			description, amount );
	}
	
	public Withdrawal( 
		final Date date, 
		final String accountNumber, 
		final String nameOfHolder, 
		final String cityOfHolder,
		final String description, 
		final long amount )
	{
		super( date, accountNumber, nameOfHolder, cityOfHolder, description, amount );
	}	
}
