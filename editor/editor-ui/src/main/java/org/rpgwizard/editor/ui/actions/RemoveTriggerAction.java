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
import lombok.AllArgsConstructor;
import org.rpgwizard.common.assets.map.SelectablePair;
import org.rpgwizard.editor.editors.MapEditor;

/**
 * CLEANUP: Clean me up
 *
 * @author Joshua Michael Daly
 */
@AllArgsConstructor
public class RemoveTriggerAction extends AbstractAction {

    private final MapEditor mapEditor;
    private final int x;
    private final int y;
    private final boolean deleteKey;

    @Override
    public void actionPerformed(ActionEvent e) {
        SelectablePair removed;
        if (deleteKey && mapEditor.getSelectedObject() instanceof SelectablePair) {
            SelectablePair selected = (SelectablePair) mapEditor.getSelectedObject();
            removed = mapEditor.getMapView().getCurrentSelectedLayer().getLayer().removeTrigger(selected.getLeft());
        } else {
            removed = mapEditor.getMapView().getCurrentSelectedLayer().getLayer().removeTriggerAt(x, y);
        }

        if (removed != null && removed == mapEditor.getSelectedObject()) {
            mapEditor.getSelectedObject().setSelectedState(false);
            mapEditor.setSelectedObject(null);
        }
    }

}
