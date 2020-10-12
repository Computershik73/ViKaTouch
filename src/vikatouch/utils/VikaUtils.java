package vikatouch.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.HttpsConnection;
import javax.microedition.io.InputConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.popup.VikaNotification;
import tube42.lib.imagelib.ImageUtils;
import vikatouch.VikaNetworkError;
import vikatouch.VikaTouch;
import vikatouch.caching.ImageStorage;
import vikatouch.canvas.VikaCanvasInst;
import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;
import vikatouch.screens.page.GroupPageScreen;
import vikatouch.screens.page.ProfilePageScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.url.URLBuilder;
import vikatouch.utils.url.URLDecoder;


public final class VikaUtils
{
	public static String parseShortTime(final long paramLong)
	{
		final Calendar cal = Calendar.getInstance();
		
		final Date date = new Date(paramLong * 1000L);
		final Date currentDate = new Date(System.currentTimeMillis());
		
		cal.setTime(date);
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		final int year = cal.get(Calendar.YEAR);
		final int month = cal.get(Calendar.MONTH);
		
		cal.setTime(currentDate);
		final int currentYear = cal.get(Calendar.YEAR);
		
	    final String time = time(date);
	    
	    final long dayDelta = (paramLong / 60L / 60L / 24L) - (System.currentTimeMillis() / 1000L / 60L / 60L / 24L);
	    
	    String result = "Давно";
	    
	    parsing:
	    {
		    if(dayDelta == 0)
		    {
		    	result = time;
		    	break parsing;
		    }
		    else if(dayDelta == 1)
		    {
		    	result = TextLocal.inst.get("date.yesterday");
		    	break parsing;
		    }
		    else if(currentYear == year)
		    {
		    	result = TextLocal.inst.formatChatDate(day, month);
		    	break parsing;
		    }
		    else
		    {
		    	result = TextLocal.inst.formatChatDate(day, month, year);
		    	break parsing;
		    }
	    }
	    
	    return result;
	}
	
	public static String parseTime(final long paramLong)
	{
		final Calendar cal = Calendar.getInstance();
		
		final Date date = new Date(paramLong * 1000L);
		final Date currentDate = new Date(System.currentTimeMillis());
		
		cal.setTime(date);
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		final int year = cal.get(Calendar.YEAR);
		final int month = cal.get(Calendar.MONTH);
		
		cal.setTime(currentDate);
		final int currentYear = cal.get(Calendar.YEAR);
		
	    final String time = time(date);
	    
	    final long dayDelta = (paramLong / 60L / 60L / 24L) - (System.currentTimeMillis() / 1000L / 60L / 60L / 24L);
	    
	    String result;
	    
	    parsing:
	    {
		    if(dayDelta == 0)
		    {
		    	result = TextLocal.inst.get("date.todayat");
		    	result += " " + time;
		    	break parsing;
		    }
		    else if(dayDelta == 1)
		    {
		    	result = TextLocal.inst.get("date.yesterday");
		    	result += " " + time;
		    	break parsing;
		    }
		    else if(currentYear == year)
		    {
		    	result = TextLocal.inst.formatShortDate(day, month);
		    	break parsing;
		    }
		    else
		    {
		    	result = TextLocal.inst.formatDate(day, month, year);
		    	break parsing;
		    }
	    }
	    
	    return result;
	}
	
	public static String parseMsgTime(final long paramLong)
	{
		/*
		final Calendar cal = Calendar.getInstance();
		
		final Date date = new Date(paramLong * 1000L);
		final Date currentDate = new Date(System.currentTimeMillis());
		
		cal.setTime(date);
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		final int year = cal.get(Calendar.YEAR);
		final int month = cal.get(Calendar.MONTH);
		
		cal.setTime(currentDate);
		final int currentYear = cal.get(Calendar.YEAR);
		
	    final String time = time(date);
	    
	    final long dayDelta = (paramLong / 60L / 60L / 24L) - (System.currentTimeMillis() / 1000L / 60L / 60L / 24L);
	    
	    String result;
	    
	    parsing:
	    {
		    if(dayDelta == 0)
		    {
		    	result = time;
		    	break parsing;
		    }
		    else if(dayDelta == 1)
		    {
		    	result = TextLocal.inst.get("date.yesterday") + " " + time;
		    	break parsing;
		    }
		    else if(currentYear == year)
		    {
		    	result = TextLocal.inst.formatChatDate(day, month) + " " + time;
		    	break parsing;
		    }
		    else
		    {
		    	result = TextLocal.inst.formatChatDate(day, month, year) + " " + time;
		    	break parsing;
		    }
	    }
	    
	    return result;
	    */
		return parseShortTime(paramLong);
	}
	
