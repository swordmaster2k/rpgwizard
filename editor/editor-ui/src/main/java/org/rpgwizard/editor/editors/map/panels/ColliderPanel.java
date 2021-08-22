/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.panels;

import java.util.List;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.common.assets.map.MapLayer;
import org.rpgwizard.common.assets.map.SelectablePair;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.map.MapLayerView;

/**
 * CLEANUP: Clean me up
 *
 * @author Joshua Michael Daly
 */
public final class ColliderPanel extends AbstractMapModelPanel {

    private final JSpinner layerSpinner;
    private final JTextField idField;

    private int lastSpinnerLayer; // Used to ensure that the selection is valid.

    public ColliderPanel(SelectablePair<String, Collider> pair) {
        ///
        /// super
        ///
        super(pair);
        ///
        /// idTextField
        ///
        idField = getJTextField(getId());
        idField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateId();
                MainWindow.getInstance().markWindowForSaving();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateId();
                MainWindow.getInstance().markWindowForSaving();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateId();
                MainWindow.getInstance().markWindowForSaving();
            }
        });
        ///
        /// layerSpinner
        ///
        layerSpinner = getJSpinner(getLayerIdx());
        lastSpinnerLayer = (int) layerSpinner.getValue();
        layerSpinner.addChangeListener((ChangeEvent e) -> {
            if (getLayerIdx() == (int) layerSpinner.getValue()) {
                return;
            }

            MapLayerView lastLayerView = getMapEditor().getMapView().getLayer(getLayerIdx());
            MapLayerView newLayerView = getMapEditor().getMapView().getLayer((int) layerSpinner.getValue());
            if (lastLayerView != null && newLayerView != null) {
                lastLayerView.getLayer().getColliders().remove(getId());
                newLayerView.getLayer().getColliders().put(getId(), getCollider());
                updateCurrentMapEditor();
                lastSpinnerLayer = (int) layerSpinner.getValue();
            } else {
                layerSpinner.setValue(lastSpinnerLayer);
            }
        });
        ///
        /// this
        ///
        insert(getJLabel("id"), idField);
        insert(getJLabel("layer"), layerSpinner);
    }

    private String getId() {
        SelectablePair<String, Collider> pair = (SelectablePair<String, Collider>) model;
        return pair.getLeft();
    }

    private Collider getCollider() {
        SelectablePair<String, Collider> pair = (SelectablePair<String, Collider>) model;
        return pair.getRight();
    }
    
    private void updateId() {
        String newId = idField.getText().trim();
        if (newId.isBlank()) {
            return;
        }

        Collider collider = getCollider();
        MapLayer layer = getLayer();
        layer.getColliders().remove(getId());
        layer.getColliders().put(newId, collider);
        model = new SelectablePair<>(newId, collider);
        MainWindow.getInstance().markWindowForSaving();
    }

    private MapLayer getLayer() {
        List<MapLayer> layers = getMapEditor().getMap().getLayers();
        for (int i = 0; i < layers.size(); i++) {
            MapLayer layer = layers.get(i);
            if (layer.getColliders().containsKey(getId())) {
                return layer;
            }
        }

        return null;
    }
    
    private int getLayerIdx() {
        List<MapLayer> layers = getMapEditor().getMap().getLayers();
        for (int i = 0; i < layers.size(); i++) {
            MapLayer layer = layers.get(i);
            if (layer.getColliders().containsKey(getId())) {
                return i;
            }
        }

        return -1;
    }

}
