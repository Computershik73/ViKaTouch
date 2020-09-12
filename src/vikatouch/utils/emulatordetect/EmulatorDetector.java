package vikatouch.utils.emulatordetect;

import vikatouch.settings.Settings;

public class EmulatorDetector
{
	public static int emulatorType;
	public static boolean isEmulator;
	public static boolean emulatorNotSupported = false;
	
	public static final int EM_KEM = 1;
	
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
	
	public static final int EM_UNDEFINED = -1;
	
	public static final int EM_NOT_EMULATOR = 0;

	public static void checkForEmulator(String platform)
	{
		if(platform.indexOf("03.xx") >= 0)
		{
			isEmulator = true;
			emulatorType = EM_S40_5_SDK;
		}
		else if(platform.equalsIgnoreCase("NOKIA_SERIES60"))
		{
			isEmulator = true;
			emulatorType = EM_KEM;
		}
		else if(platform.equalsIgnoreCase("NOKIA_SERIES40"))
		{
			isEmulator = true;
			emulatorType = EM_KEM;
		}
		else if(platform.equalsIgnoreCase("MicroEmulator-2.0"))
		{
			isEmulator = true;
			emulatorType = EM_MICROEMULATOR_V2;
			emulatorNotSupported = true;
		}
		else if(platform.equalsIgnoreCase("MicroEmulator"))
		{
			isEmulator = true;
			emulatorType = EM_MICROEMULATOR;
			emulatorNotSupported = true;
		}
		else if(platform.endsWith("/KEmulatorMod"))
		{
			isEmulator = true;
			emulatorType = EM_KEMMOD;
		}
		else if(platform.indexOf("Emulator") >= 0)
		{
			isEmulator = true;
			emulatorType = EM_UNDEFINED;
		}
		else if(platform.equalsIgnoreCase("j2me"))
		{
			isEmulator = true;
			emulatorType = EM_UNDEFINED;
		}
		else if(platform.indexOf(" ") >= 0)
		{
			isEmulator = true;
			emulatorType = EM_KEM_OR_J2L; 
			// кем так не палится. Вроде.
		}
		else
		{
			isEmulator = false;
			emulatorType = EM_NOT_EMULATOR;
		}
		Settings.setEmulatorSettings();
	}
	
	public static boolean isCompatible(int i)
	{
		switch(i)
		{
			case 0:
				return true;
				
			case -1:
				return false;
				
			case 1:
				//TODO return false;
				return true;
				
			case 4:
				return true;
				
			case 5:
				return true;
				
			case 6:
				return true;
				
			case 7:
				return false;
				
			case 8:
				return false;
				
			case 9:
				return false;
				
			case 10:
				return false;
				
			case 11:
				return true;
				
			case 16:
				return true;
				
			case 17:
				return true;
		}
		return false;
	}
	
	public static String getString(int i)
	{
		switch(i)
		{
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
