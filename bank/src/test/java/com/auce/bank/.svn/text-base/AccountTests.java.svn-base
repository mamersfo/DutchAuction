package com.auce.bank;

import java.util.Date;

import com.auce.bank.Account;
import com.auce.bank.CheckingAccount;
import com.auce.bank.Deposit;
import com.auce.bank.Mutation;
import com.auce.bank.Withdrawal;
import com.auce.bank.util.Utilities;

import junit.framework.TestCase;

public class AccountTests extends TestCase
{
	@Override
	protected void setUp () throws Exception
	{
		
	}
	
	public void testIsWithdrawalAuthorized()
	{
		Account account = new CheckingAccount( Utilities.randomAccountNumber() );
		
		assertNotNull( account );
		
		((AbstractAccount)account).setBalance( 1000L );
		
		/* one euro more than balance */
		assertFalse( account.isWithdrawalAuthorized( 1001L ) );
		
		/* one euro less than balance */
		assertTrue( account.isWithdrawalAuthorized( 999L ) );
		
		/* exact amount of balance */
		assertTrue( account.isWithdrawalAuthorized( 1000L ) );
	}
	
	public void testPerformWithdrawal()
	{
		Account account1 = new CheckingAccount( Utilities.randomAccountNumber() );

		int count = account1.getNumberOfMutations();

		Account account2 = new CheckingAccount( Utilities.randomAccountNumber() );
		
		account1.performWithdrawal( new Withdrawal(
			new Date(), account2, "", 2000L ) );

		((AbstractAccount)account1).setBalance( 1000L );
		
		account1.performWithdrawal( new Withdrawal(
			new Date(), account1, "", 200L ) );
		
		assertEquals( account1.getBalance(), 1000L );

		account1.performWithdrawal( new Withdrawal(
			new Date(), account2, "", 200L ) );
		
		assertEquals( account1.getBalance(), 800L );
		
		assertEquals( account1.getNumberOfMutations(), count + 1 );
		
		Mutation mutation = account1.getLastMutation();		
		assertNotNull( mutation );
		assertTrue( mutation instanceof Withdrawal );
	}
	
	public void testPerformDeposit()
	{
		Account account1 = new CheckingAccount( Utilities.randomAccountNumber() );
		
		int count = account1.getNumberOfMutations();
		
		Account account2 = new CheckingAccount( Utilities.randomAccountNumber() );

		account1.performDeposit( 
			new Deposit( new Date(), account1, "", 200L ) );

		((AbstractAccount)account1).setBalance( 1000L );

		account1.performDeposit( 
			new Deposit( new Date(), account2, "", 200L ) );
		
		assertEquals( account1.getBalance(), 1200L );
		
		assertEquals( account1.getNumberOfMutations(), count + 1 );
		
		Mutation mutation = account1.getLastMutation();		
		assertNotNull( mutation );
		assertTrue( mutation instanceof Deposit );		
	}	
}
