package vikatouch.screens;

import java.io.IOException;
import java.util.Random;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.VikaCanvas;
import ru.nnproject.vikaui.menu.EmptyMenu;
import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.menu.items.OptionItem;
import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.popup.ContextMenu;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import vikatouch.VikaFileManager;
import vikatouch.VikaTouch;
import vikatouch.canvas.VikaCanvasInst;
import vikatouch.items.chat.ActionItem;
import vikatouch.items.chat.ChatItem;
import vikatouch.items.chat.IMessage;
import vikatouch.items.chat.MsgItem;
import vikatouch.locale.TextLocal;
import vikatouch.screens.menu.ChatMembersScreen;
import vikatouch.screens.page.GroupPageScreen;
import vikatouch.screens.page.ProfilePageScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.IntObject;
import vikatouch.utils.ProfileObject;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.text.CountUtils;
import vikatouch.utils.text.TextEditor;
import vikatouch.utils.url.URLBuilder;

/**
 * @author Feodor0090
 * 
 */
public class ChatScreen extends MainScreen {

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
	// private String typingStr = "";
	// private String typing2Str = "";
	private String refreshErrorStr = "";
	private String sendingStr = "";
	private String[] kt;

	// private boolean scrolledDown = false;
	private int inputBoxH = 48;
	private int inputedLinesCount = 0;
	private int topPanelH = 56;

	private int loadSpace = 20;
	private int hasSpace = loadSpace;

	public static Thread updater = null;
	public static boolean updStop = false;

	public static boolean forceRedraw = false;

