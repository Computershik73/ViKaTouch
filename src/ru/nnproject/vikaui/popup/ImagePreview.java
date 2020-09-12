package ru.nnproject.vikaui.popup;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.attachments.DocumentAttachment;
import vikatouch.attachments.ISocialable;
import vikatouch.attachments.PhotoAttachment;
import vikatouch.items.menu.DocItem;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;

public class ImagePreview extends VikaNotice {

	public ImagePreview (String url, String header)
	{
		imgUrl = url;
		downloadUrl = null;
		socialActions = null;
		title = header;
		Load();
	}
	public ImagePreview (PhotoAttachment photo, String header)
	{
		imgUrl = photo.getPreviewImageUrl();
		downloadUrl = photo.getFullImageUrl();
		socialActions = (ISocialable) photo;
		title = header;
		Load();
	}
	public ImagePreview (DocItem doc)
	{
		imgUrl = doc.prevImgUrl;
		downloadUrl = doc.url;
		socialActions = (ISocialable) doc;
		title = doc.name;
		Load();
	}
	public ImagePreview (DocumentAttachment doc)
	{
		imgUrl = doc.prevImgUrl;
		downloadUrl = doc.url;
		socialActions = null;
		title = doc.name;
		Load();
	}
	
	public Image img;
	public String imgUrl;
	public String downloadUrl;
	public ISocialable socialActions;
	
	public String title = null;
	
	public int drX; public int drY;
	
	private void Load()
	{
		VikaTouch.loading = true;
		repaint();
		(new Thread()
		{
			public void run()
			{
				try
				{
					//System.out.println("Начато скачивание превью");
					Image dimg = VikaUtils.downloadImage(imgUrl);
					//System.out.println("Ресайз превью: исходное "+img.getWidth()+"х"+img.getHeight());
					
					double aspectR = (double)dimg.getWidth() / (double)dimg.getHeight();
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
					drX = (DisplayUtils.width - w)/2;
					drY = (DisplayUtils.height - h)/2;
					img = VikaUtils.resize(dimg, w, h);
				}
				catch(Exception e)
				{
					VikaTouch.error(e, ErrorCodes.DOCPREVIEWLOAD);
				}
			}
		}).start();
	}
	
	public void draw(Graphics g) {
		if(img == null) {
			g.drawImage(IconsManager.ico[IconsManager.CLOSE], DisplayUtils.width - 24, 0, 0);
			VikaTouch.loading = true;
		} else {
			VikaTouch.loading = false;
			g.setGrayScale(50);
			g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);
			g.drawImage(img, drX, drY, 0);
			
			if(title!=null)
			{
				g.setColor(255,255,255);
				Font f = Font.getFont(0, 0, Font.SIZE_MEDIUM);
				g.drawString(title, 24, 12-f.getHeight()/2, 0);
			}
			// drawing buttons
			{
				int currX = DisplayUtils.width;
				currX-=24;
				g.drawImage(IconsManager.ico[IconsManager.CLOSE], currX, 0, 0);
				currX-=24;
				if(downloadUrl!=null)
				{
					g.drawImage(IconsManager.ico[IconsManager.DOWNLOAD], currX, 0, 0);
					currX -= 24;
				}
				if(socialActions!=null) 
				{
					if(socialActions.canSave())
					{
						g.drawImage(IconsManager.ico[IconsManager.ADD], currX, 0, 0);
						currX -= 24;
					}
					g.drawImage(IconsManager.ico[IconsManager.SEND], currX, 0, 0);
					currX -= 24;
					if(socialActions.commentsAliveable())
					{
						g.drawImage(IconsManager.ico[IconsManager.COMMENTS], currX, 0, 0);
						currX -= 24;
					}
					if(socialActions.canLike())
					{
						g.drawImage(IconsManager.ico[socialActions.getLikeStatus()?IconsManager.LIKE_F:IconsManager.LIKE], currX, 0, 0);
						currX -= 24;
					}
				}
			}
		}
	}
	
	public void release(int x, int y)
	{
		if(y>24) return;
		
		int currX = DisplayUtils.width;
		currX-=24;
		// закрытие
		if(x>currX)
		{
			VikaTouch.loading = false;
			VikaTouch.canvas.currentAlert = null;
			return;
		}
		if(img == null) return;
		currX-=24;
		if(downloadUrl!=null)
		{
			// скачка
			if(x>currX)
			{
				try
				{
					VikaTouch.appInst.platformRequest(downloadUrl);
				}
				catch (ConnectionNotFoundException e) 
				{
					VikaTouch.popup(new InfoPopup("Не удалось открыть. Возможно, произошла ошибка при обработке адреса либо ваше устройство не может открыть этот документ.", null));
				}
				return;
			}
			currX -= 24;
		}
		if(socialActions!=null) 
		{
			if(socialActions.canSave())
			{
				// сохранение
				if(x>currX)
				{
					VikaTouch.popup(new InfoPopup("Сохранение пока не реализовано.", null));
					return;
				}
				currX -= 24;
			}
			// отправка
			if(x>currX)
			{
				VikaTouch.popup(new InfoPopup("Отправку ещё не завезли", null));
				return;
			}
			currX -= 24;
			if(socialActions.commentsAliveable())
			{
				// каменты
				if(x>currX)
				{
					VikaTouch.popup(new InfoPopup("Комменты тоже.", null));
					return;
				}
				currX -= 24;
			}
			if(socialActions.canLike())
			{
				// луцки
				if(x>currX)
				{
					VikaTouch.popup(new InfoPopup("Лайки сожрали неко", null));
					return;
				}
				currX -= 24;
			}
		}
		
	}

}
