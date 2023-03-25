// This file is part of VikaUI
// Copyright (C) 2020  Arman Jussuplaliyev (Shinovon)

package ru.nnproject.vikaui.screen;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.MathUtils;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.emulatordetect.*;
import vikatouch.VikaTouch;
import vikatouch.items.chat.MsgItem;


/**
 * @author Shinovon
 * 
 */

public abstract class ScrollableCanvas extends VikaScreen {

	public static boolean dragging;
	protected boolean canScroll;
	public static short oneitemheight = 50;
	public short itemsCount = 5;
	public int listHeight = itemsCount * oneitemheight;
	public int scroll;
	public Vector uiItems;
	public short scrollOffset;
	public int currentItem;
	public static boolean keysMode;
	public boolean scrollWithKeys = false;


	/*
	 * Целевая координата, к которой будет лерпаться прокрутка.
	 */
	public int scrollTarget;
	private int scrollTimer;
	/*
	 * Если false, лерпаться не будет.
	 */
	public static boolean scrollTargetActive;
	
	static {
		if(DisplayUtils.canvas != null) {
			keysMode = !DisplayUtils.canvas.hasPointerEvents();
		}
	}

	public ScrollableCanvas() {
		super();
		scrollTargetActive=false;
		//scrolled=0;
		//repaint();
	}

	public abstract void draw(Graphics g);

	public final void drag(int x, int y) {
		VikaTouch.needRepaint();
	}

	public boolean scroll(int units) {
		if(listHeight == 0 || listHeight + 100 <= DisplayUtils.height) {
			scroll = 0;
			return false;
		}
		if(scroll + units < -(listHeight + 100) + DisplayUtils.height) {
			scroll = -(listHeight + 100) + DisplayUtils.height;
			return false;
		}
		if(units == 0) return false;
		scroll += units;
		if (scroll > 0) {
			scroll = 0;
			return false;
		}
		scrollTimer = 0;
		scrollTarget = 1;
		return true;
	}
	
	protected abstract void scrollHorizontally(int deltaX);

	public static boolean poorScrolling() {
		return DisplayUtils.canvas.poorScrolling();
	}

	public void tap(int x, int y, int time) {
		VikaTouch.needRepaint();
	}

	public void press(int key) {
		try {
			if (key != -12 && key != -20) {
				keysMode = true;
			}
			if (key == -1) {
				up();
			} else if (key == -2) {
				down();
			} else if (key == -3) {
				DisplayUtils.canvas.callCommand(10, this);
			} else if (key == -4) {
				DisplayUtils.canvas.callCommand(11, this);
			} else if (key == -7) {
				DisplayUtils.canvas.callCommand(14, this);
			} else {
				((PressableUIItem) uiItems.elementAt(currentItem)).keyPress(key);
			}
		} catch (Throwable e) {
		}
		VikaTouch.needRepaint();
	}

	public void repeat(int key) {
		try {
			if (key != -12 && key != -20) {
				keysMode = true;
			}
			if (key == -1) {
				up();
			}
			if (key == -2) {
				down();
			}
		} catch (Throwable e) {

		}
		VikaTouch.needRepaint();
	}

	// private int scrolebd;

	protected void down() {
		if (scrollWithKeys) {
			keysScroll(-1);
			return;
		}
		// TODO: паблик бета
		/*
		 * if(uiItems[currentItem].getDrawHeight() > vmeshautsa) { if(scrolebd
		 * == 0) { scrolebd = uiItems[currentItem].getDrawHeight(); }
		 * if(scrolebd == -1) { scrolebd = 0; try {
		 * uiItems[currentItem].setSelected(false); } catch (Exception e) {
		 * 
		 * } currentItem++; if(currentItem >= itemsCount) { } else scrolled -=
		 * uiItems[currentItem].getDrawHeight();
		 * uiItems[currentItem].setSelected(true); }
		 * 
		 * 
		 * int x = 20; if(scrolebd < x) { scrolled -= scrolebd; scrolebd = -1; }
		 * else { scrolled -= x; scrolebd -= x; } } else
		 */
		{
			try {
				((PressableUIItem) uiItems.elementAt(currentItem)).setSelected(false);

				currentItem++;
				if (currentItem >= itemsCount) {
					currentItem = itemsCount - 1;
					//currentItem = 0;
				}
				scrollToSelected();
				((PressableUIItem) uiItems.elementAt(currentItem)).setSelected(true);
			} catch (Throwable e) {

			}
		}
		VikaTouch.needRepaint();
	}

