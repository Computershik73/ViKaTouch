package vikatouch.utils.text;

import vikatouch.locale.TextLocal;

public class CountUtils
{

	public static String countStrMessages(int length)
	{
		return countStr("message", length);
	}

	public static String countStrViews(int length)
	{
		return countStr("view", length);
	}

	public static String countStrMembers(int length)
	{
		return countStr("member", length);
	}

	public static String countStr(String x, int length)
	{
		return length + " " + getStr(x, length);
	}
	
	public static String getStr(String x, int a)
	{
		return TextLocal.inst.get("count." + x + getsomehuyna(x, a));
	}
	
	public static String getsomehuyna(String x, int a)
	{
		a = a % 10;
		String x2;
		if(a == 1)
		{
			x2 = "";
		}
		else if(a < 5)
		{
			x2 = "sfew";
		}
		else
		{
			x2 = "s";
		}
		return x2;
	}

}
