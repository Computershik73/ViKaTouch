package vikatouch.music;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.popup.ConfirmBox;
import ru.nnproject.vikaui.popup.ContextMenu;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.items.menu.OptionItem;
import vikatouch.items.music.AudioTrackItem;
import vikatouch.screens.MainScreen;
import vikatouch.screens.music.MusicScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.url.URLDecoder;

// экранчик с названием песни, перемоткой ии... Всё.
public class MusicPlayer extends MainScreen
	implements IMenu, PlayerListener
{
	
	public MusicScreen playlist;
	public int current;
	public boolean isPlaying = false;
	public boolean isReady = false;
	public boolean stop = false;
	public boolean loop = false;
	public boolean random = false;
	public boolean inSeekMode = false;
	public long seekTime;
	
	// кэш для рисования
	public String title = "Track name";
	private String artist = "track artist";
	private int titleW, artistW;
	private int currTx, currAx;
	private String totalNumber;
	private String time = "00:00";
	private String totalTime = "99:59";
	public static final int PBMARGIN = 60;
	private int x1 = PBMARGIN, x2 = 360 - PBMARGIN, currX = 100;
	public Image[] buttons;
	private Image coverOrig;
	private Image resizedCover;
	private int lastW;
	
	public static MusicPlayer inst;
	public Player player;
	public Manager man;
	public InputStream input;
	public OutputStream output;
	
	public MusicPlayer()
	{
		try
		{
			Image sheet = Image.createImage("/playerbtns.png");
			buttons = new Image[7];
			for(int i = 0; i < 7; i++)
			{
				buttons[i] = Image.createImage(sheet, i*50, 0, 50, 50, 0);
			}
		}
		catch (Exception e)
		{
			
		}
	}
	
	public void destroy()
	{
		try
		{
			player.stop();
		}
		catch (Exception e) { }
		try {
			closePlayer();
		} catch (MediaException e) {
			e.printStackTrace();
		}
	}
	
	
	public void firstLoad()
	{
		loadTrack();
	}
	
	
	public void loadTrack()
	{
		System.out.println("TRACK LOAD");
		try
		{
			player.stop();
		}
		catch (Exception e) { }
		isPlaying = true;
		isReady = false;
		stop = false;
		loadTrackInfo();
		time = "";
		totalTime = "";
		//case Settings.AUDIO_LOADANDPLAY
		//TODO methods
		inst = this;
		byte[] aByteArray207 = null;
		System.gc();
		
		//TODO move temp constants
		boolean CACHETOPRIVATE = false;
		
		try {
			try {
				closePlayer();
			} catch (Exception var20) {
				VikaTouch.popup(new InfoPopup("Player closing error", null));
			}
			String url = getC().mp3;
			final String path = (CACHETOPRIVATE ? System.getProperty("fileconn.dir.private") : System.getProperty("fileconn.dir.music")) + "vikaMusicCache.mp3";
			
			if(Settings.audioMode == Settings.AUDIO_PLAYONLINE)
			{
				new Thread()
				{
					public void run()
					{
						try
						{
							time = "00:00";
							totalTime = "--:--";
							player = Manager.createPlayer(getC().mp3);
							getCover();
							resizeCover();
							player.start();
							isReady = true;
							isPlaying = true;
							stop = false;
							try
							{
								((VolumeControl) player.getControl("VolumeControl")).setLevel(100);
							}
							catch (Exception e) { }
							totalTime = time(getC().length);
							player.addPlayerListener(inst);
						}
						catch(Exception e)
						{
							VikaTouch.popup(new InfoPopup(e.toString(), null, "Player error", null));
						}
					}
				}.start();
			}
			//else if (url.indexOf("bb2.mp3")<0) 
			else if(Settings.audioMode != Settings.AUDIO_CACHEANDPLAY)
			{
				
				new Thread()
				{
					public void run()
					{
						try
						{
							ContentConnection contCon = (ContentConnection) Connector.open(getC().mp3);
							DataInputStream dis = contCon.openDataInputStream();
							
	
							FileConnection trackFile = (FileConnection) Connector.open(path);
				
							if (trackFile.exists()) {
								trackFile.delete();
							}
							trackFile.create();
							output = trackFile.openOutputStream();
					
							int trackSize = (int) contCon.getLength();
							totalTime = (trackSize/1024/1024)+"."+(trackSize/1024%103)+"MB";
							byte[] cacheBuffer;
							
							int trackSegSize = (int) trackSize/100;
							int i=0;
							
							if ((int) contCon.getLength()!= -1) {
								while ((contCon.getLength()/100)*(i+1)<contCon.getLength()) {
									cacheBuffer = new byte[trackSegSize];
									time = i+"%";
									dis.read(cacheBuffer);
									output.write(cacheBuffer);
									i++;
									if(stop)
									{
										dis.close();
										contCon.close();
										output.close();
										return;
									}
								}
								cacheBuffer = new byte[(int) (contCon.getLength()-contCon.getLength()/100*(i))];
								time = "99,9%";
								dis.read(cacheBuffer);
								output.write(cacheBuffer);
								output.flush();
							}
							
							if (dis != null) {
								dis.close();
							}
							if (contCon != null) {
								contCon.close();
							}
							if (output!=null) {
								output.close();
							}
						
							/*if ((VikaTouch.mobilePlatform.indexOf("S60") > 0
									&& (VikaTouch.mobilePlatform.indexOf("5.5") > 0 || VikaTouch.mobilePlatform.indexOf("5.4") > 0
											|| VikaTouch.mobilePlatform.indexOf("5.3") > 0 || VikaTouch.mobilePlatform.indexOf("5.2") > 0
											|| VikaTouch.mobilePlatform.indexOf("5.1") > 0 || VikaTouch.mobilePlatform.indexOf("5.1") > 0
											|| VikaTouch.mobilePlatform.indexOf("5.0") > 0 || VikaTouch.mobilePlatform.indexOf("S8600") > 0)
									 ) || VikaTouch.mobilePlatform.indexOf("Sony") > 0)
							{*/
							if(Settings.audioMode == Settings.AUDIO_LOADANDPLAY)
							{
								getCover();
								resizeCover();
								
								try {
									player = Manager.createPlayer(path);
									player.addPlayerListener(inst);
								} catch (Exception e) {
									VikaTouch.popup(new InfoPopup("Player creating error", null)); //TODO errcodes
									e.printStackTrace();
									return;
								}
								time = "100%";
								try {
									player.realize();
								} catch (MediaException e) {
									VikaTouch.popup(new InfoPopup("Player realizing error", null));
									e.printStackTrace();
									return;
								}
								try {
									player.prefetch();
								} catch (MediaException e) {
									VikaTouch.popup(new InfoPopup("Player prefetching error", null));
									e.printStackTrace();
									return;
								}
								try {
									player.start();
									((VolumeControl) player.getControl("VolumeControl")).setLevel(100);
								} catch (MediaException e) {
									VikaTouch.popup(new InfoPopup("Player running error", null));
									e.printStackTrace();
									return;
								}
								time = "0:00";
								totalTime = time(player.getDuration()/1000000L);
							} else if(Settings.audioMode == Settings.AUDIO_LOADANDSYSTEMPLAY) {
								VikaTouch.callSystemPlayer(path);
								// ну вдруг вот то что ниже хер знает на каком ублюдстве не заработает?))
							} else {
								try {
									VikaTouch.appInst.platformRequest(path);
								} catch (ConnectionNotFoundException e) {
									e.printStackTrace();
								}
							}
							isReady = true;
							isPlaying = true;
							stop = false;
						}
						catch(Exception e)
						{
							e.printStackTrace();
							VikaTouch.popup(new InfoPopup("Common player error", null));
						}
						System.gc();
					}
				}.start();
			} 
			else 
			{
				time = "...";
				totalTime = "--:--";
				getCover();
				resizeCover();
				ContentConnection contCon = null;
				DataInputStream dis = null;
				try {
					contCon = (ContentConnection) Connector.open(url);
					dis = contCon.openDataInputStream();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					int var5 = (int) contCon.getLength();
					System.out.println("var5: "+var5);
					if (var5 != -1) {
						aByteArray207 = new byte[var5];
						dis.read(aByteArray207);
					}
				} finally {
					if (dis != null) {
						dis.close();
					}
					if (contCon != null) {
						contCon.close();
					}
				}
				ByteArrayInputStream aByteArrayInputStream212 = new ByteArrayInputStream(aByteArray207);
				System.gc();
				input = aByteArrayInputStream212;
				player = Manager.createPlayer(input, "audio/mpeg");
				try
				{
					((VolumeControl) player.getControl("VolumeControl")).setLevel(100);
				}
				catch (Exception e) { }
				player.start();
				totalTime = time(player.getDuration()/1000000L);
				isReady = true;
				isPlaying = true;
				stop = false;
			}

			//System.gc();
		} catch (Exception e) {
			VikaTouch.popup(new InfoPopup(e.toString(), null, "Player error", null));
		}
	}

	private void closePlayer() throws MediaException {
		if (player != null) {
			if (player.getState() == 400) {
				player.stop();
			}

			if (player.getState() == 300) {
				player.deallocate();
			}

			if (player.getState() == 200 || player.getState() == 100) {
				player.close();
			}
		}

		player = null;
		if(input != null)
		{
			try 
			{
				input.close();
			} 
			catch (IOException e) { }
			input = null;
		}
		System.gc();
	}
	
	public void pause()
	{
		System.out.println("PAUSE");
		try
		{
			if(isReady)
			{
				player.stop();
				isPlaying = false;
			}
			else
			{
				VikaTouch.popup(new ConfirmBox("Отменить загрузку?",null,new Runnable()
						{
							public void run() {
								if(!isReady) { stop = true; }
							}
						}, null));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void play()
	{
		try
		{
			if(isReady)
			{
				if(inSeekMode)
				{
					checkSeekTime();
					inSeekMode = false;
					player.setMediaTime(seekTime);
				}
				player.start();
				isPlaying = true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void checkSeekTime()
	{
		if(seekTime<1L) seekTime = 1L;
		if(seekTime>(getC().length-5)*1000000L)
		{
			seekTime = (getC().length-5)*1000000L;
		}
	}
	
	public void next()
	{
		if(inSeekMode)
		{
			seekTime += 5000000L;
			checkSeekTime();
		}
		else
		{
			if(random)
			{
				Random r = new Random();
				current = r.nextInt(playlist.uiItems.length);
			}
			else
			{
				current++;
				if(current>=playlist.uiItems.length) current = 0;
			}
			loadTrack();
		}
	}
	
	public void prev()
	{
		if(inSeekMode)
		{
			seekTime -= 5000000L;
		}
		else
		{
			current--;
			if(current<0) current = playlist.uiItems.length - 1;
			loadTrack();
		}
	}
	
	public void updateDrawData()
	{
		if(!isReady) return;
		time = time(player.getMediaTime()/1000000L);
		long dur = Settings.audioMode == Settings.AUDIO_PLAYONLINE ? getC().length*1000000L : player.getDuration();
		try
		{
			int dw = DisplayUtils.width;
			if(dw > DisplayUtils.height)
			{
				// альбом
				x1 = dw/2+PBMARGIN;
				x2 = dw-PBMARGIN;
				currX = dw/2 + 60 + (int)((dw/2-PBMARGIN*2)*(inSeekMode?seekTime:player.getMediaTime())/dur);
			}
			else
			{
				// квадрат, портрет
				x1 = PBMARGIN;
				x2 = dw-PBMARGIN;
				currX = PBMARGIN + (int)((dw-PBMARGIN*2)*(inSeekMode?seekTime:player.getMediaTime())/dur);
			}
		}
		catch (Exception e) { }
	}
	
	public void loadTrackInfo()
	{
		title = getC().name;
		artist = getC().artist;
		Font f = Font.getFont(0, 0, Font.SIZE_MEDIUM);
		titleW = f.stringWidth(title);
		artistW = f.stringWidth(artist);
	}
	
	public void onRotate()
	{
		resizeCover();
	}
	
	public void resizeCover()
	{
		if(coverOrig==null) 
		{
			resizedCover = null;
			return;
		}
		int dw = DisplayUtils.width;
		if(dw > DisplayUtils.height)
		{
			// альбом
			resizedCover = VikaUtils.resize(coverOrig, dw/2, dw/2);
		}
		else
		{
			// квадрат, портрет
			resizedCover = VikaUtils.resize(coverOrig, dw, dw);
		}
	}
	
	public void getCover()
	{
		if (title != null) {
			String s = VikaUtils.download("http://vikamobile.ru:80/proxy.php?https://itunes.apple.com/search?term="
					+ URLDecoder.encode(title + " " + (artist==null?"":artist)) + "&country=ru&limit=1");

			try {
				JSONObject res;
				if (!(res = new JSONObject(s)).getJSONArray("results").getJSONObject(0).isNull("artworkUrl100")) {
					s = VikaUtils.replace(res.getJSONArray("results").getJSONObject(0).getString("artworkUrl100"), "\\", "");
					if(!DisplayUtils.compact)
					{
						s = VikaUtils.replace(s, "100x100bb", "600x600bb");
					}
					s = VikaUtils.replace(s, "https", "http://vikamobile.ru:80/proxy.php?https");
					coverOrig = VikaUtils.downloadImage(s);
				}
				if(coverOrig==null)
				{
					if(playlist.coverUrl!=null) coverOrig = VikaUtils.downloadImage(playlist.coverUrl);
				}
				
			} catch (Exception var23) {
				if(playlist.coverUrl!=null)
					try {
						coverOrig = VikaUtils.downloadImage(playlist.coverUrl);
					} catch (IOException e) {
						//VikaTouch.sendLog(e.toString());
					}
			}
		}
		else
		{
			if(playlist.coverUrl!=null)
				try {
					coverOrig = VikaUtils.downloadImage(playlist.coverUrl);
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public String time(long t)
	{
		int s = (int) (t%60);
		long min = t/60;
		return min+":"+(s<10?"0":"")+s;
	}
	
	public void options()
	{
		OptionItem[] opts = new OptionItem[]
		{
			new OptionItem(this,"Плейлист",IconsManager.MENU,-1,40),
			new OptionItem(this,"Повторять",loop?IconsManager.APPLY:IconsManager.REFRESH,0,40),
			new OptionItem(this,"Случайно",random?IconsManager.APPLY:IconsManager.PLAY,1,40),
			new OptionItem(this,"Перемотка",IconsManager.SEARCH,2,40),
			new OptionItem(this,"Играть сначала",IconsManager.BACK,3,40),
			new OptionItem(this,"Скачать",IconsManager.DOWNLOAD,4,40),
			new OptionItem(this,"Проблемы с воспроизведением?",IconsManager.INFO,5,40),
			new OptionItem(this,"Свернуть приложение",IconsManager.CLOSE,6,40),
		};
		VikaTouch.popup(new ContextMenu(opts));
	}
	
	public AudioTrackItem getC()
	{
		return ((AudioTrackItem) playlist.uiItems[current]);
	}
	
	public static void launch(MusicScreen list, int track)
	{
		try 
		{
			String mrl = ((AudioTrackItem) list.uiItems[track]).mp3;
			switch(Settings.audioMode)
			{
			case Settings.AUDIO_VLC:
				//String mrl = ((AudioTrackItem) list.uiItems[track]).mp3;
				System.out.println(mrl);
				if(mrl.indexOf("xtrafrancyz")!=-1)
				{
					mrl = "https://"+mrl.substring("http://vk-api-proxy.xtrafrancyz.net/_/".length());
				}
				String cmd = "vlc.exe \""+mrl+"\"";
				boolean res = VikaTouch.appInst.platformRequest(cmd);
				if(!res)
				{
					VikaTouch.popup(new InfoPopup("Эмулятор понял запрос, но отказался выполнить. Проверьте настройки системы.", null));
				}
				System.out.println(cmd);
				break;
			case Settings.AUDIO_DOWNLOAD:
				VikaTouch.appInst.platformRequest(mrl);
				break;
			case Settings.AUDIO_SYSTEMPLAYER:
				VikaTouch.callSystemPlayer(mrl);
				break;
			default:
				MusicPlayer mp = new MusicPlayer();
				mp.playlist = list;
				mp.current = track;
				mp.firstLoad();
				VikaTouch.setDisplay(mp, 1);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void draw(Graphics g) {
		updateDrawData(); //TODO: run in different thread, repeat every 1 sec.
		int dw = DisplayUtils.width;
		int dh = DisplayUtils.height;
		int hdw = dw/2;
		int textAnchor;
		int timeY;
		if(dw!=lastW)
		{
			onRotate();
			lastW = dw;
		}
		Font f = Font.getFont(0, 0, Font.SIZE_MEDIUM);
		g.setFont(f);
		g.setGrayScale(0);
		currTx-=2;
		currAx-=2;
		if(-currTx>(titleW/2+hdw)) currTx = titleW/2+hdw;
		if(-currAx>(artistW/2+hdw)) currAx = artistW/2+hdw;
		boolean tick = (System.currentTimeMillis()%1000)<500;
		if(dw>dh)
		{
			// альбом
			textAnchor = dw * 3 / 4;
			timeY = dh-70;
			if(!inSeekMode) g.drawImage(buttons[5], textAnchor-125, dh-50, 0);
			if(!inSeekMode || tick) g.drawImage(buttons[0], textAnchor-75, dh-50, 0);
			g.drawImage(buttons[isPlaying?3:1], textAnchor-25, dh-50, 0);
			if(!inSeekMode || tick) g.drawImage(buttons[2], textAnchor+25, dh-50, 0);
			if(!inSeekMode) g.drawImage(buttons[(backScreenIsPlaylist()?4:6)], textAnchor+75, dh-50, 0);
			
			g.drawString(artist, textAnchor+(artistW>hdw?currAx:0), dh/2-f.getHeight(), Graphics.HCENTER | Graphics.TOP);
			g.drawString(title, textAnchor+(titleW>hdw?currTx:0), dh/2, Graphics.HCENTER | Graphics.TOP);
		}
		else
		{
			// портрет, квадрат
			textAnchor = hdw;
			timeY = dh-70;
			if(!inSeekMode) g.drawImage(buttons[5], hdw-125, dh-50, 0);
			if(!inSeekMode || tick) g.drawImage(buttons[0], hdw-75, dh-50, 0);
			g.drawImage(buttons[isPlaying?3:1], hdw-25, dh-50, 0);
			if(!inSeekMode || tick) g.drawImage(buttons[2], hdw+25, dh-50, 0);
			if(!inSeekMode) g.drawImage(buttons[(backScreenIsPlaylist()?4:6)], hdw+75, dh-50, 0);
			
			g.drawString(artist, textAnchor+(artistW>dw?currAx:0), timeY-f.getHeight()*3/2, Graphics.HCENTER | Graphics.TOP);
			g.drawString(title, textAnchor+(titleW>dw?currTx:0), timeY-f.getHeight()*5/2, Graphics.HCENTER | Graphics.TOP);
		}
		ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
		g.drawRect(x1, timeY, x2-x1, 10);
		if(isReady) g.fillRect(x1+2, timeY+2, currX-x1-4, 6);
		
		g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
		g.drawString(time, x1-4, timeY-4, Graphics.TOP | Graphics.RIGHT);
		g.drawString(totalTime, x2+4, timeY-4, Graphics.TOP | Graphics.LEFT);
		
		// cover
		int coverY = (dw>dh)?((dh-hdw)/2):0;
		if(resizedCover!=null) 
		{
			g.drawImage(resizedCover, 0, coverY, 0);
		}
		else
		{
			int s = (dw>dh)?hdw:dw;
			g.setGrayScale(200);
			g.fillRect(0, coverY, s, s);
		}
	}
	
	public void release(int x, int y)
	{
		int dw = DisplayUtils.width;
		int dh = DisplayUtils.height;
		int hdw = dw/2;
		if(y<dh-50) return;
		int anchor;
		if(dw>dh)
		{
			// альбом
			anchor = dw * 3 / 4;
		}
		else
		{
			// портрет, квадрат
			anchor = hdw;
		}
		if(x>anchor+125)
		{
			return;
		}
		else if(x>anchor+75)
		{
			if(!inSeekMode) VikaTouch.inst.cmdsInst.command(14, this);
		}
		else if(x>anchor+25)
		{
			next();
		}
		else if(x>anchor-25)
		{
			if(isPlaying)
			{
				pause();
			}
			else
			{
				play();
			}
		}
		else if(x>anchor-75)
		{
			prev();
		}
		else if(x>anchor-125)
		{
			if(!inSeekMode) options();
		}
		
	}
	
	public void press(int key)
	{
		if(key == -6)
		{
			if(!inSeekMode) options();
		}
		else if(key == -3)
		{
			prev();
		}
		else if(key == -5)
		{
			if(isPlaying)
			{
				pause();
			}
			else
			{
				play();
			}
		}
		else if(key == -4)
		{
			next();
		}
		else if(key == -7 /*|| key == */) //TODO вспомнить кнопку "назад" на SE
		{
			if(!inSeekMode) VikaTouch.inst.cmdsInst.command(14, this);
		}
	}
	
	public void drawHUD(Graphics g) 
	{
		
	}
	
	public boolean backScreenIsPlaylist()
	{
		return backScreen == playlist;
	}

	public void onMenuItemPress(int i) {
		if(i==0)
		{
			loop = !loop;
		}
		else if(i==1)
		{
			random = !random;
		}
		else if(i==2)
		{
			if(isReady)
			{
				seekTime = player.getMediaTime();
				if(isPlaying) pause();
				inSeekMode = true;
			}
		}
		else if(i==3)
		{
			try {
				player.stop();
				Thread.sleep(500);
			} catch (Exception e) { }
			try {
				player.setMediaTime(1);
				player.start();
			} catch (MediaException e) { }
		}
		else if(i==4)
		{
			try {
				VikaTouch.appInst.platformRequest(getC().mp3);
			} catch (ConnectionNotFoundException e) {
				e.printStackTrace();
			}
		}
		else if(i==5)
		{
			// написать что мол выберите другой метод или купите N8
		}
		else if(i==6)
		{
			VikaTouch.appInst.notifyPaused();
		}
		else if(i==-1)
		{
			if(backScreenIsPlaylist())
			{
				VikaTouch.inst.cmdsInst.command(14, this);
			}
			else
				VikaTouch.setDisplay(playlist, 1);
		}
	}


	public void onMenuItemOption(int i) {
	}


	public void playerUpdate(Player pl, String event, Object data) {
		if(event == END_OF_MEDIA)
		{
			if(loop)
			{
				try {
					player.setMediaTime(1);
					player.start();
				} catch (MediaException e) {
					e.printStackTrace();
				}
			}
			else
			{
				next();
			}
		}
		else if(event == ERROR)
		{
			String err = data.toString();
			if(err.indexOf("-3") != -1) return;
			if(err.indexOf("-2") != -1) err = "General internal error (-2)";
			VikaTouch.popup(new InfoPopup(err, null, "Player error", null));
		}
	}
}
