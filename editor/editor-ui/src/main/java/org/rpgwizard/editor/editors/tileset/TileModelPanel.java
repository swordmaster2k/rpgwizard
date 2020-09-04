/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.tileset;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.rpgwizard.common.assets.Tile;
import org.rpgwizard.common.assets.TileSet;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.ui.AbstractModelPanel;
import org.rpgwizard.editor.utilities.FileTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public final class TileModelPanel extends AbstractModelPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TileModelPanel.class);

    private static final String TYPE_KEY = "type";
    private static final String DEFENCE_KEY = "defence";
    private static final String CUSTOM_KEY = "custom";

    private static final Map<String, Integer> TYPE_COVER_MAP = Map.ofEntries(
            new AbstractMap.SimpleEntry<String, Integer>("", 0),
            new AbstractMap.SimpleEntry<String, Integer>("airport", 3),
            new AbstractMap.SimpleEntry<String, Integer>("beach", -1),
            new AbstractMap.SimpleEntry<String, Integer>("bridge", 0),
            new AbstractMap.SimpleEntry<String, Integer>("capital", 4),
            new AbstractMap.SimpleEntry<String, Integer>("city", 2),
            new AbstractMap.SimpleEntry<String, Integer>("desert", 1),
            new AbstractMap.SimpleEntry<String, Integer>("factory", 3),
            new AbstractMap.SimpleEntry<String, Integer>("harbor", 3),
            new AbstractMap.SimpleEntry<String, Integer>("mountain", 4),
            new AbstractMap.SimpleEntry<String, Integer>("plain", 1),
            new AbstractMap.SimpleEntry<String, Integer>("reef", 2),
            new AbstractMap.SimpleEntry<String, Integer>("river", -2),
            new AbstractMap.SimpleEntry<String, Integer>("road", 0),
            new AbstractMap.SimpleEntry<String, Integer>("ruins", 1),
            new AbstractMap.SimpleEntry<String, Integer>("sea", 0),
            new AbstractMap.SimpleEntry<String, Integer>("sea deep", 1),
            new AbstractMap.SimpleEntry<String, Integer>("wall", 0),
            new AbstractMap.SimpleEntry<String, Integer>("wasteland", 2),
            new AbstractMap.SimpleEntry<String, Integer>("wood", 3));

    private static final int MIN_COVER = -4;
    private static final int MAX_COVER = 4;

    private final JComboBox typeComboBox;
    private final JSlider defenceSlider;
    private final JTextField customField;

    private final JButton updateButton;

    public TileModelPanel(Tile tile) {
        ///
        /// super
        ///
        super(tile);
        ///
        /// data
        ///
        Map<String, String> data = tile.getTileSet().readTileData(tile.getIndex());
        ///
        /// typeComboBox
        ///
        String[] keys = TYPE_COVER_MAP.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        typeComboBox = getJComboBox("", keys);
        if (data.containsKey(TYPE_KEY)) {
            typeComboBox.setSelectedItem(data.get(TYPE_KEY));
        }
        ///
        /// defenceSlider
        ///
        int defaultValue = TYPE_COVER_MAP.get(typeComboBox.getSelectedItem().toString());
        defenceSlider = getJSlider(MIN_COVER, MAX_COVER, defaultValue);
        if (data.containsKey(DEFENCE_KEY)) {
            defenceSlider.setValue(Integer.valueOf(data.get(DEFENCE_KEY)));
        }
        ///
        /// customField
        ///
        customField = getJTextField("");
        if (data.containsKey(CUSTOM_KEY)) {
            customField.setText(data.get(CUSTOM_KEY));
        }
        ///
        /// updateButton
        ///
        updateButton = getJButton("Update");
        updateButton.addActionListener(new UpdateActionListener());
        ///
        /// typeComboBox.addActionListener
        ///
        typeComboBox.addActionListener((e) -> {
            String type = typeComboBox.getSelectedItem().toString();
            defenceSlider.setValue(TYPE_COVER_MAP.get(type));
            updateButton.setEnabled(true);
        });
        ///
        /// defenceSlider.addChangeListener
        ///
        defenceSlider.addChangeListener((e) -> {
            updateButton.setEnabled(true);
        });
        ///
        /// customField
        ///
        customField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateButton.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateButton.setEnabled(true);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateButton.setEnabled(true);
            }
        });
        ///
        /// this
        ///
        insert(getJLabel("Type"), typeComboBox);
        insert(getJLabel("Defence"), defenceSlider);
        insert(getJLabel("Custom"), customField);
        insert(updateButton);
    }

    private class UpdateActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Tile tile = (Tile) model;

            Map<String, String> newData = new HashMap();
            newData.put(TYPE_KEY, typeComboBox.getSelectedItem().toString());
            newData.put(DEFENCE_KEY, String.valueOf(defenceSlider.getValue()));
            newData.put(CUSTOM_KEY, customField.getText());

            TileSet tileSet = tile.getTileSet();
            tileSet.addTileData(tile.getIndex(), newData);

            try {
                FileTools.saveAsset(tileSet);
                updateButton.setEnabled(false);
            } catch (Exception ex) {
                LOGGER.error("Failed to invoke update for asset=[{}]", tileSet, ex);
                JOptionPane.showMessageDialog(MainWindow.getInstance(), "Error updating TileSet!", "Error on Update",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

    }

}
