/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board.panels;

import org.rpgwizard.common.assets.board.model.AbstractBoardModel;
import org.rpgwizard.common.assets.board.model.BoardModelChangeListener;
import org.rpgwizard.common.assets.board.model.BoardModelEvent;
import org.rpgwizard.editor.editors.BoardEditor;
import org.rpgwizard.editor.ui.AbstractModelPanel;
import org.rpgwizard.editor.MainWindow;

/**
 *
 * @author Joshua Michael Daly
 */
public class BoardModelPanel extends AbstractModelPanel implements BoardModelChangeListener {

    public BoardModelPanel(Object model) {
        super(model);
        if (model instanceof AbstractBoardModel) {
            ((AbstractBoardModel) model).addBoardChangeListener(this);
        }
    }

    @Override
    public void tearDown() {
        if (model instanceof AbstractBoardModel) {
            ((AbstractBoardModel) model).removeBoardChangeListener(this);
        }
    }

    public BoardEditor getBoardEditor() {
        return MainWindow.getInstance().getCurrentBoardEditor();
    }

    public void updateCurrentBoardEditor() {
        BoardEditor editor = MainWindow.getInstance().getCurrentBoardEditor();
        if (editor != null) {
            editor.getBoard().fireBoardChanged();
            editor.getBoardView().repaint();
        }
    }

    @Override
    public void modelChanged(BoardModelEvent e) {

    }

    @Override
    public void modelMoved(BoardModelEvent e) {

    }

}
