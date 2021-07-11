package vikatouch.screens;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.screen.VikaScreen;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.Dialogs;
import vikatouch.VikaTouch;
import vikatouch.locale.TextLocal;
import vikatouch.screens.menu.MenuScreen;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.captcha.CaptchaObject;
import vikatouch.utils.text.TextEditor;
import vikatouch.utils.url.URLBuilder;

/**
 * @author Shinovon
 * 
 */
public class CaptchaScreen extends VikaScreen {
	public Image image;
	private Thread thread;
	public static String input;
	public static boolean finished;
	public CaptchaObject obj;
	private int x;
	private int w;
	private String captchaRequiredStr;
	private String captchaStr;
	private boolean switcher;
	private String user;
	private String pass;
	private String s;

	public CaptchaScreen(String user, String pass) {
		super();
		this.user = user;
		this.pass = pass;
		captchaRequiredStr = TextLocal.inst.get("login.captcharequired");
		captchaStr = TextLocal.inst.get("login.captcha");
		s = TextLocal.inst.get("title.login");
		switcher = false;
		input = "";
	}

	public void draw(Graphics g) {
		ColorUtils.setcolor(g, -1);
		g.fillRect(0, 0, DisplayUtils.width, DisplayUtils.height);
		if (obj != null && image == null) {
			image = obj.getImage();
		}
		w = image.getWidth();
		ColorUtils.setcolor(g, -2);
		if (!switcher)
			ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawRect(0, 100, DisplayUtils.width - 1, 40);
		ColorUtils.setcolor(g, 3);
		g.fillRect(x, 150, w, 36);
		if (switcher) {
			ColorUtils.setcolor(g, ColorUtils.TEXT);
			g.drawRect(x, 150, w, 36);
		}
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		if (switcher)
			ColorUtils.setcolor(g, ColorUtils.OUTLINE);
		g.setFont(Font.getDefaultFont());
		int sw = g.getFont().stringWidth(s);
		g.drawString(s, x + (w - sw) / 2, 150 + (36 - g.getFont().getHeight()) / 2, 0);
		ColorUtils.setcolor(g, 2);
		g.setFont(Font.getFont(0, 0, Font.SIZE_MEDIUM));
		if (input != null)
			g.drawString(input, 10, 110, 0);
		x = (DisplayUtils.width - w) / 2;
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		g.drawString(captchaRequiredStr, DisplayUtils.width / 2, 0, Graphics.TOP | Graphics.HCENTER);
		g.drawImage(image, x, 24, 0);
	}

	public final void press(int x, int y) {
		if (y > 100 && y < 140 && x < 240) {
			if (thread != null)
				thread.interrupt();
			thread = new Thread() {
				public void run() {
					input = TextEditor.inputString(captchaStr, input, 32, image);
				}
			};
			thread.start();
		}
	}

	public final void release(int x, int y) {
		if (x > this.x && y > 150 && y < 186 && x < this.x + this.w) {
			CaptchaScreen.finished = true;
			VikaTouch.canvas.showCaptcha = false;
			captcha();
		}
	}

	private void captcha() {
		VikaTouch.needstoRedraw=true;
		try {
			VikaTouch.inst.tokenAnswer = VikaUtils.download_old(new URLBuilder(VikaTouch.OAUTH, "token")
					.addField("grant_type", "password").addField("client_id", "2685278")
					.addField("client_secret", "lxhD8OD7dMsqtXIm5IUY").addField("username", user)
					.addField("password", pass)
					.addField("scope",
							"notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions,wall,groups,messages,notifications,stats,ads,offline")
					.addField("captcha_sid", obj.captchasid).addField("captcha_key", CaptchaScreen.input).toString());
			if (VikaTouch.inst.tokenAnswer.indexOf("need_captcha") > -1) {
				VikaTouch.inst.captcha(user, pass);
			}
			if (VikaTouch.inst.tokenAnswer.indexOf("error") > -1) {
				return;
			}
			JSONObject json = new JSONObject(VikaTouch.inst.tokenAnswer);
			VikaTouch.accessToken = json.getString("access_token");
			VikaTouch.userId = json.getString("user_id");
			VikaTouch.inst.refreshToken();
			VikaTouch.menuScr = new MenuScreen();
			VikaTouch.needstoRedraw=true;
			VikaTouch.setDisplay(VikaTouch.menuScr, 1);
			VikaTouch.needstoRedraw=true;
			VikaTouch.inst.saveToken();
			Dialogs.refreshDialogsList(true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public final void press(int key) {
		if (key == -1 || key == -2) {
			switcher = !switcher;
		} else if (key == -5) {
			if (!switcher) {
				if (thread != null)
					thread.interrupt();
				thread = new Thread() {
					public void run() {
						input = TextEditor.inputString(captchaStr, input, 32, image);
						interrupt();
					}
				};
				thread.setPriority(Thread.MAX_PRIORITY);
				thread.start();
			} else {
				finished = true;
				VikaTouch.canvas.showCaptcha = false;
				captcha();
			}
		}
	}

}
