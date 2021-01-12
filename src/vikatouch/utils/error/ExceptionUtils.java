package vikatouch.utils.error;

/**
 * @author Shinovon
 * 
 */
public class ExceptionUtils {
	
	public final static boolean canStackTrace() {
		return false;
	}
	
	public final static String getStackTrace(Throwable t) {
		return t.toString();
	}

	private final static String format(String stackTrace) {
		return stackTrace.replace('\t', '\0');
	}

}