package com.auce.bank;

import static com.auce.bank.util.Constants.DEFAULT_DATE_FORMAT;
import static com.auce.bank.util.Constants.DEFAULT_FIELD_SEPARATOR;

import java.io.BufferedReader;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.Date;

import junit.framework.TestCase;

import com.auce.bank.impl.BankOfCollections;
import com.auce.bank.util.Utilities;

public class JournallerTests extends TestCase
{
	private Bank				bank;
	private BufferedReader		reader;
	private PipedWriter			writer;
	
	@Override
	protected void setUp () throws Exception
	{
		this.writer = new PipedWriter();
		this.reader = new BufferedReader( new PipedReader( this.writer ) );

		BankFacade facade = new BankFacade( new BankOfCollections() );
		facade.addObserver( new Journaller( writer ) );
		this.bank = facade;
	}
	
	@Override
	protected void tearDown () throws Exception
	{
		this.reader.close();
		this.writer.close();
	}
	
	public void testAddEvent ()
	{
		try
		{
			String date = DEFAULT_DATE_FORMAT.format( new Date() );
			String sep = DEFAULT_FIELD_SEPARATOR;
			
			String checkingAccountNumber = this.bank.issueAccountNumber();
			
			assertEquals( Status.EXECUTED, 
				bank.openAccount( 
					AccountType.CHECKING, 
					checkingAccountNumber, 
					"John Doe", 
					"New York"
				));
			
			String expected = 
				date + sep + 
				Event.Type.OPENING + sep +
				AccountType.CHECKING + sep +
				checkingAccountNumber + sep +
				Utilities.toQuotedString( "John Doe" ) + sep +
				Utilities.toQuotedString( "New York" ) + sep +
				"0" + sep +
				Status.EXECUTED;
			
			assertEquals( expected, this.reader.readLine() );
			
			assertNotNull( this.bank.findAccount( checkingAccountNumber ) );
			
			String savingsAccountNumber = this.bank.issueAccountNumber();
			
			assertEquals( Status.EXECUTED, 
				bank.openAccount( 
				AccountType.SAVINGS, 
				savingsAccountNumber, 
				"John Doe", 
				"New York"
			));
			
			expected = 
				date + sep +
				Event.Type.OPENING + sep +
				AccountType.SAVINGS + sep +
				savingsAccountNumber + sep +
				Utilities.toQuotedString( "John Doe" ) + sep +
				Utilities.toQuotedString( "New York" ) + sep +
				"0" + sep +
				Status.EXECUTED;
			
			assertEquals( expected, this.reader.readLine() );
			
			assertNotNull( this.bank.findAccount( savingsAccountNumber ) );	
			
			assertEquals( Status.NOT_AUTHORIZED, 
				this.bank.doPayment( 
					savingsAccountNumber, 
					checkingAccountNumber, 
					"foobar",
					500 ) );		
			
			expected =
				date + sep +
				Event.Type.PAYMENT + sep +
				savingsAccountNumber + sep +
				checkingAccountNumber + sep +
				Utilities.toQuotedString( "foobar" ) + sep +
				"500" + sep +
				Status.NOT_AUTHORIZED;
			
			assertEquals( expected, this.reader.readLine() );
			
			assertEquals( Status.EXECUTED, 
				bank.cancelAccount( checkingAccountNumber ) );
			
			expected =
				date + sep +
				Event.Type.CANCELLATION + sep +
				checkingAccountNumber + sep +
				Status.EXECUTED;
			
			assertEquals( expected, this.reader.readLine() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}
