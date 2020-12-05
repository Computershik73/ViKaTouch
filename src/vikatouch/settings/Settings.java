package vikatouch.settings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.locale.LangObject;
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

	public static final String[] supportedLanguages = { "en_US", "en_UK", "ru_RU", "es_ES", "by_BY", "ua_UA" };

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

				} catch (Exception e) {

				}
				is.close();
				bais.close();
			}
			rs.closeRecordStore();
		} catch (Exception e) {

		}
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

	public static void loadDefaultSettings() {
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
		language = "ru_RU";
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
		dialogsRefreshRate = (byte) (isLiteOrSomething ? 0 : 2);
		notifmode = 2;

		// язык соотвествующий настройкам устройства
		try {
			final String lang = Settings.hasLanguage(System.getProperty("microedition.locale"));
			if (lang != null) {
				Settings.language = lang;
			}
		} catch (Exception e) {

		}

		if (isLiteOrSomething) {
			alerts = true;
			videoResolution = "240";
			proxy = true;
			// threaded = false;
		}
	}

	public static String hasLanguage(String l) {
		for (int i = 0; i < supportedLanguages.length; i++) {
			if (supportedLanguages[i].equalsIgnoreCase(VikaUtils.replace(l, "-", "_"))) {
				return supportedLanguages[i];
			}
		}
		return "en_US";
	}

	public static void setEmulatorSettings() {
		if (EmulatorDetector.isEmulator) {
			if (EmulatorDetector.emulatorType == EmulatorDetector.EM_KEM_OR_J2L) {
				Settings.sensorMode = Settings.SENSOR_J2MELOADER;
			}

			if (EmulatorDetector.emulatorType == EmulatorDetector.EM_KEMMOD) {
				Settings.sensorMode = Settings.SENSOR_KEMULATOR;
			}
		}
	}

}
