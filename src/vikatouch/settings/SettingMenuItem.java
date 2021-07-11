package vikatouch.settings;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.menu.items.OptionItem;
import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.popup.AutoContextMenu;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.locale.TextLocal;

/**
 * @author Feodor0090
 * 
 */
public class SettingMenuItem implements PressableUIItem, IMenu {

	public SettingsScreen ss;
	public String text;
	public int icon;
	public int h;
	public int optN;
	boolean s;
	static Font f;
	static Font sf;
	public String[] opts;
	public int currentOption;
	String help;
	public boolean noyes;

	public int drawX;
	public int fillW; // for context menu

	public SettingMenuItem(SettingsScreen s, String title, int ic, int optN, int h, int[] list, int curr, String info) {
		//VikaTouch.needstoRedraw=true;
		this.h = h;
		this.optN = optN;
		icon = ic;
		text = title;
		ss = s;
		opts = new String[list.length];
		for (int i = 0; i < list.length; i++) {
			opts[i] = String.valueOf(list[i]);
		}
		currentOption = curr;
		help = info;
		f = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		sf = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
		//VikaTouch.needstoRedraw=true;
	}

	public SettingMenuItem(SettingsScreen s, String title, int ic, int optN, int h, String[] list, int curr,
			String info) {
		//VikaTouch.needstoRedraw=true;
		this.h = h;
		this.optN = optN;
		icon = ic;
		text = title;
		ss = s;
		opts = list;
		currentOption = curr;
		help = info;
		f = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		sf = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
		//VikaTouch.needstoRedraw=true;
	}

	public SettingMenuItem(SettingsScreen s, String title, int ic, int optN, int h, String[] list, int curr,
			String info, boolean noyesset) {
		//VikaTouch.needstoRedraw=true;
		this.h = h;
		this.optN = optN;
		icon = ic;
		text = title;
		ss = s;
		opts = list;
		currentOption = curr;
		help = info;
		f = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		sf = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
		noyes = noyesset;
		//VikaTouch.needstoRedraw=true;
	}

	public void paint(Graphics g, int y, int scrolled) {
		//VikaTouch.needstoRedraw=true;
		ColorUtils.setcolor(g, (ScrollableCanvas.keysMode && s) ? ColorUtils.BUTTONCOLOR : ColorUtils.TEXT);
		g.setFont((ScrollableCanvas.keysMode && s) ? sf : f);
		int x = drawX + 48;
		if (icon == -1)
			x = x - 40;
		if (icon != -1)
			g.drawImage(((ScrollableCanvas.keysMode && s) ? (IconsManager.selIco) : (IconsManager.ico))[icon],
					drawX + 12, y + (h / 2 - 12), 0);
		try {
			g.drawString(text, x, y + ((h / 4) - (((ScrollableCanvas.keysMode && s) ? sf : f).getHeight() / 2)), 0);
			ColorUtils.setcolor(g, ColorUtils.TEXT2);
			g.setFont(f);
			g.drawString(opts[currentOption], x, y + ((h * 3 / 4) - (f.getHeight() / 2)), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getDrawHeight() {
		return h;
	}

	public void tap(int x, int y) {
		//VikaTouch.needstoRedraw=true;
		keyPress(-5);
	}

	public void keyPress(int key) {
		//VikaTouch.needstoRedraw=true;
		if (key == KEY_OK) {
			if(noyes) {
				currentOption = currentOption == 0 ? 1 : 0;
				ss.settingSet(optN, currentOption);
			} else {
				OptionItem[] po = new OptionItem[opts.length];
				for (int i = 0; i < opts.length; i++) {
					if (noyes) {
						po[i] = new OptionItem(this, opts[i], (i == 0) ? IconsManager.CLOSE : IconsManager.APPLY, i, 40);
					} else {
						po[i] = new OptionItem(this, opts[i],
								(currentOption == i) ? IconsManager.APPLY : IconsManager.SETTINGS, i, 40);
					}
				}
				VikaTouch.popup(new AutoContextMenu(po));
			}
		} else if (help != null && key == KEY_FUNC) {
			VikaTouch.popup(new InfoPopup(help, null, TextLocal.inst.get("settings.help"), null));
		}
	}

	public boolean isSelected() {
		return s;
	}

	public void setSelected(boolean selected) {
		//VikaTouch.needstoRedraw=true;
		s = selected;
	}

	public void addDrawHeight(int i) {
		h += i;
	}

	public void setDrawHeight(int i) {
		h = i;
	}

	public void onMenuItemPress(int i) {
		ss.settingSet(optN, i);
		currentOption = i;
	}

	public void onMenuItemOption(int i) {
		if (help != null) {
			VikaTouch.popup(new InfoPopup(help, null, TextLocal.inst.get("settings.help"), null));
			VikaTouch.needstoRedraw=true;
		}
	}
}
