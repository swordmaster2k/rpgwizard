/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.board;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Joshua Michael Daly
 */
public class AbstractBoardModel {

    protected LinkedList<BoardModelChangeListener> changeListeners = new LinkedList<>();

    /**
     * Add a new <code>BoardChangeListener</code> for this board.
     *
     * @param listener
     *            new change listener
     */
    public void addBoardChangeListener(BoardModelChangeListener listener) {
        changeListeners.add(listener);
    }

    /**
     * Remove an existing <code>BoardChangeListener</code> for this board.
     *
     * @param listener
     *            change listener
     */
    public void removeBoardChangeListener(BoardModelChangeListener listener) {
        changeListeners.remove(listener);
    }

    /**
     * Fires the <code>BoardModelEvent</code> informs all the listeners that this model has changed.
     */
    public void fireModelChanged() {
        BoardModelEvent event = null;
        Iterator iterator = changeListeners.iterator();
        while (iterator.hasNext()) {
            if (event == null) {
                event = new BoardModelEvent(this);
            }
            ((BoardModelChangeListener) iterator.next()).modelChanged(event);
        }
    }

    /**
     * Fires the <code>BoardModelEvent</code> informs all the listeners that this model has moved on the board.
     */
    public void fireModelMoved() {
        BoardModelEvent event = null;
        Iterator iterator = changeListeners.iterator();
        while (iterator.hasNext()) {
            if (event == null) {
                event = new BoardModelEvent(this);
            }
            ((BoardModelChangeListener) iterator.next()).modelMoved(event);
        }
    }

}
