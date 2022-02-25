package vikatouch.screens;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;

import vikatouch.Dialogs;
import vikatouch.NokiaUIInvoker;
import vikatouch.VikaTouch;
import vikatouch.items.VikaNotification;
import vikatouch.items.chat.ConversationItem;
import vikatouch.locale.TextLocal;
import vikatouch.screens.menu.MenuScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.IntObject;
import vikatouch.utils.ProfileObject;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.url.URLBuilder;

/**
 * @author Shinovon
 * 
 */
public class DialogsScreen extends MainScreen {

	public static String titleStr;
	private int cw;
	
	public DialogsScreen() {
		super();
		VikaTouch.needstoRedraw=true;
		// VikaTouch.loading = true;

		if (VikaTouch.menuScr == null) {
			VikaTouch.menuScr = new MenuScreen();
		}
		if (titleStr == null) {
			titleStr = TextLocal.inst.get("title.chats");
		}
	}

	protected final void callRefresh() {
		// VikaTouch.loading = true;
		VikaTouch.needstoRedraw=true;
		Dialogs.refreshDialogsList(true, false);
		VikaTouch.needstoRedraw=true;
		// VikaTouch.loading = false;
	}

	public final void press(int key) {
		VikaTouch.needstoRedraw=true;
		if (key != -12 && key != -20) {
			keysMode = true;
		}
		if (key == -5) {
			
			Dialogs.dialogs[currentItem].keyPress(-5);
		} else if (key == -6) {
			callRefresh();
			repaint();
		} else if (key == -1) {
			up();
		} else if (key == -2) {
			down();
		} else
			super.press(key);
		
		VikaTouch.needstoRedraw=true;
	}

	protected final void up() {
		VikaTouch.needstoRedraw=true;
		try {
			Dialogs.dialogs[currentItem].setSelected(false);
		} catch (Exception e) {

		}
		currentItem--;
		if (currentItem < 0) {
			currentItem = 0;
					//Dialogs.itemsCount--;
		}
		try {
			scrollToSelected();
			Dialogs.dialogs[currentItem].setSelected(true);
			VikaTouch.needstoRedraw=true;
		} catch (Exception e) {

		}
	}

	protected final void down() {
		VikaTouch.needstoRedraw=true;
		try {
			Dialogs.dialogs[currentItem].setSelected(false);
			VikaTouch.needstoRedraw=true;
		} catch (Exception e) {

		}
		currentItem++;
		if (currentItem >= Dialogs.itemsCount-1) {
			currentItem = Dialogs.itemsCount-1;
			
		}
		/*if (currentItem >= Dialogs.itemsCount-1) {
			currentItem = Dialogs.itemsCount-2;
			VikaTouch.needstoRedraw=true;
			Dialogs.itemsCount+=10;
			//Settings.dialogsLength=Dialogs.itemsCount;
			Dialogs.loadMore();
			VikaTouch.needstoRedraw=true;
		}*/
		try {
		scrollToSelected();
		Dialogs.dialogs[currentItem].setSelected(true);
		} catch (Throwable eee) {
			
		} 
		VikaTouch.needstoRedraw=true;
	}

	

	public final void scrollToSelected() {
		VikaTouch.needstoRedraw=true;
		int itemy = 0;
		for (int i = 0; (i < Dialogs.dialogs.length && i < currentItem); i++) {
			itemy += Dialogs.dialogs[i].getDrawHeight(); // не УМНОЖИТЬ! айтемы могут быть разной высоты.
		}
		if (Dialogs.dialogs[currentItem] != null) {
			scrolled = -(itemy - DisplayUtils.height / 2 + (Dialogs.dialogs[currentItem].getDrawHeight() / 2)
					+ MainScreen.topPanelH);
			//if (scrolled>)
		}
	}

