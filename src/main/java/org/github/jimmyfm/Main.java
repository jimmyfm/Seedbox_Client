package org.github.jimmyfm;

import java.io.IOException;
import java.util.logging.Logger;

import org.github.jimmyfm.monitors.DownloadMonitor;
import org.github.jimmyfm.monitors.FolderMonitor;
import org.github.jimmyfm.utils.Settings;

public class Main {

	private static final Logger LOG = Logger.getAnonymousLogger();

	public static void main(String[] args) throws IOException, InterruptedException {
		Settings.getInstance();

		Thread downloadMonitor = new Thread(new DownloadMonitor());
		downloadMonitor.start();

		// Thread clipboardMonitor = new Thread(new ClipboardMonitor());
		// clipboardMonitor.start();

		Thread folderMonitor = new Thread(new FolderMonitor());
		folderMonitor.start();

		// Thread consoleListener = new Thread(new ConsoleListener());
		// consoleListener.start();

		downloadMonitor.join();
	}

}
