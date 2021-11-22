package vikatouch.settings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import tube42.lib.imagelib.ImageFxUtils;
import vikatouch.VikaTouch;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.emulatordetect.EmulatorDetector;
import vikatouch.utils.error.ErrorCodes;

/**
 * @author Shinovon
 * 
 */
public class Settings {

	public static boolean setted = false;

	public static boolean animateTransition;

	public static boolean proxy;

	public static boolean https;

	public static String proxyApi;

	public static String proxyOAuth;

	public static int sensorMode;

	public static boolean debugInfo;

	public static int simpleListsLength;

	public static int messagesPerLoad = 10;

	public static String language;

	public static String videoResolution; // 240 360 480 720

	public static boolean cacheImages;

	public static boolean dontLoadAvas;

	public static int msgRefreshRate = 5;

	public static byte dialogsRefreshRate = 0;

	public static final int[] dialogsRefreshRates = new int[] { 0, 5, 10, 20, 30 };

	public static int dialogsLength = 15;

	public static boolean sendLogs;

	public static short audioMode = 0; // добавь пж сохранение -ок

	public static int rtspMethod = 0;

	public static boolean symtube;

	public static boolean telemetry = true;

	public static final boolean slideAnim = true;

	public static boolean imageTrimming;

	public static byte loadMusicViaHttp;

	public static byte loadMusicWithKey;

	public static byte playerVolume;

	public static boolean loadITunesCovers;

	public static boolean autoMarkAsRead = true;

	public static boolean fullscreen = true;

	public static boolean vibOnTouch = false;

	public static byte storage;

	public static boolean hideBottom = false;
	
	public static boolean nightTheme = false;
	
	public static boolean musicviavikaserver;
	
	public static JSONObject settingsjson;

	// Не нуждаются сохранению (м.б передумаем)
	public static boolean threaded;
	public static long memoryClearCache = 500;
	public static boolean dontBack;
	public static boolean isLiteOrSomething;
	public static boolean alerts;

	// константы
	public final static String xtrafrancyzApi = "http://vk-api-proxy.xtrafrancyz.net:80";

	public final static String xtrafrancyzOAuth = "http://vk-oauth-proxy.xtrafrancyz.net";

	public final static String httpsApi = "https://api.vk.com:443";

	public final static String httpsOAuth = "https://oauth.vk.com";
	
	public final static String diskC = System.getProperty("fileconn.dir.music");
	
	public final static String diskE = "file:///E:/Sounds/";
	
	public final static String diskF = "file:///F:/Sounds/";
	
	public final static String diskEMMC = System.getProperty("fileconn.dir.memorycard")+"Sounds/";
	
	public final static String diskMaemoDocs = "file:///"+"MyDocs/.sounds/";
	
	
	

	public static final byte MUSIC_FOLDER = 0;
	public static final byte PRIVATE_FOLDER = 1;
	public static final byte DISK_C = 2;
	public static final byte DISK_D = 3;
	public static final byte DISK_E = 4;
	public static final byte DISK_F = 5;

	public static final byte AUDIO_DEFAULT = 0;
	public static final byte AUDIO_HTTP = 1;
	public static final byte AUDIO_HTTPS = 2;
	public static final byte AUDIO_EXTRA = 1;
	public static final byte AUDIO_NOEXTRA = 2;

	public static final int SENSOR_OK = 0;
	public static final int SENSOR_J2MELOADER = 1;
	public static final int SENSOR_RESISTIVE = 2;
	public static final int SENSOR_KEMULATOR = 3;

	public static final int AUDIO_PLAYONLINE = 0; // даём плееру урл и пусть играет.
	public static final int AUDIO_CACHEANDPLAY = 1; // подключаем поток и даём плееру его.
	public static final int AUDIO_LOADANDPLAY = 2; // скачиваем и даём плееру файл.
	public static final int AUDIO_LOADANDOPEN = 3; // скачиваем и просим систему открыть.
	public static final int AUDIO_LOADANDSYSTEMPLAY = 4; // скачиваем и тормошим плеер симбы методом Ильи.
	public static final int AUDIO_SYSTEMPLAYER = 5; // только не скачивая.
	public static final int AUDIO_VLC = 6; // алё, ну чо там с кемулятором // UPD: норм все
	public static final int AUDIO_DOWNLOAD = 7; // браузер
	// теперь точно всё, осталось выяснить ГДЕ ВЛЦ и что из этого умеет СЕшка.
	/*
	 * Объясняю нахера столько вариков. Понаблюдав чутка за вашей еблей с музыкой и
	 * за "работой" плеера вк4ме я пришёл к простому выводу - автовыбор по модели
	 * катит НЕ ВСЕГДА. Поэтому в установке дефолтных настроек надо будет подбирать
	 * автоматом, но давать юзеру возможность выбрать, как пинать эту бедную музыку.
	 */
	public static int notifmode = 0;

