package vikatouch.screens;

import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ru.nnproject.vikaui.popup.ConfirmBox;
import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.locale.TextLocal;
import vikatouch.screens.menu.MenuScreen;
import vikatouch.settings.Settings;
import vikatouch.settings.SettingsScreen;

public abstract class MainScreen
	extends ScrollableCanvas
{

	public static int lastMenu;
	protected boolean hasBackButton; // кст, почему не статик?
	public MainScreen backScreen;

	public static int topPanelH = 58;
	public static int bottomPanelH = 50;

	public MainScreen()
	{
		super();
	}

	protected void scrollHorizontally(int deltaX)
	{
		/*
		if(deltaX < -7)
		{
			VikaTouch.inst.cmdsInst.commandAction(10, this);
		}
		if(deltaX > 7)
		{
			VikaTouch.inst.cmdsInst.commandAction(11, this);
		}
		*/
	}

	public void release(int x, int y)
	{
		if(!dragging || !canScroll)
		{
			int wyw = bbw(DisplayUtils.idispi);
			if(y < topPanelH)
			{
				if(hasBackButton && x < oneitemheight)
				{
					//VikaTouch.setDisplay(backScreen, -1);
					VikaTouch.inst.cmdsInst.command(14, this);
				}
				if(!(this instanceof SettingsScreen) && x > DisplayUtils.width - oneitemheight)
				{
					VikaTouch.inst.cmdsInst.command(13, this);
				}
			}
			else if(y >= DisplayUtils.height - bottomPanelH)
			{
				int acenter = (DisplayUtils.width - wyw) / 2;
				if(x < wyw)
				{
					VikaTouch.inst.cmdsInst.command(0, this);
				}

				if(x > DisplayUtils.width - wyw)
				{
					VikaTouch.inst.cmdsInst.command(2, this);
				}

				if(x > acenter && x < acenter + wyw)
				{
					VikaTouch.inst.cmdsInst.command(1, this);
				}
			}
		}

		super.release(x, y);
	}

	protected void drawHUD(Graphics g, String title)
	{
		// vars
		topPanelH = DisplayUtils.compact?24:58;
		bottomPanelH = 50;
		int dw = DisplayUtils.width;
		boolean showBottomPanel = (!keysMode || !DisplayUtils.compact || (this instanceof NewsScreen)||(this instanceof DialogsScreen)||(this instanceof MenuScreen));
		// fills
		ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
		g.fillRect(0, 0, dw, topPanelH);
		ColorUtils.setcolor(g, -3);
		if(showBottomPanel) g.fillRect(0, DisplayUtils.height - bottomPanelH, dw, bottomPanelH);
			
		if(!DisplayUtils.compact)
		{
			// header & icon
			if(hasBackButton && !keysMode)
			{
				g.drawImage(IconsManager.backImg, 6, topPanelH/2-IconsManager.backImg.getHeight()/2, 0);
			}
			else if(this instanceof MenuScreen)
				g.drawImage(IconsManager.logoImg, 16, topPanelH/2-IconsManager.logoImg.getHeight()/2, 0);
			// Такое себе, согласен. Ну хоть что-то.
				
		}
		else if(this instanceof MenuScreen)
		{
			title = "ViKa touch";
		}
		g.setFont(Font.getFont(0, 0, Font.SIZE_LARGE));
		g.setGrayScale(255);
		g.drawString(title, (DisplayUtils.compact || !hasBackButton) ? 10 : 72, topPanelH/2-g.getFont().getHeight()/2, 0);
		Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
		g.setFont(f);
		
		if (showBottomPanel)
		{
			// bottom icons
			int bpiy = DisplayUtils.height - bottomPanelH/2 - 12;
			g.drawImage(((this instanceof NewsScreen)?IconsManager.selIco:IconsManager.ico)[IconsManager.NEWS], dw/6-12, bpiy, 0);
			g.drawImage(((this instanceof DialogsScreen)?IconsManager.selIco:IconsManager.ico)[IconsManager.MSGS], dw/2-12, bpiy, 0);
			g.drawImage(((this instanceof MenuScreen)?IconsManager.selIco:IconsManager.ico)[IconsManager.MENU], dw-dw/6-12, bpiy, 0);
					
			// unread count
			if(VikaTouch.unreadCount > 0)
			{
				String s = "" + VikaTouch.unreadCount;
				int d2 = 16;
				int d3 = 2;
				boolean roundrect = false;
				if(VikaTouch.unreadCount > 9)
				{
					s = "9+";
					d2 = 24;
					d3 = 2 - ((d2 - 16) / 2);
					roundrect = true;
				}
				int d = 16;
				int fh = f.getHeight();
	
				g.setColor(225, 73, 73);
				if(roundrect)
				{
					g.fillRoundRect(dw/2+d3, bpiy-5, d2, d, 8, 8);
				}
				else
				{
					g.fillArc(dw/2+d3, bpiy-5, d2, d, 0, 360);
				}
				g.setGrayScale(255);
				g.drawString(s, dw/2+2+(d-f.stringWidth(s))/2, bpiy-5+(d-fh)/2+1, 0);
			}
		}

		if(Settings.debugInfo)
		{
			g.setColor(0xffff00);
			int xx = endx;
			int yy = endy;
			if(xx == -1)
			{
				xx = lastx;
				yy = lasty;
			}
			g.drawLine(startx, starty, xx, yy);
			g.drawRect(startx-2, starty-2, 4, 4);
			g.setColor(0xff0000);
			g.drawRect(endx-2, endy-2, 4, 4);
			g.drawString("cs"+scroll + " sc" + scrolled + " d" + drift + " ds" + driftSpeed + " st" + scrollingTimer + " sp" + scrollPrev + " t" + timer, 0, 30, 0);
		}
	}

	public void drawHUD(Graphics g)
	{
		drawHUD(g, "");
	}

	/*
	protected void drawHUDOld(Graphics g, String title)
	{
		double multiplier = (double)DisplayUtils.height / 640.0;
		double ww = 10.0 * multiplier;
		int w = (int)ww;
		switch(DisplayUtils.idispi)
		{
			case DisplayUtils.DISPLAY_PORTRAIT:
			{
				ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
				g.fillRect(0, 0, DisplayUtils.width, 58);
				if(VikaTouch.menuScr != null)
				{
					ColorUtils.setcolor(g, -3);
					g.fillRect(0, DisplayUtils.height - 50, DisplayUtils.width, 50);

					if(menuImg != null)
					{
						g.drawImage(menuImg, 304, 606, 0);
					}
					if(!hasBackButton && MenuScreen.logoImg != null)
					{
						g.drawImage(MenuScreen.logoImg, 2, 2, 0);
					}
					if(newsImg != null)
					{
						g.drawImage(newsImg, 37, 604, 0);
					}
					if(VikaTouch.unreadCount > 0)
					{
						if(MenuScreen.dialImg2 != null)
						{
							g.drawImage(MenuScreen.dialImg2, 168, 599, 0);
							g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
							g.drawString(""+VikaTouch.unreadCount, 191, 598, 0);
						}
						else if(MenuScreen.dialImg != null)
						{
							g.drawImage(MenuScreen.dialImg, 168, 604, 0);
						}

					}
					else
					{
						if(MenuScreen.dialImg != null)
						{
							g.drawImage(MenuScreen.dialImg, 168, 604, 0);
						}
					}
				}
				if(hasBackButton && backImg != null)
				{
					g.drawImage(backImg, 2, 2, 0);
				}
				g.setFont(Font.getFont(0, 0, Font.SIZE_LARGE));
				g.drawString(title, 72, 29-g.getFont().getHeight()/2, 0);
				g.setFont(Font.getFont(0, 0, 8));
				break;
			}
			case DisplayUtils.DISPLAY_S40:
			case DisplayUtils.DISPLAY_ASHA311:
			{
				ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
				g.fillRect(0, 0, 240, 30);
				if(VikaTouch.menuScr != null)
				{
					ColorUtils.setcolor(g, -3);
					g.fillRect(0, DisplayUtils.height - 25, 240, 25);

					if(!hasBackButton && MenuScreen.logoImg != null)
					{
						g.drawImage(MenuScreen.logoImg, 2, 1, 0);
					}

					if(menuImg != null)
					{
						g.drawImage(menuImg, 212, 303, 0);
					}

					if(newsImg != null)
					{
						g.drawImage(newsImg, 18, 301, 0);
					}

					if(VikaTouch.unreadCount > 0)
					{
						if(MenuScreen.dialImg2 != null)
						{
							g.drawImage(MenuScreen.dialImg2, 114, 299, 0);
							g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
							g.drawString(""+VikaTouch.unreadCount, 126, 300, 0);
						}
						else if(MenuScreen.dialImg != null)
						{
							g.drawImage(MenuScreen.dialImg, 114, 302, 0);
						}

					}
					else
					{
						if(MenuScreen.dialImg != null)
						{
							g.drawImage(MenuScreen.dialImg, 114, 302, 0);
						}
					}
				}

				if(hasBackButton && backImg != null)
				{
					g.drawImage(backImg, 0, 0, 0);
				}
				g.setFont(Font.getFont(0, 0, Font.SIZE_LARGE));
				g.drawString(title, 52, 0, 0);
				g.setFont(Font.getFont(0, 0, 8));
				break;
			}

			case DisplayUtils.DISPLAY_ALBUM:
			{
				ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
				g.fillRect(0, 0, 640, 58);
				if(VikaTouch.menuScr != null)
				{
					ColorUtils.setcolor(g, -3);
					g.fillRect(0, 310, 640, 50);

					if(!hasBackButton && MenuScreen.logoImg != null)
					{
						g.drawImage(MenuScreen.logoImg, 2, 2, 0);
					}
					if(newsImg != null)
					{
						g.drawImage(newsImg, 36, 324, 0);
					}
					if(VikaTouch.unreadCount > 0)
					{

						if(MenuScreen.dialImg2 != null)
						{
							g.drawImage(MenuScreen.dialImg2, 308, 319, 0);
							g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
							g.drawString(""+VikaTouch.unreadCount, 330, 318, 0);
						}
						else if(MenuScreen.dialImg != null)
						{
							g.drawImage(MenuScreen.dialImg, 308, 324, 0);
						}

					}
					else
					{
						if(MenuScreen.dialImg != null)
						{
							g.drawImage(MenuScreen.dialImg, 308, 324, 0);
						}
					}
					if(menuImg != null)
					{
						g.drawImage(menuImg, 584, 326, 0);
					}
				}

				if(hasBackButton && backImg != null)
				{
					g.drawImage(backImg, 2, 2, 0);
				}
				g.setFont(Font.getFont(0, 0, Font.SIZE_LARGE));
				g.drawString(title, 72, 14, 0);
				g.setFont(Font.getFont(0, 0, 8));

				break;
			}

			case DisplayUtils.DISPLAY_EQWERTY:
			{
				ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
				g.fillRect(0, 0, 320, 30);
				if(VikaTouch.menuScr != null)
				{
					ColorUtils.setcolor(g, -3);
					g.fillRect(0, 215, 320, 25);

					if(!hasBackButton && MenuScreen.logoImg != null)
					{
						g.drawImage(MenuScreen.logoImg, 2, 1, 0);
					}

					if(menuImg != null)
					{
						g.drawImage(menuImg, 292, 303-75, 0);
					}

					if(newsImg != null)
					{
						g.drawImage(newsImg, 18, 301-75, 0);
					}

					if(VikaTouch.unreadCount > 0)
					{
						if(MenuScreen.dialImg2 != null)
						{
							g.drawImage(MenuScreen.dialImg2, 114, 299-75, 0);
							g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
							g.drawString(""+VikaTouch.unreadCount, 126, 300-75, 0);
						}
						else if(MenuScreen.dialImg != null)
						{
							g.drawImage(MenuScreen.dialImg, 114, 302-75, 0);
						}

					}
					else
					{
						if(MenuScreen.dialImg != null)
						{
							g.drawImage(MenuScreen.dialImg, 114, 302-75, 0);
						}
					}
				}

				if(hasBackButton && backImg != null)
				{
					g.drawImage(backImg, 2, 0, 0);
				}
				g.setFont(Font.getFont(0, 0, Font.SIZE_LARGE));
				g.drawString(title, 52, 0, 0);
				g.setFont(Font.getFont(0, 0, 8));
				break;
			}

			default:
			{
				ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
				g.fillRect(0, 0, DisplayUtils.width, oneitemheight + w);
				ColorUtils.setcolor(g, -3);
				g.fillRect(0, DisplayUtils.height - oneitemheight, DisplayUtils.width, oneitemheight);

				if(!hasBackButton && MenuScreen.logoImg != null)
				{
					g.drawImage(MenuScreen.logoImg, 2, 1, 0);
				}

				if(hasBackButton && backImg != null)
				{
					g.drawImage(backImg, 2, 1, 0);
				}

				g.drawString(title, 72, 0, 0);
			}
		}
		if(Settings.debugInfo)
		{
			g.setColor(0xffff00);
			int xx = endx;
			int yy = endy;
			if(xx == -1)
			{
				xx = lastx;
				yy = lasty;
			}
			g.drawLine(startx, starty, xx, yy);
			g.drawRect(startx-2, starty-2, 4, 4);
			g.setColor(0xff0000);
			g.drawRect(endx-2, endy-2, 4, 4);
			g.drawString("cs"+scroll + " sc" + scrolled + " d" + drift + " ds" + driftSpeed + " st" + scrollingTimer + " sp" + scrollPrev + " t" + timer, 0, 30, 0);
		}
	} */

	private int bbw(int i)
	{
		switch(i)
		{
			case 1:
				return 96;
			case 2:
				return 64;
			case 5:
				return 96;
			case 6:
				return 96;
			case 3:
				return 64;
			case 4:
				return 64;
			default:
				return 64;
		}
	}

}
