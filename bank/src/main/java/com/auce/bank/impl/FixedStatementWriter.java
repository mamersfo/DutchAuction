package com.auce.bank.impl;

import static com.auce.bank.util.Constants.LINEFEED;

import java.util.Date;

import com.auce.bank.Deposit;
import com.auce.bank.Mutation;
import com.auce.bank.Statement;
import com.auce.bank.Withdrawal;
import com.auce.bank.util.StatementWriter;
import com.auce.bank.util.Utilities;

public class FixedStatementWriter implements StatementWriter
{
	public String write( Statement statement )
	{
		StringBuilder sb = new StringBuilder();
		
		String format = null;

		for ( Mutation mutation : statement.listMutations() )
		{
			if ( mutation instanceof Withdrawal ) format = DEBIT_LINE;
			else if ( mutation instanceof Deposit ) format = CREDIT_LINE;
			
			if ( sb.length() > 0 ) sb.append( LINEFEED );
			
			sb.append( String.format( format, 
				mutation.getDate(),
				Utilities.toDottedAccountNumber( mutation.getAccountNumber() ),
				mutation.getAmount() / 100.0,
				""
			));			
			
			sb.append( LINEFEED );

			sb.append( String.format( INFO_LINE, "", mutation.getNameOfHolder() ) );

			if ( mutation.getDescription() != null )
			{
				sb.append( LINEFEED );

				sb.append( String.format( INFO_LINE, "", mutation.getDescription() ) );
			}
		}
		
		if ( statement.getBalance() < 0 ) format = DEBIT_LINE;
		else format = CREDIT_LINE;
		
		if ( sb.length() > 0 ) sb.append( LINEFEED );

		sb.append( String.format( format, 
			new Date(),
			"Balance",
			statement.getBalance() / 100.0,
			"" 
		));
		
		return sb.toString();
	}
	
	final static private String DEBIT_LINE = "%1$-10tT%2$-30s%3$10.2f%4$10s";
	final static private String CREDIT_LINE = "%1$-10tT%2$-30s%4$10s%3$10.2f";
	final static private String INFO_LINE = "%1$10s%2$-30s%1$10s%1$10s";
}
