package vikatouch.items.music;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.items.UIItem;
import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.items.JSONUIItem;
import vikatouch.music.MusicPlayer;
import vikatouch.screens.music.MusicScreen;
import vikatouch.utils.VikaUtils;

public class AudioTrackItem 
	extends JSONUIItem implements UIItem 
{
	public int id;
	public int owner_id;
	public String name;
	public String artist; // исполнтель, ну, вторая строка
	public int length;
	private String lengthS;
	private MusicScreen playlist;
	private int indexInPL;
	public String mp3;
	
	public AudioTrackItem(JSONObject json, MusicScreen s, int i)
	{
		super(json);
		setDrawHeight();
		playlist = s;
		indexInPL = i;
	}

	public void parseJSON()
	{
		System.out.println(json.toString());

		try
		{
			name = json.optString("title");
			artist = json.optString("artist");
			id = json.optInt("id");
			owner_id = json.optInt("owner_id");
			length = json.optInt("duration");
			lengthS = (length/60)+":"+(length%60<10?"0":"")+(length%60);
			mp3 = json.optString("url", "");
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
		itemDrawHeight = 50;
	}

	public void paint(Graphics g, int y, int scrolled)
	{
		//g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
		if(ScrollableCanvas.keysMode && selected)
		{
			ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
			g.fillRect(0, y, DisplayUtils.width, itemDrawHeight);
		}
		ColorUtils.setcolor(g, 0);
		if(name != null)
			g.drawString(name, 73, y, 0);
		ColorUtils.setcolor(g, ColorUtils.OUTLINE);
		if(artist!=null) g.drawString(artist, 73, y + 24, 0);
		if(lengthS!=null) g.drawString(lengthS, DisplayUtils.width-10-g.getFont().stringWidth(lengthS), y, 0);
		g.drawImage(IconsManager.ico[IconsManager.MUSIC], 20, y+13, 0);
	}

	public void tap(int x, int y)
	{
		keyPressed(-5);
	}
	
	public void keyPressed(int key)
	{
		if(key == KEY_OK)
		{
			if(MusicPlayer.inst == null)
			{
				System.out.println("Calling player");
				MusicPlayer.launch(playlist, indexInPL);
			}
			else if(MusicPlayer.inst.playlist == playlist)
			{
				if(MusicPlayer.inst.current == indexInPL)
				{
					VikaTouch.setDisplay(MusicPlayer.inst, 1);
				}
				else
				{
					MusicPlayer.inst.current = indexInPL;
					VikaTouch.setDisplay(MusicPlayer.inst, 1);
					MusicPlayer.inst.loadTrack();
				}
			}
			else
			{
				MusicPlayer.inst.destroy();
				MusicPlayer.launch(playlist, indexInPL);
			}
		}
	}
}
