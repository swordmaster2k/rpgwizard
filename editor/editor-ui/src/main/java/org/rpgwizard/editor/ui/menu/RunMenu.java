/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.menu;

import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 *
 * @author Joshua Michael Daly
 */
public final class RunMenu extends JMenu {

	private JMenuItem debugProgramMenuItem;
	private JMenuItem runProjectMenuItem;

	public RunMenu() {
		super("Run");

		setMnemonic(KeyEvent.VK_R);

		configureDebugProgramMenuItem();
		configureRunProjectMenuItem();

		add(debugProgramMenuItem);
		add(runProjectMenuItem);
	}

	public void configureDebugProgramMenuItem() {
		debugProgramMenuItem = new JMenuItem("Debug Program");
		debugProgramMenuItem.setIcon(Icons.getSmallIcon("bug"));
		// debugProgramMenuItem.setAccelerator(
		// KeyStroke.getKeyStroke(KeyEvent.VK_F5,
		// ActionEvent.ACTION_PERFORMED));
		debugProgramMenuItem.setMnemonic(KeyEvent.VK_D);

		debugProgramMenuItem.setEnabled(false);
	}

	public void configureRunProjectMenuItem() {
		runProjectMenuItem = new JMenuItem("Run Project");
		runProjectMenuItem.setIcon(Icons.getSmallIcon("run"));
		// runProjectMenuItem.setAccelerator(
		// KeyStroke.getKeyStroke(KeyEvent.VK_F11, ActionEvent.ACTION_FIRST));
		runProjectMenuItem.setMnemonic(KeyEvent.VK_N);

		runProjectMenuItem.setEnabled(false);
	}

}
