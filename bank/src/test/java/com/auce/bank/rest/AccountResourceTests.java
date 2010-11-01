package com.auce.bank.rest;

import static com.auce.bank.util.Constants.BANK_HTTP_PORT_KEY;
import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.auce.bank.util.Utilities;

public class AccountResourceTests extends TestCase
{
	final static private Logger LOGGER = LoggerFactory.getLogger( AccountResourceTests.class );
	
	final static private String URL_FORMAT = "http://localhost:%1$d/account/%2$s";
	
	private BankApplication		application;
	private Component			component;
	private int					port;
	
	public AccountResourceTests()
	{
		SLF4JBridgeHandler.install();
	}
	
	public void setUp ()
	{
		try
		{
			Utilities.loadProperties( "bank.properties" );
			this.port = Integer.parseInt( System.getProperty( BANK_HTTP_PORT_KEY ) );

			this.component = new Component();
			this.component.getServers().add( Protocol.HTTP, port );
			this.application = new BankApplication( component.getContext() );
			this.component.getDefaultHost().attachDefault( this.application );
			this.component.start();
		}
		catch( Exception e )
		{
			e.printStackTrace();
			
			fail( e.getMessage() );
		}
	}
	
	@Override
	protected void tearDown () throws Exception
	{
		this.component.stop();
	}
	
	public void testPut()
	{
		HttpClient client = new HttpClient();
		
		PutMethod method = null;
		
		try
		{
			method = new PutMethod( String.format( URL_FORMAT, this.port, "x" ) );
			LOGGER.info( method.getName() + " " + method.getPath() );
			
			StringBuilder sb = new StringBuilder();
			
			sb.append( "accountType=CHECKING," );
			sb.append( "nameOfHolder=\"Arjen van Schie\"," );
			sb.append( "cityOfHolder=\"Honselersdijk\"" );

			RequestEntity requestEntity = new StringRequestEntity(
				sb.toString(), "text/plain", "US-ASCII" );
			
			method.setRequestEntity( requestEntity );
			
			int httpStatus = client.executeMethod( method );
			
			LOGGER.info( "STATUS: {}", method.getStatusLine().toString() );

			assertEquals( HttpStatus.SC_CREATED, httpStatus );
			
			String responseBody = method.getResponseBodyAsString();			
			
			LOGGER.info( "BODY: {}", responseBody );			

			assertTrue( Utilities.isValidAccountNumber( responseBody ) );
		}
		catch( Exception e )
		{
			fail( e.getClass().getName() );
		}
		finally
		{
			if ( method != null ) method.releaseConnection();
		}
	}
	
	public void testGet()
	{
		HttpClient client = new HttpClient();

		GetMethod getMethod = null;
		
		try
		{
			PutMethod putMethod = new PutMethod( String.format( URL_FORMAT, this.port, "x" ) );
			String entity = "accountType=CHECKING,nameOfHolder=\"Arjen van Schie\",cityOfHolder=\"Honselersdijk\"";
			RequestEntity requestEntity = new StringRequestEntity( entity, "text/plain", "US-ASCII" );
			putMethod.setRequestEntity( requestEntity );
			client.executeMethod( putMethod );			
			String accountNumber = putMethod.getResponseBodyAsString();	
			putMethod.releaseConnection();
			
			getMethod = new GetMethod( String.format( URL_FORMAT, this.port, accountNumber ) );
			LOGGER.info( "{} {}", getMethod.getName(), getMethod.getPath() );
			int httpStatus = client.executeMethod( getMethod );
			LOGGER.info( "STATUS: {}", getMethod.getStatusLine() );
			assertEquals( HttpStatus.SC_OK, httpStatus );
			LOGGER.info( "BODY: {}", getMethod.getResponseBodyAsString() );
		}
		catch( Exception e )
		{
			fail( e.getClass().getName() );
		}
		finally
		{
			if ( getMethod != null ) getMethod.releaseConnection();
		}
	}	
	
	public void testPost()
	{
		HttpClient client = new HttpClient();

		PostMethod postMethod = null;
		
		try
		{
			PutMethod putMethod = new PutMethod( String.format( URL_FORMAT, this.port, "x" ) );
			String entity = "accountType=CHECKING,nameOfHolder=\"Arjen van Schie\",cityOfHolder=\"Honselersdijk\"";
			RequestEntity requestEntity = new StringRequestEntity( entity, "text/plain", "US-ASCII" );
			putMethod.setRequestEntity( requestEntity );
			client.executeMethod( putMethod );			
			String accountNumber = putMethod.getResponseBodyAsString();	
			putMethod.releaseConnection();
			
			postMethod = new PostMethod( String.format( URL_FORMAT, this.port, accountNumber ) );

			LOGGER.info( "{} {}", postMethod.getName(), postMethod.getPath() );

			NameValuePair[] data = {
				new NameValuePair( "debitAccountNumber", "123456789" ),
				new NameValuePair( "description", "This is a test" ),
				new NameValuePair( "amount", "50000" )
			};
			
			postMethod.setRequestBody( data );
			int httpStatus = client.executeMethod( postMethod );
			LOGGER.info( "STATUS: {}", postMethod.getStatusLine() );			
			assertEquals( HttpStatus.SC_FORBIDDEN, httpStatus );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			
			fail( e.getClass().getName() );
		}
		finally
		{
			if ( postMethod != null ) postMethod.releaseConnection();
		}		
	}
	
	public void testDelete()
	{
		HttpClient client = new HttpClient();

		DeleteMethod deleteMethod = null;
		
		try
		{
			PutMethod putMethod = new PutMethod( String.format( URL_FORMAT, this.port, "x" ) );
			String entity = "accountType=CHECKING,nameOfHolder=\"Arjen van Schie\",cityOfHolder=\"Honselersdijk\"";
			RequestEntity requestEntity = new StringRequestEntity( entity, "text/plain", "US-ASCII" );
			putMethod.setRequestEntity( requestEntity );
			client.executeMethod( putMethod );			
			String accountNumber = putMethod.getResponseBodyAsString();	
			putMethod.releaseConnection();
			
			deleteMethod = new DeleteMethod( String.format( URL_FORMAT, this.port, accountNumber ) );
			LOGGER.info( "{} {}", deleteMethod.getName(), deleteMethod.getPath() );
			int httpStatus = client.executeMethod( deleteMethod );
			LOGGER.info( "STATUS: {}", deleteMethod.getStatusLine() );
			assertEquals( HttpStatus.SC_NO_CONTENT, httpStatus );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			
			fail( e.getClass().getName() );
		}
		finally
		{
			if ( deleteMethod != null ) deleteMethod.releaseConnection();
		}			
	}
}
