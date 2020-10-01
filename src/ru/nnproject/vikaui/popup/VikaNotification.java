package ru.nnproject.vikaui.popup;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ru.nnproject.vikaui.screen.VikaScreen;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;

public class VikaNotification {
	
	public int type;
	
	public String title;
	public String text;
	public VikaScreen screen;
	
	public static final int COMMON = 0;
	public static final int NEW_MSG = 1;
	public static final int NEXT_TRACK = 2;
	public static final int ERROR = 3;
	
	public static Image[] icons;
	
	public boolean active = true;
	public long sendTime;
	
	public static int nofX, nofH;
	
	public VikaNotification(int _type, String _title, String _text, VikaScreen _screen)
	{
		type = _type;
		title = _title;
		text = _text;
		screen = _screen;
		sendTime = System.currentTimeMillis();
	}
	
	public void draw(Graphics g)
	{
		if(icons==null || icons.length!=4)
		{
			icons = new Image[]
			{
				IconsManager.ico[IconsManager.INFO],
				IconsManager.ico[IconsManager.MSGS],
				IconsManager.ico[IconsManager.MUSIC],
				IconsManager.ico[IconsManager.CLOSE]
			};
		}
		if(active)
		{
			long t = System.currentTimeMillis()-sendTime;
			if(t>3000L)
			{
				active = false;
				return;
			}
			Font f = Font.getFont(0, 0, 8);
			int fh = f.getHeight();
			int y = (t<2500L)?0:(int)(
				-((t-2500L)/500.0f)*fh*2
			);
			
			int left = nofX = 20;
			nofH = fh*2+y;
			
			ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
			g.fillRect(left, y, DisplayUtils.width-left*2, fh*2);
			ColorUtils.setcolor(g, ColorUtils.TEXT);
			g.drawRect(left, y, DisplayUtils.width-left*2, fh*2);
			g.drawImage(icons[type], left+4, y+fh-12, 0);
			
			g.drawString(text, left+32, y+fh, 0);
			ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
			g.drawString(title, left+32, y, 0);
		}
	}

	public void open()
	{
		if(screen!=null) VikaTouch.setDisplay(screen, 1);
	}
	public static void vib()
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					Display d = Display.getDisplay(VikaTouch.appInst);
					d.vibrate(40);
					Thread.sleep(80);
					d.vibrate(40);
					Thread.sleep(80);
					d.vibrate(40);
				} catch (Exception e) {
				}
			}
		}.start();
	}
}
