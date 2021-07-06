package vikatouch.canvas;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ru.nnproject.vikaui.VikaCanvas;
import ru.nnproject.vikaui.screen.VikaScreen;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.items.VikaNotification;
import vikatouch.screens.MainScreen;
import vikatouch.screens.temp.SplashScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.KeyCodeAdapter;
import vikatouch.utils.error.ErrorCodes;

/**
 * @author Shinovon
 * 
 */
public class VikaCanvasInst extends VikaCanvas {
	public VikaScreen currentScreen;
	public VikaScreen lastTempScreen;
	public boolean showCaptcha;
	public String currentInfo;
	public long currentInfoStartTime;
	public VikaNotification currentNof;
	//public double slide;
	public VikaScreen oldScreen;
	public static String busyStr;

	public static int netColor = 0;
	public static int updColor = 0;
	public static int msgColor = 0;

	public static String timingsStr;

	public long lastInputTime = 0;
	private Image image;
	private Graphics buffer;
	private boolean visible;
	public static final long uiSleepTimeout = 8000;

	public VikaCanvasInst() {
		super();
		this.setFullScreenMode(true);

		DisplayUtils.canvas = this;
		//slide = 0.0d;
		busyStr = "Busy...";
		if(!dontBuffer()) {
			if (!isDoubleBuffered()) {
				System.out.println("db!");
				image = Image.createImage(getWidth(), getHeight());
			    buffer = image.getGraphics();
			}
		}
		 
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

	public void draw() {
		if(dontBuffer()) {
			repaint();
			return;
		}
		Graphics g = getGraphics();
		if(buffer != null) {
			g = buffer;
		}
		draw0(g);
		if(buffer == null) {
			flushGraphics();
		} else {
			repaint();
		}
	}
	
	private boolean dontBuffer() {
		return !Settings.doubleBufferization;
	}

	public int getFPSLimit() {
		
		return Settings.fpsLimit;
	}
	
	private void draw0(Graphics g) {
		long rT = System.currentTimeMillis();
		try {
			this.updateScreen(g);
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
				String infoStr = "FPS:"+this.realFps+"(" + this.fps + ")RT:" + rT + "(" + timingsStr + ")gc:" + gcT;
				g.setGrayScale(255);
				g.fillRect(0, h, g.getFont().stringWidth(infoStr), h);
				g.setGrayScale(0);
				g.drawString(infoStr, 0, h, 0);
			}
		}
	}

	public void paint(Graphics g) {
		if (VikaTouch.needstoRedraw) {
		if(dontBuffer()) {
			draw0(g);
			return;
		}
		if (!isDoubleBuffered() && buffer != null) {
			g.drawImage(image, 0, 0, 0);
		}
		super.paint(g);
		VikaTouch.needstoRedraw=false;
		}
	}

	public void updateScreen(Graphics g) {
		DisplayUtils.checkdisplay();
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);
		/*
		try {
			if (Settings.animateTransition && oldScreen != null) {
				int slideI = (int) (slide * (double) DisplayUtils.width);
				if (Settings.slideAnim) {
					if (slideI > 0)
						g.translate(slideI - DisplayUtils.width, 0);
					else
						g.translate(slideI + DisplayUtils.width, 0);
					if (oldScreen != null && !VikaTouch.crashed) {
						oldScreen.draw(g);
					}
					if (slideI > 0)
						g.translate(DisplayUtils.width, 0);
					else
						g.translate(-DisplayUtils.width, 0);
				} else {
					if (oldScreen != null && !VikaTouch.crashed) {
						oldScreen.draw(g);
					}
					g.translate(slideI, 0);
				}
			}

		} catch (Exception e) {
			VikaTouch.error(e, -2);
			e.printStackTrace();
		}
		*/
		long csrT = System.currentTimeMillis();
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

		//g.translate(-g.getTranslateX(), 0);

		long hudrT = System.currentTimeMillis();
		csrT = hudrT - csrT;

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
		} catch (Exception e) {

		}

		long carT = System.currentTimeMillis();
		hudrT = carT - hudrT;

		try {
			if (currentAlert != null) {
				try {
					if (!VikaTouch.isS40())
						vengine.GraphicUtils.darkScreen(g, DisplayUtils.width, DisplayUtils.height, 0, 0, 0, 128);
				} catch (Exception e) {
				}
				currentAlert.draw(g);
			}
		} catch (Exception e) {

		}

		carT = System.currentTimeMillis() - carT;

		if (VikaTouch.loading && !(currentScreen instanceof SplashScreen)) {
			drawLoading(g);
		} /*
			 * if(Settings.debugInfo) { if(debugString != null) { g.setColor(0xffff00);
			 * g.drawString(debugString, 65, 2, 0); } }
			 */
		if (Settings.debugInfo) {
			timingsStr = "s:" + csrT + ",a:" + carT + ",hud:" + hudrT;
		}
	}

	private void drawLoading(Graphics g) {
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawString(busyStr, DisplayUtils.width / 2, DisplayUtils.height - 80, Graphics.TOP | Graphics.HCENTER);
	}

	public void pointerPressed(int x, int y) {
		VikaTouch.supportsTouch=true;
		try {
			try {
				if (Settings.vibOnTouch)
					Display.getDisplay(VikaTouch.appInst).vibrate(50);
				} catch (Exception e) {
					
				}
			if (currentAlert != null) {
				currentAlert.press(x, y);
			} else if (currentNof != null && currentNof.active && y < VikaNotification.nofH && x > VikaNotification.nofX
					&& x < DisplayUtils.width - VikaNotification.nofX) {
				currentNof.open();
			} else if (showCaptcha) {
				VikaTouch.captchaScr.press(x, y);
			} else if (currentScreen != null) {
				currentScreen.press(x, y);
			}
		} catch (Exception e) {
			VikaTouch.error(e, ErrorCodes.POINTERPRESSED, false);
		}
	}

	public void pointerReleased(int x, int y) {
		VikaTouch.supportsTouch=true;
		try {
			if (currentAlert != null) {
				currentAlert.release(x, y);
			} else if (showCaptcha) {
				VikaTouch.captchaScr.release(x, y);
			} else if (currentScreen != null) {
				currentScreen.release(x, y);
			}
		} catch (Exception e) {
			VikaTouch.error(e, ErrorCodes.POINTERRELEASED, false);
		}
	}

	public void pointerDragged(int x, int y) {
		VikaTouch.supportsTouch=true;
		try {
			if (currentAlert != null) {
				currentAlert.drag(x, y);
			} else if (showCaptcha) {
				VikaTouch.captchaScr.drag(x, y);
			} else if (currentScreen != null) {
				currentScreen.drag(x, y);
			}
		} catch (Exception e) {
			VikaTouch.error(e, ErrorCodes.POINTERDRAGGED, false);
		}
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
	public void tick() {
		if (Display.getDisplay(VikaTouch.appInst).getCurrent() instanceof VikaCanvasInst) {
			/*
			 * if(Settings.animateTransition) { double sliden = Math.abs(slide); if(sliden >
			 * 0) { slide *= 0.78; if(sliden < 0.015) { oldScreen = null; slide = 0; } } }
			 */
			draw();
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
