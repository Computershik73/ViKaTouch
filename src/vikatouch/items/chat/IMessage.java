package vikatouch.items.chat;

/**
 * @author Shinovon
 * 
 */
public interface IMessage {

	public int getMessageId();

	public int getFromId();

	public String getText();

	public void setName(String n);

	public void setRead(boolean i);

	public boolean isRead();

}
