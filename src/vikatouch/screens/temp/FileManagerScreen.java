package vikatouch.screens.temp;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.items.fm.FileItem;
import vikatouch.items.fm.FileManagerItem;
import vikatouch.items.fm.FolderItem;
import vikatouch.locale.TextLocal;
import vikatouch.screens.ChatScreen;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.emulatordetect.EmulatorDetector;

public class FileManagerScreen extends ScrollableCanvas {
	
	private ChatScreen chat;
	private FileManagerItem selectedItem;
	private String folder;
	private String sep;
	private boolean root;
	private FileConnection fileconn;
	private int openCount;

	public FileManagerScreen(ChatScreen chat) {
		this.chat = chat;
		load();
	}
	
	public void load() {
		root();
	}
	
	public void press(int x, int y) {
		super.press(x, y);
	}
	
	public void release(int x, int y) {
		if(y > DisplayUtils.height - 36) {
			if(x < 36) {
				back();
			} else if(x > DisplayUtils.width - 36) {
				if(uiItems[currentItem] != null)
					uiItems[currentItem].keyPressed(-7);
			} else if(x > (DisplayUtils.width - 36) / 2) {
				if(uiItems[currentItem] != null)
					uiItems[currentItem].keyPressed(-5);
			}
		}
		try {
			if (y > 24 && y < DisplayUtils.height - 24) {
				int h = uiItems[0].getDrawHeight();
				int yy1 = y - (scrolled + 24);
				int i = yy1 / h;
				if (i < 0)
					i = 0;
				if (!dragging) {
					uiItems[i].tap(x, yy1 - (h * i));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		super.release(x, y);
	}
	
	public void press(int key) {
		if(key == -6) {
			back();
		} else if(key == -7) {
			if(uiItems[currentItem] != null)
				uiItems[currentItem].keyPressed(-7);
		} else if(key == -5) {
			if(uiItems[currentItem] != null)
				uiItems[currentItem].keyPressed(-5);
		} else {
			super.press(key);
		}
	}

	private void back() {
		if(root) {
			this.invalidate();
			VikaTouch.setDisplay(chat, -1);
		} else {
			root();
		}
	}

	private void invalidate() {
		if(fileconn != null) {
			try {
				fileconn.close();
			} catch (IOException e) {
			}
			fileconn = null;
		}
		uiItems = null;
	}

	public void draw(Graphics g) {
		itemsh = itemsCount * (DisplayUtils.compact ? 24 : 50);
		g.setColor(-1);
		g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);
		update(g);
		g.setColor(0);
		g.setFont(Font.getFont(0, 0, 8));
		int y = 24;
		for(int i = 0; i < itemsCount; i++) {
			if(uiItems[i] != null) {
				uiItems[i].paint(g, y, scrolled);
				y += uiItems[i].getDrawHeight();
			}
		}
		g.translate(0, -g.getTranslateY());
		g.setColor(-1);
		g.fillRect(0, 0, DisplayUtils.width, 24);
		g.setColor(0);
		g.drawString(folder, 2, 0, 0);
		//g.drawString("Connector.open count: " + openCount, 2, 240, 0);
		if(keysMode) {
			g.setColor(-1);
			g.fillRect(0, DisplayUtils.height - 24, DisplayUtils.width, 24);
			g.setColor(0x505050);
			g.drawString(TextLocal.inst.get("back"), 2, DisplayUtils.height - 18, 0);
			if(uiItems[currentItem] != null && ((FileManagerItem) uiItems[currentItem]).isImage()) {
				g.drawString(TextLocal.inst.get("msg.send"), (DisplayUtils.width - g.getFont().stringWidth(TextLocal.inst.get("msg.send"))) / 2, DisplayUtils.height - 18, 0);
				g.drawString(TextLocal.inst.get("count.view"), DisplayUtils.width - 2 - g.getFont().stringWidth(TextLocal.inst.get("count.view")), DisplayUtils.height - 18, 0);
			} else if(uiItems[currentItem] != null && ((FileManagerItem) uiItems[currentItem]).isDirectory()) {
				g.drawString(TextLocal.inst.get("select"), DisplayUtils.width - 2 - g.getFont().stringWidth(TextLocal.inst.get("select")), DisplayUtils.height - 18, 0);
			}
		} else {
			g.setColor(-1);
			g.fillRect(0, DisplayUtils.height - 24, DisplayUtils.width, 24);
			g.drawImage(IconsManager.ico[IconsManager.BACK], 2, DisplayUtils.height - 24, 0);
			if(selectedItem != null && selectedItem.isImage()) {
				g.drawImage(IconsManager.ico[IconsManager.APPLY], (DisplayUtils.width / 2) - 12, DisplayUtils.height - 24, 0);
				g.drawImage(IconsManager.ico[IconsManager.PHOTOS], DisplayUtils.width - 26, DisplayUtils.height - 24, 0);
			}
		}
	}
	
	private void send(byte[] data) throws IOException, InterruptedException {
		VikaUtils.sendPhoto(chat.peerId, data, chat.inputText);
		chat.inputText = "";
		VikaTouch.setDisplay(chat, -1);
	}

	protected void scrollHorizontally(int deltaX) {

	}

	public void send(FileManagerItem fileManagerItem) {
		String path = fileManagerItem.getPath();
		if(!path.startsWith("file://")) {
			path = "file://" + path;
		}
		try {
			if(fileconn == null) {
				fileconn = (FileConnection) Connector.open(path);
				openCount++;
			} else
				fileconn.setFileConnection(path);
			DataInputStream dis;
			dis = fileconn.openDataInputStream();
			byte[] bytes = new byte[(int) fileconn.fileSize()];
			dis.readFully(bytes, 0, (int) fileconn.fileSize());
			dis.close();
			send(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void selected(FileManagerItem fileManagerItem) {
		selectedItem = fileManagerItem;
	}

	public void root() {
		root = true;
		folder = "main";
		sep = System.getProperty("file.separator");
		boolean hasgallery = true;
		boolean hascard = true;
		boolean hasc = true;
		String gallery = System.getProperty("fileconn.dir.photos");
		String memcard = System.getProperty("fileconn.dir.memorycard");
		String c = "file:///c:/";
		if(memcard == null || memcard.length() == 0) {
			hascard = false;
		}
		if(gallery == null || gallery.length() == 0) {
			hasgallery = false;
		}
		int len = 0;
		if(hasc) {
			len++;
		}
		if(hascard) {
			len++;
		}
		if(hasgallery) {
			len++;
		}
		uiItems = new PressableUIItem[len];
		for(int i = 0; i < len; i++) {
			if(hasc) {
				uiItems[i] = new FolderItem(this, c, "C:");
				hasc = false;
				continue;
			}
			if(hascard) {
				uiItems[i] = new FolderItem(this, memcard, "Memory card");
				hascard = false;
				continue;
			}
			if(hasgallery) {
				uiItems[i] = new FolderItem(this, gallery, "Gallery");
				hasgallery = false;
				continue;
			}
		}
		itemsCount = (short) len;

		if(keysMode && uiItems[0] != null)
			uiItems[0].setSelected(true);
	}

	public void openFolder(String path) {
		boolean createNew = false;
		root = false;
		if(path.startsWith("file://"))
			path = path.substring("file://".length());

		if(folder == null)
			createNew = true;
		else if(path.indexOf(folder) == -1)
			createNew = true;
		String parent = null;
		try {
			String z = fileconn.getPath().replace('\\', '/');
			String y = //z.substring(z.lastIndexOf('/'))
					z;
			int x = y.lastIndexOf('/');
			if(x == -1)
				x = 0;
			parent = fileconn.getPath().substring(x)+"/"+VikaUtils.replace(path, folder, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(folder.indexOf(path) != -1) {
			parent = "";
			createNew = false;
		}
		folder = path;
		System.out.println("set "+path+ " parent:" + parent+".");
		boolean s40 = VikaTouch.isS40() && !EmulatorDetector.isEmulator;
		try {
			boolean neww = true;
			try {
				if(fileconn == null || createNew || parent == null) {
					System.out.println(path);
					fileconn = (FileConnection) Connector.open("file://" + path);
					openCount++;
				} else {
					neww = false;
					System.out.println("not new "+parent);
					fileconn.setFileConnection(parent);
				}
			} catch (IllegalArgumentException e) {
				System.out.println(path);
				fileconn = (FileConnection) Connector.open("file://" + path);
				openCount++;
			}

			Enumeration var3 = fileconn.list();
			int i = 0;
			uiItems = new PressableUIItem[25];
			for (; var3.hasMoreElements(); i++) {
				String var4 = (String) var3.nextElement();
				long var5;
				if(i == 0) {
					String y = path.substring(0,path.lastIndexOf('/'));
					/*if(!neww) {
						y = y.substring(0,y.lastIndexOf('/'));
					}*/
					System.out.println("y "+y);
					int x = y.lastIndexOf('/')+1;
					fileconn.setFileConnection(path.substring(x)+var4);
				} else
					if(var4.startsWith("/"))
						fileconn.setFileConnection(var4.substring(1));
					else
						fileconn.setFileConnection(var4);
				if(var4.endsWith("/")) {
					uiItems[i] = new FolderItem(this, path + var4, var4.substring(0, var4.length() - 1));
				} else /* if(s40) {
					uiItems[i] = new FileItem(this, path + var4, var4, 0);
				} else */if (fileconn.isDirectory()) {
					uiItems[i] = new FolderItem(this, path + var4, var4.substring(0, var4.length() - 1));
				} else {
					var5 = fileconn.fileSize();
					uiItems[i] = new FileItem(this, path + var4, var4, (int)var5);
				}
			}

			fileconn.setFileConnection("");
			itemsCount = (short) i;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(keysMode && uiItems[0] != null)
			uiItems[0].setSelected(true);
	}

}
