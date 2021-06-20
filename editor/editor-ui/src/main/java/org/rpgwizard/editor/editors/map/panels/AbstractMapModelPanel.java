/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.panels;

import org.rpgwizard.common.Selectable;
import org.rpgwizard.common.assets.events.MapModelEvent;
import org.rpgwizard.common.assets.listeners.MapModelChangeListener;
import org.rpgwizard.common.assets.map.AbstractMapModel;
import org.rpgwizard.common.assets.map.SelectablePair;
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.ui.AbstractModelPanel;
import org.rpgwizard.editor.MainWindow;

/**
 *
 * @author Joshua Michael Daly
 */
public class AbstractMapModelPanel extends AbstractModelPanel implements MapModelChangeListener {

    public AbstractMapModelPanel(Object model) {
        super(model);
        if (model instanceof SelectablePair) {
            Selectable selectable = (Selectable) ((SelectablePair) model).getRight();
            if (selectable instanceof AbstractMapModel) {
                ((AbstractMapModel) selectable).addMapChangeListener(this);
            }
        }

    }

    @Override
    public void tearDown() {
        if (model instanceof SelectablePair) {
            Selectable selectable = (Selectable) ((SelectablePair) model).getRight();
            if (selectable instanceof AbstractMapModel) {
                ((AbstractMapModel) selectable).removeMapChangeListener(this);
            }
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
    public void modelChanged(MapModelEvent e) {

    }

    @Override
    public void modelMoved(MapModelEvent e) {

    }

}
