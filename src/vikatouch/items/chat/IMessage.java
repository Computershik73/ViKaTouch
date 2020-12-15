package vikatouch.items.chat;

import vikatouch.screens.ChatScreen;

public interface IMessage {

	public int getMessageId();

	public int getFromId();

	public String getText();
	
	public void setName(String name, ChatScreen chatScreen);
	
	public void setRead(boolean isRead, ChatScreen chatScreen);
	
	public boolean isRead();

}
