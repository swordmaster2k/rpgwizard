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
import org.rpgwizard.common.assets.map.SelectablePair;
import org.rpgwizard.editor.editors.MapEditor;

/**
 * CLEANUP: Clean me up
 *
 * @author Joshua Michael Daly
 */
public class RemoveColliderAction extends AbstractAction {

    private final MapEditor mapEditor;
    private final int x;
    private final int y;
    private final boolean deleteKey;

    public RemoveColliderAction(MapEditor mapEditor, int x, int y, boolean deleteKey) {
        this.mapEditor = mapEditor;
        this.x = x;
        this.y = y;
        this.deleteKey = deleteKey;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SelectablePair removed;
        if (deleteKey && mapEditor.getSelectedObject() instanceof SelectablePair) {
            SelectablePair selected = (SelectablePair) mapEditor.getSelectedObject();
            removed = mapEditor.getMapView().getCurrentSelectedLayer().getLayer().removeCollider(selected.getLeft());
        } else {
            removed = mapEditor.getMapView().getCurrentSelectedLayer().getLayer().removeColliderAt(x, y);
        }

        if (removed != null && removed == mapEditor.getSelectedObject()) {
            mapEditor.getSelectedObject().setSelectedState(false);
            mapEditor.setSelectedObject(null);
        }
    }

}
