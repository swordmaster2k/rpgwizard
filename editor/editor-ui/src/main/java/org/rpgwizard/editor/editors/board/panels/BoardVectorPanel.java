/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board.panels;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;

import org.rpgwizard.editor.editors.board.BoardLayerView;
import org.rpgwizard.common.assets.board.BoardVector;
import org.rpgwizard.common.assets.board.BoardVectorType;
import org.rpgwizard.common.assets.Event;
import org.rpgwizard.common.assets.EventType;
import org.rpgwizard.common.assets.KeyPressEvent;
import org.rpgwizard.common.assets.KeyType;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public final class BoardVectorPanel extends BoardModelPanel {

    private final JSpinner layerSpinner;
    private final JCheckBox isClosedCheckBox;
    private final JTextField idTextField;
    private final JComboBox<String> typeComboBox;
    private static final String[] VECTOR_TYPES = BoardVectorType.toStringArray();
    private static final String[] EVENT_TYPES = EventType.toStringArray();
    private final JComboBox<String> eventComboBox;
    private static final String[] KEY_TYPES = KeyType.toStringArray();
    private final JComboBox<String> keyComboBox;
    private final JComboBox eventProgramComboBox;

    private int lastSpinnerLayer; // Used to ensure that the selection is valid.

    public BoardVectorPanel(BoardVector boardVector) {
        ///
        /// super
        ///
        super(boardVector);
        ///
        /// layerSpinner
        ///
        layerSpinner = getJSpinner(((BoardVector) model).getLayer());
        layerSpinner.addChangeListener((ChangeEvent e) -> {
            BoardLayerView lastLayerView = getBoardEditor().getBoardView().getLayer(((BoardVector) model).getLayer());

            BoardLayerView newLayerView = getBoardEditor().getBoardView().getLayer((int) layerSpinner.getValue());

            // Make sure this is a valid move.
            if (lastLayerView != null && newLayerView != null) {
                // Do the swap.
                ((BoardVector) model).setLayer((int) layerSpinner.getValue());
                newLayerView.getLayer().getVectors().add((BoardVector) model);
                lastLayerView.getLayer().getVectors().remove((BoardVector) model);
                updateCurrentBoardEditor();

                // Store new layer selection index.
                lastSpinnerLayer = (int) layerSpinner.getValue();
            } else {
                // Not a valid layer revert selection.
                layerSpinner.setValue(lastSpinnerLayer);
            }
        });

        // Store currently selected layer.
        lastSpinnerLayer = (int) layerSpinner.getValue();
        ///
        /// isClosedCheckBox
        ///
        isClosedCheckBox = new JCheckBox();
        isClosedCheckBox.setSelected(((BoardVector) model).isClosed());
        isClosedCheckBox.addActionListener((ActionEvent e) -> {
            ((BoardVector) model).setClosed(isClosedCheckBox.isSelected());
            updateCurrentBoardEditor();
        });
        ///
        /// idTextField
        ///
        idTextField = getJTextField(((BoardVector) model).getId());
        idTextField.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!((BoardVector) model).getId().equals(idTextField.getText())) {
                    ((BoardVector) model).setId(idTextField.getText());
                    MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
                }
            }
        });
        ///
        /// typeComboBox
        ///
        typeComboBox = new JComboBox<>(VECTOR_TYPES);

        switch (((BoardVector) model).getType()) {
        case PASSABLE:
            typeComboBox.setSelectedIndex(0);
            break;
        case SOLID:
            typeComboBox.setSelectedIndex(1);
            break;
        }

        typeComboBox.addActionListener((ActionEvent e) -> {
            switch (typeComboBox.getSelectedIndex()) {
            case 0:
                ((BoardVector) model).setType(BoardVectorType.PASSABLE);
                break;
            case 1:
                ((BoardVector) model).setType(BoardVectorType.SOLID);
                break;
            }

            updateCurrentBoardEditor();
        });
        ///
        /// keyComboBox
        ///
        keyComboBox = new JComboBox<>(KEY_TYPES);

        if (((BoardVector) model).getEvents().get(0).getType() == EventType.KEYPRESS) {
            KeyPressEvent event = (KeyPressEvent) ((BoardVector) model).getEvents().get(0);
            keyComboBox.setSelectedItem(event.getKey());
            keyComboBox.setEnabled(true);
        } else {
            keyComboBox.setEnabled(false);
        }
        keyComboBox.addActionListener((ActionEvent e) -> {
            KeyPressEvent event = (KeyPressEvent) ((BoardVector) model).getEvents().get(0);
            event.setKey(keyComboBox.getSelectedItem().toString());
            ((BoardVector) model).getEvents().set(0, event);
            MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
        });
        ///
        /// eventComboBox
        ///
        eventComboBox = new JComboBox<>(EVENT_TYPES);
        eventComboBox.setEnabled(true);
        eventComboBox.setSelectedItem(((BoardVector) model).getEvents().get(0).getType().toString());
        eventComboBox.addActionListener((ActionEvent e) -> {
            Event event = ((BoardVector) model).getEvents().get(0);
            String selected = eventComboBox.getSelectedItem().toString();

            if (selected.equals(EventType.OVERLAP.toString())) {
                event = new Event(EventType.OVERLAP, event.getProgram());
                event.setType(EventType.OVERLAP);
                keyComboBox.setEnabled(false);
            } else if (selected.equals(EventType.KEYPRESS.toString())) {
                event = new KeyPressEvent(event.getProgram(), selected);
                keyComboBox.setEnabled(true);
            }
            ((BoardVector) model).getEvents().set(0, event);
            MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
        });
        ///
        /// eventProgramComboBox
        ///
        File directory = new File(System.getProperty("project.path") + File.separator
                + CoreProperties.getProperty("toolkit.directory.program") + File.separator);
        String[] exts = new String[] { "program", "js" };
        eventProgramComboBox = GuiHelper.getFileListJComboBox(new File[] { directory }, exts, true);
        eventProgramComboBox.setSelectedItem(((BoardVector) model).getEvents().get(0).getProgram());
        eventProgramComboBox.addActionListener((ActionEvent e) -> {
            Event event = ((BoardVector) model).getEvents().get(0);

            if (eventProgramComboBox.getSelectedItem() == null) {
                event.setProgram("");
            } else {
                event.setProgram((String) eventProgramComboBox.getSelectedItem());
            }
            ((BoardVector) model).getEvents().set(0, event);
            MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
        });
        ///
        /// this
        ///
        insert(getJLabel("ID"), idTextField);
        insert(getJLabel("Is Closed"), isClosedCheckBox);
        insert(getJLabel("Layer"), layerSpinner);
        insert(getJLabel("Type"), typeComboBox);
        insert(getJLabel("Event"), eventComboBox);
        insert(getJLabel("Key"), keyComboBox);
        insert(getJLabel("Event Program"), eventProgramComboBox);
    }
}
