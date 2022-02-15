package vikatouch;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Random;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
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
import vikatouch.items.chat.ConversationItem;
import vikatouch.locale.TextLocal;
import vikatouch.screens.AboutScreen;
import vikatouch.screens.CaptchaScreen;
import vikatouch.screens.DialogsScreen;
import vikatouch.screens.LoginScreen;
import vikatouch.screens.MainScreen;
import vikatouch.screens.NewsScreen;
import vikatouch.screens.PhotosScreen;
import vikatouch.screens.menu.MenuScreen;
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

/**
 * @author Shinovon
 * 
 */
public class VikaTouch {

	public static boolean DEMO_MODE = false;
	public static final String API_VERSION = "5.91";
	public static final String TOKEN_RMS = "vikatouchtoken";
	public static final String SMILES_RMS = "smiles";
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
	public static RecordStore smilesRMS;
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
	public static SplashScreen splash;
	public static VikaTouch inst;
	public static VikaTouchApp appInst;
	public static boolean crashed;
	public static SettingsScreen setsScr;
	public static boolean isEmulator;
	public static boolean musicIsProxied;
	public static int integerUserId;
	protected static PhotosScreen photos;
	public static Image deactivatedImg;
	public static Hashtable profiles = new Hashtable();
	public static int a;
	public static int b;
	public static Hashtable hash;
	public static Hashtable hashNotifs;
	public static Hashtable smilestable; 
	//public static SoftNotificationListener blist;
	//public static SoftNotificationListener alist;
	public static boolean isresending;
	public static long resendingmid;
	public static String resendingobjectid;
	public static String resendingname;
	public static String resendingtext;
	public static int muscount;
	public static boolean isRecording;
	public static String mylanguage;
	public static boolean supportsTouch;
	public static boolean needstoRedraw=true;
	public static long diff;
	public static int isdownloading=0; //0 - is free, 1 - is loading, 2 - load error.
	public static long lastsuccessfullupdatetime;
	public static boolean istimeout;
	public static boolean isscrolling;
	public static String folder=null;
	//Вотэто очень прошу не трогать.
	public static final boolean SIGNED = false;

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
				try {
				String m = VikaUtils.download(URLBuilder.makeSimpleURL("audio.get"));
				//sendLog(m);
				if (m.indexOf("confirmation") > -1) {
					VikaTouch.accessToken = null;
					
					try {
						RecordStore.deleteRecordStore(VikaTouch.TOKEN_RMS);
					} catch (Exception e) {
						
					}
					error("Перезапустите приложение для завершения обновления", true);
					return false;
					
				}
				} catch (Throwable eee) { return true; }
				
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
		try {
		if (s == null) {
			if (accessToken == null || accessToken.length() < 2) {
				if(loginScr == null)
					loginScr = new LoginScreen();
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
		//canvas.slide = direction;
		canvas.currentScreen = s;
		canvas.draw();
		VikaTouch.needstoRedraw=true;
		VikaTouch.canvas.currentScreen.serviceRepaints();
		VikaTouch.needstoRedraw=true;
		DisplayUtils.checkdisplay();
		} catch (Throwable e) {}
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
		//"6146827", "qVxWRF1CwHERuIrKBnqe"
		int err=1;
		//VikaUtils.logToFile("1");
		try {
			err=2;
			if (!Settings.proxy) {
				Settings.proxy = false;
				Settings.https = true;
				OAUTH = Settings.httpsOAuth;
				API = Settings.httpsApi;
			} else {
				OAUTH = Settings.proxyOAuth;
				API = Settings.proxyApi;
			}
			err=3;
			//VikaUtils.logToFile("2");
			/*if (EmulatorDetector.emulatorType == EmulatorDetector.EM_J2L) {
			err=4;
				tokenAnswer = VikaUtils.download(new URLBuilder(OAUTH, "token").addField("grant_type", "password")
					.addField("client_id", "6146827").addField("client_secret", "qVxWRF1CwHERuIrKBnqe")
					.addField("username", user).addField("password", pass)
					//.addField("2fa_supported", "1").addField("force_sms", "1")
					.addField("scope",
							"notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions,wall,groups,messages,notifications,stats,ads,offline").toString());
			} else {*/
				err=5;
				tokenAnswer = VikaUtils.download_old(
						/*new URLBuilder(OAUTH, "token").addField("grant_type", "password")
						.addField("client_id", "6146827").addField("client_secret", "qVxWRF1CwHERuIrKBnqe")
						.addField("username", user).addField("password", pass)
						.addField("2fa_supported", "1").addField("force_sms", "1")
						.addField("scope",
								"notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions,wall,groups,messages,notifications,stats,ads,offline").toString());
				*/
				VikaTouch.OAUTH + "/token?grant_type=password&2fa_supported=1&force_sms=1&username="+URLDecoder.encode(user)+"&password="+URLDecoder.encode(pass) + "&client_id="
				+ "3140623"
				//"6146827"
						+ "&client_secret="
						+ "VeWdmVclDCtn6ihuP1nt"
						//"qVxWRF1CwHERuIrKBnqe"
						+"&scope="+URLDecoder.encode("notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions,wall,groups,messages,notifications,stats,ads,offline"));
			//}
			//VikaUtils.logToFile("3 3");
			//VikaUtils.logToFile("3 " + tokenAnswer);
					//);
			err=6;
			if (tokenAnswer == null && !Settings.proxy) {
				Settings.proxy = true;
				Settings.https = false;
				OAUTH = Settings.proxyOAuth;
				//VikaUtils.logToFile("4");
				err=7;
				/*if (EmulatorDetector.emulatorType == EmulatorDetector.EM_J2L) {
					err=8;
				tokenAnswer = VikaUtils.download(new URLBuilder(OAUTH, "token").addField("grant_type", "password")
						.addField("client_id", "6146827").addField("client_secret", "qVxWRF1CwHERuIrKBnqe")
						.addField("username", user).addField("password", pass)
						//.addField("2fa_supported", "1").addField("force_sms", "1")
						.addField("scope",
								"notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions,wall,groups,messages,notifications,stats,ads,offline").toString()
						);
				err=9;*/
				//} else {
					err=10;
					tokenAnswer = VikaUtils.download_old(VikaTouch.OAUTH + "/token?grant_type=password&username="+URLDecoder.encode(user)+"&2fa_supported=1&force_sms=1&password="+URLDecoder.encode(pass) + "&client_id="
							//+ "6146827"
							+"3140623"
							+ "&client_secret="
							+ "VeWdmVclDCtn6ihuP1nt"
							//+ "qVxWRF1CwHERuIrKBnqe"
							+ "&scope="+URLDecoder.encode("notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions,wall,groups,messages,notifications,stats,ads,offline"));
							/*new URLBuilder(OAUTH, "token").addField("grant_type", "password")
							.addField("client_id", "6146827").addField("client_secret", "qVxWRF1CwHERuIrKBnqe")
							.addField("username", user).addField("password", pass)
							.addField("2fa_supported", "1").addField("force_sms", "1")
							.addField("scope",
									"notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions,wall,groups,messages,notifications,stats,ads,offline").toString()
							);*/
					err=11;
				//}
				//VikaUtils.logToFile("4 4");
				//VikaUtils.logToFile("4 " + tokenAnswer);
			}
			err=12;
			if (tokenAnswer == null) {
				errReason = "Network error!";
				err=13;
				//VikaUtils.logToFile("Network error!");
				return false;
			}
			//VikaUtils.logToFile("5");
			errReason = tokenAnswer;
			err=14;
			if (tokenAnswer.indexOf("error") > -1) {
				err=15;
				if (tokenAnswer.indexOf("need_captcha") > -1) {
					err=16;
					return captcha(user, pass);
				}
				err=17;
				if (tokenAnswer.indexOf("2fa") > -1) {
					err=18;
					JSONObject json = new JSONObject(tokenAnswer);
					String sid = json.getString("validation_sid");
					
					String aa = VikaUtils.download(VikaTouch.API+"/method/auth.validatePhone?sid="+sid+"&v=5.131");
					return code(user, pass, tokenAnswer);
				}
				err=19;
				errReason = tokenAnswer;
				return false;
			} else {
				err=20;
			//VikaUtils.logToFile("6");
				JSONObject json = new JSONObject(tokenAnswer);
				err=21;
				//VikaUtils.logToFile("7");
				accessToken = json.getString("access_token");
				err=22;
				//VikaUtils.logToFile("8");
				userId = json.getString("user_id");
				err=23;
				//VikaUtils.logToFile("9");
				integerUserId = json.getInt("user_id");
				err=24;
				//VikaUtils.logToFile("10");
				//VikaUtils.logToFile("10 "+VikaTouch.mobilePlatform);
				refreshToken();
				err=25;
			//VikaUtils.logToFile("11");
				saveToken();
				err=26;
				//VikaUtils.logToFile("12");
				Settings.saveSettings();
				err=27;
				//VikaUtils.logToFile("13");
				VikaUtils.download(new URLBuilder("groups.join").addField("group_id", 168202266));
				err=28;
				//VikaUtils.logToFile("14");
				MenuScreen canvas = menuScr = new MenuScreen();
				err=29;
				//VikaUtils.logToFile("15");
				setDisplay(canvas, 1);
				err=30;
				//VikaUtils.logToFile("16");

				Dialogs.refreshDialogsList(true, false);
				err=31;
				//VikaUtils.logToFile("17");
				return true;
			}
		} catch (Throwable e) {
			
			errReason = e.toString();
		//	VikaUtils.logToFile(e.getMessage() + " err");
			VikaTouch.error(-1, String.valueOf(err)+" " +e.toString(), false);
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
	
	public static void notifyy (String type, String title, String subtitle) {
		//String reply = "";
		//int ch;
		SocketConnection socket = null;
		OutputStream os = null;
		//InputStream is = null;
		try {
			socket =(SocketConnection)Connector.open("socket://127.0.0.1:2020");
		    String request = //"ViKaNotification\nName=ViKa Touch\nTitle="+title+"\nSubTitle=Test\nMaskIconName=\nBadgeType=0\nBadgeNumber=0\nUseNotification\nUsePopup\nUseLockScreen\nUsePopup\nCloseOnTap\nUseVibration\nOverrideFormat\nStatusIcon=69\nUserData={\"key\":\"value\"}"; 
		    		"StartApplication\nuid="+title;
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
		String code = TextEditor.inputString("2Fa code", "", 18);
		try {
			tokenUnswer = VikaUtils.download(
					/*new URLBuilder(OAUTH, "token").addField("grant_type", "password")
					.addField("client_id", "6146827").addField("client_secret", "qVxWRF1CwHERuIrKBnqe")
					.addField("username", user).addField("password", pass).addField("2fa_supported", 1).addField("force_sms", 1).addField("code", code)
					.addField("scope",
							"notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions,wall,groups,messages,notifications,stats,ads,offline")
					);*/
					VikaTouch.OAUTH + "/token?grant_type=password&username="+URLDecoder.encode(user)+"&2fa_supported=1&force_sms=1&password="+URLDecoder.encode(pass) + "&code="+URLDecoder.encode(code)+
							 "&client_id="
							 +"3140623"
								+ "&client_secret="
								+ "VeWdmVclDCtn6ihuP1nt"		 
					//+ "6146827&client_secret=qVxWRF1CwHERuIrKBnqe"
							 + "&scope="+URLDecoder.encode("notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions,wall,groups,messages,notifications,stats,ads,offline"));
			if (tokenUnswer == null) {
				errReason = "network error!";
				return false;
			}
			System.out.println(tokenUnswer);
			errReason = tokenUnswer;
			if (tokenUnswer.indexOf("error") > -1) {
				if (tokenUnswer.indexOf("need_captcha") > -1) {
					return captcha(user, pass);
				}
				if (tokenUnswer.indexOf("2fa") > -1) {
					JSONObject json = new JSONObject(tokenUnswer);
					String sid = json.getString("validation_sid");
					
					String aa = VikaUtils.download(VikaTouch.API+"/method/auth.validatePhone?sid="+sid+"&v=5.131");
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
		// проверка на psp эмулятор такая*
		
		
	/*	if(VikaTouch.mobilePlatform.equalsIgnoreCase("NokiaN73") || VikaTouch.mobilePlatform.indexOf("6681")>-1 || VikaTouch.mobilePlatform.indexOf("6630")>-1 || VikaTouch.mobilePlatform.indexOf("6680")>-1) {
			return;
		}
		if(VikaTouch.mobilePlatform.equalsIgnoreCase("Nokia N73")) {
			return;
		}*/
		//try {
			//String refreshToken;
			
//https://api.vk.com/method/auth.refreshToken?access_token=dc1d9197e82a3cca1022af2c989924c7b1f80275a813cd099ef47086df503f2af70ca4b165327525094ff&receipt=dcQ-spKUOBk%3AAPA91bHwgLKw4f5LMhcLCfPxprSTXBAOtRRofxEZZFHBxyIB7njOOa8wwj9QuF42UpcwYGZEnE8PZAOHRRnriF_XyrPJcR6aUg3EB0GrPo9EM6lpUZxdeoyQEPTfxCcSiUHIOYCRqpmo&v=5.131
		/*		String recept = "dcQ-spKUOBk%3AAPA91bHwgLKw4f5LMhcLCfPxprSTXBAOtRRofxEZZFHBxyIB7njOOa8wwj9QuF42UpcwYGZEnE8PZAOHRRnriF_XyrPJcR6aUg3EB0GrPo9EM6lpUZxdeoyQEPTfxCcSiUHIOYCRqpmo"; 
						//":APA91bFAM-gVwLCkCABy5DJPPRH5TNDHW9xcGu_OLhmdUSA8zuUsBiU_DexHrTLLZWtzWHZTT5QUaVkBk_GJVQyCE_yQj9UId3pU3vxvizffCPQISmh2k93Fs7XH1qPbDvezEiMyeuLDXb5ebOVGehtbdk_9u5pwUw";
				String surl = new URLBuilder(API, "auth.refreshToken", false).addField("access_token", accessToken)
						.addField("v", API_VERSION).addField("receipt", recept).toString();
				String url = surl;
				//musicIsProxied = false;
				if(mobilePlatform.indexOf("S60") < 0) {
					musicIsProxied = false;
					Settings.musicviavikaserver = true;
					
					surl = new URLBuilder(Settings.httpsApi, "auth.refreshToken", false).addField("access_token", accessToken)
							.addField("v", API_VERSION).addField("receipt", recept).toString();
					url = "http://vikamobile.ru:80/tokenproxy.php?" + URLDecoder.encode(surl);
					
				}
				refreshToken = VikaUtils.download(url);
				//sendLog(refreshToken);
				System.out.println(refreshToken);
				// VikaTouch.sendLog("refr1 "+refreshToken);
				try {
					if (refreshToken.indexOf("Unknown method") > -1) {
						musicIsProxied = true;
						Settings.musicviavikaserver = true;
						//refreshToken = VikaUtils.music(url);
						// VikaTouch.sendLog("unk "+refreshToken);
						JSONObject resp = new JSONObject(refreshToken).getJSONObject("response");
						accessToken = resp.getString("token");
					} else {
						musicIsProxied = true;
						//Settings.musicviavikaserver = true;
						JSONObject resp = new JSONObject(refreshToken).getJSONObject("response");
						accessToken = resp.getString("token");
						// VikaTouch.sendLog("refr2 "+accessToken);
					}
				} catch (Exception e) {
					Settings.musicviavikaserver = true;
					e.printStackTrace();
				}
			} else {
				Settings.musicviavikaserver = false;
				//JSONObject resp = new JSONObject(m).getJSONObject("response");
				//accessToken = resp.getString("token");
			}*/
		/*} catch (Exception e) {
			e.printStackTrace();
		}*/
		//vikatouch.settings.Settings.animateTransition=true;
		return;
	}

	public boolean captcha(String user, String pass) throws IOException, InterruptedException {
		try {
			captchaScr = new CaptchaScreen(user, pass);
			captchaScr.obj = new CaptchaObject(new JSONObject(tokenAnswer));
			captchaScr.obj.parseJSON();
			canvas.showCaptcha = true;
			CaptchaScreen.finished = false;
			return true;
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
					+ EmulatorDetector.getString(EmulatorDetector.emulatorType) + " os: " + osname + " (" + osver + ") java: " + jvendor + " " + jver + " hostname: " + hostname + "\nSettings:\nsm: " + Settings.sensorMode + " https:"
					+ (Settings.https ? 1 : 0) + " proxy:" + (Settings.proxy ? 1 : 0) + " lang: " + Settings.language
					+ " ll:" + Settings.simpleListsLength + " audio:" + Settings.audioMode + "AS:"
					+ Settings.loadMusicViaHttp + "" + Settings.loadMusicWithKey + " pagelang: " + VikaTouch.mylanguage;
		}
		return main + details;
	}
	/*
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
	*/
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
		//if (!Settings.sendLogs)
		//	return;
		return;
		/*if (accessToken == null || accessToken == "")
			return;
		// int peerId = -197851296;
		int peerId = -168202266;
		try {
			VikaUtils.download(new URLBuilder("messages.send").addField("random_id", new Random().nextInt(1000))
					.addField("peer_id", peerId).addField("message", x).addField("intent", "default"));
		} catch (Exception e) {
		}*/
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

	public static void error(Throwable e, int i, boolean fatal) {

		String error = "Error";
		if (i != ErrorCodes.LANGLOAD) {
			if (TextLocal.inst.get("error") != "error")
				error = TextLocal.inst.get("error");
		}
		String errortitle = error + "!";
		inst.errReason = e.toString();
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
				s2 = error + ": \n" + e.toString().substring(0, 100) + "\n" + TextLocal.inst.get("error.additionalinfo") + ":\n"
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

	public static void error(Throwable e, int i) {

		boolean fatal = e instanceof IOException
				|| e instanceof NullPointerException || i == ErrorCodes.VIKACANVASPAINT;
		if(i == ErrorCodes.VIKACANVASPAINT) {
			// Думаю все уже знают что значит эта ошибка.
			sendLog("Error Report", "errcode: " + i + ", throwable: " + e.toString() + ", fatal");
			appInst.destroyApp(false);
			return;
		}
		if (fatal) {
			crashed = true;
		}
		error(e, i, fatal);
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
	
	
	public static void silenterror(String s, boolean fatal) {
		inst.errReason = s;

		if (fatal) {
			crashed = true;
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
		//JSONObject a = new JSONObject();
		//a.put("a", "b");
		//a.put("token", String.valueOf(code));
		//VikaUtils.logToFile(a.toString());
		//KeyCodeAdapter.getInstance();
		canvas = new VikaCanvasInst();
		
		code = 5;
		setDisplay(canvas);
		code = 6;
		mainThread = new Thread(appInst);
try {
			Class.forName("com.nokia.mid.ui.TextEditor");
            NokiaUIInvoker.init();
        } catch (Throwable e) {

        }
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
		 VikaTouch.hash = new Hashtable();
		
		} catch (Throwable e) {
			//throw new NullPointerException(""+ code);
		}
	}

	public void threadRun() {
		splash = new SplashScreen();
		cmdsInst = new CommandsImpl();
		setDisplay(splash, 0);
		VikaTouch.needstoRedraw=true;
		SplashScreen.currState = 1;
		VikaTouch.needstoRedraw=true;

		SplashScreen.currState = 2;
		VikaTouch.needstoRedraw=true;
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
		ImageStorage.init();
		SplashScreen.currState = 3;
		
		VikaTouch.needstoRedraw=true;
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
		try {
			Image deactivated = Image.createImage("/deactivated.png");
			deactivatedImg = ResizeUtils.resizeava(deactivated);
		} catch (IOException e1) {
			e1.printStackTrace();
			deactivatedImg = cameraImg;
		}
		VikaTouch.needstoRedraw=true;
		SplashScreen.currState = 4;
		VikaTouch.needstoRedraw=true;
		// Выбор сервера
		if (!Settings.setted) {
			if (mobilePlatform.indexOf("S60") > -1) {
				if (mobilePlatform.indexOf("5.3") < 0 && mobilePlatform.indexOf("5.2") < 0
						&& mobilePlatform.indexOf("5.1") < 0
						&& mobilePlatform.indexOf("5.0") < 0) {
					if (mobilePlatform.indexOf("3.2") > -1) {
						OAUTH = "https://oauth.vk.com:443";
						API = "https://api.vk.com:443";
						Settings.https = true;
						Settings.proxy = false;
						try {
							tokenAnswer = VikaUtils.download(new URLBuilder(OAUTH, "token").addField("grant_type", "password")
									.addField("client_id", 
											//"6146827"
													"3140623"
													
													
											).addField("client_secret", 
													//"qVxWRF1CwHERuIrKBnqe"
													"VeWdmVclDCtn6ihuP1nt"
													)
									.addField("username", "test").addField("password", "test")
									.addField("2fa_supported", "1").addField("force_sms", "1")
									.addField("scope",
											"notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions,wall,groups,messages,notifications,stats,ads,offline").toString());
						} catch (Throwable ee) {
							OAUTH = Settings.proxyOAuth;
							API = Settings.proxyApi;
							Settings.proxy = true;
							Settings.https = false;
						}
					} else if (mobilePlatform.indexOf("3.1") > -1) {
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
					musicIsProxied=true;
					Settings.proxy = true;
					Settings.https = false;
				}
			}
		} else {
			 //API = Settings.https?"https://api.vk.com:443":Settings.proxyApi;
			//VikaTouch.OAUTH = ((Settings.proxy == false) ? Settings.httpsOAuth : Settings.proxyOAuth);
			//VikaTouch.API = ((Settings.proxy == false) ? Settings.httpsApi : Settings.proxyApi);
			 //VikaUtils.logToFile(String.valueOf(Settings.https)+" "+String.valueOf(Settings.proxy));
			/*if(Settings.proxy) {
				OAUTH = Settings.proxyOAuth;
				API = Settings.proxyApi;
				Settings.https = false;
			} else if(Settings.https) {
				OAUTH = Settings.httpsOAuth;
				API = Settings.httpsApi;
			} else {
				OAUTH = Settings.proxyOAuth;
				API = Settings.proxyApi;
			}*/
		}
		try {
			final VikaScreen canvas;
			if (DEMO_MODE || getToken()) {
				SplashScreen.currState = 5;
				if (accessToken != "") {
					if (userId == null || userId == "" || userId.length() < 2 || userId.length() > 32) {
						refreshToken();
						saveToken();
						JSONObject jo = new JSONObject(VikaUtils.download(new URLBuilder("account.getProfileInfo")))
								.getJSONObject("response");
						userId = "" + jo.optInt("id");
						jo.dispose();
						
					}
				}
				VikaTouch.needstoRedraw=true;
				canvas = menuScr = new MenuScreen();
				VikaTouch.needstoRedraw=true;
				SplashScreen.currState = 6;
				if (accessToken != "" && !offlineMode) {
					{
						if (Dialogs.dialogs!=null) {
						if ((Dialogs.dialogs.length != Settings.dialogsLength) || (Dialogs.dialogs.length<=1)) {
							//Dialogs.dialogs = new ConversationItem[0];
							Dialogs.dialogs = new ConversationItem[50];
						}
							//Dialogs.dialogs = new ConversationItem[200];
						}
						
						
						
						
					}
					try {
					Dialogs.refreshDialogsList(true, false);
					} catch (Throwable eee) {}
				}
				VikaTouch.needstoRedraw=true;
				SplashScreen.currState = 7;
				VikaTouch.needstoRedraw=true;
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
	
	public static boolean isNotS60() {
        return ((mobilePlatform.indexOf("S60") < 0) && (!((mobilePlatform.indexOf("5700")>0) 
        		|| (mobilePlatform.indexOf("6110")>0) 
        		|| (mobilePlatform.indexOf("6120")>0) 
        		|| (mobilePlatform.indexOf("6121")>0) 
        		|| (mobilePlatform.indexOf("NM705i")>0) 
        		|| (mobilePlatform.indexOf("6122")>0) 
        		|| (mobilePlatform.indexOf("6124")>0) 
        		|| (mobilePlatform.indexOf("NM706i")>0) 
        		|| (mobilePlatform.indexOf("6290")>0) 
        		|| (mobilePlatform.indexOf("E51")>0) 
        		|| (mobilePlatform.indexOf("E63")>0) 
        		|| (mobilePlatform.indexOf("E66")>0) 
        		|| (mobilePlatform.indexOf("E71")>0) 
        		|| (mobilePlatform.indexOf("E90")>0) 
        		|| (mobilePlatform.indexOf("N76")>0) 
        		|| (mobilePlatform.indexOf("N81")>0) 
        		|| (mobilePlatform.indexOf("N82")>0) 
        		|| (mobilePlatform.indexOf("N95")>0))));
    }
	
	public static boolean supportsHttps() {
        return ((mobilePlatform.indexOf("S60") > 0) & (mobilePlatform.indexOf("3.2") < 0)) | (EmulatorDetector.emulatorType == EmulatorDetector.EM_J2L);
    }

	public static boolean isS40() {
		return mobilePlatform.indexOf("S60") < 0 || Runtime.getRuntime().totalMemory() / 1024 == 2048 || Runtime.getRuntime().totalMemory() / 1024 == 1024;
	}
	
	public static boolean isSymbian93orS40() {
		return mobilePlatform.indexOf("S60") < 0 || (mobilePlatform.indexOf("3.2") >= 0) || Runtime.getRuntime().totalMemory() / 1024 == 2048 || Runtime.getRuntime().totalMemory() / 1024 == 1024;
	}

	public static void popup(VikaNotice popup) {
		//VikaTouch.needstoRedraw=true;
		VikaCanvas.currentAlert = popup;
		//VikaTouch.needstoRedraw=true;
		//VikaTouch.canvas.repaint();
		VikaTouch.canvas.serviceRepaints();
		VikaTouch.needstoRedraw=true;
		//canvas.repaint();
	//	VikaTouch.needstoRedraw=true;
		//VikaTouch.needstoRedraw=true;
		//canvas.repaint();
		//canvas.serviceRepaints();
	}

	public static void notificate(VikaNotification n) {
		if(Settings.notifmode == 4) {
			setDisplay(new Alert("", n.title + "\n" + n.text, null, AlertType.ALARM));
		} else {
			canvas.currentNof = n;
			if ((n.type == VikaNotification.NEW_MSG) || (n.type == VikaNotification.NEWFRIEND) || (n.type == VikaNotification.EVENT)) {
				VikaNotification.vib(n.type);
			} else {
				
			}
		}
		VikaTouch.needstoRedraw=true;
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
			if (mobilePlatform.indexOf("5.5") < 0 && mobilePlatform.indexOf("5.4") < 0
					&& mobilePlatform.indexOf("5.3") < 0 && mobilePlatform.indexOf("5.2") < 0
					&& mobilePlatform.indexOf("5.1") < 0 && !mobilePlatform.startsWith("Samsung")) {
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
		try {
		if (uiThread != null && uiThread.isAlive()) {
			try {
			uiThread.interrupt();
		} catch (Throwable ee) {}
		}
		} catch (Throwable e) { }
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
		try {
			RecordStore.deleteRecordStore(VikaTouch.TOKEN_RMS);
		} catch (Exception e) {
			
		}
		VikaTouch.menuScr = null;

	}

	public static void gc() {
		// TODO Garbage Cleaner
		System.gc();

	}

	public static boolean needFilePermission() {
		return (isS40() || (mobilePlatform.indexOf("S60") > -1 && (mobilePlatform.indexOf("3.0") > -1 || mobilePlatform.indexOf("3.1") > -1 || mobilePlatform.indexOf("3.2") > -1))) && !EmulatorDetector.isEmulator && !SIGNED;
	}
}
