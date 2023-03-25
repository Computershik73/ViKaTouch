package vikatouch.canvas;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ru.nnproject.vikaui.VikaCanvas;
import ru.nnproject.vikaui.popup.AutoContextMenu;
import ru.nnproject.vikaui.popup.ContextMenu;
import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.screen.VikaScreen;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.items.VikaNotification;
import vikatouch.music.MusicPlayer;
import vikatouch.screens.CameraScreen;
import vikatouch.screens.ChatScreen;
import vikatouch.screens.DialogsScreen;
import vikatouch.screens.MainScreen;
import vikatouch.screens.temp.SplashScreen;
import vikatouch.settings.Settings;
import vikatouch.settings.SettingsScreen;
import vikatouch.utils.KeyCodeAdapter;
import vikatouch.utils.error.ErrorCodes;

/**
 * @author Shinovon
 * 
 */
public class VikaCanvasInst extends VikaCanvas implements Runnable {
	public VikaScreen currentScreen;
	public VikaScreen lastTempScreen;
	public boolean showCaptcha;
	public String currentInfo;
	public long currentInfoStartTime;
	public VikaNotification currentNof;
	// public double slide;
	public VikaScreen oldScreen;
	public static String busyStr;
	public Graphics gg;
	public static int netColor = 0;
	public static int updColor = 0;
	public static int msgColor = 0;

	private Object repaintLock = new Object();
	private Object repaintResLock = new Object();
	public int repaintTime;

	public static String timingsStr;

	public long lastInputTime = 0;
	private boolean visible;
	public static final long uiSleepTimeout = 8000;

	private boolean pressed;
	private int pressX;
	private int pressY;
	private int lastX;
	private int lastY;
	private long pressTime;
	private long releaseTime;
	private boolean draggedMuch;
	private boolean scrollPreSlide;
	private float scrollSlideMaxTime;
	private float scrollSlideSpeed;
	private boolean scrollSlide;
	private boolean controlBlock;
	// private boolean draggedScrollbar;
	private int flushTime;

	public VikaCanvasInst() {
		super();
		this.setFullScreenMode(true);
		gg = this.getGraphics();
		DisplayUtils.canvas = this;
		// slide = 0.0d;
		busyStr = "Busy...";

	}

	public Graphics getG() {
		return this.getGraphics();
	}

	protected void showNotify() {
		this.visible = true;
	}

	protected void hideNotify() {
		this.visible = false;
	}

	public boolean isVisible() {
		return visible;
	}

	/*
	 * public void draw() { //if (VikaTouch.needstoRedraw) { if(dontBuffer()) {
	 * repaint(); return; } Graphics g = getGraphics(); if(buffer != null) { g =
	 * buffer; } draw0(g); if(buffer == null) { VikaTouch.needstoRedraw=true;
	 * flushGraphics(); VikaTouch.needstoRedraw=true; } else {
	 * VikaTouch.needstoRedraw=true; repaint(); VikaTouch.needstoRedraw=true; }
	 * 
	 * VikaTouch.needstoRedraw=false; if ((vikatouch.music.MusicPlayer.inst!=null)
	 * && (currentScreen != null) && (currentScreen instanceof MusicPlayer)) { if
	 * (vikatouch.music.MusicPlayer.inst.isPlaying) { VikaTouch.needstoRedraw=true;
	 * } } } if ((currentScreen != null) && ((currentAlert instanceof
	 * AutoContextMenu) || (currentAlert instanceof ContextMenu) || (currentScreen
	 * instanceof SettingsScreen))) { VikaTouch.needstoRedraw=true; }
	 * 
	 * if (ChatScreen.isRecRunning) { VikaTouch.needstoRedraw=true; }
	 * 
	 * }
	 * 
	 */
	public Thread repaintThread = new Thread(this);

