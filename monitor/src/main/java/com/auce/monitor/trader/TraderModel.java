package com.auce.monitor.trader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.event.TableModelListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.auction.entity.Trader;
import com.auce.bank.Account;

public class TraderModel extends TimerTask
{
	final static protected Logger LOGGER = LoggerFactory.getLogger( TraderModel.class );
	
	private static final long serialVersionUID = 8892460028061297896L;

	protected Trader					trader;
	protected List<TableModelListener>	tableModelListeners;
	protected List<TraderModelListener>	traderModelListeners;
	protected String					dump;
	protected URL						url;
	
	public TraderModel( Trader trader )
	{
		this.trader = trader;
		
		String urlStr = String.format(
			"%1$s/%2$s", System.getProperty( "trader.url" ), 
			this.trader.getId() );
		
		try
		{
			this.url = new URL( urlStr );
		}
		catch( MalformedURLException e )
		{
			LOGGER.error( "malformed url: {}", urlStr );
		}

		new Timer().schedule( this, 1000, 1000 );
		
		this.tableModelListeners = new LinkedList<TableModelListener>();

		this.traderModelListeners = new LinkedList<TraderModelListener>();
	}
	
	public void addTraderModelListener( TraderModelListener l )
	{
		this.traderModelListeners.add( l );
	}
	
	public void removeTraderModelListener( TraderModelListener l )
	{
		this.traderModelListeners.remove( l );
	}
	
	public int getBalance()
	{
		int result = 0;
		
		if ( this.trader != null )
		{
			Account account = this.trader.getAccount();
			
			if ( account != null )
			{
				result = (int)( account.getBalance() / 100.0 );
			}
		}
		
		return result;
	}
	
	public String getDump()
	{
		return this.dump;
	}
	
	public void setDump( String string )
	{
		this.dump = string;
	}
	
	public void fireTraderModelChanged()
	{
		for ( TraderModelListener l : this.traderModelListeners )
		{
			l.traderModelChanged();
		}
	}

	public Trader getTrader()
	{
		return this.trader;
	}
	
	public void run()
	{
		InputStream is = null;
		
		LOGGER.debug( "polling {}", this.url );
		
		try
		{
			is = this.url.openStream();
			
			BufferedReader br = 
				new BufferedReader( new InputStreamReader( is ) );
			
			StringBuffer buf = new StringBuffer();
			
			String line = null;
			
			while( ( line = br.readLine() ) != null ) 
			{
				buf.append(line);
			}
			
			this.dump = buf.toString();
			
			LOGGER.debug( "dump: {}", this.dump );
			
			this.fireTraderModelChanged();
		}
		catch( Exception e )
		{
			LOGGER.error( e.getMessage(), e );
		}
		finally
		{
			try { is.close(); } catch ( Exception e ) {}
		}
	}
}
