// This file is part of VikaUI
// Copyright (C) 2020  Arman Jussuplaliyev (Shinovon)

package ru.nnproject.vikaui.utils;

import ru.nnproject.vikaui.VikaCanvas;
import ru.nnproject.vikaui.screen.ScrollableCanvas;

/**
 * @author Shinovon
 * 
 */
public class DisplayUtils {
	public static short idispi;
	public static short width;
	public static short height;
	public static short lwidth;
	public static short lheight;
	public static byte current;
	public static VikaCanvas canvas;

	public static boolean compact; // дисплеи <300 в высоту (е72, альбом
									// 240х320, портреты 220 и 208.

	// Дисплеи
	public static final int CANVAS_LOGIN = 1;
	public static final int CANVAS_MENU = 2;
	public static final int CANVAS_NEWS = 3;
	public static final int CANVAS_CHATSLIST = 4;
	public static final int CANVAS_CHAT = 5;
	public static final int CANVAS_ABOUT = 6;
	public static final int CANVAS_SETTINGS = 7;
	public static final int CANVAS_TEMPLIST = 8;
	public static final int CANVAS_LOADING = 9;
	public static final int CANVAS_MUSICLIST = 10;
	public static final int CANVAS_DOCSLIST = 11;
	public static final int CANVAS_VIDEOSLIST = 12;
	public static final int CANVAS_FRIENDSLIST = 13;
	public static final int CANVAS_GROUPSLIST = 14;
	public static final int CANVAS_PHOTOSLIST = 15;

	// Экраны
	public static final int DISPLAY_PORTRAIT = 1;
	public static final int DISPLAY_S40 = 2;
	public static final int DISPLAY_ASHA311 = 3;
	public static final int DISPLAY_EQWERTY = 4;
	public static final int DISPLAY_ALBUM = 5;
	public static final int DISPLAY_E6 = 6;
	public static final int DISPLAY_X1 = 7;
	public static final int DISPLAY_UNDEFINED = -1;

	public static int checkdisplay() {
		short i = 0;
		if (canvas == null)
			return 0;
		width = (short) canvas.getWidth();
		height = (short) canvas.getHeight();
		ScrollableCanvas.vmeshautsa = (short) (height - 110);
		if (width != lwidth || height != lheight) {
			if (width == 360 && height == 640) {
				i = DISPLAY_PORTRAIT;
			} else if (width == 240) {
				if (height == 320) {
					i = DISPLAY_S40;
				} else if (height == 400) {
					i = DISPLAY_ASHA311;
				}
			} else if (width == 320) {
				if (height == 240) {
					ScrollableCanvas.vmeshautsa = 90;
					i = DISPLAY_EQWERTY;
				}
			} else if (width == 640) {
				if (height == 360) {
					i = DISPLAY_ALBUM;
				} else if (height == 480) {
					i = DISPLAY_E6;
				}
			} else
				i = DISPLAY_UNDEFINED;
		}

		lwidth = width;
		lheight = height;
		if (i != 0)
			idispi = i;
		compact = DisplayUtils.height <= 240; // ВСЁ. Вот и вся лапша. Потом
												// постепенно примотаю к этому
												// флагу снижение высоты айтемов
												// и т.п.
		if (compact) {
			ScrollableCanvas.oneitemheight = 36;
		}
		return i;
	}

	public static String getCanvasString() {
		return "DEBUG";
	}

}
