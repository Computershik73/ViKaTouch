package vikatouch.settings;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;


import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.menu.items.OptionItem;
import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.popup.AutoContextMenu;
import ru.nnproject.vikaui.popup.ConfirmBox;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.items.menu.OnOffItem;
import vikatouch.locale.TextLocal;
import vikatouch.screens.AboutScreen;
import vikatouch.screens.LoginScreen;
import vikatouch.screens.MainScreen;

/**
 * @author Feodor0090
 * 
 */
public class SettingsScreen extends MainScreen implements IMenu {

	static int[] countVals = new int[] {1, 2, 3, 5, 8, 10, 15, 20, 30, 50, 80, 100, 150, 200};
	static String[] musicpathVals = new String[] {Settings.diskC, Settings.diskE, Settings.diskF, Settings.diskEMMC, Settings.diskMaemoDocs};
	static int countValDef = 1;
	static int[] refreshVals = new int[] { 0, 2, 5, 8, 10, 15, 200 };
	static int refreshValDef = 3;
	static int[] fpsVals = new int[] { 100, 60, 30, 15};
	public static final int[] refreshtimeouts = new int[] { 35, 60, 90, 120, 180, 240, 300, 360, 420, 480, 1000};
	private static String titleStr;

	private PressableUIItem[] menuList;
	private PressableUIItem[] systemList;
	private PressableUIItem[] msgList;
	private PressableUIItem[] mediaList;
	private PressableUIItem[] uiList;
	private PressableUIItem backItem;
	private PressableUIItem[] debugList;
	private PressableUIItem[] specialAbilitiesList;

	public SettingsScreen() {
		super();
		VikaTouch.needstoRedraw=true;
		hasBackButton = true;
		oneitemheight = (short) (DisplayUtils.compact ? 30 : 50);
		backItem = new OptionItem(this, TextLocal.inst.get("back"), IconsManager.BACK, 0, oneitemheight);

		initAllSettsList();
		
		titleStr = TextLocal.inst.get("title.settings");
		switchList(menuList);
	}

