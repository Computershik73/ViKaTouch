package ru.nnproject.vikaui.popup;

import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.menu.items.OptionItem;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;

public class AutoContextMenu extends ContextMenu {

	public AutoContextMenu(OptionItem[] list) {
		super(list);
	}

	public void draw(Graphics g) {
		int dh = DisplayUtils.height;
		int itemsH = 16; // margin = 8
		int width = Math.min(DisplayUtils.width - 8, 350);
		int x = DisplayUtils.width / 2 - width / 2;
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null)
				continue;
			items[i].drawX = x + 8;
			items[i].fillW = width - 16;
			itemsH = itemsH + items[i].getDrawHeight();
		}

		int th = itemsH;
		int y = dh / 2 - th / 2;

		if (th > dh - 20) {
			int ih = (dh - 20) / items.length;
			th = ih * items.length + 16;
			for (int i = 0; i < items.length; i++) {
				if (items[i] == null)
					continue;
				items[i].h = ih;
			}
		}

		// BG
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRoundRect(x, y, width, th, 16, 16);
		// border
		g.setStrokeStyle(Graphics.SOLID);
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawRoundRect(x, y, width, th, 16, 16);

		int cy = 8 + y;
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null)
				continue;
			items[i].paint(g, cy, 0);
			cy = cy + items[i].getDrawHeight();
		}
	}
}
