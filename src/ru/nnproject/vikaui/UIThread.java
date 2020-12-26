package ru.nnproject.vikaui;

public class UIThread extends Thread {

	private VikaCanvas canvas;

	public UIThread(VikaCanvas canvas) {
		super();
		this.canvas = canvas;
		if(canvas.drawMaxPriority()) {
			this.setPriority(Thread.MAX_PRIORITY);
		} else {
			this.setPriority(Thread.NORM_PRIORITY);
		}
	}

	public void run() {
		while (true) {
			try {
				canvas.tick();
			} catch (Throwable e) {
				// VikaTouch.sendLog("Tick failed. "+e.toString());
			}
			// Thread.yield();
			try {
				Thread.sleep(1000 / canvas.getFPSLimit());
			} catch (Exception e) {
				// VikaTouch.sendLog("UI thread exit");
				return;
			}
		}
	}

}
