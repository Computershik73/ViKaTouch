package vikatouch.attachments;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import vikatouch.json.JSONBase;

public class PhotoSize
{
	public int height;
	public String url;
	public String type;
	public int width;
	
	public static PhotoSize[] parseSizes(JSONArray jsonArray)
	{
		PhotoSize[] sizes = new PhotoSize[10];
		if(jsonArray == null)
			return sizes;
		try
		{
			int len = jsonArray.length();
			for(int i = 0; i < len; i++)
			{
				if(i >= 10)
				{
					break;
				}
				sizes[i] = parseSize(jsonArray.getJSONObject(i));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return sizes;
	}
	
	public static PhotoSize parseSize(JSONObject jsonObject)
	{
		PhotoSize ps = new PhotoSize();
		ps.height = jsonObject.optInt("height");
		if(!jsonObject.isNull("url"))
		{
			ps.url = JSONBase.fixJSONString(jsonObject.optString("url"));
		}
		else
		{
			ps.url = JSONBase.fixJSONString(jsonObject.optString("src"));
		}
		ps.type = jsonObject.optString("type");
		ps.width = jsonObject.optInt("width");
		return ps;
	}
	
	public static PhotoSize getSize(PhotoSize[] sizes, String type)
	{
		for(int i = 0; i < sizes.length; i++)
		{
			if(sizes[i] != null && sizes[i].type.equalsIgnoreCase(type))
			{
				return sizes[i];
			}
		}
		return null;
	}
	
	public static PhotoSize searchNearestSize(PhotoSize[] sizes, int tW)
	{
		PhotoSize curr = null;
		int currD = 0xFFFF;
		for(int i = 0; i < sizes.length; i++)
		{
			if(sizes[i] != null)
			{
				int d = Math.abs(tW - sizes[i].width);
				if(d<currD)
				{
					currD = d;
					curr = sizes[i];
				}
			}
		}
		return curr;
	}
	public static PhotoSize searchSmallerSize(PhotoSize[] sizes, int tW)
	{
		PhotoSize curr = null;
		int currD = 0xFFFF;
		for(int i = 0; i < sizes.length; i++)
		{
			if(sizes[i] != null)
			{
				int d = tW - sizes[i].width;
				if(d < 0) continue;
				if(d < currD)
				{
					currD = d;
					curr = sizes[i];
				}
			}
		}
		return curr;
	}
}