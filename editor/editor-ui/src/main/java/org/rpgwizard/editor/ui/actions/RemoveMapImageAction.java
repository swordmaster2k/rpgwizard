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
import org.rpgwizard.common.assets.map.MapImage;
import org.rpgwizard.editor.editors.MapEditor;

/**
 *
 * @author Joshua Michael Daly
 */
public class RemoveMapImageAction extends AbstractAction {

    private final MapEditor mapEditor;
    private final int layerIndex;
    private final String imageId;

    public RemoveMapImageAction(MapEditor mapEditor, int layerIndex, String imageId) {
        this.mapEditor = mapEditor;
        this.layerIndex = layerIndex;
        this.imageId = imageId;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MapImage mapImage = mapEditor.getMap().removeLayerImage(layerIndex, imageId);
        if (mapImage == mapEditor.getSelectedObject()) {
            mapEditor.getSelectedObject().setSelectedState(false);
            mapEditor.setSelectedObject(null);
        }
    }

}
