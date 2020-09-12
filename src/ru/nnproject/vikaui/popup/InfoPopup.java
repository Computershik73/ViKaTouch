package ru.nnproject.vikaui.popup;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import vikatouch.VikaTouch;
import vikatouch.screens.MainScreen;

public class InfoPopup
	extends VikaNotice
{
	private String[] lines;
	private Runnable ok;
	private int linesCount;
	private String button;
	private String header;
	
	public InfoPopup(String text, Runnable onOk, String title, String btnText)
	{
		lines = TextBreaker.breakText(text, Font.getFont(0, 0, Font.SIZE_SMALL), Math.min(DisplayUtils.width-20, 340)-60);
		ok = onOk;
		linesCount = lines.length;
		header = title;
		button = btnText==null?"OK":btnText;
	}
	
	public InfoPopup(String text, Runnable onOk)
	{
		lines = TextBreaker.breakText(text, Font.getFont(0, 0, Font.SIZE_SMALL), Math.min(DisplayUtils.width-20, 340)-60);
		ok = onOk;
		linesCount = lines.length;
		header = null;
		button = "OK";
	}

	public void draw(Graphics g) {
		int width = Math.min(DisplayUtils.width-8, 350);
		Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
		Font hf = Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_MEDIUM); // Header Font
		int hh = hf.getHeight(); // Header Height
		int h1 = f.getHeight();
		int hp = header==null?0:hh+(h1/2); // Header Place (сколько он занимает)
		int th = h1*4 + h1*linesCount + hp;
		int y = DisplayUtils.height/2 - th/2;
		int x = DisplayUtils.width/2 - width/2;
		int btnW = Math.max(f.stringWidth(button)+20, 60);
		
		// drawing
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRoundRect(x, y, width, th, 16, 16);
		
		ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
		g.fillRoundRect(DisplayUtils.width/2-btnW/2, y+h1*(linesCount+1)+hp, btnW, h1*2, 14, 14); // кнопка
		if(header!=null)
		{
			g.setFont(hf);
			g.drawString(header, DisplayUtils.width/2-hf.stringWidth(header)/2, y+h1/2, 0);
		}
		
		g.setFont(f);
		g.setStrokeStyle(Graphics.SOLID);
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawRoundRect(x, y, width, th, 16, 16); // бордер
		for(int i = 0; i < linesCount;i++)
		{
			if(lines[i]!=null) g.drawString(lines[i], DisplayUtils.width/2 - f.stringWidth(lines[i])/2, y+hp+h1/2+h1*i, 0);
		}
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.drawString(button, DisplayUtils.width/2-f.stringWidth(button)/2, y+hp+h1*(linesCount+1)+h1/2, 0); // кнопка
	}
	
	public void press(int key)
	{
		if(!(key == PressableUIItem.KEY_OK || key == PressableUIItem.KEY_FUNC))
			return;
		VikaTouch.canvas.currentAlert = null;
		if(ok!=null)
			new Thread(ok).start();
	}
	
	public void release(int x, int y)
	{
		int width = Math.min(DisplayUtils.width-8, 350);
		Font f = Font.getFont(0, 0, Font.SIZE_MEDIUM);
		int h1 = f.getHeight();
		int th = h1*4 + h1*linesCount;
		int ry = DisplayUtils.height/2 - th/2;
		int rx = DisplayUtils.width/2 - width/2;
		
		if(y>ry+h1*(linesCount+1) && y<ry+th-h1)
		{
			if(x>DisplayUtils.width/2-25 && x<DisplayUtils.width/2+25)
			{
				press(PressableUIItem.KEY_OK);
			}
		}
	}
}
