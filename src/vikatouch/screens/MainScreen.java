package vikatouch.screens;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.MathUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;

//import shizaMobile.Global;
//import VikaTouch.screens.DialogsScreen;
//import shizaMobile.screens.NewsScreen;
import vikatouch.music.MusicPlayer;

import vikatouch.VikaTouch;
import vikatouch.screens.menu.MenuScreen;
import vikatouch.settings.Settings;
import vikatouch.settings.SettingsScreen;
import vikatouch.utils.VikaUtils;

/**
 * @author Shinovon
 * 
 */
public abstract class MainScreen extends ScrollableCanvas {

	protected boolean hasBackButton; // кст, почему не статик?
	public MainScreen backScreen;
	public int bottomPanelH2;
	private static Image[] miniplayerButtons;
	public static int topPanelH = 58;
	public static int bottomPanelH = 50;

	public MainScreen() {
		super();
	}

	protected void scrollHorizontally(int deltaX) {
		if (deltaX < -7) {
			VikaTouch.inst.cmdsInst.command(10, this);
		} else if (deltaX > 7) {
			VikaTouch.inst.cmdsInst.command(11, this);
		}
	}

	public void tap(int x, int y, int time) {
		if (!(this instanceof ChatScreen)) {
			if (!dragging || !canScroll) {
				int wyw = DisplayUtils.width / 4;
				boolean showBottomPanel = !Settings.hideBottom && VikaTouch.accessToken != null && !DisplayUtils.compact
						&& (!(this instanceof SettingsScreen));
				if ((y < topPanelH)) {
					if (hasBackButton && x < oneitemheight) {
						VikaTouch.inst.cmdsInst.command(14, this);
					}
					if (this instanceof MenuScreen && x > DisplayUtils.width - oneitemheight) {
						VikaTouch.inst.cmdsInst.command(13, this);
					}
				} else if (!(this instanceof SettingsScreen) && y >= DisplayUtils.height - bottomPanelH2 && showBottomPanel) {
					int acenter = (DisplayUtils.width - wyw) / 2;
					if (x < wyw) {
						VikaTouch.inst.cmdsInst.command(0, this);
					}

					if (x > DisplayUtils.width - wyw) {
						VikaTouch.inst.cmdsInst.command(2, this);
					}

					if (x > acenter && x < acenter + wyw) {
						VikaTouch.inst.cmdsInst.command(1, this);
					}
				} else if (!(this instanceof SettingsScreen) && y >= DisplayUtils.height - bottomPanelH && showBottomPanel) {
					if (x < 50 && MusicPlayer.inst != null) {
						if(MusicPlayer.inst.isPlaying) {
							MusicPlayer.inst.pause();
						} else {
							MusicPlayer.inst.play();
						}
					} else if (x > DisplayUtils.width - 50 && MusicPlayer.inst != null) {
						if(MusicPlayer.inst.isPlaying) {
							MusicPlayer.inst.next();
						} else {
							MusicPlayer.inst.destroy();
							MusicPlayer.inst = null;
						}
					} else VikaTouch.inst.cmdsInst.command(17, this);
				}
			}
		}
		
	}
	
	
	protected final void keysScrollmore(int dir) {
		VikaTouch.needstoRedraw=true;
		try {
			int delta = DisplayUtils.height / 3;
			int st = 0;
			int thisItemY = getItemY(currentItem);
			int topItemY = getItemY(currentItem - dir);
			int downItemY = thisItemY + 50;
			int down2ItemY = downItemY + 50;
			//if (currentItem + 1>uiItems.size()) {
			//	return;
			//}
			try {
				downItemY = thisItemY + ((PressableUIItem) uiItems.elementAt(currentItem)).getDrawHeight();
				down2ItemY = downItemY + 50;
				down2ItemY = downItemY + ((PressableUIItem) uiItems.elementAt(currentItem + dir)).getDrawHeight();
			} catch (RuntimeException e1) {
			}
			int scrY = -scroll - MainScreen.topPanelH + DisplayUtils.height * 3 / 4;
			//int br = 0;
			//int sc = 0;
			//scrlDbg = "dir" + dir + " " + topItemY + " " + thisItemY + " " + downItemY + " " + down2ItemY + " d" + delta
			//		+ " scry" + scrY;
			if (dir > 0) {
				// up
				if (scrY - thisItemY < 0) {
					//sc = 1;
					selectCentered();
					thisItemY = getItemY(currentItem);
					topItemY = getItemY(currentItem - dir);
					try {
						downItemY = thisItemY + ((PressableUIItem) uiItems.elementAt(currentItem)).getDrawHeight();
					} catch (RuntimeException e1) {
					}
				}

				st = -delta;
				if (scrY - thisItemY > delta) {
					//br = 1;
				} else if (scrY - topItemY > delta) {
					//br = 2;
					select(currentItem - dir);
				} else if (thisItemY < 10) {
					//br = 7;
					select(0);
				} else {
					//br = 3;
					st = topItemY - scrY + 1;
					select(currentItem - dir);
				}
			} else {
				// down
				st = delta;
				if (down2ItemY - scrY > delta && downItemY - scrY <= delta) {
					//br = 5;
					st = (down2ItemY - scrY - 1);
					select(currentItem + dir);
				} else if (downItemY - scrY > delta) {
					st = (down2ItemY - scrY - 1);
					select(currentItem + dir);
					//br = 4;
				} else {
					//br = 6;
					st = (down2ItemY - scrY - 1);
					select(currentItem + dir);
				}
			}
			scrollTarget = MathUtils.clamp(scroll - st, -listHeight, 0);
			//scrlDbg += " st" + st + "br" + br + "s" + sc;
			//System.out.println(scrlDbg);
			scrollTargetActive = true;
			VikaTouch.needstoRedraw=true;
			this.serviceRepaints();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	protected final void keysScroll(int dir) {
		VikaTouch.needstoRedraw=true;
		try {
			int delta = DisplayUtils.height / 3;
			int st = 0;
			int thisItemY = getItemY(currentItem);
			int topItemY = getItemY(currentItem - 1);
			int downItemY = thisItemY + 50;
			int down2ItemY = downItemY + 50;
			//if (currentItem + 1>uiItems.size()) {
			//	return;
			//}
			try {
				downItemY = thisItemY + ((PressableUIItem) uiItems.elementAt(currentItem)).getDrawHeight();
				down2ItemY = downItemY + 50;
				down2ItemY = downItemY + ((PressableUIItem) uiItems.elementAt(currentItem + 1)).getDrawHeight();
			} catch (RuntimeException e1) {
			}
			int scrY = -scroll - MainScreen.topPanelH + DisplayUtils.height * 3 / 4;
			//int br = 0;
			//int sc = 0;
			//scrlDbg = "dir" + dir + " " + topItemY + " " + thisItemY + " " + downItemY + " " + down2ItemY + " d" + delta
			//		+ " scry" + scrY;
			if (dir > 0) {
				// up
				if (scrY - thisItemY < 0) {
					//sc = 1;
					selectCentered();
					thisItemY = getItemY(currentItem);
					topItemY = getItemY(currentItem - 1);
					try {
						downItemY = thisItemY + ((PressableUIItem) uiItems.elementAt(currentItem)).getDrawHeight();
					} catch (RuntimeException e1) {
					}
				}

				st = -delta;
				if (scrY - thisItemY > delta) {
					//br = 1;
				} else if (scrY - topItemY > delta) {
					//br = 2;
					select(currentItem - 1);
				} else if (thisItemY < 10) {
					//br = 7;
					select(0);
				} else {
					//br = 3;
					st = topItemY - scrY + 1;
					select(currentItem - 1);
				}
			} else {
				// down
				st = delta;
				if (down2ItemY - scrY > delta && downItemY - scrY <= delta) {
					//br = 5;
					select(currentItem + 1);
				} else if (downItemY - scrY > delta) {
					//br = 4;
				} else {
					//br = 6;
					st = (down2ItemY - scrY - 1);
					select(currentItem + 1);
				}
			}
			scrollTarget = MathUtils.clamp(scroll - st, -listHeight, 0);
			//scrlDbg += " st" + st + "br" + br + "s" + sc;
			//System.out.println(scrlDbg);
			scrollTargetActive = true;
			VikaTouch.needstoRedraw=true;
			this.serviceRepaints();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/*public void selectCentered() {
		VikaTouch.needstoRedraw=true;
		try {
			System.out.println("select center");
			int y = MainScreen.topPanelH;
			int ye = y;
			int s = -scrolled + DisplayUtils.height / 2;
			for (int i = 0; (i < uiItems.length); i++) {
				ye = y + uiItems[i].getDrawHeight();
				if (y <= s && ye > s) {
					select(i);
					return;
				}
				y = ye;
			}
		} catch (Throwable e) {
			VikaTouch.sendLog("selectCentered " + e.getMessage());
		}
	}*/
	
	/*public void scrollToSelected() {
		if (this instanceof ChatScreen) {
		if (uiItems[currentItem]!=null) {
		VikaTouch.needstoRedraw=true;
		//try {
		int sc= 0;
		for (int i=0; i<uiItems.length; i++) {
			if (uiItems[i]!=null) {
				sc+=uiItems[i].getDrawHeight();
				//VikaUtils.logToFile(String.valueOf(uiItems[i].getDrawHeight())+ " ");
				if (uiItems[i].isSelected()) {
					currentItem=i;
					sc-=uiItems[i].getDrawHeight();
					
					break;
				}
			} else {
				//break;
			}
			
		}
			//scrolled = -(getItemY(currentItem) - DisplayUtils.height / 2 + (uiItems[currentItem].getDrawHeight() / 2)
		//			+ MainScreen.topPanelH);
		scrolled =  -(sc + uiItems[currentItem].getDrawHeight() / 2  ) - DisplayUtils.height / 2  + MainScreen.topPanelH ;
		//VikaUtils.logToFile("Scrolled= " + String.valueOf(scrolled)+ " ");
			//VikaTouch.
			this.serviceRepaints();
		} else {
		//	VikaUtils.logToFile("error scrolling to sel");
		}
		} else {
			try {
				scrolled = -(getItemY(currentItem) - DisplayUtils.height / 2 + (uiItems[currentItem].getDrawHeight() / 2)
						+ MainScreen.topPanelH);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		//} catch (Throwable e) {
			//e.printStackTrace();
		//}
	}*/
	
	
	public void selectCentered() {
		try {
			int y = MainScreen.topPanelH;
			int ye = y;
			int s = -scroll + DisplayUtils.height / 2;
			for (int i = 0; i < uiItems.size(); i++) {
				ye = y + ((PressableUIItem) uiItems.elementAt(i)).getDrawHeight();
				if (y <= s && ye > s) {
					select(i);
					return;
				}
				y = ye;
			}
		} catch (Throwable e) {
		}
	}
	
	public void scrollToSelected() {
		try {
			//VikaUtils.logToFile("scrolltoselected");
			scroll = -(getItemY(
					currentItem
					//1
					) - DisplayUtils.height / 2 + (((PressableUIItem) uiItems.elementAt(
							currentItem
							//1
							)).getDrawHeight() / 2)
					+ MainScreen.topPanelH);
		} catch (Throwable e) {
			//VikaUtils.logToFile(e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	
	public void scrollToTop() {
		VikaTouch.needstoRedraw=true;
		scroll = -1000;//getItemY(currentItem);
		VikaTouch.needstoRedraw=true;
		
	}

	protected void drawHUD(Graphics g, String title) {
		// vars
		boolean musicHUDShown = !DisplayUtils.compact && DisplayUtils.height > 320 && vikatouch.music.MusicPlayer.inst != null && !keysMode;
		int musichud = musicHUDShown ? 32 : 0;
		boolean smallTop = DisplayUtils.compact || (DisplayUtils.height <= 320 && !(this instanceof MenuScreen));
		
		topPanelH = DisplayUtils.compact ? 24 : 58;
		
		boolean smallBottom = DisplayUtils.compact || DisplayUtils.height < 640;
		bottomPanelH2 = Settings.hideBottom ? 0 : (smallBottom ? 36 : (musicHUDShown ? 42 : 50));
		
		bottomPanelH = Settings.hideBottom ? musichud : ((smallBottom ? 36 : (musicHUDShown ? 42 : 50)) + musichud);
		int dw = DisplayUtils.width;
		boolean showBottomPanel = !Settings.hideBottom && VikaTouch.accessToken != null && !DisplayUtils.compact
				&& (!(this instanceof SettingsScreen));
		// fills
		ColorUtils.setcolor(g, ColorUtils.TITLEPANELCOLOR);
		g.fillRect(0, 0, dw, topPanelH);
		ColorUtils.setcolor(g, ColorUtils.BOTTOMPANELCOLOR);
		if (showBottomPanel)
			g.fillRect(0, DisplayUtils.height - bottomPanelH, dw, bottomPanelH);

		if (!DisplayUtils.compact) {
			// header & icon
			if (hasBackButton && !keysMode) {
				g.drawImage(IconsManager.backImg, 6, topPanelH / 2 - 24, 0);
			} else if (this instanceof MenuScreen)
				g.drawImage(IconsManager.logoImg, 16, topPanelH / 2 - 25, 0);
			// Такое себе, согласен. Ну хоть что-то.

		}  else if (this instanceof MenuScreen) {
			title = "ViKa touch";
		}
		g.setFont(Font.getFont(0, 0, Font.SIZE_LARGE));
		g.setGrayScale(255);
		/*if (title != null) {
			if (DisplayUtils.compact || !hasBackButton || keysMode) {
				g.drawString(title, 10, topPanelH / 2 - g.getFont().getHeight() / 2, 0);
			} else {
				g.drawString(title, 72, topPanelH / 2 - g.getFont().getHeight() / 2, 0);
			}
		}*/
		if (title != null) {
			
			g.drawString(title, DisplayUtils.compact || !hasBackButton || keysMode ? 24 : smallTop ? 56 : 64, topPanelH / 2 - g.getFont().getHeight() / 2, 0);
		}
		//Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
		Font f;
		//try {
		// f = NokiaUIInvoker.getFont(0, 0, 18, 8);
		//} catch (Throwable eee) {
			 f = Font.getFont(0, 0, Font.SIZE_SMALL);
		//}
		g.setFont(f);

		if (showBottomPanel) {
			if(miniplayerButtons == null && musicHUDShown)

				try {
					Image sheet = Image.createImage("/miniplayer.png");
					miniplayerButtons = new Image[3];
					for (int i = 0; i < 3; i++) {
						miniplayerButtons[i] = Image.createImage(sheet, i * 24, 0, 24, 19, 0);
					}
				} catch (Exception e) {

				}
			
			// bottom icons
			int bpiy = DisplayUtils.height - bottomPanelH2 / 2 - 12;
			g.drawImage(((this == VikaTouch.newsScr) ? IconsManager.selIco : IconsManager.ico)[IconsManager.NEWS],
					dw / 6 - 12, bpiy, 0);
			g.drawImage(((this instanceof DialogsScreen) ? IconsManager.selIco : IconsManager.ico)[IconsManager.MSGS],
					dw / 2 - 12, bpiy, 0);
			g.drawImage(((this instanceof MenuScreen || !(this instanceof DialogsScreen || this instanceof NewsScreen || this instanceof SettingsScreen)) ? IconsManager.selIco : IconsManager.ico)[IconsManager.MENU],
					dw - dw / 6 - 12, bpiy, 0);

			// unread count
			if (VikaTouch.unreadCount > 0) {
				String s = "" + VikaTouch.unreadCount;
				int d2 = 16;
				int d3 = 2;
				boolean roundrect = false;
				if (VikaTouch.unreadCount > 9) {
					s = "9+";
					d2 = 24;
					d3 = 2 - ((d2 - 16) / 2);
					roundrect = true;
				}
				int d = 16;
				int fh = f.getHeight();

				g.setColor(225, 73, 73);
				if (roundrect) {
					g.fillRoundRect(dw / 2 + d3, bpiy - 5, d2, d, 8, 8);
				} else {
					g.fillArc(dw / 2 + d3, bpiy - 5, d2, d, 0, 360);
				}
				g.setGrayScale(255);
				g.drawString(s, dw / 2 + 2 + (d - f.stringWidth(s)) / 2, bpiy - 5 + (d - fh) / 2 + 1, 0);
			}
			
			if(musicHUDShown) {
				g.drawImage(MusicPlayer.inst.isPlaying ? miniplayerButtons[0] : miniplayerButtons[1], 10, DisplayUtils.height - bottomPanelH + 4, 0);
				g.drawImage(MusicPlayer.inst.isPlaying ? miniplayerButtons[2] : IconsManager.ico[IconsManager.CLOSE], dw - 32, DisplayUtils.height - bottomPanelH + 4, 0);
				g.setFont(f);
				//g.setFont(vikatouch.NokiaUIInvoker.getFont(0, 0, 12, 8));
				ColorUtils.setcolor(g, ColorUtils.TEXT);
				String s1 = MusicPlayer.inst.title;
				String s2 = MusicPlayer.inst.artist;
				g.drawString(s1, (dw - g.getFont().stringWidth(s1)) / 2, DisplayUtils.height - bottomPanelH + 4, 0);
				ColorUtils.setcolor(g, ColorUtils.TEXT2);
				g.drawString(s2, (dw - g.getFont().stringWidth(s2)) / 2, DisplayUtils.height - bottomPanelH + 18, 0);
			}
		}

		/*
		 * if(Settings.debugInfo) { g.setColor(0xffff00); int xx = endx; int yy = endy;
		 * if(xx == -1) { xx = lastx; yy = lasty; } g.drawLine(startx, starty, xx, yy);
		 * g.drawRect(startx-2, starty-2, 4, 4); g.setColor(0xff0000);
		 * g.drawRect(endx-2, endy-2, 4, 4); g.drawString("cs"+scroll + " sc" + scrolled
		 * + " d" + drift + " ds" + driftSpeed + " st" + scrollingTimer + " sp" +
		 * scrollPrev + " t" + timer, 0, 30, 0); }
		 */
	}

	public void drawHUD(Graphics g) {
		drawHUD(g, "");
	}

	/*
	 * protected void drawHUDOld(Graphics g, String title) { double multiplier =
	 * (double)DisplayUtils.height / 640.0; double ww = 10.0 * multiplier; int w =
	 * (int)ww; switch(DisplayUtils.idispi) { case DisplayUtils.DISPLAY_PORTRAIT: {
	 * ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR); g.fillRect(0, 0,
	 * DisplayUtils.width, 58); if(VikaTouch.menuScr != null) {
	 * ColorUtils.setcolor(g, -3); g.fillRect(0, DisplayUtils.height - 50,
	 * DisplayUtils.width, 50);
	 * 
	 * if(menuImg != null) { g.drawImage(menuImg, 304, 606, 0); } if(!hasBackButton
	 * && MenuScreen.logoImg != null) { g.drawImage(MenuScreen.logoImg, 2, 2, 0); }
	 * if(newsImg != null) { g.drawImage(newsImg, 37, 604, 0); }
	 * if(VikaTouch.unreadCount > 0) { if(MenuScreen.dialImg2 != null) {
	 * g.drawImage(MenuScreen.dialImg2, 168, 599, 0); g.setFont(Font.getFont(0, 0,
	 * Font.SIZE_SMALL)); g.drawString(""+VikaTouch.unreadCount, 191, 598, 0); }
	 * else if(MenuScreen.dialImg != null) { g.drawImage(MenuScreen.dialImg, 168,
	 * 604, 0); }
	 * 
	 * } else { if(MenuScreen.dialImg != null) { g.drawImage(MenuScreen.dialImg,
	 * 168, 604, 0); } } } if(hasBackButton && backImg != null) {
	 * g.drawImage(backImg, 2, 2, 0); } g.setFont(Font.getFont(0, 0,
	 * Font.SIZE_LARGE)); g.drawString(title, 72, 29-g.getFont().getHeight()/2, 0);
	 * g.setFont(Font.getFont(0, 0, 8)); break; } case DisplayUtils.DISPLAY_S40:
	 * case DisplayUtils.DISPLAY_ASHA311: { ColorUtils.setcolor(g,
	 * ColorUtils.BUTTONCOLOR); g.fillRect(0, 0, 240, 30); if(VikaTouch.menuScr !=
	 * null) { ColorUtils.setcolor(g, -3); g.fillRect(0, DisplayUtils.height - 25,
	 * 240, 25);
	 * 
	 * if(!hasBackButton && MenuScreen.logoImg != null) {
	 * g.drawImage(MenuScreen.logoImg, 2, 1, 0); }
	 * 
	 * if(menuImg != null) { g.drawImage(menuImg, 212, 303, 0); }
	 * 
	 * if(newsImg != null) { g.drawImage(newsImg, 18, 301, 0); }
	 * 
	 * if(VikaTouch.unreadCount > 0) { if(MenuScreen.dialImg2 != null) {
	 * g.drawImage(MenuScreen.dialImg2, 114, 299, 0); g.setFont(Font.getFont(0, 0,
	 * Font.SIZE_SMALL)); g.drawString(""+VikaTouch.unreadCount, 126, 300, 0); }
	 * else if(MenuScreen.dialImg != null) { g.drawImage(MenuScreen.dialImg, 114,
	 * 302, 0); }
	 * 
	 * } else { if(MenuScreen.dialImg != null) { g.drawImage(MenuScreen.dialImg,
	 * 114, 302, 0); } } }
	 * 
	 * if(hasBackButton && backImg != null) { g.drawImage(backImg, 0, 0, 0); }
	 * g.setFont(Font.getFont(0, 0, Font.SIZE_LARGE)); g.drawString(title, 52, 0,
	 * 0); g.setFont(Font.getFont(0, 0, 8)); break; }
	 * 
	 * case DisplayUtils.DISPLAY_ALBUM: { ColorUtils.setcolor(g,
	 * ColorUtils.BUTTONCOLOR); g.fillRect(0, 0, 640, 58); if(VikaTouch.menuScr !=
	 * null) { ColorUtils.setcolor(g, -3); g.fillRect(0, 310, 640, 50);
	 * 
	 * if(!hasBackButton && MenuScreen.logoImg != null) {
	 * g.drawImage(MenuScreen.logoImg, 2, 2, 0); } if(newsImg != null) {
	 * g.drawImage(newsImg, 36, 324, 0); } if(VikaTouch.unreadCount > 0) {
	 * 
	 * if(MenuScreen.dialImg2 != null) { g.drawImage(MenuScreen.dialImg2, 308, 319,
	 * 0); g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
	 * g.drawString(""+VikaTouch.unreadCount, 330, 318, 0); } else
	 * if(MenuScreen.dialImg != null) { g.drawImage(MenuScreen.dialImg, 308, 324,
	 * 0); }
	 * 
	 * } else { if(MenuScreen.dialImg != null) { g.drawImage(MenuScreen.dialImg,
	 * 308, 324, 0); } } if(menuImg != null) { g.drawImage(menuImg, 584, 326, 0); }
	 * }
	 * 
	 * if(hasBackButton && backImg != null) { g.drawImage(backImg, 2, 2, 0); }
	 * g.setFont(Font.getFont(0, 0, Font.SIZE_LARGE)); g.drawString(title, 72, 14,
	 * 0); g.setFont(Font.getFont(0, 0, 8));
	 * 
	 * break; }
	 * 
	 * case DisplayUtils.DISPLAY_EQWERTY: { ColorUtils.setcolor(g,
	 * ColorUtils.BUTTONCOLOR); g.fillRect(0, 0, 320, 30); if(VikaTouch.menuScr !=
	 * null) { ColorUtils.setcolor(g, -3); g.fillRect(0, 215, 320, 25);
	 * 
	 * if(!hasBackButton && MenuScreen.logoImg != null) {
	 * g.drawImage(MenuScreen.logoImg, 2, 1, 0); }
	 * 
	 * if(menuImg != null) { g.drawImage(menuImg, 292, 303-75, 0); }
	 * 
	 * if(newsImg != null) { g.drawImage(newsImg, 18, 301-75, 0); }
	 * 
	 * if(VikaTouch.unreadCount > 0) { if(MenuScreen.dialImg2 != null) {
	 * g.drawImage(MenuScreen.dialImg2, 114, 299-75, 0); g.setFont(Font.getFont(0,
	 * 0, Font.SIZE_SMALL)); g.drawString(""+VikaTouch.unreadCount, 126, 300-75, 0);
	 * } else if(MenuScreen.dialImg != null) { g.drawImage(MenuScreen.dialImg, 114,
	 * 302-75, 0); }
	 * 
	 * } else { if(MenuScreen.dialImg != null) { g.drawImage(MenuScreen.dialImg,
	 * 114, 302-75, 0); } } }
	 * 
	 * if(hasBackButton && backImg != null) { g.drawImage(backImg, 2, 0, 0); }
	 * g.setFont(Font.getFont(0, 0, Font.SIZE_LARGE)); g.drawString(title, 52, 0,
	 * 0); g.setFont(Font.getFont(0, 0, 8)); break; }
	 * 
	 * default: { ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR); g.fillRect(0, 0,
	 * DisplayUtils.width, oneitemheight + w); ColorUtils.setcolor(g, -3);
	 * g.fillRect(0, DisplayUtils.height - oneitemheight, DisplayUtils.width,
	 * oneitemheight);
	 * 
	 * if(!hasBackButton && MenuScreen.logoImg != null) {
	 * g.drawImage(MenuScreen.logoImg, 2, 1, 0); }
	 * 
	 * if(hasBackButton && backImg != null) { g.drawImage(backImg, 2, 1, 0); }
	 * 
	 * g.drawString(title, 72, 0, 0); } } if(Settings.debugInfo) {
	 * g.setColor(0xffff00); int xx = endx; int yy = endy; if(xx == -1) { xx =
	 * lastx; yy = lasty; } g.drawLine(startx, starty, xx, yy); g.drawRect(startx-2,
	 * starty-2, 4, 4); g.setColor(0xff0000); g.drawRect(endx-2, endy-2, 4, 4);
	 * g.drawString("cs"+scroll + " sc" + scrolled + " d" + drift + " ds" +
	 * driftSpeed + " st" + scrollingTimer + " sp" + scrollPrev + " t" + timer, 0,
	 * 30, 0); } }
	 */

	// events
	public void onLeave() {
		VikaTouch.needstoRedraw=true;
		scrollTargetActive=false;
		//scrolled=0;
	}
}
