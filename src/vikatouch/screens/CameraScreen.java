package vikatouch.screens;

import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.media.MediaException;

import ru.nnproject.vikaui.screen.VikaScreen;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import tube42.lib.imagelib.ImageUtils;
import vikatouch.VikaTouch;
import vikatouch.utils.Camera;
import vikatouch.utils.VikaUtils;

public class CameraScreen extends VikaScreen {

	private boolean failed;
	private boolean takenPhoto;
	private boolean takePhotoFailed;
	private Image img;
	private ChatScreen chat;
	private String error;
	private short lwidth;
	private boolean symbianPortrait;
	private Image back;
	private Image cam;
	private Image flash;
	private Image flashnot;
	private Image flashyes;
	private Image rotate;
	private Image selfie;
	private boolean flashOn;
	private boolean selfieOn;
	private boolean autofocus;

	public CameraScreen(ChatScreen chatScreen) {
		lwidth = DisplayUtils.width;
		this.chat = chatScreen;
		try {
			flashnot = Image.createImage("/flashoff.png");
			flashyes = Image.createImage("/flash.png");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		flash = flashnot;
		if (DisplayUtils.width == 360 || (DisplayUtils.width == 640 && DisplayUtils.height == 360)) {
			back = IconsManager.ico[IconsManager.BACK];
			rotate = IconsManager.ico[IconsManager.ANSWER];
			selfie = IconsManager.ico[IconsManager.FRIENDS];
			cam = IconsManager.ico[IconsManager.CAMERA];
		}
		if (DisplayUtils.width == 360) {
			rotateall(Sprite.TRANS_ROT270);
			symbianPortrait = true;
		}
		try {
			Camera.init(VikaTouch.canvas, selfieOn);
			int x = 0;
			int y = 50;
			if (DisplayUtils.width == 640) {
				x = 50;
				y = 0;
			}
			Camera.show(DisplayUtils.width - x, DisplayUtils.height, y);
		} catch (Throwable e) {
			failed = true;
			VikaTouch.sendLog("eee " + e.toString());
			error = VikaUtils.replace(VikaUtils.replace(e.toString(), "Exception", ""), "javax.microedition.media.",
					"");
			e.printStackTrace();
		}
		repaint();
	}

	private void rotateall(int rot) {
		back = Image.createImage(back, 0, 0, back.getWidth(), back.getHeight(), rot);
		cam = Image.createImage(cam, 0, 0, cam.getWidth(), cam.getHeight(), rot);
		rotate = Image.createImage(rotate, 0, 0, rotate.getWidth(), rotate.getHeight(), rot);
		selfie = Image.createImage(selfie, 0, 0, selfie.getWidth(), selfie.getHeight(), rot);
		flashnot = Image.createImage(flashnot, 0, 0, flashnot.getWidth(), flashnot.getHeight(), rot);
		flashyes = Image.createImage(flashyes, 0, 0, flashyes.getWidth(), flashyes.getHeight(), rot);
		flash = Image.createImage(flash, 0, 0, flash.getWidth(), flash.getHeight(), rot);

	}

	public void draw(Graphics g) {
		DisplayUtils.checkdisplay();

		int dh = DisplayUtils.height;
		if (DisplayUtils.width != lwidth && !failed && !takenPhoto && !takePhotoFailed) {
			onLeave();
			try {
				Camera.init(VikaTouch.canvas, selfieOn);
				int x = 0;
				int y = 50;
				if (DisplayUtils.width == 640) {
					x = 50;
					y = 0;
				}
				Camera.show(DisplayUtils.width - x, DisplayUtils.height, y);
				if (flashOn)
					flashOn = Camera.setFlash(flashOn);
			} catch (Throwable e) {
				failed = true;
				error = VikaUtils.replace(VikaUtils.replace(e.toString(), "Exception", ""), "javax.microedition.media.",
						"");
				e.printStackTrace();
			}
			lwidth = DisplayUtils.width;
		}

		g.setColor(0);
		g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);
		g.setColor(0x505050);
		g.setFont(Font.getFont(0, 0, 8));
		if (!takenPhoto && !takePhotoFailed) {
			// if
			// (vikatouch.utils.Camera.videoControl.getSourceWidth()<vikatouch.utils.Camera.videoControl.getSourceHeight())
			// {
			// g.drawImage(IconsManager.ico[IconsManager.CAMERA], (DisplayUtils.width - 24)
			// / 2, DisplayUtils.height - 38, 0);
			// } else
			if (DisplayUtils.width == 640 && DisplayUtils.height == 360) {
				g.drawImage(rotate, DisplayUtils.width - 38, 12, 0);
				g.drawImage(selfie, DisplayUtils.width - 38, dh / 4, 0);
				g.drawImage(cam, DisplayUtils.width - 38, (dh - 24) / 2, 0);
				g.drawImage(flash, DisplayUtils.width - 38, dh - dh / 4 - 24, 0);
				g.drawImage(back, DisplayUtils.width - 38, dh - 36, 0);
			} else/*
					 * if(DisplayUtils.width == 240 || DisplayUtils.width == 320 ||
					 * DisplayUtils.width == 360)
					 */ {
				g.drawImage(flash, 14, DisplayUtils.height - 38, 0);
				g.drawImage(IconsManager.ico[IconsManager.BACK], DisplayUtils.width - 36, DisplayUtils.height - 38, 0);
				g.drawImage(IconsManager.ico[IconsManager.CAMERA], (DisplayUtils.width - 24) / 2,
						(DisplayUtils.height - 38), 0);
			}
			// }
			// g.drawImage(IconsManager.ico[IconsManager.CAMERA], butx, buty , 0);
		}

		if (failed) {
			g.drawString("Access fail. " + error, 0, 0, 0);
		}
		if (takenPhoto) {
			if (img != null)
				g.drawImage(img, 0, 0, 0);
			/*
			 * if(keysMode) { g.drawString("Send", DisplayUtils.width -
			 * (g.getFont().stringWidth("Send") + 2), DisplayUtils.height -
			 * (g.getFont().getHeight() + 2), 0); } else{
			 */
			g.drawImage(IconsManager.ico[IconsManager.REPOST], DisplayUtils.width - 25, DisplayUtils.height - 38, 0);
			g.drawImage(IconsManager.ico[IconsManager.BACK], 0, DisplayUtils.height - 38, 0);
		}
		if (takePhotoFailed) {
			g.drawString("Fail. " + error, 0, 0, 0);
		}
	}

