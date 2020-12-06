package vikatouch.screens.menu;

import javax.microedition.lcdui.Graphics;

import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;

public class PhotosScreen extends MainScreen {
	private String title;

	public PhotosScreen(int owner, int album) {
		title = TextLocal.inst.get("title.photos");
	}

	public void draw(Graphics g) {
		drawHUD(g, title);
	}

}
