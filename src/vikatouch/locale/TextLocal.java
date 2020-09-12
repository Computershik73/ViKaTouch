package vikatouch.locale;

import java.io.*;
import java.util.Hashtable;

import vikatouch.VikaTouch;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;

public class TextLocal
{
	public static TextLocal inst;
	private Hashtable hashtable;

	/**
	 *  Вызывать только после загрузки настроек!!
	 */
	public static void init()
	{
		inst = new TextLocal();
	}
	
	private TextLocal()
	{
		hashtable = new Hashtable();
		loadLanguage(Settings.language);
	}
	
	public void loadLanguage(LangObject lang)
	{
		loadLanguage(lang.shortName);
	}
	
	public void loadLanguage(String lang)
	{
		System.out.println(lang);
		try
		{
			char[] chars = new char[16000];
			InputStream stream = this.getClass().getResourceAsStream("/local/" + lang + ".txt");
			InputStreamReader isr = new InputStreamReader(stream, "UTF-8");
			isr.read(chars);
			isr.close();
			String x = "";
			boolean iscomment = false;
			for(int i = 0; i < chars.length; i++)
			{
				final char c = chars[i];
				
				if(c == 0)
				{
					break;
				}
				
				if(c == '#')
				{
					iscomment = true;	
				}
				
				if(c == '\n')
				{
					if(!iscomment && x != null && x.length() > 2)
					{
						int splitLoc = x.indexOf("=");
						int len = x.length();
						String key = x.substring(0, splitLoc);
						String val = VikaUtils.replace(x.substring(splitLoc + 1, len - 1), "|", "\n");
						hashtable.put(key, val);
						//System.out.println(key + "=" + val);
						//System.out.println();
					}
					iscomment = false;
					x = "";
				}
				else
					x += String.valueOf(c);
			}
			x = null;
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			VikaTouch.error(e, ErrorCodes.LANGLOAD);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			VikaTouch.error(e, ErrorCodes.LANGLOAD);
		}
	}
	
	public String get(String key)
	{
		try
		{
			if(hashtable.containsKey(key))
			{
				return (String) hashtable.get(key);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			VikaTouch.error(e, ErrorCodes.LANGGET);
		}
		return key;
	}

	private String replace(String str, String from, int to)
	{
		if(to == -1)
		{
			return str;
		}
		if(to == -2)
		{
			return str;
		}
		return replace(str, from, "" + to);
	}

	private String replace(String str, String from, String to)
	{
		if(to == null)
		{
			return str;
		}
		try
		{
			String result = "";
			int j = str.indexOf(from);
			int k = 0;
	
			for (int i = from.length(); j != -1; j = str.indexOf(from, k))
			{
				result += str.substring(k, j) + to;
				k = j + i;
			}
	
			result += str.substring(k, str.length());
			return result;
		}
		catch (Exception e)
		{
			return str;
		}
	}
	
	public String formatTime(int hour, int minute)
	{
		return format("time", -1, -2, -1, hour, minute);
	}

	public String formatShortDate(int day, int month, int year)
	{
		return format("shortdate", day, month, year, -1, -1);
	}

	public String formatShortDate(int day, int month)
	{
		return format("shortdatenoyear", day, month, -1, -1, -1);
	}

	public String formatChatDate(int day, int month)
	{
		return format("chatdate", day, month, -1, -1, -1);
	}
	
	public String formatChatDate(int day, int month, int year)
	{
		return format("chatdatewyear", day, month, year, -1, -1);
	}

	public String formatFullDate(int day, int month, int year, int hour, int minute)
	{
		return format("fulldate", day, month, year, hour, minute);
	}

	public String formatDate(int day, int month, int year)
	{
		return format("date", day, month, year, -1, -1);
	}

	public String format(String format, int day, int month, int year, int H, int M)
	{
		String result = get("format." + format);
		try
		{
			result = replace(result, "DD", day);
			
			result = replace(result, "S", getShortMonth(month));
			
			result = replace(result, "N", getMonth(month));
			
			result = replace(result, "O", month + 1);
			
			result = replace(result, "YEAR", year);
			
			String h = "" + H;
			
			String HH = "" + (H < 10 ? "0" + H : "" + H);
			
			String TT = "AM";
			
			String MM = "" + (M < 10 ? "0" + M : "" + M);
			
			if(H == 0)
			{
				h = "" + 12;
				TT = "PM";
			}
			else if(H == 13)
			{
				h = "" + 1;
				TT = "PM";
			}
			else if(H > 13)
			{
				h = "" + H % 12;
				TT = "PM";
			}
			
			String tt = TT.toLowerCase();
			
			result = replace(result, "h", h);
			result = replace(result, "HH", HH);
			result = replace(result, "H", H);
			result = replace(result, "MM", MM);
			result = replace(result, "M", M);
			result = replace(result, "tt", tt);
			result = replace(result, "TT", TT);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println(format + " " + result);
		return result;
	}
	
	private String getMonthS(int i)
	{
		switch(i)
		{
			case 0:
			{
				return "jan";
			}
			case 1:
			{
				return "feb";
			}
			case 2:
			{
				return "mar";
			}
			case 3:
			{
				return "apr";
			}
			case 4:
			{
				return "may";
			}
			case 5:
			{
				return "jun";
			}
			case 6:
			{
				return "jul";
			}
			case 7:
			{
				return "aug";
			}
			case 8:
			{
				return "sep";
			}
			case 9:
			{
				return "oct";
			}
			case 10:
			{
				return "nov";
			}
			case 11:
			{
				return "dec";
			}
			case -1:
			{
				return null;
			}
			case -2:
			{
				return null;
			}
			default:
			{
				return "";
			}
		}
	}

	private String getMonth(int month)
	{
		if(month == -1)
		{
			return null;
		}
		if(month == -2)
		{
			return null;
		}
		return get("date.month." + getMonthS(month));
	}

	private String getShortMonth(int month)
	{
		if(month == -1)
		{
			return null;
		}
		if(month == -2)
		{
			return null;
		}
		return get("date.shortmonth." + getMonthS(month));
	}

	public String getFormatted(String string, String[] vars)
	{
		//vars: илья
		//in: %1 щас срет 100\%
		//process: \var\1 щас срет 100\temp\
		//out: илья щас срет 100%
		String x = get(string);
		x = replace(x, "\\%", "\\temp\\");
		x = replace(x, "%", "\\var\\");
		
		x = replace(x, "\\temp\\", "%");
		
		// это конечно ничего не чинит, косяк там где-то в другом месте
		for(int i = 0; i < vars.length; i++)
		{
			x = replace(x, "\\var\\" + (i+1), vars[i]);
		}
		
		
		return x;
	}

}
