package org.github.jimmyfm.monitors;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.logging.Logger;

import org.github.jimmyfm.utils.WebUtils;

public class ClipboardMonitor implements Runnable {

	private static final Logger LOG = Logger.getLogger(ClipboardMonitor.class.getName());
	private static final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();

	public void run() {
		LOG.info("Started");
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(10000);
				doWork();
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			} catch (UnsupportedFlavorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void doWork() throws UnsupportedFlavorException, IOException {
		Transferable t = CLIPBOARD.getContents(null);

		if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			String data = (String) t.getTransferData(DataFlavor.stringFlavor);
			try {
				URI magnetLink = new URI(data);
				if ("magnet".equals(magnetLink.getScheme())) {

					String name = data.substring(data.indexOf("dn=") + 3);
					name = name.substring(0, name.indexOf("&") - 1);

					WebUtils.addMagnetLink(data, URLDecoder.decode(name, "UTF-8"));
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

		}
	}

}
