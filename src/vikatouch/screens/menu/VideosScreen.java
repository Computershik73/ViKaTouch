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
import vikatouch.items.LoadMoreButtonItem;
import vikatouch.items.menu.VideoItem;
import vikatouch.json.INextLoadable;
import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;

/**
 * @author Feodor0090
 * 
 */
public class VideosScreen extends MainScreen implements INextLoadable {

	public VideosScreen() {
		VikaTouch.needstoRedraw = true;
		hasBackButton = true;
	}

	public int fromVid;
	public int currId;
	public static Thread downloaderThread;
	public String range = null;
	public boolean canLoadMore = true;
	private String whose;
	private String name2;
	protected String formattedTitle;
	private boolean loadingMore;
	protected int totalVids;

	public void load(final int from, final int id, final String name1, final String name2) {
		VikaTouch.needstoRedraw = true;
		scroll = 0;

		final int count = Settings.simpleListsLength;
		fromVid = from;
		currId = id;
		this.whose = name1;
		this.name2 = name2;
		if (downloaderThread != null && downloaderThread.isAlive())
			downloaderThread.interrupt();

		downloaderThread = new Thread() {
			public void run() {
				try {
					VikaTouch.needstoRedraw = true;
					VikaTouch.canvas.serviceRepaints();
					try {
						Thread.sleep(1000);
					} catch (Throwable eee) {
					}
					String x = VikaUtils.download(new URLBuilder("video.get").addField("owner_id", id)
							.addField("count", Settings.simpleListsLength).addField("offset", from));

					// VikaUtils.logToFile(x);
					try {
						VikaTouch.loading = true;
						// System.out.println(x);
						JSONObject response = new JSONObject(x).getJSONObject("response");
						JSONArray items = response.getJSONArray("items");
						totalVids = response.getInt("count");
						itemsCount = (short) items.length();
						canLoadMore = itemsCount < totalVids;
						if (totalVids <= from + count) {
							canLoadMore = false;
						}
						uiItems = null;
						uiItems = new Vector(itemsCount + (canLoadMore ? 1 : 0));
						for (int i = 0; i < itemsCount; i++) {
							JSONObject item = items.getJSONObject(i);
							VideoItem vi = new VideoItem(item);
							uiItems.addElement(vi);
							vi.parseJSON();
							Thread.yield();
						}
						range = " (" + (from + 1) + "-" + (itemsCount + from) + ")";
						if (canLoadMore && VikaTouch.isNotS60()) {
							uiItems.addElement(new LoadMoreButtonItem(VideosScreen.this));
							itemsCount++;
						}
						VikaTouch.loading = true;
						String name = name1;
						if (name == null && name2 != null)
							name = name2;

						if (name == null || name2 == null)
							formattedTitle = TextLocal.inst.get("title.videos");
						else
							formattedTitle = TextLocal.inst.getFormatted("title.videosw", new String[] { name, name2 });
						Thread.sleep(500);
						VikaTouch.loading = true;
						for (int i = 0; i < itemsCount - (canLoadMore ? 1 : 0); i++) {
							if (uiItems.elementAt(i) instanceof VideoItem)
								((VideoItem) uiItems.elementAt(i)).loadIcon();
							Thread.yield();
						}
						VikaTouch.loading = false;
					} catch (JSONException e) {
						e.printStackTrace();
						VikaTouch.error(e, ErrorCodes.VIDEOSPARSE);
					}

					VikaTouch.loading = false;
				} catch (InterruptedException e) {
					// return;
				} catch (Throwable e) {
					e.printStackTrace();
					VikaTouch.error(e, ErrorCodes.VIDEOSLOAD);
				}
				VikaTouch.loading = false;
			}
		};

		downloaderThread.start();
	}

