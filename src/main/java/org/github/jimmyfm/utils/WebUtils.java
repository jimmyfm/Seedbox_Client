package org.github.jimmyfm.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebUtils {

	public static final String BASE_URL = "https://www.torrentcloud.eu/cpv2/";
	public static final String FILES = "?_LIST_FILES";
	public static final String TORR = "?_LIST_TORRENTS";
	public static final String DELETE = "?_LIST_TORRENTS&DELETE=yes&SEARCH=&TORRENT=";
	public static final String SINGLE_TORRENT = "?_LIST_FILES&TORRENT=";
	public static final String START_TORRENT = "?_LIST_TORRENTS&SLOTS=1&SEARCH=&TORRENT=";

	public static final List<TorrentStatus> getTorrentStatus() throws IOException {

		List<TorrentStatus> res = new ArrayList<>();

		HttpsURLConnection conn = getConnection(WebUtils.TORR);
		Document doc = Jsoup.parse(conn.getInputStream(), "UTF-8", WebUtils.BASE_URL);

		for (Element row : doc.select("tr.enl")) {
			TorrentStatus ts = new TorrentStatus();
			ts.id = row.select("a[id]").first().id();
			ts.title = row.select("b.titl").first().text();
			ts.isStarted = row.select("img[src*=stop]").size() > 0;
			ts.isStopped = row.select("img[src*=start]").size() > 0;
			ts.isDone = row.select("img[src*=files]").size() > 0;
			res.add(ts);
		}

		return res;
	}

	public static final TorrentStatus getFirst(List<TorrentStatus> torrents) {
		TorrentStatus res = torrents.get(0);

		for (TorrentStatus ts : torrents) {
			if (res.id.compareTo(ts.id) > 0) {
				res = ts;
			}
		}

		return res;
	}

	public static final TorrentStatus getDone(List<TorrentStatus> torrents) {
		for (TorrentStatus ts : torrents) {
			if (ts.isDone) {
				return ts;
			}
		}
		return null;
	}

	public static final HttpsURLConnection getConnection(String page) throws IOException {
		URL u = new URL(BASE_URL + page);
		URLConnection uc = u.openConnection();

		HttpsURLConnection conn = HttpsURLConnection.class.cast(uc);

		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		conn.setRequestProperty("Accept-Encoding", "deflate");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
		conn.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");

		String userPassword = Settings.getInstance().getUsername() + ":" + Settings.getInstance().getPassword();
		String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes());
		uc.setRequestProperty("Authorization", "Basic " + encoding);

		return conn;
	}

	public static final void addMagnetLink(String magnetLink, String name) throws IOException {
		HttpsURLConnection conn = getConnection(TORR);
		MultipartUtility multipartUtility = new MultipartUtility(conn);
		multipartUtility.addFormField("torrentmagnet", magnetLink);
		multipartUtility.addFormField("name", name);
		multipartUtility.addFormField("slots", "0");
		multipartUtility.addFormField("cmdSend", "Run");
		multipartUtility.termintateFormData();

		Document doc = Jsoup.parse(conn.getInputStream(), "UTF-8", BASE_URL);
		System.out.println(doc);
	}

	public static final void addTorrentFile(File torrent) throws IOException {
		HttpsURLConnection conn = getConnection(TORR);
		MultipartUtility multipartUtility = new MultipartUtility(conn);
		multipartUtility.addFormField("torrentmagnet", "");
		multipartUtility.addFormField("name", (new Date()).toString());
		multipartUtility.addFormField("slots", "1");
		multipartUtility.addFormField("cmdSend", "Run");
		multipartUtility.addFilePart("file", torrent);
		multipartUtility.termintateFormData();

		Document doc = Jsoup.parse(conn.getInputStream(), "UTF-8", BASE_URL);
		Elements error = doc.select("span.error");
		if (error.size() > 0) {
			System.out.println(error.get(0).data());
		}

		Elements info = doc.select("span.info");
		if (info.size() > 0) {
			System.out.println(info.get(0).data());
		}

	}

	public static final String getDownloadURL(TorrentStatus torrent) throws IOException {
		HttpsURLConnection conn = getConnection(WebUtils.SINGLE_TORRENT + torrent.id.substring(1));
		Document doc = Jsoup.parse(conn.getInputStream(), "UTF-8", WebUtils.BASE_URL);

		Element linkElement = doc.select("a:contains((HTTPS)").first();
		if (linkElement == null) {
			return null;
		}
		return linkElement.attr("href").concat("/");

		// XXX Torrentcloud.eu have problems listing contents of complex
		// downloads
		// String link = ;
		// for (Element el : doc.select("a[target=_blank]")) {
		// String href = el.attr("href");
		// if (href.startsWith(link)) {
		// res.add(href);
		// }
		// }
	}
}
