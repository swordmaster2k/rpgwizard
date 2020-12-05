/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.panels;

import org.rpgwizard.common.assets.events.MapChangedEvent;
import org.rpgwizard.common.assets.listeners.MapChangeListener;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.ui.AbstractModelPanel;
import org.rpgwizard.editor.MainWindow;

/**
 *
 * @author Joshua Michael Daly
 */
public class MapModelPanel extends AbstractModelPanel implements MapChangeListener {

    public MapModelPanel(Object model) {
        super(model);
        if (model instanceof Map) {
            ((Map) model).addMapChangeListener(this);
        }
    }

    @Override
    public void tearDown() {
        if (model instanceof Map) {
            ((Map) model).removeMapChangeListener(this);
        }
    }

    public MapEditor getMapEditor() {
        return MainWindow.getInstance().getCurrentMapEditor();
    }

    public void updateCurrentMapEditor() {
        MapEditor editor = MainWindow.getInstance().getCurrentMapEditor();
        if (editor != null) {
            editor.getMap().fireMapChanged();
            editor.getMapView().repaint();
        }
    }

    @Override
    public void mapChanged(MapChangedEvent e) {

    }

    @Override
    public void mapLayerAdded(MapChangedEvent e) {

    }

    @Override
    public void mapLayerMovedUp(MapChangedEvent e) {

    }

    @Override
    public void mapLayerMovedDown(MapChangedEvent e) {

    }

    @Override
    public void mapLayerCloned(MapChangedEvent e) {

    }

    @Override
    public void mapLayerDeleted(MapChangedEvent e) {

    }

    @Override
    public void mapSpriteAdded(MapChangedEvent e) {

    }

    @Override
    public void mapSpriteRemoved(MapChangedEvent e) {

    }

    @Override
    public void mapImageAdded(MapChangedEvent e) {

    }

    @Override
    public void mapImageRemoved(MapChangedEvent e) {

    }

}
