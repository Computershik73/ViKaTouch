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

	protected int startx;
	protected int starty;
	protected int endx;
	protected int endy;
	protected short scroll;
	public static int scrolled;
	protected int lasty;
	public static boolean dragging;
	protected boolean canScroll;
	public static short oneitemheight = 50;
	public short itemsCount = 5;
	public int itemsh = itemsCount * oneitemheight;
	protected int lastx;
	public static short vmeshautsa = 528;
	public static final double scrollSpeed = 1.8;
	public Vector uiItems;
	public short scrollOffset;
	public int currentItem;
	public static boolean keysMode;
	public boolean scrollWithKeys = false;

	public boolean drift;
	public float driftSpeed;
	public float driftingTime;
	protected boolean preDrift;
	private long pressTime;

	/*
	 * Целевая координата, к которой будет лерпаться прокрутка.
	 */
	public int scrollTarget;
	/*
	 * Если false, лерпаться не будет.
	 */
	public static boolean scrollTargetActive;
	private long releaseTime;
	
	static {
		if(DisplayUtils.canvas != null) {
			keysMode = !DisplayUtils.canvas.hasPointerEvents();
		}
	}

	public ScrollableCanvas() {
		super();
		//repaint();
	}

	public abstract void draw(Graphics g);

	public final void drag(int x, int y) {
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
		keysMode = false;
		/*if (DisplayUtils.canvas.isSensorModeJ2MELoader()) {
			if (!dragging) {
				lastx = startx;
				lasty = starty;
			}
			final int deltaX = lastx - x;
			final int deltaY = lasty - y;
			final int ndeltaX = Math.abs(deltaX);
			final int ndeltaY = Math.abs(deltaY);
			if (ndeltaY > 4 || ndeltaX > 2) {
				if (canScroll) {
					if (ndeltaY > ndeltaX) {
						scroll = (short) ((double) -deltaY * scrollSpeed);
						preDrift += scroll;
						driftingTime += Math.abs(scroll) / 14;
						if (Math.abs(scroll / 3) > Math.abs(driftSpeed))
							driftSpeed = (short) (scroll / 3);
					} else {
						scrollHorizontally(deltaX);
					}
				}
				dragging = true;
			}
			lastx = x;
			lasty = y;
			pressTime = System.currentTimeMillis();
		} else */{
			try {
				if (!dragging) {
					//System.out.println("start dragging");
					if (poorScrolling()) {
						lastx = startx;
						lasty = y;
					} else {
						lastx = startx;
						lasty = starty;
					}
				}
				final int deltaX = lastx - x;
				final int deltaY = lasty - y;
				//System.out.println("deltaY " + deltaY);
				final int ndeltaX = Math.abs(deltaX);
				final int ndeltaY = Math.abs(deltaY);
				if (canScroll) {
					if (poorScrolling()) {
						scroll += (short) -deltaY;
						if (ndeltaY < ndeltaX - 2) scrollHorizontally(deltaX);
					} else {
						float f1 = 10F;
						float f2 = 3F;
						if(EmulatorDetector.emulatorType == EmulatorDetector.EM_J2L || DisplayUtils.canvas.isSensorModeJ2MELoader()) f1 = 7F;
						scroll += (short) -deltaY;
						//preDrift += -deltaY;
						preDrift = true;
						driftingTime += (Math.abs(deltaY) / f1) * 2400F;
						driftSpeed += -deltaY / f2;
						if (ndeltaY < ndeltaX - 2) scrollHorizontally(deltaX);
					}
				}
				if (DisplayUtils.canvas.isSensorModeOK()) {
					if (ndeltaY > 0 || ndeltaX > 0) {
						dragging = true;
					}
				} else {
					if (ndeltaY > 2 || ndeltaX > 2) {
						dragging = true;
					}
				}
				if (poorScrolling()) {
					dragging = true;
				}
				lastx = x;
				lasty = y;
				VikaTouch.needstoRedraw=true;
				this.serviceRepaints();
				VikaTouch.needstoRedraw=true;
				//if (DisplayUtils.canvas.isSensorModeJ2MELoader()) pressTime = System.currentTimeMillis();
			} catch (Throwable e) {

			}
		}
	}

	protected abstract void scrollHorizontally(int deltaX);

	public void press(int x, int y) {
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
		//System.out.println("press");
		lastx = x;
		lasty = y;
		pressTime = System.currentTimeMillis();
		driftingTime = 0;
		drift = false;
		driftSpeed = 0;
		preDrift = false;
		keysMode = false;
		startx = x;
		starty = y;
		endx = -1;
		endy = -1;
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
	}

	public static boolean poorScrolling() {
		return DisplayUtils.canvas.poorScrolling();
	}

	public void release(int x, int y) {
		try {
			VikaTouch.needstoRedraw=true;
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			releaseTime = System.currentTimeMillis();
			/*
			if (DisplayUtils.canvas.isSensorModeJ2MELoader()) {
				if (!poorScrolling() && releaseTime - 30 < pressTime) {
					if (preDrift != 0)
						drag(x, y);
					drift = preDrift;
				}
			} else*/
			if (!poorScrolling() && releaseTime - 131 < pressTime) {
				//if (preDrift)
				//	drag(x, y);
				drift = preDrift;
			} else {
				drift = false;
			}
			preDrift = false;
			keysMode = false;
			endx = x;
			endy = y;
			dragging = false;
			VikaTouch.needstoRedraw=true;
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
		} catch (Throwable e) {

		}
		//System.out.println("release drift: " + drift + ", driftingTime: " + driftingTime + " driftSpeed: " + driftSpeed + " dragDuration: " + (releaseTime - pressTime));
	}

	public void press(int key) {
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
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
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
	}

	public void repeat(int key) {
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
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
	}

	// private int scrolebd;

	protected void down() {
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
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
					currentItem = 0;
				}
				scrollToSelected();
				((PressableUIItem) uiItems.elementAt(currentItem)).setSelected(true);
			} catch (Throwable e) {

			}
		}
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
	}

	protected void up() {
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
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
			currentItem = (short) (itemsCount - 1);
		}
		scrollToSelected();
		try {
			((PressableUIItem) uiItems.elementAt(currentItem)).setSelected(true);
		} catch (Throwable e) {
			e.printStackTrace();

		}
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
	}

	public abstract void scrollToSelected();

	public abstract void selectCentered();

	public void select(int i) {
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
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
			VikaTouch.needstoRedraw=true;
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
		} catch (Throwable e) {

		}
	}

	protected abstract void keysScroll(int dir);

	protected final void update(Graphics g) {
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
		try {
			if (scrollTargetActive) {
				scroll = 0;
				if (Math.abs(scrolled - scrollTarget) < 4) {
					scrolled = scrollTarget;
					scrollTargetActive = false;
				} else {
					scrolled = MathUtils.lerp(scrolled, scrollTarget, 15, 100);
				}
				g.translate(0, scrolled);
				return;
			}
			try {
				if (!poorScrolling()) {
					long l = System.currentTimeMillis() - releaseTime;
					if (drift && /*Math.abs(drift) > 0 && */Math.abs(driftSpeed) > 1F && l < driftingTime) {
						scroll += driftSpeed;
						//drift += driftSpeed;
						driftSpeed *= 0.967f;
						//System.out.println("drifting " + drift + " | " + driftSpeed + " left: " + (l - driftingTime));
					} else {
						drift = false;
					}
				}
			} catch (ArithmeticException e) {
				e.printStackTrace();
			}
			int a = DisplayUtils.height;
			int b = itemsh + 110;
			if (b > a) {
				canScroll = true;
			} else {
				canScroll = false;
				if (scrolled < 0) {
					scrolled = 0;
				}
			}
			if (scroll != 0) {
				scrolled = scrolled + scroll;
				if (scrolled > 0) {
					scrolled = 0;
					drift = false;
				}
				if (scrolled < a - b && scrolled != 0) {
					scrolled = a - b;
					drift = false;
				}
				g.translate(0, scrolled);
				scroll = 0;
			} else {
				if (scrolled > 0) {
					scrolled = 0;
				}
				if (scrolled < a - b && scrolled != 0) {
					scrolled = a - b;
				}
				g.translate(0, scrolled);
			}
			if (!poorScrolling())
				scroll = 0;
		} catch (Throwable e) {

		}
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
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
						((MsgItem) uiItems.elementAt(i)).paint(VikaTouch.canvas.getG(), 0, scrolled);
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

