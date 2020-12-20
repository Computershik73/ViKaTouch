package vikatouch.screens;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.RecordControl;

import ru.nnproject.vikaui.utils.DisplayUtils;
import vikamobilebase.HttpMultipartRequest;
import vikatouch.VikaNetworkError;
import vikatouch.VikaTouch;
import vikatouch.locale.TextLocal;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.url.URLBuilder;

public class VoiceRecorder extends MainScreen {

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
			Hashtable var3 = new Hashtable();

			uploadUrl = VikaUtils.replace(uploadUrl, "\\/", "/");
			byte[] var5 = (new HttpMultipartRequest("http://vikamobile.ru:80/upload.php?" + uploadUrl, var3,
					"upload_field", "bb2.mp3", "multipart/form-data", recorderSoundArray)).send();
			String var4 = new String(var5);

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
}
