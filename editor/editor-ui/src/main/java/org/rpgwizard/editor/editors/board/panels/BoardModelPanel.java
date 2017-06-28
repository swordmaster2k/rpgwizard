/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board.panels;

import org.rpgwizard.editor.editors.BoardEditor;
import org.rpgwizard.editor.ui.AbstractModelPanel;
import org.rpgwizard.editor.MainWindow;

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
