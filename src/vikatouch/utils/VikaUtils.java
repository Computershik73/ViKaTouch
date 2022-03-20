package vikatouch.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.utils.images.IconsManager;
import tube42.lib.imagelib.ImageUtils;
import vikatouch.VikaNetworkError;
import vikatouch.VikaTouch;
import vikatouch.caching.ImageStorage;
import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;
import vikatouch.screens.page.GroupPageScreen;
import vikatouch.screens.page.ProfilePageScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.text.TextEditor;
import vikatouch.utils.url.URLBuilder;
import vikatouch.utils.url.URLDecoder;



/**
 * @author Shinovon
 * @author Feodor0090
 * 
 */
public final class VikaUtils {
	private static Thread fileThread;
	private static Object downloadLock = new Object();
	public static Thread pronouncer;
	public static Player playertext;
	
	public static long TimeOffset() {
		long diff = 0;
		//https://api.vk.com/method/utils.getServerTime?access_token=a4ffbd174de&v=5.130
			try {
				String x = VikaUtils.download(VikaTouch.API + "/method/utils.getServerTime?access_token="
						+ VikaTouch.accessToken + "&v=" + VikaTouch.API_VERSION);
				long timest = new JSONObject(x).getLong("response")*1000;
				diff = System.currentTimeMillis() - timest;
				//VikaTouch.sendLog(String.valueOf(timest)+ " " + String.valueOf(System.currentTimeMillis())+ " " + String.valueOf(diff));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return diff;
		
	}
	
	public static String parseShortTime(final long paramLong) {
		final Calendar cal = Calendar.getInstance();

		final Date date = new Date(paramLong * 1000L);
		final Date currentDate = new Date(System.currentTimeMillis());

		cal.setTime(date);
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		final int year = cal.get(Calendar.YEAR);
		final int month = cal.get(Calendar.MONTH);
		

		cal.setTime(currentDate);
		final int curday = cal.get(Calendar.DAY_OF_MONTH);
		final int currentYear = cal.get(Calendar.YEAR);

		final String time = time(date);

		final long dayDelta = (long) (Math.floor((System.currentTimeMillis() / 1000L / 60L / 60L / 24L)) - (long) (Math.floor(paramLong / 60L / 60L / 24L)));

		String result = "Давно";

		parsing: {
			if (dayDelta == 0) {
				if (curday==day) {
				result = time;
				break parsing;
				} else {
					//if (System.currentTimeMillis()-paramLong * 1000L<=14400000)
					result = TextLocal.inst.get("date.yesterday");
					break parsing;
				}
			} else if (dayDelta == 1) {
				if (curday==day+1) {
				result = TextLocal.inst.get("date.yesterday");
				break parsing;
				} else {
					//result = "pizdec" + String.valueOf(curday) +  " " + String.valueOf(day);
					result = time;
					break parsing;
				}
				
			} else if (currentYear == year) {
				if (curday==day+1) { 
					result = TextLocal.inst.get("date.yesterday");
					break parsing;
				} else {
					result = TextLocal.inst.formatChatDate(day, month);
					break parsing;
				}
				
			} else {
				result = TextLocal.inst.formatChatDate(day, month, year);
				break parsing;
			}
		}

		return result;
	}

	public static String parseTime(final long paramLong) {
		final Calendar cal = Calendar.getInstance();

		final Date date = new Date(paramLong * 1000L);
		final Date currentDate = new Date(System.currentTimeMillis());

		cal.setTime(date);
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		final int year = cal.get(Calendar.YEAR);
		final int month = cal.get(Calendar.MONTH);

		cal.setTime(currentDate);
		final int curday = cal.get(Calendar.DAY_OF_MONTH);
		final int currentYear = cal.get(Calendar.YEAR);

		final String time = time(date);

		final long dayDelta = (long) (Math.floor((System.currentTimeMillis() / 1000L / 60L / 60L / 24L))) - ((long)Math.floor((paramLong / 60L / 60L / 24L)));

		String result;

		parsing: {
			if (dayDelta == 0) {
				if (curday==day) {
				result = TextLocal.inst.get("date.todayat");
				result += " " + time;
				break parsing;
				} else {
					result = TextLocal.inst.get("date.yesterday");
					result += " " + time;
					break parsing;
				}
			} else if (dayDelta == 1) {
				if (curday==day+1) {
				result = TextLocal.inst.get("date.yesterday");
				result += " " + time;
				break parsing;
				} else {
					result = TextLocal.inst.get("date.todayat");
					result += " " + time;
					break parsing;
				}
			} else if (currentYear == year) {
				if (curday==day+1) { 
					result = TextLocal.inst.get("date.yesterday");
					result += " " + time;
					break parsing;
				} else {
				result = TextLocal.inst.formatShortDate(day, month);
				result += " " + time;
				break parsing;
				}
			} else {
				result = TextLocal.inst.formatDate(day, month, year);
				result += " " + time;
				break parsing;
			}
		}

		return result;
	}

	public static String parseMsgTime(final long paramLong) {
		/*
		 * final Calendar cal = Calendar.getInstance();
		 * 
		 * final Date date = new Date(paramLong * 1000L); final Date currentDate = new
		 * Date(System.currentTimeMillis());
		 * 
		 * cal.setTime(date); final int day = cal.get(Calendar.DAY_OF_MONTH); final int
		 * year = cal.get(Calendar.YEAR); final int month = cal.get(Calendar.MONTH);
		 * 
		 * cal.setTime(currentDate); final int currentYear = cal.get(Calendar.YEAR);
		 * 
		 * final String time = time(date);
		 * 
		 * final long dayDelta = (paramLong / 60L / 60L / 24L) -
		 * (System.currentTimeMillis() / 1000L / 60L / 60L / 24L);
		 * 
		 * String result;
		 * 
		 * parsing: { if(dayDelta == 0) { result = time; break parsing; } else
		 * if(dayDelta == 1) { result = TextLocal.inst.get("date.yesterday") + " " +
		 * time; break parsing; } else if(currentYear == year) { result =
		 * TextLocal.inst.formatChatDate(day, month) + " " + time; break parsing; } else
		 * { result = TextLocal.inst.formatChatDate(day, month, year) + " " + time;
		 * break parsing; } }
		 * 
		 * return result;
		 */
		return parseShortTime(paramLong);
	}

	public static String music(final String url) throws IOException, InterruptedException {
		
		if ((Settings.musicviavikaserver == true) || (VikaTouch.isS40())) {
			final String x = URLDecoder.encode(url);
			Settings.loadMusicViaHttp = Settings.AUDIO_HTTPS;
			//VikaTouch.sendLog("is http://vikamobile.ru:80/tokenproxy.php?" + x);
			return download("http://vikamobile.ru:80/tokenproxy.php?" + x);
		} else {
			//VikaTouch.sendLog("notis " + url);
			return download(url);
		}
	}
	
	public static void logToFile(String text) {
	//return;
	FileConnection fileCon = null;
	
	try {
		fileCon = (FileConnection) Connector.open(System.getProperty("fileconn.dir.music") + "log.txt", 3);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	if (!fileCon.exists()) {
		try {
			fileCon.create();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} else {
		/*try {
			fileCon.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fileCon.create();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	OutputStream stream = null;
	try {
		stream = fileCon.openOutputStream(fileCon.fileSize());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	try {
		stream.write(text.getBytes());
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	try {
		stream.flush();
		stream.close();
		fileCon.close();
	} catch (Exception e2) {
		e2.printStackTrace();
	}
	
}

	public static String download(URLBuilder url) throws IOException, InterruptedException {
		return download(url.toString());
	}

	public static String downloadE(URLBuilder url) throws VikaNetworkError, InterruptedException {
		try {
			return download(url);
		} catch (IOException e) {
			throw new VikaNetworkError(e.toString());
		}
	}

	public static String download(String url) throws IOException, InterruptedException {
		if (VikaTouch.isS40()) {
			VikaTouch.isdownloading=1;
			synchronized (downloadLock) {
				return download0(url);
			}
		} else {
			VikaTouch.isdownloading=1;
			return download0(url);
		}
	}
	
	
	//http://translate.google.com/translate_tts?ie=UTF-8&total=1&idx=0&textlen=32&client=tw-ob&q=Test&tl=En-gb
	public static void pronounceText(final String text) throws IOException {
		
		/*if (pronouncer != null)
		{
				if (pronouncer.isAlive()) {
					return;
					//pronouncer.interrupt();
				}	
			}*/
			
		//new Thread() {
			//public void run() {
				String textt = text;
				textt=textt.replace(':', ' ');
				textt=textt.replace('/', ' ');
				String url="http://translate.google.com/translate_tts?ie=UTF-8&total=1&idx=0&textlen=99232&client=tw-ob&q="+URLDecoder.encode(textt)+"&tl=Ru-ru";
				//VikaTouch.sendLog(url);
				byte[] file = null;
				ByteArrayOutputStream var4 = null;
				HttpConnection var13 = null;
				InputStream var14 = null;
				try {
					var4 = new ByteArrayOutputStream();
					var13 = (HttpConnection) Connector.open(url, Connector.READ);
					var13.setRequestMethod("GET");
					var13.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36");
					int i = var13.getResponseCode();
					if (i != 200 && i != 401) {
						// System.out.println("not 200 and not 401");
						if (var13.getHeaderField("Location") != null) {
							String replacedURL = var13.getHeaderField("Location");
							var13.close();
							var13 = (HttpConnection) Connector.open(replacedURL, Connector.READ);
							var13.setRequestMethod("GET");
							var13.setRequestProperty("User-Agent",
									"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36");
							var14 = var13.openInputStream();
							// long var8 = var13.getLength();
							byte[] var6 = new byte[VikaTouch.isNotS60()? 4096 : 10000];
							// long var10 = 0L;

							int var7;

							while ((var7 = var14.read(var6)) != -1) {
								// var10 += (long) var7;
								var4.write(var6, 0, var7);
								var4.flush();
							}
						} else {
							
						}
					} else {
						var14 = var13.openInputStream();
						// long var8 = var13.getLength();
						//524288
						byte[] var6 = new byte[VikaTouch.isNotS60()? 4096 : 10000];
						// long var10 = 0L;

						int var7;

						while ((var7 = var14.read(var6)) != -1) {
							// var10 += (long) var7;
							var4.write(var6, 0, var7);
							var4.flush();
						}
					}
					
					
					file = var4.toByteArray();
				} catch (Throwable e) {
					//throw new IOException(e.toString());
				} finally {
					if (var14 != null)
						try {
							var14.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					if (var13 != null)
						try {
							var13.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					if (var4 != null)
						try {
							var4.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				ByteArrayInputStream bis = new ByteArrayInputStream(file);
				//source.read(file);
				/*FileConnection fileCon = null ;
				try {
					fileCon = (FileConnection) Connector.open(System.getProperty("fileconn.dir.music") + "temp.mp3", Connector.READ_WRITE);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!fileCon.exists()) {
					try {
						fileCon.create();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						fileCon.delete();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						fileCon.create();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				OutputStream stream = null;
				try {
					stream = fileCon.openOutputStream(fileCon.fileSize());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					stream.write(file);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					stream.flush();
					stream.close();
					fileCon.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				
				
				*/
				
				try {
					//playertext = Manager.createPlayer(System.getProperty("fileconn.dir.music") + "temp.mp3");
					playertext = Manager.createPlayer(bis, "audio/mpeg");
					playertext.realize();
					playertext.prefetch();
					((VolumeControl) playertext.getControl("VolumeControl"))
					.setLevel(100);
					playertext.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MediaException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//}
		//};
		//pronouncer.start();
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	
	public static String getRedirUrl(String oldurl) throws IOException, InterruptedException  {
		HttpConnection var13 = null;
		int i = 0;
		while (i!=200) {
		var13 = (HttpConnection) Connector.open(oldurl, Connector.READ);
		var13.setRequestMethod("GET");
		var13.setRequestProperty("User-Agent",
				"KateMobileAndroid/51.1 lite-442 (Android 4.2.2; SDK 17; x86; LENOVO Lenovo S898t+; ru)");
		 i = var13.getResponseCode();
		 
		if (var13.getHeaderField("Location") != null) {
				oldurl = var13.getHeaderField("Location");
			}
		var13.close();
		}
		
		return oldurl;
		
	}

	private static String download0(String var1) throws InterruptedException {
		/*if (System.currentTimeMillis() - VikaTouch.lastsuccessfullupdatetime>16200) {
			VikaTouch.needstoRedraw=true;
			VikaTouch.silenterror("Сети нет более 2 минут!", false);
			VikaTouch.needstoRedraw=true;
			Player notifplayer;
			try {
				notifplayer = Manager.createPlayer("device://tone");
				notifplayer.realize();
				notifplayer.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MediaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}*/
		ByteArrayOutputStream var4 = null;
		HttpConnection var13 = null;
		InputStream var14 = null;
		VikaTouch.isdownloading=1;
		//VikaUtils.logToFile(var1+"\n");
		try {
			var4 = new ByteArrayOutputStream();
			var13 = (HttpConnection) Connector.open(var1, Connector.READ);
			var13.setRequestMethod("GET");
			var13.setRequestProperty("User-Agent",
					//"KateMobileAndroid/51.1 lite-442 (Android 4.2.2; SDK 17; x86; LENOVO Lenovo S898t+; ru)"
					"com.vk.vkclient/12 (unknown, iPhone OS 9.3.5, iPhone, Scale/2.000000)"
					);
			int i = var13.getResponseCode();
			if (i != 200 && i != 401 && i!= 403) {
				
				// System.out.println("not 200 and not 401");
				if (var13.getHeaderField("Location") != null) {
					String replacedURL = var13.getHeaderField("Location");
					var13.close();
					var13 = (HttpConnection) Connector.open(replacedURL, Connector.READ);
					var13.setRequestMethod("GET");
					var13.setRequestProperty("User-Agent",
							//"KateMobileAndroid/51.1 lite-442 (Android 4.2.2; SDK 17; x86; LENOVO Lenovo S898t+; ru)"
							"com.vk.vkclient/12 (unknown, iPhone OS 9.3.5, iPhone, Scale/2.000000)"
							);
					var14 = var13.openInputStream();
					// long var8 = var13.getLength();
					byte[] var6 = new byte[VikaTouch.isNotS60()? 4096 : 524288];
					// long var10 = 0L;

					int var7;

					while ((var7 = var14.read(var6)) != -1) {
						// var10 += (long) var7;
						var4.write(var6, 0, var7);
						var4.flush();
					}
				} else {
					//Thread.sleep(500);
					//return download0(var1);
					return null;
				}
			} else {
				var14 = var13.openInputStream();
				// long var8 = var13.getLength();
				//524288
				byte[] var6 = new byte[VikaTouch.isNotS60()? 4096 : 524288];
				// long var10 = 0L;

				int var7;

				while ((var7 = var14.read(var6)) != -1) {
					// var10 += (long) var7;
					var4.write(var6, 0, var7);
					var4.flush();
				}
			}
			
			String str = null;
			//if (var1.indexOf("getHistory")>0) {
				//VikaUtils.logToFile(new String(var4.toByteArray()));
				//, "UTF-8");)
			//str = toUTF8(var4.toByteArray());
			//} else {
				str = new String(var4.toByteArray()
				, "UTF-8");
			//}
					//new String(var4.toByteArray());
					//, "UTF-8");
			//str = bytesToStringUTFCustom(var4.toByteArray());
				if (str.indexOf("per second")>0) {
					
				}
				VikaTouch.isdownloading=0;
				VikaTouch.needstoRedraw=true;
				//if (str!=null && str!="") {
				VikaTouch.lastsuccessfullupdatetime=System.currentTimeMillis();
				//VikaUtils.logToFile("pizdec");
				//VikaUtils.logToFile(String.valueOf(VikaTouch.lastsuccessfullupdatetime));
				//}
			return str;
		} catch (Throwable e) {
			VikaTouch.isdownloading=2;
			VikaTouch.needstoRedraw=true;
			//VikaUtils.logToFile(String.valueOf("Throwable " + VikaTouch.lastsuccessfullupdatetime));
			return null;
			//throw new IOException(e.toString());
		} finally {
			if (var14 != null)
				try {
					var14.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (var13 != null)
				try {
					var13.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (var4 != null)
				try {
					var4.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}
	
	
	public static String bytesToStringUTFCustom(byte[] bytes) {
		 char[] buffer = new char[bytes.length >> 1];
		 for(int i = 0; i < buffer.length; i++) {
		  int bpos = i << 1;
		  char c = (char)(((bytes[bpos]&0x00FF)<<8) + (bytes[bpos+1]&0x00FF));
		  buffer[i] = c;
		 }
		 return new String(buffer);
		}
	
	public static String toUTF8(byte [] textarr) {
		String sum="";
		String codes="";
		int start=0;
		int i = 0;
		int length = textarr.length;
		int smilesize = 4;
		//for (int i=0; i<textarr.length; i++) {
		//	codes+=(String.valueOf(textarr[i])+ " ");
		while (i<length) {
		try {
			
			if (((textarr[i])==-16) && ((textarr[i+1])==-97)) {
				//VikaUtils.logToFile("smiledetected at "+String.valueOf(i)+ " textarr length is " + String.valueOf(length));
				start = i;
				//codes+=(String.valueOf(textarr[i])+ " ");
				//byte [] temp = new byte [10];
				//System.arraycopy(textarr, start, temp, 0, 10);
				byte [] before = new byte [start];
				System.arraycopy(textarr, 0, before, 0, start);
				try {
					String befst = new String(before, "UTF-8");
					for (int j =0; j<before.length; j++) {
						codes+=(String.valueOf(before[j])+ " ");
					}
				//	VikaUtils.logToFile("befstring: "+ codes + " " + befst+"\n");
					codes="";
					sum+=befst;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte [] temp = new byte [smilesize];
				System.arraycopy(textarr, start, temp, 0, smilesize);
				sum+=new String (temp);
				length = length-start-smilesize;
				System.arraycopy(textarr, start+smilesize, textarr, 0, length);
				
				i++;
				//VikaUtils.logToFile("successfully extracted, new textarr size is: "+String.valueOf(length));
			} else {
				i++;
			}
		} catch (Throwable eee ) {}
			//i++;
			
		}
		
		//if (start==0) {
			try {
				sum+=new String (textarr, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}
		//codes+="\n";
		
		/*try {
			byte [] aa = VikaUtils.downloadBytes("http://vikamobile.ru:80/smile.php");
			codes+="smile ";
			for (int i=0; i<aa.length; i++) {
				//if ((((char)textarr[i])==240) || (((char)textarr[i])==159)) {
					codes+=(String.valueOf(aa[i])+ " ");
				//}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
	//	codes+="\n";
		//VikaUtils.logToFile(sum);
		/*try {
			sum = new String(textarr, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//VikaUtils.logToFile(codes);
		return sum;
	}
	
	private static byte[] downloadBytes(String var1) throws IOException, InterruptedException {
		ByteArrayOutputStream var4 = null;
		HttpConnection var13 = null;
		InputStream var14 = null;
		try {
			var4 = new ByteArrayOutputStream();
			var13 = (HttpConnection) Connector.open(var1, Connector.READ);
			var13.setRequestMethod("GET");
			var13.setRequestProperty("User-Agent",
					"KateMobileAndroid/51.1 lite-442 (Android 4.2.2; SDK 17; x86; LENOVO Lenovo S898t+; ru)");
			int i = var13.getResponseCode();
			if (i != 200 && i != 401) {
				// System.out.println("not 200 and not 401");
				if (var13.getHeaderField("Location") != null) {
					String replacedURL = var13.getHeaderField("Location");
					var13.close();
					var13 = (HttpConnection) Connector.open(replacedURL, Connector.READ);
					var13.setRequestMethod("GET");
					var13.setRequestProperty("User-Agent",
							"KateMobileAndroid/51.1 lite-442 (Android 4.2.2; SDK 17; x86; LENOVO Lenovo S898t+; ru)");
					var14 = var13.openInputStream();
					// long var8 = var13.getLength();
					byte[] var6 = new byte[VikaTouch.isNotS60()? 4096 : 524288];
					// long var10 = 0L;

					int var7;

					while ((var7 = var14.read(var6)) != -1) {
						// var10 += (long) var7;
						var4.write(var6, 0, var7);
						var4.flush();
					}
				} else {
					
				}
			} else {
				var14 = var13.openInputStream();
				// long var8 = var13.getLength();
				//524288
				byte[] var6 = new byte[VikaTouch.isNotS60()? 4096 : 524288];
				// long var10 = 0L;

				int var7;

				while ((var7 = var14.read(var6)) != -1) {
					// var10 += (long) var7;
					var4.write(var6, 0, var7);
					var4.flush();
				}
			}
			
			
			return var4.toByteArray();
		} catch (Throwable e) {
			//throw new IOException(e.toString());
		} finally {
			if (var14 != null)
				var14.close();
			if (var13 != null)
				var13.close();
			if (var4 != null)
				var4.close();
		}
		return null;
	}
	

	public static String download_old(String url) throws IOException {
		HttpConnection httpconn = null;
		InputStream is = null;
		InputStreamReader isr = null;
		String result = null;
		Connection conn = Connector.open(url, Connector.READ);
		httpconn = (HttpConnection) conn;
		httpconn.setRequestMethod("GET");
		httpconn.setRequestProperty("User-Agent", "KateMobileAndroid/78.1 lite-500 (Android 11; SDK 30; arm64-v8a; Xiaomi POCO X3 NFC; ru)"); 
				//"KateMobileAndroid/51.1 lite-442 (Symbian; SDK 17; x86; Nokia; ru)");

		StringBuffer sb = new StringBuffer();
		char[] buffer;
		int i;
		System.out.println(url + " " + httpconn.getResponseCode());
		if (httpconn.getResponseCode() != 200 && httpconn.getResponseCode() != 401) {
			// System.out.println("not 200 and not 401");
			if (httpconn.getHeaderField("Location") != null) {
				String replacedURL = httpconn.getHeaderField("Location");
				httpconn.close();
				httpconn = (HttpConnection) Connector.open(replacedURL, Connector.READ);
				httpconn.setRequestMethod("GET");
				httpconn.setRequestProperty("User-Agent", "KateMobileAndroid/78.1 lite-500 (Android 11; SDK 30; arm64-v8a; Xiaomi POCO X3 NFC; ru)");
						//"KateMobileAndroid/51.1 lite-442 (Symbian; SDK 17; x86; Nokia; ru)");
				is = httpconn.openInputStream();
				isr = new InputStreamReader(is, "UTF-8");
				sb = new StringBuffer();
				if (httpconn.getResponseCode() == 200 || httpconn.getResponseCode() == 401) {
					buffer = new char[10000];

					while ((i = isr.read(buffer, 0, buffer.length)) != -1) {
						sb.append(buffer, 0, i);
					}

				}
			}
		} else {
			is = httpconn.openInputStream();
			isr = new InputStreamReader(is, "UTF-8");

			buffer = new char[10000];
			while ((i = isr.read(buffer, 0, buffer.length)) != -1) {
				sb.append(buffer, 0, i);

			}
			buffer = null;
		}

		result = sb.toString();
		if (isr != null)
			isr.close();
		if (is != null)
			is.close();
		return result;
	}

	/*
	 * 
	 * public static String sendPostRequest(String url, String vars) {
	 * HttpConnection httpconn = null; DataInputStream dis = null; DataOutputStream
	 * dos = null; String result = "";
	 * 
	 * try { httpconn = (HttpConnection) Connector.open(url, 3);
	 * httpconn.setRequestMethod("POST"); dos = httpconn.openDataOutputStream();
	 * byte[] bytes = vars.getBytes();
	 * 
	 * int i; for (i = 0; i < bytes.length; ++i) { dos.writeByte(bytes[i]); } dis =
	 * new DataInputStream(httpconn.openInputStream()); for (; (i = dis.read()) !=
	 * -1; result += (char) i); } catch (IOException e) { result = "ERROR"; }
	 * finally { try { if (httpconn != null) { httpconn.close(); } } catch
	 * (IOException e) {
	 * 
	 * }
	 * 
	 * try { if (dis != null) { dis.close(); } } catch (IOException e) {
	 * 
	 * }
	 * 
	 * try { if (dos != null) { dos.close(); } } catch (IOException e) {
	 * 
	 * }
	 * 
	 * }
	 * 
	 * return result; }
	 * 
	 * public static String strToHex(String str) { char[] chars = str.toCharArray();
	 * StringBuffer sb = new StringBuffer();
	 * 
	 * for (int i = 0; i < chars.length; ++i) { char c = chars[i];
	 * sb.append(Integer.toHexString(c).toUpperCase()); }
	 * 
	 * return sb.toString(); }
	 * 
	 */
	public static String replace(String str, String from, String to) {

		final StringBuffer sb = new StringBuffer();
		int j = str.indexOf(from);
		int k = 0;

		if (j == -1)
			return str;

		for (int i = from.length(); j > -1; j = str.indexOf(from, k)) {
			sb.append(str.substring(k, j)).append(to);
			k = j + i;
		}

		sb.append(str.substring(k, str.length()));
		return sb.toString();
	}

	public static boolean startsWith(String str, String need) {
		int l = need.length();
		return str.substring(0, l).equalsIgnoreCase(need);
	}

	public static Image resize(Image image, int width, int height) {

		int origWidth = image.getWidth();
		int origHeight = image.getHeight();
		if (height == -1) {
			height = width * origHeight / origWidth;
		}
		return ImageUtils.resize(image, width, height, !Settings.fastImageScaling, !Settings.fastImageScaling);
	}

	public static Image downloadImage(String url) throws IOException, InterruptedException {
		//if (ImageStorage.has(url)) {
		//	return ImageStorage.get(url);
		//}
		
		
		Image i;
		VikaTouch.isdownloading=1;
		if (VikaTouch.isS40()) {
			synchronized (downloadLock) {
			i = downloadImage0(url);
			//ImageStorage.save(url, i);
			
				return i;
			}
		} else {
			i = downloadImage0(url);
			//ImageStorage.save(url, i);
			return i;
		}
	}
	
	public static void freeMemoryLow() {
		//tokenRMS = null;
		VikaTouch.newsScr = null;
		VikaTouch.loginScr = null;
		VikaTouch.splash = null;
		System.gc();
	}

	private static Image downloadImage0(String url) throws IOException, InterruptedException {
		try {
			VikaTouch.isdownloading=1;
			if (!Settings.https)
				// url = replace(url, "https:", "http:");
				// if (vkApi != "https://api.vk.com:443") {
				url = replace(
						replace(replace(replace(url, "https://cs", "http://vk-api-proxy.vikamobile.ru/_/cs"),
								"https://vk-api", "http://vk-api"), "https:\\/\\/vk-api", "http://vk-api"),
						"https://sun", "http://vk-api-proxy.vikamobile.ru/_/sun");
			// url = replace(url, )
			// кеширование картинок включается если запрос http
			//	VikaUtils.logToFile(url+"\n");
			boolean caching = true;
			// !startsWith(url, "file") && Settings.cacheImages;
			if (url.indexOf("camera_50") > -1 || url.indexOf("camera_100") > -1) {
				return VikaTouch.cameraImg;
			}
			if (url.indexOf("deactivated_50") > -1 || url.indexOf("deactivated_100") > -1) {
				return VikaTouch.deactivatedImg;
			}
			if (url.indexOf("php") > -1 || url.indexOf("getVideoPreview") > -1) {
				caching = false;
			}
			// System.out.println(url + " " + caching);
			String filename = null;
			if (caching) {

				filename = url;

				if (filename.indexOf("?") > -1)
					filename = filename.substring(0, filename.indexOf("?"));

				filename = replace(
						replace(replace(replace(replace(
								replace(replace(replace(replace(replace(replace(
										replace(replace(replace(replace(filename, VikaTouch.API, ""),
												"vk-api-proxy.vikamobile.ru", ""), "?ava=1", ""), ".userapi.", ""),
										"http:", ""), "https:", ""), "=", ""), "?", ""), ":80", ""), "\\", ""),
								"/", ""), ":443", ""), "_", ""), "vk.comimages", ""),
						"com", "");

				// System.out.println(filename+" ||| "+url);
				
				Image image = null;
				if (ImageStorage.has(filename)) {     
					image = ImageStorage.get(filename);
					//VikaUtils.logToFile("get image: "+filename+"\n");
				}
				
				if (image != null) {
					return image;
				}

			}

			// ByteArrayOutputStream baos = null;
			final Connection con = Connector.open(url, Connector.READ);
			if (con instanceof HttpConnection) {
				HttpConnection var2 = (HttpConnection) con;
				var2.setRequestMethod("GET");
				var2.setRequestProperty("User-Agent",
						"KateMobileAndroid/78.1 lite-500 (Android 11; SDK 30; arm64-v8a; Xiaomi POCO X3 NFC; ru)");
						//"KateMobileAndroid/51.1 lite-442 (Android 4.2.2; SDK 17; x86; LENOVO Lenovo S898t+; ru)");
				int respcode = var2.getResponseCode();
				if (respcode != 200 && respcode != 401) {
					if (var2.getHeaderField("Location") != null) {
						url = var2.getHeaderField("Location");
					} else {
						con.close();
						throw new IOException("" + respcode);
					}
				}

				con.close();
			} else if (con instanceof FileConnection) {
				caching = false;
				con.close();
				DataInputStream dis = null;
				FileConnection fcon = null;
				try {
					fcon = (FileConnection) Connector.open(url, Connector.READ);
					dis = fcon.openDataInputStream();
					//image = Image.createImage(dis);
					VikaTouch.isdownloading=0;
					VikaTouch.needstoRedraw=true;
					System.gc();
					return Image.createImage(dis);
				} finally {
					if (fcon != null)
						fcon.close();
					if (dis != null)
						dis.close();
					
				}

				/*
				 * try { int length = (int) fileconn.fileSize(); byte[] imgBytes = new
				 * byte[length];
				 * 
				 * dis.readFully(imgBytes);
				 * 
				 * try { return Image.createImage(imgBytes, 0, imgBytes.length); } catch
				 * (IllegalArgumentException e) {
				 * 
				 * } } finally { if (dis != null) { dis.close(); }
				 * 
				 * if (fileconn != null) { fileconn.close(); }
				 * 
				 * if (baos != null) { baos.close(); }
				 * 
				 * }
				 */
			}
			con.close();
			ContentConnection ccon = null;
			DataInputStream cin = null;
			try {
				ccon = (ContentConnection) Connector.open(url, Connector.READ);
				cin = (ccon).openDataInputStream();
				Image image = Image.createImage(cin);
				if (image != null && caching) {
					ImageStorage.save(filename, image);
					//VikaUtils.logToFile("save image: " + filename+ "\n");
				}
				VikaTouch.isdownloading=0;
				VikaTouch.needstoRedraw=true;
				return image;
			} finally {
				if (ccon != null)
					ccon.close();
				if (cin != null)
					cin.close();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			VikaTouch.isdownloading=2;
			VikaTouch.needstoRedraw=true;
			return VikaTouch.cameraImg;
		}
		/*
		 * try { /* int length; byte[] imgBytes; if ((length = (int)
		 * contconn.getLength()) != -1) { imgBytes = new byte[length];
		 * cin.readFully(imgBytes); } else { baos = new ByteArrayOutputStream();
		 * 
		 * int i; while ((i = cin.read()) != -1) { baos.write(i); }
		 * 
		 * imgBytes = baos.toByteArray(); baos.close(); } try { Image image =
		 * Image.createImage(imgBytes, 0, imgBytes.length); try { if(image != null &&
		 * caching) { ImageStorage.save(filename, image); } } catch (Exception e) {
		 * 
		 * } return image; } catch (IllegalArgumentException e) {
		 * 
		 * }
		 * 
		 * } finally { if (cin != null) { cin.close(); }
		 * 
		 * if (contconn != null) { contconn.close(); }
		 * 
		 * if (baos != null) { baos.close(); } }
		 * 
		 * return null;
		 */
	}

	public static String time(Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int hours = cal.get(11);
		int minutes = cal.get(12);
		String time = TextLocal.inst.formatTime(hours, minutes);
		return time;
	}

	/*
	 * public static String fullDate(Date date) {
	 * 
	 * final Calendar cal = Calendar.getInstance(); cal.setTime(date); int day =
	 * cal.get(Calendar.DAY_OF_MONTH); int month = cal.get(Calendar.MONTH); int year
	 * = cal.get(Calendar.YEAR); int hour = cal.get(Calendar.HOUR); int minutes =
	 * cal.get(Calendar.MINUTE); return TextLocal.inst.formatFullDate(day, month,
	 * year, hour, minutes); }
	 */
	public static void request(URLBuilder url) throws IOException {
		makereq(url.toString());
	}

	public static void makereq(String url) throws IOException {
		HttpConnection httpconn = null;
		VikaTouch.isdownloading=1;
		VikaTouch.needstoRedraw=true;
		Connection conn = Connector.open(url, Connector.READ);
		httpconn = (HttpConnection) conn;
		httpconn.setRequestMethod("GET");
		httpconn.setRequestProperty("User-Agent", "KateMobileAndroid/51.1 lite-442 (Android 4.2.2; SDK 17; x86; LENOVO Lenovo S898t+; ru)");
		httpconn.openInputStream();
		httpconn.close();
		VikaTouch.isdownloading=0;
		VikaTouch.needstoRedraw=true;
	}

	// функция адаптированна* из вика мобиле
	public static String strToHex(String var0) {
		char[] var4 = var0.toCharArray();
		StringBuffer var1 = new StringBuffer();

		for (int var2 = 0; var2 < var4.length; ++var2) {
			char var3 = var4[var2];
			var1.append(Integer.toHexString(var3).toUpperCase());
		}

		return var1.toString();
	}

	public static String cut(String str, int l) {
		if (str == null)
			return "".intern();
		try {
			if (str.length() < l + 2) {
				return str;
			}
			return str.substring(0, l) + "...";
		} catch (StringIndexOutOfBoundsException e) {
			return str;
		}
	}

	public static String[] searchLinks(String text) {
		if (text == null || text.length() < 2)
			return null;
		int lm = 16; // links max (больше на экран не влезет (смотря какой конечно))
		String[] la = new String[lm];
		int li = 0; // индекс в массиве
		int tl = text.length();

		String[] glinks = new String[] { "http://", "https://", "rtsp://", "ftp://", "smb://" }; // вроде всё.
																									// Ага, я
																									// слал/принимал
																									// пару раз
																									// ссылки на
																									// расшаренные
																									// папки как
																									// smb://server/folder
		try {
			// System.out.println(text);
			// System.out.println("tl "+tl);
			// Поиск внешних ссылок
			// сначала ищем их на случай сообщения
			// @id89277233 @id2323 @id4 @id5 @id6 ... [ещё 100509 @] ... @id888292,
			// http://что-тоТам
			// В беседе вики такое постоянно.
			for (int gli = 0; gli < glinks.length; gli++) {
				int ii = 0; // Indexof Index
				while (true) {
					ii = text.indexOf(glinks[gli], ii);
					// System.out.println("ii "+ii);
					if (ii == -1) {
						break;
					} else {
						int lci = ii + 6;
						while (lci < tl && text.charAt(lci) != ' ') {
							lci++;
						}
						String l = text.substring(ii, lci);
						la[li] = l;
						li++;
						if (li >= lm)
							return la;
						ii = lci;
					}
				}
			}

			// Поиск ссылок ВК
			int cc = 0; // current char
			while (cc < tl) {
				char c = text.charAt(cc);
				if (c == '@') {
					int cs = cc;
					cc++;
					while (cc < tl && text.charAt(cc) != ' ' && text.charAt(cc) != ']') {
						cc++;
					}
					String l = text.substring(cs, cc);
					la[li] = l;
					li++;
					if (li >= lm)
						return la;
				} else if (c == '[') {
					cc++;
					int cs = cc;
					while (cc < tl && text.charAt(cc) != '|') {
						cc++;
					}
					String l = text.substring(cs, cc);
					la[li] = l;
					li++;
					if (li >= lm)
						return la;
				}
				cc++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("links c "+li);
		return la;
	}

	public static MainScreen openPage(int id) {
		if (id < 0) {
			GroupPageScreen p = new GroupPageScreen(-id);
			p.load();
			return p;
		} else {
			ProfilePageScreen p = new ProfilePageScreen(id);
			p.load();
			return p;
		}
	}

	public static void openLink(String s) {
		try {
			if (s.startsWith("@")) {
				// упоминание
			} else if (s.startsWith("id")) {
				try {
					VikaTouch.setDisplay(VikaUtils.openPage(Integer.parseInt(s.substring(2))), 1);
				} catch (RuntimeException e) {
				}
			} else if (s.indexOf("rtsp://") > -1) {
				VikaTouch.openRtspLink(s);
			} else if (s.indexOf("youtube.com") > -1) {
				if (!Settings.symtube) {
					VikaTouch.appInst.platformRequest(s);
				} else {
					VikaTouch.appInst.platformRequest("http://vikamobile.ru/getl.php?url=" + URLDecoder.encode(s));
				}
			} else
				VikaTouch.appInst.platformRequest(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// unfinished
	public static boolean processVkLink(String link) {
		link = replace(replace(link, "https://", ""), "http://", "");
		link = replace(link, "m.vk.com", "vk.com");
		try {
			link = link.substring(0, link.indexOf("?"));
		} catch (Exception e) {
		}
		if (link.indexOf("vk.com/") != 0)
			return false;
		String target = link.substring(7);
		if (target.indexOf("wall") == 0) {

		}
		return true;
	}

	public static Vector filenamesVector = new Vector(1, 5);
	public static Vector filesVector = new Vector(1, 5);

	public static List selectPhoto(String var0) {
		final List list = new List("Выбрать фото", 3);
		if (true) {
			filenamesVector.removeAllElements();
			filesVector.removeAllElements();
			if (var0 == "main") {
				try {
					var0 = System.getProperty("fileconn.dir.photos");

					try {
						final String v2 = System.getProperty("fileconn.dir.memorycard");

						final String v3 = "file:///E:/";
						if (v2 != null) {
							filesVector.addElement(v2);
							list.append("Карта памяти:", null);
							if (v2.toLowerCase().indexOf("e:") < 0) {
								filesVector.addElement(v3);
								list.append("Диск E:", null);
							}
						} else {
							filesVector.addElement(v3);
							list.append("Диск E:", null);
						}
					} catch (Exception e) {

					}
				} catch (Exception e) {

					try {
						var0 = System.getProperty("fileconn.dir.memorycard");
					} catch (Exception e2) {

					}
				}
			}
			final String url = var0;
			if (fileThread != null) {
				try {
				fileThread.interrupt();
				} catch (Throwable ee) {}
			}
			fileThread = new Thread() {
				public void run() {
					try {
						FileConnection var2 = (FileConnection) Connector.open(url, Connector.READ);
						String var4;
						Enumeration var3 = var2.list("*", true);
						for (; var3.hasMoreElements() && this.isAlive(); filenamesVector.addElement(var4)) {

							var4 = (String) var3.nextElement();
							long var5;
							if ((var2 = (FileConnection) Connector.open(url + var4, Connector.READ)).isDirectory()) {
								var5 = var2.directorySize(false);
								list.append(var4 + " - " + Integer.toString((int) (var5 / 1024L)) + "кб\n", null);
								filesVector.addElement(url + var4);
								var2.close();
							} else {
								var5 = var2.fileSize();
								if (var4.endsWith("png") || var4.endsWith("tga") || var4.endsWith("jpg")
										|| var4.endsWith("jpeg") || var4.endsWith("bmp") || var4.endsWith("gif")
										|| var4.endsWith("tiff") || var4.endsWith("jfif")) {
									list.append(var4 + " - " + Integer.toString((int) (var5 / 1024L)) + "кб\n",
											IconsManager.ico[IconsManager.PHOTOS]);
								} else {
									list.append(var4 + " - " + Integer.toString((int) (var5 / 1024L)) + "кб\n",
											IconsManager.ico[IconsManager.DOCS]);
								}
								filesVector.addElement(var2.getURL());
							}
							if (!this.isAlive()) {
								break;
							}
						}

						var2.close();
					} catch (IOException var7) {
					} catch (SecurityException var8) {

					} catch (Exception var8) {
					}
				}
			};
			fileThread.start();

		}
		return list;
	}

	public static void sendPhoto(int peerId, byte[] var5, String text) throws IOException, InterruptedException {
		String var11 = VikaUtils.download(VikaTouch.API + "/method/photos.getMessagesUploadServer?access_token="
				+ VikaTouch.accessToken + "&user_id=" + VikaTouch.userId + "&v=" + VikaTouch.API_VERSION);
		//VikaUtils.logToFile(var11);
		String aString163 = var11.substring(var11.indexOf("upload_url\":\"") + 13, var11.indexOf("\",\"user_id"));

		aString163 = VikaUtils.replace(aString163, "\\/", "/");

		if (!Settings.https)
			aString163 = VikaUtils.replace(aString163, "https:", "http:");
		//VikaUtils.logToFile(aString163);
		JSONObject json;
		try {
			json = new JSONObject(upload(aString163, "photo", "bb2.jpg", var5));
		} catch (Exception e) {
			throw new IOException(e.toString());
		}
		//VikaUtils.logToFile("jsonn "+json.toString());
		String photo = json.getString("photo");
		String server = "" + json.getInt("server");
		String hash = json.getString("hash");
		String var217;

		String var17;

		String var175;

		String var10000 = var17 = VikaUtils.download(
				VikaTouch.API + "/method/photos.saveMessagesPhoto?photo=" + URLDecoder.encode(photo) + "&server="
						+ server + "&hash=" + hash + "&access_token=" + VikaTouch.accessToken + "&v=" + VikaTouch.API_VERSION);
		//VikaUtils.logToFile(var10000);
		//var217 = var10000.substring(var10000.indexOf("owner_id") + 10, var17.indexOf("has_tags") - 2);
		var217 = var10000.substring(var10000.indexOf("owner_id") + 10, var17.indexOf("access_key") - 2);
		var175 = var17.substring(var17.indexOf("\"id") + 5, var17.indexOf("owner_id") - 2);
		URLBuilder url;
		if (peerId < 2000000000L) {
			url = new URLBuilder("messages.send").addField("user_id", peerId)
					.addField("random_id", new Random().nextInt(100))
					.addField("attachment", "photo" + var217 + "_" + var175);
			if (text != null && text.length() > 0) {
				url = url.addField("text", text);
			}
		} else if (peerId < 0l) {
			peerId = -peerId;
			url = new URLBuilder("messages.send").addField("group_id", peerId)
					.addField("random_id", new Random().nextInt(100))
					.addField("attachment", "photo" + var217 + "_" + var175);
			if (text != null && text.length() > 0) {
				url = url.addField("text", text);
			}
		} else {
			peerId -= 2000000000L;
			url = new URLBuilder("messages.send").addField("chat_id", peerId)
					.addField("random_id", new Random().nextInt(100))
					.addField("attachment", "photo" + var217 + "_" + var175);
			if (text != null && text.length() > 0) {
				url = url.addField("text", text);
			}
		}
		//VikaUtils.logToFile(url.toString());
		VikaUtils.download(url);
	}

	public static void sendCameraPhoto(int peerId) throws Exception {
		String var11;
		try {
			var11 = VikaUtils.download(VikaTouch.API + "/method/photos.getMessagesUploadServer?access_token="
					+ VikaTouch.accessToken + "&user_id=" + VikaTouch.userId + "&v=" + VikaTouch.API_VERSION);
		} catch (Exception e) {
			throw new Exception("a " + e.toString());
		}
		String aString163 = var11.substring(var11.indexOf("upload_url\":\"") + 13, var11.indexOf("\",\"user_id"));

		aString163 = VikaUtils.replace(aString163, "\\/", "/");
		if (!Settings.https)
			aString163 = VikaUtils.replace(aString163, "https:", "http:");

		JSONObject json;
		try {
			json = new JSONObject(new String(uploadPhoto(aString163, "photo")));
		} catch (Exception e) {
			throw new Exception(e.toString() + " c " + aString163);
		}
		String photo = json.getString("photo");
		String server = "" + json.getInt("server");
		String hash = json.getString("hash");

		String var17;

		String var10000;

		try {
			var10000 = var17 = VikaUtils.download(
					VikaTouch.API + "/method/photos.saveMessagesPhoto?photo=" + URLDecoder.encode(photo) + "&server="
							+ server + "&hash=" + hash + "&access_token=" + VikaTouch.accessToken + "&v=" + VikaTouch.API_VERSION);
		} catch (Exception e) {
			throw new Exception("d " + e.toString());
		}
		String ownerid = var10000.substring(var10000.indexOf("owner_id") + 10, var17.indexOf("has_tags") - 2);

		String photoid = var17.substring(var17.indexOf("\"id") + 5, var17.indexOf("owner_id") - 2);
		URLBuilder url;
		if (peerId < 2000000000L) {
			url = new URLBuilder("messages.send").addField("user_id", peerId)
					.addField("random_id", new Random().nextInt(100))
					.addField("attachment", "photo" + ownerid + "_" + photoid);
		} else if (peerId < 0l) {
			peerId = -peerId;
			url = new URLBuilder("messages.send").addField("group_id", peerId)
					.addField("random_id", new Random().nextInt(100))
					.addField("attachment", "photo" + ownerid + "_" + photoid);
		} else {
			peerId -= 2000000000L;
			url = new URLBuilder("messages.send").addField("chat_id", peerId)
					.addField("random_id", new Random().nextInt(100))
					.addField("attachment", "photo" + ownerid + "_" + photoid);
		}
		try {
			VikaUtils.download(url);
		} catch (Exception e) {
			throw new Exception("x " + e.toString());
		}
	}

	public static byte[] photoData;

	private static String uploadPhoto(String var0, String var1) throws Exception {
		var0 = var0 + "&" + var1 + "=";
		String var2 = "[{\":!}]";

		for (int var3 = 0; var3 < var2.length(); ++var3) {
			char var4 = var2.charAt(var3);
			String var5 = Integer.toHexString(var4);
			if (var5.length() < 2) {
				var5 = "0" + var5;
			}

			int var6 = var0.indexOf(63) + 1;
			var0 = var0.substring(0, var6) + replace(var0.substring(var6), "" + var4, "%" + var5);
		}

		HttpConnection var24 = null;
		InputStream var25 = null;
		String var26 = "\r\n";
		String var7 = "7d73991305de";
		String var8 = "--" + var7;
		String var9 = var26 + var8 + var26 + "Content-Disposition: form-data; name=\"" + var1
				+ "\"; filename=\"img.png\"" + var26 + "Content-Type: image/png" + var26 + var26;
		String var10 = var26 + var8 + "--" + var26;
		byte[] var11 = var9.getBytes("utf-8");
		byte[] var12 = var10.getBytes("utf-8");
		byte[] var13 = new byte[photoData.length + var11.length + var12.length];
		System.arraycopy(var11, 0, var13, 0, var11.length);
		System.arraycopy(photoData, 0, var13, var11.length, photoData.length);
		System.arraycopy(var12, 0, var13, var11.length + photoData.length, var12.length);
		photoData = null;

		byte[] var23;
		try {
			var24 = (HttpConnection) Connector.open(var0, Connector.READ_WRITE);
			var24.setRequestMethod("POST");
			var24.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + var7);
			var24.setRequestProperty("Content-Length", "" + var13.length);
			OutputStream var14 = var24.openOutputStream();
			var14.write(var13);
			var14.close();
			int var15 = var24.getResponseCode();
			if (var15 != 200) {
				throw new Exception();
			}

			var25 = var24.openInputStream();
			byte[] var16 = new byte[1024];
			ByteArrayOutputStream var17 = new ByteArrayOutputStream();
			int var18 = 1;

			while (var18 > 0) {
				var18 = var25.read(var16);
				if (var18 > 0) {
					var17.write(var16, 0, var18);
				}
			}

			var23 = var17.toByteArray();
		} catch (Exception var22) {
			var23 = null;
			throw new Exception("ud " + var22.toString());
		}

		try {
			var25.close();
		} catch (Exception var21) {
			;
		}

		try {
			var24.close();
		} catch (Exception var20) {
			;
		}

		var13 = var23;
		String var27 = null;

		try {
			var27 = new String(var13, "utf-8");
		} catch (UnsupportedEncodingException var19) {
			var19.printStackTrace();
		}
		return var27;
	}

	public static String upload(String server, String field, String filename, byte[] file) throws Exception {
		server = server + "&" + field + "=";
		String var2 = "[{\":!}]";

		for (int var3 = 0; var3 < var2.length(); ++var3) {
			char var4 = var2.charAt(var3);
			String var5 = Integer.toHexString(var4);
			if (var5.length() < 2) {
				var5 = "0" + var5;
			}

			int var6 = server.indexOf(63) + 1;
			server = server.substring(0, var6) + replace(server.substring(var6), "" + var4, "%" + var5);
		}

		HttpConnection var24 = null;
		InputStream var25 = null;
		String var26 = "\r\n";
		String var7 = "7d73991305de";
		String var8 = "--" + var7;
		String var9 = var26 + var8 + var26 + "Content-Disposition: form-data; name=\"" + field
				+ "\"; filename=\""+filename+"\"" + var26 + "Content-Type: image/png" + var26 + var26;
		String var10 = var26 + var8 + "--" + var26;
		byte[] var11 = var9.getBytes("utf-8");
		byte[] var12 = var10.getBytes("utf-8");
		byte[] var13 = new byte[file.length + var11.length + var12.length];
		System.arraycopy(var11, 0, var13, 0, var11.length);
		System.arraycopy(file, 0, var13, var11.length, file.length);
		System.arraycopy(var12, 0, var13, var11.length + file.length, var12.length);

		byte[] var23;
		try {
			var24 = (HttpConnection) Connector.open(server, Connector.READ_WRITE);
			var24.setRequestMethod("POST");
			var24.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + var7);
			var24.setRequestProperty("Content-Length", "" + var13.length);
			OutputStream var14 = var24.openOutputStream();
			var14.write(var13);
			var14.close();
			int var15 = var24.getResponseCode();
			if (var15 != 200) {
				throw new Exception();
			}

			var25 = var24.openInputStream();
			byte[] var16 = new byte[1024];
			ByteArrayOutputStream var17 = new ByteArrayOutputStream();
			int var18 = 1;

			while (var18 > 0) {
				var18 = var25.read(var16);
				if (var18 > 0) {
					var17.write(var16, 0, var18);
				}
			}

			var23 = var17.toByteArray();
		} catch (Exception var22) {
			var23 = null;
			throw new Exception("ud " + var22.toString());
		}

		try {
			var25.close();
		} catch (Exception var21) {
			;
		}

		try {
			var24.close();
		} catch (Exception var20) {
			;
		}

		var13 = var23;
		String var27 = null;

		try {
			var27 = new String(var13, "utf-8");
		} catch (UnsupportedEncodingException var19) {
			var19.printStackTrace();
		}
		return var27;
	}
	public static Image saveOrLoadSmileToRMS(String smilename) {
	
		String er="0";
		RecordStore smilesRMS = null;
		try {
			smilesRMS = RecordStore.openRecordStore(smilename, true);
		} catch (Throwable e1) {
		//	VikaTouch.sendLog("errr");
			//
		}
		er="1";
		try {
			if (smilesRMS != null) {
				if (smilesRMS.getNumRecords() > 0) {
					try {
					byte[] aa = smilesRMS.enumerateRecords(null, null, true).nextRecord();
					
					Image a =  Image.createImage(aa, 0,aa.length);
					smilesRMS.closeRecordStore();
					return a;
					} catch (Throwable aaa) {
						/*VikaTouch.sendLog(aaa.toString());
						byte[] bytes = VikaUtils.downloadBytes("http://vikamobile.ru:80/emoji/"+smilename);
						VikaTouch.sendLog("ff "+String.valueOf(bytes.length));
						smilesRMS.setRecord(1, bytes, 0, bytes.length);
						smilesRMS.closeRecordStore();
						Image a =  Image.createImage(bytes, 0, bytes.length);
						return a;*/
					}
					
				} else {
					byte[] bytes = VikaUtils.downloadBytes("http://vk-api-proxy.vikamobile.ru:80/_/vk.com/emoji/e/"
							//"http://vk-api-proxy.vikamobile.ru:80/_/vk.com/images/emoji/"
						//	"http://vikamobile.ru:80/emoji/"
					+smilename);
					//VikaTouch.sendLog("fff "+String.valueOf(bytes.length));
					smilesRMS.addRecord(bytes, 0, bytes.length);
					smilesRMS.closeRecordStore();
					Image a =  Image.createImage(bytes, 0, bytes.length);
					
					//smilesRMS.closeRecordStore();
					return a;
				}		
			}	
		} catch (Throwable e) {	
			VikaTouch.sendLog(e.getMessage());
		}
		return null;
   }
	
/*	private boolean getToken() {
		try {
			tokenRMS = RecordStore.openRecordStore(TOKEN_RMS, true);
			if (tokenRMS.getNumRecords() > 0) {
				String s = new String(tokenRMS.getRecord(1), "UTF-8");
				accessToken = s.substring(0, s.indexOf(";"));

				// Вся эта хрень нужна для запуска в оффлайне
				String s2 = s.substring(s.indexOf(";") + 1, s.length());
				userId = s2.substring(0, s2.indexOf(";"));
				tokenRMS.closeRecordStore();
				// VikaTouch.sendLog("gettoken: "+accessToken);
				// оптимизация
				return true;
			}
			tokenRMS.closeRecordStore();
		} catch (Exception e) {
			VikaTouch.error(e, ErrorCodes.TOKENLOAD);
		}
		return false;
	}*/
	public static String decimal2hex(int d) {
	    String digits = "0123456789ABCDEF";
	    if (d <= 0) return "0";
	    int base = 16;   // flexible to change in any base under 16
	    String hex = "";
	    while (d > 0) {
	        int digit = d % base;              // rightmost digit
	        hex = digits.charAt(digit) + hex;  // string concatenation
	        d = d / base;
	    }
	    return hex;
	}
	
	public static final byte[] intToByteArray(int value) {
	    return new byte[] {
	            (byte)(value >>> 24),
	            (byte)(value >>> 16),
	            (byte)(value >>> 8),
	            (byte)value};
	}
	
	
	public static String bytesToHex(byte[] bytes) {
		final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	
    

public String HexOfString(String str) {
   byte[] utfString = null;
try {
	utfString = str.getBytes("UTF-8");
} catch (UnsupportedEncodingException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
   return bytesToHex(utfString);
}
	
	public static String toUTF8Array(String str) {
	    Vector utf8 = new Vector(str.length(), 1);
	    for (int i=0; i < str.length(); i++) {
	        int charcode = str.charAt(i);
	        if (charcode < 0x80) utf8.addElement(new Integer(charcode));
	        else if (charcode < 0x800) {
	        	utf8.addElement(new Integer(0xc0 | (charcode >> 6))); 
	        	utf8.addElement(new Integer( 0x80 | (charcode & 0x3f)));
	        }
	        else if (charcode < 0xd800 || charcode >= 0xe000) {
	        	utf8.addElement(new Integer(0xe0 | (charcode >> 12))); 
	        	utf8.addElement(new Integer(0x80 | ((charcode>>6) & 0x3f))); 
	        	utf8.addElement(new Integer(0x80 | (charcode & 0x3f)));
	        }
	        // surrogate pair
	        else {
	            i++;
	            // UTF-16 encodes 0x10000-0x10FFFF by
	            // subtracting 0x10000 and splitting the
	            // 20 bits of 0x0-0xFFFFF into two halves
	            charcode = (char) (0x10000 + (((charcode & 0x3ff)<<10)
	                      | (str.charAt(i) & 0x3ff)));
	            utf8.addElement(new Integer(0xf0 | (charcode >>18))); 
	            utf8.addElement(new Integer(0x80 | ((charcode>>12) & 0x3f))); 
	            utf8.addElement( new Integer(      0x80 | ((charcode>>6) & 0x3f))); 
	            utf8.addElement( new Integer(          0x80 | (charcode & 0x3f)));
	        }
	    }
	    //String[] array = utf8.toArray(new String[utf8.size()]);
	    
	    //Displaying Array Elements
	    System.out.println("String Array Elements :");
	    String a = "";
	    for(int i=0; i < utf8.capacity(); i++){
	       a+=utf8.elementAt(i);
	    }
		return a;
	}
	
	
	
	public static Image loadSmile(String smilePath) {
		String err="0";
		String tpath = (System.getProperty("fileconn.dir.private"));
		Image im = null;
		if(VikaTouch.smilestable.containsKey(smilePath)) {
			return (Image)VikaTouch.smilestable.get(smilePath);
		} else {
		if (true)
		//(VikaTouch.isSymbian93orS40()) 
		{
			err="1";
			try {
				err="2";
			im = saveOrLoadSmileToRMS(smilePath);
			err="3";
			if (im==null) {
				err="nulll";
			}
		VikaTouch.smilestable.put(smilePath, im);
		err="4";
		return im;
			} catch (Throwable ee) {
				//VikaTouch.sendLog(err);
				try {
					return Image.createImage("/emoji/D83DDE00.png");	
				} catch (IOException e1) {
					//VikaTouch.sendLog(err);
				}	
			}
		} else {
			try {
			 im = Image.createImage(tpath+"emoji/"+smilePath);
			 VikaTouch.smilestable.put(smilePath, im);
			 return im;
		} catch (Throwable e) {
			try {
				byte[] bytes = VikaUtils.downloadBytes("http://vk-api-proxy.vikamobile.ru:80/_/vk.com/emoji/e/"
						//"http://vk-api-proxy.vikamobile.ru:80/_/vk.com/images/emoji/"
				//		"http://vikamobile.ru:80/emoji/"
				+smilePath);
				FileConnection outConn = (FileConnection) Connector.open(tpath+smilePath, Connector.READ_WRITE);
				if (!(outConn.exists())) {
					outConn.create();
				}
					OutputStream outStream = outConn.openOutputStream();
					outStream.write(bytes);
					outStream.flush();
					outStream.close();
				     outConn.close();
				im = Image.createImage(bytes, 0, bytes.length);
			VikaTouch.smilestable.put(smilePath, im);
			return im;
			} catch (Throwable ee) {
				//VikaTouch.sendLog("end" + ee.getMessage());
				try {
					return Image.createImage("/emoji/D83DDE00.png");
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					//VikaTouch.sendLog(e1.getMessage());
				}
			}
		}
		}
		
		}
		return im;
		
	}
}