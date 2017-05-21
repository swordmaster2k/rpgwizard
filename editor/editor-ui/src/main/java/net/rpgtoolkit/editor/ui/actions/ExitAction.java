/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import net.rpgtoolkit.editor.MainWindow;

/**
 * 
 * 
 * @author Joshua Michael Daly
 */
public class ExitAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		MainWindow.getInstance().dispatchEvent(
				new WindowEvent(MainWindow.getInstance(),
						WindowEvent.WINDOW_CLOSING));
	}

}
