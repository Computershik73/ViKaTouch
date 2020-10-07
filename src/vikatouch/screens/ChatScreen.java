package vikatouch.screens;

import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.VikaCanvas;
import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.popup.ContextMenu;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.screen.VikaScreen;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import vikatouch.VikaTouch;
import vikatouch.canvas.VikaCanvasInst;
import vikatouch.items.chat.MsgItem;
import vikatouch.locale.TextLocal;
import vikatouch.settings.Settings;
import vikatouch.utils.IntObject;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.text.CountUtils;
import vikatouch.utils.text.TextEditor;
import vikatouch.utils.url.URLBuilder;

public class ChatScreen
	extends MainScreen
{
	
	public boolean ready = false;
	
	
	private static final int TYPE_USER = 1;
	private static final int TYPE_CHAT = 2;
	private static final int TYPE_GROUP = 3;
	public int peerId;
	public int localId;
	public int type;
	public static final int OFFSET_INT = 2000000000;
	private static final int msgYMargin = 4;
	public String title = "dialog";
	public String title2 = "оффлайн";
	public String inputText = "";
	private String[] inputedTextToDraw;
	private boolean inputChanged;
	private JSONObject json;
	private JSONObject chatSettings;
	
	private String enterMsgStr = "";
	private String enterMsgStrSel = "";
	private String typingStr = "";
	private String typing2Str = "";
	private String refreshErrorStr = "";
	private String sendingStr = "";
	public static String[] kt;
	
	private boolean scrolledDown = false;
	private int inputBoxH = 48;
	private int inputedLinesCount = 0;
	private int topPanelH = 56;
	
	private int loadSpace = 20;
	private int hasSpace = loadSpace;
	
	public static Thread updater = null;
	public static boolean updStop = false;
	
	public static boolean forceRedraw = false;
	
	public static void stopUpdater()
	{
		
		if(updater!=null && updater.isAlive())
		{
			//updater.interrupt();
			updStop = true;
			updater = null;
		}
		if(typer !=null && typer.isAlive())
		{
			typer.interrupt();
			typer = null;
		}
		if(reporter !=null && reporter.isAlive())
		{
			reporter.interrupt();
			reporter = null;
		}
	}
	
	// 0 - сообщения, 1 - прикреп, 2 - поле, 3 - смайлы, 4 - отправка
	private byte buttonSelected = 0;
	
	// данные для сообщения
	public long answerMsgId = 0;
	public String answerName;
	public String answerText;
	
	public boolean refreshOk = true;
	private long pressTime;
	
	public static void attachAnswer(long id, String name, String text)
	{
		if(VikaTouch.canvas.currentScreen instanceof ChatScreen)
		{
			ChatScreen c = (ChatScreen) VikaTouch.canvas.currentScreen;
			c.answerMsgId = id;
			c.answerName = name;
			c.answerText = text;
		}
	}
	public static void editMsg(final MsgItem msg)
	{
		if(VikaTouch.canvas.currentScreen instanceof ChatScreen)
		{
			final ChatScreen c = (ChatScreen) VikaTouch.canvas.currentScreen;
			if(typer != null && typer.isAlive())
				typer.interrupt();
			typer = new Thread()
			{
				public void run()
				{
					String newText = TextEditor.inputString(TextLocal.inst.get("msg.editing"), msg.text==null?"":msg.text, 0);
					URLBuilder url = new URLBuilder("messages.edit").addField("peer_id", c.peerId)
							.addField("message", newText).addField("keep_forward_messages", "1")
							.addField("keep_snippets", "1").addField("dont_parse_links", "1");
					if(c.type == TYPE_CHAT)
					{
						url = url.addField("conversation_message_id", ""+msg.mid);
					}
					else
					{
						url = url.addField("message_id", ""+msg.mid);
					}
					String res = VikaUtils.download(url);
					msg.ChangeText(newText);
				}
			};
			typer.start();
		}
	}
	
	public static Hashtable profileNames = new Hashtable();
	
	public ChatScreen(int peerId, String title)
	{
		title2 = TextLocal.inst.get("title2.loading");
		this.title = title;
		this.peerId = peerId;
		parse();
	}
	
	public ChatScreen(int peerId)
	{
		title2 = TextLocal.inst.get("title2.loading");
		this.peerId = peerId;
		parse();
	}

	private void parse()
	{
		scrollWithKeys = true;
		VikaCanvasInst.msgColor = 0xffffffff;
		enterMsgStr = TextLocal.inst.get("msg.entermsg");
		enterMsgStrSel = TextLocal.inst.get("msg.keyboard");
		typingStr = TextLocal.inst.get("msg.typing");
		typing2Str = TextLocal.inst.get("msg.typing2");
		refreshErrorStr = TextLocal.inst.get("title2.msgloadingfailed");
		sendingStr = TextLocal.inst.get("msg.sending");
		if(peerId < 0)
		{
			this.localId = -peerId;
			type = TYPE_GROUP;
			//title2 = "group" + this.localId;
			this.title2 = "";
			(new Thread() {
				public void run() {
					messagesDialog();
				}
			}).start();
		}
		else if(peerId > 0)
		{
			if(peerId > OFFSET_INT)
			{
				this.localId = peerId - OFFSET_INT;
				this.type = TYPE_CHAT;
				//title2 = "chat" + this.localId;
				try
				{
					String x = VikaUtils.download(new URLBuilder("messages.getConversationsById").addField("peer_ids", peerId));
					try
					{
						json = new JSONObject(x).getJSONObject("response").getJSONArray("items").getJSONObject(0);
						
						chatSettings = json.getJSONObject("chat_settings");
						
						this.title2 = CountUtils.countStrMembers(chatSettings.optInt("members_count"));
					}
					catch (JSONException e)
					{
						//this.title2 = e.toString();
						this.title2 = "Ошибка JSON";
					}
					
					(new Thread() {
						public void run() {
							messagesChat();
						}
					}).start();
				}
				catch (Exception e)
				{
					this.title2 = TextLocal.inst.get("msg.failedtoload");
				}
			}
			else
			{
				this.localId = peerId;
				this.type = TYPE_USER;
				//title2 = "dm" + this.localId;
				try
				{
					String x = VikaUtils.download(new URLBuilder("users.get").addField("user_ids", peerId).addField("fields", "online").addField("name_case", "nom"));
					try
					{
						JSONObject json = new JSONObject(x).getJSONArray("response").getJSONObject(0);
						this.title2 = json.optInt("online") > 0 ? TextLocal.inst.get("online") : TextLocal.inst.get("msg.offline");
					}
					catch (JSONException e)
					{
						this.title2 = "Ошибка JSON";
					}
				}
				catch (Exception e)
				{
					this.title2 = TextLocal.inst.get("msg.failedtoload");
				}
				(new Thread() {
					public void run() {
						messagesDialog();
					}
				}).start();
			}
		}
	}

	int inr = 0, outr = 0;
	private void messagesChat()
	{
		//VikaTouch.sendLog("Messages in chat mode");
		try
		{
			VikaCanvasInst.msgColor = 0xffff0000;
			// скачка сообщений
			uiItems = new PressableUIItem[Settings.messagesPerLoad+loadSpace];
			//VikaTouch.sendLog("Requesting history");
			String x = VikaUtils.download(new URLBuilder("messages.getHistory").addField("peer_id", peerId).addField("extended", 1).addField("count", Settings.messagesPerLoad).addField("offset", 0));
			//VikaTouch.sendLog("Requesting history ok");
			VikaCanvasInst.msgColor = 0xffffff00;
			JSONObject response = new JSONObject(x).getJSONObject("response");
			JSONArray profiles = response.getJSONArray("profiles");
			JSONArray items = response.getJSONArray("items");
			inr = response.getJSONArray("conversations").getJSONObject(0).optInt("in_read");
			outr = response.getJSONArray("conversations").getJSONObject(0).optInt("out_read");
			
			for(int i = 0; i < profiles.length(); i++)
			{
				JSONObject profile = profiles.getJSONObject(i);
				String firstname = profile.optString("first_name");
				String lastname = profile.optString("last_name");
				int id = profile.optInt("id");
				if(id > 0 && firstname != null)
					profileNames.put(new IntObject(id), firstname + " " + lastname);
			}
			//VikaTouch.sendLog(""+items.length()+" msgs");
			MsgItem last = null;
			for(int i = 0; i < items.length(); i++)
			{
				VikaCanvasInst.msgColor = 0xff00ff00;
				MsgItem m = new MsgItem(items.getJSONObject(i));
				m.parseJSON();
				int fromId = m.fromid; 

				String name = (fromId < 0 ? "g" : "") + "id" + fromId;
				
				if(fromId > 0 && profileNames.containsKey(new IntObject(fromId)))
				{
					name = (String)profileNames.get(new IntObject(fromId));
				}
				
				boolean chain = false;
				if(i+1<items.length())
				{
					chain = fromId == items.getJSONObject(i+1).optInt("from_id");
				}
				m.showName = !chain;
				
				m.name = (m.foreign ? name : "Вы");
				uiItems[uiItems.length-1-i-loadSpace] = m;
				if(i == 0)
				{
					last = m;
				}
				
				itemsCount = (short) uiItems.length;
			}
			if(Settings.autoMarkAsRead && last!=null)
			{
				VikaCanvasInst.msgColor = 0xffff00ff;
				VikaUtils.request(new URLBuilder("messages.markAsRead").addField("start_message_id", ""+last.mid).addField("peer_id", peerId));
				VikaCanvasInst.msgColor = 0xff00ff00;
			}
			x = null;
			items.dispose();
			profiles.dispose();
			response.dispose();
			loadAtts();
		}
		catch (Exception e)
		{
			this.title2 = TextLocal.inst.get("msg.failedtoload2");
			VikaTouch.sendLog(e.toString());
		}
		finally
		{
			ready = true;
		}
	}

	private void messagesDialog()
	{
		try
		{
			VikaCanvasInst.msgColor = 0xffff0000;
			// скачка сообщений
			uiItems = new PressableUIItem[Settings.messagesPerLoad+loadSpace];
			//VikaTouch.sendLog("Requesting history");
			String x = VikaUtils.download(new URLBuilder("messages.getHistory").addField("peer_id", peerId).addField("count", Settings.messagesPerLoad).addField("offset", 0));
			//VikaTouch.sendLog("Requesting history ok");
			VikaCanvasInst.msgColor = 0xffffff00;
			JSONArray json = new JSONObject(x).getJSONObject("response").getJSONArray("items");
			profileNames.put(new IntObject(peerId), title);
			for(int i = 0; i<json.length();i++) 
			{
				VikaCanvasInst.msgColor = 0xff00ff00;
				MsgItem m = new MsgItem(json.getJSONObject(i));
				m.parseJSON();
				int fromId = m.fromid;
				
				boolean chain = false;
				if(i+1<json.length())
				{
					chain = fromId == json.getJSONObject(i+1).optInt("from_id");
				}
				m.showName = !chain;
						
				m.name = (m.foreign?title:"Вы");
				uiItems[uiItems.length-1-i-loadSpace] = m;
				if(Settings.autoMarkAsRead && i == 0)
				{
					VikaCanvasInst.msgColor = 0xffff00ff;
					VikaUtils.request(new URLBuilder("messages.markAsRead").addField("start_message_id", ""+m.mid).addField("peer_id", peerId));
					VikaCanvasInst.msgColor = 0xff00ff00;
				}
			}
			itemsCount = (short) uiItems.length;
			loadAtts();
		}
		catch (Exception e)
		{
			this.title2 = TextLocal.inst.get("msg.failedtoload2");
			e.printStackTrace();
		}
		finally
		{
			ready = true;
		}
	}
	
	public void loadAtts()
	{
		VikaCanvasInst.msgColor = 0xff0000ff;
		VikaTouch.loading = true;
		int i=0;
		try
		{
			for(i=0; i<uiItems.length; i++)
			{
				if(uiItems[i]==null) continue;
				if(uiItems[i] instanceof MsgItem)
				{
					((MsgItem) uiItems[i]).loadAtts();
				}
			}
		}
		catch(Exception e)
		{
			VikaTouch.popup(new InfoPopup("Attachments error, msg "+i+" exc "+e.toString(), null));
		}
		VikaCanvasInst.msgColor = 0xff00ffff;
		try
		{
			forceRedraw = true;
			repaint();
			Thread.sleep(50);
			repaint();
			forceRedraw = false;
			Thread.sleep(50);
			currentItem = markMsgs(inr, outr);
			scrollToSelected();
			uiItems[currentItem].setSelected(true);
		}
		catch (Exception e)
		{
			
		}
		
		System.gc();
		
		try {
			while(updStop) Thread.sleep(200);
			runUpdater();
			VikaTouch.loading = false;
			VikaCanvasInst.msgColor = 0xff000000;
		} catch (InterruptedException e) {
		}
	}
	
	public int markMsgs(int inRead, int outRead)
	{
		try
		{
			boolean inR = false, outR = false;
			int r = 0;
			for(int i=uiItems.length-1; i>=0; i--)
			{
				if(uiItems[i]!=null)
				{
					MsgItem mi = (MsgItem) uiItems[i];
					if(mi.mid == inRead)
					{
						inR = true;
						r = i;
					}
					if(mi.mid == outRead)
						outR = true;
					mi.isRead = mi.foreign?outR:inR;
				}
			}
			return r;
		}
		catch(RuntimeException e)
		{
			return 0;
		}
	}

	public void draw(Graphics g)
	{
		update(g);
		
		try 
		{
			g.translate(0, topPanelH);
			drawDialog(g);
			
			g.translate(0, -g.getTranslateY());
			
			drawHeader(g);
			drawTextbox(g);
		}
		catch (Exception e)
		{
		}
	}
	
	public void drawHUD(Graphics g) 
	{
		
	}

	protected void scrollHorizontally(int deltaX)
	{
		
	}
	
	public final void press(int x, int y)
	{
		pressTime = System.currentTimeMillis();
		if(!dragging)
		{
			if(y > 590)
			{
				//нижняя панель
				
				//текстбокс
				if(x > 50 && x < DisplayUtils.width - 98)
				{
				}
			}
			else if(y < 50)
			{
				//верхняя панель
				if(x < 50)
				{
				}
			}
		}
		super.press(x, y);
	}
	
	public final void release(int x, int y)
	{
		if(!dragging)
		{
			if(y > DisplayUtils.height-inputBoxH-(answerMsgId==0?0:Font.getFont(0, 0, Font.SIZE_SMALL).getHeight()*2))
			{
				if(answerMsgId!=0&&y<DisplayUtils.height-inputBoxH)
				{
					// ответ
					if(x > DisplayUtils.width - 40) answerMsgId = 0;
				}
				else
				{
					//нижняя панель
					
					//текстбокс
					if(x > 50 && x < DisplayUtils.width - 98)
					{
						showTextBox();
					}
					else if(x < 50)
					{
						//прикреп
						VikaTouch.popup(new InfoPopup("Будет реализовано в будущих обновлениях", null));
					}
					else if(x > DisplayUtils.width - 40)
					{
						//отправить
						send();
					}
					else if(x > DisplayUtils.width - 90)
					{
						//емоци и стикеры
						VikaTouch.popup(new InfoPopup("Будет реализовано в будущих обновлениях", null));
					}
				}
			}
			else if(y < 50)
			{
				//верхняя панель
				if(x < 50)
				{
					stopUpdater();
					VikaTouch.inst.cmdsInst.command(14, this);
				}
			}
			else
			{
				msgClick(y, System.currentTimeMillis() - pressTime);
			}
		}
		super.release(x, y);
	}
	
	public void press(int key)
	{
		if(key != -12 && key != -20)
		{
			keysMode = true;
		}
		/*if(VikaTouch.canvas.currentAlert!=null)
		{
			VikaTouch.canvas.currentAlert.press(key);
			repaint();
			return;
		}*/
		if(key == -1)
		{
			up();
		}
		else if(key == -2)
		{
			down();
		}
		else if(key == -3)
		{
			up();
		}
		else if(key == -4)
		{
			down();
		}
		else if(key == -10)
		{
			send();
		}
		else if(key == -5)
		{ // ok
			switch (buttonSelected)
			{
			case 0:
				uiItems[currentItem].keyPressed(key);
				break;
			case 1:
				// прикреп
				VikaTouch.popup(new InfoPopup("Будет реализовано в будущих обновлениях", null));
				break;
			case 2:
				showTextBox();
				break;
			case 3:
				// смайлы
				VikaTouch.popup(new InfoPopup("Будет реализовано в будущих обновлениях", null));
				break;
			case 4:
				send();
				buttonSelected = 2;
				break;
			}
		}
		else if(key == -6)
		{ // lsk
			if(buttonSelected==0)
			{
				buttonSelected = 2;
			}
			else
			{
				buttonSelected = 0;
			}
		}
		else if(key == -7)
		{ // rsk
			stopUpdater();
			VikaTouch.inst.cmdsInst.command(14, this);
		}
		repaint();
	}
	
	protected void down()
	{
		if(buttonSelected == 0)
		{
			keysScroll(-1);
			/*
			try
			{
				uiItems[currentItem].setSelected(false);
			}
			catch (Exception e)
			{ }
			currentItem++;
			if(currentItem >= uiItems.length || uiItems[currentItem] == null)
			{
				currentItem--;
				buttonSelected = 2;
			}
			else
				scrollToSelected();
			uiItems[currentItem].setSelected(true);*/
		}
		else
		{
			buttonSelected++;
			if(buttonSelected>4) buttonSelected = 4;
		}
	}

	protected void up()
	{
		if(buttonSelected == 0)
		{
			keysScroll(+1);
			/*try
			{
				uiItems[currentItem].setSelected(false);
			}
			catch (Exception e) { }
			currentItem--;
			if(currentItem < 0)
			{
				currentItem = 0;
			}
			scrollToSelected();
			try 
			{
				uiItems[currentItem].setSelected(true);
			}
			catch (Exception e) { }*/
		}
		else
		{
			buttonSelected--;
		}
	}
	
	public int getItemY(int n)
	{
		int y=0;
		for(int i=0;(i<uiItems.length&&i<n);i++)
		{
			y += uiItems[i].getDrawHeight();
			y += msgYMargin;
		}
		return y + topPanelH;
	}
	
	public void selectCentered()
	{
		int y = MainScreen.topPanelH;
		int ye = y;
		int s = -scrolled + DisplayUtils.height/2;
		for(int i=0;(i<uiItems.length&&i<uiItems.length);i++)
		{
			if(uiItems[i] == null) return;
			ye = y + uiItems[i].getDrawHeight();
			if(y<=s && ye < s)
			{
				try
				{
					uiItems[currentItem].setSelected(false);
				} catch (Exception e) {}
				try
				{
					uiItems[i].setSelected(true);
					currentItem = i;
				} catch (Exception e) {}
				return;
			}
			y = ye + msgYMargin;
		}
	}
	
	public void repeat(int key)
	{
		if(key != -12 && key != -20)
		{
			keysMode = true;
		}
		if(key == -1)
		{
			up();
		}
		if(key == -2)
		{
			down();
		}
		repaint();
	}

	public boolean canSend = true;
	private static Thread reporter;
	private static Thread typer;
	
	private void send()
	{
		if(!canSend) return;
		canSend = false;
		new Thread()
		{
			public void run()
			{
				try
				{
					VikaTouch.loading = true;
					URLBuilder url = new URLBuilder("messages.send").addField("random_id", new Random().nextInt(10000)).addField("peer_id", peerId).addField("message", inputText).addField("intent", "default");
					if(answerMsgId!=0)
					{
						url = url.addField("reply_to", ""+answerMsgId);
						answerMsgId = 0;
					}
					String res = VikaUtils.download(url);
					if(res==null)
					{
						VikaTouch.popup(new InfoPopup(TextLocal.inst.get("msg.sendneterror"), null));
					}
					else
					{
						inputText = null;
						inputChanged = true;
						inputedLinesCount = 0;
					}
				}
				catch(Exception e)
				{
					VikaTouch.popup(new InfoPopup(TextLocal.inst.get("msg.senderror")+" - "+e.toString(), null));
				}
				finally
				{
					canSend = true;
					VikaTouch.loading = false;
				}
			}
		}.start();
	}
	
	// Удаляет старые сообщения из списка и пододвигает остальные назад, чтоб все влезли.
	private void shiftList()
	{
		currentItem -= loadSpace;
		int deltaScroll = 0;
		for(int i=0;i<loadSpace;i++)
		{
			deltaScroll += uiItems[i].getDrawHeight()+msgYMargin;
		}
		scrolled+=deltaScroll;
		hasSpace += loadSpace;
		for(int i=0;i<uiItems.length;i++)
		{
			if(i - loadSpace >= 0)
			{
				uiItems[i - loadSpace] = uiItems[i];
			}
			uiItems[i] = null;
		}
		System.gc();
	}
	
	JSONArray temp1;
	JSONObject temp2;
	private void update () throws JSONException
	{
		try
		{
			boolean more = true;
			while(more)
			{
				if(updStop) return;
				VikaCanvasInst.updColor = 0xffff0000;
				long mid = ((MsgItem) uiItems[uiItems.length-hasSpace-1]).mid;
				
				final String x = VikaUtils.download(new URLBuilder("messages.getHistory")
						.addField("start_message_id", String.valueOf(mid))
						.addField("peer_id", peerId).addField("count", 1).addField("offset", -1).addField("extended", 1));
				
				JSONArray items;
				VikaCanvasInst.updColor = 0xffffff00;
				try
				{
					if(x == null || x.length()<10) throw new JSONException("Empty answer");
					temp1 = null;
					temp2 = null;
					Thread parser = new Thread()
					{
						public void run()
						{
							try {
								JSONObject res = new JSONObject(x).getJSONObject("response");
								temp1 = res.getJSONArray("items");
								temp2 = res.getJSONArray("conversations").getJSONObject(0);
							} catch (Throwable e) {
								refreshOk = false;
								return;
							}
						}
					};
					parser.start();
					for(int i=0;i<100;i++)
					{
						if(temp1!=null) break;
						Thread.sleep(20);
					}
					if(temp1==null)
					{
						parser.interrupt();
						refreshOk = false;
						return;
					}
					VikaCanvasInst.updColor = 0xffff7f00;
				}
				catch (Exception e)
				{
					refreshOk = false;
					return;
				}
				items = temp1;
				
				int inRead = temp2.optInt("in_read");
				int outRead = temp2.optInt("out_read");
				
				int newMsgCount = items.length();
				System.out.println(newMsgCount+"");
				if(newMsgCount==0)
				{
					more = false;
					break;
				}
				else
				{
					if(items.getJSONObject(0).optLong("id")==mid)
					{
						more = false;
						break;
					}
					VikaCanvasInst.updColor = 0xff00ff00;
					if(updStop) return;
					if(newMsgCount>=hasSpace-1)
					{
						//System.out.println("List shifting");
						shiftList();
					}
					if(type == TYPE_CHAT)
					{
						try
						{
							JSONArray profiles = new JSONObject(x).getJSONObject("response").getJSONArray("profiles");
							for(int i = 0; i < profiles.length(); i++)
							{
								JSONObject profile = profiles.getJSONObject(i);
								String firstname = profile.optString("first_name");
								String lastname = profile.optString("last_name");
								int id = profile.optInt("id");
								if(id > 0 && firstname != null)
									profileNames.put(new IntObject(id), firstname + " " + lastname);
							}
						}
						catch(JSONException e)
						{ }
						catch(NullPointerException e)
						{ }
					}
					VikaCanvasInst.updColor = 0xff0000ff;
					MsgItem[] newMsgs = new MsgItem[newMsgCount];
					for(int i = 0; i < newMsgCount; i++)
					{
						if(updStop) return;
						VikaCanvasInst.updColor = 0xff0000ff;
						MsgItem m = new MsgItem(items.getJSONObject(i));
						m.parseJSON();
						int fromId = m.fromid; 
						String name = "user" + fromId;
						
						if(profileNames.containsKey(new IntObject(fromId)))
						{
							name = (String) profileNames.get(new IntObject(fromId));
						}
						
						boolean chain = false;
						if(i+1<newMsgCount)
						{
							chain = fromId == items.getJSONObject(i+1).optInt("from_id");
						}
						m.showName = !chain;
						m.name = (m.foreign ? name :"Вы");
						newMsgs[i] = m;
						if(updStop) return;
						m.loadAtts();
						try
						{
							if(Settings.autoMarkAsRead)
							{
								VikaCanvasInst.updColor = 0xff00ffff;
								VikaUtils.download(new URLBuilder("messages.markAsRead").addField("start_message_id", ""+m.mid).addField("peer_id", peerId));
							}
						}
						catch(Exception e)
						{
							
						}
					}
					VikaCanvasInst.updColor = 0xffff00ff;
					// аппенд
					for(int i=0; i < newMsgCount; i++)
					{
						if(updStop) return;
						MsgItem m = newMsgs[newMsgCount-i-1];
						uiItems[uiItems.length-hasSpace] = m;
						hasSpace--;
						VikaCanvasInst.updColor = 0xffff00ff;
					}
					markMsgs(inRead, outRead);
				}	
				System.gc();
				
			}
			refreshOk = true;
		}
		catch(JSONException e)
		{
			throw e;
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	private void runUpdater()
	{
		stopUpdater();
		
		updater = new Thread()
		{
			public void run()
			{
				try
				{
					while(updStop) Thread.sleep(200);
				}
				catch (Throwable e)
				{
					VikaCanvasInst.updColor = 0xff000000;
					return;
				}
				while(true)
				{
					try
					{
						for(int i=0; i<Settings.msgRefreshRate; i++)
						{
							Thread.sleep(1000);
							if(updStop)
							{
								updStop = false; return;
							}
						}
					}
					catch (InterruptedException e)
					{ return; } // забавный факт, оно падает при убивании потока во время сна. Я к тому что его надо либо не ловить, либо при поимке завершать галиматью вручную.
					try
					{
						VikaCanvasInst.updColor = 0xffffffff;
						if(updStop)
						{
							updStop = false;
							VikaCanvasInst.updColor = 0xff000000;
							return;
							
						}
						if(VikaTouch.canvas.currentScreen instanceof ChatScreen) update();
					}
					catch (Exception e)
					{
						e.printStackTrace();
						refreshOk = false;
					}
					finally
					{
						VikaCanvasInst.updColor = 0xff000000;
						if(updStop)
						{
							updStop = false;
							return;
						}
					}
				}
			}
		};
		updater.start();
	}
	
	private void showTextBox()
	{
		if(!canSend) return;
		if(typer != null && typer.isAlive())
			typer.interrupt();
		typer = new Thread()
		{
			public void run()
			{
				inputText = TextEditor.inputString(TextLocal.inst.get("msg"), inputText==null?"":inputText, 0);
				inputChanged = true;
			}
		};
		if(reporter != null && reporter.isAlive())
			reporter.interrupt();
		reporter = new Thread()
		{
			public void run()
			{
				while(typer.isAlive())
				{
					URLBuilder url = new URLBuilder("messages.setActivity").addField("user_id", VikaTouch.userId).addField("peer_id", peerId).addField("type", "typing");
					String res = VikaUtils.download(url);
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		};
		typer.start();
		reporter.start();
	}

	private void msgClick(int tapY, long tapTime)
	{
		tapY-=topPanelH;
		VikaCanvasInst.debugString = "hold " + tapTime;
		if(uiItems==null) return;
		int y = 0;
		int gTapY = tapY-scrolled;
		for(int i=0; i<uiItems.length; i++)
		{
			if(uiItems[i] == null) continue;
			y+=msgYMargin;
			int y2 = y+uiItems[i].getDrawHeight();
			if(y<gTapY&&gTapY<y2)
			{
				uiItems[i].tap(0, gTapY-y);
			}
			y = y2;
		}
	}
	
	private void drawDialog(Graphics g)
	{
		if(uiItems==null) return;
		try
		{
			int y = 0;
			for(int i=0; i<uiItems.length; i++)
			{
				if(uiItems[i] == null) continue;
				try
				{
					y+=msgYMargin;
					if(y+scrolled < DisplayUtils.height || forceRedraw) uiItems[i].paint(g, y, scrolled);
					y+=uiItems[i].getDrawHeight();
				}
				catch(RuntimeException e)
				{ }
			}
			this.itemsh = y + 50;
		}
		catch(Exception e)
		{
			VikaTouch.error(e, -8);
		}
	}

	private void drawTextbox(Graphics g)
	{
		// расчёты и обработка текста
		int m = 4; // margin
		int dw = DisplayUtils.width; int dh = DisplayUtils.height;
		Font font = Font.getFont(0, 0, Font.SIZE_SMALL);
		g.setFont(font);
		int answerH = (int)(font.getHeight()*2);
		
		if(inputChanged)
		{
			try
			{
				inputedTextToDraw = TextBreaker.breakText(inputText, false, null, true, DisplayUtils.width - 150);
				inputChanged = false;
				if(inputedTextToDraw != null)
				{
					for(inputedLinesCount = 0; inputedTextToDraw[inputedLinesCount]!=null; inputedLinesCount++) { }
				}
				else
				{
					inputedLinesCount = 0;
				}
			}
			catch (Exception e)
			{
				inputedLinesCount = 0;
			}
			inputBoxH = Math.max(48, font.getHeight()*inputedLinesCount+m*2);
		}
		
		//рендер бокса
		ColorUtils.setcolor(g, -8);
		g.fillRect(0, dh - inputBoxH - 1, dw, 1);
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(0, dh - inputBoxH, dw, inputBoxH);
		
		if(inputedLinesCount == 0)
		{
			if(buttonSelected == 2)
			{
				ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
				g.setFont(Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(enterMsgStrSel, 48, dh-24-font.getHeight()/2, 0);
				g.setFont(font);
			}
			else
			{
				ColorUtils.setcolor(g, ColorUtils.OUTLINE);
				g.drawString(enterMsgStr, 48, dh-24-font.getHeight()/2, 0);
			}
		}
		else
		{
			ColorUtils.setcolor(g, buttonSelected == 2?ColorUtils.BUTTONCOLOR:ColorUtils.TEXT);
			int currY = dh - inputBoxH + m;
			
			for(int i = 0; i < inputedLinesCount; i++)
			{
				if(inputedTextToDraw[i] == null) continue;
				
				g.drawString(inputedTextToDraw[i], 48, currY, 0);
				currY += font.getHeight();
			}
			
		}
		
		g.drawImage((buttonSelected != 1?IconsManager.ico:IconsManager.selIco)[IconsManager.ATTACHMENT], 12, DisplayUtils.height - 36, 0);
		g.drawImage((buttonSelected != 3?IconsManager.ico:IconsManager.selIco)[IconsManager.STICKERS], DisplayUtils.width - 86, DisplayUtils.height - 36, 0);
		if(canSend || (System.currentTimeMillis()%500)<250)
		{
			if(keysMode)
				g.drawImage((buttonSelected != 4?IconsManager.ico:IconsManager.selIco)[inputedLinesCount==0?IconsManager.VOICE:IconsManager.SEND], DisplayUtils.width - 40, DisplayUtils.height - 36, 0);
			else
				g.drawImage(inputedLinesCount==0?IconsManager.ico[IconsManager.VOICE]:IconsManager.selIco[IconsManager.SEND], DisplayUtils.width - 40, DisplayUtils.height - 36, 0);
		}
		if(keysMode) drawKeysTips(g);
		
		if(answerMsgId!=0)
		{
			try
			{
				int rh = inputBoxH+answerH;
				ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
				g.fillRect(0, dh - rh, dw, answerH);
				ColorUtils.setcolor(g, ColorUtils.TEXT);
				g.drawString(answerText, 12, dh - rh+font.getHeight(), 0);
				ColorUtils.setcolor(g, ColorUtils.COLOR1);
				g.drawString(answerName, 12, dh - rh, 0);
				g.fillRect(6, dh - rh+2, 2, answerH-4);
				ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
				g.fillRect(dw-40, dh - rh, 40, answerH);
				g.drawImage(IconsManager.ico[IconsManager.CLOSE], dw - 32, dh-rh+answerH/2-12, 0);
			}
			catch (Exception e) { }
		}
	}

	private void drawHeader(Graphics g)
	{
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(0, 0, DisplayUtils.width, topPanelH-1);
		ColorUtils.setcolor(g, -12);
		g.fillRect(0, topPanelH-1, DisplayUtils.width, 1);
		
		Font font1 = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
		g.setFont(font1);
		ColorUtils.setcolor(g, 0);
		g.drawString(title, 64, 10, 0);
		
		Font font2 = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		g.setFont(font2);
		ColorUtils.setcolor(g, ColorUtils.TEXT2);
		if(!canSend)
		{
			g.drawString(sendingStr, 64, 30, 0);
		}
		else if(refreshOk)
		{
			g.drawString(title2, 64, 30, 0);
		}
		else
		{
			g.setColor(255, 0, 0);
			g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
			g.drawString(this.refreshErrorStr, 64, 30, 0);
		}
		
		g.drawImage(IconsManager.selIco[IconsManager.BACK], 16, 16, 0);
		g.drawImage(IconsManager.selIco[IconsManager.INFO], DisplayUtils.width - 38, 16, 0);
	}

	private void drawKeysTips(Graphics g)
	{
		String left; String ok; String right;
		if(kt==null)
		{
			kt = new String[] { 
				TextLocal.inst.get("back"),
				TextLocal.inst.get("msg.write"),
				TextLocal.inst.get("up"),
				TextLocal.inst.get("options"),
				TextLocal.inst.get("msg.attach"),
				TextLocal.inst.get("keyboard"),
				TextLocal.inst.get("msg.stickers"),
				TextLocal.inst.get("msg.send"),
				TextLocal.inst.get("msg.sending"),
				TextLocal.inst.get("close")
			};
		}
		if(VikaTouch.canvas.currentAlert==null)
		{
			right = kt[0];
			left = buttonSelected==0?kt[1]:kt[2];
			ok = canSend?((new String[] {kt[3], kt[4], kt[5], kt[6], kt[7]})[buttonSelected]):kt[8];
		}
		else if(VikaTouch.canvas.currentAlert instanceof ContextMenu)
		{
			right = kt[9];
			left = "";
			ContextMenu m = (ContextMenu) VikaTouch.canvas.currentAlert;
			ok = m.items[m.selected].text;
		}
		else
		{
			return;
		}
		Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
		int h = f.getHeight();
		int y = DisplayUtils.height-h;
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(0, y, DisplayUtils.width, h);
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.fillRect(0, y-1, DisplayUtils.width, 1);
		
		int o = 4;
		g.drawString(left, o, y, 0);
		g.drawString(right, DisplayUtils.width-(o+f.stringWidth(right)), y, 0);
		g.drawString(ok, DisplayUtils.width/2-(f.stringWidth(ok)/2), y, 0);
	}
}
