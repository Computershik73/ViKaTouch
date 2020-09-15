package vikatouch.canvas;

import java.io.InputStream;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ru.nnproject.vikaui.VikaCanvas;
import ru.nnproject.vikaui.popup.ConfirmBox;
import ru.nnproject.vikaui.popup.VikaNotice;
import ru.nnproject.vikaui.screen.VikaScreen;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.GifDecoder;
import vikatouch.VikaTouch;
import vikatouch.screens.MainScreen;
import vikatouch.screens.temp.SplashScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.error.ErrorCodes;

public class VikaCanvasInst
	extends VikaCanvas
{
	public VikaScreen currentScreen;
	public VikaScreen lastTempScreen;
	public boolean showCaptcha;
	private Image frame;
	private GifDecoder d;
	public VikaNotice currentAlert;
	public double slide;
	public VikaScreen oldScreen;
	public static String busyStr;

	public VikaCanvasInst()
	{
		super();
		this.setFullScreenMode(true);

		DisplayUtils.canvas = this;
		
		try
		{
			InputStream in = this.getClass().getResourceAsStream("/loading.gif");
			d = new GifDecoder();
			int err = d.read(in);
	        if (err == 0)
	        {
	           frame = d.getImage();
	        }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		slide = 0.0d;
		busyStr = "Busy...";
	}
	
	public void paint(Graphics g)
	{
		long rT = System.currentTimeMillis();
		try
		{
			this.updateScreen(g);
			rT = System.currentTimeMillis() - rT;
		}
		catch (Throwable e)
		{
			VikaTouch.sendLog("Paint failed. "+e.toString());
			VikaTouch.error(e, ErrorCodes.VIKACANVASPAINT);
		}
		long gcT = System.currentTimeMillis();
		if(Runtime.getRuntime().freeMemory()<1024*128) System.gc();
		gcT = System.currentTimeMillis() - gcT;
		g.setGrayScale(0);
		g.fillRect(0, 30, 140, 30);
		g.setGrayScale(255);
		g.drawString("RT: "+rT+" GCT: "+gcT, 0, 30, 0);
	}
	
	public void updateScreen(Graphics g)
	{
		DisplayUtils.checkdisplay();
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);
		long lsrT = System.currentTimeMillis();
		try
		{
			
			if(Settings.animateTransition && oldScreen != null)
			{
				int slideI = (int)(slide * (double)DisplayUtils.width);
				if(Settings.slideAnim)
				{
					if(slideI > 0)
						g.translate(slideI-DisplayUtils.width, 0);
					else
						g.translate(slideI+DisplayUtils.width, 0);
					if(oldScreen != null && !VikaTouch.crashed)
					{
						oldScreen.draw(g);
					}
					if(slideI > 0)
						g.translate(DisplayUtils.width, 0);
					else
						g.translate(-DisplayUtils.width, 0);
				}
				else
				{
					if(oldScreen != null && !VikaTouch.crashed)
					{
						oldScreen.draw(g);
					}
					g.translate(slideI, 0);
				}
			}
			
		}
		catch (Exception e)
		{
			VikaTouch.error(e, -2);
			e.printStackTrace();
		}
		long csrT = System.currentTimeMillis();
		lsrT = csrT - lsrT;
		try
		{
			
	
			ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
			g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);
			
			if(currentScreen != null)
			{
				currentScreen.draw(g);
			}
			
		}
		catch (Exception e)
		{
			VikaTouch.error(e, ErrorCodes.VIKACANVASPAINT);
		}
		
		if(showCaptcha)
		{
			VikaTouch.captchaScr.draw(g);
		}
		
		g.translate(-g.getTranslateX(), 0);
		
		long hudrT = System.currentTimeMillis();
		csrT = hudrT - csrT;
		
		try
		{
			if(currentScreen != null && currentScreen instanceof MainScreen)
			{
				((MainScreen) currentScreen).drawHUD(g);
			}
		}
		catch (Exception e)
		{
			
		}
		
		hudrT = System.currentTimeMillis() - hudrT;
		
		try
		{
			if(currentAlert != null)
			{
				vengine.GraphicsUtil.darkScreen(g, DisplayUtils.width, DisplayUtils.height, 0, 0, 0, 128);
				currentAlert.draw(g);
			}
		}
		catch (Exception e)
		{
			
		}
		
		
		
		if(VikaTouch.loading && !(currentScreen instanceof SplashScreen))
		{
			drawLoading(g);
		}/*
		if(Settings.debugInfo)
		{
			if(debugString != null)
			{
				g.setColor(0xffff00);
				g.drawString(debugString, 65, 2, 0);
			}
		}*/
		
		{
			g.setGrayScale(0);
			g.fillRect(0, 60, 200, 30);
			g.setGrayScale(255);
			g.drawString("csrT:"+csrT+" lsrT:"+lsrT+" hud:"+hudrT, 0, 60, 0);
		}
		
		{
			int freeMem = (int) (Runtime.getRuntime().freeMemory()/1024);
			int totalMem = (int) (Runtime.getRuntime().totalMemory()/1024);
			String infoStr = "Mem: "+(totalMem - freeMem)+"K/"+totalMem+"K , free: "+freeMem+"K";
			g.setGrayScale(255);
			g.fillRect(0, 0, g.getFont().stringWidth(infoStr), 30);
			g.setGrayScale(0);
			g.drawString(infoStr, 0, 0, 0);
		}
	}
	
	private void drawLoading(Graphics g)
	{
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawString(busyStr, DisplayUtils.width / 2, DisplayUtils.height - 80, Graphics.TOP | Graphics.HCENTER);
		
		if(frame != null)
		{
			g.drawImage(frame, DisplayUtils.width / 2, DisplayUtils.height - 128, Graphics.TOP | Graphics.HCENTER);
		}
	}
	
	public void updategif()
	{
        int n = d.getFrameCount();
        for (int i = 0; i < n; i++)
        {
            frame = d.getFrame(i);
            repaint();
            try
            {
            	Thread.sleep(40);
            }
            catch (Exception e)
            {
            }
        }
    }

	public void pointerPressed(int x, int y)
	{
		if(currentAlert != null)
		{
			currentAlert.press(x, y);
		}
		else if(showCaptcha)
		{
			VikaTouch.captchaScr.press(x, y);
		}
		else if(currentScreen != null)
		{
			currentScreen.press(x, y);
		}
	}
	
	public void pointerReleased(int x, int y)
	{
		if(currentAlert != null)
		{
			currentAlert.release(x, y);
		}
		else if(showCaptcha)
		{
			VikaTouch.captchaScr.release(x, y);
		}
		else if(currentScreen != null)
		{
			currentScreen.release(x, y);
		}
	}
	
	public void pointerDragged(int x, int y)
	{
		if(currentAlert != null)
		{
			currentAlert.drag(x, y);
		}
		else if(showCaptcha)
		{
			VikaTouch.captchaScr.drag(x, y);
		}
		else if(currentScreen != null)
		{
			currentScreen.drag(x, y);
		}
	}
	
	public void keyPressed(int i)
	{
		if(currentAlert != null)
		{
			currentAlert.press(i);
		}
		else if(showCaptcha)
		{
			VikaTouch.captchaScr.press(i);
		}
		else if(currentScreen != null)
		{
			currentScreen.press(i);
		}
	}
	
	public void keyRepeated(int i)
	{
		if(currentAlert != null)
		{
			currentAlert.repeat(i);
		}
		else if(showCaptcha)
		{
			VikaTouch.captchaScr.repeat(i);
		}
		else if(currentScreen != null)
		{
			currentScreen.repeat(i);
		}
	}
	
	public void keyReleased(int i)
	{
		if(currentAlert != null)
		{
			currentAlert.release(i);
		}
		else if(showCaptcha)
		{
			VikaTouch.captchaScr.release(i);
		}
		else if(currentScreen != null)
		{
			currentScreen.release(i);
		}
	}

	public void paint()
	{
		if(!VikaTouch.appInst.isPaused || currentAlert != null)
		{
			repaint();
			//serviceRepaints();
		}
	}

	public void tick() 
	{
		if(Display.getDisplay(VikaTouch.appInst).getCurrent() instanceof VikaCanvasInst)
		{
			if(VikaTouch.loading)
			{
				updategif();
				/*if(Settings.animateTransition)
				{
					oldScreen = null;
					slide = 0;
				}*/
			}
			else
				paint();
			/*if(Settings.animateTransition)
			{
				double sliden = Math.abs(slide);
				if(sliden > 0)
				{
					slide *= 0.78;
					if(sliden < 0.015)
					{
						oldScreen = null;
						slide = 0;
					}
				}
			}*/
		}
	}

	public void callCommand(int i, VikaScreen screen)
	{
		VikaTouch.inst.cmdsInst.command(i, screen);
	}

	public boolean isSensorModeOK()
	{
		return Settings.sensorMode == Settings.SENSOR_OK;
	}

	public boolean isSensorModeJ2MELoader()
	{
		return Settings.sensorMode == Settings.SENSOR_J2MELOADER;
	}

	public boolean poorScrolling()
	{
		return Settings.sensorMode == Settings.SENSOR_KEMULATOR;
	}

}
