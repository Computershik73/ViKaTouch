package vikatouch.utils;

import vikatouch.json.JSONBase;

/**
 * @author Shinovon
 *
 */
public class ProfileObject {

	private int id;
	private String firstName;
	private String lastName;
	private String url;
	
	public ProfileObject(int id, String name, String url) {
		if(url == null || url.indexOf("camera_50.png") > -1) {
			url = null;
		} else if(url.indexOf("\\/") > -1) {
			url = JSONBase.fixJSONString(url);
		}
		this.id = id;
		this.firstName = name;
		this.url = url;
	}
	
	public ProfileObject(int id, String firstName, String lastName, String url) {
		if(url.indexOf("camera_50.png") > -1) {
			url = null;
		} else if(url.indexOf("\\/") > -1) {
			url = JSONBase.fixJSONString(url);
		}
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return lastName == null ? firstName : firstName + " " + lastName;
	}

	public String getUrl() {
		return url == null ? "camera_50" : url;
	}

	public String getFirstName() {
		return firstName;
	}

}
