package com.auce.client.bank;

import java.io.IOException;

import com.auce.bank.Cancellation;
import com.auce.bank.Instruction;
import com.auce.bank.Opening;
import com.auce.bank.Payment;
import com.auce.bank.Statement;
import com.auce.util.SocketClient;
import com.auce.util.SocketClient.Callback;

public class BankInstructionRunner implements Runnable
{
	private final Instruction 	instruction;
	private final SocketClient	socketClient;
	private final Callback		callback;
	
	public BankInstructionRunner( 
		Instruction instruction, SocketClient socketClient, Callback callback )
	{
		this.instruction = instruction;
		this.socketClient = socketClient;
		this.callback = callback;
	}
	
	public void start()
	{
		new Thread( this ).start();
	}
	
	public String mapToString ( Instruction i )
	{
		String result = null;
		
		if ( i instanceof Cancellation )
		{
			result = String.format( "%1$s,%2$s", 
				i.getType().toString(),
				((Cancellation)i).getAccountNumber()
			);
		}
		else if ( i instanceof Opening )
		{
			Opening o = (Opening)i;
			
			result = String.format( "%1$s,%2$s,\"%3$s\",\"%4$s\"",
				o.getType().toString(),
				o.getAccountType(),
				o.getNameOfHolder(),
				o.getCityOfHolder()
			);
		}
		else if ( i instanceof Payment )
		{
			Payment p = (Payment)i;
			
			result = String.format( "%1$s,%2$s,%3$s,\"%4$s\",%5$d",
				p.getType().toString(),
				p.getDebitAccountNumber(),
				p.getCreditAccountNumber(),
				p.getDescription(),
				p.getAmount()
			);
		}
		else if ( i instanceof Statement )
		{
			Statement s = (Statement)i;
			
			result = String.format( "%1$s,%2$s",
				s.getType().toString(),
				s.getAccountNumber()
			);
		}

		return result;
	}	

	public void run ()
	{
		try
		{
			this.socketClient.send( this.mapToString( this.instruction ), callback );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
}
