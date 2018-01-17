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
import org.rpgwizard.editor.ui.actions.SidePanelAction;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 *
 * @author Joshua Michael Daly
 */
public final class WindowMenu extends JMenu {

	private JMenuItem sidePanel;

	public WindowMenu() {
		super("Window");

		setMnemonic(KeyEvent.VK_W);

		configureSidePanelMenuItem();

		add(sidePanel);
	}

	public JMenuItem getZoomInMenuItem() {
		return sidePanel;
	}

	public void configureSidePanelMenuItem() {
		sidePanel = new JMenuItem("Side Panel");
		sidePanel.setIcon(Icons.getSmallIcon("application-sidebar"));
		sidePanel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, ActionEvent.CTRL_MASK));
		// sidePanel.setMnemonic(KeyEvent.VK_PLUS);
		sidePanel.addActionListener(new SidePanelAction());
	}

}
