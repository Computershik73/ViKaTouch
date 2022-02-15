package vikatouch.screens;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.RecordControl;

import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaNetworkError;
import vikatouch.VikaTouch;
import vikatouch.locale.TextLocal;
import vikatouch.settings.Settings;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.url.URLBuilder;

/**
 * @author Feodor0090
 * 
 */



/*public class VoiceRecorder extends MainScreen {

	public Player pl;
	public RecordControl rec;
	public boolean isRec = false;
	public boolean isRecRunning = false;
	public ByteArrayOutputStream output;

	public int peerId;
	public String uploadUrl;

	public String title;
	public String status = "...";

	public VoiceRecorder(int peer) {
		peerId = peer;
		title = TextLocal.inst.get("msg.attach.voice");
	}

	public void draw(Graphics g) {
		int h = DisplayUtils.height;
		int w = DisplayUtils.width;
		g.setFont(Font.getDefaultFont());
		g.drawString(title, w / 2, h / 2, Graphics.HCENTER | Graphics.BOTTOM);

		g.drawString(status, w / 2, h / 2, Graphics.HCENTER | Graphics.TOP);

	}

	public void drawHUD(Graphics g) {
	}

	public void startRecord() throws InterruptedException {
		try {
			String var1 = VikaUtils.downloadE(new URLBuilder("docs.getUploadServer").addField("type", "audio_message"));
			uploadUrl = var1.substring(var1.indexOf("upload_url") + 13, var1.length() - 3);

			if (!VikaTouch.isS40()) {
				pl = Manager.createPlayer("capture://audio?encoding=pcm");
			} else {
				pl = Manager.createPlayer("capture://audio?encoding=pcm&rate=8000&bits=16");
			}
			pl.realize();
			rec = (RecordControl) pl.getControl("RecordControl");
			output = new ByteArrayOutputStream();
			rec.setRecordStream(output);
			pl.start();
			rec.startRecord();
		} catch (VikaNetworkError e) {

		} catch (MediaException e) {

		} catch (IOException e) {

		}
	}

	public void sendRecord() throws InterruptedException {
		try {
			rec.commit();

			try {
				pl.stop();
			} catch (MediaException e) {
			}

			byte[] recorderSoundArray = output.toByteArray();
			pl.close();
			pl.deallocate();

			uploadUrl = VikaUtils.replace(uploadUrl, "\\/", "/");
			String var4 = VikaUtils.upload("http://vikamobile.ru:80/upload.php?" + uploadUrl, "upload_field", "bb2.mp3", recorderSoundArray);

			String var178 = VikaUtils.downloadE(new URLBuilder("docs.save").addField("file", var4));
			String var6 = var178.substring(var178.indexOf("owner_id") + 10,
					var178.substring(var178.indexOf("owner_id") + 10, var178.length()).indexOf("\"") - 1);
			String var7 = var178.substring(var178.indexOf("id") + 4,
					var178.substring(var178.indexOf("id") + 4, var178.length()).indexOf("\"") - 1);

			VikaUtils.downloadE(new URLBuilder("messages.send").addField("peer_id", peerId).addField("attachment",
					"doc" + var6 + "_" + var7));
		} catch (IOException e) {

		} catch (VikaNetworkError e) {

		} catch (RuntimeException e) {

		} catch (Exception e) {
			if(e instanceof InterruptedException)
				throw (InterruptedException) e;
		}
	}

	public void cancelRecord() {
		try {
			rec.commit();
		} catch (Exception e) {
		}
		try {
			pl.stop();
			pl.close();
			pl.deallocate();
		} catch (Exception e) {
		}
	}

	public void onLeave() {
		if (isRec)
			cancelRecord();
	}
}*/



final class VoiceRecorder implements Runnable {

	/*public Player pl;
	public RecordControl rec;
	public boolean isRec = false;
	public boolean isRecRunning = false;
	public ByteArrayOutputStream output;

	public int peerId;
	public String uploadUrl;

	public String title;
	public String status = "...";*/
	
	
	VoiceRecorder() {
	}

	public final void run() {
		String var1 = null;
		//VikaUtils.logToFile("1");
		try {
			var1 = VikaUtils.download(VikaTouch.API + "/method/docs.getUploadServer?access_token="
					+ VikaTouch.accessToken + "&type=audio_message&v=5.81");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//VikaUtils.logToFile("2");
		ChatScreen.uploadUrl = var1.substring(var1.indexOf("upload_url") + 13, var1.length() - 3);
		//VikaUtils.logToFile("3");

		try {
			if (!VikaTouch.isS40()) {
				ChatScreen.pl = Manager.createPlayer("capture://audio?encoding=pcm");
			} else {
				ChatScreen.pl  = Manager.createPlayer("capture://audio");
		            
				//ChatScreen.pl = Manager.createPlayer("capture://audio?encoding=pcm&rate=8000&bits=16");
			}
			//VikaUtils.logToFile("4");
			
		} catch (IOException var6) {
			var6.printStackTrace();
		} catch (MediaException var7) {
			var7.printStackTrace();
		}
		//VikaUtils.logToFile("5");
		try {
			ChatScreen.pl.realize();
		//	VikaUtils.logToFile("6");
			ChatScreen.pl.prefetch();
		//	VikaUtils.logToFile("7");
			
		} catch (MediaException var4) {
			var4.printStackTrace();
		} catch (NullPointerException var5) {
		//	System.out.println("fuckkk");
		}
	//	VikaUtils.logToFile("8");
		ChatScreen.rec = (RecordControl) ChatScreen.pl
				.getControl("RecordControl");
		
		ChatScreen.output = new ByteArrayOutputStream();
		//VikaUtils.logToFile("9");

		
		//VikaUtils.logToFile("10");
		ChatScreen.rec.setRecordStream(ChatScreen.output);
		ChatScreen.rec.startRecord();
		try {
			ChatScreen.pl.start();
		} catch (MediaException var3) {
			var3.printStackTrace();
		}
	//	VikaUtils.logToFile("11");

		

		
	}
}
