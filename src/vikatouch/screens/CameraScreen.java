package vikatouch.screens;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
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

	public CameraScreen() {
		try {
			Camera.init(VikaTouch.canvas);
			Camera.show(DisplayUtils.width, DisplayUtils.height, 50);
		} catch (Exception e) {
			failed = true;
			e.printStackTrace();
		}
	}

	public void draw(Graphics g) {
		g.setColor(0xffffff);
		g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);
		g.drawImage(IconsManager.ico[IconsManager.CAMERA], (DisplayUtils.width - 24) / 2, DisplayUtils.height - 38, 0);
		g.drawImage(IconsManager.ico[IconsManager.BACK], 0, DisplayUtils.height - 38, 0);
		if (failed) {
			g.drawString("Access fail.-", 0, 0, 0);
		}
		if (takenPhoto) {
			g.drawImage(img, 0, 0, 0);
		}
		if (takePhotoFailed) {
			g.drawString("Fail.", 0, 0, 0);
		}
	}

	public void press(int key) {
		if (key == Canvas.FIRE && !failed && !takenPhoto && !takePhotoFailed) {
			takePhoto();
		}
		if (key == -7) {
			VikaTouch.inst.cmdsInst.command(14, this);
		}
	}

	public void release(int x, int y) {
		if (x > DisplayUtils.width / 2 - 25 && x < DisplayUtils.width / 2 + 25 && y > DisplayUtils.height - 50
				&& !failed && !takenPhoto && !takePhotoFailed) {
			takePhoto();
		}
		if (x < 50 && y > DisplayUtils.height - 50) {
			VikaTouch.inst.cmdsInst.command(14, this);
		}
	}

	private void takePhoto() {
		byte[] arrayOfByte = null;
		try {
			arrayOfByte = Camera.takeSnapshot(DisplayUtils.width, DisplayUtils.height);
		} catch (Throwable localThrowable) {
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
		} catch (Exception localException2) {
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
