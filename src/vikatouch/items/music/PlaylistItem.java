package vikatouch.items.music;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import vikatouch.VikaTouch;
import vikatouch.items.JSONItem;
import vikatouch.items.JSONUIItem;
import vikatouch.screens.music.MusicScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;

/**
 * @author Feodor0090
 * 
 */
public class PlaylistItem extends JSONItem {
	public int id;
	public int owner_id;
	public String name;
	private String iconUrl;
	private Image iconImg;
	private int size;
	private String bigCoverUrl;

	public PlaylistItem(JSONObject json) {
		super(json);
		setDrawHeight();
	}
	
	public PlaylistItem(String namee, int sizee, int ownerid, int idd, String iconUrll, String bigCoverUrll) {
		name = namee;
		size = sizee;
		owner_id = ownerid;
		id = idd;
		iconUrl = iconUrll;
		bigCoverUrl = bigCoverUrll;
		setDrawHeight();
	}

	public void parseJSON() {
		System.out.println(json.toString());

		try {
			name = json.optString("title");
			size = json.optInt("count");
			owner_id = json.optInt("owner_id");
			id = json.optInt("id");
			iconUrl = fixJSONString(json.getJSONObject("photo").optString("photo_135"));
			bigCoverUrl = fixJSONString(json.getJSONObject("photo").optString("photo_600"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		setDrawHeight();
		System.gc();
	}

	private void setDrawHeight() {
		itemDrawHeight = 102;
	}

	public void paint(Graphics g, int y, int scrolled) {
		if (iconImg == null) {
			ColorUtils.setcolor(g, 6);
			g.fillRect(1, y + 1, 100, 100);
		} else {
			g.drawImage(iconImg, 1, y + 1, 0);
		}

		ColorUtils.setcolor(g, (ScrollableCanvas.keysMode && selected) ? ColorUtils.BUTTONCOLOR : ColorUtils.TEXT);
		g.setFont(Font.getFont(0, 0, Font.SIZE_MEDIUM));
		if (name != null)
			g.drawString(name, 102, y + 12, 0);
		ColorUtils.setcolor(g, ColorUtils.OUTLINE);
		g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
		g.drawString(size + " аудиозаписей", 102, y + 46, 0);
		VikaTouch.needstoRedraw=true;
	}

	private Image getIcon() {
		Image img = null;
		try {
			if ((!Settings.dontLoadAvas) && (iconUrl!=null))
				img = VikaUtils.resize(VikaUtils.downloadImage(iconUrl), 100, 100);
		} catch (Exception e) {

		}
		return img;
	}

	public void loadIcon() {
		iconImg = getIcon();
	}

	public void open() {
		//VikaTouch.sendLog(String.valueOf(id));
		MusicScreen pls = new MusicScreen();
		pls.load(owner_id, id, name);
		pls.coverUrl = bigCoverUrl;
		VikaTouch.setDisplay(pls, 1);
	}

	public void tap(int x, int y) {
		open();
	}

	public void keyPress(int key) {
		if (key == KEY_OK) {
			open();
		}
	}
}
