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
			short requestcount = 10;
			short startswith = 0;
			short postscount = 10;
			uiItems = new PostItem[postscount];
			int len2 = postscount;
			final String s = VikaUtils.download(
					new URLBuilder("newsfeed.get")
					.addField("filters", "post,photo,photo_tag,wall_photo")
					.addField("count", requestcount)
					.addField("fields", "groups,profiles,items")
					);
			JSONObject response = new JSONObject(s).getJSONObject("response");
			JSONArray items = response.getJSONArray("items");
			profiles = response.getJSONArray("profiles");
			groups = response.getJSONArray("groups");
			//System.out.println(s);
			itemsh = 0;
			int i2 = startswith;
			for(int i = 0; i < len2; i++)
			{
				if(i2 >= requestcount)
				{
					break;
				}
				JSONObject item = items.getJSONObject(i2);
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
				if(((PostItem) uiItems[i]).text == "" && ((PostItem) uiItems[i]).prevImage == null)
				{
					uiItems[i] = null;
					i--;
				}
				else
					itemsh += uiItems[i].getDrawHeight() + 8;
				i2++;
			}
			itemsCount = postscount;
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
		double multiplier = (double)DisplayUtils.height / 640.0;
		double ww = 10.0 * multiplier;
		int w = (int)ww;
		try
		{
			ColorUtils.setcolor(g, -1);
			g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);
			ColorUtils.setcolor(g, 0);
			g.setFont(Font.getFont(0, 0, 8));
			//g.drawString(string, 8, 8, 0);
			
			update(g);
			int y = oneitemheight + w;
			try
			{
				for(int i = 0; i < itemsCount; i++)
				{
					if(uiItems[i] != null)
					{
						uiItems[i].paint(g, y, scrolled);
						y += uiItems[i].getDrawHeight() + 8;
					}
				}
				if(y != itemsh)
					itemsh = y;
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
			switch(DisplayUtils.idispi)
			{
				case DisplayUtils.DISPLAY_ALBUM:
				case DisplayUtils.DISPLAY_PORTRAIT:
				{
					if(y > 58 && y < DisplayUtils.height - oneitemheight)
					{
						int yy = 0;
						for(int i = 0; i < itemsCount; i++)
						{
							int y1 = scrolled + 50 + yy;
							int y2 = y1 + uiItems[i].getDrawHeight();
							yy += uiItems[i].getDrawHeight();
							if(y > y1 && y < y2)
							{
								uiItems[i].tap(x, y1 - y);
								itemsh = 0;
								for(int i2 = 0; i2 < itemsCount; i2++)
								{
									if(uiItems[i2] != null)
										itemsh += uiItems[i2].getDrawHeight();
								}
								break;
							}
							
						}
					}
					break;
				}
				
			}
		}
		super.release(x, y);
	}

	protected void scrollHorizontally(int deltaX)
	{
		
	}

}
