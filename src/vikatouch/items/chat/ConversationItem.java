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
import vikatouch.items.JSONItem;
import vikatouch.items.JSONUIItem;
import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.IntObject;
import vikatouch.utils.ProfileObject;
import vikatouch.utils.ResizeUtils;
import vikatouch.utils.VikaUtils;

/**
 * @author Shinovon
 * 
 */
public class ConversationItem extends JSONItem {
	public String text;
	public String fulltext;
	public String title;
	public long chatid;
	public boolean ls;
	public long date;
	public int unread;
	public boolean mention;
	public boolean isMuted;
	public String avaurl;
	public String online;
	private String time;
	private String type;
	private boolean isGroup;
	public int id;
	public int peerId;
	public int lastSenderId;
	private Image ava;
	public int inread;
	public int outread;
	public int last_message_id;
	public boolean unanswered;
	// private static Image deleteImg;
	// private static Image unreadImg;
	public String lasttext;

	public ConversationItem(JSONObject json) {
		super(json);
		itemDrawHeight = 63;
		/*
		 * if(DisplayUtils.compact) { itemDrawHeight = 36; } else {
		 */
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
		//if (DisplayUtils.width > 240)
			ava = VikaTouch.cameraImg;
		// }
	}
	
	

	public void getAva() {

		// if(!DisplayUtils.compact)	
		// {
		if (DisplayUtils.width > 240 && (ava == null || ava == VikaTouch.cameraImg)) {
			ava = VikaTouch.cameraImg;
			Image img = null;
			try {
				img = VikaTouch.cameraImg;
				if (avaurl != null && !Settings.dontLoadAvas) {
					try {
						img = VikaUtils.downloadImage(avaurl, true);
					} catch (Exception e) {
						ava = VikaTouch.cameraImg;
					}
				}
				ava = ResizeUtils.resizeChatAva(img);
			} catch (Throwable e) {
				ava = VikaTouch.cameraImg;
			}
		}
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
		// }
		// avaurl = null;
	}

	public String getTime() {
		/*
		 * супер-мега костыль 2000 try { if(date == 0) Thread.sleep(10l); }
		 * catch (InterruptedException e) {}
		 */
		return VikaUtils.parseShortTime(date);
	}

