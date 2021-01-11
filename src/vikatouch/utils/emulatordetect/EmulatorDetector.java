package vikatouch.utils.emulatordetect;

import vikatouch.settings.Settings;

/**
 * @author Shinovon
 * 
 */
public class EmulatorDetector {
	public static int emulatorType;
	public static boolean isEmulator;
	public static boolean emulatorNotSupported;
	public static boolean supportsHttps;
	private static String version;

	public static final int EM_KEM = 1;

	public static final int EM_JBED = 18;

	public static final int EM_KEM_OR_J2L = 17;

	public static final int EM_J2L = 16;

	public static final int EM_SONYERICSSON_JAVA_SDK = 4;

	public static final int EM_S40_5_SDK = 5;

	public static final int EM_S40_6_SDK = 6;

	public static final int EM_EPOC_COMPATIBLE = 7;

	public static final int EM_MICROEMULATOR = 8;

	public static final int EM_MICROEMULATOR_V2 = 9;

	public static final int EM_PC = 10;

	public static final int EM_KEMMOD = 11;

	public static final int EM_JAVA_SDK = 12;

	public static final int EM_WTK = 13;

	public static final int EM_UNDEFINED = -1;

	public static final int EM_NOT_EMULATOR = 0;

	public static void checkForEmulator(String platform) {
		String osname = System.getProperty("os.name");
		String jvendor = System.getProperty("java.vendor");
		detect: {
			try {
				Class.forName("emulator.Emulator");
				isEmulator = true;
				emulatorType = EM_KEM;
				emulatorNotSupported = true;
				version = "v1.0.3";
				if (!platform.endsWith("/KEmulatorMod"))
					break detect;
			} catch (Throwable e) {
				
			}
			if(osname != null) {
				if(osname.indexOf("nux") != -1 || osname.startsWith("Windows")) {
					emulatorType = EM_PC;
					isEmulator = true;
				}
			}
			if(jvendor != null && jvendor.equalsIgnoreCase("The Android Project")) {
				isEmulator = true;
				emulatorType = EM_J2L;
				supportsHttps = true;
			} else if (platform.endsWith("/KEmulatorMod")) {
				emulatorNotSupported = false;
				isEmulator = true;
				emulatorType = EM_KEMMOD;
				supportsHttps = true;
				version = "v1-v3";
				if(System.getProperty("kemulator.notificationapi.version") != null) {
					version = "v5";
				}
				if(System.getProperty("fileconn.dir.memorycard") != null) {
					version = "v6";
				}
				if(System.getProperty("microedition.hostname") != null) {
					version = "v7";
				}
				if(System.getProperty("kemulator.mod.version") != null) {
					version = System.getProperty("kemulator.mod.version");
				}
			} else if (platform.indexOf("03.xx") >= 0) {
				isEmulator = true;
				emulatorType = EM_S40_5_SDK;
			} else if (platform.equalsIgnoreCase("Nokia 6233")) {
				supportsHttps = true;
				isEmulator = true;
				emulatorType = EM_J2L;
			} if (platform.equalsIgnoreCase("NOKIA_SERIES60")) {
				isEmulator = true;
				emulatorType = EM_KEM;
				emulatorNotSupported = true;
			} else if (platform.equalsIgnoreCase("NOKIA_SERIES40")) {
				isEmulator = true;
				emulatorType = EM_KEM;
				emulatorNotSupported = true;
			} else if (platform.equalsIgnoreCase("MicroEmulator-2.0")) {
				isEmulator = true;
				emulatorType = EM_MICROEMULATOR_V2;
				supportsHttps = true;
			} else if (platform.equalsIgnoreCase("SunMicrosystems_wtk")) {
				isEmulator = true;
				emulatorType = EM_WTK;
			} else if (platform.toLowerCase().startsWith("sonyericsson") && platform.toLowerCase().endsWith("/javasdk")) {
				isEmulator = true;
				emulatorType = EM_SONYERICSSON_JAVA_SDK;
			} else if (platform.toLowerCase().endsWith("/javasdk")) {
				isEmulator = true;
				emulatorType = EM_JAVA_SDK;
			} else if(platform.indexOf("Jbed-FastBCC") != -1) {
				isEmulator = true;
				emulatorType = EM_JBED;
			} else if (platform.equalsIgnoreCase("MicroEmulator")) {
				isEmulator = true;
				emulatorType = EM_MICROEMULATOR;
				emulatorNotSupported = true;
			} else if(!isEmulator) {
				if (platform.indexOf("Emulator") >= 0) {
					isEmulator = true;
					emulatorType = EM_UNDEFINED;
				} else if (platform.equalsIgnoreCase("j2me")) {
					isEmulator = true;
					emulatorType = EM_UNDEFINED;
				} else if (platform.indexOf(" ") >= 0) {
					isEmulator = true;
					emulatorType = EM_KEM_OR_J2L;
				} else {
					isEmulator = false;
					emulatorType = EM_NOT_EMULATOR;
				}
			}
			break detect;
		}
		Settings.setEmulatorSettings();
	}

	public static boolean isCompatible(int i) {
		switch (i) {
		case EM_NOT_EMULATOR:
			return true;

		case EM_UNDEFINED:
			return false;

		case EM_KEM:
			return false;

		case EM_SONYERICSSON_JAVA_SDK:
			return true;

		case EM_S40_5_SDK:
			return true;

		case EM_S40_6_SDK:
			return true;

		case EM_EPOC_COMPATIBLE:
			return false;

		case EM_MICROEMULATOR:
			return false;

		case EM_MICROEMULATOR_V2:
			return true;

		case EM_PC:
			return false;

		case EM_KEMMOD:
			return true;

		case EM_J2L:
			return true;

		case EM_KEM_OR_J2L:
			return true;

		case EM_JBED:
			return true;

		case EM_WTK:
			return true;
		}
		return false;
	}

	public static String getString(int i) {
		switch (i) {
		case 0:
			return "notemulator";

		case -1:
			return "unknown";

		case 1:
			return "kemulator";

		case 4:
			return "sonyericsson-java-sdk";

		case 5:
			return "s40v5sdk";

		case 6:
			return "s40v6sdk";

		case 7:
			return "epoc32emulator";

		case 8:
			return "microemu";

		case 9:
			return "microemu-v2";

		case 10:
			return "unknown";

		case 11:
			return "kemulator-mod" + (version != null ? "-" + version : "");
			
		case 12:
		case 13:
			return "wtk2";

		case 16:
			return "j2meloader";

		case 17:
			return "j2ml-or-kem";

		case 18:
			return "jbed";

		}
		return "unknown";
	}

}
