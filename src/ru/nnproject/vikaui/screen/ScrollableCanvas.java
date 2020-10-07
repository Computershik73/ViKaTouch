package ru.nnproject.vikaui.screen;

import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.VikaCanvas;
import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.screens.MainScreen;
import vikatouch.utils.VikaUtils;

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
	
	public String scrlDbg = "";
	
	
	/*
	 * Целевая координата, к которой будет лерпаться прокрутка.
	 */
	public int scrollTarget;
	/*
	 * Если false, лерпаться не будет.
	 */
	public boolean scrollTargetActive;
	
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
			if(ndeltaY > 4 || ndeltaX > 4)
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
	
	public void selectCentered()
	{
		System.out.println("select center");
		int y = MainScreen.topPanelH;
		int ye = y;
		int s = -scrolled + DisplayUtils.height/2;
		for(int i=0;(i<uiItems.length);i++)
		{
			ye = y + uiItems[i].getDrawHeight();
			if(y<=s && ye > s)
			{
				select(i);
				return;
			}
			y = ye;
		}
	}
	
	
	public void select(int i)
	{
		System.out.println("select "+i);
		if(i<0) i = 0;
		if(i>=uiItems.length) i = uiItems.length - 1;
		try
		{
			uiItems[currentItem].setSelected(false);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		try
		{
			uiItems[i].setSelected(true);
			currentItem = i;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
	
	protected final void keysScroll(int dir)
	{
		int delta = DisplayUtils.height/3;
		int st = 0;
		int thisItemY = getItemY(currentItem);
		int topItemY = getItemY(currentItem-1);
		int downItemY = thisItemY+50;
		int down2ItemY = downItemY+50;
		try
		{
			downItemY = thisItemY+uiItems[currentItem].getDrawHeight();
			down2ItemY = downItemY+uiItems[currentItem+1].getDrawHeight();
		}
		catch(RuntimeException e1) { }
		int scrY = -scrolled - MainScreen.topPanelH + DisplayUtils.height/2;
		int br = 0;
		int sc = 0;
		scrlDbg = "dir"+dir+" "+topItemY+" "+thisItemY+" "+downItemY+" "+down2ItemY+" d"+delta + " scry"+scrY;
		if(dir>0)
		{
			// up
			if(scrY-thisItemY<0)
			{
				sc=1;
				selectCentered();
				thisItemY = getItemY(currentItem);
				topItemY = getItemY(currentItem-1);
				try
				{
					downItemY = thisItemY+uiItems[currentItem].getDrawHeight();
				}
				catch(RuntimeException e1) { }
			}
			
			if(scrY-thisItemY > delta)
			{
				br=1;
				st = -delta;
			}
			else if(scrY-topItemY > delta)
			{
				br=2;
				st = -delta;
				select(currentItem-1);
			}
			else if(thisItemY<10)
			{
				br = 7;
				st = -delta;
				select(0);
			}
			else
			{
				br=3;
				st = -topItemY+1;
				select(currentItem-1);
			}
		}
		else
		{
			// down
			if(down2ItemY-scrY > delta && downItemY-scrY <= delta)
			{
				br=5;
				st = delta;
				select(currentItem+1);
			}
			else if(downItemY-scrY > delta)
			{
				br=4;
				st = delta;
			}
			else
			{
				br=6;
				st = (down2ItemY-scrY-1);
				select(currentItem+1);
			}
		}
		scrollTarget = Math.min(-MainScreen.topPanelH, -st + scrolled);
		scrlDbg += " st"+st+ "br"+br+"s"+sc;
		scrollTargetActive = true;
	}

	protected final void update(Graphics g)
	{
		if(scrollTargetActive)
		{
			scroll = 0;
			if(Math.abs(scrolled-scrollTarget)<4)
			{
				scrolled = scrollTarget;
				scrollTargetActive = false;
			}
			else
			{
				scrolled = VikaUtils.lerp(scrolled, scrollTarget, 15, 100);
			}
			g.translate(0, scrolled);
			return;
		}
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
		if(scroll != 0)
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
		if(uiItems==null) return 1;
		int y=0;
		for(int i=0;(i<uiItems.length&&i<n);i++)
		{
			if(uiItems[i] != null)
				y += uiItems[i].getDrawHeight(); // не УМНОЖИТЬ! айтемы могут быть разной высоты.
		}
		return y;
	}
	
}