	public static String region;
	
	// 100/60/30/15
	public static int fpsLimit = 30;

	public static boolean doubleBufferization = true;
	
	public static boolean drawMaxPriority = true;

	public static boolean fastImageScaling = true;

	public static boolean oldlcduiFm;

	public static boolean qualityNotif;

	public static boolean showpics;
	
	public static String musicpath;

	public static int mpc;

	static {
		loadSettings();
	}

	public static void loadSettings() {
		try {
			RecordStore rs = RecordStore.openRecordStore("vikatouchsettings", true);
			if (rs.getNumRecords() > 0) {
				
					byte[] aa = rs.enumerateRecords(null, null, true).nextRecord();
					
					
					
				
				
				setted = true;
				final ByteArrayInputStream bais = new ByteArrayInputStream(aa);
				//final ByteArrayInputStream bais = new ByteArrayInputStream(rs.getRecord(0));
				final DataInputStream is = new DataInputStream(bais);
				
				try {
					String settingsstring = is.readUTF();
					
					settingsjson = new JSONObject(settingsstring);
					//VikaUtils.logToFile(settingsstring);
					isLiteOrSomething = false;//VikaTouch.isS40();
					threaded = true;
					
					if (settingsjson.has("animateTransition")) {
					animateTransition = settingsjson.optBoolean("animateTransition");
					} else {
						animateTransition=false;
					}
					
					if (settingsjson.has("proxy")) {
						proxy = settingsjson.optBoolean("proxy");
						}
					
					if (settingsjson.has("https")) {
						https = settingsjson.optBoolean("https");
						} 
					
					if (settingsjson.has("debugInfo")) {
						debugInfo = settingsjson.optBoolean("debugInfo");
						} else {
							debugInfo=false;
						}
					
					if (settingsjson.has("proxyApi")) {
						proxyApi = settingsjson.optString("proxyApi");
						} else {
							proxyApi=xtrafrancyzApi;
						}
					
					if (settingsjson.has("proxyOAuth")) {
						proxyOAuth = settingsjson.optString("proxyOAuth");
						} else {
							proxyOAuth=xtrafrancyzOAuth;
						}
					
					if (settingsjson.has("sensorMode")) {
						sensorMode = settingsjson.optInt("sensorMode");
						} else {
							sensorMode=SENSOR_OK;
						}
					
					if (settingsjson.has("simpleListsLength")) {
						simpleListsLength = settingsjson.optInt("simpleListsLength");
						} else {
							simpleListsLength = 30;
						}
					
					if (settingsjson.has("messagesPerLoad")) {
						messagesPerLoad = settingsjson.optInt("messagesPerLoad");
						} else {
							messagesPerLoad=10;
						}
					
					if (settingsjson.has("dialogsLength")) {
						dialogsLength = settingsjson.optInt("dialogsLength");
						} else {
							dialogsLength=20;
						}
					
					if (settingsjson.has("videoResolution")) {
						videoResolution = settingsjson.optString("videoResolution");
						} else {
							videoResolution = Math.min(DisplayUtils.width, DisplayUtils.height) >= 360 ? "360" : "240";
						}
					
					if (settingsjson.has("language")) {
						language = settingsjson.optString("language");
						} else {
							language = "english";
						}
					
					if (settingsjson.has("cacheImages")) {
						cacheImages = settingsjson.optBoolean("cacheImages");
						} else {
							cacheImages = true;
						}
					
					if (settingsjson.has("dontLoadAvas")) {
						dontLoadAvas = settingsjson.optBoolean("dontLoadAvas");
						} else {
							dontLoadAvas = false;
						}
					
					if (settingsjson.has("sendLogs")) {
						sendLogs = settingsjson.optBoolean("sendLogs");
						} else {
							sendLogs = true;
						}
					
					if (settingsjson.has("autoMarkAsRead")) {
						autoMarkAsRead = settingsjson.optBoolean("autoMarkAsRead");
						} else {
							autoMarkAsRead = true;
						}
					
			
					
					if (settingsjson.has("fullscreen")) {
						fullscreen = settingsjson.optBoolean("fullscreen");
						} else {
							fullscreen = true;
						}
					
					if (settingsjson.has("vibOnTouch")) {
						vibOnTouch = settingsjson.optBoolean("vibOnTouch");
						} else {
							vibOnTouch = false;
						}
					
					if (settingsjson.has("dialogsRefreshRate")) {
						dialogsRefreshRate = (byte) settingsjson.optInt("dialogsRefreshRate");
						} else {
							dialogsRefreshRate = 10;
						}
					
					if (settingsjson.has("audioMode")) {
						audioMode = (short) settingsjson.optInt("audioMode");
						} else {
							audioMode = AUDIO_LOADANDPLAY;
						}
					
					if (settingsjson.has("loadMusicWithKey")) {
						loadMusicWithKey = (byte) settingsjson.optInt("loadMusicWithKey");
						} else {
							loadMusicWithKey = 1;
						}
					
					if (settingsjson.has("imageTrimming")) {
						imageTrimming = settingsjson.optBoolean("imageTrimming");
						} else {
							imageTrimming = false;
						}
					
					if (settingsjson.has("playerVolume")) {
						playerVolume = (byte) settingsjson.optInt("playerVolume");
						} else {
							playerVolume = 100;
						}
					
					if (settingsjson.has("loadITunesCovers")) {
						loadITunesCovers = settingsjson.optBoolean("loadITunesCovers");
						} else {
							loadITunesCovers = true;
						}
					
					
				
					
					if (settingsjson.has("playerVolume")) {
						playerVolume = (byte) settingsjson.optInt("playerVolume");
						} else {
							playerVolume = 100;
						}
					
					
					if (settingsjson.has("notifmode")) {
						notifmode = settingsjson.optInt("notifmode");
						} else {
							notifmode = 2;
						}
					
					
					if (settingsjson.has("hideBottom")) {
						hideBottom = settingsjson.optBoolean("hideBottom");
						} else {
							hideBottom = false;
						}
					
					
					if (settingsjson.has("region")) {
						region = settingsjson.optString("region");
						} else {
							region = "US";
						}
					
					if (settingsjson.has("musicviavikaserver")) {
						musicviavikaserver = settingsjson.optBoolean("musicviavikaserver");
						} else {
							musicviavikaserver = false;
						}
					
					
					if (settingsjson.has("fpsLimit")) {
						fpsLimit = settingsjson.optInt("fpsLimit");
						} else {
							if (VikaTouch.isS40()) {
								fpsLimit = 15;
							}
						}
					
					if (settingsjson.has("muscount")) {
						VikaTouch.muscount = settingsjson.optInt("muscount");
						} else {
							if (VikaTouch.isS40()) {
								VikaTouch.muscount=30;
							} else {
								VikaTouch.muscount=200;
							}
						}
					
					if (settingsjson.has("nightTheme")) {
						nightTheme = settingsjson.optBoolean("nightTheme");
						} else {
							nightTheme = false;
						}
					
					if (settingsjson.has("showpics")) {
						showpics = settingsjson.optBoolean("showpics");
						} else {
							showpics = true;
						}
					
					if (settingsjson.has("musicpath")) {
						musicpath = settingsjson.optString("musicpath");
						//Settings.mpc = 
						} else {
							musicpath =  System.getProperty("fileconn.dir.music");
							if ((VikaTouch.mobilePlatform.indexOf("NokiaN91")>-1) || (VikaTouch.mobilePlatform.indexOf("NokiaN8")>-1) || (VikaTouch.mobilePlatform.indexOf("Nokia808")>-1)) {
								musicpath="file:///E:/Sounds/";
							}
							String jver = System.getProperty("java.version");
							if (jver == null) {
								jver = "-";
							} else {
							if (jver.indexOf("phoneme")<0) {
								if (musicpath == null)
									musicpath = System.getProperty("fileconn.dir.photos");
								if (musicpath == null)
									musicpath = "C:/Users/ilmoh/Downloads/";
								} else {
									musicpath = "file:///"+"MyDocs/.sounds/";
								}
							}
							
						}
					//dialogsRefreshRate = (byte) (isLiteOrSomething ? 2 : 0);
					
					//musicviavikaserver = false;
					/*if (VikaTouch.isS40()) {
						fpsLimit = 15;
						VikaTouch.muscount=30;
						
						https = false;
						}
					else {
						VikaTouch.muscount=200;
						if (VikaTouch.supportsHttps()) {
							https = true;
						}
					}*/
					if (!(VikaTouch.supportsHttps())) {
						loadMusicViaHttp = Settings.AUDIO_HTTP;
					}
					
					String x = System.getProperty("microedition.locale");
					// язык соотвествующий настройкам устройства
					try {
						String supportedLanguages[] = {"en_US",   "en_UK",   "ru_RU",   "es_ES",   "by_BY",       "ua_UA",     "kk_KZ", "pl_PL"};
						language = Settings.setLang(x, supportedLanguages, new String[] {"english", "english", "russian", "spanish", "belarussian", "ukrainian", "russian", "polish"});
						region = Settings.setRegion(x, supportedLanguages, new String[] {"US",      "UK",      "RU",      "ES",      "BY",          "UA",        "KZ",         "PL"});
					} catch (Exception e) {

					}

					if (isLiteOrSomething) {
						alerts = true;
						videoResolution = "240";
						proxy = true;
						https = false;
						notifmode = 4;
						// threaded = false;
					}
					
					doubleBufferization = true;
					drawMaxPriority = true;
					
					// настройки для резистивок (аши, тачи с клавами и т.д)
					try {
						String d[] = {"Nokia202", "Nokia203", "Nokia300", "Nokia305", "Nokia306", "Nokia308", "Nokia309", "Nokia310", "Nokia311"};
						for(int i = 0; i < d.length; i++) {
							if(VikaTouch.mobilePlatform.startsWith(d[i])) {
								sensorMode = SENSOR_RESISTIVE;
								break;
							}
						}
					} catch (Exception e) {

					}
					

				
					
					
					/*animateTransition = is.readBoolean();
					proxy = is.readBoolean();
					https = is.readBoolean();
					
					debugInfo = is.readBoolean();
					proxyApi = is.readUTF();
					proxyOAuth = is.readUTF();
					sensorMode = is.readShort();
					simpleListsLength = is.readShort();
					messagesPerLoad = is.readShort();
					videoResolution = is.readUTF();
					language = is.readUTF();
					dontLoadAvas = is.readBoolean();
					audioMode = is.readShort();
					rtspMethod = is.readShort();
					symtube = is.readBoolean();
					msgRefreshRate = is.readShort();
					// 2.8.5
					storage = is.readByte();
					imageTrimming = is.readBoolean();
					autoMarkAsRead = is.readBoolean();
					playerVolume = is.readByte();
					loadMusicViaHttp = is.readByte();
					loadITunesCovers = is.readBoolean();
					loadMusicWithKey = is.readByte();
					// 2.8.6
					vibOnTouch = is.readBoolean();
					fullscreen = is.readBoolean();
					// 2.8.7
					dialogsRefreshRate = is.readByte();
					dialogsLength = is.readInt();
					// 2.8.10
					notifmode = is.readInt();
					// 2.8.12
					hideBottom = is.readBoolean();
					region = is.readUTF();
					// 2.8.13
					fpsLimit = is.readShort();
					if (VikaTouch.isS40()) {
					fpsLimit = 15;
					}
					
					doubleBufferization = is.readBoolean();
					drawMaxPriority = is.readBoolean();
					fastImageScaling = is.readBoolean();
					//2.8.14
					//тут должна быть настройка с старым фм, но я передумал.
					//2.8.15
					nightTheme = is.readBoolean();
					//2.9.1
					sendLogs = is.readBoolean();
					//2.9.3
					musicviavikaserver = is.readBoolean();*/
					
				} catch (Exception e) {
					//VikaTouch.error(e, ErrorCodes.SETSSAVE);
					VikaTouch.sendLog(e.getMessage());
					//musicviavikaserver = false;
				}
				is.close();
				bais.close();
			}
			rs.closeRecordStore();
		} catch (Exception e) {

		}
		
		try {
			if(isOldLang(language)) {
				String x = language;
				String supportedLanguages[] = {"en_US",   "en_UK",   "ru_RU",   "es_ES",   "by_BY",       "ua_UA"};
				language = Settings.setLang(x, supportedLanguages, new String[] {"english", "english", "russian", "spanish", "belarussian", "ukrainian", "russian"});
				region = Settings.setRegion(x, supportedLanguages, new String[] {"US",      "UK",      "RU",      "ES",      "BY",          "UA",        "KZ"});
			}
		} catch (Exception e) {

		}
		if(fpsLimit <= 0) {
			fpsLimit = 60;
		}
		
		switchLightTheme();
	}

