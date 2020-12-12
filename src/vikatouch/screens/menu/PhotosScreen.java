package vikatouch.screens.menu;

import javax.microedition.lcdui.Graphics;

import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;

public class PhotosScreen extends MainScreen {
	private String title;

	public PhotosScreen(int owner, int album) {
		title = TextLocal.inst.get("title.photos");
		hasBackButton = true;
	}

	public void draw(Graphics g) {
		update(g);

		g.translate(0, -g.getTranslateY());
	}
	
	public final void drawHUD(Graphics g) {
		drawHUD(g, title);
	}

}
