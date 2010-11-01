package com.auce.client.bank;

import com.auce.bank.Instruction;

public class BankEvent
{
	private final Bank			source;
	private final Instruction	instruction;
	
	public BankEvent( Bank source, Instruction instruction )
	{
		this.source = source;
		this.instruction = instruction;
	}

	public Bank getSource ()
	{
		return source;
	}

	public Instruction getInstruction ()
	{
		return instruction;
	}
}
