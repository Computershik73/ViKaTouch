package vikatouch.items;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import vikatouch.attachments.Attachment;
import vikatouch.json.JSONBase;
import vikatouch.utils.VikaUtils;

public class JSONItem extends JSONBase {
	public JSONItem(JSONObject json) {
		this.json = json;
	}

	public JSONItem() {
	}

	public Attachment[] attachments = new Attachment[5];

	public int fromid;
	public String text;
	public long date;

	public void parseJSON() {
		if (json == null)
			return;
		text = fixJSONString(json.optString("text"));
		fromid = json.optInt("from_id");
		date = json.optLong("date");
	}

	protected void parseAttachments() {
		try {
			if (!json.isNull("attachments")) {
				JSONArray attachments = json.optJSONArray("attachments");
				if (this.attachments.length > attachments.length()) {
					this.attachments = new Attachment[attachments.length()];
				}
				for (int i = 0; i < attachments.length(); i++) {
					if (i >= this.attachments.length) {
						break;
					}
					this.attachments[i] = Attachment.parse(attachments.optJSONObject(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public String getTime() {
		return VikaUtils.parseTime(date);
	}
}
