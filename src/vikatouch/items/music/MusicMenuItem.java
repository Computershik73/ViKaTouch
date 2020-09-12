package vikatouch.items.music;

import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.items.menu.OptionItem;
import vikatouch.music.MusicPlayer;

// а-ля по канонам ООП
public class MusicMenuItem extends OptionItem {

	public String origStr;
	public MusicMenuItem(IMenu m, String t, int ic, int i, int h) {
		super(m, t, ic, i, h);
		origStr = t;
	}
	
	public void paint(Graphics g, int y, int scrolled)
	{
		icon = (MusicPlayer.inst==null)?IconsManager.MUSIC:IconsManager.PLAY;
		text = (MusicPlayer.inst==null)?origStr:(origStr+" ("+MusicPlayer.inst.title+")");
		super.paint(g, y, scrolled);
	}

}
