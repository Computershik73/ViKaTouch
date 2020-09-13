package vikatouch.screens;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.Dialogs;
import vikatouch.VikaTouch;
import vikatouch.locale.TextLocal;
import vikatouch.screens.menu.MenuScreen;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;

public class DialogsScreen
	extends MainScreen
	{

	private static String titleStr;

	public DialogsScreen()
	{
		super();
		
		VikaTouch.loading = true;
		
		if(VikaTouch.menuScr == null)
		{
			VikaTouch.menuScr = new MenuScreen();
		}
		if(titleStr == null)
		{
			titleStr = TextLocal.inst.get("title.chats");
		}
	}

	protected final void callRefresh()
	{
		VikaTouch.loading = true;
		Dialogs.refreshDialogsList(true);
	}
	
	public final void press(int key)
	{
		if(key != -12 && key != -20)
		{
			keysMode = true;
		}
		if(key == -5)
		{
			Dialogs.dialogs[currentItem].keyPressed(-5);
		}
		else if(key == -6)
		{
			callRefresh();
			repaint();
		}
		else if(key == -1)
		{
			up();
		}
		else if(key == -2)
		{
			down();
		}
		else
			super.press(key);
	}
	
	protected final void up()
	{
		try
		{
			Dialogs.dialogs[currentItem].setSelected(false);
		}
		catch (Exception e)
		{
			
		}
		currentItem--;
		if(currentItem < 0)
		{
			currentItem = Dialogs.itemsCount--;
		}
		scrollToSelected();
		Dialogs.dialogs[currentItem].setSelected(true);
	}
	
	protected final void down()
	{
		try
		{
			Dialogs.dialogs[currentItem].setSelected(false);
		}
		catch (Exception e)
		{
			
		}
		currentItem++;
		if(currentItem >= Dialogs.itemsCount)
		{
			currentItem = 0;
		}
		scrollToSelected();
		Dialogs.dialogs[currentItem].setSelected(true);
	}
	
	public final void scrollToSelected()
	{
		int itemy=0;
		for(int i=0;(i<Dialogs.dialogs.length&&i<currentItem);i++)
		{
			itemy += Dialogs.dialogs[i].getDrawHeight(); // не УМНОЖИТЬ! айтемы могут быть разной высоты.
		}
		scrolled = -(itemy-DisplayUtils.height/2+(Dialogs.dialogs[currentItem].getDrawHeight()/2)+MainScreen.topPanelH);
	}

	public void draw(Graphics g)
	{
		ColorUtils.setcolor(g, 0);
		g.setFont(Font.getFont(0, 0, 8));
		itemsh = Dialogs.itemsCount * 63;
		double multiplier = (double)DisplayUtils.height / 640.0;
		double ww = 10.0 * multiplier;
		int w = (int)ww;
		try
		{
			update(g);
			int y = oneitemheight + w;
			try
			{
				if(Dialogs.dialogs !=null)
				{
					for(int i = 0; i < Dialogs.itemsCount; i++)
					{
						if(Dialogs.dialogs[i] != null)
						{
							Dialogs.dialogs[i].paint(g, y, scrolled);
							y += Dialogs.dialogs[i].itemDrawHeight;
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				VikaTouch.error(e, ErrorCodes.DIALOGSITEMDRAW);
			}
			g.translate(0, -g.getTranslateY());
			
		}
		catch (Exception e)
		{
			VikaTouch.error(e, ErrorCodes.DIALOGSDRAW);
			e.printStackTrace();
		}
	}
	
	public final void drawHUD(Graphics g)
	{
		drawHUD(g, Dialogs.dialogs==null?"Загрузка диалогов...":titleStr); // временно, потом оно будет грузиться во время сплеша. Привет Илье))0)
	}
	
	public void unselectAll()
	{
		if(Dialogs.selected)
		{
			for(int i = 0; i < Dialogs.itemsCount; i++)
			{
				if(Dialogs.dialogs[i] != null)
				{
					Dialogs.dialogs[i].selected = false;
				}
				Thread.yield();
			}
			Dialogs.selected = false;
		}
	}
	
	public final void release(int x, int y)
	{
		// тача больше нигде нет. Ладно.
		try
		{
			if(Dialogs.dialogs!=null)
			{
				if(y > 58 && y < DisplayUtils.height - oneitemheight)
				{
					int yy1 = (y - 58) - scrolled;
					int i = yy1 / 63;
					if(i < 0)
						i = 0;
					unselectAll();
					if(!dragging)
					{
						Dialogs.dialogs[i].tap(x, yy1 - (63 * i));
					}
					Dialogs.dialogs[i].released(dragging);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		super.release(x, y);
	}
	
	public final void press(int x, int y)
	{
		try
		{
			if(Dialogs.dialogs!=null)
			{
				if(y > 58 && y < DisplayUtils.height - oneitemheight)
				{

					int yy1 = (y - 58) - scrolled;
					int i = yy1 / 63;
					if(i < 0)
						i = 0;
					unselectAll();
					Dialogs.dialogs[i].pressed();
					repaint();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		super.press(x, y);
	}

	protected void scrollHorizontally(int deltaX)
	{
		
	}

}
