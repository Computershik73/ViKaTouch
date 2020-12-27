package vikatouch.utils;
import java.io.IOException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;

import vikatouch.VikaTouch;

public final class Camera {
	private static Player player;
	private static VideoControl videoControl;

	public static void init(Canvas paramCanvas) throws IOException, MediaException {
		if (player == null) {
			try {
				try {
					if(VikaTouch.mobilePlatform.indexOf("S60") != -1 ) {
						player = Manager.createPlayer("capture://video");
					} else {
						player = Manager.createPlayer("capture://image");
					}
				} catch (Exception e) {
					player = Manager.createPlayer("capture://image");
				}
			} catch (Exception e) {
				player = Manager.createPlayer("capture://video");
			}
			player.realize();
		}
		if (videoControl == null) {
			videoControl = (VideoControl) player.getControl("VideoControl");
			videoControl.initDisplayMode(1, paramCanvas);
		}
	}

	public static void show(int width, int height, int i) throws MediaException {
		videoControl.setDisplaySize(width, height - i);
		videoControl.setDisplayLocation((width - videoControl.getDisplayWidth()) / 2, 0);
		videoControl.setVisible(true);
		player.prefetch();
		player.start();
		VikaTouch.sendLog("camera show start " + videoControl.toString() + " source: " + videoControl.getSourceWidth()+"x" + videoControl.getSourceHeight() + " display: " + videoControl.getDisplayWidth()+"x"+videoControl.getDisplayHeight());
	}

	public static void stop() throws MediaException {
		if (videoControl != null) {
			videoControl.setVisible(false);
			videoControl = null;
		}
		if (player != null) {
			try {
				player.stop();
			} catch (Exception localException1) {
			}
			try {
				player.deallocate();
			} catch (Exception localException2) {
			}
			try {
				player.close();
			} catch (Exception localException3) {
			}
			player = null;
		}
	}

	public static byte[] takeSnapshot(int width, int height) throws MediaException {
		byte[] arrayOfByte = null;
		try {
			arrayOfByte = videoControl
					.getSnapshot(/*"width=" + width + "&height=" + height*/null);
		} catch (SecurityException localSecurityException) {
			arrayOfByte = null;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			arrayOfByte = videoControl.getSnapshot(null);
		}
		return arrayOfByte;
	}
}