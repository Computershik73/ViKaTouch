package vikatouch.screens.menu;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.items.LoadMoreButtonItem;
import vikatouch.items.menu.DocItem;
import vikatouch.json.INextLoadable;
import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;

/**
 * @author Shinovon
 * 
 */
public class DocsScreen extends MainScreen implements INextLoadable {

	public DocsScreen() {
		super();
		VikaTouch.needstoRedraw=true;
		isPreviewShown = false;
		VikaTouch.loading = true;
	}

	public static DocsScreen current;

	public final static int loadDocsCount = 30;
	public int fromDoc = 0;
	public int totalDocs = 0;
	public int currId;
	public static Thread downloaderThread;

	public boolean isPreviewShown = false;
	public Image previewImage = null;
	public int previewX = 0;
	public int previewY = 0;
	public String range = null;

	public boolean canLoadMore = true;

	private String formattedTitle;

	private String whose;

	private String name2;

	public void loadDocs(final int from, final int id, final String name1, final String name2) {
		this.whose = name1;
		this.name2 = name2;
		formattedTitle = TextLocal.inst.get("title.docs");
		scrolled = 0;
		uiItems = null;
		current = this;
		final int count = loadDocsCount;
		fromDoc = from;
		currId = id;
		if (downloaderThread != null && downloaderThread.isAlive())
			downloaderThread.interrupt();

		downloaderThread = new Thread() {
			public void run() {
				try {
					VikaTouch.loading = true;
					String x = VikaUtils.download(new URLBuilder("docs.get").addField("owner_id", id)
							.addField("count", count).addField("offset", from));
					try {
						VikaTouch.loading = true;
						JSONObject response = new JSONObject(x).getJSONObject("response");
						JSONArray items = response.getJSONArray("items");
						totalDocs = response.getInt("count");
						itemsCount = (short) items.length();
						canLoadMore = !(itemsCount < count);
						if (totalDocs <= from + count) {
							canLoadMore = false;
						}
						uiItems = new PressableUIItem[itemsCount + (canLoadMore ? 1 : 0)];
						for (int i = 0; i < itemsCount; i++) {
							JSONObject item = items.getJSONObject(i);
							uiItems[i] = new DocItem(item);
							((DocItem) uiItems[i]).parseJSON();
						}
						range = " (" + (from + 1) + "-" + (itemsCount + from) + ")";
						if (canLoadMore) {
							uiItems[itemsCount] = new LoadMoreButtonItem(DocsScreen.this);
							itemsCount++;
						}
						String name = name1;
						if (name == null && name2 != null)
							name = name2;

						if (name == null || name2 == null)
							formattedTitle = TextLocal.inst.get("title.docs");
						else
							formattedTitle = TextLocal.inst.getFormatted("title.docsw", new String[] { name1, name2 });
					} catch (JSONException e) {
						e.printStackTrace();
						VikaTouch.error(e, ErrorCodes.DOCUMENTSPARSE);
					}

					VikaTouch.loading = false;
				} catch (InterruptedException e) {
					return;
				} catch (Exception e) {
					e.printStackTrace();
					VikaTouch.error(e, ErrorCodes.DOCUMENTSLOAD);
				}
				VikaTouch.loading = false;
			}
		};
		hasBackButton = true;

		downloaderThread.start();
	}

	public void draw(Graphics g) {
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.setFont(Font.getFont(0, 0, 8));
		itemsh = itemsCount * 52;
		try {
			update(g);
			int y = topPanelH;
			try {
				if (uiItems != null)
					for (int i = 0; i < itemsCount; i++) {
						if (uiItems[i] != null) {
							uiItems[i].paint(g, y, scrolled);
							y += uiItems[i].getDrawHeight();
						}

					}
			} catch (Exception e) {
				VikaTouch.error(e, ErrorCodes.DOCUMENTSITEMDRAW);
			}
			g.translate(0, -g.getTranslateY());

		} catch (Exception e) {
			VikaTouch.error(e, ErrorCodes.DOCUMENTSDRAW);
			e.printStackTrace();
		}
		try {
			if (isPreviewShown) {
				if (previewImage == null) {
					VikaTouch.loading = true;
				} else {
					VikaTouch.loading = false;
					g.setGrayScale(200);
					g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);
					g.drawImage(previewImage, previewX, previewY, 0);
				}
			}
		} catch (Exception e) {
			VikaTouch.error(e, ErrorCodes.DOCPREVIEWDRAW);
			e.printStackTrace();
		}
	}

	public final void drawHUD(Graphics g) {
		drawHUD(g, formattedTitle);
	}

	public final void release(int x, int y) {
		try {
			if (isPreviewShown) {
				isPreviewShown = false;
				previewImage = null;
				return;
			}

			if (y > topPanelH && y < DisplayUtils.height - bottomPanelH) {
				int h = 48 + (DocItem.BORDER * 2);
				int yy1 = y - (scrolled + 58);
				int i = yy1 / h;
				if (i < 0)
					i = 0;
				if (!dragging) {
					uiItems[i].tap(x, yy1 - (h * i));
				}

			}
		} catch (ArrayIndexOutOfBoundsException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.release(x, y);
	}

	public void loadNext() {
		down();
		loadDocs(fromDoc + loadDocsCount, currId, whose, name2);
	}

	public void onLeave() {
		current = null;
	}

}
