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
	int butx;
	int buty;
	int backX;
	int backY;

	public CameraScreen(ChatScreen chatScreen) {
		lwidth = DisplayUtils.width;
		this.chat = chatScreen;
		butx=(DisplayUtils.width - 24) / 2;
		buty=DisplayUtils.height - 38;
		backX=0;
		backY=DisplayUtils.height - 38;
		//buty=360 - 38;
		try {
			Camera.init(VikaTouch.canvas);
			Camera.show(DisplayUtils.width, DisplayUtils.height, 50);
		} catch (Exception e) {
			failed = true;
			error = VikaUtils.replace(VikaUtils.replace(e.toString(), "Exception", ""), "javax.microedition.media.", "");
			e.printStackTrace();
		}
	}

	public void draw(Graphics g) {
		DisplayUtils.checkdisplay();
		
		if(DisplayUtils.width != lwidth && !failed && !takenPhoto && !takePhotoFailed) {
			onLeave();
			try {
				Camera.init(VikaTouch.canvas);
				Camera.show(DisplayUtils.width-50, DisplayUtils.height, 50);
				if (vikatouch.utils.Camera.videoControl.getSourceWidth()<vikatouch.utils.Camera.videoControl.getSourceHeight()) {
					butx=(DisplayUtils.width - 24) / 2;
					buty=DisplayUtils.height - 38;
				} else {
					butx= (DisplayUtils.width - 38);
					buty=(DisplayUtils.height-24)/2;
					backX=(DisplayUtils.width - 38);
					backY=DisplayUtils.height-38;
				}
			} catch (Exception e) {
				failed = true;
				error = VikaUtils.replace(VikaUtils.replace(e.toString(), "Exception", ""), "javax.microedition.media.", "");
				e.printStackTrace();
			}
			lwidth = DisplayUtils.width;
		}
		
		g.setColor(0);
		g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);
		g.setColor(0x505050);
		g.setFont(Font.getFont(0, 0, 8));
		if (!takenPhoto && !takePhotoFailed) {
			//if (vikatouch.utils.Camera.videoControl.getSourceWidth()<vikatouch.utils.Camera.videoControl.getSourceHeight()) {
			//g.drawImage(IconsManager.ico[IconsManager.CAMERA], (DisplayUtils.width - 24) / 2, DisplayUtils.height - 38, 0);
			//} else {
			//	g.drawImage(IconsManager.ico[IconsManager.CAMERA], (DisplayUtils.width - 38), (DisplayUtils.height-24)/2 , 0);
			//}
			g.drawImage(IconsManager.ico[IconsManager.CAMERA], butx, buty , 0);
		}
		
		g.drawImage(IconsManager.ico[IconsManager.BACK], backX, backY, 0);
		if (failed) {
			g.drawString("Access fail. "+error, 0, 0, 0);
		}
		if (takenPhoto) {
			if(img != null)
				g.drawImage(img, 0, 0, 0);
			/*if(keysMode) {
				g.drawString("Send", DisplayUtils.width - (g.getFont().stringWidth("Send") + 2), DisplayUtils.height - (g.getFont().getHeight() + 2), 0);
			} else{*/
			g.drawImage(IconsManager.ico[IconsManager.REPOST], DisplayUtils.width - 25, DisplayUtils.height - 38, 0);
		}
		if (takePhotoFailed) {
			g.drawString("Fail. "+error, 0, 0, 0);
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
			Camera.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		final int l = chat.peerId;
		VikaTouch.loading = true;
		VikaTouch.setDisplay(chat, 1);
		img= null;
		new Thread() {
			public void run() {
				VikaTouch.loading = true;
				try {
					VikaTouch.loading = true;
					VikaUtils.sendCameraPhoto(l);
					VikaTouch.loading = false;
				} catch (Throwable e) {
					VikaTouch.sendLog("camerasend: " +e.toString());
				}
				VikaTouch.loading = false;
			}
		}.start();
	}

	public void release(int x, int y) {
		if (x > butx - 25 && x < butx + 75 && y > buty - 50 && y< buty+50
				&& !failed && !takenPhoto && !takePhotoFailed) {
			takePhoto();
			return;
		}
		if(y > DisplayUtils.height - 50 && x > DisplayUtils.width - 50 && takenPhoto && !takePhotoFailed) {
			send();
			return;
		}
		if ((x < backX && y > backY - 50 && backX<100) ||  (x > backX &&  y > backY - 50 && backY>DisplayUtils.height - 50)) {
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
			error = e.toString();
			e.printStackTrace();
		}

		try {
			Camera.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((arrayOfByte != null) && (arrayOfByte.length > 0)) {
			VikaUtils.photoData = arrayOfByte;
			takenPhoto = true;
			img = ImageUtils.resize(Image.createImage(VikaUtils.photoData, 0, arrayOfByte.length), DisplayUtils.width, DisplayUtils.height, false, false);
		} else {
			takePhotoFailed = true;
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
