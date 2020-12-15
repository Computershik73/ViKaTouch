package vikatouch.items.chat;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import vikatouch.Dialogs;
import vikatouch.VikaTouch;
import vikatouch.items.JSONUIItem;
import vikatouch.locale.TextLocal;
import vikatouch.settings.Settings;
import vikatouch.utils.ResizeUtils;
import vikatouch.utils.VikaUtils;

public class ConversationItem extends JSONUIItem {
	public String text;
	public String title;
	public long chatid;
	public boolean ls;
	public long date;
	public int unread;
	public boolean mention;
	public boolean isMuted;
	public String avaurl;
	private String time;
	private String type;
	private boolean isGroup;
	public int id;
	public int peerId;
	public int lastSenderId;
	private Image ava;
	// private static Image deleteImg;
	// private static Image unreadImg;
	public String lasttext;

	public ConversationItem(JSONObject json) {
		super(json);
		itemDrawHeight = 63;
		/*
		 * if(DisplayUtils.compact) { itemDrawHeight = 36; } else {
		 */
		ava = VikaTouch.cameraImg;
		// }
	}

	public void getAva() {

		// if(!DisplayUtils.compact)
		// {
		if (ava == null || ava == VikaTouch.cameraImg) {
			ava = VikaTouch.cameraImg;
			Image img = null;
			try {
				img = VikaTouch.cameraImg;
				if (avaurl != null && !Settings.dontLoadAvas) {
					try {
						img = VikaUtils.downloadImage(avaurl);
					} catch (Exception e) {
						ava = VikaTouch.cameraImg;
					}
				}
				ava = ResizeUtils.resizeChatAva(img);
			} catch (Throwable e) {
				ava = VikaTouch.cameraImg;
			}
		}
		// }
		// avaurl = null;
	}

	public String getTime() {
		/*
		 * супер-мега костыль 2000 try { if(date == 0) Thread.sleep(10l); } catch
		 * (InterruptedException e) {}
		 */
		return VikaUtils.parseShortTime(date);
	}

	public void paint(Graphics g, int y, int scrolled) {
		Font font = Font.getFont(0, 0, 8);
		ColorUtils.setcolor(g, 0);

		if (selected) {
			ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
			g.fillRect(0, y - 1, DisplayUtils.width, itemDrawHeight + 1);
			ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
			// g.drawImage(deleteImg, DisplayUtils.width - 25, y + 17, 0);
		}

		/*
		 * if(DisplayUtils.compact) { } else {
		 */
		int h = itemDrawHeight;
		int hfh = font.getHeight() / 2;
		if (title != null) {
			g.drawString(title, 73, y + h / 4 - hfh, 0);
		}

		if (!selected) {
			ColorUtils.setcolor(g, ColorUtils.OUTLINE);
		}

		g.drawString(text == null ? "Сообщение" : text, 73, y + h * 3 / 4 - hfh, 0);

		if (!selected) {
			ColorUtils.setcolor(g, 7);
		}

		if (time != null) {
			g.drawString(time, DisplayUtils.width - (16 + font.stringWidth(time)), y + h / 4 - hfh, 0);
		}

		if (ava != null) {
			g.drawImage(ava, 14, y + 8, 0);
			if (IconsManager.ac == null) {
				System.out.print("F");
			} else // а что, бывало что оно не загрузилось? лол))
				g.drawImage(selected ? IconsManager.acs : IconsManager.ac, 14, y + 8, 0); // и вообще, ACS проверить
																							// забыли. Приделаю костыль.

		}

		if (!selected) {
			ColorUtils.setcolor(g, -5);
			g.fillRect(72, y + itemDrawHeight, DisplayUtils.width - 72, 1);
		}
		if (unread > 0) {
			int rh = hfh * 2;
			int hm = 4;
			String s = (mention ? "@ " : "") + unread;

			ColorUtils.setcolor(g, ColorUtils.COLOR1);
			g.fillRoundRect(DisplayUtils.width - 16 - font.stringWidth(s) - hm * 2, y + h * 3 / 4 - hfh,
					font.stringWidth(s) + hm * 2, rh, rh / 2, rh / 2);

			g.setGrayScale(255);
			g.drawString(s, DisplayUtils.width - 16 - font.stringWidth(s) - hm, y + h * 3 / 4 - hfh, 0);
		}
		// }
	}

