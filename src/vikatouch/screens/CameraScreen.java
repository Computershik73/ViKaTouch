package vikatouch.screens;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.media.MediaException;

import ru.nnproject.vikaui.screen.VikaScreen;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.utils.Camera;
import vikatouch.utils.VikaUtils;

public class CameraScreen extends VikaScreen {

	private boolean failed;
	private boolean takenPhoto;
	private boolean takePhotoFailed;
	private Image img;
	private ChatScreen chat;

	public CameraScreen(ChatScreen chatScreen) {
		this.chat = chatScreen;
		try {
			Camera.init(VikaTouch.canvas);
			Camera.show(DisplayUtils.width, DisplayUtils.height, 50);
		} catch (Exception e) {
			failed = true;
			e.printStackTrace();
		}
	}

	public void draw(Graphics g) {
		g.setColor(0);
		g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);
		g.setColor(0x505050);
		g.setFont(Font.getFont(0, 0, 8));
		if (!takenPhoto && !takePhotoFailed) {
			g.drawImage(IconsManager.ico[IconsManager.CAMERA], (DisplayUtils.width - 24) / 2, DisplayUtils.height - 38, 0);
		}
		g.drawImage(IconsManager.ico[IconsManager.BACK], 0, DisplayUtils.height - 38, 0);
		if (failed) {
			g.drawString("Access fail.-", 0, 0, 0);
		}
		if (takenPhoto) {
			g.drawImage(img, 0, 0, 0);
			g.drawString("Send", DisplayUtils.width - (g.getFont().stringWidth("Send") + 2), DisplayUtils.height - (g.getFont().getHeight() + 2), 0);
		}
		if (takePhotoFailed) {
			g.drawString("Fail.", 0, 0, 0);
		}
	}

	public void press(int key) {
		if (key == -5) {
			if(!failed) {
				if(!takenPhoto && !takePhotoFailed) {
					takePhoto();
				} else if(takenPhoto && !takePhotoFailed) {
					
				}
			}
		}

		if (key == -7) {
			if(takenPhoto && !takePhotoFailed) {
				send();
			}
		}
		if (key == -6) {
			VikaTouch.setDisplay(chat, 1);
		}
	}

	private void send() {
		try {
			VikaUtils.sendPhoto(chat.peerId, VikaUtils.photoData, chat.inputText);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		chat.inputText = "";
		VikaUtils.photoData = null;
		VikaTouch.setDisplay(chat, 1);
	}

	public void release(int x, int y) {
		if (x > DisplayUtils.width / 2 - 25 && x < DisplayUtils.width / 2 + 25 && y > DisplayUtils.height - 50
				&& !failed && !takenPhoto && !takePhotoFailed) {
			takePhoto();
		}
		if (x < 50 && y > DisplayUtils.height - 50) {
			try {
				Camera.stop();
			} catch (MediaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			VikaTouch.setDisplay(chat, 1);
		}
	}

	private void takePhoto() {
		byte[] arrayOfByte = null;
		try {
			arrayOfByte = Camera.takeSnapshot(DisplayUtils.width, DisplayUtils.height);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if ((arrayOfByte != null) && (arrayOfByte.length > 0)) {
			VikaUtils.photoData = arrayOfByte;
			takenPhoto = true;
			img = Image.createImage(VikaUtils.photoData, 0, arrayOfByte.length);
		} else {
			takePhotoFailed = true;
		}
		try {
			Camera.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onLeave() {
		try {
			Camera.stop();
		} catch (MediaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
