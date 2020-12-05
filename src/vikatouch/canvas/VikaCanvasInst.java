package vikatouch.canvas;

import java.io.InputStream;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ru.nnproject.vikaui.VikaCanvas;
import ru.nnproject.vikaui.popup.ConfirmBox;
import ru.nnproject.vikaui.popup.VikaNotice;
import ru.nnproject.vikaui.popup.VikaNotification;
import ru.nnproject.vikaui.screen.VikaScreen;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.GifDecoder;
import vikatouch.VikaTouch;
import vikatouch.screens.MainScreen;
import vikatouch.screens.temp.SplashScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.error.ErrorCodes;

public class VikaCanvasInst extends VikaCanvas {
	public VikaScreen currentScreen;
	public VikaScreen lastTempScreen;
	public boolean showCaptcha;
	private Image frame;
	private GifDecoder d;
	public VikaNotice currentAlert;
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
	public static final long uiSleepTimeout = 8000;

	public VikaCanvasInst() {
		super();
		this.setFullScreenMode(true);

		DisplayUtils.canvas = this;

		if(!(DisplayUtils.width < 240)) {
			try {
				InputStream in = this.getClass().getResourceAsStream("/loading.gif");
				d = new GifDecoder();
				int err = d.read(in);
				if (err == 0) {
					frame = d.getImage();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//slide = 0.0d;
		busyStr = "Busy...";
	}

	public void paint(Graphics g) {
		long rT = System.currentTimeMillis();
		try {
			this.updateScreen(g);
			rT = System.currentTimeMillis() - rT;
		} catch (Throwable e) {
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
				String memStr = String.valueOf(totalMem - freeMem) + "K/" + totalMem + "K, free:" + freeMem + "K";
				g.setGrayScale(255);
				g.fillRect(0, 0, g.getFont().stringWidth(memStr), h);
				g.setGrayScale(0);
				g.drawString(memStr, 0, 0, 0);

				if (timingsStr == null)
					timingsStr = "...";
				String infoStr = "RT:" + rT + " (" + timingsStr + ") gc:" + gcT;
				g.setGrayScale(255);
				g.fillRect(0, h, g.getFont().stringWidth(infoStr), h);
				g.setGrayScale(0);
				g.drawString(infoStr, 0, h, 0);
			}

			g.setColor(msgColor);
			g.fillRect(DisplayUtils.width - 8, 0, 4, 4);
			g.setColor(updColor);
			g.fillRect(DisplayUtils.width - 4, 0, 4, 4);
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
			timingsStr = "s:" + csrT + " a:" + carT + " hud:" + hudrT;
		}
	}

	private void drawLoading(Graphics g) {
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawString(busyStr, DisplayUtils.width / 2, DisplayUtils.height - 80, Graphics.TOP | Graphics.HCENTER);

		if (frame != null) {
			g.drawImage(frame, DisplayUtils.width / 2, DisplayUtils.height - 128, Graphics.TOP | Graphics.HCENTER);
		}
	}

	public void updategif() {
		int n = d.getFrameCount();
		for (int i = 0; i < n; i++) {
			frame = d.getFrame(i);
			repaint();
			try {
				Thread.sleep(40);
			} catch (Exception e) {
			}
		}
	}

	public void pointerPressed(int x, int y) {
		if (Settings.vibOnTouch)
			Display.getDisplay(VikaTouch.appInst).vibrate(50);
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
	}

	public void pointerReleased(int x, int y) {
		if (currentAlert != null) {
			currentAlert.release(x, y);
		} else if (showCaptcha) {
			VikaTouch.captchaScr.release(x, y);
		} else if (currentScreen != null) {
			currentScreen.release(x, y);
		}
	}

	public void pointerDragged(int x, int y) {
		if (currentAlert != null) {
			currentAlert.drag(x, y);
		} else if (showCaptcha) {
			VikaTouch.captchaScr.drag(x, y);
		} else if (currentScreen != null) {
			currentScreen.drag(x, y);
		}
	}

	public void keyPressed(int i) {
		if (currentAlert != null) {
			currentAlert.press(i);
		} else if (showCaptcha) {
			VikaTouch.captchaScr.press(i);
		} else if (currentScreen != null) {
			currentScreen.press(i);
		}
	}

	public void keyRepeated(int i) {
		if (currentAlert != null) {
			currentAlert.repeat(i);
		} else if (showCaptcha) {
			VikaTouch.captchaScr.repeat(i);
		} else if (currentScreen != null) {
			currentScreen.repeat(i);
		}
	}

	public void keyReleased(int i) {
		if (currentAlert != null) {
			currentAlert.release(i);
		} else if (showCaptcha) {
			VikaTouch.captchaScr.release(i);
		} else if (currentScreen != null) {
			currentScreen.release(i);
		}
	}

	public void paint() {
		if (!VikaTouch.appInst.isPaused || currentAlert != null) {
			repaint();
			// serviceRepaints();
		}
	}

	public void tick() {
		if (Display.getDisplay(VikaTouch.appInst).getCurrent() instanceof VikaCanvasInst) {
			if (VikaTouch.loading && !(DisplayUtils.width < 240)) {
				updategif();
				/*
				 * if(Settings.animateTransition) { oldScreen = null; slide = 0; }
				 */
			} else
				paint();
			/*
			 * if(Settings.animateTransition) { double sliden = Math.abs(slide); if(sliden >
			 * 0) { slide *= 0.78; if(sliden < 0.015) { oldScreen = null; slide = 0; } } }
			 */
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

}
