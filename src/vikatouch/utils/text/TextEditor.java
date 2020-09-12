package vikatouch.utils.text;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import vikatouch.VikaTouch;

public class TextEditor implements CommandListener {
	public String str;
	    private static TextBox textBox;
	    private static TextField textField;
	    private static boolean inputFinished;
		private static Displayable screen;
	    private static final Command ok;
	    private static final Command cancel;
	    
	    TextEditor(final String str) {
	        this.str = str;
	    }
	    
	    public static String inputString(final String s, final String s2, final int n, final Image image) {
	        TextEditor.inputFinished = false;
	        final TextEditor commandListener = new TextEditor(null);
	        screen = Display.getDisplay(VikaTouch.appInst).getCurrent();
	        final Form current = new Form(s);
	        current.append(new ImageItem((String)null, image, 771, (String)null));
	        current.append(TextEditor.textField = new TextField((String)null, s2, (n > 0) ? n : 1024, 0));
	        current.addCommand(TextEditor.ok);
	        current.addCommand(TextEditor.cancel);
	        current.setCommandListener((CommandListener)commandListener);
	        VikaTouch.setDisplay((Displayable)current);
	        while (!TextEditor.inputFinished) {
	            Thread.yield();
	            try {
	                Thread.sleep(20L);
	            }
	            catch (Exception ex) {}
	        }
	        return commandListener.str;
	    }
	    
	    public static String inputString(final String s, final String s2, final int n) {
	        return inputString(s, s2, n, false);
	    }
	    
	    public static String inputString(final String preset, final String header, final int max, final boolean password) {
	        TextEditor.inputFinished = false;
	        final TextEditor commandListener = new TextEditor(header);
	        screen = Display.getDisplay(VikaTouch.appInst).getCurrent();
	        TextEditor.textBox = new TextBox(preset, header, (max > 0) ? max : 1024, password ? 65536 : 0);
	        textBox.addCommand(TextEditor.ok);
	        TextEditor.textBox.setCommandListener((CommandListener)commandListener);
	        VikaTouch.setDisplay((Displayable)TextEditor.textBox);
	        while (!TextEditor.inputFinished) {
	            Thread.yield();
	            try {
	                Thread.sleep(20L);
	            }
	            catch (Exception ex) {}
	        }
	        return commandListener.str;
	    }
	    
	    public void commandAction(final Command command, final Displayable displayable) {
	        if (command == TextEditor.ok) {
	            if (displayable == TextEditor.textBox) {
	                this.str = TextEditor.textBox.getString();
	            }
	            else {
	                this.str = TextEditor.textField.getString();
	            }
	        }
	        VikaTouch.setDisplay(TextEditor.screen);
	        VikaTouch.appInst.isPaused = false;
	        if(TextEditor.screen instanceof Canvas)
	        	((Canvas) TextEditor.screen).setFullScreenMode(true);
	        TextEditor.textField = null;
	        TextEditor.textBox = null;
	        TextEditor.inputFinished = true;
	    }
	    
	    static {
	        ok = new Command("ОК", 4, 0);
	        cancel = new Command("Отмена", 1, 1);
	    }
}

