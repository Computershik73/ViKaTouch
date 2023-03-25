// This file is part of VikaUI
// Copyright (C) 2020  Arman Jussuplaliyev (Shinovon)

package ru.nnproject.vikaui;

import javax.microedition.lcdui.game.GameCanvas;

import ru.nnproject.vikaui.popup.VikaNotice;
import ru.nnproject.vikaui.screen.VikaScreen;
import vikatouch.VikaTouch;

/**
 * @author Shinovon
 * 
 */
public abstract class VikaCanvas extends GameCanvas {

	public static VikaNotice currentAlert;
	public int fps;
	public int realFps;

	protected VikaCanvas() {
		super(false);
	}

	public abstract void callCommand(int i, VikaScreen scrollableCanvas);

	public abstract boolean isSensorModeOK();

	public abstract boolean isSensorModeJ2MELoader();

	public abstract boolean poorScrolling();

	protected abstract int getFPSLimit();

	protected abstract boolean drawMaxPriority();

	public abstract boolean isNight();
}
