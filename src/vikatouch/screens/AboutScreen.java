package vikatouch.screens;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.locale.TextLocal;

/**
 * @author Shinovon
 * 
 */
public class AboutScreen extends ScrollableCanvas {

	// private static final String[] strings;
	/*
	 * static { strings = new String[]{ "Издатель:", "Ilya Visotsky", "",
	 * "Разработчики:", "shinovon", "Feodor0090", "", "Бета-тестеры:",
	 * "nikitashustol", "bodyz", "mishivanov", "alias_omnia", "niceday", "avilolo",
	 * "linuxoid85", "", }; }
	 */

	private static String backStr;
	private static String publisherStr;
	private static String devsStr;
	// private static String teamStr;
	private String testersStr;

	public AboutScreen() {
		super();
		listHeight = 48 + (16 * 24) + 25;

		backStr = TextLocal.inst.get("back");

		devsStr = TextLocal.inst.get("about.devs");

		publisherStr = TextLocal.inst.get("about.vendor");

		testersStr = TextLocal.inst.get("about.testers");
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
		// teamStr = TextLocal.inst.get("about.team");
	}

	public void draw(Graphics g) {
		update(g);
		ColorUtils.setcolor(g, ColorUtils.TEXT);

		g.setFont(Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_LARGE));

		g.drawString("Vika Mobile", 8, 8, 0);

		g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));

		g.drawString("Версия: " + VikaTouch.getVersion(),
				Font.getFont(0, 0, Font.SIZE_LARGE).stringWidth("Vika Mobile") + 12, 20, 0);

		g.setFont(Font.getFont(0, 0, Font.SIZE_MEDIUM));

		/*
		 * g.drawString("Издатель:", 32, 48, 0); g.drawString("Ilya Visotsky", 32, 72,
		 * 0);
		 * 
		 * g.drawString("Разработчик:", 32, 120, 0); g.drawString("shinovon", 32, 144,
		 * 0);
		 * 
		 * g.drawString("Разработчик/Бета-тестер:", 32, 192, 0);
		 * g.drawString("Feodor0090", 32, 216, 0);
		 */
		// for(int i = 0; i < strings.length; i++)
		{
			g.drawString("Copyright:", 32, 48, 0);
			g.drawString("(c) 2023", 32, 48 + 24, 0);
			g.drawString(devsStr, 32, 48 + (3 * 24), 0);
			g.drawString("shinovon", 32, 48 + (4 * 24), 0);
			g.drawString("Feodor0090", 32, 48 + (5 * 24), 0);
			//g.drawString(publisherStr, 32, 48 + (7 * 24), 0);
			g.drawString("Ilya Vysotsky", 32, 48 + (6 * 24), 0);
		}

		g.translate(0, -g.getTranslateY());

		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(0, DisplayUtils.height - 25, 640, 25);

		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawString(backStr, 0, DisplayUtils.height - 24, 0);

	}

	public void tap(int x, int y, int time) {
		if (x < 50 && y > DisplayUtils.height - 30) {
			VikaTouch.setDisplay(VikaTouch.setsScr, -1);
			VikaTouch.needstoRedraw=true;
			VikaTouch.canvas.serviceRepaints();
		} else
			super.tap(x, y, time);
	}

	public void press(int key) {
		if (key == 8 || key == -6) {
			VikaTouch.setDisplay(VikaTouch.setsScr, -1);
			VikaTouch.needstoRedraw=true;
			VikaTouch.canvas.serviceRepaints();
		} else
			super.press(key);
	}

	protected void scrollHorizontally(int deltaX) {

	}

	public void scrollToSelected() {
		
	}

	public void selectCentered() {
		
	}

	protected void keysScroll(int dir) {
		
	}

}
