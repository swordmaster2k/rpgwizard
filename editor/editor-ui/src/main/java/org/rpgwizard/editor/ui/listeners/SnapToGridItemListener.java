/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBoxMenuItem;
import org.rpgwizard.editor.MainWindow;

/**
 *
 * @author Joshua Michael Daly
 */
public class SnapToGridItemListener implements ItemListener {

	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBoxMenuItem snapToGridMenuItem = (JCheckBoxMenuItem) e.getItem();

		if (snapToGridMenuItem.getState()) {
			MainWindow.getInstance().setSnapToGrid(true);
		} else {
			MainWindow.getInstance().setSnapToGrid(false);
		}
	}

}
