package vikatouch;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import ru.nnproject.vikaui.screen.VikaScreen;
import vikatouch.screens.AboutScreen;
import vikatouch.screens.ChatScreen;
import vikatouch.screens.DialogsScreen;
import vikatouch.screens.LoginScreen;
import vikatouch.screens.MainScreen;
import vikatouch.screens.NewsScreen;
import vikatouch.screens.PhotosScreen;
import vikatouch.screens.menu.DocsScreen;
import vikatouch.screens.menu.FriendsScreen;
import vikatouch.screens.menu.GroupsScreen;
import vikatouch.screens.menu.MenuScreen;
import vikatouch.screens.menu.VideosScreen;
import vikatouch.screens.music.MusicScreen;
import vikatouch.screens.page.ProfilePageScreen;
import vikatouch.settings.Settings;
import vikatouch.settings.SettingsScreen;

public class CommandsImpl implements CommandListener {

	public static final Command close = new Command("Закрыть приложение", 4, 0);

	public void commandAction(Command c, Displayable d) {
		if (c == close) {
			VikaTouch.appInst.destroyApp(false);
		}
	}

	public void command(final int i, final VikaScreen s) {
		final Thread t = new Thread() {
			public void run() {
				try {
					switch (i) {
					case -1: {
						// Выход
						// Settings.saveSettings();
						VikaTouch.appInst.destroyApp(false);
						break;
					}
					case 0: {
						// Новости
						news(s);
						break;
					}
					case 1: {
						// Сообщения
	
						dialogs(s);
						break;
					}
					case 2: {
						// Меню
						menu(s);
						break;
					}
					case 3: {
						// Логин
						VikaTouch.loading = true;
	
						if (s instanceof LoginScreen) {
							VikaTouch.inst.login(LoginScreen.user, LoginScreen.pass);
						}
	
						VikaTouch.loading = false;
						break;
					}
					case 4: {
						// Друзья
						if (s instanceof MenuScreen) {
							FriendsScreen friendsScr = new FriendsScreen();
							friendsScr.loadFriends(0, 0, null, null);
							VikaTouch.setDisplay(friendsScr, 1);
						}
						break;
					}
					case 5: {
						// Группы
						if (s instanceof MenuScreen) {
							GroupsScreen grScr = new GroupsScreen();
							grScr.loadGroups(0, VikaTouch.integerUserId, null, null);
							VikaTouch.setDisplay(grScr, 1);
						}
						break;
					}
					case 6: {
						// Музыка
						if (s instanceof MenuScreen) {
							MusicScreen.open(VikaTouch.integerUserId, null, null);
						}
						break;
					}
					case 7: {
						// Видео
						if (s instanceof MenuScreen) {
							VideosScreen videosScr = new VideosScreen();
							videosScr.load(0, 0, null, null);
							VikaTouch.setDisplay(videosScr, 1);
						}
						break;
					}
					case 8: {
						// Фотки
						if (s instanceof MenuScreen) {
							// VikaTouch.popup(new InfoPopup("Функционал фотографий ещё не реализован.
							// Следите за обновлениями.",null,TextLocal.inst.get("title.photos"),
							// TextLocal.inst.get("back")));
	
							if (VikaTouch.photos == null)
								VikaTouch.photos = new PhotosScreen(VikaTouch.integerUserId, 0);
							VikaTouch.setDisplay(VikaTouch.photos, 1);
						}
						break;
					}
					case 9: {
						// Документы
						if (s instanceof MenuScreen) {
							DocsScreen docsScr = new DocsScreen();
							docsScr.loadDocs(0, 0, null, null);
							VikaTouch.setDisplay(docsScr, 1);
						}
						break;
					}
					case 10: {
						// свайп влево
						if (s instanceof MenuScreen || s instanceof DocsScreen || s instanceof GroupsScreen
								|| s instanceof VideosScreen || s instanceof FriendsScreen || s instanceof PhotosScreen) {
							dialogs(s);
						} else if (s instanceof DialogsScreen) {
							news(s);
						}
						break;
					}
					case 11: {
	
						// свайп вправо
						if (s instanceof DialogsScreen) {
							menu(s);
						} else if (s instanceof NewsScreen) {
							dialogs(s);
						}
						break;
					}
					case 13: {
						// Настройки
						if (VikaTouch.setsScr == null)
							VikaTouch.setsScr = new SettingsScreen();
						VikaTouch.setDisplay(VikaTouch.setsScr, 1);
						break;
					}
					case 14: {
						// Назад
						back(s);
						break;
					}
					case 15: {
						// О программе
						if (VikaTouch.about == null)
							VikaTouch.about = new AboutScreen();
						VikaTouch.setDisplay(VikaTouch.about, 1);
						break;
					}
					case 16: {
						// Свой профиль
						VikaTouch.setDisplay(new ProfilePageScreen(VikaTouch.integerUserId), 1);
						break;
					}
					default: {
						break;
					}
					}
				} catch (Exception e) {
					
				}
			}
		};
		t.start();
	}

