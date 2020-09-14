package vikatouch.attachments;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.popup.ContextMenu;
import ru.nnproject.vikaui.popup.ImagePreview;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.items.menu.OptionItem;
import vikatouch.music.MusicPlayer;
import vikatouch.screens.music.MusicScreen;
import vikatouch.utils.error.ErrorCodes;

public class AudioAttachment 
extends Attachment implements IMenu
{
	public AudioAttachment() {
		this.type = "audio";
	}
	
	public String name;
	public String url;
	private int docType;
	public int size;
	private String ext;
	public String musUrl;

	public void parseJSON() 
	{
		try
		{
			name = json.optString("artist")+ " "+json.optString("title");
			url = fixJSONString(json.optString("url"));
			size = json.optInt("duration");
			ext = json.optString("ext");
			//docType = json.optInt("type");
			VikaTouch.sendLog(name+" "+url+" "+size+" "+ext);
			if(!json.isNull("url"))
			{
			/*	PhotoSize[] prevSizes = PhotoSize.parseSizes(json.getJSONObject("preview").getJSONObject("photo").getJSONArray("sizes"));

				PhotoSize prevPs = null;
				try
				{
					prevPs = PhotoSize.getSize(prevSizes, "x");
					if(prevPs==null) throw new Exception();
				}
				catch (Exception e1)
				{
					try
					{
						prevPs = PhotoSize.getSize(prevSizes, "o");
					}
					catch (Exception e2)
					{ }
				}
				if(prevPs != null)*/
					musUrl = fixJSONString(json.optString("url"));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			VikaTouch.error(e, ErrorCodes.DOCPARSE);
		}

		System.gc();
	}
	public int getDrawHeight()
	{
		return 40;
	}
	public void draw(Graphics g, int x1, int y1, int w)
	{
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(x1, y1, w, getDrawHeight());
		g.drawImage(IconsManager.ico[IconsManager.MUSIC], x1+4, y1+8, 0);
		ColorUtils.setcolor(g, ColorUtils.COLOR1);
		Font f = Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_SMALL);
		g.setFont(f);
		if(name!=null)
			g.drawString(name, x1+34, y1 + 10 - f.getHeight()/2, 0);
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		f = Font.getFont(0, 0, Font.SIZE_SMALL);
		g.setFont(f);
		g.drawString(String.valueOf(size)+ " sec", x1+34, y1 + 30 - f.getHeight()/2, 0);
	}
	
	public void press()
	{
		OptionItem[] i = new OptionItem[musUrl==null?1:2];
		i[0] = new OptionItem(this, "Скачать", IconsManager.MUSIC, 0, 50);
		if(musUrl!=null)
		{
			i[1] = new OptionItem(this, "Открыть", IconsManager.MUSIC, 1, 50);
		}
		VikaTouch.popup(new ContextMenu(i));
	}
	public void onMenuItemPress(int i) {
		if(i==0)
		{
			try
			{
				VikaTouch.appInst.platformRequest(url);
			}
			catch(Exception e)
			{ }
		}
		else if(i==1&&musUrl!=null)
		{
			MusicScreen ms = new MusicScreen();
			ms.loadAtt(this);
			MusicPlayer.launch(ms, 0);
		}
	}
	public void onMenuItemOption(int i) { }

}
