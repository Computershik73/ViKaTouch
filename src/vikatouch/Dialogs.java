package vikatouch;

import java.util.TimerTask;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.popup.VikaNotification;
import vikatouch.items.chat.ConversationItem;
import vikatouch.screens.ChatScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.url.URLBuilder;

public class Dialogs extends TimerTask {

	public static ConversationItem[] dialogs = new ConversationItem[0];

	public static JSONArray profiles;

	public static JSONArray groups;

	public static int itemsCount;

	public static boolean selected;

	public static Thread downloaderThread;

	private static Thread downloaderThread2;

	public static Thread updater = null;

	public static boolean isUpdatingNow = false;

	private static Runnable runnable;

	protected static boolean avasLoaded;

	public static void refreshDialogsList(final boolean async, final boolean sendNofs) {
		if (downloaderThread != null && downloaderThread.isAlive())
			downloaderThread.interrupt();

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
					String x = VikaUtils.downloadE(new URLBuilder("messages.getConversations").addField("count", "1"));

					try {
						// if(async) VikaTouch.loading = true;
						JSONObject response = new JSONObject(x).getJSONObject("response");
						JSONArray items = response.getJSONArray("items");
						JSONObject item = items.getJSONObject(0);
						boolean hasNew = dialogs[0] == null;
						short unreadC = 0;
						try {
							hasNew = dialogs[0] == null
									|| !VikaUtils.cut(item.getJSONObject("last_message").optString("text"), 7)
											.equalsIgnoreCase(VikaUtils.cut(dialogs[0].lasttext, 7));
							if (hasNew) {
								avasLoaded = false;
							}

						} catch (Exception e) {
						}
						unreadC = (short) response.optInt("unread_count");
						// itemsCount = (short) response.optInt("count");
						response.dispose();
						items.dispose();
						item.dispose();
						if (VikaTouch.unreadCount != unreadC || unreadC > 0 || hasNew) {
							VikaTouch.unreadCount = unreadC;
							x = VikaUtils.download(new URLBuilder("messages.getConversations").addField("filter", "all")
									.addField("extended", "1").addField("count", Settings.dialogsLength));
							if (async)
								VikaTouch.loading = true;
							response = new JSONObject(x).getJSONObject("response");
							items = response.getJSONArray("items");
							profiles = response.getJSONArray("profiles");
							groups = response.optJSONArray("groups");
							for (int i = 0; i < items.length(); i++) {
								item = items.getJSONObject(i);
								dialogs[i] = new ConversationItem(item);
								dialogs[i].parseJSON();
								dialogs[i].disposeJson();
								item.dispose();
							}
							if (sendNofs && hasNew && dialogs.length > 1 && dialogs[0] != null
									&& !String.valueOf(dialogs[0].lastSenderId).equals(VikaTouch.userId)) {
								if(VikaTouch.mobilePlatform.indexOf("S60") >= 0 && VikaTouch.mobilePlatform.indexOf("5.3") >= 0) {
									VikaTouch.notify("type", dialogs[0].title, VikaUtils.cut(dialogs[0].text, 40));
								}
								VikaTouch.notificate(new VikaNotification(VikaNotification.NEW_MSG, dialogs[0].title,
										VikaUtils.cut(dialogs[0].text, 40), VikaTouch.dialogsScr));
							}
							items.dispose();
							x = null;

						}
						response.dispose();
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (Throwable e) {
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
							int n = Settings.dialogsLength;
							if (VikaTouch.mobilePlatform.indexOf("S60") < 0) {
								n = 0;
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
			VikaTouch.setDisplay(new ChatScreen(peerId, title), 1);
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
		if (updater != null && updater.isAlive())
			updater.interrupt();
	}

}
