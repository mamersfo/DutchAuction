package com.auce.bank.util;

import junit.framework.TestCase;

import com.auce.bank.util.Utilities;

public class UtilitiesTests extends TestCase
{
	public void testIsNumberValid ()
	{
		/* Incorrect input */
		
		assertFalse( Utilities.isValidAccountNumber( null ) );
		assertFalse( Utilities.isValidAccountNumber( "12345678" ) );
		assertFalse( Utilities.isValidAccountNumber( "1234567890" ) );
		assertFalse( Utilities.isValidAccountNumber( "12345ABCD" ) );

		/* Incorrect account number with 9 digits */
		
		assertFalse( Utilities.isValidAccountNumber( "987654321" ) );
		assertFalse( Utilities.isValidAccountNumber( "000000000" ) );

		/* Correct, verified account number with 9 digits */
		
		assertTrue( Utilities.isValidAccountNumber( "123456789" ) );
		assertTrue( Utilities.isValidAccountNumber( "978426231" ) );
		assertTrue( Utilities.isValidAccountNumber( "232833621" ) );
	}	
	
	public void testRandomAccountNumber()
	{
		assertTrue( Utilities.isValidAccountNumber( 
			Utilities.randomAccountNumber() ) );
	}

}
