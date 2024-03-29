package vikatouch.items.chat;



import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.VikaCanvas;
import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.menu.items.OptionItem;
import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.popup.AutoContextMenu;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.locale.TextLocal;
import vikatouch.screens.ChatScreen;
import vikatouch.screens.menu.ChatMembersScreen;
import vikatouch.updates.VikaUpdate.VEUtils;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.text.CountUtils;
import vikatouch.utils.url.URLBuilder;


public class UnsentMsgItem  implements IMenu, IMessage , PressableUIItem {
	public UnsentMsgItem(JSONObject json) {
		
		VikaTouch.needstoRedraw=true;
		smilesarray = new Vector(0, 1);
	}
 
	public String text;
	private int mid;
	private String[] drawText;
	private String name = "";
	public boolean foreign;
	public static int msgWidth = 300;
	public static int margin = 10;
	public static int attMargin = 5;
	public int linesC;
	private String time = "";
	public boolean showName;

	private int attH = -1;
	private int fwdH = -1;

	private boolean hasReply;
	public String replyName;
	public String replyText;
	private boolean attsReady;

	private boolean isRead = true;
	public MsgItem[] forward;

	public int forwardedX = -1;
	public int forwardedW = -1;
	//public smiles[] smilesarray;
	public Vector smilesarray = new Vector(0, 1);
	public String codestext="";
	public  int llll;
	private int itemDrawHeight;
	private boolean selected;
	private long date;
	
	/*public void ChangeText(String s) {
		VikaTouch.needstoRedraw=true;
		text = s;
		int h1 = Font.getFont(0, 0, 8).getHeight();
		drawText = TextBreaker.breakText(text, Font.getFont(0, 0, Font.SIZE_SMALL), msgWidth - h1);
		linesC = drawText.length;
		itemDrawHeight = h1 * (linesC + 1);
	}*/
	
	public static final long unsignedIntToLong(byte[] b) 
	{
	    long l = 0;
	    l |= b[0] & 0xFF;
	    l <<= 8;
	    l |= b[1] & 0xFF;
	    l <<= 8;
	    l |= b[2] & 0xFF;
	    l <<= 8;
	    l |= b[3] & 0xFF;
	    return l;
	}
	
	
	public static final int unsignedShortToInt(byte[] b) 
	{
	    int i = 0;
	    i |= b[0] & 0xFF;
	    i <<= 8;
	    i |= b[1] & 0xFF;
	    return i;
	}

	public void parseJSON() {
		
	}

	public void loadAtts() {}

	
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

