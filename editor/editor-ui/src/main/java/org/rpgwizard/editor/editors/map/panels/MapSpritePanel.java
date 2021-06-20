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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.rpgwizard.common.assets.map.EventType;
import org.rpgwizard.common.assets.Script;
import org.rpgwizard.common.assets.map.KeyType;
import org.rpgwizard.common.assets.map.MapLayer;
import org.rpgwizard.common.assets.map.MapSprite;
import org.rpgwizard.common.assets.map.SelectablePair;
import org.rpgwizard.common.assets.sprite.Sprite;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.map.MapLayerView;
import org.rpgwizard.editor.editors.map.generation.ScriptDialog;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public final class MapSpritePanel extends MapModelPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapSpritePanel.class);

    private final JComboBox fileComboBox;
    private final JTextField idField;
    private final JComboBox threadComboBox;
    private final JSpinner xSpinner;
    private final JSpinner ySpinner;
    private final JSpinner layerSpinner;
    private int lastSpinnerLayer; // Used to ensure that the selection is valid. // REFACTOR: FIX
    private final JComboBox eventComboBox;
    private static final String[] EVENT_TYPES = EventType.toStringArray();
    private final JButton configureEventButton;
    private static final String[] KEY_TYPES = KeyType.toStringArray();
    private final JComboBox<String> keyComboBox;

    public MapSpritePanel(SelectablePair<String, MapSprite> pair) {
        ///
        /// super
        ///
        super(pair);
        ///
        /// fileComboBox
        ///
        String[] exts = (String[]) ArrayUtils.addAll(EditorFileManager.getTypeExtensions(Sprite.class));
        fileComboBox = GuiHelper.getFileListJComboBox(new File[] { EditorFileManager.getFullPath(Sprite.class) }, exts,
                true);
        fileComboBox.setSelectedItem(getSprite().getAsset());
        fileComboBox.addActionListener((ActionEvent e) -> {
            String fileName = (String) fileComboBox.getSelectedItem();
            if (fileName == null) {
                return;
            }
            getSprite().setAsset((String) fileComboBox.getSelectedItem());
            getSprite().prepareSprite();
            updateCurrentMapEditor();
        });
        ///
        /// idField
        ///
        idField = getJTextField(getId());
        idField.setEditable(false);
        ///
        /// configureEventButton
        ///
        configureEventButton = new JButton("Configure");
        configureEventButton.addActionListener((ActionEvent e) -> {
            try {
                String script = getSprite().getEvent().getScript();
                ScriptDialog dialog = new ScriptDialog(MainWindow.getInstance(), script);
                dialog.display();

                String newProgram = dialog.getNewValue();
                if (newProgram != null) {
                    getSprite().setScript(newProgram);
                    MainWindow.getInstance().markWindowForSaving();
                }
            } catch (Exception ex) {
                LOGGER.error("Caught exception while setting new event!", ex);
            }
        });
        ///
        /// multiTaskingTextField
        ///
        threadComboBox = GuiHelper.getFileListJComboBox(new File[] { EditorFileManager.getFullPath(Script.class) },
                EditorFileManager.getTypeExtensions(Script.class), true);
        threadComboBox.setSelectedItem(getSprite().getThread());
        threadComboBox.addActionListener((ActionEvent e) -> {
            if (threadComboBox.getSelectedItem() != null) {
                getSprite().setThread((String) threadComboBox.getSelectedItem());
                MainWindow.getInstance().markWindowForSaving();
            }
        });
        ///
        /// xSpinner
        ///
        xSpinner = getJSpinner(getSprite().getStartLocation().getX());
        xSpinner.addChangeListener((ChangeEvent e) -> {
            MapSprite sprite = getSprite();
            if (sprite.getStartLocation().getX() != (int) xSpinner.getValue()) {
                sprite.getStartLocation().setX((int) xSpinner.getValue());
                updateCurrentMapEditor();
            }
        });
        ///
        /// ySpinner
        ///
        ySpinner = getJSpinner(getSprite().getStartLocation().getY());
        ySpinner.addChangeListener((ChangeEvent e) -> {
            MapSprite sprite = getSprite();
            if (sprite.getStartLocation().getY() != (int) ySpinner.getValue()) {
                sprite.getStartLocation().setY((int) ySpinner.getValue());
                updateCurrentMapEditor();
            }
        });
        ///
        /// layerSpinner
        ///
        // REFACTOR: FIX ME
        layerSpinner = getJSpinner(getLayer());
        layerSpinner.addChangeListener((ChangeEvent e) -> {
            if (getLayer() == (int) layerSpinner.getValue()) {
                return;
            }

            MapLayerView lastLayerView = getMapEditor().getMapView().getLayer(getLayer());
            MapLayerView newLayerView = getMapEditor().getMapView().getLayer((int) layerSpinner.getValue());
            if (lastLayerView != null && newLayerView != null) {
                lastLayerView.getLayer().getSprites().remove(getId());
                newLayerView.getLayer().getSprites().put(getId(), getSprite());
                updateCurrentMapEditor();
                lastSpinnerLayer = (int) layerSpinner.getValue();
            } else {
                layerSpinner.setValue(lastSpinnerLayer);
            }
        });
        ///
        /// keyComboBox
        ///
        keyComboBox = new JComboBox<>(KEY_TYPES);
        if (EventType.KEYPRESS.getValue().equals(getSprite().getEvent().getType())) {
            keyComboBox.setSelectedItem(getSprite().getEvent().getKey());
            keyComboBox.setEnabled(true);
        } else {
            keyComboBox.setEnabled(false);
        }
        keyComboBox.addActionListener((ActionEvent e) -> {
            getSprite().getEvent().setKey(keyComboBox.getSelectedItem().toString());
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// typeComboBox
        ///
        eventComboBox = new JComboBox(EVENT_TYPES);
        eventComboBox.setSelectedItem(getSprite().getEvent().getType());
        eventComboBox.addActionListener((ActionEvent e) -> {
            String selected = eventComboBox.getSelectedItem().toString();

            if (selected.equalsIgnoreCase(EventType.OVERLAP.toString())) {
                getSprite().getEvent().setType(EventType.OVERLAP.getValue());
                MainWindow.getInstance().markWindowForSaving();
                keyComboBox.setEnabled(false);
            } else if (selected.equalsIgnoreCase(EventType.KEYPRESS.toString())) {
                getSprite().getEvent().setType(EventType.KEYPRESS.getValue());
                MainWindow.getInstance().markWindowForSaving();
                keyComboBox.setEnabled(true);
            }
        });
        ///
        /// this
        ///
        insert(getJLabel("id"), idField);
        insert(getJLabel("asset"), fileComboBox);
        insert(getJLabel("x"), xSpinner);
        insert(getJLabel("y"), ySpinner);
        insert(getJLabel("layer"), layerSpinner);
        insert(getJLabel("event"), eventComboBox);
        insert(getJLabel("key"), keyComboBox);
        insert(getJLabel("script"), configureEventButton);
        insert(getJLabel("thread"), threadComboBox);
    }

    // REFACTOR: FIX ME
    // @Override
    // public void modelMoved(MapModelEvent e) {
    // if (e.getSource() == model) {
    // MapSprite sprite = (MapSprite) e.getSource();
    // xSpinner.setValue(sprite.getX());
    // ySpinner.setValue(sprite.getY());
    // MainWindow.getInstance().markWindowForSaving();
    // }
    // }

    private String getId() {
        SelectablePair<String, MapSprite> pair = (SelectablePair<String, MapSprite>) model;
        return pair.getLeft();
    }

    private MapSprite getSprite() {
        SelectablePair<String, MapSprite> pair = (SelectablePair<String, MapSprite>) model;
        return pair.getRight();
    }

    private int getLayer() {
        List<MapLayer> layers = getMapEditor().getMap().getLayers();
        for (int i = 0; i < layers.size(); i++) {
            MapLayer layer = layers.get(i);
            if (layer.getSprites().containsKey(getId())) {
                return i;
            }
        }

        return -1;
    }

}
