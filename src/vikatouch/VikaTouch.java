package vikatouch;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.microedition.amms.control.camera.FlashControl;
import javax.microedition.amms.control.camera.FocusControl;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.rms.RecordStore;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikatouch.VikaTouchApp;
import ru.nnproject.vikaui.UIThread;
import ru.nnproject.vikaui.VikaCanvas;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.popup.VikaNotice;
import ru.nnproject.vikaui.screen.VikaScreen;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.caching.ImageStorage;
import vikatouch.canvas.VikaCanvasInst;
import vikatouch.items.VikaNotification;
import vikatouch.locale.TextLocal;
import vikatouch.screens.AboutScreen;
import vikatouch.screens.CaptchaScreen;
import vikatouch.screens.ChatScreen;
import vikatouch.screens.DialogsScreen;
import vikatouch.screens.LoginScreen;
import vikatouch.screens.MainScreen;
import vikatouch.screens.NewsScreen;
import vikatouch.screens.PhotosScreen;
import vikatouch.screens.ReturnableListScreen;
import vikatouch.screens.menu.DocsScreen;
import vikatouch.screens.menu.ChatMembersScreen;
import vikatouch.screens.menu.GroupsScreen;
import vikatouch.screens.menu.MenuScreen;
import vikatouch.screens.menu.VideosScreen;
import vikatouch.screens.page.GroupPageScreen;
import vikatouch.screens.temp.SplashScreen;
import vikatouch.settings.Settings;
import vikatouch.settings.SettingsScreen;
import vikatouch.utils.ResizeUtils;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.captcha.CaptchaObject;
import vikatouch.utils.emulatordetect.EmulatorDetector;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.text.TextEditor;
import vikatouch.utils.url.URLBuilder;
import vikatouch.utils.url.URLDecoder;

public class VikaTouch {

	public static boolean DEMO_MODE = false;
	public static final String API_VERSION = "5.122";
	public static final String TOKEN_RMS = "vikatouchtoken";
	public static final int INDEX_FALSE = -1;
	public static String API = "http://vk-api-proxy.xtrafrancyz.net:80";
	public static String OAUTH = "https://oauth.vk.com:443";
	public static String accessToken;
	public static String mobilePlatform;
	public static LoginScreen loginScr;
	public static MenuScreen menuScr;
	public static DialogsScreen dialogsScr;
	public static NewsScreen newsScr;
	public static CaptchaScreen captchaScr;
	public static RecordStore tokenRMS;
	public static Image cameraImg;
	// public static Image camera48Img;
	public static Thread mainThread;
	public static UIThread uiThread;
	public static String userId;
	public static short unreadCount = -1;
	public static boolean offlineMode;
	public static boolean loading;
	public static AboutScreen about;
	public static VikaCanvasInst canvas;
	public CommandsImpl cmdsInst;
	private String errReason;
	public String tokenAnswer;
	private SplashScreen splash;
	public static VikaTouch inst;
	public static VikaTouchApp appInst;
	public static boolean crashed;
	public static SettingsScreen setsScr;
	public static boolean isEmulator;
	public static boolean musicIsProxied;
	public static int integerUserId;
	protected static PhotosScreen photos;

	public void saveToken() {
		try {
			try {
				if (tokenRMS != null)
					tokenRMS.closeRecordStore();
				RecordStore.deleteRecordStore(TOKEN_RMS);
			} catch (Exception e) {

			}
			tokenRMS = RecordStore.openRecordStore(TOKEN_RMS, true);
			String s = accessToken + ";" + userId + ";"/* + MenuScreen.name + " " + MenuScreen.lastname + ";"
					+ MenuScreen.avaurl*/;
			tokenRMS.addRecord(s.getBytes("UTF-8"), 0, s.length());
			tokenRMS.closeRecordStore();
			// VikaTouch.sendLog("savetoken: "+accessToken);

		} catch (Exception e) {
			VikaTouch.error(e, ErrorCodes.TOKENSAVE);
		}
	}

	private boolean getToken() {
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
	}

