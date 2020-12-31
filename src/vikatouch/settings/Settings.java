package vikatouch.settings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import tube42.lib.imagelib.ImageFxUtils;
import vikatouch.VikaTouch;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.emulatordetect.EmulatorDetector;
import vikatouch.utils.error.ErrorCodes;

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

	public static int messagesPerLoad;

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
	public static final int AUDIO_VLC = 6; // алё, ну чо там с кемулятором
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
	public static int fpsLimit = 60;

	public static boolean doubleBufferization = false;
	
	public static boolean drawMaxPriority = false;

	public static boolean fastImageScaling;

	public static boolean oldlcduiFm;

	static {
		loadDefaultSettings();
	}

	public static void loadSettings() {
		try {
			RecordStore rs = RecordStore.openRecordStore("vikatouchsettings", true);
			if (rs.getNumRecords() > 0) {
				setted = true;

				final ByteArrayInputStream bais = new ByteArrayInputStream(rs.getRecord(1));
				final DataInputStream is = new DataInputStream(bais);

				try {
					animateTransition = is.readBoolean();
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
					doubleBufferization = is.readBoolean();
					drawMaxPriority = is.readBoolean();
					fastImageScaling = is.readBoolean();
					//2.8.14
					//тут должна быть настройка с старым фм, но я передумал.
					//2.8.15
					nightTheme = is.readBoolean();
				} catch (Exception e) {

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
				} catch (Exception e) {

				}
				RecordStore rs = RecordStore.openRecordStore("vikatouchsettings", true);
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				final DataOutputStream os = new DataOutputStream(baos);

				os.writeBoolean(animateTransition);
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

				final byte[] b = baos.toByteArray();
				rs.addRecord(b, 0, b.length);
				os.close();
				baos.close();
				rs.closeRecordStore();
			} catch (Exception e) {
				VikaTouch.error(e, ErrorCodes.SETSSAVE);
			}
		}
	}
	
	public static void switchLightTheme() {
		try {
			if(nightTheme) {
				IconsManager.ac = ImageFxUtils.transformARGB(IconsManager.ac, 0, -255, -255, -255);
			} else {
				IconsManager.ac = Image.createImage("/ava.png");
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void loadDefaultSettings() {
		nightTheme = false;
		setted = false;
		animateTransition = false;
		proxy = false;
		https = false;
		debugInfo = false;
		proxyApi = xtrafrancyzApi;
		proxyOAuth = xtrafrancyzOAuth;
		sensorMode = SENSOR_OK;
		simpleListsLength = 30;
		messagesPerLoad = 30;
		dialogsLength = 20;
		videoResolution = Math.min(DisplayUtils.width, DisplayUtils.height) >= 360 ? "360" : "240";
		language = "english";
		cacheImages = true;
		dontLoadAvas = false;
		sendLogs = true;
		autoMarkAsRead = true;
		isLiteOrSomething = VikaTouch.isS40();
		threaded = true;
		audioMode = AUDIO_LOADANDPLAY;
		loadMusicViaHttp = 0;
		loadMusicWithKey = 0;
		imageTrimming = false;
		playerVolume = 100;
		loadITunesCovers = true;
		autoMarkAsRead = true;
		fullscreen = true;
		vibOnTouch = false;
		dialogsRefreshRate = (byte) (isLiteOrSomething ? 2 : 0);
		notifmode = 2;
		hideBottom = false;
		region = "US";

		// язык соотвествующий настройкам устройства
		try {
			String supportedLanguages[] = {"en_US",   "en_UK",   "ru_RU",   "es_ES",   "by_BY",       "ua_UA",     "kk_KZ"};
			language = Settings.setLang(System.getProperty("microedition.locale"), supportedLanguages, new String[] {"english", "english", "russian", "spanish", "belarussian", "ukrainian", "russian"});
			region = Settings.setRegion(System.getProperty("microedition.locale"), supportedLanguages, new String[] {"US",      "UK",      "RU",      "ES",      "BY",          "UA",        "KZ"});
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
			String d[] = {"Nokia203", "Nokia305", "Nokia308", "Nokia311"};
			for(int i = 0; i < d.length; i++) {
				if(VikaTouch.mobilePlatform.startsWith(d[i])) {
					sensorMode = SENSOR_RESISTIVE;
					break;
				}
			}
		} catch (Exception e) {

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

			if (EmulatorDetector.emulatorType == EmulatorDetector.EM_KEMMOD) {
				Settings.sensorMode = Settings.SENSOR_KEMULATOR;
			}
		}
	}

}
