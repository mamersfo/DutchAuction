package com.auce.util.multicast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MulticastChannelUtilities
{
	final static protected Timer TIMER = new Timer();
	
	static public void sendLater( MulticastChannel channel, String userId, String message )
	{
		TIMER.schedule( new SendTask( channel, userId, message ), 0, 500 );
	}
	
	static private class SendTask extends TimerTask
	{
		final static protected Logger LOGGER = LoggerFactory.getLogger( SendTask.class );
		
		protected MulticastChannel	channel;
		protected String			userId;
		protected String			message;

		private SendTask( MulticastChannel channel, String userId, String message )
		{
			this.channel = channel;
			this.userId = userId;
			this.message = message;
		}

		public void run ()
		{
			if ( this.channel.isConnected() )
			{
				try
				{
					LOGGER.info( "sending message to {}: {}", 
						this.channel.getGroup().getHostAddress(),
						this.message );
					
					this.channel.sendMessage( this.userId, this.message );
					
					this.cancel();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
			else
			{
				LOGGER.info( "not connected, will retry sending {}", this.message );
			}
		}
	}	
}