	public void press(int key) {
		if (key == -5) {
			if (!failed) {
				if (!takenPhoto && !takePhotoFailed) {
					takePhoto();
				} else if (takenPhoto && !takePhotoFailed) {

				}
			}
		}

		if (key == -7) {
			if (takenPhoto && !takePhotoFailed) {
				send();
			}
		}

		if (!takenPhoto && !takePhotoFailed) {
			if (key == -6) {

				flashOn = !flashOn;
				setflash(true);
			}
			if (key == -7) {
				stop();
				VikaTouch.setDisplay(chat, 1);
			}
		} else if (takenPhoto && !takePhotoFailed) {
			if (key == -6) {
				stop();
				VikaTouch.setDisplay(chat, 1);
			}
		}
	}

	private void setflash(boolean b) {
		try {
			if (b || flashOn)
				flashOn = Camera.setFlash(flashOn);
		} catch (Exception e) {
			flashOn = false;
			VikaTouch.sendLog("setflash " + e.toString());
			e.printStackTrace();
		}
		if (flashOn) {
			flash = flashyes;
		} else {
			flash = flashnot;
		}

	}

	private void send() {
		stop();
		final int l = chat.peerId;
		VikaTouch.loading = true;
		VikaTouch.setDisplay(chat, 1);
		img = null;
		new Thread() {
			public void run() {
				VikaTouch.loading = true;
				try {
					VikaTouch.loading = true;
					VikaUtils.sendCameraPhoto(l);
					VikaTouch.loading = false;
				} catch (Throwable e) {
					VikaTouch.sendLog("camerasend: " + e.toString());
				}
				VikaTouch.loading = false;
			}
		}.start();
	}

