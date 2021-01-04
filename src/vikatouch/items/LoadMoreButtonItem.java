package vikatouch.items;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.json.INextLoadable;
import vikatouch.locale.TextLocal;

public class LoadMoreButtonItem implements PressableUIItem {

	public LoadMoreButtonItem(INextLoadable list) {
		text = TextLocal.inst.get("loadmore");
		l = list;
		// precalcs
		f = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
		h = f.getHeight();
		w = f.stringWidth(text);
	}

	private INextLoadable l;
	private int w;
	private int h;
	private Font f;

	public boolean selected;

	public static String text = "Загрузить ещё...";

	public void paint(Graphics g, int y, int scrolled) {
		if (ScrollableCanvas.keysMode && selected) {
			ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
			g.fillRect(0, y, DisplayUtils.width, 40);
		}
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.setFont(f);
		g.drawString(text, DisplayUtils.width / 2 - (w / 2), y + (20 - (h / 2)), 0);
	}

	public int getDrawHeight() {
		return 40;
	}

	public void tap(int x, int y) {
		l.loadNext();
	}

	public void keyPress(int key) {
		if (key == KEY_OK) {
			l.loadNext();
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void addDrawHeight(int i) {

	}

	public void setDrawHeight(int i) {

	}

}
