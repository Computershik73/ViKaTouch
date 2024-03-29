package vikatouch.items.chat;

import java.util.Vector;
import vikatouch.items.smile;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.VikaCanvas;
import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.menu.items.OptionItem;
import ru.nnproject.vikaui.popup.AutoContextMenu;
import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.screen.ScrollableCanvas;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import ru.nnproject.vikaui.utils.text.TextBreaker;
import vikatouch.VikaTouch;
import vikatouch.attachments.Attachment;
import vikatouch.attachments.AudioAttachment;
import vikatouch.attachments.DocumentAttachment;
import vikatouch.attachments.PhotoAttachment;
import vikatouch.attachments.StickerAttachment;
import vikatouch.attachments.VideoAttachment;
import vikatouch.attachments.VoiceAttachment;
import vikatouch.attachments.WallAttachment;
import vikatouch.locale.TextLocal;
import vikatouch.screens.ChatScreen;
import vikatouch.screens.menu.ChatMembersScreen;
import vikatouch.screens.page.GroupPageScreen;
import vikatouch.screens.page.ProfilePageScreen;
import vikatouch.settings.Settings;
import vikatouch.updates.VikaUpdate.VEUtils;
import vikatouch.utils.IntObject;
import vikatouch.utils.ProfileObject;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.text.CountUtils;
import vikatouch.utils.url.URLBuilder;
import vikatouch.utils.url.URLDecoder;

/**
 * @author Feodor0090
 * 
 */
public class MsgItem extends ChatItem implements IMenu, IMessage {
	public MsgItem(JSONObject json) {
		super(json);
		VikaTouch.needstoRedraw=true;
		smilesarray = new Vector(0, 1);
	}

	private int mid;
	private String[] drawText;
	private String name = "";
	public boolean foreign;
	public static int msgWidth = 300;
	public static int margin = 10;
	public static int attMargin = 5;
	public int linesC;
	private String time = "";
	public boolean showName;

	private int attH = -1;
	private int fwdH = -1;

	private boolean hasReply;
	public String replyName;
	public String replyText;
	public boolean attsReady;

	private boolean isRead = true;
	public MsgItem[] forward;

	public int forwardedX = -1;
	public int forwardedW = -1;
	//public smiles[] smilesarray;
	public Vector smilesarray = new Vector(0, 1);
	public String codestext="";
	public  int llll;
	public void ChangeText(String s) {
		VikaTouch.needstoRedraw=true;
		text = s;
		int h1 = Font.getFont(0, 0, 8).getHeight();
		drawText = TextBreaker.breakText(text, Font.getFont(0, 0, Font.SIZE_SMALL), msgWidth - h1);
		linesC = drawText.length;
		itemDrawHeight = h1 * (linesC + 1);
		
	}
	
	public static final long unsignedIntToLong(byte[] b) 
	{
	    long l = 0;
	    l |= b[0] & 0xFF;
	    l <<= 8;
	    l |= b[1] & 0xFF;
	    l <<= 8;
	    l |= b[2] & 0xFF;
	    l <<= 8;
	    l |= b[3] & 0xFF;
	    return l;
	}
	
	
	public static final int unsignedShortToInt(byte[] b) 
	{
	    int i = 0;
	    i |= b[0] & 0xFF;
	    i <<= 8;
	    i |= b[1] & 0xFF;
	    return i;
	}

