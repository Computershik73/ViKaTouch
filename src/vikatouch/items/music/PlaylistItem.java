package vikatouch.items.music;

import java.io.IOException;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.attachments.PhotoSize;
import vikatouch.items.JSONUIItem;
import vikatouch.screens.menu.DocsScreen;
import vikatouch.screens.music.MusicScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.ResizeUtils;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;

public class PlaylistItem extends JSONUIItem {
	public int id;
	public int owner_id;
	public String name;
	private String iconUrl;
	private Image iconImg;
	private int size;
	private String bigCoverUrl;
	
	public PlaylistItem(JSONObject json)
	{
		super(json);
		setDrawHeight();
	}

	public void parseJSON()
	{
		System.out.println(json.toString());

		try
		{
			name = json.optString("title");
			size = json.optInt("count");
			owner_id = json.optInt("owner_id");
			id = json.optInt("id");
			iconUrl = fixJSONString(json.getJSONObject("photo").optString("photo_135"));
			bigCoverUrl = fixJSONString(json.getJSONObject("photo").optString("photo_600"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		setDrawHeight();
		System.gc();
	}

	private void setDrawHeight()
	{
		itemDrawHeight = 102;
	}

	public void paint(Graphics g, int y, int scrolled)
	{
		/*if(ScrollableCanvas.keysMode && selected)
		{
			ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
			g.fillRect(0, y, DisplayUtils.width, itemDrawHeight);
		}*/
		
		if(iconImg == null)
		{
			ColorUtils.setcolor(g, 6);
			g.fillRect(1, y+1, 100, 100);
		}
		else
		{
			g.drawImage(iconImg, 1, y+1, 0);
		}

		ColorUtils.setcolor(g, 0);
		g.setFont(Font.getFont(0, 0, Font.SIZE_MEDIUM));
		if(name != null)
			g.drawString(name, 102, y+12, 0);
		ColorUtils.setcolor(g, ColorUtils.OUTLINE);
		g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
		g.drawString(size+" аудиозаписей", 102, y + 46, 0);
	}

	private Image getIcon()
	{
		Image img = null;
		try
		{
			if(!Settings.dontLoadAvas)
				img = VikaUtils.resize(VikaUtils.downloadImage(iconUrl), 100, 100);
		}
		catch (Exception e)
		{
			
		}
		return img;
	}
	
	public void loadIcon()
	{
		iconImg = getIcon();
	}
	
	public void open() {
		MusicScreen pls = new MusicScreen();
		pls.load(owner_id,id,name);
		pls.coverUrl = bigCoverUrl;
		VikaTouch.setDisplay(pls, 1);
	}

	public void tap(int x, int y)
	{
		open();
	}
	
	public void keyPressed(int key)
	{
		if(key == KEY_OK)
		{
			open();
		}
	}
}
