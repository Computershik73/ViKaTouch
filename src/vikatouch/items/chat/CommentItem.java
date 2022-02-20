package vikatouch.items.chat;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import vikatouch.Dialogs;
import vikatouch.VikaTouch;
import vikatouch.attachments.Attachment;
import vikatouch.attachments.DocumentAttachment;
import vikatouch.attachments.PhotoAttachment;
import vikatouch.attachments.StickerAttachment;
import vikatouch.attachments.VideoAttachment;
import vikatouch.attachments.VoiceAttachment;
import vikatouch.attachments.WallAttachment;
import vikatouch.items.JSONItem;
import vikatouch.items.JSONUIItem;
import vikatouch.items.smile;
import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;
import vikatouch.settings.Settings;
import vikatouch.updates.VikaUpdate.VEUtils;
import vikatouch.utils.IntObject;
import vikatouch.utils.ProfileObject;
import vikatouch.utils.ResizeUtils;
import vikatouch.utils.VikaUtils;

/**
 * @author Shinovon
 * 
 */
public class CommentItem extends JSONItem {
	
	private int сid;
	private String[] drawText;
	private String name = "";
	public boolean foreign;
	public static int msgWidth = 300;
	public static int margin = 10;
	public static int attMargin = 5;
	public int linesC;
	//private String time = "";
	
	public String text;
	public String fulltext;
	public String title;
	public String shorttitle;
	public long commentid;
	public String post_id;
	public boolean ls;
	public long date;
	public int unread;
	public boolean mention;
	private int attH = -1;
	public String avaurl;
	public String online;
	private String time;
	private boolean attsReady;
	public int id;
	public int peerId;
	public int lastSenderId;
	private Image ava;
	public boolean shouldbeshown;
	public int parentcomment;
	public boolean isChildComment;
	public boolean unanswered;
	// private static Image deleteImg;
	// private static Image unreadImg;
	

	/*public CommentItem(JSONObject json) {
		super(json);
		itemDrawHeight = 63;
		/*
		 * if(DisplayUtils.compact) { itemDrawHeight = 36; } else {
		 */
		
		//VikaTouch.needstoRedraw=true;
		//VikaTouch.canvas.serviceRepaints();
		//if (DisplayUtils.width > 240)
			//ava = VikaTouch.cameraImg;
		// }
	//}*/
	
