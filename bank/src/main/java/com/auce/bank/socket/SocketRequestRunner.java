package com.auce.bank.socket;

import static com.auce.bank.util.Constants.LINEFEED;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.auce.bank.util.Gateway;
import com.auce.bank.util.Response;
import com.auce.bank.util.StringRequest;
import com.auce.bank.util.StringResponse;

public class SocketRequestRunner implements Runnable
{
	private final Socket			socket;
	private final Gateway<String>	gateway;
	
	protected SocketRequestRunner( Socket socket, Gateway<String> gateway )
	{
		this.socket = socket;		
		this.gateway = gateway;
	}
	
	public void run ()
	{
		BufferedReader reader = null;

		PrintWriter writer = null;
		
		try
		{
			reader = new BufferedReader( 
				new InputStreamReader( this.socket.getInputStream() ) );
			
			writer = new PrintWriter( 
				new OutputStreamWriter( this.socket.getOutputStream() ) );

			StringRequest request = 
				new StringRequest( this.socket.getInetAddress(), reader.readLine() );
			
			Response<String> response = 
				(StringResponse)this.gateway.handleRequest( request );

			writer.write( response.getStatus().toString() );
			
			String body = response.getBody();
			
			if ( body != null && body.length() > 0 )
			{
				writer.write( LINEFEED );			
				writer.write( response.getBody() );
			}
			
			writer.flush();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			if ( this.socket != null )
			{
				try
				{
					this.socket.close();
				}
				catch( IOException e )
				{
				}
			}
			
			if ( reader != null )
			{
				try
				{
					reader.close();
				}
				catch( IOException e )
				{
				}
			}
			
			if ( writer != null )
			{
				writer.close();
			}			
		}
	}
}
