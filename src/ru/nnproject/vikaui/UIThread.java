package ru.nnproject.vikaui;

import vikatouch.VikaTouch;

public class UIThread
	extends Thread
{
	
	private VikaCanvas canvas;

	public UIThread(VikaCanvas canvas)
	{
		super();
		this.canvas = canvas;
		this.setPriority(Thread.NORM_PRIORITY);
	}

	public void run()
	{
		while(true)
		{
			try
			{
				canvas.tick();
			}
			catch (Exception e)
			{
				VikaTouch.sendLog("Tick failed. "+e.toString());
			}
			Thread.yield();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				VikaTouch.sendLog("UI thread exit");
				return;
			}
		}
	}

}
