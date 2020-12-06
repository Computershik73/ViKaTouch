package vikatouch.screens.menu;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.items.LoadMoreButtonItem;
import vikatouch.items.menu.GroupItem;
import vikatouch.json.INextLoadable;
import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;

public class GroupsScreen extends MainScreen implements INextLoadable {

	public GroupsScreen() {
		super();
		VikaTouch.loading = true;
		//groupsStr = TextLocal.inst.get("title.groups");
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

	//private String groupsStr;

	protected String formattedTitle;

	private String whose;

	private String name2;

	public void loadGroups(final int from, final int id, final String name1, final String name2) {
		formattedTitle = TextLocal.inst.get("title.groups");;
		scrolled = 0;
		uiItems = null;
		fromG = from;
		currId = id;
		whose = name1;
		this.name2 = name2;

		abortLoading();

		downloaderThread = new Thread() {

			public void run() {
				String err = "0";
				try {
					VikaTouch.loading = true;
					String x = VikaUtils.download(new URLBuilder("groups.get").addField("extended", "1")
							.addField("count", Settings.simpleListsLength).addField("fields", "members_count,counters")
							.addField("user_id", id).addField("offset", from));
					err = "1";
					// VikaTouch.sendLog(x.substring(0, 700));
					// x = vikatouch.json.JSONBase.fixJSONString(x);
					err = "2";
					VikaTouch.loading = true;
					err = "3";
					// repaint();
					err = "4";
					JSONObject response = new JSONObject(x).optJSONObject("response");
					err = "5";
					JSONArray items = response.optJSONArray("items");
					err = "6";
					totalItems = response.getInt("count");
					err = "7";
					itemsCount = (short) items.length();
					err = "8";
					// System.out.println(totalItems + " - "+itemsCount);
					canLoadMore = totalItems > from + Settings.simpleListsLength;
					err = "9";
					uiItems = new PressableUIItem[itemsCount + (canLoadMore ? 1 : 0)];
					err = "10";
					for (int i = 0; i < itemsCount; i++) {
						// try
						// {
						err = String.valueOf(i) + " i";
						VikaTouch.loading = true;
						JSONObject item = items.optJSONObject(i);
						uiItems[i] = new GroupItem(item);
						((GroupItem) uiItems[i]).parseJSON();
						/*
						 * } catch (JSONException e) { e.printStackTrace();
						 * VikaTouch.sendLog(String.valueOf(i)+" "+ e.getMessage());
						 * //VikaTouch.error(e, ErrorCodes.GROUPSPARSE); } catch (Throwable e) {
						 * VikaTouch.sendLog(e.getMessage()); }
						 */
					}
					// err=String.valueOf(i)+" i2";
					range = " (" + (from + 1) + "-" + (itemsCount + from) + ")";
					// err=String.valueOf(i)+" i3";
					if (canLoadMore) {
						uiItems[itemsCount] = new LoadMoreButtonItem(GroupsScreen.this);
						itemsCount++;
					}
					err = "11";
					if (keysMode) {
						currentItem = 0;
						err = "12";
						if (uiItems != null)
							uiItems[0].setSelected(true);
						err = "13";
					}
					VikaTouch.loading = true;
					err = "14";
					String name = name1;
					if (name == null && name2 != null)
						name = name2;
					err = "15";
					if (name == null || name2 == null)
						formattedTitle = TextLocal.inst.get("title.groups");
					else
						formattedTitle = TextLocal.inst.getFormatted("title.groupsw", new String[] { name, name2 });
					err = "1";
					repaint();
					err = "16";
					Thread.sleep(1000);
					err = "17";
					VikaTouch.loading = true;
					if (!Settings.dontLoadAvas) {
						for (int i = 0; i < itemsCount - (canLoadMore ? 1 : 0); i++) {
							VikaTouch.loading = true;
							((GroupItem) uiItems[i]).getAva();
						}
					}
					VikaTouch.loading = false;
				} catch (NullPointerException e) {
					VikaTouch.sendLog(err + " null " + e.getMessage());
					e.printStackTrace();
				} catch (InterruptedException e) {
					VikaTouch.sendLog(err + " inter " + e.getMessage());
					e.printStackTrace();
				} catch (Throwable e) {
					e.printStackTrace();
					VikaTouch.sendLog(err + " th " + e.getMessage());
					// VikaTouch.error(e, ErrorCodes.GROUPSLOAD);
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
		ColorUtils.setcolor(g, 0);
		g.setFont(Font.getFont(0, 0, 8));
		itemsh = itemsCount * 52;
		try {
			update(g);
			int y = topPanelH;

			if (uiItems != null) {
				for (int i = 0; i < itemsCount; i++) {
					try {
						if (uiItems[i] != null) {
							uiItems[i].paint(g, y, scrolled);
							y += uiItems[i].getDrawHeight();
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

	public final void release(int x, int y) {
		try {
			if (y > topPanelH && y < DisplayUtils.height - bottomPanelH) {
				int h = 50;
				int yy1 = y - (scrolled + topPanelH);
				int i = yy1 / h;
				if (i < 0)
					i = 0;
				if (!dragging) {
					uiItems[i].tap(x, yy1 - (h * i));
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
