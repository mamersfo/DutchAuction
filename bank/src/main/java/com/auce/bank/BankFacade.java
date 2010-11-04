
package com.auce.bank;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import com.auce.bank.util.Observer;

public class BankFacade implements Bank
{
	private final Bank 					bank;
	private List<Observer<Instruction>> observers;
	private final boolean				isDirectDebit;

	public BankFacade( final Bank bank )
	{
		this.bank = bank;
		
		this.observers = new ArrayList<Observer<Instruction>>();

		this.isDirectDebit = Boolean.parseBoolean( System.getProperty( "bank.direct.debit" ) );
	}
	
	public boolean isDirectDebit()
	{
		return this.isDirectDebit;
	}

	public void addObserver ( final Observer<Instruction> observer )
	{
		this.observers.add( observer );
	}

	public void removeObserver ( final Observer<Instruction> observer )
	{
		this.observers.remove( observer );
	}

	protected void notifyObservers ( final Instruction instruction )
	{
		for ( Observer<Instruction> observer : this.observers )
		{
			observer.addEvent( instruction );
		}
	}

	public Account findAccount ( final String accountNumber )
	{
		return this.bank.findAccount( accountNumber );
	}

	public String issueAccountNumber ()
	{
		return this.bank.issueAccountNumber();
	}

	public void processCancellation ( final Cancellation cancellation )
	{
		if ( cancellation != null )
		{
			cancellation.setDate( new Date() );

			Status status = this.bank.cancelAccount( cancellation.getAccountNumber() );

			cancellation.setStatus( status );

			this.notifyObservers( cancellation );
		}
	}

	public Status cancelAccount ( final String accountNumber )
	{
		Cancellation cancellation = new Cancellation( accountNumber );

		this.processCancellation( cancellation );

		return cancellation.getStatus();
	}

	public void processPayment ( final Payment payment )
	{
		if ( payment != null )
		{
			payment.setDate( new Date() );

			Status status = this.bank.doPayment( payment.getDebitAccountNumber(), payment.getCreditAccountNumber(), payment.getDescription(), payment.getAmount() );

			payment.setStatus( status );

			this.notifyObservers( payment );
		}
	}

	public Status doPayment ( final String debitAccountNumber, final String creditAccountNumber, final String description, final long amount )
	{
		Payment payment = new Payment( debitAccountNumber, creditAccountNumber, description, amount );

		this.processPayment( payment );

		return payment.getStatus();
	}

	public void processOpening ( final Opening opening )
	{
		if ( opening != null )
		{
			opening.setDate( new Date() );

			String accountNumber = opening.getAccountNumber();

			if ( accountNumber == null )
			{
				accountNumber = this.bank.issueAccountNumber();

				opening.setAccountNumber( accountNumber );
			}

			Status status = this.bank.openAccount( 
				opening.getAccountType(), 
				opening.getAccountNumber(), 
				opening.getNameOfHolder(), 
				opening.getCityOfHolder()
			);

			opening.setStatus( status );

			this.notifyObservers( opening );
		}
	}

	public Status openAccount ( 
			final AccountType accountType, 
			final String accountNumber, 
			final String nameOfHolder, 
			final String cityOfHolder )
	{
		Opening opening = new Opening( 
			accountType, 
			accountNumber, 
			nameOfHolder, 
			cityOfHolder
		);

		this.processOpening( opening );

		return opening.getStatus();
	}

	public void processStatement ( final Statement statement )
	{
		if ( statement != null )
		{
			statement.setDate( new Date() );

			Account account = this.bank.findAccount( statement.getAccountNumber() );

			if ( account != null )
			{
				statement.addMutations( account.listMutations() );

				statement.setBalance( account.getBalance() );

				statement.setStatus( Status.EXECUTED );
			}
			else
			{
				statement.setStatus( Status.NOT_FOUND );
			}

			this.notifyObservers( statement );
		}
	}

	public Bank getTarget ()
	{
		return this.bank;
	}
}
