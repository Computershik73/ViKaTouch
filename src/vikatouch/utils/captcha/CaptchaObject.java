package vikatouch.utils.captcha;

import java.io.IOException;

import javax.microedition.lcdui.Image;

import org.json.me.JSONObject;

import vikatouch.json.JSONBase;
import vikatouch.utils.VikaUtils;

public class CaptchaObject
	extends JSONBase
{

	public String captchasid;
	protected String captchaimg;

	public CaptchaObject(JSONObject json)
	{
		this.json = json;
	}

	public void parseJSON()
	{
		captchasid = json.optString("captcha_sid"); 
		captchaimg = fixJSONString(json.optString("captcha_img")); 
	}
	
	public Image getImage()
	{
		try {
			return VikaUtils.downloadImage(captchaimg);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
