package vikatouch.screens;

import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import tube42.lib.imagelib.ImageUtils;
import vikatouch.Dialogs;
import vikatouch.VikaTouch;
import vikatouch.canvas.VikaCanvasInst;
import vikatouch.items.chat.CommentItem;
import vikatouch.items.chat.ConversationItem;
import vikatouch.items.chat.MsgItem;
import vikatouch.locale.TextLocal;
import vikatouch.settings.Settings;
import vikatouch.utils.IntObject;
import vikatouch.utils.ProfileObject;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.text.CountUtils;
import vikatouch.utils.url.URLBuilder;

public class CommentsScreen extends MainScreen {
	private String title;
	private int post_id;
	private int owner_id;
	private boolean can_write;
	private int loadSpace = 20;
	private int hasSpace = loadSpace;
	private boolean lastcommentloaded=false;
	private int commentscount;
	private static final int msgYMargin = 4;
	public static CommentItem[] comments = new CommentItem[0];
	
	public CommentsScreen(int sourceid, int id) {
		VikaTouch.needstoRedraw=true;
		commentscount=-1;
		title = TextLocal.inst.get("title.comments");
		hasBackButton = true;
		this.post_id=id;
		this.owner_id=sourceid;
		comments = new CommentItem[50];
		
		if (DisplayUtils.compact) {
			topPanelH = 30;
		}
		if (VikaTouch.isresending==true) {
		//
		//VikaTouch.isresending=false;
		}
		
		can_write=true;
		//li=null;
		// VikaTouch.sendLog(String.valueOf(this.title) + " " +
		// String.valueOf(this.peerId));
		parse();
	}
	
	
	
	private void parse() {
		VikaTouch.needstoRedraw=true;
		commentscount=-1;
		int errst = 0;
		scrollWithKeys = true;
		errst = 1;
		VikaCanvasInst.msgColor = 0xffffffff;
		
		
		
		
		
		
		
		errst = 8;
		
			errst = 9;
			
			errst = 11;
			
			
			errst = 12;
			// title2 = "group" + this.localId;
			
			errst = 13;
			(new Thread() {
				public void run() {
					try {
						loadComments();
					} catch (Throwable ee) {}
				}
			}).start();
			errst = 14;
		

					
				
			
		
	}