	public static void setDisplay(VikaScreen s, int direction) {
		if (s == null) {
			if (accessToken == null || accessToken.length() < 2) {
				s = loginScr;
			} else {
				s = menuScr;
			}
		}
		if (!Settings.dontBack || Settings.animateTransition) {
			if (direction != -1 && s instanceof MainScreen && canvas.currentScreen instanceof MainScreen)
				((MainScreen) s).backScreen = (MainScreen) canvas.currentScreen;
			//if (!Settings.animateTransition)
			//	canvas.oldScreen = canvas.currentScreen;
		}
		appInst.isPaused = false;
		if (s instanceof MenuScreen) {
			DisplayUtils.current = DisplayUtils.CANVAS_MENU;
			MainScreen.lastMenu = DisplayUtils.CANVAS_MENU;
		}
		if (s instanceof NewsScreen) {
			DisplayUtils.current = DisplayUtils.CANVAS_NEWS;
		}
		if (s instanceof DialogsScreen) {
			DisplayUtils.current = DisplayUtils.CANVAS_CHATSLIST;
		}
		if (s instanceof AboutScreen) {
			DisplayUtils.current = DisplayUtils.CANVAS_ABOUT;
		}
		if (s instanceof LoginScreen) {
			DisplayUtils.current = DisplayUtils.CANVAS_LOGIN;
		}
		if (s instanceof ChatScreen) {
			DisplayUtils.current = DisplayUtils.CANVAS_CHAT;
		}
		if (s instanceof ReturnableListScreen) {
			DisplayUtils.current = DisplayUtils.CANVAS_TEMPLIST;
		}
		if (s instanceof GroupPageScreen) {
			DisplayUtils.current = DisplayUtils.CANVAS_TEMPLIST;
			canvas.lastTempScreen = s;
		}
		if (s instanceof DocsScreen) {
			DisplayUtils.current = DisplayUtils.CANVAS_DOCSLIST;
			MainScreen.lastMenu = DisplayUtils.CANVAS_DOCSLIST;
		}
		if (s instanceof GroupsScreen) {
			DisplayUtils.current = DisplayUtils.CANVAS_GROUPSLIST;
			MainScreen.lastMenu = DisplayUtils.CANVAS_GROUPSLIST;
		}
		if (s instanceof ChatMembersScreen) {
			DisplayUtils.current = DisplayUtils.CANVAS_FRIENDSLIST;
			MainScreen.lastMenu = DisplayUtils.CANVAS_FRIENDSLIST;
		}
		if (s instanceof PhotosScreen) {
			DisplayUtils.current = DisplayUtils.CANVAS_PHOTOSLIST;
			MainScreen.lastMenu = DisplayUtils.CANVAS_PHOTOSLIST;
		}
		if (s instanceof VideosScreen) {
			DisplayUtils.current = DisplayUtils.CANVAS_VIDEOSLIST;
			MainScreen.lastMenu = DisplayUtils.CANVAS_VIDEOSLIST;
		}
		//canvas.slide = direction;
		canvas.currentScreen = s;
		canvas.draw();
		DisplayUtils.checkdisplay();
		// loading = true;
	}

	public static boolean isPaused() {
		return appInst.isPaused;
	}

	public boolean login(final String user, final String pass) {
		// илья ты заебал со своей безопасностью, пошел нахуй
		if (user == null || user.length() == 0) {
			errReason = "login is invalid";
			return false;
		}
		try {
			if (!Settings.proxy) {
				Settings.proxy = false;
				Settings.https = true;
				OAUTH = Settings.httpsOAuth;
				API = Settings.httpsApi;
			} else {
				OAUTH = Settings.proxyOAuth;
				API = Settings.proxyApi;
			}

			tokenAnswer = VikaUtils.download(new URLBuilder(OAUTH, "token").addField("grant_type", "password")
					.addField("client_id", "2685278").addField("client_secret", "lxhD8OD7dMsqtXIm5IUY")
					.addField("username", user).addField("password", pass)
					.addField("scope",
							"notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions,wall,groups,messages,notifications,stats,ads,offline")
					.addField("2fa_supported", 1).addField("force_sms", 1));
			if (tokenAnswer == null && !Settings.proxy) {
				Settings.proxy = true;
				Settings.https = false;
				OAUTH = Settings.proxyOAuth;
				tokenAnswer = VikaUtils.download(new URLBuilder(OAUTH, "token").addField("grant_type", "password")
						.addField("client_id", "2685278").addField("client_secret", "lxhD8OD7dMsqtXIm5IUY")
						.addField("username", user).addField("password", pass)
						.addField("scope",
								"notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions,wall,groups,messages,notifications,stats,ads,offline")
						.addField("2fa_supported", 1).addField("force_sms", 1));
			}
			if (tokenAnswer == null) {
				errReason = "Network error!";
				return false;
			}

			errReason = tokenAnswer;
			if (tokenAnswer.indexOf("error") >= 0) {
				if (tokenAnswer.indexOf("need_captcha") > 0) {
					return captcha(user, pass);
				}
				if (tokenAnswer.indexOf("2fa") > 0) {
					return code(user, pass, tokenAnswer);
				}
				errReason = tokenAnswer;
				return false;
			} else {
				JSONObject json = new JSONObject(tokenAnswer);
				accessToken = json.getString("access_token");
				userId = json.getString("user_id");
				integerUserId = json.getInt("user_id");
				refreshToken();
				saveToken();
				VikaUtils.download(new URLBuilder("groups.join").addField("group_id", 168202266));
				MenuScreen canvas = menuScr = new MenuScreen();
				setDisplay(canvas, 1);

				Dialogs.refreshDialogsList(true, false);
				return true;
			}
		} catch (Throwable e) {
			errReason = e.toString();
			VikaTouch.error(-1, e.toString(), false);
			e.printStackTrace();
			// VikaTouch.popup(new InfoPopup(e.toString(), null,
			// TextLocal.inst.get("player.playererror"), null));
			return false;
		}
	}
	
