package vikatouch.screens;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ru.nnproject.vikaui.screen.VikaScreen;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.locale.TextLocal;
import vikatouch.screens.menu.MenuScreen;
import vikatouch.utils.text.TextEditor;

public class LoginScreen
	extends VikaScreen
{
	
	//private static Image vikaLogo;
	private static Image loginpressed;
	private static Image login;
	//private static Image settingsImg;
	private static boolean pressed;
	public static boolean loginSucsess;
	public boolean isLoggingInNow = false;
	public static String user = "";
	public static String pass = "";
	public static Thread thread;
	private int selectedBtn;
	private boolean keysMode;
	
	// tapping cache
	private int[] tapCoords;
	
	private String titleLoginStr;
	protected String passwordStr;
	protected String loginStr;
	private String warnStr;
	private String failedStr;

	public LoginScreen()
	{
		/*try
		{
			vikaLogo = Image.createImage("/vikab48.jpg");
		}
		catch (IOException e)
		{ }*/
		try
		{
			login = Image.createImage("/login.png");
			loginpressed = Image.createImage("/loginpressed.png");
		}
		catch (IOException e)
		{ }
		/*try
		{
			settingsImg = Image.createImage("/settings.png");
		}
		catch (IOException e)
		{ }*/
		pressed = false;
		titleLoginStr = TextLocal.inst.get("title.login");
		loginStr = TextLocal.inst.get("login.login");
		passwordStr = TextLocal.inst.get("login.password");
		warnStr = TextLocal.inst.get("login.warn");
		failedStr = TextLocal.inst.get("login.failed");
		selectedBtn = 1;
		keysMode = true;
	}
	
	public void login()
	{
		if(user != null && user.length() >= 5 && pass != null && pass.length() >= 6)
		{
			if(!loginSucsess)
			{
				new Thread()
				{
					public void run()
					{
						try
						{
							isLoggingInNow = true;
							VikaTouch.loading = true;
							loginSucsess = VikaTouch.inst.login(user, pass);
							
							String reason;
							if(!loginSucsess && (reason = VikaTouch.getReason()) != null)
							{
								//VikaTouch.popup(new InfoPopup(reason, null, failedStr, "OK"));
								VikaTouch.warn(reason, failedStr);
							}
						}
						finally
						{
							isLoggingInNow = false;
							VikaTouch.loading = false;
						}
					}
				}.start();
			}
		}
		else
		{
			VikaTouch.loading = false;
			VikaTouch.warn(warnStr);
		}
	}
	
	public final void press(int key)
	{
		keysMode = true;
		if(isLoggingInNow) return;
		if((key == -5) || (key==Canvas.FIRE))
		{
			if(selectedBtn == 0)
			{
				VikaTouch.inst.cmdsInst.command(13, this);
			}
			else if(selectedBtn == 1)
			{
				if(thread != null)
					thread.interrupt();
				thread = new Thread()
				{
					public void run()
					{
						user = TextEditor.inputString(loginStr, user, 28, false);
						repaint();
					}
				};
				thread.start();
			}
			else if(selectedBtn == 2)
			{
				if(thread != null)
					thread.interrupt();
				thread = new Thread()
				{
					public void run()
					{
						pass = TextEditor.inputString(passwordStr, pass, 32, false);
						repaint();
					}
				};
				thread.start();
			}
			else if(selectedBtn == 3) 
			{
				login();
			}
		}
		else if ((key == -2) || (key==Canvas.DOWN))
		{
			selectedBtn++;
			if(selectedBtn > 3)
			{
				selectedBtn = 3;
			}
			if(selectedBtn == 3)
			{
				pressed = true;
			}
		}
		else if ((key == -1) || (key==Canvas.UP))
		{
			selectedBtn--;
			if(selectedBtn == 2)
			{
				pressed = false;
			}
			if(selectedBtn < 0)
				selectedBtn = 0;
		}
		else if(key == -7 || key == -8 || key == 8)
		{
			if(selectedBtn == 1)
			{
				user = user.substring(0, user.length() - 1);
			}
			else if(selectedBtn == 2)
			{
				pass = pass.substring(0, pass.length() - 1);
			}
		}
		else
		{
			String s = VikaTouch.canvas.getKeyName(key);
			if(s.length() == 1)
			{
				if(selectedBtn == 1)
				{
					if(s == "*")
						s = "+";
					user += s;
				}
				else if(selectedBtn == 2)
				{
					pass += s;
				}
			}
		}
	}

	public void draw(Graphics g)
	{
		try
		{
			short sh = DisplayUtils.height;
			short sw = DisplayUtils.width;
			short yCenter = (short) (sh/2);
			byte fH = 40; // field height
			boolean shortLayout = sh<250;
			byte fieldsMargin = (byte) (shortLayout?30:60);
			
			ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
			g.fillRect(0, 0, sw, sh);
			
			ColorUtils.setcolor(g, ColorUtils.COLOR1);
			g.fillRect(0, 0, DisplayUtils.width, shortLayout?24:48);
			if(keysMode && selectedBtn == 0)
			{
			//	g.setGrayScale(255);
			//	g.drawRect(DisplayUtils.width-35, DisplayUtils.compact?0:18, 24, 24);
				g.drawImage(IconsManager.ico[IconsManager.SETTINGS], DisplayUtils.width-36, DisplayUtils.compact?0:8, 0);
			}
			else
			{
				g.drawImage(IconsManager.selIco[IconsManager.TOPBAR], DisplayUtils.width-36, DisplayUtils.compact?0:8, 0);
			}
			//if(!shortLayout) g.fillRect(0, DisplayUtils.height-50, DisplayUtils.width, 50);
			//if(vikaLogo != null && !shortLayout)
			//{
		//		g.drawImage(vikaLogo, 2, 0, 0);
			//}
			Font f;
			//Font f = Font.getFont(0, 0, Font.SIZE_LARGE);
			//g.setFont(f); 
			ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
			//g.drawString("Vika Touch - "+titleLoginStr, 8, (shortLayout?12:24)-f.getHeight()/2, 0);
			
			tapCoords = new int[] { yCenter - fH/2 - 8 - fH, yCenter - fH/2 - 8, yCenter - fH/2, yCenter + fH/2, yCenter + fH/2 + 8, yCenter + fH/2 + 8 + 32};
			
			ColorUtils.setcolor(g, ColorUtils.TEXTBOX_OUTLINE);
			if(selectedBtn == 1)
				ColorUtils.setcolor(g, ColorUtils.TEXT);
			g.drawRect(fieldsMargin, tapCoords[0], sw-fieldsMargin*2, fH);
			ColorUtils.setcolor(g, ColorUtils.TEXTBOX_OUTLINE);
			if(selectedBtn == 2)
				ColorUtils.setcolor(g, ColorUtils.TEXT);
			g.drawRect(fieldsMargin, tapCoords[2], sw-fieldsMargin*2, fH);
			ColorUtils.setcolor(g, ColorUtils.TEXTBOX_OUTLINE);
			if(loginpressed != null && pressed) {
				g.drawImage(loginpressed, DisplayUtils.width/2-loginpressed.getWidth()/2, tapCoords[4], 0);
			} else if(login != null)
				g.drawImage(login, DisplayUtils.width/2-login.getWidth()/2, tapCoords[4], 0);
			
			f = Font.getFont(0, 0, Font.SIZE_SMALL);
			g.setFont(f);
			ColorUtils.setcolor(g, ColorUtils.TEXT); // нихера не видно ни на СЕшке, ни на ж2меЛ. Подозреваю что везде не на амоледе.
			if(user != null)
				g.drawString(user, fieldsMargin+10, tapCoords[0]+fH/2-f.getHeight()/2, 0);
			if(pass != null)
			{
				String strpass = "";
				if(keysMode && selectedBtn == 2)
				{
					strpass = pass;
				}
				else
				{
					for(int i = 0; i < pass.length(); i++)
						strpass += "*";
				}
				g.drawString(strpass, fieldsMargin+10, tapCoords[2]+fH/2-f.getHeight()/2, 0);
			}
		}
		catch(Exception e)
		{ }
	}
	
	public final void press(int x, int y) {
		if(isLoggingInNow) return;
		if(x > DisplayUtils.width - 50 && y < 50)
		{
			VikaTouch.inst.cmdsInst.command(13, this);
		}
		keysMode = false;
		if(y>tapCoords[4]&&y<tapCoords[5])
		{
			pressed = true;
			if(!loginSucsess)
				repaint();
		}
		else if(y>tapCoords[0]&&y<tapCoords[1])
		{
			if(thread != null)
				thread.interrupt();
			thread = new Thread()
			{
				public void run()
				{
					user = TextEditor.inputString(loginStr, user, 28, false);
					repaint();
					interrupt();
				}
			};
			thread.start();
		}
		else if(y>tapCoords[2]&&y<tapCoords[3])
		{
			if(thread != null)
				thread.interrupt();
			thread = new Thread()
			{
				public void run()
				{
					pass = TextEditor.inputString(passwordStr, pass, 32, true);
					repaint();
					interrupt();
				}
			};
			thread.start();
		}
	}

	public final void release(int x, int y) {
		
		if(isLoggingInNow) return;
		if(pressed)
		{
			pressed = false;
			if(y>tapCoords[4]&&y<tapCoords[5])
			{
				login();
			}
			else
			{
				if(!loginSucsess)
					repaint();
			}
		}
	}
}