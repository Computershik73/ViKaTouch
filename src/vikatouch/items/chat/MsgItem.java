package vikatouch.items.chat;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.popup.AutoContextMenu;
import ru.nnproject.vikaui.popup.ContextMenu;
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
import vikatouch.canvas.VikaCanvasInst;
import vikatouch.items.menu.OptionItem;
import vikatouch.locale.TextLocal;
import vikatouch.screens.ChatScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.IntObject;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.text.CountUtils;
import vikatouch.utils.url.URLBuilder;
import vikatouch.utils.url.URLDecoder;

public class MsgItem
	extends ChatItem implements IMenu
{
	public MsgItem(JSONObject json)
	{
		super(json);
	}
	
	public int mid;
	private String[] drawText;
	public String name = "";
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
	private boolean attsReady;
	
	public boolean isRead = true;
	public MsgItem[] forward;
	
	public int forwardedX = -1;
	public int forwardedW = -1;

	public void ChangeText(String s)
	{
		text = s;
		int h1 = Font.getFont(0, 0, 8).getHeight();
		drawText = TextBreaker.breakText(text, Font.getFont(0, 0, Font.SIZE_SMALL), msgWidth-h1);
		linesC = drawText.length;
		itemDrawHeight = h1*(linesC+1);
	}
	public void parseJSON()
	{
		super.parseJSON();
		msgWidth = DisplayUtils.width - (DisplayUtils.width<=240?10:40);
		margin = (DisplayUtils.width<=240?0:10);
		try
		{
			parseAttachments();
			// {"id":354329,"important":false,"date":1596389831,"attachments":[],"out":0,"is_hidden":false,"conversation_message_id":7560,"fwd_messages":[],"random_id":0,"text":"Будет срач с Лëней или он уже потерял интерес?","from_id":537403336,"peer_id":537403336}
			
			foreign = !(""+json.optInt("from_id")).equalsIgnoreCase(VikaTouch.userId);
			mid = json.optInt("id");
			int h1 = Font.getFont(0, 0, 8).getHeight();
			drawText = TextBreaker.breakText(text, Font.getFont(0, 0, Font.SIZE_SMALL), (forwardedW==-1?msgWidth:forwardedW)-h1);
			linesC = drawText.length;
			
			itemDrawHeight = h1*(linesC+1);
			
			JSONObject reply = json.optJSONObject("reply_message");
			JSONArray fwds = json.optJSONArray("fwd_messages");
			if(reply!=null)
			{
				boolean breakReplyText = true;
				hasReply = true;
				replyText = reply.optString("text");
				if(replyText == null || replyText == "" || replyText.length() <= 0)
				{
					replyText = "";
					JSONArray replyAttachs = reply.optJSONArray("attachments");
					JSONArray replyFwds = reply.optJSONArray("fwd_messages");
					if(replyAttachs != null)
					{
						if(replyAttachs.length() == 1)
						{
							JSONObject att = replyAttachs.getJSONObject(0);
							String type = att.getString("type");
							//VikaTouch.sendLog(type);
							if(type.equals("photo"))
							{
								replyText = "[Фотография]";
							}
							else if(type.equals("audio_message"))
							{
								replyText = "["+TextLocal.inst.get("msg.attach.voice")+"]";
							}
							else if(type.equals("audio"))
							{
								replyText = "["+TextLocal.inst.get("msg.attach.audio")+"]";
							}
							else if(type.equals("wall_reply"))
							{
								replyText = "[Комментарий]";
							}
							else
							{
								replyText = "["+TextLocal.inst.get("msg.attach.attachment")+"]";
							}
						}
						else if(replyAttachs.length() > 1)
						{
							replyText = "["+TextLocal.inst.get("msg.attach.attachments")+" (" + replyAttachs.length() + ")]";
						}
						breakReplyText = false;
					}
					else if(replyFwds != null)
					{
						replyText = CountUtils.countStrMessages(replyFwds.length());
						breakReplyText = false;
					}
					else
					{
						replyText = "";
					}
				}
				if(breakReplyText)
					replyText = TextBreaker.breakText(replyText, false, null, true, msgWidth-h1-h1)[0];
				
				int fromId = reply.optInt("from_id");
				if(fromId==Integer.parseInt(VikaTouch.userId))
				{
					replyName = TextLocal.inst.get("msg.you");
				}
				else
				{
					if(fromId > 0 && ChatScreen.profileNames.containsKey(new IntObject(fromId)))
					{
						replyName = (String) ChatScreen.profileNames.get(new IntObject(fromId));
					}
				}
			}
			if(fwds!=null && fwds.length()>0)
			{
				int fwdX = forwardedX==-1 ? (foreign ? (margin*2) : (DisplayUtils.width-msgWidth)) : (forwardedX + margin);
				int fwdW = forwardedX==-1 ? (msgWidth - margin) : (forwardedW - margin);
				forward = new MsgItem[fwds.length()];
				try
				{
					for(int i = 0; i<fwds.length(); i++)
					{
						MsgItem m = new MsgItem(fwds.getJSONObject(i));
						m.forwardedW = fwdW;
						m.forwardedX = fwdX;
						m.parseJSON();
						int fromId = m.fromid; 
	
						String name = (fromId < 0 ? "g" : "") + "id" + fromId;
						
						if(fromId > 0 && ChatScreen.profileNames.containsKey(new IntObject(fromId)))
						{
							name = (String)ChatScreen.profileNames.get(new IntObject(fromId));
						}
						m.showName = true;
						m.name = (m.foreign ? name : "Вы");
						m.loadAtts();
						forward[i] = m;
					}
				}
				catch(RuntimeException e) { }
			}
		}
		catch (Exception e)
		{
			text = e.toString();
			e.printStackTrace();
		}
		
		// experimental
		{
			//if(text.equals("Т")) VikaTouch.popup(new InfoPopup(json.toString(), null));
		}
	}
	
	public void loadAtts()
	{
		if(attH<0)
		{
			attH = 0;
			// prepairing attachments
			if(!attsReady)
			{
				attsReady = true;
				try {
					for(int i=0; i<attachments.length; i++)
					{
						Attachment at = attachments[i];
						if(at==null) continue;
						if(at instanceof PhotoAttachment && !Settings.isLiteOrSomething)
						{
							((PhotoAttachment) at).loadForMessage();
						}
						if(at instanceof VideoAttachment && !Settings.isLiteOrSomething)
						{
							((VideoAttachment) at).loadForMessage();
						}
						if(at instanceof VoiceAttachment)
						{
							((VoiceAttachment) at).mid = mid;
						}
						if(at instanceof StickerAttachment)
						{
							int stickerH = DisplayUtils.width > 250 ? 128 : 64;
							attH += stickerH + attMargin;
						}
						else
						{
							attH += at.getDrawHeight() + attMargin;
						}
					}
					if(attH != 0) { attH += attMargin; }
				}
				catch (Exception e)
				{
					attH = 0;
					e.printStackTrace();
				}
			}
		}
	}
	
	public VoiceAttachment findVoice()
	{
		for(int i=0; i<attachments.length; i++)
		{
			Attachment at = attachments[i];
			if(at==null) continue;
			if(at instanceof VoiceAttachment)
			{
				return (VoiceAttachment) at;
			}
		}
		return null;
	}
	
	public void paint(Graphics g, int y, int scrolled)
	{
		if(!ChatScreen.forceRedraw && y+scrolled+itemDrawHeight < -50) return;
		// drawing
		Font font = Font.getFont(0, 0, 8);
		g.setFont(font);
		int h1 = font.getHeight();
		int attY = h1*(linesC+1+(showName?1:0)+(hasReply?2:0));
		int th = attY + attH + fwdH;
		itemDrawHeight = th;
		int textX = 0;
		int radius = 16;
		int msgWidthInner = (forwardedW==-1?msgWidth:forwardedW);
		if(forwardedX!=-1)
		{
			ColorUtils.setcolor(g, ColorUtils.FOREIGNMSG);
			g.fillRoundRect(forwardedX, y, msgWidthInner, th, radius, radius);
			textX = forwardedX + h1/2;
		}
		else if(foreign)
		{
			ColorUtils.setcolor(g, ColorUtils.FOREIGNMSG);
			g.fillRoundRect(margin, y, msgWidthInner, th, radius, radius);
			g.fillRect(margin, y+th-radius, radius, radius);
			textX = margin + h1/2;
			if(selected && ScrollableCanvas.keysMode)
			{
				ColorUtils.setcolor(g, ColorUtils.TEXT);
				g.setStrokeStyle(Graphics.SOLID);
				g.drawRoundRect(margin, y, msgWidthInner, th, radius, radius);
			}
			
			if(!isRead)
			{
				ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
				g.fillArc(margin+msgWidthInner+1, y+16, 8, 8, 0, 360);
			}
		}
		else
		{
			ColorUtils.setcolor(g, ColorUtils.MYMSG);
			g.fillRoundRect(DisplayUtils.width-(margin+msgWidthInner), y, msgWidthInner, th, radius, radius);
			g.fillRect(DisplayUtils.width-(margin+radius), y+th-radius, radius, radius);
			textX = DisplayUtils.width-(margin+msgWidthInner) + h1/2;
			if(selected && ScrollableCanvas.keysMode)
			{
				ColorUtils.setcolor(g, ColorUtils.TEXT);
				g.setStrokeStyle(Graphics.SOLID);
				g.drawRoundRect(DisplayUtils.width-(margin+msgWidthInner), y, msgWidthInner, th, radius, radius);
			}
			if(!isRead)
			{
				//System.out.println("unread out");
				ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
				g.fillArc(DisplayUtils.width-(margin+msgWidthInner)-9, y+16, 8, 8, 0, 360);
			}
		}
		if(name!=null&&showName)
		{
			ColorUtils.setcolor(g, ColorUtils.COLOR1);
			g.drawString(name, textX, y+h1/2, 0);
			ColorUtils.setcolor(g, ColorUtils.OUTLINE);
			if(time == null || time.length()<1)
			{
				time = getTime();
				//System.out.println("msg time: "+time);
			}
			g.drawString(time, textX-h1+msgWidthInner-font.stringWidth(time), y+h1/2, 0);
		}
		ColorUtils.setcolor(g, ColorUtils.TEXT);
		for(int i = 0; i < linesC; i++)
		{
			g.drawString(drawText[i]==null?" ":drawText[i], textX, y+h1/2+h1*(i+(showName?1:0)), 0);
		}
		
		if(hasReply)
		{
			if(replyText!=null) g.drawString(replyText, textX+h1, y+h1/2+h1*(linesC+1+(showName?1:0)), 0);
			ColorUtils.setcolor(g, ColorUtils.COLOR1);
			if(replyName != null)
				g.drawString(replyName, textX+h1, y+h1/2+h1*(linesC+(showName?1:0)), 0);
			g.fillRect(textX+h1/2-1, y+h1/2+h1*(linesC+(showName?1:0)), 2, h1*2);
		}
		
		// рендер аттачей
		if(attH>0 && attsReady)
		{
			attY += attMargin;
			for(int i=0; i<attachments.length; i++)
			{
				Attachment at = attachments[i];
				if(at==null) continue;
				int x1 = foreign ? (margin + attMargin) : (DisplayUtils.width - (margin + msgWidthInner) + attMargin);
				if(at instanceof PhotoAttachment)
				{
					PhotoAttachment pa = (PhotoAttachment) at;
					int rx = forwardedX==-1 ? (foreign ? (margin + attMargin) : (DisplayUtils.width - (margin + attMargin) - pa.renderW)) : (forwardedX + attMargin);
					if(pa.renderImg == null)
					{
						if(Settings.isLiteOrSomething)
						{
							g.drawString("Фотография", textX, y+attY, 0);
						}
						else
							g.drawString("Не удалось загрузить изображение", textX, y+attY, 0);
					}
					else
					{
						g.drawImage(pa.renderImg, rx, y+attY, 0);
					}
				}
				else if(at instanceof VideoAttachment)
				{
					VideoAttachment va = (VideoAttachment) at;
					int rx = forwardedX==-1 ? (foreign ? (margin + attMargin) : (DisplayUtils.width - (margin + attMargin) - va.renderW)) : (forwardedX + attMargin);
					if(va.renderImg == null)
					{
						if(Settings.isLiteOrSomething)
						{
							g.drawString("Видео", textX, y+attY, 0);
						}
						else
							g.drawString("Не удалось загрузить изображение", textX, y+attY, 0);
					}
					else
					{
						g.drawImage(va.renderImg, rx, y+attY, 0);
						g.drawString(va.title, x1, y+attY+va.renderH, 0);
					}
				}
				else if(at instanceof DocumentAttachment)
				{
					((DocumentAttachment) at).draw(g, x1, y+attY, msgWidthInner - attMargin*2);
				}
				else if(at instanceof WallAttachment)
				{
					((WallAttachment) at).draw(g, x1, y+attY, msgWidthInner - attMargin*2);
				}
				else if(at instanceof StickerAttachment)
				{
					int stickerW = DisplayUtils.width > 250 ? 128 : 64;
					int rx = forwardedX==-1 ? (foreign ? (margin + attMargin) : (DisplayUtils.width - (margin + attMargin) - stickerW)) : (forwardedX+attMargin);
					g.drawImage(((StickerAttachment) at).getImage(stickerW), rx, y+attY, 0);
				}
				
				attY += at.getDrawHeight()+attMargin;
			}
		}
		
		try
		{
			if(forward!=null && forward.length>0)
			{
				fwdH=0;
				for(int i = 0; i<forward.length; i++)
				{
					if(forward[i] == null) continue;
					forward[i].paint(g, attY+fwdH+y, scrolled);
					fwdH+=forward[i].getDrawHeight();
				}
			}
		} catch (RuntimeException e) { }
	}
	
	public String[] searchLinks()
	{
		if(text == null || text.length()<2) return null;
		int lm = 8; // links max (больше на экран не влезет (смотря какой конечно))
		String[] la = new String[lm];
		int li = 0; // индекс в массиве
		int tl = text.length();
		
		final String[] glinks = new String[] { "http://", "https://", "rtsp://", "ftp://", "smb://" }; // вроде всё. Ага, я слал/принимал пару раз ссылки на расшаренные папки как smb://server/folder
		try
		{
			//System.out.println(text);
			//System.out.println("tl "+tl);
			// Поиск внешних ссылок
			// сначала ищем их на случай сообщения
			// @id89277233 @id2323 @id4 @id5 @id6 ... [ещё 100509 @] ... @id888292, http://что-тоТам
			// В беседе вики такое постоянно.
			for(int gli=0; gli<glinks.length; gli++)
			{
				int ii = 0; // Indexof Index
				while(true)
				{
					ii = text.indexOf(glinks[gli], ii);
					//System.out.println("ii "+ii);
					if(ii == -1)
					{
						break;
					}
					else
					{
						int lci = ii+6;
						while(lci<tl && text.charAt(lci)!=' ') { lci++; }
						String l = text.substring(ii, lci);
						la[li] = l;
						li++;
						if(li>=lm) return la;
						ii = lci;
					}
				}
			}
					
			// Поиск ссылок ВК
			int cc = 0; // current char
			while(cc<tl)
			{
				char c = text.charAt(cc);
				if(c=='@')
				{
					int cs = cc;
					cc++;
					while(cc<tl && text.charAt(cc)!=' ' && text.charAt(cc)!=']') { cc++; }
					String l = text.substring(cs, cc);
					la[li] = l;
					li++;
					if(li>=lm) return la;
				}
				else if(c=='[')
				{
					cc++;
					int cs = cc;
					while(cc<tl && text.charAt(cc)!='|') { cc++; }
					String l = text.substring(cs, cc);
					la[li] = l;
					li++;
					if(li>=lm) return la;
				}
				cc++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println("links c "+li);
		return la;
	}

	public String getTime()
	{
		return VikaUtils.parseMsgTime(date);
	}
	
	public int getDrawHeight()
	{
		return itemDrawHeight;
	}

	public void tap(int x, int y)
	{
		keyPressed(-5);
	}

	public void keyPressed(int key)
	{
		if(key == -5) 
		{
			int h = 48;
			OptionItem[] opts = new OptionItem[6];
			opts[0] = new OptionItem(this, TextLocal.inst.get("msg.reply"), IconsManager.ANSWER, -1, h);
			opts[1] = foreign ? new OptionItem(this, TextLocal.inst.get("msg.markasread"), IconsManager.APPLY, -5, h)
				: new OptionItem(this, TextLocal.inst.get("msg.edit"), IconsManager.EDIT, -4, h);
			opts[2] = new OptionItem(this, TextLocal.inst.get("msg.delete"), IconsManager.CLOSE, -2, h);
			opts[3] = new OptionItem(this, TextLocal.inst.get("msg.fwd"), IconsManager.SEND, -6, h);
			opts[4] = new OptionItem(this, TextLocal.inst.get("msg.links")+"...", IconsManager.LINK, -8, h);
			opts[5] = new OptionItem(this, TextLocal.inst.get("msg.attach.attachments")+"...", IconsManager.ATTACHMENT, -9, h);
			VikaTouch.popup(new AutoContextMenu(opts));
		}
	}

	public void onMenuItemPress(int i) {
		if(i<=-100)
		{
			// ссылки
			i = -i;
			i = i - 100;
			try
			{
				String s = searchLinks()[i];
				VikaUtils.openLink(s);
			}
			catch (RuntimeException e) 
			{
				e.printStackTrace();
			}
			return;
		} 
		else if(i>=0)
		{ // прикрепы
			try
			{
				attachments[i].press();
			}
			catch (Exception e) { }
			return;
		}
		// основная менюшка
		// Да, такая лапша. Ты ещё покруче делаешь.
		switch(i)
		{
		case -1:
			ChatScreen.attachAnswer(mid, name, text);
			break; // БРЕАК НА МЕСТЕ!!11!!1!
		case -2:
			OptionItem[] opts1 = new OptionItem[2];
			opts1[0] = new OptionItem(this, "Удалить у себя", IconsManager.EDIT, -98, 60);
			opts1[1] = new OptionItem(this, "Удалить везде", IconsManager.CLOSE, -99, 60);
			VikaTouch.popup(new AutoContextMenu(opts1));
			break;
		case -4:
			if(!foreign) ChatScreen.editMsg(this);
			break;
		case -5:
			try
			{
				if(VikaTouch.canvas.currentScreen instanceof ChatScreen)
				{
					ChatScreen c = (ChatScreen) VikaTouch.canvas.currentScreen;
					URLBuilder url = new URLBuilder("messages.markAsRead").addField("start_message_id", ""+mid).addField("peer_id", c.peerId);
					String res = VikaUtils.download(url);
				}
			}
			catch (Exception e)
			{ e.printStackTrace(); }
			break;
		case -6:
			VikaTouch.popup(new InfoPopup("Пересылку скоро изобретём.", null));
			break;
		case -8:
			{
				String[] links = searchLinks();
				int c = 0;
				while(links[c]!=null)
				{
					c++;
				}
				if(c==0)
				{
					VikaTouch.popup(new InfoPopup("Ссылки не найдены либо произошла ошибка.", null));
				}
				else
				{
					OptionItem[] opts2 = new OptionItem[c];
					int h = DisplayUtils.height>240?50:30; // вот как делается адаптация, а не твои километровые свитчи и да, я буду ещё долго ворчать.
					for(int j = 0; j < c; j++)
					{
						int icon = IconsManager.LINK;
						if(links[j].indexOf("id")==0) { icon = IconsManager.FRIENDS; }
						if(links[j].indexOf("club")==0) { icon = IconsManager.GROUPS; }
						if(links[j].indexOf("rtsp")==0) { icon = IconsManager.VIDEOS; }
						opts2[j] = new OptionItem(this, links[j], icon, -(j+100), h);
					}
					VikaTouch.popup(new AutoContextMenu(opts2));
				}
			}
			break;
		case -9:
			{
				int l = attachments.length;
				OptionItem[] opts = new OptionItem[l];
				int photoC = 1;
				int h = DisplayUtils.height>240?36:30;
				for(int j=0;j<l;j++)
				{
					Attachment a = attachments[j];
					if(a.type.equals("photo"))
					{
						opts[j] = new OptionItem(this, "Фотография "+photoC, IconsManager.PHOTOS, j, h);
						a.attNumber = photoC;
						photoC++;
					}
					else if(a.type.equals("doc"))
					{
						DocumentAttachment da = (DocumentAttachment) a;
						opts[j] = new OptionItem(this, da.name + " ("+(da.size/1000)+"kb)", IconsManager.DOCS, j, h);
					}
					else if(a.type.equals("audio"))
					{
						AudioAttachment aa = (AudioAttachment) a;
						opts[j] = new OptionItem(this, aa.name, IconsManager.MUSIC, j, h);
					}
					else if(a.type.equals("video"))
					{
						VideoAttachment va = (VideoAttachment) a;
						opts[j] = new OptionItem(this, va.title, IconsManager.VIDEOS, j, h);
					}
					else if(a.type.equals("wall"))
					{
						opts[j] = new OptionItem(this, TextLocal.inst.get("msg.attach.wall"), IconsManager.NEWS, j, h);
					}
					else if(a.type.equals("audio_message"))
					{
						opts[j] = new OptionItem(this, VoiceAttachment.name, IconsManager.VOICE, j, h);
					}
					else
					{
						opts[j] = new OptionItem(this, "Вложение", IconsManager.ATTACHMENT, j, h);
					}
				}
				if(opts != null && opts.length>0)
				{
					VikaTouch.popup(new AutoContextMenu(opts));
				}
				else
				{
					VikaTouch.popup(new InfoPopup("У этого сообщения нет вложений.", null));
				}
			}
			break;
		case -98:
		case -99:
			{
				boolean ok = false;
				try
				{
					URLBuilder url = new URLBuilder("messages.delete").addField("message_ids", ""+mid).addField("delete_for_all", i==-99?1:0);
					String x = VikaUtils.download(url); 
					JSONObject res = (new JSONObject(x)).getJSONObject("response");
					ok = (res.optInt(""+mid)==1);
				}
				catch (Exception e)
				{ 
					e.printStackTrace();
					ok = false;
				}
				if(ok)
				{
					text = "[удалено]";
					drawText = new String[] { text };
					linesC = 1;
				}
				break;
			}
		}
	}

	public void onMenuItemOption(int i) {
		
	}

}
