package vikatouch.screens.music;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

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
import vikatouch.items.LoadMoreButtonItem;
import vikatouch.items.music.AudioTrackItem;
import vikatouch.locale.TextLocal;
import vikatouch.music.MusicPlayer;
import vikatouch.screens.LoginScreen;
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
		VikaTouch.needstoRedraw=true;
	}

	public void loadAtt(AudioAttachment aa) {
		itemsCount = 1;
		scrolled = 0;
		this.albumId = 0;
		ownerId = 0;
		this.title = TextLocal.inst.get("attachment");
		hasBackButton = true;
		uiItems = new Vector(1);
		AudioTrackItem ati = new AudioTrackItem(null, this, 0);
		ati.name = aa.name;
		ati.artist = "";
		ati.id = 0;
		ati.owner_id = 0;
		ati.length = aa.size;
		ati.lengthS = (ati.length / 60) + ":" + (ati.length % 60 < 10 ? "0" : "") + (ati.length % 60);
		ati.mp3 = aa.musUrl;
		uiItems.addElement(ati);
		VikaTouch.loading = false;
		System.gc();
	}

	public void load(final int oid, final int albumId, String title) {
		VikaTouch.needstoRedraw=true;
		scrolled = 0;
		
		this.albumId = albumId;
		ownerId = oid;
		this.title = title;
		hasBackButton = true;
		
		 
		
		
		if (downloaderThread != null && downloaderThread.isAlive()) {
			try {
			downloaderThread.interrupt();
			} catch (Throwable ee) {}
		}
		downloaderThread = new Thread() {
			public void run() {
				
				try {
					String m = VikaUtils.download(URLBuilder.makeSimpleURL("audio.get"));
					
					if ((m.indexOf("authorization failed")<0) && (m.indexOf("confirmation required") < 0)) {
						VikaTouch.inst.saveToken();
					} else {
						if (m.indexOf("authorization failed") > -1) {
							try {
								RecordStore.deleteRecordStore(VikaTouch.TOKEN_RMS);
							} catch (Exception e) {
								
							}
							VikaTouch.error("Сессия недействительна, перелогиньтесь", true);
						}
						if (m.indexOf("confirmation required") > -1) {
							VikaTouch.accessToken = null;
							try {
								RecordStore.deleteRecordStore(VikaTouch.TOKEN_RMS);
							} catch (Exception e) {
								
							}
							VikaTouch.error("ВК криво залогинил вас и не дал прав на музыку, перезайдите", true);
						}
					}
					VikaTouch.loading = true;
				//	String x = VikaUtils.music(new URLBuilder(Settings.proxyApi, "audio.get", true).addField("owner_id", oid)
					//		.addField("album_id", albumId).addField("count", VikaTouch.muscount).addField("offset", 0).toString());
				//	String x = VikaUtils.download(VikaTouch.API + "/method/audio.get?access_token="
				//			+ VikaTouch.accessToken + "&count="+VikaTouch.muscount+"&owner_id=" + oid + "&v=5.91");
					String x = "";
					//VikaTouch.sendLog(String.valueOf(albumId));
					if (albumId==0) {
						String musurl = VikaTouch.API + "/method/audio.get?access_token="
								+ VikaTouch.accessToken + "&count="+String.valueOf(VikaTouch.muscount)+"&owner_id=" + String.valueOf(oid) + "&v=5.91";
						//VikaTouch.sendLog(musurl);
						x = VikaUtils.music(musurl);
					} else {
						//VikaUtils.music(new URLBuilder(Settings.proxyApi, "audio.get", true).addField("owner_id", oid)
							//			.addField("album_id", albumId).addField("count", VikaTouch.muscount).addField("offset", 0).toString());
						String musurl = VikaTouch.API + "/method/audio.get?album_id="+String.valueOf(albumId)+"&access_token="
								+ VikaTouch.accessToken + "&count="+
								String.valueOf(VikaTouch.muscount)+
								"&offset=0&owner_id=" + String.valueOf(oid) + "&v=5.91";
						//VikaTouch.sendLog(musurl);
						x = VikaUtils.music(musurl);
						//VikaTouch.sendLog(musurl);
					}
					// VikaTouch.sendLog(x);
					if (x.indexOf("error") > -1) {
						//VikaTouch.sendLog(x);
						//if (x.indexOf("deprecated")>-1) {
						/*	try {
								VikaTouch.logout();
								VikaTouch.gc();
							} catch (Exception e) {

							}
							VikaTouch.error(ErrorCodes.MUSICLISTLOAD, "Для работы музыки после обновления приложения перелогиньтесь!", false);
							
							VikaTouch.needstoRedraw=true;
							VikaTouch.setDisplay(new LoginScreen(), -1);
							return;
						//} else {
						//VikaTouch.error(ErrorCodes.MUSICLISTLOAD, x, false);
						//return;
						//}*/
					}
					try {
						VikaTouch.loading = true;
						JSONObject response = new JSONObject(x).getJSONObject("response");
						JSONArray items = response.getJSONArray("items");
						itemsCount = (short) items.length();
						uiItems=null;
						uiItems = new Vector(itemsCount);
						for (int i = 0; i < itemsCount; i++) {
							JSONObject item = items.getJSONObject(i);
							AudioTrackItem ati = new AudioTrackItem(item, MusicScreen.this, i);
							uiItems.addElement(ati);
							ati.parseJSON();
							Thread.sleep(15);
							Thread.yield();
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
		VikaTouch.needstoRedraw=true;
	}
	
	public void load(final String q) {
		VikaTouch.needstoRedraw=true;
	    scrolled = 0;
	   
	    this.title = TextLocal.inst.get("music.searchresult");
	    this.hasBackButton = true;
	    if (downloaderThread != null && downloaderThread.isAlive()) {
	    	try {
	    	downloaderThread.interrupt();
	    	} catch (Throwable ee) {}
	    }
	    downloaderThread = new Thread() {
	       
	        
	        
	        
	        public void run() {
	          try {
	            VikaTouch.loading = true;
	          //  VikaTouch.inst.refreshToken();;
	           // VikaTouch.inst.saveToken();
	           
	           // String x = VikaUtils.music((new URLBuilder("audio.search", true)).addField().addField("count", 50).addField("offset", 0).toString());
	            String x = VikaUtils.music(VikaTouch.API + "/method/audio.search?offset=0&count=50&v=5.91"+"&access_token="
						+ VikaTouch.accessToken + "&q="+URLDecoder.encodeFull(q));
	            if (x.indexOf("error") > -1) {
					if (x.indexOf("deprecated")>-1) {
						try {
							VikaTouch.logout();
							VikaTouch.gc();
						} catch (Exception e) {

						}
						VikaTouch.error(ErrorCodes.MUSICLISTLOAD, "Для работы музыки после обновления приложения перелогиньтесь!", false);
						
						VikaTouch.needstoRedraw=true;
						VikaTouch.setDisplay(new LoginScreen(), -1);
						return;
					} else {
					VikaTouch.error(ErrorCodes.MUSICLISTLOAD, x, false);
					return;
					}
				}
	            try {
	              System.out.println(x);
	              VikaTouch.loading = true;
	              JSONObject response = (new JSONObject(x)).getJSONObject("response");
	              JSONArray items = response.getJSONArray("items");
	              itemsCount = (short)items.length();
	              uiItems = null;
	          	uiItems = new Vector(itemsCount);
	              for (int i = 0; i < itemsCount; i++) {
	                JSONObject item = items.getJSONObject(i);
	                AudioTrackItem ati = new AudioTrackItem(item, MusicScreen.this, i);
					uiItems.addElement(ati);
					ati.parseJSON();
	            	VikaTouch.needstoRedraw=true;
	               // Thread.sleep(15L);
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
	    VikaTouch.needstoRedraw=true;
	  }
	
	
	public void loadRecommendations(final String audio_id) {
		VikaTouch.needstoRedraw=true;
	    scrolled = 0;
	    
	    this.title = TextLocal.inst.get("music.recommendations");
	    this.hasBackButton = true;
	    if (downloaderThread != null && downloaderThread.isAlive()) {
	     try {
	    	downloaderThread.interrupt();
	     } catch (Throwable ee) {}
	    }
	    downloaderThread = new Thread() {
	       
	        
	        
	        
	        public void run() {
	          try {
	            VikaTouch.loading = true;
	          //  VikaTouch.inst.refreshToken();;
	           // VikaTouch.inst.saveToken();
	            String x= "";
	           if (audio_id.equals("")) {
	        	   // x = VikaUtils.download(VikaTouch.API + "/method/audio.getRecommendations?access_token="
					//		+ VikaTouch.accessToken+"&offset=0&count=50&v=5.91");
	        	  // x = VikaUtils.music((new URLBuilder("audio.getRecommendations", true)).addField("count", 50).addField("offset", 0).toString());
	        	   x = VikaUtils.music(VikaTouch.API + "/method/audio.getRecommendations?access_token="
	   							+ VikaTouch.accessToken+"&offset=0&count=50&v=5.91");
		           
	           } else {
	        	  // x = VikaUtils.download(VikaTouch.API + "/method/audio.getRecommendations?access_token="
					//		+ VikaTouch.accessToken+"&offset=0&count=50&v=5.91&target_audio="+audio_id);
	        	   //x = VikaUtils.music((new URLBuilder("audio.getRecommendations", true)).addField("count", 50).addField("offset", 0).addField("targetAudio", audio_id).toString());
	        	   x = VikaUtils.music(VikaTouch.API + "/method/audio.getRecommendations?access_token="
  							+ VikaTouch.accessToken+"&offset=0&count=50&v=5.91");
	 	          
	           }
	           if (x.indexOf("error") > -1) {
					if (x.indexOf("deprecated")>-1) {
						try {
							VikaTouch.logout();
							VikaTouch.gc();
						} catch (Exception e) {

						}
						VikaTouch.error(ErrorCodes.MUSICLISTLOAD, "Для работы музыки после обновления приложения перелогиньтесь!", false);
						
						VikaTouch.needstoRedraw=true;
						VikaTouch.setDisplay(new LoginScreen(), -1);
						return;
					} else {
					VikaTouch.error(ErrorCodes.MUSICLISTLOAD, x, false);
					return;
					}
				}
	            try {
	              System.out.println(x);
	              VikaTouch.loading = true;
	              JSONObject response = (new JSONObject(x)).getJSONObject("response");
	              JSONArray items = response.getJSONArray("items");
	              itemsCount = (short)items.length();
	              uiItems = null;
	              uiItems = new Vector(itemsCount);
	              for (int i = 0; i < itemsCount; i++) {
	                JSONObject item = items.getJSONObject(i);
	                AudioTrackItem ati = new AudioTrackItem(item, MusicScreen.this, i);
					uiItems.addElement(ati);
					ati.parseJSON();
	            	VikaTouch.needstoRedraw=true;
	                //Thread.sleep(15L);
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
	    VikaTouch.needstoRedraw=true;
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
					for (int i = 0; i < uiItems.size(); i++) {
						if (((PressableUIItem) uiItems.elementAt(i)) != null) {
							if (y + scrolled > DisplayUtils.height)
								break;
							((PressableUIItem) uiItems.elementAt(i)).paint(g, y, scrolled);
							y += ((PressableUIItem) uiItems.elementAt(i)).getDrawHeight();
						}
						itemsh = y + 60;
					}
				}
			} catch (Throwable e) {
				VikaTouch.error(e, ErrorCodes.MUSICITEMDRAW);
				e.printStackTrace();
			}
			g.translate(0, -g.getTranslateY());
		} catch (Throwable e) {
			VikaTouch.error(e, ErrorCodes.MUSICDRAW);
			e.printStackTrace();
		}
	}

	public final void drawHUD(Graphics g) {
		super.drawHUD(g, uiItems == null ? "(" + loadingStr + "...)" : title);
	}

	public final void release(int x, int y) {
		VikaTouch.needstoRedraw=true;
		try {
			if (y > topPanelH && y < DisplayUtils.height - bottomPanelH && !dragging) {
				int h = 50;
				int yy1 = y - (scrolled + topPanelH);
				int i = yy1 / h;
				if (i < 0)
					i = 0;
				//if (!dragging) {
				((PressableUIItem) uiItems.elementAt(i)).tap(x, yy1 - (h * i));
				//}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		super.release(x, y);
	}

	public static void open(final int id, final String name, final String name2) {
		VikaTouch.needstoRedraw=true;
		IMenu m = new EmptyMenu() {
			public void onMenuItemPress(int i) {
				VikaTouch.needstoRedraw=true;
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
		VikaTouch.needstoRedraw=true;
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