	public CommentItem(JSONObject json, boolean isChildComment) {
		super(json);
		itemDrawHeight = 63;
		/*
		 * if(DisplayUtils.compact) { itemDrawHeight = 36; } else {
		 */
		this.isChildComment=isChildComment;
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
		//if (DisplayUtils.width > 240)
			//ava = VikaTouch.cameraImg;
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
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
		// }
		// avaurl = null;
	}

	
	private static int count(String in, char t) {
		int r = 0;
		char[] c = in.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == t) {
				r++;
			}
		}
		return r;
	}
	

	public void setDrawHeight(int i) {
		this.itemDrawHeight = i;
	}
	
	public String getTime() {
		/*
		 * супер-мега костыль 2000 try { if(date == 0) Thread.sleep(10l); }
		 * catch (InterruptedException e) {}
		 */
		return VikaUtils.parseShortTime(date);
	}
	
	private static String[] split(String in, char t) {
		int l = count(in, t) + 1;
		String[] res = new String[l];
		String[] c = VEUtils.singleSplit(in, t);
		res[0] = c[0];
		for (int i = 1; i < l - 1; i++) {
			c = VEUtils.singleSplit(c[1], t);
			res[i] = c[0];
		}
		res[l - 1] = c[1];
		return res;
	}
	
	public void loadAtts() {
		VikaTouch.needstoRedraw=true;
		if (attH < 0) {
			attH = 0;
			// prepairing attachments
			if (!attsReady) {
				attsReady = true;
				try {
					for (int i = 0; i < attachments.length; i++) {
						Attachment at = attachments[i];
						if (at == null)
							continue;
						if (at instanceof PhotoAttachment && !Settings.isLiteOrSomething) {
							((PhotoAttachment) at).loadForComment();
						}
						if (at instanceof VideoAttachment && !Settings.isLiteOrSomething) {
							((VideoAttachment) at).loadForComment();
						}
						if (at instanceof VoiceAttachment) {
							((VoiceAttachment) at).mid = commentid;
						}
						if (at instanceof StickerAttachment) {
							int stickerH = DisplayUtils.width > 250 ? 128 : 64;
							attH += stickerH + attMargin;
						} else {
							attH += at.getDrawHeight() + attMargin;
						}
					}
					if (attH != 0) {
						attH += attMargin;
					}
				} catch (Throwable e) {
					attH = 0;
					e.printStackTrace();
				}
			}
		}
	}

	public void paint(Graphics g, int y, int scrolled) {
		
		int h = itemDrawHeight;
		Font font = Font.getFont(0, 0, 8);
		
		g.setFont(font);
		int h1 = font.getHeight();
		int hfh = font.getHeight() / 2;
		int tx = 73;
		if (DisplayUtils.width <= 240)
			tx = 4;
		//int attY = h1 * (linesC + 1 + (showName ? 1 : 0) + (hasReply ? 2 : 0));
		//int th = attY + attH + fwdH;
		//itemDrawHeight = th;
		int attY = h1 * (linesC + 2);
		itemDrawHeight = h1 * (linesC + 2)+attH;
		//g.drawString("title", 10, 10, 0);
		/*if (unread > 0) {
			ColorUtils.setcolor(g, ColorUtils.UNREAD_MSG_COLOR);
			g.fillRect(0, y - 1, DisplayUtils.width, itemDrawHeight + 1);
		}
		if (unanswered) {
			ColorUtils.setcolor(g, ColorUtils.UNREAD_MSG_COLOR);
			g.fillRect(tx - 2 , (int)(y + h * 3 / 4 - hfh), DisplayUtils.width-5 - tx + 2, hfh * 2 + 3);
		}*/
		
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		
		/*if (selected) {
			ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
			g.fillRect(0, y - 1, DisplayUtils.width, itemDrawHeight + 1);
			ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
			// g.drawImage(deleteImg, DisplayUtils.width - 25, y + 17, 0);
		}*/
		if (title != null) {
			int x = 150;
			if (DisplayUtils.width <= 240)
				x = 72;
			shorttitle = TextBreaker.shortText(title, DisplayUtils.width - tx - 16 - font.stringWidth(time), Font.getFont(0, 0, 8));
		}
		
		int textX = 0;
		int radius = 16;
		textX = tx;
		if (shorttitle != null) {
			g.drawString(shorttitle, tx, 
					//y + h / 4 - hfh
					y + h1 / 2 , 0);
		} else {
			//g.drawString("title", tx, y + h / 4 - hfh, 0);
		}
		
		if (time != null) {
			g.drawString(time, DisplayUtils.width - (16 + font.stringWidth(time)), y + h1 / 2, 0);
		}
		//DisplayUtils.width - (margin + msgWidth) + h1 / 2;
		//int msgWidthInner = (isChildComment == false ? msgWidth : forwardedW);
	/*	if (isChildComment != екгу) {
			ColorUtils.setcolor(g, ColorUtils.FOREIGNMSG);
			g.fillRoundRect(forwardedX, y, forwardedW, th, radius, radius);
			textX = forwardedX + h1 / 2;
		} else if (foreign) {
			ColorUtils.setcolor(g, ColorUtils.FOREIGNMSG);
			g.fillRoundRect(margin, y, msgWidth, th, radius, radius);
			g.fillRect(margin, y + th - radius, radius, radius);
			textX = margin + h1 / 2;
			if (selected && ScrollableCanvas.keysMode) {
				ColorUtils.setcolor(g, ColorUtils.TEXT);
				g.setStrokeStyle(Graphics.SOLID);
				g.drawRoundRect(margin, y, msgWidth, th, radius, radius);
			}

			if (!isRead) {
				ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
				g.fillArc(margin + msgWidth + 1, y + 16, 8, 8, 0, 360);
			}
		} else {
			ColorUtils.setcolor(g, ColorUtils.MYMSG);
			g.fillRoundRect(DisplayUtils.width - (margin + msgWidth), y, msgWidth, th, radius, radius);
			g.fillRect(DisplayUtils.width - (margin + radius), y + th - radius, radius, radius);
			textX = DisplayUtils.width - (margin + msgWidth) + h1 / 2;
			
			if (selected && ScrollableCanvas.keysMode) {
				ColorUtils.setcolor(g, ColorUtils.TEXT);
				g.setStrokeStyle(Graphics.SOLID);
				g.drawRoundRect(DisplayUtils.width - (margin + msgWidth), y, msgWidth, th, radius, radius);
			}
		
		*/
		/*
		 * if(DisplayUtils.compact) { } else {
		 */
		int symbolspassed=0;
		//smilesarray = new Vector(1, 5);
	//	try {
			for (int i = 0; i < linesC; i++) {
				int errst=0;
				try {
					errst=1;
				if (drawText[i] != null) {
					errst=2;
				//	VikaTouch.sendLog("i = "+String.valueOf(i) + " " + "text="+drawText[i]);
					String s1 = drawText[i];
					errst=3;
					int yy=0;
					errst=4;
					if ((s1.indexOf("[club") > -1 || s1.indexOf("[id") > -1) && s1.indexOf("|") > -1
							&& s1.indexOf("]") > -1) {
						errst=5;
						String[] arr1 = split(s1, '[');
						errst=6;
						int x = textX;
						errst=7;
						yy = y + h1 / 2 + h1 * (i + 1);
						errst=8;
						ColorUtils.setcolor(g, ColorUtils.MSGTEXT);
						errst=9;
						if (arr1[0] != null) {
							errst=10;
							g.drawString(arr1[0], x, yy, 0);
							errst=11;
							x += font.stringWidth(arr1[0]);
							errst=12;
							for (int c = 1; c < arr1.length; c++) {
								errst=13;
								if (arr1[c] != null && arr1[c].indexOf('|') > -1) {
									errst=14;
									String[] arr2 = VEUtils.split(arr1[c], 2, '|');
									errst=15;
									String[] arr3 = VEUtils.singleSplit(arr2[1], ']');
									errst=16;
									String ping = arr3[0];
									errst=17;
									String after = arr3[1];
									errst=18;
									g.setColor(0x2a5885);
									errst=19;
									g.drawString(ping, x, yy, 0);
									errst=20;
									x += font.stringWidth(ping);
									errst=21;
									if (after != null) {
										errst=22;
										ColorUtils.setcolor(g, ColorUtils.MSGTEXT);
										errst=23;
										g.drawString(after, x, yy, 0);
										errst=24;
										x += font.stringWidth(after);
										errst=25;
									}
									errst=26;
								}
								errst=27;
							}
							errst=28;
						}
						errst=29;
					} else {
						errst=30;
						if (s1.equalsIgnoreCase("@all") || s1.startsWith("@all")
								|| s1.equalsIgnoreCase("@online")) {
							errst=31;
							g.setColor(0x2a5885);
						}
						errst=32;
						yy = y + h1 / 2 + h1 * (i + 1);
						errst=33;
						g.drawString(s1, textX, yy, 0);
						errst=34;
					}
					errst=35;
				
					errst=42;
					symbolspassed+=s1.length();
					errst=43;
				}
				} catch (Throwable eh) {
					VikaTouch.sendLog(eh.getMessage()+String.valueOf(errst));
				}
			}
			foreign=false;
			int forwardedX = -1;
			int msgWidthInner = msgWidth;
			// рендер аттачей
						if (attH > 0 && attsReady) {
							attY += attMargin;
							for (int i = 0; i < attachments.length; i++) {
								Attachment at = attachments[i];
								if (at == null)
									continue;
								int x1 = foreign ? (margin + attMargin)
										: (DisplayUtils.width - (margin + msgWidthInner) + attMargin);
								if (at instanceof PhotoAttachment) {
									PhotoAttachment pa = (PhotoAttachment) at;
									int rx = forwardedX == -1
											? (foreign ? (margin + attMargin)
													: (DisplayUtils.width - (margin + attMargin) - pa.renderW))
											: (forwardedX + attMargin);
									if (pa.renderImg == null) {
										if (Settings.isLiteOrSomething) {
											g.drawString("Фотография", textX, y + attY, 0);
										} else
											g.drawString("Не удалось загрузить изображение", textX, y + attY, 0);
									} else {
										g.drawImage(pa.renderImg, rx, y + attY, 0);
									}
								} else if (at instanceof VideoAttachment) {
									VideoAttachment va = (VideoAttachment) at;
									int rx = forwardedX == -1
											? (foreign ? (margin + attMargin)
													: (DisplayUtils.width - (margin + attMargin) - va.renderW))
											: (forwardedX + attMargin);
									if (va.renderImg == null) {
										if (Settings.isLiteOrSomething) {
											g.drawString("Видео", textX, y + attY, 0);
										} else
											g.drawString("Не удалось загрузить изображение", textX, y + attY, 0);
									} else {
										g.drawImage(va.renderImg, rx, y + attY, 0);
										g.drawString(va.title, x1, y + attY + va.renderH, 0);
									}
								} else if (at instanceof DocumentAttachment) {
									((DocumentAttachment) at).draw(g, x1, y + attY, msgWidthInner - attMargin * 2);
								} else if (at instanceof WallAttachment) {
									((WallAttachment) at).draw(g, x1, y + attY, msgWidthInner - attMargin * 2);
								} else if (at instanceof StickerAttachment) {
									int stickerW = DisplayUtils.width > 250 ? 128 : 64;
									int rx = forwardedX == -1
											? (foreign ? (margin + attMargin)
													: (DisplayUtils.width - (margin + attMargin) - stickerW))
											: (forwardedX + attMargin);
									g.drawImage(((StickerAttachment) at).getImage(stickerW), rx, y + attY, 0);
								}

								attY += at.getDrawHeight() + attMargin;
							}
						}

		/*if (!selected) {
			ColorUtils.setcolor(g, ColorUtils.OUTLINE);
		}*/

		//g.drawString(text == null ? TextLocal.inst.get("msg") : text, tx, y + h * 3 / 4 - hfh, 0);

		if (!selected) {
			ColorUtils.setcolor(g, 7);
		}

		/*if (time != null) {
			g.drawString(time, DisplayUtils.width - (16 + font.stringWidth(time)), y + h / 4 - hfh, 0);
		}*/

		if (DisplayUtils.width > 240 && ava != null) {
			g.drawImage(ava, 14, y + h1 / 2, 0);
			if (IconsManager.ac == null) {
				System.out.print("F");
			} else // а что, бывало что оно не загрузилось? лол))
				//if (selected) {
					//g.drawImage(IconsManager.acs, 14, y + 8, 0);
				//} else {
					//if (unread > 0) {
					//	g.drawImage(IconsManager.acsh, 14, y + 8, 0);
					//} else {
				if (!Settings.nightTheme) {
				ResizeUtils.drawRectWithEmptyCircleInside(g, 255, 255, 255, 14, y + h1 / 2, 25);
				} else {
					ResizeUtils.drawRectWithEmptyCircleInside(g, 0, 0, 0, 14, y + h1 / 2, 25);
				}
				//g.drawImage(IconsManager.ac, 14, y + h1 / 2, 0);
				//	}
				//}
				//g.drawImage(selected ? IconsManager.acs : IconsManager.ac, 14, y + 8, 0); // и
																							// вообще,
																							// ACS
																							// проверить
																							// забыли.
																							// Приделаю
																							// костыль.
		}

		/*if (!selected) {
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
		if (online!=null) {
			if (online.equals("1")) {
				ColorUtils.setcolor(g, ColorUtils.ONLINE);
				g.fillArc(50, y + 46, 12, 12, 0, 360);
				//g.drawImage(IconsManager.onlineimg, 38, y + 32, 0);
			}
		}*/
		//if ((y<DisplayUtils.height - MainScreen.bottomPanelH) && (y>DisplayUtils.height - MainScreen.bottomPanelH - itemDrawHeight) && (Dialogs.isUpdatingNow==false)) {
			
		//}
		// }
	}

	

  /* { "id": 770641,
    "from_id": 151850844,
    "date": 1631987245,
    "text": "Оригинальности там не найти. Думаю разочаруется в покупке",
    "post_id": 770640,
    "owner_id": -36741297,
    "parents_stack": [],
    "thread": {
      "count": 1,
      "items": [
        {
          "id": 770644,
          "from_id": 499273853,
          "date": 1631987530,
          "text": "[id151850844|Дмитрий], А если и не оригинал, то как там по качеству?",
          "post_id": 770640,
          "owner_id": -36741297,
          "parents_stack": [
            770641
          ],
          "reply_to_user": 151850844,
          "reply_to_comment": 770641
        }
      ],
      "can_post": true,
      "show_reply_button": true
    }*/
  
	
	
	
	
	
	public void parseJSON() {
		//try {
		title = "Sender";	
		text = 	json.optString("text");
		peerId = json.optInt("from_id");
		date = json.optInt("date");
		time = VikaUtils.parseTime(date);
			//VikaTouch.sendLog("null");
		try {
		ProfileObject p = ((ProfileObject) VikaTouch.profiles.get(new IntObject(peerId)));
		title = p.getName();
		avaurl = p.getUrl();
		} catch (Throwable ee) {
			title = "Unknown sender";	
		}
		//title = title;
		
		msgWidth = DisplayUtils.width - (DisplayUtils.width <= 240 ? 4 : 73);
		margin = (DisplayUtils.width <= 240 ? 0 : 10);
		isChildComment=json.has("reply_to_comment");
		drawText = TextBreaker.breakText(text, Font.getFont(0, 0, Font.SIZE_SMALL),
				(isChildComment == true ?  msgWidth-margin : msgWidth));
		linesC = drawText.length;
		int h1 = Font.getFont(0, 0, 8).getHeight();
		

		itemDrawHeight = h1 * (linesC + 2);
		parseAttachments();
			/*JSONObject conv = json.optJSONObject("conversation");
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
		}*/

		/*try {
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
		System.gc();*/
			//this.disposeJson();
	}

	public void tap(int x, int y) {
		//Dialogs.openDialog(this);
		/*
		 * if(x > DisplayUtils.width - 25 && y > 16 && y < 32) { remove(); }
		 * else { Dialogs.openDialog(this); }
		 */
	}

	public void keyPress(int key) {
		//Dialogs.openDialog(this);
	}
	
	public int getDrawHeight() {
		return itemDrawHeight;
	}

	public void pressed() {
		//Dialogs.selected = true;
		selected = true;
	}

	public void released(boolean dragging) {
		if (dragging) {
			selected = false;
		}
	}

}
