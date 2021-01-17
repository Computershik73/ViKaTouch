package vikatouch.items.chat;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.locale.TextLocal;
import vikatouch.screens.ChatScreen;
import vikatouch.utils.IntObject;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.ProfileObject;

/**
 * @author Shinovon
 * 
 */
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

		if (type.equalsIgnoreCase("kick_user")) {
			String s1;
			if (VikaTouch.profiles.containsKey(new IntObject(fromid))) {
				s1 = ((ProfileObject) VikaTouch.profiles.get(new IntObject(fromid))).getName();
			} else {
				s1 = "id" + fromid;
			}
			String s2;

			if (VikaTouch.profiles.containsKey(new IntObject(memberid))) {
				s2 = ((ProfileObject) VikaTouch.profiles.get(new IntObject(memberid))).getName();
			} else {
				s2 = "id" + memberid;
			}
			text = TextLocal.inst.getFormatted(fromid == memberid ? "msg.action.leave" : "msg.action.kick", new String[] { s1, s2});

		} else {
			String s1;
			if (VikaTouch.profiles.containsKey(new IntObject(fromid))) {
				s1 = ((ProfileObject) VikaTouch.profiles.get(new IntObject(fromid))).getName();
			} else {
				s1 = "id" + fromid;
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

	public void keyPress(int key) {

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
			// int msgWidthInner = msgWidth;
			// if (foreign) {
			ColorUtils.setcolor(g, ColorUtils.FOREIGNMSG);
			g.fillRoundRect(margin, y, msgWidth, th, radius, radius);
			g.fillRect(margin, y + th - radius, radius, radius);
			textX = margin + h1 / 2;
			if (selected && ScrollableCanvas.keysMode) {
				ColorUtils.setcolor(g, ColorUtils.TEXT);
				g.setStrokeStyle(Graphics.SOLID);
				g.drawRoundRect(margin, y, msgWidth, th, radius, radius);
			}
			/*
			 * } else { ColorUtils.setcolor(g, ColorUtils.MYMSG);
			 * g.fillRoundRect(DisplayUtils.width - (margin + msgWidth), y,
			 * msgWidth, th, radius, radius); g.fillRect(DisplayUtils.width -
			 * (margin + radius), y + th - radius, radius, radius); textX =
			 * DisplayUtils.width - (margin + msgWidth) + h1 / 2; if (selected
			 * && ScrollableCanvas.keysMode) { ColorUtils.setcolor(g,
			 * ColorUtils.TEXT); g.setStrokeStyle(Graphics.SOLID);
			 * g.drawRoundRect(DisplayUtils.width - (margin + msgWidth), y,
			 * msgWidth, th, radius, radius); }
			 */
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
		this.isRead = isRead;
	}

	public boolean isRead() {
		return isRead;
	}

}
