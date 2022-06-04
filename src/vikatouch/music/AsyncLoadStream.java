package vikatouch.music;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.media.Control;
import javax.microedition.media.protocol.ContentDescriptor;
import javax.microedition.media.protocol.SourceStream;

import vikatouch.utils.VikaUtils;

public class AsyncLoadStream extends InputStream implements SourceStream {

	private int len;
	private int index;
	private Thread thread;
	private int loaded;
	private boolean closed;
	private byte[] buffer;
	private boolean finished;
	private AsyncLoadListener listener;
	private ContentDescriptor contentDescriptor;
	protected boolean interrupted;


	public AsyncLoadStream(String url) throws IOException {
		contentDescriptor = new ContentDescriptor("audio/mpeg");
		final ContentConnection connection = (ContentConnection) Connector.open(url);
		final InputStream in = connection.openInputStream();
		len = (int) connection.getLength();
		if(len == -1)
			throw new IOException();
		init(in, connection, len);
	}
	public AsyncLoadStream(InputStream in, int length) throws IOException {
		init(in, null, length);
	}

	private void init(final InputStream in, final ContentConnection connection, int length) throws IOException {
		len = length;
		if(len <= 0)
			throw new IllegalArgumentException("length");
		buffer = new byte[len];
		VikaUtils.logToFile("stream init " + Thread.currentThread());
		thread = new Thread() {
			public void run() {
				VikaUtils.logToFile("stream runnnn " + Thread.currentThread());
				//byte[] buf = new byte[8 * 1024];
				int read;
				long start = System.currentTimeMillis();
				try {
					/*
					while ((read = in.read(buf)) != -1) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
						//if(interrupted) {
							silentDeallocate();
							in.close();
							if(connection != null) connection.close();
							return;
						}
						for(int i = 0; i < read; i++) {
							//buffer[loaded] = buf[i] & 0xff;
							buffer[loaded] = buf[i];
							loaded++;
						}
						if(listener != null) {
							float f = ((float)loaded/(float)len) * 100F;
							listener.bufferedPercent(f);
							//System.out.println(f+"%");
						}
					}
					 */
					while ((read = in.read()) != -1) {
						if(loaded % 16384 == 0) {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								silentDeallocate();
								in.close();
								if(connection != null) connection.close();
								return;
							}
							if(listener != null) {
								float f = ((float)loaded/(float)len) * 100F;
								listener.bufferedPercent(f);
								System.out.println(f+"%");
							}
						}
						buffer[loaded] = (byte) read;
						loaded++;
					}
					if(listener != null) listener.bufferDone(loaded);
					VikaUtils.logToFile("Loaded: " + loaded / 1024 + "K (" + (System.currentTimeMillis()-start) + "ms)");
				} catch (Exception e) {
					e.printStackTrace();
					VikaUtils.logToFile("Error async: " + e.toString());
				}
				try {
					in.close();
				} catch (Exception e) {
				}
				if(connection != null)
					try {
						connection.close();
					} catch (Exception e) {
					}
				finished = true;
			}
		};
		thread.setPriority(10);
		thread.start();
	}

	public void setListener(AsyncLoadListener newListener) {
		listener = newListener;
	}

	/**
	 * Returns buffer
	 * @throws IOException Deallocated
	 * @throws IOException Buffer loading not finished yet
	 */
	public ByteArrayInputStream getBytesInputStream() throws IOException {
		return new ByteArrayInputStream(getBytes());
	}

	/**
	 * Returns buffer
	 * @throws IOException Deallocated
	 * @throws IOException Buffer loading not finished yet
	 */
	public byte[] getBytes() throws IOException {
		if(buffer == null) throw new IOException("Deallocated");
		if(!finished) throw new IOException("Not finished");
		//byte[] b = new byte[len];
		//for(int i = 0; i < len; i++) {
		//	b[i] = (byte)(buffer[i] & 0xff);
		//}
		//return b;
		//System.gc();
		return buffer;
	}

	/*
	public int[] getBuffer() throws IOException {
		if(buffer == null) throw new IOException("Deallocated");
		if(!finished) throw new IOException("Not finished");
		return buffer;
	}
	 */

	/**
	 * Resets read position
	 * @throws IOException Deallocated
	 */
	public void reset() throws IOException {
		VikaUtils.logToFile("reset");
		if(buffer == null) throw new IOException("Deallocated");
		if(listener != null) listener.positionReset();
		index = 0;
		closed = false;
	}

	/**
	 * blocks read()
	 * @see #deallocate
	 */
	public void close() throws IOException {
		VikaUtils.logToFile("close");
		super.close();
		//thread.interrupt();
		closed = true;
	}

	/**
	 * @throws IOException Deallocated
	 * @throws IOException Closed
	 */
	public int read() throws IOException {
		if(buffer == null) throw new IOException("Deallocated");
		if(closed) throw new IOException("Closed");
		if(index >= len) return -1;
		while(loaded <= index) {
			VikaUtils.logToFile("read wait");
			try {
				Thread.sleep(20);
			} catch (Exception e) {
				throw new RuntimeException("interrupted");
			}
			Thread.yield();
		}
		int i = buffer[index] & 0xff;
		index++;
		return i;
	}

	/**
	 * deallocates buffer, closes stream
	 */
	public void deallocate() throws IOException {
		VikaUtils.logToFile("deallocate");
		if(buffer == null) return;
		if(listener != null) listener.deallocated();
		interrupted = true;
		thread.interrupt();
		closed = true;
		buffer = null;
		System.gc();
	}

	protected void silentDeallocate() throws IOException {
		VikaUtils.logToFile("silentDeallocate");
		if(buffer == null) return;
		closed = true;
		buffer = null;
		System.gc();
	}

	/**
	 * @deprecated Unsafe usage
	 * @throws IOException Deallocated
	 * @throws IOException Closed
	 * @throws IOException Out of bounds
	 */
	public void setPosition(int i) throws IOException {
		VikaUtils.logToFile("setPosition " + i);
		checkPosition(i);
		index = i;
	}


	/**
	 * @throws IOException Deallocated
	 * @throws IOException Closed
	 * @throws IOException Out of bounds
	 */
	public void checkPosition(int i) throws IOException {
		if(buffer == null) throw new IOException("Deallocated");
		if(closed) throw new IOException("Closed");
		if(i >= len || i >= loaded || i < 0) throw new IOException("Out of bounds");
	}

	public boolean finished() {
		return finished;
	}

	public Control getControl(String controlType) {
		return null;
	}

	public Control[] getControls() {
		return new Control[0];
	}

	public ContentDescriptor getContentDescriptor() {
		return contentDescriptor;
	}

	public long getContentLength() {
		VikaUtils.logToFile("getContentLength " + len);
		return len;
	}

	public int getSeekType() {
		return 2;
	}

	public int getTransferSize() {
		return 4096;
	}

	public long seek(long where) throws IOException {
		if(where == index) return where;
		VikaUtils.logToFile("seek " + where);
		if(where < 0) where = 0;
		setPosition((int) where);
		return index;
	}

	public long tell() {
		VikaUtils.logToFile("tell " + index);
		return index;
	}

}
