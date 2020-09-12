package ru.nnproject.vikaui.screen;

import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.VikaCanvas;
import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.screens.MainScreen;

public abstract class ScrollableCanvas
	extends VikaScreen
{
	
	protected int startx;
	protected int starty;
	protected int endx;
	protected int endy;
	protected short scroll;
	protected int scrolled;
	protected int lasty;
	protected boolean dragging;
	protected boolean canScroll;
	public static short oneitemheight = 50;
	public short itemsCount = 5;
	public int itemsh = itemsCount * oneitemheight;
	protected int lastx;
	public static short vmeshautsa = 528;
	public static final double scrollSpeed = 1.8;
	public PressableUIItem[] uiItems;
	public short scrollOffset;
	public int currentItem;
	public static boolean keysMode = false;
	public boolean scrollWithKeys = false;
	
	public short drift;
	public short driftSpeed;
	public short scrollingTimer;
	protected short scrollPrev;
	protected short timer;
	
	public ScrollableCanvas()
	{
		super();
	}

	public abstract void draw(Graphics g);

	public final void drag(int x, int y)
	{
		keysMode = false;
		if(!dragging)
			lasty = starty;
		final int deltaX = lastx - x;
		final int deltaY = lasty - y;
		final int ndeltaX = Math.abs(deltaX);
		final int ndeltaY = Math.abs(deltaY);
		if(canScroll)
		{
			if(ndeltaY > ndeltaX)
			{
				scroll = (short)((double) -deltaY * scrollSpeed);
				scrollPrev += scroll;
				scrollingTimer += Math.abs(scroll) / 14;
				if(Math.abs(scroll / 3) > Math.abs(driftSpeed))
					driftSpeed = (short) (scroll / 3);
				if(poorScrolling())
					scroll *= 4;
			}
			else
			{
				scrollHorizontally(deltaX);
			}
		}
		if(DisplayUtils.canvas.isSensorModeOK())
		{
			if(ndeltaY > 0 || ndeltaX > 0)
			{
				dragging = true;
			}
		}
		else if(DisplayUtils.canvas.isSensorModeJ2MELoader())
		{
			if(ndeltaY > 1 || ndeltaX > 1)
			{
				dragging = true;
			}
		}
		if(poorScrolling())
		{
			dragging = true;
		}
		lastx = x;
		lasty = y;
		timer = 0;
	}

	protected abstract void scrollHorizontally(int deltaX);

	public void press(int x, int y)
	{
		timer = 0;
		VikaCanvas.debugString = "pressed " + x +" " +y;
		scrollingTimer = 0;
		drift = 0;
		driftSpeed = 0;
		scrollPrev = 0;
		keysMode = false;
		startx = x;
		starty = y;
		endx = -1;
		endy = -1;
	}
	
	public static boolean poorScrolling()
	{
		return DisplayUtils.canvas.poorScrolling();
	}

	public void release(int x, int y)
	{
		VikaCanvas.debugString = "released " + x +" " +y;
		if(!poorScrolling() && timer < 7)
		{
			if(scrollPrev != 0)
				drag(x, y);
			drift = scrollPrev;
		}
		scrollPrev = 0;
		keysMode = false;
		endx = x;
		endy = y;
		dragging = false;
		repaint();
	}
	
	public void press(int key)
	{
		if(key != -12 && key != -20)
		{
			keysMode = true;
		}
		if(key == -1)
		{
			up();
		}
		else if(key == -2)
		{
			down();
		}
		else if(key == -3)
		{
			DisplayUtils.canvas.callCommand(10, this);
		}
		else if(key == -4)
		{
			DisplayUtils.canvas.callCommand(11, this);
		}
		else if(key == -7)
		{
			DisplayUtils.canvas.callCommand(14, this);
		}
		else
		{
			uiItems[currentItem].keyPressed(key);
		}
		repaint();
		try
		{
			VikaCanvas.debugString = "" + key + " " + DisplayUtils.canvas.getKeyName(key) + " " + currentItem + " " + itemsCount + " " + uiItems[currentItem].isSelected();
		}
		catch (Exception e)
		{
			
		}
	}
	
	public void repeat(int key)
	{
		if(key != -12 && key != -20)
		{
			keysMode = true;
		}
		if(key == -1)
		{
			up();
		}
		if(key == -2)
		{
			down();
		}
		repaint();
	}
	
	private int scrolebd;
	
	protected void down()
	{
		if(scrollWithKeys)
		{
			keysScroll(-1);
			return;
		}
		//TODO: паблик бета
		/*
		if(uiItems[currentItem].getDrawHeight() > vmeshautsa)
		{
			if(scrolebd == 0)
			{
				scrolebd = uiItems[currentItem].getDrawHeight();
			}
			if(scrolebd == -1)
			{
				scrolebd = 0;
				try
				{
					uiItems[currentItem].setSelected(false);
				}
				catch (Exception e)
				{
					
				}
				currentItem++;
				if(currentItem >= itemsCount)
				{
				}
				else
					scrolled -= uiItems[currentItem].getDrawHeight();
				uiItems[currentItem].setSelected(true);
			}
			
			
			int x = 20;
			if(scrolebd < x)
			{
				scrolled -= scrolebd;
				scrolebd = -1;
			}
			else
			{
				scrolled -= x;
				scrolebd -= x;
			}
		}
		else
		*/
		{
			try
			{
				uiItems[currentItem].setSelected(false);
			}
			catch (Exception e)
			{
				
			}
			currentItem++;
			if(currentItem >= itemsCount)
			{
				currentItem = 0;
			}
			scrollToSelected();
			uiItems[currentItem].setSelected(true);
		}
	}

	protected void up()
	{
		if(scrollWithKeys)
		{
			keysScroll(+1);
			return;
		}
		try
		{
			uiItems[currentItem].setSelected(false);
		}
		catch (Exception e) { }
		currentItem--;
		if(currentItem < 0)
		{
			currentItem = (short) (itemsCount-1);
		}
		scrollToSelected();
		try 
		{
			uiItems[currentItem].setSelected(true);
		}
		catch (Exception e) { }
	}
	
	public void scrollToSelected()
	{
		scrolled = -(getItemY(currentItem)-DisplayUtils.height/2+(uiItems[currentItem].getDrawHeight()/2)+MainScreen.topPanelH);
	}
	
	protected final void keysScroll(int dir)
	{
		scroll = (short)(dir*80);
		//scrollPrev += scroll;
		scrollingTimer = 25;
		driftSpeed = (short) (20*dir);
		drift = scroll;
		scrollPrev = 0;
	}

	protected final void update(Graphics g)
	{
		try
		{
			if(!poorScrolling())
			{
				if(timer < 3200)
					timer++;
				if(scrollingTimer > 0)
					scrollingTimer--;
				
				if(drift != 0 && driftSpeed != 0 && scrollingTimer > 5)
				{
					scroll += driftSpeed;
					drift -= driftSpeed;
					driftSpeed *= 0.975;
				}
			}
		}
		catch(ArithmeticException e)
		{
			
		}
		boolean d2 = scroll != 0;
		if(itemsh > vmeshautsa)
		{
			canScroll = true;
		}
		else
		{
			canScroll = false;
			if(scrolled < 0)
			{
				scrolled = 0;
			}
		}
		if(d2)
		{
			scrolled = scrolled + scroll;
			if(scrolled > 0)
			{
				scrolled = 0;
			}
			if(scrolled < vmeshautsa - itemsh && scrolled != 0)
			{
				scrolled = vmeshautsa - itemsh;
			}
			g.translate(0, scrolled);
			scroll = 0;
		}
		else
		{
			if(scrolled > 0)
			{
				scrolled = 0;
			}
			if(scrolled < vmeshautsa - itemsh && scrolled != 0)
			{
				scrolled = vmeshautsa - itemsh;
			}
			g.translate(0, scrolled);
		}
		if(!poorScrolling())
			scroll = 0;
	}

	protected void callRefresh()
	{
		
	}
	
	public int getItemY(int n)
	{
		int y=0;
		for(int i=0;(i<uiItems.length&&i<n);i++)
		{
			y += uiItems[i].getDrawHeight(); // не УМНОЖИТЬ! айтемы могут быть разной высоты.
		}
		return y;
	}
	
}
