package vikatouch.items;

import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.items.UIItem;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import vikatouch.VikaTouch;
import vikatouch.attachments.Attachment;
import vikatouch.attachments.PhotoAttachment;
import vikatouch.attachments.PhotoSize;
import vikatouch.items.chat.MsgItem;
import vikatouch.screens.NewsScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;

public class PostItem
	extends JSONUIItem
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
	public boolean canlike;
	public String copyright;
	public int replyownerid;
	public int replypostid;
	public Image prevImage;
	private String avaurl;
	private String[] drawText;
	public String name = "";
	public Image ava;
	public boolean isreply;
	private boolean largefont;
	private int sourceid;
	private boolean full;
	private String reposterName;
	private String type;
	private String data;
	private boolean dontLoadAva;
	protected boolean hasPrevImg;
	
	public void parseJSON()
	{
		super.parseJSON();
		super.parseAttachments();
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
		try
		{
			likes = json2.optJSONObject("likes").optInt("count");
			reposts = json2.optJSONObject("reposts").optInt("count");
			views = json2.optJSONObject("views").optInt("count");
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
		
		type = json2.optString("type"); 
		
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
		}
		//itemDrawHeight = 82;
		isreply = replypostid != 0;
		itemDrawHeight = 72;
		int xx = 0;
		xx = replyownerid;
		if(xx == 0)
			xx = fromid;
		if(xx == 0)
			xx = ownerid;
		if(xx == 0)
			xx = sourceid;
		labelgetnameandphoto:
		{
			if(xx < 0)
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
		}
		
		boolean b1 = false;
		boolean b2 = false;
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
		
		
		
		if(reposterName != null)
		{
			itemDrawHeight += 43;
		}

		getPhotos();
		
		if(data != null && data.equalsIgnoreCase("profile_photo"))
		{
			text = "обновил фотографию на странице";
		}
		
		drawText = TextBreaker.breakText(text, largefont, this, full, DisplayUtils.width - 32);
		
		System.gc();
	}
	
	private void getPhotos() 
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					if(attachments[0] != null)
					{
						if(attachments[0] instanceof PhotoAttachment)
						{
							hasPrevImg = true;
							try
							{
								((PhotoAttachment)attachments[0]).loadForNews();
								prevImage = ((PhotoAttachment)attachments[0]).renderImg;
							}
							catch (Exception e2)
							{
								e2.printStackTrace();
							}
						}
						if(prevImage != null)
						{
							itemDrawHeight += prevImage.getHeight() + 16;
						}
					}
				}
				catch (Exception e)
				{
					VikaTouch.error(e, ErrorCodes.POSTIMAGE);
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void paint(Graphics g, int y, int scrolled)
	{
		int yy = 10 + y;
		
		getAva();
		
		if(ava != null)
		{
			g.drawImage(ava, 14, 10 + y, 0);
			yy += ava.getHeight() + 12;
		}
		else
		{
			g.drawImage(VikaTouch.cameraImg, 14, 10 + y, 0);
			yy += VikaTouch.cameraImg.getHeight() + 12;
		}
		

		g.drawImage(IconsManager.ac, 14, 10 + y, 0);
		
		ColorUtils.setcolor(g, 5);
		
		g.drawString("" + name, 76, 18 + y, 0);

		if(largefont)
			g.setFont(Font.getFont(0, 0, Font.SIZE_LARGE));
		else
			g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
		if(drawText != null)
		{
			for(int i = 0; i < drawText.length; i++)
			{
				if(drawText[i] != null)
				{
					if(drawText[i].length() > 0)
					{
						if(i == 9 && drawText.length == 10)
							g.setColor(68, 104, 143);
						
						g.drawString(""+drawText[i], 16, yy, 0);
						
						ColorUtils.setcolor(g, 5);
					}
					yy += 24;
				}
				else
				{
					break;
				}
			}
		}
		
		if(largefont)
			g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));

		if(prevImage != null)
		{
			int ix = (DisplayUtils.width - prevImage.getWidth()) / 2;
			g.drawImage(prevImage, ix, yy + 3, 0);
		}
	}

	private void getAva()
	{
		if(!Settings.dontLoadAvas)
		{
			if(avaurl != null && ava == null && !dontLoadAva)
			{
				new Thread(new Runnable(){
				
					public void run() {

						try
						{
				dontLoadAva = true;
				ava = VikaUtils.downloadImage(avaurl);
					}
					catch (Exception e)
					{
						
					}
				}}).start();
			}
		}
	}

	public void tap(int x, int y)
	{
		full = true;
		parseJSON();
	}

	public void keyPressed(int key)
	{
		
	}
}
