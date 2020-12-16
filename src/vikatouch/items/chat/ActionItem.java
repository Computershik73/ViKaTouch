package vikatouch.items.chat;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.screens.ChatScreen;
import vikatouch.utils.IntObject;
import vikatouch.utils.VikaUtils;

public class ActionItem extends ChatItem implements IMessage {

	private int msgWidth;
	private int margin;
	private int mid;
	private int memberid;
	private String type;
	private boolean isRead = true;

	public ActionItem(JSONObject json) {
		super(json);
	}
	
	public void parseJSON() {
		msgWidth = DisplayUtils.width - (DisplayUtils.width <= 240 ? 10 : 40);
		margin = (DisplayUtils.width <= 240 ? 0 : 10);
		mid = json.getInt("id");
		JSONObject action = json.getJSONObject("action");
		type = VikaUtils.replace(action.getString("type"), "chat_", "");
		memberid = action.optInt("member_id");
		fromid = json.getInt("from_id");
		
		if(type.equalsIgnoreCase("kick_user")) {
			String s1;
			if(fromid > 0) {
				if(ChatScreen.profileNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "id" + fromid;
				}
			} else {
				if(ChatScreen.groupNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "gid" + -fromid;
				}
			}
			String s2;
			if(memberid > 0) {
				if(ChatScreen.profileNames != null) {
					s2 = (String) ChatScreen.profileNames.get(new IntObject(memberid));
				} else {
					s2 = "id" + memberid;
				}
			} else {
				if(ChatScreen.groupNames != null) {
					s2 = (String) ChatScreen.groupNames.get(new IntObject(memberid));
				} else {
					s2 = "gid" + -memberid;
				}
			}
			text = s1 + " изгнал " + s2;
			
		} else if(type.equalsIgnoreCase("invite_user")) {
			String s1;
			if(fromid > 0) {
				if(ChatScreen.profileNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "id" + fromid;
				}
			} else {
				if(ChatScreen.groupNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "gid" + -fromid;
				}
			}
			String s2;
			if(memberid > 0) {
				if(ChatScreen.profileNames != null) {
					s2 = (String) ChatScreen.profileNames.get(new IntObject(memberid));
				} else {
					s2 = "id" + memberid;
				}
			} else {
				if(ChatScreen.groupNames != null) {
					s2 = (String) ChatScreen.groupNames.get(new IntObject(memberid));
				} else {
					s2 = "gid" + -memberid;
				}
			}
			text = s1 + " пригласил " + s2;
		} else if(type.equalsIgnoreCase("photo_update")) {
			String s1;
			if(fromid > 0) {
				if(ChatScreen.profileNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "id" + fromid;
				}
			} else {
				if(ChatScreen.groupNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "gid" + -fromid;
				}
			}

			text = s1 + " поменял фото";
		} else if(type.equalsIgnoreCase("photo_remove")) {
			String s1;
			if(fromid > 0) {
				if(ChatScreen.profileNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "id" + fromid;
				}
			} else {
				if(ChatScreen.groupNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "gid" + -fromid;
				}
			}

			text = s1 + " удалил фото";
		} else if(type.equalsIgnoreCase("create")) {
			String s1;
			if(fromid > 0) {
				if(ChatScreen.profileNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "id" + fromid;
				}
			} else {
				if(ChatScreen.groupNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "gid" + -fromid;
				}
			}

			text = s1 + " создал беседу";
		} else if(type.equalsIgnoreCase("pin_message")) {
			String s1;
			if(fromid > 0) {
				if(ChatScreen.profileNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "id" + fromid;
				}
			} else {
				if(ChatScreen.groupNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "gid" + -fromid;
				}
			}

			text = s1 + " закрепил сообщение";
		} else if(type.equalsIgnoreCase("unpin_message")) {
			String s1;
			if(fromid > 0) {
				if(ChatScreen.profileNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "id" + fromid;
				}
			} else {
				if(ChatScreen.groupNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "gid" + -fromid;
				}
			}

			text = s1 + " открепил сообщение";
		} else if(type.equalsIgnoreCase("chat_invite_user_by_link")) {
			String s1;
			if(fromid > 0) {
				if(ChatScreen.profileNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "id" + fromid;
				}
			} else {
				if(ChatScreen.groupNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "gid" + -fromid;
				}
			}

			text = s1 + " зашел по ссылке";
		} else {
			String s1;
			if(fromid > 0) {
				if(ChatScreen.profileNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "id" + fromid;
				}
			} else {
				if(ChatScreen.groupNames != null) {
					s1 = (String) ChatScreen.profileNames.get(new IntObject(fromid));
				} else {
					s1 = "gid" + -fromid;
				}
			}

			text = s1 + " " + type;
		}
		try {
			Font font = Font.getFont(0, 0, 8);
			msgWidth = 20 + font.stringWidth(text);
			margin = (DisplayUtils.width - msgWidth) / 2;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void tap(int x, int y) {
		
	}

	public void keyPressed(int key) {
		
	}

	public void paint(Graphics g, int y, int scrolled) {
		try {
			if (!ChatScreen.forceRedraw && y + scrolled + itemDrawHeight < -50)
				return;
			// drawing
			Font font = Font.getFont(0, 0, 8);
			g.setFont(font);
			int h1 = font.getHeight();
			int attY = h1 * 2;
			int th = attY;
			itemDrawHeight = th;
			int textX = 0;
			int radius = 16;
			//int msgWidthInner = msgWidth;
			//if (foreign) {
			ColorUtils.setcolor(g, ColorUtils.FOREIGNMSG);
			g.fillRoundRect(margin, y, msgWidth, th, radius, radius);
			g.fillRect(margin, y + th - radius, radius, radius);
			textX = margin + h1 / 2;
			if (selected && ScrollableCanvas.keysMode) {
				ColorUtils.setcolor(g, ColorUtils.TEXT);
				g.setStrokeStyle(Graphics.SOLID);
				g.drawRoundRect(margin, y, msgWidth, th, radius, radius);
			}
			/*} else {
				ColorUtils.setcolor(g, ColorUtils.MYMSG);
				g.fillRoundRect(DisplayUtils.width - (margin + msgWidth), y, msgWidth, th, radius, radius);
				g.fillRect(DisplayUtils.width - (margin + radius), y + th - radius, radius, radius);
				textX = DisplayUtils.width - (margin + msgWidth) + h1 / 2;
				if (selected && ScrollableCanvas.keysMode) {
					ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.setStrokeStyle(Graphics.SOLID);
					g.drawRoundRect(DisplayUtils.width - (margin + msgWidth), y, msgWidth, th, radius, radius);
				}*/
			ColorUtils.setcolor(g, ColorUtils.TEXT);
			g.drawString(text, textX, y + h1 / 2, 0);
		} catch (Throwable e) {
			VikaTouch.sendLog(e.toString());
		}
	}

	public int getMessageId() {
		return mid;
	}

	public String getText() {
		return "";
	}

	public int getFromId() {
		return fromid;
	}

	public void setName(String name) {
		
	}

	public void setRead(boolean isRead) {
		this.isRead  = isRead;
	}

	public boolean isRead() {
		return isRead;
	}
	
}
