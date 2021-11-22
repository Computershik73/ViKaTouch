package vikatouch.screens.page;

import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.menu.items.OptionItem;
import ru.nnproject.vikaui.popup.ConfirmBox;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import vikatouch.Dialogs;
import vikatouch.VikaTouch;
import vikatouch.json.JSONBase;
import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;
import vikatouch.screens.NewsScreen;
import vikatouch.screens.menu.DocsScreen;
import vikatouch.screens.menu.FriendsScreen;
import vikatouch.screens.menu.VideosScreen;
import vikatouch.screens.music.MusicScreen;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;

/**
 * @author Feodor0090
 * 
 */
public class GroupPageScreen extends MainScreen implements IMenu {

	public int id;

	// group fields
	public String name;
	public String link;
	public String status;
	public boolean isMember;
	public boolean isAdmin;
	public int membersCount;
	public String[] description;
	public String site;
	public Image ava;
	public boolean canMsg;
	// counters
	public int docs;
	public int topics;
	public int photos;
	public int videos;
	public int music;

	// system
	private boolean isInfoShown = false;

	private String descEmptyStr;
	private String loadingStr;

	public Thread downloaderThread;

	public GroupPageScreen(int id) {
		VikaTouch.needstoRedraw=true;
		hasBackButton = true;
		this.id = id;
		load();
		descEmptyStr = TextLocal.inst.get("group.descriptionempty");
		loadingStr = TextLocal.inst.get("title2.loading");
	}

	public void load() {
		VikaTouch.needstoRedraw=true;
		if (downloaderThread != null && downloaderThread.isAlive()) {
		try {
			downloaderThread.interrupt();
		} catch (Throwable ee) {}
		}
		System.gc();
		downloaderThread = new Thread() {

			public void run() {
				try {
					VikaTouch.loading = true;
					String x = VikaUtils.download(new URLBuilder("groups.getById").addField("group_id", id).addField(
							"fields",
							"description,contacts,members_count,counters,status,links,fixed_post,site,ban_info,can_message"));
					try {
						VikaTouch.loading = true;
						JSONObject res = new JSONObject(x).getJSONArray("response").getJSONObject(0);
						name = res.optString("name");
						link = res.optString("screen_name");
						status = res.optString("status");
						isAdmin = res.optInt("is_admin") > 0;
						isMember = res.optInt("is_member") > 0;
						membersCount = res.optInt("members_count");
						canMsg = res.optInt("can_message") > 0;
						try {
							description = TextBreaker.breakText(res.optString("description"), false, null, true,
									DisplayUtils.width - 32);
						} catch (Exception e) {
							e.printStackTrace();
						}
						site = res.optString("site");

						JSONObject counters = res.getJSONObject("counters");
						docs = counters.optInt("docs");
						topics = counters.optInt("topics");
						music = counters.optInt("audios");
						videos = counters.optInt("videos");
						photos = counters.optInt("photos");

						try {
							ava = VikaUtils.downloadImage(JSONBase.fixJSONString(res.optString("photo_50")));
						} catch (Exception e) {
						}
						itemsCount = 13;
						int h = oneitemheight = (short) (DisplayUtils.compact ? 30 : 50);
						uiItems = new OptionItem[13];
						uiItems[0] = new OptionItem(GroupPageScreen.this,
								TextLocal.inst.get("menu.members") + " (" + membersCount + ")", IconsManager.GROUPS, 0,
								h);
						uiItems[1] = new OptionItem(GroupPageScreen.this,
								isMember ? TextLocal.inst.get("menu.grleave") : TextLocal.inst.get("menu.grjoin"),
								isMember ? IconsManager.CLOSE : IconsManager.ADD, 1, h);
						uiItems[2] = new OptionItem(GroupPageScreen.this,
								canMsg ? TextLocal.inst.get("menu.writemsg") : TextLocal.inst.get("menu.cannotwrite"),
								IconsManager.MSGS, 2, h);
						uiItems[3] = new OptionItem(GroupPageScreen.this, TextLocal.inst.get("menu.wall"),
								IconsManager.NEWS, 3, h);
						uiItems[4] = new OptionItem(GroupPageScreen.this, TextLocal.inst.get("menu.info"),
								IconsManager.INFO, 4, h);
						uiItems[5] = new OptionItem(GroupPageScreen.this,
								TextLocal.inst.get("menu.photos") + " (" + photos + ")", IconsManager.PHOTOS, 5, h);
						uiItems[6] = new OptionItem(GroupPageScreen.this,
								TextLocal.inst.get("menu.music") + " (" + music + ")", IconsManager.MUSIC, 6, h);
						uiItems[7] = new OptionItem(GroupPageScreen.this,
								TextLocal.inst.get("menu.videos") + " (" + videos + ")", IconsManager.VIDEOS, 7, h);
						uiItems[8] = new OptionItem(GroupPageScreen.this,
								TextLocal.inst.get("menu.documents") + " (" + docs + ")", IconsManager.DOCS, 8, h);
						uiItems[9] = new OptionItem(GroupPageScreen.this,
								TextLocal.inst.get("menu.discussions") + " (" + topics + ")", IconsManager.COMMENTS, 9,
								h);
						uiItems[10] = new OptionItem(GroupPageScreen.this,
								(site == null || site.length() < 5)
										? TextLocal.inst.get("menu.website") + ": "
												+ TextLocal.inst.get("menu.nowebsite")
										: TextLocal.inst.get("menu.website") + ": " + site,
								IconsManager.LINK, 10, h);
						uiItems[11] = new OptionItem(GroupPageScreen.this, TextLocal.inst.get("menu.links"),
								IconsManager.LINK, 11, h);
						uiItems[12] = new OptionItem(GroupPageScreen.this, TextLocal.inst.get("menu.contacts"),
								IconsManager.GROUPS, 11, h);
					} catch (JSONException e) {
						e.printStackTrace();
						VikaTouch.error(e, ErrorCodes.GROUPPAGEPARSE);
					}

					VikaTouch.loading = false;
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					return;
				} catch (Exception e) {
					e.printStackTrace();
					VikaTouch.error(e, ErrorCodes.GROUPPAGELOAD);
				}
				VikaTouch.loading = false;
				System.gc();
			}
		};

		downloaderThread.start();
		VikaTouch.needstoRedraw=true;
	}

