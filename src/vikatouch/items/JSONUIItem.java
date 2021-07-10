package vikatouch.items;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.items.PressableUIItem;
import vikatouch.VikaTouch;

/**
 * @author Shinovon
 * 
 */
public abstract class JSONUIItem extends JSONItem implements PressableUIItem {
	public int itemDrawHeight;
	public boolean selected;

	public JSONUIItem() {
	}

	public JSONUIItem(JSONObject json) {
		super(json);
	}

	public int getDrawHeight() {
		return itemDrawHeight;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		VikaTouch.needstoRedraw=true;
	}

	public void setDrawHeight(int i) {
		this.itemDrawHeight = i;
	}

	public void addDrawHeight(int i) {
		this.itemDrawHeight += i;
	}
}
