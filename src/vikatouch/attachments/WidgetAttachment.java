package vikatouch.attachments;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.items.OptionItem;
import ru.nnproject.vikaui.popup.ContextMenu;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import vikatouch.VikaTouch;
import vikatouch.items.chat.MsgItem;
import vikatouch.locale.TextLocal;
import vikatouch.music.MusicPlayer;
import vikatouch.screens.NewsScreen;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;

/**
 * @author Shinovon
 * 
 */
public class WidgetAttachment extends Attachment {
	public class row {
		public String title;
		public String subtitle;
		public String textposition;
		public String iconposition;
		public String iconurl;
		public Image icon;
		public String righttext;
		public String righttextposition;
	}
	//public static String name;
	//public String url;
	public Image headerImage; 
	public String headerTitle;
	public row[] rows;
	public int rowscount;
	/*public String[] titlerows;
	public String[] subtitlerows;
	public int size;
	public String length;
	public String musUrl;
	public String text;
	public String[] textB;
	public int lastW = 0;
	public long mid;*/
	
	
	
	
	public WidgetAttachment() {
		this.type = "widget";
	}

	

	public static String title = "";

	public void parseJSON() {
		this.type = "widget";
		String er="0";
		try {
			JSONObject jitem = json.getJSONObject("item");
			
			//VikaTouch.sendLog(jitem.toString());
			JSONObject payload = jitem.getJSONObject("payload");
			er="1";
			if (payload.has("header_title")) {
				er="1";
				headerTitle=payload.optString("header_title");
				er="2";
				try {
				headerImage = VikaUtils.resize(VikaUtils.downloadImage(payload.optJSONArray("header_icon").optJSONObject(0).optString("url")), 20, 20);
				} catch (Throwable eee) {}
				er="3";
			}
			er="4";
			rows = new row[10];
			er="5";
			JSONArray rowsjson = payload.getJSONArray("rows");
			er="6";
			rowscount = rowsjson.length();
			er="7";
			for (int i=0; i<rowscount; i++) {
				er="8";
				JSONObject jrow = rowsjson.getJSONObject(i);
				er="9";
				if (jrow.has("left")) {
					er="10";
					JSONObject left = jrow.getJSONObject("left");
					er="11";
					if (left.optString("type").equals("icon")) {
						er="12";
						rows[i].iconurl = left.getJSONObject("payload").getJSONArray("items").getJSONObject(0).optString("url");
						er="13";
						rows[i].icon = VikaUtils.downloadImage(rows[i].iconurl);
						er="14";
					}
					er="15";
				}
				er="16";
				if (jrow.has("middle")) {
					er="17";
					JSONObject jmiddle =  jrow.getJSONObject("middle");
					er="18";
					if (jmiddle.has("title")) {
						rows[i].title = jmiddle.optJSONObject("title").optString("value");
						er="19";
					}
					er="20";
					if (jmiddle.has("subtitle")) {
						er="21";
						rows[i].title = jmiddle.optJSONObject("subtitle").optString("value");
						er="22";
					}
					er="23";
				}
				er="24";
				if (jrow.has("right")) {
					er="25";
					JSONObject right = jrow.getJSONObject("right");
					er="26";
					if (right.optString("type").equals("counter")) {
						er="27";
						rows[i].righttext = right.getJSONObject("payload").optString("value");
						er="28";
					}
					er="29";
				}
				er="30";
			}
			er="31";
			/*name = TextLocal.inst.get("msg.attach.voice");
			size = json.optInt("duration");
			length = MusicPlayer.time(size);
			musUrl = fixJSONString(json.optString("link_mp3"));
			text = json.optString("transcript");
			if (text != null && text.length() > 2) {
				textB = TextBreaker.breakText(text, Font.getFont(0, 0, 8), MsgItem.msgWidth - 30);
			}*/
		} catch (Throwable e) {
			e.printStackTrace();
			VikaTouch.sendLog(er);
			//VikaTouch.error(e, ErrorCodes.DOCPARSE);
		}

		System.gc();
	}

	public int getDrawHeight() {
		/*int th = 0;
		if (textB != null)
			th = (textB.length * Font.getFont(0, 0, 8).getHeight()) + 5;*/
		return 40 * (1+rowscount);
	}

	public void draw(Graphics g, int x1, int y1, int w) {
		/*if (lastW != w) {
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
		}*/
		try {
		int i=0;
		Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
		g.setFont(f);
		while (i< rows.length && (rows[i].title!=null)) {
		g.drawString(rows[i].title, x1 + 34, y1 + 10 + f.getHeight() * i, 0);
		i++;
		}
		} catch (Throwable eee ) {}
	}

	public void press() {
		//OptionItem[] i = new OptionItem[2];
		//i[0] = new OptionItem(this, "Прослушать", IconsManager.PLAY, 0, 50);
		//i[1] = new OptionItem(this, "Транскрипт", IconsManager.EDIT, 1, 50);
		//VikaTouch.popup(new ContextMenu(i));
		// в плеере скачать можно.
		// MusicScreen ms = new MusicScreen();
		// ms.loadAtt(this);
		// MusicPlayer.launch(ms, 0);
	}

	/*public void onMenuItemPress(int i) {
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
	}*/
}
