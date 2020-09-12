package ru.nnproject.vikaui.menu.items;

public abstract interface PressableUIItem extends UIItem
{
	public static final int KEY_OK = -5;
	public static final int KEY_FUNC = -6;
	public static final int KEY_DELETE = 8;
	public static final int KEY_BACK = 999; //TODO: obtain code
	public static final int KEY_RFUNC = -7;
	
	public void tap(int x, int y);
	
	public void keyPressed(int key);
	
	public boolean isSelected();
	
	public void setSelected(boolean selected);
}
