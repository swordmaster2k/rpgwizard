/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.rpgwizard.editor.editors.BoardEditor;
import org.rpgwizard.editor.editors.board.BoardVectorBrush;
import org.rpgwizard.editor.MainWindow;

/**
 *
 * @author Joshua Michael Daly
 */
public class VectorAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		BoardEditor.toggleSelectedOnBoardEditor();

		BoardVectorBrush brush = new BoardVectorBrush();
		MainWindow.getInstance().setCurrentBrush(brush);

		if (MainWindow.getInstance().getMainMenuBar().getViewMenu()
				.getShowVectorsMenuItem().isSelected() == false) {
			MainWindow.getInstance().getMainMenuBar().getViewMenu()
					.getShowVectorsMenuItem().setSelected(true);
		}
	}

}
