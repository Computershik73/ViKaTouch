package vikatouch.items.fm;

import java.io.IOException;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.popup.ImagePreview;
import vikatouch.screens.temp.FileManagerScreen;

/**
 * @author Shinovon
 * 
 */
public abstract class FileManagerItem implements PressableUIItem {

	protected FileManagerScreen fms;
	private boolean selected;
	private int drawHeight;
	protected String path;
	private String displayString;
	private int size;
	
	public FileManagerItem(FileManagerScreen fms, String path, String name, int size) {
		this.fms = fms;
		this.path = path;
		this.displayString = name;
		this.size = size;
		this.drawHeight = DisplayUtils.compact ? 24 : 50;
	}
	
	public FileManagerItem(FileManagerScreen fms, String path, String name) {
		this(fms, path, name, 0);
	}
	
	public FileManagerItem(FileManagerScreen fms, String path) {
		this(fms, path, null);
	}
	
	public FileManagerItem(FileManagerScreen fms, FileConnection fc) throws IOException {
		this(fms, fc.getPath(), fc.getName(), (int) fc.fileSize());
	}

	public void paint(Graphics g, int y, int scrolled) {
		g.setFont(Font.getFont(0, 0, 8));
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawString(displayString, 36, y + 1, 0);
		g.drawString(getSizeString() ,DisplayUtils.width - 2 - g.getFont().stringWidth(getSizeString()), y+2, 0);
		g.drawImage(IconsManager.ico[getIcon()], 2, y + (drawHeight / 2 - 12), 0);
		if(selected) {
			ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
			g.drawRect(0, y, DisplayUtils.width-1, drawHeight);
			g.drawRect(1, y+1, DisplayUtils.width-3, drawHeight-2);
		}
	}

	protected int getIcon() {
		if(isImage()) {
			return IconsManager.PHOTOS;
		} else {
			return IconsManager.DOCS;
		}
	}

	public boolean isImage() {
		return path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".gif");
	}

	public int getDrawHeight() {
		return this.drawHeight;
	}

	public void addDrawHeight(int i) {
		this.drawHeight += i;
	}

	public void setDrawHeight(int i) {
		this.drawHeight = i;
	}

	public void tap(int x, int y) {
		fms.selected(this);
	}
	
	protected String getSizeString() {
		if(size == 0)
			return "";
		return size / 1024 + "K";
	}

	public void keyPress(int key) {
		if(key == -5) {
			if(isImage())
				fms.send(this);
		} else if(key == -6) {
			if(isImage())
				preview();
		}
	}

	protected void preview() {
		
		VikaTouch.popup(new ImagePreview(this.path, "Фото: "));
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.repaint();
		
		//VikaTouch.appInst.platformRequest(this.path);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getPath() {
		return path;
	}

	public boolean isDirectory() {
		return false;
	}

}