	public static void saveSettings() {
		if (setted) {
			try {
				try {
					RecordStore.deleteRecordStore("vikatouchsettings");
				} catch (Throwable e) {

				}
				RecordStore rs = RecordStore.openRecordStore("vikatouchsettings", true);
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				final DataOutputStream os = new DataOutputStream(baos);
				
				settingsjson = new JSONObject();
				
				settingsjson.put("animateTransition", animateTransition);
				settingsjson.put("proxy", proxy);
				settingsjson.put("https", https);
				settingsjson.put("debugInfo", debugInfo);
				settingsjson.put("proxyApi", proxyApi);
				settingsjson.put("proxyOAuth", proxyOAuth);
				settingsjson.put("sensorMode", sensorMode);
				settingsjson.put("simpleListsLength", simpleListsLength);
				settingsjson.put("messagesPerLoad", messagesPerLoad);
				settingsjson.put("videoResolution", videoResolution);
				settingsjson.put("language", language);
				settingsjson.put("dontLoadAvas", dontLoadAvas);
				settingsjson.put("audioMode", audioMode);
				settingsjson.put("rtspMethod", rtspMethod);
				settingsjson.put("symtube", symtube);
				settingsjson.put("msgRefreshRate", msgRefreshRate);
				settingsjson.put("storage", storage);
				settingsjson.put("imageTrimming", imageTrimming);
				settingsjson.put("autoMarkAsRead", autoMarkAsRead);
				settingsjson.put("playerVolume", playerVolume);
				settingsjson.put("loadMusicViaHttp", loadMusicViaHttp);
				settingsjson.put("loadITunesCovers", loadITunesCovers);
				settingsjson.put("loadMusicWithKey", loadMusicWithKey);
				settingsjson.put("vibOnTouch", vibOnTouch);
				settingsjson.put("fullscreen", fullscreen);
				settingsjson.put("dialogsRefreshRate", dialogsRefreshRate);
				settingsjson.put("dialogsLength", dialogsLength);
				settingsjson.put("notifmode", notifmode);
				settingsjson.put("hideBottom", hideBottom);
				settingsjson.put("region", region);
				settingsjson.put("fpsLimit", fpsLimit);
				settingsjson.put("doubleBufferization", doubleBufferization);
				settingsjson.put("drawMaxPriority", drawMaxPriority);
				settingsjson.put("fastImageScaling", fastImageScaling);
				settingsjson.put("nightTheme", nightTheme);
				settingsjson.put("sendLogs", sendLogs);
				settingsjson.put("musicviavikaserver", musicviavikaserver);
				settingsjson.put("showpics", showpics);
				settingsjson.put("musicpath", musicpath);
				
				//VikaUtils.logToFile(settingsjson.toString());
				os.writeUTF(settingsjson.toString());
				//VikaTouch.sendLog(settingsjson.toString());
				
				
				
				
				/*os.writeBoolean(animateTransition);
				os.writeBoolean(proxy);
				os.writeBoolean(https);
				os.writeBoolean(debugInfo);
				os.writeUTF(proxyApi);
				os.writeUTF(proxyOAuth);
				os.writeShort(sensorMode);
				os.writeShort(simpleListsLength);
				os.writeShort(messagesPerLoad);
				os.writeUTF(videoResolution);
				os.writeUTF(language);
				os.writeBoolean(dontLoadAvas);
				os.writeShort(audioMode);
				os.writeShort(rtspMethod);
				os.writeBoolean(symtube);
				os.writeShort(msgRefreshRate);
				// 2.8.5
				os.writeByte(storage);
				os.writeBoolean(imageTrimming);
				os.writeBoolean(autoMarkAsRead);
				os.writeByte(playerVolume);
				os.writeByte(loadMusicViaHttp);
				os.writeBoolean(loadITunesCovers);
				os.writeByte(loadMusicWithKey);
				// 2.8.6
				os.writeBoolean(vibOnTouch);
				os.writeBoolean(fullscreen);
				// 2.8.7
				os.writeByte(dialogsRefreshRate);
				os.writeInt(dialogsLength);
				// 2.8.10
				os.writeInt(notifmode);
				// 2.8.12
				os.writeBoolean(hideBottom);
				os.writeUTF(region);
				// 2.8.13
				os.writeShort(fpsLimit);
				os.writeBoolean(doubleBufferization);
				os.writeBoolean(drawMaxPriority);
				os.writeBoolean(fastImageScaling);
				//2.8.14
				//тут должна быть настройка с старым фм, но я передумал.
				//2.8.15
				os.writeBoolean(nightTheme);
				//2.9.1
				os.writeBoolean(sendLogs);
				//2.9.3
				os.writeBoolean(musicviavikaserver);*/
				
				byte[] b = baos.toByteArray();
				rs.addRecord(b, 0, b.length);
				os.close();
				baos.close();
				rs.closeRecordStore();
			} catch (Exception e) {
				//VikaTouch.error(e, ErrorCodes.SETSSAVE);
			}
		}
	}
	
