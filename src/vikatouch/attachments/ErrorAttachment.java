package vikatouch.attachments;

public class ErrorAttachment
	extends Attachment
{
	
	public String description;

	public ErrorAttachment(String desc)
	{
		this.description = desc;
		this.type = null;
	}

	public void parseJSON()
	{
		
	}

}
