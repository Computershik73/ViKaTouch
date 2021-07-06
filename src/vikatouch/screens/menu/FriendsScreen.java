package vikatouch.screens.menu;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.EmptyMenu;
import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.menu.items.OptionItem;
import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.popup.ContextMenu;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.items.LoadMoreButtonItem;
import vikatouch.items.menu.FriendItem;
import vikatouch.json.INextLoadable;
import vikatouch.locale.TextLocal;
import vikatouch.music.MusicPlayer;
import vikatouch.screens.MainScreen;
import vikatouch.screens.music.MusicScreen;
import vikatouch.screens.music.PlaylistsScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.text.TextEditor;
import vikatouch.utils.url.URLBuilder;

/**
 * @author Shinovon
 * 
 */
public class FriendsScreen extends MainScreen implements INextLoadable {
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
	public String range = null;
	public int totalItems;
	public boolean canLoadMore = true;

	private String name2;

	private String formattedTitle;

	public void loadFriends(final int from, final int id, final String name1, final String name2, final boolean online) {
		formattedTitle = TextLocal.inst.get("title.people");
		scrolled = 0;
		uiItems = null;
		fromF = from;
		currId = id;

		abortLoading();

		downloaderThread = new Thread() {
			public void run() {
				try {
					// System.out.println("Friends list");
					VikaTouch.loading = true;
					repaint();
					String x;
					if (id < 0) {
						// как участники
						// !!! это дает ошибку! и я не знаю почему!
						// И какую же? У меня ни разу не падало.
						x = VikaUtils.download(
								new URLBuilder("groups.getMembers").addField("count", Settings.simpleListsLength)
										.addField("fields", "domain,last_seen,photo_50").addField("offset", from)
										.addField("group_id", -id));
					} else {
						// как друзья
						x = VikaUtils
								.download(new URLBuilder("friends.get"+ (online ? "Online" : "")).addField("count", Settings.simpleListsLength)
										.addField("fields", "domain,last_seen,photo_50").addField("offset", from)
										.addField("user_id", id).addField("order", "hints"));
						VikaTouch.sendLog(x);
					}
					try {
						
						VikaTouch.loading = true;
						if (!online) {
						JSONObject response = new JSONObject(x).getJSONObject("response");
						JSONArray items = response.getJSONArray("items");
						totalItems = response.getInt("count");
						itemsCount = (short) items.length();
						canLoadMore = totalItems > from + Settings.simpleListsLength;
						uiItems = new PressableUIItem[itemsCount + (canLoadMore ? 1 : 0)];
						for (int i = 0; i < itemsCount; i++) {
							VikaTouch.loading = true;
							JSONObject item = items.getJSONObject(i);
							uiItems[i] = new FriendItem(item);
							((FriendItem) uiItems[i]).parseJSON();
						}
						range = " (" + (from + 1) + "-" + (itemsCount + from) + ")";
						if (canLoadMore) {
							uiItems[itemsCount] = new LoadMoreButtonItem(FriendsScreen.this);
							itemsCount++;
						}
						if (keysMode) {
							currentItem = 0;
							uiItems[0].setSelected(true);
						}
						VikaTouch.loading = true;
						String name = name1;
						if (name == null && name2 != null)
							name = name2;

						if (name == null || name2 == null)
							formattedTitle = TextLocal.inst.get("title.friends");
						else
							formattedTitle = TextLocal.inst.getFormatted("title.friendsw",
									new String[] { name, name2 });

						repaint();
						Thread.sleep(1000); // ну вдруг юзер уже нажмёт? Зачем зря грузить
						VikaTouch.loading = true;
						if (!Settings.dontLoadAvas) {
							for (int i = 0; i < itemsCount - (canLoadMore ? 1 : 0); i++) {
								/*
								 * if(!this.isAlive()) { return; }
								 */
								if (!(VikaTouch.canvas.currentScreen instanceof FriendsScreen)) {
									VikaTouch.loading = false;
									return; // Костыль деревянный, 1 штука, 78 lvl, 6 ранг
									// не одобряю. для чего создали thread.isAlive()?
									// Он как-бы при закрытии экрана не стопается. Кстати, если он умер, то он и
									// проверить не сможет жив ли он
									// цикл будет продолжаться пока он не закончится.
								}
								VikaTouch.loading = true;
								((FriendItem) uiItems[i]).getAva();
							}
						}
						} else {
							JSONArray response = new JSONObject(x).getJSONArray("response");
							totalItems = response.length();
							itemsCount = (short) response.length();
							canLoadMore = totalItems > from + Settings.simpleListsLength;
							uiItems = new PressableUIItem[itemsCount + (canLoadMore ? 1 : 0)];
						}
						VikaTouch.loading = false;
					} catch (JSONException e) {
						e.printStackTrace();
						VikaTouch.error(e, ErrorCodes.FRIENDSPARSE);
					}

					VikaTouch.loading = false;
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					return;
				} catch (Exception e) {
					e.printStackTrace();
					VikaTouch.error(e, ErrorCodes.FRIENDSLOAD);
				}
				VikaTouch.loading = false;
			}
		};
		hasBackButton = true;

		downloaderThread.start();
	}
	
	
	public static void open(final int id, final String name, final String name2) {
		VikaTouch.needstoRedraw=true;
		IMenu m = new EmptyMenu() {
			public void onMenuItemPress(int i) {
				try {
					if (i == 0) {
						FriendsScreen friendsScr = new FriendsScreen();
						friendsScr.loadFriends(id == VikaTouch.integerUserId ? 0 : 0, VikaTouch.integerUserId, name, name2, false);
						VikaTouch.setDisplay(friendsScr, 1);
					} else if (i == 1) {
						FriendsScreen friendsScr = new FriendsScreen();
						friendsScr.loadFriends(id == VikaTouch.integerUserId ? 0 : 0, VikaTouch.integerUserId , name, name2, true);
						VikaTouch.setDisplay(friendsScr, 1);
					} else if (i == 2) {
						VikaTouch.popup(new InfoPopup(TextLocal.inst.get("popup.unrealized"), null));
					} else if (i == 3) {
						VikaTouch.popup(new InfoPopup(TextLocal.inst.get("popup.unrealized"), null));
			        } 
				} catch (Exception e) {
					VikaTouch.sendLog("Friends open: " + e.toString());
				}
			}
		};
		OptionItem[] oi = new OptionItem[id==VikaTouch.integerUserId ? 4 : 2];
		try {
			oi[0] = new OptionItem(m, TextLocal.inst.get("friends.all"), IconsManager.FRIENDS, 0, 50);
			oi[1] = new OptionItem(m, TextLocal.inst.get("friends.online"), IconsManager.FRIENDS, 1, 50);
			if (id==VikaTouch.integerUserId) {
			 oi[2] = new OptionItem(m, TextLocal.inst.get("friends.incoming"), IconsManager.FRIENDS, 2, 50);
			 oi[3] = new OptionItem(m, TextLocal.inst.get("friends.outcoming"), IconsManager.FRIENDS, 3, 50);
			}
				
			
		} catch (Exception e) {
		}

		VikaTouch.popup(new ContextMenu(oi));
	}

	public void draw(Graphics g) {
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.setFont(Font.getFont(0, 0, 8));
		itemsh = itemsCount * 52;
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

	public final void release(int x, int y) {
		VikaTouch.needstoRedraw=true;
		try {
			if (y > topPanelH && y < DisplayUtils.height - bottomPanelH) {
				int h = uiItems[0].getDrawHeight();
				int yy1 = y - (scrolled + topPanelH);
				int i = yy1 / h;
				if (i < 0)
					i = 0;
				if (!dragging) {
					uiItems[i].tap(x, yy1 - (h * i));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		super.release(x, y);
	}

	public void loadNext() {
		VikaTouch.needstoRedraw=true;
		loadFriends(fromF + Settings.simpleListsLength, currId, whose, name2, false);
	}

	
}
