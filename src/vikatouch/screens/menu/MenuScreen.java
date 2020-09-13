package vikatouch.screens.menu;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.json.me.JSONObject;

import ru.nnproject.vikaui.menu.IMenu;
import ru.nnproject.vikaui.utils.ColorUtils;
import ru.nnproject.vikaui.utils.DisplayUtils;
import ru.nnproject.vikaui.utils.images.IconsManager;
import vikatouch.VikaTouch;
import vikatouch.items.menu.OptionItem;
import vikatouch.items.music.MusicMenuItem;
import vikatouch.json.JSONBase;
import vikatouch.locale.TextLocal;
import vikatouch.screens.MainScreen;
import vikatouch.settings.Settings;
import vikatouch.utils.ResizeUtils;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.error.ErrorCodes;
import vikatouch.utils.url.URLBuilder;

public class MenuScreen
	extends MainScreen implements IMenu
{

	private static Image profileImg;
	public static String name;
	public static boolean hasAva;
	public static String lastname;
	public static String status;
	public static String avaurl;
	/*
	private static int[] itemscmd = {4, 5, 6, 7, 8, 9, -1};
	*/
	private int btnsLen = 8 ;
	public String exitStr;
	public String docsStr;
	public String photosStr;
	public String videosStr;
	public String musicStr;
	public String groupsStr;
	public String friendsStr;
	private String aboutStr;
	private int selectedBtn;

	public MenuScreen()
	{
		super();

		profileImg = VikaTouch.cameraImg;
		if(VikaTouch.DEMO_MODE)
		{
			name = "Арман";
			lastname = "Джусупгалиев";
			//БЕЗУМНО МОЖНО БЫТЬ ПЕРВЫМ
			status = "Волк если волк, а когда волк волка волку волк - волк."; // так ещё упоротее))0)
			
			hasAva = true;
		}
		if(VikaTouch.userId != null)
		{
			try
			{
				if((avaurl == null && hasAva && profileImg != null) || name == null || name == "null" || name == "" || avaurl == "" || !VikaTouch.offlineMode)
				{
					String var10 = VikaUtils.download(new URLBuilder("users.get")
						.addField("user_ids", VikaTouch.userId)
						.addField("fields", "photo_id,verified,sex,bdate,city,country,has_photo,photo_50,status"));
					JSONObject profileobj = new JSONObject(var10).getJSONArray("response").getJSONObject(0);
					name = profileobj.optString("first_name");
					lastname = profileobj.optString("last_name");
					status = profileobj.optString("status");
					avaurl = JSONBase.fixJSONString(profileobj.optString("photo_50"));
					hasAva = profileobj.optInt("has_photo") == 1;
				}
				if(!Settings.dontLoadAvas && hasAva && avaurl != null && avaurl != "" && avaurl != "null")
				{
					try
					{
						profileImg = ResizeUtils.resizeava(
								VikaUtils.downloadImage(avaurl));
					}
					catch (Throwable e)
					{
						e.printStackTrace();
						if(!VikaTouch.offlineMode)
							VikaTouch.error(e, ErrorCodes.MENUAVATAR);
						// там всегда нулл будет. Больше падать нечему. И нечего всё крашить, не скачалось и хрен с ним
						hasAva = false;
					}
				}
			}
			catch (StringIndexOutOfBoundsException var19)
			{
				VikaTouch.error(var19, ErrorCodes.MENUPROFILEINFO1);
			}
			catch (NullPointerException var19)
			{
				VikaTouch.error(var19, ErrorCodes.MENUPROFILEINFO2);
			}
			catch (Exception var19)
			{
				VikaTouch.error(var19, ErrorCodes.MENUPROFILEINFO3);
			}
			catch (Throwable a)
			{
				VikaTouch.error(a, ErrorCodes.MENUPROFILEINFO3);
			}
		}
		else
		{
			VikaTouch.error(ErrorCodes.MENUNOUSERID, false);
		}
		try
		{
			friendsStr = TextLocal.inst.get("menu.friends");
			exitStr = TextLocal.inst.get("menu.quit");
			groupsStr = TextLocal.inst.get("menu.groups");
			musicStr = TextLocal.inst.get("menu.music");
			docsStr = TextLocal.inst.get("menu.documents");
			photosStr = TextLocal.inst.get("menu.photos");
			videosStr = TextLocal.inst.get("menu.videos");
			aboutStr = TextLocal.inst.get("menu.about");
		}
		catch (Exception e)
		{
			
		}
		int uiih = DisplayUtils.compact?30:50; // е72, ландшафт 240, СЕ портрет
		uiItems = new OptionItem[7];
		uiItems[0] = new OptionItem(this, friendsStr, IconsManager.FRIENDS, 4, uiih);
		uiItems[1] = new OptionItem(this, groupsStr, IconsManager.GROUPS, 5, uiih);
		uiItems[2] = new MusicMenuItem(this, musicStr, IconsManager.MUSIC, 6, uiih);
		uiItems[3] = new OptionItem(this, videosStr, IconsManager.VIDEOS, 7, uiih);
		uiItems[4] = new OptionItem(this, photosStr, IconsManager.CAMERA, 8, uiih);
		uiItems[5] = new OptionItem(this, docsStr, IconsManager.DOCS, 9, uiih);
		uiItems[6] = new OptionItem(this, exitStr, IconsManager.CLOSE, -1, uiih);
		
		itemsCount = 7;
		itemsh = 140 + uiih*itemsCount;
		
		// sending stats
		//TODO ENABLE ON OBT
		VikaTouch.sendStats();
	}

	protected final void up()
	{
		if(selectedBtn > 0)
			uiItems[selectedBtn-1].setSelected(false);
		selectedBtn--;
		if(selectedBtn < 0)
			selectedBtn = 0;
		scrollToSelected();
		if(selectedBtn > 0)
			uiItems[selectedBtn-1].setSelected(true);
	}
	
	protected final void down()
	{
		if(selectedBtn > 0)
			uiItems[selectedBtn-1].setSelected(false);
		selectedBtn++;
		if(selectedBtn >= btnsLen)
			selectedBtn = btnsLen - 1;
		scrollToSelected();
		if(selectedBtn > 0)
			uiItems[selectedBtn-1].setSelected(true);
	}
	
	public void scrollToSelected()
	{
		System.out.println("Y: "+getItemY(selectedBtn-1));
		scrolled = -(getItemY(selectedBtn-1)-DisplayUtils.height/2+(uiItems[selectedBtn-1].getDrawHeight()/2)+topPanelH+50);
	}
	
	public final void press(int key)
	{
		keysMode = true;
		if(key == -5)
		{
			if(selectedBtn == 0)
			{
				VikaTouch.inst.cmdsInst.command(13, this);
			}
			else
			{
				uiItems[selectedBtn-1].keyPressed(-5); // проверял хоть?
			}
		}
		else
			super.press(key);
		repaint();
	}
	
	public void draw(Graphics g)
	{
		try
		{
			int y = topPanelH+82; // init offset
			update(g);
			if(!DisplayUtils.compact)
			{
				ColorUtils.setcolor(g, -2);
				g.fillRect(0, 132, DisplayUtils.width, 8);
				ColorUtils.setcolor(g, -10);
				g.fillRect(0, 133, DisplayUtils.width, 1);
				ColorUtils.setcolor(g, -11);
				g.fillRect(0, 134, DisplayUtils.width, 1);
				ColorUtils.setcolor(g, -7);
				g.fillRect(0, 139, DisplayUtils.width, 1);
				ColorUtils.setcolor(g, -12);
				g.fillRect(0, 140, DisplayUtils.width, 1);
			}
			if(profileImg != null)
			{
				g.drawImage(profileImg, 16, topPanelH+13, 0);
				g.drawImage(IconsManager.ac, 16, topPanelH+13, 0);
				if(VikaTouch.offlineMode)
				{
					g.setColor(200, 0, 0);
				}
				else
					ColorUtils.setcolor(g, ColorUtils.ONLINE);
				g.fillArc(16+38, topPanelH+13+38, 12, 12, 0, 360);
			}
			g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
			ColorUtils.setcolor(g, ColorUtils.TEXT);
			g.drawString(name+" "+lastname, 74, topPanelH+12, 0);
			//g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
			//g.drawString(status==null?"":status, 74, 98, 0);
			
			ColorUtils.setcolor(g, -3);
			//g.drawRect(0, 140, DisplayUtils.width, 50);
			if(uiItems!=null)
			{
				for (int i=0;i<uiItems.length;i++)
				{
					if(uiItems[i]!=null) {
						uiItems[i].paint(g, y, scrolled);
						y+=uiItems[i].getDrawHeight();
					}
				}
			}
			g.translate(0, -g.getTranslateY());
		}
		catch(Exception e)
		{
			g.setColor(255, 0, 0);
			g.fillRect(0, 0, 50, 9000);
		}
	}
	
	public final void drawHUD(Graphics g, String x)
	{
		super.drawHUD(g, "");

		//g.drawImage(settingsImg, DisplayUtils.width-35, DisplayUtils.compact?0:18, 0);
		
		
		if(keysMode && selectedBtn == 0)
		{
		//	g.setGrayScale(255);
		//	g.drawRect(DisplayUtils.width-35, DisplayUtils.compact?0:18, 24, 24);
			g.drawImage(IconsManager.ico[IconsManager.SETTINGS], DisplayUtils.width-35, DisplayUtils.compact?0:18, 0);
		}
		else
		{
			g.drawImage(IconsManager.selIco[IconsManager.TOPBAR], DisplayUtils.width-35, DisplayUtils.compact?0:18, 0);
		}
	}
/*
	public final void draw(Graphics g)
	{
		{
			switch(DisplayUtils.idispi)
			{
				case DisplayUtils.DISPLAY_ALBUM:
				case DisplayUtils.DISPLAY_PORTRAIT:
				{
					//Nokia N8, E7, C7, C6, N97, 5800, и т.д.. Портретная
					update(g);
					ColorUtils.setcolor(g, -2);
					g.fillRect(0, 132, 640, 8);
					ColorUtils.setcolor(g, -10);
					g.fillRect(0, 133, 640, 1);
					ColorUtils.setcolor(g, -11);
					g.fillRect(0, 134, 640, 1);
					ColorUtils.setcolor(g, -7);
					g.fillRect(0, 139, 640, 1);
					ColorUtils.setcolor(g, -12);
					g.fillRect(0, 140, 640, 1);
					if(profileimg != null)
					{
						if(hasAva)
						{
							g.drawImage(profileimg, 16, 71, 0);
							
							//Обрезка
							
							ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
							
							g.drawRect(15, 120, 51, 2);
							g.drawRect(15, 70, 51, 1);
							
							g.drawRect(16, 71, 1, 14);
							g.drawRect(16, 71, 14, 1);
							g.drawRect(17, 72, 11, 1);
							g.drawRect(17, 82, 1, 1);
							g.fillTriangle(17, 73, 17, 82, 28, 73);
							
							g.drawRect(65, 72, 1, 14);
							g.drawRect(52, 72, 14, 1);
							g.drawRect(64, 83, 1, 1);
							g.drawRect(54, 73, 1, 1);
							g.fillTriangle(64, 73, 64, 82, 55, 73);
							
							g.drawRect(65, 72, 1, 14);
							g.drawRect(52, 72, 14, 1);
							g.drawRect(64, 83, 1, 1);
							g.drawRect(54, 73, 1, 1);
							g.drawRect(64, 73, 1, 10);
							g.fillTriangle(64, 73, 64, 82, 55, 73);
							
							g.drawRect(16, 106, 1, 14);
							g.drawRect(16, 119, 14, 1);
							g.drawRect(17, 118, 11, 1);
							g.drawRect(17, 108, 1, 1);
							g.fillTriangle(17, 118, 17, 108, 28, 118);
							
							g.drawArc(15, 70, 51, 51, 0, 360);
							
							g.drawRect(65, 106, 1, 14);
							g.drawRect(54, 119, 12, 1);
							g.drawLine(52, 112, 54, 110);
							g.drawLine(61, 110, 64, 113);
							g.drawRect(64, 108, 1, 12);
							g.drawRect(63, 118, 1, 1);
							g.drawRect(61, 108, 2, 1);
							
							g.drawArc(51, 108, 14, 14, 0, 360);
							g.drawArc(52, 109, 13, 13, 0, 360);
							
							//Онлайн
							ColorUtils.setcolor(g, ColorUtils.ONLINE);
							g.fillArc(53, 110, 11, 11, 0, 360);
						}
						else
						{
							g.drawImage(profileimg, 15, 70, 0);
						}
					}

					ColorUtils.setcolor(g, -3);
					g.drawRect(0, 140, 360, 50);
					//g.drawRect(0, 58, 20, items);
					for(int d = 0; d<(itemsh/oneitemheight)-1; d++)
					{
						g.drawRect(0, 140+(d*oneitemheight), DisplayUtils.width, 50);
						//g.drawString(""+d/50, 20, 150+d, 0);
					}

					if(keysMode)
					{
						ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
						if(selectedBtn > 0)
						{
							g.fillRect(0, 140 + (oneitemheight * (selectedBtn - 1)), 650, oneitemheight);
						}
					}
					if(friendimg != null)
					{
						g.drawImage(friendimg, 16, 162, 0);
					}

					if(groupimg != null)
					{
						g.drawImage(groupimg, 17, 216, 0);
					}

					if(musicimg != null)
					{
						g.drawImage(musicimg, 18, 264, 0);
					}

					if(videosimg != null)
					{
						g.drawImage(videosimg, 18, 314, 0);
					}

					if(photosimg != null)
					{
						g.drawImage(photosimg, 17, 364, 0);
					}

					if(docsimg != null)
					{
						g.drawImage(docsimg, 18, 414, 0);
					}

					if(exit != null)
					{
						g.drawImage(exit, 20, 466, 0);
					}
					
					ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(name+" "+lastname, 82, 80, 0);
					if(keysMode && selectedBtn == 1)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(friendsStr, 56, 158, 0);
					if(keysMode && selectedBtn == 2)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(groupsStr, 56, 208, 0);
					if(keysMode && selectedBtn == 3)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(musicStr, 56, 258, 0);
					if(keysMode && selectedBtn == 4)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(videosStr, 56, 308, 0);
					if(keysMode && selectedBtn == 5)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(photosStr, 56, 358, 0);
					if(keysMode && selectedBtn == 6)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(docsStr, 56, 408, 0);
					if(keysMode && selectedBtn == 7)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(exitStr, 56, 458, 0);
					//g.drawString("", 56, 408, 0);
						
					g.translate(0,-g.getTranslateY());
						
					if(DisplayUtils.idispi == DisplayUtils.DISPLAY_PORTRAIT)
					{
						ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
						g.fillRect(0, 0, 360, 58);
						ColorUtils.setcolor(g, -3);
						g.fillRect(0, 590, 360, 50);
	
						if(logoImg != null)
						{
							g.drawImage(logoImg, 2, 2, 0);
						}
						
						if(menuImg != null)
						{
							g.drawImage(menuImg, 304, 606, 0);
						}
						
						if(newsImg != null)
						{
							g.drawImage(newsImg, 37, 603, 0);
						}
						
						if(settingsImg != null)
						{
							g.drawImage(settingsImg, 325, 18, 0);
						}
						
						if(keysMode && selectedBtn == 0)
						{
							ColorUtils.setcolor(g, ColorUtils.TEXTCOLOR1);
							g.drawRect(325, 18, 24, 24);
						}
						
						if(VikaTouch.unreadCount > 0)
						{
	
							if(dialImg2 != null)
							{
								g.drawImage(dialImg2, 168, 599, 0);
								g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
								g.drawString("" + VikaTouch.unreadCount, 191, 598, 0);
							}
							else if(dialImg != null)
							{
								g.drawImage(dialImg, 168, 604, 0);
							}
						}
						else
						{
							if(dialImg != null)
							{
								g.drawImage(dialImg, 168, 604, 0);
							}
						}
					}
					else if(DisplayUtils.idispi == DisplayUtils.DISPLAY_ALBUM)
					{
						ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
						g.fillRect(0, 0, 640, 58);
						ColorUtils.setcolor(g, -3);
						g.fillRect(0, 310, 640, 50);

						if(logoImg != null)
						{
							g.drawImage(MenuScreen.logoImg, 2, 2, 0);
						}
						
						if(newsImg != null)
						{
							g.drawImage(newsImg, 36, 323, 0);
						}
						
						if(settingsImg != null)
						{
							g.drawImage(settingsImg, 605, 18, 0);
						}
						
						if(selectedBtn == 0)
						{
							ColorUtils.setcolor(g, ColorUtils.TEXTCOLOR1);
							g.drawRect(605, 18, 24, 24);
						}
						
						if(VikaTouch.unreadCount > 0)
						{

							if(dialImg2 != null)
							{
								g.drawImage(MenuScreen.dialImg2, 308, 319, 0);
								g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
								g.drawString("" + VikaTouch.unreadCount, 330, 318, 0);
							}
							else if(dialImg != null)
							{
								g.drawImage(dialImg, 308, 324, 0);
							}
						}
						else
						{
							if(dialImg != null)
							{
								g.drawImage(dialImg, 308, 324, 0);
							}
						}

						if(menuImg != null)
						{
							g.drawImage(menuImg, 584, 326, 0);
						}
						break;
					}
					break;
				}

				case DisplayUtils.DISPLAY_S40:
				case DisplayUtils.DISPLAY_ASHA311:
				{
					//S40, bada, asha 311
					
					update(g);
					
					ColorUtils.setcolor(g, -7);
					g.fillRect(0, 66, 240, 4);
					ColorUtils.setcolor(g, -8);
					g.fillRect(0, 68, 240, 2);
					ColorUtils.setcolor(g, -4);
					g.fillRect(0, 29, 240, 1);
					ColorUtils.setcolor(g, -5);
					g.fillRect(0, 30, 240, 1);
					ColorUtils.setcolor(g, -6);
					g.fillRect(0, 31, 240, 1);
					ColorUtils.setcolor(g, -9);
					g.fillRect(0, 294, 240, 1);
					g.fillRect(0, 70, 240, 1);
					

					if(profileimg != null)
					{
						if(hasAva)
						{
							g.drawImage(profileimg, 8, 35, 0);
							ColorUtils.setcolor(g, ColorUtils.ONLINE);
							g.fillArc(27, 54, 6, 6, 0, 360);
						}
						else
						{
							g.drawImage(profileimg, 8, 35, 0);
						}
					}
					
					ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
					if(selectedBtn > 0)
					{
						g.fillRect(0, 70 + (oneitemheight * (selectedBtn - 1)), 240, oneitemheight);
					}
					

					if(friendimg != null)
					{
						g.drawImage(friendimg, 10, 83, 0);
					}

					if(groupimg != null)
					{
						g.drawImage(groupimg, 9, 108, 0);
					}
					
					ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.setFont(Font.getFont(0, 0, 8));
					
					g.drawString(name + " " + lastname, 41, 40-8,0);

					if(keysMode && selectedBtn == 1)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(friendsStr, 28, 72, 0);
					if(keysMode && selectedBtn == 2)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(groupsStr, 28, 96, 0);
					if(keysMode && selectedBtn == 3)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(musicStr, 29, 120, 0);
					if(keysMode && selectedBtn == 4)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(videosStr, 29, 144, 0);
					if(keysMode && selectedBtn == 5)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(photosStr, 29, 168, 0);
					if(keysMode && selectedBtn == 6)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(docsStr, 29, 192, 0);
					if(keysMode && selectedBtn == 7)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(exitStr, 29, 228, 0);

					if(exit != null)
					{
						g.drawImage(exit, 9, 209, 0);
					}
					
					g.translate(0,-g.getTranslateY());

					ColorUtils.setcolor(g, 3);
					g.fillRect(0, 0, 320, 30);
					ColorUtils.setcolor(g, -3);
					g.fillRect(0, DisplayUtils.height - 25, 320, 25);

					if(logoImg != null)
					{
						g.drawImage(logoImg, 1, 1, 0);
					}
					
					if(settingsImg != null)
					{
						g.drawImage(settingsImg, 228, 9, 0);
					}
					
					if(menuImg != null)
					{
						g.drawImage(menuImg, 212, 303, 0);
					}
					
					if(newsImg != null)
					{
						g.drawImage(newsImg, 18, 301, 0);
					}
					
					if(VikaTouch.unreadCount > 0)
					{
						if(dialImg2 != null)
						{
							g.drawImage(dialImg2, 114, 299, 0);
							g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
							g.drawString(""+VikaTouch.unreadCount, 126, 300, 0);
						}
						else if(dialImg != null)
						{
							g.drawImage(dialImg, 114, 302, 0);
						}

					}
					else
					{
						if(dialImg != null)
						{
							g.drawImage(dialImg, 114, 302, 0);
						}
					}
					
					break;
				}
				

				
				case DisplayUtils.DISPLAY_EQWERTY:
				{
					//e72, e5, etc
					
					update(g);
					
					ColorUtils.setcolor(g, -7);
					g.fillRect(0, 66, 320, 4);
					ColorUtils.setcolor(g, -8);
					g.fillRect(0, 68, 320, 2);
					ColorUtils.setcolor(g, -4);
					g.fillRect(0, 29, 320, 1);
					ColorUtils.setcolor(g, -5);
					g.fillRect(0, 30, 320, 1);
					ColorUtils.setcolor(g, -6);
					g.fillRect(0, 31, 320, 1);
					ColorUtils.setcolor(g, -9);
					g.fillRect(0, 294, 320, 1);
					g.fillRect(0, 70, 320, 1);
					
					if(profileimg != null)
					{
						if(hasAva)
						{
							g.drawImage(profileimg, 8, 35, 0);
							ColorUtils.setcolor(g, ColorUtils.ONLINE);
							g.fillArc(27, 54, 6, 6, 0, 360);
						}
						else
						{
							g.drawImage(profileimg, 8, 35, 0);
						}
					}
					
					ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
					if(selectedBtn > 0)
					{
						g.fillRect(0, 70 + (oneitemheight * (selectedBtn - 1)), 320, oneitemheight);
					}
					

					if(friendimg != null)
					{
						g.drawImage(friendimg, 10, 83, 0);
					}

					if(groupimg != null)
					{
						g.drawImage(groupimg, 9, 108, 0);
					}
					
					ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.setFont(Font.getFont(0, 0, 8));
					
					g.drawString(name + " " + lastname, 41, 40-8,0);
					

					if(keysMode && selectedBtn == 1)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(friendsStr, 28, 72, 0);
					if(keysMode && selectedBtn == 2)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(groupsStr, 28, 96, 0);
					if(keysMode && selectedBtn == 3)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(musicStr, 29, 120, 0);
					if(keysMode && selectedBtn == 4)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(videosStr, 29, 144, 0);
					if(keysMode && selectedBtn == 5)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(photosStr, 29, 168, 0);
					if(keysMode && selectedBtn == 6)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(docsStr, 29, 192, 0);
					if(keysMode && selectedBtn == 7)
						ColorUtils.setcolor(g, ColorUtils.BACKGROUND);
					else
						ColorUtils.setcolor(g, ColorUtils.TEXT);
					g.drawString(exitStr, 29, 228, 0);
					
					g.translate(0,-g.getTranslateY());

					
					ColorUtils.setcolor(g, ColorUtils.BUTTONCOLOR);
					g.fillRect(0, 0, 320, 30);
					ColorUtils.setcolor(g, -3);
					g.fillRect(0, 215, 320, 25);
						
						
					if(menuImg != null)
					{
						g.drawImage(menuImg, 292, 303-75, 0);
					}
						
					if(newsImg != null)
					{
						g.drawImage(newsImg, 18, 301-75, 0);
					}
						
					if(VikaTouch.unreadCount > 0)
					{
						if(dialImg2 != null)
						{
							g.drawImage(dialImg2, 114, 299-75, 0);
							g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
							g.drawString(""+VikaTouch.unreadCount, 126, 300-75, 0);
						}
						else if(dialImg != null)
						{
							g.drawImage(dialImg, 114, 302-75, 0);
						}
	
					}
					else
					{
						if(dialImg != null)
						{
							g.drawImage(dialImg, 114, 302-75, 0);
						}
					}
				}
				
				case DisplayUtils.DISPLAY_UNDEFINED:
					
				default:
				{

					ColorUtils.setcolor(g, 2);
					g.drawString("Ваше ", 2, 0, 0);
					g.drawString("разрешение", 2, 15, 0);
					g.drawString("экрана", 2, 30, 0);
					g.drawString("не", 2, 45, 0);
					g.drawString("поддерживается", 2, 60, 0);
				}
			}
		}
	}*/
	
	public final void release(int x, int y)
	{
		if(!dragging)
		{
			if(y > 58 && y < DisplayUtils.height-50)
			{
				int y1 = scrolled + 140;
				for(int i = 0; i < itemsCount; i++)
				{
					int y2 = y1 + uiItems[i].getDrawHeight();
					if(y > y1 && y < y2)
					{
						uiItems[i].tap(x, y - y1);
						break;
					}
					y1 = y2;	
				}
			}
		}
		super.release(x, y);
	}
	/*
	public final void release(int x, int y)
	{
		if(!dragging)
		{
			switch(DisplayUtils.idispi)
			{
				case DisplayUtils.DISPLAY_PORTRAIT:
				{
					if(y > 58 && y < 590)
					{
						for(int i = 0; i < itemsCount; i++)
						{
							int y1 = scrolled + 140 + (i * oneitemheight);
							int y2 = y1 + oneitemheight;
							if(y > y1 && y < y2)
							{
								VikaTouch.inst.cmdsInst.command(itemscmd[i], this);
								break;
							}
							
						}
					}
					break;
				}

				case DisplayUtils.DISPLAY_S40:
				case DisplayUtils.DISPLAY_ASHA311:
				case DisplayUtils.DISPLAY_EQWERTY:
				{
					if(y > 30 && y < DisplayUtils.height - 25)
					{
						for(int i = 0; i < itemsCount; i++)
						{
							int y1 = scrolled + 70 + (i * oneitemheight);
							int y2 = y1 + oneitemheight;
							if(y > y1 && y < y2)
							{
								VikaTouch.inst.cmdsInst.command(itemscmd[i], this);
								break;
							}
							
						}
					}
					break;
				}
				case DisplayUtils.DISPLAY_ALBUM:
				{
					if(y > 58 && y < 310)
					{
						for(int i = 0; i < itemsCount; i++)
						{
							int y1 = scrolled + 140 + (i * oneitemheight);
							int y2 = y1 + oneitemheight;
							if(y > y1 && y < y2)
							{
								VikaTouch.inst.cmdsInst.command(itemscmd[i], this);
								break;
							}
							
						}
					}
					break;
				}
				
				default:
				{
					
					break;
				}
			}
		}
		super.release(x, y);
	} */

	public void onMenuItemPress(int i) {
		VikaTouch.inst.cmdsInst.command(i, this);
	}

	public void onMenuItemOption(int i) {
		// TODO Auto-generated method stub
		
	}

}