	public static String music(final String url)
	{
		if(VikaTouch.musicIsProxied)
		{
			final String x = URLDecoder.encode(url);
			return download("http://vikamobile.ru:80/tokenproxy.php?" + x);
		}
		else
		{
			return download(url);
		}
	}
	
	public static String download(URLBuilder url)
	{
		return download(url.toString());
	}
	
	public static String downloadE(URLBuilder url) throws VikaNetworkError
	{
		String res = download(url);
		if(res==null)
		{
			VikaTouch.offlineMode=true;
			throw new VikaNetworkError();
		}
		return res;
	}
	
	public static String download(String var1) {
		ByteArrayOutputStream var4 = null;  
		try {
	        
			var4 = new ByteArrayOutputStream();
	         HttpConnection var13 = null;
	         var13	= (HttpConnection) Connector.open(var1);
	         var13.setRequestMethod("GET");
	         var13.setRequestProperty("User-Agent", "KateMobileAndroid/51.1 lite-442 (Symbian; SDK 17; x86; Nokia; ru)");
	         
	         InputStream var14 = var13.openInputStream();
	         long var8 = var13.getLength();
	         byte[] var6 = new byte[16384]; 
	         long var10 = 0L;

	         int var7;
	         
			while((var7 = var14.read(var6)) != -1) {
	            var10 += (long)var7;
	            var4.write(var6, 0, var7);
	            var4.flush();
	            
	         }

	         var14.close();
	         var13.close();
	         var4.close();
	        

	         

	         
	      } catch (Exception var12) {
	         

	         
	      }
		String str = null;
		try {
			str = new String(var4.toByteArray(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	   }
	
	
	public static String download_old(String url)
	{
		int step=0;
		VikaCanvasInst.netColor = 0xffff0000;
		HttpConnection httpconn = null;
		InputStream is = null;
		InputStreamReader isr = null;
		String result = null;
		try
		{
			step = 1;
			Connection conn = Connector.open(url);
			step = 2;
			httpconn = (HttpConnection) conn;
			httpconn.setRequestMethod("GET");
			httpconn.setRequestProperty("User-Agent", "KateMobileAndroid/51.1 lite-442 (Symbian; SDK 17; x86; Nokia; ru)");
			step = 3;
			is = httpconn.openInputStream();
			step = 4;
			VikaCanvasInst.netColor = 0xffff00ff;
			isr = new InputStreamReader(is, "UTF-8"); 
			VikaCanvasInst.netColor = 0xffff7f00;
			step = 5;
			StringBuffer sb = new StringBuffer();
			char[] buffer;
			int i;
			if (httpconn.getResponseCode() != 200 && httpconn.getResponseCode() != 401)
			{
				//System.out.println("not 200 and not 401");
				if(httpconn.getHeaderField("Location") != null)
				{
					String replacedURL = httpconn.getHeaderField("Location");
					step = 6;
					try
					{
						isr.close();
					}
					catch (IOException e)
					{ }
					step = 7;
					httpconn.close();
					step = 8;
					httpconn = (HttpConnection) Connector.open(replacedURL);
					step = 9;
					httpconn.setRequestMethod("GET");
					httpconn.setRequestProperty("User-Agent", "KateMobileAndroid/51.1 lite-442 (Symbian; SDK 17; x86; Nokia; ru)");
					step = 10;
					is = httpconn.openInputStream();
					step = 11;
					isr = new InputStreamReader(is, "UTF-16");
					step = 12;
					sb = new StringBuffer();
					if (httpconn.getResponseCode() == 200 || httpconn.getResponseCode() == 401)
					{
						buffer = new char[10000];
	
						while ((i = isr.read(buffer, 0, buffer.length)) != -1)
						{
							sb.append(buffer, 0, i);
						}
	
					}
					step = 13;
				}
			}
			else
			{
				buffer = new char[10000];
				step = 14;
				while ((i = isr.read(buffer, 0, buffer.length)) != -1)
				{
					VikaCanvasInst.netColor = 0xff00ff00;
					sb.append(buffer, 0, i);
					
				}
				buffer = null;
				step = 15;
			}
			
			result = sb.toString();
			step = 16;
			//result = replace(sb.toString(), "<br>", " ");
		}
		catch (RuntimeException e)
		{
			VikaTouch.notificate(new VikaNotification(VikaNotification.ERROR, TextLocal.inst.get("error.net"), e.toString()+", step "+step, null));
		}
		catch (IOException e)
		{
			VikaTouch.notificate(new VikaNotification(VikaNotification.ERROR, TextLocal.inst.get("error.net"), e.toString()+", step "+step, null));
		}
		finally
		{
			try
			{
				VikaCanvasInst.netColor = 0xffffff00;
				if(isr != null)
					isr.close();
				if(is != null)
					is.close();
			}
			catch (IOException e)
			{
				VikaTouch.notificate(new VikaNotification(VikaNotification.ERROR, TextLocal.inst.get("error.net"), e.toString()+", disposing data", null));
			}
			catch (RuntimeException e)
			{
				VikaTouch.notificate(new VikaNotification(VikaNotification.ERROR, TextLocal.inst.get("error.net"), e.toString()+", disposing data", null));
			}
			try
			{
				VikaCanvasInst.netColor = 0xff0000ff;
				if(httpconn != null)
					httpconn.close();
				VikaCanvasInst.netColor = 0xff00ffff;
			}
			catch (IOException e)
			{
				VikaTouch.notificate(new VikaNotification(VikaNotification.ERROR, TextLocal.inst.get("error.net"), e.toString()+", disposing http", null));
			}
			catch (RuntimeException e)
			{
				VikaTouch.notificate(new VikaNotification(VikaNotification.ERROR, TextLocal.inst.get("error.net"), e.toString()+", disposing http", null));
			} catch (Throwable eee) {
				VikaTouch.notificate(new VikaNotification(VikaNotification.ERROR, TextLocal.inst.get("error.net"), eee.toString()+", disposing http", null));
			}
		}
		VikaCanvasInst.netColor = 0xff000000;
		return result;
	}
	/*
	
	public static String sendPostRequest(String url, String vars)
	{
		HttpConnection httpconn = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;
		String result = "";

		try {
			httpconn = (HttpConnection) Connector.open(url, 3);
			httpconn.setRequestMethod("POST");
			dos = httpconn.openDataOutputStream();
			byte[] bytes = vars.getBytes();

			int i;
			for (i = 0; i < bytes.length; ++i)
			{
				dos.writeByte(bytes[i]);
			}
			dis = new DataInputStream(httpconn.openInputStream());
			for (; (i = dis.read()) != -1; result += (char) i);
		}
		catch (IOException e)
		{
			result = "ERROR";
		}
		finally
		{
			try
			{
				if (httpconn != null)
				{
					httpconn.close();
				}
			}
			catch (IOException e)
			{
				
			}

			try
			{
				if (dis != null)
				{
					dis.close();
				}
			}
			catch (IOException e)
			{
				
			}

			try
			{
				if (dos != null)
				{
					dos.close();
				}
			}
			catch (IOException e)
			{
				
			}

		}

		return result;
	}

	public static String strToHex(String str)
	{
		char[] chars = str.toCharArray();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < chars.length; ++i)
		{
			char c = chars[i];
			sb.append(Integer.toHexString(c).toUpperCase());
		}

		return sb.toString();
	}
	
	*/
	public static String replace(String str, String from, String to)
	{
		
		final StringBuffer sb = new StringBuffer();
		int j = str.indexOf(from);
		int k = 0;
		
		if(j == -1)
			return str;

		for (int i = from.length(); j != -1; j = str.indexOf(from, k))
		{
			sb.append(str.substring(k, j)).append(to);
			k = j + i;
		}

		sb.append(str.substring(k, str.length()));
		return sb.toString();
	}
	
	
	public static boolean startsWith(String str, String need)
	{
		int l = need.length();
		return str.substring(0,l).equalsIgnoreCase(need);
	}
	
	public static Image resize(Image image, int width, int height)
	{
		
		int origWidth = image.getWidth();
		int origHeight = image.getHeight();
		if (height == -1) {
			height = width * origHeight / origWidth;
		}
		return ImageUtils.resize(image, width, height, false, false);
	}

	public static Image downloadImage(String url) 
			throws IOException
	{
		//if(!Settings.https)
			url = replace(url, "https:", "http:");
		// кеширование картинок включается если запрос http
		boolean caching = !startsWith(url, "file") && Settings.cacheImages;
		if(url.indexOf("camera_50") >= 0)
		{
			return VikaTouch.cameraImg;
		}
		if(url.indexOf("php") >= 0 || url.indexOf("getVideoPreview") >= 0)
		{
			caching = false;
		}
		//System.out.println(url + " " + caching);
		String filename = null;
		if(caching)
		{
			try
			{
				filename = url;

				if(filename.indexOf("?") > 0)
					filename = filename.substring(0, filename.indexOf("?"));
				
				filename = 
					replace(
						replace(
							replace(
								replace(
									replace(
										replace(
											replace(
												replace(
													replace(
														replace(
															replace(
																replace(
																	replace(
																		replace(
																			replace(
										filename
										, VikaTouch.API, "")
										, "vk-api-proxy.xtrafrancyz.net", "")
										, "?ava=1", "")
										, ".userapi.", "")
						, "http:", "")
						, "https:", "")
						, "=", "")
						, "?", "")
						, ":80", "")
						, "\\", "")
						, "/", "")
						, ":443", "")
						, "_", "")
						, "vk.comimages", "")
						, "com", "");
			
				//System.out.println(filename+" ||| "+url);
			
				Image image = ImageStorage.get(filename);
				if(image != null)
				{
					return image;
				}
				
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		}

		//ByteArrayOutputStream baos = null;
		final Connection con = Connector.open(url);
		if(con instanceof HttpConnection)
		{
			HttpConnection var2 = (HttpConnection) con; 
			var2.setRequestMethod("GET");
			var2.setRequestProperty("User-Agent", "KateMobileAndroid/51.1 lite-442 (Symbian; SDK 17; x86; Nokia; ru)");
			int respcode = var2.getResponseCode();
			if (respcode != 200 && respcode != 401) {
				if(var2.getHeaderField("Location") != null)
				{
					url = var2.getHeaderField("Location");
				}
				else
				{
					
					throw new IOException("" + respcode);
				}
			}
			
			var2.close();
		}
		else if(con instanceof FileConnection)
		{
			caching = false;
			con.close();

			DataInputStream dis = ((FileConnection) Connector.open(url)).openDataInputStream();

			return Image.createImage(dis);
			
			/*
			try
			{
				int length = (int) fileconn.fileSize();
				byte[] imgBytes = new byte[length];
				
				dis.readFully(imgBytes);
				
				try
				{
					return Image.createImage(imgBytes, 0, imgBytes.length);
				}
				catch (IllegalArgumentException e)
				{
					
				}
			}
			finally
			{
				if (dis != null)
				{
					dis.close();
				}

				if (fileconn != null)
				{
					fileconn.close();
				}

				if (baos != null)
				{
					baos.close();
				}

			}*/
		}
		con.close();
		DataInputStream cin = ((ContentConnection) Connector.open(url)).openDataInputStream();

		Image image = Image.createImage(cin);
		 if(image != null && caching)
			{
				ImageStorage.save(filename, image);
			}
		 return image;
		 /*
		try
		{
			 /*
			int length;
			byte[] imgBytes;
			if ((length = (int) contconn.getLength()) != -1)
			{
				imgBytes = new byte[length];
				cin.readFully(imgBytes);
			}
			else
			{
				baos = new ByteArrayOutputStream();
				
				int i;
				while ((i = cin.read()) != -1)
				{
					baos.write(i);
				}

				imgBytes = baos.toByteArray();
				baos.close();
			}
			try
			{
				Image image = Image.createImage(imgBytes, 0, imgBytes.length);
				try
				{
					if(image != null && caching)
					{
						ImageStorage.save(filename, image);
					}
				}
				catch (Exception e)
				{
					
				}
				return image;
			}
			catch (IllegalArgumentException e)
			{
				
			}
			
		}
		finally
		{
			if (cin != null)
			{
				cin.close();
			}

			if (contconn != null)
			{
				contconn.close();
			}

			if (baos != null)
			{
				baos.close();
			}
		}

		return null;
		*/
	}

	public static String time(Date date)
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int hours = cal.get(11);
		int minutes = cal.get(12);
		String time = TextLocal.inst.formatTime(hours, minutes);
		return time;
	}
/*
	public static String fullDate(Date date)
	{

		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		int hour = cal.get(Calendar.HOUR);
		int minutes = cal.get(Calendar.MINUTE);
		return TextLocal.inst.formatFullDate(day, month, year, hour, minutes);
	}
*/
	public static void request(URLBuilder url)
		throws IOException
	{
		makereq(url.toString());
	}

	public static void makereq(String url)
		throws IOException
	{
		HttpConnection httpconn = null;
		Connection conn = Connector.open(url);
		httpconn = (HttpConnection) conn;
		httpconn.setRequestMethod("GET");
		httpconn.setRequestProperty("User-Agent", "KateMobileAndroid/51.1 lite-442 (Symbian; SDK 17; x86; Nokia; ru)");
		httpconn.openInputStream();	
		httpconn.close();
	}
	
	//функция адаптированна* из вика мобиле
	public static String strToHex(String var0) {
		char[] var4 = var0.toCharArray();
		StringBuffer var1 = new StringBuffer();

		for (int var2 = 0; var2 < var4.length; ++var2) {
			char var3 = var4[var2];
			var1.append(Integer.toHexString(var3).toUpperCase());
		}

		return var1.toString();
	}
	
	public static String cut(String str, int l)
	{
		if(str==null) return "".intern();
		try
		{
			if(str.length()<l+2)
			{
				return str;
			}
			return str.substring(0, l)+"...";
		}
		catch (StringIndexOutOfBoundsException e)
		{
			return str;
		}
	}
	
	public static String[] searchLinks(String text)
	{
		if(text == null || text.length()<2) return null;
		int lm = 8; // links max (больше на экран не влезет (смотря какой конечно))
		String[] la = new String[lm];
		int li = 0; // индекс в массиве
		int tl = text.length();
		
		final String[] glinks = new String[] { "http://", "https://", "rtsp://", "ftp://", "smb://" }; // вроде всё. Ага, я слал/принимал пару раз ссылки на расшаренные папки как smb://server/folder
		try
		{
			//System.out.println(text);
			//System.out.println("tl "+tl);
			// Поиск внешних ссылок
			// сначала ищем их на случай сообщения
			// @id89277233 @id2323 @id4 @id5 @id6 ... [ещё 100509 @] ... @id888292, http://что-тоТам
			// В беседе вики такое постоянно.
			for(int gli=0; gli<glinks.length; gli++)
			{
				int ii = 0; // Indexof Index
				while(true)
				{
					ii = text.indexOf(glinks[gli], ii);
					//System.out.println("ii "+ii);
					if(ii == -1)
					{
						break;
					}
					else
					{
						int lci = ii+6;
						while(lci<tl && text.charAt(lci)!=' ') { lci++; }
						String l = text.substring(ii, lci);
						la[li] = l;
						li++;
						if(li>=lm) return la;
						ii = lci;
					}
				}
			}
					
			// Поиск ссылок ВК
			int cc = 0; // current char
			while(cc<tl)
			{
				char c = text.charAt(cc);
				if(c=='@')
				{
					int cs = cc;
					cc++;
					while(cc<tl && text.charAt(cc)!=' ' && text.charAt(cc)!=']') { cc++; }
					String l = text.substring(cs, cc);
					la[li] = l;
					li++;
					if(li>=lm) return la;
				}
				else if(c=='[')
				{
					cc++;
					int cs = cc;
					while(cc<tl && text.charAt(cc)!='|') { cc++; }
					String l = text.substring(cs, cc);
					la[li] = l;
					li++;
					if(li>=lm) return la;
				}
				cc++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println("links c "+li);
		return la;
	}
	
	public static MainScreen openPage(int id)
	{
		if(id<0)
		{
			GroupPageScreen p = new GroupPageScreen(-id);
			p.load();
			return p;
		}
		else
		{
			ProfilePageScreen p = new ProfilePageScreen(id);
			p.load();
			return p;
		}
	}
	
	public static void openLink(String s)
	{
		try
		{
			if(s.indexOf("@")==0)
			{
				// упоминание
			}
			else if(s.indexOf("id")==0)
			{
				try
				{
					VikaTouch.setDisplay(VikaUtils.openPage(Integer.parseInt(s.substring(2))), 1);
				}
				catch(RuntimeException e) { }
			}
			else if(s.indexOf("rtsp://")!=-1)
			{
				VikaTouch.openRtspLink(s);
			}
			else if(s.indexOf("youtube.com")!=-1)
			{
				if(!Settings.symtube)
				{
					VikaTouch.appInst.platformRequest(s);
				}
				else
				{
					VikaTouch.appInst.platformRequest("http://vikamobile.ru/getl.php?url="+URLDecoder.encode(s));
				}
			}
			else
				VikaTouch.appInst.platformRequest(s);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	// unfinished
	public static boolean processVkLink(String link)
	{
		link = replace(replace(link, "https://", ""), "http://", "");
		link = replace(link, "m.vk.com", "vk.com");
		try
		{
			link = link.substring(0, link.indexOf("?"));
		}
		catch(Exception e) { }
		if(link.indexOf("vk.com/")!=0) return false;
		String target = link.substring(7);
		if(target.indexOf("wall")==0)
		{
			
		}
		return true;
	}
	
	public static int lerp(final int start, final int target, final int mul, final int div)
	{
		return start + ((target-start) * mul / div);
	}
	public static int clamp(final int val, final int min, final int max)
	{
		return Math.max(Math.min(val, max), min);
	}
}