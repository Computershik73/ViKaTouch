package vikatouch.items.menu;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.items.UIItem;
import vikatouch.items.JSONUIItem;

public class PhotoItem
	extends JSONUIItem
{
	public int itemDrawWidth = 50;
	private String iconUrl;
	private Image iconImg;
	
	public PhotoItem(JSONObject json, boolean dontLoadImage)
	{
		super(json);
		
		if(!dontLoadImage)
		{
			
		}
	}
	
	public void paint(Graphics g, int y, int scrolled)
	{
		
	}

	public void tap(int x, int y)
	{
		
	}

	public void keyPressed(int key)
	{
		
	}

}
