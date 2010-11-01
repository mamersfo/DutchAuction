package com.auce.bank.impl;

import java.util.HashMap;
import java.util.Map;

import com.auce.bank.AbstractBank;
import com.auce.bank.Account;

public class BankOfCollections extends AbstractBank
{
	private Map<String,Account>	accounts;

	public BankOfCollections()
	{
		this.accounts = new HashMap<String,Account>();
	}

	@Override
	protected void addAccount ( Account account )
	{
		this.accounts.put( account.getAccountNumber(), account );
	}

	@Override
	protected void removeAccount ( Account account )
	{
		this.accounts.remove( account.getAccountNumber() );
	}

	public Account findAccount ( String accountNumber )
	{
		return this.accounts.get( accountNumber );
	}

}
