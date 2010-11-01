package com.auce.util;



public interface Dao<T>
{
	int count();
	T[] list();
	T find( String id );
	void add( T o );
}
