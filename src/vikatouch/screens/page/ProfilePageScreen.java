package vikatouch.screens.page;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.popup.ConfirmBox;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import vikatouch.Dialogs;
import vikatouch.VikaTouch;
import vikatouch.items.menu.OptionItem;
import vikatouch.json.JSONBase;
import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;
import vikatouch.screens.menu.DocsScreen;
import vikatouch.screens.menu.FriendsScreen;
import vikatouch.screens.menu.GroupsScreen;
import vikatouch.screens.menu.MenuScreen;
import vikatouch.screens.menu.VideosScreen;
import vikatouch.screens.music.MusicScreen;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;

public class ProfilePageScreen
	extends MainScreen
	implements IMenu
{

	public int id;
	public boolean closed;
	// friend fields
	public String name;
	public String link;
	public String status;
	public boolean canBeFriend;
	public byte friendState;
	public Image ava;
	public boolean canMsg;
	public boolean online;
	public int lastSeen;
	
	// counters
	public int docs;
	public int groups;
	public int photos;
	public int videos;
	public int music;
	public int friends;
	
	// system
	public static Thread downloaderThread;
	private boolean friendAdd; // если true, друг добавляется. Если 0, удаляется.
	private String visitStr;
	protected String wname;
	protected String name2;
	protected static String removeStr;
	protected static String acceptStr;
	protected static String cancelStr;
	protected static String addStr;
	private static String userStr;
	private static String loadingStr;
	protected static String onlineStr;
	protected static String wasOnlineStr;
	protected static String wasOnlineJustNowStr;
	protected static String minutesAgoStr;
	protected static String hoursAgoStr;
	protected static String closedStr;
	protected static String writeMessageStr;
	protected static String friendsStr;
	protected static String wallStr;
	protected static String groupsStr;
	protected static String cannotWriteStr;
	protected static String docsStr;
	protected static String musicStr;
	protected static String videosStr;
	protected static String photosStr;
	
	public ProfilePageScreen(int id)
	{
		if(onlineStr == null)
		{
			userStr = TextLocal.inst.get("user");
			loadingStr = TextLocal.inst.get("menu.loading");
			removeStr = TextLocal.inst.get("friend.remove");
			acceptStr = TextLocal.inst.get("friend.accept");
			cancelStr = TextLocal.inst.get("friend.cancel");
			addStr = TextLocal.inst.get("friend.add");
			photosStr = TextLocal.inst.get("menu.photos");
			videosStr = TextLocal.inst.get("menu.videos");
			musicStr = TextLocal.inst.get("menu.music");
			docsStr = TextLocal.inst.get("menu.documents");
			cannotWriteStr = TextLocal.inst.get("menu.cannotwrite");
			writeMessageStr = TextLocal.inst.get("menu.writemsg");
			friendsStr = TextLocal.inst.get("menu.friends");
			wallStr = TextLocal.inst.get("menu.wall");
			groupsStr = TextLocal.inst.get("menu.groups");
			closedStr = TextLocal.inst.get("profile.closed");
			onlineStr = TextLocal.inst.get("online");
			minutesAgoStr = TextLocal.inst.get("date.minutesago");
			hoursAgoStr = TextLocal.inst.get("date.hoursago");
			wasOnlineStr = TextLocal.inst.get("wasonlinedate");
			wasOnlineJustNowStr = wasOnlineStr + " " + TextLocal.inst.get("date.justnow");
		}
		hasBackButton = true;
		this.id = id;
		load();
	}
	
	public void load()
	{
		if(downloaderThread != null && downloaderThread.isAlive())
			downloaderThread.interrupt();
		System.gc();
		final ProfilePageScreen thisC = this;
		downloaderThread = new Thread()
		{

			public void run()
			{
				try
				{
					VikaTouch.loading = true;
					String x = VikaUtils.download(new URLBuilder("users.get").addField("user_ids", id)
							.addField("fields", "photo_50,online,domain,status,last_seen,common_count,can_write_private_message,can_send_friend_request,is_friend,friend_status,counters"));
					try
					{
						VikaTouch.loading = true;
						JSONObject res = new JSONObject(x).getJSONArray("response").getJSONObject(0);
						
						closed = res.optInt("can_access_closed") == 1;
					
						name = res.optString("first_name") + " " + res.optString("last_name");
						link = res.optString("domain");
						status = res.optString("status");
						canBeFriend = res.optInt("can_send_friend_request") == 1;
						canMsg = res.optInt("can_write_private_message") == 1;
						friendState = (byte)res.optInt("friend_status");
						friendAdd = (friendState==0||friendState==2);
						
						try {
							lastSeen = res.getJSONObject("last_seen").optInt("time");
						}
						catch (Exception e) { }
						online = res.optInt("online") == 1;
						
						if(online)
						{
							visitStr = onlineStr;
						}
						else
						{
							int now = (int)(System.currentTimeMillis()/1000);
							int r = now - lastSeen;
							if(r<90) 
							{
								visitStr = wasOnlineJustNowStr;
							}
							else if(r<60*60)
							{
								visitStr = wasOnlineStr + " "+(r/60)+" "+minutesAgoStr;
							}
							else
							{
								visitStr = wasOnlineStr + " "+(r/3600)+" "+hoursAgoStr;
							}
						}
						
						try 
						{
							JSONObject counters = res.getJSONObject("counters");
							docs = counters.optInt("docs");
							groups = counters.optInt("groups");
							music = counters.optInt("audios");
							videos = counters.optInt("videos");
							photos = counters.optInt("photos");
							friends = counters.optInt("friends");
						}
						catch (Exception e) {}
						
						try {
							ava = VikaUtils.downloadImage(JSONBase.fixJSONString(res.optString("photo_50")));
						} catch (Exception e) { }
						
						int h = oneitemheight=(short) (DisplayUtils.compact?30:50);
						if(closed) 
						{
							itemsCount = 2;
							uiItems = new OptionItem[2];
							uiItems[0] = new OptionItem(thisC, closedStr, IconsManager.INFO, 0, 50);
						}
						else
						{
							itemsCount = 9;
							uiItems = new OptionItem[9];
							uiItems[0] = new OptionItem(thisC, canMsg?writeMessageStr:cannotWriteStr, IconsManager.MSGS, 0, h);
							
							uiItems[2] = new OptionItem(thisC, friendsStr + " ("+friends+")", IconsManager.FRIENDS, 2, h);
							uiItems[3] = new OptionItem(thisC, wallStr, IconsManager.NEWS, 3, h);
							uiItems[4] = new OptionItem(thisC, groupsStr+" ("+groups+")", IconsManager.GROUPS, 4, h);
							uiItems[5] = new OptionItem(thisC, photosStr+" ("+photos+")", IconsManager.PHOTOS, 5, h);
							uiItems[6] = new OptionItem(thisC, musicStr+" ("+music+")", IconsManager.MUSIC, 6, h);
							uiItems[7] = new OptionItem(thisC, videosStr+" ("+videos+")", IconsManager.VIDEOS, 7, h);
							uiItems[8] = new OptionItem(thisC, docsStr+" ("+docs+")", IconsManager.DOCS, 8, h);
						}
						uiItems[1] = new OptionItem(thisC, (new String[] {addStr,cancelStr,acceptStr,removeStr})[friendState],
								(friendState==3||friendState==1)?IconsManager.CLOSE:IconsManager.ADD, 1, h);
						try
						{
							String x2 = VikaUtils.download(new URLBuilder("users.get").addField("user_ids", id).addField("name_case", "gen"));
							JSONObject cc = new JSONObject(x2).getJSONArray("response").getJSONObject(0);
							wname = ""+cc.getString("first_name");
						}
						catch (Exception e)
						{
							wname = name;
						}
						name2 = ""+res.optString("first_name");
					}
					catch (JSONException e)
					{
						e.printStackTrace();
						VikaTouch.error(e, ErrorCodes.GROUPPAGEPARSE);
					}

					VikaTouch.loading = false;
				}
				catch (NullPointerException e)
				{
					e.printStackTrace();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					VikaTouch.error(e, ErrorCodes.GROUPPAGELOAD);
				}
				VikaTouch.loading = false;
				System.gc();
			}
		};

		downloaderThread.start();
	}
	
	public void draw(Graphics g)
	{
		int y = topPanelH+82; // init offset
		update(g);
		if(!DisplayUtils.compact)
		{
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
		if(ava != null)
		{
			g.drawImage(ava, 16, topPanelH+13, 0);
			g.drawImage(IconsManager.ac, 16, topPanelH+13, 0);
			ColorUtils.setcolor(g, ColorUtils.ONLINE);
			g.fillArc(16+38, topPanelH+13+38, 12, 12, 0, 360);
		}
		itemsh = itemsCount * oneitemheight + y;
		g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawString(name==null?loadingStr+"...":name, 74, 74, 0);
		g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
		g.drawString(status==null?"":status, 74, 98, 0);
		
		ColorUtils.setcolor(g, -3);
		g.drawRect(0, 140, DisplayUtils.width, 50);
		if(uiItems!=null)
		{
			for (int i=0;i<uiItems.length;i++)
			{
				if(uiItems[i]!=null) {
					uiItems[i].paint(g, y, scrolled);
					y+=uiItems[i].getDrawHeight();
				}
			}
		}
		g.translate(0, -g.getTranslateY());
	}
	
	public final void release(int x, int y)
	{
		if(!dragging)
		{
			if(y > 58 && y < DisplayUtils.height-50)
			{
				for(int i = 0; i < itemsCount; i++)
				{
					int y1 = scrolled + 140 + (i * oneitemheight);
					int y2 = y1 + oneitemheight;
					if(y > y1 && y < y2)
					{
						onMenuItemPress(i);
						break;
					}
				}
			}
		}
		super.release(x, y);
	}

	public void onMenuItemPress(int i)
	{
		switch (i) 
		{
			case 0:
				if(closed) 
				{ } // юзается как алерт, ничего не делаем.
				if(canMsg)
				{
					Dialogs.openDialog(id, name);
				}
				break;
			case 1:
				VikaTouch.loading = true;
				(new Thread()
				{
					public void run()
					{
						if(friendAdd)
						{
							VikaUtils.download(new URLBuilder("friends.add").addField("user_id", id));
						}
						else
						{
							VikaUtils.download(new URLBuilder("friends.delete").addField("user_id", id));
						}
						load();
					}
				}
				).start();
				break;
			case 2:
				FriendsScreen fs = new FriendsScreen();
				VikaTouch.setDisplay(fs, 1);
				fs.loadFriends(0, id, wname, name2);
				break;
			case 3:
				break;
			case 4:
				GroupsScreen gs = new GroupsScreen();
				VikaTouch.setDisplay(gs, 1);
				gs.loadGroups(0, id, wname, name2);
				break;
			case 6:
				MusicScreen.open(id, wname, name2);
				break;
			case 7:
				VideosScreen vs = new VideosScreen();
				VikaTouch.setDisplay(vs, 1);
				vs.load(0, id, wname, name2);
				break;
			case 8:
				if(docs>0) {
					DocsScreen dc = new DocsScreen();
					VikaTouch.setDisplay(dc, 1);
					dc.loadDocs(0, id, wname, name2);
				}
				break;
		}
		
	}
	
	public void drawHUD(Graphics g)
	{
		drawHUD(g, link==null?userStr:link);
	}

	public void onMenuItemOption(int i)
	{
		
	}
}
