package ru.nnproject.vikatouch;

import javax.microedition.midlet.*;

import vikatouch.VikaTouch;
import vikatouch.screens.ChatScreen;

public final class VikaTouchApp
	extends MIDlet
	implements Runnable
{
	public boolean isPaused;
	public boolean started = false;
	

	public void destroyApp(boolean arg0)
	{
		ChatScreen.stopUpdater();
		VikaTouch.inst.stop();
		this.notifyDestroyed();
	}

	protected void pauseApp()
	{
		isPaused = true;
	}

	protected void startApp()
	{
		VikaTouch.mobilePlatform = System.getProperty("microedition.platform");
		//Зачем!!
		/*
		if(VikaTouch.mobilePlatform.equals("Nokia_SERIES60")|| VikaTouch.mobilePlatform.equals("Nokia_SERIES40"))
		{
			VikaTouch.mobilePlatform = "KEmulator";
		}
		*/
		isPaused = false;
		
		if(!started)
		{
			started = true;
			VikaTouch.appInst = this;
			VikaTouch.inst = new VikaTouch();
			VikaTouch.inst.start();
		}
	}

	public void run()
	{
		VikaTouch.inst.threadRun();
	}
}