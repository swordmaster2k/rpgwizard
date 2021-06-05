/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.panels;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.rpgwizard.common.assets.map.MapImage;
import org.rpgwizard.common.assets.map.MapLayer;
import org.rpgwizard.common.assets.map.SelectablePair;
import org.rpgwizard.editor.editors.map.MapLayerView;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 * @author Joshua Michael Daly
 */
public final class MapImagePanel extends MapModelPanel {

    private final JTextField idTextField;
    private final JComboBox fileComboBox;
    private final JSpinner xSpinner;
    private final JSpinner ySpinner;
    private final JSpinner layerSpinner;

    private int lastSpinnerLayer; // Used to ensure that the selection is valid.

    public MapImagePanel(SelectablePair<String, MapImage> pair) {
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
        /// fileComboBox
        ///
        String[] exts = (String[]) ArrayUtils.addAll(EditorFileManager.getImageExtensions());
        fileComboBox = GuiHelper.getFileListJComboBox(new File[] { new File(EditorFileManager.getGraphicsPath()) },
                exts, true);
        fileComboBox.setSelectedItem(getImage());
        fileComboBox.addActionListener((ActionEvent e) -> {
            String fileName = (String) fileComboBox.getSelectedItem();

            if (fileName == null) {
                return;
            }

            getImage().loadBufferedImage((String) fileComboBox.getSelectedItem());
            updateCurrentMapEditor();
        });
        ///
        /// xSpinner
        ///
        xSpinner = getJSpinner(getImage().getX());
        xSpinner.addChangeListener((ChangeEvent e) -> {
            MapImage image = getImage();

            if (image.getX() != (int) xSpinner.getValue()) {
                image.setX((int) xSpinner.getValue());
                updateCurrentMapEditor();
            }
        });
        ///
        /// ySpinner
        ///
        ySpinner = getJSpinner(getImage().getY());
        ySpinner.addChangeListener((ChangeEvent e) -> {
            MapImage image = getImage();

            if (image.getY() != (int) ySpinner.getValue()) {
                image.setY((int) ySpinner.getValue());
                updateCurrentMapEditor();
            }
        });
        ///
        /// layerSpinner
        ///
        layerSpinner = getJSpinner(getLayer());
        layerSpinner.addChangeListener((ChangeEvent e) -> {
            if (getLayer() == (int) layerSpinner.getValue()) {
                return;
            }

            MapLayerView lastLayerView = getMapEditor().getMapView().getLayer(getLayer());
            MapLayerView newLayerView = getMapEditor().getMapView().getLayer((int) layerSpinner.getValue());
            if (lastLayerView != null && newLayerView != null) {
                lastLayerView.getLayer().getImages().remove(getId());
                newLayerView.getLayer().getImages().put(getId(), getImage());
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
        insert(getJLabel("image"), fileComboBox);
        insert(getJLabel("x"), xSpinner);
        insert(getJLabel("y"), ySpinner);
        insert(getJLabel("layer"), layerSpinner);
    }

    // REFACTOR: FIX ME
    // @Override
    // public void modelMoved(MapModelEvent e) {
    // if (e.getSource() == model) {
    // MapImage sprite = (MapImage) e.getSource();
    // xSpinner.setValue(sprite.getX());
    // ySpinner.setValue(sprite.getY());
    // MainWindow.getInstance().markWindowForSaving();
    // }
    // }
    private String getId() {
        SelectablePair<String, MapImage> pair = (SelectablePair<String, MapImage>) model;
        return pair.getLeft();
    }

    private MapImage getImage() {
        SelectablePair<String, MapImage> pair = (SelectablePair<String, MapImage>) model;
        return pair.getRight();
    }

    private int getLayer() {
        List<MapLayer> layers = getMapEditor().getMap().getLayers();
        for (int i = 0; i < layers.size(); i++) {
            MapLayer layer = layers.get(i);
            if (layer.getImages().containsKey(getId())) {
                return i;
            }
        }

        return -1;
    }

}
