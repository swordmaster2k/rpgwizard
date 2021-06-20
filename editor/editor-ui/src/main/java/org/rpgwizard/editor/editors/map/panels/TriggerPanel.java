/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
// REFACTOR: FIX ME
package org.rpgwizard.editor.editors.map.panels;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import org.rpgwizard.common.assets.Event;
import org.rpgwizard.common.assets.Trigger;
import org.rpgwizard.common.assets.map.EventType;
import org.rpgwizard.common.assets.map.KeyType;
import org.rpgwizard.common.assets.map.MapLayer;
import org.rpgwizard.common.assets.map.SelectablePair;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.map.MapLayerView;
import org.rpgwizard.editor.editors.map.generation.ScriptDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public final class TriggerPanel extends MapModelPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TriggerPanel.class);

    private final JTextField idTextField;
    private final JSpinner layerSpinner;
    private static final String[] EVENT_TYPES = EventType.toStringArray();
    private final JButton configureEventButton;
    private final JComboBox<String> eventComboBox;
    private static final String[] KEY_TYPES = KeyType.toStringArray();
    private final JComboBox<String> keyComboBox;

    private int lastSpinnerLayer; // Used to ensure that the selection is valid.

    public TriggerPanel(SelectablePair<String, Trigger> pair) {
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
                lastLayerView.getLayer().getTriggers().remove(getId());
                newLayerView.getLayer().getTriggers().put(getId(), getTrigger());
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

        if (EventType.KEYPRESS.getValue().equals(getTrigger().getEvents().get(0).getType())) {
            Event event = getTrigger().getEvents().get(0);
            keyComboBox.setSelectedItem(event.getKey());
            keyComboBox.setEnabled(true);
        } else {
            keyComboBox.setEnabled(false);
        }
        keyComboBox.addActionListener((ActionEvent e) -> {
            Event event = getTrigger().getEvents().get(0);
            event.setKey(keyComboBox.getSelectedItem().toString());
            getTrigger().getEvents().set(0, event);
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// eventComboBox
        ///
        eventComboBox = new JComboBox<>(EVENT_TYPES);
        eventComboBox.setEnabled(true);
        eventComboBox.setSelectedItem(getTrigger().getEvents().get(0).getType());
        eventComboBox.addActionListener((ActionEvent e) -> {
            Event event = getTrigger().getEvents().get(0);
            String eventType = eventComboBox.getSelectedItem().toString();
            if (eventType.equals(EventType.OVERLAP.toString())) {
                event = new Event(EventType.OVERLAP.getValue(), event.getScript(), null);
                keyComboBox.setEnabled(false);
            } else if (eventType.equals(EventType.KEYPRESS.toString())) {
                String key = keyComboBox.getSelectedItem().toString();
                event = new Event(EventType.KEYPRESS.getValue(), event.getScript(), key);
                keyComboBox.setEnabled(true);
            }
            getTrigger().getEvents().set(0, event);
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// configureEventButton
        ///
        configureEventButton = new JButton("Configure");
        configureEventButton.addActionListener((ActionEvent e) -> {
            try {
                Event event = getTrigger().getEvents().get(0);
                String script = event.getScript();
                ScriptDialog dialog = new ScriptDialog(MainWindow.getInstance(), script);
                dialog.display();

                String newScript = dialog.getNewValue();
                if (newScript != null) {
                    event.setScript(newScript);
                    getTrigger().getEvents().set(0, event);
                    MainWindow.getInstance().markWindowForSaving();
                }
            } catch (Exception ex) {
                LOGGER.error("Caught exception while setting new event!", ex);
            }
        });
        ///
        /// this
        ///
        insert(getJLabel("id"), idTextField);
        insert(getJLabel("layer"), layerSpinner);
        insert(getJLabel("type"), eventComboBox);
        insert(getJLabel("key"), keyComboBox);
        insert(getJLabel("script"), configureEventButton);
    }

    private String getId() {
        SelectablePair<String, Trigger> pair = (SelectablePair<String, Trigger>) model;
        return pair.getLeft();
    }

    private Trigger getTrigger() {
        SelectablePair<String, Trigger> pair = (SelectablePair<String, Trigger>) model;
        return pair.getRight();
    }

    private int getLayer() {
        List<MapLayer> layers = getMapEditor().getMap().getLayers();
        for (int i = 0; i < layers.size(); i++) {
            MapLayer layer = layers.get(i);
            if (layer.getTriggers().containsKey(getId())) {
                return i;
            }
        }

        return -1;
    }

}