	public void paint(Graphics g, int y, int scrolled) {
		if (VikaTouch.needstoRedraw==false) {
			return;
		}
		try {
			if (!ChatScreen.forceRedraw && y + scrolled + itemDrawHeight < -50)
				return;
			if (y + scrolled >DisplayUtils.height) {
				return;
			}
			// drawing
			Font font = Font.getFont(0, 0, 8);
			g.setFont(font);
			int h1 = font.getHeight();
			int attY = h1 * (linesC + 1 + (showName ? 1 : 0) + (hasReply ? 2 : 0));
			int th = attY + attH + fwdH;
			itemDrawHeight = th;
			int textX = 0;
			int radius = 16;
			int msgWidthInner = (forwardedW == -1 ? msgWidth : forwardedW);
			if (forwardedX != -1) {
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
				if (!isRead) {
					// System.out.println("unread out");
					ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
					g.fillArc(DisplayUtils.width - (margin + msgWidth) - 9, y + 16, 8, 8, 0, 360);
				}
			}
			if (name != null && showName) {
				ColorUtils.setcolor(g, ColorUtils.COLOR1);
				g.drawString(name, textX, y + h1 / 2, 0);
				ColorUtils.setcolor(g, ColorUtils.OUTLINE);
				if (time == null || time.length() < 1) {
					time = getTime();
					// System.out.println("msg time: "+time);
				}
				g.drawString(time, textX - h1 + msgWidthInner - font.stringWidth(time), y + h1 / 2, 0);
			}
			ColorUtils.setcolor(g, ColorUtils.MSGTEXT);
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
							yy = y + h1 / 2 + h1 * (i + (showName ? 1 : 0));
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
							yy = y + h1 / 2 + h1 * (i + (showName ? 1 : 0));
							errst=33;
							g.drawString(s1, textX, yy, 0);
							errst=34;
						}
						errst=35;
						if (smilesarray.capacity()>0) {
							errst=36;
							//String gf=" ";
							int lil=0;
					
						//if (gf!=" ") {
						//VikaTouch.sendLog(gf);
						//}
						errst=41;
						
					}
						errst=42;
						symbolspassed+=s1.length();
						errst=43;
					}
					} catch (Throwable eh) {
						VikaTouch.sendLog(eh.getMessage()+String.valueOf(errst));
					}
				}
		/*	} catch (Throwable t) {
				t.printStackTrace();
			}*/
			//VikaTouch.sendLog("Всего смайлов в облачке: "+String.valueOf(smilesarray.capacity()));
			//smilesarray.removeAllElements();
			
			if (hasReply) {
				ColorUtils.setcolor(g, ColorUtils.MSGTEXT);
				if (replyText != null)
					g.drawString(replyText, textX + h1, y + h1 / 2 + h1 * (linesC + 1 + (showName ? 1 : 0)), 0);
				ColorUtils.setcolor(g, ColorUtils.COLOR1);
				if (replyName != null)
					g.drawString(replyName, textX + h1, y + h1 / 2 + h1 * (linesC + (showName ? 1 : 0)), 0);
				g.fillRect(textX + h1 / 2 - 1, y + h1 / 2 + h1 * (linesC + (showName ? 1 : 0)), 2, h1 * 2);
			}

		

			try {
				if (forward != null && forward.length > 0) {
					fwdH = 0;
					for (int i = 0; i < forward.length; i++) {
						if (forward[i] == null)
							continue;
						forward[i].paint(g, attY + fwdH + y, scrolled);
						fwdH += forward[i].getDrawHeight();
					}
				}
			} catch (RuntimeException e) {
			}
		} catch (Throwable e) {
			VikaTouch.sendLog(e.toString());
		}
		
	}

	public String[] searchLinks() {
		if (text == null || text.length() < 2)
			return null;
		int lm = 8; // links max (больше на экран не влезет (смотря какой
					// конечно))
		String[] la = new String[lm];
		int li = 0; // индекс в массиве
		int tl = text.length();

		String[] glinks = new String[] { "http://", "https://", "rtsp://", "ftp://", "smb://" }; // вроде
																									// всё.
																									// Ага,
																									// я
																									// слал/принимал
																									// пару
																									// раз
																									// ссылки
																									// на
																									// расшаренные
																									// папки
																									// как
																									// smb://server/folder
		try {
			// System.out.println(text);
			// System.out.println("tl "+tl);
			// Поиск внешних ссылок
			// сначала ищем их на случай сообщения
			// @id89277233 @id2323 @id4 @id5 @id6 ... [ещё 100509 @] ...
			// @id888292,
			// http://что-тоТам
			// В беседе вики такое постоянно.
			for (int gli = 0; gli < glinks.length; gli++) {
				int ii = 0; // Indexof Index
				while (true) {
					ii = text.indexOf(glinks[gli], ii);
					// System.out.println("ii "+ii);
					if (ii == -1) {
						break;
					} else {
						int lci = ii + 6;
						while (lci < tl && text.charAt(lci) != ' ') {
							lci++;
						}
						String l = text.substring(ii, lci);
						la[li] = l;
						li++;
						if (li >= lm)
							return la;
						ii = lci;
					}
				}
			}

			// Поиск ссылок ВК
			int cc = 0; // current char
			while (cc < tl) {
				char c = text.charAt(cc);
				if (c == '@') {
					int cs = cc;
					cc++;
					while (cc < tl && text.charAt(cc) != ' ' && text.charAt(cc) != ']') {
						cc++;
					}
					String l = text.substring(cs, cc);
					la[li] = l;
					li++;
					if (li >= lm)
						return la;
				} else if (c == '[') {
					cc++;
					int cs = cc;
					while (cc < tl && text.charAt(cc) != '|') {
						cc++;
					}
					String l = text.substring(cs, cc);
					la[li] = l;
					li++;
					if (li >= lm)
						return la;
				}
				cc++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("links c "+li);
		return la;
	}

	public String getTime() {
		return VikaUtils.parseMsgTime(date);
	}

	public int getDrawHeight() {
		return itemDrawHeight;
	}

	public void tap(int x, int y) {
		VikaTouch.needstoRedraw=true;
		keyPress(-5);
	}

	public void keyPress(int key) {
		VikaTouch.needstoRedraw=true;
		if (key == -5) {
			if (VikaCanvas.currentAlert==null) { 
			int h = 48;
			OptionItem[] opts;
			if (ChatScreen.peerId > ChatScreen.OFFSET_INT) {
			 opts = new OptionItem[8];
			} else {
				opts = new OptionItem[7];
			}
			opts[0] = new OptionItem(this, TextLocal.inst.get("msg.reply"), IconsManager.ANSWER, -1, h);
			opts[1] = foreign ? new OptionItem(this, TextLocal.inst.get("msg.markasread"), IconsManager.APPLY, -5, h)
					: new OptionItem(this, TextLocal.inst.get("msg.edit"), IconsManager.EDIT, -4, h);
			opts[2] = new OptionItem(this, TextLocal.inst.get("msg.delete"), IconsManager.CLOSE, -2, h);
			opts[3] = new OptionItem(this, TextLocal.inst.get("msg.fwd"), IconsManager.SEND, -6, h);
			opts[4] = new OptionItem(this, TextLocal.inst.get("msg.links") + "...", IconsManager.LINK, -8, h);
			opts[5] = new OptionItem(this, TextLocal.inst.get("msg.attach.attachments") + "...",
					IconsManager.ATTACHMENT, -9, h);
			opts[6] = new OptionItem(this, this.name + "...",
					IconsManager.FRIENDS, -10, h);
			if (ChatScreen.peerId > ChatScreen.OFFSET_INT) {
			opts[7] = new OptionItem(this, ChatScreen.title + "...",
					IconsManager.GROUPS, -11, h);
			}
			VikaTouch.popup(new AutoContextMenu(opts));
			}
			VikaTouch.needstoRedraw=true;
			//VikaTouch.sendLog(String.valueOf(VikaCanvas.currentAlert.getClass()));
			//VikaTouch.sendLog(String.valueOf(VikaTouch.canvas.currentScreen.getClass()));
			VikaTouch.canvas.currentScreen.serviceRepaints();
			VikaTouch.needstoRedraw=true;
		}
	}

	public void onMenuItemPress(int i) {
		VikaTouch.needstoRedraw=true;
		if (i <= -100) {
			// ссылки
			i = -i;
			i = i - 100;
			try {
				String s = searchLinks()[i];
				VikaUtils.openLink(s);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			return;
		} else if (i >= 0) { // прикрепы
			try {
				//if (i<attachments.length) {
				//attachments[i].press();
				//} else {
				//	forward[]
				//}
			} catch (Exception e) {
			}
			VikaTouch.needstoRedraw=true;
			return;
		}
		// основная менюшка
		// Да, такая лапша. Ты ещё покруче делаешь.
		switch (i) {
		case -1:
			ChatScreen.attachAnswer(mid, name, text);
			VikaTouch.needstoRedraw=true;
			break; // БРЕАК НА МЕСТЕ!!11!!1!
		case -2:
			OptionItem[] opts1 = new OptionItem[2];
			opts1[0] = new OptionItem(this, TextLocal.inst.get("msg.remove0"), IconsManager.EDIT, -98, 60);
			opts1[1] = new OptionItem(this, TextLocal.inst.get("msg.remove1"), IconsManager.CLOSE, -99, 60);
			VikaTouch.popup(new AutoContextMenu(opts1));
			break;
		case -4:
			//if (!foreign)
			//	ChatScreen.editMsg(this);
			VikaTouch.needstoRedraw=true;
			break;
		case -5:
			try {
				if (VikaTouch.canvas.currentScreen instanceof ChatScreen) {
					ChatScreen c = (ChatScreen) VikaTouch.canvas.currentScreen;
					URLBuilder url = new URLBuilder("messages.markAsRead").addField("start_message_id", "" + mid)
							.addField("peer_id", c.peerId);
					VikaUtils.download(url);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case -6:
			//VikaTouch.popup(new InfoPopup(TextLocal.inst.get("popup.unrealized"), null));
			VikaTouch.resendingmid=mid;
			VikaTouch.resendingname=name;
			VikaTouch.resendingtext=text;
			vikatouch.screens.DialogsScreen.titleStr="Выберите диалог для пересылки:";
			VikaTouch.setDisplay(VikaTouch.dialogsScr, -1);
			VikaTouch.needstoRedraw=true;
			break;
		case -8: {
			String[] links = searchLinks();
			int c = 0;
			while (links[c] != null) {
				c++;
			}
			if (c == 0) {
				VikaTouch.popup(new InfoPopup(TextLocal.inst.get("error.linksnotfound"), null));
				VikaTouch.needstoRedraw=true;
			} else {
				OptionItem[] opts2 = new OptionItem[c];
				int h = DisplayUtils.height > 240 ? 50 : 30; // вот как делается
																// адаптация, а
																// не твои
																// километровые
																// свитчи и да,
																// я буду ещё
																// долго
																// ворчать.
				for (int j = 0; j < c; j++) {
					int icon = IconsManager.LINK;
					if (links[j].startsWith("id")) {
						icon = IconsManager.FRIENDS;
					}
					if (links[j].startsWith("club")) {
						icon = IconsManager.GROUPS;
					}
					if (links[j].startsWith("rtsp")) {
						icon = IconsManager.VIDEOS;
					}
					opts2[j] = new OptionItem(this, links[j], icon, -(j + 100), h);
				}
				VikaTouch.popup(new AutoContextMenu(opts2));
				VikaTouch.needstoRedraw=true;
			}
		}
			break;
		case -9: {}
			break;
		case -98:
		case -10:
			
			break;
		case -11:
			/*if (ChatScreen.type == ChatScreen.TYPE_USER) {
				VikaTouch.setDisplay(new ProfilePageScreen(ChatScreen.localId), 1);
			} else if (ChatScreen.type == ChatScreen.TYPE_GROUP) {
				VikaTouch.setDisplay(new GroupPageScreen(ChatScreen.localId), 1);
			} else*/ if (ChatScreen.type == ChatScreen.TYPE_CHAT) {
				String x2 = CountUtils.countStrMembers(ChatScreen.members);
				VikaTouch.setDisplay(new ChatMembersScreen(ChatScreen.peerId, x2, ChatScreen.members), 1);
			}
			break;
		case -99: {
			boolean ok = false;
			try {
				URLBuilder url = new URLBuilder("messages.delete").addField("message_ids", "" + mid)
						.addField("delete_for_all", i == -99 ? 1 : 0);
				String x = VikaUtils.download(url);
				JSONObject res = (new JSONObject(x)).getJSONObject("response");
				ok = (res.optInt("" + mid) == 1);
			} catch (Exception e) {
				e.printStackTrace();
				ok = false;
			}
			if (ok) {
				text = TextLocal.inst.get("msg.removed");
				drawText = new String[] { text };
				linesC = 1;
			}
			VikaTouch.needstoRedraw=true;
			break;
		}
		}
	}

	public void onMenuItemOption(int i) {

	}

	public int getMessageId() {
		return mid;
	}

	public String getText() {
		return text;
	}

	public int getFromId() {
		return VikaTouch.integerUserId;
	}

	public void setName(String n) {
		this.name = n;
	}

	public void setRead(boolean i) {
		this.isRead = i;
	}

	public boolean isRead() {
		return this.isRead;
	}


	

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setDrawHeight(int i) {
		this.itemDrawHeight = i;
	}

	public void addDrawHeight(int i) {
		this.itemDrawHeight += i;
	}

}