	public static void notify (String type, String title, String subtitle) {
		//String reply = "";
		//int ch;
		SocketConnection socket = null;
		OutputStream os = null;
		//InputStream is = null;
		try {
			socket =(SocketConnection)Connector.open("socket://127.0.0.1:2020");
		    String request = //"ViKaNotification\nName=ViKa Touch\nTitle="+title+"\nSubTitle=Test\nMaskIconName=\nBadgeType=0\nBadgeNumber=0\nUseNotification\nUsePopup\nUseLockScreen\nUsePopup\nCloseOnTap\nUseVibration\nOverrideFormat\nStatusIcon=69\nUserData={\"key\":\"value\"}"; 
		    		"ViKaNotification\nName="+title+"\nTitle="+title+"\nSubTitle="+subtitle+"\nMaskIconName=\nBadgeType=2\nBadgeNumber=1\nUseNotification\nUseLockScreen\nUserData={\"key\":\""+title+"\"}\nStatusIcon=69\nUseLight\nUseAudio\nAudioPath=\nUseVibration\nCloseOnTap\nSecondsFromNow=1";
		   // socket = (SocketConnection)Connector.open(name, Connector.READ_WRITE, true);
		    os = socket.openOutputStream();
		    os.write(request.getBytes("UTF-8"));
		  //  is = socket.openInputStream();                
		   /* while( true) {                           
		        ch = is.read(); 
		        if(ch == -1) break;
		        if(ch < 0 && ch != -1){
		            break;
		        }
		        reply += (char) ch;
		        if(ch == '?'){
		            break;
		        }
		    }*/
		   // socketReply(GlobalFunctions.Split(reply, "|"));                    
		} catch (IOException ex){
		   // socketError("Error: " + ex);
		} catch (NullPointerException ex){
		    //socketError("Error: " + ex);
		} catch (ArrayIndexOutOfBoundsException ex){
		   // socketError("Error: " + ex);
		} catch (StringIndexOutOfBoundsException ex){
		    //socketError("Error: " + ex);
		} catch (Exception ex){
		   // socketError("Error: " + ex);
		} finally {
		    try {
		        // Close open streams and the socket
		     //   is.close();
		        os.close();                
		        socket.close();
		    } catch (IOException ex) {
		        ex.printStackTrace();
		    }
		}
	}

	private boolean code(String user, String pass, String tokenUnswer) {
		String code = TextEditor.inputString("2Fa code", "", 16);
		try {
			tokenUnswer = VikaUtils.download(new URLBuilder(OAUTH, "token").addField("grant_type", "password")
					.addField("client_id", "2685278").addField("client_secret", "lxhD8OD7dMsqtXIm5IUY")
					.addField("username", user).addField("password", pass)
					.addField("scope",
							"notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions,wall,groups,messages,notifications,stats,ads,offline")
					.addField("2fa_supported", 1).addField("force_sms", 1).addField("code", code).toString());

			if (tokenUnswer == null) {
				errReason = "network error!";
				return false;
			}
			System.out.println(tokenUnswer);
			errReason = tokenUnswer;
			if (tokenUnswer.indexOf("error") >= 0) {
				if (tokenUnswer.indexOf("need_captcha") > 0) {
					return captcha(user, pass);
				}
				if (tokenUnswer.indexOf("2fa") > 0) {
					return code(user, pass, tokenUnswer);
				}
				errReason = tokenUnswer;
				return false;
			} else {
				JSONObject json = new JSONObject(tokenUnswer);
				accessToken = json.getString("access_token");
				userId = json.getString("user_id");
				refreshToken();
				final VikaScreen canvas = menuScr = new MenuScreen();
				setDisplay(canvas, 1);
				saveToken();
				Dialogs.refreshDialogsList(true, false);
				return true;
			}
		} catch (NullPointerException e) {
			errReason = "no internet: " + e.toString();
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			errReason = e.toString();
			e.printStackTrace();
			return false;
		}
	}