	public void loadComments() {
		// TODO Auto-generated method stub
		String x="";
		lastcommentloaded=false;
		try {
			x = VikaUtils.download(new URLBuilder("wall.getComments").addField("post_id", String.valueOf(post_id))
					.addField("owner_id", owner_id).addField("thread_items_count", "3").addField("extended", "1").addField("count", "100"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//"https://api.vk.com/method/wall.getComments?access_token=a4ffeaf275bb8e33e1b23c19b3bbe5401968ae66b45cf64a82bbe619ccdbfcb904ac27fddfdf6fbd174de&v=5.130&post_id=770640&owner_id=-36741297&thread_items_count=3&extended=1";
		
		
		
			JSONObject response = new JSONObject(x).optJSONObject("response");
			//VikaTouch.sendLog(response.toString());
			JSONArray profiles = response.optJSONArray("profiles");
			
			JSONArray groups = response.optJSONArray("groups");
			
			
			
			try {
				if(profiles != null) {
					for(int i = 0; i < profiles.length(); i++) {
						JSONObject profile = profiles.getJSONObject(i);
						if(!VikaTouch.profiles.containsKey(new IntObject(profile.getInt("id"))) && profile != null)
							VikaTouch.profiles.put(new IntObject(profile.getInt("id")), 
									new ProfileObject(profile.getInt("id"), 
											profile.getString("first_name"), profile.getString("last_name"), 
											profile.getString("photo_50").indexOf("camera_50") > -1 ? "camera_50." : profile.getString("photo_50"), profile.optString("online")));
					}
				}
			} catch (Exception e) {
				
			}
			try {
				if(groups != null) {
					for(int i = 0; i < groups.length(); i++) {
						JSONObject group = groups.getJSONObject(i);
						if(!VikaTouch.profiles.containsKey(new IntObject(-group.getInt("id"))) && group != null)
							VikaTouch.profiles.put(new IntObject(-group.getInt("id")), 
									new ProfileObject(group.getInt("id"), 
											group.getString("name"), 
											group.getString("photo_50").indexOf("camera_50") > -1 ? "camera_50." : group.getString("photo_50")));
					}
				}
			} catch (Exception e) {
				
			}
			
			
			
			JSONArray items = response.optJSONArray("items");
			//VikaTouch.sendLog(String.valueOf(items.length()));
			//int i=0;
			commentscount = items.length(); 
			for (int i=0; i<items.length(); i++)
			{
				//if (items.isNull(i)) {
				//	break;
				//}
				try {
				CommentItem m = new CommentItem(items.getJSONObject(i), false);
				m.parseJSON();
				m.getAva();
			//	int fromId = m.fromid;

				/*boolean chain = false;
				if (i + 1 < items.length()) {
					chain = fromId == items.optJSONObject(i + 1).optInt("from_id");
				}
				m.showName = !chain;*/

				//m.setName(m.foreign ? title : TextLocal.inst.get("msg.you"));
				//uiItems[uiItems.length - 1 - i - loadSpace] = m;
				comments[i]=m;
				} catch (JSONException e) {
					
					VikaTouch.error(-2, "loadComments " + e.toString(), false);
				}
				//VikaTouch.sendLog(String.valueOf(i));
			}
			this.lastcommentloaded=true;
			loadAtts();
		
	}
	
	
	public void loadAtts() {
		VikaTouch.needstoRedraw=true;
		VikaCanvasInst.msgColor = 0xff0000ff;
		VikaTouch.loading = true;
		int i = 0;
		try {
			for (i = 0; i < comments.length; i++) {
				if (comments[i] == null)
					continue;
				//if (comments[i] instanceof CommentItem) {
					comments[i].loadAtts();
				//}
			}
		} catch (Throwable e) {
			//VikaTouch.popup(new InfoPopup("Attachments error, msg " + i + " exc " + e.toString(), null));
		}
		VikaCanvasInst.msgColor = 0xff00ffff;
		try {
			
			repaint();
			Thread.sleep(50);
			repaint();
			
			Thread.sleep(50);
			
			//scrollToSelected();
			//comments[currentItem].setSelected(true);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		System.gc();

	}



	public void draw(Graphics g) {
		
		update(g);
		
		
		
		try {
			g.translate(0, topPanelH);
			drawComments(g);

			g.translate(0, -g.getTranslateY());

			drawHeader(g);
			//drawTextbox(g);
			/*
			 * g.setColor(0, 0, 0); g.fillRect(0, 60, 300, 40); g.setColor(200, 200, 200);
			 * g.drawString(scrlDbg, 0, 60, 0);
			 * g.drawString("scr:"+scrolled+" i"+currentItem, 0,80,0);
			 */
		} catch (Throwable e) {
			e.printStackTrace();
			//VikaTouch.sendLog(e.getMessage());
		}
		//g.translate(0, -g.getTranslateY());
	}

	

	private void drawTextbox(Graphics g) {
		// TODO Auto-generated method stub
		
	}



	private void drawHeader(Graphics g) {

		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(0, 0, DisplayUtils.width, topPanelH - 1);
		ColorUtils.setcolor(g, -12);
		g.fillRect(0, topPanelH - 1, DisplayUtils.width, 1);

		Font font1;
		//if (DisplayUtils.verycompact) {
			font1 = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
		//} else {
		//font1 = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
		//}
	    int yy=0;
		double xx=1;
		if (DisplayUtils.verycompact) {
			yy=15;
		} else {
			if (DisplayUtils.compact) {
				yy=10;
			}
			
		}
		if (DisplayUtils.verycompact) {
			xx=1;
		} else {
			if (DisplayUtils.compact) {
				xx=1;
			}
			
		}
		
		
		g.setFont(font1);
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		if (!this.lastcommentloaded) {
			title = TextLocal.inst.get("title.comments")+ (commentscount!=-1 ? " ("+String.valueOf(commentscount)+")..." : "...");
		} else {
			title = TextLocal.inst.get("title.comments")+" "+(commentscount!=-1 ? "("+String.valueOf(commentscount)+")" : "");
		}
		g.drawString(TextBreaker.shortText(title, DisplayUtils.width - 50 + yy - 38, font1), 50-yy, (int)Math.floor(0+1*xx), 0);

		//Font font2 = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		//g.setFont(font2);
		//ColorUtils.setcolor(g, ColorUtils.TEXT2);
		

		g.drawImage(IconsManager.selIco[IconsManager.BACK], 16-yy, 20-2*yy, 0);
		g.drawImage(IconsManager.selIco[IconsManager.INFO], DisplayUtils.width - 38+yy, 20-2*yy, 0);
		
	
		
	}
	
	public final void press(int key) {
		VikaTouch.needstoRedraw=true;
		if (key != -12 && key != -20) {
			keysMode = true;
		}
		if (key == -5) {
			
			comments[currentItem].keyPress(-5);
		} else if (key == -6) {
			callRefresh();
			repaint();
		} else if (key == -1) {
			up();
		} else if (key == -2) {
			down();
		} else
			super.press(key);
		
		VikaTouch.needstoRedraw=true;
	}

	protected final void up() {
		VikaTouch.needstoRedraw=true;
		try {
			comments[currentItem].setSelected(false);
		} catch (Exception e) {

		}
		currentItem--;
		if (currentItem < 0) {
			currentItem = 0;
					//Dialogs.itemsCount--;
		}
		try {
			scrollToSelected();
			comments[currentItem].setSelected(true);
			VikaTouch.needstoRedraw=true;
		} catch (Exception e) {

		}
	}

	protected final void down() {
		VikaTouch.needstoRedraw=true;
		try {
			comments[currentItem].setSelected(false);
			VikaTouch.needstoRedraw=true;
		} catch (Exception e) {

		}
		currentItem++;
		if (currentItem >= comments.length-1) {
			currentItem = comments.length-1;
			
		}
		/*if (currentItem >= Dialogs.itemsCount-1) {
			currentItem = Dialogs.itemsCount-2;
			VikaTouch.needstoRedraw=true;
			Dialogs.itemsCount+=10;
			//Settings.dialogsLength=Dialogs.itemsCount;
			Dialogs.loadMore();
			VikaTouch.needstoRedraw=true;
		}*/
		try {
		scrollToSelected();
		comments[currentItem].setSelected(true);
		} catch (Throwable eee) {
			
		} 
		VikaTouch.needstoRedraw=true;
	}

	

	public final void scrollToSelected() {
		VikaTouch.needstoRedraw=true;
		int itemy = 0;
		for (int i = 0; (i < comments.length && i < currentItem); i++) {
			itemy += comments[i].getDrawHeight(); // не УМНОЖИТЬ! айтемы могут быть разной высоты.
		}
		if (comments[currentItem] != null) {
			scrolled = -(itemy - DisplayUtils.height / 2 + (comments[currentItem].getDrawHeight() / 2)
					+ MainScreen.topPanelH);
			//if (scrolled>)
		}
	}



	private void drawComments(Graphics g) {
			VikaTouch.needstoRedraw=true;
			if (comments == null) {
				//VikaTouch.sendLog("nullcomments");
				return;
			}
			try {
				int y = 0;
				for (int i = 0; i < comments.length; i++) {
					if (comments[i] == null)
						continue;
					try {
						y += msgYMargin;
						if (y + scrolled < DisplayUtils.height) 
							comments[i].paint(g, y, scrolled);
						//VikaTouch.sendLog("paint : " + String.valueOf(i));
						//y += msgYMargin;
						y += comments[i].getDrawHeight();
					} catch (RuntimeException e) {
					}
				}
				this.itemsh = y + 50;
			} catch (Throwable e) {
				// VikaTouch.error(e, -8);
				VikaTouch.sendLog(e.getMessage());
			}
		
	}



	public final void drawHUD(Graphics g) {
		drawHUD(g, title);
	}
	
	private void shiftList() {
		currentItem -= loadSpace;
		int deltaScroll = 0;
		for (int i = 0; i < loadSpace; i++) {
			deltaScroll += comments[i].getDrawHeight() + msgYMargin;
		}
		scrolled += deltaScroll;
		hasSpace += loadSpace;
		for (int i = 0; i < comments.length; i++) {
			if (i - loadSpace >= 0) {
				comments[i - loadSpace] = comments[i];
			}
			comments[i] = null;
		}
		System.gc();
	}
	
	public void onLeave() {
		comments=new CommentItem[0];
	}

}

