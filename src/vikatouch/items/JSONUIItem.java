package vikatouch.items;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.items.PressableUIItem;

public abstract class JSONUIItem
	extends JSONItem
	implements PressableUIItem
{
	public int itemDrawHeight;
	public boolean selected;

	public JSONUIItem(JSONObject json)
	{
		super(json);
	}
	
	public int getDrawHeight()
	{
		return itemDrawHeight;
	}
	
	public boolean isSelected()
	{
		return selected;
	}
	
	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}
	
	public void setDrawHeight(int i)
	{
		this.itemDrawHeight = i;
	}
	
	public void addDrawHeight(int i)
	{
		this.itemDrawHeight += i;
	}
}
