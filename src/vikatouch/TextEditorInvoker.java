package vikatouch;

import javax.microedition.lcdui.Font;

import com.nokia.mid.ui.TextEditor;
import com.nokia.mid.ui.TextEditorListener;

import ru.nnproject.vikaui.utils.ColorUtils;

//import shizaMobile.Global;

/**
 * @author Shinovon
 *
 */
public class TextEditorInvoker {
	
	private static TextEditor textEditor;

	static void showTextEditor(String text, int max, int constraints, int x, int y, int w, int h, int bgColor, int textColor, final NokiaUITextEditorListener listener) {
		if(VikaTouch.isS40())
			vikatouch.VikaTouch.canvas.setFullScreenMode(false);
		try {
			if (textEditor==null) {
			textEditor = TextEditor.createTextEditor("", max, constraints, w, h);
			
			}
			textEditor.setForegroundColor(textColor);
			//if (ColorUtils.isNight()) {
			//textEditor.setBackgroundColor(0);
			 //} else {
				 textEditor.setBackgroundColor(bgColor);
			 //}
			
			textEditor.setParent(vikatouch.VikaTouch.canvas);
			textEditor.setPosition(x, y);
			textEditor.setSize(w, h);
			
		
			//Font font = Font.getFont(0, 0, Font.SIZE_SMALL);
			//textEditor.setFont(font);
			 textEditor.setMultiline(true);
			textEditor.setZPosition(0);
			// textEditor.setTouchEnabled(true);
			 //getConstraints()
			textEditor.setVisible(true);
			textEditor.setFocus(true);
			//textEditor.setCaretXY(0,-8);
			textEditor.setContent(text);
			//textEditor.setCaret(text.length());
			 final NokiaUITextEditor editor = new NokiaUITextEditor() {
				public String getContent() {
					//textEditor.setCaretXY(0,8);
					
					return textEditor.getContent();
					//
				}	
			};
			textEditor.setTextEditorListener(new TextEditorListener() {
				public void inputAction(TextEditor textEditor, int actions) {
					//VikaTouch.log("act: " + actions);
					//textEditor.setCaretXY(0,8);
					if (actions==TextEditorListener.ACTION_OPTIONS_CHANGE) {
						textEditor.setFocus(false);
					}
					listener.action(editor, actions);
					
				//	textEditor.setVisible(false);
				}	
			});
			//textEditor.setVisible(false);
		} catch (Throwable e) {
			VikaTouch.sendLog(e.getMessage());
		}
	}
	
	
	/* Не S60
    */
   public static boolean isNotS60() {
       return VikaTouch.mobilePlatform.indexOf("S60") < 0;
   }

	static void init() {
	}
	
	static void setTextEditorPosition(int x, int y) {
		if(textEditor != null) {
			try {
				textEditor.setPosition(x, y);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	static String hideTextEditor() {
		if(VikaTouch.isS40())
			vikatouch.VikaTouch.canvas.setFullScreenMode(true);
		try {
			return textEditor.getContent();
		} catch (Throwable e) {
			return "";
		} finally {
			textEditor.setFocus(false);
			//textEditor.setParent(null);
			textEditor = null;
		}
	}
	
	static boolean textEditorShown() {
		return textEditor != null;
	}
	
	
	static Object getInst() {
        return textEditor;
    }
	//прикол такой что если в классе есть упоминание несуществующего класса то он весь класс роняет нафигн

	public static String getContent() {
		return textEditor.getContent();
	}
	
	public static void setContent(String x) {
		textEditor.setContent(x);
	}


	public static void setPosition(int x, int y) {
		textEditor.setPosition(x, y);
	}

	public static void setSize(int x, int y) {
		textEditor.setSize(x, y);
	}
	
}
