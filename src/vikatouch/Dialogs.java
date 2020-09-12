package vikatouch;

import java.util.TimerTask;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import vikatouch.items.chat.ConversationItem;
import vikatouch.screens.ChatScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.url.URLBuilder;

public class Dialogs
	extends TimerTask
{
	
	public static int dialogsCount = 15;
	
	public static ConversationItem[] dialogs = new ConversationItem[15];
	
	public static JSONArray profiles;
	
	public static JSONArray groups;

	public static int itemsCount;

	public static boolean selected;
	
	public static Thread downloaderThread;

	private static Thread downloaderThread2;

	private static Runnable runnable;
	
	public static void refreshDialogsList(final boolean async)
	{
		if(downloaderThread != null && downloaderThread.isAlive())
			downloaderThread.interrupt();
		
		runnable = new Runnable()
		{
			public void run()
			{
				try
				{
					VikaTouch.loading = true;
					String x = VikaUtils.download(new URLBuilder("messages.getConversations").addField("count", "1"));
					try
					{
						VikaTouch.loading = true;
						JSONObject response = new JSONObject(x).getJSONObject("response");
						JSONArray items = response.getJSONArray("items");
						JSONObject item = items.getJSONObject(0);
						boolean u = dialogs[0] == null;
						short has = 0;
						try
						{
							u = dialogs[0] == null || !item.getJSONObject("last_message").optString("text").substring(0, 7).equalsIgnoreCase(dialogs[0].lasttext.substring(0, 7));
						}
						catch (Exception e)
						{
							
						}
						has = (short) response.optInt("unread_count");
						itemsCount = (short) response.optInt("count");
						if(itemsCount > dialogsCount)
						{
							itemsCount = dialogsCount;
						}
						response.dispose("response pre");
						items.dispose("items pre");
						item.dispose("item pre");
						if(VikaTouch.unreadCount != has || has > 0 || u)
						{
							VikaTouch.unreadCount = has;
							VikaTouch.loading = true;
							x = VikaUtils.download(new URLBuilder("messages.getConversations").addField("filter", "all").addField("extended", "1").addField("count", dialogsCount));
							VikaTouch.loading = true;
							response = new JSONObject(x).getJSONObject("response");
							items = response.getJSONArray("items");
							profiles = response.getJSONArray("profiles");
							groups = response.optJSONArray("groups");
							if(itemsCount > dialogsCount)
							{
								itemsCount = dialogsCount;
							}
							for(int i = 0; i < itemsCount; i++)
							{
								item = items.getJSONObject(i);
								dialogs[i] = new ConversationItem(item);
								dialogs[i].parseJSON();
								item.dispose("item for");
							}
							items.dispose("items");
							x = null;
						}
						response.dispose("response");
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}

					VikaTouch.loading = false;
				}
				catch (NullPointerException e)
				{
					if(!VikaTouch.offlineMode)
					VikaTouch.warn("Сбой соединения с сервером. Проверьте ваше подключение. Приложение переключено в оффлайн режим");
					VikaTouch.offlineMode = true;
					e.printStackTrace();
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
				VikaTouch.loading = true;
				
				//поток качающий картинки
				if(!Settings.dontLoadAvas)
				{
					downloaderThread2 = new Thread()
					{
						public void run()
						{
							VikaTouch.loading = true;
							
							for(int i = 0; i < itemsCount; i++)
							{
								if(dialogs[i] != null)
								{
									dialogs[i].getAva();
								}
							}
							
							VikaTouch.loading = false;
						}
					};
					downloaderThread2.start();
				}
				Thread.yield();
			}
		};
		if(async)
		{
			downloaderThread = new Thread(runnable);
			downloaderThread.start();
		}
		else
		{
			runnable.run();
		}
	}
	
	public static void refreshDialog()
	{
		
	}

	public static void openDialog(ConversationItem dialogItem)
	{
		openDialog(dialogItem.peerId, dialogItem.title);
	}

	public static void openDialog(int peerId)
	{
		VikaTouch.setDisplay(new ChatScreen(peerId), 1);
	}

	public static void openDialog(int peerId, String title)
	{
		VikaTouch.setDisplay(new ChatScreen(peerId, title), 1);
	}

	public void run()
	{
		if(!VikaTouch.offlineMode)
		{
			refreshDialogsList(true);
		}
	}

}
