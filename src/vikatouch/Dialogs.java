package vikatouch;

import java.io.IOException;
import java.util.TimerTask;

import javax.microedition.lcdui.Display;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;



import vikatouch.items.VikaNotification;
import vikatouch.items.chat.ConversationItem;
import vikatouch.locale.TextLocal;
import vikatouch.screens.ChatScreen;
import vikatouch.screens.DialogsScreen;
import vikatouch.screens.menu.MenuScreen;
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

	public static JSONArray items;

	private static int wasSelected;
	
	
	
	public static void loadMore() {
		// TODO Auto-generated method stub
		boolean async = true;
		if (downloaderThread != null && downloaderThread.isAlive())
			try {
				downloaderThread.interrupt();
			} catch (Throwable ee) {}
			runnable = new Runnable() {
				public void run() {
					if (isUpdatingNow)
						return;
					isUpdatingNow = true;
		avasLoaded = false;
		isUpdatingNow=true;
		//VikaTouch.unreadCount = unreadC;
		String x = null;
		try {
			
			x = VikaUtils.download(new URLBuilder("messages.getConversations").addField("filter", "all")
					.addField("extended", "1").addField("count", String.valueOf(itemsCount)));
			//System.out.print(x);
			//VikaTouch.sendLog(x);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			
			e1.printStackTrace();
			//return;
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			
			e1.printStackTrace();
			System.out.println("interrupted");
			//return;
		} catch (Throwable eee) {  }
		//if (async)
			VikaTouch.loading = true;
		JSONObject response = new JSONObject(x).getJSONObject("response");
		int c = response.getInt("count");
		items = response.optJSONArray("items");
		JSONArray profiles = response.optJSONArray("profiles");
		JSONArray groups = response.optJSONArray("groups");
	//	VikaTouch.sendLog(String.valueOf(itemsCount)+" "+String.valueOf(items.length()));
		if(profiles != null) 
			try {
				for(int i = 0; i < profiles.length(); i++) {
					JSONObject profile = profiles.optJSONObject(i);
					if(!VikaTouch.profiles.containsKey(new IntObject(profile.getInt("id"))) && profile != null) {
						VikaTouch.profiles.put(new IntObject(profile.getInt("id")), 
								new ProfileObject(profile.getInt("id"), 
										profile.getString("first_name"), profile.getString("last_name"), 
										profile.optString("photo_50"), profile.optString("online")));
					} else {
						if (VikaTouch.profiles.containsKey(new IntObject(profile.getInt("id"))) && (profile != null))  {
							if (!(profile.optString("online").equals(((ProfileObject) VikaTouch.profiles.get(new IntObject(profile.getInt("id")))).getOnline()))) {
								VikaTouch.profiles.put(new IntObject(profile.getInt("id")), 
										new ProfileObject(profile.getInt("id"), 
												profile.getString("first_name"), profile.getString("last_name"), 
												profile.optString("photo_50"), profile.optString("online")));
							}
						}
								
					}
				}
			} catch (Exception e) {
				
			}
		if(groups != null)
			try {
				for(int i = 0; i < groups.length(); i++) {
					JSONObject group = groups.optJSONObject(i);
					if(!VikaTouch.profiles.containsKey(new IntObject(-group.getInt("id"))) && group != null)
						VikaTouch.profiles.put(new IntObject(-group.getInt("id")), 
								new ProfileObject(group.getInt("id"), 
										group.getString("name"), 
										group.optString("photo_50")));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			try {
			JSONObject item = items.getJSONObject(0);
			dialogs[0] = new ConversationItem(item);
			dialogs[0].parseJSON();
			dialogs[0].disposeJson();
			item.dispose();
			} catch (Throwable eee) {}
			boolean hasNew = false;
			try {
			 hasNew = dialogs[0] == null
					|| !VikaUtils.cut(items.getJSONObject(0).getJSONObject("last_message").optString("text"), 7)
							.equalsIgnoreCase(VikaUtils.cut(dialogs[0].lasttext, 7)) || !VikaUtils.cut(items.getJSONObject(1).getJSONObject("last_message").optString("text"), 7)
							.equalsIgnoreCase(VikaUtils.cut(dialogs[1].lasttext, 7)) || !VikaUtils.cut(items.getJSONObject(2).getJSONObject("last_message").optString("text"), 7)
							.equalsIgnoreCase(VikaUtils.cut(dialogs[2].lasttext, 7)) || !VikaUtils.cut(items.getJSONObject(3).getJSONObject("last_message").optString("text"), 7)
							.equalsIgnoreCase(VikaUtils.cut(dialogs[3].lasttext, 7)) || !VikaUtils.cut(items.getJSONObject(4).getJSONObject("last_message").optString("text"), 7)
							.equalsIgnoreCase(VikaUtils.cut(dialogs[4].lasttext, 7)) || !VikaUtils.cut(items.getJSONObject(5).getJSONObject("last_message").optString("text"), 7)
							.equalsIgnoreCase(VikaUtils.cut(dialogs[5].lasttext, 7));
			} catch (Throwable eee) {
				
			}
		if (dialogs.length > 0 && dialogs[0] != null
				&& (!String.valueOf(dialogs[0].lastSenderId).equals(VikaTouch.userId) || (hasNew))) 
		{
			VikaTouch.needstoRedraw=true;
			if (Settings.notifmode == 4) {
				try {
				NokiaUIInvoker.softNotification(String.valueOf(dialogs[0].lastSenderId), VikaUtils.cut(dialogs[0].title, 10));
				} catch (Throwable ee) {}
			} 
			System.out.println("hasNew");
			//VikaTouch.sendLog("hasNew");
			for (int i = 0; i < itemsCount; i++) {
				try {
				JSONObject item = items.getJSONObject(i);
				dialogs[i] = new ConversationItem(item);
				if (i>=9) {
				//VikaTouch.sendLog(item.toString());
				}
				dialogs[i].parseJSON();
				//dialogs[i].avaurl=null;
				//dialogs[i].getAva();
				//dialogs[i].disposeJson();
				//item.dispose();
				} catch (Throwable eee) {}
			}
			
			//!!!
			//VikaTouch.sendLog("lastsenderid = " + dialogs[0].lastSenderId);
			//VikaTouch.sendLog("notifid = " +String.valueOf(VikaTouch.a));
			
		//	VikaTouch.sendLog("notifid = " +String.valueOf(VikaTouch.a));
			
			//VikaUtils.pronounceText("У вас новое сообщение от " + dialogs[0].title + ". Текст "+dialogs[0].fulltext);
			if (Settings.notifmode== 5) {
				try {
					VikaUtils.pronounceText("У вас новое сообщение от " + dialogs[0].title + ". Текст "+dialogs[0].fulltext);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				} else {
			try {
			VikaTouch.notificate(new VikaNotification(VikaNotification.NEW_MSG, dialogs[0].title,
					VikaUtils.cut(dialogs[0].lasttext, 40), VikaTouch.dialogsScr));
			VikaTouch.needstoRedraw=true;
			} catch (Throwable eee) {}
				}
			//return;
			//if(VikaTouch.mobilePlatform.indexOf("S60") > -1 && (VikaTouch.mobilePlatform.indexOf("5.3") > -1 || VikaTouch.mobilePlatform.indexOf("5.4") > -1 || VikaTouch.mobilePlatform.indexOf("5.5") > -1)) {
			//	VikaTouch.notifyy("type", "100058ec", "");
				//notify("type", dialogs[0].title, VikaUtils.cut(dialogs[0].text, 40));
				
			//}
			
		} else {
			
			int ost = c-itemsCount;
			
			//if (ost<0) {
				//VikaTouch.sendLog(String.valueOf(c)+ " - "+ String.valueOf(itemsCount) + " = "+ String.valueOf(ost));
			//}
			/*if (ost<0) {
				return;
			}*/
				if (ost>10) {
				ost=10;
			} else {
				//if (ost<=0) {
				ost=10+ost;
				//}
			}
			for (int i = 0; i < ost; i++) {
				try {
				JSONObject item = items.optJSONObject(itemsCount-10+i);
				dialogs[itemsCount-10+i] = new ConversationItem(item);
				dialogs[itemsCount-10+i].parseJSON();
				//VikaTouch.sendLog(String.valueOf(itemsCount-10+i)+" " +dialogs[itemsCount-10+i].text);
				//dialogs[i].avaurl=null;
				//dialogs[i].getAva();
				//dialogs[itemsCount-10+i].disposeJson();
				//item.dispose();
				} catch (Throwable eee) {}
			}
		}
		isUpdatingNow=false;
		if (!Settings.dontLoadAvas && !avasLoaded) {
			downloaderThread2 = new Thread() {
				public void run() {
					// if(isUpdatingNow) return;
					// VikaTouch.loading = true;
					int n = itemsCount;
					if (VikaTouch.mobilePlatform.indexOf("S60") < 0 && !(EmulatorDetector.emulatorType == EmulatorDetector.EM_J2L || 
							EmulatorDetector.emulatorType == EmulatorDetector.EM_KEM_OR_J2L ||
							EmulatorDetector.emulatorType == EmulatorDetector.EM_KEMNNMOD || EmulatorDetector.emulatorType == EmulatorDetector.EM_KEM
							)) {
						avasLoaded = true;
						return;
					}
					for (int i = 0; i < n; i++) {
						try {
							if (dialogs[i] != null) {
								if (isUpdatingNow)
									//Thread.yield();
									//return;
									break;
								// dialogs[i].text=dialogs[i].avaurl;
								dialogs[i].getAva();

							}
						} catch (Throwable e) {
							//VikaTouch.sendLog("Dialogs avas error " + e.getMessage());
						}
					}

					avasLoaded = true;
					// VikaTouch.loading = false;
				}
			};
			downloaderThread2.start();
		}
		
		//items.dispose();
		x = null;
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
			}
		};
		if (async) {
			downloaderThread = new Thread(runnable);
			downloaderThread.start();
		} else {
			runnable.run();
		}
		
		

	
	}
	
	
	
	
	
	
	
	
	
	

	public static void refreshDialogsList(final boolean async, final boolean sendNofs) {
		 Dialogs.wasSelected = 0;
		if (Dialogs.itemsCount>0) {
		for (int i = 0; i < Dialogs.itemsCount; i++) {
			if (Dialogs.dialogs[i] != null) {
				if (Dialogs.dialogs[i].selected == true) {
					wasSelected = i;
					break;
				}
			}
		}
		
		}
		System.gc();
		if (System.currentTimeMillis() - VikaTouch.lastsuccessfullupdatetime>120000) {
		/*	VikaTouch.needstoRedraw=true;
			VikaTouch.silenterror("Сети нет более 2 минут!", false);
			VikaTouch.needstoRedraw=true;
			Player notifplayer;
			try {
				notifplayer = Manager.createPlayer("device://tone");
				notifplayer.realize();
				notifplayer.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MediaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			Display d = Display.getDisplay(VikaTouch.appInst);
			VikaTouch.needstoRedraw=true;
			d.vibrate(1500);
			
		}
		//VikaTouch.needstoRedraw=true;
		
		VikaTouch.canvas.serviceRepaints();
		if (VikaUtils.playertext!=null) {
			if (VikaUtils.playertext.getState()==Player.STARTED) {
				return;
			} else {
				if (VikaUtils.playertext.getState()==Player.CLOSED) {
					VikaUtils.playertext.deallocate();
					VikaUtils.playertext=null;
					//return;
				}
			}
		}
		
		if (downloaderThread != null && downloaderThread.isAlive())
		try {
			VikaTouch.isdownloading=0;
			VikaTouch.needstoRedraw=true;
			downloaderThread.interrupt();
			return;
		} catch (Throwable ee) {}
		runnable = new Runnable() {
			public void run() { 	
				if (isUpdatingNow) {
					VikaTouch.isdownloading=1;
					VikaTouch.needstoRedraw=true;
					return;
				}
				isUpdatingNow = true;
				try {
					String xy = VikaUtils.download(new URLBuilder("account.getCounters"));
					
					JSONObject stats = new JSONObject(xy).getJSONObject("response");
					if (stats.has("friends")) {
					if (stats.optInt("friends")>Integer.parseInt(MenuScreen.frreqStr)) {
						if (Settings.notifmode==5) {
						VikaUtils.pronounceText("У вас новая заявка в друзья");
						} else {
							VikaTouch.notificate(new VikaNotification(VikaNotification.NEWFRIEND, "Уведомление",
									"У вас новая заявка в друзья", VikaTouch.dialogsScr));
							VikaTouch.needstoRedraw=true;
							
						}
					}
					MenuScreen.frreqStr = String.valueOf(stats.optInt("friends"));
					} else {
						MenuScreen.frreqStr = "0";
					}
					if (stats.has("notifications")) {
					if (stats.optInt("notifications")>Integer.parseInt(MenuScreen.notifStr)) {
						if (Settings.notifmode==5) {
							VikaUtils.pronounceText("У вас новое уведомление");
							} else {
								VikaTouch.notificate(new VikaNotification(VikaNotification.EVENT, "Уведомление",
										"У вас новое уведомление", VikaTouch.dialogsScr));
								VikaTouch.needstoRedraw=true;
								
							}
					}
						MenuScreen.notifStr = String.valueOf(stats.optInt("notifications"));
					} else {
						MenuScreen.notifStr = "0";
					}
				} catch (Throwable eee) {
					
				}
				short unreadC = 0;
				try {
					VikaTouch.isdownloading=1;
					VikaTouch.needstoRedraw=true;
					//if (dialogs.length != Settings.dialogsLength)
					if ((dialogs.length) <= 1) {
						dialogs = new ConversationItem[100];
						
					//itemsCount = Settings.dialogsLength;
					}
					// if(async) VikaTouch.loading = true;
					String x = VikaUtils.downloadE(new URLBuilder("messages.getConversations").addField("count", "6"));

					try {
						// if(async) VikaTouch.loading = true;
						JSONObject response = new JSONObject(x).getJSONObject("response");
						 items = response.getJSONArray("items");
						JSONObject item = items.getJSONObject(0);
						boolean hasNew = dialogs[0] == null;
						
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
												

						} catch (Throwable e) {
						}
						if (dialogs[0] != null) {
						for (int j=0; j<5; j++) {
							JSONObject conv = items.optJSONObject(j).optJSONObject("conversation");
							//if ((items.getJSONObject(j).optInt("in_read")!=0) && (items.getJSONObject(j).optInt("out_read")!=0) && (items.getJSONObject(j).optInt("last_message_id")!=0)) {
							if (dialogs[j].unanswered!=(conv.optInt("last_message_id")>conv.optInt("out_read"))) {
								dialogs[j].unanswered=(conv.optInt("last_message_id")>conv.optInt("out_read"));
							//}
						}
						}
						}
						unreadC = (short) response.optInt("unread_count");
						//VikaUtils.logToFile("unreadC="+String.valueOf(unreadC)+ " unreadCount="+ String.valueOf(VikaTouch.unreadCount)+" " );
						try {
							if (VikaTouch.userId.equals("3225000")) {
						DialogsScreen.titleStr = TextLocal.inst.get("title.chats")+ " ("+String.valueOf(unreadC) +")" + " / (" + MenuScreen.frreqStr + ")";
							} else {
						DialogsScreen.titleStr = TextLocal.inst.get("title.chats")+ " ("+String.valueOf(unreadC)+")";
							}
						} catch (Throwable eee ) {}
						// itemsCount = (short) response.optInt("count");
						response.dispose();
						//items.dispose();
						item.dispose();
						if 
						(VikaTouch.unreadCount != unreadC || 
								hasNew) {
							VikaTouch.isdownloading=1;
							VikaTouch.needstoRedraw=true;
							avasLoaded = false;
							//VikaTouch.unreadCount = unreadC;
							x = VikaUtils.download(new URLBuilder("messages.getConversations").addField("filter", "all")
									.addField("extended", "1").addField("count", 
											//Settings.dialogsLength
											itemsCount
											));
							
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
										if(!VikaTouch.profiles.containsKey(new IntObject(profile.getInt("id"))) && profile != null) {
											VikaTouch.profiles.put(new IntObject(profile.getInt("id")), 
													new ProfileObject(profile.getInt("id"), 
															profile.getString("first_name"), profile.getString("last_name"), 
															profile.optString("photo_50"), profile.optString("online")));
										} else {
											if (VikaTouch.profiles.containsKey(new IntObject(profile.getInt("id"))) && (profile != null))  {
												if (!(profile.optString("online").equals(((ProfileObject) VikaTouch.profiles.get(new IntObject(profile.getInt("id")))).getOnline()))) {
													VikaTouch.profiles.put(new IntObject(profile.getInt("id")), 
															new ProfileObject(profile.getInt("id"), 
																	profile.getString("first_name"), profile.getString("last_name"), 
																	profile.optString("photo_50"), profile.optString("online")));
												}
											}
													
										}
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
								JSONObject itemm = items.getJSONObject(i);
								dialogs[i] = new ConversationItem(itemm);
								dialogs[i].parseJSON();
								//dialogs[i].disposeJson();
								itemm.dispose();
							}
							
							
							if (dialogs.length > 1 && dialogs[0] != null
									&& !String.valueOf(dialogs[0].lastSenderId).equals(VikaTouch.userId)) {
								VikaTouch.needstoRedraw=true;
								if (Settings.notifmode == 4) {
									try {
									NokiaUIInvoker.softNotification(String.valueOf(dialogs[0].lastSenderId), VikaUtils.cut(dialogs[0].title, 10));
									} catch (Throwable ee) {}
								} 
								//!!!
								//VikaTouch.sendLog("lastsenderid = " + dialogs[0].lastSenderId);
								//VikaTouch.sendLog("notifid = " +String.valueOf(VikaTouch.a));
								
							//	VikaTouch.sendLog("notifid = " +String.valueOf(VikaTouch.a));
								
								
								
								if (VikaTouch.unreadCount < unreadC || hasNew) {
									VikaTouch.unreadCount = unreadC;
									if (Settings.notifmode== 5) {
										VikaUtils.pronounceText("У вас новое сообщение от " + dialogs[0].title + ". Текст "+dialogs[0].fulltext);
										} else {
									try {
								VikaTouch.notificate(new VikaNotification(VikaNotification.NEW_MSG, dialogs[0].title,
										VikaUtils.cut(dialogs[0].lasttext, 40), VikaTouch.dialogsScr));
								VikaTouch.needstoRedraw=true;
								VikaTouch.needstoRedraw=true;
								
								VikaTouch.needstoRedraw=true;
								//return;
								} catch (Throwable eee) {
									VikaTouch.needstoRedraw=true;
									VikaTouch.needstoRedraw=true;
									VikaTouch.unreadCount = unreadC;
									VikaTouch.needstoRedraw=true;
									//return;
								}
										}
								}
								VikaTouch.needstoRedraw=true;
								VikaTouch.unreadCount = unreadC;
								VikaTouch.needstoRedraw=true;
								//return;
								//if(VikaTouch.mobilePlatform.indexOf("S60") > -1 && (VikaTouch.mobilePlatform.indexOf("5.3") > -1 || VikaTouch.mobilePlatform.indexOf("5.4") > -1 || VikaTouch.mobilePlatform.indexOf("5.5") > -1)) {
								//	VikaTouch.notifyy("type", "100058ec", "");
									//notify("type", dialogs[0].title, VikaUtils.cut(dialogs[0].text, 40));
									
								//}
								
							}
							//items.dispose();
							x = null;
							VikaTouch.isdownloading=0;
							VikaTouch.needstoRedraw=true;
							VikaTouch.unreadCount = unreadC;
						}
						//response.dispose();
					} catch (JSONException e) {
						VikaTouch.isdownloading=2;
						VikaTouch.needstoRedraw=true;
						VikaTouch.unreadCount = unreadC;
						e.printStackTrace();
					} catch (Throwable e) {
						VikaTouch.unreadCount = unreadC;
						VikaTouch.isdownloading=2;
						VikaTouch.needstoRedraw=true;
						e.printStackTrace();
					}
					if (async)
						VikaTouch.loading = false;
				} catch (VikaNetworkError e) {
					VikaTouch.unreadCount = unreadC;
					VikaTouch.offlineMode = true;
					VikaTouch.isdownloading=2;
					VikaTouch.needstoRedraw=true;
				} catch (Throwable e) {
					VikaTouch.unreadCount = unreadC;
					VikaTouch.isdownloading=2;
					VikaTouch.needstoRedraw=true;
					VikaTouch.sendLog("Dialogs loading error " + e.toString());
				} finally {
					VikaTouch.unreadCount = unreadC;
					isUpdatingNow = false;
					VikaTouch.isdownloading=0;
					VikaTouch.needstoRedraw=true;
				}
				// if(async) VikaTouch.loading = true;
				if (DialogsScreen.keysMode) {
					DialogsScreen.unselectAll();
					Dialogs.dialogs[wasSelected].setSelected(true);
					}
				runUpdater();
				// if(downloaderThread2 != null && downloaderThread2.isAlive())
				// downloaderThread2.interrupt();
				// поток качающий картинки
				if (!Settings.dontLoadAvas && !avasLoaded) {
					downloaderThread2 = new Thread() {
						public void run() {
							VikaTouch.isdownloading=1;
							VikaTouch.needstoRedraw=true;
							// if(isUpdatingNow) return;
							// VikaTouch.loading = true;
							int n = itemsCount;
							if (VikaTouch.mobilePlatform.indexOf("S60") < 0 && !(EmulatorDetector.emulatorType == EmulatorDetector.EM_J2L || 
									//EmulatorDetector.emulatorType == EmulatorDetector.EM_KEM_OR_J2L ||
									EmulatorDetector.emulatorType == EmulatorDetector.EM_KEMNNMOD || EmulatorDetector.emulatorType == EmulatorDetector.EM_KEM
									)) {
								avasLoaded = true;
								VikaTouch.isdownloading=0;
										VikaTouch.needstoRedraw=true;
								return;
							}
							for (int i = 0; i < n; i++) {
								try {
									if (dialogs[i] != null) {
										if (isUpdatingNow)
											break;
										// dialogs[i].text=dialogs[i].avaurl;
										VikaTouch.isdownloading=1;
										VikaTouch.needstoRedraw=true;
										dialogs[i].getAva();
										VikaTouch.isdownloading=0;
										VikaTouch.needstoRedraw=true;

									}
								} catch (Throwable e) {
									//VikaTouch.sendLog("Dialogs avas error " + e.getMessage());
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
		if (DialogsScreen.keysMode) {
		Dialogs.dialogs[wasSelected].setSelected(true);
		}
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
	}

	public static void runUpdater() {
		if (Settings.dialogsRefreshRate != 0 && (updater == null || !updater.isAlive())) {
			updater = new Thread() {
				public void run() {
					VikaTouch.isdownloading=1;
					VikaTouch.needstoRedraw=true;
					try {
						while (true) {
							if (Settings.dialogsRefreshRates[Settings.dialogsRefreshRate] == 0) {
								VikaTouch.isdownloading=0;
								VikaTouch.needstoRedraw=true;
								return;
							}
							Thread.sleep(Settings.dialogsRefreshRates[Settings.dialogsRefreshRate] * 1000);
							//if (!(VikaTouch.canvas.currentScreen instanceof ChatScreen)) {
								VikaTouch.isdownloading=1;
								VikaTouch.needstoRedraw=true;
								refreshDialogsList(false, true);
								VikaTouch.isdownloading=0;
								VikaTouch.needstoRedraw=true;
							//}
						}
					} catch (Throwable t) {
						VikaTouch.isdownloading=2;
						VikaTouch.needstoRedraw=true;
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
		openDialog(dialogItem.peerId, dialogItem.title, dialogItem.unread);
	}

	public static void openDialog(int peerId) {
		// VikaTouch.appInst.notifyDestroyed();
		try {
		VikaTouch.setDisplay(new ChatScreen(peerId), 1);
		} catch (Throwable eee) {}
	}

	public static void openDialog(int peerId, String title, int unread) {
		// VikaTouch.appInst.notifyDestroyed();
		try {
			VikaTouch.isresending=true;
			
			VikaTouch.setDisplay(new ChatScreen(peerId, title, unread), 1);
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
			//VikaTouch.appInst.notifyDestroyed();
		}
	}

	public void run() {
		if (!VikaTouch.offlineMode) {
			VikaTouch.isdownloading=1;
			refreshDialogsList(true, false);
			if (DialogsScreen.keysMode) {
				int wasSelected = 0;
				if (Dialogs.itemsCount>0) {
				for (int i = 0; i < Dialogs.itemsCount; i++) {
					if (Dialogs.dialogs[i] != null) {
						if (Dialogs.dialogs[i].selected == true) {
							wasSelected = i;
							//break;
						}
					}
				}
				
				}
				
				
				
				DialogsScreen.di.currentItem = wasSelected ;
				if (wasSelected!=0) {
				Dialogs.dialogs[0].setSelected(false);
				}
			Dialogs.dialogs[DialogsScreen.di.currentItem].setSelected(true);
			}
			VikaTouch.needstoRedraw=true;
			DialogsScreen.di.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			//VikaTouch.isdownloading=0;
		}
	}

	public static void stopUpdater() {
		try {
		if (updater != null && updater.isAlive()) {
		try {
			updater.interrupt();
		} catch (Throwable ee) {}
		}
		} catch (Throwable ee) { }
	}

}