	private void initAllSettsList() {
		VikaTouch.needstoRedraw=true;
		String[] eOd = new String[] { TextLocal.inst.get("settings.disabled"), TextLocal.inst.get("settings.enabled") };
		String[] dialogsRR = new String[] { eOd[0], "5", "10", "20", "30" };
		String[] rrefreshtimeout = new String[] { "35", "60", "90", "120", "180", "240", "300", "360", "420", "480", "1000" };
		String[] notifModes = new String[] { TextLocal.inst.get("settings.disabled"),
				TextLocal.inst.get("settings.vibro"), TextLocal.inst.get("settings.sound"), TextLocal.inst.get("settings.tone"), TextLocal.inst.get("settings.alert"), TextLocal.inst.get("settings.pronouncetext"), TextLocal.inst.get("settings.vibro") +" + "+ TextLocal.inst.get("settings.sound") };

		// частота обновления
		int rr = -1;
		for (int i = 0; i < refreshVals.length; i++) {
			if (refreshVals[i] == Settings.msgRefreshRate)
				rr = i;
		}
		if (rr == -1) {
			rr = refreshValDef;
			//Settings.messagesPerLoad = refreshVals[rr];
		}
		if (rr == -1) {
			rr = refreshValDef;
			//Settings.messagesPerLoad = refreshVals[rr];
		}
		// сообщения за раз
		int mc = -1;
		for (int i = 0; i < countVals.length; i++) {
			if (countVals[i] == Settings.messagesPerLoad)
				mc = i;
		}
		if (mc == -1) {
			mc = countValDef;
			Settings.messagesPerLoad = countVals[mc];
		}
		
		
		// списки
		int j = -1;
		for (int i = 0; i < countVals.length; i++) {
			if (countVals[i] == Settings.simpleListsLength)
				j = i;
		}
		if (j == -1) {
			j = countValDef;
			Settings.simpleListsLength = countVals[j];
		}
		// диалоги
		int j0 = -1;
		for (int i = 0; i < countVals.length; i++) {
			if (countVals[i] == Settings.dialogsLength)
				j0 = i;
		}
		if (j0 == -1) {
			j0 = countValDef;
			Settings.dialogsLength = countVals[j0];
		}

		int j1 = -1;
		for (int i = 0; i < refreshtimeouts.length; i++) {
			if (refreshtimeouts[i] == Settings.refreshtimeout)
				j1 = i;
		}
		
		int fc = -1;
		for (int i = 0; i < fpsVals.length; i++) {
			if (fpsVals[i] == Settings.fpsLimit)
				fc = i;
		}
		
		//Settings.mpc = -1;
		for (int i = 0; i < musicpathVals.length; i++) {
			if (musicpathVals[i].equals(Settings.musicpath))
				Settings.mpc = i;
		}
		
		
		menuList = new PressableUIItem[] {
				new OptionItem(this, TextLocal.inst.get("settings.system"), IconsManager.DEVICE, -100, oneitemheight),
				new OptionItem(this, TextLocal.inst.get("settings.appearance"), IconsManager.MENU, -101, oneitemheight),
				new OptionItem(this, TextLocal.inst.get("title.chats"), IconsManager.MSGS, -105, oneitemheight),
				new OptionItem(this, TextLocal.inst.get("settings.media"), IconsManager.PLAY, -102, oneitemheight),
				new OptionItem(this, TextLocal.inst.get("settings.spabilities"), IconsManager.FRIENDS, -103,
						oneitemheight),
				new OptionItem(this, TextLocal.inst.get("settings.debug"), IconsManager.SETTINGS, -104, oneitemheight),
				new SettingMenuItem(this, TextLocal.inst.get("settings.sendlogs"), IconsManager.SEND, 12, oneitemheight,
						eOd, Settings.sendLogs ? 1 : 0, null),
				new OptionItem(this, TextLocal.inst.get("settings.reset"), IconsManager.SETTINGS, -3, oneitemheight),
				new OptionItem(this, TextLocal.inst.get("menu.about"), IconsManager.INFO, 31, oneitemheight),
				new OptionItem(this, TextLocal.inst.get("settings.logout"), IconsManager.BACK, -1, oneitemheight), };
		systemList = new PressableUIItem[] { backItem,
				new SettingMenuItem(this, TextLocal.inst.get("settings.connection"), IconsManager.LINK, 3,
						oneitemheight,
						new String[] { TextLocal.inst.get("settings.proxy"),
								TextLocal.inst.get("settings.directhttps") },
						Settings.https ? 1 : 0, null),

				// new SettingMenuItem(this, TextLocal.inst.get("settings.cacheimages"),
				// IconsManager.PHOTOS, 1,
				// oneitemheight, eOd, Settings.cacheImages?1:0, null, true),
				new SettingMenuItem(this, TextLocal.inst.get("settings.listslength"), IconsManager.MENU, 5,
						oneitemheight, countVals, j, null),

				new SettingMenuItem(this, TextLocal.inst.get("FPS"), IconsManager.DEVICE, 60,
						oneitemheight, fpsVals, fc, null),
				new SettingMenuItem(this, TextLocal.inst.get("settings.doublebuffer"), IconsManager.DEVICE, 61,
						oneitemheight, eOd, Settings.doubleBufferization ? 1 : 0, null),
				new SettingMenuItem(this, TextLocal.inst.get("settings.drawpriority"), IconsManager.DEVICE, 62,
						oneitemheight, 
						eOd, Settings.drawMaxPriority ? 1 : 0, null),

				new SettingMenuItem(this, TextLocal.inst.get("settings.imagescaling"), IconsManager.PHOTOS, 63,
						oneitemheight, 
						new String[] { TextLocal.inst.get("settings.fast"), TextLocal.inst.get("settings.filtered") }, Settings.fastImageScaling ? 0 : 1, null),

		};
		msgList = new PressableUIItem[] { backItem,
				new SettingMenuItem(this, TextLocal.inst.get("settings.historycount"), IconsManager.MSGS, 6,
						oneitemheight, countVals, mc, null),
				new SettingMenuItem(this, TextLocal.inst.get("settings.refreshrate"), IconsManager.REFRESH, 7,
						oneitemheight, refreshVals, rr, null),
				// блять я тебя захуярю
				/**/new SettingMenuItem(this, TextLocal.inst.get("settings.automarkasread"), IconsManager.APPLY, 15,
						oneitemheight, eOd, Settings.autoMarkAsRead ? 1 : 0, null, true), /**/
				new SettingMenuItem(this, TextLocal.inst.get("settings.dialogscount"), IconsManager.MENU, 20,
						oneitemheight, countVals, j0, null),
				new SettingMenuItem(this, TextLocal.inst.get("settings.dialogsrefreshrate"), IconsManager.REFRESH, 21,
						oneitemheight, dialogsRR, Settings.dialogsRefreshRate, null),
				
				new SettingMenuItem(this, TextLocal.inst.get("settings.refreshtimeout"), IconsManager.REFRESH, 25,
						oneitemheight, rrefreshtimeout, j1, null),

				new SettingMenuItem(this, TextLocal.inst.get("settings.notificationmode"), IconsManager.REFRESH, 22,
						oneitemheight, notifModes, Settings.notifmode, null), };
		mediaList = new PressableUIItem[] { backItem,
				new OptionItem(this, TextLocal.inst.get("settings.videoresolution"), IconsManager.VIDEOS, 21,
						oneitemheight),
				new SettingMenuItem(this, TextLocal.inst.get("settings.audio"), IconsManager.MUSIC, 9, oneitemheight,
						new String[] { TextLocal.inst.get("settings.audioc.0"), TextLocal.inst.get("settings.audioc.1"),
								TextLocal.inst.get("settings.audioc.2"), TextLocal.inst.get("settings.audioc.3"),
								TextLocal.inst.get("settings.audioc.4"), TextLocal.inst.get("settings.audioc.5"),
								TextLocal.inst.get("settings.audioc.6"), TextLocal.inst.get("settings.audioc.7") },
						Settings.audioMode, null), // Это все способы открыть аудио. В теории. UPD: ну-ну.
				new SettingMenuItem(this, TextLocal.inst.get("settings.rtsp"), IconsManager.LINK, 13, oneitemheight,
						new String[] { TextLocal.inst.get("settings.rtspc.0"), TextLocal.inst.get("settings.rtspc.1"),
								TextLocal.inst.get("settings.rtspc.2") },
						Settings.rtspMethod, null),
				new SettingMenuItem(this, TextLocal.inst.get("settings.loadmusicviahttp"), IconsManager.DOWNLOAD, 16,
						oneitemheight, new String[] { TextLocal.inst.get("settings.auto"), "HTTP", "HTTPS" },
						Settings.loadMusicViaHttp, null),
				new SettingMenuItem(this, TextLocal.inst.get("settings.dontloadmusicwithkey"), IconsManager.DOWNLOAD,
						18, oneitemheight,
						new String[] { TextLocal.inst.get("settings.auto"), TextLocal.inst.get("settings.disabled"),
								TextLocal.inst.get("settings.enabled") },
						Settings.loadMusicWithKey, null),
				new SettingMenuItem(this, TextLocal.inst.get("settings.itunescovers"), IconsManager.PHOTOS, 17,
						oneitemheight, eOd, Settings.loadITunesCovers ? 1 : 0, null, true),
				new SettingMenuItem(this, TextLocal.inst.get("settings.youtube"), IconsManager.PLAY, 14, oneitemheight,
						new String[] { "m.youtube.com", "SymTube" }, Settings.https ? 1 : 0, null),
				new SettingMenuItem(this, TextLocal.inst.get("settings.musicpath"), IconsManager.MUSIC, 64, oneitemheight,
						new String[] { TextLocal.inst.get("Settings.diskC"), TextLocal.inst.get("Settings.diskE"),
								TextLocal.inst.get("Settings.diskF"), TextLocal.inst.get("Settings.diskEMMC"), Settings.diskMaemoDocs },
						Settings.mpc, null)	
				
		
		};
		specialAbilitiesList = new PressableUIItem[] { backItem, new SettingMenuItem(this,
				TextLocal.inst.get("settings.sensor"), IconsManager.DEVICE, 4, oneitemheight,
				new String[] { TextLocal.inst.get("settings.disabled"), TextLocal.inst.get("settings.j2meloader"),
						TextLocal.inst.get("settings.resistive"), TextLocal.inst.get("settings.kemulator") },
				Settings.sensorMode, TextLocal.inst.get("settings.sensorInfo")),

				new SettingMenuItem(this, TextLocal.inst.get("settings.dontloadavas"), IconsManager.PHOTOS, 2,
						oneitemheight, eOd, Settings.dontLoadAvas ? 1 : 0, null, true), 
				new OnOffItem(this, TextLocal.inst.get("settings.showpics"), IconsManager.SETTINGS, 98, oneitemheight, Settings.showpics),		
		};

		debugList = new PressableUIItem[] { backItem,
				// new OptionItem(this, TextLocal.inst.get("settings.clearсache"),
				// IconsManager.CLOSE, -2, oneitemheight),
				new SettingMenuItem(this, TextLocal.inst.get("settings.debugcrap"), IconsManager.DEVICE, 10,
						oneitemheight, eOd, Settings.debugInfo ? 1 : 0, null, true), };
		uiList = new PressableUIItem[] { backItem,
				/*
				 * new SettingMenuItem(this, TextLocal.inst.get("settings.transitionanimation"),
				 * IconsManager.ANIMATION, 0, oneitemheight, eOd,
				 * Settings.animateTransition?1:0, null),
				 */
				// как известно, анимация давно сломанна, да и вообще в ней нет смысла т.к. при
				// открытии экрана всё лагает.
				

				new OptionItem(this, TextLocal.inst.get("settings.language"), IconsManager.EDIT, 23, oneitemheight),
				new OptionItem(this, TextLocal.inst.get("settings.region"), IconsManager.EDIT, 50, oneitemheight),
				new SettingMenuItem(this, TextLocal.inst.get("settings.hidebottom"), IconsManager.DEVICE, 24,
						oneitemheight, eOd, Settings.hideBottom ? 1 : 0, null, true),
				new SettingMenuItem(this, TextLocal.inst.get("settings.vibontouch"), IconsManager.DEVICE, 19,
						oneitemheight, eOd, Settings.vibOnTouch ? 1 : 0, null, true),
				new OnOffItem(this, TextLocal.inst.get("settings.night"), IconsManager.SETTINGS, 99, oneitemheight, Settings.nightTheme) };

	}

