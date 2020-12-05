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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.rpgwizard.common.assets.map.EventType;
import org.rpgwizard.common.assets.Program;
import org.rpgwizard.common.assets.map.KeyType;
import org.rpgwizard.common.assets.map.MapSprite;
import org.rpgwizard.common.assets.sprite.Sprite;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.map.generation.ProgramDialog;
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
    // private final JTextField idField; // REFACTOR: FIX
    private final JComboBox threadComboBox;
    private final JSpinner xSpinner;
    private final JSpinner ySpinner;
    // private final JSpinner layerSpinner; // REFACTOR: FIX
    private int lastSpinnerLayer; // Used to ensure that the selection is valid. // REFACTOR: FIX
    private final JComboBox eventComboBox;
    private static final String[] EVENT_TYPES = EventType.toStringArray();
    private final JButton configureEventButton;
    private static final String[] KEY_TYPES = KeyType.toStringArray();
    private final JComboBox<String> keyComboBox;

    public MapSpritePanel(final MapSprite mapSprite) {
        ///
        /// super
        ///
        super(mapSprite);
        ///
        /// fileComboBox
        ///
        String[] exts = (String[]) ArrayUtils.addAll(EditorFileManager.getTypeExtensions(Sprite.class));
        fileComboBox = GuiHelper.getFileListJComboBox(new File[] { EditorFileManager.getFullPath(Sprite.class) }, exts,
                true);
        fileComboBox.setSelectedItem(mapSprite.getAsset());
        fileComboBox.addActionListener((ActionEvent e) -> {
            String fileName = (String) fileComboBox.getSelectedItem();
            if (fileName == null) {
                return;
            }
            mapSprite.setAsset((String) fileComboBox.getSelectedItem());
            mapSprite.prepareSprite();
            updateCurrentMapEditor();
        });
        ///
        /// idField
        ///
        // REFACTOR: FIX ME
        // idField = getJTextField(((MapSprite) model).getId());
        // idField.getDocument().addDocumentListener(new DocumentListener() {
        // @Override
        // public void changedUpdate(DocumentEvent e) {
        // updateSpriteId();
        // MainWindow.getInstance().markWindowForSaving();
        // }
        //
        // @Override
        // public void removeUpdate(DocumentEvent e) {
        // updateSpriteId();
        // MainWindow.getInstance().markWindowForSaving();
        // }
        //
        // @Override
        // public void insertUpdate(DocumentEvent e) {
        // updateSpriteId();
        // MainWindow.getInstance().markWindowForSaving();
        // }
        //
        // private void updateSpriteId() {
        // ((MapSprite) model).setId(idField.getText());
        // MainWindow.getInstance().markWindowForSaving();
        // }
        // });
        ///
        /// configureEventButton
        ///
        configureEventButton = new JButton("Configure");
        configureEventButton.addActionListener((ActionEvent e) -> {
            try {
                String program = mapSprite.getEvent().getScript();
                ProgramDialog dialog = new ProgramDialog(MainWindow.getInstance(), program);
                dialog.display();

                String newProgram = dialog.getNewValue();
                if (newProgram != null) {
                    mapSprite.setScript(newProgram);
                    MainWindow.getInstance().markWindowForSaving();
                }
            } catch (Exception ex) {
                LOGGER.error("Caught exception while setting new event!", ex);
            }
        });
        ///
        /// multiTaskingTextField
        ///
        threadComboBox = GuiHelper.getFileListJComboBox(new File[] { EditorFileManager.getFullPath(Program.class) },
                EditorFileManager.getTypeExtensions(Program.class), true);
        threadComboBox.setSelectedItem(mapSprite.getThread());
        threadComboBox.addActionListener((ActionEvent e) -> {
            if (threadComboBox.getSelectedItem() != null) {
                mapSprite.setThread((String) threadComboBox.getSelectedItem());
                MainWindow.getInstance().markWindowForSaving();
            }
        });
        ///
        /// xSpinner
        ///
        xSpinner = getJSpinner(((MapSprite) model).getStartLocation().getX());
        xSpinner.addChangeListener((ChangeEvent e) -> {
            MapSprite sprite = (MapSprite) model;
            if (sprite.getStartLocation().getX() != (int) xSpinner.getValue()) {
                sprite.getStartLocation().setX((int) xSpinner.getValue());
                updateCurrentMapEditor();
            }
        });
        ///
        /// ySpinner
        ///
        ySpinner = getJSpinner(((MapSprite) model).getStartLocation().getY());
        ySpinner.addChangeListener((ChangeEvent e) -> {
            MapSprite sprite = (MapSprite) model;
            if (sprite.getStartLocation().getY() != (int) ySpinner.getValue()) {
                sprite.getStartLocation().setY((int) ySpinner.getValue());
                updateCurrentMapEditor();
            }
        });
        ///
        /// layerSpinner
        ///
        // REFACTOR: FIX ME
        // layerSpinner = getJSpinner(((MapSprite) model).getLayer());
        // layerSpinner.addChangeListener((ChangeEvent e) -> {
        // MapSprite sprite = (MapSprite) model;
        // MapLayerView lastLayerView = getMapEditor().getMapView().getLayer((int) sprite.getLayer());
        // MapLayerView newLayerView = getMapEditor().getMapView().getLayer((int) layerSpinner.getValue());
        //
        // // Make sure this is a valid move.
        // if (lastLayerView != null && newLayerView != null) {
        // // Do the swap.
        // sprite.setLayer((int) layerSpinner.getValue());
        // newLayerView.getLayer().getSprites().add(sprite);
        // lastLayerView.getLayer().getSprites().remove(sprite);
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
        /// keyComboBox
        ///
        keyComboBox = new JComboBox<>(KEY_TYPES);
        if (((MapSprite) model).getEvent().getType().equals(EventType.KEYPRESS.getValue())) {
            keyComboBox.setSelectedItem(((MapSprite) model).getEvent().getKey());
            keyComboBox.setEnabled(true);
        } else {
            keyComboBox.setEnabled(false);
        }
        keyComboBox.addActionListener((ActionEvent e) -> {
            ((MapSprite) model).getEvent().setKey(keyComboBox.getSelectedItem().toString());
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// typeComboBox
        ///
        eventComboBox = new JComboBox(EVENT_TYPES);
        eventComboBox.setSelectedItem(mapSprite.getEvent().getType());
        eventComboBox.addActionListener((ActionEvent e) -> {
            String selected = eventComboBox.getSelectedItem().toString();

            if (selected.equalsIgnoreCase(EventType.OVERLAP.toString())) {
                mapSprite.getEvent().setType(EventType.OVERLAP.getValue());
                MainWindow.getInstance().markWindowForSaving();
                keyComboBox.setEnabled(false);
            } else if (selected.equalsIgnoreCase(EventType.KEYPRESS.toString())) {
                mapSprite.getEvent().setType(EventType.KEYPRESS.getValue());
                MainWindow.getInstance().markWindowForSaving();
                keyComboBox.setEnabled(true);
            }
        });
        ///
        /// this
        ///
        insert(getJLabel("Sprite File"), fileComboBox);
        // insert(getJLabel("ID"), idField); // REFACTOR: FIX
        insert(getJLabel("X"), xSpinner);
        insert(getJLabel("Y"), ySpinner);
        // insert(getJLabel("Layer"), layerSpinner); // REFACTOR: FIX
        insert(getJLabel("Event"), eventComboBox);
        insert(getJLabel("Key"), keyComboBox);
        insert(getJLabel("Event Program"), configureEventButton);
        insert(getJLabel("Thread"), threadComboBox);
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

}
