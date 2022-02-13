package vikatouch.items;

import javax.microedition.lcdui.Graphics;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.items.PressableUIItem;
import vikatouch.VikaTouch;
import vikatouch.attachments.Attachment;
import vikatouch.json.JSONBase;
import vikatouch.utils.VikaUtils;

/**
 * @author Shinovon
 * 
 */
public abstract class JSONItem extends JSONBase implements PressableUIItem  {
	public int itemDrawHeight;
	public boolean selected;

	public JSONItem() {
	}

	/*public JSONItem(JSONObject json) {
		super(json);
	}*/

	public int getDrawHeight() {
		return itemDrawHeight;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setDrawHeight(int i) {
		this.itemDrawHeight = i;
	}

	public void addDrawHeight(int i) {
		this.itemDrawHeight += i;
	}
	
	
	public JSONItem(JSONObject json) {
		this.json = json;
	}

	

	public Attachment[] attachments = new Attachment[15];

	public int fromid;
	public String text;
	public long date;

	public void parseJSON() {
		if (json == null) {
			text = "Ошибка";
			fromid = 0;
			date = 0;
			VikaTouch.needstoRedraw=true;
			VikaTouch.canvas.serviceRepaints();
			return;
		}
		text = fixJSONString(json.optString("text"));
		fromid = json.optInt("from_id");
		date = json.optLong("date");
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
	}

	protected void parseAttachments() {
		try {
			if (!json.isNull("attachments")) {
				JSONArray attachments = json.optJSONArray("attachments");
				if (this.attachments.length > attachments.length()) {
					this.attachments = new Attachment[attachments.length()];
					//setDrawHeight(1);
				}
				for (int i = 0; i < attachments.length(); i++) {
					if (i >= this.attachments.length) {
						break;
					}
					this.attachments[i] = Attachment.parse(attachments.optJSONObject(i));
					addDrawHeight(this.attachments[i].getDrawHeight());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
	}

	public String getTime() {
		return VikaUtils.parseTime(date);
	}

	
}
