package vikatouch.items.fm;

import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.locale.TextLocal;
import vikatouch.screens.temp.FileManagerScreen;

public class FolderLoadNextItem extends FolderItem {

	public FolderLoadNextItem(FileManagerScreen fms, String path) {
		super(fms, path, TextLocal.inst.get("loadmore"));
	}
	

	public void tap(int x, int y) {
		fms.openFolder(this.path, FileManagerScreen.len - 1);
	}

	public void keyPressed(int key) {
		if(key == -5 || key == -6) {
			fms.openFolder(this.path, FileManagerScreen.len - 1);
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
		return "";
	}

}
