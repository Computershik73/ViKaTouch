package vikatouch.screens.menu;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

//import ru.nnproject.shizaui.menu.items.DelayedPress;
import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;

import vikatouch.VikaTouch;
import vikatouch.items.LoadMoreButtonItem;
import vikatouch.items.menu.FriendItem;
import vikatouch.items.menu.GroupItem;
import vikatouch.json.INextLoadable;
import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;

/**
 * @author Shinovon
 * 
 */
public class GroupsScreen extends MainScreen implements INextLoadable {

	public GroupsScreen() {
		super();
		VikaTouch.loading = true;
		// groupsStr = TextLocal.inst.get("title.groups");
	}

	public boolean isReady() {
		return uiItems != null;
	}

	public static void abortLoading() {
		try {
			if (downloaderThread != null && downloaderThread.isAlive())
				downloaderThread.interrupt();
		} catch (Throwable e) {
		}
	}

	public static Thread downloaderThread;

	public int currId;
	public int fromG;
	public String range = null;
	public int totalItems;
	public boolean canLoadMore = true;

	// private String groupsStr;

	protected String formattedTitle;

	private String whose;

	private String name2;

	private long pressTime;

	/*public void load(final String s) {
		formattedTitle = TextLocal.inst.get("music.searchresult");
		scrolled = 0;
		uiItems = null;
		canLoadMore = false;

		downloaderThread = new Thread() {

			public void run() {
				try {
					VikaTouch.loading = true;
					String x = Util.download(new URLBuilder("groups.search").addField("extended", "1").addField("q", s)
							.addField("count", Settings.simpleListsLength).addField("fields", "members_count,counters"));
					VikaTouch.loading = true;
					JSONObject response = new JSONObject(x).optJSONObject("response");
					JSONArray items = response.optJSONArray("items");
					totalItems = response.getInt("count");
					itemsCount = (short) items.length();
					uiItems = new Vector(itemsCount);
					for (int i = 0; i < uiItems.size(); i++) {
						VikaTouch.loading = true;
						JSONObject item = items.optJSONObject(i);
						GroupItem gi = new GroupItem(item);
						uiItems.addElement(gi);
						gi.parseJSON();
						Thread.yield();
					}
					if (keysMode) {
						currentItem = 0;
						if (uiItems != null)
							((PressableUIItem) uiItems.elementAt(0)).setSelected(true);
					}
					VikaTouch.loading = true;
					//repaint();
					Thread.sleep(1000);
					VikaTouch.loading = true;
					if (!Settings.dontLoadAvas) {
						for (int i = 0; i < uiItems.size(); i++) {
							VikaTouch.loading = true;
							((GroupItem) ((PressableUIItem) uiItems.elementAt(i))).getAva();
							Thread.yield();
						}
					}
					VikaTouch.loading = false;
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					return;
				} catch (Throwable e) {
					e.printStackTrace();
				}
				VikaTouch.loading = false;
			}
		};
		hasBackButton = true;

		downloaderThread.start();
	}*/

