package vikatouch;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

import ru.nnproject.kemulator.filemanagerapi.AbstractFileManager;
import ru.nnproject.kemulator.filemanagerapi.FileManagerAPI;
import ru.nnproject.vikaui.popup.InfoPopup;
import vikatouch.screens.ChatScreen;
import vikatouch.screens.temp.FileManagerScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;

/**
 * @author Shinovon
 * 
 */
public class VikaFileManager {
	
	private static List list;

	public static void chatPhoto(final ChatScreen chat) {
		try {
			if (System.getProperty("kemulator.filemanagerapi.version") != null) {
				AbstractFileManager fm = FileManagerAPI.getInstance("Открыть файл", FileManagerAPI.NATIVE);
				fm.setFilterExtensions(new String[] { ".jpg", ".jpeg", ".png" }, "Любое изображение");
				if (fm.openFile()) {
					FileConnection fc = fm.getFileConnection();
					DataInputStream in = fc.openDataInputStream();
					int len = (int) fc.fileSize();
					byte[] var5 = new byte[len];
					in.readFully(var5, 0, len);
					VikaUtils.sendPhoto(chat.peerId, var5, chat.inputText);
					chat.inputText = "";
					return;
				} else {
					return;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if (Settings.oldlcduiFm) {
			list = VikaUtils.selectPhoto("main");
			VikaTouch.setDisplay(list);
			final Command back = new Command("Назад", 2, 0);
			final Command dirBack = new Command("Назад", 2, 0);
			final Command preview = new Command("Предпросмотр", 8, 1);
			list.addCommand(back);
			list.addCommand(List.SELECT_COMMAND);
			list.addCommand(preview);
			list.setCommandListener(new CommandListener() {

				public void commandAction(Command arg0, Displayable arg1) {
					if (arg0 == List.SELECT_COMMAND) {
						int var194 = list.getSelectedIndex();
						int var196;
						byte[] var5 = null;
						ByteArrayOutputStream var201 = new ByteArrayOutputStream();

						try {
							FileConnection var197 = (FileConnection) Connector
									.open(String.valueOf(VikaUtils.filesVector.elementAt(var194)), 1);
							// заходить в папку
							if (var197.isDirectory()) {
								list = VikaUtils.selectPhoto(var197.getURL());
								VikaTouch.setDisplay(list);
								list.addCommand(dirBack);
								list.addCommand(List.SELECT_COMMAND);
								list.addCommand(preview);
								list.setCommandListener(this);
								return;
							}
							DataInputStream var220 = null;
							if (!var197.exists()) {
								list.append("File " + var197.getName() + " doesn't exist!", (Image) null);
							} else {
								var196 = (int) var197.fileSize();
								if (var196 > 600 * 1024) {
									// файл весит больше 600 кб
									VikaTouch.popup(
											new InfoPopup("Фото весит более 600кб, и не может быть отправлено.", null));
									return;
								}
								var220 = var197.openDataInputStream();
								var5 = new byte[var196];
								var220.readFully(var5, 0, var196);
							}
							var220.close();
							var201.close();
							VikaUtils.sendPhoto(chat.peerId, var5, chat.inputText);
							chat.inputText = "";
							list = null;
							VikaTouch.setDisplay(VikaTouch.canvas);
						} catch (Exception e) {
						}
					} else if (arg0 == back) {
						VikaTouch.setDisplay(VikaTouch.canvas);
					} else if (arg0 == preview) {
						int var194 = list.getSelectedIndex();
						try {
							VikaTouch.appInst.platformRequest((String) VikaUtils.filesVector.elementAt(var194));
						} catch (Exception e) {
						}
						return;
					} else if (arg0 == dirBack) {
						list = VikaUtils.selectPhoto("main");

						VikaTouch.setDisplay(list);
						list.addCommand(back);
						list.addCommand(List.SELECT_COMMAND);
						list.addCommand(preview);
						list.setCommandListener(this);
						return;
					}
				}

			});
		} else {
			VikaTouch.setDisplay(new FileManagerScreen(chat), 1);
		}
	}

}
