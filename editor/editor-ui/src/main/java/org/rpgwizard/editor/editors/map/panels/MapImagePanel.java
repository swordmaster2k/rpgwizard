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
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.rpgwizard.common.assets.map.MapImage;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 * @author Joshua Michael Daly
 */
public final class MapImagePanel extends MapModelPanel {

    private final JComboBox fileComboBox;
    private final JSpinner xSpinner;
    private final JSpinner ySpinner;
    // private final JSpinner layerSpinner; // REFACTOR: FIX ME

    private int lastSpinnerLayer; // Used to ensure that the selection is valid.

    public MapImagePanel(final MapImage mapImage) {
        ///
        /// super
        ///
        super(mapImage);
        ///
        /// fileComboBox
        ///
        String[] exts = (String[]) ArrayUtils.addAll(EditorFileManager.getImageExtensions());
        fileComboBox = GuiHelper.getFileListJComboBox(new File[] { new File(EditorFileManager.getGraphicsPath()) },
                exts, true);
        fileComboBox.setSelectedItem(mapImage.getImage());
        fileComboBox.addActionListener((ActionEvent e) -> {
            String fileName = (String) fileComboBox.getSelectedItem();

            if (fileName == null) {
                return;
            }

            mapImage.loadBufferedImage((String) fileComboBox.getSelectedItem());
            updateCurrentMapEditor();
        });
        ///
        /// xSpinner
        ///
        xSpinner = getJSpinner(((MapImage) model).getX());
        xSpinner.addChangeListener((ChangeEvent e) -> {
            MapImage image = (MapImage) model;

            if (image.getX() != (int) xSpinner.getValue()) {
                image.setX((int) xSpinner.getValue());
                updateCurrentMapEditor();
            }
        });
        ///
        /// ySpinner
        ///
        ySpinner = getJSpinner(((MapImage) model).getY());
        ySpinner.addChangeListener((ChangeEvent e) -> {
            MapImage image = (MapImage) model;

            if (image.getY() != (int) ySpinner.getValue()) {
                image.setY((int) ySpinner.getValue());
                updateCurrentMapEditor();
            }
        });
        ///
        /// layerSpinner
        ///
        // REFACTOR: FIX ME
        // layerSpinner = getJSpinner(((MapImage) model).getLayer());
        // layerSpinner.addChangeListener((ChangeEvent e) -> {
        // MapImage image = (MapImage) model;
        //
        // MapLayerView lastLayerView = getMapEditor().getMapView().getLayer((int) image.getLayer());
        //
        // MapLayerView newLayerView = getMapEditor().getMapView().getLayer((int) layerSpinner.getValue());
        //
        // // Make sure this is a valid move.
        // if (lastLayerView != null && newLayerView != null) {
        // // Do the swap.
        // image.setLayer((int) layerSpinner.getValue());
        // newLayerView.getLayer().getImages().add(image);
        // lastLayerView.getLayer().getImages().remove(image);
        // updateCurrentMapEditor();
        //
        // // Store new layer selection index.
        // lastSpinnerLayer = (int) layerSpinner.getValue();
        // } else {
        // // Not a valid layer revert selection.
        // layerSpinner.setValue(lastSpinnerLayer);
        // }
        // });
        ///
        /// this
        ///
        insert(getJLabel("Image"), fileComboBox);
        insert(getJLabel("X"), xSpinner);
        insert(getJLabel("Y"), ySpinner);
        // insert(getJLabel("Layer"), layerSpinner); // REFACTOR: FIX ME
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

}
