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
import org.rpgwizard.common.assets.board.BoardVector;
import org.rpgwizard.editor.editors.BoardEditor;

/**
 *
 * @author Joshua Michael Daly
 */
public class RemoveVectorAction extends AbstractAction {

    private final BoardEditor boardEditor;
    private final int x;
    private final int y;
    private final boolean deleteKey;

    public RemoveVectorAction(BoardEditor boardEditor, int x, int y, boolean deleteKey) {
        this.boardEditor = boardEditor;
        this.x = x;
        this.y = y;
        this.deleteKey = deleteKey;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BoardVector removed;

        if (deleteKey && boardEditor.getSelectedObject() instanceof BoardVector) {
            BoardVector selected = (BoardVector) boardEditor.getSelectedObject();
            removed = boardEditor.getBoardView().getCurrentSelectedLayer().getLayer().removeVector(selected);
        } else {
            removed = boardEditor.getBoardView().getCurrentSelectedLayer().getLayer().removeVectorAt(x, y);
        }

        if (removed != null && removed == boardEditor.getSelectedObject()) {
            boardEditor.getSelectedObject().setSelectedState(false);
            boardEditor.setSelectedObject(null);
        }
    }

}
