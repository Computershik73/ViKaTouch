package vikatouch.screens;


import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.attachments.WallAttachment;
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
	public String titleStr;
	
	public int newsSource = 0;
	public boolean fromAtt = false;
	
	public NewsScreen()
	{
		super();
		titleStr = TextLocal.inst.get("title.newsfeed");
		
		VikaTouch.loading = true;
		
		if(VikaTouch.menuScr == null)
			VikaTouch.menuScr = new MenuScreen();
		
		scrollWithKeys = true;
	}
	
	public void loadPosts()
	{
		new Thread()
		{
			public void run()
			{
				VikaTouch.loading = true;
				try
				{
					int count = 50;
					URLBuilder url;
					if(newsSource == 0)
					{
						url = new URLBuilder("newsfeed.get").addField("filters", "post")
							.addField("count", count).addField("fields", "groups,profiles,items");
					}
					else
					{
						url = new URLBuilder("wall.get").addField("filter", "all").addField("extended", 1)
								.addField("count", count).addField("owner_id", newsSource);
					}
					hasBackButton = newsSource!=0;
					final String s = VikaUtils.download(url);
					//VikaTouch.sendLog(url.toString());
					//VikaTouch.sendLog(newsSource+" "+(s.length()>210?s.substring(0, 200):s));
					VikaTouch.loading = true;
					JSONObject response = null;
					try
					{
						response = new JSONObject(s).getJSONObject("response");
					} 
					catch (JSONException e)
					{
						VikaTouch.popup(new InfoPopup(s, null));
						return;
					}
					JSONArray items = response.getJSONArray("items");
					
					int itemsCount = items.length();
					uiItems = new PostItem[itemsCount];
					
					profiles = response.getJSONArray("profiles");
					groups = response.getJSONArray("groups");
					
					itemsh = 0;
					for(int i = 0; i < itemsCount; i++)
					{
						VikaTouch.loading = true;
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
						Thread.sleep(20);
					}
				}
				catch (Exception e)
				{
					VikaTouch.error(e, ErrorCodes.NEWSPARSE);
					e.printStackTrace();
				}
				VikaTouch.loading = false;
			}
		}.start();
		
		System.gc();
	}

	public void loadAtt(WallAttachment att)
	{
		fromAtt = true;
		VikaTouch.loading = true;
		try
		{
			hasBackButton = true;
			VikaTouch.loading = true;
			uiItems = new PostItem[1];
			
			itemsh = 0;
			VikaTouch.loading = true;
			JSONObject item = att.json;
			JSONObject itemCopy;
			try
			{
				itemCopy = item.getJSONArray("copy_history").getJSONObject(0);
			}
			catch(Exception e)
			{
				itemCopy = item;
			}
			uiItems[0] = new PostItem(itemCopy, item);
			((PostItem) uiItems[0]).parseJSON();
		}
		catch (Exception e)
		{
			VikaTouch.error(e, -ErrorCodes.NEWSPARSE);
			e.printStackTrace();
		}
		VikaTouch.loading = false;
	}
	protected final void callRefresh()
	{
		if(!fromAtt) loadPosts();
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
							if(y+scrolled < DisplayUtils.height) uiItems[i].paint(g, y, scrolled);
							y += uiItems[i].getDrawHeight();
						}
					}
					
					itemsh = y+50;
				}
			}
			catch (Exception e)
			{
				VikaTouch.error(e, ErrorCodes.NEWSPOSTSDRAW);
			}
			/*g.translate(0, -g.getTranslateY());
			g.setColor(0, 0, 0);
			g.fillRect(0, 60, 300, 25);
			g.setColor(200, 200, 200);
			g.drawString(scrlDbg, 0, 60, 0);*/
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
				int yy = topPanelH+10;
				for(int i = 0; i < uiItems.length; i++)
				{
					try
					{
						int y1 = scrolled + yy;
						int y2 = y1 + uiItems[i].getDrawHeight();
						yy += uiItems[i].getDrawHeight();
						if(y > y1 && y < y2)
						{
							//VikaTouch.sendLog(i+" x"+x+" y"+(y1-y));
							uiItems[i].tap(x, y - y1);
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
	
	public void onLeave()
	{
		VikaTouch.newsScr = null;
	}

}
