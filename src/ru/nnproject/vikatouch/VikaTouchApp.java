package ru.nnproject.vikatouch;

import javax.microedition.midlet.*;

import vikatouch.Dialogs;
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

		if (!started) {
			started = true;
			VikaTouch.appInst = this;
			VikaTouch.inst = new VikaTouch();
			VikaTouch.inst.start();
		}
	}

	public void run() {
		try {
			VikaTouch.inst.threadRun();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}