	public static void switchLightTheme() {
		try {
			if(nightTheme) {
				IconsManager.logoImg = Image.createImage("/vikaheadnight2.png");
				IconsManager.acs = ImageFxUtils.transformARGB(Image.createImage("/ava.png"), 0, -255 + 30, -255 + 30, -255 + 30);
				IconsManager.ac = ImageFxUtils.transformARGB(Image.createImage("/ava.png"), 0, -255, -255, -255);
			} else {
				IconsManager.logoImg = Image.createImage("/vikahead.png");
				IconsManager.ac = Image.createImage("/ava.png");
				IconsManager.acs = Image.createImage("/avas.png");
			}
			IconsManager.onlineimg = Image.createImage("/ic_online.png");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void loadDefaultSettings() {
		String x = System.getProperty("microedition.locale");
		nightTheme = false;
		setted = false;
		animateTransition = false;
		//proxy = true;
		//https = false;
		debugInfo = false;
		proxyApi = xtrafrancyzApi;
		proxyOAuth = xtrafrancyzOAuth;
		sensorMode = SENSOR_OK;
		simpleListsLength = 10;
		messagesPerLoad = 10;
		dialogsLength = 10;
		videoResolution = Math.min(DisplayUtils.width, DisplayUtils.height) >= 360 ? "360" : "240";
		language = "english";
		cacheImages = true;
		dontLoadAvas = false;
		sendLogs = true;
		autoMarkAsRead = true;
		isLiteOrSomething = false;
				//VikaTouch.isS40();
		threaded = true;
		audioMode = AUDIO_LOADANDPLAY;
		
		loadMusicWithKey = 1;
		imageTrimming = false;
		playerVolume = 100;
		loadITunesCovers = true;
		autoMarkAsRead = true;
		fullscreen = true;
		vibOnTouch = false;
		dialogsRefreshRate = (byte) (isLiteOrSomething ? 2 : 1);
		notifmode = 2;
		hideBottom = false;
		region = "US";
		musicviavikaserver = false;
		showpics = true;
		
		musicpath =  System.getProperty("fileconn.dir.music");
		if ((VikaTouch.mobilePlatform.indexOf("NokiaN91")>-1) || (VikaTouch.mobilePlatform.indexOf("NokiaN8")>-1) || (VikaTouch.mobilePlatform.indexOf("Nokia808")>-1)) {
			musicpath="file:///E:/Sounds/";
		}
		String jver = System.getProperty("java.version");
		if (jver == null) {
			jver = "-";
		} else {
		if (jver.indexOf("phoneme")<0) {
			if (musicpath == null)
				musicpath = System.getProperty("fileconn.dir.photos");
			if (musicpath == null)
				musicpath = "C:/Users/ilmoh/Downloads/";
			} else {
				musicpath = "file:///"+"MyDocs/.sounds/";
			}
		}
		
		//musicviavikaserver = false;
		if (VikaTouch.isS40()) {
			fpsLimit = 15;
			VikaTouch.muscount=30;
			
			https = false;
			proxy = true;
			}
		else {
			VikaTouch.muscount=200;
			if (VikaTouch.supportsHttps()) {
				https = true;
				proxy = false;
			}
		}
		if (!(VikaTouch.supportsHttps())) {
			loadMusicViaHttp = Settings.AUDIO_HTTP;
		}
		
		
		// язык соотвествующий настройкам устройства
		try {
			String supportedLanguages[] = {"en_US",   "en_UK",   "ru_RU",   "es_ES",   "by_BY",       "ua_UA",     "kk_KZ", "pl_PL"};
			language = Settings.setLang(x, supportedLanguages, new String[] {"english", "english", "russian", "spanish", "belarussian", "ukrainian", "russian", "polish"});
			region = Settings.setRegion(x, supportedLanguages, new String[] {"US",      "UK",      "RU",      "ES",      "BY",          "UA",        "KZ",         "PL"});
		} catch (Exception e) {

		}

		if (isLiteOrSomething) {
			alerts = true;
			videoResolution = "240";
			proxy = true;
			https = false;
			notifmode = 4;
			// threaded = false;
		}
		
		// настройки для резистивок (аши, тачи с клавами и т.д)
		try {
			String d[] = {"Nokia202", "Nokia203", "Nokia300", "Nokia305", "Nokia306", "Nokia308", "Nokia309", "Nokia310", "Nokia311"};
			for(int i = 0; i < d.length; i++) {
				if(VikaTouch.mobilePlatform.startsWith(d[i])) {
					sensorMode = SENSOR_RESISTIVE;
					break;
				}
			}
		} catch (Exception e) {

		}
		

		try {
			setAudioModeForDevice(VikaTouch.mobilePlatform);
		} catch (Exception e) {

		}
	}
	
	private static void setAudioModeForDevice(String s) throws Exception {
		if(s.indexOf("sw_platform_version=5.3") > -1) {
			
		}
	}

	private static String setRegion(String l, String[] supportedLanguages, String[] regions) {
		for (int i = 0; i < supportedLanguages.length; i++) {
			if (supportedLanguages[i].equalsIgnoreCase(VikaUtils.replace(l, "-", "_"))) {
				return regions[i];
			}
		}
		return region;
	}

	private static String setLang(String l, String[] supportedLanguages, String[] langs) {
		for (int i = 0; i < supportedLanguages.length; i++) {
			if (supportedLanguages[i].equalsIgnoreCase(VikaUtils.replace(l, "-", "_"))) {
				return langs[i];
			}
		}
		return language;
	}

	public static boolean isOldLang(String l) {
		return l.length() == 5 && l.indexOf("_") == 2;
	}

	public static void setEmulatorSettings() {
		if (EmulatorDetector.isEmulator) {
			if (EmulatorDetector.emulatorType == EmulatorDetector.EM_J2L) {
				Settings.sensorMode = Settings.SENSOR_J2MELOADER;
				Settings.proxy = false;
				Settings.https = true;
			}
			
			if (EmulatorDetector.emulatorType == EmulatorDetector.EM_KEM_OR_J2L) {
				Settings.sensorMode = Settings.SENSOR_J2MELOADER;
			}

			if (EmulatorDetector.emulatorType == EmulatorDetector.EM_KEM) {
				Settings.sensorMode = Settings.SENSOR_KEMULATOR;
				Settings.audioMode = Settings.AUDIO_SYSTEMPLAYER;
			}

			if (EmulatorDetector.emulatorType == EmulatorDetector.EM_KEMNNMOD) {
				Settings.sensorMode = Settings.SENSOR_KEMULATOR;
				Settings.audioMode = Settings.AUDIO_VLC;
			}
		}
	}

}
