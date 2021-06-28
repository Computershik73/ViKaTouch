package vikatouch.screens.music;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import ru.nnproject.vikaui.menu.EmptyMenu;
import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.menu.items.OptionItem;
import ru.nnproject.vikaui.menu.items.PressableUIItem;
import ru.nnproject.vikaui.popup.ContextMenu;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.popup.VikaNotice;
import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.screen.VikaScreen;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.attachments.AudioAttachment;
import vikatouch.items.music.AudioTrackItem;
import vikatouch.locale.TextLocal;
import vikatouch.music.MusicPlayer;
import vikatouch.screens.MainScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.text.TextEditor;
import vikatouch.utils.url.URLBuilder;
import vikatouch.utils.url.URLDecoder;
/**
 * @author Feodor0090
 * 
 */
public class MusicScreen extends MainScreen {

	public int ownerId;
	public int albumId;
	public String coverUrl = null;
	public Image cover = null;

	public String title;
	private String loadingStr;

	public boolean playAfter;
public static String q;
	public static Thread downloaderThread;
	public static Thread thread;
	public MusicScreen() {
		super();
		loadingStr = TextLocal.inst.get("title.loading");
	}

	public void loadAtt(AudioAttachment aa) {
		itemsCount = 1;
		scrolled = 0;
		this.albumId = 0;
		ownerId = 0;
		this.title = TextLocal.inst.get("attachment");
		hasBackButton = true;
		uiItems = new PressableUIItem[1];
		AudioTrackItem ati = new AudioTrackItem(null, this, 0);
		ati.name = aa.name;
		ati.artist = "";
		ati.id = 0;
		ati.owner_id = 0;
		ati.length = aa.size;
		ati.lengthS = (ati.length / 60) + ":" + (ati.length % 60 < 10 ? "0" : "") + (ati.length % 60);
		ati.mp3 = aa.musUrl;
		uiItems[0] = ati;
		VikaTouch.loading = false;
		System.gc();
	}

	public void load(final int oid, final int albumId, String title) {
		scrolled = 0;
		uiItems = null;
		this.albumId = albumId;
		ownerId = oid;
		this.title = title;
		hasBackButton = true;

		if (downloaderThread != null && downloaderThread.isAlive())
			downloaderThread.interrupt();

		downloaderThread = new Thread() {
			public void run() {
				try {
					VikaTouch.loading = true;
				//	String x = VikaUtils.music(new URLBuilder(Settings.proxyApi, "audio.get", true).addField("owner_id", oid)
					//		.addField("album_id", albumId).addField("count", VikaTouch.muscount).addField("offset", 0).toString());
					String x = VikaUtils.music(new URLBuilder("audio.get").addField("owner_id", oid)
							.addField("album_id", albumId).addField("count", VikaTouch.muscount).addField("offset", 0).toString());
					// VikaTouch.sendLog(x);
					if (x.indexOf("error") > -1) {
						VikaTouch.error(ErrorCodes.MUSICLISTLOAD, x, false);
						return;
					}
					try {
						System.out.println(x);
						VikaTouch.loading = true;
						JSONObject response = new JSONObject(x).getJSONObject("response");
						JSONArray items = response.getJSONArray("items");
						itemsCount = (short) items.length();
						uiItems = new PressableUIItem[itemsCount];
						for (int i = 0; i < itemsCount; i++) {
							JSONObject item = items.getJSONObject(i);
							uiItems[i] = new AudioTrackItem(item, MusicScreen.this, i);
							((AudioTrackItem) uiItems[i]).parseJSON();
							Thread.sleep(15);
							// должно не зависать
						}
					} catch (JSONException e) {
						e.printStackTrace();
						VikaTouch.error(e, ErrorCodes.MUSICLISTPARSE);
					}
					VikaTouch.loading = false;
				} catch (InterruptedException e1) {
					return;
				} catch (Exception e) {
					e.printStackTrace();
					VikaTouch.error(e, ErrorCodes.MUSICLISTLOAD);
					VikaTouch.popup(new InfoPopup(
							"Token error. Try to restart the application and your network connection.", null));
				}
				VikaTouch.loading = false;
				System.gc();
				if (playAfter) {
					if (MusicPlayer.inst != null) {
						MusicPlayer.inst.controlsBlocked = false;
						MusicPlayer.inst.loadTrack();
					}
				}
			}
		};
		downloaderThread.start();
	}
	
