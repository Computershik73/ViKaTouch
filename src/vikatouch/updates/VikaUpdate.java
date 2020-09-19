package vikatouch.updates;

import javax.microedition.io.ConnectionNotFoundException;

import ru.nnproject.vikaui.popup.ConfirmBox;
import ru.nnproject.vikaui.popup.InfoPopup;
import vikatouch.VikaTouch;
import vikatouch.utils.VikaUtils;

public class VikaUpdate {
	
	public String currVer;
	public String newVer;
	public String changeLog;
	public String url;
	
	public static VikaUpdate check()
	{
		try
		{
			String ver = VikaUtils.download("http://vikamobile.ru/vkt/ver.txt");
			String[] vers = VEUtils.split(ver, 3, '.');
			String[] cvers = VEUtils.split(VikaTouch.getVersion(), 3, '.');
			
			if(Integer.parseInt(vers[1])>Integer.parseInt(cvers[1]) || Integer.parseInt(vers[2])>Integer.parseInt(cvers[2]))
			{
				VikaUpdate vu = new VikaUpdate();
				vu.currVer = VikaTouch.getVersion();
				vu.newVer = ver;
				vu.changeLog = VikaUtils.download("http://vikamobile.ru/vkt/cl.txt");
				vu.url = "http://vikamobile.ru/vkt/"+ver+".jar";
			}
			
			return null;
		}
		catch (Throwable t)
		{
			return null;
		}
	}
	
	public void ask()
	{
		VikaTouch.popup(new InfoPopup("Доступна новая версия - "+newVer+"\n"+changeLog, new Runnable() {
			public void run () {
				VikaTouch.popup(new ConfirmBox("Загрузить?", "", new Runnable() {
					public void run () {
						try {
							VikaTouch.appInst.platformRequest(url);
						} catch (ConnectionNotFoundException e) {
						}
					}
				}, null));
			}
		}));
	}
	
	// кусок виэновских утилит
	public static class VEUtils {
		
		public static String getStr(char[] a, int from, int to)
		{
			String s = "";
			for(int i = from; (i < to && i < a.length); i++)
			{
				s += String.valueOf(a[i]);
			}
			return s;
		}
		public static String getStr(char[] a, int from, char to)
		{
			System.out.println("getStr with char ending");
			String s = "";
			for(int i = from; i < a.length; i++)
			{
				if(to == a[i])
				{
					return s;
				}
				s += String.valueOf(a[i]);
			}
			return s;
		}
		public static int findLF(char[] a, int from)
		{
			for(int i = from; i < a.length; i++)
			{
				if('\n' == a[i]) return i;
			}
			return -1;
		}
		public static String[] singleSplit(String s, char t)
		{
			String[] a = new String[2];
			int i = s.indexOf(t);
			a[0] = s.substring(0, i);
			a[1] = s.substring(i+1);
			return a;
		}
		
		public static String[] split(String in, int l, char t)
		{
			String[] res = new String[l];
			String[] c = singleSplit(in, t);
			res[0] = c[0];
			for(int i = 1; i < l-1; i++)
			{
				c = singleSplit(c[1], t);
				res[i] = c[0];
			}
			res[l-1] = c[1];
			return res;
		}
	}

}
