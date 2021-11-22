package vikatouch.items.menu;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.items.JSONUIItem;
import vikatouch.screens.page.ProfilePageScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.ResizeUtils;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;

/**
 * @author Shinovon
 * 
 */
public class FriendItem extends JSONUIItem {
	private String name;
	// private String link;
	private int id;
	private Image ava = null;
	// private int lastSeen;
	private boolean online;
	private String city;
	private int yrsOld;

	public static final int BORDER = 1;

	public FriendItem(JSONObject json) {
		super(json);
		itemDrawHeight = 52;
		ava = VikaTouch.cameraImg;
	}

	public void parseJSON() {
		try {
			name = json.optString("first_name") + " " + json.optString("last_name");
			// link = json.optString("domain");
			id = json.optInt("id");
			/*
			 * try { lastSeen = json.getJSONObject("last_seen").optInt("time");
			 * } catch (Exception e) {
			 * 
			 * }
			 */
			try {
				city = json.getJSONObject("city").optString("title");
			} catch (Throwable e) {

			}
			online = json.optInt("online") == 1;
			/*
			 * try { ava =
			 * VikaUtils.downloadImage(fixJSONString(json.optString("photo_50"))
			 * ); switch(DisplayUtils.idispi) { case DisplayUtils.DISPLAY_S40:
			 * case DisplayUtils.DISPLAY_ASHA311: case
			 * DisplayUtils.DISPLAY_EQWERTY: { ava = ResizeUtils.resizeava(ava);
			 * break; } default: break; } } catch (Exception e) {
			 * System.out.println("Юзер "+link+": ошибка аватарки");
			 * //System.out.println(json.toString()); }
			 */
		} catch (Throwable e) {
			e.printStackTrace();
			VikaTouch.error(e, ErrorCodes.FRIENDPARSE);
		}

		setDrawHeight();

		System.gc();
	}

	private void setDrawHeight() {
		itemDrawHeight = 52;
	}

	public int getDrawHeight() {
		return itemDrawHeight;
	}

	public void getAva() {
		try {
			if (!Settings.dontLoadAvas)
				ava = ResizeUtils.resizeItemPreview(VikaUtils.downloadImage(fixJSONString(json.optString("photo_50"))));
		} catch (InterruptedException e) {

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void paint(Graphics g, int y, int scrolled) {
		int tx = 4;
		if (ava != null) {
			g.drawImage(ava, 14, y + BORDER, 0);
			g.drawImage(IconsManager.ac, 14, y + BORDER, 0);
			if (online) {
				ColorUtils.setcolor(g, ColorUtils.ONLINE);
				g.fillArc(52, y + itemDrawHeight - 16, 11, 11, 0, 360);
			}
			tx = 73;
		}
		ColorUtils.setcolor(g, ColorUtils.TEXT);

		if (ScrollableCanvas.keysMode && selected) {
			ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
			g.drawRect(0, y, DisplayUtils.width - 1, itemDrawHeight);
			g.drawRect(1, y + 1, DisplayUtils.width - 3, itemDrawHeight - 2);
		}
		if (name != null)
			g.drawString(name, tx, y, 0);
		String descrS = city != null ? city : yrsOld != 0 ? yrsOld + " лет" : "";
		if (descrS != "" && descrS != null) {
			g.drawString(descrS, tx, y + 24, 0);
		}
	}

	public void tap(int x, int y) {
		try {
			VikaTouch.setDisplay(new ProfilePageScreen(id), 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void keyPress(int key) {
		if (key == KEY_OK)
			tap(20, 20);
	}
}
