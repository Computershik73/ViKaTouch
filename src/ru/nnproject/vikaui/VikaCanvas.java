package ru.nnproject.vikaui;

import javax.microedition.lcdui.game.GameCanvas;

import ru.nnproject.vikaui.screen.VikaScreen;

public abstract class VikaCanvas extends GameCanvas {

	protected VikaCanvas() {
		super(false);
	}

	public abstract void tick();

	public abstract void callCommand(int i, VikaScreen scrollableCanvas);

	public abstract void draw();

	public abstract boolean isSensorModeOK();

	public abstract boolean isSensorModeJ2MELoader();

	public abstract boolean poorScrolling();

	protected abstract int getFPSLimit();

	protected abstract boolean drawMaxPriority();

}
