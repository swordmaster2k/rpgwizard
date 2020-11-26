/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.rpgwizard.common.assets.tileset.Tile;
import org.rpgwizard.editor.editors.BoardEditor;
import org.rpgwizard.editor.editors.board.brush.SelectionBrush;
import org.rpgwizard.editor.MainWindow;

/**
 *
 * @author Joshua Michael Daly
 */
public class SelectionAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        BoardEditor.toggleSelectedOnBoardEditor();

        SelectionBrush brush = new SelectionBrush(new Tile[1][1]);
        MainWindow.getInstance().setCurrentBrush(brush);
    }

}
