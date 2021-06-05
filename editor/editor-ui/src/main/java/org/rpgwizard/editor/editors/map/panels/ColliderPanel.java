/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
// REFACTOR: FIX ME
package org.rpgwizard.editor.editors.map.panels;

import java.util.List;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.common.assets.map.MapLayer;
import org.rpgwizard.common.assets.map.SelectablePair;
import org.rpgwizard.editor.editors.map.MapLayerView;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public final class ColliderPanel extends MapModelPanel {

    private final JSpinner layerSpinner;
    private final JTextField idTextField;

    private int lastSpinnerLayer; // Used to ensure that the selection is valid.

    public ColliderPanel(SelectablePair<String, Collider> pair) {
        ///
        /// super
        ///
        super(pair);
        ///
        /// idTextField
        ///
        idTextField = getJTextField(getId());
        idTextField.setEditable(false);
        ///
        /// layerSpinner
        ///
        layerSpinner = getJSpinner(getLayer());
        lastSpinnerLayer = (int) layerSpinner.getValue();
        layerSpinner.addChangeListener((ChangeEvent e) -> {
            if (getLayer() == (int) layerSpinner.getValue()) {
                return;
            }

            MapLayerView lastLayerView = getMapEditor().getMapView().getLayer(getLayer());
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
        insert(getJLabel("id"), idTextField);
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

    private int getLayer() {
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
