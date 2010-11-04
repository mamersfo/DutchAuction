package com.auce.client.bank;


public interface Bank
{
	public void openAccount( String nameOfHolder, String cityOfHolder );
	
	public void doPayment( String accountNumber, String description, long amount );
	
	public long getBalance();
	
	public void getStatement();
	
	public void addBankListener( BankListener listener );
	
	public void removeBankListener( BankListener listener );	
	
	public boolean isDirectDebit();	
}
