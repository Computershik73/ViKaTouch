package vikatouch;

import com.nokia.mid.ui.SoftNotification;

public interface NotificationListener {
	
	public void notificationSelected(SoftNotification notification);
	
	public void notificationDismissed(SoftNotification notification);

}
