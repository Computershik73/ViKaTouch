package vikatouch.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class HttpMultipartRequest {
	
	private byte[] aByteArray15 = null;
	private String aString16 = null;

	public HttpMultipartRequest(String var1, Hashtable var2, String var3, String var4, String var5, byte[] var6) {
		this.aString16 = var1;
		var1 = "----------V2ymHFg03ehbqgZCaKO6jy";
		String var7 = getBoundaryMessage("----------V2ymHFg03ehbqgZCaKO6jy", var2, var3, var4, var5);
		var1 = "\r\n--" + var1 + "--\r\n";
		ByteArrayOutputStream var8;
		try
		{
		(var8 = new ByteArrayOutputStream()).write(var7.getBytes());
		var8.write(var6);
		var8.write(var1.getBytes());
		this.aByteArray15 = var8.toByteArray();
		var8.close();
		}catch(Exception e)
		{
			
		}
	}
	
	private static String getBoundaryMessage(String var0, Hashtable var1, String var2, String var3, String var4) {
		StringBuffer var5 = (new StringBuffer("--")).append(var0).append("\r\n");
		Enumeration var6 = var1.keys();

		while (var6.hasMoreElements()) {
			String var7 = (String) var6.nextElement();
			String var8 = (String) var1.get(var7);
			var5.append("Content-Disposition: form-data; name=\"").append(var7).append("\"\r\n\r\n").append(var8)
					.append("\r\n--").append(var0).append("\r\n");
		}

		var5.append("Content-Disposition: form-data; name=\"").append(var2).append("\"; filename=\"").append(var3)
				.append("\"\r\nContent-Type: ").append(var4).append("\r\n\r\n");
		return var5.toString();
	}

	public final byte[] send() {
		HttpConnection var1 = null;
		InputStream var2 = null;
		ByteArrayOutputStream var3 = new ByteArrayOutputStream();
		byte[] var4 = null;
		String s = this.aString16;
		try {
		/*	if(vikaMobile.mobilePlatform.indexOf("BlackBerry") == 0)
			{
				if(vikaMobile.bbIsWifi)
				{
					s += ";deviceside=true;interface=wifi";
				}
				else
				{
					s += ";deviceside=true";
				}
			}*/
			(var1 = (HttpConnection) Connector.open(s)).setRequestProperty("Content-Type",
					"multipart/form-data; boundary=----------V2ymHFg03ehbqgZCaKO6jy");
			var1.setRequestMethod("POST");
			OutputStream var5;
			(var5 = var1.openOutputStream()).write(this.aByteArray15);
			var5.close();
			var2 = var1.openInputStream();

			int var13;
			while ((var13 = var2.read()) != -1) {
				var3.write(var13);
			}

			var4 = var3.toByteArray();
		} catch (Exception var11) {
			var11.printStackTrace();
		} finally {
			try {
				var3.close();
				if (var2 != null) {
					var2.close();
				}

				if (var1 != null) {
					var1.close();
				}
			} catch (Exception var10) {
				var10.printStackTrace();
			}

		}

		return var4;
	}
	
}