	public void parseJSON() {
		try {
			JSONObject conv = json.optJSONObject("conversation");
			JSONObject peer = conv.optJSONObject("peer");
			peerId = peer.optInt("id");
			// System.out.println(json.toString());
			try {
				if (!conv.isNull("chat_settings")) {
					JSONObject chatSettings = conv.optJSONObject("chat_settings");
					title = fixJSONString(chatSettings.optString("title"));
					isGroup = chatSettings.optBoolean("is_group_channel");
					avaurl = fixJSONString(chatSettings.getJSONObject("photo").optString("photo_50"));
					chatSettings.dispose();
				}
			} catch (Throwable e) {
				// System.out.println("conv " + peerId + ": " + e.toString());
				// chat_settings может не существовать, так-что это исключение игнорируется

				// if(e instanceof InterruptedException) throw e;
			}

			unread = conv.optInt("unread_count");
			mention = conv.has("mentions");

			type = fixJSONString(peer.optString("type"));
			id = peer.optInt("local_id");

			peer.dispose();

			conv.dispose();

			if (type.equalsIgnoreCase("user") && Dialogs.profiles != null) {
				for (int i = 0; i < Dialogs.profiles.length(); i++) {
					JSONObject profile = Dialogs.profiles.optJSONObject(i);
					if (profile != null && profile.optInt("id") == id) {
						title = fixJSONString(profile.optString("first_name") + " " + profile.optString("last_name"));
						if (!Settings.dontLoadAvas)
							avaurl = fixJSONString(profile.optString("photo_50"));
						break;
					}
				}
			}
			if (type.equalsIgnoreCase("group") && Dialogs.groups != null) {
				for (int i = 0; i < Dialogs.groups.length(); i++) {
					JSONObject group = Dialogs.groups.optJSONObject(i);
					if (group != null && group.optInt("id") == id) {
						title = fixJSONString(group.optString("name"));
						if (!Settings.dontLoadAvas)
							avaurl = fixJSONString(group.optString("photo_50"));
						break;
					}

				}
			}
		} catch (Throwable e) {
			// VikaTouch.error(e, ErrorCodes.CONVERPARSE);
			e.printStackTrace();
		}

		try {
			JSONObject msg = json.optJSONObject("last_message");

			date = msg.optLong("date");

			lasttext = text = fixJSONString(msg.optString("text"));

			time = getTime();

			String nameauthora = "";

			if (text == "" || text == null || text.length() == 0) {
				JSONArray attachments = msg.optJSONArray("attachments");
				// if(lastmessage.attachments != null && lastmessage.attachments.length != 0 &&
				// lastmessage.attachments[0] != null)
				// {
				try {
					if(attachments.optJSONObject(0) != null) {
						if (attachments.optJSONObject(0).optString("type").equals("photo")) {
							text = TextLocal.inst.get("msg.attach.photo");
						} else {
							if (attachments.optJSONObject(0).optString("type").equals("audio")) {
								text = TextLocal.inst.get("msg.attach.audio");
							} else {
								if (attachments.optJSONObject(0).optString("type").equals("video")) {
									text = TextLocal.inst.get("msg.attach.video");
								} else {
									if (attachments.optJSONObject(0).optString("type").equals("wall")) {
										text = TextLocal.inst.get("msg.attach.wall");
									} else {
	
										text = TextLocal.inst.get("msg.attach.attachment");
	
									}
								}
							}
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
					/*
					 * if(lastmessage.attachments[0] instanceof PhotoAttachment) { text =
					 * TextLocal.inst.get("msg.attach.photo"); } else if(lastmessage.attachments[0]
					 * instanceof AudioAttachment) { text = "Аудиозапись"; } else { text =
					 * "Вложение"; }
					 */
				}
				// }

				// text = TextLocal.inst.get("msg.attach.attachment");
			}
			lastSenderId = msg.optInt("from_id");
			msg.dispose();
			if (("" + lastSenderId).equalsIgnoreCase(VikaTouch.userId)) {
				nameauthora = TextLocal.inst.get("msg.you");
			} else if (isGroup || type.equalsIgnoreCase("chat")) {
				for (int i = 0; i < Dialogs.profiles.length(); i++) {
					JSONObject profile = Dialogs.profiles.getJSONObject(i);
					if (lastSenderId == profile.optInt("id")) {
						nameauthora = profile.optString("first_name");
					}
				}
			}

			if (nameauthora != "") {
				text = nameauthora + ": " + text;
			}

			text = TextBreaker.shortText(text, DisplayUtils.width - 80, Font.getFont(0, 0, 8));
		} catch (Throwable e) {
			e.printStackTrace();
		}

		if (title != null) {
			title = TextBreaker.shortText(title, DisplayUtils.width - 80, Font.getFont(0, 0, 8));
		}
		type = null;
		json.dispose();
		json = null;
		System.gc();
	}

	public void tap(int x, int y) {
		Dialogs.openDialog(this);
		/*
		 * if(x > DisplayUtils.width - 25 && y > 16 && y < 32) { remove(); } else {
		 * Dialogs.openDialog(this); }
		 */
	}

	public void keyPressed(int key) {
		Dialogs.openDialog(this);
	}

	public void pressed() {
		Dialogs.selected = true;
		selected = true;
	}

	public void released(boolean dragging) {
		if (dragging) {
			selected = false;
		}
	}

}
