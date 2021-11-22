package vikatouch.attachments;

import java.io.IOException;

import javax.microedition.lcdui.Image;

import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;
import vikatouch.items.chat.CommentItem;
import vikatouch.items.chat.MsgItem;
import vikatouch.popup.ImagePreview;
import vikatouch.utils.VikaUtils;

/**
 * @author Shinovon
 * 
 */
public class PhotoAttachment extends ImageAttachment implements ISocialable {
	public int albumid;
	public long ownerid;
	public long userid = 100;
	public int origwidth;
	public int origheight;
	public PhotoSize[] sizes = new PhotoSize[10];

	// for msg
	public int renderH = 20;
	public int renderW;
	public Image renderImg = null;

	public PhotoAttachment() {
		this.type = "photo";
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
	}

	public void parseJSON() {
		sizes = PhotoSize.parseSizes(json.optJSONArray("sizes"));
		origwidth = json.optInt("width");
		origheight = json.optInt("height");
		ownerid = json.optInt("owner_id");
		albumid = json.optInt("album_id");
		userid = json.optInt("user_id");
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.serviceRepaints();
	}

	public Image getImg(String x) throws Exception {
		return VikaUtils.downloadImage(PhotoSize.getSize(sizes, x).url);
	}

	public Image getImg(int i) {
		try {
			return VikaUtils.downloadImage(sizes[i].url);
		} catch (Exception e) {
			try {
				return Image.createImage("/image.png");
			} catch (IOException e1) {
				return null;
			}
		}
	}

	public String getPreviewImageUrl() {
		PhotoSize ps = null;
		try {
			ps = PhotoSize.getSize(sizes, "x");
			if (ps == null)
				throw new Exception();
		} catch (Exception e1) {
			try {
				ps = PhotoSize.getSize(sizes, "o");
			} catch (Exception e2) {
			}
		}
		if (ps == null)
			return null;
		return ps.url;
	}

	public String getFullImageUrl() {
		return PhotoSize.searchNearestSize(sizes, 0xffff).url;
	}

	public void press() {
		VikaTouch.popup(new ImagePreview(this, "Вложение " + attNumber));
	}

	// имеющиеся методы для идиотов. Точнее я не уверен что вот то будет работать, и
	// мне проще написать это чем 2 часа ловить баги. Потом втюхаю в I.
	public PhotoSize getMessageImage() {
		//return PhotoSize.searchSmallerSize(sizes,
			//	Math.min((int) (DisplayUtils.width * 0.6), MsgItem.msgWidth - MsgItem.attMargin * 2));
		return PhotoSize.searchNearestSize(sizes,
				Math.min((int) (DisplayUtils.width), MsgItem.msgWidth - MsgItem.attMargin * 2));
	}

	// Загружает картинку
	public void loadForMessage() {
		try {
			PhotoSize ps = getMessageImage();
			Image i = VikaUtils.downloadImage(ps.url);
			int w = Math.min((int) (DisplayUtils.width * 0.6), MsgItem.msgWidth - MsgItem.attMargin * 2);
			if (ps.width > w) {
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

	// Загружает картинку
	public void loadForComment() {
		try {
			PhotoSize ps = getMessageImage();
			Image i = VikaUtils.downloadImage(ps.url);
			int w = Math.min((int) (DisplayUtils.width * 0.6), CommentItem.msgWidth - CommentItem.attMargin * 2);
			if (ps.width > w) {
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
			PhotoSize ps = (DisplayUtils.width > DisplayUtils.height)
					? PhotoSize.searchSmallerSizeH(sizes, DisplayUtils.height)
					: PhotoSize.searchSmallerSize(sizes, DisplayUtils.width);
			Image i = VikaUtils.downloadImage(ps.url);
			renderH = i.getHeight();
			renderW = i.getWidth();
			renderImg = i;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getDrawHeight() {
		return renderH;
	}

	public Image getImage(int height) {
		return null;
	}

	public boolean canSave() {
		// TODO
		return false;
	}

	public void save() {
		// TODO
	}

	public boolean canLike() {
		// TODO
		return true;
	}

	public boolean getLikeStatus() {
		// TODO
		return false;
	}

	public void like(boolean val) {
		// TODO
	}

	public void send() {
		// TODO
	}

	public void repost() {
		// TODO
	}

	public boolean commentsAliveable() {
		// TODO
		return true;
	}

	public void openComments() {
		// TODO
	}

	public Image getPreviewImage() {
		return null;
	}

	public Image getFullImage() {
		try {
			return VikaUtils.downloadImage(getFullImageUrl());
		} catch (InterruptedException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
}
