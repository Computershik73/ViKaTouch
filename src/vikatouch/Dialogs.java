package vikatouch;

import java.util.TimerTask;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import com.nokia.mid.ui.SoftNotification;
import com.nokia.mid.ui.SoftNotificationListener;
import com.nokia.mid.ui.TextEditor;
import com.nokia.mid.ui.TextEditorListener;

import vikatouch.items.VikaNotification;
import vikatouch.items.chat.ConversationItem;
import vikatouch.screens.ChatScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.IntObject;
import vikatouch.utils.ProfileObject;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.emulatordetect.EmulatorDetector;
import vikatouch.utils.url.URLBuilder;

/**
 * @author Shinovon
 * 
 */
public class Dialogs extends TimerTask {

	public static ConversationItem[] dialogs = new ConversationItem[0];

	public static int itemsCount;

	public static boolean selected;

	public static Thread downloaderThread;

	private static Thread downloaderThread2;

	public static Thread updater = null;

	public static boolean isUpdatingNow = false;

	private static Runnable runnable;

	protected static boolean avasLoaded;

		public static void refreshDialogsList(final boolean async, final boolean sendNofs) {
			try {
			if (downloaderThread != null && downloaderThread.isAlive())
				downloaderThread.interrupt();
			} catch (Throwable eee) {}
		runnable = new Runnable() {
			public void run() {
				if (isUpdatingNow)
					return;
				isUpdatingNow = true;
				try {
					if (dialogs.length != Settings.dialogsLength)
						dialogs = new ConversationItem[Settings.dialogsLength];
					itemsCount = Settings.dialogsLength;
					// if(async) VikaTouch.loading = true;
					String x = VikaUtils.downloadE(new URLBuilder("messages.getConversations").addField("count", "6"));

					try {
						// if(async) VikaTouch.loading = true;
						JSONObject response = new JSONObject(x).getJSONObject("response");
						JSONArray items = response.getJSONArray("items");
						JSONObject item = items.getJSONObject(0);
						boolean hasNew = dialogs[0] == null;
						short unreadC = 0;
						try {
							//hasNew = dialogs[0] == null
							//		|| !VikaUtils.cut(item.getJSONObject("last_message").optString("text"), 7)
							//				.equalsIgnoreCase(VikaUtils.cut(dialogs[0].lasttext, 7));
							hasNew = dialogs[0] == null
											|| !VikaUtils.cut(item.getJSONObject("last_message").optString("text"), 7)
													.equalsIgnoreCase(VikaUtils.cut(dialogs[0].lasttext, 7)) || !VikaUtils.cut(items.getJSONObject(1).getJSONObject("last_message").optString("text"), 7)
													.equalsIgnoreCase(VikaUtils.cut(dialogs[1].lasttext, 7)) || !VikaUtils.cut(items.getJSONObject(2).getJSONObject("last_message").optString("text"), 7)
													.equalsIgnoreCase(VikaUtils.cut(dialogs[2].lasttext, 7)) || !VikaUtils.cut(items.getJSONObject(3).getJSONObject("last_message").optString("text"), 7)
													.equalsIgnoreCase(VikaUtils.cut(dialogs[3].lasttext, 7)) || !VikaUtils.cut(items.getJSONObject(4).getJSONObject("last_message").optString("text"), 7)
													.equalsIgnoreCase(VikaUtils.cut(dialogs[4].lasttext, 7)) || !VikaUtils.cut(items.getJSONObject(5).getJSONObject("last_message").optString("text"), 7)
													.equalsIgnoreCase(VikaUtils.cut(dialogs[5].lasttext, 7));
												

						} catch (Exception e) {
						}
						unreadC = (short) response.optInt("unread_count");
						// itemsCount = (short) response.optInt("count");
						response.dispose();
						items.dispose();
						item.dispose();
						if (VikaTouch.unreadCount != unreadC || hasNew) {
							avasLoaded = false;
							VikaTouch.unreadCount = unreadC;
							x = VikaUtils.download(new URLBuilder("messages.getConversations").addField("filter", "all")
									.addField("extended", "1").addField("count", Settings.dialogsLength));
							if (async)
								VikaTouch.loading = true;
							response = new JSONObject(x).getJSONObject("response");
							items = response.getJSONArray("items");
							JSONArray profiles = response.optJSONArray("profiles");
							JSONArray groups = response.optJSONArray("groups");
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
									e.printStackTrace();
								}
							for (int i = 0; i < items.length(); i++) {
								item = items.getJSONObject(i);
								dialogs[i] = new ConversationItem(item);
								dialogs[i].parseJSON();
								dialogs[i].disposeJson();
								item.dispose();
							}
							if (dialogs.length > 1 && dialogs[0] != null
									&& !String.valueOf(dialogs[0].lastSenderId).equals(VikaTouch.userId)) {
								try {
								NokiaUIInvoker.softNotification(String.valueOf(dialogs[0].lastSenderId), VikaUtils.cut(dialogs[0].title, 10));
								} catch (Throwable ee) {}
								//!!!
								//VikaTouch.sendLog("lastsenderid = " + dialogs[0].lastSenderId);
								//VikaTouch.sendLog("notifid = " +String.valueOf(VikaTouch.a));
								
							//	VikaTouch.sendLog("notifid = " +String.valueOf(VikaTouch.a));
								
								VikaTouch.notificate(new VikaNotification(VikaNotification.NEW_MSG, dialogs[0].title,
										VikaUtils.cut(dialogs[0].lasttext, 40), VikaTouch.dialogsScr));
								return;
								//if(VikaTouch.mobilePlatform.indexOf("S60") > -1 && (VikaTouch.mobilePlatform.indexOf("5.3") > -1 || VikaTouch.mobilePlatform.indexOf("5.4") > -1 || VikaTouch.mobilePlatform.indexOf("5.5") > -1)) {
								//	VikaTouch.notifyy("type", "100058ec", "");
									//notify("type", dialogs[0].title, VikaUtils.cut(dialogs[0].text, 40));
									
								//}
								
							}
							items.dispose();
							x = null;

						}
						response.dispose();
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (Throwable e) {
						e.printStackTrace();
					}
					if (async)
						VikaTouch.loading = false;
				} catch (VikaNetworkError e) {
					VikaTouch.offlineMode = true;
				} catch (Throwable e) {
					VikaTouch.sendLog("Dialogs loading error " + e.toString());
				} finally {
					isUpdatingNow = false;
				}
				// if(async) VikaTouch.loading = true;

				runUpdater();
				// if(downloaderThread2 != null && downloaderThread2.isAlive())
				// downloaderThread2.interrupt();
				// поток качающий картинки
				if (!Settings.dontLoadAvas && !avasLoaded) {
					downloaderThread2 = new Thread() {
						public void run() {
							// if(isUpdatingNow) return;
							// VikaTouch.loading = true;
							int n = itemsCount;
							if (VikaTouch.mobilePlatform.indexOf("S60") < 0 && !(EmulatorDetector.emulatorType == EmulatorDetector.EM_J2L || 
									EmulatorDetector.emulatorType == EmulatorDetector.EM_KEM_OR_J2L ||
									EmulatorDetector.emulatorType == EmulatorDetector.EM_KEMMOD || EmulatorDetector.emulatorType == EmulatorDetector.EM_KEM
									)) {
								avasLoaded = true;
								return;
							}
							for (int i = 0; i < n; i++) {
								try {
									if (dialogs[i] != null) {
										if (isUpdatingNow)
											break;
										// dialogs[i].text=dialogs[i].avaurl;
										dialogs[i].getAva();

									}
								} catch (Throwable e) {
									VikaTouch.sendLog("Dialogs avas error " + e.getMessage());
								}
							}

							avasLoaded = true;
							// VikaTouch.loading = false;
						}
					};
					downloaderThread2.start();
				}
			}
		};
		if (async) {
			downloaderThread = new Thread(runnable);
			downloaderThread.start();
		} else {
			runnable.run();
		}
	}

