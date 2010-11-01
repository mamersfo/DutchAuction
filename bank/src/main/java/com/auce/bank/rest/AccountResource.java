package com.auce.bank.rest;

import static com.auce.bank.util.Constants.DEFAULT_FIELD_SEPARATOR;
import static com.auce.bank.util.Constants.EQUALS;

import java.net.InetAddress;
import java.util.HashMap;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import com.auce.bank.Event;
import com.auce.bank.impl.BankGateway;
import com.auce.bank.util.Response;
import com.auce.bank.util.StringRequest;

public class AccountResource extends Resource
{
	private InetAddress		inetAddress;
	private String			accountNumber;

	public AccountResource( 
		Context context, 
		org.restlet.data.Request request, 
		org.restlet.data.Response response )
	{
		super( context, request, response );
		
		this.getVariants().add( new Variant( MediaType.TEXT_PLAIN ) );
		
		this.accountNumber = 
			(String)this.getRequest().getAttributes().get( "accountNumber" );
		
		try
		{
			this.inetAddress = InetAddress.getByName( 
				this.getRequest().getClientInfo().getAddress() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public Representation getRepresentation ( Variant variant )
	{
		Status status = null;
		
		Representation result = null;

		try
		{	
			StringRequest stringRequest = new StringRequest( this.inetAddress,
				Event.Type.STATEMENT + "," + this.accountNumber );
			
			Response<String> stringResponse =
				this.getGateway().handleRequest( stringRequest );
						
			result = new StringRepresentation( 
				stringResponse.getBody(), MediaType.TEXT_PLAIN );
			
			status = Status.SUCCESS_OK;
		}
		catch( Exception e )
		{
			e.printStackTrace();
			
			status = Status.SERVER_ERROR_INTERNAL;
		}
		
		this.getResponse().setStatus( status );
		
		return result;
	}
	
	@Override
	public boolean allowPost ()
	{
		return true;
	}
	
	@Override
	public void post ( Representation entity )
	{
		Status status = null;
		
		try
		{
			Form form = new Form( entity );
			
			StringRequest request = new StringRequest(
				this.inetAddress,
				"PAYMENT," + 
				form.getFirstValue( "debitAccountNumber" ) + "," +
				this.accountNumber + "," +
				"\"" + form.getFirstValue( "description" ) + "\"," +
				form.getFirstValue( "amount" ) );
			
			Response<String> response = this.getGateway().handleRequest( request );
			
			
			if ( Response.Status.NO_CONTENT == response.getStatus() )
			{
				status = Status.SUCCESS_NO_CONTENT;
			}
			else
			{
				status = Status.CLIENT_ERROR_FORBIDDEN;
				
				this.getResponse().setEntity(
					new StringRepresentation( 
						response.getBody(), 
						MediaType.TEXT_PLAIN ) );				
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			
			status = Status.SERVER_ERROR_INTERNAL;
		}

		this.getResponse().setStatus( status );
	}
	
	@Override
	public boolean allowPut ()
	{
		return true;
	}
	
	@Override
	public void put ( Representation entity )
	{
		Status status = null;
		
		try
		{
			HashMap<String,String> map = new HashMap<String,String>();
			
			String[] fields = entity.getText().split( DEFAULT_FIELD_SEPARATOR );
			
			for ( int i=0; i < fields.length; i++ )
			{
				String[] nvp = fields[i].split( EQUALS );
				
				map.put( nvp[0], nvp[1] );
			}
			
			StringRequest request = new StringRequest(
				this.inetAddress,
				"OPENING," + map.get( "accountType" ) + "," +
				map.get( "nameOfHolder" ) + "," + map.get( "cityOfHolder" ) );
			
			Response<String> stringResponse =
				this.getGateway().handleRequest( request );
			
			if ( Response.Status.CREATED == stringResponse.getStatus() )
			{
				status = Status.SUCCESS_CREATED;
			}
			else
			{
				status = Status.CLIENT_ERROR_FORBIDDEN;
			}
			
			this.getResponse().setEntity(
				new StringRepresentation( 
					stringResponse.getBody(), 
					MediaType.TEXT_PLAIN ) );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			
			status = Status.SERVER_ERROR_INTERNAL;
		}

		this.getResponse().setStatus( status );
	}
	
	@Override
	public boolean allowDelete ()
	{
		return true;
	}
	
	@Override
	public void delete ()
	{
		Status status = null;
		
		StringRequest request = new StringRequest(
			this.inetAddress,
			"CANCELLATION," + this.accountNumber );
		
		Response<String> response = this.getGateway().handleRequest( request );
		
		if ( Response.Status.NO_CONTENT == response.getStatus() )
		{
			status = Status.SUCCESS_NO_CONTENT;
		}
		else
		{
			status = Status.CLIENT_ERROR_CONFLICT;
		}
		
		this.getResponse().setStatus( status );		
	}
	
	public String getAccountNumber()
	{
		return this.accountNumber;
	}
	
	public BankApplication getApplication()
	{
		return (BankApplication)this.getContext()
			.getAttributes().get( Application.KEY );
	}

	public BankGateway getGateway()
	{
		return this.getApplication().getGateway();		
	}
}
