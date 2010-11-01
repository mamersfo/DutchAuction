package com.auce.bank;

public class Payment extends Instruction
{
	private String	debitAccountNumber;
	private String	creditAccountNumber;
	private String	description;
	private long	amount;
	
	public Payment()
	{
	}
	
	public Payment( 
			final String debitAccountNumber, 
			final String creditAccountNumber, 
			final String description, 
			final long amount )
	{
		this.debitAccountNumber = debitAccountNumber;
		this.creditAccountNumber = creditAccountNumber;
		this.description = description;
		this.amount = amount;
	}
	
	public Type getType ()
	{
		return Type.PAYMENT;
	}

	public String getDebitAccountNumber ()
	{
		return debitAccountNumber;
	}

	public void setDebitAccountNumber ( final String debitAccountNumber )
	{
		this.debitAccountNumber = debitAccountNumber;
	}

	public String getCreditAccountNumber ()
	{
		return creditAccountNumber;
	}

	public void setCreditAccountNumber ( final String creditAccountNumber )
	{
		this.creditAccountNumber = creditAccountNumber;
	}
	
	public String getAccountNumber()
	{
		return this.creditAccountNumber;
	}

	public String getDescription ()
	{
		return description;
	}

	public void setDescription ( final String description )
	{
		this.description = description;
	}

	public long getAmount ()
	{
		return amount;
	}

	public void setAmount ( final long amount )
	{
		this.amount = amount;
	}

	@Override
	public String toString ()
	{
		final StringBuilder sb = new StringBuilder( this.getClass().getSimpleName() );
		
		sb.append( " { " );

		sb.append( "debitAccountNumber=" + this.debitAccountNumber + ", " );
		sb.append( "creditAccountNumber=" + this.creditAccountNumber + ", " );
		sb.append( "description=\"" + this.description + "\", " );
		sb.append( "amount=" + this.amount + ", " );
		
		sb.append( "date=" + this.getDate() + ", " );
		sb.append( "status=" + this.getStatus() + " }" );
		
		sb.append( " }" );
		
		return sb.toString();
	}
}
