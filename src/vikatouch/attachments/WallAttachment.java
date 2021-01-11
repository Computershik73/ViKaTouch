package vikatouch.attachments;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.locale.TextLocal;
import vikatouch.screens.NewsScreen;

/**
 * @author Shinovon
 * 
 */
public class WallAttachment extends Attachment {
	public void parseJSON() {
		this.type = "wall";
		title = TextLocal.inst.get("msg.attach.wall");
	}

	public static String title = "";

	public int getDrawHeight() {
		return 30;
	}

	public void press() {
		NewsScreen ns = new NewsScreen();
		ns.loadAtt(this);
		VikaTouch.setDisplay(ns, 1);
	}

	public void draw(Graphics g, int x1, int y1, int w) {
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(x1, y1, w, getDrawHeight());
		g.drawImage(IconsManager.ico[IconsManager.NEWS], x1 + 4, y1 + 3, 0);
		ColorUtils.setcolor(g, ColorUtils.COLOR1);
		Font f = Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_SMALL);
		g.setFont(f);
		g.drawString(title, x1 + 34, y1 + 15 - f.getHeight() / 2, 0);
	}

}
