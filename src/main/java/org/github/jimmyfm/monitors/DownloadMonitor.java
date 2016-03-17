package org.github.jimmyfm.monitors;

import static org.github.jimmyfm.utils.WebUtils.BASE_URL;
import static org.github.jimmyfm.utils.WebUtils.DELETE;
import static org.github.jimmyfm.utils.WebUtils.START_TORRENT;
import static org.github.jimmyfm.utils.WebUtils.getConnection;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.github.jimmyfm.utils.TorrentStatus;
import org.github.jimmyfm.utils.WebUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DownloadMonitor implements Runnable {

	private static final Logger LOG = Logger.getLogger(DownloadMonitor.class.getName());

	public void run() {
		LOG.info("Started");
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(10000);
				doWork();
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void doWork() throws IOException, InterruptedException {
		List<TorrentStatus> torrents = WebUtils.getTorrentStatus();

		TorrentStatus done = WebUtils.getDone(torrents);
		if (done != null) {
			String dlUrl = WebUtils.getDownloadURL(done);
			if (dlUrl != null) {
				List<String> dls = getFileList(dlUrl);
				LOG.info("Downloading");
				downloadItAll(done, dls);
				LOG.info("Download done");
				deleteTorrent(done.id);
			}
		}
		torrents.remove(done);

		done = WebUtils.getFirst(torrents);
		if (done != null) {
			checkAndStartATorrent(done);
		}
	}

	private static void checkAndStartATorrent(TorrentStatus torrent) throws IOException {
		HttpsURLConnection conn = getConnection(START_TORRENT + torrent.id.substring(1));
		Document doc = Jsoup.parse(conn.getInputStream(), "UTF-8", BASE_URL);
		// TODO: Check status
	}

	private static void deleteTorrent(String id) throws IOException {
		HttpsURLConnection conn = getConnection(DELETE + id.substring(1));
		Document doc = Jsoup.parse(conn.getInputStream(), "UTF-8", BASE_URL);
		// TODO: Check status
	}

	private static void downloadItAll(TorrentStatus done, List<String> urls) throws IOException, InterruptedException {
		String baseUrl = urls.remove(0);

		ThreadPoolExecutor tpe = new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(urls.size()));

		for (String url : urls) {
			tpe.execute(new DownladRunnable(done, baseUrl, url));
		}

		tpe.shutdown();
		while (!tpe.isTerminated()) {
			TimeUnit.SECONDS.sleep(10);
		}

		LOG.info("All DL Done");
	}

	public final static List<String> getFileList(String baseUrl) throws IOException {
		List<String> res = new ArrayList<>();
		res.add(baseUrl);

		URL dl = new URL(baseUrl);
		HttpsURLConnection conn = HttpsURLConnection.class.cast(dl.openConnection());
		Document doc = Jsoup.parse(conn.getInputStream(), "UTF-8", WebUtils.BASE_URL);
		for (Element linkEl : doc.select("A")) {
			String href = linkEl.attr("href");

			if (href == null || "../".equals(href)) {
				continue;
			}

			if (href.endsWith("/")) {
				List<String> subList = getFileList(baseUrl + href);
				subList.remove(0);
				res.addAll(subList);
				continue;
			}

			res.add(baseUrl + linkEl.attr("href"));
		}
		return res;
	}

}
