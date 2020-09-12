package vikamobilebase;

//UNUSED
public class HttpMultipartRequest
{
	/*
	private byte[] byteArray = null;
	private String url = null;

	public HttpMultipartRequest(String url, Hashtable var2, String var3, String var4, String var5, byte[] data)
	{
		this.url = url;
		String var1 = "----------V2ymHFg03ehbqgZCaKO6jy";
		String var7 = getBoundaryMessage("----------V2ymHFg03ehbqgZCaKO6jy", var2, var3, var4, var5);
		var1 = "\r\n--" + var1 + "--\r\n";
		ByteArrayOutputStream var8;
		try
		{
			var8 = new ByteArrayOutputStream();
			var8.write(var7.getBytes());
			var8.write(data);
			var8.write(var1.getBytes());
			this.byteArray = var8.toByteArray();
			var8.close();
		}
		catch (Exception e)
		{
			
		}
	}
	
	private static String getBoundaryMessage(String var0, Hashtable var1, String var2, String var3, String var4)
	{
		StringBuffer var5 = (new StringBuffer("--")).append(var0).append("\r\n");
		Enumeration var6 = var1.keys();

		while (var6.hasMoreElements())
		{
			String var7 = (String) var6.nextElement();
			String var8 = (String) var1.get(var7);
			var5.append("Content-Disposition: form-data; name=\"").append(var7).append("\"\r\n\r\n").append(var8)
					.append("\r\n--").append(var0).append("\r\n");
		}

		var5.append("Content-Disposition: form-data; name=\"").append(var2).append("\"; filename=\"").append(var3)
				.append("\"\r\nContent-Type: ").append(var4).append("\r\n\r\n");
		return var5.toString();
	}

	public final byte[] send()
	{
		HttpConnection var1 = null;
		InputStream var2 = null;
		ByteArrayOutputStream var3 = new ByteArrayOutputStream();
		byte[] var4 = null;

		try {
			(var1 = (HttpConnection) Connector.open(this.url)).setRequestProperty("Content-Type",
					"multipart/form-data; boundary=----------V2ymHFg03ehbqgZCaKO6jy");
			var1.setRequestMethod("POST");
			OutputStream var5;
			(var5 = var1.openOutputStream()).write(this.byteArray);
			var5.close();
			var2 = var1.openInputStream();

			int var13;
			while ((var13 = var2.read()) != -1)
			{
				var3.write(var13);
			}

			var4 = var3.toByteArray();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				var3.close();
				if (var2 != null)
				{
					var2.close();
				}

				if (var1 != null)
				{
					var1.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}

		return var4;
	}
	*/
}