	public static void runUpdater() {
		if (Settings.dialogsRefreshRate != 0 && (updater == null || !updater.isAlive())) {
			updater = new Thread() {
				public void run() {
					try {
						while (true) {
							if (Settings.dialogsRefreshRates[Settings.dialogsRefreshRate] == 0)
								return;
							Thread.sleep(Settings.dialogsRefreshRates[Settings.dialogsRefreshRate] * 1000);
							if (!(VikaTouch.canvas.currentScreen instanceof ChatScreen))
								refreshDialogsList(false, true);
						}
					} catch (Throwable t) {
						isUpdatingNow = false;
						return;
					}
				}
			};
			updater.start();
		}
	}

	public static void openDialog(ConversationItem dialogItem) {
		// VikaTouch.appInst.notifyDestroyed();
		// VikaTouch.sendLog(String.valueOf(dialogItem.peerId)+"
		// "+String.valueOf(dialogItem.title));
		// VikaTouch.error(String.valueOf(dialogItem.peerId)+"
		// "+String.valueOf(dialogItem.title), false);
		openDialog(dialogItem.peerId, dialogItem.title);
	}

	public static void openDialog(int peerId) {
		// VikaTouch.appInst.notifyDestroyed();
		VikaTouch.setDisplay(new ChatScreen(peerId), 1);
	}

	public static void openDialog(int peerId, String title) {
		// VikaTouch.appInst.notifyDestroyed();
		try {
			VikaTouch.isresending=true;
			
			VikaTouch.setDisplay(new ChatScreen(peerId, title), 1);
			if (VikaTouch.resendingmid!=0) {
			ChatScreen.attachAnswer(VikaTouch.resendingmid, VikaTouch.resendingname, VikaTouch.resendingtext);
			}
			if (VikaTouch.resendingobjectid!="") {
				ChatScreen.attachAnswer(VikaTouch.resendingobjectid, VikaTouch.resendingname, VikaTouch.resendingtext);	
			}
			//VikaTouch.sendLog(String.valueOf(VikaTouch.resendingmid)+ " " + VikaTouch.resendingname + " " + VikaTouch.resendingtext);
			//ChatScreen.attachAnswer(VikaTouch.resendingmid, VikaTouch.resendingname, VikaTouch.resendingtext);
		} catch (Throwable e) {
			// VikaTouch.sendLog("Dialog fail. "+e.toString());
			VikaTouch.appInst.notifyDestroyed();
		}
	}

	public void run() {
		if (!VikaTouch.offlineMode) {
			refreshDialogsList(true, false);
		}
	}

	public static void stopUpdater() {
		try {
		if (updater != null && updater.isAlive())
			updater.interrupt();
		} catch (Throwable ee) { }
	}

}
