package vikatouch;



/**
 * @author Shinovon
 *
 */
/*public class NokiaUITextEditor extends com.nokia.mid.ui.TextEditor {
	
	public String getContent() {
		//textEditor.setCaretXY(0,8);
		ChatScreen.inputText=this.getContent();
		VikaTouch.needstoRedraw=false;
		return this.getContent();
		//
	}	

}*/
public interface NokiaUITextEditor {

    public String getContent();

}