	public void updateScreen() {
		Graphics g = getGraphics();
		Graphics g2 = g;
		long l = System.currentTimeMillis();
		Image img = null;
		if (Settings.doubleBufferization) {
			img = Image.createImage(DisplayUtils.width, DisplayUtils.height);
			g = img.getGraphics();
		}
		if (currentScreen != null) {
			if (!ScrollableCanvas.keysMode && VikaTouch.scrolling && !showCaptcha && currentAlert == null/* && !draggedScrollbar */) {
				if (!scrollPreSlide && (releaseTime - pressTime) > 0) {
					if (scrollSlide && Math.abs(scrollSlideSpeed) > 0.8F
							&& (System.currentTimeMillis() - releaseTime) < scrollSlideMaxTime
							&& currentScreen.scroll((int) scrollSlideSpeed)) {
						float f = 0.967f;
						// if(ui.avgFps > 1) {
						// f = f/30f*ui.avgFps;
						// }
						scrollSlideSpeed *= f;
					} else {
						scrollSlideSpeed = 0;
						VikaTouch.scrolling = false;
					}
				}
			}
			try {
				_updateScreen(g);
			} catch (Error e) {
			}
		}
		if (Settings.debugInfo) {
			int h = g.getFont().getHeight();

			int freeMem = (int) (Runtime.getRuntime().freeMemory() / 1024);
			int totalMem = (int) (Runtime.getRuntime().totalMemory() / 1024);
			String memStr = String.valueOf(totalMem - freeMem) + "K/" + totalMem + "K," + freeMem + "K";
			g.setGrayScale(255);
			g.fillRect(0, 0, g.getFont().stringWidth(memStr), h);
			g.setGrayScale(0);
			g.drawString(memStr, 0, 0, 0);

			if (timingsStr == null)
				timingsStr = "...";
			long rT = System.currentTimeMillis()-l;
			int fps = (int) (1000/(rT+flushTime));
			if(!VikaTouch.scrolling) {
				if(fps > 1) {
					fps = 1;
				}
			} else if(fps > 33) {
				fps = 33;
			}
			String infoStr = "FPS:" + fps + "RT:" + rT+"FT:"+flushTime;
			g.setGrayScale(255);
			g.fillRect(0, h, g.getFont().stringWidth(infoStr), h);
			g.setGrayScale(0);
			g.drawString(infoStr, 0, h, 0);
		}
		if (Settings.doubleBufferization) {
			g2.drawImage(img, 0, 0, 0);
		}
		l = System.currentTimeMillis();
		flushGraphics();
		flushTime = (int) (System.currentTimeMillis() - l);
	}

	public void run() {
		boolean wasScrolling = false;
		try {
			while (true) {
				while (VikaTouch.display.getCurrent() != this) {
					Thread.sleep(100);
				}
				if (!VikaTouch.scrolling) {
					if (wasScrolling) {
						_repaint();
						wasScrolling = false;
					}
					synchronized (repaintLock) {
						repaintLock.wait(1000);
					}
				}
				_repaint();
				if (VikaTouch.scrolling) {
					wasScrolling = true;
				} else {
					synchronized (repaintResLock) {
						repaintResLock.notify();
					}
				}
				waitRepaint();
			}
		} catch (InterruptedException e) {
		}
	}

	private void waitRepaint() throws InterruptedException {
		int i = 30;
		i -= repaintTime;
		if (i > 0)
			Thread.sleep(i);
	}

	private void _repaint() {
		long time = System.currentTimeMillis();
		this.updateScreen();
		repaintTime = (int) (System.currentTimeMillis() - time);
	}

	public void repaint(boolean wait) {
		if (VikaTouch.display.getCurrent() != this || VikaTouch.scrolling)
			return;
		synchronized (repaintLock) {
			repaintLock.notify();
		}
		if (wait) {
			try {
				synchronized (repaintResLock) {
					repaintResLock.wait(1000);
				}
			} catch (Exception e) {
			}
		}
	}

	public int getFPSLimit() {

		return Settings.fpsLimit;
	}

