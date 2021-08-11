package vikatouch.items;

import vikatouch.VikaTouch;

public class smile {
	public int smilePos;
	public String smilePath;
	public int smileX = 0;
	public int smileY = 0;
	public smile(int smPos, String smPath) {
		smilePos=smPos;
		smilePath=smPath;
	}
	public smile (int smPos, String smPath, int smX, int smY) {
		smilePos=smPos;
		smilePath=smPath;
		smileX=smX;
		smileY=smY;
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
	}
}