	public void parseJSON() {
		VikaTouch.needstoRedraw=true;
		super.parseJSON();
		//VikaUtils.logToFile(json.toString());
		msgWidth = DisplayUtils.width - (DisplayUtils.width <= 240 ? 10 : 40);
		margin = (DisplayUtils.width <= 240 ? 0 : 10);
		try {
			parseAttachments();
			// {"id":354329,"important":false,"date":1596389831,"attachments":[],"out":0,"is_hidden":false,"conversation_message_id":7560,"fwd_messages":[],"random_id":0,"text":"Будет
			// срач с Лëней или он уже потерял
			// интерес?","from_id":537403336,"peer_id":537403336}

			foreign = !("" + json.optInt("from_id")).equalsIgnoreCase(VikaTouch.userId);
			mid = json.optInt("id");

			int h1 = Font.getFont(0, 0, 8).getHeight();
			// 27864
			String textt = "";
			int ii = 0;
			// String a = "\u05D0\u05D1";
			// byte[] xxx = text.getBytes("UTF-8");

			// for (int ii=0; ii<xxx.length-1; ii++) {
			/// textt += Integer.toBinaryString(xxx[ii]).+ " ";
			// }
			// textt = VikaUtils.replace(text, from, to);
			codestext ="";
			int smilescount=0;
			if (foreign) {
			//if (ChatScreen.peerlanguage==null) {
				//text = TextLocal.translateText(text, "en", VikaTouch.mylanguage);
			//} else {
			//if (VikaTouch.mylanguage!=ChatScreen.peerlanguage) {
			//	text = TextLocal.translateText(text, ChatScreen.peerlanguage, VikaTouch.mylanguage);
			//}
			//}
			}
			//VikaUtils.logToFile(" "+String.valueOf((long)text.toCharArray()[0])+ " ");
			/*int value = 0;
			byte[] a = new byte [4];
			a [0] =  (byte)text.toCharArray()[0];
			a [1] = (byte)text.toCharArray()[1];
			a [2] = (byte)text.toCharArray()[2];
			a [3] = (byte)text.toCharArray()[3];
			for (int j=0; j<=3; j++) {
			    value = (value << 8) + (a[j] & 0xFF);
			}
			long coode = unsignedIntToLong(a);
			int codede = unsignedShortToInt(a);*/
			//VikaUtils.logToFile(" "+String.valueOf(value)+ " "+String.valueOf(coode)+String.valueOf(codede)+" ");
			
			//VikaUtils.logToFile(String.valueOf(text.length()));
			while (ii < text.length()) {
				
				//ByteBuffer.wrap(bytes).getInt();
				if (((long)text.toCharArray()[ii] >= 55350) && ((long)text.toCharArray()[ii] <= 55360)) {
					codestext = codestext.concat(" ").concat(String.valueOf((long) text.toCharArray()[ii])).concat(" ");
					ii++;
					codestext = codestext.concat(" ").concat(String.valueOf((long) text.toCharArray()[ii])).concat(" ");
					textt+="     ";
					//smilesarray.addElement(new smile(ii-1+2*smilescount, Integer.toHexString((int) text.toCharArray()[ii-1]).toUpperCase()+Integer.toHexString((int) text.toCharArray()[ii]).toUpperCase()+".png"));
					//String hex = Integer.toHexString((int) text.toCharArray()[ii-1]);

					//int parsedResult = (int) Long.parseLong(hex, 16);
					//VikaTouch.sendLog(String.valueOf(parsedResult));
					//Thread.sleep(500);
					//String hex2 = Integer.toHexString((int) text.toCharArray()[ii]);

					//int parsedResult2 = (int) Long.parseLong(hex2, 16);
					//VikaTouch.sendLog(String.valueOf(parsedResult)+ " " + String.valueOf(parsedResult2));
					//String str = String.valueOf((int)(text.toCharArray()[ii-1])) + String.valueOf((int)text.toCharArray()[ii]);
					// String strr = new String(str.getBytes("UTF-16"), "UTF-16");
					//VikaTouch.sendLog(
							//VikaUtils.toUTF8Array(
					//				URLDecoder.encode(String.valueOf(text.toCharArray()[ii-1]))+URLDecoder.encode(String.valueOf(text.toCharArray()[ii]))
									//)
					//);
					String str = String.valueOf(text.toCharArray()[ii-1])+String.valueOf(text.toCharArray()[ii]);
				/*	for (byte b : a.getBytes("UTF-8")) {
						  sb.append(String.format("%02x", 0xff & b));
					}
					StringBuilder*/ 
					//+String.valueOf(text.toCharArray()[ii]).charAt(0)).toCharArray()[0]);
					//byte[] utfString = str.getBytes("UTF-8");
					//str = new String(utfString,"UTF-8") ;
					//byte[] utf8Bytes = str.getBytes();

					 // String result = new String(utf8Bytes);
					//int a = (int)text.toCharArray()[ii-1];
					//int b = (int)text.toCharArray()[ii];
					//VikaTouch.sendLog(String.valueOf(a)+ " "+ String.valueOf(b));
					//String c = " "+String.valueOf((char) a) + " " + String.valueOf((char) b);
					//VikaUtils.intToByteArray(a);
					//VikaTouch.sendLog(VikaUtils.bytesToHex(VikaUtils.intToByteArray(a)));
					//VikaTouch.sendLog(VikaUtils.bytesToHex(str.getBytes("UTF-8")).toLowerCase()+".png");
					smilesarray.addElement(new smile(ii-1+2*smilescount, VikaUtils.bytesToHex(str.getBytes("UTF-8")).toLowerCase()+".png"));
					//VikaUtils.logToFile(String.valueOf(ii-1+2*smilescount)+ " "+VikaUtils.bytesToHex(str.getBytes("UTF-8")).toLowerCase()+".png" );
				//VikaTouch.sendLog(String.valueOf((char)((int)text.toCharArray()[ii-1]))+ String.valueOf((char)((int)text.toCharArray()[ii])));
					//char.
					//VikaTouch.sendLog(String.valueOf((long) text.toCharArray()[ii-1]) + " " + String.valueOf((long) text.toCharArray()[ii]) + " "+ String.valueOf((int) text.toCharArray()[ii-1]) + " " + String.valueOf((int) text.toCharArray()[ii]) + " "+ VikaUtils.decimal2hex((int) text.toCharArray()[ii-1])+ " " + VikaUtils.decimal2hex((int) text.toCharArray()[ii]));
					//smilesarray.addElement(new smile(ii-1+2*smilescount, "/emoji/D83DDE00.png"));
					smilescount++;
					//Integer.toHexString((int) text.toCharArray()[ii]).toUpperCase();
					//55357 56838
					/*if (((long) text.toCharArray()[ii]) == 56611) {
						textt+="     ";
						//xdImg= Image.createImage("/D83DDE06.png");
						smilesarray.addElement(new smile(ii-1+2*smilescount, "/emoji/D83DDE02.png"));
						smilescount++;
					}
					
					if (((long) text.toCharArray()[ii]) == 56834) {
						textt += String.valueOf((char) 57349);
					}
					if (((long) text.toCharArray()[ii]) == 56832) {
						textt += String.valueOf((char) 57350);
					}
					if (((long) text.toCharArray()[ii]) == 56835) {
						textt += String.valueOf((char) 57351);
					}
					if (((long) text.toCharArray()[ii]) == 56836) {
						textt += String.valueOf((char) 57352);
					}
					if (((long) text.toCharArray()[ii]) == 56833) {
						textt += String.valueOf((char) 57353);
					}
					if (((long) text.toCharArray()[ii]) == 56834) {
						textt+="     ";
						//xdImg= Image.createImage("/D83DDE06.png");
						smilesarray.addElement(new smile(ii-1+2*smilescount, "/emoji/D83DDE02.png"));
						smilescount++;
					}
					
					if (((long) text.toCharArray()[ii]) == 56837) {
						textt+="     ";
						//xdImg= Image.createImage("/D83DDE06.png");
						smilesarray.addElement(new smile(ii-1+2*smilescount, "/emoji/D83DDE05.png"));
						smilescount++;
					}
					if (((long) text.toCharArray()[ii]) == 56838) {
						textt+="     ";
						//xdImg= Image.createImage("/D83DDE06.png");
						smilesarray.addElement(new smile(ii-1+2*smilescount, "/emoji/D83DDE06.png"));
						smilescount++;
						//textt += String.valueOf((char) 57355);
					}
					if (((long) text.toCharArray()[ii]) == 56841) {
						textt+="     ";
						//xdImg= Image.createImage("/D83DDE06.png");
						smilesarray.addElement(new smile(ii-1+2*smilescount, "/emoji/D83DDE09.png"));
						smilescount++;
					}
					if (((long) text.toCharArray()[ii]) == 56842) {
						textt+="     ";
						//xdImg= Image.createImage("/D83DDE06.png");
						smilesarray.addElement(new smile(ii-1+2*smilescount, "/emoji/D83DDE0A.png"));
						smilescount++;
					}*/

				} else {
					//if (((long) text.toCharArray()[ii]) == 56841) {
					//	
					//} else {
					textt += String.valueOf(text.toCharArray()[ii]);
					//}
					//textt+=" "+String.valueOf((long) text.toCharArray()[ii])+" ";
					//codestext = codestext.concat(" ").concat(String.valueOf((long) text.toCharArray()[ii])).concat(" ");
				}
				// String.valueOf((char) 27864);
				// (long) text.toCharArray()[ii];

				// textt+=(long)text.toCharArray()[ii]+" ";

				ii++;
			}
			// int b1, b2, ucs;
			// b1 = str[0] & 0x1f; b2 = str[1] & 0x3f;
			// ucs = (b1 << 6) | b2;

			// textt = VikaUtils.replace(textt, "5535756834",
			// String.valueOf((char)57349));

			text = textt;
		//	VikaTouch.sendLog(codestext);
			drawText = TextBreaker.breakText(text, Font.getFont(0, 0, Font.SIZE_SMALL),
					(forwardedW == -1 ? msgWidth : forwardedW) - h1);
			// отладка

			linesC = drawText.length;

		//	itemDrawHeight = h1 * (linesC + 1);

			JSONObject reply = json.optJSONObject("reply_message");
			JSONArray fwds = json.optJSONArray("fwd_messages");
			if (reply != null) {
				boolean breakReplyText = true;
				hasReply = true;
				replyText = reply.optString("text");
				if (replyText == null || replyText == "" || replyText.length() <= 0) {
					replyText = "";
					JSONArray replyAttachs = reply.optJSONArray("attachments");
					JSONArray replyFwds = reply.optJSONArray("fwd_messages");
					if (replyAttachs != null) {
						if (replyAttachs.length() == 1) {
							JSONObject att = replyAttachs.optJSONObject(0);
							String type = att.optString("type");
							// VikaTouch.sendLog(type);
							if (type.equals("photo")) {
								replyText = "[" + TextLocal.inst.get("msg.attach.photo") + "]";
							} else if (type.equals("audio_message")) {
								replyText = "[" + TextLocal.inst.get("msg.attach.voice") + "]";
							} else if (type.equals("audio")) {
								replyText = "[" + TextLocal.inst.get("msg.attach.audio") + "]";
							} else if (type.equals("wall_reply")) {
								replyText = "[Комментарий]";
							} else {
								replyText = "[" + TextLocal.inst.get("msg.attach.attachment") + "]";
							}
						} else if (replyAttachs.length() > 1) {
							replyText = "[" + TextLocal.inst.get("msg.attach.attachments") + " ("
									+ replyAttachs.length() + ")]";
						}
						breakReplyText = false;
					} else if (replyFwds != null) {
						replyText = CountUtils.countStrMessages(replyFwds.length());
						breakReplyText = false;
					} else {
						replyText = "";
					}
				}
				if (breakReplyText)
					replyText = TextBreaker.shortText(replyText, msgWidth - h1 - h1, Font.getFont(0, 0, 8));

				int fromId = reply.optInt("from_id");
				if (fromId == Integer.parseInt(VikaTouch.userId)) {
					replyName = TextLocal.inst.get("msg.you");
				} else if (VikaTouch.profiles.containsKey(new IntObject(fromid))) {
					replyName = ((ProfileObject)VikaTouch.profiles.get(new IntObject(fromid))).getName();
				}
			}
			if (fwds != null && fwds.length() > 0) {
				int fwdX = forwardedX == -1 ? (foreign ? (margin * 2) : (DisplayUtils.width - msgWidth))
						: (forwardedX + margin);
				int fwdW = forwardedX == -1 ? (msgWidth - margin) : (forwardedW - margin);
				forward = new MsgItem[fwds.length()];
				try {
					for (int i = 0; i < fwds.length(); i++) {
						MsgItem m = new MsgItem(fwds.optJSONObject(i));
						m.forwardedW = fwdW;
						m.forwardedX = fwdX;
						m.parseJSON();
						int fromId = m.fromid;

						if (VikaTouch.profiles.containsKey(new IntObject(fromId))) {
							m.name = ((ProfileObject)VikaTouch.profiles.get(new IntObject(fromId))).getName();
						} else if(m.foreign) {
							m.name = TextLocal.inst.get("msg.you");
						} else {
							m.name = "id" + fromId;
						}
						m.showName = true;
						m.loadAtts();
						forward[i] = m;
					}
				} catch (RuntimeException e) {
				}
			}
		} catch (Exception e) {
			text = e.toString();
			e.printStackTrace();
		}

		// experimental
		{
			// if(text.equals("Т")) VikaTouch.popup(new
			// InfoPopup(json.toString(), null));
		}
	}

