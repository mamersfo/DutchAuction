package com.auce.util.multicast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MulticastChannelLogger implements MulticastChannelListener
{
	final static protected Logger LOGGER = LoggerFactory.getLogger( MulticastChannelLogger.class );
	
	public void handleEvent ( MulticastChannelEvent messageEvent )
	{
		for ( String line : messageEvent.lines() )
		{
			LOGGER.info( 
				"message on " + ((MulticastChannel)messageEvent.getSource()).getId() + 
				" from " + messageEvent.getSender() + ": " + line );
		}
	}
}
