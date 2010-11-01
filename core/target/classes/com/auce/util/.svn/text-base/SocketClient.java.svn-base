package com.auce.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient
{
	public interface Callback
	{
		public void handleResponse( String request, String response );
	}

	private String	host;
	private int		port;
	private int		timeout;
	
	public SocketClient( String host, int port, int timeout )
	{
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}
	
	public void send ( String request, Callback callback ) throws IOException
	{
		Socket socket = null;
		
		BufferedReader reader = null;
		
		PrintWriter writer = null;
		
		try
		{
			socket = new Socket( this.host, this.port );
			
			socket.setSoTimeout( this.timeout );
			
			writer = new PrintWriter(
				new OutputStreamWriter( socket.getOutputStream() ) );
			
			writer.println( request );
			
			writer.flush();
			
			reader = new BufferedReader(
				new InputStreamReader( socket.getInputStream() ), 8192 );

			StringBuilder sb = new StringBuilder();
			
			String line = null;
			
			while ( ( line = reader.readLine() ) != null )
			{
				sb.append( line );
				sb.append( "\n" );
			}
				
			callback.handleResponse( request, sb.toString() );
		}
		finally
		{
			if ( writer != null )
			{
				writer.close();
			}
			
			if ( socket != null )
			{
				try
				{
					socket.close();
				}
				catch( IOException e )
				{
				}
			}
		}
	}	
}
