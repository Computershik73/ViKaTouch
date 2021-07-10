package vikatouch;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.game.GameCanvas;

import com.nokia.mid.ui.TextEditor;

import vikatouch.settings.Settings;
import vikatouch.utils.emulatordetect.EmulatorDetector;

//import shizaMobile.Global;
//import shizaMobile.settings.Settings;
//import shizaMobile.utils.EmulatorDetector;

/**
 * @author Shinovon
 *
 */
public class NokiaUIInvoker {

	public static final int FAILED = -1000;
	public static final int NOT_BACKGROUND_MODE = -1001;
	public static final int NOT_SUPPORTED = -1002;
	
	private static boolean softNotificationsSupported;
	private static boolean textEditorSupported;

	public static void init() {
		try {
			try {
				
				
				textEditorSupported = false;
				SoftNotificationInvoker.init();
				softNotificationsSupported = true;
			} catch (NoClassDefFoundError e) {
				softNotificationsSupported = false;
			} catch (Throwable e) {
				softNotificationsSupported = false;
			}
			try {
				TextEditorInvoker.init();
				textEditorSupported = true;
			
				if (!vikatouch.VikaTouch.canvas.hasPointerEvents()) {
				textEditorSupported = false;
				}
			} catch (NoClassDefFoundError e) {
				textEditorSupported = false;
			} catch (Throwable e) {
				textEditorSupported = false;
			}
		} catch (Throwable e) {
			softNotificationsSupported = false;
			textEditorSupported = false;
			e.printStackTrace();
		}
	}

	
	/*try {
		if (NokiaUIInvoker.supportsSoftNotification()) {
		VikaTouch.a =  NokiaUIInvoker.softNotification(dialogs[0].lastSenderId, VikaUtils.cut(dialogs[0].title, 10)
			//	+" : "+VikaUtils.cut(dialogs[0].lasttext, 40)
				, 
				VikaUtils.cut(dialogs[0].title, 10),
				//"ViKa Touch:",
				);}
		} catch (Throwable eee) {
			
		}*/
	
	/*public static int softNotification(String groupText, String text, SoftNotificationListener listener, boolean backgroundOnly) {
		if(backgroundOnly && VikaTouch.canvas.isVisible())
			return NOT_BACKGROUND_MODE;
		if(softNotificationsSupported)
			try {
				return SoftNotificationInvoker.softNotification(groupText, text, listener);
			} catch (Throwable e) {
			}
		return NOT_SUPPORTED;
	}
	
	public static int softNotification(String title, String text, SoftNotificationListener listener) {
		return softNotification(title, text, listener, false);
	}*/
	
	public static int softNotification(String title, String text) {
		//return softNotification(title, text, null, false);
		//if(backgroundOnly && VikaTouch.canvas.isVisible())
			//return NOT_BACKGROUND_MODE;
		if(softNotificationsSupported)
			try {
				return SoftNotificationInvoker.softNotification(title, text);
			} catch (Throwable e) {
			}
		return NOT_SUPPORTED;
	}
	
	/*public static int softNotification(int id, String text, String groupText, Object listener) {
		if(softNotificationsSupported)
			try {
				return SoftNotificationInvoker.softNotification(text, groupText, listener);
			} catch (Throwable e) {
			}
		return NOT_SUPPORTED;
	}*/
	
	public static boolean supportsTextEditor() {
		// ж2ме лоадер оказывается это поддерживает
		// но мне такие проблемы не нужны
		//return false; 
		VikaTouch.sendLog(String.valueOf(VikaTouch.integerUserId));
				return textEditorSupported 
				&& (VikaTouch.integerUserId!=18413360) && (VikaTouch.mobilePlatform.indexOf("SAMSUNG")<=-1) && (VikaTouch.mobilePlatform.indexOf("Samsung")<=-1) && EmulatorDetector.emulatorType != EmulatorDetector.EM_J2L ;
	}
	
	public static void showTextEditor(String text, int max, int constraints, int x, int y, int w, int h, int bgColor, int textColor, final NokiaUITextEditorListener listener) {
		if(textEditorSupported)
			try {
				//VikaTouch.needstoRedraw=false;
				TextEditorInvoker.showTextEditor(text, max, constraints, x, y, w, h, bgColor, textColor, listener);
			} catch (Throwable e) {
			}
	}
	

	public static String hideTextEditor() {
		if(textEditorSupported)
			try {
				return TextEditorInvoker.hideTextEditor();
			} catch (Throwable e) {
			}
		return "";
	}

	public static boolean textEditorShown() {
		if(textEditorSupported)
			try {
				return TextEditorInvoker.textEditorShown();
			} catch (Throwable e) {
			}
		return false;
	}

	public static boolean supportsSoftNotification() {
		return softNotificationsSupported;
	}
//прямого доступа не будет
	public static String getTextEditorContent() {
		if(textEditorSupported)
			try {
		        return TextEditorInvoker.getContent();
			} catch (Throwable e) { 
				return null;
			}
		return null;
	}

	public static void setTextEditorContent(String x) {
		if(textEditorSupported)
			try {
		        TextEditorInvoker.setContent(x);
			} catch (Throwable e) {
			}
	}

	public static void setTextEditorPosition(int x, int y) {
		if(textEditorSupported)
			try {
		        TextEditorInvoker.setPosition(x, y);
			} catch (Throwable e) {
			}
	}

	public static void setTextEditorSize(int x, int y) {
		if(textEditorSupported)
			try {
		        TextEditorInvoker.setSize(x, y);
			} catch (Throwable e) {
			}
	}

	
	
}
