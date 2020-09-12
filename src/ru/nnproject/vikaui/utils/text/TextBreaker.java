package ru.nnproject.vikaui.utils.text;

import java.util.Vector;

import javax.microedition.lcdui.Font;

import ru.nnproject.vikaui.menu.items.UIItem;

public class TextBreaker
{
	
	public static String[] breakText(final String text, boolean shortText, UIItem item, final boolean full, final int width)
	{
		Font font = Font.getFont(0, 0, 8);
		String[] result;
		if(text.length() > 24 || text.indexOf("\n") != -1)
		{
			char[] chars = text.toCharArray();
			int lncount = 0;
			result = new String[10];
			if(full)
				result = new String[100];
			try
			{
				if(font.stringWidth(text) > width)
				{
					String x2 = "";
					for(int i2 = 0; i2 < text.length(); i2++)
					{
						if(chars[i2] == '\n')
						{
							result[lncount] = x2;
							x2 = "";
							lncount++;
							if(item != null)
								item.addDrawHeight(24);
						}
						else
						{
							x2 += ""+chars[i2];
							if(font.stringWidth(x2) > width)
							{
								result[lncount] = x2;
								x2 = "";
								lncount++;
								if(item != null)
									item.addDrawHeight(24);
							}
							else if(text.length() - i2 <= 1)
							{
								result[lncount] = x2;
								x2 = "";
								lncount++;
								if(item != null)
									item.addDrawHeight(24);
							}
						}
					}
				}
				else
				{
					result[lncount] = text;
					if(item != null)
						item.addDrawHeight(24);
				}
				
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				result[result.length-1] = "Показать полностью...";
			}
		}
		else
		{
			result = new String[2];
			result[0] = text; 
			shortText = true;
			if(text.length() > 1)
				if(item != null)
					item.addDrawHeight(24);
		}
		return result;
	}

	// взято из виэна
	public static String[] breakText(String text, Font font, int maxWidth)
	{
		if(text == null || text.length() == 0 || (text.length()==1&&text.charAt(0)==' '))
		{
			return new String[0];
		}
		Vector v = new Vector(5,3);
		char[] chars = text.toCharArray();
		if(font.stringWidth(text) > maxWidth)
		{
			int i1 = 0;
			for(int i2 = 0; i2 < text.length(); i2++)
			{
				if(chars[i2] == '\n')
				{
					v.addElement(text.substring(i1, i2));
					i2 = i1 = i2+1;
				}
				else
				{
					if(text.length() - i2 <= 1)
					{
						v.addElement(text.substring(i1, text.length()));
						break;
					}
					else if(font.substringWidth(text, i1, i2-i1) >= maxWidth)
					{
						boolean f = false;
						for(int j=i2; j>i1; j--)
						{
							char c = text.charAt(j);
							if(c==' '||c=='-')
							{
								f = true;
								v.addElement(text.substring(i1, j+1));
								i2 = i1 = j+1;
								break;
							}
						}
						if(!f)
						{
							i2 = i2-2;
							v.addElement(text.substring(i1, i2));
							i2 = i1 = i2+1;
						}
					}
				}
			}
		}
		else
		{
			v.addElement(text);
		}
		String[] r = new String[v.size()];
		v.copyInto(r);
		return r;
	}
}
