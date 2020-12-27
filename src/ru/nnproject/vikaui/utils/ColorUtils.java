package ru.nnproject.vikaui.utils;

import javax.microedition.lcdui.Graphics;

import vikatouch.settings.Settings;

public class ColorUtils
{
	public final static int[] color = {80, 118, 167};
	public final static int[] textcolor = {193, 206, 224};
	public final static int[] buttoncolor = {81, 129, 184};
	public final static int[] buttoncolorhover = {102, 144, 192};
	public final static int[] onlinecolor = {74, 178, 78};
	public static final int BACKGROUND = -1;
	public static final int COLOR1 = 1;
	public static final int TEXTCOLOR1 = 2;
	public static final int BUTTONCOLOR = 3;
	public static final int ONLINE = 4;
	public static final int TEXT = 5;
	public static final int OUTLINE = 6;
	public static final int TEXTBOX_OUTLINE = -2;
	public static final int TEXT2 = 9;
	
	public static final int MYMSG = 40;
	public static final int FOREIGNMSG = 41;
	public static final int BUTTONTEXT = 50;
	public static final int BOTTOMPANELCOLOR = -3;
	public static final int TITLEPANELCOLOR = -99;
	public static final int MSGTEXT = -50;
	
	public static void setcolor(final Graphics g, final int i)
	{
		switch(i)
		{
			case 0:
			{
				if(Settings.nightTheme)
					g.setGrayScale(225);
				else
					g.setColor(0);
				break;
			}
			case MYMSG:
			{
				if(Settings.nightTheme)
					g.setGrayScale(24);
				else
					g.setColor(198, 218, 246);
				break;
			}
			case FOREIGNMSG:
			{
				if(Settings.nightTheme)
					g.setGrayScale(44);
				else
					g.setColor(236, 238, 240);
				break;
			}
			case COLOR1:
			{
				g.setColor(color[0], color[1], color[2]);
				break;
			}
			case TEXTCOLOR1:
			{
				g.setColor(textcolor[0], textcolor[1], textcolor[2]);
				break;
			}
			case TITLEPANELCOLOR:
			{
				if(Settings.nightTheme)
					g.setGrayScale(12);
				else
					g.setColor(buttoncolor[0], buttoncolor[1], buttoncolor[2]);
				break;
			}
			case BUTTONCOLOR:
			{
				if(Settings.nightTheme)
					g.setGrayScale(30);
				else
					g.setColor(buttoncolor[0], buttoncolor[1], buttoncolor[2]);
				break;
			}
			case ONLINE:
			{
				g.setColor(onlinecolor[0], onlinecolor[1], onlinecolor[2]);
				break;
			}
			case TEXT:
			{
				if(Settings.nightTheme)
					g.setGrayScale(225);
				else
					g.setColor(0);
				break;
			}
			case OUTLINE:
			{
				g.setGrayScale(100);
				break;
			}
			case 7:
			{
				g.setColor(157, 164, 172);
				break;
			}
			case 8:
			{
				g.setColor(158, 180, 205);
				break;
			}
			case MSGTEXT:
			{
				if(Settings.nightTheme)
					g.setGrayScale(180);
				else
					g.setGrayScale(0);
				break;
			}
			case TEXT2:
			{
				g.setGrayScale(145);
				break;
			}
			case BACKGROUND:
			{
				if(Settings.nightTheme)
					g.setColor(0);
				else
					g.setGrayScale(255);
				break;
			}
			case BUTTONTEXT:
			{
				g.setGrayScale(255);
				break;
			}
			case TEXTBOX_OUTLINE:
			{
				if(Settings.nightTheme)
					g.setGrayScale(35);
				else
					g.setGrayScale(238);
				break;
			}
			case BOTTOMPANELCOLOR:
			{
				if(Settings.nightTheme)
					g.setGrayScale(30);
				else
					g.setGrayScale(249);
				break;
			}
			case -4:
			{
				if(Settings.nightTheme)
					g.setGrayScale(9);
				else
					g.setColor(0xCFD6DE);
				break;
			}
			case -5:
			{
				if(Settings.nightTheme)
					g.setGrayScale(3);
				else
					g.setGrayScale(231);
				break;
			}
			case -6:
			{
				if(Settings.nightTheme)
					g.setGrayScale(2);
				else
					g.setGrayScale(243);
				break;
			}
			case -7:
			{
				if(Settings.nightTheme)
					g.setGrayScale(17);
				else
					g.setGrayScale(234);
				break;
			}
			case -8:
			{
				if(Settings.nightTheme)
					g.setGrayScale(2);
				else
					g.setGrayScale(237);
				break;
			}
			case -9:
			{
				if(Settings.nightTheme)
					g.setGrayScale(19);
				else
					g.setGrayScale(247);
				break;
			}
			case -10:
			{
				if(Settings.nightTheme)
					g.setGrayScale(32);
				else
					g.setGrayScale(222);
				break;
			}
			case -11:
			{
				if(Settings.nightTheme)
					g.setGrayScale(25);
				else
					g.setGrayScale(229);
				break;
			}
			case -12:
			{
				if(Settings.nightTheme)
					g.setGrayScale(15);
				else
					g.setGrayScale(241);
				break;
			}
			default:
			{
				//g.setGrayScale(255);
				break;
			}
		}
		return;
	}

}