	public void paint(Graphics g, int y, int scrolled) {
		String rgb = Settings.dialogselectcolor;
		int r = Integer.parseInt(rgb.substring(0, rgb.indexOf(",")));
	//	VikaUtils.logToFile(rgb.substring(0, rgb.indexOf(","))+ " ");
		rgb = rgb.substring(rgb.indexOf(",")+1);
		int gg = Integer.parseInt(rgb.substring(0, rgb.indexOf(",")));
	//	VikaUtils.logToFile(rgb.substring(0, rgb.indexOf(",")));
		rgb = rgb.substring(rgb.indexOf(",")+1);
		int b = Integer.parseInt(rgb); 
		
		int h = itemDrawHeight;
		Font font = Font.getFont(0, 0, 8);
		Font boldfont = Font.getFont(0, Font.STYLE_BOLD, 8);
		int hfh = font.getHeight() / 2;
		int tx = 73;
		if (DisplayUtils.width <= 240)
			tx = 4;
		
		
		if (unread > 0) {
			
			ColorUtils.setcolor(g, ColorUtils.UNREAD_MSG_COLOR);
			g.fillRect(0, y - 1, DisplayUtils.width, itemDrawHeight + 1);
			
		}
		
		if (selected) {
			
			//VikaUtils.logToFile(rgb+ " ");
			g.setColor(r, gg, b);
			g.fillRect(0, y - 1, DisplayUtils.width, itemDrawHeight + 1);
			ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
			// g.drawImage(deleteImg, DisplayUtils.width - 25, y + 17, 0);
		}

		/*
		 * if(DisplayUtils.compact) { } else {
		 */
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		
		
		
		
		
		if (!selected) {
			ColorUtils.setcolor(g, 7);
		}
		
		
		
		
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.setFont(boldfont);
		if (title != null) {
			g.drawString(title, tx, y + h / 4 - hfh, 0);
		}
		g.setFont(font);

		if (time != null) {
			g.drawString(time, DisplayUtils.width - (16 + font.stringWidth(time)), y + h / 4 - hfh, 0);
		}

		if (DisplayUtils.width > 240 && ava != null) {
			g.drawImage(ava, 14, y + 8, 0);
			if (IconsManager.ac == null) {
				//System.out.print("F");
			} else // а что, бывало что оно не загрузилось? лол))
				if (selected) {
					//g.drawImage(IconsManager.acs, 14, y + 8, 0);
					//if (unread > 0) {
						//ResizeUtils.drawRectWithEmptyCircleInside(g, 243, 244, 246, 14, y+8, 25);
						
					//} else {
						
						ResizeUtils.drawRectWithEmptyCircleInside(g, r, gg, b, 14, y+8, 25);
					//}
				} else {
					if (unread > 0) {
						
						ResizeUtils.drawRectWithEmptyCircleInside(g, 243, 244, 246, 14, y+8, 25);
						//g.drawImage(IconsManager.acsh, 14, y + 8, 0);
					} else {
						if (Settings.nightTheme) {
						ResizeUtils.drawRectWithEmptyCircleInside(g, 0, 0, 0, 14, y+8, 25);
						} else {
							ResizeUtils.drawRectWithEmptyCircleInside(g, 255, 255, 255, 14, y+8, 25);
						}
						//g.drawImage(IconsManager.ac, 14, y + 8, 0);
					}
				}
				//g.drawImage(selected ? IconsManager.acs : IconsManager.ac, 14, y + 8, 0); // и
																							// вообще,
																							// ACS
																							// проверить
																							// забыли.
																							// Приделаю
																							// костыль.
		}
		
		if (unread > 0) {
			g.setFont(boldfont);
			int rh = hfh * 2;
			int hm = 4;
			String s = (mention ? "@ " : "") + unread;

			ColorUtils.setcolor(g, ColorUtils.COLOR1);
			g.fillRoundRect(DisplayUtils.width - 16 - font.stringWidth(s) - hm * 2, y + h * 3 / 4 - hfh,
					font.stringWidth(s) + hm * 2, rh, rh / 2, rh / 2);

			g.setGrayScale(255);
			g.drawString(s, DisplayUtils.width - 16 - font.stringWidth(s) - hm, y + h * 3 / 4 - hfh, 0);
			g.setFont(font);
		}
		
		
		if (unanswered) {
			ColorUtils.setcolor(g, ColorUtils.UNREAD_MSG_COLOR);
			g.fillRect(tx - 2 , (int)(y + h * 3 / 4 - hfh), DisplayUtils.width-5 - tx + 2, hfh * 2 + 3);
		}
		
		if (!selected) {
			ColorUtils.setcolor(g, ColorUtils.OUTLINE);
		}
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		if (VikaTouch.userId.equals("3225000")) {
			ColorUtils.setcolor(g, 0);
		}
		g.drawString(text == null ? TextLocal.inst.get("msg") : text, tx, y + h * 3 / 4 - hfh, 0);
		if (VikaTouch.userId.equals("3225000")) {
			ColorUtils.setcolor(g, ColorUtils.TEXT);
		}
		

		if (!selected) {
			ColorUtils.setcolor(g, -5);
			g.fillRect(0, y + itemDrawHeight, DisplayUtils.width, 1);
		}
		
		if (online!=null) {
			if (online.equals("1")) {
				ColorUtils.setcolor(g, ColorUtils.ONLINE);
				g.fillArc(50, y + 46, 12, 12, 0, 360);
				//g.drawImage(IconsManager.onlineimg, 38, y + 32, 0);
			}
		}
		//if ((y<DisplayUtils.height - MainScreen.bottomPanelH) && (y>DisplayUtils.height - MainScreen.bottomPanelH - itemDrawHeight) && (Dialogs.isUpdatingNow==false)) {
			
		//}
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
				// chat_settings может не существовать, так-что это исключение
				// игнорируется

				// if(e instanceof InterruptedException) throw e;
			}
			try {
			unread = conv.optInt("unread_count");
			inread = conv.optInt("in_read");
			outread = conv.optInt("out_read");
			last_message_id = conv.optInt("last_message_id");
			unanswered=false;
			if ((inread!=0) && (outread!=0) && (last_message_id!=0)) {
				
				if ((last_message_id>outread)) {
					unanswered=true;
				}
			}
			} catch (Throwable er ) {}
			mention = conv.has("mentions");

			type = fixJSONString(peer.optString("type"));
			id = peer.optInt("local_id");

			peer.dispose();

			conv.dispose();