	// unused
	private void draw0(Graphics g) {
		long rT = System.currentTimeMillis();
		try {
			// this.updateScreen(g);
			rT = System.currentTimeMillis() - rT;
		} catch (Throwable e) {
			e.printStackTrace();
			VikaTouch.sendLog("Paint failed. " + e.toString());
			VikaTouch.error(e, ErrorCodes.VIKACANVASPAINT);
		}

		long gcT = System.currentTimeMillis();
		if (Runtime.getRuntime().freeMemory() < 1024 * 128)
			System.gc();
		gcT = System.currentTimeMillis() - gcT;

		if (VikaTouch.isdownloading == 1) {
			// if ((VikaTouch.integerUserId==3225000) ||
			// (VikaTouch.integerUserId==310674350)) {
			g.setColor(255, 120, 120);
			g.fillRect(DisplayUtils.width - 10, 0, 10, 10);
			VikaTouch.needstoRedraw = true;
			/*
			 * } else { g.setColor(255,216,0); g.fillRect(0, 0, 5, 5);
			 * VikaTouch.needstoRedraw=true; }
			 */
		} else {
			if (VikaTouch.isdownloading == 2) {
				g.setColor(255, 0, 0);
				g.fillRect(0, 0, 10, 10);
				VikaTouch.needstoRedraw = true;
			} else {
				if (VikaTouch.isdownloading == 0) {
					VikaTouch.needstoRedraw = true;
				}

			}
		}

		if (VikaTouch.istimeout == true) {
			g.setColor(255, 0, 255);
			if (VikaTouch.integerUserId != 3225000) {
				g.fillRect(getWidth() / 2 - 2, 0, 5, 5);
			} else {
				g.fillRect(getWidth() / 2 - 2, 0, 5, 105);
			}
			VikaTouch.needstoRedraw = true;
		}
		try {
			/*
			 * if (VikaTouch.isscrolling==true) { g.setColor(0,0,255);
			 * g.fillRect((getWidth()/2+3), 0, 5, 5); VikaTouch.needstoRedraw=true; }
			 */
		} catch (Throwable eeee) {
		}

		{

			if (Settings.debugInfo) {
				int h = g.getFont().getHeight();

				int freeMem = (int) (Runtime.getRuntime().freeMemory() / 1024);
				int totalMem = (int) (Runtime.getRuntime().totalMemory() / 1024);
				String memStr = String.valueOf(totalMem - freeMem) + "K/" + totalMem + "K," + freeMem + "K";
				g.setGrayScale(255);
				g.fillRect(0, 0, g.getFont().stringWidth(memStr), h);
				g.setGrayScale(0);
				g.drawString(memStr, 0, 0, 0);

				if (timingsStr == null)
					timingsStr = "...";
				String infoStr = "FPS:" + this.realFps + "(" + this.fps + ")RT:" + rT + "(" + timingsStr + ")gc:" + gcT;
				g.setGrayScale(255);
				g.fillRect(0, h, g.getFont().stringWidth(infoStr), h);
				g.setGrayScale(0);
				g.drawString(infoStr, 0, h, 0);
			}
		}
	}

	public void _updateScreen(Graphics g) {
		// if (VikaTouch.needstoRedraw) {
		DisplayUtils.checkdisplay();
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);
		/*
		 * try { if (Settings.animateTransition && oldScreen != null) { int slideI =
		 * (int) (slide * (double) DisplayUtils.width); if (Settings.slideAnim) { if
		 * (slideI > 0) g.translate(slideI - DisplayUtils.width, 0); else
		 * g.translate(slideI + DisplayUtils.width, 0); if (oldScreen != null &&
		 * !VikaTouch.crashed) { oldScreen.draw(g); } if (slideI > 0)
		 * g.translate(DisplayUtils.width, 0); else g.translate(-DisplayUtils.width, 0);
		 * } else { if (oldScreen != null && !VikaTouch.crashed) { oldScreen.draw(g); }
		 * g.translate(slideI, 0); } }
		 * 
		 * } catch (Exception e) { VikaTouch.error(e, -2); e.printStackTrace(); }
		 */
		try {

			ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
			g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);

