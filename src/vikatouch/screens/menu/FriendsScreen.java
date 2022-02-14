package vikatouch.screens.menu;

import java.util.Vector;

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
		} catch (Throwable e) {
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
	
	//"https://api.vk.com/method/execute?code=var%20requests%20%3D%20API.friends.getRequests%28%7B%22sort%22%3A%220%22%2C%22offset%22%3A0%2C%22out%22%3A0%2C%22count%22%3A100%7D%29%3Breturn%20%7Brequests_users%3AAPI.users.get%28%7Buser_ids%3Arequests.items%2Cfields%3A%22photo_50%22%7D%29%2Crequests%3Arequests.count%2C%7D%3B&access_token=bc84aeb68ef499f6ae83485a6fe3f99eb0a90ad3252b7926a9ae36994a612029134238ced39b9a6a2c3bc&v=5.57&lang=ru";

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
					try {
					Thread.sleep(500);
					} catch (Throwable ee) {}
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
						//VikaTouch.sendLog(x);
					}
					try {
						VikaTouch.loading = true;
						JSONObject response = new JSONObject(x).getJSONObject("response");
						JSONArray items = response.getJSONArray("items");
						totalItems = response.getInt("count");
						itemsCount = (short) items.length();
						canLoadMore = totalItems > from + Settings.simpleListsLength;
						uiItems = new Vector(itemsCount + (canLoadMore ? 1 : 0));
						for (int i = 0; i < itemsCount; i++) {
							try {
							VikaTouch.loading = true;
							JSONObject item = items.getJSONObject(i);
							FriendItem fi = new FriendItem(item);
							uiItems.addElement(fi);
							fi.parseJSON();
							} catch (Throwable eee) {}
							//Thread.yield();
						}
						range = " (" + (from + 1) + "-" + (itemsCount + from) + ")";
						if (canLoadMore) {
							uiItems.addElement(new LoadMoreButtonItem(FriendsScreen.this));
							itemsCount++;
						}
						if (keysMode) {
							currentItem = 0;
							((PressableUIItem) uiItems.elementAt(0)).setSelected(true);
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

						//repaint();
						Thread.sleep(1000); // ну вдруг юзер уже нажмёт? Зачем зря грузить
						VikaTouch.loading = true;
						if (!Settings.dontLoadAvas) {
							for (int i = 0; i < uiItems.size(); i++) {
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
								((FriendItem) ((PressableUIItem) uiItems.elementAt(i))).getAva();
								Thread.yield();
							}
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
	
	public void loadRequests(final int from, final boolean neww) {
		formattedTitle = TextLocal.inst.get("title.newrequests") + " ("+TextLocal.inst.get("title2.loading")+")";
		scrolled = 0;
		uiItems = null;
		fromF = from;
		

		abortLoading();

		downloaderThread = new Thread() {
			public void run() {
				try {
					// System.out.println("Friends list");
					VikaTouch.loading = true;
					repaint();
					String x;
					
						// как друзья
						x = VikaUtils
								.download(VikaTouch.API+"/method/execute?code=var%20requests%20%3D%20API.friends.getRequests%28%7B%22sort%22%3A%220%22%2C%22offset%22%3A0%2C%22out%22%3A0%2C%22count%22%3A1000%7D%29%3Breturn%20%7Brequests_users%3AAPI.users.get%28%7Buser_ids%3Arequests.items%2Cfields%3A%22photo_50%22%7D%29%2Crequests%3Arequests.count%2C%7D%3B&access_token="+VikaTouch.accessToken+"&v=5.57");
						//VikaTouch.sendLog(x);
					
					try {
						
						VikaTouch.needstoRedraw=true;
						VikaTouch.canvas.serviceRepaints();
						if (neww) {
						JSONObject response = new JSONObject(x).getJSONObject("response");
						JSONArray items = response.getJSONArray("requests_users");
						totalItems = response.getInt("requests");
						itemsCount = (short) items.length();
						canLoadMore = totalItems > from + Settings.simpleListsLength;
						uiItems = new Vector(itemsCount + (canLoadMore ? 1 : 0));
						if (itemsCount>0) {
						for (int i = 0; i < itemsCount; i++) {
							VikaTouch.loading = true;
							JSONObject item = items.getJSONObject(i);
							FriendItem fi = new FriendItem(item);
							uiItems.addElement(fi);
							fi.parseJSON();
							Thread.yield();
						}
						range = " (" + (from + 1) + "-" + (itemsCount + from) + ")";
						if (canLoadMore) {
							uiItems.addElement(new LoadMoreButtonItem(FriendsScreen.this));
							itemsCount++;
						}
						if (keysMode) {
							currentItem = 0;
							((PressableUIItem) uiItems.elementAt(0)).setSelected(true);
						}
						VikaTouch.loading = true;
						
						
							formattedTitle = TextLocal.inst.get("title.newrequests") + " (" + String.valueOf(totalItems)+")";
						

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
								((FriendItem) uiItems.elementAt(i)).getAva();
							}
						}
						} else {
							formattedTitle = TextLocal.inst.get("title.newrequests") + " (0) ";
							Thread.sleep(1000); // ну вдруг юзер уже нажмёт? Зачем зря грузить
							VikaTouch.loading = true;
							repaint();
						}
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
						FriendsScreen friendsScr = new FriendsScreen();
						friendsScr.loadRequests(0, true);
						VikaTouch.setDisplay(friendsScr, 1);
						
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
					for (int i = 0; i < uiItems.size(); i++) {
						if (((PressableUIItem) uiItems.elementAt(i)) != null) {
							((PressableUIItem) uiItems.elementAt(i)).paint(g, y, scrolled);
							y += ((PressableUIItem) uiItems.elementAt(i)).getDrawHeight();
						}
						Thread.yield();
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
		VikaTouch.canvas.serviceRepaints();
		try {
			if (y > topPanelH && y < DisplayUtils.height - bottomPanelH) {
				int h = ((PressableUIItem) uiItems.elementAt(0)).getDrawHeight();
				int yy1 = y - (scrolled + topPanelH);
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
		super.release(x, y);
	}

	public void loadNext() {
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
		loadFriends(fromF + Settings.simpleListsLength, currId, whose, name2, false);
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
	}

	
}
