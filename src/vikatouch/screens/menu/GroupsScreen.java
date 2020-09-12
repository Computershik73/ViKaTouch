package vikatouch.screens.menu;

import java.io.IOException;

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
import vikatouch.items.menu.GroupItem;
import vikatouch.json.INextLoadable;
import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;


public class GroupsScreen
	extends MainScreen implements INextLoadable
{

	public GroupsScreen()
	{
		super();
		VikaTouch.loading = true;
		groupsStr = TextLocal.inst.get("title.groups");
	}

	public boolean isReady()
	{
		return uiItems != null;
	}
	public static void abortLoading()
	{
		try {
			if(downloaderThread != null && downloaderThread.isAlive())
				downloaderThread.interrupt();
		} catch (Exception e) { }
	}
	
	public static Thread downloaderThread;
	
	public int currId;
	public int fromG;
	public String range = null;
	public int totalItems;
	public boolean canLoadMore = true;

	private String groupsStr;

	protected String formattedTitle;

	private String whose;

	private String name2;

	public void loadGroups(final int from, final int id, final String name1, final String name2)
	{
		formattedTitle = groupsStr;
		scrolled = 0;
		uiItems = null;
		final GroupsScreen thisC = this;
		fromG = from;
		currId = id;
		whose = name1;
		this.name2 = name2;
		
		abortLoading();

		downloaderThread = new Thread()
		{

			public void run()
			{
				try
				{
					VikaTouch.loading = true;
					String x = VikaUtils.download(new URLBuilder("groups.get").addField("extended", "1")
							.addField("count", Settings.simpleListsLength).addField("fields", "members_count,counters").addField("user_id", id).addField("offset", from));
					try
					{
						VikaTouch.loading = true;
						repaint();
						JSONObject response = new JSONObject(x).getJSONObject("response");
						JSONArray items = response.getJSONArray("items");
						totalItems = response.getInt("count");
						itemsCount = (short) items.length();
						//System.out.println(totalItems + " - "+itemsCount);
						canLoadMore = totalItems > from+Settings.simpleListsLength;
						uiItems = new PressableUIItem[itemsCount+(canLoadMore?1:0)];
						for(int i = 0; i < itemsCount; i++)
						{
							VikaTouch.loading = true;
							JSONObject item = items.getJSONObject(i);
							uiItems[i] = new GroupItem(item);
							((GroupItem) uiItems[i]).parseJSON();
						}
						range = " ("+(from+1)+"-"+(itemsCount+from)+")";
						if(canLoadMore) {
							uiItems[itemsCount] = new LoadMoreButtonItem(thisC);
							itemsCount++;
						}
					}
					catch (JSONException e)
					{
						e.printStackTrace();
						VikaTouch.error(e, ErrorCodes.GROUPSPARSE);
					}
					if(keysMode) {
						currentItem = 0;
						uiItems[0].setSelected(true);
					}
					VikaTouch.loading = true;
					String name = name1;
					if(name == null && name2 != null)
						name = name2;
					
					if(name == null || name2 == null)
						formattedTitle = TextLocal.inst.get("title.groups");
					else
						formattedTitle = TextLocal.inst.getFormatted("title.groupsw", new String[] { name, name2 });
					repaint();
					Thread.sleep(1000);
					VikaTouch.loading = true;
					if(!Settings.dontLoadAvas)
					{
						for(int i = 0; i < itemsCount - (canLoadMore?1:0); i++)
						{
							VikaTouch.loading = true;
							((GroupItem) uiItems[i]).getAva();
						}
					}
					VikaTouch.loading = false;
				}
				catch (NullPointerException e)
				{
					e.printStackTrace();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					VikaTouch.error(e, ErrorCodes.GROUPSLOAD);
				}
				VikaTouch.loading = false;
			}
		};
		hasBackButton = true;

		downloaderThread.start();
	}
	public void draw(Graphics g)
	{
		ColorUtils.setcolor(g, 0);
		g.setFont(Font.getFont(0, 0, 8));
		itemsh = itemsCount * 52;
		try
		{
			update(g);
			int y = topPanelH;
			try
			{
				if(uiItems != null)
				{
					for(int i = 0; i < itemsCount; i++)
					{
						if(uiItems[i] != null)
						{
							uiItems[i].paint(g, y, scrolled);
							y += uiItems[i].getDrawHeight();
						}
	
					}
				}
			}
			catch (Exception e)
			{
				VikaTouch.error(e, ErrorCodes.GROUPSITEMDRAW);
			}
			g.translate(0, -g.getTranslateY());
		}
		catch (Exception e)
		{
			VikaTouch.error(e, ErrorCodes.GROUPSDRAW);
			e.printStackTrace();
		}
	}
	
	public final void drawHUD(Graphics g)
	{
		drawHUD(g, formattedTitle);
	}
	
	public final void release(int x, int y)
	{
		try
		{
			switch(DisplayUtils.idispi)
			{
				case DisplayUtils.DISPLAY_ALBUM:
				case DisplayUtils.DISPLAY_PORTRAIT:
				{
					if(y > 58 && y < DisplayUtils.height - oneitemheight)
					{
						int h = 48 + (DocItem.BORDER * 2);
						int yy1 = y - (scrolled + 58);
						int i = yy1 / h;
						if(i < 0)
							i = 0;
						if(!dragging)
						{
							uiItems[i].tap(x, yy1 - (h * i));
						}
						break;
					}
					break;
				}
	
			}
		}
		catch (ArrayIndexOutOfBoundsException e) 
		{ 
			// Всё нормально, просто тапнули ПОД последним элементом.
			// ты на что-то намекаешь?
			// Я? я ни на что, просто оно реально плюётся если тапнуть под последним. Ничего не трогай, сломаем. (с) Feodor0090
			// ок че
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		super.release(x, y);
	}

	public void loadNext() {
		loadGroups(fromG+Settings.simpleListsLength, currId, whose, name2);
	}
}
