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
	public static short width;
	public static short height;
	public static VikaCanvas canvas;

	public static boolean compact; // дисплеи <300 в высоту (е72, альбом
									// 240х320, портреты 220 и 208.

	public static void checkdisplay() {
		if (canvas == null)
			return;
		width = (short) canvas.getWidth();
		height = (short) canvas.getHeight();
		compact = DisplayUtils.height <= 240;
		if (compact)
			ScrollableCanvas.oneitemheight = 36;
	}

}
