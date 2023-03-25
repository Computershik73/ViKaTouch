// This file is part of VikaUI
// Copyright (C) 2020  Arman Jussuplaliyev (Shinovon)

package ru.nnproject.vikaui.screen;

import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Player;

import ru.nnproject.vikaui.VikaCanvas;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.music.MusicPlayer;
import vikatouch.screens.ChatScreen;
import vikatouch.utils.VikaUtils;

/**
 * @author Shinovon
 * 
 */
public abstract class VikaScreen {
	public abstract void draw(Graphics g);

	public void tap(int x, int y, int time) {
	}

	public void drag(int x, int y) {
	}

	public void press(int i) {
	}

	public void release(int i) {
	}

	public void repeat(int i) {
	}
	
	public static void paint(Graphics g) {
	}

	public void repaint() {
		VikaTouch.needRepaint();
	}
	
	public  void serviceRepaints() {
		VikaTouch.needRepaint();
	}

	public void onLeave() {
		VikaTouch.needRepaint();
	}

	public boolean scroll(int i) {
		return false;
	}
	
}