	public void loadMoreVideos(final int from, final int id, final String name1, final String name2) {
		VikaTouch.needstoRedraw = true;
		scroll = 0;

		final int count = Settings.simpleListsLength;
		fromVid = from;
		currId = id;
		this.whose = name1;
		this.name2 = name2;
		//if (downloaderThread != null && downloaderThread.isAlive())
		//	downloaderThread.interrupt();
		final int oldItemsCount = itemsCount;
		downloaderThread = new Thread() {
			public void run() {
				try {
					VikaTouch.needstoRedraw = true;
					VikaTouch.canvas.serviceRepaints();
					try {
						Thread.sleep(1000);
					} catch (Throwable eee) {
					}
					String x = VikaUtils.download(new URLBuilder("video.get").addField("owner_id", id)
							.addField("count", Settings.simpleListsLength).addField("offset", from));

					// VikaUtils.logToFile(x);
					try {
						VikaTouch.loading = true;
						// System.out.println(x);
						JSONObject response = new JSONObject(x).getJSONObject("response");
						JSONArray items = response.getJSONArray("items");
						int iii = items.length();
						int totalVids = response.getInt("count");
						System.out.println(totalVids);
						//itemsCount += items.length();
						canLoadMore = uiItems.size() < totalVids;
						//uiItems = null;
						//uiItems = new Vector(itemsCount + (canLoadMore ? 1 : 0));
						for (int i = 0; i < iii; i++) {
							JSONObject item = items.getJSONObject(i);
							VideoItem vi = new VideoItem(item);
							uiItems.addElement(vi);
							vi.parseJSON();
							Thread.yield();
						}
						range = " (" + (from + 1) + "-" + (iii + from) + ")";
						
						VikaTouch.loading = true;
						String name = name1;
						if (name == null && name2 != null)
							name = name2;

						if (name == null || name2 == null)
							formattedTitle = TextLocal.inst.get("title.videos");
						else
							formattedTitle = TextLocal.inst.getFormatted("title.videosw", new String[] { name, name2 });
						Thread.sleep(500);
						VikaTouch.loading = true;
						for (int i = oldItemsCount; i < uiItems.size() - (canLoadMore ? 1 : 0); i++) {
							if (uiItems.elementAt(i) instanceof VideoItem)
								((VideoItem) uiItems.elementAt(i)).loadIcon();
							Thread.yield();
						}
						loadingMore = false;
						itemsCount = (short) uiItems.size();
						VikaTouch.loading = false;
					} catch (JSONException e) {
						e.printStackTrace();
						VikaTouch.error(e, ErrorCodes.VIDEOSPARSE);
					}

					VikaTouch.loading = false;
				} catch (InterruptedException e) {
					// return;
				} catch (Throwable e) {
					e.printStackTrace();
					VikaTouch.error(e, ErrorCodes.VIDEOSLOAD);
				}
				VikaTouch.loading = false;
			}
		};

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
							int ih = ((PressableUIItem) uiItems.elementAt(i)).getDrawHeight();
							if(scroll +y+ ih > 0 && scroll+y < DisplayUtils.height) {
								((PressableUIItem) uiItems.elementAt(i)).paint(g, y, scroll);
							}
							y += ih;
						}
						
						}
					
					if(!VikaTouch.isNotS60() && uiItems.size() > 10 && !loadingMore && canLoadMore) {
						if (-scroll+(DisplayUtils.height)>=listHeight+MenuScreen.bottomPanelH) {
							System.out.println("LOAD MORE");
							loadingMore = true;
							loadMoreVideos(fromVid + Settings.simpleListsLength, currId, whose, name2);
						}
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				VikaTouch.error(e, ErrorCodes.VIDEOSDRAW);
			}
			g.translate(0, -g.getTranslateY());
		} catch (Exception e) {
			e.printStackTrace();
			VikaTouch.error(e, ErrorCodes.VIDEOSDRAW);
		}
	}

	public final void drawHUD(Graphics g) {
		drawHUD(g, formattedTitle + (loadingMore ? " ("+ TextLocal.inst.get("loading") +"...)" : ""));
	}

	public final void tap(int x, int y, int time) {
		VikaTouch.needstoRedraw = true;
		try {
			if (y > 58 && y < DisplayUtils.height - oneitemheight) {
				int h = 50;
				int yy1 = y - (scroll + 58);
				int i = yy1 / h;
				if (i < 0)
					i = 0;
				if (!dragging) {
					((PressableUIItem) uiItems.elementAt(i)).tap(x, yy1 - (h * i));
				}
				// return;
			}
			// return;
		} catch (ArrayIndexOutOfBoundsException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.tap(x, y, time);
	}

	public void loadNext() {
		VikaTouch.needstoRedraw = true;
		load(fromVid + Settings.simpleListsLength, currId, whose, name2);
	}

}
