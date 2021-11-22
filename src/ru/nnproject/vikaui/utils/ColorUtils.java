// This file is part of VikaUI
// Copyright (C) 2020  Arman Jussuplaliyev (Shinovon)

package ru.nnproject.vikaui.utils;

import javax.microedition.lcdui.Graphics;

/**
 * @author Shinovon
 * 
 */
public class ColorUtils {
	/*
	 * public final static int[] color = {80, 118, 167}; public final static
	 * int[] textcolor = {193, 206, 224}; public final static int[] buttoncolor
	 * = {81, 129, 184}; public final static int[] buttoncolorhover = {102, 144,
	 * 192}; public final static int[] onlinecolor = {74, 178, 78};
	 */
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
	public static final int MUSICCOLOR = -49;
	public static final int UNREAD_MSG_COLOR = -52;

	public static boolean isNight() {
		return DisplayUtils.canvas.isNight();
	}

	public static void setcolor(final Graphics g, final int i) {
		switch (i) {
		case 0: {
			if (isNight())
				g.setGrayScale(225);
			else
				g.setColor(0);
			break;
		}
		case MYMSG: {
			if (isNight())
				g.setGrayScale(24);
			else
				g.setColor(198, 218, 246);
			break;
		}
		case FOREIGNMSG: {
			if (isNight())
				g.setGrayScale(44);
			else
				g.setColor(236, 238, 240);
			break;
		}
		case COLOR1: {
			g.setColor(80, 118, 167);
			break;
		}
		case TEXTCOLOR1: {
			g.setColor(193, 206, 224);
			break;
		}
		case TITLEPANELCOLOR: {
			if (isNight())
				g.setGrayScale(12);
			else
				g.setColor(81, 129, 184);
			break;
		}
		case MUSICCOLOR: {
			if (isNight())
				g.setGrayScale(100);
			else
				g.setColor(81, 129, 184);
			break;
		}
		case BUTTONCOLOR: {
			if (isNight())
				g.setGrayScale(30);
			else
				g.setColor(81, 129, 184);
			break;
		}
		case ONLINE: {
			g.setColor(74, 178, 78);
			break;
		}
		case TEXT: {
			if (isNight())
				g.setGrayScale(225);
			else
				g.setColor(0);
			break;
		}
		case OUTLINE: {
			g.setGrayScale(100);
			break;
		}
		case 7: {
			g.setColor(157, 164, 172);
			break;
		}
		case 8: {
			g.setColor(158, 180, 205);
			break;
		}
		case MSGTEXT: {
			if (isNight())
				g.setGrayScale(180);
			else
				g.setGrayScale(0);
			break;
		}
		case TEXT2: {
			g.setGrayScale(145);
			break;
		}
		case BACKGROUND: {
			if (isNight())
				g.setColor(0);
			else
				g.setGrayScale(255);
			break;
		}
		case BUTTONTEXT: {
			g.setGrayScale(255);
			break;
		}
		case TEXTBOX_OUTLINE: {
			if (isNight())
				g.setGrayScale(35);
			else
				g.setGrayScale(238);
			break;
		}
		case BOTTOMPANELCOLOR: {
			if (isNight())
				g.setGrayScale(30);
			else
				g.setGrayScale(249);
			break;
		}
		case UNREAD_MSG_COLOR: {
			if (isNight())
				g.setColor(0);
			else
				g.setColor(243,244,246);
			break;
		}
		
		
		case -4: {
			if (isNight())
				g.setGrayScale(9);
			else
				g.setColor(0xCFD6DE);
			break;
		}
		case -5: {
			if (isNight())
				g.setGrayScale(3);
			else
				g.setGrayScale(231);
			break;
		}
		case -6: {
			if (isNight())
				g.setGrayScale(2);
			else
				g.setGrayScale(243);
			break;
		}
		case -7: {
			if (isNight())
				g.setGrayScale(17);
			else
				g.setGrayScale(234);
			break;
		}
		case -8: {
			if (isNight())
				g.setGrayScale(2);
			else
				g.setGrayScale(237);
			break;
		}
		case -9: {
			if (isNight())
				g.setGrayScale(19);
			else
				g.setGrayScale(247);
			break;
		}
		case -10: {
			if (isNight())
				g.setGrayScale(32);
			else
				g.setGrayScale(222);
			break;
		}
		case -11: {
			if (isNight())
				g.setGrayScale(25);
			else
				g.setGrayScale(229);
			break;
		}
		case -12: {
			if (isNight())
				g.setGrayScale(15);
			else
				g.setGrayScale(241);
			break;
		}
		default: {
			// g.setGrayScale(255);
			break;
		}
		}
		return;
	}

}
