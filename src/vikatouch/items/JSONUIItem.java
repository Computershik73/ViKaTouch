package vikatouch.items;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.items.PressableUIItem;

/**
 * @author Shinovon
 * 
 */
public abstract class JSONUIItem extends JSONItem implements PressableUIItem {
	public JSONUIItem(JSONObject json) {
		super(json);
		// TODO Auto-generated constructor stub
	}
	public int itemDrawHeight;
	public boolean selected;

	/*public JSONUIItem() {
		super();
	}

	public JSONUIItem(JSONObject json) {
		super(json);
	}*/

	/*public int getDrawHeight() {
		return itemDrawHeight;
	}

	public boolean isSelected() {
		return super();
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setDrawHeight(int i) {
		this.itemDrawHeight = i;
	}

	public void addDrawHeight(int i) {
		this.itemDrawHeight += i;
	}*/
}
