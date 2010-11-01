package com.auce.util;


public interface Mapper<T>
{
	public T read( String source, String message );
	public String write( T obj );
}
