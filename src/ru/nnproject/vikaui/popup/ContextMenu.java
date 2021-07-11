// This file is part of VikaUI
// Copyright (C) 2020  Arman Jussuplaliyev (Shinovon)

package ru.nnproject.vikaui.popup;

import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.VikaCanvas;
import ru.nnproject.vikaui.menu.items.OptionItem;
import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;

/**
 * @author Feodor0090
 * 
 */
public class ContextMenu extends VikaNotice {

	public OptionItem[] items;
	public int selected = 0;
	private boolean dragging;

	public ContextMenu(OptionItem[] list) {
		VikaTouch.needstoRedraw=true;
		items = list;
		if (ScrollableCanvas.keysMode)
			items[selected].setSelected(true);
		VikaTouch.needstoRedraw=true;
	}

	public void draw(Graphics g) {
		VikaTouch.needstoRedraw=true;
		int itemsH = 16; // margin = 8
		int width = Math.min(DisplayUtils.width - 8, 350);
		int x = DisplayUtils.width / 2 - width / 2;
		for (int i = 0; i < items.length; i++) {
			items[i].drawX = x + 8;
			items[i].fillW = width - 16;
			itemsH = itemsH + items[i].getDrawHeight();
		}
		VikaTouch.needstoRedraw=true;
		int th = itemsH;
		int y = DisplayUtils.height / 2 - th / 2;

		// BG
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRoundRect(x, y, width, th, 16, 16);
		// border
		g.setStrokeStyle(Graphics.SOLID);
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawRoundRect(x, y, width, th, 16, 16);
		VikaTouch.needstoRedraw=true;
		int cy = 8 + y;
		for (int i = 0; i < items.length; i++) {
			items[i].paint(g, cy, 0);
			VikaTouch.needstoRedraw=true;
			cy = cy + items[i].getDrawHeight();
		}
		VikaTouch.needstoRedraw=true;
	}

	public void press(int key) {
		VikaTouch.needstoRedraw=true;
		ScrollableCanvas.keysMode = true;
		try {
			if (key == PressableUIItem.KEY_OK) {
				VikaCanvas.currentAlert = null;
				items[selected].keyPress(PressableUIItem.KEY_OK);
			} else if (key == -1) {
				items[selected].setSelected(false);
				selected--;
				if (selected < 0)
					selected = items.length - 1;
				items[selected].setSelected(true);
			} else if (key == -2) {
				items[selected].setSelected(false);
				selected++;
				if (selected >= items.length)
					selected = 0;
				items[selected].setSelected(true);
			} else if (key == PressableUIItem.KEY_RFUNC) {
				VikaCanvas.currentAlert = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		VikaTouch.needstoRedraw=true;
	}

	public void press(int x, int y) {
		VikaTouch.needstoRedraw=true;
		lastx = x;
		lasty = y;
		dragging = false;
		VikaTouch.needstoRedraw=true;
	}

	int lastx = 0, lasty = 0;

	public void drag(int x, int y) {
		//VikaTouch.needstoRedraw=true;
		if (Math.abs(x - lastx) > 3 || Math.abs(y - lasty) > 3) {
			dragging = true;
		}
		//VikaTouch.needstoRedraw=true;
	}

	public void release(int x, int y) {
		
		if (dragging)
			return;
		int margin = 8;
		int itemsH = margin * 2; // margin = 8
		int width = Math.min(DisplayUtils.width - 8, 350);
		int rx = DisplayUtils.width / 2 - width / 2;
		for (int i = 0; i < items.length; i++) {
			items[i].drawX = x + margin;
			items[i].fillW = width - margin * 2;
			itemsH = itemsH + items[i].getDrawHeight();
		}

		int th = itemsH;
		int ry = DisplayUtils.height / 2 - th / 2;

		if (x < rx || x > rx + width || y < ry || y > ry + th) {
			VikaCanvas.currentAlert = null;
			VikaTouch.needstoRedraw=true;
			return;
		}

		int tapY = y - ry;
		int currY = margin;
		for (int i = 0; i < items.length; i++) {
			int h = items[i].getDrawHeight();
			if (tapY > currY && tapY < currY + h) {
				VikaCanvas.currentAlert = null;
				VikaTouch.needstoRedraw=true;
				items[i].tap(x - rx, tapY - currY);
				return;
			}
			currY = currY + h;
		}
		//VikaTouch.needstoRedraw=true;
		//VikaTouch.needstoRedraw=true;
	}

}
