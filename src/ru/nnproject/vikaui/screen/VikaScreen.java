package ru.nnproject.vikaui.screen;

import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.utils.DisplayUtils;

public abstract class VikaScreen
{
	public abstract void draw(Graphics g);
	
	public void press(int x, int y)
	{
		
	}
	
	public void release(int x, int y)
	{
		
	}
	
	public void drag(int x, int y)
	{
		
	}

	public void press(int i)
	{
		
	}

	public void release(int i)
	{
		
	}

	public void repeat(int i)
	{
		
	}
	
	protected void repaint()
	{
		if(DisplayUtils.canvas != null)
		{
			DisplayUtils.canvas.paint();
		}
	}
}
