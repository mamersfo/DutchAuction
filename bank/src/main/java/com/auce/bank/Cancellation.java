package com.auce.bank;


public class Cancellation extends Instruction
{
	private String	accountNumber;
	
	public Cancellation()
	{
		super();
	}
	
	public Cancellation( String accountNumber )
	{
		super();
		
		this.accountNumber = accountNumber;
	}
	
	public Type getType()
	{
		return Type.CANCELLATION;
	}
	
	public String getAccountNumber()
	{
		return this.accountNumber;
	}
	
	public void setAccountNumber( final String accountNumber )
	{
		this.accountNumber = accountNumber;
	}
}
