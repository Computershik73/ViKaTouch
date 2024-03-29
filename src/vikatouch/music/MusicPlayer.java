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
import javax.microedition.io.file.IllegalModeException;
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
import ru.nnproject.vikaui.menu.items.OptionItem;
import ru.nnproject.vikaui.popup.AutoContextMenu;
import ru.nnproject.vikaui.popup.ConfirmBox;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.attachments.VoiceAttachment;
import vikatouch.items.VikaNotification;
import vikatouch.items.music.AudioTrackItem;
import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;
import vikatouch.screens.music.MusicScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.url.URLDecoder;

/**
 * @author Feodor0090
 * 
 */
// экранчик с названием песни, перемоткой ии... Всё.
public class MusicPlayer extends MainScreen implements IMenu, PlayerListener, AsyncLoadListener {

	public MusicScreen playlist;
	public static int current;
	public boolean isPlaying = false;
	public boolean isReady = false;
	public boolean stop = false;
	public static boolean loop = false;
	public static boolean random = false;
	public boolean inSeekMode = false;
	public boolean controlsBlocked = false;
	public long seekTime;

	public VoiceAttachment voice;

	public static String url;

	// кэш для рисования 
	public String title = "Track name";
	public String artist = "track artist";
	private int titleW, artistW;
	private int currTx, currAx;
	// private String totalNumber;
	private String time = "00:00";
	private String totalTime = "99:59";
	public static final int PBMARGIN = 60;
	private int x1 = PBMARGIN, x2 = 360 - PBMARGIN, currX = 100;
	public Image[] buttons;
	private Image coverOrig;
	private Image resizedCover;
	public static Image defaultCover;
	private int lastW;

	private int volumeY, volumeX1, volumeX2;

	public static MusicPlayer inst;
	public Player player;
	public Manager mgr;
	public InputStream inStream;
	public OutputStream outStream;
	public Thread loader;
	public FileConnection outConn;
	public DataInputStream netInStream;
	public ContentConnection inConn;
	public int prevsongindex=-1;
	public int prevprevsongindex=-1;
	protected AsyncLoadDataSource asyncsrc;
	private int buffered;
	private int currX2;

	public MusicPlayer() {
		try {
			Image sheet = Image.createImage("/playerbtns.png");
			buttons = new Image[7];
			for (int i = 0; i < 7; i++) {
				buttons[i] = Image.createImage(sheet, i * 50, 0, 50, 50, 0);
			}
		} catch (Exception e) {

		}
	}

	public void destroy() {
		try {
			player.stop();
		} catch (Exception e) {
		}
		closePlayer();

	}

	public void firstLoad() {
		loadTrack();
	}

