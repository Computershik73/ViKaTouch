package vikatouch.screens;


import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.items.PostItem;
import vikatouch.locale.TextLocal;
import vikatouch.screens.menu.MenuScreen;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;

public class NewsScreen
	extends MainScreen
{
	
	public static JSONArray profiles;
	public static JSONArray groups;
	private static String titleStr;
	
	public NewsScreen()
	{
		super();
		titleStr = TextLocal.inst.get("title.newsfeed");
		
		VikaTouch.loading = true;
		
		if(VikaTouch.menuScr == null)
			VikaTouch.menuScr = new MenuScreen();
		
		loadPosts();
		scrollWithKeys = true;
	}
	
	private void loadPosts()
	{
		new Thread( new Runnable()
				{
		public void run()
		{
		VikaTouch.loading = true;
		try
		{
			int count = 20;
			
			final String s = VikaUtils.download(
					new URLBuilder("newsfeed.get")
					.addField("filters", "post")
					.addField("count", count)
					.addField("fields", "groups,profiles,items")
					);
			JSONObject response = new JSONObject(s).getJSONObject("response");
			JSONArray items = response.getJSONArray("items");
			int itemsCount = items.length();
			uiItems = new PostItem[itemsCount];
			
			profiles = response.getJSONArray("profiles");
			groups = response.getJSONArray("groups");
			
			itemsh = 0;
			for(int i = 0; i < itemsCount; i++)
			{
				JSONObject item = items.getJSONObject(i);
				JSONObject itemCopy;
				try
				{
					itemCopy = item.getJSONArray("copy_history").getJSONObject(0);
				}
				catch(Exception e)
				{
					itemCopy = item;
				}
				uiItems[i] = new PostItem(itemCopy, item);
				((PostItem) uiItems[i]).parseJSON();
			}
		}
		catch (Exception e)
		{
			VikaTouch.error(e, ErrorCodes.NEWSPARSE);
			e.printStackTrace();
		}
		}
				}).start();
		
		System.gc();
	}

	protected final void callRefresh()
	{
		loadPosts();
	}

	public void draw(Graphics g)
	{
		try
		{
			
			update(g);
			
			int y = topPanelH+10;
			try
			{
				if(uiItems!=null)
				{
					for(int i = 0; i < uiItems.length; i++)
					{
						if(uiItems[i] != null)
						{
							uiItems[i].paint(g, y, scrolled);
							y += uiItems[i].getDrawHeight() + 10;
						}
					}
					
					itemsh = y;
				}
			}
			catch (Exception e)
			{
				VikaTouch.error(e, ErrorCodes.NEWSPOSTSDRAW);
			}
			g.translate(0, -g.getTranslateY());
		}
		catch (Exception e)
		{
			VikaTouch.error(e, ErrorCodes.NEWSDRAW);
			e.printStackTrace();
		}
	}
	public final void drawHUD(Graphics g)
	{
		drawHUD(g, titleStr);
	}
	
	public final void release(int x, int y)
	{
		if(!dragging)
		{
			if(y > 58 && y < DisplayUtils.height - oneitemheight)
			{
				int yy = 0;
				for(int i = 0; i < uiItems.length; i++)
				{
					try
					{
						int y1 = scrolled + 50 + yy;
						int y2 = y1 + uiItems[i].getDrawHeight();
						yy += uiItems[i].getDrawHeight();
						if(y > y1 && y < y2)
						{
							uiItems[i].tap(x, y1 - y);
						}
					}
					catch (Exception e)
					{
						
					}
				}
			}
		}
		super.release(x, y);
	}

	protected void scrollHorizontally(int deltaX)
	{
		
	}

}
