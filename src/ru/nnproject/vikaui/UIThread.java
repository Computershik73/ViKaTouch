package ru.nnproject.vikaui;

public class UIThread
	extends Thread
{
	
	private VikaCanvas canvas;

	public UIThread(VikaCanvas canvas)
	{
		super();
		this.canvas = canvas;
		this.setPriority(Thread.MAX_PRIORITY);
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
				
			}
			Thread.yield();
		}
	}

}
