package vikatouch.screens;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextField;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.RecordControl;

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

import vikatouch.NokiaUIInvoker;
//import vikatouch.NokiaUITextEditor;
//import vikatouch.NokiaUITextEditorListener;
import vikatouch.TestUtils;
import vikatouch.VikaFileManager;
import vikatouch.VikaNetworkError;
import vikatouch.VikaTouch;
import vikatouch.canvas.VikaCanvasInst;
import vikatouch.items.chat.ActionItem;
import vikatouch.items.chat.ChatItem;
import vikatouch.items.chat.IMessage;
import vikatouch.items.chat.MsgItem;
import vikatouch.items.chat.UnsentMsgItem;
import vikatouch.locale.TextLocal;
import vikatouch.music.MusicPlayer;
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
import vikatouch.utils.HttpMultipartRequest;

/**
 * @author Feodor0090
 * 
 */
public class ChatScreen extends MainScreen implements PlayerListener {

	public boolean ready = false;

	public static final int TYPE_USER = 1;
	public static final int TYPE_CHAT = 2;
	public static final int TYPE_GROUP = 3;
	public static int peerId;
	public static int localId;
	public static int type;
	public static final int OFFSET_INT = 2000000000;
	private static final int msgYMargin = 4;
	public static String title = "dialog";
	public String title2 = "оффлайн";
	public static String inputText = "";
	private String[] inputedTextToDraw;
	public static boolean inputChanged;
	private JSONObject json;
	private JSONObject chatSettings;
	private JSONObject conversationinfo;
	public  ChatScreen inst;
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
	
	//Голосовухи
	
	public static Player pl;
	public static RecordControl rec;
	public boolean isRec = false;
	public static boolean isRecRunning = false;
	public static ByteArrayOutputStream output;

	
	public static String uploadUrl;

	
	public String status = "...";
	
	//private NokiaUITextEditorListener li = null;

	public static void stopUpdater() {
		try {
		if (updater != null && updater.isAlive()) {
			// updater.interrupt();
			updStop = true;
			updater = null;
		}
		if (typer != null && typer.isAlive()) {
			try {
			typer.interrupt();
			} catch (Throwable ee) {}
			typer = null;
		}
		if (reporter != null && reporter.isAlive()) {
			try {
			reporter.interrupt();
			} catch (Throwable ee) {}
			reporter = null;
		}
		} catch (Throwable e) {}
	}

	// 0 - сообщения, 1 - прикреп, 2 - поле, 3 - смайлы, 4 - отправка
	private static byte buttonSelected = 0;

	// данные для сообщения
	public static long answerMsgId = 0;
	public static String objectId = "";
	public static String answerName;
	public static String answerText;
	public boolean canWrite;

	public boolean refreshOk = true;
	private long pressTime;

	private int unread_count;

	public static Thread msgthread;

	private static String peercountry;

	public static String peerlanguage;

	public static void attachAnswer(long id, String name, String text) {
		if (VikaTouch.canvas.currentScreen instanceof ChatScreen) {
			//ChatScreen c = (ChatScreen) VikaTouch.canvas.currentScreen;
			ChatScreen.answerMsgId = id;
			ChatScreen.answerName = name;
			ChatScreen.answerText = text;
			ChatScreen.buttonSelected=2;
			ChatScreen.showTextBox();
			VikaTouch.needstoRedraw=true;
			//serviceRepaints();
			VikaTouch.canvas.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			canSend=true;
			//VikaTouch.sendLog("id: "+String.valueOf(id)+" name: "+String.valueOf(name)+" text: "+text);
		}
	}
	
