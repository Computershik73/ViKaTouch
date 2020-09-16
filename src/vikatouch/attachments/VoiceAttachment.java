package vikatouch.attachments;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import vikatouch.VikaTouch;
import vikatouch.items.chat.MsgItem;
import vikatouch.locale.TextLocal;
import vikatouch.music.MusicPlayer;
import vikatouch.screens.music.MusicScreen;
import vikatouch.utils.error.ErrorCodes;

public class VoiceAttachment 
	extends DocumentAttachment 
{

	public VoiceAttachment() {
		this.type = "audio_message";
	}
	
	public String name;
	public String url;
	public int size;
	public String length;
	public String musUrl;
	public String text;
	public String[] textB;
	public int lastW = 0;

	public void parseJSON() 
	{
		try
		{
			name = TextLocal.inst.get("msg.attach.voice");
			size = json.optInt("duration");
			length = MusicPlayer.time(size);
			if(!json.isNull("url"))
			{
				musUrl = fixJSONString(json.optString("link_mp3"));
			}
			text = json.optString("transcript");
			if(text!=null && text.length()>2)
			{
				textB = TextBreaker.breakText(text, Font.getFont(0, 0, 8), MsgItem.msgWidth-30);
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
		int th = 0;
		if(textB!=null) th = (textB.length * Font.getFont(0, 0, 8).getHeight())+5;
		return 40+th;
	}
	public void draw(Graphics g, int x1, int y1, int w)
	{
		if(lastW!=w)
		{
			if(text!=null && text.length()>2)
			{
				textB = TextBreaker.breakText(text, Font.getFont(0, 0, 8), w);
			}
		}
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(x1, y1, w, 40);
		g.drawImage(IconsManager.ico[IconsManager.VOICE], x1+4, y1+8, 0);
		ColorUtils.setcolor(g, ColorUtils.COLOR1);
		Font f = Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_SMALL);
		g.setFont(f);
		if(name!=null)
			g.drawString(name, x1+34, y1 + 10 - f.getHeight()/2, 0);
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		f = Font.getFont(0, 0, Font.SIZE_SMALL);
		g.setFont(f);
		g.drawString(length, x1+34, y1 + 30 - f.getHeight()/2, 0);
		try
		{
			if(textB!=null)
			{
				for(int i=0;i<textB.length;i++)
				{
					g.drawString(textB[i], x1, y1+45+f.getHeight()*i, 0);
				}
			}
		}
		catch (Exception e) { }
	}
	
	public void press()
	{
		// в плеере скачать можно. 
		//MusicScreen ms = new MusicScreen();
		//ms.loadAtt(this);
		//MusicPlayer.launch(ms, 0);
	}
}