	public void loadAtts() {
		VikaTouch.needstoRedraw=true;
		if (attH < 0) {
			attH = 0;
			// prepairing attachments
			if (!attsReady) {
				attsReady = true;
				try {
					 llll = attachments.length;
					for (int i = 0; i < attachments.length; i++) {
						Attachment at = attachments[i];
						if (at == null)
							continue;
						if (at instanceof PhotoAttachment && !Settings.isLiteOrSomething) {
							((PhotoAttachment) at).loadForMessage();
							attH += at.getDrawHeight() +  attMargin;
						}
						if (at instanceof VideoAttachment && !Settings.isLiteOrSomething) {
							((VideoAttachment) at).loadForMessage();
							attH += at.getDrawHeight() +  attMargin;
						}
						if (at instanceof VoiceAttachment) {
							((VoiceAttachment) at).mid = mid;
							attH += at.getDrawHeight() +  attMargin;
						}
						
						if (at instanceof AudioAttachment) {	
							attH += at.getDrawHeight() +  attMargin;
						}
						
						if (at instanceof StickerAttachment) {
							int stickerH = DisplayUtils.width > 250 ? 128 : 64;
							attH += stickerH + attMargin;
						} else {
							//attH += at.getDrawHeight() + attMargin;
						}
						if (at instanceof WallAttachment) {
							//((WallAttachment) at).loadForMessage();
							attH += at.getDrawHeight() +  attMargin;
						} 
						
						
					}
					if (attH != 0) {
						attH += attMargin;
					}
				} catch (Throwable e) {
					attH = 0;
					e.printStackTrace();
				}
			}
		}
		
	}

	public VoiceAttachment findVoice() {
		for (int i = 0; i < attachments.length; i++) {
			Attachment at = attachments[i];
			if (at == null)
				continue;
			if (at instanceof VoiceAttachment) {
				return (VoiceAttachment) at;
			}
		}
		return null;
	}

	private static String[] split(String in, char t) {
		int l = count(in, t) + 1;
		String[] res = new String[l];
		String[] c = VEUtils.singleSplit(in, t);
		res[0] = c[0];
		for (int i = 1; i < l - 1; i++) {
			c = VEUtils.singleSplit(c[1], t);
			res[i] = c[0];
		}
		res[l - 1] = c[1];
		return res;
	}
 
