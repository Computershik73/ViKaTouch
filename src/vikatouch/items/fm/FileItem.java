package vikatouch.items.fm;

import java.io.IOException;

import javax.microedition.io.file.FileConnection;

import vikatouch.screens.temp.FileManagerScreen;

public class FileItem extends FileManagerItem {

	public FileItem(FileManagerScreen fms, String path, String name, int size) {
		super(fms, path, name, size);
	}

	public FileItem(FileManagerScreen fms, String path, String name) {
		super(fms, path, name);
	}

	public FileItem(FileManagerScreen fms, String path) {
		super(fms, path);
	}

	public FileItem(FileManagerScreen fms, FileConnection fileConn) throws IOException {
		super(fms, fileConn);
	}

}
