package vikatouch.screens.temp;

import java.util.Random;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import vikatouch.VikaTouch;
import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;

public class SplashScreen extends MainScreen {
	private Image logo;
	private String header;

	public static int currState = 0; // до 7

	private final int[] statesProgress = new int[] { 0, 5, 10, 20, 40, 60, 80, 100 };
	private String[] statesNames = new String[] { "Starting application", "Loading settings", "Loading text tables",
			"Preparing assets", "Autorization", "Loading profile data", "Pre-loading conversations", "Ready." };
	private String tipStr = "Tip";
	private String[] tip;
	private int tipL;

	public SplashScreen() {
		super();

		try {
			logo = Image.createImage("/vika256.png");
		} catch (Exception e) {
			logo = Image.createImage(1, 1);
		}

		header = "ViKa Touch " + VikaTouch.getRelease() + " v" + VikaTouch.getVersion();
	}

	public void draw(Graphics g) {
		int dw = DisplayUtils.width;
		int dh = DisplayUtils.height;
		int hdw = dw / 2;
		Font f = Font.getFont(0, 0, Font.SIZE_MEDIUM);
		g.setFont(f);
		int sy = (dh - (260 + 20 + 4 + f.getHeight() * 2)) / 2;
		if (dh >= 360) {
			try {
				g.drawImage(logo, hdw - 128, sy, 0);
				ColorUtils.setcolor(g, ColorUtils.TEXT);
				g.drawString(header, hdw - f.stringWidth(header) / 2, sy + 260, 0);
				ColorUtils.setcolor(g, ColorUtils.COLOR1);
				g.fillRect(40, sy + 260 + f.getHeight() + 4, dw - 80, 16);
				ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
				g.fillRect(42, sy + 260 + f.getHeight() + 4 + 2, dw - 84, 12);
				ColorUtils.setcolor(g, ColorUtils.COLOR1);
				g.fillRect(43, sy + 260 + f.getHeight() + 4 + 3, (dw - 86) * statesProgress[currState] / 100, 10);
				ColorUtils.setcolor(g, ColorUtils.TEXT);
				g.drawString(statesNames[currState], hdw, sy + 260 + f.getHeight() + 4 + 20,
						Graphics.TOP | Graphics.HCENTER);

				// Подсказка
				if (tip != null && dh > 450) // толку то, всё равно не влазит.
				{
					g.drawString(tipStr, hdw - f.stringWidth(tipStr) / 2, sy + 260 + f.getHeight() * 3 + 20 + 4, 0);
					f = Font.getFont(0, 0, Font.SIZE_SMALL);
					g.setFont(f);
					for (int i = 0; i < tipL; i++) {
						g.drawString(tip[i], hdw, sy + 260 + 24 + f.getHeight() * (i + 5),
								Graphics.TOP | Graphics.HCENTER);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (logo != null)
				g.drawImage(logo, hdw - 128, dh / 2 - 128, 0);
			ColorUtils.setcolor(g, ColorUtils.COLOR1);
			g.fillRect(40, dh - 18, dw - 80, 16);
			g.setGrayScale(255);
			g.fillRect(42, dh - 16, dw - 84, 12);
			ColorUtils.setcolor(g, ColorUtils.COLOR1);
			g.fillRect(43, dh - 15, (dw - 86) * statesProgress[currState] / 100, 10);
		}
	}

	public void setText() {
		try {
			for (int i = 3; i <= 7; i++) {
				statesNames[i] = TextLocal.inst.get("splash.title." + i);
			}
			tipStr = TextLocal.inst.get("splash.tip");
			int tipsC = Integer.parseInt(TextLocal.inst.get("langinfo.hints"));
			Random r = new Random();
			int i = r.nextInt(tipsC);
			tip = TextBreaker.breakText(TextLocal.inst.get("splash.tip." + i), Font.getFont(0, 0, Font.SIZE_SMALL),
					DisplayUtils.width - 40);
			tipL = tip.length;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void drawHUD(Graphics g) {
	}

	public final void release(int x, int y) {

	}

	public void disp() {
		logo = null;
		statesNames = null;
	}

}
