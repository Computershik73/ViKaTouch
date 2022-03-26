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

	public void press(int x, int y) {
		VikaTouch.needstoRedraw=true;
	}

	public void release(int x, int y) {
		VikaTouch.needstoRedraw=true;
	}

	public void drag(int x, int y) {
		VikaTouch.needstoRedraw=true;
	}

	public void press(int i) {
		VikaTouch.needstoRedraw=true;
	}

	public void release(int i) {
		VikaTouch.needstoRedraw=true;
	}

	public void repeat(int i) {
		VikaTouch.needstoRedraw=true;
	}

	/*
   	public static void repaint() {
		if ((DisplayUtils.canvas != null) && (VikaTouch.needstoRedraw)) {
			DisplayUtils.canvas.draw();
			VikaTouch.needstoRedraw=false;
		}
	}

	public void onLeave() {
		VikaTouch.needstoRedraw=true;
	}
	
	public static void repaint() {
		if ((DisplayUtils.canvas != null) && (VikaTouch.needstoRedraw)) {
			//DisplayUtils.canvas.draw();
			DisplayUtils.canvas.repaint();
			VikaTouch.needstoRedraw=false;
		}
	}
	
	public static void servicerepaints() {
		if ((DisplayUtils.canvas != null) && (VikaTouch.needstoRedraw)) {
			DisplayUtils.canvas.serviceRepaints();
			VikaTouch.needstoRedraw=false;
		}
	}*/
	
	public static void paint(Graphics g) {
		if (ChatScreen.isRecRunning) {
			VikaTouch.needstoRedraw=true;
		
	}
		if ((DisplayUtils.canvas != null) && (VikaTouch.needstoRedraw) && (VikaCanvas.currentAlert==null)) {
			DisplayUtils.canvas.paint(g);
			if (vikatouch.music.MusicPlayer.inst!=null) {
			if (!(vikatouch.music.MusicPlayer.inst.isPlaying)) { 
			VikaTouch.needstoRedraw=false;
			}
			}
			
		}
	}

	public void repaint() {
		//if ((DisplayUtils.canvas != null) && (VikaTouch.needstoRedraw)) {
		if (ChatScreen.isRecRunning) {
			VikaTouch.needstoRedraw=true;
		
		}
		if (vikatouch.music.MusicPlayer.inst!=null) {
			if (!(vikatouch.music.MusicPlayer.inst.isPlaying)) { 
			DisplayUtils.canvas.repaint();
			}
		}
		
			//VikaTouch.needstoRedraw=true;
		//}
	}
	
	public  void serviceRepaints() {
		//VikaUtils.logToFile("servicerepaint");
		//if ((DisplayUtils.canvas != null) && (VikaTouch.needstoRedraw)) {
			DisplayUtils.canvas.serviceRepaints();
			VikaTouch.needstoRedraw=true;
		//}
	}

	public void onLeave() {
		VikaTouch.needstoRedraw=true;
		
	}
	
}
