package vikatouch.attachments;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.music.MusicPlayer;
import vikatouch.screens.music.MusicScreen;
import vikatouch.utils.error.ErrorCodes;

public class AudioAttachment extends DocumentAttachment {
	public AudioAttachment() {
		this.type = "audio";
	}

	public String name;
	public String url;
	public int size;
	public String musUrl;

	public void parseJSON() {
		try {
			name = json.optString("artist") + " " + json.optString("title");
			url = fixJSONString(json.optString("url"));
			size = json.optInt("duration");
			if (!json.isNull("url")) {
				musUrl = fixJSONString(json.optString("url"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			VikaTouch.error(e, ErrorCodes.DOCPARSE);
		}

		System.gc();
	}

	public int getDrawHeight() {
		return 40;
	}

	public void draw(Graphics g, int x1, int y1, int w) {
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(x1, y1, w, getDrawHeight());
		g.drawImage(IconsManager.ico[IconsManager.MUSIC], x1 + 4, y1 + 8, 0);
		ColorUtils.setcolor(g, ColorUtils.COLOR1);
		Font f = Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_SMALL);
		g.setFont(f);
		if (name != null)
			g.drawString(name, x1 + 34, y1 + 10 - f.getHeight() / 2, 0);
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		f = Font.getFont(0, 0, Font.SIZE_SMALL);
		g.setFont(f);
		g.drawString(String.valueOf(size) + " sec", x1 + 34, y1 + 30 - f.getHeight() / 2, 0);
	}

	public void press() {
		// в плеере скачать можно.
		MusicScreen ms = new MusicScreen();
		ms.loadAtt(this);
		MusicPlayer.launch(ms, 0);
	}

}
