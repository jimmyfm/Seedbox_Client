package org.github.jimmyfm.monitors;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Logger;

import org.github.jimmyfm.utils.Settings;
import org.github.jimmyfm.utils.WebUtils;

public class FolderMonitor implements Runnable {

	private static final Logger LOG = Logger.getLogger(FolderMonitor.class.getName());

	public void run() {
		LOG.info("Started");
		while (!Thread.currentThread().isInterrupted()) {
			try {
				doWork();
				Thread.sleep(10000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void doWork() throws IOException {
		File folder = new File(Settings.getInstance().getPickupFolder());

		File[] listOfFiles = folder.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				return name.endsWith(".torrent") && dir.isDirectory();
			}
		});

		for (File file : listOfFiles) {
			addAndRename(file);
		}
	}

	private void addAndRename(File file) throws IOException {
		LOG.info("Adding torrent: " + file);
		WebUtils.addTorrentFile(file);
		file.renameTo(new File(file.getAbsolutePath() + ".added"));
	}

}
