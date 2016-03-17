package org.github.jimmyfm.monitors;

import java.io.Console;
import java.util.logging.Logger;

public class ConsoleListener implements Runnable {
	private static final Logger LOG = Logger.getLogger(ConsoleListener.class.getName());

	public void run() {
		LOG.info("Started");
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(10000);
				doWork();
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void doWork() {
		Console cons = System.console();
		String s = cons.readLine();
		// LOG.info(s);
	}

}
