package com.auce.bank;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.auce.bank.impl.BankGatewayTests;
import com.auce.bank.rest.AccountResourceTests;
import com.auce.bank.socket.SocketServerTests;
import com.auce.bank.util.UtilitiesTests;

public class AllTests
{

	public static Test suite ()
	{
		TestSuite suite = new TestSuite( "Test for com.auce.bank" );
		//$JUnit-BEGIN$
		suite.addTestSuite( UtilitiesTests.class );
		suite.addTestSuite( AccountTests.class );
		suite.addTestSuite( BankTests.class );
		suite.addTestSuite( JournallerTests.class );
		suite.addTestSuite( BankGatewayTests.class );
		suite.addTestSuite( SocketServerTests.class );
		suite.addTestSuite( AccountResourceTests.class );
		//$JUnit-END$
		return suite;
	}

}
