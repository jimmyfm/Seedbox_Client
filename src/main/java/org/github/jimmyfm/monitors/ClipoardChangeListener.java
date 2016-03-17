package org.github.jimmyfm.monitors;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ClipoardChangeListener implements FlavorListener {

	public ClipoardChangeListener() {
		Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(this);
	}

	@Override
	public void flavorsChanged(FlavorEvent e) {
		Clipboard c = Clipboard.class.cast(e.getSource());
		try {
			System.out.println("changed!!! " + c.getData(DataFlavor.stringFlavor) + " " + e.toString());
		} catch (UnsupportedFlavorException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
