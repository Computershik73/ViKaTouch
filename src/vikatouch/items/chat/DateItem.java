package vikatouch.items.chat;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.utils.VikaUtils;

public final class DateItem
	extends ChatItem
{
	private int width;
	private String stringDate;

	public DateItem(long date)
	{
		super(null);
		this.date = date;
		this.itemDrawHeight = 26;
		this.stringDate = VikaUtils.parseShortTime(date);
	}
	
	public void parseJSON()
	{
		
	}
	
	public void paint(Graphics g, int y, int scrolled)
	{
		Font font = Font.getFont(0, 0, 8);
		g.setFont(font);
		
		this.width = font.stringWidth(this.stringDate);
		
		if(this.width < 74)
		{
			this.width = 74;
		}
		else
		{
			this.width += 32;
		}
		if(scrolled < 0)
		{
			ColorUtils.setcolor(g, -1);
			g.fillRoundRect((DisplayUtils.width - this.width) / 2, 56, this.width, this.itemDrawHeight, 10, 10);
		}
	}

	public void tap(int x, int y)
	{
		
	}
	public void keyPressed(int key)
	{
		
	}

}
