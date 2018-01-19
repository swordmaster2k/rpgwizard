/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import org.rpgwizard.editor.ui.actions.EastPanelAction;
import org.rpgwizard.editor.ui.actions.WestPanelAction;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 *
 * @author Joshua Michael Daly
 */
public final class WindowMenu extends JMenu {

	private JMenuItem westPanel;
        private JMenuItem eastPanel;

	public WindowMenu() {
		super("Window");

		setMnemonic(KeyEvent.VK_W);

		configureWestPanelMenuItem();
                configureEastPanelMenuItem();

		add(westPanel);
                add(eastPanel);
	}

	public JMenuItem getZoomInMenuItem() {
		return westPanel;
	}

	public void configureWestPanelMenuItem() {
		westPanel = new JMenuItem("West Panel");
		westPanel.setIcon(Icons.getSmallIcon("application-sidebar"));
		westPanel.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_OPEN_BRACKET, ActionEvent.CTRL_MASK));
		westPanel.addActionListener(new WestPanelAction());
	}
        
        public void configureEastPanelMenuItem() {
		eastPanel = new JMenuItem("East Panel");
		eastPanel.setIcon(Icons.getSmallIcon("application-sidebar-flipped"));
		eastPanel.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_CLOSE_BRACKET, ActionEvent.CTRL_MASK));
		eastPanel.addActionListener(new EastPanelAction());
	}

}
