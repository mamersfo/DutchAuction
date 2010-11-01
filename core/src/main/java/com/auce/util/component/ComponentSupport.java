package com.auce.util.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auce.util.Identity;


public abstract class ComponentSupport implements Component, Identity
{
	protected String	id;
	protected Thread	thread;
	protected int		period;
	protected boolean	isRunning;
	protected Logger	logger; 
	
	public ComponentSupport( String id )
	{
		this.id = id;

		this.period = 100;

		this.logger = LoggerFactory.getLogger( this.getClass().getSimpleName() + "-" + id );
	}
	
	public String getId ()
	{
		return this.id;
	}
	
	public int getPeriod()
	{
		return this.period;
	}
	
	public void setPeriod ( int millis )
	{
		this.period = millis;
	}

	public boolean isRunning()
	{
		return this.isRunning;
	}
	
	public void run()
	{
		try
		{
			this.beforeRunning();
		}
		catch( RuntimeException e )
		{
			throw e;
		}

		try
		{
			this.isRunning = true;
			
			while ( this.isRunning )
			{
				try
				{
					this.poll();

					Thread.sleep( this.period );
				}
				catch( Exception e )
				{
					logger.error( e.getMessage(), e );
				}
			}
		}
		catch( RuntimeException e )
		{
			logger.error( e.getMessage(), e );
		}
		
		this.isRunning = false;
		
		try
		{
			this.afterRunning();
		}
		catch( RuntimeException e )
		{
			throw e;
		}
	}
	
	protected abstract void poll();

	protected abstract void afterRunning();

	protected abstract void beforeRunning();

	public void start ()
	{
		if ( this.thread == null )
		{
			this.thread = new Thread( this, this.id );
			
			this.thread.start();
		}
	}
	
	public void stop()
	{
		if ( this.thread != null )
		{
			try
			{
				this.thread.join();
				
				this.thread = null;
			}
			catch( Exception e )
			{
			}
		}
	}
}
