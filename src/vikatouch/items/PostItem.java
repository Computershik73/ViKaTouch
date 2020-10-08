package vikatouch.items;

import java.util.Random;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.popup.AutoContextMenu;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import vikatouch.VikaTouch;
import vikatouch.attachments.Attachment;
import vikatouch.attachments.DocumentAttachment;
import vikatouch.attachments.ISocialable;
import vikatouch.attachments.PhotoAttachment;
import vikatouch.attachments.StickerAttachment;
import vikatouch.attachments.VideoAttachment;
import vikatouch.items.menu.OptionItem;
import vikatouch.locale.TextLocal;
import vikatouch.screens.NewsScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;

public class PostItem
	extends JSONUIItem implements ISocialable, IMenu
{
	
	private JSONObject json2;

	public PostItem(JSONObject json, JSONObject ob)
	{
		super(json);
		json2 = ob;
	}

	public int ownerid;
	public int id;
	
	public int views;
	public int reposts;
	public int likes;
	public boolean canLike;
	public int comments;
	private boolean liked;
	public boolean canlike;
	
	public String copyright;
	public int replyownerid;
	public int replypostid;
	
	private String avaurl;
	private String[] drawText;
	public String name = "";
	public Image ava;
	
	public boolean isreply;
	private int sourceid;
	private String reposterName;
	private String type;
	private String data;
	private boolean dontLoadAva;
	protected boolean hasPrevImg;
	public long date;
	public String dateS;
	
	private int xx;
	
	// tap data
	int repX, comX;
	int[] attsY0;
	
	int attH = 0;
	
	public void parseJSON()
	{
		super.parseJSON();
		super.parseAttachments();
		int ec = 0;
		try
		{
			try
			{
				if(text == null || text == "")
				{
					text = fixJSONString(json2.optString("text"));
				}
			}
			catch (Exception e)
			{
				VikaTouch.error(e, ErrorCodes.POSTTEXT);
				e.printStackTrace();
				text = "";
			}
			ec = 1;
			try
			{
				likes = json2.optJSONObject("likes").optInt("count");
				liked = json2.optJSONObject("likes").optInt("user_likes") == 1;
				canlike = json2.optJSONObject("likes").optInt("can_like") == 1;
				reposts = json2.optJSONObject("reposts").optInt("count");
				views = json2.optJSONObject("views").optInt("count");
				comments = json2.optJSONObject("comments").optInt("count");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
	
			try
			{
				JSONObject postSource = json2.getJSONObject("post_source");
				data = postSource.optString("data");
			}
			catch (Exception e)
			{
				
			}
			ec = 2;
			try
			{
				date = json2.optLong("date");
				dateS = VikaUtils.parseTime(date);
			}
			catch (Exception e)
			{
			}
			
			type = json2.optString("type"); 
			ec = 3;
			copyright = json2.optString("copyright");
			ownerid = json2.optInt("owner_id");
			sourceid = json2.optInt("source_id");
			id = json2.optInt("id");
			replyownerid = json2.optInt("reply_owner_id");
			replypostid = json2.optInt("reply_post_id");
			if(id == 0)
			{
				copyright = json.optString("copyright");
				ownerid = json.optInt("owner_id");
				id = json.optInt("id");
				replyownerid = json.optInt("reply_owner_id");
				replypostid = json.optInt("reply_post_id");
			} ec = 3;
			//itemDrawHeight = 82;
			isreply = replypostid != 0;
			itemDrawHeight = 72;
			xx = 0;
			xx = replyownerid;
			if(xx == 0)
				xx = fromid;
			if(xx == 0)
				xx = ownerid;
			if(xx == 0)
				xx = sourceid;
			ec = 4;
			labelgetnameandphoto:
			{
				if(xx < 0)
				{
					if(NewsScreen.groups!=null)
					{
						for(int i = 0; i < NewsScreen.groups.length(); i++)
						{
							try
							{
								JSONObject group = NewsScreen.groups.getJSONObject(i);
								final int gid = group.optInt("id");
								if(gid == -xx)
								{
									name = group.optString("name");
									avaurl = fixJSONString(group.optString("photo_50"));
									break labelgetnameandphoto;
								}
							}
							catch (Exception e)
							{
								VikaTouch.error(e, ErrorCodes.POSTAVAGROUPS);
								e.printStackTrace();
							}
						}
					}
					else
					{
						JSONObject jo2 = new JSONObject(VikaUtils.download(new URLBuilder("groups.getById").addField("group_id", -xx))).getJSONArray("response").getJSONObject(0);
						avaurl = fixJSONString(jo2.optString("photo_50"));
						name = jo2.optString("name", "");
					}
				}
			}
			ec = 5;
			boolean b1 = false;
			boolean b2 = false;
			if(xx>=0)
			{
				if(NewsScreen.profiles!=null)
				{
					for(int i = 0; i < NewsScreen.profiles.length(); i++)
					{
						try
						{
							JSONObject profile = NewsScreen.profiles.getJSONObject(i);
							int uid = profile.optInt("id");
							if(sourceid <= 0)
							{
								b2 = true;
							}
							if(!b2 && uid == sourceid)
							{
								reposterName = "" + profile.optString("first_name") + " " + profile.optString("last_name");
								b2 = true;
							}
							if(xx < 0)
							{
								b1 = true;
							}
							if(!b1 && uid == xx)
							{
								name = "" + profile.optString("first_name") + " " + profile.optString("last_name");
								b1 = true;
								JSONObject jo2 = new JSONObject(VikaUtils.download(new URLBuilder("users.get").addField("user_ids", ""+profile.optInt("id")).addField("fields", "photo_50"))).getJSONArray("response").getJSONObject(0);
								avaurl = fixJSONString(jo2.optString("photo_50"));
							}
							if(b1 && b2)
							{
								break;
							}
						}
						catch (Exception e)
						{
							VikaTouch.error(e, ErrorCodes.POSTAVAPROFILES);
							e.printStackTrace();
						}
					}
				}
				else
				{
					JSONObject u = new JSONObject(VikaUtils.download(new URLBuilder("users.get").addField("user_ids", xx).addField("fields", "photo_50"))).getJSONArray("response").getJSONObject(0);
					avaurl = fixJSONString(u.optString("photo_50"));
					name = u.optString("first_name") + " " + u.optString("last_name");
				}
			}
			ec = 6;
			itemDrawHeight = 100;
	
			
			if(data != null && data.equalsIgnoreCase("profile_photo"))
			{
				text = "обновил фотографию на странице";
			}
			ec = 7;
			
			drawText = TextBreaker.breakText(text, Font.getFont(0, 0, 8), DisplayUtils.width - 32);
			ec= 8;
			getRes();
		}
		catch(Throwable t)
		{
			VikaTouch.popup(new InfoPopup("post, code "+ec+" ex "+t.toString(), null));
		}
		System.gc();
	}

	public void paint(Graphics g, int y, int scrolled)
	{
		if(y+scrolled+itemDrawHeight < -50) return;
		Font f = Font.getFont(0, 0, 8);
		int fh = f.getHeight();
		int textX = 16;
		int dw = DisplayUtils.width;
		g.setFont(f);
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		
		int cy = 0;
		
		if(ava != null)
		{
			g.drawImage(ava, 10, 5 + y, 0);
			g.drawImage(IconsManager.ac, 10, 5 + y, 0);
		}
		
		ColorUtils.setcolor(g, 5);
		
		if(name!=null) g.drawString(name, 70, y + 5 + 12 - f.getHeight()/2, 0);
		ColorUtils.setcolor(g, ColorUtils.OUTLINE);
		if(dateS!=null) g.drawString(dateS, 70, y + 5 + 38 - f.getHeight()/2, 0);
		ColorUtils.setcolor(g, ColorUtils.TEXT);

		cy += 60;
		if(drawText != null)
		{
			for(int i = 0; i < drawText.length; i++)
			{
				if(drawText[i] != null)
					g.drawString(drawText[i], textX, y + cy + fh*i, 0);
			}
			cy+=fh*(drawText.length+1);
		}

		try
		{
			if(attH>0)
			{
				cy += 5;
				attsY0 = new int[attachments.length];
				for(int i=0; i<attachments.length; i++)
				{
					Attachment at = attachments[i];
					if(at==null) continue;
					attsY0[i] = cy;
					if(at instanceof PhotoAttachment)
					{
						PhotoAttachment pa = (PhotoAttachment) at;
						if(pa.renderImg == null)
						{
							if(Settings.isLiteOrSomething) {
								g.drawString("Фотография", textX, y+cy, 0);
							} else
								g.drawString("Не удалось загрузить изображение", textX, y+cy, 0);
						}
						else
						{
							g.drawImage(pa.renderImg, (dw-pa.renderW)/2, y+cy, 0);
						}
					}
					else if(at instanceof VideoAttachment)
					{
						VideoAttachment va = (VideoAttachment) at;
						if(va.renderImg == null)
						{
							if(Settings.isLiteOrSomething)
							{
								g.drawString("Видео", textX, y+cy, 0);
							}
							else
								g.drawString("Не удалось загрузить изображение", textX, y+cy, 0);
						}
						else
						{
							g.drawImage(va.renderImg, (dw-va.renderW)/2, y+cy, 0);
							g.drawString(va.title, textX, y+cy+va.renderH, 0);
						}
					}
					else if(at instanceof DocumentAttachment)
					{
						((DocumentAttachment) at).draw(g, textX, y+cy, dw - textX*2);
					}
					else
					{
						attsY0[i] = 65535;
					}
					
					cy += at.getDrawHeight();
				}
			}
			else
			{
				attsY0 = null;
			}
		}
		catch (Exception e) 
		{ 
			attsY0 = null;
		}
		
		cy+=10;
		ColorUtils.setcolor(g, ColorUtils.OUTLINE);
		
		g.drawImage(IconsManager.ico[liked?IconsManager.LIKE_F:IconsManager.LIKE], 24, y+cy, 0);
		String likesS = String.valueOf(likes);
		g.drawString(likesS, 60, y+cy+12 - fh/2, 0);
		
		repX = 60+12+f.stringWidth(likesS);
		g.drawImage(IconsManager.ico[IconsManager.REPOST], 60+12+f.stringWidth(likesS), y+cy, 0);
		String repostsS = String.valueOf(reposts);
		g.drawString(repostsS, 60+48+f.stringWidth(likesS), y+cy+12 - fh/2, 0);
		
		comX = 120+f.stringWidth(likesS)+f.stringWidth(repostsS);
		g.drawImage(IconsManager.ico[IconsManager.COMMENTS], 120+f.stringWidth(likesS)+f.stringWidth(repostsS), y+cy, 0);
		String comsS = String.valueOf(comments);
		g.drawString(comsS, 120+f.stringWidth(likesS)+f.stringWidth(repostsS)+32, y+cy+12 - fh/2, 0);
		
		cy+=40;
		itemDrawHeight = cy;
	}
	
	public void loadAtts()
	{
		if(attH<=0)
		{
			attH = 0;
			// prepairing attachments
			try {
				for(int i=0; i<attachments.length; i++)
				{
					Attachment at = attachments[i];
					if(at==null) continue;

					if(at instanceof PhotoAttachment)
					{
						((PhotoAttachment) at).loadForNews();
					}
					if(at instanceof VideoAttachment)
					{
						((VideoAttachment) at).loadForMessage();
					}
					if(at instanceof StickerAttachment)
					{
						int stickerH = DisplayUtils.width > 250 ? 128 : 64;
						attH += stickerH + 5;
					}
					else
					{
						attH += at.getDrawHeight() + 5;
					}
				}
				if(attH != 0) { attH += 5; }
			}
			catch (Exception e)
			{
				attH = 0;
				VikaTouch.sendLog(e.toString());
			}
		}
	}

	public void getRes()
	{
		(new Thread() {
			
			public void run() {
				ava = VikaTouch.cameraImg;
				if(!Settings.dontLoadAvas && avaurl != null && !dontLoadAva)
				{
					try
					{
						dontLoadAva = true;
						ava = VikaUtils.downloadImage(avaurl);
					}
					catch (Exception e)
					{
						ava = VikaTouch.cameraImg;
					}
				}
				loadAtts();
			}
		}).start();
	}
	
	public int getDrawHeight()
	{
		return itemDrawHeight;
	}

	public void tap(int x, int y)
	{
		boolean post = true;
		if(y >= itemDrawHeight-45)
		{
			post = false;
			if(x<repX)
			{
				like(!liked);
			}
			
		}
		else if(attsY0!=null)
		{
			for(int i=0; i<attachments.length;i++)
			{
				if(attsY0[i]!=0 && y > attsY0[i])
				{
					post = false;
					attachments[i].press();
				}
			}
		}
		
		if(post) options(false);
	}

	private void options(boolean keys) {
		int h = 50;
		OptionItem[] o = new OptionItem[keys?3:2];
		o[0] = new OptionItem(this,name==null?"Page":name,IconsManager.FRIENDS,1,h);
		o[1] = new OptionItem(this,TextLocal.inst.get("wall.links"),IconsManager.LINK,2,h);
		if(keys)
		{
			o[3] = new OptionItem(this,TextLocal.inst.get(liked?"wall.unlike":"wall.like"),liked?IconsManager.LIKE_F:IconsManager.LIKE,3,h);
		}
		VikaTouch.popup(new AutoContextMenu(o));
	}

	public void keyPressed(int key)
	{
		if(key == -5) options(true);
	}

	public boolean canSave() {
		return false;
	}
	public void save() { }

	public boolean canLike() {
		return canLike;
	}

	public boolean getLikeStatus() {
		return liked;
	}

	public void like(final boolean val) {
		new Thread()
		{
			public void run ()
			{
				VikaTouch.loading = true;
				URLBuilder url;
				if(val)
				{
					url = new URLBuilder("likes.add");
				}
				else
				{
					url = new URLBuilder("likes.delete");
				}
				url.addField("type", "post").addField("owner_id", ownerid).addField("item_id", id);
				String res = VikaUtils.download(url);
				if(res==null)
				{
					VikaTouch.popup(new InfoPopup("Ошибка", null));
				}
				liked = val;
				VikaTouch.loading = false;
			}
		}.start();
	}

	public void send() {
		// TODO Auto-generated method stub
		
	}

	public void repost() {
		// TODO Auto-generated method stub
		
	}

	public boolean commentsAliveable() {
		// TODO Auto-generated method stub
		return false;
	}

	public void openComments() {
		// TODO Auto-generated method stub
		
	}

	public void onMenuItemPress(int i) {
		if(i==1)
		{
			VikaTouch.setDisplay(VikaUtils.openPage(xx), 1);
		}
		else if(i==2)
		{
			String[] links = VikaUtils.searchLinks(text);
			int c = 0;
			while(links[c]!=null)
			{
				c++;
			}
			if(c==0)
			{
				VikaTouch.popup(new InfoPopup("Ссылки не найдены либо произошла ошибка.", null));
			}
			else
			{
				OptionItem[] opts2 = new OptionItem[c];
				int h = DisplayUtils.height>240?36:30; // вот как делается адаптация, а не твои километровые свитчи и да, я буду ещё долго ворчать.
				for(int j = 0; j < c; j++)
				{
					int icon = IconsManager.LINK;
					if(links[j].indexOf("id")==0) { icon = IconsManager.FRIENDS; }
					if(links[j].indexOf("club")==0) { icon = IconsManager.GROUPS; }
					if(links[j].indexOf("rtsp")==0) { icon = IconsManager.VIDEOS; }
					opts2[j] = new OptionItem(this, links[j], icon, -j, h);
				}
				VikaTouch.popup(new AutoContextMenu(opts2));
			}
		}
		else if(i==3)
		{
			like(!liked);
		}
		else
		{
			try
			{
				String s = VikaUtils.searchLinks(text)[-i];
				VikaUtils.openLink(s);
			}
			catch (RuntimeException e) 
			{
			}
		}
	}

	public void onMenuItemOption(int i) {
		
	}
}
