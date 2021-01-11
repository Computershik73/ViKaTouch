package vikatouch.screens;

import javax.microedition.lcdui.Graphics;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.attachments.WallAttachment;
import vikatouch.items.LoadMoreButtonItem;
import vikatouch.items.PostItem;
import vikatouch.json.INextLoadable;
import vikatouch.locale.TextLocal;
import vikatouch.screens.menu.MenuScreen;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;

/**
 * @author Shinovon
 * 
 */
public class NewsScreen extends MainScreen implements INextLoadable {

	public static JSONArray profiles;
	public static JSONArray groups;
	public String titleStr;

	public int newsSource = 0;
	public boolean fromAtt = false;

	public String startPost = null;
	public int offset = 0;

	static final int count = 15;

	public NewsScreen() {
		super();
		titleStr = TextLocal.inst.get("title.newsfeed");

		VikaTouch.loading = true;

		if (VikaTouch.menuScr == null)
			VikaTouch.menuScr = new MenuScreen();

		scrollWithKeys = true;
	}

	public void loadPosts() {
		loadPosts(true);
	}

	public void loadPosts(final boolean fromLatest) {
		if (fromLatest)
			offset = 0;
		new Thread() {
			public void run() {
				VikaTouch.loading = true;

				int step = 0;
				try {
					URLBuilder url;
					fromAtt = false;
					if (newsSource == 0) {
						url = new URLBuilder("newsfeed.get").addField("filters", "post").addField("count", count)
								.addField("fields", "groups,profiles,items");
					} else {
						url = new URLBuilder("wall.get").addField("filter", "all").addField("extended", 1)
								.addField("count", count).addField("owner_id", newsSource);
					}
					if (!fromLatest && startPost != null) {
						if (newsSource == 0) {
							url = url.addField("start_from", startPost);
						} else {
							url.addField("offset", offset);
						}
					}
					hasBackButton = newsSource != 0;

					step = 1;
					final String s = VikaUtils.download(url);
					// VikaTouch.sendLog(url.toString());
					// VikaTouch.sendLog(newsSource+" "+(s.length()>210?s.substring(0, 200):s));
					VikaTouch.loading = true;
					JSONObject response = null;
					try {
						response = new JSONObject(s).getJSONObject("response");
					} catch (JSONException e) {
						VikaTouch.popup(new InfoPopup(s, null));
						return;
					}
					step = 2;
					JSONArray items = response.getJSONArray("items");
					step = 3;
					int itemsCount = items.length();
					uiItems = new PressableUIItem[itemsCount + 1];
					step = 4;
					uiItems[itemsCount] = new LoadMoreButtonItem(NewsScreen.this);
					step = 5;
					profiles = response.getJSONArray("profiles");
					groups = response.getJSONArray("groups");
					step = 6;
					startPost = response.optString("next_from", null);

					itemsh = 0;
					for (int i = 0; i < itemsCount; i++) {
						VikaTouch.loading = true;
						JSONObject item = items.getJSONObject(i);
						JSONObject itemCopy;
						try {
							itemCopy = item.getJSONArray("copy_history").getJSONObject(0);
						} catch (RuntimeException e) {
							itemCopy = item;
						}
						uiItems[i] = new PostItem(itemCopy, item);
						((PostItem) uiItems[i]).parseJSON();
						Thread.sleep(20);
					}
					itemsCount++;
				} catch (InterruptedException e) {
					return;
				} catch (Exception e) {
					VikaTouch.sendLog("news fail step " + step);
					VikaTouch.error(e, ErrorCodes.NEWSPARSE);
				} catch (OutOfMemoryError me) {
					uiItems[0] = null;
					System.gc();
					VikaTouch.popup(new InfoPopup(TextLocal.inst.get("error.outofmem"), null));
				}
				// другим ошибкам разрешаем выпасть из потока
				VikaTouch.loading = false;
			}
		}.start();

		System.gc();
	}

	public void loadAtt(WallAttachment att) {
		fromAtt = true;
		VikaTouch.loading = true;
		try {
			hasBackButton = true;
			VikaTouch.loading = true;
			uiItems = new PostItem[1];

			itemsh = 0;
			VikaTouch.loading = true;
			JSONObject item = att.json;
			JSONObject itemCopy;
			try {
				itemCopy = item.getJSONArray("copy_history").getJSONObject(0);
			} catch (Exception e) {
				itemCopy = item;
			}
			uiItems[0] = new PostItem(itemCopy, item);
			((PostItem) uiItems[0]).parseJSON();
		} catch (Exception e) {
			VikaTouch.error(e, -ErrorCodes.NEWSPARSE);
			e.printStackTrace();
		}
		VikaTouch.loading = false;
	}

	protected final void callRefresh() {
		if (!fromAtt)
			loadPosts(true);
	}

	public void draw(Graphics g) {
		try {

			update(g);

			int y = topPanelH + 10;
			try {
				if (uiItems != null) {
					for (int i = 0; i < uiItems.length; i++) {
						if (uiItems[i] != null) {
							if (y + scrolled < DisplayUtils.height)
								uiItems[i].paint(g, y, scrolled);
							y += uiItems[i].getDrawHeight();
						}
					}

					itemsh = y + 50;
				}
			} catch (Exception e) {
				VikaTouch.error(e, ErrorCodes.NEWSPOSTSDRAW);
			}
			g.translate(0, -g.getTranslateY());
			/*
			 * g.setColor(0, 0, 0); g.fillRect(0, 60, 300, 25); g.setColor(200, 200, 200);
			 * g.drawString(scrlDbg, 0, 60, 0);
			 */
		} catch (Exception e) {
			VikaTouch.error(e, ErrorCodes.NEWSDRAW);
			e.printStackTrace();
		}
	}

	public final void drawHUD(Graphics g) {
		drawHUD(g, titleStr);
	}

	public final void release(int x, int y) {
		if (!dragging) {
			if (y > topPanelH && y < DisplayUtils.height - oneitemheight) {
				int yy = topPanelH + 10;
				for (int i = 0; i < uiItems.length; i++) {
					try {
						int y1 = scrolled + yy;
						int y2 = y1 + uiItems[i].getDrawHeight();
						yy += uiItems[i].getDrawHeight();
						if (y > y1 && y < y2) {
							// VikaTouch.sendLog(i+" x"+x+" y"+(y1-y));
							uiItems[i].tap(x, y - y1);
						}
					} catch (Exception e) {

					}
				}
			}
		}
		super.release(x, y);
	}

	protected void scrollHorizontally(int deltaX) {

	}

	public void loadNext() {
		if (newsSource != 0) {
			offset += count;
		}
		scrolled = 0;
		currentItem = 0;
		loadPosts(false);
	}

	public void onLeave() {
		profiles = null;
		groups = null;
		VikaTouch.newsScr = null;
	}

}
