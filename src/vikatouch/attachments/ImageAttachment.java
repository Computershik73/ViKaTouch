package vikatouch.attachments;

import javax.microedition.lcdui.Image;

/**
 * @author Shinovon
 * 
 */
public abstract class ImageAttachment extends Attachment {
	public abstract Image getPreviewImage();

	public abstract Image getFullImage();

	// public abstract String getPreviewImageUrl();
	// public abstract String getFullImageUrl();

	public abstract Image getImage(int height); // Может таки не по индексу, а по требуемой высоте всё получать?))

}
