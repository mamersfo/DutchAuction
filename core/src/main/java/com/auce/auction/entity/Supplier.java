package com.auce.auction.entity;

import com.auce.util.Identity;


public class Supplier extends Entity implements Identity
{
	protected String	id;
	protected String	name;
	
	public Supplier( String id, String name )
	{
		this.id = id;
		this.name = name;
	}

	public String getId ()
	{
		return id;
	}

	public String getName ()
	{
		return name;
	}
}
