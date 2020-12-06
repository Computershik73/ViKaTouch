package vikatouch.attachments;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.popup.ContextMenu;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import vikatouch.VikaTouch;
import vikatouch.items.chat.MsgItem;
import vikatouch.items.menu.OptionItem;
import vikatouch.locale.TextLocal;
import vikatouch.music.MusicPlayer;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;

public class VoiceAttachment extends DocumentAttachment {

	public VoiceAttachment() {
		this.type = "audio_message";
	}

	public static String name;
	public String url;
	public int size;
	public String length;
	public String musUrl;
	public String text;
	public String[] textB;
	public int lastW = 0;
	public long mid;

	public void parseJSON() {
		try {
			name = TextLocal.inst.get("msg.attach.voice");
			size = json.optInt("duration");
			length = MusicPlayer.time(size);
			musUrl = fixJSONString(json.optString("link_mp3"));
			text = json.optString("transcript");
			if (text != null && text.length() > 2) {
				textB = TextBreaker.breakText(text, Font.getFont(0, 0, 8), MsgItem.msgWidth - 30);
			}
		} catch (Exception e) {
			e.printStackTrace();
			VikaTouch.error(e, ErrorCodes.DOCPARSE);
		}

		System.gc();
	}

	public int getDrawHeight() {
		int th = 0;
		if (textB != null)
			th = (textB.length * Font.getFont(0, 0, 8).getHeight()) + 5;
		return 40 + th;
	}

	public void draw(Graphics g, int x1, int y1, int w) {
		if (lastW != w) {
			if (text != null && text.length() > 2) {
				textB = TextBreaker.breakText(text, Font.getFont(0, 0, 8), w);
			}
		}
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(x1, y1, w, 40);
		g.drawImage(IconsManager.ico[IconsManager.VOICE], x1 + 4, y1 + 8, 0);
		ColorUtils.setcolor(g, ColorUtils.COLOR1);
		Font f = Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_SMALL);
		g.setFont(f);
		if (name != null)
			g.drawString(name, x1 + 34, y1 + 10 - f.getHeight() / 2, 0);
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		f = Font.getFont(0, 0, Font.SIZE_SMALL);
		g.setFont(f);
		g.drawString(length, x1 + 34, y1 + 30 - f.getHeight() / 2, 0);
		try {
			if (textB != null) {
				for (int i = 0; i < textB.length; i++) {
					g.drawString(textB[i], x1, y1 + 45 + f.getHeight() * i, 0);
				}
			}
		} catch (Exception e) {
		}
	}

	public void press() {
		OptionItem[] i = new OptionItem[2];
		i[0] = new OptionItem(this, "Прослушать", IconsManager.PLAY, 0, 50);
		i[1] = new OptionItem(this, "Транскрипт", IconsManager.EDIT, 1, 50);
		VikaTouch.popup(new ContextMenu(i));
		// в плеере скачать можно.
		// MusicScreen ms = new MusicScreen();
		// ms.loadAtt(this);
		// MusicPlayer.launch(ms, 0);
	}

	public void onMenuItemPress(int i) {
		if (i == 0) {
			try {
				MusicPlayer mp = new MusicPlayer();
				mp.voice = this;
				mp.firstLoad();
				VikaTouch.setDisplay(mp, 1);
			} catch (Exception e) {
			}
		} else if (i == 1) {
			String x = "0";
			try {
				x = VikaUtils.download(new URLBuilder("messages.getById").addField("message_ids", String.valueOf(mid))
						.addField("extended", 1));
				JSONObject r = new JSONObject(x).getJSONObject("response").getJSONArray("items").getJSONObject(0);
				MsgItem m = new MsgItem(r);
				m.parseJSON();

				m.loadAtts();

				VoiceAttachment v = m.findVoice();
				if (v != null) {
					VikaTouch.popup(new InfoPopup(v.text, null));
				}
			} catch (JSONException e) {
				VikaTouch.popup(new InfoPopup("Transcript parse error", null));
				VikaTouch.sendLog(x);
			} catch (Exception e) {
				VikaTouch.popup(new InfoPopup("Transcript loading error", null));
				VikaTouch.sendLog("Voice transcript: " + e.toString());
			}
		}
	}
}
