package vikatouch.utils;

public class IntObject
{
	public IntObject(int value)
	{
		this.value = value;
	}
	
	public final int hashCode()
	{
		return value;
	}
	
	public final boolean equals(Object var1)
	{
		if (var1 instanceof IntObject) {
			if(this.value == ((IntObject) var1).value)
			{
				return true;
			}
			if(this == (IntObject) var1)
			{
				return true;
			}
			return false;
		} else if (var1 instanceof Integer) {
			if(this.value == ((Integer) var1).intValue())
			{
				return true;
			}
			return false;
		}
		return false;
	}
	
	public int value;

}
