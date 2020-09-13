package vikatouch.settings;

import javax.microedition.lcdui.Graphics;
import javax.microedition.rms.RecordStore;

import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.popup.ConfirmBox;
import ru.nnproject.vikaui.popup.ContextMenu;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.items.*;
import vikatouch.items.menu.OptionItem;
import vikatouch.locale.TextLocal;
import vikatouch.screens.AboutScreen;
import vikatouch.screens.LoginScreen;
import vikatouch.screens.MainScreen;
import vikatouch.screens.menu.MenuScreen;

public class SettingsScreen
	extends MainScreen
	implements IMenu 
{
	
	int[] countVals = new int[] { 10, 20, 30, 50, 80, 100 }; int countValDef = 1;
	int[] refreshVals = new int[] { 0, 2, 5, 8, 10, 15 }; int refreshValDef = 3;
	private static String titleStr;
	
	private PressableUIItem[] menuList;
	private PressableUIItem[] systemList;
	private PressableUIItem[] mediaList;
	private PressableUIItem[] uiList;
	private PressableUIItem backItem;
	private PressableUIItem[] debugList;
	private PressableUIItem[] specialAbilitiesList;
	
	public SettingsScreen()
	{
		super();
		hasBackButton = true;
		String[] eOd = new String[] { TextLocal.inst.get("settings.disabled"), TextLocal.inst.get("settings.enabled") };
		oneitemheight = (short) (DisplayUtils.compact?30:50);
		backItem = new OptionItem(this, TextLocal.inst.get("back"), IconsManager.BACK, 0, oneitemheight);
		


		// частота обновления
		int rr = -1;
		for(int i=0;i<refreshVals.length;i++)
		{
			if(refreshVals[i]==Settings.refreshRate) rr = i;
		}
		if(rr==-1)
		{
			rr = refreshValDef;
			Settings.messagesPerLoad = refreshVals[rr];
		}
		// сообщения за раз
		int mc = -1;
		for(int i=0;i<countVals.length;i++)
		{
			if(countVals[i]==Settings.messagesPerLoad) mc = i;
		}
		if(mc==-1)
		{
			mc = countValDef;
			Settings.messagesPerLoad = countVals[mc];
		}
		// списки
		int j = -1;
		for(int i=0;i<countVals.length;i++)
		{
			if(countVals[i]==Settings.simpleListsLength) j = i;
		}
		if(j==-1)
		{
			j = countValDef;
			Settings.simpleListsLength = countVals[j];
		}
		
		menuList = new PressableUIItem[]
		{
			new OptionItem(this, TextLocal.inst.get("settings.system"), IconsManager.DEVICE, -100, oneitemheight),
			new OptionItem(this, TextLocal.inst.get("settings.appearance"), IconsManager.MENU, -101, oneitemheight),
			new OptionItem(this, TextLocal.inst.get("settings.media"), IconsManager.PLAY, -102, oneitemheight),
			new OptionItem(this, TextLocal.inst.get("settings.spabilities"), IconsManager.FRIENDS, -103, oneitemheight),
			new OptionItem(this, TextLocal.inst.get("settings.debug"), IconsManager.SETTINGS, -104, oneitemheight),
			new SettingMenuItem(this, TextLocal.inst.get("settings.telemetry"), IconsManager.SEND, 11, 
					oneitemheight, eOd, Settings.telemetry?1:0, null),
			new SettingMenuItem(this, TextLocal.inst.get("settings.reporterrors"), IconsManager.SEND, 12, 
					oneitemheight, eOd, Settings.sendErrors?1:0, null),
			new OptionItem(this, TextLocal.inst.get("settings.reset"), IconsManager.SETTINGS, -3, oneitemheight),
			new OptionItem(this, TextLocal.inst.get("menu.about"), IconsManager.INFO, 31, oneitemheight),
			new OptionItem(this, TextLocal.inst.get("settings.logout"), -1, -1, oneitemheight),
		};
		systemList = new PressableUIItem[]
		{
			backItem,
			new SettingMenuItem(this, TextLocal.inst.get("settings.connection"), IconsManager.LINK, 3, 
					oneitemheight, new String[] { TextLocal.inst.get("settings.proxy"), TextLocal.inst.get("settings.directhttps") }, 
					Settings.https?1:0, null),
			
			new SettingMenuItem(this, TextLocal.inst.get("settings.cacheimages"), IconsManager.PHOTOS, 1, 
					oneitemheight, eOd, Settings.cacheImages?1:0, null),
			new SettingMenuItem(this, TextLocal.inst.get("settings.listslength"), IconsManager.MENU, 5, 
				oneitemheight, countVals, j, null),
			new SettingMenuItem(this, TextLocal.inst.get("settings.historycount"), IconsManager.MSGS, 6, 
				oneitemheight, countVals, j, null),
			new SettingMenuItem(this, TextLocal.inst.get("settings.refreshrate"), IconsManager.REFRESH, 7, 
				oneitemheight, refreshVals, rr, null),
			//блять я тебя захуярю
			/*new SettingMenuItem(this, TextLocal.inst.get("settings.automarkasread"), IconsManager.APPLY, 15, 
					oneitemheight, eOd, Settings.autoMarkAsRead?1:0, null),*/
		};
		mediaList = new PressableUIItem[]
		{
			backItem,
			new OptionItem(this, TextLocal.inst.get("settings.videoresolution"), IconsManager.VIDEOS, 21, oneitemheight),
			new SettingMenuItem(this, TextLocal.inst.get("settings.audio"), IconsManager.MUSIC, 9, 
					oneitemheight, new String[] { TextLocal.inst.get("settings.audioc.0"), TextLocal.inst.get("settings.audioc.1"),
							TextLocal.inst.get("settings.audioc.2"), TextLocal.inst.get("settings.audioc.3"),
							TextLocal.inst.get("settings.audioc.4"), TextLocal.inst.get("settings.audioc.5"),
							TextLocal.inst.get("settings.audioc.6"), TextLocal.inst.get("settings.audioc.7")}, 
					Settings.audioMode, null), // Это все способы открыть аудио. В теории. UPD: ну-ну.
			new SettingMenuItem(this, TextLocal.inst.get("settings.rtsp"), IconsManager.LINK, 13, 
					oneitemheight, new String[] { TextLocal.inst.get("settings.rtspc.0"), TextLocal.inst.get("settings.rtspc.1"),
							TextLocal.inst.get("settings.rtspc.2") }, 
					Settings.rtspMethod, null),
			new SettingMenuItem(this, TextLocal.inst.get("settings.youtube"), IconsManager.PLAY, 14, 
					oneitemheight, new String[] { "m.youtube.com", "SymTube" }, 
					Settings.https?1:0, null),
		};
		specialAbilitiesList = new PressableUIItem[]
		{
			backItem,
			new SettingMenuItem(this, TextLocal.inst.get("settings.sensor"), IconsManager.DEVICE, 4, 
					oneitemheight, new String[] { TextLocal.inst.get("settings.disabled"), TextLocal.inst.get("settings.j2meloader"), TextLocal.inst.get("settings.resistive"), TextLocal.inst.get("settings.kemulator") }, 
					Settings.sensorMode, TextLocal.inst.get("settings.sensorInfo")),
			

			new SettingMenuItem(this, TextLocal.inst.get("settings.dontloadavas"), IconsManager.PHOTOS, 2, 
					oneitemheight, eOd, Settings.dontLoadAvas?1:0, null),
		};

		debugList = new PressableUIItem[]
		{
			backItem,
			new OptionItem(this, TextLocal.inst.get("settings.clearсache"), IconsManager.CLOSE, -2, oneitemheight),
			new SettingMenuItem(this, TextLocal.inst.get("settings.debugcrap"), IconsManager.DEVICE, 10, 
					oneitemheight, eOd, Settings.debugInfo?1:0, null),
		};
		uiList = new PressableUIItem[]
		{
			backItem,
			new SettingMenuItem(this, TextLocal.inst.get("settings.transitionanimation"), IconsManager.ANIMATION, 0, 
				oneitemheight, eOd, Settings.animateTransition?1:0, null),

			
			new OptionItem(this, TextLocal.inst.get("settings.language"), IconsManager.EDIT, 23, oneitemheight),
		};
		
		titleStr = TextLocal.inst.get("title.settings");
		switchList(menuList);
	}

	public void draw(Graphics g)
	{
		update(g);
		
		int y = 58;
		if(uiItems!=null)
		{
			for (int i=0;i<uiItems.length;i++)
			{
				if(uiItems[i]!=null) {
					y+=2;
					uiItems[i].paint(g, y, scrolled);
					y+=uiItems[i].getDrawHeight()+2;
				}
			}
		}
		
		g.translate(0, -g.getTranslateY());
	}
	
	public final void drawHUD(Graphics g)
	{
		drawHUD(g, titleStr);
	}
	
	public final void release(int x, int y)
	{
		try
		{
			if(y > 58 && y < DisplayUtils.height - oneitemheight)
			{
				int h = oneitemheight+4;
				int yy1 = y - (scrolled + 58);
				int i = yy1 / h;
				if(i < 0)
					i = 0;
				if(!dragging)
				{
					uiItems[i].tap(x, yy1 - (h * i));
				}
			}
				
		}
		catch (ArrayIndexOutOfBoundsException e) 
		{ 
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		super.release(x, y);
	}
	
	public void settingSet(int setIndex, int var)
	{
		Settings.setted = true;
		switch(setIndex)
		{
			case 0:
			{
				Settings.animateTransition = var==1;
				break;
			}
			case 1:
			{
				Settings.cacheImages = var==1;
				break;
			}
			case 2:
			{
				Settings.dontLoadAvas = var==1;
				break;
			}
			case 3:
			{
				Settings.https = var==1; //TODO real switching
				Settings.proxy = var!=1;
				break;
			}
			case 4:
			{
				Settings.sensorMode = var;
				break;
			}
			case 5:
			{
				Settings.simpleListsLength = countVals[var];
				break;
			}
			case 6:
			{
				Settings.messagesPerLoad = countVals[var];
				break;
			}
			case 7:
			{
				Settings.refreshRate = refreshVals[var];
				break;
			}
			case 9:
			{
				Settings.audioMode = var;
				break;
			}
			case 10:
			{
				Settings.debugInfo = var==1;
				break;
			}
			case 11:
			{
				Settings.telemetry = var==1;
				break;
			}
			case 12:
			{
				Settings.sendErrors = var==1;
				break;
			}
			case 13:
			{
				Settings.rtspMethod = var;
				break;
			}
			case 14:
			{
				Settings.symtube = var==1;
				break;
			}
			case 15:
			{
				Settings.autoMarkAsRead = var==1;
				break;
			}
		}
		//Settings.saveSettings();
		// а вариант "поменял настройку и закрыл приложение" не? Оно кст и не работало, т.к. 14 команда давно не используется. И вообще, точно ли тот командИмпл нужен, когда всё кругом на оптион айтемах с их IMenu...
	}

	public void onMenuItemPress(int i)
	{
		switch(i)
		{
			case 0:
			{
				switchList(menuList);
				break;
			}
			case -100:
			{
				switchList(systemList);
				break;
			}
			case -101:
			{
				switchList(uiList);
				break;
			}
			case -102:
			{
				switchList(mediaList);
				break;
			}
			case -103:
			{
				switchList(specialAbilitiesList);
				break;
			}
			case -104:
			{
				switchList(debugList);
				break;
			}
			case -1:
			{
				if(VikaTouch.accessToken != null && VikaTouch.accessToken != "")
				{
					VikaTouch.popup(new ConfirmBox(TextLocal.inst.get("settings.logout")+"?", null, new Runnable()
					{
						public void run()
						{
							try
							{
								VikaTouch.logout();
								VikaTouch.gc();
							}
							catch(Exception e)
							{
		
							}
							VikaTouch.setDisplay(new LoginScreen(), -1);
						}}, null));
				}
				else
				{
					
				}
				break;
			}
			case -2:
			{
				break;
			}
			case -3:
			{
				VikaTouch.popup(new ConfirmBox(TextLocal.inst.get("settings.reset"), null, new Runnable()
				{
					public void run()
					{
						Settings.loadDefaultSettings();
						Settings.setted = true;
					}
				}, null));
				break;
			}
			case 21:
			{
				// со строками сеттинг айтем пока не умеет
				OptionItem[] it = new OptionItem[4];
				it[0] = new OptionItem(this, "240", IconsManager.VIDEOS, 11, oneitemheight);
				it[1] = new OptionItem(this, "360", IconsManager.VIDEOS, 12, oneitemheight);
				it[2] = new OptionItem(this, "480", IconsManager.VIDEOS, 13, oneitemheight);
				it[3] = new OptionItem(this, "720", IconsManager.VIDEOS, 14, oneitemheight);
				VikaTouch.popup(new ContextMenu(it));
				break;
			}
			case 31:
			{
				if(VikaTouch.about == null)
					VikaTouch.about = new AboutScreen();
				VikaTouch.setDisplay(VikaTouch.about, 1);
				break;
			}
			case 23:
			{
				OptionItem[] it = new OptionItem[4];
				it[0] = new OptionItem(this, "English (UK)", IconsManager.EDIT, 1, oneitemheight);
				it[1] = new OptionItem(this, "English (US)", IconsManager.EDIT, 2, oneitemheight);
				it[2] = new OptionItem(this, "Русский", IconsManager.EDIT, 3, oneitemheight);
				it[3] = new OptionItem(this, "Russian (translit)", IconsManager.EDIT, 4, oneitemheight);
				VikaTouch.popup(new ContextMenu(it));
				break;
			}
			// 11-20 - разреши видео! Пока, потом я мб таки запихну это дело в SettingItem.
		}
		if(i>=11&&i<=19)
		{
			int j = i - 11;
			String[] res = new String[] { "240", "360", "480", "720" };
			Settings.setted = true;
			Settings.videoResolution = res[j];
		}
		if(i>=1&&i<=9)
		{
			int j = i - 1;
			String[] res = new String[] { "en_UK", "en_US", "ru_RU", "lt_RU" };
			Settings.setted = true;
			Settings.language = res[j];
			/*VikaTouch.popup(new InfoPopup("Language was changed to "+res[j]+". The application must be restarted.", new Runnable()
			{
				public void run()
				{
					VikaTouch.appInst.destroyApp(false);
				}
			}, "Restart required", "Restart"));*/
			VikaTouch.popup(new InfoPopup("Settings will be applied after restart", null, "","OK"));
		}
	}

	private void switchList(PressableUIItem[] l) {
		if(l == this.menuList)
			hasBackButton = true;
		else
			hasBackButton = false;
		try
		{
			uiItems[currentItem].setSelected(false); // точно упадёт на старте - списка то ещё нет.
		} catch (Exception e) { }
		try
		{
			uiItems = l;
			itemsh = 58 + ((oneitemheight+4) * uiItems.length);
			itemsCount = (short) uiItems.length;
			if(keysMode)
			{
				currentItem = 0;
				uiItems[0].setSelected(true);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void onMenuItemOption(int i)
	{
		
	}

}
