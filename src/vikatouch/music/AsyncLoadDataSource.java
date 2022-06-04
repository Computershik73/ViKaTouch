package vikatouch.music;

import java.io.IOException;

import javax.microedition.media.Control;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;

import vikatouch.utils.VikaUtils;
import vikatouch.utils.emulatordetect.EmulatorDetector;

public class AsyncLoadDataSource extends DataSource {

	private SourceStream[] streams = new SourceStream[1];
	private String locator;
	private AsyncLoadStream stream;

	public AsyncLoadDataSource(String aLocator) throws IOException {
		super(aLocator);
		this.locator = aLocator;
		stream = new AsyncLoadStream(locator);
		streams[0] = stream;
	}

	public Control getControl(String controlType) {
		return null;
	}

	public Control[] getControls() {
		return new Control[0];
	}

	public void setListener(AsyncLoadListener l) {
		if(stream != null) stream.setListener(l);
	}

	public void connect() throws IOException {
		VikaUtils.logToFile("connect");
		if(stream == null) {
			stream = new AsyncLoadStream(locator);
			streams[0] = stream;
		}
	}

	public void disconnect() {
		VikaUtils.logToFile("disconnect");
		try {
			stream.deallocate();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getContentType() {
		VikaUtils.logToFile("getContentType");
		//return EmulatorDetector.isEmulator && EmulatorDetector.emulatorType == EmulatorDetector.EM_J2L ? "audio/mpeg" : null;
		return "audio/mpeg";
	}

	public SourceStream[] getStreams() {
		return streams;
	}

	public AsyncLoadStream getStream() {
		return stream;
	}

	public void start() throws IOException {
	}

	public void stop() throws IOException {
	}

}
