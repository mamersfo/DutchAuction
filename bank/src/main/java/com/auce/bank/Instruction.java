package com.auce.bank;

import java.util.Date;

public abstract class Instruction implements Event
{
	private Date	date;
	private Status	status;

	public Date getDate ()
	{
		return date;
	}

	public void setDate ( final Date date )
	{
		this.date = date;
	}

	public Status getStatus ()
	{
		return status;
	}

	public void setStatus ( final Status status )
	{
		this.status = status;
	}
	
	public abstract String getAccountNumber();
}
