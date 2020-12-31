package ru.nnproject.vikaui.screen;

import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.MathUtils;

public abstract class ScrollableCanvas extends VikaScreen {

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

	/*
	 * Целевая координата, к которой будет лерпаться прокрутка.
	 */
	public int scrollTarget;
	/*
	 * Если false, лерпаться не будет.
	 */
	public boolean scrollTargetActive;

	public ScrollableCanvas() {
		super();
		repaint();
	}

	public abstract void draw(Graphics g);

	public final void drag(int x, int y) {
		keysMode = false;
		if (DisplayUtils.canvas.isSensorModeJ2MELoader()) {
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
						scrollPrev += scroll;
						scrollingTimer += Math.abs(scroll) / 14;
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
			timer = 0;
		} else {
		try {
			if (!dragging) {
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
			final int ndeltaX = Math.abs(deltaX);
			final int ndeltaY = Math.abs(deltaY);
			if (canScroll) {
				if (ndeltaY > ndeltaX) {
					scroll = (short) ((double) -deltaY * scrollSpeed);
					scrollPrev += scroll;
					scrollingTimer += Math.abs(scroll) / 14;
					if (Math.abs(scroll / 3) > Math.abs(driftSpeed))
						driftSpeed = (short) (scroll / 3);
					if (poorScrolling())
						scroll *= 16;
				} else {
					scrollHorizontally(deltaX);
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
			timer = 0;
		} catch (Throwable e) {
			
		}
		}
	}

	protected abstract void scrollHorizontally(int deltaX);

	public void press(int x, int y) {
		timer = 0;
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

	public static boolean poorScrolling() {
		return DisplayUtils.canvas.poorScrolling();
	}

	public void release(int x, int y) {
		//dragged = false;
		try {
			if (!poorScrolling() && timer < 7) {
				if (scrollPrev != 0)
					drag(x, y);
				drift = scrollPrev;
			}
			scrollPrev = 0;
			keysMode = false;
			endx = x;
			endy = y;
			dragging = false;
			repaint();
		} catch (Throwable e) {
			
		}
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
				uiItems[currentItem].keyPressed(key);
			}
			repaint();
		} catch (Throwable e) {
			// VikaTouch.sendLog("press "+ e.getMessage());
		}
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
			repaint();
		} catch (Throwable e) {
			
		}
	}

	//private int scrolebd;

	protected void down() {
		if (scrollWithKeys) {
			keysScroll(-1);
			return;
		}
		// TODO: паблик бета
		/*
		 * if(uiItems[currentItem].getDrawHeight() > vmeshautsa) { if(scrolebd == 0) {
		 * scrolebd = uiItems[currentItem].getDrawHeight(); } if(scrolebd == -1) {
		 * scrolebd = 0; try { uiItems[currentItem].setSelected(false); } catch
		 * (Exception e) {
		 * 
		 * } currentItem++; if(currentItem >= itemsCount) { } else scrolled -=
		 * uiItems[currentItem].getDrawHeight(); uiItems[currentItem].setSelected(true);
		 * }
		 * 
		 * 
		 * int x = 20; if(scrolebd < x) { scrolled -= scrolebd; scrolebd = -1; } else {
		 * scrolled -= x; scrolebd -= x; } } else
		 */
		{
			try {
				uiItems[currentItem].setSelected(false);

				currentItem++;
				if (currentItem >= itemsCount) {
					currentItem = 0;
				}
				scrollToSelected();
				uiItems[currentItem].setSelected(true);
			} catch (Throwable e) {
				
			}
		}
	}

	protected void up() {
		if (scrollWithKeys) {
			keysScroll(+1);
			return;
		}
		try {
			uiItems[currentItem].setSelected(false);
		} catch (Throwable e) {

			e.printStackTrace();
			

		}
		currentItem--;
		if (currentItem < 0) {
			currentItem = (short) (itemsCount - 1);
		}
		scrollToSelected();
		try {
			uiItems[currentItem].setSelected(true);
		} catch (Throwable e) {
			e.printStackTrace();
			
		}
	}

	public abstract void scrollToSelected();

	public abstract void selectCentered();

	public void select(int i) {
		try {
			System.out.println("select " + i);
			if (i < 0)
				i = 0;
			if (i >= uiItems.length)
				i = uiItems.length - 1;
			try {
				uiItems[currentItem].setSelected(false);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			try {
				
				uiItems[i].setSelected(true);
				currentItem = i;
			} catch (RuntimeException e) {
				System.out.println(i);
				e.printStackTrace();
			}
		} catch (Throwable e) {
			
		}
	}

	protected abstract void keysScroll(int dir);

	protected final void update(Graphics g) {
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
					if (timer < 3200)
						timer++;
					if (scrollingTimer > 0)
						scrollingTimer--;

					if (drift != 0 && driftSpeed != 0 && scrollingTimer > 5) {
						scroll += driftSpeed;
						drift -= driftSpeed;
						driftSpeed *= 0.975;
					}
				}
			} catch (ArithmeticException e) {

			}
			if (itemsh > vmeshautsa) {
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
				}
				if (scrolled < vmeshautsa - itemsh && scrolled != 0) {
					scrolled = vmeshautsa - itemsh;
				}
				g.translate(0, scrolled);
				scroll = 0;
			} else {
				if (scrolled > 0) {
					scrolled = 0;
				}
				if (scrolled < vmeshautsa - itemsh && scrolled != 0) {
					scrolled = vmeshautsa - itemsh;
				}
				g.translate(0, scrolled);
			}
			if (!poorScrolling())
				scroll = 0;
		} catch (Throwable e) {
			
		}
	}

	protected void callRefresh() {

	}

	public int getItemY(int n) {
		try {
			if (uiItems == null)
				return 1;
			int y = 0;
			for (int i = 0; (i < uiItems.length && i < n); i++) {
				if (uiItems[i] != null)
					y += uiItems[i].getDrawHeight(); // не УМНОЖИТЬ! айтемы могут быть разной высоты.
			}
			return y;
		} catch (Throwable e) {
			
		}
		return 0;
	}

}
