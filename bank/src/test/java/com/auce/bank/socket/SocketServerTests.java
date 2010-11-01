package com.auce.bank.socket;

import static com.auce.bank.util.Constants.BANK_SOCKET_PORT_KEY;
import static com.auce.bank.util.Constants.LINEFEED;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

import com.auce.bank.AccountType;
import com.auce.bank.BankFacade;
import com.auce.bank.Opening;
import com.auce.bank.impl.BankGateway;
import com.auce.bank.impl.BankOfCollections;
import com.auce.bank.util.Response;
import com.auce.bank.util.Utilities;


public class SocketServerTests extends TestCase
{
	protected SocketServer	server;
	protected BankGateway	gateway;
	protected int			port;
	
	@Override
	protected void setUp () throws Exception
	{
		FileInputStream is = null;

		try
		{
			File file = new File( "src/test/resources/bank.properties" );
			
			assertTrue( file.exists() );
	
			is = new FileInputStream( file );
			
			Properties properties = new Properties( System.getProperties() );
			
			properties.load( is );
			
			System.setProperties( properties );
			
			this.port = Integer.parseInt( 
				System.getProperty( BANK_SOCKET_PORT_KEY ) );
		}
		catch( Exception e )
		{
			fail( e.getMessage() );
		}
		finally
		{
			if ( is != null )
			{
				try
				{
					is.close();
				}
				catch( IOException e )
				{
				}
			}
		}		
		
		this.gateway = new BankGateway( new BankFacade( new BankOfCollections() ) );
		this.server = new SocketServer( this.port, this.gateway );
		this.server.start();
	}
	
	@Override
	protected void tearDown () throws Exception
	{
		this.server.stop();
	}
	
	public void test()
	{
		SocketClient client = new SocketClient( "localhost", this.port );
		
		Opening opening = new Opening();
		opening.setAccountType( AccountType.CHECKING );
		opening.setNameOfHolder( "Martin van Amersfoorth" );
		opening.setCityOfHolder( "Rotterdam" );
		
		final String request = this.gateway.mapToString( opening );
		
		try
		{
			client.send( request, new SocketClient.Callback() {
	
				public void handleResponse ( String request, String response )
				{
					String[] items = response.split( LINEFEED );
					assertEquals( 2, items.length );
					assertEquals( Response.Status.CREATED, Response.Status.valueOf( items[0] ) );
					assertTrue( Utilities.isValidAccountNumber( items[1] ) );
				}
			});
		}
		catch( Exception e )
		{
			fail( e.getClass().getSimpleName() + ": " + e.getMessage() );
		}
	}
}
