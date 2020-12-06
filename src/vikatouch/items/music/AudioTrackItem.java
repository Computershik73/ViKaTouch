package vikatouch.items.music;

import javax.microedition.lcdui.Graphics;

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

public class AudioTrackItem extends JSONUIItem implements UIItem {
	public int id;
	public int owner_id;
	public String name;
	public String artist; // исполнтель, ну, вторая строка
	public int length;
	public String lengthS;
	public MusicScreen playlist;
	public int indexInPL;
	public String mp3;

	public AudioTrackItem() {
		id = 0;
		owner_id = 0;
		name = "";
		artist = "";
		length = 5;
		lengthS = "null";
		playlist = null;
		mp3 = "";
	}

	public AudioTrackItem(JSONObject json, MusicScreen s, int i) {
		super(json);
		setDrawHeight();
		playlist = s;
		indexInPL = i;
	}

	public void parseJSON() {
		setDrawHeight();
		if (json == null)
			return;

		try {
			name = json.optString("title").intern();
			artist = json.optString("artist").intern();
			id = json.optInt("id");
			owner_id = json.optInt("owner_id");
			length = json.optInt("duration");
			lengthS = (length / 60) + ":" + (length % 60 < 10 ? "0" : "") + (length % 60);
			mp3 = json.optString("url", "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		disposeJson();

		System.gc();
	}

	private void setDrawHeight() {
		itemDrawHeight = 50;
	}

	public void paint(Graphics g, int y, int scrolled) {
		if (y + scrolled + itemDrawHeight < -50)
			return;
		// g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
		ColorUtils.setcolor(g, (ScrollableCanvas.keysMode && selected) ? ColorUtils.BUTTONCOLOR : 0);
		if (name != null)
			g.drawString(name, 48, y, 0);
		ColorUtils.setcolor(g, ColorUtils.OUTLINE);
		if (lengthS != null)
			g.drawString(lengthS, DisplayUtils.width - 10 - g.getFont().stringWidth(lengthS), y, 0);
		int icon = IconsManager.MUSIC;
		if (MusicPlayer.inst != null && playlist == MusicPlayer.inst.playlist
				&& indexInPL == MusicPlayer.inst.current) {
			icon = IconsManager.PLAY;
			// ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
		}
		if (artist != null)
			g.drawString(artist, 48, y + 24, 0);

		g.drawImage(((ScrollableCanvas.keysMode && selected) ? (IconsManager.selIco) : (IconsManager.ico))[icon], 12,
				y + 13, 0);
	}

	public void tap(int x, int y) {
		keyPressed(-5);
	}

	public void keyPressed(int key) {
		if (key == KEY_OK) {
			if (MusicPlayer.inst == null) {
				// System.out.println("Calling player");
				MusicPlayer.launch(playlist, indexInPL);
			} else if (MusicPlayer.inst.playlist == playlist) {
				if (MusicPlayer.inst.current == indexInPL) {
					restorePlayerScreen();
				} else {
					MusicPlayer.inst.current = indexInPL;
					restorePlayerScreen();
					MusicPlayer.inst.loadTrack();
				}
			} else {
				MusicPlayer.inst.destroy();
				MusicPlayer.launch(playlist, indexInPL);
			}
		}
	}

	public void restorePlayerScreen() {
		if (MusicPlayer.inst.backScreenIsPlaylist()) {
			VikaTouch.setDisplay(MusicPlayer.inst, 1);
		} else
			VikaTouch.setDisplay(playlist.backScreen, -1);
	}
}
