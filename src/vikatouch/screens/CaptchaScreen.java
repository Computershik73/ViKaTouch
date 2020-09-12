package vikatouch.screens;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ru.nnproject.vikaui.screen.VikaScreen;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.locale.TextLocal;
import vikatouch.utils.captcha.CaptchaObject;
import vikatouch.utils.text.TextEditor;

public class CaptchaScreen
	extends VikaScreen
{
	public Image image;
	private Thread thread;
	public static String input;
	public static boolean finished;
	public CaptchaObject obj;
	private int x;
	private int w;
	private String captchaRequiredStr;
	private String captchaStr;
	private boolean switcher;
	
	public CaptchaScreen()
	{
		super();
		captchaRequiredStr = TextLocal.inst.get("login.captcharequired");
		captchaStr = TextLocal.inst.get("login.captcha");
		switcher = false;
	}

	public void draw(Graphics g)
	{
		ColorUtils.setcolor(g, -1);
		g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);
		if(obj != null && image == null)
		{
			image = obj.getImage();
		}
		w = image.getWidth();
		ColorUtils.setcolor(g, -2);
		if(!switcher)
			ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawRect(0, 100, 240, 40);
		ColorUtils.setcolor(g, 2);
		if(input != null)
			g.drawString(input, 10, 110, 0);
		x = (DisplayUtils.width - w) / 2;
		ColorUtils.setcolor(g, -5);
		g.drawString(captchaRequiredStr/*"Требуется ввод капчи!"*/, DisplayUtils.width / 2, 0, Graphics.TOP | Graphics.HCENTER);
		g.drawImage(image, x, 24, 0);
		ColorUtils.setcolor(g, 3);
		g.fillRect(x, 150, w, 36);
	}
	
	public final void press(short x, short y)
	{
		if(y > 100 && y < 140 && x < 240)
		{
			if(thread != null)
				thread.interrupt();
			thread = new Thread()
			{
				public void run()
				{
					input = TextEditor.inputString(captchaStr, "", 32, false);
					interrupt();
				}
			};
			thread.start();
		}
	}
	
	public final void release(short x, short y)
	{
		if(x > this.x && y > 150 && y < 186 && x < this.x + this.w)
		{
			finished = true;
			VikaTouch.canvas.showCaptcha = false;
		}
	}
	public final void press(int key)
	{
		if(key == -1 || key == -2)
		{
			switcher = !switcher;
		}
		else
		if(key == -5)
		{
			if(!switcher)
			{
				if(thread != null)
					thread.interrupt();
				thread = new Thread()
				{
					public void run()
					{
						input = TextEditor.inputString(captchaStr, "", 32, false);
						interrupt();
					}
				};
				thread.start();
			}
			else
			{
				finished = true;
				VikaTouch.canvas.showCaptcha = false;
			}
		}
	}

}