	private void stop() {
		try {
			Camera.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void press(int x, int y) {
		if (!autofocus && !failed && !takenPhoto && !takePhotoFailed && x < DisplayUtils.width - 50
				&& y < DisplayUtils.height - 50 && x > 50 && y > 50) {
			try {
				Camera.macroon();
			} catch (MediaException e) {
				VikaTouch.sendLog("macrop " + e.toString());
				e.printStackTrace();
			}
		}
	}

	public void release(int x, int y) {
		if (failed) {
			if (y > DisplayUtils.height - 50) {
				stop();
				VikaTouch.setDisplay(chat, 1);
				return;
			}
		}

		if (!failed && !takenPhoto && !takePhotoFailed && x < DisplayUtils.width - 50 && y < DisplayUtils.height - 50
				&& x > 50 && y > 50) {
			if (autofocus) {
				try {
					Camera.autofocus();
					return;
				} catch (MediaException e) {
					VikaTouch.sendLog("autofocus " + e.toString());
					e.printStackTrace();
				}
			} else {
				try {
					Camera.macrooff();
					return;
				} catch (MediaException e) {
					VikaTouch.sendLog("macror " + e.toString());
					e.printStackTrace();
				}
			}
		}
		if (DisplayUtils.width == 640 && DisplayUtils.height == 360) {
			if (x > DisplayUtils.width - 50) {
				if (y > ((DisplayUtils.height - 24) / 2 - 12) && y < ((DisplayUtils.height - 24) / 2 + 36) && !failed
						&& !takenPhoto && !takePhotoFailed) {
					takePhoto();
					return;
				}
				if (y < 50) {
					symbianPortrait = !symbianPortrait;
					if (symbianPortrait) {
						rotateall(Sprite.TRANS_ROT270);
					} else {
						rotateall(Sprite.TRANS_ROT90);
					}

					try {
						Camera.init(VikaTouch.canvas, selfieOn);
						int xx = 0;
						int yy = 50;
						if (DisplayUtils.width == 640) {
							xx = 50;
							yy = 0;
						}
						Camera.show(DisplayUtils.width - xx, DisplayUtils.height, yy);
						setflash(false);
					} catch (Throwable e) {

						VikaTouch.sendLog("rotate reopen " + e.toString());
						failed = true;
						stop();
						error = VikaUtils.replace(VikaUtils.replace(e.toString(), "Exception", ""),
								"javax.microedition.media.", "");
						e.printStackTrace();
					}
					return;
				}
				if (y > DisplayUtils.height / 4 - 12 && y < DisplayUtils.height / 4 + 36) {
					System.out.println("selfiecam");
					selfieOn = !selfieOn;
					flashOn = false;
					stop();
					try {
						Camera.init(VikaTouch.canvas, selfieOn);
						Camera.show(DisplayUtils.width - 50, DisplayUtils.height, 0);
					} catch (Exception e) {
						VikaTouch.sendLog("selfie reopen " + e.toString());
						selfieOn = false;
						failed = true;
						try {
							Camera.stop();
						} catch (MediaException e1) {
							e1.printStackTrace();
						}
						error = VikaUtils.replace(VikaUtils.replace(e.toString(), "Exception", ""),
								"javax.microedition.media.", "");
						e.printStackTrace();
					}
					return;
				}
				if (y > DisplayUtils.height - DisplayUtils.height / 4 - 36
						&& y < DisplayUtils.height - DisplayUtils.height / 4 + 12) {
					System.out.println("flash");

					flashOn = !flashOn;

					setflash(true);
					return;
				}
			}
		} else if (x > DisplayUtils.width / 2 - 25 && x < DisplayUtils.width / 2 + 25 && y > DisplayUtils.height - 50
				&& !failed && !takenPhoto && !takePhotoFailed) {
			takePhoto();
			return;
		}
		if (y > DisplayUtils.height - 50 && x > DisplayUtils.width - 50) {
			if (takenPhoto && !takePhotoFailed) {
				send();
				return;
			} else if (!failed && !takenPhoto && !takePhotoFailed) {
				stop();
				VikaTouch.setDisplay(chat, 1);
			}
		}
		if (DisplayUtils.width != 640 && x < 50 && y > DisplayUtils.height - 50) {
			stop();
			VikaTouch.setDisplay(chat, 1);
		}
	}

	private void takePhoto() {
		byte[] arrayOfByte = null;
		try {
			arrayOfByte = Camera.takeSnapshot(DisplayUtils.width, DisplayUtils.height);
		} catch (Throwable e) {

			error = e.toString();
			e.printStackTrace();
		}

		stop();
		if ((arrayOfByte != null) && (arrayOfByte.length > 0)) {
			VikaUtils.photoData = arrayOfByte;
			takenPhoto = true;
			img = ImageUtils.resize(Image.createImage(VikaUtils.photoData, 0, arrayOfByte.length), DisplayUtils.width,
					DisplayUtils.height, false, false);
		} else {
			takePhotoFailed = true;
		}
	}

	public void onLeave() {
		stop();
	}

}
