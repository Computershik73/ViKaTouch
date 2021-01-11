// This file is part of VikaUI
// Copyright (C) 2020  Arman Jussuplaliyev (Shinovon)

package ru.nnproject.vikaui;

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
			this.setPriority(Thread.MAX_PRIORITY);
		} else {
			this.setPriority(Thread.NORM_PRIORITY);
		}
	}

	public void run() {
		Thread fpsThread = new Thread("FPS-Counter") {
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
		};
		fpsThread.setPriority(Thread.NORM_PRIORITY);
		fpsThread.start();
		while (true) {
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
			// Thread.yield();
			try {
				double i = (1000d / (double)canvas.getFPSLimit());
				i -= wastedTime;
				if (i < 1)
					i = 1;
				Thread.sleep((long)i);
			} catch (Exception e) {
				fpsThread.interrupt();
				return;
			}
		}
	}

}