	protected void callLeaveEvent(VikaScreen s) {
		try {
			s.onLeave();
		} catch (Exception e) {
		}
	}

	protected void back(VikaScreen s) {

		if (s instanceof MenuScreen || s instanceof LoginScreen) {
			// command(-1, s);
			return;
			// открывался сплеш....
			// дурак чтоли
			// Кто? Арман шоле?
		}
		// А вот с экраном плейлиста как раз всё ок было.
		/*
		 * else if (s instanceof MusicScreen)//Добавил Белов Юрий {
		 * VikaTouch.setDisplay(VikaTouch.menuScr, -1); return; }//тут закончил
		 */
		if (Settings.dontBack) {
			if (s instanceof SettingsScreen) {
				// Settings.saveSettings();
				if (VikaTouch.menuScr != null && VikaTouch.accessToken != null && VikaTouch.accessToken != "") {
					VikaTouch.setDisplay(VikaTouch.menuScr, -1);
				} else {
					VikaTouch.setDisplay(VikaTouch.loginScr, -1);
				}
			} else if (s instanceof ChatScreen) {
				VikaTouch.setDisplay(VikaTouch.dialogsScr, -1);
			} else
				VikaTouch.setDisplay(VikaTouch.menuScr, -1);
		} else {
			if (s instanceof SettingsScreen) {

			}
			if (s instanceof MainScreen) {
				VikaTouch.setDisplay(((MainScreen) s).backScreen, -1);
			}
			if (s instanceof ChatScreen) {
				VikaTouch.setDisplay(VikaTouch.dialogsScr, -1);
				Dialogs.refreshDialogsList(true, false);
			}
		}

		callLeaveEvent(s);
	}

	protected void news(VikaScreen s) {
		if (!(s instanceof NewsScreen)) {
			// VikaTouch.loading = true;

			if (VikaTouch.newsScr == null) {
				VikaTouch.newsScr = new NewsScreen();
				VikaTouch.newsScr.newsSource = 0;
				VikaTouch.newsScr.loadPosts();
			}
			VikaTouch.setDisplay(VikaTouch.newsScr, -1);
			callLeaveEvent(s);
		}
	}

	protected void dialogs(VikaScreen s) {
		if (!(s instanceof DialogsScreen)) {
			// VikaTouch.loading = true;

			if (VikaTouch.dialogsScr == null)
				VikaTouch.dialogsScr = new DialogsScreen();
			VikaTouch.setDisplay(VikaTouch.dialogsScr, 0);
			callLeaveEvent(s);
		} else {
			Dialogs.refreshDialogsList(true, false);
		}
	}

	protected void menu(VikaScreen s) {
		// меню должно возвращать В МЕНЮ, есть кнопка назад.
		if (!(s instanceof MenuScreen)) {
			VikaTouch.setDisplay(VikaTouch.menuScr, 1);
			callLeaveEvent(s);
		}
		/*
		 * if(s instanceof SettingsScreen) { // Settings.saveSettings(); }
		 * if(MenuScreen.lastMenu == DisplayUtils.CANVAS_MENU || s instanceof DocsScreen
		 * || s instanceof GroupsScreen || s instanceof VideosScreen || s instanceof
		 * FriendsScreen || s instanceof PhotosScreen) { if(!(s instanceof MenuScreen))
		 * { VikaTouch.setDisplay(VikaTouch.menuScr, 1); } } else if(MenuScreen.lastMenu
		 * == DisplayUtils.CANVAS_DOCSLIST) { VikaTouch.setDisplay(VikaTouch.docsScr,
		 * 1); } else if(MenuScreen.lastMenu == DisplayUtils.CANVAS_PHOTOSLIST) {
		 * VikaTouch.setDisplay(VikaTouch.photosScr, 1); } else if(MenuScreen.lastMenu
		 * == DisplayUtils.CANVAS_FRIENDSLIST) {
		 * VikaTouch.setDisplay(VikaTouch.friendsScr, 1); } else if(MenuScreen.lastMenu
		 * == DisplayUtils.CANVAS_GROUPSLIST) { VikaTouch.setDisplay(VikaTouch.grScr,
		 * 1); } else if(MenuScreen.lastMenu == DisplayUtils.CANVAS_VIDEOSLIST) {
		 * VikaTouch.setDisplay(VikaTouch.videosScr, 1); }
		 */
	}

}
