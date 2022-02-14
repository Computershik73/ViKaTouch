package vikatouch.screens.temp;


import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.Dialogs;
import vikatouch.VikaTouch;
import vikatouch.items.fm.FileItem;
import vikatouch.items.fm.FileManagerItem;
import vikatouch.items.fm.FolderItem;
import vikatouch.items.fm.FolderLoadNextItem;
import vikatouch.locale.TextLocal;
import vikatouch.screens.ChatScreen;
import vikatouch.screens.MainScreen;
import vikatouch.utils.VikaUtils;

/**
 * @author Shinovon
 * 
 */
public class FileManagerScreen extends ScrollableCanvas {
	
	private ChatScreen chat;
	private FileManagerItem selectedItem;
	private String folder=null;
	private boolean root;
	private FileConnection fileconn;
	public static int len;

	public FileManagerScreen(ChatScreen chat) {
		VikaTouch.needstoRedraw=true;
		this.chat = chat;
		load();
		currentItem = 0;
	}
	
	public void load() {
		VikaTouch.needstoRedraw=true;
		len = 30;
		folder=VikaTouch.folder;
		if (folder==null) {
 		root();
		} else {
			openFolder(folder, 0);
		}
	}
	
	public void press(int x, int y) {
		VikaTouch.needstoRedraw=true;
		super.press(x, y);
	}
	
