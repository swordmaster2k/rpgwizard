/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import net.rpgtoolkit.editor.utilities.EditorFileManager;

/**
 *
 * @author Joshua Michael Daly
 */
public class OpenFileAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		EditorFileManager.openFile();
	}

}
