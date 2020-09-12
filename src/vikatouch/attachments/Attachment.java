package vikatouch.attachments;

import org.json.me.JSONObject;

import vikatouch.json.JSONBase;

public abstract class Attachment
	extends JSONBase
{
	public String type;
	
	public int getDrawHeight() { return 0; }
	
	public int attNumber;
	
	// да, для всего. Т.к. кнопка одна, а координаты городить слишком мелко.
	public void press () { }
	
	public static Attachment parse(JSONObject json)
	{
		String type = json.optString("type");
		Attachment result = null;
		
		if(type == null)
		{
			return null;
		}
		try
		{
			if(type.equals("photo"))
			{
				result = new PhotoAttachment();
				result.json = json.getJSONObject("photo");
			}
			else if(type.equals("video"))
			{
				result = new VideoAttachment();
				result.json = json.getJSONObject("video");
			}
			else if(type.equals("audio"))
			{
				result = new AudioAttachment();
				result.json = json.getJSONObject("audio");
			}
			else if(type.equals("doc"))
			{
				result = new DocumentAttachment();
				result.json = json.getJSONObject("doc");
			}
			else if(type.equals("link"))
			{
				result = new LinkAttachment();
				result.json = json.getJSONObject("link");
			}
			else if(type.equals("market"))
			{
				result = new UnsupportedAttachment();
			}
			else if(type.equals("market_album"))
			{
				result = new UnsupportedAttachment();
			}
			else if(type.equals("wall"))
			{
				result = new WallAttachment();
				result.json = json.getJSONObject("wall");
			}
			else if(type.equals("wall_reply"))
			{
				result = new WallReplyAttachment();
				result.json = json.getJSONObject("wall_reply");
			}
			else if(type.equals("sticker"))
			{
				result = new StickerAttachment();
				result.json = json.getJSONObject("sticker");
			}
			else if(type.equals("gift"))
			{
				result = new UnsupportedAttachment();
				//result = new GiftAttachment();
				//result.json = json.getJSONObject("gift");
			}
		}
		catch(Exception e)
		{
			if(result == null)
			{
				result = new ErrorAttachment(e.toString());
			}
		}
		if(result != null)
		{
			result.parseJSON();
		}
		return result;
	}

}