	public static void attachAnswer(String resendingobjectid, String resendingname, String resendingtext) {
		if (VikaTouch.canvas.currentScreen instanceof ChatScreen) {
			//ChatScreen c = (ChatScreen) VikaTouch.canvas.currentScreen;
			//c.answerMsgId = id;
			ChatScreen.objectId=resendingobjectid;
			ChatScreen.answerName = resendingname;
			ChatScreen.answerText = resendingtext;
			ChatScreen.buttonSelected=2;
			ChatScreen.showTextBox();
			VikaTouch.needstoRedraw=true;
			//serviceRepaints();
			VikaTouch.canvas.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			canSend=true;
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
					if (!msg.foreign) {
					URLBuilder url = new URLBuilder("messages.edit").addField("peer_id", c.peerId)
							.addField("message", newText).addField("keep_forward_messages", "1")
							.addField("keep_snippets", "1").addField("dont_parse_links", "1");
					//if (c.type == TYPE_CHAT) {
					//	url = url.addField("conversation_message_id", "" + msg.getMessageId());
					//} else {
						url = url.addField("message_id", "" + msg.getMessageId());
					//}
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
				}
			};
			typer.start();
		}
	}

	public ChatScreen(int peerId, String title, int unread) {
		VikaTouch.isscrolling=true;
		VikaTouch.needstoRedraw=true;
		inst = this;
		title2 = TextLocal.inst.get("title2.loading");
		this.title = title;
		this.peerId = peerId;
		this.unread_count=unread;
		if (DisplayUtils.compact) {
			topPanelH = 30;
		}
		if (VikaTouch.isresending==true) {
		//
		//VikaTouch.isresending=false;
		}
		VikaTouch.isRecording=false;
		canWrite=true;
		//li=null;
		// VikaTouch.sendLog(String.valueOf(this.title) + " " +
		// String.valueOf(this.peerId));
		try {
		parse();
		} catch (Throwable e) {}
	}

	public ChatScreen(int peerId) {
		VikaTouch.isscrolling=true;
		VikaTouch.needstoRedraw=true;
		inst = this;
		//VikaTouch.resendingobjectid="";
		if (DisplayUtils.compact) {
			topPanelH = 30;
		}
		title2 = TextLocal.inst.get("title2.loading");
		this.peerId = peerId;
		//this.title = "loading";
		try {
			if(!VikaTouch.profiles.containsKey(new IntObject(peerId))) {
				this.title = ((ProfileObject)VikaTouch.profiles.get(new IntObject(peerId))).getName();
			} else {
				this.title = "notfound";
			}
		//VikaTouch.profiles.put(new IntObject(peerId), new ProfileObject(localId, title, null));
			} catch (Throwable e2) {
				this.title = "titledialog";
			}
		canWrite=true;
		try {
		parse();
	} catch (Throwable e) {}
	}

	private void parse() {
		VikaTouch.needstoRedraw=true;
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
			ChatScreen.peercountry = null;
			ChatScreen.peerlanguage=VikaTouch.mylanguage;
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
					//	return;
					} /*catch (IOException e) {
						e.printStackTrace();
						VikaTouch.error(-2, "x1 " + e.toString(), false);
						ChatScreen.this.title2 = TextLocal.inst.get("msg.failedtoload");*/
					//} 
				catch (Throwable ee) {}
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
				ChatScreen.peercountry = null;
				ChatScreen.peerlanguage=VikaTouch.mylanguage;
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
						unread_count = json.optInt("unread_count");
						//conversationinfo =  new JSONObject(x).optJSONObject("response").optJSONArray("items").optJSONObject(0);
						canWrite =  new JSONObject(x).optJSONObject("response").optJSONArray("items").optJSONObject(0).optJSONObject("can_write").getBoolean("allowed");
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
							} catch (Throwable e) {
								return;
							} /*catch (IOException e) {
								e.printStackTrace();
								VikaTouch.error(-2, "x7 " + e.toString(), false);
								ChatScreen.this.title2 = TextLocal.inst.get("msg.failedtoload");
							}*/
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
				boolean a = true;
				// title2 = "dm" + this.localId;
				m:	
				try {
					
						//a= true;
					String xxx = VikaUtils
							.download(new URLBuilder("messages.getConversationsById").addField("peer_ids", peerId));
					
					canWrite =  new JSONObject(xxx).optJSONObject("response").optJSONArray("items").optJSONObject(0).optJSONObject("can_write").getBoolean("allowed");
					
					String x = VikaUtils.download(new URLBuilder("users.get").addField("user_ids", peerId)
							.addField("fields", "online,country,can_write_private_message,sex,last_seen").addField("name_case", "nom"));
					try {
						JSONObject jj = new JSONObject(x);
						
						if (jj.has("response")) {
						JSONObject json = new JSONObject(x).optJSONArray("response").optJSONObject(0);
						//unread_count = json.optInt("unread_count");
						//VikaTouch.sendLog(String.valueOf(unread_count));
					//	canWrite =  (json.optInt("can_write_private_message")==1);
					//	this.title2 = json.optInt("online") > 0 ? TextLocal.inst.get("msg.online")
					//			: TextLocal.inst.get("msg.offline");
						int sex = json.optInt("sex");
						String wasOnlineStr = (sex==1) ? TextLocal.inst.get("femalewasonlinedate") : TextLocal.inst.get("wasonlinedate");
						String onlineStr = TextLocal.inst.get("online");
						String minutesAgoStr = TextLocal.inst.get("date.minutesago");
						String hoursAgoStr = TextLocal.inst.get("date.hoursago");
						String wasOnlineJustNowStr = wasOnlineStr + " " + TextLocal.inst.get("date.justnow");
						
						int lastSeen = 0;
						try {
							lastSeen = json.optJSONObject("last_seen").optInt("time");
						} catch (Throwable e) {
						}
						boolean online = json.optInt("online") == 1;

						String visitStr;
						if (online) {
							visitStr = onlineStr;
						} else {
							int now = (int) (System.currentTimeMillis() / 1000);
							int r = now - lastSeen;
							if (r < 90) {
								visitStr = wasOnlineJustNowStr;
							} else if (r < 60 * 60) {
								visitStr = wasOnlineStr + " " + (r / 60) + " " + minutesAgoStr;
							} else {
								visitStr = wasOnlineStr + " " + (r / 3600) + " " + hoursAgoStr;
							}
						}
						this.title2 = visitStr;
						
						if (json.has("country")) {
						JSONObject country = json.optJSONObject("country");
						if (country==null) {
							ChatScreen.peercountry = null;
							ChatScreen.peerlanguage=VikaTouch.mylanguage;
						} else {
							
							ChatScreen.peercountry =  country.optString("id");
							ChatScreen.peerlanguage = (String) TextLocal.countries.get(peercountry);
						}
						}
						} else {
							throw  new JSONException("x");
							
						}
						
					} catch (JSONException e) {
						break m;
						
					//	this.title2 = "Ошибка JSON";
					//	VikaTouch.error(-2, "x4 " + e.toString(), false);
					} catch (Throwable eee) {
						break m;
					}

					msgthread = (new Thread() {
						public void run() {
							try {
								messagesDialog();
							} catch (Throwable e) {
								//return;
							}/* catch (IOException e) {
								e.printStackTrace();
								ChatScreen.this.title2 = TextLocal.inst.get("msg.failedtoload");
								VikaTouch.error(-2, "x5 " + e.toString() + " " + e.getMessage(), false);
							}*/
						}
					});
							msgthread.start();
				} catch (Throwable e) {
					//VikaTouch.error(-2, "x6 " + e.toString(), false);
					this.title2 = TextLocal.inst.get("msg.failedtoload");
				}
			}
		}
		/*while (uiItems.isEmpty() || uiItems==null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//
		}*/
		VikaTouch.isscrolling=false;
		VikaTouch.needstoRedraw=true;
	//	VikaUtils.logToFile("f");
	//	scrollToSelected();
	}

	int inr = 0, outr = 0;

	public static int members;

	private boolean hasPinnedMessage;

	private String pinName;

	private String pinText;

	private int pinId;

	public static Thread voiceRecorderThread;

	private void messagesChat() throws InterruptedException {
		VikaTouch.needstoRedraw=true;
		try {
			String errst = "f";
			// VikaTouch.sendLog("Messages in chat mode");

			VikaCanvasInst.msgColor = 0xffff0000;
			// скачка сообщений
			uiItems = new Vector(Settings.messagesPerLoad);
			//uiItems = new PressableUIItem[Settings.messagesPerLoad + loadSpace];
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
						/*uiItems[uiItems.length - 1 - i - loadSpace] = act;
						itemsCount = (short) uiItems.length;*/
						uiItems.insertElementAt(act, 0);
						itemsCount = (short) uiItems.size();
					} else {
						MsgItem m = new MsgItem(j);
						errst = "msgit" + String.valueOf(i);
						m.parseJSON();
						//m.loadAtts();
						errst = "mparse" + String.valueOf(i);
						int fromId = m.fromid;
						long date = m.date;

						String name = (fromId < 0 ? "g" : "") + "id" + fromId;

						if (VikaTouch.profiles.containsKey(new IntObject(fromId))) {
							name = ((ProfileObject) VikaTouch.profiles.get(new IntObject(fromId))).getName();
						}

						boolean chain = false;
						if (i + 1 < items.length()) {
							chain = ((fromId == items.getJSONObject(i + 1).optInt("from_id")) && (items.getJSONObject(i + 1).optLong("date")-date>86400000));
						}
						m.showName = !chain;

						m.setName(m.foreign ? name : TextLocal.inst.get("msg.you"));
						errst = "mui" + String.valueOf(i);
						uiItems.insertElementAt(m, 0);
						//uiItems[uiItems.length - 1 - i - loadSpace] = m;
						errst = "mui2" + String.valueOf(i);
						if (i == 0) {
							// last = m;
						}
						errst = "mui3" + String.valueOf(i);
						itemsCount = (short) uiItems.size();
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
			
			x = null;
			errst = "msgdisp";
			//loadAtts();
			errst = "loadat";
			//ready = true;
		
			try {
				//itemsCount = (short) uiItems.length;
				loadAtts();
				} catch (Throwable eee) {}
				json.dispose();
				 
				
				this.repaint();
				ready = true;
				this.repaint();
				//scrollToSelected();
				
			
			
			
			//scrollToSelected();
			try {
				if (Settings.autoMarkAsRead) {
					VikaCanvasInst.updColor = 0xff00ffff;
					VikaUtils.download(new URLBuilder("messages.markAsRead").addField("peer_id", peerId));
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
			// finally
			// {

			// }
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	 public void startRecord() {
		 voiceRecorderThread = new Thread(new VoiceRecorder());
			/*Alert alert = new Alert("Внимание", "Идёт запись...", (Image) null, AlertType.INFO);
			errstring = 11;

			alert.setCommandListener(vikaInst);
			alert.setTimeout(-2);
			sendVoice = new Command("Отправить", 4, 1);
			alert.addCommand(sendVoice);
			cancelVoice = new Command("Отмена", 3, 1);
			alert.addCommand(cancelVoice);
			errstring = 12;
			
			Display.getDisplay(vikaInst).setCurrent(alert);
			errstring = 13;*/
			
			voiceRecorderThread.start();
		 
	 } 
	
	public void startRecdord()  {
		//try {
			String var1 = "";
			try {
				var1 = VikaUtils.download(new URLBuilder("docs.getUploadServer").addField("type", "audio_message"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				//VikaUtils.logToFile("ioo");
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				//VikaUtils.logToFile("inter");
				e1.printStackTrace();
			}
			//VikaUtils.logToFile(var1);
			uploadUrl = var1.substring(var1.indexOf("upload_url") + 13, var1.length() - 3);
			try {
			pl = Manager.createPlayer("capture://audio");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			} catch (MediaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
			//VikaUtils.logToFile("url is "+uploadUrl);
			/*if (!VikaTouch.isS40()) {
				try {
					pl = Manager.createPlayer("capture://audio?encoding=pcm");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				//	VikaUtils.logToFile("ioio");
				} catch (MediaException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				//	VikaUtils.logToFile("med");
				}
			} else {
				try {
					pl = Manager.createPlayer("capture://audio?encoding=pcm&rate=8000&bits=16");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MediaException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
			pl.addPlayerListener(this);
			try {
				pl.realize();
			} catch (MediaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			//	VikaUtils.logToFile("me");
			}
			try {
				pl.prefetch();
			} catch (MediaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			//	VikaUtils.logToFile("pref");
			}
			
			rec = (RecordControl) pl.getControl("RecordControl");
			output = new ByteArrayOutputStream();
			rec.setRecordStream(output);
			try {
				pl.start();
			} catch (MediaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			//	VikaUtils.logToFile("jopa");
			}
			rec.startRecord();
		//	VikaUtils.logToFile("recording...");
			VikaTouch.needstoRedraw=true;
			VikaTouch.canvas.serviceRepaints();
		/*} catch (VikaNetworkError e) {

		} catch (MediaException e) {

		} catch (IOException e) {

		}*/
	}
	
	
	
	
	
	
	
	public void sendRecord()  {
		voiceRecorderThread.interrupt();
			try {
				//rec.stopRecord();
				
			rec.commit();
			
			} catch (Throwable eee ) {
				//VikaUtils.logToFile(eee.getMessage());
				//VikaUtils.logToFile("commit");
			}
			/*try {
				pl.stop();
			} catch (Throwable e) {
				VikaUtils.logToFile(e.getMessage());
				VikaUtils.logToFile("pl.stop");
			}*/

			
			/*try {
			pl.close();
			pl.deallocate();
			} catch (Throwable ee) {
				VikaUtils.logToFile(ee.getMessage());
				VikaUtils.logToFile("pl.close()");
			}*/
			
			
			byte[] recorderSoundArray = output.toByteArray();
			
			try {
				output.close();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			uploadUrl = VikaUtils.replace(uploadUrl, "\\/", "/");
			//VikaUtils.logToFile(uploadUrl + " " + recorderSoundArray.length);
			String var4 = null;
			/*try {
				var4 = VikaUtils.upload(uploadUrl, "upload_field", "bb2.mp3", recorderSoundArray);
VikaUtils.logToFile(var4);
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}*/
			
			
			Hashtable var3 = new Hashtable();
			var4 = "";

			try {
				
				byte[] var5 = (new HttpMultipartRequest("http://vikamobile.ru:80/upload.php?" + uploadUrl, var3, "upload_field",
						"audio"+String.valueOf(new Random().nextInt(10000))+".mp3", "multipart/form-data", recorderSoundArray)).send();
				var4 = new String(var5);
				//VikaUtils.logToFile(var4);
			} catch (Exception var152) {
				var152.printStackTrace();
			}

			String var178 = null;
			try {
				var178 = VikaUtils.downloadE(new URLBuilder("docs.save").addField("file", var4));
			//	VikaUtils.logToFile(var178);
			} catch (VikaNetworkError e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			/*String var6 = var178.substring(var178.indexOf("owner_id") + 10,
					var178.substring(var178.indexOf("owner_id") + 10, var178.length()).indexOf("\"") - 1);
			String var7 = var178.substring(var178.indexOf("id") + 4,
					var178.substring(var178.indexOf("id") + 4, var178.length()).indexOf("\"") - 1);*/
			JSONObject aa = new JSONObject(var178);
		//	VikaUtils.logToFile("json="+aa.toString()+"\n");
			JSONObject resp = aa.getJSONObject("response");
			//VikaUtils.logToFile("resp="+resp.toString()+"\n");
			JSONObject aud = resp.getJSONObject("audio_message");
		//	VikaUtils.logToFile("aud="+aud.toString()+"\n");
			String var6 = aud.getString("owner_id");
			String var7 = aud.getString("id");
		//	VikaUtils.logToFile("user"+var6+"user");
		//	VikaUtils.logToFile("id"+var7+"id");
			try {
				String a;
				//if ((inputedLinesCount == 0 || (NokiaUIInvoker.supportsTextEditor() && (NokiaUIInvoker.getTextEditorContent()==null || NokiaUIInvoker.getTextEditorContent()==""))) && (VikaTouch.resendingobjectid=="" || VikaTouch.resendingobjectid==null)) {
						a = VikaUtils.downloadE(new URLBuilder("messages.send").addField("peer_id", peerId).addField("attachment",
						"doc" + var6 + "_" + var7).addField("random_id", new Random().nextInt(1000)));
				/*} else {
					try {
        	        if(NokiaUIInvoker.supportsTextEditor()) {
        	            if(NokiaUIInvoker.textEditorShown()) {
        	                inputText = NokiaUIInvoker.getTextEditorContent();
        	                inputChanged = true;
        	                //VikaTouch.sendLog(inputText);
        	               // String code = TestUtils.getEmojiString("1f609");
        	               
        	            }
        	        }
                } catch (Throwable e) {
                	
                }
					a = VikaUtils.downloadE(new URLBuilder("messages.send").addField("peer_id", peerId).addField("attachment",
							"doc" + var6 + "_" + var7).addField("random_id", new Random().nextInt(1000)).addField("message", inputText));
				}*/
			//	VikaUtils.logToFile(a);
			} catch (VikaNetworkError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		/*	uploadUrl = VikaUtils.replace(uploadUrl, "\\/", "/");
			VikaUtils.logToFile("uploading ");
			String var4 = "uploading";
			try {
				var4 = VikaUtils.upload(uploadUrl, "upload_field", "bb2.mp3", recorderSoundArray);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			VikaUtils.logToFile(var4);
			VikaUtils.logToFile("saving doc...");
			String var178 = "saving";
			try {
				var178 = VikaUtils.download(new URLBuilder("docs.save").addField("file", var4));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			VikaUtils.logToFile(var178);
			
			String var6 = var178.substring(var178.indexOf("owner_id") + 10,
					var178.substring(var178.indexOf("owner_id") + 10, var178.length()).indexOf("\"") - 1);
			String var7 = var178.substring(var178.indexOf("id") + 4,
					var178.substring(var178.indexOf("id") + 4, var178.length()).indexOf("\"") - 1);

			String vv = "send";
			try {
				vv = VikaUtils.download(new URLBuilder("messages.send").addField("peer_id", peerId).addField("attachment",
						"doc" + var6 + "_" + var7));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			VikaUtils.logToFile(vv);*/
		/*} catch (IOException e) {
			VikaUtils.logToFile("IO");
		} catch (RuntimeException e) {
			VikaUtils.logToFile("RuntimeException");
		} catch (Exception e) {
			if(e instanceof InterruptedException)
				throw (InterruptedException) e;
		} catch (Throwable eee) {
			VikaUtils.logToFile("Throwable");
		}*/
	}
	
	
	

	public void senddRecord() throws InterruptedException {
		try {
			rec.commit();

			try {
				pl.stop();
			} catch (MediaException e) {
			}

			byte[] recorderSoundArray = output.toByteArray();
			pl.close();
			pl.deallocate();

			uploadUrl = VikaUtils.replace(uploadUrl, "\\/", "/");
			String var4 = VikaUtils.upload("http://vikamobile.ru:80/upload.php?" + uploadUrl, "upload_field", "bb2.mp3", recorderSoundArray);

			String var178 = VikaUtils.downloadE(new URLBuilder("docs.save").addField("file", var4));
			String var6 = var178.substring(var178.indexOf("owner_id") + 10,
					var178.substring(var178.indexOf("owner_id") + 10, var178.length()).indexOf("\"") - 1);
			String var7 = var178.substring(var178.indexOf("id") + 4,
					var178.substring(var178.indexOf("id") + 4, var178.length()).indexOf("\"") - 1);

			VikaUtils.downloadE(new URLBuilder("messages.send").addField("peer_id", peerId).addField("attachment",
					"doc" + var6 + "_" + var7));
		} catch (IOException e) {

		} catch (VikaNetworkError e) {

		} catch (RuntimeException e) {

		} catch (Exception e) {
			if(e instanceof InterruptedException)
				throw (InterruptedException) e;
		}
	}

	public void cancelRecord() {
		voiceRecorderThread.interrupt();
		try {
			rec.commit();
		} catch (Exception e) {
		}
		try {
			pl.stop();
			pl.close();
			pl.deallocate();
		} catch (Exception e) {
		}
	}
	
	

	private void messagesDialog() throws IOException, InterruptedException {
		VikaTouch.needstoRedraw=true;
		JSONArray json = null;
		int jl = 0;
		try {
		VikaCanvasInst.msgColor = 0xffff0000;
		// скачка сообщений
		if (unread_count> Settings.messagesPerLoad) {
			uiItems = new Vector(unread_count);
		} else {
			uiItems = new Vector(Settings.messagesPerLoad);
		}
		// VikaTouch.sendLog("Requesting history");
		String x = "";
		if (unread_count> Settings.messagesPerLoad) {
		 x = VikaUtils.download(new URLBuilder("messages.getHistory").addField("peer_id", peerId)
				.addField("count", unread_count).addField("offset", 0).addField("extended", 1));
		} else {
			x = VikaUtils.download(new URLBuilder("messages.getHistory").addField("peer_id", peerId)
					.addField("count", Settings.messagesPerLoad).addField("offset", 0).addField("extended", 1));
		}
		// VikaTouch.sendLog("Requesting history ok");
		VikaCanvasInst.msgColor = 0xffffff00;
		JSONObject res = new JSONObject(x).optJSONObject("response");
		 json = res.optJSONArray("items");
		 
		 
		 JSONArray profiles = res.optJSONArray("profiles");
			
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
			} catch (Throwable e) {
				
			}
		 
		if(!VikaTouch.profiles.containsKey(new IntObject(peerId)))
			VikaTouch.profiles.put(new IntObject(peerId), new ProfileObject(localId, title, null));
		inr = res.optJSONArray("conversations").optJSONObject(0).optInt("in_read");
		outr = res.optJSONArray("conversations").optJSONObject(0).optInt("out_read");
		 jl = json.length();
		} catch (Throwable ee) {}
		for (int i = 0; i < jl; i++) {
			try {
				VikaCanvasInst.msgColor = 0xff00ff00;
				MsgItem m = new MsgItem(json.getJSONObject(i));
				m.parseJSON();
				//m.loadAtts();
				//m.itemDrawHeight=
				int fromId = m.fromid;

				boolean chain = false;
				if (i + 1 < json.length()) {
					chain = fromId == json.optJSONObject(i + 1).optInt("from_id");
				}
				m.showName = !chain;

				m.setName(m.foreign ? title : TextLocal.inst.get("msg.you"));
				uiItems.insertElementAt(m, 0);
				//uiItems[uiItems.length - 1 - i - loadSpace] = m;
				if (Settings.autoMarkAsRead && i == 0) {
					VikaCanvasInst.msgColor = 0xffff00ff;
					VikaUtils.request(new URLBuilder("messages.markAsRead")
							.addField("start_message_id", "" + m.getMessageId()).addField("peer_id", peerId));
					VikaCanvasInst.msgColor = 0xff00ff00;
				}
			} catch (Throwable e) {
				e.printStackTrace();
				this.title2 = TextLocal.inst.get("msg.failedtoload2");
				//VikaTouch.error(-2, "chat 6 " + e.toString(), false);
				// VikaTouch.sendLog(e.getMessage());
			}
		}
		try {
		//itemsCount = (short) uiItems.length;
		loadAtts();
		} catch (Throwable eee) {}
		json.dispose();
		 
		
		this.repaint();
		ready = true;
		this.repaint();
		//scrollToSelected();
		/*if (Settings.unsentmsgs!=null) {
		int k=-1;
		for (int h=0; h< Settings.unsentmsgs.length(); h++) {
			if (Settings.unsentmsgs.getJSONObject(h).optInt("peer_id")==peerId) {
				k=h;
			}
		}
		if (k!=1) {
		UnsentMsgItem um = new UnsentMsgItem(Settings.unsentmsgs.getJSONObject(k));
		uiItems[jl+1]=um;
		}
		}*/
		
	}

	/*public void loadAtts() {
		(new Thread() {
			public void run() {
				VikaTouch.needstoRedraw=true;
				VikaCanvasInst.msgColor = 0xff0000ff;
				VikaTouch.loading = true;
				int i = 0;
				int maxnum = 0;
				try {
					for (i = 0; i < uiItems.length; i++) {
						if (uiItems[i] == null)
							continue;
						if (uiItems[i] instanceof MsgItem) {
							maxnum = i;
							((MsgItem) uiItems[i]).loadAtts();
							
						}
					}
				} catch (Throwable e) {
					//VikaTouch.popup(new InfoPopup("Attachments error, msg " + i + " exc " + e.toString(), null));
				}
				VikaCanvasInst.msgColor = 0xff00ffff;
				try {
					forceRedraw = true;
					//repaint();
					//Thread.sleep(50);
					//repaint();
					forceRedraw = false;
					//T//hread.sleep(50);
					IMessage mi = (IMessage) uiItems[maxnum];
					//if (mi.getMessageId() == l)
					currentItem = mi.getMessageId() ;
							//markMsgs(inr, outr);
					//scrollToSelected();
					uiItems[currentItem].setSelected(true);
					
						
					//
					
					
					
				} catch (Throwable e) {
					e.printStackTrace();
				}

				System.gc();
				//scrollToSelected();
			
				try {
					while (updStop)
						Thread.sleep(200);
					runUpdater();
					VikaTouch.loading = false;
					VikaCanvasInst.msgColor = 0xff000000;
				} catch (InterruptedException e) {
				} catch (Throwable ee) {}
				
			//}}).start();
			this.repaint();
			this.serviceRepaints();
		
	}*/
	
	
	public void loadAtts() {
		VikaCanvasInst.msgColor = 0xff0000ff;
		VikaTouch.loading = true;
		int i = 0;
		try {
			for (i = 0; i < uiItems.size(); i++) {
				if (uiItems.elementAt(i) == null)
					continue;
				if (uiItems.elementAt(i) instanceof MsgItem) {
					((MsgItem) uiItems.elementAt(i)).loadAtts();
					
				}
				VikaTouch.needstoRedraw=true;
				this.serviceRepaints();
				VikaTouch.needstoRedraw=true;
			}
		} catch (Throwable e) {
			VikaTouch.popup(new InfoPopup("Attachments error, msg " + i + " exc " + e.toString(), null));
		}
		VikaCanvasInst.msgColor = 0xff00ffff;
		try {
			forceRedraw = true;
			//repaint();
			//Thread.sleep(50);
			//repaint();
			forceRedraw = false;
			//Thread.sleep(50);
			currentItem = markMsgs(inr, outr);
			((PressableUIItem) uiItems.elementAt(currentItem)).setSelected(true);
			scrollToSelected();
			
		} catch (Throwable e) {
			e.printStackTrace();
		}

		System.gc();

		try {
			//while (updStop)
			//	Thread.sleep(200);
			runUpdater();
			VikaTouch.loading = false;
			VikaCanvasInst.msgColor = 0xff000000;
		} catch (Exception e) {
		}
	}
	
	
	public int markMsgs(int inRead, int outRead) {
		try {
			int l = Math.min(inRead, outRead);
			boolean r = false;
			int rn = 0;
			for (int i = uiItems.size() - 1; i >= 0; i--) {
				if (uiItems.elementAt(i) != null && uiItems.elementAt(i) instanceof IMessage) {
					IMessage mi = (IMessage) uiItems.elementAt(i);
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
			return 0;
		}
	}

	/*public int markMsgs(int inRead, int outRead) {
		VikaTouch.needstoRedraw=true;
		int rn = 0;
		return 0;
		try {
		//	return 0;
			int l = Math.min(inRead, outRead);
			//VikaTouch.sendLog("in:"+inRead+" out:"+outRead);
			boolean r = false;
			
			if (uiItems!=null) {
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
			} else {
				return 0;
			}
			return rn;
		} catch (Throwable e) {
			e.printStackTrace();
			//VikaTouch.sendLog("Marking: " + e.toString());
			return rn;
		}
	}*/

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
		VikaTouch.needstoRedraw=true;
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
						//  vikatouch.TextEditorInvoker.textEditor.setVisible(true);
						
						showTextBox();
						
					} else if (x < 50) {
						// прикреп
						addAtt();
					} else if (x > DisplayUtils.width - 40) {
						// отправить
						//if ((inputText !=null) && (inputText!= "") ){
						if (NokiaUIInvoker.supportsTextEditor()) {
						if ((inputedLinesCount == 0 || (inputText== "") || (NokiaUIInvoker.supportsTextEditor() && (NokiaUIInvoker.getTextEditorContent()==null || NokiaUIInvoker.getTextEditorContent()==""))) && (VikaTouch.resendingobjectid=="" || VikaTouch.resendingobjectid==null)) {
							if (isRecRunning==true) {
								canWrite=true;
								isRecRunning=false;
								//title = "Отправка записи";
								sendRecord();
								  //  }}).start();
								//title = "Запись отправлена";
							} else  {
								isRecRunning = true;
								canWrite=false;
							//title = "Идёт запись";
							startRecord();
							}
						} else {
							send();
							
						}
						} else {
							if ((inputedLinesCount == 0 || (inputText== "")) && (VikaTouch.resendingobjectid=="" || VikaTouch.resendingobjectid==null)) {
								if (isRecRunning==true) {
									canWrite=true;
									isRecRunning=false;
									//title = "Отправка записи";
									sendRecord();
									  //  }}).start();
									//title = "Запись отправлена";
								} else  {
									isRecRunning = true;
									canWrite=false;
								//title = "Идёт запись";
								startRecord();
								}
							} else {
								send();
								
							}
						}
						//} else {
							//voicescreen
							//inputText = TextLocal.inst.get("player.recording");
							//VikaTouch.isRecording=true;
							//VikaTouch.setDisplay(new V(ChatScreen.this), 1);
						//}
					} else if (x > DisplayUtils.width - 90) {
						// емоци и стикеры
						VikaTouch.popup(new InfoPopup(TextLocal.inst.get("popup.unrealized"), null));
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
		VikaTouch.needstoRedraw=false;
	}

	public void press(int key) {
		VikaTouch.needstoRedraw=true;
		//VikaUtils.logToFile(String.valueOf(key)+ " ");
		if (key != -12 && key != -20) {
			keysMode = true;
		}
		/*
		 * if(VikaCanvas.currentAlert!=null) {
		 * VikaCanvas.currentAlert.press(key); f; return; }
		 */
		if (key == -1) {
			if (isRecRunning) {
				return;
			} 
			VikaTouch.needstoRedraw=true;
			up();
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
		} else if (key == -2) {
			if (isRecRunning) {
				return;
			} 
			VikaTouch.needstoRedraw=true;
			//if (ready) {
			down();
			//down();
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			//}
		} else if (key == -3) {
			if (isRecRunning) {
				return;
			} 
			VikaTouch.needstoRedraw=true;
			// if (ready) {
			fullup();
			// }
		} else if (key == -4) {
			if (isRecRunning) {
				return;
			} 
			VikaTouch.needstoRedraw=true;
			fulldown();
		} else if (key == -10) {
			VikaTouch.needstoRedraw=true;
			if (inputedLinesCount != 0) {
				if (isRecRunning==true) {
					isRecRunning=false;
					canWrite=true;
					//title = "Отправка записи";
					sendRecord();
					  //  }}).start();
					//title = "Запись отправлена";
					//return;
					canWrite=true;
				} else  {
					canWrite=false;
					isRecRunning = true;
					VikaTouch.needstoRedraw=true;
					this.serviceRepaints();
					//this.repaint();
					
					VikaTouch.needstoRedraw=true;
				//title = "Идёт запись";
				startRecord();
				this.serviceRepaints();
				//this.repaint();
				//return;
				}
			} else {
				send();
				//return;
			}
			/*if (inputedLinesCount != 0) {
				send();
				} else {*/
					//voicescreen
					//VikaTouch.setDisplay(new CameraScreen(ChatScreen.this), 1);
		//		}
		} else if (key == -5) { // ok
			VikaTouch.needstoRedraw=true;
			switch (buttonSelected) {
			case 0:
				((PressableUIItem) uiItems.elementAt(currentItem)).keyPress(key);
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
				VikaTouch.popup(new InfoPopup(TextLocal.inst.get("popup.unrealized"), null));
				break;
			case 4:
				
				if ((inputedLinesCount == 0)) {
					if (isRecRunning==true) {
						isRecRunning=false;
						canWrite=true;
						//title = "Отправка записи";
						sendRecord();
						  //  }}).start();
						//title = "Запись отправлена";
					} else  {
						isRecRunning = true;
						canWrite=false;
					//title = "Идёт запись";
					startRecord();
					}
				} else {
					send();
				}
				
				//send();
				//buttonSelected = 2;
				break;
			}
		} else if (key == -6) { // lsk
			VikaTouch.needstoRedraw=true;
			if (isRecRunning) {
				return;
			} else {
			if (buttonSelected == 0) {
				buttonSelected = 2;
			} else {
				buttonSelected = 0;
			}
			}
		} else if (key == -7) { // rsk
			if (isRecRunning) {
				isRecRunning = false;
				canWrite=true;
				cancelRecord();
				VikaTouch.needstoRedraw=true;
				this.serviceRepaints();
			} else {
			VikaTouch.needstoRedraw=true;
			stopUpdater();
			VikaTouch.inst.cmdsInst.command(14, this);
			}
		} else if (key == 10) {
			send();
		}
		VikaTouch.needstoRedraw=true;
		//repaint();
	}

	private void addAtt() {
		VikaTouch.needstoRedraw=true;
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
										VikaTouch.popup(new InfoPopup(TextLocal.inst.get("popup.unrealized"), null));
									}
								} else if (i == 1) {
									VikaTouch.popup(new InfoPopup(TextLocal.inst.get("popup.unrealized"), null));
								} else if(i == 2) {
									if(NokiaUIInvoker.textEditorShown())
							            NokiaUIInvoker.hideTextEditor();
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
		VikaTouch.needstoRedraw=true;
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
		VikaTouch.needstoRedraw=true;
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
	
	protected void fulldown() {
		VikaTouch.needstoRedraw=true;
		if (buttonSelected == 0) {
			keysScrollmore(-10);
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

	protected void fullup() {
		VikaTouch.needstoRedraw=true;
		if (buttonSelected == 0) {
			keysScrollmore(+10);
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
		for (int i = 0; (i < uiItems.size() && i < n); i++) {
			try {
			if (uiItems.elementAt(i) != null) {
				VikaTouch.isscrolling=true;
				if (((PressableUIItem) uiItems.elementAt(i)).getDrawHeight()<=1) {
					try {
					((PressableUIItem) uiItems.elementAt(i)).paint(VikaTouch.canvas.getG(), 0, scrolled);
					} catch (Throwable eee) {
					//	VikaUtils.logToFile("pizdec");
					}
				}
				y += ((PressableUIItem) uiItems.elementAt(i)).getDrawHeight();
				y += msgYMargin;
				//VikaUtils.logToFile(" itemY " + String.valueOf(i)+ " = " + String.valueOf(((MsgItem) uiItems.elementAt(i)).getDrawHeight() + msgYMargin));
			}
			} catch (Throwable eeee) {
			//	VikaUtils.logToFile(" getItemY error: " + String.valueOf(i));
				return 0;
			}
		}
		
		//VikaUtils.logToFile(" getItemY: " + String.valueOf(y + topPanelH));
		return y + topPanelH;
	}
	
	public void scrollToSelected() {
		try {
			//VikaUtils.logToFile("scrolltoselected");
			VikaTouch.isscrolling=true;
			scrolled = -(this.getItemY(
					currentItem
					//1
					) 
					/*- DisplayUtils.height / 2 + (((MsgItem) uiItems.elementAt(
							currentItem
							//1
							)).getDrawHeight() / 2)*/
					+ MainScreen.topPanelH);
		} catch (Throwable e) {
			//VikaUtils.logToFile(e.getMessage());
			e.printStackTrace();
		}
	}

	public void selectCentered() {
		int y = MainScreen.topPanelH;
		int ye = y;
		int s = -scrolled + DisplayUtils.height / 2;
		for (int i = 0; i < uiItems.size(); i++) {
			if (uiItems.elementAt(i) == null)
				return;
			ye = y + ((PressableUIItem) uiItems.elementAt(i)).getDrawHeight();
			if (y <= s && ye > s) {
				try {
					((PressableUIItem) uiItems.elementAt(currentItem)).setSelected(false);
				} catch (Throwable e) {
				}
				try {
					((PressableUIItem) uiItems.elementAt(i)).setSelected(true);
					currentItem = i;
				} catch (Throwable e) {
				}
				return;
			}
			y = ye + msgYMargin;
		}
	}

	public void repeat(int key) {
		VikaTouch.needstoRedraw=true;
		if (key != -12 && key != -20) {
			keysMode = true;
		}
		if (key == -1) {
			up();
		}
		if (key == -2) {
			down();
		}
		serviceRepaints();
		//repaint();
	}

	public static boolean canSend = true;
	private static Thread reporter;
	private static Thread typer;

/*	private void send() {
        if (!canSend)
            return;
        canSend = false;
        if(NokiaUIInvoker.supportsTextEditor()) {
            if(NokiaUIInvoker.textEditorShown()) {
                inputText = NokiaUIInvoker.hideTextEditor();
                inputChanged = true;
            }
        }
        new Thread() {
            public void run() {
                setPriority(10);
                try {
                    VikaTouch.loading = true;
                    URLBuilder url = new URLBuilder("messages.send").addField("random_id", new Random().nextInt(1000))
                            .addField("peer_id", peerId).addField("message", inputText).addField("intent", "default");
                    if (answerMsgId != 0) {
                        url.addField("reply_to", "" + answerMsgId);
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
    }*/
	
	
	
	private void send() {
		VikaTouch.needstoRedraw=true;
        if (!canSend)
            return;
        canSend = false;
        VikaTouch.needstoRedraw=true;
        this.serviceRepaints();
        VikaTouch.needstoRedraw=true;
        buttonSelected = 0;
       // inputedLinesCount = 0;
        
        new Thread() {
            public void run() {
                setPriority(10);
                VikaTouch.needstoRedraw=true;
                canSend = false;
                VikaTouch.canvas.serviceRepaints();
                VikaTouch.needstoRedraw=true;
                try {
					Thread.sleep(200);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
                try {
        	        if(NokiaUIInvoker.supportsTextEditor()) {
        	            if(NokiaUIInvoker.textEditorShown()) {
        	                inputText = NokiaUIInvoker.getTextEditorContent();
        	                inputChanged = true;
        	                //VikaTouch.sendLog(inputText);
        	               // String code = TestUtils.getEmojiString("1f609");
        	               
        	            }
        	        }
                } catch (Throwable e) {
                	//VikaTouch.sendLog(e.getMessage());
                }
                int e = 0;
                String res = null;
                URLBuilder url;
                //try {
                	e=1;
                	VikaTouch.loading = true;
                	e=2;
                     url = new URLBuilder("messages.send").addField("random_id", new Random().nextInt(1000))
                            .addField("peer_id", peerId).addField("message", inputText).addField("intent", "default");
                    
               /*     inputText="";
                    inputedLinesCount = 0;
                    inputedTextToDraw=null;
                    inputChanged = false;*/
                    if (answerMsgId != 0) {
                    	e=7;
                    //	if (VikaTouch.resendingobjectid!="") {
                    		e=8;
                       if (VikaTouch.isresending==true) {
                    	   e=9;
                    	   url.addField("forward_messages", "" + String.valueOf(answerMsgId));
                    	   e=10;
                       } else {
                    	   e=11;
                    	url.addField("reply_to", "" + String.valueOf(answerMsgId));
                    	e=12;
                       }
                       answerMsgId = 0;
                       e=13;
                    	//} 
                    	e=14;
                    }
                    
                    if ((objectId!=null) && (objectId!="")) {
                    	e=15;
                		url.addField("attachment", objectId);
                		e=16;
                		answerMsgId = 0;
                		e=17;
                		objectId="";
                		e=18;
                	}
                    
                   // VikaTouch.sendLog(url.toString());
                    try {
						Thread.sleep(200);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    e=19;
                   // VikaUtils.logToFile(url.toString());
                   try {
					res = VikaUtils.download(url);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                    e=20;
                    int a=0;
                    while ((res==null) && (a<10)) {
                    	try {
							res = VikaUtils.download(url);
                    	} catch (Throwable e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                    	try {
							sleep(6000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                    	a++;
                    }
                    if (res==null) {
                    	JSONObject unsentmsg = new JSONObject();
                    	unsentmsg.put("url", url);
                    	unsentmsg.put("peer_id", peerId);
                    	unsentmsg.put("message", inputText);
                    	unsentmsg.put("datetime", System.currentTimeMillis());
                    	if (answerMsgId!=0) {
                    		unsentmsg.put("message", answerMsgId);
                    	}
                    	Settings.unsentmsgs.put(unsentmsg);
                    	 VikaTouch.needstoRedraw=true;
                         
                    	return;
                    }
                    if(NokiaUIInvoker.supportsTextEditor()) {
                    	e=3;
        	            if(NokiaUIInvoker.textEditorShown()) {
        	            	e=4;
                    NokiaUIInvoker.setTextEditorContent("");
                    inputText="";
                    inputedLinesCount = 0;
                    e=5;
	                inputChanged = true;
	                e=6;
	                
        	            }
                    }
                   // VikaTouch.sendLog(res);
                  /*  if (res == null) {
                    	while (res==null) {
                    		 canSend = false;
                    		res = VikaUtils.download(url);
                    	}
                    	if (res.indexOf("response")>=0) {
                            inputText = "";
                            inputChanged = false;
                            inputedTextToDraw=null;
                            inputedLinesCount = 0;
                            e=22;
                        	} else {
                        		int a = res.indexOf("response");
                        		while (a<0) {
                        			 canSend = false;
                        			res = VikaUtils.download(url);
                        			if (res!=null) {
                        				a = res.indexOf("response");
                        				break;
                        			} else {
                        				res = VikaUtils.download(url);
                        			}
                        		}
                        	}
                    	//VikaTouch.popup(new InfoPopup(TextLocal.inst.get("msg.sendneterror"), null));
                    	e=21;*/
                    //} else {
                    //	if (res.indexOf("response")>=0) {
                        inputText = "";
                        inputChanged = true;
                        inputedTextToDraw=null;
                        inputedLinesCount = 0;
                       answerMsgId = 0;
                    	objectId = "";
                    	answerName = "";
                    	answerText = "";
                       /* e=22;
                    	} else {
                    		int a = res.indexOf("response");
                    		while (a<0) {
                    			 canSend = false;
                    			res = VikaUtils.download(url);
                    			if (res!=null) {
                    				a = res.indexOf("response");
                    			} else {
                    				res = VikaUtils.download(url);
                    			}
                    		}
                    	}*/
                   // }
              //  } catch (Throwable ee) {
               /* 	 canSend = false;
                	 VikaTouch.isdownloading=2;
                	 VikaTouch.needstoRedraw=true;
                	 VikaTouch.canvas.serviceRepaints();*/
                	/* while (true) {
                		 try {
                		 res = VikaUtils.download(url);
                		 } catch (Throwable eee) {}
                		 try {
							Thread.sleep(2000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                	 }*/
                	//VikaTouch.popup(new InfoPopup(TextLocal.inst.get("msg.senderror") + " - " + ee.toString() + " "+String.valueOf(e), null));
              //  } finally {
                    	VikaTouch.needstoRedraw=true;
                    	VikaTouch.canvas.serviceRepaints();
                        try {
                			Thread.sleep(500);
                		} catch (Throwable eee) {
                			// TODO Auto-geneerated catch block
                			//e.printStackTrace();
                		}
                    	updStop=false;
                         update();
                    	canSend = true;
                    VikaTouch.loading = false;
                    VikaTouch.needstoRedraw=true;
                    
                	VikaTouch.canvas.serviceRepaints();
                	VikaTouch.needstoRedraw=true;
                //}
            }
        }.start();
        buttonSelected = 0;
        VikaTouch.needstoRedraw=true;
        this.serviceRepaints();
        try {
			Thread.sleep(500);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       // updStop=false;
       // update();
        VikaTouch.needstoRedraw=true;
        this.serviceRepaints();
    }

	// Удаляет старые сообщения из списка и пододвигает остальные назад, чтоб все
	// влезли.
	/*private void shiftList() {
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
	}*/

	JSONArray temp1;
	JSONObject temp2;

	private String playertime =  "0:00";

	

	

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
				//long mid = ((IMessage) uiItems[uiItems.length - hasSpace - 1]).getMessageId();
				long mid = ((IMessage) uiItems.lastElement()).getMessageId();
				VikaTouch.isdownloading=1;
           	 VikaTouch.needstoRedraw=true;
				final String x = VikaUtils.download(new URLBuilder("messages.getHistory")
						.addField("start_message_id", String.valueOf(mid)).addField("peer_id", peerId)
						.addField("count", 1).addField("offset", -1).addField("extended", 1));
				//VikaTouch.isdownloading=0;
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
						try {
						parser.interrupt();
						} catch (Throwable ee) {}
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
					/*if (newMsgCount >= hasSpace - 1) {
						// System.out.println("List shifting");
						shiftList();
					}*/
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
															profile.optString("photo_50"), profile.optString("online")));
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
						uiItems.addElement(m);
						VikaCanvasInst.updColor = 0xffff00ff;
					}
					markMsgs(inRead, outRead);
				}

				System.gc();

			}
			refreshOk = true;
			VikaTouch.isdownloading=0;
			 VikaTouch.needstoRedraw=true;
		} catch (JSONException e) {
			VikaTouch.isdownloading=2;
			 VikaTouch.needstoRedraw=true;
			e.printStackTrace();
			// throw e;
		} catch (Throwable e) {
			VikaTouch.isdownloading=2;
			 VikaTouch.needstoRedraw=true;
			e.printStackTrace();
		}
	}

	private void runUpdater() {
		VikaTouch.needstoRedraw=true;
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

	/*private void showTextBox() {
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
						/try {
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
	}*/
	

  /*  private void showTextBox() {
    	
        if (!canSend)
            return;
        if(NokiaUIInvoker.supportsTextEditor()) {
            if(NokiaUIInvoker.textEditorShown()) {
              // inputText = NokiaUIInvoker.hideTextEditor();
                inputChanged = true;
                //return;
            }
        }
            new Thread() {
                public void run() {
                    try {
                    	VikaUtils.download(new URLBuilder("messages.setActivity").addField("user_id", VikaTouch.userId).addField("peer_id", peerId).addField("type", "typing"));
                    } catch (Exception e) {
                    }
                }
            }.start();
            
            if (li==null) {
            	
                li = new NokiaUITextEditorListener() {
                    public void action(NokiaUITextEditor editor, int act) {
                    	//if (inputText!=editor.getContent()) {
                        inputText = editor.getContent();
                        inputChanged = true;
                    	//} else {
                    	//	inputChanged = false;
                    	//}
                    }
                };
                }
                NokiaUIInvoker.showTextEditor(inputText, 256, TextField.ANY, 40,
        				DisplayUtils.height - 48
                				
                				, DisplayUtils.width-130, 48, 0xffffffff, 
                				//0x80000000,
                				0x000000aa, 
                				li);
                return;
         NokiaUIInvoker.showTextEditor(inputText, 256, TextField.ANY, 0, 
            		//VikaTouch.isNotS60() ? 800 : 270
            		DisplayUtils.height-20
            				
            				, DisplayUtils.width, 24, 0x80000000, -1, new NokiaUITextEditorListener() {
                public void action(NokiaUITextEditor editor, int act) {
                    inputText = editor.getContent();
                    inputChanged = true;
                }
            });
         //   return;
      //  }
       
    //    if(NokiaUIInvoker.supportsTextEditor()) {
     //   if(!(NokiaUIInvoker.textEditorShown())) {
        	
       	     //  vikatouch.TextEditorInvoker.textEditor.setVisible(true);
       	    //  vikatouch.TextEditorInvoker.textEditor.setFocus(true);
       	     //   
     	//  return;
     //}
       	      
        new Thread() {
            public void run() {
                try {
                	VikaUtils.download(new URLBuilder("messages.setActivity").addField("user_id", VikaTouch.userId).addField("peer_id", peerId).addField("type", "typing"));
                } catch (Exception e) {
                }
            }
        }.start();
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
                        VikaUtils.download(new URLBuilder("messages.setActivity").addField("user_id", VikaTouch.userId).addField("peer_id", peerId).addField("type", "typing"));
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        };
        typer.start();
        reporter.start();
    }*/
	
	
	private static void showTextBox() {
		VikaTouch.needstoRedraw=true;
        if (!canSend)
            return;
        if(NokiaUIInvoker.supportsTextEditor()) {
            if(NokiaUIInvoker.textEditorShown()) {
                inputText = //NokiaUIInvoker.hideTextEditor();
                		NokiaUIInvoker.getTextEditorContent();
                inputChanged = true;
               // return;
            }
            new Thread() {
                public void run() {
                    try {
                    	VikaUtils.download(new URLBuilder("messages.setActivity").addField("user_id", VikaTouch.userId).addField("peer_id", peerId).addField("type", "typing"));
                    } catch (Exception e) {
                    }
                }
            }.start();
          //  Command exitcmd = new Command("Exit", Command.EXIT, 7);
          // VikaTouch.getCurrentDisplay().addCommand(exitcmd);
            NokiaUIInvoker.showTextEditor(inputText, 4090, TextField.ANY, 48,
    				DisplayUtils.height - 35 
            			//270	
            				, DisplayUtils.width-144, 25, ColorUtils.isNight() ? 0x000000ff : 0xffffffff, 
            				ColorUtils.isNight() ? 0xffffff00 : 0x80000000,  null);
            						/*new NokiaUITextEditorListener() {
                public void action(NokiaUITextEditor editor, int act) {
                    inputText = editor.getContent();
                   // VikaTouch.sendLog(inputText);
                    inputChanged = true;
                }
            });*/
           // NokiaUIInvoker.getTextEditorInst().setVisible(true);
	       // NokiaUIInvoker.getTextEditorInst().setFocus(true);
            return;
        }
        if (typer != null && typer.isAlive()) {
         try {
        	typer.interrupt();
         } catch (Throwable ee) {}
        }
        typer = new Thread() {
            public void run() {
                inputText = TextEditor.inputString(TextLocal.inst.get("msg"), inputText == null ? "" : inputText, 0);
                inputChanged = true;
            }
        };
        if (reporter != null && reporter.isAlive()) {
         try {
        	reporter.interrupt();
         } catch (Throwable ee) {}
        }
        reporter = new Thread() {
            public void run() {
                while (typer.isAlive()) {
                    try {
                    	VikaUtils.download(new URLBuilder("messages.setActivity").addField("user_id", VikaTouch.userId).addField("peer_id", peerId).addField("type", "typing"));
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        };
        typer.start();
        reporter.start();
    }
	
	

	/*private void msgClick(int tapY, long tapTime) {
		VikaTouch.needstoRedraw=true;
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
		//VikaTouch.needstoRedraw=true;
		
		//serviceRepaints();
	}*/
	
	
	private void msgClick(int tapY, long tapTime) {
		tapY -= topPanelH;
		if (uiItems == null)
			return;
		int y = 0;
		int gTapY = tapY - scrolled;
		for (int i = 0; i < uiItems.size(); i++) {
			if (uiItems.elementAt(i) == null)
				continue;
			y += msgYMargin;
			int y2 = y + ((PressableUIItem) uiItems.elementAt(i)).getDrawHeight();
			if (y < gTapY && gTapY < y2) {
				((PressableUIItem) uiItems.elementAt(i)).tap(0, gTapY - y);
			}
			y = y2;
		}
	}

	/*private void drawDialog(Graphics g) {
		VikaTouch.needstoRedraw=true;
		//if (!ready) {
		//	return;
		//}
		if (updStop) {
			
				return;
			
		}
		if (uiItems == null)
			return;
		try {
			int y = 0;
			for (int i = 0; i < uiItems.length; i++) {
				if (uiItems[i] == null)
					continue;
				try {
					y += msgYMargin;
					if (uiItems[i] instanceof MsgItem) {
					while (!((MsgItem)uiItems[i]).attsReady) {
						Thread.yield();
					}
					}
					//if (y + scrolled < DisplayUtils.height || forceRedraw) {
					VikaTouch.needstoRedraw=true;
						uiItems[i].paint(g, y, scrolled);
						VikaTouch.needstoRedraw=true;
						this.serviceRepaints();
						VikaTouch.needstoRedraw=true;
					y += uiItems[i].getDrawHeight();
				} catch (RuntimeException e) {
				}
			}
			this.itemsh = y + 50;
		} catch (Throwable e) {
			// VikaTouch.error(e, -8);
			VikaTouch.sendLog(e.getMessage());
		}
	}*/
	
	
	private void drawDialog(Graphics g) {
		if (uiItems == null)
			return;
		try {
			int y = 0;
			for (int i = 0; i < uiItems.size(); i++) {
				if (uiItems.elementAt(i) == null)
					continue;
				try {
					y += msgYMargin;
					if (y + scrolled < DisplayUtils.height || forceRedraw)
						((PressableUIItem) uiItems.elementAt(i)).paint(g, y, scrolled);
					y += ((PressableUIItem) uiItems.elementAt(i)).getDrawHeight();
				} catch (RuntimeException e) {
				}
			}
			this.itemsh = y + 50;
		} catch (Throwable e) {
		}
	}

	private void drawTextbox(Graphics g) {
		
		if (canWrite==true) {
		// расчёты и обработка текста
		int m = 4; // margin
		int dw = DisplayUtils.width;
		int dh = DisplayUtils.height;
		Font font = Font.getFont(0, 0, Font.SIZE_SMALL);
		g.setFont(font);
		int answerH = (int) (font.getHeight() * 2);
		/*if(NokiaUIInvoker.supportsTextEditor() && NokiaUIInvoker.textEditorShown()) {
            String s = NokiaUIInvoker.getTextEditorContent();
            if(!s.equals(inputText)) {
                inputChanged = true;
                inputText = s;
            }
        }*/
		int xx=0;
		if (DisplayUtils.verycompact) {
		inputBoxH = 25;
		xx=10;
		}
		if (VikaTouch.isRecording) {
			
		} else {
		if (inputChanged) {
			try {
				inputedTextToDraw = TextBreaker.breakText(inputText, false, null, true, DisplayUtils.width - 150);
				inputChanged = false;
				if ((inputedTextToDraw != null) && (inputText != "")) {
					for (inputedLinesCount = 0; inputedTextToDraw[inputedLinesCount] != null; inputedLinesCount++) {
					}
				} else {
					inputedLinesCount = 0;
				}
			} catch (Throwable e) {
				inputedLinesCount = 0;
			}
			inputBoxH = Math.max(48, (font.getHeight() * (inputedLinesCount<=3 ? (inputedLinesCount ) : (3))) + m * 2);
		}
		if(NokiaUIInvoker.supportsTextEditor()) {
			inputBoxH = 69;	
		}
		
		if(NokiaUIInvoker.supportsTextEditor() && NokiaUIInvoker.textEditorShown()) {
            inputBoxH = 69;
            ColorUtils.setcolor(g, -8);
            g.fillRect(0, dh - inputBoxH - 1, dw, 2);
            ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
            g.fillRect(0, dh - inputBoxH, dw, inputBoxH);
        } else {
        
		// рендер бокса
        	if (!canSend) {
        		inputedLinesCount = 0;
        	}
        if ((buttonSelected != 0) || (keysMode==false) || (inputedLinesCount != 0)) {
        	if ((keysMode==true)) {
        		Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
        		int h = f.getHeight();
		ColorUtils.setcolor(g, -8);
		g.fillRect(0, dh - inputBoxH - 1 -h, dw, 1);
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(0, dh - inputBoxH -h, dw, inputBoxH);
        	} else {
        		ColorUtils.setcolor(g, -8);
        		g.fillRect(0, dh - inputBoxH - 1, dw, 1);
        		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
        		g.fillRect(0, dh - inputBoxH, dw, inputBoxH);
        		
        	}
        }
		if (inputedLinesCount == 0) {
			if (buttonSelected == 2) {
				
				ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
				g.setFont(Font.getFont(0, Font.STYLE_BOLD, Font.SIZE_SMALL));
				if (keysMode) {
					Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
	        		int h = f.getHeight();
				g.drawString(enterMsgStrSel, 48-2*xx, dh - 24 - font.getHeight() / 2 - h, 0);
				g.setFont(font);
				} else {
					
					g.drawString(enterMsgStrSel, 48-2*xx, dh - 24 - font.getHeight() / 2, 0);
					g.setFont(font);
					
				}
			} else {
				if ((keysMode==false) || (buttonSelected != 0)) {
					 
				ColorUtils.setcolor(g, ColorUtils.OUTLINE);
					 if (keysMode==true) {
						 Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
			        		int h = f.getHeight();
				g.drawString(enterMsgStr, 48-2*xx, dh - 24 - font.getHeight() / 2 - h, 0);
					 } else {
						 g.drawString(enterMsgStr, 48-2*xx, dh - 24 - font.getHeight() / 2, 0);
					 }
				}
			}
		} else {
			ColorUtils.setcolor(g, buttonSelected == 2 ? ColorUtils.BUTTONCOLOR : ColorUtils.TEXT);
			int currY = dh - inputBoxH + m;
			if (keysMode) {
				Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
        		int h = f.getHeight();
        		currY = currY - h;
			}

			for (int i = 0; i < (inputedLinesCount<=3 ? (inputedLinesCount ) : (3)); i++) {
				if (inputedTextToDraw[i] == null)
					continue;

				g.drawString(inputedTextToDraw[i], 48, currY, 0);
				currY += font.getHeight();
			}

		}
        }
		if ((buttonSelected != 0) || (keysMode==false) || (inputedLinesCount != 0)) {
			if (keysMode) {
				Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
        		int h = f.getHeight();
		g.drawImage((buttonSelected != 1 ? IconsManager.ico : IconsManager.selIco)[IconsManager.ATTACHMENT], 12-xx,
				DisplayUtils.height - 36 - h, 0);
		g.drawImage((buttonSelected != 3 ? IconsManager.ico : IconsManager.selIco)[IconsManager.STICKERS],
				DisplayUtils.width - 86+xx, DisplayUtils.height - 36+xx - h, 0);
			} else {
				g.drawImage((buttonSelected != 1 ? IconsManager.ico : IconsManager.selIco)[IconsManager.ATTACHMENT], 12-xx,
						DisplayUtils.height - 36, 0);
				g.drawImage((buttonSelected != 3 ? IconsManager.ico : IconsManager.selIco)[IconsManager.STICKERS],
						DisplayUtils.width - 86+xx, DisplayUtils.height - 36+xx, 0);
			}
		}
	}
		if ((buttonSelected != 0) || (keysMode==false) || (inputedLinesCount != 0)) {
		if (canSend || (System.currentTimeMillis() % 500) < 250) {
			if (keysMode) {
				Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
        		int h = f.getHeight();
				g.drawImage((buttonSelected != 4 ? IconsManager.ico : IconsManager.selIco)[((inputedLinesCount == 0 || (NokiaUIInvoker.supportsTextEditor() && (NokiaUIInvoker.getTextEditorContent()==null || NokiaUIInvoker.getTextEditorContent()==""))) && (VikaTouch.resendingobjectid=="" || VikaTouch.resendingobjectid==null))
						? IconsManager.VOICE
						: IconsManager.SEND], DisplayUtils.width - 40 + xx, DisplayUtils.height - 36+xx-h, 0);
			} else {
				boolean drawvoice=false;
				if (NokiaUIInvoker.supportsTextEditor()) {
					if (NokiaUIInvoker.getTextEditorContent()==null) {
						 drawvoice=true;
					} else if ((NokiaUIInvoker.getTextEditorContent()=="") || (NokiaUIInvoker.getTextEditorContent().length()<=0)) {
						drawvoice=true;
					}
				} else {
					if (inputedLinesCount == 0) {
						drawvoice=true;
					} else {
						drawvoice=false;
					}
				}
				if (drawvoice==true) {
					g.drawImage(IconsManager.ico[IconsManager.VOICE], DisplayUtils.width - 40+xx, DisplayUtils.height - 36+xx, 0);
				} else {
					g.drawImage(IconsManager.ico[IconsManager.SEND], DisplayUtils.width - 40+xx, DisplayUtils.height - 36+xx, 0);
				}
				//g.drawImage(
					//	((inputedLinesCount == 0 || (NokiaUIInvoker.supportsTextEditor() && (NokiaUIInvoker.getTextEditorContent()==null || NokiaUIInvoker.getTextEditorContent()=="" || NokiaUIInvoker.getTextEditorContent().length()<=0))) && (VikaTouch.resendingobjectid=="" || VikaTouch.resendingobjectid==null)) ? IconsManager.ico[IconsManager.VOICE]
						//		: IconsManager.selIco[IconsManager.SEND],
						//DisplayUtils.width - 40+xx, DisplayUtils.height - 36+xx, 0);
				//if (((inputedLinesCount == 0 || (NokiaUIInvoker.supportsTextEditor() && (NokiaUIInvoker.getTextEditorContent()==null || NokiaUIInvoker.getTextEditorContent()==""))) && (VikaTouch.resendingobjectid=="" || VikaTouch.resendingobjectid==null))==false) {
				//	VikaTouch.sendLog("inputedLinesCount == 0 : " + String.valueOf(inputedLinesCount == 0)+" NokiaUIInvoker.supportsTextEditor() : "+String.valueOf(NokiaUIInvoker.supportsTextEditor())+ " NokiaUIInvoker.getTextEditorContent()==null :" + String.valueOf(NokiaUIInvoker.getTextEditorContent()==null) + " VikaTouch.resendingobjectid==\"\" :" + String.valueOf(VikaTouch.resendingobjectid=="") + "contents="+NokiaUIInvoker.getTextEditorContent().charAt(0));
				//}
				
		}
		}
		}
		if (keysMode)
			drawKeysTips(g);

		if ((answerMsgId != 0) || (objectId!="")) {
			try {
				if (objectId!="") {
				if (objectId.indexOf("wall")>-1) {
					answerName = TextLocal.inst.get("msg.attach.wall"); 
				}
				}
				
				int rh = inputBoxH + answerH;
				if (keysMode) {
					Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
	        		int h = f.getHeight();
	        		rh = rh+h;
				}
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
		//NokiaUIInvoker.getTextEditorInst().setCaretXY(0, 8);
		/*NokiaUIInvoker.getTextEditorInst().setPosition(48, DisplayUtils.height - 125 );
		NokiaUIInvoker.getTextEditorInst().setSize(DisplayUtils.width-144, 20);*/
		if(NokiaUIInvoker.supportsTextEditor() && NokiaUIInvoker.textEditorShown()) {
            NokiaUIInvoker.setTextEditorPosition(48, DisplayUtils.height-35);
            NokiaUIInvoker.setTextEditorSize(DisplayUtils.width-144, 25);
        }
		} else {
			if (isRecRunning) {
				VikaTouch.needstoRedraw=true;
				Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
	    		int h = f.getHeight();
				g.fillRect(0, DisplayUtils.height - inputBoxH - 1 - h, DisplayUtils.width, 1);
				ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
				g.fillRect(0, DisplayUtils.height - inputBoxH -h, DisplayUtils.width, inputBoxH+h);
				ColorUtils.setcolor(g, ColorUtils.TEXT);
				g.setFont(f);
				int stw = f.stringWidth(title);
				g.drawString("Идёт запись "+  
				MusicPlayer.time
					(	pl.getMediaTime() / 1000000L)
						
				, 
						10
						//(DisplayUtils.width-stw)/2
						, DisplayUtils.height - (inputBoxH/2) - (h / 2), 0);
			} else {
			Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
    		int h = f.getHeight();
			g.fillRect(0, DisplayUtils.height - inputBoxH - 1 - h, DisplayUtils.width, 1);
			ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
			g.fillRect(0, DisplayUtils.height - inputBoxH -h, DisplayUtils.width, inputBoxH+h);
			ColorUtils.setcolor(g, ColorUtils.TEXT);
			g.setFont(f);
			int stw = f.stringWidth(title);
			g.drawString(TextLocal.inst.get("msg.restricted"), 
					10
					//(DisplayUtils.width-stw)/2
					, DisplayUtils.height - (inputBoxH/2) - (h / 2), 0);
				}
			}
        
	
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
		g.drawString(TextBreaker.shortText(title, DisplayUtils.width - 50 + yy - 38, font1), 50-yy, (int)Math.floor(0+1*xx), 0);

		Font font2 = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		g.setFont(font2);
		ColorUtils.setcolor(g, ColorUtils.TEXT2);
		if (!canSend) {
			g.drawString(sendingStr, 50-yy, (int)Math.floor(0+14*xx), 0);
		} else if (refreshOk) {
			g.drawString(title2, 50-yy, (int)Math.floor(0+14*xx), 0);
		} else {
			g.setColor(255, 0, 0);
			g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
			g.drawString(this.refreshErrorStr, 50-yy, (int)Math.floor(0+14*xx), 0);
		}
		if (VikaTouch.integerUserId!=3225000) {
		g.drawImage(IconsManager.selIco[IconsManager.BACK], 16-yy, 20-2*yy, 0);
		g.drawImage(IconsManager.selIco[IconsManager.INFO], DisplayUtils.width - 38+yy, 20-2*yy, 0);
		}
		if (hasPinnedMessage) {

			Font font = Font.getFont(0, 0, Font.SIZE_SMALL);
			g.setFont(font);
			int dw = DisplayUtils.width;
			int sty = 56;
			int pinh = (int) (font.getHeight() * 2);
			ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
			g.fillRect (0, sty, dw, pinh);
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
	
	
	public void onLeave() {
	    try { 
		NokiaUIInvoker.hideTextEditor();
	    } catch (Throwable eee) {}
		VikaTouch.needstoRedraw=true;
		try { 
		msgthread.interrupt();
		   } catch (Throwable eee) {}
       // if(NokiaUIInvoker.textEditorShown())
		try { 
        stopUpdater();
		 } catch (Throwable eee) {}
        canSend=true;
        
        VikaTouch.isresending=false;
        VikaTouch.resendingmid=0;
        VikaTouch.resendingname="";
        VikaTouch.resendingtext=""; 
        VikaTouch.resendingobjectid="";
        answerMsgId = 0;
    	objectId = "";
    	answerName = "";
    	answerText = "";
    	buttonSelected=0;
        uiItems=null;
        currentItem=0;
        scrolled=0;
        inst=null;
        vikatouch.screens.DialogsScreen.titleStr="Сообщения";
    }

	public void playerUpdate(Player player, String event, Object eventData) {
		// TODO Auto-generated method stub
		VikaTouch.canvas.serviceRepaints();
		this.serviceRepaints();
		//currentItem=0;
		//this.repaint();
		VikaTouch.needstoRedraw=true;
		this.playertime = MusicPlayer.time(pl.getMediaTime() / 1000000L);
		VikaTouch.needstoRedraw=true;
		
	}

	
}
