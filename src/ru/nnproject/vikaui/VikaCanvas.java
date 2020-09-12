package ru.nnproject.vikaui;

import javax.microedition.lcdui.game.GameCanvas;

import ru.nnproject.vikaui.screen.VikaScreen;

public abstract class VikaCanvas
	extends GameCanvas
{
	public static String debugString = "";

	protected VikaCanvas()
	{
		super(false);
	}

	public abstract void tick();

	public abstract void callCommand(int i, VikaScreen scrollableCanvas);

	public abstract void paint();

	public abstract boolean isSensorModeOK();
	
	public abstract boolean isSensorModeJ2MELoader();
	
	public abstract boolean poorScrolling();

}
