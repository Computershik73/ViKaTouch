package ru.nnproject.vikaui.popup;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.screens.MainScreen;

public class ConfirmBox
	extends VikaNotice
{

	private String line1;
	private String line2;
	private Runnable ok;
	private Runnable cancel;
	private boolean hasYes;
	private String customYes;
	private String customNo;
	
	public ConfirmBox(String text, String subtext, Runnable onOk, Runnable onCancel)
	{
		line1 = text;
		line2 = subtext;
		ok = onOk;
		cancel = onCancel;
		this.hasYes = true;
	}
	
	public ConfirmBox(String text, String subtext, Runnable onOk, Runnable onCancel, boolean hasYes)
	{
		line1 = text;
		line2 = subtext;
		ok = onOk;
		cancel = onCancel;
		this.hasYes = hasYes;
	}
	
	public ConfirmBox(String text, String subtext, Runnable onOk, Runnable onCancel, String customYes, String customNo, boolean hasYes)
	{
		line1 = text;
		line2 = subtext;
		ok = onOk;
		cancel = onCancel;
		this.hasYes = hasYes;
		this.customYes = customYes;
		this.customNo = customNo;
	}
	
	public void draw(Graphics g)
	{
		int width = Math.min(DisplayUtils.width-20, 300);
		Font f = Font.getFont(0, 0, Font.SIZE_MEDIUM);
		int h1 = f.getHeight();
		int th = h1*6;
		int y = DisplayUtils.height/2 - th/2;
		int x = DisplayUtils.width/2 - width/2;
		String okT = "Да";
		String cancT = "Отмена";
		if(customYes != null)
			okT = customYes;
		if(customNo != null)
			cancT = customNo;
		
		// drawing
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRoundRect(x, y, width, th, 16, 16);
		
		ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
		if(hasYes)
			g.fillRoundRect(x+20, y+h1*3, width/2-40, h1*2, 30, 30);
		g.fillRoundRect(DisplayUtils.width/2+20, y+h1*3, width/2-40, h1*2, 30, 30);
		
		g.setFont(f);
		g.setStrokeStyle(Graphics.SOLID);
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawRoundRect(x, y, width, th, 16, 16);
		if(line1!=null)
			g.drawString(line1, DisplayUtils.width/2 - f.stringWidth(line1)/2, y+h1/2, 0);
		if(line2!=null)
			g.drawString(line2, DisplayUtils.width/2 - f.stringWidth(line2)/2, y+h1+h1/2, 0);
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		if(hasYes)
			g.drawString(okT, ((x+20)+(DisplayUtils.width/2-20))/2-f.stringWidth(okT)/2, y+h1*3+h1/2, 0);
		g.drawString(cancT, ((DisplayUtils.width/2+20)+(DisplayUtils.width/2+20+(width/2-40)))/2-f.stringWidth(cancT)/2, y+h1*3+h1/2, 0);
	}
	
	public void press(int key)
	{
		if(key == PressableUIItem.KEY_OK || key == PressableUIItem.KEY_FUNC)
		{
			VikaTouch.canvas.currentAlert = null;
			if(ok!=null)
				new Thread(ok).start();
		}
		else if(key == PressableUIItem.KEY_RFUNC || key == PressableUIItem.KEY_BACK)
		{
			VikaTouch.canvas.currentAlert = null;
			if(cancel!=null)
				new Thread(cancel).start();
		}
	}
	
	public void release(int x, int y)
	{
		int width = Math.min(DisplayUtils.width-20, 300);
		Font f = Font.getFont(0, 0, Font.SIZE_MEDIUM);
		int h1 = f.getHeight();
		int th = h1*6;
		int ry = DisplayUtils.height/2 - th/2;
		int rx = DisplayUtils.width/2 - width/2; // углы диалога
		
		if(y>ry+h1*3 && y<ry+h1*5)
		{
			if(x>rx+20 && x<DisplayUtils.width/2-20)
			{
				press(PressableUIItem.KEY_OK);
			}
			else if(x>DisplayUtils.width/2+20 && x<DisplayUtils.width/2+20+(width/2-40))
			{
				press(PressableUIItem.KEY_RFUNC);
			}
		}
	}
}
