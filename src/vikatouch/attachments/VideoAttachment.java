package vikatouch.attachments;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.items.chat.CommentItem;
import vikatouch.items.chat.MsgItem;
import vikatouch.items.menu.VideoItem;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.url.URLBuilder;

/**
 * @author Feodor0090
 * 
 */
public class VideoAttachment extends ImageAttachment {
	public VideoAttachment() {
		this.type = "video";
	}

	public int albumid;
	public long ownerid;
	public int userid = 100;
	public int id;
	public String key;
	public int origwidth;
	public int origheight;
	public PhotoSize[] sizes = new PhotoSize[10];
	public String title;

	// for msg
	public int renderH;
	public int renderW;
	public Image renderImg = null;
	public String previewurl;

	public void parseJSON() {
		// VikaUtils.logToFile(json.toString());
		//sizes = PhotoSize.parseSizes(json.optJSONArray("image"));
		origwidth = json.optInt("width");
		origheight = json.optInt("height");
		id = json.optInt("id");
		key = json.optString("access_key");
		ownerid = json.optInt("owner_id");
		albumid = json.optInt("album_id");
		userid = json.optInt("user_id");
		title = json.optString("title");
	}

	public PhotoSize getMessageImage() {
		//return PhotoSize.searchSmallerSize(sizes,
			//	Math.min((int) (DisplayUtils.width * 0.6), MsgItem.msgWidth - MsgItem.attMargin * 2));
		return PhotoSize.searchNearestSize(sizes,
				Math.min((int) (DisplayUtils.width), MsgItem.msgWidth - MsgItem.attMargin * 2));
	}

	public void loadForMessage() {
		try {
			//PhotoSize ps = getMessageImage();
			previewurl = fixJSONString(json.optString("photo_320"));
			Image i = VikaUtils.downloadImage(previewurl);
			
			
			int w = Math.min((int) (DisplayUtils.width * 0.6), MsgItem.msgWidth - MsgItem.attMargin * 2);
			if (i.getWidth() > w) {
				i = VikaUtils.resize(i, w, -1);
			}

			renderH = i.getHeight();
			renderW = i.getWidth();
			renderImg = i;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public PhotoSize getCommentImage() {
		return PhotoSize.searchSmallerSize(sizes,
				Math.min((int) (DisplayUtils.width * 0.6), CommentItem.msgWidth - CommentItem.attMargin * 2));
	}

	public void loadForComment() {
		try {
			//PhotoSize ps = getMessageImage();
			previewurl = fixJSONString(json.optString("photo_320"));
			Image i = VikaUtils.downloadImage(previewurl);
			int w = Math.min((int) (DisplayUtils.width * 0.6), CommentItem.msgWidth - CommentItem.attMargin * 2);
			if (i.getWidth() > w) {
				i = VikaUtils.resize(i, w, -1);
			}

			renderH = i.getHeight();
			renderW = i.getWidth();
			renderImg = i;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadForNews() {
		try {
			previewurl = fixJSONString(json.optString("photo_320"));
			//PhotoSize ps = PhotoSize.searchSmallerSize(sizes, DisplayUtils.width);
			Image i = VikaUtils.downloadImage(previewurl);
			renderH = i.getHeight();
			renderW = i.getWidth();
			renderImg = i;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getDrawHeight() {
		return renderH + Font.getFont(0, 0, 8).getHeight();
	}

	public void press() {
		try {
			String x = VikaUtils.download(new URLBuilder("video.get").addField("videos",
					String.valueOf(ownerid) + "_" + id + (key == null ? "" : ("_" + key))));
			JSONObject r = new JSONObject(x).getJSONObject("response").getJSONArray("items").getJSONObject(0);
			VideoItem i = new VideoItem(r);
			i.parseJSON();
			i.keyPress(-5);
		} catch (Exception e) {
			VikaTouch.sendLog("Video att press: " + e.toString());
		}
	}

	public Image getPreviewImage() {
		return null;
	}

	public Image getFullImage() {
		return null;
	}

	public Image getImage(int height) {
		return null;
	}

}
