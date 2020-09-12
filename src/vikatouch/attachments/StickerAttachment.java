package vikatouch.attachments;

import java.io.IOException;

import javax.microedition.lcdui.Image;

import vikatouch.utils.VikaUtils;

public class StickerAttachment
	extends ImageAttachment
{
	
	public int productid;
	public int stickerid;
	public PhotoSize[] images = new PhotoSize[10];

	public void parseJSON()
	{
		productid = json.optInt("product_id");
		stickerid = json.optInt("sticker_id");
		images = PhotoSize.parseSizes(json.optJSONArray("images_with_background"));
	}

	public Image getPreviewImage()
	{
		return getImg(0);
	}
	
	private Image getImg(int i)
	{
		try
		{
			return VikaUtils.downloadImage(images[i].url);
		}
		catch(Exception e)
		{
			try {
				return Image.createImage("/image.png");
			} catch (IOException e1)
			{
				return null;
			}
		}
	}
	

	public Image getFullImage()
	{
		return getImg(3);
	}

	public Image getImage(int height)
	{
		switch(height)
		{
			case 64:
			{
				return getImg(0);
			}
			case 128:
			{
				return getImg(1);
			}
			case 256:
			{
				return getImg(2);
			}
			case 512:
			{
				return getImg(3);
			}
			default:
			{
				return getPreviewImage();
			}
		}
	}

}