	public void loadGroups(final int from, final int id, final String name1, final String name2) {
		formattedTitle = TextLocal.inst.get("title.groups");
		scrolled = 0;
		uiItems = null;
		fromG = from;
		currId = id;
		whose = name1;
		this.name2 = name2;

		abortLoading();

		downloaderThread = new Thread() {

			public void run() {
				try {
					VikaTouch.loading = true;
					String x = VikaUtils.download(new URLBuilder("groups.get").addField("extended", "1")
							.addField("count", Settings.simpleListsLength).addField("fields", "members_count,counters")
							.addField("user_id", id).addField("offset", from));
					VikaTouch.loading = true;
					JSONObject response = new JSONObject(x).optJSONObject("response");
					JSONArray items = response.optJSONArray("items");
					totalItems = response.getInt("count");
					itemsCount = (short) items.length();
					canLoadMore = totalItems > from + Settings.simpleListsLength;
					uiItems = new Vector(itemsCount + (canLoadMore ? 1 : 0));
					for (int i = 0; i < itemsCount; i++) {
						VikaTouch.loading = true;
						JSONObject item = items.optJSONObject(i);
						GroupItem gi = new GroupItem(item);
						uiItems.addElement(gi);
						gi.parseJSON();
						Thread.yield();
					}
					// err=String.valueOf(i)+" i2";
					range = " (" + (from + 1) + "-" + (itemsCount + from) + ")";
					// err=String.valueOf(i)+" i3";
					if (canLoadMore) {
						uiItems.addElement(new LoadMoreButtonItem(GroupsScreen.this));
						itemsCount++;
					}
					if (keysMode) {
						currentItem = 0;
						if (uiItems != null)
							((PressableUIItem) uiItems.elementAt(0)).setSelected(true);
					}
					VikaTouch.loading = true;
					String name = name1;
					if (name == null && name2 != null)
						name = name2;
					if (name == null || name2 == null)
						formattedTitle = TextLocal.inst.get("title.groups");
					else
						formattedTitle = TextLocal.inst.getFormatted("title.groupsw", new String[] { name, name2 });
					//repaint();
					Thread.sleep(1000);
					VikaTouch.loading = true;
					if (!Settings.dontLoadAvas) {
						for (int i = 0; i < uiItems.size(); i++) {
							VikaTouch.loading = true;
							((GroupItem) uiItems.elementAt(i)).getAva();
							VikaTouch.needstoRedraw=true;
							//Thread.yield();
						}
					}
					VikaTouch.loading = false;
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					return;
				} catch (Throwable e) {
					e.printStackTrace();
				}
				VikaTouch.loading = false;
			}
		};
		hasBackButton = true;

		downloaderThread.start();
	}

	public void draw(Graphics g) {
		if (uiItems == null) {
			return;
		}
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.setFont(Font.getFont(0, 0, 8));
		itemsh = itemsCount * 52;
		try {
			update(g);
			int y = topPanelH;

			if (uiItems != null) {
				for (int i = 0; i < uiItems.size(); i++) {
					try {
						if (uiItems.elementAt(i) != null) {
							((PressableUIItem) uiItems.elementAt(i)).paint(g, y, scrolled);
							y += ((PressableUIItem) uiItems.elementAt(i)).getDrawHeight();
						}
					} catch (Throwable e) {
						VikaTouch.error(e, ErrorCodes.GROUPSITEMDRAW);
					}

				}
			}

			g.translate(0, -g.getTranslateY());
		} catch (Throwable e) {
			VikaTouch.error(e, ErrorCodes.GROUPSDRAW);
			e.printStackTrace();
		}
	}

	public final void drawHUD(Graphics g) {
		drawHUD(g, formattedTitle);
	}
	
	public final void press(int x, int y) {
		pressTime = System.currentTimeMillis();
		super.press(x, y);
	}

	public final void release(int x, int y) {
		VikaTouch.needstoRedraw=true;
		try {
			if (y > topPanelH && y < DisplayUtils.height - bottomPanelH) {
				int h = 50;
				int yy1 = y - (scrolled + topPanelH);
				int i = yy1 / h;
				if (i < 0)
					i = 0;
				if (!dragging) {
					((PressableUIItem) uiItems.elementAt(i)).tap(x, yy1 - (h * i));
				}

			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// Всё нормально, просто тапнули ПОД последним элементом.
			// ты на что-то намекаешь?
			// Я? я ни на что, просто оно реально плюётся если тапнуть под последним. Ничего
			// не трогай, сломаем. (с) Feodor0090
			// ок че
		} catch (Throwable e) {
			e.printStackTrace();
		}
		super.release(x, y);
	}

	public void loadNext() {
		loadGroups(fromG + Settings.simpleListsLength, currId, whose, name2);
	}
}
