/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.ui.menu;

import net.rpgtoolkit.editor.ui.listeners.SnapToGridItemListener;
import net.rpgtoolkit.editor.ui.listeners.ShowVectorsItemListener;
import net.rpgtoolkit.editor.ui.listeners.ShowGridItemListener;
import net.rpgtoolkit.editor.ui.listeners.ShowCoordinatesItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import net.rpgtoolkit.editor.ui.actions.ZoomInAction;
import net.rpgtoolkit.editor.ui.actions.ZoomOutAction;
import net.rpgtoolkit.editor.ui.resources.Icons;

/**
 *
 * @author Joshua Michael Daly
 */
public final class ViewMenu extends JMenu {

	private JMenuItem zoomInMenuItem;
	private JMenuItem zoomOutMenuItem;
	private JCheckBoxMenuItem showGridMenuItem;
	private JCheckBoxMenuItem showCoordinatesMenuItem;
	private JCheckBoxMenuItem showVectorsMenuItem;
	private JCheckBoxMenuItem snapToGridMenuItem;

	/**
   *
   */
	public ViewMenu() {
		super("View");

		setMnemonic(KeyEvent.VK_V);

		configureZoomInMenuItem();
		configureZoomOutMenuItem();
		configureShowGridMenuItem();
		configureShowCoordinatesMenuItem();
		configureShowVectorsMenuItem();
		configureSnapToGridMenuItem();

		add(zoomInMenuItem);
		add(zoomOutMenuItem);
		add(new JSeparator());
		add(showGridMenuItem);
		add(showCoordinatesMenuItem);
		add(showVectorsMenuItem);
		add(new JSeparator());
		add(snapToGridMenuItem);
	}

	public JMenuItem getZoomInMenuItem() {
		return zoomInMenuItem;
	}

	public JMenuItem getZoomOutMenuItem() {
		return zoomOutMenuItem;
	}

	public JCheckBoxMenuItem getShowGridMenuItem() {
		return showGridMenuItem;
	}

	public JCheckBoxMenuItem getShowCoordinatesMenuItem() {
		return showCoordinatesMenuItem;
	}

	public JCheckBoxMenuItem getShowVectorsMenuItem() {
		return showVectorsMenuItem;
	}

	/**
   *
   */
	public void configureZoomInMenuItem() {
		zoomInMenuItem = new JMenuItem("Zoom In");
		zoomInMenuItem.setIcon(Icons.getSmallIcon("zoom-in"));
		zoomInMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ADD,
				ActionEvent.CTRL_MASK));
		zoomInMenuItem.setMnemonic(KeyEvent.VK_PLUS);
		zoomInMenuItem.addActionListener(new ZoomInAction());
	}

	/**
   *
   */
	public void configureZoomOutMenuItem() {
		zoomOutMenuItem = new JMenuItem("Zoom Out");
		zoomOutMenuItem.setIcon(Icons.getSmallIcon("zoom-out"));
		zoomOutMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_SUBTRACT, ActionEvent.CTRL_MASK));
		zoomOutMenuItem.setMnemonic(KeyEvent.VK_MINUS);
		zoomOutMenuItem.addActionListener(new ZoomOutAction());
	}

	/**
   *
   */
	public void configureShowGridMenuItem() {
		showGridMenuItem = new JCheckBoxMenuItem("Show Grid");
		showGridMenuItem.setIcon(Icons.getSmallIcon("grid"));
		showGridMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
				ActionEvent.CTRL_MASK));
		showGridMenuItem.setMnemonic(KeyEvent.VK_G);
		showGridMenuItem.addItemListener(new ShowGridItemListener());
	}

	/**
   *
   */
	public void configureShowCoordinatesMenuItem() {
		showCoordinatesMenuItem = new JCheckBoxMenuItem("Show Coordinates");
		// showGridMenuItem.setAccelerator(
		// KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		// showGridMenuItem.setMnemonic(KeyEvent.VK_G);
		showCoordinatesMenuItem
				.addItemListener(new ShowCoordinatesItemListener());
	}

	/**
   *
   */
	public void configureShowVectorsMenuItem() {
		showVectorsMenuItem = new JCheckBoxMenuItem("Show Vectors");
		// showGridMenuItem.setAccelerator(
		// KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		// showGridMenuItem.setMnemonic(KeyEvent.VK_G);
		showVectorsMenuItem.addItemListener(new ShowVectorsItemListener());
	}

	/**
   *
   */
	public void configureSnapToGridMenuItem() {
		snapToGridMenuItem = new JCheckBoxMenuItem("Snap to Grid");
		snapToGridMenuItem.addItemListener(new SnapToGridItemListener());
	}

}