			if (currentScreen != null) {
				currentScreen.draw(g);
			}

		} catch (Exception e) {
			e.printStackTrace();
			VikaTouch.error(e, ErrorCodes.VIKACANVASPAINT);
		}

		if (showCaptcha) {
			VikaTouch.captchaScr.draw(g);
		}

		try {
			if (currentScreen != null && currentScreen instanceof MainScreen) {
				((MainScreen) currentScreen).drawHUD(g);
			}
		} catch (Exception e) {

		}

		try {
			if (currentNof != null) {
				currentNof.draw(g);
			}
		} catch (Throwable e) {

		}

		try {
			if (currentAlert != null) {
				currentAlert.draw(g);
			}
		} catch (Throwable e) {

		}
		;

		if (VikaTouch.loading && !(currentScreen instanceof SplashScreen)) {
			drawLoading(g);
		}
	}

	private void drawLoading(Graphics g) {
		// ColorUtils.setcolor(g, ColorUtils.TEXT);
		// g.drawString(busyStr, DisplayUtils.width / 2, DisplayUtils.height - 80,
		// Graphics.TOP | Graphics.HCENTER);
	}

	public void pointerPressed(int x, int y) {
		if (Settings.vibOnTouch) {
			try {
				Display.getDisplay(VikaTouch.appInst).vibrate(50);
			} catch (Exception e) {

			}
		}
		VikaTouch.supportsTouch = true;
		pressed = true;
		lastX = pressX = x;
		lastY = pressY = y;
		pressTime = System.currentTimeMillis();
		draggedMuch = false;
		scrollSlide = false;
		scrollPreSlide = false;
		if (currentScreen != null && currentScreen instanceof CameraScreen) {
			((CameraScreen) currentScreen).press(x, y);
		} else if (currentScreen != null && currentScreen instanceof DialogsScreen) {
			((DialogsScreen) currentScreen).press(x, y);
		}
		VikaTouch.needRepaint();
	}

	/*
	 * private void _press(int x, int y) { VikaTouch.supportsTouch=true; try { try {
	 * if (Settings.vibOnTouch) Display.getDisplay(VikaTouch.appInst).vibrate(50); }
	 * catch (Exception e) {
	 * 
	 * } if (currentAlert != null) { currentAlert.press(x, y); } else if (currentNof
	 * != null && currentNof.active && y < VikaNotification.nofH && x >
	 * VikaNotification.nofX && x < DisplayUtils.width - VikaNotification.nofX) {
	 * currentNof.open(); } else if (showCaptcha) { VikaTouch.captchaScr.press(x,
	 * y); } else if (currentScreen != null) { currentScreen.press(x, y); } } catch
	 * (Exception e) { VikaTouch.error(e, ErrorCodes.POINTERPRESSED, false); } }
	 */
	public void pointerReleased(int x, int y) {
		int time = (int) ((releaseTime = System.currentTimeMillis()) - pressTime);
		int dx = Math.abs(x - pressX);
		int dy = y - pressY;
		int ady = Math.abs(dy);
		if (pressed) {
			/*
			 * if(s != null && s.hasScrollBar()) { if(x > width - (AppUI.getScrollBarWidth()
			 * + 5)) { s.setScrollBarY(y); draggedScrollbar = true; } }
			 */
			if (currentScreen != null && currentScreen instanceof CameraScreen) {
				((CameraScreen) currentScreen).release(x, y);
			} else if (currentScreen != null && currentScreen instanceof DialogsScreen) {
				((DialogsScreen) currentScreen).release(x, y);
			}
			// if(!draggedScrollbar) {
			if (draggedMuch) {
				if (time < 200) {
					scrollSlide = scrollPreSlide;
					VikaTouch.scrolling = true;
				} else {
					scrollSlide = false;
				}
			} else {
				if (dx <= 6 && ady <= 6) {
					_tap(x, y, time);
				} else if (time < 200 && dx < 12) {
					if (currentScreen != null && !showCaptcha && currentAlert == null) {
						currentScreen.scroll(-dy);
					}
				}
			}
			/*
			 * } else { ui.scrolling = false; }
			 */
			scrollPreSlide = false;
			pressed = false;
		}
		VikaTouch.needRepaint();
	}

	public void _tap(int x, int y, int time) {
		try {
			if (currentAlert != null) {
				currentAlert.tap(x, y, time);
			} else if (currentNof != null && currentNof.active && y < VikaNotification.nofH && x > VikaNotification.nofX
					&& x < DisplayUtils.width - VikaNotification.nofX) {
				currentNof.open();
			} else if (showCaptcha) {
				VikaTouch.captchaScr.tap(x, y, time);
			} else if (currentScreen != null) {
				currentScreen.tap(x, y, time);
			}
		} catch (Exception e) {
			e.printStackTrace();
			VikaTouch.error(e, ErrorCodes.POINTERRELEASED, false);
		}
	}

	public void pointerDragged(int x, int y) {
		if (currentScreen != null && !showCaptcha && currentAlert == null) {
			/*
			 * if(s.hasScrollBar()) { if(x > width - (AppUI.getScrollBarWidth() + 5)) {
			 * s.setScrollBarY(y); draggedScrollbar = true; lastX = x; lastY = y;
			 * needRepaint(); ui.scrolling = true; return; } }
			 */
			// dragTime = System.currentTimeMillis();
			final int sdX = Math.abs(pressX - x);
			final int sdY = Math.abs(pressY - y);
			final int dX = lastX - x;
			final int dY = lastY - y;
			final int adX = Math.abs(dX);
			final int adY = Math.abs(dY);
			// if(draggedMuch) {
			if (adY > 0 && adX < 16/* && !draggedScrollbar */) {
				float f1 = 8F;
				float f2 = 2.5F;
				scrollPreSlide = true;
				scrollSlideMaxTime += (Math.abs(dY) / f1) * 2400F;
				float m = -dY / f2;
				// разные направления
				if (scrollSlideSpeed > 0 && m < 0 || scrollSlideSpeed < 0 && m > 0)
					scrollSlideSpeed = 0;
				if (Math.abs(scrollSlideSpeed) > 60) {
					scrollSlideSpeed *= 0.95;
					m *= 0.8;
				}
				scrollSlideSpeed += m;
				currentScreen.scroll(-dY);
				// preDrift += -deltaY;
				// if (adY < adX - 2) scrollHorizontally(dX);
			}
			// }
			if (sdY > 1 || sdX > 1) {
				draggedMuch = true;
				if (!VikaTouch.scrolling) {
					VikaTouch.needRepaint();
					VikaTouch.scrolling = true;
				}
			}
		}
		lastX = x;
		lastY = y;
	}

	public void keyPressed(int i) {
		i = KeyCodeAdapter.getInstance().adoptKeyCode(i);
		if (currentAlert != null) {
			currentAlert.press(i);
		} else if (showCaptcha) {
			VikaTouch.captchaScr.press(i);
		} else if (currentScreen != null) {
			currentScreen.press(i);
		}
	}

	public void keyRepeated(int i) {
		i = KeyCodeAdapter.getInstance().adoptKeyCode(i);
		if (currentAlert != null) {
			currentAlert.repeat(i);
		} else if (showCaptcha) {
			VikaTouch.captchaScr.repeat(i);
		} else if (currentScreen != null) {
			currentScreen.repeat(i);
		}
	}

	public void keyReleased(int i) {
		i = KeyCodeAdapter.getInstance().adoptKeyCode(i);
		if (currentAlert != null) {
			currentAlert.release(i);
		} else if (showCaptcha) {
			VikaTouch.captchaScr.release(i);
		} else if (currentScreen != null) {
			currentScreen.release(i);
		}
	}

	public void callCommand(int i, VikaScreen screen) {
		VikaTouch.inst.cmdsInst.command(i, screen);
	}

	public boolean isSensorModeOK() {
		return Settings.sensorMode == Settings.SENSOR_OK;
	}

	public boolean isSensorModeJ2MELoader() {
		return Settings.sensorMode == Settings.SENSOR_J2MELOADER;
	}

	public boolean poorScrolling() {
		return Settings.sensorMode == Settings.SENSOR_KEMULATOR;
	}

	protected boolean drawMaxPriority() {
		return Settings.drawMaxPriority;
	}

	public boolean isNight() {
		return Settings.nightTheme;
	}

}
