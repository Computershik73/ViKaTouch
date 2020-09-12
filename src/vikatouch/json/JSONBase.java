package vikatouch.json;

import org.json.me.JSONObject;

import vikatouch.utils.VikaUtils;

public abstract class JSONBase
{
	public JSONObject json;
	
	public abstract void parseJSON();
	
	public final static String fixJSONString(String jsonString)
	{
		if(jsonString == null || jsonString == "")
		{
			return "";
		}
		return VikaUtils.replace(VikaUtils.replace(VikaUtils.replace(jsonString, "\\/", "/"), "\\n", "\n"), "\\\"", "\"");
	}

}