	public void loadTrack() {
		/*	if (VikaTouch.canvas.currentScreen != this && voice == null) {
			VikaTouch.notificate(new VikaNotification(VikaNotification.NEXT_TRACK, "Сейчас играет",
					VikaUtils.cut(getC().name, 40), this));
		}*/
		VikaTouch.needstoRedraw=true;
		if (player != null) {
			if (player.getState() == 400) {
				try {
					player.stop();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				if (player.getState() == 300) {
					player.deallocate();
				}
			}  catch (Exception e1) {}
			try {
				if (player.getState() == 200 || player.getState() == 100) {
					player.close();
				}
			}  catch (Exception e2) {}
		}
		try {
			player.stop();
		} catch (Exception e3) {
		}
		coverOrig = null;
		resizedCover = null;
		isPlaying = true;
		isReady = false;
		// stop = false;
		loadTrackInfo();
		time = "";
		totalTime = "";
		inst = this;
		byte[] aByteArray207 = null;
		System.gc();
		try {
			outStream.close();
			outConn.close();
			netInStream.close();
			inConn.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		VikaTouch.needstoRedraw=true;
		//boolean CACHETOPRIVATE = false;
		/*String tpath = (CACHETOPRIVATE ? System.getProperty("fileconn.dir.private")
				: System.getProperty("fileconn.dir.music"));
		if ((VikaTouch.mobilePlatform.indexOf("NokiaN91")>-1) || (VikaTouch.mobilePlatform.indexOf("NokiaN8")>-1) || (VikaTouch.mobilePlatform.indexOf("Nokia808")>-1)) {
			tpath="file:///E:/Sounds/";
		}
		String jver = System.getProperty("java.version");
		if (jver == null) {
			jver = "-";
		} else {
		if (jver.indexOf("phoneme")<0) {
			if (tpath == null)
				tpath = System.getProperty("fileconn.dir.photos");
			if (tpath == null)
				tpath = "C:/Users/ilmoh/Downloads/";
			} else {
				tpath = "file:///"+"MyDocs/.sounds/";
			}
		}*/
		String tpath = Settings.musicpath;
		final String path = this.voice==null ? tpath + 
				//((tpath.indexOf("C:")>-1) ?
				//String.valueOf(current) :
				String.valueOf(getC().id) 
		//)
		+"vikaMusicCache.mp3" : tpath + String.valueOf(voice.mid) + "vikaVoiceCache.mp3";
		System.out.println(path);
		//for (int i=0; i<99; i++) 
		if (prevprevsongindex!=-1) 
		{
			String pathx = tpath +  String.valueOf(prevprevsongindex) +"vikaMusicCache.mp3";
			FileConnection outConnx = null;
			try {
				outConnx = (FileConnection) Connector.open(pathx, Connector.READ_WRITE);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (outConnx.exists()) {
				try {
					outConnx.delete();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					outConnx.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				try {
					outConn.delete();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					outConn.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			} else {
				//outConn.create();
			}
		}

		if (prevsongindex!=-1) 
		{
			String pathx = tpath +  String.valueOf(prevsongindex) +"vikaMusicCache.mp3";
			FileConnection outConnx = null;
			try {
				outConnx = (FileConnection) Connector.open(pathx, Connector.READ_WRITE);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (outConnx.exists()) {

				try {
					outConnx.delete();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					outConnx.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					outConn.delete();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					outConn.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			} else {
				//outConn.create();
			}
		}


		System.out.println(path);
		try {
			outConn = (FileConnection) Connector.open(path, Connector.READ_WRITE);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			try {
				outConn.delete();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				outConn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			if (outConn.exists()) {
				//outConn.delete();
				//outConn.create();
				player = Manager.createPlayer(
						//Connector.openInputStream(url)
						outConn.openInputStream()
						, "audio/mpeg");
				player.realize();

				player.start();
				isReady = true;
				isPlaying = true;
				try {
					((VolumeControl) player.getControl("VolumeControl")).setLevel(Settings.playerVolume);
				} catch (Exception e) {
				}
				totalTime = time(voice == null ? getC().length : voice.size);
				stop = false;
				player.addPlayerListener(inst);
				return;
			} else {
				outConn.create();
			}
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (Exception e) {
			//e.printStackTrace();
		} 
		// TODO move temp constants


		//try {
		try {
			closePlayer();
		} catch (Exception var20) {
			//VikaTouch.popup(new InfoPopup("Player closing error", null));
		}
		try {
			getCover();
			VikaTouch.needstoRedraw=true;
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			resizeCover();
		} catch (Exception ee) {}
		//resizeCover();
		url = getMp3Link();
		// VikaTouch.sendLog(url);

		if ((Settings.audioMode == Settings.AUDIO_PLAYONLINE) || (this.voice != null)) {
			if(loader != null) {
				try {
					loader.interrupt();
				} catch (Throwable ee) {}
			}
			loader = null;
			loader = new Thread() {
				public void run() {
					while (stop) {
						//	VikaTouch.needstoRedraw=true;
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							return;
						}
					}
					VikaTouch.needstoRedraw=true;
					try {
						time = "00:00";
						totalTime = "--:--";
						player = Manager.createPlayer(Connector.openInputStream(url), "audio/mpeg");
						player.realize();

						player.start();
						isReady = true;
						isPlaying = true;
						try {
							((VolumeControl) player.getControl("VolumeControl")).setLevel(Settings.playerVolume);
						} catch (Exception e) {
						}
						totalTime = time(voice == null ? getC().length : voice.size);
						stop = false;
						player.addPlayerListener(inst);
						getCover();
						resizeCover();
						VikaTouch.needstoRedraw=true;
					} catch (InterruptedException e) {
						return;
					} catch (Exception e) {
						e.printStackTrace();
						isReady = true;
						String es = e.toString();
						if (es.indexOf("nvalid") > -1 && es.indexOf("esponse") > -1) {
							es = TextLocal.inst.get("player.accessfailed");
						} else {
							es = "Online mode loading: " + es;
						}
						VikaTouch.popup(new InfoPopup(es, null, TextLocal.inst.get("player.playererror"), null));
					} finally {
						stop = false;
					}
				}
			};
			loader.setPriority(Thread.MAX_PRIORITY);
			loader.start();
		} else if(Settings.audioMode == Settings.AUDIO_ASYNC) {

			if(loader != null)
				loader.interrupt();
			loader = null;
			loader = new Thread() {
				public void run() {
					while (stop) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							return;
						}
					}
					try {
						time = "00:00";
						totalTime = "--:--";
						player = Manager.createPlayer(asyncsrc = new AsyncLoadDataSource(url));
						asyncsrc.setListener(MusicPlayer.this);
						player.realize();
						player.prefetch();
						player.start();
						isReady = true;
						isPlaying = true;
						try {
							((VolumeControl) player.getControl("VolumeControl")).setLevel(Settings.playerVolume);
						} catch (Exception e) {
						}
						totalTime = time(voice == null ? getC().length : voice.size);
						stop = false;
						player.addPlayerListener(inst);
						getCover();
						//resizeCover();
					} catch (InterruptedException e) {
						return;
					} catch (Throwable e) {
						e.printStackTrace();
						isReady = true;
						String es = e.toString();
						if (es.indexOf("nvalid") > -1 && es.indexOf("esponse") > -1) {
							es = TextLocal.inst.get("player.accessfailed");
						} else {
							es = "async: " + es;
						}
						VikaTouch.popup(new InfoPopup(es, null, TextLocal.inst.get("player.playererror"), null));
					} finally {
						stop = false;
					}
				}
			};
			loader.start();
		} else if (Settings.audioMode != Settings.AUDIO_CACHEANDPLAY) {

			loader = new Thread() {
				private int downloaded;

				public void run() {
					while (stop) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							break;
						}
					}
					try {

						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							return;
						}
						// outConn = (FileConnection) Connector.open(path, Connector.READ_WRITE);
						/*try {
								if (outConn.exists()) {
									outConn.delete();
									outConn.create();
								} else {
									outConn.create();
								}
							} catch (IOException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							} */

						outStream = outConn.openOutputStream();
						//VikaUtils.makereq(url);
						//Thread.sleep(1000);
						//VikaTouch.sendLog(url);
						url = VikaUtils.getRedirUrl(url);
						inConn = (ContentConnection) Connector.open(url, Connector.READ);
						netInStream = inConn.openDataInputStream();

						int trackSize = (int) inConn.getLength();
						totalTime = (trackSize / 1024 / 1024) + "." + (trackSize / 1024 % 103) + "MB";
						byte[] cacheBuffer;

						int trackSegSize = (int) trackSize / 100;
						int i = 0;

						boolean simple = true;

						if(trackSize / 1024 == 0) {
							// Происходит абсолютно рандомно, но после одного раза.
							// UPD 10.01 04:54: добавил Thread.sleep(100); перед загрузкой, вроде помогло.
							//VikaTouch.sendLog(url);
							//simple=true;
							System.out.println(url);
							VikaTouch.popup(new InfoPopup("Cache error: 404", null));
							try {
								outStream.close();
								outConn.close();
								netInStream.close();
								inConn.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
							return;
						}

						if(simple) {
							//if(trackSize / 1024 != 0) {
							System.out.println("need: " + trackSize / 1024 + "K");
							//}
							byte[] buf = new byte[VikaTouch.isNotS60() ? 65536 : 524288];
							int read;
							downloaded = 0;
							Thread speedCount = null;
							//if(trackSize / 1024 != 0) {
							speedCount = new Thread() {
								public void run() {
									System.out.println("speed run");
									try {
										// на какоето время показывать размер песни, а потом уже скорость
										VikaTouch.needstoRedraw=true;
										Thread.sleep(10);
									} catch (InterruptedException e1) {
										System.out.println("ne uspel");
										return;
									}
									System.out.println("speed start");
									while(true) {
										int i = downloaded;
										try {
											Thread.sleep(1000);
											VikaTouch.needstoRedraw=true;
											VikaTouch.canvas.serviceRepaints();
										} catch (InterruptedException e) {
											System.out.println("speed end");
											VikaTouch.canvas.serviceRepaints();
											return;
										}
										int kilo = downloaded - i;
										double mega = (double)kilo / 1024d / 1024d;
										double round = 0;
										int pow = 100;
										double tmp = mega * pow;
										round = (double) (int) ((tmp - (int) tmp) >= 0.5 ? tmp + 1 : tmp) / pow;
										totalTime = round + " MB/s";
										System.out.println(round + " MB/s");
										VikaTouch.needstoRedraw=true;
										VikaTouch.canvas.serviceRepaints();
										yield();
									}
								}
							};

							speedCount.setPriority(Thread.NORM_PRIORITY);
							speedCount.start();
							//}
							while((read = netInStream.read(buf)) != -1) {
								outStream.write(buf, 0, read);
								outStream.flush();
								downloaded += read;
								//if(trackSize / 1024 != 0) {
								i = (int)(((double)downloaded / (double)trackSize) * 100d);
								time = i + "%";
								VikaTouch.needstoRedraw=true;
								VikaTouch.canvas.serviceRepaints();
								//}

								if (stop) {
									stop = false;
									if (speedCount!=null) {
										try {
											speedCount.interrupt();
										} catch (Throwable ee) {}
									}
									outStream.close();
									outConn.close();
									netInStream.close();
									inConn.close();
									return;
								}
							}
							try {
								speedCount.interrupt();
							} catch (Throwable ee) {}
							System.out.println("downloaded: " + (downloaded / 1024) + "K");
						} else {

							if ((int) inConn.getLength() != -1) {
								while ((inConn.getLength() / 100) * (i + 1) < inConn.getLength()) {
									cacheBuffer = new byte[trackSegSize];
									time = i + "%";
									netInStream.read(cacheBuffer);
									outStream.write(cacheBuffer);
									i++;
									if (stop) {
										stop = false;
										netInStream.close();
										inConn.close();
										outStream.close();
										return;
									}
									if (Runtime.getRuntime().freeMemory() < trackSize * 3)
										System.gc();
								}
								cacheBuffer = new byte[(int) (inConn.getLength() - inConn.getLength() / 100 * (i))];
								time = "99,9%";
								netInStream.read(cacheBuffer);
								outStream.write(cacheBuffer);
								outStream.flush();
							}
						}

						if (netInStream != null) {
							netInStream.close();
						}
						if (inConn != null) {
							inConn.close();
						}
						if (outStream != null) {
							outStream.close();
						}
						stop = false;
						try {
							//speedCount.interrupt();
							outStream.close();
						} catch (Exception e1) {}
						try {
							outConn.close();

						} catch (Exception e1) {}
						try {
							netInStream.close();
						} catch (Exception e1) {}
						try {
							inConn.close();
						} catch (Exception e1) {}
						/*
						 * if ((VikaTouch.mobilePlatform.indexOf("S60") > 0 &&
						 * (VikaTouch.mobilePlatform.indexOf("5.5") > 0 ||
						 * VikaTouch.mobilePlatform.indexOf("5.4") > 0 ||
						 * VikaTouch.mobilePlatform.indexOf("5.3") > 0 ||
						 * VikaTouch.mobilePlatform.indexOf("5.2") > 0 ||
						 * VikaTouch.mobilePlatform.indexOf("5.1") > 0 ||
						 * VikaTouch.mobilePlatform.indexOf("5.1") > 0 ||
						 * VikaTouch.mobilePlatform.indexOf("5.0") > 0 ||
						 * VikaTouch.mobilePlatform.indexOf("S8600") > 0) ) ||
						 * VikaTouch.mobilePlatform.indexOf("Sony") > 0) {
						 */
						if (Settings.audioMode == Settings.AUDIO_LOADANDPLAY) {

							try {
								player = Manager.createPlayer(Connector.openInputStream(path), "audio/mpeg");
								player.addPlayerListener(inst);
							} catch (Exception e) {
								stop = false;
								e.printStackTrace();
								VikaTouch.popup(new InfoPopup("Player creating error", null)); // TODO errcodes
								return;
							}
							time = "100%";
							try {
								player.realize();
							} catch (MediaException e) {
								stop = false;
								e.printStackTrace();
								VikaTouch.popup(new InfoPopup("Player realizing error", null));
								return;
							}
							try {
								player.prefetch();
							} catch (MediaException e) {
								stop = false;
								if (trackSize < 10000) {
									VikaTouch.popup(new InfoPopup(TextLocal.inst.get("player.accessfailed"), null));
								} else
									VikaTouch.popup(new InfoPopup("Player prefetching error", null));
								e.printStackTrace();
								return;
							}
							try {
								player.start();
								VikaTouch.canvas.serviceRepaints();
							} catch (MediaException e) {
								stop = false;
								VikaTouch.popup(new InfoPopup("Player running error", null));
								e.printStackTrace();
								return;
							}
							try {
								((VolumeControl) player.getControl("VolumeControl"))
								.setLevel(Settings.playerVolume);
							} catch (Throwable e) {
								e.printStackTrace();
							}
							time = "0:00";
							totalTime = time(player.getDuration() / 1000000L);
							VikaTouch.needstoRedraw=true;
							if(player.getDuration() <= 0)
								totalTime = time(voice == null ? getC().length : voice.size);
							try {
								player.addPlayerListener(inst);
								VikaTouch.canvas.serviceRepaints();
							} catch (Throwable e) {
								e.printStackTrace();
							}
						} else if (Settings.audioMode == Settings.AUDIO_LOADANDSYSTEMPLAY) {
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
						getCover();
						resizeCover();
					} catch (Throwable e) {
						e.printStackTrace();
						isReady = true;
						stop = false;
						VikaTouch.popup(new InfoPopup("Common player error: " + e.toString(), null));
					}
					System.gc();
				}
			};
			loader.setPriority(Thread.MAX_PRIORITY);
			loader.start();
		} else {
			time = "...";
			totalTime = "--:--";
			ContentConnection contCon = null;
			DataInputStream dis = null;
			try {
				contCon = (ContentConnection) Connector.open(url, Connector.READ);
				dis = contCon.openDataInputStream();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				int var5 = (int) contCon.getLength();
				System.out.println("var5: " + var5);
				if (var5 != -1) {
					aByteArray207 = new byte[var5];
					try {
						dis.read(aByteArray207);
						VikaTouch.needstoRedraw=true;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (Throwable ee) {

			} finally {
				if (dis != null) {
					try {
						dis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (contCon != null) {
					try {
						contCon.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			ByteArrayInputStream aByteArrayInputStream212 = new ByteArrayInputStream(aByteArray207);
			System.gc();
			inStream = aByteArrayInputStream212;
			try {
				player = Manager.createPlayer(inStream, "audio/mpeg");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Throwable e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				player.realize();
			} catch (Throwable e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				((VolumeControl) player.getControl("VolumeControl")).setLevel(Settings.playerVolume);
				VikaTouch.needstoRedraw=true;
			} catch (Exception e) {
			}
			try {
				player.start();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			totalTime = time(player.getDuration() / 1000000L);
			isReady = true;
			isPlaying = true;
			stop = false;
			try {
				getCover();
				VikaTouch.needstoRedraw=true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//resizeCover();
			//prevprevsongindex=prevsongindex;
			//prevsongindex=current;

		}

		// System.gc();
		//} catch (Throwable e) {
		//stop = false;
		//	VikaTouch.popup(new InfoPopup(e.toString(), null, TextLocal.inst.get("player.playererror"), null));
		//}

		Settings.saveSettings(); // громкость там...
	}

	private void closePlayer()  {
		if (player != null) {
			if (player.getState() == 400) {
				try {
					player.stop();
				} catch (MediaException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e1) {}
			}
			try { 
				if (player.getState() == 300) {
					player.deallocate();
				} }
			catch (Exception e2) {}
			try {
				if (player.getState() == 200 || player.getState() == 100) {
					player.close();
				}
			} catch (Exception e3) {}
		}

		player = null;
		if (inStream != null) {
			try {
				inStream.close();
			} catch (Exception e) {
			}
			inStream = null;
		}
		VikaTouch.needstoRedraw=true;
		
		if(asyncsrc != null) {
			try {
				asyncsrc.disconnect();
			} catch (Exception e) {
			}
			asyncsrc = null;
		}
		
		System.gc();
	}

	public void pause() {
		if (controlsBlocked)
			return;
		System.out.println("PAUSE");
		try {
			if (isReady) {
				isPlaying = false;
				try {
					VikaTouch.needstoRedraw=true;
					player.stop();
					//player.deallocate();
				} catch (Exception e) {

				}



			} else {
				VikaTouch.popup(new ConfirmBox(TextLocal.inst.get("player.cancel"), null, new Runnable() {
					public void run() {
						if (!isReady) {
							VikaTouch.needstoRedraw=true;
							stop = true;
						}
					}
				}, null, TextLocal.inst.get("ok"), TextLocal.inst.get("cancel")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play() {
		//if (controlsBlocked)
		//	return;
		try {
			if (isReady) {
				if (inSeekMode) {
					checkSeekTime();
					inSeekMode = false;
					player.setMediaTime(seekTime);
					VikaTouch.needstoRedraw=true;
				}
				player.start();
				isPlaying = true;
				VikaTouch.needstoRedraw=true;
				VikaTouch.canvas.serviceRepaints();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void tapSeek(int x) {
		if (controlsBlocked)
			return;
		try {
			if (!isReady)
				return;
			if (player == null)
				return;
			// if(Settings.audioMode == Settings.AUDIO_PLAYONLINE) return;
			if (x <= x1 || x >= x2)
				return;

			double p = (float) (x - x1) / (x2 - x1);
			long st = ((long) ((getC().length) * p)) * 1000000L;

			if (st < 1L)
				st = 1L;
			if (Settings.audioMode == Settings.AUDIO_PLAYONLINE || st < player.getDuration()) {
				try {
					player.stop();
				} catch (Exception e) {
				}
				player.setMediaTime(st);
				player.start();
				inSeekMode = false;
				isPlaying = true;
				VikaTouch.needstoRedraw=true;
			}
		} catch (Exception e) {
			VikaTouch.popup(new InfoPopup(e.toString(), null, TextLocal.inst.get("player.playererror"), null));
		}
	}

	public void checkSeekTime() {
		if (seekTime < 1L)
			seekTime = 1L;
		if (seekTime > (getC().length - 5) * 1000000L) {
			seekTime = (getC().length - 5) * 1000000L;
			VikaTouch.needstoRedraw=true;
		}
	}

	public void next() {
		if (voice != null)
			return;
		if (controlsBlocked)
			return;
		if (inSeekMode) {
			seekTime += 5000000L;
			checkSeekTime();
			VikaTouch.needstoRedraw=true;
		} else {
			if (!isReady) {
				stop = true;
			}
			if (random) {
				Random r = new Random();
				current = r.nextInt(playlist.uiItems.size());
			} else {
				current++;
				if (current >= playlist.uiItems.size())
					current = 0;
			}




			if (player != null) {
				if (player.getState() == 400) {
					try {
						player.stop();
					} catch (MediaException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (player.getState() == 300) {
					player.deallocate();
				}

				if (player.getState() == 200 || player.getState() == 100) {
					player.close();
				}
			}
			try {
				player.stop();
			} catch (Exception e) {
			}

			if(loader != null) {
				try {
					loader.interrupt();
				} catch (Throwable ee) {}
			}


			if (netInStream != null) {
				try {
					netInStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (inConn != null) {
				try {
					inConn.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (outStream != null) {
				try {
					outStream.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			stop = false;
			try {
				//speedCount.interrupt();
				outStream.close();
			} catch (Exception e1) {}
			try {
				if (!isReady) {
					outConn.delete();
				}
				outConn.close();


			} catch (Exception e1) {}
			try {
				netInStream.close();
			} catch (Exception e1) {}
			try {
				inConn.close();
			} catch (Exception e1) {}
			/*boolean CACHETOPRIVATE = false;
			String tpath = (CACHETOPRIVATE ? System.getProperty("fileconn.dir.private")
					: System.getProperty("fileconn.dir.music"));
			String jver = System.getProperty("java.version");
			if (jver == null)
				jver = "-";
			if (jver.indexOf("phoneme")<0) {
			if (tpath == null)
				tpath = System.getProperty("fileconn.dir.photos");
			if (tpath == null)
				tpath = "file:///C:/";
			} else {
				tpath = "file:///"+"MyDocs/.sounds/";
			}
			final String path = tpath +  String.valueOf(prevprevsongindex) +"vikaMusicCache.mp3";
			System.out.println(path);
			try {
				outConn = (FileConnection) Connector.open(path, Connector.READ_WRITE);
			} catch (Exception e2) {}
				// TODO Auto-generated catch block
				//e2.printStackTrace();
				try {
					outConn.delete();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					outConn.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//}
			try {
				if (outConn.exists()) {
					outConn.delete();
					//outConn.create();
				} else {
					//outConn.create();
				}
			} catch (IOException e) {
				//e.printStackTrace();
			} catch (Exception e) {
				//printStackTrace();
			} 
			prevprevsongindex=prevsongindex;
			prevsongindex=current;*/
			loadTrack();
			VikaTouch.needstoRedraw=true;
		}
	}

	public void prev() {
		if (voice != null)
			return;
		if (controlsBlocked)
			return;
		if (inSeekMode) {
			seekTime -= 5000000L;
			VikaTouch.needstoRedraw=true;
		} else {
			if (!isReady) {
				stop = true;
			}

			if (player != null) {
				if (player.getState() == 400) {
					try {
						player.stop();
					} catch (MediaException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (player.getState() == 300) {
					player.deallocate();
				}

				if (player.getState() == 200 || player.getState() == 100) {
					player.close();
				}
			}
			try {
				player.stop();
			} catch (Exception e) {
			}

			if(loader != null) {
				try {
					loader.interrupt();
				} catch (Throwable ee) {}
			}


			if (netInStream != null) {
				try {
					netInStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (inConn != null) {
				try {
					inConn.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (outStream != null) {
				try {
					outStream.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			stop = false;
			try {
				//speedCount.interrupt();
				outStream.close();
			} catch (Exception e1) {}
			try {
				if (!isReady) {
					outConn.delete();
				}
				outConn.close();


			} catch (Exception e1) {}
			try {
				netInStream.close();
			} catch (Exception e1) {}
			try {
				inConn.close();
			} catch (Exception e1) {}



			if (!isReady) {
				try {
					outConn.delete();
					outConn.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			current--;
			if (current < 0)
				current = playlist.uiItems.size() - 1;
			loadTrack();
			VikaTouch.needstoRedraw=true;
		}
	}

	public void changeVolume(boolean up) {
		int v = Settings.playerVolume;
		if (up) {
			v += 10;
			if (v > 100)
				v = 100;
		} else {
			if (v <= 10) {
				v = 1;
			} else
				v -= 10;
		}
		try {
			((VolumeControl) player.getControl("VolumeControl")).setLevel(v);
		} catch (Exception e) {
		}
		Settings.playerVolume = (byte) v;
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
	}

	public void updateDrawData() {
		VikaTouch.needstoRedraw=true;
		this.serviceRepaints();
		long dur = 1;
		long curr = 0;
		if (isReady) {
			if (player == null)
				return;
			curr = inSeekMode ? seekTime : player.getMediaTime();
			//String temptime = time(curr / 1000000L);
			//if (time!=temptime) {
			time = time(curr / 1000000L);
			//temptime;
			VikaTouch.needstoRedraw=true;
			this.serviceRepaints();
			VikaTouch.needstoRedraw=true;
			//}
			dur = Settings.audioMode == Settings.AUDIO_PLAYONLINE
					? (voice == null ? getC().length : voice.size) * 1000000L
							: player.getDuration();
		}
		try {
			int dw = DisplayUtils.width;
			if (dw > DisplayUtils.height && showCover) {
				// альбом
				x1 = dw / 2 + PBMARGIN;
				x2 = dw - PBMARGIN;
				currX = dw / 2 + 60 + (int) ((dw / 2 - PBMARGIN * 2) * (inSeekMode ? seekTime : curr) / dur);
				currX2 = dw / 2 + 60 + (int) ((dw / 2 - PBMARGIN * 2) * buffered / 100);
			} else {
				// квадрат, портрет
				x1 = PBMARGIN;
				x2 = dw - PBMARGIN;
				currX = PBMARGIN + (int) ((dw - PBMARGIN * 2) * buffered / dur);
				currX2 = PBMARGIN + (int) ((dw - PBMARGIN * 2) * buffered / 100);
			}
		} catch (Exception e) {
		}
		VikaTouch.needstoRedraw=true;
		//this.repaint();
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
	}

	public void loadTrackInfo() {
		if (voice == null) {
			title = getC().name;
			artist = getC().artist;
			Font f = Font.getFont(0, 0, Font.SIZE_MEDIUM);
			titleW = f.stringWidth(title);
			artistW = f.stringWidth(artist);
		} else {
			title = TextLocal.inst.get("msg.attach.voice");
			artist = "";
			titleW = artistW = 0;
		}
	}

	public void onRotate() {
		resizeCover();
		VikaTouch.needstoRedraw=true;
		this.repaint();
		this.serviceRepaints();
		VikaTouch.needstoRedraw=true;
	}

	public void resizeCover() {
		if (coverOrig == null) {
			resizedCover = null;
			return;
		}
		int dw = DisplayUtils.width;
		if (dw > DisplayUtils.height) {
			// альбом
			resizedCover = VikaUtils.resize(coverOrig, dw / 2, dw / 2);
		} else {
			// квадрат, портрет
			resizedCover = VikaUtils.resize(coverOrig, dw, dw);
		}
	}

	public void getCover() throws InterruptedException {
		if (voice != null)
			return;
		if (DisplayUtils.height <= 220)
			return;
		if (title != null && Settings.loadITunesCovers) {
			String q = "http://vikamobile.ru:80/proxy.php?https://itunes.apple.com/search?term="
					+ URLDecoder.encode(title + " " + (artist == null ? "" : artist)) + "&country=ru&limit=1";
			try {
				String s = VikaUtils.download(q);
				// VikaTouch.sendLog(s);
				// VikaTouch.sendLog(q);

				JSONObject res = new JSONObject(s);
				if (res.optInt("resultsCount") == 0) {
					// VikaTouch.sendLog("Searching without artist");
					q = "http://vikamobile.ru:80/proxy.php?https://itunes.apple.com/search?term="
							+ URLDecoder.encode(title + " " + (artist == null ? "" : artist)) + "&country=ru&limit=1";
					s = VikaUtils.download(q);
					// VikaTouch.sendLog(s);
					// VikaTouch.sendLog(q);
					res = new JSONObject(s);
				}
				if (!res.getJSONArray("results").getJSONObject(0).isNull("artworkUrl100")) {
					s = VikaUtils.replace(res.getJSONArray("results").getJSONObject(0).getString("artworkUrl100"), "\\",
							"");
					if (!DisplayUtils.compact) {
						s = VikaUtils.replace(s, "100x100bb", "600x600bb");
					}
					s = VikaUtils.replace(s, "https", "http://vikamobile.ru:80/pr.php?https");
					coverOrig = VikaUtils.downloadImage(s);
				}
				if (coverOrig == null) {
					if (playlist.coverUrl != null)
						coverOrig = VikaUtils.downloadImage(playlist.coverUrl);
				}

			} catch (Exception var23) {
				if (playlist.coverUrl != null) {
					try {
						if (playlist.cover == null) {
							coverOrig = playlist.cover = VikaUtils.downloadImage(playlist.coverUrl);
						} else
							coverOrig = playlist.cover;
					} catch (IOException e) {
					}
				}
			}
		} else {
			if (playlist.coverUrl != null) {
				try {
					if (playlist.cover == null) {
						coverOrig = playlist.cover = VikaUtils.downloadImage(playlist.coverUrl);
					} else
						coverOrig = playlist.cover;
				} catch (IOException e) {
				}
			}
		}
		VikaTouch.needstoRedraw=true;
	}

	public static String time(long t) {
		int s = (int) (t % 60);
		long min = t / 60;

		//VikaTouch.canvas.serviceRepaints();
		return min + ":" + (s < 10 ? "0" : "") + s;

	}

	public void options() {
		int h = 50;
		OptionItem[] opts = new OptionItem[] {
				new OptionItem(this, TextLocal.inst.get("player.playlist"), IconsManager.MENU, -1, h),
				new OptionItem(this, TextLocal.inst.get("player.loop"),
						loop ? IconsManager.APPLY : IconsManager.REFRESH, 0, h),
				new OptionItem(this, TextLocal.inst.get("player.random"),
						random ? IconsManager.APPLY : IconsManager.PLAY, 1, h),
				new OptionItem(this, TextLocal.inst.get("player.seek"), IconsManager.SEARCH, 2, h),
				new OptionItem(this, TextLocal.inst.get("player.playagain"), IconsManager.BACK, 3, h),
				new OptionItem(this, TextLocal.inst.get("player.download"), IconsManager.DOWNLOAD, 4, h),
				new OptionItem(this, TextLocal.inst.get("player.troubleshooting"), IconsManager.SETTINGS, 5, h),
				new OptionItem(this, TextLocal.inst.get("player.hideapp"), IconsManager.CLOSE, 6, h), };
		VikaTouch.needstoRedraw=true;
		VikaTouch.popup(new AutoContextMenu(opts));
		VikaTouch.needstoRedraw=true;
	}

	public void troubleOptions() {
		int h = 60;
		OptionItem[] opts = new OptionItem[] {
				new OptionItem(this, TextLocal.inst.get("player.updatelinks"), IconsManager.REFRESH, 10, h),
				new OptionItem(this, TextLocal.inst.get("player.forcedreboot"), IconsManager.CLOSE, 11, h), };
		VikaTouch.needstoRedraw=true;
		VikaTouch.popup(new AutoContextMenu(opts));
		VikaTouch.needstoRedraw=true;
	}

	public AudioTrackItem getC() {
		try {
			return ((AudioTrackItem) playlist.uiItems.elementAt(current));
		} catch (RuntimeException e) {
			return new AudioTrackItem(); // fake item
		}
	}

	public String getMp3Link() {
		String turl = "";
		if (voice != null) {
			String s = voice.musUrl;
			//if (VikaTouch.mobilePlatform.indexOf("NokiaN97") > 1) {
			turl = s;
			//} else {
			// обычно УРЛ голоса в обработке не нуждается.
			//return s;
			//}
		} else {
			turl = getC().mp3;
		}
		//boolean https = false;
		boolean extra;

		/*if (Settings.loadMusicViaHttp == Settings.AUDIO_HTTP) {
			https = false;
		} else if (Settings.loadMusicViaHttp == Settings.AUDIO_HTTPS) {
			https = true;
		} */ /*else {
			if (VikaTouch.mobilePlatform.indexOf("NokiaN97") > 1) {
				https = false;
			} else if (!Settings.https) {
				https = false;
			} else {
				https = Settings.audioMode != Settings.AUDIO_PLAYONLINE;
			}
		}*/
		if (Settings.loadMusicWithKey == Settings.AUDIO_EXTRA) {
			extra = true;
		} else if (Settings.loadMusicWithKey == Settings.AUDIO_NOEXTRA) {
			extra = false;
		} else {
			if (VikaTouch.mobilePlatform.indexOf("NokiaN97") > 1) {
				extra = false;
			} else {
				extra = Settings.https;
			}
		}
		try {
			if (
					//	https ||
					Settings.https) {
				turl = VikaUtils.replace(turl, "http:", "https:");
				// turl = VikaUtils.replace(turl, "httpss", "https");
			} else {
				turl = VikaUtils.replace(turl, "https:", "http:");
			}
		} catch (Exception e) {
		}

		try {
			if (!extra) {
				turl = turl.substring(0, turl.indexOf("?"));
			}
		} catch (Exception e) {
		}

		return turl;
	}

	public static void launch(MusicScreen list, int track) {
		try {
			MusicPlayer.current = track;
			switch (Settings.audioMode) {
			case Settings.AUDIO_VLC:
				String mrl = ((AudioTrackItem) list.uiItems.elementAt(track)).mp3;

				System.out.println(mrl);
				if (mrl.indexOf("symbian.live") > -1) {
					mrl = "https://" + mrl.substring("http://vk-api-proxy.symbian.live/_/".length());
				}
				if (mrl.indexOf("vikamobile.ru") > -1) {
					mrl = "https://" + mrl.substring("http://vk-api-proxy.vikamobile.ru/_/".length());
				} 
				mrl = VikaUtils.replace(mrl, "https:///", "https://");
				String cmd = "vlc.exe \"" + mrl + "\"";
				boolean res = VikaTouch.appInst.platformRequest(cmd);
				if (!res) {
					VikaTouch.popup(new InfoPopup(
							"Эмулятор понял запрос, но отказался выполнить. Проверьте настройки системы.", null));
				}
				System.out.println(cmd);
				break; 
			case Settings.AUDIO_DOWNLOAD:
				VikaTouch.appInst.platformRequest(url
						///((AudioTrackItem) list.uiItems[track]).mp3
						);
				break;
			case Settings.AUDIO_SYSTEMPLAYER:
				VikaTouch.callSystemPlayer(((AudioTrackItem) list.uiItems.elementAt(track)).mp3);
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

	int timeY;
	private boolean showCover;

	public void draw(Graphics g) {
		try {
			this.serviceRepaints();
			int dw = DisplayUtils.width;
			int dh = DisplayUtils.height;
			int hdw = dw / 2;
			int textAnchor;
			showCover = Math.min(dw, dh) > 240;
			updateDrawData();

			if (dw != lastW) {
				onRotate();
				lastW = dw;
			}
			Font f = Font.getFont(0, 0, Font.SIZE_MEDIUM);
			g.setFont(f);
			ColorUtils.setcolor(g, 0);
			currTx -= 1;
			currAx -= 1;
			if (-currTx > (titleW / 2 + hdw))
				currTx = titleW / 2 + hdw;
			if (-currAx > (artistW / 2 + hdw))
				currAx = artistW / 2 + hdw;
			boolean tick = (System.currentTimeMillis() % 1000) < 500;
			if (dw > dh && showCover) {
				// альбом
				textAnchor = dw * 3 / 4;
				timeY = dh - 90;
				volumeY = 40;
				volumeX1 = hdw + 40;
				volumeX2 = dw - 40;
				if (!inSeekMode)
					g.drawImage(buttons[5], textAnchor - 125, dh - 50, 0);
				if ((voice == null || inSeekMode) && (!inSeekMode || tick))
					g.drawImage(buttons[0], textAnchor - 75, dh - 50, 0);
				g.drawImage(buttons[isPlaying ? 3 : 1], textAnchor - 25, dh - 50, 0);
				if ((voice == null || inSeekMode) && (!inSeekMode || tick))
					g.drawImage(buttons[2], textAnchor + 25, dh - 50, 0);
				if (!inSeekMode)
					g.drawImage(buttons[(backScreenIsPlaylist() ? 4 : 6)], textAnchor + 75, dh - 50, 0);

				g.drawString(artist, textAnchor + (artistW > hdw ? currAx : 0), dh / 2 - f.getHeight(),
						Graphics.HCENTER | Graphics.TOP);
				g.drawString(title, textAnchor + (titleW > hdw ? currTx : 0), dh / 2, Graphics.HCENTER | Graphics.TOP);
			} else {
				// портрет, квадрат
				textAnchor = hdw;
				timeY = dh - 90;
				volumeY = timeY - f.getHeight() * 4;
				volumeX1 = 50;
				volumeX2 = dw - 50;
				if (!inSeekMode)
					g.drawImage(buttons[5], hdw - 125, dh - 50, 0);
				if ((voice == null || inSeekMode) && (!inSeekMode || tick))
					g.drawImage(buttons[0], hdw - 75, dh - 50, 0);
				g.drawImage(buttons[isPlaying ? 3 : 1], hdw - 25, dh - 50, 0);
				if ((voice == null || inSeekMode) && (!inSeekMode || tick))
					g.drawImage(buttons[2], hdw + 25, dh - 50, 0);
				if (!inSeekMode)
					g.drawImage(buttons[(backScreenIsPlaylist() ? 4 : 6)], hdw + 75, dh - 50, 0);

				g.drawString(artist, textAnchor + (artistW > dw ? currAx : 0), timeY - f.getHeight() * 3 / 2,
						Graphics.HCENTER | Graphics.TOP);
				g.drawString(title, textAnchor + (titleW > dw ? currTx : 0), timeY - f.getHeight() * 5 / 2,
						Graphics.HCENTER | Graphics.TOP);
			}
			ColorUtils.setcolor(g, ColorUtils.MUSICCOLOR);
			g.drawRect(x1, timeY, x2 - x1, 10);
			if (isReady) {
				if(Settings.audioMode == Settings.AUDIO_ASYNC) {
					g.setGrayScale(200);
					g.fillRect(x1 + 2, timeY + 2, Math.min(currX2 - x1 - 4, x2 - x1 - 4), 6);
					ColorUtils.setcolor(g, ColorUtils.MUSICCOLOR);
				}
				g.fillRect(x1 + 2, timeY + 2, Math.min(currX - x1 - 4, x2 - x1 - 4), 6);
			} else {
				int t = (int) (System.currentTimeMillis() % 1000);
				int px1 = 0, px2 = 0;
				if (t < 500) {
					px1 = (x2 - ((x2 - x1) * t / 500)) + 2;
					px2 = x2 - 2;
				} else {
					px1 = x1 + 2;
					px2 = (x2 - ((x2 - x1) * (t - 500) / 500));
				}
				g.setGrayScale(200);
				g.fillRect(px1, timeY + 2, px2 - px1, 6);
				ColorUtils.setcolor(g, ColorUtils.MUSICCOLOR);
			}

			int volumeXc = volumeX1 + ((volumeX2 - volumeX1) * Settings.playerVolume / 100);
			ColorUtils.setcolor(g, ColorUtils.MUSICCOLOR);
			g.fillRect(volumeX1, volumeY, volumeXc - volumeX1, 6);
			g.setGrayScale(200);
			g.fillRect(volumeXc, volumeY, volumeX2 - volumeXc, 6);

			ColorUtils.setcolor(g, ColorUtils.MUSICCOLOR);
			f = Font.getFont(0, 0, Font.SIZE_SMALL);
			g.setFont(f);
			g.drawString(time, x1 - 4, timeY - 6, Graphics.TOP | Graphics.RIGHT);
			g.drawString(totalTime, x2 + 4, timeY - 6, Graphics.TOP | Graphics.LEFT);
			g.drawString("Vol", volumeX1 - 4, volumeY + 3 - f.getHeight() / 2, Graphics.TOP | Graphics.RIGHT);
			g.drawString(String.valueOf(Settings.playerVolume), volumeX2 + 4, volumeY + 3 - f.getHeight() / 2,
					Graphics.TOP | Graphics.LEFT);

			if (showCover) {
				// cover
				int coverY = (dw > dh) ? ((dh - hdw) / 2) : 0;
				if (resizedCover != null) {
					g.drawImage(resizedCover, 0, coverY, 0);
				} else {
					int s = (dw > dh) ? hdw : dw;
					g.setGrayScale(200);
					g.fillRect(0, coverY, s, s);
					if (defaultCover == null) {
						defaultCover = Image.createImage("/emptyCover.png");
					}
					g.drawImage(defaultCover, s / 2, (dw > dh) ? (dh / 2) : hdw, Graphics.HCENTER | Graphics.VCENTER);
				}
			}
		} catch (Exception e) {

		}
	}

	public void tap(int x, int y, int time) {
		int dw = DisplayUtils.width;
		int dh = DisplayUtils.height;
		int hdw = dw / 2;
		if (y >= timeY - 4 && y <= timeY + 14) {
			tapSeek(x);
			return;
		}
		if (y >= volumeY - 6 && y <= volumeY + 12) {
			x -= volumeX1;
			float v = (float) x / (volumeX2 - volumeX1);
			int vi = (int) (v * 100);
			if (vi < 0)
				vi = 0;
			if (vi > 100)
				vi = 100;
			Settings.playerVolume = (byte) vi;
			((VolumeControl) player.getControl("VolumeControl")).setLevel(Settings.playerVolume);
			return;
		}
		if (y < dh - 50)
			return;
		int anchor;
		if (dw > dh && showCover) {
			// альбом
			anchor = dw * 3 / 4;
		} else {
			// портрет, квадрат
			anchor = hdw;
		}
		if (x > anchor + 125) {
			return;
		} else if (x > anchor + 75) {
			if (controlsBlocked)
				return;
			if (!inSeekMode) {
				if (voice != null)
					pause();
				VikaTouch.inst.cmdsInst.command(14, this);
			}
		} else if (x > anchor + 25) {
			next();
		} else if (x > anchor - 25) {
			if (isPlaying) {
				pause();
			} else {
				play();
			}
		} else if (x > anchor - 75) {
			prev();
		} else if (x > anchor - 125) {
			if (!inSeekMode)
				options();
		}

	}

	public void press(int key) {
		if (key == -6) {
			if (!inSeekMode)
				options();
		} else if (key == -3) {
			if (voice != null) {
				try {
					player.setMediaTime(1);
					player.start();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			} else {
				prev();
			}
		} else if (key == -5) {
			if (isPlaying) {
				pause();
			} else {
				play();
			}
		} else if (key == -4) {
			next();
		} else if (key == -1) {
			changeVolume(true);
		} else if (key == -2) {
			changeVolume(false);
		} else if (key == -7 /* || key == */) // TODO вспомнить кнопку "назад" на SE
		{
			if (controlsBlocked)
				return;
			if (!inSeekMode) {
				if (voice != null)
					pause();
				VikaTouch.inst.cmdsInst.command(14, this);
			}
		}
	}

	public void drawHUD(Graphics g) {

	}

	public boolean backScreenIsPlaylist() {
		return backScreen == playlist;
	}

	public void onMenuItemPress(int i) {
		if (i == 0) {
			loop = !loop;
		} else if (i == 1) {
			random = !random;
		} else if (i == 2) {
			if (controlsBlocked)
				return;
			if (Settings.audioMode == Settings.AUDIO_PLAYONLINE || voice != null) {
				VikaTouch.popup(new InfoPopup(TextLocal.inst.get("player.rewindimpossible"), null));
			} else if (isReady) {
				seekTime = player.getMediaTime();
				if (isPlaying)
					pause();
				inSeekMode = true;
			}
		} else if (i == 3) {
			if (controlsBlocked)
				return;
			if (!isReady)
				return;
			if (Settings.audioMode == Settings.AUDIO_PLAYONLINE || voice != null) {
				try {
					try {
						closePlayer();
					} catch (Exception e2) {
					}
					player = Manager.createPlayer(Connector.openInputStream(url), "audio/mpeg");
					player.start();
					isReady = true;
					isPlaying = true;
					try {
						((VolumeControl) player.getControl("VolumeControl")).setLevel(100);
					} catch (Exception e) {
					}
					totalTime = time(getC().length);
					stop = false;
					player.addPlayerListener(inst);
				} catch (Exception e) {

				}
			} else {
				try {
					player.stop();
					Thread.sleep(500);
				} catch (Exception e) {
				}
				try {
					player.setMediaTime(1);
					player.start();
				} catch (MediaException e) {
				}
			}
		} else if (i == 4) {
			if (controlsBlocked)
				return;
			try {
				VikaTouch.appInst.platformRequest(url);
			} catch (ConnectionNotFoundException e) {
				e.printStackTrace();
			}
		} else if (i == 5) {
			troubleOptions();
		} else if (i == 6) {
			VikaTouch.appInst.notifyPaused();
			Display.getDisplay(VikaTouch.appInst).setCurrent(null);
		} else if (i == -1) {
			if (voice != null) {
				if (controlsBlocked)
					return;
				if (backScreenIsPlaylist()) {
					VikaTouch.inst.cmdsInst.command(14, this);
				} else
					VikaTouch.setDisplay(playlist, 1);
			}
		} else if (i == 10) {
			if (controlsBlocked)
				return;
			playlist.reload(true);
		} else if (i == 11) {
			try {
				if (loader != null && loader.isAlive()) {
					try {
						loader.interrupt();
					} catch (Throwable ee) {}
				}
			} catch (Exception e) {
			}
			try {
				closePlayer();
				Thread.sleep(500);
			} catch (Exception e) {
			}
			controlsBlocked = false;
			isReady = true;
			inSeekMode = false;
			isPlaying = false;
			stop = false;
			loadTrack();
		}
		VikaTouch.needstoRedraw=true;
	}

	public void onMenuItemOption(int i) {
	}

	public void playerUpdate(Player pl, String event, Object data) {
		//System.out.println(event);
		updateDrawData();
		VikaTouch.needstoRedraw=true;
		//VikaTouch.canvas.repaint();
		VikaTouch.canvas.serviceRepaints();
		//this.repaint();
		//this.serviceRepaints();
		if (event == END_OF_MEDIA) {
			System.out.println("end of media");
			if (loop) {
				try {
					player.stop();
					player.close();
					player.deallocate();

				} catch (MediaException e) {
				}
				if (Settings.audioMode == Settings.AUDIO_LOADANDPLAY) {
					try {
						closePlayer();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						player = Manager.createPlayer(Connector.openInputStream(System.getProperty("fileconn.dir.music") + "vikaMusicCache.mp3"), "audio/mpeg");
						player.addPlayerListener(inst);
						player.realize();
						VikaTouch.needstoRedraw=true;
						//VikaTouch.canvas.repaint();
						VikaTouch.canvas.serviceRepaints();
						//#vyfa123809!
					} catch (Exception e) {
						e.printStackTrace();
						VikaTouch.popup(new InfoPopup("Player creating error", null));
						return;
					}
					try {
						player.start();
						((VolumeControl) player.getControl("VolumeControl")).setLevel(100);
						VikaTouch.needstoRedraw=true;
						//VikaTouch.canvas.repaint();
						VikaTouch.canvas.serviceRepaints();
					} catch (MediaException e) {
						e.printStackTrace();
						VikaTouch.popup(new InfoPopup("Player running error", null));
						return;
					}
				} else {
					try {
						player.setMediaTime(1);
						player.start();
					} catch (MediaException e) {
						e.printStackTrace();
					}
				}
			} else {

				next();
			}
			VikaTouch.needstoRedraw=true;
		} else if (event == ERROR) {
			System.out.println("event error");
			String err = data.toString();
			if (err.indexOf("-3") > -1)
				return;
			if (err.indexOf("-2") > -1)
				err = TextLocal.inst.get("player.internalerror");
			VikaTouch.popup(new InfoPopup(err, null, TextLocal.inst.get("player.playererror"), null));
			VikaTouch.needstoRedraw=true;
		} else {
			VikaTouch.needstoRedraw=true;
			//VikaTouch.canvas.repaint();
			VikaTouch.canvas.serviceRepaints();
			System.out.println("unknown event!! " + event + " " + data);
		}
	}
	
	public void bufferedPercent(float percent) {
		buffered = (int) percent;
	}

	public void bufferDone(int size) {
		buffered = 100;
	}

	public void deallocated() {
		buffered = 0;
	}

	public void positionReset() {
	}
}
