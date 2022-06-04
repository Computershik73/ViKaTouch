package vikatouch.music;

public interface AsyncLoadListener {

	public void bufferedPercent(float percent);

	public void bufferDone(int size);

	public void deallocated();

	public void positionReset();

}
