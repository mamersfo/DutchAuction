package com.auce.bank.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Constants
{
	final static public String LINEFEED = "\n";
	final static public String DOT = ".";
	final static public String COMMA = ",";
	final static public String SEMICOLON = ";";
	final static public String EQUALS = "=";
	final static public String DOUBLE_QUOTE = "\"";
	
	final static public String DEFAULT_FIELD_SEPARATOR = COMMA;
	final static public DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd" );

	public static final String BANK_TAN_MAX_KEY = "bank.tan.max";
	public static final String BANK_SOCKET_PORT_KEY = "bank.socket.port";
	public static final String BANK_HTTP_PORT_KEY = "bank.http.port";
	public static final String BANK_DROP_BOX_DIR_KEY = "bank.drop.box.dir";	
	public static final String BANK_ACCOUNT_START_AMOUNT_KEY = "bank.account.start.amount";
}
