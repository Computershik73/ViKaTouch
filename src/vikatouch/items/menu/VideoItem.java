package vikatouch.items.menu;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikatouch.VikaTouchApp;
import ru.nnproject.vikaui.popup.ConfirmBox;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.items.JSONUIItem;
import vikatouch.settings.Settings;
import vikatouch.utils.ResizeUtils;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.emulatordetect.EmulatorDetector;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLDecoder;

public class VideoItem
	extends JSONUIItem
{
	public int id;
	public int owner;
	public String title;
	public int length;
	public String iconUrl;
	public Image iconImg;
	public String descr;
	public int views;
	
	//private static Image downloadBI = null;
	
	public String file;
	public String playerUrl;
	public String external = null;
	
	public VideoItem(JSONObject json)
	{
		super(json);
	}
	
	public void parseJSON()
	{
		id = json.optInt("id");
		owner = json.optInt("owner_id");
		title = json.optString("title");
		descr = json.optString("description");
		try {
			iconUrl = fixJSONString(json.getJSONArray("image").getJSONObject(0).optString("url"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		length = json.optInt("duration");
		playerUrl = json.optString("player");
		views = json.optInt("views");
		itemDrawHeight = 52;
		
		try {
			JSONObject files = json.getJSONObject("files");
			if(files!=null)
			{
				external = files.optString("external");
				if(external!=null&&external.length()>6)
				{
					external = fixJSONString(external);
				}
				else
				{
					external = null;
					file = files.optString("mp4_"+Settings.videoResolution);
					if(file==null)
					{
						file = files.optString("mp4_360");
					}
					if(file==null)
					{
						file = files.optString("mp4_240");
					}
					if(file!=null) file = fixJSONString(file);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public int getDrawHeight() { return itemDrawHeight; }
	
	public void loadIcon()
	{
		try
		{
			iconImg = ResizeUtils.resizeItemPreview(VikaUtils.downloadImage(iconUrl));
		}
		catch (Exception e) { }
	}
	
	public void tap(int x, int y)
	{
		if(x<DisplayUtils.width - 50)
		{
			keyPressed(KEY_OK);
		}
		else
		{
			keyPressed(KEY_FUNC);
		}
	}

	public void keyPressed(int key)
	{
		if(external==null)
		{
			if(key == KEY_FUNC)
			{
				if(file!=null)
					VikaTouch.popup(new ConfirmBox("Загрузить видео-файл?","Будет скачано "+Settings.videoResolution+"p.", new Runnable() {
						public void run() 
						{
							try {
								VikaTouch.appInst.platformRequest(file);
							} catch (ConnectionNotFoundException e) {
								e.printStackTrace();
							}
						}
					}, null));
			}
			if(key == KEY_OK)
			{
				if(EmulatorDetector.isEmulator)
				{
					try
					{
						VikaTouch.appInst.platformRequest(playerUrl);
					}
					catch (ConnectionNotFoundException e) 
					{ }
				}
				else
				{
					if(file!=null)
					{
						VikaTouch.popup(new ConfirmBox("Выбрано разрешение: "+Settings.videoResolution+"p", "Воспроизвести?", new Runnable() {
							public void run() 
							{
								playOnline();
							}
						}, null));
					}
					else
					{
						VikaTouch.popup(new InfoPopup("Файл видео недоступен.", null));
					}
				}
			}
		}
		else
		{
			if(key == KEY_OK)
			{
				VikaTouch.popup(new ConfirmBox(external, "Открыть?", new Runnable() {
					public void run() 
					{
						playExternal();
					}
				}, null));
			}
		}
	}
	public void playExternal()
	{
		// https://vikamobile.ru/getl.php?url=
		try {
			if(external.indexOf("youtube")==-1 || !Settings.symtube)
			{
				VikaTouch.appInst.platformRequest(external);
			}
			else
			{
				VikaTouch.appInst.platformRequest("https://vikamobile.ru/getl.php?url="+URLDecoder.encode(external));
			}
		} catch (ConnectionNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void playOnline()
	{
		VikaTouch.callSystemPlayer(file);
	}

	public void paint(Graphics g, int y, int scrolled)
	{
		if(ScrollableCanvas.keysMode && selected)
		{
			ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
			g.fillRect(0, y, DisplayUtils.width, itemDrawHeight);
		}
		ColorUtils.setcolor(g, 0);
		if(title != null)
			g.drawString(title, 73, y, 0);
		ColorUtils.setcolor(g, ColorUtils.OUTLINE);
		int sec = length%60;
		int min = length/60;
		String subStr = min+":"+(sec<10?"0":"")+sec+"    "+views+" ";
		g.drawString(subStr, 73, y + 24, 0);
		g.drawImage(IconsManager.ico[IconsManager.VIEWS], 73+g.getFont().stringWidth(subStr)-2, y+24, 0);
		if(iconImg != null)
		{
			g.drawImage(iconImg, 14, y+1, 0);
		}
		if(!ScrollableCanvas.keysMode && external == null)
		{
			try
			{
				int iy = (itemDrawHeight - 24) / 2;
				if(iy < 0)
					iy = 0;
				iy += y;
				g.drawImage(IconsManager.ico[IconsManager.DOWNLOAD], DisplayUtils.width - 24, y, 0);
			} 
			catch (Exception e)
			{
				
			}
		}
	}

}
