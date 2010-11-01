package com.auce.bank;



public class Opening extends Instruction
{
	private AccountType		accountType;
	private String			accountNumber;
	private String			nameOfHolder;
	private String			cityOfHolder;
	private long			balance;
	
	public Opening()
	{
		super();
	}
	
	public Opening(
		final AccountType accountType, 
		final String accountNumber, 
		final String nameOfHolder, 
		final String cityOfHolder )
	{
		super();
		
		this.accountType = accountType;
		this.accountNumber = accountNumber;
		this.nameOfHolder = nameOfHolder;
		this.cityOfHolder = cityOfHolder;
	}
	
	public Type getType()
	{
		return Type.OPENING;
	}

	public AccountType getAccountType ()
	{
		return accountType;
	}

	public void setAccountType ( AccountType accountType )
	{
		this.accountType = accountType;
	}

	public String getAccountNumber ()
	{
		return accountNumber;
	}

	public void setAccountNumber ( String accountNumber )
	{
		this.accountNumber = accountNumber;
	}

	public String getNameOfHolder ()
	{
		return nameOfHolder;
	}

	public void setNameOfHolder ( String nameOfHolder )
	{
		this.nameOfHolder = nameOfHolder;
	}

	public String getCityOfHolder ()
	{
		return cityOfHolder;
	}

	public void setCityOfHolder ( String cityOfHolder )
	{
		this.cityOfHolder = cityOfHolder;
	}

	public long getBalance ()
	{
		return balance;
	}

	public void setBalance ( long balance )
	{
		this.balance = balance;
	}
	
	@Override
	public String toString ()
	{
		final StringBuilder sb = new StringBuilder( 
			this.getClass().getSimpleName() + " { " );
		
		sb.append( "accountType=" + this.accountType + ", " );
		sb.append( "accountNumber=" + this.accountNumber + ", " );
		sb.append( "nameOfHolder=" + this.nameOfHolder + ", " );
		sb.append( "cityOfHolder=" + this.cityOfHolder + ", " );
		sb.append( "balance=" + this.balance + ", " );
		
		sb.append( "date=" + this.getDate() + ", " );
		sb.append( "status=" + this.getStatus() + " }" );
		
		return sb.toString();
	}
}
