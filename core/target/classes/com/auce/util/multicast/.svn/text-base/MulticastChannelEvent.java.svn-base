package com.auce.util.multicast;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import static com.auce.util.Constants.LINE_SEPARATOR;

public class MulticastChannelEvent
{
	protected MulticastChannel	source;
	protected String			sender;
	protected String			text;
	
	public MulticastChannelEvent( MulticastChannel channel, String sender, String text )
	{
		this.source = channel;
		this.sender = sender;
		this.text = text;
	}
	
	public MulticastChannel getSource()
	{
		return this.source;
	}

	public String getSender ()
	{
		return this.sender;
	}
	
	public String getText()
	{
		return this.text;
	}

	public List<String> lines()
	{
		List<String> lines = new LinkedList<String>();
		
		if ( this.text.indexOf( LINE_SEPARATOR ) != -1 )
		{
			java.util.StringTokenizer st = new StringTokenizer( this.text, LINE_SEPARATOR );
			
			while ( st.hasMoreTokens() )
			{
				lines.add( st.nextToken() );
			}
		}
		else
		{
			lines.add( this.text );
		}

		return lines;
	}
}