	public void draw(Graphics g) {
		int y = topPanelH + 82; // init offset
		update(g);
		if (!DisplayUtils.compact) {
			ColorUtils.setcolor(g, -2);
			g.fillRect(0, 132, DisplayUtils.width, 8);
			ColorUtils.setcolor(g, -10);
			g.fillRect(0, 133, DisplayUtils.width, 1);
			ColorUtils.setcolor(g, -11);
			g.fillRect(0, 134, DisplayUtils.width, 1);
			ColorUtils.setcolor(g, -7);
			g.fillRect(0, 139, DisplayUtils.width, 1);
			ColorUtils.setcolor(g, -12);
			g.fillRect(0, 140, DisplayUtils.width, 1);
		}
		if (ava != null) {
			g.drawImage(ava, 16, topPanelH + 13, 0);
			g.drawImage(IconsManager.ac, 16, topPanelH + 13, 0);
		}
		itemsh = itemsCount * oneitemheight + y;
		g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawString(name == null ? loadingStr : name, 74, topPanelH + 16, 0);
		g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
		g.drawString(status == null ? "" : status, 74, topPanelH + 40, 0);

		ColorUtils.setcolor(g, -3);
		g.drawRect(0, 140, DisplayUtils.width, 50);
		if (isInfoShown) {
			if (description == null) {
				isInfoShown = false;
				((OptionItem) uiItems[4]).text = descEmptyStr;
			}
			Font df = Font.getFont(0, 0, 8);
			g.setFont(df);
			ColorUtils.setcolor(g, ColorUtils.TEXT);
			y += 16;
			for (int i = 0; i < description.length; i++) {
				if (description[i] != null) {
					g.drawString(description[i], 16, y, 0);
					y += df.getHeight();
				}
			}
		} else {
			if (uiItems != null) {
				for (int i = 0; i < uiItems.length; i++) {
					if (uiItems[i] != null) {
						uiItems[i].paint(g, y, scrolled);
						y += uiItems[i].getDrawHeight();
					}
				}
			}
		}
		g.translate(0, -g.getTranslateY());
	}

	public final void drawHUD(Graphics g) {
		drawHUD(g, link == null ? TextLocal.inst.get("group") : link);
	}

	public final void release(int x, int y) {
		VikaTouch.needstoRedraw=true;
		if (!dragging) {
			if (y > 58 && y < DisplayUtils.height - 50) {
				for (int i = 0; i < itemsCount; i++) {
					int y1 = scrolled + 140 + (i * oneitemheight);
					int y2 = y1 + oneitemheight;
					if (y > y1 && y < y2) {
						uiItems[i].tap(x, y - y1);
						break;
					}

				}
			}
		}
		super.release(x, y);
	}

	public void onMenuItemPress(int i) {
		VikaTouch.needstoRedraw=true;
		if (isInfoShown) {
			isInfoShown = false;
		} else {
			switch (i) {
			case 0:
				FriendsScreen fs = new FriendsScreen();
				VikaTouch.setDisplay(fs, 1);
				fs.loadFriends(0, -id, name, name, false);
				break;
			case 1:
				VikaTouch
						.popup(new ConfirmBox(
								isMember ? TextLocal.inst.get("menu.grleave") + "?"
										: TextLocal.inst.get("menu.grjoin") + "?",
								null,

								new Thread() {
									public void run() {
										try {
											if (isMember) {
												VikaUtils.download(
														new URLBuilder("groups.leave").addField("group_id", id));
											} else {
												VikaUtils.download(
														new URLBuilder("groups.join").addField("group_id", id));
											}
											load();
										} catch (InterruptedException e) {
											return;
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}, null, TextLocal.inst.get("ok"), TextLocal.inst.get("cancel")));
				break;
			case 2:
				// сообщение
				if (canMsg) {
					Dialogs.openDialog(-id, name, 0);
				}
				break;
			case 3:
				NewsScreen newsScr = new NewsScreen();
				newsScr.newsSource = -id;
				newsScr.titleStr = name;
				VikaTouch.setDisplay(newsScr, 1);
				newsScr.loadPosts();
				break;
			case 4:
				isInfoShown = true;
				break;
			case 6:
				MusicScreen.open(-id, name, name);
				break;
			case 7:
				if (videos > 0) {
					VideosScreen vs = new VideosScreen();
					VikaTouch.setDisplay(vs, 1);
					vs.load(0, -id, name, name);
				}
				break;
			case 8:
				if (docs > 0) {
					DocsScreen dc = new DocsScreen();
					VikaTouch.setDisplay(dc, 1);
					dc.loadDocs(0, -id, name, name);
				}
				break;
			case 10:
				try {
					VikaTouch.appInst.platformRequest(site);
				} catch (Exception e) {
				}
				break;
			}
		}
	}

	public void onMenuItemOption(int i) {

	}

}
