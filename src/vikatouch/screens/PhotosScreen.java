package vikatouch.screens;

import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.utils.DisplayUtils;
import tube42.lib.imagelib.ImageUtils;
import vikatouch.VikaTouch;
import vikatouch.locale.TextLocal;

public class PhotosScreen extends MainScreen {
	private String title;

	public PhotosScreen(int owner, int album) {
		title = TextLocal.inst.get("title.photos");
		hasBackButton = true;
	}

	public void draw(Graphics g) {
		int size = 72;
		int count = 17;
		update(g);
		int y = 0;
		int z = 0; 
		int x = 0;
		boolean set = false;
		for(x = 0; x < count; x++) {
			if((x + 1 - (z*y)) * size <= DisplayUtils.width) {
				g.drawImage(ImageUtils.resize(VikaTouch.cameraImg, size, size, false, false), (x - (z*y)) * size, topPanelH + y * size, 0);
			} else {
				if(!set)
					z = x;
				set = true;
				y++;
				g.drawImage(ImageUtils.resize(VikaTouch.cameraImg, size, size, false, false), (x - (z*y)) * size, topPanelH + y * size, 0);
			}
		}
		itemsh = topPanelH + y * size;
		g.translate(0, -g.getTranslateY());
	}
	
	public final void drawHUD(Graphics g) {
		drawHUD(g, title);
	}

}
