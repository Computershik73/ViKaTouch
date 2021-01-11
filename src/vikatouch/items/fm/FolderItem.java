package vikatouch.items.fm;

import javax.microedition.io.file.FileConnection;

import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.screens.temp.FileManagerScreen;

/**
 * @author Shinovon
 * 
 */
public class FolderItem extends FileManagerItem {
	
	public FolderItem(FileManagerScreen fms, String path, String name) {
		super(fms, path, name, 0);
	}
	
	public FolderItem(FileManagerScreen fms, FileConnection fc) {
		super(fms, fc.getPath(), fc.getName(), 0);
	}

	public void tap(int x, int y) {
		fms.openFolder(this.path, 0);
	}

	public void keyPress(int key) {
		if(key == -5 || key == -6) {
			fms.openFolder(this.path, 0);
		}
	}

	public boolean isImage() {
		return false;
	}

	public boolean isDirectory() {
		return true;
	}

	protected int getIcon() {
		return IconsManager.REPOST;
	}
	
	protected String getSizeString() {
		return "DIR";
	}

}