	public void draw(Graphics g) {
		update(g);

		int y = topPanelH;
		if (uiItems != null) {
			for (int i = 0; i < uiItems.size(); i++) {
				if (((PressableUIItem) uiItems.elementAt(i)) != null) {
					((PressableUIItem) uiItems.elementAt(i)).paint(g, y, this.scrolled);
					y += ((PressableUIItem) uiItems.elementAt(i)).getDrawHeight();
				}
			}
		}

		g.translate(0, -g.getTranslateY());
	}

	public final void drawHUD(Graphics g) {
		drawHUD(g, titleStr);
	}

	public final void release(int x, int y) {
		VikaTouch.needstoRedraw=true;
		/*try {
			if (y > topPanelH && y < DisplayUtils.height - bottomPanelH && uiItems != null && !dragging) {
				int yy = topPanelH;
				for (int i = 0; i < uiItems.length; i++) {
					try {
						int y1 = scrolled + yy;
						int y2 = y1 + uiItems[i].getDrawHeight();
						yy += uiItems[i].getDrawHeight();
						if (y > y1 && y < y2) {
							uiItems[i].tap(x, y - y1);
						}
					} catch (Exception e) {

					}
				}
			}

		} catch (ArrayIndexOutOfBoundsException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		try {
			if ((y > MainScreen.topPanelH) && (y < DisplayUtils.height - MainScreen.bottomPanelH)
					&& (this.uiItems != null) && (!this.dragging)) {
				int yy = MainScreen.topPanelH;
				for (int i = 0; i < this.uiItems.size(); i++) {
					try {
						int y1 = this.scrolled + yy;
						int y2 = y1 + ((PressableUIItem) uiItems.elementAt(i)).getDrawHeight();
						yy += ((PressableUIItem) uiItems.elementAt(i)).getDrawHeight();
						if ((y > y1) && (y < y2)) {
							((PressableUIItem) uiItems.elementAt(i)).tap(x, y - y1);
						}
					} catch (Exception localException1) {
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		//super.release(x, y);
	
		VikaTouch.needstoRedraw=true;
		super.release(x, y);
		VikaTouch.needstoRedraw=true;
	}

	public void settingSet(int setIndex, int var) {
		VikaTouch.needstoRedraw=true;
		Settings.setted = true;
		switch (setIndex) {
		case 0: {
			Settings.animateTransition = var == 1;
			break;
		}
		case 1: {
			Settings.cacheImages = var == 1;
			break;
		}
		case 2: {
			Settings.dontLoadAvas = var == 1;
			break;
		}
		case 3: {
			Settings.https = !(var == 0); // TODO real switching
			Settings.proxy = (var == 0);
			VikaTouch.OAUTH = ((var == 1) ? Settings.httpsOAuth : Settings.proxyOAuth);
			VikaTouch.API = ((var == 1) ? Settings.httpsApi : Settings.proxyApi);
			break;
		}
		case 4: {
			Settings.sensorMode = var;
			break;
		}
		case 5: {
			Settings.simpleListsLength = countVals[var];
			break;
		}
		case 6: {
			Settings.messagesPerLoad = countVals[var];
			break;
		}
		case 7: {
			Settings.msgRefreshRate = refreshVals[var];
			break;
		}
		case 9: {
			Settings.audioMode = (short) var;
			break;
		}
		case 10: {
			Settings.debugInfo = var == 1;
			break;
		}
		case 11: {
			Settings.telemetry = var == 1;
			break;
		}
		case 12: {
			Settings.sendLogs = var == 1;
			break;
		}
		case 13: {
			Settings.rtspMethod = var;
			break;
		}
		case 14: {
			Settings.symtube = var == 1;
			break;
		}
		case 15: {
			Settings.autoMarkAsRead = var == 1;
			break;
		}
		case 16: {
			Settings.loadMusicViaHttp = (byte) var;
			break;
		}
		case 17: {
			Settings.loadITunesCovers = var == 1;
			break;
		}
		case 18: {
			Settings.loadMusicWithKey = (byte) var;
			break;
		}
		case 19: {
			Settings.vibOnTouch = var == 1;
			break;
		}
		case 20: {
			Settings.dialogsLength = countVals[var];
			break;
		}
		case 21: {
			Settings.dialogsRefreshRate = (byte) var;
			break;
		}
		case 22: {
			Settings.notifmode = var;
			break;
		}
		case 24: {
			Settings.hideBottom = var == 1;
			break;
		}
		
		case 25: {
			Settings.refreshtimeout = refreshtimeouts[var];
			break;
		}
			
		case 60: {
			Settings.fpsLimit = fpsVals[var];
			break;
		}
		case 61: {
			Settings.doubleBufferization = var == 1;
			break;
		}
		case 62: {
			Settings.drawMaxPriority = var == 1;
			break;
		}
		case 63: {
			Settings.fastImageScaling = var == 0;
			break;
		} 
		case 64 : {
			Settings.musicpath = musicpathVals[var];
			break;
		}
		}
		initAllSettsList();
		// Settings.saveSettings();
		// а вариант "поменял настройку и закрыл приложение" не? Оно кст и не работало,
		// т.к. 14 команда давно не используется. И вообще, точно ли тот командИмпл
		// нужен, когда всё кругом на оптион айтемах с их IMenu...
	}

	public void onMenuItemPress(int i) {
		VikaTouch.needstoRedraw=true;
		switch (i) {
		case 0: {
			switchList(menuList);
			this.repaint();
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			break;
		}
		case -100: {
			switchList(systemList);
			this.repaint();
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			break;
		}
		case -101: {
			switchList(uiList);
			this.repaint();
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			break;
		}
		case -102: {
			switchList(mediaList);
			this.repaint();
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			break;
		}
		case -103: {
			switchList(specialAbilitiesList);
			this.repaint();
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			break;
		}
		case -104: {
			switchList(debugList);
			this.repaint();
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			break;
		}
		case -105: {
			switchList(msgList);
			this.repaint();
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			break;
		}
		case 99: {
			Settings.nightTheme = !Settings.nightTheme;
			Settings.switchLightTheme();
			VikaTouch.needstoRedraw=true;
			break;
		}
		
		case 98: {
			Settings.showpics = !Settings.showpics;
			VikaTouch.needstoRedraw=true;
			break;
		}
		
		case -1: {
			if (VikaTouch.accessToken != null && VikaTouch.accessToken != "") {
				VikaTouch.popup(new ConfirmBox(TextLocal.inst.get("settings.logout") + "?", null, new Runnable() {
					public void run() {
						try {
							VikaTouch.logout();
							VikaTouch.gc();
						} catch (Exception e) {

						}
						VikaTouch.needstoRedraw=true;
						VikaTouch.setDisplay(new LoginScreen(), -1);
					}
				}, null, TextLocal.inst.get("ok"), TextLocal.inst.get("cancel")));
			} else {

			}
			break;
		}
		case -2: {
			this.repaint();
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			break;
		}
		case -3: {
			VikaTouch.needstoRedraw=true;
			this.repaint();
			this.serviceRepaints();
			VikaTouch.popup(new ConfirmBox(TextLocal.inst.get("settings.reset"), null, new Runnable() {
				public void run() {
					Settings.loadSettings();
					Settings.setted = true;
				}
			}, null, TextLocal.inst.get("ok"), TextLocal.inst.get("cancel")));
			break;
		}
		case 21: {
			VikaTouch.needstoRedraw=true;
			// со строками сеттинг айтем пока не умеет
			OptionItem[] it = new OptionItem[4];
			it[0] = new OptionItem(this, "240", IconsManager.VIDEOS, 11, oneitemheight);
			it[1] = new OptionItem(this, "360", IconsManager.VIDEOS, 12, oneitemheight);
			it[2] = new OptionItem(this, "480", IconsManager.VIDEOS, 13, oneitemheight);
			it[3] = new OptionItem(this, "720", IconsManager.VIDEOS, 14, oneitemheight);
			
			VikaTouch.popup(new AutoContextMenu(it));
			VikaTouch.needstoRedraw=true;
			this.repaint();
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			break;
		}
		case 31: {
			VikaTouch.needstoRedraw=true;
			if (VikaTouch.about == null)
				VikaTouch.about = new AboutScreen();
			VikaTouch.setDisplay(VikaTouch.about, 1);
			this.repaint();
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			break;
		}
		case 23: {
			VikaTouch.needstoRedraw=true;
			OptionItem[] it = new OptionItem[7];
			it[0] = new OptionItem(this, "English", IconsManager.EDIT, 1, oneitemheight);
			it[1] = new OptionItem(this, "Русский", IconsManager.EDIT, 2, oneitemheight);
			it[2] = new OptionItem(this, "Español", IconsManager.EDIT, 3, oneitemheight);
			it[3] = new OptionItem(this, "Український", IconsManager.EDIT, 4, oneitemheight);
			it[4] = new OptionItem(this, "Беларускі", IconsManager.EDIT, 5, oneitemheight);
			it[5] = new OptionItem(this, "Tiếng Việt", IconsManager.EDIT, 6, oneitemheight);
			it[6] = new OptionItem(this, "Polszczyzna", IconsManager.EDIT, 7, oneitemheight);
			VikaTouch.needstoRedraw=true;
			VikaTouch.popup(new AutoContextMenu(it));
			VikaTouch.needstoRedraw=true;
			this.repaint();
			VikaTouch.needstoRedraw=true;
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			break;
		}
		case 50: {
			VikaTouch.needstoRedraw=true;
			OptionItem[] it = new OptionItem[8];
			it[2] = new OptionItem(this, "Россия (RU)", IconsManager.EDIT, 32, oneitemheight);
			it[0] = new OptionItem(this, "United States (US)", IconsManager.EDIT, 33, oneitemheight);
			it[1] = new OptionItem(this, "United Kingdom (UK)", IconsManager.EDIT, 34, oneitemheight);
			it[3] = new OptionItem(this, "España (ES)", IconsManager.EDIT, 35, oneitemheight);
			it[4] = new OptionItem(this, "Україна (UA)", IconsManager.EDIT, 36, oneitemheight);
			it[5] = new OptionItem(this, "Беларусь (BY)", IconsManager.EDIT, 37, oneitemheight);
			it[6] = new OptionItem(this, "Қазақстан (KZ)", IconsManager.EDIT, 38, oneitemheight);
			it[7] = new OptionItem(this, "Polska (PL)", IconsManager.EDIT, 39, oneitemheight);
			VikaTouch.popup(new AutoContextMenu(it));
			VikaTouch.needstoRedraw=true;
			break;
		}
		case 22: {
			VikaTouch.needstoRedraw=true;
			OptionItem[] it = new OptionItem[4];
			it[0] = new OptionItem(this, TextLocal.inst.get("settings.disabled"), IconsManager.EDIT, 0, oneitemheight);
			it[1] = new OptionItem(this, TextLocal.inst.get("settings.vibro"), IconsManager.EDIT, 1,
					oneitemheight);
			it[2] = new OptionItem(this, TextLocal.inst.get("settings.sound"), IconsManager.EDIT, 2,
					oneitemheight);
			it[3] = new OptionItem(this, TextLocal.inst.get("settings.alert"), IconsManager.EDIT, 4,
					oneitemheight);
			VikaTouch.popup(new AutoContextMenu(it));
			VikaTouch.needstoRedraw=true;
			this.repaint();
			VikaTouch.needstoRedraw=true;
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			break;
		}
		// 11-20 - разреши видео! Пока, потом я мб таки запихну это дело в SettingItem.
		}
		if (i >= 11 && i <= 19) {
			VikaTouch.needstoRedraw=true;
			int j = i - 11;
			String[] res = new String[] { "240", "360", "480", "720" };
			Settings.setted = true;
			Settings.videoResolution = res[j];
			VikaTouch.needstoRedraw=true;
			this.repaint();
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			// Settings.saveSettings();
		}
		if (i >= 1 && i <= 9) {
			VikaTouch.needstoRedraw=true;
			int j = i - 1;
			String[] res = new String[] { "english", "russian", "spanish", "ukrainian", "belarussian", "vietnamese", "polish"};
			Settings.setted = true;
			Settings.language = res[j];
			System.out.println(Settings.language);
			VikaTouch.needstoRedraw=true;
			this.repaint();
			VikaTouch.needstoRedraw=true;
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			/*
			 * VikaTouch.popup(new InfoPopup("Language was changed to "+res[j]
			 * +". The application must be restarted.", new Runnable() { public void run() {
			 * VikaTouch.appInst.destroyApp(false); } }, "Restart required", "Restart"));
			 */
			// Settings.saveSettings();
			//VikaTouch.popup(new InfoPopup("Settings will be applied after restart", null, "", "OK"));
		}
		if(i >= 32 && i <= 40) {
			VikaTouch.needstoRedraw=true;
			int j = i - 32;
			String[] res = new String[] { "RU", "US", "UK", "ES", "UA", "BY", "KZ", "PL"};
			Settings.setted = true;
			Settings.region = res[j];
			System.out.println(Settings.region);
			VikaTouch.needstoRedraw=true;
			this.repaint();
			VikaTouch.needstoRedraw=true;
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
		}

