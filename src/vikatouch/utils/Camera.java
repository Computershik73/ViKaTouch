package vikatouch.utils;

import java.io.IOException;

import javax.microedition.amms.control.camera.FlashControl;
import javax.microedition.amms.control.camera.FocusControl;
import javax.microedition.lcdui.Canvas;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;

import vikatouch.VikaTouch;

public final class Camera {
	private static Player player;
	public static VideoControl videoControl;
	private static Object focusControl;
	private static Object flashControl;

	public static void init(Canvas paramCanvas, boolean selfieOn) throws IOException, MediaException {
		if (player == null) {
			if (selfieOn) {
				player = Manager.createPlayer("capture://devcam1");
			} else {
				try {
					try {
						if (VikaTouch.mobilePlatform.indexOf("S60") != -1) {
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
			}
			player.realize();
		}
		if (videoControl == null) {
			videoControl = (VideoControl) player.getControl("VideoControl");
			videoControl.initDisplayMode(1, paramCanvas);
		}
		try {
			focusControl = (FocusControl) player.getControl("javax.microedition.amms.control.camera.FocusControl");
				/*
				 * if(((FocusControl)focusControl).isMacroSupported()) {
				 * ((FocusControl)focusControl).setMacro(true); }
				 */
			if (((FocusControl) focusControl).isAutoFocusSupported()) {
				((FocusControl) focusControl).setFocus(FocusControl.AUTO);
			}
		} catch (Throwable e) {
			VikaTouch.sendLog("focuscontrol " + e.toString()+" " +focusControl);
		}

		try {
			flashControl = (FlashControl) player.getControl("javax.microedition.amms.control.camera.FlashControl");
			((FlashControl) flashControl).setMode(FlashControl.AUTO);
		} catch (Throwable e) {
			VikaTouch.sendLog("flashcontrol " + e.toString()+" " +flashControl);
		}
	}

	public static void macrooff() throws MediaException {
		if (focusControl != null && ((FocusControl) focusControl).isMacroSupported()) {
			((FocusControl) focusControl).setMacro(false);
		}
	}

	public static void macroon() throws MediaException {
		if (focusControl != null && ((FocusControl) focusControl).isMacroSupported()) {
			((FocusControl) focusControl).setMacro(true);
		}
	}

	public static void autofocus() throws MediaException {
		if (focusControl != null && ((FocusControl) focusControl).isAutoFocusSupported()) {
			((FocusControl) focusControl).setFocus(FocusControl.AUTO);
		}
	}

	public static void show(int width, int height, int i) throws MediaException {
		videoControl.setDisplaySize(width, height - i);
		videoControl.setDisplayLocation((width - videoControl.getDisplayWidth()) / 2, 0);
		videoControl.setVisible(true);
		player.prefetch();
		player.start();
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
			arrayOfByte = videoControl.getSnapshot(/* "width=" + width + "&height=" + height */null);
		} catch (SecurityException localSecurityException) {
			arrayOfByte = null;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			arrayOfByte = videoControl.getSnapshot(null);
		}
		return arrayOfByte;
	}

	public static boolean setFlash(boolean flashOn) throws Exception {
		if (flashControl == null) {
			return false;
		}
		if (flashOn) {
			((FlashControl) flashControl).setMode(FlashControl.FORCE);
			return true;
		} else {
			((FlashControl) flashControl).setMode(FlashControl.OFF);
			return false;
		}
	}
}