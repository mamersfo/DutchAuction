package com.auce.bank.impl;

import static com.auce.bank.util.Constants.LINEFEED;

import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;

import com.auce.bank.AccountType;
import com.auce.bank.BankFacade;
import com.auce.bank.Cancellation;
import com.auce.bank.Opening;
import com.auce.bank.Payment;
import com.auce.bank.Statement;
import com.auce.bank.test.TestObserver;
import com.auce.bank.util.Request;
import com.auce.bank.util.Response;
import com.auce.bank.util.StringRequest;
import com.auce.bank.util.Utilities;

public class BankGatewayTests extends TestCase
{
	private BankGateway	gateway;
		
	@Override
	protected void setUp () throws Exception
	{
		Utilities.loadProperties( "bank.properties" );
		BankFacade facade = new BankFacade( new BankOfCollections() );
		this.gateway = new BankGateway( facade );
		TestObserver observer = new TestObserver( facade );
		facade.addObserver( observer );
	}
	
	public void testHandleRequest()
	{
		try
		{
			// Open checking account
			Opening opening = new Opening();
			opening.setAccountType( AccountType.CHECKING );
			opening.setNameOfHolder( "Martin van Amersfoorth" );
			opening.setCityOfHolder( "Rotterdam" );
			Request<String> request = new StringRequest( 
				InetAddress.getLocalHost(), this.gateway.mapToString( opening ) );
			Response<String> response = this.gateway.handleRequest( request );
			assertEquals( Response.Status.CREATED, response.getStatus() );
			String[] bodyItems = response.getBody().split( LINEFEED );
			assertEquals( 1, bodyItems.length );
			assertTrue( Utilities.isValidAccountNumber( bodyItems[0] ) );
			String checkingAccountNumber = bodyItems[0];
			
			// Statement
			Statement statement = new Statement( checkingAccountNumber );
			request = new StringRequest( 
				InetAddress.getLocalHost(), this.gateway.mapToString( statement ) );
			response = this.gateway.handleRequest( request );
			assertEquals( Response.Status.OK, response.getStatus() );
			System.out.println( response.getBody() );
			bodyItems = response.getBody().split( LINEFEED );
			assertEquals( 4, bodyItems.length );
			
			// Open savings account
			opening = new Opening();
			opening.setAccountType( AccountType.SAVINGS );
			opening.setNameOfHolder( "Martin van Amersfoorth" );
			opening.setCityOfHolder( "Rotterdam" );
			request = new StringRequest( 
				InetAddress.getLocalHost(), this.gateway.mapToString( opening ) );
			response = this.gateway.handleRequest( request );
			assertEquals( Response.Status.CREATED, response.getStatus() );
			bodyItems = response.getBody().split( LINEFEED );
			assertEquals( 1, bodyItems.length );
			assertTrue( Utilities.isValidAccountNumber( bodyItems[0] ) );
			String savingsAccountNumber = bodyItems[0];
			
			// Transfer funds to savings account
			Payment payment = new Payment( savingsAccountNumber, checkingAccountNumber, "MONEY FOR A RAINY DAY", 97389 );
			request = new StringRequest( 
				InetAddress.getLocalHost(), this.gateway.mapToString( payment ) );
			response = this.gateway.handleRequest( request );
			assertEquals( Response.Status.NO_CONTENT, response.getStatus() );
			String body = response.getBody();
			assertEquals( 0, body.length() );
			
			// Cancellation
			Cancellation cancellation = new Cancellation( checkingAccountNumber );
			request = new StringRequest( 
				InetAddress.getLocalHost(), this.gateway.mapToString( cancellation ) );
			response = this.gateway.handleRequest( request );
			assertEquals( Response.Status.CONFLICT, response.getStatus() );
			body = response.getBody();
			assertEquals( 0, body.length() );
		}
		catch( UnknownHostException e )
		{
			fail( e.getClass().getSimpleName() + ": " + e.getMessage() );
		}
	}
}