	public void load(final String q) {
	    scrolled = 0;
	    uiItems = null;
	    this.title = TextLocal.inst.get("music.searchresult");
	    this.hasBackButton = true;
	    if (downloaderThread != null && downloaderThread.isAlive())
	      downloaderThread.interrupt(); 
	    downloaderThread = new Thread() {
	       
	        
	        
	        
	        public void run() {
	          try {
	            VikaTouch.loading = true;
	          //  VikaTouch.inst.refreshToken();;
	           // VikaTouch.inst.saveToken();
	           
	            String x = VikaUtils.music((new URLBuilder(Settings.httpsApi, "audio.search", true)).addField("q", q).addField("count", 50).addField("offset", 0).toString());
	            if (x.indexOf("error") > -1) {
	              VikaTouch.error(49, x, false);
	              return;
	            } 
	            try {
	              System.out.println(x);
	              VikaTouch.loading = true;
	              JSONObject response = (new JSONObject(x)).getJSONObject("response");
	              JSONArray items = response.getJSONArray("items");
	              itemsCount = (short)items.length();
	              uiItems = new PressableUIItem[itemsCount];
	              for (int i = 0; i < itemsCount; i++) {
	                JSONObject item = items.getJSONObject(i);
	                uiItems[i] = (PressableUIItem)new AudioTrackItem(item, MusicScreen.this, i);
	                ((AudioTrackItem)uiItems[i]).parseJSON();
	                Thread.sleep(15L);
	              } 
	            } catch (JSONException e) {
	              e.printStackTrace();
	              VikaTouch.error((Throwable)e, 48);
	            } 
	            VikaTouch.loading = false;
	          } catch (InterruptedException e1) {
	            return;
	          } catch (Exception e) {
	            e.printStackTrace();
	            VikaTouch.error(e, 49);
	          } 
	          VikaTouch.loading = false;
	          System.gc();
	          if (playAfter && 
	            MusicPlayer.inst != null) {
	            MusicPlayer.inst.controlsBlocked = false;
	            MusicPlayer.inst.loadTrack();
	          } 
	        }
	      };
	    downloaderThread.start();
	  }
	
	
	public void loadRecommendations(final String audio_id) {
	    scrolled = 0;
	    uiItems = null;
	    this.title = TextLocal.inst.get("music.recommendations");
	    this.hasBackButton = true;
	    if (downloaderThread != null && downloaderThread.isAlive())
	      downloaderThread.interrupt(); 
	    downloaderThread = new Thread() {
	       
	        
	        
	        
	        public void run() {
	          try {
	            VikaTouch.loading = true;
	          //  VikaTouch.inst.refreshToken();;
	           // VikaTouch.inst.saveToken();
	            String x= "";
	           if (audio_id.equals("")) {
	             x = VikaUtils.music((new URLBuilder(Settings.httpsApi, "audio.getRecommendations", true)).addField("count", 50).addField("offset", 0).toString());
	           } else {
	        	    x = VikaUtils.music((new URLBuilder(Settings.httpsApi, "audio.getRecommendations", true)).addField("count", 50).addField("offset", 0).addField("targetAudio", audio_id).toString());
	           }
	            if (x.indexOf("error") > -1) {
	              VikaTouch.error(49, x, false);
	              return;
	            } 
	            try {
	              System.out.println(x);
	              VikaTouch.loading = true;
	              JSONObject response = (new JSONObject(x)).getJSONObject("response");
	              JSONArray items = response.getJSONArray("items");
	              itemsCount = (short)items.length();
	              uiItems = new PressableUIItem[itemsCount];
	              for (int i = 0; i < itemsCount; i++) {
	                JSONObject item = items.getJSONObject(i);
	                uiItems[i] = (PressableUIItem)new AudioTrackItem(item, MusicScreen.this, i);
	                ((AudioTrackItem)uiItems[i]).parseJSON();
	                Thread.sleep(15L);
	              } 
	            } catch (JSONException e) {
	              e.printStackTrace();
	              VikaTouch.error((Throwable)e, 48);
	            } 
	            VikaTouch.loading = false;
	          } catch (InterruptedException e1) {
	            return;
	          } catch (Exception e) {
	            e.printStackTrace();
	            VikaTouch.error(e, 49);
	          } 
	          VikaTouch.loading = false;
	          System.gc();
	          if (playAfter && 
	            MusicPlayer.inst != null) {
	            MusicPlayer.inst.controlsBlocked = false;
	            MusicPlayer.inst.loadTrack();
	          } 
	        }
	      };
	    downloaderThread.start();
	  }
	
	