	public static void stopUpdater() {

		if (updater != null && updater.isAlive()) {
			// updater.interrupt();
			updStop = true;
			updater = null;
		}
		if (typer != null && typer.isAlive()) {
			typer.interrupt();
			typer = null;
		}
		if (reporter != null && reporter.isAlive()) {
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

	public static void attachAnswer(long id, String name, String text) {
		if (VikaTouch.canvas.currentScreen instanceof ChatScreen) {
			ChatScreen c = (ChatScreen) VikaTouch.canvas.currentScreen;
			c.answerMsgId = id;
			c.answerName = name;
			c.answerText = text;
		}
	}

	public static void editMsg(final MsgItem msg) {
		if (VikaTouch.canvas.currentScreen instanceof ChatScreen) {
			final ChatScreen c = (ChatScreen) VikaTouch.canvas.currentScreen;
			if (typer != null && typer.isAlive())
				typer.interrupt();
			typer = new Thread() {
				public void run() {
					String newText = TextEditor.inputString(TextLocal.inst.get("msg.editing"),
							msg.text == null ? "" : msg.text, 0);
					URLBuilder url = new URLBuilder("messages.edit").addField("peer_id", c.peerId)
							.addField("message", newText).addField("keep_forward_messages", "1")
							.addField("keep_snippets", "1").addField("dont_parse_links", "1");
					if (c.type == TYPE_CHAT) {
						url = url.addField("conversation_message_id", "" + msg.getMessageId());
					} else {
						url = url.addField("message_id", "" + msg.getMessageId());
					}
					/* String res = */
					try {
						VikaUtils.download(url);
						msg.ChangeText(newText);
					} catch (InterruptedException e) {
						return;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			typer.start();
		}
	}

	public ChatScreen(int peerId, String title) {
		title2 = TextLocal.inst.get("title2.loading");
		this.title = title;
		this.peerId = peerId;
		// VikaTouch.sendLog(String.valueOf(this.title) + " " +
		// String.valueOf(this.peerId));
		parse();
	}

	public ChatScreen(int peerId) {
		title2 = TextLocal.inst.get("title2.loading");
		this.peerId = peerId;
		parse();
	}

	private void parse() {
		int errst = 0;
		scrollWithKeys = true;
		errst = 1;
		VikaCanvasInst.msgColor = 0xffffffff;
		errst = 2;
		enterMsgStr = TextLocal.inst.get("msg.entermsg");
		errst = 3;
		enterMsgStrSel = TextLocal.inst.get("msg.keyboard");
		errst = 4;
		// typingStr = TextLocal.inst.get("msg.typing");
		errst = 5;
		// typing2Str = TextLocal.inst.get("msg.typing2");
		errst = 6;
		refreshErrorStr = TextLocal.inst.get("title2.msgloadingfailed");
		errst = 7;
		sendingStr = TextLocal.inst.get("msg.sending");
		errst = 8;
		if (peerId < 0) {
			errst = 9;
			this.localId = -peerId;
			errst = 11;
			type = TYPE_GROUP;
			errst = 12;
			// title2 = "group" + this.localId;
			this.title2 = "";
			errst = 13;
			(new Thread() {
				public void run() {
					try {
						messagesDialog();
					} catch (InterruptedException e) {
						return;
					} catch (IOException e) {
						e.printStackTrace();
						VikaTouch.error(-2, "x1 " + e.toString(), false);
						ChatScreen.this.title2 = TextLocal.inst.get("msg.failedtoload");
					}
				}
			}).start();
			errst = 14;
		} else if (peerId > 0) {
			errst = 15;
			if (peerId > OFFSET_INT) {
				errst = 16;
				this.localId = peerId - OFFSET_INT;
				errst = 17;
				this.type = TYPE_CHAT;
				errst = 18;
				// title2 = "chat" + this.localId;
				try {
					errst = 19;
					String x = VikaUtils
							.download(new URLBuilder("messages.getConversationsById").addField("peer_ids", peerId));
					errst = 20;
					try {
						errst = 21;
						json = new JSONObject(x).optJSONObject("response").optJSONArray("items").optJSONObject(0);
						errst = 22;

						chatSettings = json.optJSONObject("chat_settings");
						errst = 23;
						this.title2 = CountUtils.countStrMembers(members = chatSettings.optInt("members_count"));
						errst = 24;
					} catch (JSONException e) {
						VikaTouch.error(-2, errst + " x2 " + e.toString(), false);
						// this.title2 = e.toString();
						this.title2 = "Ошибка JSON";
					}
					errst = 25;
					(new Thread() {
						public void run() {
							try {
								messagesChat();
							} catch (InterruptedException e) {
								return;
							} catch (IOException e) {
								e.printStackTrace();
								VikaTouch.error(-2, "x7 " + e.toString(), false);
								ChatScreen.this.title2 = TextLocal.inst.get("msg.failedtoload");
							}
						}
					}).start();
					errst = 26;
				} catch (Throwable e) {
					this.title2 = TextLocal.inst.get("msg.failedtoload");
					this.title2 = String.valueOf(errst);
					VikaTouch.error(-2, errst + " " + e.toString(), false);
				}
			} else {
				this.localId = peerId;
				this.type = TYPE_USER;
				// title2 = "dm" + this.localId;
				try {
					String x = VikaUtils.download(new URLBuilder("users.get").addField("user_ids", peerId)
							.addField("fields", "online").addField("name_case", "nom"));
					try {
						JSONObject json = new JSONObject(x).optJSONArray("response").optJSONObject(0);
						this.title2 = json.optInt("online") > 0 ? TextLocal.inst.get("online")
								: TextLocal.inst.get("msg.offline");
					} catch (JSONException e) {
						this.title2 = "Ошибка JSON";
						VikaTouch.error(-2, "x4 " + e.toString(), false);
					}

					(new Thread() {
						public void run() {
							try {
								messagesDialog();
							} catch (InterruptedException e) {
								return;
							} catch (IOException e) {
								e.printStackTrace();
								ChatScreen.this.title2 = TextLocal.inst.get("msg.failedtoload");
								VikaTouch.error(-2, "x5 " + e.toString() + " " + e.getMessage(), false);
							}
						}
					}).start();
				} catch (Throwable e) {
					VikaTouch.error(-2, "x6 " + e.toString(), false);
					this.title2 = TextLocal.inst.get("msg.failedtoload");
				}
			}
		}
	}

	int inr = 0, outr = 0;

	private int members;

	private boolean hasPinnedMessage;

	private String pinName;

	private String pinText;

	private int pinId;

	private void messagesChat() throws IOException, InterruptedException {
		try {
			String errst = "f";
			// VikaTouch.sendLog("Messages in chat mode");

			VikaCanvasInst.msgColor = 0xffff0000;
			// скачка сообщений
			uiItems = new PressableUIItem[Settings.messagesPerLoad + loadSpace];
			// VikaTouch.sendLog("Requesting history");

			String x = VikaUtils.download(new URLBuilder("messages.getHistory").addField("peer_id", peerId)
					.addField("extended", 1).addField("count", Settings.messagesPerLoad).addField("offset", 0));

			String x2 = VikaUtils
					.download(new URLBuilder("messages.getConversationMembers").addField("peer_id", peerId));
			// VikaTouch.sendLog("Requesting history ok");
			errst = "history";
			VikaCanvasInst.msgColor = 0xffffff00;
			JSONObject response = new JSONObject(x).optJSONObject("response");
			JSONObject response2 = new JSONObject(x2).optJSONObject("response");
			errst = "response";
			JSONArray profiles = response2.optJSONArray("profiles");
			errst = "profiles";
			JSONArray groups = response2.optJSONArray("groups");
			errst = "groups";
			JSONArray items = response.optJSONArray("items");
			errst = "items";
			inr = response.optJSONArray("conversations").optJSONObject(0).optInt("in_read");
			outr = response.optJSONArray("conversations").optJSONObject(0).optInt("out_read");
			errst = "outr";
			try {
				if(profiles != null) {
					for(int i = 0; i < profiles.length(); i++) {
						JSONObject profile = profiles.getJSONObject(i);
						if(!VikaTouch.profiles.containsKey(new IntObject(profile.getInt("id"))) && profile != null)
							VikaTouch.profiles.put(new IntObject(profile.getInt("id")), 
									new ProfileObject(profile.getInt("id"), 
											profile.getString("first_name"), profile.getString("last_name"), 
											profile.getString("photo_50").indexOf("camera_50") > -1 ? "camera_50." : profile.getString("photo_50")));
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
			// VikaTouch.sendLog(""+items.length()+" msgs");
			// MsgItem last = null;
			for (int i = 0; i < items.length(); i++) {
				try {
					VikaCanvasInst.msgColor = 0xff00ff00;
					errst = "msg" + String.valueOf(i);
					JSONObject j = items.optJSONObject(i);
					if (j == null) {
					} else if (j.optJSONObject("action") != null) {
						ActionItem act = new ActionItem(j);
						act.parseJSON();
						uiItems[uiItems.length - 1 - i - loadSpace] = act;
						itemsCount = (short) uiItems.length;
					} else {
						MsgItem m = new MsgItem(j);
						errst = "msgit" + String.valueOf(i);
						m.parseJSON();
						errst = "mparse" + String.valueOf(i);
						int fromId = m.fromid;

						String name = (fromId < 0 ? "g" : "") + "id" + fromId;

						if (VikaTouch.profiles.containsKey(new IntObject(fromId))) {
							name = ((ProfileObject) VikaTouch.profiles.get(new IntObject(fromId))).getName();
						}

						boolean chain = false;
						if (i + 1 < items.length()) {
							chain = fromId == items.getJSONObject(i + 1).optInt("from_id");
						}
						m.showName = !chain;

						m.setName(m.foreign ? name : TextLocal.inst.get("msg.you"));
						errst = "mui" + String.valueOf(i);
						uiItems[uiItems.length - 1 - i - loadSpace] = m;
						errst = "mui2" + String.valueOf(i);
						if (i == 0) {
							// last = m;
						}
						errst = "mui3" + String.valueOf(i);
						itemsCount = (short) uiItems.length;
						errst = "mui4" + String.valueOf(i);
					}
				} catch (Throwable e) {
					if (e instanceof InterruptedException) {
						throw (InterruptedException) e;
					}
					System.out.println(errst);
					e.printStackTrace();
					/*
					 * try { Thread.sleep(2000); } catch (InterruptedException e1) {
					 * e1.printStackTrace(); }
					 */
					// VikaTouch.sendLog(errst + e.getMessage());
					this.title2 = errst + e.getMessage();
					// TextLocal.inst.get("msg.failedtoload2");

				}
			}

			items.dispose();
			profiles.dispose();
			response.dispose();
			if (!DisplayUtils.compact) {
				try {
					x = VikaUtils
							.download(new URLBuilder("messages.getConversationsById").addField("peer_ids", peerId));
					System.out.println(x);
					JSONObject j1 = new JSONObject(x).getJSONObject("response");
					JSONObject j = j1.getJSONArray("items").getJSONObject(0).getJSONObject("chat_settings")
							.getJSONObject("pinned_message");
					hasPinnedMessage = true;
					pinText = j.optString("text");
					pinId = j.getInt("id");
					int fromid = j.getInt("from_id");
					if (VikaTouch.profiles.containsKey(new IntObject(fromid))) {
						pinName = ((ProfileObject) VikaTouch.profiles.get(new IntObject(fromid))).getName();
					} else {
						pinName = "id" + fromid;
					}
					if (pinName == null) {
						pinName = "id" + fromid;
					}
					if (pinText == null) {
						if (j.getJSONArray("attachments").length() > 1) {
							pinText = "Вложения";
						} else if (j.getJSONArray("attachments").length() > 0) {
							pinText = "Вложение";
						} else {
							pinText = "";
						}
					} else {
						pinText = VikaUtils.replace(pinText, "\n", " ");
						pinText = TextBreaker.shortText(pinText, DisplayUtils.width - 48, Font.getFont(0, 0, 8));
					}
				} catch (Throwable e) {
					e.printStackTrace();
					hasPinnedMessage = false;
				}
			}
			/*
			 * if(Settings.autoMarkAsRead && last!=null) { VikaCanvasInst.msgColor =
			 * 0xffff00ff; VikaUtils.request(new
			 * URLBuilder("messages.markAsRead").addField("start_message_id",
			 * ""+last.mid).addField("peer_id", peerId)); VikaCanvasInst.msgColor =
			 * 0xff00ff00; errst="msgauto"; }
			 */
			try {
				if (Settings.autoMarkAsRead) {
					VikaCanvasInst.updColor = 0xff00ffff;
					VikaUtils.download(new URLBuilder("messages.markAsRead").addField("peer_id", peerId));
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
			x = null;
			errst = "msgdisp";
			loadAtts();
			errst = "loadat";
			ready = true;

			// finally
			// {

			// }
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void messagesDialog() throws IOException, InterruptedException {

		VikaCanvasInst.msgColor = 0xffff0000;
		// скачка сообщений
		uiItems = new PressableUIItem[Settings.messagesPerLoad + loadSpace];
		// VikaTouch.sendLog("Requesting history");
		String x = VikaUtils.download(new URLBuilder("messages.getHistory").addField("peer_id", peerId)
				.addField("count", Settings.messagesPerLoad).addField("offset", 0).addField("extended", 1));
		// VikaTouch.sendLog("Requesting history ok");
		VikaCanvasInst.msgColor = 0xffffff00;
		JSONObject res = new JSONObject(x).optJSONObject("response");
		JSONArray json = res.optJSONArray("items");
		if(!VikaTouch.profiles.containsKey(new IntObject(peerId)))
			VikaTouch.profiles.put(new IntObject(peerId), new ProfileObject(localId, title, null));
		inr = res.optJSONArray("conversations").optJSONObject(0).optInt("in_read");
		outr = res.optJSONArray("conversations").optJSONObject(0).optInt("out_read");

		for (int i = 0; i < json.length(); i++) {
			try {
				VikaCanvasInst.msgColor = 0xff00ff00;
				MsgItem m = new MsgItem(json.getJSONObject(i));
				m.parseJSON();
				int fromId = m.fromid;

				boolean chain = false;
				if (i + 1 < json.length()) {
					chain = fromId == json.optJSONObject(i + 1).optInt("from_id");
				}
				m.showName = !chain;

				m.setName(m.foreign ? title : TextLocal.inst.get("msg.you"));
				uiItems[uiItems.length - 1 - i - loadSpace] = m;
				if (Settings.autoMarkAsRead && i == 0) {
					VikaCanvasInst.msgColor = 0xffff00ff;
					VikaUtils.request(new URLBuilder("messages.markAsRead")
							.addField("start_message_id", "" + m.getMessageId()).addField("peer_id", peerId));
					VikaCanvasInst.msgColor = 0xff00ff00;
				}
			} catch (Throwable e) {
				e.printStackTrace();
				this.title2 = TextLocal.inst.get("msg.failedtoload2");
				VikaTouch.error(-2, "chat 6 " + e.toString(), false);
				// VikaTouch.sendLog(e.getMessage());
			}
		}
		itemsCount = (short) uiItems.length;
		loadAtts();

		ready = true;

	}

	public void loadAtts() {
		VikaCanvasInst.msgColor = 0xff0000ff;
		VikaTouch.loading = true;
		int i = 0;
		try {
			for (i = 0; i < uiItems.length; i++) {
				if (uiItems[i] == null)
					continue;
				if (uiItems[i] instanceof MsgItem) {
					((MsgItem) uiItems[i]).loadAtts();
				}
			}
		} catch (Throwable e) {
			VikaTouch.popup(new InfoPopup("Attachments error, msg " + i + " exc " + e.toString(), null));
		}
		VikaCanvasInst.msgColor = 0xff00ffff;
		try {
			forceRedraw = true;
			repaint();
			Thread.sleep(50);
			repaint();
			forceRedraw = false;
			Thread.sleep(50);
			currentItem = markMsgs(inr, outr);
			scrollToSelected();
			uiItems[currentItem].setSelected(true);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		System.gc();

		try {
			while (updStop)
				Thread.sleep(200);
			runUpdater();
			VikaTouch.loading = false;
			VikaCanvasInst.msgColor = 0xff000000;
		} catch (InterruptedException e) {
		}
	}

	public int markMsgs(int inRead, int outRead) {
		try {
			int l = Math.min(inRead, outRead);
			// VikaTouch.sendLog("in:"+inRead+" out:"+outRead);
			boolean r = false;
			int rn = 0;
			for (int i = uiItems.length - 1; i >= 0; i--) {
				if (uiItems[i] != null) {
					IMessage mi = (IMessage) uiItems[i];
					if (mi.getMessageId() == l) {
						r = true;
						rn = i;
					}
					mi.setRead(r);
				}
			}
			return rn;
		} catch (Throwable e) {
			e.printStackTrace();
			VikaTouch.sendLog("Marking: " + e.toString());
			return 0;
		}
	}

	public void draw(Graphics g) {
		update(g);

		try {
			g.translate(0, topPanelH);
			drawDialog(g);

			g.translate(0, -g.getTranslateY());

			drawHeader(g);
			drawTextbox(g);
			/*
			 * g.setColor(0, 0, 0); g.fillRect(0, 60, 300, 40); g.setColor(200, 200, 200);
			 * g.drawString(scrlDbg, 0, 60, 0);
			 * g.drawString("scr:"+scrolled+" i"+currentItem, 0,80,0);
			 */
		} catch (Throwable e) {
			e.printStackTrace();
			VikaTouch.sendLog(e.getMessage());
		}
	}

	public void drawHUD(Graphics g) {

	}

	protected void scrollHorizontally(int deltaX) {

	}

	public final void press(int x, int y) {
		pressTime = System.currentTimeMillis();
		if (!dragging) {
			if (y > 590) {
				// нижняя панель

				// текстбокс
				if (x > 50 && x < DisplayUtils.width - 98) {
				}
			} else if (y < 50) {
				// верхняя панель
				if (x < 50) {
				}
			}
		}
		super.press(x, y);
	}

	public final void release(int x, int y) {
		if (!dragging) {
			if (y > DisplayUtils.height - inputBoxH
					- (answerMsgId == 0 ? 0 : Font.getFont(0, 0, Font.SIZE_SMALL).getHeight() * 2)) {
				if (answerMsgId != 0 && y < DisplayUtils.height - inputBoxH) {
					// ответ
					if (x > DisplayUtils.width - 40)
						answerMsgId = 0;
				} else {
					// нижняя панель

					// текстбокс
					if (x > 50 && x < DisplayUtils.width - 98) {
						showTextBox();
					} else if (x < 50) {
						// прикреп
						addAtt();
					} else if (x > DisplayUtils.width - 40) {
						// отправить
						send();
					} else if (x > DisplayUtils.width - 90) {
						// емоци и стикеры
						VikaTouch.popup(new InfoPopup("Будет реализовано в будущих обновлениях", null));
					}
				}
			} else if (y < topPanelH) {
				// верхняя панель
				if (y < 56) {
					if (x < 50) {
						stopUpdater();
						VikaTouch.inst.cmdsInst.command(14, this);
					} else if (x > DisplayUtils.width - 50) {
						if (this.type == TYPE_USER) {
							VikaTouch.setDisplay(new ProfilePageScreen(this.localId), 1);
						} else if (this.type == TYPE_GROUP) {
							VikaTouch.setDisplay(new GroupPageScreen(this.localId), 1);
						} else if (this.type == TYPE_CHAT) {
							String x2 = CountUtils.countStrMembers(this.members);
							VikaTouch.setDisplay(new ChatMembersScreen(this.peerId, x2, this.members), 1);
						}
					}
				} else {
					if (hasPinnedMessage && pinId != 0) {
						this.currentItem = pinId;
						scrollToSelected();
					}
				}
			} else {
				msgClick(y, System.currentTimeMillis() - pressTime);
			}
		}
		super.release(x, y);
	}

	public void press(int key) {
		if (key != -12 && key != -20) {
			keysMode = true;
		}
		/*
		 * if(VikaCanvas.currentAlert!=null) {
		 * VikaCanvas.currentAlert.press(key); repaint(); return; }
		 */
		if (key == -1) {
			up();
		} else if (key == -2) {
			down();
		} else if (key == -3) {
			up();
		} else if (key == -4) {
			down();
		} else if (key == -10) {
			send();
		} else if (key == -5) { // ok
			switch (buttonSelected) {
			case 0:
				uiItems[currentItem].keyPress(key);
				break;
			case 1:
				// прикреп
				// VikaTouch.popup(new InfoPopup("Будет реализовано в будущих обновлениях",
				// null));
				addAtt();
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
		} else if (key == -6) { // lsk
			if (buttonSelected == 0) {
				buttonSelected = 2;
			} else {
				buttonSelected = 0;
			}
		} else if (key == -7) { // rsk
			stopUpdater();
			VikaTouch.inst.cmdsInst.command(14, this);
		}
		repaint();
	}

	private void addAtt() {

		IMenu m = new EmptyMenu() {
			public void onMenuItemPress(int i) {
				try {
					final boolean fotka;
					if (i == 0) {
						fotka = true;
					} else if (i == 1) {
						fotka = false;
					} else {
						fotka = false;
					}

					IMenu m = new EmptyMenu() {

						public void onMenuItemPress(int i) {
							try {
								if (i == 0) {
									if (fotka) {
										VikaFileManager.chatPhoto(ChatScreen.this);
									} else {
										VikaTouch.popup(new InfoPopup("не реализовано", null));
									}
								} else if (i == 1) {
									VikaTouch.popup(new InfoPopup("не реализовано", null));
								} else if(i == 2) {
									VikaTouch.setDisplay(new CameraScreen(ChatScreen.this), 1);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					OptionItem[] oi = new OptionItem[2];
					if(fotka) {
						oi = new OptionItem[3];
					}
					try {
						oi[0] = new OptionItem(m, TextLocal.inst.get("msg.attach.memory"), IconsManager.DEVICE, 0, 50);
						oi[1] = new OptionItem(m, TextLocal.inst.get("msg.attach.album"), IconsManager.ATTACHMENT, 1, 50);
						if(fotka) {
							oi[2] = new OptionItem(m, TextLocal.inst.get("msg.attach.camera"), IconsManager.CAMERA, 2, 50);
						}
					} catch (Exception e) {
					}
					VikaTouch.popup(new ContextMenu(oi));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		OptionItem[] oi = new OptionItem[2];
		try {
			oi[0] = new OptionItem(m, TextLocal.inst.get("msg.attach.photo"), IconsManager.PHOTOS, 0, 50);
			oi[1] = new OptionItem(m, TextLocal.inst.get("msg.attach.doc"), IconsManager.DOCS, 1, 50);
		} catch (Exception e) {
		}
		VikaTouch.popup(new ContextMenu(oi));
	}

	protected void down() {
		if (buttonSelected == 0) {
			keysScroll(-1);
			/*
			 * try { uiItems[currentItem].setSelected(false); } catch (Throwable e) { }
			 * currentItem++; if(currentItem >= uiItems.length || uiItems[currentItem] ==
			 * null) { currentItem--; buttonSelected = 2; } else scrollToSelected();
			 * uiItems[currentItem].setSelected(true);
			 */
		} else {
			buttonSelected++;
			if (buttonSelected > 4)
				buttonSelected = 4;
		}
	}

	protected void up() {
		if (buttonSelected == 0) {
			keysScroll(+1);
			/*
			 * try { uiItems[currentItem].setSelected(false); } catch (Throwable e) { }
			 * currentItem--; if(currentItem < 0) { currentItem = 0; } scrollToSelected();
			 * try { uiItems[currentItem].setSelected(true); } catch (Throwable e) { }
			 */
		} else {
			buttonSelected--;
		}
	}

	public int getItemY(int n) {
		int y = 0;
		for (int i = 0; (i < uiItems.length && i < n); i++) {
			if (uiItems[i] != null) {
				y += uiItems[i].getDrawHeight();
				y += msgYMargin;
			}
		}
		return y + topPanelH;
	}

	public void selectCentered() {
		System.out.println("select center in chat");
		int y = MainScreen.topPanelH;
		int ye = y;
		int s = -scrolled + DisplayUtils.height / 2;
		for (int i = 0; (i < uiItems.length && i < uiItems.length); i++) {
			if (uiItems[i] == null)
				return;
			ye = y + uiItems[i].getDrawHeight();
			if (y <= s && ye > s) {
				try {
					uiItems[currentItem].setSelected(false);
				} catch (Throwable e) {
				}
				try {
					uiItems[i].setSelected(true);
					currentItem = i;
				} catch (Throwable e) {
				}
				return;
			}
			y = ye + msgYMargin;
		}
	}

	public void repeat(int key) {
		if (key != -12 && key != -20) {
			keysMode = true;
		}
		if (key == -1) {
			up();
		}
		if (key == -2) {
			down();
		}
		repaint();
	}

	public boolean canSend = true;
	private static Thread reporter;
	private static Thread typer;

	private void send() {
		if (!canSend)
			return;
		canSend = false;
		new Thread() {
			public void run() {
				try {
					VikaTouch.loading = true;
					URLBuilder url = new URLBuilder("messages.send").addField("random_id", new Random().nextInt(10000))
							.addField("peer_id", peerId).addField("message", inputText).addField("intent", "default");
					if (answerMsgId != 0) {
						url = url.addField("reply_to", "" + answerMsgId);
						answerMsgId = 0;
					}
					String res = VikaUtils.download(url);
					if (res == null) {
						VikaTouch.popup(new InfoPopup(TextLocal.inst.get("msg.sendneterror"), null));
					} else {
						inputText = null;
						inputChanged = true;
						inputedLinesCount = 0;
					}
				} catch (Throwable e) {
					VikaTouch.popup(new InfoPopup(TextLocal.inst.get("msg.senderror") + " - " + e.toString(), null));
				} finally {
					canSend = true;
					VikaTouch.loading = false;
				}
			}
		}.start();
	}

	// Удаляет старые сообщения из списка и пододвигает остальные назад, чтоб все
	// влезли.
	private void shiftList() {
		currentItem -= loadSpace;
		int deltaScroll = 0;
		for (int i = 0; i < loadSpace; i++) {
			deltaScroll += uiItems[i].getDrawHeight() + msgYMargin;
		}
		scrolled += deltaScroll;
		hasSpace += loadSpace;
		for (int i = 0; i < uiItems.length; i++) {
			if (i - loadSpace >= 0) {
				uiItems[i - loadSpace] = uiItems[i];
			}
			uiItems[i] = null;
		}
		System.gc();
	}

	JSONArray temp1;
	JSONObject temp2;

	private void update() throws JSONException {
		try {
			boolean more = true;
			while (more) {
				if (updStop)
					return;
				if(VikaTouch.canvas.currentScreen != this) {
					return;
				}
				VikaCanvasInst.updColor = 0xffff0000;
				long mid = ((IMessage) uiItems[uiItems.length - hasSpace - 1]).getMessageId();
				final String x = VikaUtils.download(new URLBuilder("messages.getHistory")
						.addField("start_message_id", String.valueOf(mid)).addField("peer_id", peerId)
						.addField("count", 1).addField("offset", -1).addField("extended", 1));

				JSONArray items;
				VikaCanvasInst.updColor = 0xffffff00;
				try {
					if (x == null || x.length() < 10)
						throw new JSONException("Empty answer");
					temp1 = null;
					temp2 = null;
					Thread parser = new Thread() {
						public void run() {
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
					for (int i = 0; i < 100; i++) {
						if (temp1 != null)
							break;
						Thread.sleep(20);
					}
					if (temp1 == null) {
						parser.interrupt();
						refreshOk = false;
						return;
					}
					VikaCanvasInst.updColor = 0xffff7f00;
				} catch (Throwable e) {
					refreshOk = false;
					return;
				}
				items = temp1;

				int inRead = temp2.optInt("in_read");
				int outRead = temp2.optInt("out_read");

				int newMsgCount = items.length();
				System.out.println(newMsgCount + "");
				if (newMsgCount == 0) {
					markMsgs(inRead, outRead);
					more = false;
					break;
				} else {
					if (items.getJSONObject(0).optLong("id") == mid) {
						markMsgs(inRead, outRead);
						more = false;
						break;
					}
					VikaCanvasInst.updColor = 0xff00ff00;
					if (updStop)
						return;
					if (newMsgCount >= hasSpace - 1) {
						// System.out.println("List shifting");
						shiftList();
					}
					if (type == TYPE_CHAT) {
						try {
							JSONObject x2 = new JSONObject(x).getJSONObject("response");
							JSONArray profiles = x2.optJSONArray("profiles");
							JSONArray groups = x2.optJSONArray("groups");
							if(profiles != null) 
								try {
									for(int i = 0; i < profiles.length(); i++) {
										JSONObject profile = profiles.getJSONObject(i);
										if(!VikaTouch.profiles.containsKey(new IntObject(profile.getInt("id"))) && profile != null)
											VikaTouch.profiles.put(new IntObject(profile.getInt("id")), 
													new ProfileObject(profile.getInt("id"), 
															profile.getString("first_name"), profile.getString("last_name"), 
															profile.optString("photo_50")));
									}
								} catch (Exception e) {
									
								}
							if(groups != null)
								try {
									for(int i = 0; i < groups.length(); i++) {
										JSONObject group = groups.getJSONObject(i);
										if(!VikaTouch.profiles.containsKey(new IntObject(-group.getInt("id"))) && group != null)
											VikaTouch.profiles.put(new IntObject(-group.getInt("id")), 
													new ProfileObject(group.getInt("id"), 
															group.getString("name"), 
															group.optString("photo_50")));
									}
								} catch (Exception e) {
									
								}
							x2.dispose();
							profiles.dispose();
							groups.dispose();
							x2 = null;
						} catch (Throwable e) {
							
						}
					}
					VikaCanvasInst.updColor = 0xff0000ff;
					ChatItem[] newMsgs = new ChatItem[newMsgCount];
					for (int i = 0; i < newMsgCount; i++) {
						if (updStop)
							return;
						VikaCanvasInst.updColor = 0xff0000ff;
						JSONObject j = items.getJSONObject(i);
						if (j.optJSONObject("action") != null) {
							ActionItem act = new ActionItem(j);
							act.parseJSON();
							newMsgs[i] = act;
						} else {
							MsgItem m = new MsgItem(j);
							m.parseJSON();
							int fromId = m.fromid;
							String name = "id" + fromId;

							if (VikaTouch.profiles.containsKey(new IntObject(fromId))) {
								name = ((ProfileObject) VikaTouch.profiles.get(new IntObject(fromId))).getName();
							}

							boolean chain = false;
							if (i + 1 < newMsgCount) {
								chain = fromId == items.getJSONObject(i + 1).optInt("from_id");
							}
							m.showName = !chain;
							m.setName(m.foreign ? name : TextLocal.inst.get("msg.you"));
							newMsgs[i] = m;
							if (updStop)
								return;
							m.loadAtts();
							try {
								if (Settings.autoMarkAsRead) {
									VikaCanvasInst.updColor = 0xff00ffff;
									System.out.println(m.getMessageId());
									VikaUtils.download(new URLBuilder("messages.markAsRead")
											.addField("start_message_id", "" + m.getMessageId())
											.addField("peer_id", peerId));
								}
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					}
					VikaCanvasInst.updColor = 0xffff00ff;
					// аппенд
					for (int i = 0; i < newMsgCount; i++) {
						if (updStop)
							return;
						ChatItem m = newMsgs[newMsgCount - i - 1];
						uiItems[uiItems.length - hasSpace] = m;
						hasSpace--;
						VikaCanvasInst.updColor = 0xffff00ff;
					}
					markMsgs(inRead, outRead);
				}

				System.gc();

			}
			refreshOk = true;
		} catch (JSONException e) {
			e.printStackTrace();
			// throw e;
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void runUpdater() {
		stopUpdater();

		updater = new Thread() {
			public void run() {
				try {
					while (updStop)
						Thread.sleep(200);
				} catch (Throwable e) {
					VikaCanvasInst.updColor = 0xff000000;
					return;
				}
				while (true) {
					try {
						for (int i = 0; i < Settings.msgRefreshRate; i++) {
							Thread.sleep(1000);
							if (updStop) {
								updStop = false;
								return;
							}
						}
					} catch (InterruptedException e) {
						return;
					} // забавный факт, оно падает при убивании потока во время сна. Я к тому что его
						// надо либо не ловить, либо при поимке завершать галиматью вручную.
					try {
						VikaCanvasInst.updColor = 0xffffffff;
						if (updStop) {
							updStop = false;
							VikaCanvasInst.updColor = 0xff000000;
							return;

						}
						if (VikaTouch.canvas.currentScreen instanceof ChatScreen)
							update();
					} catch (Throwable e) {
						e.printStackTrace();
						refreshOk = false;
					} finally {
						VikaCanvasInst.updColor = 0xff000000;
						if (updStop) {
							updStop = false;
							return;
						}
					}
				}
			}
		};
		updater.start();
	}

	private void showTextBox() {
		if (!canSend)
			return;
		if (typer != null && typer.isAlive())
			typer.interrupt();
		typer = new Thread() {
			public void run() {
				inputText = TextEditor.inputString(TextLocal.inst.get("msg"), inputText == null ? "" : inputText, 0);
				inputChanged = true;
			}
		};
		if (reporter != null && reporter.isAlive())
			reporter.interrupt();
		reporter = new Thread() {
			public void run() {
				while (typer.isAlive()) {
					try {
						/* String res = */try {
							VikaUtils.download(
									new URLBuilder("messages.setActivity").addField("user_id", VikaTouch.userId)
											.addField("peer_id", peerId).addField("type", "typing"));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
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

	private void msgClick(int tapY, long tapTime) {
		tapY -= topPanelH;
		if (uiItems == null)
			return;
		int y = 0;
		int gTapY = tapY - scrolled;
		for (int i = 0; i < uiItems.length; i++) {
			if (uiItems[i] == null)
				continue;
			y += msgYMargin;
			int y2 = y + uiItems[i].getDrawHeight();
			if (y < gTapY && gTapY < y2) {
				uiItems[i].tap(0, gTapY - y);
			}
			y = y2;
		}
	}

	private void drawDialog(Graphics g) {
		if (uiItems == null)
			return;
		try {
			int y = 0;
			for (int i = 0; i < uiItems.length; i++) {
				if (uiItems[i] == null)
					continue;
				try {
					y += msgYMargin;
					if (y + scrolled < DisplayUtils.height || forceRedraw)
						uiItems[i].paint(g, y, scrolled);
					y += uiItems[i].getDrawHeight();
				} catch (RuntimeException e) {
				}
			}
			this.itemsh = y + 50;
		} catch (Throwable e) {
			// VikaTouch.error(e, -8);
			VikaTouch.sendLog(e.getMessage());
		}
	}

	private void drawTextbox(Graphics g) {
		// расчёты и обработка текста
		int m = 4; // margin
		int dw = DisplayUtils.width;
		int dh = DisplayUtils.height;
		Font font = Font.getFont(0, 0, Font.SIZE_SMALL);
		g.setFont(font);
		int answerH = (int) (font.getHeight() * 2);

		if (inputChanged) {
			try {
				inputedTextToDraw = TextBreaker.breakText(inputText, false, null, true, DisplayUtils.width - 150);
				inputChanged = false;
				if (inputedTextToDraw != null) {
					for (inputedLinesCount = 0; inputedTextToDraw[inputedLinesCount] != null; inputedLinesCount++) {
					}
				} else {
					inputedLinesCount = 0;
				}
			} catch (Throwable e) {
				inputedLinesCount = 0;
			}
			inputBoxH = Math.max(48, font.getHeight() * inputedLinesCount + m * 2);
		}

		// рендер бокса
		ColorUtils.setcolor(g, -8);
		g.fillRect(0, dh - inputBoxH - 1, dw, 1);
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(0, dh - inputBoxH, dw, inputBoxH);

		if (inputedLinesCount == 0) {
			if (buttonSelected == 2) {
				ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
				g.setFont(Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(enterMsgStrSel, 48, dh - 24 - font.getHeight() / 2, 0);
				g.setFont(font);
			} else {
				ColorUtils.setcolor(g, ColorUtils.OUTLINE);
				g.drawString(enterMsgStr, 48, dh - 24 - font.getHeight() / 2, 0);
			}
		} else {
			ColorUtils.setcolor(g, buttonSelected == 2 ? ColorUtils.BUTTONCOLOR : ColorUtils.TEXT);
			int currY = dh - inputBoxH + m;

			for (int i = 0; i < inputedLinesCount; i++) {
				if (inputedTextToDraw[i] == null)
					continue;

				g.drawString(inputedTextToDraw[i], 48, currY, 0);
				currY += font.getHeight();
			}

		}

		g.drawImage((buttonSelected != 1 ? IconsManager.ico : IconsManager.selIco)[IconsManager.ATTACHMENT], 12,
				DisplayUtils.height - 36, 0);
		g.drawImage((buttonSelected != 3 ? IconsManager.ico : IconsManager.selIco)[IconsManager.STICKERS],
				DisplayUtils.width - 86, DisplayUtils.height - 36, 0);
		if (canSend || (System.currentTimeMillis() % 500) < 250) {
			if (keysMode)
				g.drawImage((buttonSelected != 4 ? IconsManager.ico : IconsManager.selIco)[inputedLinesCount == 0
						? IconsManager.VOICE
						: IconsManager.SEND], DisplayUtils.width - 40, DisplayUtils.height - 36, 0);
			else
				g.drawImage(
						inputedLinesCount == 0 ? IconsManager.ico[IconsManager.VOICE]
								: IconsManager.selIco[IconsManager.SEND],
						DisplayUtils.width - 40, DisplayUtils.height - 36, 0);
		}
		if (keysMode)
			drawKeysTips(g);

		if (answerMsgId != 0) {
			try {
				int rh = inputBoxH + answerH;
				ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
				g.fillRect(0, dh - rh, dw, answerH);
				ColorUtils.setcolor(g, ColorUtils.TEXT);
				g.drawString(answerText, 12, dh - rh + font.getHeight(), 0);
				ColorUtils.setcolor(g, ColorUtils.COLOR1);
				g.drawString(answerName, 12, dh - rh, 0);
				g.fillRect(6, dh - rh + 2, 2, answerH - 4);
				ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
				g.fillRect(dw - 40, dh - rh, 40, answerH);
				g.drawImage(IconsManager.ico[IconsManager.CLOSE], dw - 32, dh - rh + answerH / 2 - 12, 0);
			} catch (Throwable e) {
			}
		}
	}

	private void drawHeader(Graphics g) {
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(0, 0, DisplayUtils.width, topPanelH - 1);
		ColorUtils.setcolor(g, -12);
		g.fillRect(0, topPanelH - 1, DisplayUtils.width, 1);

		Font font1 = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
		g.setFont(font1);
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawString(TextBreaker.shortText(title, DisplayUtils.width - 50 - 38, font1), 50, 12, 0);

		Font font2 = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		g.setFont(font2);
		ColorUtils.setcolor(g, ColorUtils.TEXT2);
		if (!canSend) {
			g.drawString(sendingStr, 50, 32, 0);
		} else if (refreshOk) {
			g.drawString(title2, 50, 32, 0);
		} else {
			g.setColor(255, 0, 0);
			g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
			g.drawString(this.refreshErrorStr, 50, 30, 0);
		}

		g.drawImage(IconsManager.selIco[IconsManager.BACK], 16, 16, 0);
		g.drawImage(IconsManager.selIco[IconsManager.INFO], DisplayUtils.width - 38, 16, 0);
		if (hasPinnedMessage) {

			Font font = Font.getFont(0, 0, Font.SIZE_SMALL);
			g.setFont(font);
			int dw = DisplayUtils.width;
			int sty = 56;
			int pinh = (int) (font.getHeight() * 2);
			ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
			g.fillRect(0, sty, dw, pinh);
			ColorUtils.setcolor(g, ColorUtils.TEXT);
			g.drawString(pinText, 28, sty + font.getHeight(), 0);
			ColorUtils.setcolor(g, ColorUtils.COLOR1);
			g.drawString(pinName, 28, sty, 0);
			// g.fillRect(6, sty + 2, 2, pinh - 4);
			// ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
			// g.fillRect(dw - 40, sty, 40, pinh);
			g.drawImage(IconsManager.ico[IconsManager.PIN], 4, 56 + (font.getHeight() / 4), 0);
			topPanelH = 56 + pinh + 2;
		}
	}

	private void drawKeysTips(Graphics g) {
		String left;
		String ok;
		String right;
		if (kt == null) {
			kt = new String[] { TextLocal.inst.get("back"), TextLocal.inst.get("msg.write"), TextLocal.inst.get("up"),
					TextLocal.inst.get("options"), TextLocal.inst.get("msg.attach"), TextLocal.inst.get("keyboard"),
					TextLocal.inst.get("msg.stickers"), TextLocal.inst.get("msg.send"),
					TextLocal.inst.get("msg.sending"), TextLocal.inst.get("close") };
		}
		if (VikaCanvas.currentAlert == null) {
			right = kt[0];
			left = buttonSelected == 0 ? kt[1] : kt[2];
			ok = canSend ? ((new String[] { kt[3], kt[4], kt[5], kt[6], kt[7] })[buttonSelected]) : kt[8];
		} else if (VikaCanvas.currentAlert instanceof ContextMenu) {
			right = kt[9];
			left = "";
			ContextMenu m = (ContextMenu) VikaCanvas.currentAlert;
			ok = m.items[m.selected].text;
		} else {
			return;
		}
		Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
		int h = f.getHeight();
		int y = DisplayUtils.height - h;
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(0, y, DisplayUtils.width, h);
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.fillRect(0, y - 1, DisplayUtils.width, 1);

		int o = 4;
		g.drawString(left, o, y, 0);
		g.drawString(right, DisplayUtils.width - (o + f.stringWidth(right)), y, 0);
		g.drawString(ok, DisplayUtils.width / 2 - (f.stringWidth(ok) / 2), y, 0);
	}
}