	protected void up() {
		if (scrollWithKeys) {
			keysScroll(+1);
			return;
		}
		try {
			((PressableUIItem) uiItems.elementAt(currentItem)).setSelected(false);
		} catch (Throwable e) {

			e.printStackTrace();

		}
		currentItem--;
		if (currentItem < 0) {
			currentItem = 0;
			//currentItem = (short) (itemsCount - 1);
		}
		scrollToSelected();
		try {
			((PressableUIItem) uiItems.elementAt(currentItem)).setSelected(true);
		} catch (Throwable e) {
			e.printStackTrace();

		}
		VikaTouch.needRepaint();
	}

	public abstract void scrollToSelected();

	public abstract void selectCentered();

	public void select(int i) {
		try {
			if (i < 0)
				i = 0;
			if (i >= uiItems.size())
				i = uiItems.size() - 1;
			try {
				((PressableUIItem) uiItems.elementAt(currentItem)).setSelected(false);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			try {

				((PressableUIItem) uiItems.elementAt(i)).setSelected(true);
				currentItem = i;
			} catch (RuntimeException e) {
				System.out.println(i);
				e.printStackTrace();
			}
			VikaTouch.needRepaint();
		} catch (Throwable e) {

		}
	}

	protected abstract void keysScroll(int dir);

	protected final void update(Graphics g) {
		if(Math.abs(scroll) > 65535) return;
		if(scrollTarget <= 0) {
			scrollTimer = 0;
			VikaTouch.scrolling = true;
			if (Math.abs(scroll - scrollTarget) < 1) {
				scroll = scrollTarget;
				scrollTarget = 1;
				VikaTouch.scrolling = false;
			} else {
				scroll = MathUtils.lerp(scroll, scrollTarget, 4, 20);
			}
			if(scroll > 0) {
				scroll = 0;
				scrollTarget = 1;
				VikaTouch.scrolling = false;
			}
			if(scroll < -(listHeight + 100) + DisplayUtils.height) {
				scroll = -(listHeight + 100) + DisplayUtils.height;
				scrollTarget = 1;
				VikaTouch.scrolling = false;
			}
		}
		if(scroll < -(listHeight + 100) + DisplayUtils.height && scroll != 0 && !VikaTouch.scrolling) {
			scroll = -(listHeight + 100) + DisplayUtils.height;
		}
		if(scroll > 0) {
			scroll = 0;
		}
		g.translate(0, scroll);
	}

	public void smoothlyScrollTo(int i) {
		if(i > 0) i = 0;
		scrollTarget = i;
		repaint();
	}
	
	protected void callRefresh() {

	}

	public int getItemY(int n) {
		try {
		//	VikaUtils.logToFile("getItemY");
			String l= "";
			if (uiItems == null)
				return 1;
			int y = 0;
			for (int i = 0; (i < uiItems.size() && i < n); i++) {
				if (uiItems.elementAt(i) != null) {
					if (((PressableUIItem) uiItems.elementAt(i)).getDrawHeight()<=1) {
						try {
						((MsgItem) uiItems.elementAt(i)).paint(VikaTouch.canvas.getG(), 0, scroll);
						} catch (Throwable eee) {
							VikaUtils.logToFile("pizdec");
						}
					}
					
					y += ((PressableUIItem) uiItems.elementAt(i)).getDrawHeight(); // не УМНОЖИТЬ! айтемы
														// могут быть разной
														// высоты.
					//if (uiItems.elementAt(i) instance)
					//VikaUtils.logToFile(" "+String.valueOf(i)+ " " + String.valueOf(((PressableUIItem) uiItems.elementAt(i)).getDrawHeight()));
					//l=l+String.valueOf(y)+ " ";
				}
			}
			//VikaUtils.logToFile("total " + l);
			return y;
		} catch (Throwable e) {	
			VikaUtils.logToFile(e.getMessage());

		}
		return 0;
	}

}

