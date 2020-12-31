package vikatouch.utils.emulatordetect;

import vikatouch.settings.Settings;

public class EmulatorDetector {
	public static int emulatorType;
	public static boolean isEmulator;
	public static boolean emulatorNotSupported;
	public static boolean supportsHttps;

	public static final int EM_KEM = 1;

	public static final int EM_JBED = 18;

	public static final int EM_KEM_OR_J2L = 17;

	public static final int EM_J2L = 16;

	public static final int EM_SDK = 4;

	public static final int EM_S40_5_SDK = 5;

	public static final int EM_S40_6_SDK = 6;

	public static final int EM_EPOC_COMPATIBLE = 7;

	public static final int EM_MICROEMULATOR = 8;

	public static final int EM_MICROEMULATOR_V2 = 9;

	public static final int EM_PC = 10;

	public static final int EM_KEMMOD = 11;

	public static final int EM_WTK = 102;

	public static final int EM_UNDEFINED = -1;

	public static final int EM_NOT_EMULATOR = 0;

	public static void checkForEmulator(String platform) {
		String osname = System.getProperty("os.name");
		String jvendor = System.getProperty("java.vendor");
		detect: {
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
				isEmulator = true;
				emulatorType = EM_KEMMOD;
				supportsHttps = true;
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

		case EM_SDK:
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
			return false;

		case EM_PC:
			return false;

		case EM_KEMMOD:
			return true;

		case EM_J2L:
			return true;

		case EM_KEM_OR_J2L:
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
			return "nokiasdk";

		case 5:
			return "s40v5sdk";

		case 6:
			return "s40v6sdk";

		case 7:
			return "incompatible";

		case 8:
			return "microemu";

		case 9:
			return "microemu-v2";

		case 10:
			return "unknown";

		case 11:
			return "kemulator-mod";

		case 16:
			return "j2meloader";

		case 17:
			return "j2ml-or-kem";
		}
		return "unknown";
	}

}
