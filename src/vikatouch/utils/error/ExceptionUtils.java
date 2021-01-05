package vikatouch.utils.error;

public class ExceptionUtils {
	
	public final static boolean canStackTrace() {
		try {
			Class.forName("com.nokia.mj.impl.utils.DebugUtils");
			return true;
		} catch (Throwable e) {
			
		}
		return false;
	}
	
	public final static String getStackTrace(Throwable t) {
		try {
			Class.forName("com.nokia.mj.impl.utils.DebugUtils");
			return format(com.nokia.mj.impl.utils.DebugUtils.getStackTrace(t));
		} catch (Throwable e) {
			
		}
		return t.toString();
	}

	private final static String format(String stackTrace) {
		return stackTrace.replace('\t', '\0');
	}

}
