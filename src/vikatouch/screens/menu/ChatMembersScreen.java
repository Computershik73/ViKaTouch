package vikatouch.screens.menu;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;


import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.items.menu.MemberItem;
import vikatouch.screens.MainScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;

/**
 * @author Shinovon
 * 
 */
public class ChatMembersScreen extends MainScreen {
	/*
	 * private static String loadingStr;
	 * 
	 * private static String membersStr;
	 * 
	 * private static String friendsStr;
	 * 
	 * private static String peopleStr;
	 * 
	 * public FriendsScreen() { if (loadingStr == null) { loadingStr =
	 * TextLocal.inst.get("title.loading"); peopleStr =
	 * TextLocal.inst.get("title.people"); friendsStr =
	 * TextLocal.inst.get("title.friends"); membersStr =
	 * TextLocal.inst.get("title.members"); } }
	 */

	public boolean isReady() {
		return uiItems != null;
	}

	public static void abortLoading() {
		try {
			if (downloaderThread != null && downloaderThread.isAlive())
				downloaderThread.interrupt();
		} catch (Exception e) {
		}
	}

	public static Thread downloaderThread;

	public int currId;
	public int fromF;
	public String whose = null;
	public int totalItems;

	private String formattedTitle;

	public ChatMembersScreen(final int id, String title, int count) {
		VikaTouch.needstoRedraw=true;
		formattedTitle = title;
		scroll = 0;
		uiItems = null;
		currId = id;
		totalItems = count;
		abortLoading();

		downloaderThread = new Thread() {
			public void run() {
				try {
					repaint();
					String x = VikaUtils.download(new URLBuilder("messages.getConversationMembers")
							.addField("fields", "name,photo_50,online").addField("peer_id", id));
					try {
						JSONObject response = new JSONObject(x).getJSONObject("response");
						JSONArray items = response.getJSONArray("items");
						JSONArray profiles = response.getJSONArray("profiles");
						JSONArray groups = response.optJSONArray("groups");
						itemsCount = (short) items.length();
						uiItems = new Vector(itemsCount);
						for (int i = 0; i < itemsCount; i++) {

							JSONObject item = items.getJSONObject(i);
							//uiItems[i] = new MemberItem(item.getInt("member_id"), profiles, groups);
						//((MemberItem) uiItems[i]).parseJSON();
							
							MemberItem mi = new MemberItem(item.getInt("member_id"), profiles, groups);
							uiItems.addElement(mi);
							mi.parseJSON();
							Thread.yield();

						}
						if (keysMode) {
							currentItem = 0;
							((PressableUIItem) uiItems.elementAt(0)).setSelected(true);
						}

						repaint();
						if (!Settings.dontLoadAvas) {
							for (int i = 0; i < uiItems.size(); i++) {
								if(uiItems.elementAt(i) instanceof MemberItem)
									((MemberItem) ((PressableUIItem) uiItems.elementAt(i))).getAva();
								Thread.yield();
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
						VikaTouch.error(e, ErrorCodes.FRIENDSPARSE);
					} catch (NullPointerException e) {
						e.printStackTrace();
					}

				} catch (InterruptedException e) {
					return;
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
					VikaTouch.error(e, ErrorCodes.FRIENDSLOAD);
				}
			}
		};
		hasBackButton = true;

		downloaderThread.start();
	}

	public void draw(Graphics g) {
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.setFont(Font.getFont(0, 0, 8));
		listHeight = itemsCount * 52;
		try {
			update(g);
			int y = topPanelH;
			try {
				if (uiItems != null) {
					for (int i = 0; i < uiItems.size(); i++) {
						if (((PressableUIItem) uiItems.elementAt(i)) != null) {
							((PressableUIItem) uiItems.elementAt(i)).paint(g, y, scroll);
							y += ((PressableUIItem) uiItems.elementAt(i)).getDrawHeight();
						}
					}
				}
			} catch (Exception e) {
				VikaTouch.error(e, ErrorCodes.FRIENDSITEMDRAW);
			}
			g.translate(0, -g.getTranslateY());
		} catch (Exception e) {
			VikaTouch.error(e, ErrorCodes.FRIENDSDRAW);
			e.printStackTrace();
		}
	}

	public final void drawHUD(Graphics g) {
		// super.drawHUD(g, uiItems==null?peopleStr+"
		// ("+loadingStr+"...)":(currId<0?membersStr:friendsStr)/*+(range==null?"":range)*/+"
		// "+(whose==null?"":whose));
		super.drawHUD(g, formattedTitle);
	}

	public final void tap(int x, int y, int time) {
		VikaTouch.needstoRedraw=true;
		try {
			if (y > topPanelH && y < DisplayUtils.height - bottomPanelH) {
				int h = ((PressableUIItem) uiItems.elementAt(0)).getDrawHeight();
				int yy1 = y - (scroll + topPanelH);
				int i = yy1 / h;
				if (i < 0)
					i = 0;
				if (!dragging) {
					((PressableUIItem) uiItems.elementAt(i)).tap(x, yy1 - (h * i));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		super.tap(x, y, time);
	}
}
