package vikatouch.utils;

/**
 * @author Shinovon
 * 
 */
public class DebugUtils {

	public static void printStackTrace() {
		try {
			throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
