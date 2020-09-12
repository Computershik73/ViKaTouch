package vikatouch.attachments;

// Нужен для объединения разных объектов ВК, умеющих в социалку. Например, в ImagePreview. Пригодится короче.
public interface ISocialable {
	public boolean canSave(); // возвращать false например для УЖЕ своиих фото.
	public void save(); // сохранить себе
	public boolean canLike(); // документы - 0 например
	public boolean getLikeStatus(); // лайкнул ли
	public void like(boolean val); // 1 - поставить лайк, 0 - убрать.
	public void send(); // отправить в лс
	public void repost(); // репост. Всё кроме постов - при вызове ничего не делать.
	public boolean commentsAliveable(); // можно ли комментировать
	public void openComments(); // открыть экран комментариев
	//public void sendComment(Comment comment); // отправить коммент
}
