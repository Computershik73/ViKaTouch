package vikatouch.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import tube42.lib.imagelib.ImageUtils;
import vikatouch.VikaTouch;
import vikatouch.caching.ImageStorage;
import vikatouch.locale.TextLocal;
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
			return download("http://vkt.nnproject.tk/tokenproxy.php?" + x);
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
	
	public static String download(String url)
	{
		HttpConnection httpconn = null;
		InputStream is = null;
		InputStreamReader isr = null;
		String result = null;
		
		try
		{
			Connection conn = Connector.open(url);
			//System.out.println("conn is " + conn.toString() + " " + conn.getClass().getName());
			httpconn = (HttpConnection) conn;
			httpconn.setRequestMethod("GET");
			httpconn.setRequestProperty("User-Agent", "KateMobileAndroid/51.1 lite-442 (Symbian; SDK 17; x86; Nokia; ru)");
			is = httpconn.openInputStream();
			isr = new InputStreamReader(is, "UTF-8"); 
			StringBuffer sb = new StringBuffer();
			char[] buffer;
			int i;
			if (httpconn.getResponseCode() != 200 && httpconn.getResponseCode() != 401)
			{
				//System.out.println("not 200 and not 401");
				if(httpconn.getHeaderField("Location") != null)
				{
					final String replacedURL = httpconn.getHeaderField("Location");
					try
					{
						isr.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					httpconn.close();
					httpconn = (HttpConnection) Connector.open(replacedURL);
					httpconn.setRequestMethod("GET");
					httpconn.setRequestProperty("User-Agent", "KateMobileAndroid/51.1 lite-442 (Symbian; SDK 17; x86; Nokia; ru)");
					is = httpconn.openInputStream();
					isr = new InputStreamReader(is, "UTF-16");
					sb = new StringBuffer();
					if (httpconn.getResponseCode() == 200 || httpconn.getResponseCode() == 401)
					{
						buffer = new char[10000];
	
						while ((i = isr.read(buffer, 0, buffer.length)) != -1)
						{
							sb.append(buffer, 0, i);
						}
	
					}
				}
			}
			else
			{
				//System.out.println("yay"+httpconn.getResponseCode());
				buffer = new char[10000];
				
				while ((i = isr.read(buffer, 0, buffer.length)) != -1)
				{
					sb.append(buffer, 0, i);
				}
				
				buffer = null;

			}
			
			result = sb.toString();

			//result = replace(sb.toString(), "<br>", " ");
		}
		catch (Throwable e)
		{
			System.out.println("Fail " + url);
			e.printStackTrace();
		}

		try
		{
			if(isr != null)
				isr.close();
			if(is != null)
				is.close();
			if(httpconn != null)
				httpconn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

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
		if(!Settings.https)
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
}