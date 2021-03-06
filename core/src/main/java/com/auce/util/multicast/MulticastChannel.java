
package com.auce.util.multicast;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.LinkedList;
import java.util.List;

import com.auce.util.SystemException;
import com.auce.util.component.ComponentSupport;

public class MulticastChannel extends ComponentSupport
{
	final static protected int					BUFFER_LENGTH					= 65508;
	final static protected int					DEFAULT_SOCKET_TIMEOUT_MILLIS	= 5000;
	final static public int						DEFAULT_TTL						= 2;
	final static protected long					MAGIC_NUMBER = 4969756929653643804L;

	protected InetAddress						group;
	protected int								port;
	protected int								timeToLive;
	protected MulticastSocket					socket;
	protected DatagramPacket					packet;
	protected List<MulticastChannelListener>	listeners;

	public MulticastChannel( String id )
	{
		super( id );
		
		this.period = 100;

		this.port = -1;

		this.timeToLive = DEFAULT_TTL;

		this.packet = new DatagramPacket( new byte[BUFFER_LENGTH], BUFFER_LENGTH );

		this.listeners = new LinkedList<MulticastChannelListener>();
	}

	public InetAddress getGroup ()
	{
		return group;
	}

	public void setGroup ( InetAddress group )
	{
		this.group = group;
	}

	public int getPort ()
	{
		return port;
	}

	public void setPort ( int port )
	{
		this.port = port;
	}

	public int getTtl ()
	{
		return timeToLive;
	}

	public void setTtl ( int ttl )
	{
		this.timeToLive = ttl;
	}

	// Listener

	public void addChannelListener ( MulticastChannelListener listener )
	{
		this.listeners.add( listener );
	}

	public void removeChannelListener ( MulticastChannelListener listener )
	{
		this.listeners.remove( listener );
	}

	protected void fireChannelMessage ( String userId, String text )
	{
		MulticastChannelEvent messageEvent = new MulticastChannelEvent( this, userId, text );

		for ( MulticastChannelListener listener : this.listeners )
		{
			try
			{
				listener.handleEvent( messageEvent );
			}
			catch ( Throwable e )
			{
			}
		}
	}

	// Connection

	protected void beforeRunning ()
	{
		try
		{
			this.connect();
		}
		catch ( Exception e )
		{
			throw new SystemException( e );
		}
	}

	public boolean isConnected ()
	{
		return ( this.socket != null && this.socket.isBound() && this.socket.isClosed() == false );
	}

	public void connect () throws IOException
	{
		logger.info( "connecting to group: " + this.group + ", port: " + this.port + ", ttl: " + this.timeToLive );

		if ( this.isConnected() )
		{
			this.socket.close();

			this.socket = null;
		}

		if ( this.group == null )
		{
			throw new IllegalStateException( "group not defined" );
		}

		if ( this.port == -1 )
		{
			throw new IllegalStateException( "port not defined" );
		}

		try
		{
			this.socket = new MulticastSocket( port );
			this.socket.setSoTimeout( DEFAULT_SOCKET_TIMEOUT_MILLIS );
			this.socket.setTimeToLive( timeToLive );
			this.socket.joinGroup( group );
		}
		catch ( IOException e )
		{
			this.socket = null;

			throw e;
		}
	}

	public void disconnect ()
	{
		if ( this.socket != null )
		{
			this.socket.close();

			this.socket = null;
		}
	}

	@Override
	protected void poll ()
	{
		try
		{
			this.packet.setLength( BUFFER_LENGTH );

			this.socket.receive( packet );

			/*

			DataInputStream istream = 
				new DataInputStream( 
					new ByteArrayInputStream( 
						packet.getData(), 
						packet.getOffset(), 
						packet.getLength() ) );

			long magic = istream.readLong();

			if ( magic != MAGIC_NUMBER )
			{
				logger.warn( 
					"ignoring message from {} with wrong magic number", 
						packet.getSocketAddress() );
				
				return;
			}
			

			String sender = istream.readUTF();
			
			String text = istream.readUTF();
			
			*/
			
			String string = new String ( this.packet.getData(), 0, this.packet.getLength() );
			
			String[] parts = string.split( ":");
			
			String sender = parts[0];
			
			String text = parts[1];

			if ( sender.equals( this.id ) == false )
			{
				this.fireChannelMessage( sender, text );
			}
		}
		catch ( InterruptedIOException e )
		{
			;
		}
		catch ( Throwable e )
		{
			logger.error( e.getMessage(), e );
		}
	}

	public void sendMessage ( String userId, String message ) throws IOException
	{
		if ( this.socket == null ) return;

		/*
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream( byteStream );
		dataStream.writeLong( MAGIC_NUMBER );
		dataStream.writeUTF( userId );
		dataStream.writeUTF( message );
		dataStream.close();

		byte[] data = byteStream.toByteArray();
		 */
		
		StringBuilder sb = new StringBuilder();
		sb.append( userId );
		sb.append( ":" );
		sb.append( message );

		String string = sb.toString();
		
		byte[] data = string.getBytes();
		int length = string.length();

		DatagramPacket packet = 
			new DatagramPacket( 
				data, 
				length, 
				this.group, 
				this.socket.getLocalPort() );

		this.socket.send( packet );
	}

	@Override
	protected void afterRunning() 
	{
		// TODO Auto-generated method stub	
	}
}