	private static int count(String in, char t) {
		int r = 0;
		char[] c = in.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == t) {
				r++;
			}
		}
		return r;
	}

	public void paint(Graphics g, int y, int scrolled) {
		Font font = Font.getFont(0, 0, 8);
		g.setFont(font);
		
		//if (VikaTouch.needstoRedraw==false) {
		//	return;
		//}
		try {
			//if (!ChatScreen.forceRedraw && y + scrolled + itemDrawHeight < -50)
				//return;
			//if (y + scrolled >DisplayUtils.height) {
				//return;
			//}
			// drawing
			int h1 = font.getHeight();
			int attY = h1 * (linesC + 1 + (showName ? 1 : 0) + (hasReply ? 2 : 0));
			int th = attY + attH + fwdH;
			//VikaUtils.logToFile(" h: "+String.valueOf(attY)+" "+String.valueOf(attH)+" "+String.valueOf(fwdH));
			itemDrawHeight = th;
			int textX = 0;
			int radius = 16;
			int msgWidthInner = (forwardedW == -1 ? msgWidth : forwardedW);
			if (forwardedX != -1) {
				ColorUtils.setcolor(g, ColorUtils.FOREIGNMSG);
				g.fillRoundRect(forwardedX, y, forwardedW, th, radius, radius);
				textX = forwardedX + h1 / 2;
			} else if (foreign) {
				ColorUtils.setcolor(g, ColorUtils.FOREIGNMSG);
				g.fillRoundRect(margin, y, msgWidth, th, radius, radius);
				g.fillRect(margin, y + th - radius, radius, radius);
				textX = margin + h1 / 2;
				if (selected && ScrollableCanvas.keysMode) {
					ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.setStrokeStyle(Graphics.SOLID);
					g.drawRoundRect(margin, y, msgWidth, th, radius, radius);
				}

				if (!isRead) {
					ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
					g.fillArc(margin + msgWidth + 1, y + 16, 8, 8, 0, 360);
				}
			} else {
				ColorUtils.setcolor(g, ColorUtils.MYMSG);
				g.fillRoundRect(DisplayUtils.width - (margin + msgWidth), y, msgWidth, th, radius, radius);
				g.fillRect(DisplayUtils.width - (margin + radius), y + th - radius, radius, radius);
				textX = DisplayUtils.width - (margin + msgWidth) + h1 / 2;
				
				if (selected && ScrollableCanvas.keysMode) {
					ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.setStrokeStyle(Graphics.SOLID);
					g.drawRoundRect(DisplayUtils.width - (margin + msgWidth), y, msgWidth, th, radius, radius);
				}
				if (!isRead) {
					// System.out.println("unread out");
					ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
					g.fillArc(DisplayUtils.width - (margin + msgWidth) - 9, y + 16, 8, 8, 0, 360);
				}
			}
			if (name != null && showName) {
				ColorUtils.setcolor(g, ColorUtils.COLOR1);
				g.drawString(name, textX, y + h1 / 2, 0);
				ColorUtils.setcolor(g, ColorUtils.OUTLINE);
				if (time == null || time.length() < 1) {
					time = getTime();
					// System.out.println("msg time: "+time);
				}
				g.drawString(time, textX - h1 + msgWidthInner - font.stringWidth(time), y + h1 / 2, 0);
			}
			ColorUtils.setcolor(g, ColorUtils.MSGTEXT);
			int symbolspassed=0;
			//smilesarray = new Vector(1, 5);
		//	try {
				for (int i = 0; i < linesC; i++) {
					int errst=0;
					try {
						errst=1;
					if (drawText[i] != null) {
						errst=2;
					//	VikaTouch.sendLog("i = "+String.valueOf(i) + " " + "text="+drawText[i]);
						String s1 = drawText[i];
						errst=3;
						int yy=0;
						errst=4;
						if ((s1.indexOf("[club") > -1 || s1.indexOf("[id") > -1) && s1.indexOf("|") > -1
								&& s1.indexOf("]") > -1) {
							errst=5;
							String[] arr1 = split(s1, '[');
							errst=6;
							int x = textX;
							errst=7;
							yy = y + h1 / 2 + h1 * (i + (showName ? 1 : 0));
							errst=8;
							ColorUtils.setcolor(g, ColorUtils.MSGTEXT);
							errst=9;
							if (arr1[0] != null) {
								errst=10;
								g.drawString(arr1[0], x, yy, 0);
								errst=11;
								x += font.stringWidth(arr1[0]);
								errst=12;
								for (int c = 1; c < arr1.length; c++) {
									errst=13;
									if (arr1[c] != null && arr1[c].indexOf('|') > -1) {
										errst=14;
										String[] arr2 = VEUtils.split(arr1[c], 2, '|');
										errst=15;
										String[] arr3 = VEUtils.singleSplit(arr2[1], ']');
										errst=16;
										String ping = arr3[0];
										errst=17;
										String after = arr3[1];
										errst=18;
										g.setColor(0x2a5885);
										errst=19;
										g.drawString(ping, x, yy, 0);
										errst=20;
										x += font.stringWidth(ping);
										errst=21;
										if (after != null) {
											errst=22;
											ColorUtils.setcolor(g, ColorUtils.MSGTEXT);
											errst=23;
											g.drawString(after, x, yy, 0);
											errst=24;
											x += font.stringWidth(after);
											errst=25;
										}
										errst=26;
									}
									errst=27;
								}
								errst=28;
							}
							errst=29;
						} else {
							errst=30;
							if (s1.equalsIgnoreCase("@all") || s1.startsWith("@all")
									|| s1.equalsIgnoreCase("@online")) {
								errst=31;
								g.setColor(0x2a5885);
							}
							errst=32;
							yy = y + h1 / 2 + h1 * (i + (showName ? 1 : 0));
							errst=33;
							g.drawString(s1, textX, yy, 0);
							errst=34;
						}
						errst=35;
						if (smilesarray.capacity()>0) {
							errst=36;
							//String gf=" ";
							int lil=0;
						for (int li=0; li<smilesarray.capacity(); li++) {
							errst=37;
							
							//VikaTouch.sendLog("F"+String.valueOf(li));
							if (((smile)smilesarray.elementAt(li)).smilePos>=symbolspassed) {
									 if (((smile)smilesarray.elementAt(li)).smilePos<=(symbolspassed+s1.length()))
									 {
								int tempsmPos = ((smile)smilesarray.elementAt(li)).smilePos;
								errst=38;
								String tempsmPath = ((smile)smilesarray.elementAt(li)).smilePath;
								errst=39;
							//	VikaTouch.sendLog("substr" + s1.substring(0, ((smile)smilesarray.elementAt(li)).smilePos-symbolspassed));
								//VikaTouch.sendLog(" smposx = ".concat(String.valueOf(textX+Font.getDefaultFont().stringWidth(s1.substring(0, ((vsmile)smilesarray.elementAt(li)).smilePos-symbolspassed))-6)).concat(" maxwidth = ").concat(String.valueOf(msgWidthInner)));
							if (textX+Font.getDefaultFont().stringWidth(s1.substring(0, ((smile)smilesarray.elementAt(li)).smilePos-symbolspassed))-6<msgWidth - h1) {
								smilesarray.setElementAt(new smile (tempsmPos, tempsmPath, textX+Font.getDefaultFont().stringWidth(s1.substring(0, ((smile)smilesarray.elementAt(li)).smilePos-symbolspassed)), yy+3), li);
							} else {
								lil=0;
								smilesarray.setElementAt(new smile (tempsmPos, tempsmPath, textX-6
										//+Font.getDefaultFont().stringWidth(s1.substring(0, ((smile)smilesarray.elementAt(li)).smilePos-symbolspassed))-6
										, y + h1 / 2 + h1 * (i + 1 + (showName ? 1 : 0))+3), li);
								
								/*if (textX+Font.getDefaultFont().stringWidth(s1.substring(0, ((smile)smilesarray.elementAt(li+1)).smilePos-symbolspassed))-6 -((smile)smilesarray.elementAt(li)).smileX<12) {
								for (int lii=li+1; lii<smilesarray.capacity(); lii++) {
									smilesarray.setElementAt(new smile (((smile)smilesarray.elementAt(lii)).smilePos, ((smile)smilesarray.elementAt(lii)).smilePath, 0, 0), lii);
								}
								}*/
							}
							errst=40;
							//VikaTouch.sendLog(String.valueOf(textX+Font.getDefaultFont().stringWidth(s1.substring(0, ((smile)smilesarray.elementAt(li)).smilePos+1-symbolspassed))));
							//((smile)smilesarray.elementAt(li)).smileX = textX+Font.getDefaultFont().stringWidth(s1.substring(0, ((smile)smilesarray.elementAt(li)).smilePos-symbolspassed));
							//((smile)smilesarray.elementAt(li)).smileY = yy;
							//VikaTouch.sendLog("F"+String.valueOf(li));
							} else {
							/*	VikaTouch.sendLog("smilepos = " + String.valueOf(((smile)smilesarray.elementAt(li)).smilePos)+ "length = " + String.valueOf(symbolspassed+s1.length()));
								if (((smile)smilesarray.elementAt(li)).smilePos<=(symbolspassed+s1.length()+5)) {
									int tempsmPos = ((smile)smilesarray.elementAt(li)).smilePos;
									
									String tempsmPath = ((smile)smilesarray.elementAt(li)).smilePath;
									
								//	VikaTouch.sendLog("substr" + s1.substring(0, ((smile)smilesarray.elementAt(li)).smilePos-symbolspassed));
								smilesarray.setElementAt(new smile (tempsmPos, tempsmPath, textX
										//+Font.getDefaultFont().stringWidth(s1.substring(0, ((smile)smilesarray.elementAt(li)).smilePos-symbolspassed))-6
										, y + h1 / 2 + h1 * (i + 1 + (showName ? 1 : 0))+3), li);
								
								}*/
							}
						}
							//g.drawImage(Image.createImage(((smile)smilesarray.elementAt(li)).smilePath), textX+Font.getDefaultFont().stringWidth(s1.substring(0, ((smile)smilesarray.elementAt(li)).smilePos-symbolspassed)), yy, 0);
							lil++;
						}
						//if (gf!=" ") {
						//VikaTouch.sendLog(gf);
						//}
						errst=41;
						
					}
						errst=42;
						symbolspassed+=s1.length();
						errst=43;
					}
					} catch (Throwable eh) {
						VikaTouch.sendLog(eh.getMessage()+String.valueOf(errst));
					}
				}
		/*	} catch (Throwable t) {
				t.printStackTrace();
			}*/
			//VikaTouch.sendLog("Всего смайлов в облачке: "+String.valueOf(smilesarray.capacity()));
			//smilesarray.removeAllElements();
			String smilesinfo = "";
			if (smilesarray.capacity()>0) {
			for (int li=0; li<smilesarray.capacity(); li++) {
				/*try {
				smilesinfo=smilesinfo+"smile " + String.valueOf(li)+ " path " +  ((smile)smilesarray.elementAt(li)).smilePath + " smileX:" + String.valueOf(((smile)smilesarray.elementAt(li)).smileX) + " smileY:" + String.valueOf(((smile)smilesarray.elementAt(li)).smileY) + " smilePos:" + String.valueOf(((smile)smilesarray.elementAt(li)).smilePos) + " ";
				} catch (Throwable ee) {
					//VikaTouch.sendLog(ee.getMessage());
				}*/
				//smilesinfo=smilesinfo+" "+((smile)smilesarray.elementAt(li)).smilePath+" ";
				try {
					g.drawImage(
							VikaUtils.loadSmile(((smile)smilesarray.elementAt(li)).smilePath),
							//Image.createImage("/emoji/D83DDE00.png"),
							((smile)smilesarray.elementAt(li)).smileX, ((smile)smilesarray.elementAt(li)).smileY, 0);
					//g.drawImage(Image.createImage(((smile)smilesarray.elementAt(li)).smilePath), ((smile)smilesarray.elementAt(li)).smileX , ((smile)smilesarray.elementAt(li)).smileY, 0);
				} catch (Throwable eg) {
					
				}
			}
			}
			if (smilesinfo!="") {
			VikaTouch.sendLog(smilesinfo);
			}
			if (hasReply) {
				ColorUtils.setcolor(g, ColorUtils.MSGTEXT);
				if (replyText != null)
					g.drawString(replyText, textX + h1, y + h1 / 2 + h1 * (linesC + 1 + (showName ? 1 : 0)), 0);
				ColorUtils.setcolor(g, ColorUtils.COLOR1);
				if (replyName != null)
					g.drawString(replyName, textX + h1, y + h1 / 2 + h1 * (linesC + (showName ? 1 : 0)), 0);
				g.fillRect(textX + h1 / 2 - 1, y + h1 / 2 + h1 * (linesC + (showName ? 1 : 0)), 2, h1 * 2);
			}

			// рендер аттачей
			if (attH > 0 /*&& attsReady*/) {
				attY += attMargin;
				for (int i = 0; i < attachments.length; i++) {
					Attachment at = attachments[i];
					if (at == null)
						continue;
					int x1 = foreign ? (margin + attMargin)
							: (DisplayUtils.width - (margin + msgWidthInner) + attMargin);
					if (at instanceof PhotoAttachment) {
						PhotoAttachment pa = (PhotoAttachment) at;
						int rx = forwardedX == -1
								? (foreign ? (margin + attMargin)
										: (DisplayUtils.width - (margin + attMargin) - pa.renderW))
								: (forwardedX + attMargin);
						if (pa.renderImg == null) {
							if (Settings.isLiteOrSomething) {
								g.drawString("Фотография", textX, y + attY, 0);
							}/*else
								g.drawString("Не удалось загрузить изображение", textX, y + attY, 0);
								*/
						} else {
							g.drawImage(pa.renderImg, rx, y + attY, 0);
						}
					} else if (at instanceof VideoAttachment) {
						VideoAttachment va = (VideoAttachment) at;
						int rx = forwardedX == -1
								? (foreign ? (margin + attMargin)
										: (DisplayUtils.width - (margin + attMargin) - va.renderW))
								: (forwardedX + attMargin);
						if (va.renderImg == null) {
							if (Settings.isLiteOrSomething) {
								g.drawString("Видео", textX, y + attY, 0);
							} else
								g.drawString("Не удалось загрузить изображение", textX, y + attY, 0);
						} else {
							g.drawImage(va.renderImg, rx, y + attY, 0);
							g.drawString(va.title, x1, y + attY + va.renderH, 0);
						}
					} else if (at instanceof DocumentAttachment) {
						((DocumentAttachment) at).draw(g, x1, y + attY, msgWidthInner - attMargin * 2);
					} else if (at instanceof WallAttachment) {
						((WallAttachment) at).draw(g, x1, y + attY, msgWidthInner - attMargin * 2);
					} else if (at instanceof StickerAttachment) {
						int stickerW = DisplayUtils.width > 250 ? 128 : 64;
						int rx = forwardedX == -1
								? (foreign ? (margin + attMargin)
										: (DisplayUtils.width - (margin + attMargin) - stickerW))
								: (forwardedX + attMargin);
						g.drawImage(((StickerAttachment) at).getImage(stickerW), rx, y + attY, 0);
					}

					attY += 
							at.getDrawHeight() + 
							attMargin;
				}
			}

			try {
				if (forward != null && forward.length > 0) {
					fwdH = 0;
					for (int i = 0; i < forward.length; i++) {
						if (forward[i] == null)
							continue;
						forward[i].paint(g, attY + fwdH + y, scrolled);
						fwdH += forward[i].getDrawHeight();
					}
				}
			} catch (RuntimeException e) {
			}
			th = attY + fwdH;
			itemDrawHeight = th;
		} catch (Throwable e) {
			VikaTouch.sendLog(e.toString());
		}
		//th = attY + attH + fwdH;
		//itemDrawHeight = th;
	}

	public String[] searchLinks() {
		if (text == null || text.length() < 2)
			return null;
		int lm = 8; // links max (больше на экран не влезет (смотря какой
					// конечно))
		String[] la = new String[lm];
		int li = 0; // индекс в массиве
		int tl = text.length();

		String[] glinks = new String[] { "http://", "https://", "rtsp://", "ftp://", "smb://" }; // вроде
																									// всё.
																									// Ага,
																									// я
																									// слал/принимал
																									// пару
																									// раз
																									// ссылки
																									// на
																									// расшаренные
																									// папки
																									// как
																									// smb://server/folder
		try {
			// System.out.println(text);
			// System.out.println("tl "+tl);
			// Поиск внешних ссылок
			// сначала ищем их на случай сообщения
			// @id89277233 @id2323 @id4 @id5 @id6 ... [ещё 100509 @] ...
			// @id888292,
			// http://что-тоТам
			// В беседе вики такое постоянно.
			for (int gli = 0; gli < glinks.length; gli++) {
				int ii = 0; // Indexof Index
				while (true) {
					ii = text.indexOf(glinks[gli], ii);
					// System.out.println("ii "+ii);
					if (ii == -1) {
						break;
					} else {
						int lci = ii + 6;
						while (lci < tl && text.charAt(lci) != ' ') {
							lci++;
						}
						String l = text.substring(ii, lci);
						la[li] = l;
						li++;
						if (li >= lm)
							return la;
						ii = lci;
					}
				}
			}

			// Поиск ссылок ВК
			int cc = 0; // current char
			while (cc < tl) {
				char c = text.charAt(cc);
				if (c == '@') {
					int cs = cc;
					cc++;
					while (cc < tl && text.charAt(cc) != ' ' && text.charAt(cc) != ']') {
						cc++;
					}
					String l = text.substring(cs, cc);
					la[li] = l;
					li++;
					if (li >= lm)
						return la;
				} else if (c == '[') {
					cc++;
					int cs = cc;
					while (cc < tl && text.charAt(cc) != '|') {
						cc++;
					}
					String l = text.substring(cs, cc);
					la[li] = l;
					li++;
					if (li >= lm)
						return la;
				}
				cc++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("links c "+li);
		return la;
	}

	public String getTime() {
		return VikaUtils.parseMsgTime(date);
	}

	public int getDrawHeight() {
		return itemDrawHeight;
	}

	public void tap(int x, int y) {
		VikaTouch.needstoRedraw=true;
		keyPress(-5);
	}

	public void keyPress(int key) {
		VikaTouch.needstoRedraw=true;
		if (key == -5) {
			if (VikaCanvas.currentAlert==null) { 
			int h = 48;
			OptionItem[] opts;
			if (ChatScreen.peerId > ChatScreen.OFFSET_INT) {
				if (foreign) {
			 opts = new OptionItem[9];
				} else {
					 opts = new OptionItem[8];
				}
			} else {
				if (foreign) {
				opts = new OptionItem[8];
				} else {
					opts = new OptionItem[7];
				}
			}
			opts[0] = new OptionItem(this, TextLocal.inst.get("msg.reply"), IconsManager.ANSWER, -1, h);
			opts[1] = foreign ? new OptionItem(this, TextLocal.inst.get("msg.markasread"), IconsManager.APPLY, -5, h)
					: new OptionItem(this, TextLocal.inst.get("msg.edit"), IconsManager.EDIT, -4, h);
			opts[2] = new OptionItem(this, TextLocal.inst.get("msg.delete"), IconsManager.CLOSE, -2, h);
			opts[3] = new OptionItem(this, TextLocal.inst.get("msg.fwd"), IconsManager.SEND, -6, h);
			opts[4] = new OptionItem(this, TextLocal.inst.get("msg.links") + "...", IconsManager.LINK, -8, h);
			opts[5] = new OptionItem(this, TextLocal.inst.get("msg.attach.attachments") + "...",
					IconsManager.ATTACHMENT, -9, h);
			opts[6] = new OptionItem(this, this.name + "...",
					IconsManager.FRIENDS, -10, h);
			if (ChatScreen.peerId > ChatScreen.OFFSET_INT) {
			opts[7] = new OptionItem(this, ChatScreen.title + "...",
					IconsManager.GROUPS, -11, h);
			if (foreign) {
				opts[8] = new OptionItem(this, TextLocal.inst.get("msg.copy"), IconsManager.EDIT, -4, h);
				
			}
			} else {
				if (foreign) {
					opts[7] = new OptionItem(this, TextLocal.inst.get("msg.copy"), IconsManager.EDIT, -4, h);
					
				}
			}
			
			VikaTouch.popup(new AutoContextMenu(opts));
			}
			VikaTouch.needstoRedraw=true;
			//VikaTouch.sendLog(String.valueOf(VikaCanvas.currentAlert.getClass()));
			//VikaTouch.sendLog(String.valueOf(VikaTouch.canvas.currentScreen.getClass()));
			VikaTouch.canvas.currentScreen.serviceRepaints();
			VikaTouch.needstoRedraw=true;
		}
	}

	public void onMenuItemPress(int i) {
		VikaTouch.needstoRedraw=true;
		if (i <= -100) {
			// ссылки
			i = -i;
			i = i - 100;
			try {
				String s = searchLinks()[i];
				VikaUtils.openLink(s);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			return;
		} else if (i >= 0) { // прикрепы
			try {
				//if (i<attachments.length) {
				attachments[i].press();
				//} else {
				//	forward[]
				//}
			} catch (Exception e) {
			}
			VikaTouch.needstoRedraw=true;
			return;
		}
		// основная менюшка
		// Да, такая лапша. Ты ещё покруче делаешь.
		switch (i) {
		case -1:
			ChatScreen.attachAnswer(mid, name, text);
			
			VikaTouch.needstoRedraw=true;
			break; // БРЕАК НА МЕСТЕ!!11!!1!
		case -2:
			OptionItem[] opts1 = new OptionItem[2];
			opts1[0] = new OptionItem(this, TextLocal.inst.get("msg.remove0"), IconsManager.EDIT, -98, 60);
			opts1[1] = new OptionItem(this, TextLocal.inst.get("msg.remove1"), IconsManager.CLOSE, -99, 60);
			VikaTouch.popup(new AutoContextMenu(opts1));
			break;
		case -4:
			//if (!foreign)
				ChatScreen.editMsg(this);
			VikaTouch.needstoRedraw=true;
			break;
		case -5:
			try {
				if (VikaTouch.canvas.currentScreen instanceof ChatScreen) {
					ChatScreen c = (ChatScreen) VikaTouch.canvas.currentScreen;
					VikaTouch.needstoRedraw=true;
					c.serviceRepaints();
					URLBuilder url = new URLBuilder("messages.markAsRead").addField("start_message_id", "" + mid)
							.addField("peer_id", ChatScreen.peerId);
					VikaTouch.needstoRedraw=true;
					c.serviceRepaints();
					VikaUtils.download(url);
					VikaTouch.needstoRedraw=true;
					c.serviceRepaints();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case -6:
			//VikaTouch.popup(new InfoPopup(TextLocal.inst.get("popup.unrealized"), null));
			VikaTouch.resendingmid=mid;
			VikaTouch.resendingname=name;
			VikaTouch.resendingtext=text;
			vikatouch.screens.DialogsScreen.titleStr="Выберите диалог для пересылки:";
			VikaTouch.setDisplay(VikaTouch.dialogsScr, -1);
			VikaTouch.needstoRedraw=true;
			break;
		case -8: {
			String[] links = searchLinks();
			int c = 0;
			while (links[c] != null) {
				c++;
			}
			if (c == 0) {
				VikaTouch.popup(new InfoPopup(TextLocal.inst.get("error.linksnotfound"), null));
				VikaTouch.needstoRedraw=true;
			} else {
				OptionItem[] opts2 = new OptionItem[c];
				int h = DisplayUtils.height > 240 ? 50 : 30; // вот как делается
																// адаптация, а
																// не твои
																// километровые
																// свитчи и да,
																// я буду ещё
																// долго
																// ворчать.
				for (int j = 0; j < c; j++) {
					int icon = IconsManager.LINK;
					if (links[j].startsWith("id")) {
						icon = IconsManager.FRIENDS;
					}
					if (links[j].startsWith("club")) {
						icon = IconsManager.GROUPS;
					}
					if (links[j].startsWith("rtsp")) {
						icon = IconsManager.VIDEOS;
					}
					opts2[j] = new OptionItem(this, links[j], icon, -(j + 100), h);
				}
				VikaTouch.popup(new AutoContextMenu(opts2));
				VikaTouch.needstoRedraw=true;
			}
		}
			break;
		case -9: {
			int l = llll;
			
			int sum = 0;
			
			if (forward!=null) {
				if (forward.length>0) {
					for (int k = 0 ; k< forward.length; k++) {
						
							sum+= forward[k].attachments.length;
						
					}
				}
			}
			
			OptionItem[] opts = new OptionItem[l+sum];
			int photoC = 1;
			int h = DisplayUtils.height > 240 ? 36 : 30;
			int jj=0;
			for (int j = 0; j < l; j++) {
				Attachment a = attachments[j];
				if (a.type.equals("photo")) {
					opts[j] = new OptionItem(this, TextLocal.inst.get("msg.attach.photo") + " " + photoC, IconsManager.PHOTOS, j, h);
					a.attNumber = photoC;
					photoC++;
				} else if (a.type.equals("doc")) {
					DocumentAttachment da = (DocumentAttachment) a;
					opts[j] = new OptionItem(this, da.name + " (" + (da.size / 1000) + "kb)", IconsManager.DOCS, j, h);
				} else if (a.type.equals("audio")) {
					AudioAttachment aa = (AudioAttachment) a;
					opts[j] = new OptionItem(this, aa.name, IconsManager.MUSIC, j, h);
				} else if (a.type.equals("video")) {
					VideoAttachment va = (VideoAttachment) a;
					opts[j] = new OptionItem(this, va.title, IconsManager.VIDEOS, j, h);
				} else if (a.type.equals("wall")) {
					opts[j] = new OptionItem(this, TextLocal.inst.get("msg.attach.wall"), IconsManager.NEWS, j, h);
				} else if (a.type.equals("audio_message")) {
					opts[j] = new OptionItem(this, VoiceAttachment.name, IconsManager.VOICE, j, h);
				} else {
					opts[j] = new OptionItem(this, TextLocal.inst.get("msg.attach.attachment"), IconsManager.ATTACHMENT, j, h);
				}
				jj++;
			}
			//int jj=l;
			
			if (forward!=null) {
				if (forward.length>0) {
					Attachment[] temp = new Attachment[l+sum];
					for (int jjj=0; jjj<l; jjj++) {
						temp[jjj]=attachments[jjj];
					}
					for (int k = 0 ; k< forward.length; k++) {
						// temp = new Attachment[temp.length+forward[k].attachments.length];
						
						
						for (int j = 0; j < forward[k].attachments.length; j++) {
							Attachment a = forward[k].attachments[j];
							temp[jj]=a;
							
							
							if (a.type.equals("photo")) {
								opts[jj] = new OptionItem(this, TextLocal.inst.get("msg.attach.photo") + " " + photoC, IconsManager.PHOTOS, jj, h);
								a.attNumber = photoC;
								photoC++;
							} else if (a.type.equals("doc")) {
								DocumentAttachment da = (DocumentAttachment) a;
								opts[jj] = new OptionItem(this, da.name + " (" + (da.size / 1000) + "kb)", IconsManager.DOCS, jj, h);
							} else if (a.type.equals("audio")) {
								AudioAttachment aa = (AudioAttachment) a;
								opts[jj] = new OptionItem(this, aa.name, IconsManager.MUSIC, jj, h);
							} else if (a.type.equals("video")) {
								VideoAttachment va = (VideoAttachment) a;
								opts[jj] = new OptionItem(this, va.title, IconsManager.VIDEOS, jj, h);
							} else if (a.type.equals("wall")) {
								opts[jj] = new OptionItem(this, TextLocal.inst.get("msg.attach.wall"), IconsManager.NEWS, jj, h);
							} else if (a.type.equals("audio_message")) {
								opts[jj] = new OptionItem(this, VoiceAttachment.name, IconsManager.VOICE, jj, h);
							} else {
								opts[jj] = new OptionItem(this, TextLocal.inst.get("msg.attach.attachment"), IconsManager.ATTACHMENT, jj, h);
							}
						jj++;
						}
					}
					//attachments=temp;
					attachments=new Attachment[temp.length];
					for (int jjj=0; jjj<temp.length; jjj++) {
						attachments[jjj]=temp[jjj];
					}
				}
				
			}
			
			if (opts != null && opts.length > 0) {
				VikaTouch.popup(new AutoContextMenu(opts));
			} else {
				VikaTouch.popup(new InfoPopup("У этого сообщения нет вложений.", null));
			}
			VikaTouch.needstoRedraw=true;
		}
			break;
		case -98:
			boolean okk = false;
			try {
				URLBuilder url = new URLBuilder("messages.delete").addField("message_ids", "" + mid)
						.addField("delete_for_all", i == -99 ? 1 : 0);
				String x = VikaUtils.download(url);
				JSONObject res = (new JSONObject(x)).getJSONObject("response");
				okk = (res.optInt("" + mid) == 1);
			} catch (Exception e) {
				e.printStackTrace();
				okk = false;
			}
			if (okk) {
				text = TextLocal.inst.get("msg.removed");
				drawText = new String[] { text };
				linesC = 1;
			}
			VikaTouch.needstoRedraw=true;
			break;
		case -10:
			if (this.fromid>0) {
				VikaTouch.setDisplay(new ProfilePageScreen(this.fromid), 1);
			} else  {
				VikaTouch.setDisplay(new GroupPageScreen(-(this.fromid)), 1);
			} /*else if (ChatScreen.type == ChatScreen.TYPE_CHAT) {
				String x2 = CountUtils.countStrMembers(ChatScreen.members);
				VikaTouch.setDisplay(new ChatMembersScreen(ChatScreen.peerId, x2, ChatScreen.members), 1);
			}*/
			break;
		case -11:
			/*if (ChatScreen.type == ChatScreen.TYPE_USER) {
				VikaTouch.setDisplay(new ProfilePageScreen(ChatScreen.localId), 1);
			} else if (ChatScreen.type == ChatScreen.TYPE_GROUP) {
				VikaTouch.setDisplay(new GroupPageScreen(ChatScreen.localId), 1);
			} else*/ if (ChatScreen.type == ChatScreen.TYPE_CHAT) {
				String x2 = CountUtils.countStrMembers(ChatScreen.members);
				VikaTouch.setDisplay(new ChatMembersScreen(ChatScreen.peerId, x2, ChatScreen.members), 1);
			}
			break;
		case -99: {
			boolean ok = false;
			try {
				URLBuilder url = new URLBuilder("messages.delete").addField("message_ids", "" + mid)
						.addField("delete_for_all", i == -99 ? 1 : 0);
				String x = VikaUtils.download(url);
				JSONObject res = (new JSONObject(x)).getJSONObject("response");
				ok = (res.optInt("" + mid) == 1);
			} catch (Exception e) {
				e.printStackTrace();
				ok = false;
			}
			if (ok) {
				text = TextLocal.inst.get("msg.removed");
				drawText = new String[] { text };
				linesC = 1;
			}
			VikaTouch.needstoRedraw=true;
			break;
		}
		}
	}

	public void onMenuItemOption(int i) {

	}

	public int getMessageId() {
		return mid;
	}

	public String getText() {
		return text;
	}

	public int getFromId() {
		return fromid;
	}

	public void setName(String n) {
		this.name = n;
	}

	public void setRead(boolean i) {
		this.isRead = i;
	}

	public boolean isRead() {
		return this.isRead;
	}

}
