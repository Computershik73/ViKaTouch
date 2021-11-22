package vikatouch;

import com.nokia.mid.ui.SoftNotification;
import com.nokia.mid.ui.SoftNotificationException;
import com.nokia.mid.ui.SoftNotificationListener;


/**
 * @author Shinovon
 *
 */
class SoftNotificationInvoker {
	
	//private static 
	
	 static int softNotification(String groupText, final String text) {
		try {
			SoftNotification o = SoftNotification.newInstance();
			o.setText(text, groupText);
			SoftNotificationListener listener = new SoftNotificationListener() {

				
				public void notificationSelected(SoftNotification notification) {
					// TODO Auto-generated method stub
					
					VikaTouch.sendLog("openDialognotificationSelected");	
					try {
					Integer did = (Integer)VikaTouch.hash.get(new Integer (notification.getId()));
					Dialogs.openDialog(did.intValue());
					} catch (Throwable ee) {
						VikaTouch.sendLog("didget " + ee.getMessage());	
					}
					//return;
				}
				public void notificationDismissed(SoftNotification notification) {
					// TODO Auto-generated method stub
					//VikaTouch.sendLog("openDialognotificationDismissed");	
					//return;
				}
				
				
			};
		//	if(listener != null) {
				o.setListener(listener);
				/*(new SoftNotificationListener() {
					public void notificationDismissed(SoftNotification notification) {
						//listener.closed(notification.getId());
					}
					public void notificationSelected(SoftNotification notification) {
						//listener.selected(notification.getId());
					}
				});*/
			//}
			o.post();
			return o.getId();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return -1000;
	}
	
	  public static int softNotification(int id, String text, final String groupText, SoftNotificationListener listener) {
		//try {
			
		  SoftNotification o = SoftNotification.newInstance(VikaTouch.a);
		  /*try {
			o.remove();
		} catch (SoftNotificationException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}*/
			try {
				o.setText(text, groupText);
			} catch (SoftNotificationException e1) {
				// TODO Auto-generated catch block
				VikaTouch.sendLog("SetTextSoftNotificationInvokersoftNotification" + e1.getMessage());
				e1.printStackTrace();
			}
			//if(listener != null)
				o.setListener(listener);
				/*(new SoftNotificationListener() {
					//int a = 0;
					public void notificationDismissed(SoftNotification notification) {
						//listener.notificationDismissed(notification);
						VikaTouch.sendLog("notification was dismissed");
					}
					public void notificationSelected(SoftNotification notification) {
						VikaTouch.sendLog("notification was selected");
						Dialogs.openDialog(notification.getId());
						//listener.notificationSelected(notification);
					}
				});*/
				
			try {
				o.post();
			} catch (SoftNotificationException e) {
				// TODO Auto-generated catch block
				VikaTouch.sendLog("PostSoftNotificationInvokersoftNotification" + e.getMessage());
				e.printStackTrace();
			}
			try {
			VikaTouch.hash.put(new Integer(o.getId()), new Integer(id));
			} catch (Throwable e2) {
				VikaTouch.sendLog("hashput" + e2.getMessage());
				return 1;
			}
			return o.getId();
		/*} catch (Throwable e) {
			VikaTouch.sendLog("SoftNotificationInvokersoftNotification" + e.getMessage());
			e.printStackTrace();
			return -1000;
		}*/
		
	}
	
	static void init() {
	}

}
