package com.auce.bank.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.bank.util.Gateway;


public class SocketServer implements Runnable
{
	final static private Logger LOGGER = LoggerFactory.getLogger( SocketServer.class );
	
	private int				port;
	private boolean 		running;
	private Gateway<String>	gateway;
	
	public SocketServer( int port, Gateway<String> gateway )
	{
		this.port = port;	
		
		this.gateway = gateway;
	}
	
	public synchronized void start()
	{
		this.running = true;
		
		new Thread( this ).start();
	}

	public synchronized void stop()
	{
		this.running = false;
	}
	
	public void run ()
	{
		LOGGER.info( "Running {} on SocketServer at port {}",
			this.gateway.getClass().getSimpleName(),
			this.port );
		
		ServerSocket serverSocket = null;
		
		BufferedReader reader = null;

		PrintWriter writer = null;

		try
		{			
			serverSocket = new ServerSocket( this.port );
			
			serverSocket.setSoTimeout( 1000 );
			
			while ( this.running )
			{
				try
				{
					Socket clientSocket = serverSocket.accept();
					
					SocketRequestRunner request = 
						new SocketRequestRunner( clientSocket, this.gateway );
					
					new Thread( request ).start();
				}
				catch( SocketTimeoutException e )
				{
				}
				finally
				{
					if ( reader != null )
					{
						try
						{
							reader.close();
						}
						catch( Exception e )
						{
						}
					}
					
					if ( writer != null )
					{
						try
						{
							writer.close();
						}
						catch( Exception e )
						{
						}
					}						
				}
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		finally
		{
			if ( serverSocket != null )
			{
				try
				{
					serverSocket.close();
					
					serverSocket = null;
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}		
		}	
	}
}
