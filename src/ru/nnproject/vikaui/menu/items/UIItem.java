package ru.nnproject.vikaui.menu.items;

import javax.microedition.lcdui.Graphics;

public interface UIItem
{
	public void paint(Graphics g, int y, int scrolled);
	
	public int getDrawHeight();
	
	public void addDrawHeight(int i);
	
	public void setDrawHeight(int i);
}
