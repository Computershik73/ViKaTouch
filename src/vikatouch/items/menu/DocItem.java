package vikatouch.items.menu;

import java.io.IOException;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.attachments.ISocialable;
import vikatouch.attachments.PhotoSize;
import vikatouch.items.JSONUIItem;
import vikatouch.json.JSONBase;
import vikatouch.screens.menu.DocsScreen;
import vikatouch.utils.ResizeUtils;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;

public class DocItem
	extends JSONUIItem implements ISocialable
{
	public String name;
	public String url;
	private String iconUrl;
	public String prevImgUrl;
	private int size;
	private PhotoSize[] prevSizes;
	private Image iconImg;
	private int documentType;
	private String ext;
	private int type;
	private String time;
	
	//private static Image downloadBI = null;

	public static final int BORDER = 1;

	//типы вложения
	private static final int TYPE_TEXT = 1;
	private static final int TYPE_ARCHIVE = 2;
	private static final int TYPE_GIF = 3;
	private static final int TYPE_PHOTO = 4;
	private static final int TYPE_AUDIO = 5;
	private static final int TYPE_VIDEO = 6;
	private static final int TYPE_EBOOK = 7;
	private static final int TYPE_UNKNOWN = 8;
	private static final int TYPE_UNDEFINED = 0;
	
	private static Image doczipImg;
	private static Image docsisImg;
	private static Image docjarImg;
	private static Image docmusImg;
	private static Image docvidImg;
	private static Image docfileImg;
	private static Image doctxtImg;

	public DocItem(JSONObject json)
	{
		super(json);
		itemDrawHeight = 50;
		try
		{
			if(doczipImg == null)
			{
				doczipImg = ResizeUtils.resizeItemPreview(Image.createImage("/doczip.png"));
				docsisImg = ResizeUtils.resizeItemPreview(Image.createImage("/docsis.png"));
				doctxtImg = ResizeUtils.resizeItemPreview(Image.createImage("/doctxt.png"));
				docfileImg = ResizeUtils.resizeItemPreview(Image.createImage("/docfile.png"));
				docmusImg = ResizeUtils.resizeItemPreview(Image.createImage("/docmus.png"));
				docvidImg = ResizeUtils.resizeItemPreview(Image.createImage("/docvid.png"));
				docjarImg = ResizeUtils.resizeItemPreview(Image.createImage("/docjar.png"));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void parseJSON()
	{
		//System.out.println(json.toString());

		try
		{
			date = json.optInt("date");
			name = json.optString("title");
			url = fixJSONString(json.optString("url"));
			size = json.optInt("size");
			ext = json.optString("ext");
			type = json.optInt("type");

			if(!json.isNull("preview"))
			{
				prevSizes = PhotoSize.parseSizes(json.getJSONObject("preview").getJSONObject("photo").getJSONArray("sizes"));

				PhotoSize iconPs = null;
				PhotoSize prevPs = null;

				try
				{
					iconPs = PhotoSize.getSize(prevSizes, "s");
					if(iconPs==null) throw new Exception();
				}
				catch (Exception e)
				{
					try
					{
						iconPs = PhotoSize.getSize(prevSizes, "d");
					}
					catch (Exception e3)
					{
						e3.printStackTrace();
					}
				}
				try
				{
					prevPs = PhotoSize.getSize(prevSizes, "x");
					if(prevPs==null) throw new Exception();
				}
				catch (Exception e1)
				{
					try
					{
						prevPs = PhotoSize.getSize(prevSizes, "o");
					}
					catch (Exception e2)
					{
						//не достучались до превьюхи..
					}
				}

				if(iconPs != null)
				{
					iconUrl = fixJSONString(iconPs.url);
				}

				if(prevPs != null)
				{
					prevImgUrl = fixJSONString(prevPs.url);
				}
			}
		}
		catch (JSONException e)
		{
			//e.printStackTrace();
			//Предпросмотр не завезли - видимо не картинка. Ну и ладно.
		}
		catch (Exception e)
		{
			e.printStackTrace();
			VikaTouch.error(e, ErrorCodes.DOCPARSE);
		}

		setDrawHeight();

		System.gc();
	}

	private void setDrawHeight()
	{
		itemDrawHeight = 50 + (BORDER * 2);
	}

	public void paint(Graphics g, int y, int scrolled)
	{
		if(ScrollableCanvas.keysMode && selected)
		{
			ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
			g.fillRect(0, y, DisplayUtils.width, itemDrawHeight);
		}
		
		if(iconImg == null)
			iconImg = getIcon();

		if(time == null)
			time = getTime();
		ColorUtils.setcolor(g, 0);
		if(name != null)
			g.drawString(name, 73, y, 0);
		ColorUtils.setcolor(g, ColorUtils.OUTLINE);
		g.drawString(size / 1000 + "кб, " + time, 73, y + 24, 0);
		if(iconImg != null)
		{
			g.drawImage(iconImg, 14, y + BORDER, 0);
		}
		if(!ScrollableCanvas.keysMode)
		{
			try
			{
				//if(downloadBI == null)
				//{
				//	downloadBI = Image.createImage("/downloadBtn.png");
				//}
				int iy = (itemDrawHeight - 24) / 2;
				if(iy < 0)
					iy = 0;
				iy += y;
				g.drawImage(IconsManager.ico[IconsManager.DOWNLOAD], DisplayUtils.width - 24, y, 0);
			} 
			catch (Exception e)
			{
				
			}
		}
	}

	private Image getIcon()
	{
		Image img = null;
		try
		{
			img = ResizeUtils.resizeItemPreview(VikaUtils.downloadImage(iconUrl));
		}
		catch (Exception e)
		{
			try
			{
				switch(type)
				{
					case TYPE_PHOTO:
					case TYPE_GIF:
						return VikaTouch.cameraImg;
					case TYPE_AUDIO:
						return docmusImg;
					case TYPE_VIDEO:
						return docvidImg;
					case TYPE_ARCHIVE:
						if(ext.toLowerCase().indexOf("sis") != VikaTouch.INDEX_FALSE)
						{
							return docsisImg;
						}
						else
							return doczipImg;
					case TYPE_TEXT:
					case TYPE_EBOOK:
						return doctxtImg;
					case TYPE_UNKNOWN:
					case TYPE_UNDEFINED:
					default:
						if(ext.toLowerCase().indexOf("jar") != VikaTouch.INDEX_FALSE || ext.toLowerCase().indexOf("jad") != VikaTouch.INDEX_FALSE)
						{
							return docjarImg;
						}
						else if(ext.toLowerCase().indexOf("sis") != VikaTouch.INDEX_FALSE)
						{
							return docsisImg;
						}
						else if(ext.toLowerCase().indexOf("rar") != VikaTouch.INDEX_FALSE || ext.toLowerCase().indexOf("zip") != VikaTouch.INDEX_FALSE || ext.toLowerCase().indexOf("tar") != VikaTouch.INDEX_FALSE || ext.toLowerCase().indexOf("7z") != VikaTouch.INDEX_FALSE)
						{
							return doczipImg;
						}
						/*else if(ext.toLowerCase().indexOf("torrent") != VikaTouch.INDEX_FALSE)
						{
							return ResizeUtils.resizeItemPreview(Image.createImage("/doctorr.png"));
						}*/
						else
						{
							return docfileImg;
						}
				}
			}
			catch (Exception e2)
			{

			}
		}
		return img;
	}
	
	public void startPreview() {
		if(type == TYPE_PHOTO)
		{
			
			if(prevImgUrl==null) { return; }
			VikaTouch.docsScr.isPreviewShown = true;
			(new Thread()
			{
				public void run()
				{
					try
					{
						//System.out.println("Начато скачивание превью");
						Image img = VikaUtils.downloadImage(prevImgUrl);
						//System.out.println("Ресайз превью: исходное "+img.getWidth()+"х"+img.getHeight());
						
						double aspectR = (double)img.getWidth() / (double)img.getHeight();
						double SAR = (double)DisplayUtils.width / (double)DisplayUtils.height;
						boolean vertical = aspectR < SAR;
						int w = 0; int h = 0;
						if(vertical) {
							h = DisplayUtils.height;
							w = (int)(h*aspectR);
						}
						else
						{
							w = DisplayUtils.width;
							h = (int)(w/aspectR);
						}
						VikaTouch.docsScr.previewX = (DisplayUtils.width - w)/2;
						VikaTouch.docsScr.previewY = (DisplayUtils.height - h)/2;
						VikaTouch.docsScr.previewImage = VikaUtils.resize(img, w, h);
					}
					catch(Exception e)
					{
						VikaTouch.docsScr.isPreviewShown = false;
						VikaTouch.error(e, ErrorCodes.DOCPREVIEWLOAD);
					}
				}
			}).start();
		}
	}

	public void tap(int x, int y)
	{
		try
		{
			if(x<DisplayUtils.width - 50)
			{
				startPreview();
			}
			else
			{
				VikaTouch.appInst.platformRequest(url);
			}
		}
		catch (Exception e)
		{

		}
	}
	
	public void keyPressed(int key)
	{
		if(DocsScreen.current.isPreviewShown)
		{
			DocsScreen.current.isPreviewShown = false;
			DocsScreen.current.previewImage = null;
			return;
		}
		if(type == TYPE_PHOTO)
		{
			if(key == KEY_FUNC)
			{
				try
				{
					VikaTouch.appInst.platformRequest(url);
				}
				catch (ConnectionNotFoundException e) 
				{
					
				}
			}
			if(key == KEY_OK)
			{
				startPreview();
			}
		}
		else
		{
			if(key == KEY_OK || key == KEY_FUNC)
			{
				try
				{
					VikaTouch.appInst.platformRequest(url);
				}
				catch (ConnectionNotFoundException e) 
				{
					
				}
			}
		}
	}

	public boolean canSave() {
		// TODO
		return false;
	}

	public void save() {
		// TODO
	}
	public void send() {
		// TODO
	}

	public void repost() {
		// TODO
	}
	
	public void openComments() { }
	public boolean canLike() { return false; }
	public boolean getLikeStatus() { return false; }
	public void like(boolean val) { }
	public boolean commentsAliveable() { return false; }
}
