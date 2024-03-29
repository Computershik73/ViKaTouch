package vikatouch.utils;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import tube42.lib.imagelib.ImageUtils;
import vikatouch.settings.Settings;

/**
 * @author Shinovon
 * 
 */
public class ResizeUtils {

	

	public static Image resizeava(Image img) {
		short h = (short) img.getHeight();
		short need = h;
		/*
		 * switch(DisplayUtils.idispi) { case DisplayUtils.DISPLAY_E6: case
		 * DisplayUtils.DISPLAY_PORTRAIT: case DisplayUtils.DISPLAY_ALBUM: { need = 50;
		 * break; }
		 * 
		 * case DisplayUtils.DISPLAY_S40: case DisplayUtils.DISPLAY_ASHA311: case
		 * DisplayUtils.DISPLAY_EQWERTY: { need = 25; break; }
		 * 
		 * default: { need = 50; break; } }
		 */
		need = 50;
		if (h != need) {
			return ImageUtils.resize(img, need, need, !Settings.fastImageScaling, !Settings.fastImageScaling);
			// return VikaUtils.resize(img, need, -1);
		}
		return img;
	}

	public static Image resizeChatAva(Image img) {
		
		short h = (short) img.getHeight();
		short need = h;
		/*
		 * switch(DisplayUtils.idispi) { case DisplayUtils.DISPLAY_E6: case
		 * DisplayUtils.DISPLAY_PORTRAIT: case DisplayUtils.DISPLAY_ALBUM: { need = 50;
		 * break; }
		 * 
		 * case DisplayUtils.DISPLAY_S40: case DisplayUtils.DISPLAY_ASHA311: case
		 * DisplayUtils.DISPLAY_EQWERTY: { need = 50; break; }
		 * 
		 * default: { need = 50; break; } }
		 */
		need = 50;
		if (h != need) {
			return ImageUtils.resize(img, need, need, !Settings.fastImageScaling, !Settings.fastImageScaling);
			// return VikaUtils.resize(img, need, -1);
		}
		return img;
	}

	public static Image prepareForCaching(Image img) {
		short w = (short) img.getWidth();
		short h = (short) img.getHeight();
		short need = h;
		/*
		 * if(h == 50) need = 48;
		 */
		if (h != need) {
			short needh = getHeight(w, h, need);
			return ImageUtils.resize(img, need, needh, !Settings.fastImageScaling, !Settings.fastImageScaling);
			// return VikaUtils.resize(img, need, -1);
		}
		return img;
	}

	private static short getHeight(short w, short h, short need) {
		return (short) (need * h / w);
	}

	public static Image resizeItemPreview(Image img) {
		short w = (short) img.getWidth();
		short h = (short) img.getHeight();
		short need = h;
		/*
		 * switch(DisplayUtils.idispi) { case DisplayUtils.DISPLAY_E6: case
		 * DisplayUtils.DISPLAY_PORTRAIT: case DisplayUtils.DISPLAY_ALBUM: { need = 48;
		 * break; }
		 * 
		 * case DisplayUtils.DISPLAY_S40: case DisplayUtils.DISPLAY_ASHA311: case
		 * DisplayUtils.DISPLAY_EQWERTY: { need = 48; break; }
		 * 
		 * case DisplayUtils.DISPLAY_UNDEFINED: default: { need = 48; break; } }
		 */
		need = 50;
		if (h != need) {
			short needh = getHeight(w, h, need);
			return ImageUtils.resize(img, need, needh, !Settings.fastImageScaling, !Settings.fastImageScaling);
			// return VikaUtils.resize(img, need, -1);
		}
		return img;
	} 
	
	public static void drawRectWithEmptyCircleInside(Graphics g, int r, int gg, int b, int x, int y, int radius) {
		
		//if (Settings.nightTheme) {
		//	g.setColor(0, 0, 0);
		//} else {
			g.setColor(r, gg, b);
		//}
		int xx = x;
		int yy = y;
		//int i=0;
		while (yy<2*radius+y) {
			while (xx<2*radius+x) {
			
				if (((xx-(x+radius))*(xx-(x+radius))+(yy-(y+radius))*(yy-(y+radius)))>radius*radius) {
						g.drawLine(xx, yy, xx, yy);
						
				}
			xx++;
			}
			xx=x;
			yy++;
		}
		xx=x;
		yy=y;
	}
	
	
	
}