	public void reload(boolean playAfter) {
		if (ownerId != 0) {
			if (MusicPlayer.inst != null) {
				MusicPlayer.inst.controlsBlocked = true;
			}
			this.playAfter = playAfter;
			load(ownerId, albumId, title);
		} else {
			VikaTouch.popup(new InfoPopup("Playlist's owner ID isn't defined, reloading failed.", null));
		}
	}

	public void draw(Graphics g) {
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.setFont(Font.getFont(0, 0, 8));
		try {
			update(g);
			int y = topPanelH;
			try {
				if (uiItems != null) {
					for (int i = 0; i < itemsCount; i++) {
						if (uiItems[i] != null) {
							if (y + scrolled > DisplayUtils.height)
								break;
							uiItems[i].paint(g, y, scrolled);
							y += uiItems[i].getDrawHeight();
						}
						itemsh = y + 60;
					}
				}
			} catch (Exception e) {
				VikaTouch.error(e, ErrorCodes.MUSICITEMDRAW);
				e.printStackTrace();
			}
			g.translate(0, -g.getTranslateY());
		} catch (Exception e) {
			VikaTouch.error(e, ErrorCodes.MUSICDRAW);
			e.printStackTrace();
		}
	}

	public final void drawHUD(Graphics g) {
		super.drawHUD(g, uiItems == null ? "(" + loadingStr + "...)" : title);
	}

	public final void release(int x, int y) {
		try {
			if (y > 58 && y < DisplayUtils.height - oneitemheight) {
				int h = 50;
				int yy1 = y - (scrolled + 58);
				int i = yy1 / h;
				if (i < 0)
					i = 0;
				if (!dragging) {
					uiItems[i].tap(x, yy1 - (h * i));
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.release(x, y);
	}

	public static void open(final int id, final String name, final String name2) {
		IMenu m = new EmptyMenu() {
			public void onMenuItemPress(int i) {
				try {
					if (i == 0) {
						MusicScreen pls = new MusicScreen();
						VikaTouch.setDisplay(pls, 1);
						pls.load(id, 0, getMusicTitle("music", name, name2));
					} else if (i == 1) {
						PlaylistsScreen pls = new PlaylistsScreen();
						VikaTouch.setDisplay(pls, 1);
						pls.load(id, getMusicTitle("playlists", name, name2));
					} else if (i == 2) {
						VikaTouch.setDisplay(MusicPlayer.inst, 1);
					} else if (i == 3) {
						final MusicScreen pls = new MusicScreen();
						if (thread != null)
							thread.interrupt();
						
						thread = new Thread() {
							public void run() {
								q = TextEditor.inputString("Введите название композиции: ", "", 28, false);
								
								pls.load(q);
								pls.repaint();
								VikaTouch.setDisplay(pls, 1);
								pls.repaint();
							}
						};
						thread.start();
						
						
						
						// Thread.start();
			            } else if (i == 4) {
							MusicScreen pls = new MusicScreen();
							VikaTouch.setDisplay(pls, 1);
							pls.loadRecommendations("");
						}
				} catch (Exception e) {
					VikaTouch.sendLog("Music open: " + e.toString());
				}
			}
		};
		OptionItem[] oi = new OptionItem[MusicPlayer.inst == null ? 4 : 5];
		try {
			oi[0] = new OptionItem(m, TextLocal.inst.get("music.all"), IconsManager.MUSIC, 0, 50);
			oi[1] = new OptionItem(m, TextLocal.inst.get("title.playlists"), IconsManager.MENU, 1, 50);
			 oi[2] = new OptionItem(m, TextLocal.inst.get("music.search"), 12, 3, 50);
			 oi[3] = new OptionItem(m, TextLocal.inst.get("music.recommendations"), 12, 4, 50);
			if (MusicPlayer.inst != null) {
				 oi[4] = new OptionItem(m, MusicPlayer.inst.title == null ? "Player" : MusicPlayer.inst.title,
						IconsManager.PLAY, 2, 50);
			}
		} catch (Exception e) {
		}

		VikaTouch.popup(new ContextMenu(oi));
	}

	protected static String getMusicTitle(String s, String name, String name2) {
		if (name == null) {
			return TextLocal.inst.get("title." + s);
		} else {
			return TextLocal.inst.getFormatted("title." + s + "w", new String[] { name, name2 });
		}
	}
}
