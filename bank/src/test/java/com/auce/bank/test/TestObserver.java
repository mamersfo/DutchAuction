package com.auce.bank.test;

import static com.auce.bank.util.Constants.BANK_ACCOUNT_START_AMOUNT_KEY;

import java.util.ArrayList;
import java.util.List;

import com.auce.bank.AbstractAccount;
import com.auce.bank.Account;
import com.auce.bank.AccountType;
import com.auce.bank.BankFacade;
import com.auce.bank.Instruction;
import com.auce.bank.Opening;
import com.auce.bank.util.Observer;

public class TestObserver implements Observer<Instruction>
{
	private BankFacade		bank;
	private Account			rootAccount;
	private List<String>	welcomeMessages;
	private long			accountStartAmount;
	
	public TestObserver( BankFacade bank )
	{
		this.bank = bank;
		
		String accountNumber = this.bank.issueAccountNumber();
		
		this.bank.getTarget().openAccount( 
			AccountType.CHECKING, 
			accountNumber, 
			"Masterclass Java Team", 
			"Rotterdam"
		);
		
		this.rootAccount = this.bank.findAccount( accountNumber );
		
		((AbstractAccount)this.rootAccount).setBalance( Long.MAX_VALUE );
		
		this.welcomeMessages = new ArrayList<String>();
		this.welcomeMessages.add( "LIVE LONG AND PROFIT" );
		this.welcomeMessages.add( "HAPPY HUNTING TRADER" );
		this.welcomeMessages.add( "GOOD LUCK, MAY NEED IT" );
		this.welcomeMessages.add( "MAY THE FRUIT BE WITH YOU" );
		this.welcomeMessages.add( "FAILURE IS NOT AN OPTION" );
		
		try
		{
			this.accountStartAmount = Long.parseLong(
				System.getProperty( BANK_ACCOUNT_START_AMOUNT_KEY ) );
		}
		catch( NumberFormatException e )
		{
			this.accountStartAmount = 1000000;
		}
	}
	
	private String getRandomWelcomeMessage()
	{
		int index = (int)Math.round( Math.random() * 
			( this.welcomeMessages.size() - 1 ) );
		
		return this.welcomeMessages.get( index );
	}

	public void addEvent ( Instruction instruction )
	{
		if ( instruction instanceof Opening )
		{
			Opening opening = (Opening)instruction;
			
			String accountNumber = opening.getAccountNumber();
			
			if ( AccountType.CHECKING == opening.getAccountType() )
			{
				this.bank.doPayment( 
					accountNumber, 
					this.rootAccount.getAccountNumber(), 
					this.getRandomWelcomeMessage(), 
					this.accountStartAmount
				);
			}
		}
	}
}
