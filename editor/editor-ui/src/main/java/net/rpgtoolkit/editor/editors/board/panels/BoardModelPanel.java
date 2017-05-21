/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.editors.board.panels;

import net.rpgtoolkit.editor.editors.BoardEditor;
import net.rpgtoolkit.editor.ui.AbstractModelPanel;
import net.rpgtoolkit.editor.MainWindow;

/**
 *
 * @author Joshua Michael Daly
 */
public class BoardModelPanel extends AbstractModelPanel {

	public BoardModelPanel(Object model) {
		super(model);
	}

	public BoardEditor getBoardEditor() {
		return MainWindow.getInstance().getCurrentBoardEditor();
	}

	public void updateCurrentBoardView() {
		BoardEditor editor = MainWindow.getInstance().getCurrentBoardEditor();

		if (editor != null) {
			editor.getBoardView().repaint();
		}
	}

}