			if ((type.equalsIgnoreCase("user") || type.equalsIgnoreCase("group")) && VikaTouch.profiles.containsKey(new IntObject(peerId))) {
				ProfileObject p = ((ProfileObject) VikaTouch.profiles.get(new IntObject(peerId)));
				title = p.getName();
				avaurl = p.getUrl();
				online = p.getOnline();
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
			
			lastSenderId = msg.optInt("from_id");

			String nameauthora = "";
			textget: {
				if (text == "" || text == null || text.length() == 0) {
					JSONObject action = msg.optJSONObject("action");
					if(action != null) {
						String type = VikaUtils.replace(action.getString("type"), "chat_", "");
						int memberid = action.optInt("member_id");
		
						if (type.equalsIgnoreCase("kick_user")) {
							String s1;
							if (VikaTouch.profiles.containsKey(new IntObject(lastSenderId))) {
								s1 = ((ProfileObject) VikaTouch.profiles.get(new IntObject(lastSenderId))).getName();
							} else {
								s1 = "id" + lastSenderId;
							}
							String s2;
		
							if (VikaTouch.profiles.containsKey(new IntObject(memberid))) {
								s2 = ((ProfileObject) VikaTouch.profiles.get(new IntObject(memberid))).getName();
							} else {
								s2 = "id" + memberid;
							}
							text = TextLocal.inst.getFormatted(lastSenderId == memberid ? "msg.action.leave" : "msg.action.kick", new String[] { s1, s2});
		
						} else {
							String s1;
							if (VikaTouch.profiles.containsKey(new IntObject(lastSenderId))) {
								s1 = ((ProfileObject) VikaTouch.profiles.get(new IntObject(lastSenderId))).getName();
							} else {
								s1 = "id" + lastSenderId;
							}
							String s2 = "null";
							if(memberid != 0) {
							if (VikaTouch.profiles.containsKey(new IntObject(memberid))) {
								s2 = ((ProfileObject) VikaTouch.profiles.get(new IntObject(memberid))).getName();
							} else {
								s2 = "id" + memberid;
							}
							}
							text = TextLocal.inst.getFormatted("msg.action." + type, new String[] { s1, s2});
						}
						break textget;
					} else {
						JSONArray attachments = msg.optJSONArray("attachments");
						try {
							if (attachments.optJSONObject(1) != null) {
								text = TextLocal.inst.get("msg.attach.attachments");
							} else if (attachments.optJSONObject(0) != null) {
								if (attachments.optJSONObject(0).optString("photo", null) != null) {
									text = TextLocal.inst.get("msg.attach.photo");
								} else if (attachments.optJSONObject(0).optString("audio", null) != null) {
									text = TextLocal.inst.get("msg.attach.audio");
								} else if (attachments.optJSONObject(0).optString("video", null) != null) {
									text = TextLocal.inst.get("msg.attach.video");
								} else if (attachments.optJSONObject(0).optString("wall", null) != null) {
									text = TextLocal.inst.get("msg.attach.wall");
								} else if (attachments.optJSONObject(0).optString("action", null) != null) {
									text = TextLocal.inst.get("msg.attach.action");
								} else if (attachments.optJSONObject(0).optString("gift", null) != null) {
									text = TextLocal.inst.get("msg.attach.gift");
								} else {
									text = TextLocal.inst.get("msg.attach.attachment");
								}
							}
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
			}
			msg.dispose();
			if (("" + lastSenderId).equalsIgnoreCase(VikaTouch.userId)) {
				nameauthora = TextLocal.inst.get("msg.you");
			} else if (isGroup || type.equalsIgnoreCase("chat")) {
				if (VikaTouch.profiles.containsKey(new IntObject(lastSenderId))) {
					nameauthora = ((ProfileObject) VikaTouch.profiles.get(new IntObject(lastSenderId))).getFirstName();
				}
			}

			if (nameauthora != "") {
				text = nameauthora + ": " + text;
			}
			int x = 100;
			if (DisplayUtils.width <= 240)
				x = 32;
			fulltext=text;
			text = TextBreaker.shortText(text, DisplayUtils.width - x, Font.getFont(0, 0, 8));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
		if (title != null) {
			int x = 150;
			if (DisplayUtils.width <= 240)
				x = 72;
			title = TextBreaker.shortText(title, DisplayUtils.width - x, Font.getFont(0, 0, 8));
		}
		type = null;
		json.dispose();
		json = null;
		System.gc();
	}

	public void tap(int x, int y) {
		Dialogs.openDialog(this);
		/*
		 * if(x > DisplayUtils.width - 25 && y > 16 && y < 32) { remove(); }
		 * else { Dialogs.openDialog(this); }
		 */
	}

	public void keyPress(int key) {
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
