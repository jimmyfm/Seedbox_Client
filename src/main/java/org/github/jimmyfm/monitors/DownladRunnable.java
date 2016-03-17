package org.github.jimmyfm.monitors;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.github.jimmyfm.utils.Settings;
import org.github.jimmyfm.utils.TorrentStatus;

public class DownladRunnable implements Runnable {

	private static final Logger LOG = Logger.getLogger(DownladRunnable.class.getName());

	private TorrentStatus torrent;
	private String baseUrl;
	private String url;

	public DownladRunnable(TorrentStatus torrent, String baseUrl, String url) {
		this.torrent = torrent;
		this.baseUrl = baseUrl;
		this.url = url;
	}

	@Override
	public void run() {
		try {
			LOG.info("DL started");
			doWork();
			LOG.info("DL done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void doWork() throws IOException {
		File dlFolder = new File(Settings.getInstance().getDropdownFolder(), torrent.id);
		String relativePath = url.substring(baseUrl.length());

		File file = new File(dlFolder, URLDecoder.decode(relativePath, "UTF-8"));
		file.getParentFile().mkdirs();

		URL dlLoop = new URL(url);
		HttpsURLConnection connLoop = HttpsURLConnection.class.cast(dlLoop.openConnection());
		int size = connLoop.getHeaderFieldInt("Content-Length", -1);

		if (file.exists()) {
			// Partial download or whatever, let's download it again
			if (size != file.length()) {
				file.delete();
			} else {
				// File already downloaded, let's move one
				return;
			}
		}

		Files.copy(dlLoop.openStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

}
