package vikatouch.screens.music;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.items.music.PlaylistItem;
import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;

/**
 * @author Feodor0090
 * 
 */
public class PlaylistsScreen extends MainScreen {

	private static String plStr;

	public PlaylistsScreen() {
		super();
		plStr = TextLocal.inst.get("title.playlists");
	}

	public int currId;
	public Thread downloaderThread;

	public String title = null;

	public void load(final int id, final String title) {
		this.title = title;
		scrolled = 0;
		uiItems = null;
		currId = id;
		if (downloaderThread != null && downloaderThread.isAlive()) {
			downloaderThread.interrupt();
		}
		downloaderThread = null;

		downloaderThread = new Thread() {
			public void run() {
				try {
					VikaTouch.loading = true;
					String x = VikaUtils.music(new URLBuilder("audio.getPlaylists").addField("owner_id", id)
							.addField("count", 100).addField("offset", 0).toString());
					try {
						System.out.println(x);
						VikaTouch.loading = true;
						JSONObject response = new JSONObject(x).getJSONObject("response");
						JSONArray items = response.getJSONArray("items");
						itemsCount = (short) items.length();
						uiItems = new PressableUIItem[itemsCount];
						for (int i = 0; i < itemsCount; i++) {
							JSONObject item = items.getJSONObject(i);
							uiItems[i] = new PlaylistItem(item);
							((PlaylistItem) uiItems[i]).parseJSON();
						}
					} catch (JSONException e) {
						e.printStackTrace();
						VikaTouch.error(e, ErrorCodes.PLAYLISTSPARSE);
					}
					VikaTouch.loading = true;
					repaint();
					Thread.sleep(1000);
					VikaTouch.loading = true;
					for (int i = 0; i < itemsCount; i++) {
						if (!(VikaTouch.canvas.currentScreen instanceof PlaylistsScreen)) {
							VikaTouch.loading = false;
							return;
						}
						VikaTouch.loading = true;
						((PlaylistItem) uiItems[i]).loadIcon();
					}
					VikaTouch.loading = false;
				} catch (InterruptedException e) {
					return;
				} catch (Throwable e) {
					VikaTouch.error(e, ErrorCodes.PLAYLISTSLOAD);
				}
				VikaTouch.loading = false;
				System.gc();
			}
		};
		hasBackButton = true;
		downloaderThread.start();
	}

	public void draw(Graphics g) {
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.setFont(Font.getFont(0, 0, 8));
		try {
			update(g);
			int y = topPanelH;
			try {
				if (uiItems != null) {
					for (int i = 0; i < itemsCount; i++) {
						if (uiItems[i] != null) {
							uiItems[i].paint(g, y, scrolled);
							y += uiItems[i].getDrawHeight();
						}
					}
					itemsh = y + 100;
				}
			} catch (Exception e) {
				VikaTouch.error(e, ErrorCodes.DOCUMENTSITEMDRAW);
			}
			g.translate(0, -g.getTranslateY());

		} catch (Exception e) {
			VikaTouch.error(e, ErrorCodes.DOCUMENTSDRAW);
			e.printStackTrace();
		}
	}

	public final void drawHUD(Graphics g) {
		drawHUD(g, uiItems != null ? title : plStr);
	}

	public final void release(int x, int y) {
		try {
			if (y > topPanelH && y < DisplayUtils.height - bottomPanelH) {
				int h = 102;
				int yy1 = y - (scrolled + 58);
				int i = yy1 / h;
				if (i < 0)
					i = 0;
				if (!dragging) {
					if (downloaderThread != null && downloaderThread.isAlive()) {
						downloaderThread.interrupt();
					}
					downloaderThread = null;
					uiItems[i].tap(x, yy1 - (h * i));
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.release(x, y);
	}

}