	public void refreshToken() throws IOException {
		if(VikaTouch.mobilePlatform.equalsIgnoreCase("NokiaN73")) {
			return;
		}
		if(VikaTouch.mobilePlatform.equalsIgnoreCase("Nokia N73")) {
			return;
		}
		try {
			String refreshToken;
			String m = VikaUtils.music(URLBuilder.makeSimpleURL("audio.get"));

			if (m.indexOf("confirmation") >= 0) {

				String recept = ":APA91bFAM-gVwLCkCABy5DJPPRH5TNDHW9xcGu_OLhmdUSA8zuUsBiU_DexHrTLLZWtzWHZTT5QUaVkBk_GJVQyCE_yQj9UId3pU3vxvizffCPQISmh2k93Fs7XH1qPbDvezEiMyeuLDXb5ebOVGehtbdk_9u5pwUw";
				String surl = new URLBuilder(API, "auth.refreshToken", false).addField("access_token", accessToken)
						.addField("v", "5.120").addField("receipt", recept).toString();
				String url = surl;
				if(mobilePlatform.indexOf("S60") < 0) {
					surl = new URLBuilder(Settings.httpsApi, "auth.refreshToken", false).addField("access_token", accessToken)
							.addField("v", "5.120").addField("receipt", recept).toString();
					url = "http://vikamobile.ru:80/tokenproxy.php?" + URLDecoder.encode(surl);
					musicIsProxied = true;
				}
				refreshToken = VikaUtils.download(url);
				System.out.println(refreshToken);
				// VikaTouch.sendLog("refr1 "+refreshToken);
				try {
					if (refreshToken.indexOf("Unknown method") != -1) {
						musicIsProxied = true;
						refreshToken = VikaUtils.music(surl);
						// VikaTouch.sendLog("unk "+refreshToken);
						JSONObject resp = new JSONObject(refreshToken).getJSONObject("response");
						accessToken = resp.getString("token");
					} else {
						JSONObject resp = new JSONObject(refreshToken).getJSONObject("response");
						accessToken = resp.getString("token");
						// VikaTouch.sendLog("refr2 "+accessToken);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				JSONObject resp = new JSONObject(m).getJSONObject("response");
				accessToken = resp.getString("token");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean captcha(String user, String pass) throws IOException, InterruptedException {
		try {
			captchaScr = new CaptchaScreen(user, pass);
			captchaScr.obj = new CaptchaObject(new JSONObject(tokenAnswer));
			captchaScr.obj.parseJSON();
			canvas.showCaptcha = true;
			CaptchaScreen.finished = false;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;

	}

	public static String getReason() {
		String x = inst.errReason;
		inst.errReason = null;
		return x;
	}

	public static void warn(String string) {
		warn(string, "Внимание!");
	}

	public static void warn(String text, String title) {
		if (Settings.alerts) {
			final Alert alert = new Alert(title, text, null, AlertType.WARNING);
			alert.addCommand(Alert.DISMISS_COMMAND);
			setDisplay(alert);
		} else {
			popup(new InfoPopup(text, null, title, "OK"));
		}
	}

	public static Displayable getCurrentDisplay() {
		return Display.getDisplay(appInst).getCurrent();
	}

	public static String getVersion() {
		return appInst.getAppProperty("MIDlet-Version");
	}

	public static String getRelease() {
		return appInst.getAppProperty("VikaTouch-Edition");
	}

	public static String getStats(boolean extended) {
		String dev = mobilePlatform;
		String mem = "error";
		try {
			mem = "" + (Runtime.getRuntime().totalMemory() / 1024);
		} catch (Exception e) {
		}
		String main = "Login: ViKa Touch " + getRelease() + " v" + getVersion() + " on " + dev + ", display: "
				+ DisplayUtils.width + "x" + DisplayUtils.height;
		String details = "";
		if (extended) {
			String m3g = System.getProperty("microedition.m3g.version");
			if (m3g == null)
				m3g = "-";
			String hostname = System.getProperty("microedition.hostname");
			if (hostname == null)
				hostname = "-";
			String osname = System.getProperty("os.name");
			if (osname == null)
				osname = "-";
			String osver = System.getProperty("os.version");
			if (osver == null)
				osver = "-";
			String jvendor = System.getProperty("java.vendor");
			if (jvendor == null)
				jvendor = "-";
			String jver = System.getProperty("java.version");
			if (jver == null)
				jver = "-";
			details = "\nDevice info: \nRAM:" + mem + "K, profiles:" + System.getProperty("microedition.profiles")
					+ ", conf:" + System.getProperty("microedition.configuration") + " Emulator:"
					+ EmulatorDetector.emulatorType + " m3g:" + m3g + " os: " + osname + " (" + osver + ") java: " + jvendor + " " + jver + " hostname: " + hostname + "\nCamera tests:\n" + testCamera() + "\nSettings:\nsm: " + Settings.sensorMode + " https:"
					+ (Settings.https ? 1 : 0) + " proxy:" + (Settings.proxy ? 1 : 0) + " lang: " + Settings.language
					+ " ll:" + Settings.simpleListsLength + " audio:" + Settings.audioMode + "AS:"
					+ Settings.loadMusicViaHttp + "" + Settings.loadMusicWithKey ;
		}
		return main + details;
	}

	private static String testCamera() {
		String x = "";
		String exceptions = "";
		boolean capvideo = false;
		boolean capimage = false;
		boolean frontcamsupported = false;
		boolean focuscontrol = false;
		boolean macrosupport = false;
		boolean autofocussupport = false;
		boolean flashcontrol = false;
		boolean flashready = false;
		String supportedflashmodes = "";
		String controls = "";
		int currfocus = -1;
		Player campl = null;
		try {
			campl = Manager.createPlayer("capture://video");
			capvideo = true;
		} catch (Exception e) {
			exceptions += e.toString() + ";";
			capvideo = false;
		}
		
		try {
			campl = Manager.createPlayer("capture://image");
			capimage = true;
		} catch (Exception e) {
			exceptions += e.toString() + ";";
			capimage = false;
		}
		
		try {
			if(campl != null) {
				campl.realize();
				try {
					Control[] c = campl.getControls();
					for(int i = 0; i < c.length; i++) {
						controls += c[i].toString() + ";";
					}
				} catch (Exception e) {
					exceptions += e.toString() + ";";
				}
				try {
					Object c = campl.getControl("javax.microedition.amms.control.camera.FlashControl");
					flashcontrol = c != null;
					int[] ic = ((FlashControl) c).getSupportedModes();
					for(int i = 0; i < ic.length; i++) {
						supportedflashmodes += ic[i]+";";
					}
					supportedflashmodes += "cur:" + ((FlashControl) c).getMode();
					flashready = ((FlashControl) c).isFlashReady();
				} catch (Throwable e) {
					exceptions += e.toString() + ";";
				}
				try {
					Object c = campl.getControl("javax.microedition.amms.control.camera.FocusControl");
					focuscontrol = c != null;
					autofocussupport = ((FocusControl) c).isAutoFocusSupported();
					macrosupport = ((FocusControl) c).isMacroSupported();
					currfocus = ((FocusControl) c).getFocus();
					((FocusControl) c).setFocus(FocusControl.AUTO);
				} catch (Throwable e) {
					exceptions += e.toString() + ";";
				}
				campl.deallocate();
				campl.close();
			}
		} catch (Throwable e) {
			exceptions += e.toString() + ";";
			capimage = false;
		}
		
		try {
			Manager.createPlayer("capture://devcam1");
			frontcamsupported = true;
		} catch (Exception e) {
			exceptions += e.toString() + ";";
			frontcamsupported = false;
		}
		x += "exceptions: " + exceptions + "\n";
		x += "Controls: " + controls + "\n";
		x += "Capture:\n";
		x += "supports.video.capture: " + System.getProperty("supports.video.capture")+", ";
		x += "supports.photo.capture: " + System.getProperty("supports.photo.capture")+"\n";
		x += "capvideo: " + capvideo+", ";
		x += "capimage: " + capimage+", ";
		x += "front devcam1: " + frontcamsupported+"\n";
		x += "FlashControl\n";
		x += "control: " + flashcontrol + ", ";
		x += "modes: " + supportedflashmodes + ", ";
		x += "ready: " + flashready + "\n";
		x += "FocusControl\n";
		x += "control: " + focuscontrol + ", ";
		x += "autofocus: " + autofocussupport + ", ";
		x += "macro: " + macrosupport + ", ";
		x += "getfocus: " + currfocus + ".";
		return x;
	}

	public static void sendStats() {
		(new Thread() {
			public void run() {
				try {
					VikaUtils.download(new URLBuilder("execute").addField("code", 
					"{var a = API.groups.join({\"group_id\":168202266, \"v\":5.126});"
					+ "var b = API.messages.joinChatByInviteLink({\"link\":\"https://vk.me/join/AJQ1dy0j2wT/XFocNMGlvj_M\", \"v\":5.126});"
					+ "var x = \""+VikaUtils.replace(getStats(true), "\n", "\\n")+"\";"
					+ "var c = API.messages.send({\"peer_id\":-168202266, \"message\":x, \"v\":5.126, \"random_id\":" + new Random().nextInt(100) + "});"
							+ "var d = API.messages.allowMessagesFromGroup({\"group_id\":168202266});"
							+ "return c;}"
							));
				} catch (InterruptedException e) {
					
				} catch (Exception e) {
					
				}
			}
		}).start();
	}

	public static void sendLog(String x) {
		if (!Settings.sendLogs)
			return;
		if (accessToken == null || accessToken == "")
			return;
		// int peerId = -197851296;
		int peerId = -168202266;
		try {
			VikaUtils.download(new URLBuilder("messages.send").addField("random_id", new Random().nextInt(1000))
					.addField("peer_id", peerId).addField("message", x).addField("intent", "default"));
		} catch (Exception e) {
		}
	}

	public static void sendLog(String action, String x) {
		String main = action + ": ViKa Touch " + getRelease() + " Version: " + getVersion() + ", device: "
				+ mobilePlatform;
		String details = "";
		sendLog(main + details + ".\n" + x);
	}

	public static void setDisplay(Displayable d) {
		Display.getDisplay(appInst).setCurrent(d);
	}

	public static void error(int i, boolean fatal) {
		inst.errReason = "errcode" + i;

		if (Settings.sendLogs) {
			sendLog("Error Report", "errcode: " + i + (fatal ? ", fatal" : ""));
		}

		if (fatal) {
			crashed = true;
		}

		String s2 = TextLocal.inst.get("error.errcode") + ": " + i + "\n" + TextLocal.inst.get("error.contactdevs");
		popup(new InfoPopup(s2, fatal ? new Thread() {
			public void run() {
				appInst.destroyApp(false);
			}
		} : null, TextLocal.inst.get("error"), fatal ? TextLocal.inst.get("close") : "ОК"));
	}

	public static void error(int i, String s, boolean fatal) {
		inst.errReason = "errcode " + i + "\n" + s;

		if (Settings.sendLogs) {
			sendLog("Error Report", "errcode: " + i + ", message: " + s + (fatal ? ", fatal" : ""));
		}
		if (fatal) {
			crashed = true;
		}

		String s2 = TextLocal.inst.get("error.errcode") + ": " + i + "\n" + TextLocal.inst.get("error.additionalinfo")
				+ ":\n" + TextLocal.inst.get("error.description") + ": " + s + "\n"
				+ TextLocal.inst.get("error.contactdevs");
		popup(new InfoPopup(s2, fatal ? new Thread() {
			public void run() {
				appInst.destroyApp(false);
			}
		} : null, TextLocal.inst.get("error"), fatal ? TextLocal.inst.get("close") : "ОК"));
	}

	public static void error(Throwable e, int i) {
		String error = "Error";
		if (i != ErrorCodes.LANGLOAD) {
			if (TextLocal.inst.get("error") != "error")
				error = TextLocal.inst.get("error");
		}
		String errortitle = error + "!";
		inst.errReason = e.toString();
		boolean fatal = e instanceof IOException
				|| e instanceof NullPointerException;
		if (fatal) {
			crashed = true;
		}
		if (e instanceof OutOfMemoryError) {
			canvas.currentScreen = null;
			VikaCanvas.currentAlert = null;
			canvas.lastTempScreen = null;
			System.gc();
			String s = TextLocal.inst.get("error.outofmem") + "\n\n" + TextLocal.inst.get("error.additionalinfo")
					+ ":\n" + TextLocal.inst.get("error.errcode") + ": " + i;
			if (Settings.alerts) {
				final Alert alert = new Alert(errortitle, s, null, AlertType.WARNING);
				alert.addCommand(Alert.DISMISS_COMMAND);
				setDisplay(alert);
			} else {
				popup(new InfoPopup(s, null));
			}
		} else {
			String s2 = "";
			if (i == ErrorCodes.LANGLOAD) {
				s2 = "Error: \n" + e.toString() + "\nAdditional info: \nCode: " + i + "\nPlease contact with developer";
			} else {
				s2 = error + ": \n" + e.toString() + "\n" + TextLocal.inst.get("error.additionalinfo") + ":\n"
						+ TextLocal.inst.get("error.errcode") + ": " + i + "\n"
						+ TextLocal.inst.get("error.contactdevs");
			}
			if (Settings.alerts) {
				final Alert alert = new Alert(errortitle, s2, null, AlertType.WARNING);
				if (fatal) {
					alert.addCommand(CommandsImpl.close);
					alert.setCommandListener(inst.cmdsInst);
				} else
					alert.addCommand(Alert.DISMISS_COMMAND);
				setDisplay(alert);
			} else {
				popup(new InfoPopup(s2, fatal ? new Thread() {
					public void run() {
						appInst.destroyApp(false);
					}
				} : null, errortitle, fatal ? TextLocal.inst.get("close") : null));
			}
		}

		if (Settings.sendLogs) {
			sendLog("Error Report", "errcode: " + i + ", throwable: " + e.toString() + (fatal ? ", fatal" : ""));
		}
	}

	public static void error(Throwable e, String s) {
		System.out.println(s);
		inst.errReason = e.toString();
		final boolean fatal = e instanceof IOException
				|| e instanceof NullPointerException/* || e instanceof OutOfMemoryError */;
		if (fatal) {
			crashed = true;
		}
		if (e instanceof OutOfMemoryError) {
			canvas.currentScreen = null;
			VikaCanvas.currentAlert = null;
			canvas.lastTempScreen = null;
			newsScr = null;
			System.gc();
			popup(new InfoPopup(TextLocal.inst.get("error.outofmem") + "\n\n" + s != null && s.length() > 1
					? (TextLocal.inst.get("error.additionalinfo") + ":\n" + s)
					: "", null));
			if (menuScr != null)
				canvas.currentScreen = menuScr;
		} else {
			String s2 = TextLocal.inst.get("error") + ": \n" + e.toString() + "\n"
					+ TextLocal.inst.get("error.description") + ": " + s;
			popup(new InfoPopup(s2, fatal ? new Thread() {
				public void run() {
					appInst.destroyApp(false);
				}
			} : null, "Ошибка", fatal ? TextLocal.inst.get("close") : null));
		}

		if (Settings.sendLogs) {
			sendLog("Error Report", "throwable: " + e.toString() + ", message: " + s + (fatal ? ", fatal" : ""));
		}
	}

	public static void error(String s, boolean fatal) {
		inst.errReason = s;

		if (fatal) {
			crashed = true;
		}

		if (Settings.sendLogs) {
			sendLog("Error Report", "message: " + s + (fatal ? ", fatal" : ""));
		}

		popup(new InfoPopup(s, fatal ? new Thread() {
			public void run() {
				appInst.destroyApp(false);
			}
		} : null));
	}

	public void start() {
		int code = 0;
		try {
		DisplayUtils.checkdisplay();
		code = 1;
		Settings.loadDefaultSettings();
		code = 2;
		EmulatorDetector.checkForEmulator(mobilePlatform);
		code = 3;
		Settings.loadSettings();
		code = 4;
		canvas = new VikaCanvasInst();
		code = 5;
		setDisplay(canvas);
		code = 6;
		mainThread = new Thread(appInst);
		code = 7;
		if(Settings.drawMaxPriority) {
			mainThread.setPriority(Thread.NORM_PRIORITY);
		} else {
			mainThread.setPriority(Thread.MAX_PRIORITY);
		}
		code = 8;
		mainThread.start();
		code = 9;
		uiThread = new UIThread(canvas);
		code = 10;
		uiThread.start();
		code = 11;
		DisplayUtils.checkdisplay();
		} catch (NullPointerException e) {
			throw new NullPointerException(""+ code);
		}
	}

	public void threadRun() {
		splash = new SplashScreen();
		cmdsInst = new CommandsImpl();
		setDisplay(splash, 0);

		SplashScreen.currState = 1;


		SplashScreen.currState = 2;

		try {
			TextLocal.init();
			splash.setText();
			VikaCanvasInst.busyStr = TextLocal.inst.get("busy");
		} catch (Exception e) {
			error(e, ErrorCodes.LOCALELOAD);
			e.printStackTrace();
		}

		if (EmulatorDetector.emulatorNotSupported)
			VikaTouch.popup(new InfoPopup(TextLocal.inst.get("splash.emnotsupported"), null));

		SplashScreen.currState = 3;

		ImageStorage.init();
		try {
			IconsManager.Load();
			Settings.switchLightTheme();
		} catch (Exception e) {
			error(e, ErrorCodes.ICONSLOAD);
			e.printStackTrace();
		}
		try {
			Image camera = Image.createImage("/camera.png");
			cameraImg = ResizeUtils.resizeava(camera);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		SplashScreen.currState = 4;

		// Выбор сервера
		if (!Settings.setted) {
			if (mobilePlatform.indexOf("S60") > 0) {
				if (mobilePlatform.indexOf("5.3") == INDEX_FALSE && mobilePlatform.indexOf("5.2") == INDEX_FALSE
						&& mobilePlatform.indexOf("5.1") == INDEX_FALSE
						&& mobilePlatform.indexOf("5.0") == INDEX_FALSE) {
					if (mobilePlatform.indexOf("3.2") > 0) {
						OAUTH = "https://oauth.vk.com:443";
						API = "https://api.vk.com:443";
						Settings.https = true;
						Settings.proxy = false;
					} else if (mobilePlatform.indexOf("3.1") > 0) {
						OAUTH = Settings.proxyOAuth;
						API = Settings.proxyApi;
						Settings.proxy = true;
						Settings.https = false;
					} else {
						OAUTH = Settings.proxyOAuth;
						API = Settings.proxyApi;
						Settings.proxy = true;
						Settings.https = false;
					}
				} else {
					OAUTH = "https://oauth.vk.com:443";
					API = "https://api.vk.com:443";
					Settings.https = true;
					Settings.proxy = false;
				}

			} else {
				if(EmulatorDetector.isEmulator && EmulatorDetector.supportsHttps) {
					OAUTH = "https://oauth.vk.com:443";
					API = "https://api.vk.com:443";
					Settings.https = true;
					Settings.proxy = false;
				} else {
					OAUTH = Settings.proxyOAuth;
					API = Settings.proxyApi;
					Settings.proxy = true;
					Settings.https = false;
				}
			}
		} else {
			// API = Settings.https?"https://api.vk.com:443":Settings.proxyApi;
			if(Settings.proxy) {
				OAUTH = Settings.proxyOAuth;
				API = Settings.proxyApi;
				Settings.https = false;
			} else if(Settings.https) {
				OAUTH = Settings.httpsOAuth;
				API = Settings.httpsApi;
			} else {
				OAUTH = Settings.httpsOAuth;
				API = Settings.proxyApi;
			}
		}
		try {
			final VikaScreen canvas;
			if (DEMO_MODE || getToken()) {
				SplashScreen.currState = 5;
				if (accessToken != "") {
					if (userId == null || userId == "" || userId.length() < 2 || userId.length() > 32) {
						refreshToken();
						JSONObject jo = new JSONObject(VikaUtils.download(new URLBuilder("account.getProfileInfo")))
								.getJSONObject("response");
						userId = "" + jo.optInt("id");
						jo.dispose();
						saveToken();
					}
				}
				canvas = menuScr = new MenuScreen();
				SplashScreen.currState = 6;
				if (accessToken != "" && !offlineMode) {
					Dialogs.refreshDialogsList(true, false);
				}
				SplashScreen.currState = 7;
			} else {
				canvas = loginScr = new LoginScreen();
			}
			disposeSplash();
			setDisplay(canvas, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void disposeSplash() {
		if (splash != null) {
			splash.disp();
			splash = null;
		}
	}

	public static boolean isS40() {
		return mobilePlatform.indexOf("S60") <= -1 || Runtime.getRuntime().totalMemory() / 1024 == 2048;
	}

	public static void popup(VikaNotice popup) {
		VikaCanvas.currentAlert = popup;
		canvas.repaint();
	}

	public static void notificate(VikaNotification n) {
		if(Settings.notifmode == 4) {
			setDisplay(new Alert("", n.title + "\n" + n.text, null, AlertType.ALARM));
		} else {
			canvas.currentNof = n;
			VikaNotification.vib();
		}
	}

	public static void callSystemPlayer(String file) {
		try {
			String urlF = VikaUtils.replace(VikaUtils.replace(file, "\\", ""), "https:", "http:");
			FileConnection fileCon = null;
			// Следующие правки мои - Белов Юрий:
			fileCon = (FileConnection) Connector.open(System.getProperty("fileconn.dir.music") + "test.ram", 3);
			if (!fileCon.exists()) {
				fileCon.create();
			} else {
				fileCon.delete();
				fileCon.create();
			}

			OutputStream stream = fileCon.openOutputStream();
			stream.write(urlF.getBytes("UTF-8"));
			try {
				stream.flush();
				stream.close();
				fileCon.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			String mobilePlatform = VikaTouch.mobilePlatform;
			if (mobilePlatform.indexOf("5.5") <= 0 && mobilePlatform.indexOf("5.4") <= 0
					&& mobilePlatform.indexOf("5.3") <= 0 && mobilePlatform.indexOf("5.2") <= 0
					&& mobilePlatform.indexOf("5.1") <= 0 && mobilePlatform.indexOf("Samsung") < 0) {
				VikaTouch.appInst.platformRequest(urlF);
			} else {
				VikaTouch.appInst.platformRequest(System.getProperty("fileconn.dir.music") + "test.ram");
			}
		} catch (Exception e) {
			e.printStackTrace();
			VikaTouch.error(e, ErrorCodes.VIDEOPLAY);
		}

	}

	public static void openRtspLink(String link) {
		if (Settings.rtspMethod == 1) {
			callSystemPlayer(link);
		} else if (Settings.rtspMethod == 2) {
			try {
				VikaTouch.appInst.platformRequest("vlc " + link);
			} catch (ConnectionNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			try {
				VikaTouch.appInst.platformRequest(link);
			} catch (ConnectionNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		Settings.saveSettings();
		if (VikaTouch.accessToken != null && VikaTouch.accessToken != "") {
			try {
				// VikaUtils.request(new URLBuilder("account.setOffline"));
			} catch (Exception e) {

			}
		}
		if (uiThread != null && uiThread.isAlive())
			uiThread.interrupt();
	}

	public void freeMemoryLow() {
		tokenRMS = null;
		newsScr = null;
		loginScr = null;
		splash = null;
		gc();
	}

	public static void logout() throws Exception {
		// VikaTouch.sendLog("logout: "+accessToken);
		VikaTouch.accessToken = null;
		try {

			if (VikaTouch.tokenRMS != null)
				VikaTouch.tokenRMS.closeRecordStore();
		} catch (Exception e) {

		}
		RecordStore.deleteRecordStore(VikaTouch.TOKEN_RMS);
		VikaTouch.menuScr = null;

	}

	public static void gc() {
		// TODO Garbage Cleaner
		System.gc();

	}
}
