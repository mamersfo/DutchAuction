package com.auce.bank;

import junit.framework.TestCase;

import com.auce.bank.impl.BankOfCollections;
import com.auce.bank.util.Utilities;

public class BankTests extends TestCase
{
	private Bank		bank;
	private Account		checkingAccount;
	private Account		savingsAccount;
	
	@Override
	protected void setUp () throws Exception
	{
		Utilities.loadProperties( "bank.properties" );
		
		this.bank = new BankOfCollections();
		
		String accountNumber = this.bank.issueAccountNumber();
		
		assertEquals( Status.EXECUTED, bank.openAccount( 
			AccountType.CHECKING, 
			accountNumber, 
			"John Doe", 
			"New York"
		));
		
		this.checkingAccount = this.bank.findAccount( accountNumber );
		
		assertNotNull( this.checkingAccount );
		
		accountNumber = this.bank.issueAccountNumber();
		
		assertEquals( Status.EXECUTED, bank.openAccount( 
			AccountType.SAVINGS, 
			accountNumber, 
			"John Doe", 
			"New York"
		));
		
		this.savingsAccount = this.bank.findAccount( accountNumber );
		
		assertNotNull( this.savingsAccount );		
	}
	
	public void testOpenAccount ()
	{
		/* Missing information */
		
		assertEquals( Status.NOT_COMPLETE,
			this.bank.openAccount( 
				null, "1234567890", "Barack Obama", "Chicago" ) );

		assertEquals( Status.NOT_COMPLETE,
			this.bank.openAccount( 
				AccountType.CHECKING, null, "Barack Obama", "Chicago" ) );

		assertEquals( Status.NOT_COMPLETE,
			this.bank.openAccount( 
				AccountType.CHECKING, "1234567890", null, "Chicago" ) );

		assertEquals( Status.NOT_COMPLETE,
			this.bank.openAccount( 
				AccountType.CHECKING, "1234567890", "Barack Obama", null ) );

		/* Invalid account number */
		
		assertEquals( Status.NOT_VALID,
			this.bank.openAccount( 
				AccountType.CHECKING, 
				"987654321", 
				"Barack Obama", 
				"Chicago"
			));
		
		/* Use of existing account number not allowed */
		
		assertEquals( Status.NOT_ALLOWED,
			this.bank.openAccount( 
				AccountType.CHECKING, 
				this.checkingAccount.getAccountNumber(), 
				"Barack Obama", 
				"Chicago"
			));
		
		/* Success */
		
		assertEquals( Status.EXECUTED,
			this.bank.openAccount( 
				AccountType.CHECKING, 
				"123456789", 
				"Barack Obama", 
				"Chicago"
			));
	}

	public void testFindAccount ()
	{
		assertNull( this.bank.findAccount( "987654321" ) );
	
		assertNotNull( this.bank.findAccount( checkingAccount.getAccountNumber() ) );

		assertNotNull( this.bank.findAccount( savingsAccount.getAccountNumber() ) );
	}

	public void testCancelAccount ()
	{
		assertEquals( Status.NOT_COMPLETE, this.bank.cancelAccount( null ) );

		assertEquals( Status.NOT_VALID, this.bank.cancelAccount( "987654321" ) );
		
		assertEquals( Status.NOT_FOUND,
			this.bank.cancelAccount( this.bank.issueAccountNumber() ) );

		((AbstractAccount)this.checkingAccount).setBalance( 1L );
		
		assertEquals( Status.NOT_ALLOWED,
			this.bank.cancelAccount( checkingAccount.getAccountNumber() ) );
		
		assertTrue( savingsAccount.getBalance() == 0L );
		
		assertEquals( Status.EXECUTED,
			this.bank.cancelAccount( savingsAccount.getAccountNumber() ) );
	}
	
	public void testProcessPayment()
	{
		/* Invalid account numbers */
		
		assertEquals( Status.NOT_VALID, 
			this.bank.doPayment( 
				"000000000", 
				"123456789", 
				"", 
				1000L ) );
		
		assertEquals( Status.NOT_VALID, 
			this.bank.doPayment( 
				"123456789", 
				"000000000", 
				"", 
				1000L ) );
		
		/* Non-existing account numbers */
		
		assertEquals( Status.NOT_FOUND, 
			this.bank.doPayment( 
				"123456789", 
				this.checkingAccount.getAccountNumber(), 
				"", 
				1000L ) );

		assertEquals( Status.NOT_FOUND, 
			this.bank.doPayment( 
				this.checkingAccount.getAccountNumber(), 
				"123456789", 
				"", 
				1000L ) );
		
		/* Insufficient funds */

		assertEquals( Status.NOT_AUTHORIZED, 
			this.bank.doPayment( 
				this.savingsAccount.getAccountNumber(), 
				this.checkingAccount.getAccountNumber(), 
				"",
				this.checkingAccount.getBalance() + 1 ) );
		
		/* Sufficient funds */
		
		((AbstractAccount)this.checkingAccount).setBalance( 1000L );
		
		assertEquals( Status.EXECUTED, 
			this.bank.doPayment( 
				savingsAccount.getAccountNumber(), 
				checkingAccount.getAccountNumber(), 
				"",
				checkingAccount.getBalance() - 1 ) );		
	}	
}
