package vikatouch;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import ru.nnproject.vikaui.popup.InfoPopup;
import ru.nnproject.vikaui.screen.VikaScreen;
import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.locale.TextLocal;
import vikatouch.screens.AboutScreen;
import vikatouch.screens.ChatScreen;
import vikatouch.screens.DialogsScreen;
import vikatouch.screens.LoginScreen;
import vikatouch.screens.MainScreen;
import vikatouch.screens.NewsScreen;
import vikatouch.screens.menu.DocsScreen;
import vikatouch.screens.menu.FriendsScreen;
import vikatouch.screens.menu.GroupsScreen;
import vikatouch.screens.menu.MenuScreen;
import vikatouch.screens.menu.PhotosScreen;
import vikatouch.screens.menu.VideosScreen;
import vikatouch.screens.music.MusicScreen;
import vikatouch.screens.page.GroupPageScreen;
import vikatouch.settings.Settings;
import vikatouch.settings.SettingsScreen;
import vikatouch.utils.VikaUtils;
import vikatouch.utils.url.URLBuilder;

public class CommandsImpl
	implements CommandListener
{

    public static final Command close = new Command("Закрыть приложение", 4, 0);

	public void commandAction(Command c, Displayable d)
	{
		if(c == close)
		{
			VikaTouch.appInst.destroyApp(false);
		}
	}
	
	public void command(final int i, final VikaScreen s)
	{
		final Thread t = new Thread()
		{
			public void run()
			{
				switch(i)
				{
					case -1:
					{
						//Выход
						Settings.saveSettings();
						VikaTouch.appInst.destroyApp(false);
						break;
					}
					case 0:
					{
						//Новости
						news(s);
						break;
					}
					case 1:
					{
						//Сообщения
						dialogs(s);
						break;
					}
					case 2:
					{
						//Меню
						menu(s);
						break;
					}
					case 3:
					{
						//Логин
						VikaTouch.loading = true;
						
						if(s instanceof LoginScreen)
						{
							VikaTouch.inst.login(LoginScreen.user, LoginScreen.pass);
						}

						VikaTouch.loading = false;
						break;
					}
					case 4:
					{
						//Друзья
						if(s instanceof MenuScreen)
						{
							if(VikaTouch.friendsScr == null)
								VikaTouch.friendsScr = new FriendsScreen();
							VikaTouch.friendsScr.loadFriends(0, 0, null, null);
							VikaTouch.setDisplay(VikaTouch.friendsScr, 1);
						}
						break;
					}
					case 5:
					{
						//Группы
						if(s instanceof MenuScreen)
						{
							if(VikaTouch.grScr == null)
								VikaTouch.grScr = new GroupsScreen();
							VikaTouch.grScr.loadGroups(0, Integer.parseInt(VikaTouch.userId), null, null);
							VikaTouch.setDisplay(VikaTouch.grScr, 1);
						}
						break;
					}
					case 6:
					{
						//Музыка
						if(s instanceof MenuScreen)
						{
							MusicScreen.open(Integer.parseInt(VikaTouch.userId), null, null);
						}
						break;
					}
					case 7:
					{
						//Видео
						if(s instanceof MenuScreen)
						{
							if(VikaTouch.videosScr == null)
								VikaTouch.videosScr = new VideosScreen();
							VikaTouch.videosScr.load(0, 0, null, null);
							VikaTouch.setDisplay(VikaTouch.videosScr, 1);
						}
						break;
					}
					case 8:
					{
						//Фотки
						if(s instanceof MenuScreen)
						{
							VikaTouch.popup(new InfoPopup("Функционал фотографий ещё не реализован. Следите за обновлениями.",null,TextLocal.inst.get("title.photos"), TextLocal.inst.get("back")));
							/*
							if(VikaTouch.photosCanv == null)
								VikaTouch.photosCanv = new PhotosScreen();
							VikaTouch.setDisplay(VikaTouch.photosCanv);*/
						}
						break;
					}
					case 9:
					{
						//Документы
						if(s instanceof MenuScreen)
						{
							if(VikaTouch.docsScr == null)
								VikaTouch.docsScr = new DocsScreen();
							VikaTouch.docsScr.loadDocs(0, 0, null, null);
							VikaTouch.setDisplay(VikaTouch.docsScr, 1);
						}
						break;
					}
					case 10:
					{
						//свайп влево
						if(s instanceof MenuScreen || s instanceof DocsScreen  || s instanceof GroupsScreen || s instanceof VideosScreen || s instanceof FriendsScreen || s instanceof PhotosScreen)
						{
							dialogs(s);
						}
						if(s instanceof DialogsScreen)
						{
							news(s);
						}
						break;
					}
					case 11:
					{
						
						//свайп вправо
						if(s instanceof DialogsScreen)
						{
							menu(s);
						}
		
						if(s instanceof NewsScreen)
						{
							dialogs(s);
						}
						break;
					}
					case 13:
					{
						//Настройки
						if(VikaTouch.setsScr == null)
							VikaTouch.setsScr = new SettingsScreen();
						VikaTouch.setDisplay(VikaTouch.setsScr, 1);
						break;
					}
					case 14:
					{
						//Назад
						back(s);
						break;
					}
					case 15:
					{
						// О программе
						if(VikaTouch.about == null)
							VikaTouch.about = new AboutScreen();
						VikaTouch.setDisplay(VikaTouch.about, 1);
						break;
					}
					default:
					{
						break;
					}
				}
				Thread.yield();
			}
		};
		t.start();
	}

	protected void back(VikaScreen s)
	{
		
		if(s instanceof MenuScreen)
		{
			//command(-1, s);
			return;
			// открывался сплеш....
			//дурак чтоли
		}
		if(Settings.dontBack)
		{
			if(s instanceof SettingsScreen)
			{
				//Settings.saveSettings();
				if(VikaTouch.menuScr != null && VikaTouch.accessToken != null && VikaTouch.accessToken != "")
				{
					VikaTouch.setDisplay(VikaTouch.menuScr, -1);
				}
				else
				{
					VikaTouch.setDisplay(VikaTouch.loginScr, -1);
				}
			}
			if(s instanceof DocsScreen || s instanceof AboutScreen || s instanceof GroupsScreen || s instanceof VideosScreen || s instanceof FriendsScreen || s instanceof PhotosScreen)
			{
				VikaTouch.setDisplay(VikaTouch.menuScr, -1);
			}
			if(s instanceof ChatScreen)
			{
				VikaTouch.setDisplay(VikaTouch.dialogsScr, -1);
			}
			if(s instanceof GroupPageScreen)
			{
				VikaTouch.setDisplay(VikaTouch.grScr, -1);
			}
		}
		else
		{
			if(s instanceof SettingsScreen)
			{
			//	Settings.saveSettings();
			}
			if(s instanceof MainScreen)
			{
				VikaTouch.setDisplay(((MainScreen)s).backScreen, -1);
			}
			if(s instanceof ChatScreen)
			{
				VikaTouch.setDisplay(VikaTouch.dialogsScr, -1);
			}
		}
	}

	protected void news(VikaScreen s)
	{
		if(!(s instanceof NewsScreen))
		{
			VikaTouch.loading = true;
			
			if(VikaTouch.newsScr == null)
				VikaTouch.newsScr = new NewsScreen();
			VikaTouch.setDisplay(VikaTouch.newsScr, -1);
		}
	}

	protected void dialogs(VikaScreen s)
	{
		if(!(s instanceof DialogsScreen))
		{
			VikaTouch.loading = true;

			if(VikaTouch.dialogsScr == null)
				VikaTouch.dialogsScr = new DialogsScreen();
			VikaTouch.setDisplay(VikaTouch.dialogsScr, 0);
		}
		else
		{
			Dialogs.refreshDialogsList(true);
		}
	}

	protected void menu(VikaScreen s)
	{
		if(s instanceof SettingsScreen)
		{
		//	Settings.saveSettings();
		}
		if(MenuScreen.lastMenu == DisplayUtils.CANVAS_MENU || s instanceof DocsScreen  || s instanceof GroupsScreen || s instanceof VideosScreen || s instanceof FriendsScreen || s instanceof PhotosScreen)
		{
			if(!(s instanceof MenuScreen))
			{
				VikaTouch.setDisplay(VikaTouch.menuScr, 1);
			}
		}
		else if(MenuScreen.lastMenu == DisplayUtils.CANVAS_DOCSLIST)
		{
			VikaTouch.setDisplay(VikaTouch.docsScr, 1);
		}
		else if(MenuScreen.lastMenu == DisplayUtils.CANVAS_PHOTOSLIST)
		{
			VikaTouch.setDisplay(VikaTouch.photosScr, 1);
		}
		else if(MenuScreen.lastMenu == DisplayUtils.CANVAS_FRIENDSLIST)
		{
			VikaTouch.setDisplay(VikaTouch.friendsScr, 1);
		}
		else if(MenuScreen.lastMenu == DisplayUtils.CANVAS_GROUPSLIST)
		{
			VikaTouch.setDisplay(VikaTouch.grScr, 1);
		}
		else if(MenuScreen.lastMenu == DisplayUtils.CANVAS_VIDEOSLIST)
		{
			VikaTouch.setDisplay(VikaTouch.videosScr, 1);
		}
	}

}
