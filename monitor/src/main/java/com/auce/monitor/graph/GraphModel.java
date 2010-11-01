package com.auce.monitor.graph;

import java.awt.Color;
import java.util.List;

public interface GraphModel
{
	public Color getColor();
	public List<Integer> values();
	public void addGraphModelListener( GraphModelListener listener );
	public String getTitle ();
}