		initAllSettsList();
	}

	private void switchList(PressableUIItem[] l) {
		VikaTouch.needstoRedraw=true;
		this.repaint();
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
		if (l == this.menuList)
			hasBackButton = true;
		else
			hasBackButton = false;
		VikaTouch.needstoRedraw=true;
		try {
			VikaTouch.needstoRedraw=true;
			if (this.uiItems != null) {
				((PressableUIItem) uiItems.elementAt(currentItem)).setSelected(false);
			}	// точно упадёт на старте - списка то ещё нет.
		} catch (Throwable e) {
		}
		try {
			this.uiItems = new Vector(l.length);
			for(int i = 0; i < l.length; i++) {
				uiItems.addElement(l[i]);
			}
			this.itemsh = (58 + (oneitemheight + 4) * this.uiItems.size());
			this.itemsCount = ((short) this.uiItems.size());
			if (keysMode) {
				this.currentItem = 0;
				((PressableUIItem) uiItems.elementAt(0)).setSelected(true);
			}
			VikaTouch.needstoRedraw=true;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		VikaTouch.needstoRedraw=true;
		this.repaint();
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
	}

	public void onMenuItemOption(int i) {
		VikaTouch.needstoRedraw=true;
		this.repaint();
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;

	}

	public void onLeave() {
		VikaTouch.needstoRedraw=true;
		this.repaint();
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
		 Settings.saveSettings();
	}

}
