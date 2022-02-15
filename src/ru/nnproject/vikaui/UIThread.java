// This file is part of VikaUI
// Copyright (C) 2020  Arman Jussuplaliyev (Shinovon)

package ru.nnproject.vikaui;

import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

import vikatouch.VikaTouch;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;

/**
 * @author Shinovon
 * 
 */
public class UIThread extends Thread {

	private VikaCanvas canvas;
	private int frames;

	public UIThread(VikaCanvas canvas) {
		super();
		this.canvas = canvas;
		if (canvas.drawMaxPriority()) {
			// Ставим девятку, что-бы в случае крайней необходимости, интерфейс был в меньшем приоритете.
			this.setPriority(9);
		} else {
			this.setPriority(Thread.NORM_PRIORITY);
		}
	}

	public void run() {
		/*Thread fpsThread = new Thread("FPS-Counter") {
			public void run() {
				while(true) {
					try {
						canvas.realFps = frames;
						frames = 0;
						Thread.yield();
						Thread.sleep(1000);
					} catch (Exception e) {
						return;
					}
				}
			}
		};*/
		//fpsThread.setPriority(Thread.NORM_PRIORITY);
		//fpsThread.start();
		while (true) {
			
			if (System.currentTimeMillis() - VikaTouch.lastsuccessfullupdatetime>(Settings.refreshtimeout * 1000)) {
				//VikaTouch.needstoRedraw=true;
				VikaTouch.istimeout=true;
				//VikaTouch.silenterror("Сети нет более 16 секунд!", false);
				//Display d = Display.getDisplay(VikaTouch.appInst);
				VikaTouch.needstoRedraw=true;
				//d.vibrate(500);
				
				
			/*	 try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 d.vibrate(1000);
				 try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 d.vibrate(1000);*/
				
				/*Player notifplayer;
				try {
					notifplayer = Manager.createPlayer("device://tone");
					notifplayer.realize();
					notifplayer.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MediaException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				
			} else {
				VikaTouch.istimeout=false;
			}
			long wastedTime = 0;
			try {
				long i = System.currentTimeMillis();
				canvas.tick();
				wastedTime = System.currentTimeMillis() - i;
				frames++;
				canvas.fps = (int) ((1000d - wastedTime) / wastedTime);
				//canvas.frametime = (int) wastedTime;
			} catch (Throwable e) {
				
			}
			 Thread.yield();
			try {
				double i = (1000d / (double)canvas.getFPSLimit());
				i -= wastedTime;
				if (i < 1)
					i = 1;
				Thread.sleep((long)i);
			} catch (Exception e) {
				//fpsThread.interrupt();
				return;
			}
		}
	}

}