	public void release(int x, int y) {
		VikaTouch.needstoRedraw=true;
		if(y > DisplayUtils.height - 36) {
			if(x < 36) {
				back();
			} else if(x > DisplayUtils.width - 36) {
				if(selectedItem != null && selectedItem.isImage()) {
					selectedItem.keyPress(-6);
				}
			} else if(x > (DisplayUtils.width - 36) / 2 && x < (DisplayUtils.width - 36) / 2 + 36) {
				if(selectedItem != null && selectedItem.isImage()) {
					send(selectedItem);
				}
			}
		}
		try {
			if (y > 24 && y < DisplayUtils.height - 24) {
				int h = ((PressableUIItem) uiItems.elementAt(0)).getDrawHeight();
				int yy1 = y - (scrolled + 24);
				int i = yy1 / h;
				if (i < 0)
					i = 0;
				if (!dragging) {
					((PressableUIItem) uiItems.elementAt(i)).tap(x, yy1 - (h * i));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		super.release(x, y);
	}
	
	public void press(int key) {
		VikaTouch.needstoRedraw=true;
		if(key == -6) {
			if(uiItems.elementAt(currentItem) != null)
				((PressableUIItem) uiItems.elementAt(currentItem)).keyPress(-6);
		} else if(key == -7) {
			back();
		} else if(key == -5) {
			if(uiItems.elementAt(currentItem) != null)
				((PressableUIItem) uiItems.elementAt(currentItem)).keyPress(-5);
		} else {
			super.press(key);
		}
	}

	private void back() {
		VikaTouch.needstoRedraw=true;
		if(root) {
			this.invalidate();
			VikaTouch.setDisplay(chat, -1);
		} else {
			root();
		}
	}

	private void invalidate() {
		VikaTouch.needstoRedraw=true;
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
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);
		update(g);
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.setFont(Font.getFont(0, 0, 8));
		int y = 24;
		try {
			for(int i = 0; i < uiItems.size(); i++) {
				if(uiItems.elementAt(i) != null) {
					((PressableUIItem) uiItems.elementAt(i)).paint(g, y, scrolled);
					y += ((PressableUIItem) uiItems.elementAt(i)).getDrawHeight();
				}
			}
		} catch (Exception e) {
			
		}
		g.translate(0, -g.getTranslateY());
		ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
		g.fillRect(0, 0, DisplayUtils.width, 24);
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawString(folder, 2, 0, 0);
		if(keysMode) {
			ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
			g.fillRect(0, DisplayUtils.height - 24, DisplayUtils.width, 24);
			g.setColor(0x505050);
			g.drawString(TextLocal.inst.get("back"), DisplayUtils.width - 2 - g.getFont().stringWidth(TextLocal.inst.get("back")), DisplayUtils.height - 18, 0);
			if(uiItems.elementAt(currentItem)  != null && ((FileManagerItem) uiItems.elementAt(currentItem) ).isImage()) {
				g.drawString(TextLocal.inst.get("msg.send"), (DisplayUtils.width - g.getFont().stringWidth(TextLocal.inst.get("msg.send"))) / 2, DisplayUtils.height - 18, 0);
				g.drawString(TextLocal.inst.get("count.view"), 2, DisplayUtils.height - 18, 0);
			} else if(uiItems.elementAt(currentItem)  != null && ((FileManagerItem) uiItems.elementAt(currentItem) ).isDirectory()) {
				g.drawString(TextLocal.inst.get("select"), 0, DisplayUtils.height - 18, 0);
			}
		} else {
			ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
			g.fillRect(0, DisplayUtils.height - 24, DisplayUtils.width, 24);
			g.drawImage(IconsManager.selIco[IconsManager.BACK], 2, DisplayUtils.height - 24, 0);
			if(selectedItem != null && selectedItem.isImage()) {
				g.drawImage(IconsManager.selIco[IconsManager.APPLY], (DisplayUtils.width / 2) - 12, DisplayUtils.height - 24, 0);
				g.drawImage(IconsManager.selIco[IconsManager.PHOTOS], DisplayUtils.width - 26, DisplayUtils.height - 24, 0);
			} else {
				g.drawImage(IconsManager.ico[IconsManager.APPLY], (DisplayUtils.width / 2) - 12, DisplayUtils.height - 24, 0);
				g.drawImage(IconsManager.ico[IconsManager.PHOTOS], DisplayUtils.width - 26, DisplayUtils.height - 24, 0);
			}
		}
	}
	
	private void send(byte[] data) throws IOException, InterruptedException {
		VikaTouch.needstoRedraw=true;
		VikaUtils.sendPhoto(chat.peerId, data, chat.inputText);
		chat.inputText = "";
		VikaTouch.setDisplay(chat, -1);
	}

	protected void scrollHorizontally(int deltaX) {

	}

	public void send(FileManagerItem fileManagerItem) {
		VikaTouch.needstoRedraw=true;
		String path = fileManagerItem.getPath();
		if(!path.startsWith("file://")) {
			path = "file://" + path;
		}
		FileConnection fileconn;
		try {
			fileconn = (FileConnection) Connector.open(path, Connector.READ);
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
		VikaTouch.needstoRedraw=true;
		if(selectedItem != null)
			selectedItem.setSelected(false);
		selectedItem = fileManagerItem;
		selectedItem.setSelected(true);
	}

	public void root() {
		VikaTouch.needstoRedraw=true;
		selectedItem = null;
		root = true;
		folder = "main";
		boolean hasgallery = true;
		boolean hascard = true;
		boolean hasc = true;
		boolean hasroot = true;
		boolean hasdiske = false;
		String gallery = System.getProperty("fileconn.dir.photos");
		String memcard = System.getProperty("fileconn.dir.memorycard");
		String c = "file:///c:/";
		String root = "file:///";
		String e = "file:///e:/";
		if(memcard == null || memcard.length() == 0) {
			memcard = "file:///f:/";
			hascard = true;
			hasdiske = true;
		} else if(memcard.toLowerCase().indexOf("/e:") == -1) {
			hasdiske = true;
		} else {
			hasdiske = true;
			memcard = "file:///f:/";
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
		if(hasdiske) {
			len++;
		}
		if(hasroot) {
			len++;
		}
		uiItems = new Vector(len);
		currentItem = 0;
		//for(int i = 0; i < len; i++) {
			if(hasc) {
				uiItems.addElement(new FolderItem(this, c, "C:"));
				hasc = false;
				//continue;
			}
			if(hasdiske) {
				uiItems.addElement(new FolderItem(this, e, "E:"));
				hasdiske = false;
				//continue;
			}
			if(hascard) {
				uiItems.addElement(new FolderItem(this, memcard, TextLocal.inst.get("fm.memorycard")));
				hascard = false;
				//continue;
			}
			if(hasgallery) {
				uiItems.addElement(new FolderItem(this, gallery, TextLocal.inst.get("fm.gallery")));
				hasgallery = false;
				//continue;
			}
			if(hasroot) {
				uiItems.addElement(new FolderItem(this, root, TextLocal.inst.get("fm.root")));
				hasroot = false;
				//continue;
			}
		//}
		itemsCount = (short) len;

		if(keysMode && uiItems.elementAt(0) != null)
			((PressableUIItem) uiItems.elementAt(0)).setSelected(true);
		
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
	}

	public void openFolder(String path, int offset) {
		VikaTouch.needstoRedraw=true;
		boolean s40 = VikaTouch.needFilePermission();
		selectedItem = null;
		uiItems = new Vector(len);
		currentItem = 0;
		root = false;
		if(path.startsWith("file://"))
			path = path.substring("file://".length());
		folder = path;
		VikaTouch.folder=folder;;
		try {
			fileconn = (FileConnection) Connector.open("file://" + path, Connector.READ);
			Enumeration var3 = fileconn.list("*", true);
			int i = 0;
			boolean loadnext = false;
			for (; var3.hasMoreElements(); i++) {
				if(i < offset)
					continue;
				String var4 = (String) var3.nextElement();
				long var5;
				FileConnection fc = null;
				if(var4.endsWith("/")) {
					uiItems.addElement(new FolderItem(this, path + var4, var4.substring(0, var4.length() - 1)));
				} else if(s40) {
					uiItems.addElement(new FileItem(this, path + var4, var4, 0));
				} else if ((fc = (FileConnection) Connector.open("file://" + path + var4, Connector.READ)).isDirectory()) {
					uiItems.addElement(new FolderItem(this, path + var4, var4.substring(0, var4.length() - 1)));
				} else {
					var5 = fc.fileSize();
					uiItems.addElement(new FileItem(this, path + var4, var4, (int)var5));
				}
				if(fc != null) {
					fc.close();
				}
				if(i + 1 >= len - 1) {
					loadnext = true;
					break;
				}
			}
			if(loadnext) {
				uiItems.setElementAt(new FolderLoadNextItem(this, path), len - 1);
				itemsCount = (short) len;
			} else {
				itemsCount = (short) (i - offset);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(e instanceof IOException || e instanceof IllegalArgumentException) {
				VikaTouch.popup(new InfoPopup(TextLocal.inst.get("fm.noaccess") + "\n" + TextLocal.inst.get("error.additionalinfo") + "\n" + e.toString(), null));
				root();
			} else {
				VikaTouch.popup(new InfoPopup(TextLocal.inst.get("error") + "!\n" + TextLocal.inst.get("error.additionalinfo") + ":\n" + e.toString(), null));
			}
		}
		if(keysMode && uiItems.elementAt(0) != null)
			((PressableUIItem) uiItems.elementAt(0)).setSelected(true);
	}

	public void scrollToSelected() {
		VikaTouch.needstoRedraw=true;
		int itemy = 0;
		for (int i = 0; (i < itemsCount && i < currentItem); i++) {
			itemy += ((PressableUIItem)uiItems.elementAt(i)).getDrawHeight(); // не УМНОЖИТЬ! айтемы могут быть разной высоты.
		}
		if (uiItems.elementAt(currentItem) != null) {
			scrolled = -(itemy - DisplayUtils.height / 2 + (((PressableUIItem)uiItems.elementAt(currentItem)).getDrawHeight() / 2)
					+ MainScreen.topPanelH);
			
		}
	}

	public void selectCentered() {
		VikaTouch.needstoRedraw=true;
	}

	protected void keysScroll(int dir) {
		VikaTouch.needstoRedraw=true;
	}

}
