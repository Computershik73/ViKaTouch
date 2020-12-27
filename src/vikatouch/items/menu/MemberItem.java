package vikatouch.items.menu;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.items.JSONUIItem;
import vikatouch.screens.page.GroupPageScreen;
import vikatouch.screens.page.ProfilePageScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.ResizeUtils;
import vikatouch.utils.VikaUtils;

public class MemberItem extends JSONUIItem {
	private String name;
	//private String link;
	private int id;
	private Image ava = null;
	//private int lastSeen;
	private boolean online;
	private JSONArray profiles;
	private String avaurl;
	private JSONArray groups;

	public static final int BORDER = 1;

	public MemberItem(int id, JSONArray profiles, JSONArray groups) {
		super(null);
		this.profiles = profiles;
		itemDrawHeight = 52;
		ava = VikaTouch.cameraImg;
		this.id = id;
		name = "id" + id;
		this.groups = groups;
	}

	public void parseJSON() {
		if(id < 0 && groups != null) {
			for(int i = 0; i < groups.length(); i++) {
				try {
					if(groups.getJSONObject(i).getInt("id") == -id) {
						JSONObject json = groups.getJSONObject(i);
						name = json.optString("name");
						if (!Settings.dontLoadAvas)
							avaurl = fixJSONString(json.optString("photo_50"));
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			for(int i = 0; i < profiles.length(); i++) {
				try {
					if(profiles.getJSONObject(i).getInt("id") == id) {
						JSONObject json = profiles.getJSONObject(i);
						name = json.optString("first_name") + " " + json.optString("last_name");
						online = json.optInt("online") == 1;
						if (!Settings.dontLoadAvas)
							avaurl = fixJSONString(json.optString("photo_50"));
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		setDrawHeight();
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
				ava = ResizeUtils.resizeItemPreview(VikaUtils.downloadImage(avaurl));
		} catch (InterruptedException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void paint(Graphics g, int y, int scrolled) {
		if (selected) {
			ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
			g.fillRect(0, y, DisplayUtils.width, itemDrawHeight);
		}
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		if (name != null)
			g.drawString(name, 73, y, 0);
		ColorUtils.setcolor(g, 6);

		if (ava != null) {
			g.drawImage(ava, 14, y + BORDER, 0);
		} else {
			g.fillRect(14, y + BORDER, 50, 50);
		}
		g.drawImage(selected ? IconsManager.acs : IconsManager.ac, 14, y + BORDER, 0);
		if (online) {
			ColorUtils.setcolor(g, ColorUtils.ONLINE);
			g.fillArc(52, y + itemDrawHeight - 16, 11, 11, 0, 360);
		}
	}

	public void tap(int x, int y) {
		if(id < 0) {
			try {
				VikaTouch.setDisplay(new GroupPageScreen(-id), 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				VikaTouch.setDisplay(new ProfilePageScreen(id), 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void keyPressed(int key) {
		if (key == KEY_OK)
			tap(20, 20);
	}
}
