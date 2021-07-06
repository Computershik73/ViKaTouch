package ru.nnproject.vikaui.menu.items;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;

/**
 * @author Feodor0090
 * 
 */
public class OptionItem implements PressableUIItem {

	public IMenu menu;
	public String text;
	public int icon;
	public int h;
	public int i;
	boolean s;
	static Font f;
	static Font sf;

	public int drawX;
	public int fillW; // for context menu

	public OptionItem(IMenu m, String t, int ic, int i, int h) {
		VikaTouch.needstoRedraw=true;
		this.h = h;
		this.i = i;
		icon = ic;
		text = t;
		menu = m;
		// ворчали что слишком крупно. Согласен. Ты кстати тоже, это я медиум
		// пихнул, до
		// меня смалл был
		f = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		sf = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
	}

	public void paint(Graphics g, int y, int scrolled) {
		/*
		 * if(ScrollableCanvas.keysMode && s) { ColorUtils.setcolor(g,
		 * ColorUtils.BUTTONCOLOR); g.fillRect(drawX, y,
		 * fillW==0?DisplayUtils.width:fillW, h); }
		 */
		ColorUtils.setcolor(g, (ScrollableCanvas.keysMode && s) ? ColorUtils.BUTTONCOLOR : ColorUtils.TEXT);
		g.setFont((ScrollableCanvas.keysMode && s) ? sf : f);
		int x = drawX + 48;
		if (icon == -1)
			x = x - 40;
		if (text != null)
			g.drawString(text, x, y + ((h / 2) - (((ScrollableCanvas.keysMode && s) ? sf : f).getHeight() / 2)), 0);
		if (icon != -1)
			g.drawImage(((ScrollableCanvas.keysMode && s) ? (IconsManager.selIco) : (IconsManager.ico))[icon],
					drawX + 12, y + (h / 2 - 12), 0);
	}

	public int getDrawHeight() {
		return h;
	}

	public void tap(int x, int y) {
		menu.onMenuItemPress(i);
	}

	public void keyPress(int key) {
		if (key == KEY_OK)
			menu.onMenuItemPress(i);
	}

	public boolean isSelected() {
		return s;
	}

	public void setSelected(boolean selected) {
		VikaTouch.needstoRedraw=true;
		s = selected;
	}

	public void addDrawHeight(int i) {
		h += i;
	}

	public void setDrawHeight(int i) {
		h = i;
	}

}
