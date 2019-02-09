/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board.state;

import org.rpgwizard.common.assets.Board;

/**
 *
 * @author Joshua Michael Daly
 */
public class UndoRedoState {

    private final Board board;
    private final UndoRedoType type;

    public UndoRedoState(Board board, UndoRedoType type) {
        this.board = board;
        this.type = type;
    }

    /**
     * Copy constructor.
     * 
     * @param state
     */
    public UndoRedoState(UndoRedoState state) {
        this.board = new Board(state.getBoard());
        this.type = state.type;
    }

    public Board getBoard() {
        return board;
    }

    public UndoRedoType getType() {
        return type;
    }

}
