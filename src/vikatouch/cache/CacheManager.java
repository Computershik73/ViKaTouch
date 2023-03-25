package vikatouch.cache;


import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

import vikatouch.VikaTouch;
import vikatouch.utils.*;


public class CacheManager {
	
	//private static Hashtable strings = new Hashtable();
	private static Hashtable images = new Hashtable();
	private static Hashtable indexes = new Hashtable();
	private static int lastIndex = 1;

	public static void init() {
		loadImageIndexes();
		removeTempImages();
	}

	public static void save() {
		writeImageIndexes();
	}
	
	private static void loadImageIndexes() {
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore("i.index", false);
			ByteArrayInputStream bais = new ByteArrayInputStream(rs.getRecord(1));
			DataInputStream dis = new DataInputStream(bais);
			int len = dis.readShort();
			lastIndex = dis.readInt();
			for(int i = 0; i < len; i++) {
				indexes.put(dis.readUTF(), new Object[] { toImageIndex(dis.readInt()), new Boolean(dis.readBoolean()) });
			}
			dis.close();
			rs.closeRecordStore();
			bais.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void writeImageIndexes() {
		try {
			RecordStore.deleteRecordStore("i.index");
		} catch (Throwable e) {
		}
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore("i.index", true);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			int len = indexes.size();
			dos.writeShort(len);
			dos.writeInt(lastIndex);
			Enumeration keys = indexes.keys();
			Enumeration elements = indexes.elements();
			while(keys.hasMoreElements()) {
				String k = (String) keys.nextElement();
				Object[] v = (Object[]) elements.nextElement();
				dos.writeUTF(k);
				dos.writeInt(fromImageIndex((String)v[0]));
				dos.writeBoolean(((Boolean)v[1]).booleanValue());
			}
			final byte[] data = baos.toByteArray();
			dos.close();
			baos.close();
			rs.addRecord(data, 0, data.length);
			rs.closeRecordStore();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private static void removeAllIndexedImageRecords() {
		try {
			if(indexes.size() == 0) return;
			Enumeration elements = indexes.elements();
			while(elements.hasMoreElements()) {
				String v = (String) elements.nextElement();
				RecordStore.deleteRecordStore("i.f" + v);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void clearImageIndexes() {
		removeAllIndexedImageRecords();
		indexes.clear();
		try {
			RecordStore.deleteRecordStore("i.index");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static int fromImageIndex(String s) {
		if(!s.startsWith("_")) return 0;
		return Integer.parseInt(s.substring(1), 16);
	}
	
	private static String toImageIndex(int i) {
		String s = Integer.toHexString(i);
		while(s.length() < 3) s = "0" + s;
		return "_" + s;
	}
	
	private static String getExistingImageIndex(String url) {
		String s = uniqueUrl(url);
		if(indexes.containsKey(s)) {
			return (String) ((Object[]) indexes.get(s))[0];
		}
		return null;
	}
	
	private static String getImageIndex(String url, boolean longlive) {
		String s = uniqueUrl(url);
		if(indexes.containsKey(s)) {
			return (String) ((Object[]) indexes.get(s))[0];
		}
		int i = lastIndex++;
		String k = toImageIndex(i);
		indexes.put(s, new Object[] { k, new Boolean(longlive) });
		writeImageIndexes();
		return k;
	}
	
	private static String uniqueUrl(String url) {
		String s = url.toLowerCase();
		return s;
	}
/*
	public static void saveDialogs() {
		if(!Settings.cacheDialogs)
			return;
		clearDialogs();
		if(Dialogs.dialogs == null) return;
		JSONArray json = new JSONArray();
		json.put(Global.unreadCount);
		for(int i = 0; i < Dialogs.itemsCount; i++) {
			ConversationItem conv = Dialogs.dialogs[i];
			if(conv != null) {
				Hashtable h = new Hashtable();
				h.put("title", conv.title);
				h.put("text", conv.text);
				h.put("lasttext", conv.lasttext);
				h.put("peerid", new Integer(conv.peerId));
				if(conv.avaurl != null)
					h.put("ava", conv.avaurl);
				h.put("date", new Long(conv.date));
				json.put(h);
			}
		}
		String x = json.toString();
		json = null;
		try {
			RecordStore rs = RecordStore.openRecordStore("tvkconvs", true);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeUTF(x);
			dos.close();
			byte[] bytes = baos.toByteArray();
			dos.close();
			baos.close();
			rs.addRecord(bytes, 0, bytes.length);
			rs.closeRecordStore();
			Global.log(Global.GREEN_LOG, "cache: dialogs saved");
		} catch (Exception e) {
			Global.log(Global.ERROR_LOG, "cache: dialogs save: " + e.toString());
			e.printStackTrace();
		}
	}
	*/
	/*
	public static boolean loadDialogs() {
		if(!Settings.cacheDialogs)
			return false;
		RecordStore rs = null;
		ByteArrayInputStream bais = null;
		try {
			rs = RecordStore.openRecordStore("tvkconvs", false);
			bais = new ByteArrayInputStream(rs.getRecord(1));
			DataInputStream dis = new DataInputStream(bais);
			String x = dis.readUTF();
			dis.close();
			rs.closeRecordStore();
			bais.close();
			JSONArray json = new JSONArray(x);
			boolean newSave = false;
			try {
				Global.unreadCount = (short) json.getInt(0);
				newSave = true;
			} catch (JSONException e) {
				Global.log(Global.WARN_LOG, "cache: cache old format!");
			}
			Dialogs.itemsCount = json.length() - (newSave ? 1 : 0);
			try {
				Dialogs.dialogs = new ConversationItem[json.length() - (newSave ? 1 : 0)];
				for(int i = newSave ? 1 : 0; i < json.length(); i++) {
					final ConversationItem c = new ConversationItem();
					JSONObject j = (JSONObject) json.get(i);
					c.peerId = j.getInt("peerid");
					c.title = j.optString("title");
					c.text = j.optString("text");
					c.lasttext = j.optString("lasttext");
					c.avaurl = j.optString("ava", null);
					c.date = j.optLong("date");
					c.time = c.getTime();
					if(DisplayUtils.width > 240)
						Global.tasks.addElement(new Runnable() {
							public void run() {
								try {
									c.getAvaCache();
								} catch (Throwable e) {
									e.printStackTrace();
								}
							}
						});
					Dialogs.dialogs[i - (newSave ? 1 : 0)] = c;
				}
			} catch (JSONException e) {
				Global.log(Global.ERROR_LOG, "cache: dialogs parse fail: " + e.toString());
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				Global.log(Global.ERROR_LOG, "cache: dialogs parse: " + e.toString());
				e.printStackTrace();
			}
			Global.log(Global.GREEN_LOG, "cache: dialogs loaded");
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}*/
/*
	public static boolean hasUser(int id) {
		String[] s = RecordStore.listRecordStores();
		for(int i = 0; i < s.length; i++) {
			if(s[i].startsWith("tprf")) {
				if(s[i].equalsIgnoreCase("tprf_" + id)) {
					return true;
				}
			}
		}
		return false;
	}*/
	/*
	public static void saveProfile(int id, Profile p) {
		try {
			RecordStore rs = RecordStore.openRecordStore("tprf_" + id, true);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			if(!p.isUser()) {
				dos.writeShort(1);
				dos.writeUTF(p.getName());
				dos.writeUTF(p.getUrl());
			} else if(!p.hasAccName()) {
				dos.writeShort(2);
				dos.writeUTF(p.getFirstName());
				dos.writeUTF(p.getLastName());
				dos.writeUTF(p.getUrl());
			} else {
				dos.writeShort(3);
				dos.writeUTF(p.getFirstName());
				dos.writeUTF(p.getLastName());
				dos.writeUTF(p.getUrl());
				dos.writeUTF(p.getFirstNameAcc());
				dos.writeUTF(p.getLastNameAcc());
			}
			byte[] bytes = baos.toByteArray();
			dos.close();
			baos.close();
			rs.addRecord(bytes, 0, bytes.length);
			rs.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
	/*
	public static Profile getProfile(int id) {
		RecordStore rs = null;
		ByteArrayInputStream bais = null;
		try {
			rs = RecordStore.openRecordStore("tprf_" + id, false);
			bais = new ByteArrayInputStream(rs.getRecord(1));
			DataInputStream dis = new DataInputStream(bais);
			int type = dis.readShort();
			if(type == 1) {
				//simple profile
				String fn = dis.readUTF();
				String url = dis.readUTF();
				dis.close();
				return new Profile(id, fn, url);
			}
			if(type == 2) {
				//simple user
				String fn = dis.readUTF();
				String ln = dis.readUTF();
				String url = dis.readUTF();
				dis.close();
				return new Profile(id, fn, ln, url);
			}
			if(type == 3) {
				//advanced user
				String fn = dis.readUTF();
				String ln = dis.readUTF();
				String url = dis.readUTF();
				String fnacc = dis.readUTF();
				String lnacc = dis.readUTF();
				dis.close();
				return new ProfileObject(id, fn, ln, url).setFirstNameAcc(fnacc).setLastNameAcc(lnacc);
			}

			dis.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null)
				try {
					rs.closeRecordStore();
				} catch (Exception e) {
				}
			if(bais != null)
				try {
					bais.close();
				} catch (Exception e) {
				}
		}
		return null;
	}
*/
	public static boolean hasImage(String url) {
		String s = uniqueUrl(url);
		if(images.containsKey(s)) return true;
		if(indexes.containsKey(s)) return true;
		/*
		String[] s = RecordStore.listRecordStores();
		for(int i = 0; i < s.length; i++) {
			if(s[i].startsWith("i.")) {
				if(s[i].equalsIgnoreCase("i." + h)) {
					return true;
				}
			}
		}
		*/
		return false;
	}
	
	public static Image getImage(String url) {
		if(url.equals("camera_50")) return VikaTouch.cameraImg;
		String u = uniqueUrl(url);
		if(images.containsKey(u)) return (Image) images.get(u);
		String s = getExistingImageIndex(url);
		if(s == null) return null;
		/*
		String h = getUniqueName(url);
		if(images.containsKey(url)) return (Image) images.get(url);
		*/
		RecordStore rs = null;
		ByteArrayInputStream bais = null;
		try {
			boolean longlive = ((Boolean)((Object[])indexes.get(u))[1]).booleanValue();
			rs = RecordStore.openRecordStore("i.f" + (!longlive ? "t" : "") + s, false);
			bais = new ByteArrayInputStream(rs.getRecord(1));
			Image i = Image.createImage(bais);
			if(longlive) images.put(u, i);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null)
				try {
					rs.closeRecordStore();
				} catch (Exception e) {
				}
			if(bais != null)
				try {
					bais.close();
				} catch (Exception e) {
				}
		}
		
		return null;
	}

	public static Image saveImage(String url, InputStream is, final boolean longlive) throws IOException {
		final String s = getImageIndex(url, longlive);
		final String u = uniqueUrl(url);
		
		//RecordStore rs = null;
		//final String s = getUniqueName(url);
		//try {
			/*
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final byte[] buf = new byte[4096];
            int read;
            while ((read = is.read(buf)) != -1) {
                baos.write(buf, 0, read);
            }
			is.close();
			byte[] bytes = baos.toByteArray();
			rs.addRecord(bytes, 0, bytes.length);
			rs.closeRecordStore();
			baos.close();
			*/
		try {
			final byte[] bytes = VikaUtils.getBytes(is);
			is.close();
			VikaTouch.tasks.addElement(new Runnable() {
				public void run() {
					try {
						RecordStore rs = RecordStore.openRecordStore("i.f" + (!longlive ? "t" : "") + s, true);
						rs.addRecord(bytes, 0, bytes.length);
						rs.closeRecordStore();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			Image i = Image.createImage(bytes, 0, bytes.length);
			if(longlive) images.put(u, i);
			return i;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			//throw new IOException(e.toString());
		}/* finally {
			if(bais != null)
				try {
					bais.close();
				} catch (Exception e) {
				}
		}*/
		return null;
		//return Image.createImage(is);
	}

	public static void clearImages() {
		clearImageIndexes();
		String[] s = RecordStore.listRecordStores();
		for(int i = 0; i < s.length; i++) {
			if(s[i].startsWith("timgcache") || s[i].startsWith("i.")) {
				try {
					RecordStore.deleteRecordStore(s[i]);
				} catch (Exception e) {
				}
			}
		}
	}

	public static void clearProfiles() {
		String[] s = RecordStore.listRecordStores();
		for(int i = 0; i < s.length; i++) {
			if(s[i].startsWith("tprf")) {
				try {
					RecordStore.deleteRecordStore(s[i]);
				} catch (Exception e) {
					
				}
			}
		}
	}
	
	public static void removeTempImages() {
		String[] s = RecordStore.listRecordStores();
		for(int i = 0; i < s.length; i++) {
			if(s[i].startsWith("i.ft")) {
				try {
					RecordStore.deleteRecordStore(s[i]);
				} catch (Exception e) {
				}
			}
		}
		for(Enumeration e = indexes.keys(); e.hasMoreElements(); ) {
			String k = (String) e.nextElement();
			Object[] v = (Object[]) indexes.get(k);
			if(!((Boolean)v[1]).booleanValue()) {
				indexes.remove(k);
			}
		}
		writeImageIndexes();
	}


}
