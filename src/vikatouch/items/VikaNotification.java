package vikatouch.items;

import javax.microedition.io.Connector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;

import ru.nnproject.vikaui.screen.VikaScreen;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import vikatouch.VikaTouch;
import vikatouch.settings.Settings;

/**
 * @author Feodor0090
 * 
 */
public class VikaNotification {

	public int type;

	public String title;
	public String text;
	public VikaScreen screen;

	public static final int COMMON = 0;
	public static final int NEW_MSG = 1;
	public static final int NEXT_TRACK = 2;
	public static final int ERROR = 3;

	public static Image[] icons;

	public boolean active = true;
	public long sendTime;

	public static int nofX, nofH;

	public VikaNotification(int _type, String _title, String _text, VikaScreen _screen) {
		type = _type;
		title = _title;
		text = _text;
		screen = _screen;
		sendTime = System.currentTimeMillis();
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
	}

	public void draw(Graphics g) {
		VikaTouch.needstoRedraw=true;
		
		VikaTouch.canvas.serviceRepaints();
		VikaTouch.needstoRedraw=true;
		if (icons == null || icons.length != 4) {
			icons = new Image[] { IconsManager.ico[IconsManager.INFO], IconsManager.ico[IconsManager.MSGS],
					IconsManager.ico[IconsManager.MUSIC], IconsManager.ico[IconsManager.CLOSE] };
		}
		if (active) {
			long t = System.currentTimeMillis() - sendTime;
			if (t > 3000L) {
				active = false;
				return;
			}
			Font f = Font.getFont(0, 0, 8);
			int fh = f.getHeight();
			int y = (t < 2500L) ? 0 : (int) (-((t - 2500L) / 500.0f) * fh * 2);

			int left = nofX = 20;
			nofH = fh * 2 + y;

			ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
			g.fillRect(left, y, DisplayUtils.width - left * 2, fh * 2);
			ColorUtils.setcolor(g, ColorUtils.TEXT);
			g.drawRect(left, y, DisplayUtils.width - left * 2, fh * 2);
			g.drawImage(icons[type], left + 4, y + fh - 12, 0);
			//text = TextBreaker.shortText(text, DisplayUtils.width - x, Font.getFont(0, 0, 8));
			g.drawString(TextBreaker.shortText(text, DisplayUtils.width - left - 55, Font.getFont(0, 0, 8)), left + 32, y + fh, 0);
			ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
			g.drawString(TextBreaker.shortText(title, DisplayUtils.width - left - 55, Font.getFont(0, 0, 8)), left + 32, y, 0);
		}
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
	}

	public void open() {
		if (screen != null && screen != VikaTouch.canvas.currentScreen)
			VikaTouch.setDisplay(screen, 1);
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
	}

	public static void vib() {
		if(Settings.notifmode == 0) {
			return;
		}
		new Thread() {
			public void run() {
				try {
					if (Settings.notifmode == 1) {
						Display d = Display.getDisplay(VikaTouch.appInst);
						d.vibrate(1000);

						// Thread.sleep(100);
						// d.vibrate(1000);
						// Thread.sleep(100);
						// d.vibrate(1000);
					} else if (Settings.notifmode == 2) {
						Player notifplayer;
						if(Settings.qualityNotif)
							notifplayer = Manager.createPlayer(Connector.openInputStream("http://vikamobile.ru:80/music/bb2.mp3"), "audio/mpeg");
						else
							notifplayer = Manager.createPlayer(getClass().getResourceAsStream("/c.mp3"), "audio/mpeg");
						notifplayer.realize();
						try {
							((VolumeControl) notifplayer.getControl("VolumeControl")).setLevel(100);
						} catch (Throwable e) {
						}

						notifplayer.start();
					} else if (Settings.notifmode == 3) {
						Player notifplayer = Manager.createPlayer("device://tone");
						notifplayer.realize();
						notifplayer.start();
					} 
				} catch (Exception e) {
				}
			}
		}.start();
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
	}
}
