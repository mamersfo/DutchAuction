package com.auce.auction.repository.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.auce.auction.entity.Clock;
import com.auce.auction.entity.Product;
import com.auce.auction.entity.Supplier;
import com.auce.util.SystemException;

public class RepositoryCsvImpl extends RepositoryMemoryImpl
{
	public RepositoryCsvImpl()
	{
		InputStream is = null;
		
		CSVReader reader = null;
		
		// ClockDao
		
		try
		{
			is = this.openStream( "Clock" );
			
			reader = new CSVReader( is );
			
			while ( reader.ready() )
			{
				String[] csv = reader.read();
				
				if ( csv != null && csv.length > 0 )
				{
					try
					{
						this.clockDao.add( 
							new Clock( csv[0], csv[1], Integer.parseInt( csv[2] ) ) );
					}
					catch ( Exception e )
					{
						LOGGER.error( e.getMessage() );
					}
				}
			}
		}
		catch ( IOException e )
		{
			throw new SystemException( e );
		}
		finally
		{
			if ( is != null )
			{
				try
				{
					is.close();
				}
				catch( Exception e )
				{
				}

				is = null;
			}
		}			
		
		// ProductDao
		
		try
		{
			is = this.openStream( "Product" );
			
			reader = new CSVReader( is );
			
			while ( reader.ready() )
			{
				String[] csv = reader.read();
				
				if ( csv != null && csv.length > 0 )
				{
					try
					{
						this.productDao.add( new Product( csv[0], csv[1], csv[2] ) );
					}
					catch ( Exception e )
					{
						LOGGER.error( e.getMessage() );
					}
				}
			}
		}
		catch ( IOException e )
		{
			throw new SystemException( e );
		}
		finally
		{
			if ( is != null )
			{
				try
				{
					is.close();
				}
				catch( Exception e )
				{
				}

				is = null;
			}
		}				
		
		// SupplierDao
		
		try
		{
			is = this.openStream( "Supplier" );
			
			reader = new CSVReader( is );

			while ( reader.ready() )
			{
				String[] csv = reader.read();

				if ( csv != null )
				{
					try
					{
						this.supplierDao.add( new Supplier( csv[0], csv[1] ) );
					}
					catch ( Exception e )
					{
						LOGGER.error( e.getMessage() );
					}
				}
			}
		}
		catch ( IOException e )
		{
			throw new SystemException( e );
		}
		finally
		{
			if ( is != null )
			{
				try
				{
					is.close();
				}
				catch( Exception e )
				{
				}

				is = null;
			}
		}		
	}
	
	class CSVReader
	{
		protected BufferedReader	reader;
		
		public CSVReader( InputStream is ) throws FileNotFoundException
		{
			this.reader = new BufferedReader( new InputStreamReader( is ) );
		}
		
		public boolean ready() throws IOException
		{
			return this.reader.ready();
		}
		
		public String[] read() throws IOException
		{
			if ( this.reader.ready() )
			{
				String line = this.reader.readLine();
				
				LOGGER.debug( "read() - {}", line );
				
				return line.split( "," );
			}

			return new String[0];
		}
	}	
}