	public void draw(Graphics g) {
		
		if (Dialogs.dialogs == null) {
			return;
		}
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.setFont(Font.getFont(0, 0, 8));
		itemsh = Dialogs.itemsCount * 63;
		double multiplier = (double) DisplayUtils.height / 640.0;
		double ww = 10.0 * multiplier;
		int w = (int) ww;
		// try
		// {
		try {
		update(g);
		} catch (Throwable eee) {}
		int y = oneitemheight + w;
		
		if (Dialogs.dialogs != null) {
			int i=0;
			String coords= "";
			while (true) {
			//for (int i = 0; i < Dialogs.itemsCount; i++) {
				if (i>Dialogs.itemsCount-1) {
					break;
				}
				try {
					
					if (Dialogs.dialogs[i] != null) {
						Dialogs.dialogs[i].paint(g, y, scrolled);
						coords+=" , i: "+String.valueOf(i)+", y+g.getTranslateY(): "+String.valueOf(y+g.getTranslateY());
						cw = Dialogs.dialogs[i].itemDrawHeight;
						if ((y+g.getTranslateY()<DisplayUtils.height - MainScreen.bottomPanelH) && (y+g.getTranslateY()>=DisplayUtils.height - MainScreen.bottomPanelH - cw-10)  && (i>=Dialogs.itemsCount-1) && (Dialogs.isUpdatingNow==false)) {
							coords.concat(", loaded more"+String.valueOf(itemsCount)+ " items , ");
							Dialogs.itemsCount+=10;
							Dialogs.loadMore();
						}
						y += Dialogs.dialogs[i].itemDrawHeight;
						
						
					}
				} catch (Throwable e) {
					//e.printStackTrace();
					// VikaTouch.error(e, ErrorCodes.DIALOGSITEMDRAW);
					coords+=e.getMessage();
					break;
				}
				i++;
			}
			coords+=" DisplayUtils.height-MainScreen.bottomPanelH = "+String.valueOf(DisplayUtils.height - MainScreen.bottomPanelH) + " DisplayUtils.height-MainScreen.bottomPanelH-cw-10 ="+String.valueOf(DisplayUtils.height - MainScreen.bottomPanelH- cw-10);
			//VikaTouch.sendLog(coords);
		}
		
		g.translate(0, -g.getTranslateY());

		/*
		 * } catch (Throwable e) { VikaTouch.error(e, ErrorCodes.DIALOGSDRAW);
		 * e.printStackTrace(); }
		 */
	}

	public final void drawHUD(Graphics g) {
		drawHUD(g, Dialogs.dialogs == null ? TextLocal.inst.get("title2.loading") : titleStr); // временно, потом оно будет грузиться
																					// во время сплеша. Привет Илье))0)
	}

	public void unselectAll() {
		VikaTouch.needstoRedraw=true;
		if (Dialogs.selected) {
			for (int i = 0; i < Dialogs.itemsCount; i++) {
				if (Dialogs.dialogs[i] != null) {
					Dialogs.dialogs[i].selected = false;
				}
			}
			Dialogs.selected = false;
		}
		VikaTouch.needstoRedraw=true;
	}

	public final void release(int x, int y) {
		VikaTouch.needstoRedraw=true;
		// тача больше нигде нет. Ладно.
		try {
			if (Dialogs.dialogs != null) {
				if (y > MainScreen.topPanelH && y < DisplayUtils.height - MainScreen.bottomPanelH) {
					int yy1 = (y - MainScreen.topPanelH) - scrolled;
					int i = yy1 / cw;
					if (i < 0)
						i = 0;
					unselectAll();
					if (!dragging) {
						Dialogs.dialogs[i].tap(x, yy1 - (63 * i));
					} else {
						//if ((yy1-(63*i))>DisplayUtils.height - MainScreen.bottomPanelH-cw) 
					}
					Dialogs.dialogs[i].released(dragging);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		VikaTouch.needstoRedraw=true;
		super.release(x, y);
	}

	public final void press(int x, int y) {
		VikaTouch.needstoRedraw=true;
		try {
			if (Dialogs.dialogs != null) {
				if (y > MainScreen.topPanelH && y < DisplayUtils.height - MainScreen.bottomPanelH) {
					int yy1 = (y - MainScreen.topPanelH) - scrolled;
					int i = yy1 / cw;
					if (i < 0)
						i = 0;
					unselectAll();
					if (Dialogs.dialogs[i] != null) {
						Dialogs.dialogs[i].pressed();
					}
					this.serviceRepaints();
					VikaTouch.needstoRedraw=true;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		super.press(x, y);
		VikaTouch.needstoRedraw=true;
	}

	protected void scrollHorizontally(int deltaX) {

	}

}
