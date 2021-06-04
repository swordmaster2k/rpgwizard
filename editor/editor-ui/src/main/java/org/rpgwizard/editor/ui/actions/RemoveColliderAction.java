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
import org.apache.commons.lang3.tuple.Pair;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.editor.editors.MapEditor;

/**
 * REFACTOR: FIX ME
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
        Collider removed = null;

        if (deleteKey && mapEditor.getSelectedObject() instanceof Collider) {
            Collider selected = (Collider) mapEditor.getSelectedObject();
            Pair<String, Collider> pair = mapEditor.getMapView().getCurrentSelectedLayer().getLayer()
                    .removeCollider(selected);
            if (pair != null && pair.getValue() != null) {
                removed = pair.getValue();
            }
        } else {
            Pair<String, Collider> pair = mapEditor.getMapView().getCurrentSelectedLayer().getLayer()
                    .removeColliderAt(x, y);
            if (pair != null && pair.getValue() != null) {
                removed = pair.getValue();
            }
        }

        if (removed != null && removed == mapEditor.getSelectedObject()) {
            mapEditor.getSelectedObject().setSelectedState(false);
            mapEditor.setSelectedObject(null);
        }
    }

}
