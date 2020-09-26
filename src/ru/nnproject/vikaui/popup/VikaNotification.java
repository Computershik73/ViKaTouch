package ru.nnproject.vikaui.popup;

import javax.microedition.lcdui.Graphics;

public class VikaNotification {
	
	public int type;
	
	public String title;
	public String text;
	
	public static final int COMMON = 0;
	public static final int NEW_MSG = 1;
	public static final int NEXT_TRACK = 2;
	
	public boolean active = true;
	public long sendTime;
	
	public VikaNotification(int _type, String _title, String _text)
	{
		type = _type;
		title = _title;
		text = _text;
		sendTime = System.currentTimeMillis();
	}
	
	public void draw(Graphics g)
	{
		if(active)
		{
			long t = System.currentTimeMillis()-sendTime;
			if(t>4000L)
			{
				active = false;
				return;
			}
			int y = 0;
		}
	}
}
