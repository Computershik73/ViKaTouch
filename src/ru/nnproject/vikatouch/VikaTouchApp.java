package ru.nnproject.vikatouch;

import java.util.Hashtable;

import javax.microedition.midlet.*;

import vikatouch.Dialogs;
import vikatouch.NokiaUIInvoker;
import vikatouch.VikaTouch;
import vikatouch.screens.ChatScreen;

/**
 * @author Shinovon
 * 
 */
public final class VikaTouchApp extends MIDlet implements Runnable {
	public boolean isPaused;
	public boolean started = false;

	public void destroyApp(boolean arg0) {
		ChatScreen.stopUpdater();
		VikaTouch.inst.stop();
		Dialogs.stopUpdater();
		this.notifyDestroyed();
	}

	protected void pauseApp() {
		isPaused = true;
	}

	protected void startApp() {
		VikaTouch.mobilePlatform = System.getProperty("microedition.platform");
		isPaused = false;
		//if (VikaTouch.a!=0) {
		/*try {
			VikaTouch.sendLog("started " + String.valueOf(VikaTouch.a));
		} catch (Throwable e) {
			
		}*/
			//}
		if (!started) {
			VikaTouch.lastsuccessfullupdatetime=System.currentTimeMillis();
			VikaTouch.a=0;
			VikaTouch.supportsTouch=false;
			VikaTouch.smilestable=new Hashtable();
			try {
			started = true;
			VikaTouch.appInst = this;
			VikaTouch.inst = new VikaTouch();
			
			VikaTouch.inst.start();
			} catch (Throwable e) {

	        }
		}
		
	}

	public void run() {
		try {
			VikaTouch.inst.threadRun();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}