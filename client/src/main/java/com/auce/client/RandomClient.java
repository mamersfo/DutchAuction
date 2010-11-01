package com.auce.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import com.auce.auction.entity.AuctionListener;
import com.auce.auction.entity.AuctionProxy;
import com.auce.auction.repository.Repository;
import com.auce.auction.repository.impl.RepositoryMemoryImpl;
import com.auce.client.bank.Bank;
import com.auce.client.bank.BankProxy;
import com.auce.client.strategy.RandomPurchasingStrategy;
import com.auce.client.strategy.RandomSellingStrategy;
import com.auce.util.multicast.MulticastChannel;

public class RandomClient extends AbstractClient
{
	public RandomClient( Repository repository, Bank bank )
	{
		super( repository, bank );
	}
		
	public static void main ( String[] args )
	{
		String filename = null;
		
		if ( args.length > 0 ) filename = args[0];
		else filename = "conf/client1.conf";
		
		InputStream inputStream = null;
		
		try
		{
			File file = new File( System.getProperty( "user.dir" ), filename );
			inputStream = new FileInputStream( file );
			Properties props = new Properties( System.getProperties() );
			props.load( inputStream );
			System.setProperties( props );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		finally
		{
			if ( inputStream != null )
			{
				try
				{
					inputStream.close();
				}
				catch( IOException e )
				{
				}
			}
		}

		try
		{
			Repository repository = new RepositoryMemoryImpl();
			Bank bank = new BankProxy();
			
			RandomClient client = new RandomClient( repository, bank );
			client.setPurchasingStrategy( new RandomPurchasingStrategy( client ) );
			client.setSellingStrategy( new RandomSellingStrategy( client ) );

			// Auction
			
			AuctionProxy auction = new AuctionProxy( repository );
			auction.addAuctionListener( (AuctionListener)client.getPurchasingStrategy() );
			
			// Market Channel

			MulticastChannel market = new MulticastChannel( "M1" );
			market.setGroup( InetAddress.getByName( System.getProperty( "market.group" ) ) );
			market.setPort( Integer.parseInt( System.getProperty( "market.port" ) ) );
			market.setPeriod( Integer.parseInt( System.getProperty( "market.period" ) ) );
			market.addChannelListener( client );
			
			// Start
			
			((BankProxy)bank).start();
			auction.start();
			market.start();			
		}
		catch( Exception e )
		{
			e.printStackTrace();
			
			System.exit( 0 );
		}
	}
}