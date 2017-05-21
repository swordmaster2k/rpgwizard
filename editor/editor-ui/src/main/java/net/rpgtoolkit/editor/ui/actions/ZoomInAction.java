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
import net.rpgtoolkit.editor.MainWindow;
import net.rpgtoolkit.editor.editors.BoardEditor;

/**
 *
 * @author Joshua Michael Daly
 */
public class ZoomInAction extends AbstractAction {

	/**
	 *
	 * @param e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		BoardEditor editor = MainWindow.getInstance().getCurrentBoardEditor();

		if (editor != null) {
			editor.zoomIn();
		}
	}

}
