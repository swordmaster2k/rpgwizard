/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.lang3.ArrayUtils;
import org.rpgwizard.common.assets.board.BoardSprite;
import org.rpgwizard.common.assets.Enemy;
import org.rpgwizard.common.assets.EventType;
import org.rpgwizard.common.assets.KeyType;
import org.rpgwizard.common.assets.NPC;
import org.rpgwizard.common.assets.Program;
import org.rpgwizard.common.assets.board.model.BoardModelEvent;
import org.rpgwizard.editor.editors.board.BoardLayerView;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class BoardSpritePanel extends BoardModelPanel {

	private final JComboBox fileComboBox;
	private final JLabel fileLabel;

	private final JTextField idField;
	private final JLabel idLabel;

	private final JComboBox eventProgramComboBox;
	private final JLabel eventProgramLabel;

	private final JComboBox threadComboBox;
	private final JLabel threadLabel;

	private final JSpinner xSpinner;
	private final JLabel xLabel;

	private final JSpinner ySpinner;
	private final JLabel yLabel;

	private final JSpinner layerSpinner;
	private final JLabel layerLabel;

	private int lastSpinnerLayer; // Used to ensure that the selection is valid.

	private final JComboBox eventComboBox;
	private final JLabel eventLabel;

	private static final String[] EVENT_TYPES = EventType.toStringArray();

	private static final String[] KEY_TYPES = KeyType.toStringArray();
	private final JComboBox<String> keyComboBox;
	private final JLabel keyLabel;

	public BoardSpritePanel(final BoardSprite boardSprite) {
        ///
        /// super
        ///
        super(boardSprite);
        ///
        /// fileComboBox
        ///
        String[] exts = (String[]) ArrayUtils.addAll(
                EditorFileManager.getTypeExtensions(NPC.class),
                EditorFileManager.getTypeExtensions(Enemy.class)
        );
        fileComboBox = GuiHelper.getFileListJComboBox(
                new File[]{
                    EditorFileManager.getFullPath(Enemy.class),
                    EditorFileManager.getFullPath(NPC.class)
                }, 
                exts, true
        );
        fileComboBox.setSelectedItem(boardSprite.getFileName());
        fileComboBox.addActionListener((ActionEvent e) -> {
            String fileName = (String) fileComboBox.getSelectedItem();

            if (fileName == null) {
                return;
            }

            boardSprite.setFileName((String) fileComboBox.getSelectedItem());
            updateCurrentBoardView();

            MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
        });
        ///
        /// idField
        ///
        idField = new JTextField();
        idField.setText(((BoardSprite) model).getId());
        idField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSpriteId();
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSpriteId();
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSpriteId();
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }

            private void updateSpriteId() {
                ((BoardSprite) model).setId(idField.getText());
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }
        });
        ///
        /// activationComboBox
        ///
        exts = EditorFileManager.getTypeExtensions(Program.class);
        eventProgramComboBox = GuiHelper.getFileListJComboBox(new File[]{EditorFileManager.getFullPath(Program.class)}, exts, true);
        eventProgramComboBox.setSelectedItem(boardSprite.getEventProgram());
        eventProgramComboBox.addActionListener((ActionEvent e) -> {
            if (eventProgramComboBox.getSelectedItem() != null) {
                boardSprite.setEventProgram((String) eventProgramComboBox.getSelectedItem());
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }
        });
        ///
        /// multiTaskingTextField
        ///
        threadComboBox = GuiHelper.getFileListJComboBox(new File[]{EditorFileManager.getFullPath(Program.class)}, exts, true);
        threadComboBox.setSelectedItem(boardSprite.getThread());
        threadComboBox.addActionListener((ActionEvent e) -> {
            if (threadComboBox.getSelectedItem() != null) {
                boardSprite.setThread((String) threadComboBox.getSelectedItem());
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }
        });
        ///
        /// xSpinner
        ///
        xSpinner = new JSpinner();
        xSpinner.setValue(((BoardSprite) model).getX());
        xSpinner.addChangeListener((ChangeEvent e) -> {
            BoardSprite sprite = (BoardSprite) model;

            if (sprite.getX() != (int) xSpinner.getValue()) {
                sprite.setX((int) xSpinner.getValue());
                updateCurrentBoardView();
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }
        });
        ///
        /// ySpinner
        ///
        ySpinner = new JSpinner();
        ySpinner.setValue(((BoardSprite) model).getY());
        ySpinner.addChangeListener((ChangeEvent e) -> {
            BoardSprite sprite = (BoardSprite) model;

            if (sprite.getY() != (int) ySpinner.getValue()) {
                sprite.setY((int) ySpinner.getValue());
                updateCurrentBoardView();
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }
        });
        ///
        /// layerSpinner
        ///
        layerSpinner = getJSpinner(((BoardSprite) model).getLayer());
        layerSpinner.addChangeListener((ChangeEvent e) -> {
            BoardSprite sprite = (BoardSprite) model;

            BoardLayerView lastLayerView = getBoardEditor().getBoardView().
                    getLayer((int) sprite.getLayer());

            BoardLayerView newLayerView = getBoardEditor().getBoardView().
                    getLayer((int) layerSpinner.getValue());

            // Make sure this is a valid move.
            if (lastLayerView != null && newLayerView != null) {
                // Do the swap.
                sprite.setLayer((int) layerSpinner.getValue());
                newLayerView.getLayer().getSprites().add(sprite);
                lastLayerView.getLayer().getSprites().remove(sprite);
                updateCurrentBoardView();

                // Store new layer selection index.
                lastSpinnerLayer = (int) layerSpinner.getValue();

                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            } else {
                // Not a valid layer revert selection.
                layerSpinner.setValue(lastSpinnerLayer);
            }
        });
        ///
        /// typeComboBox
        ///
        eventComboBox = new JComboBox(EVENT_TYPES);
        eventComboBox.setSelectedItem(boardSprite.getEventType().toString());
        eventComboBox.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = eventComboBox.getSelectedItem().toString();
                
                if (selected.equalsIgnoreCase(EventType.OVERLAP.toString())) {
                    boardSprite.setEventType(EventType.OVERLAP);
                    MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
                    keyComboBox.setEnabled(false);
                } else if (selected.equalsIgnoreCase(EventType.KEYPRESS.toString())) {
                    boardSprite.setEventType(EventType.KEYPRESS);
                    MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
                    keyComboBox.setEnabled(true);
                }
            }
            
        });
                ///
        /// keyComboBox
        ///
        keyComboBox = new JComboBox<>(KEY_TYPES);
        if (((BoardSprite) model).getEventType() == EventType.KEYPRESS) {
            keyComboBox.setSelectedItem(((BoardSprite) model).getActivationKey());
            keyComboBox.setEnabled(true);
        } else {
            keyComboBox.setEnabled(false);
        }
        keyComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ((BoardSprite) model).setActivationKey(keyComboBox.getSelectedItem().toString());
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }

        });
        ///
        /// this
        ///
        horizontalGroup.addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(fileLabel = getJLabel("Sprite File"))
                        .addComponent(idLabel = getJLabel("ID"))
                        .addComponent(xLabel = getJLabel("X"))
                        .addComponent(yLabel = getJLabel("Y"))
                        .addComponent(layerLabel = getJLabel("Layer"))
                        .addComponent(eventLabel = getJLabel("Event"))
                        .addComponent(keyLabel = getJLabel("Key"))
                        .addComponent(eventProgramLabel = getJLabel("Event Program"))
                        .addComponent(threadLabel = getJLabel("Thread")));

        horizontalGroup.addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(fileComboBox)
                        .addComponent(idField)
                        .addComponent(xSpinner)
                        .addComponent(ySpinner)
                        .addComponent(layerSpinner)
                        .addComponent(eventComboBox)
                        .addComponent(keyComboBox)
                        .addComponent(eventProgramComboBox)
                        .addComponent(threadComboBox));

        layout.setHorizontalGroup(horizontalGroup);

        verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(fileLabel).addComponent(fileComboBox));

        verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(idLabel).addComponent(idField));

        verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(xLabel).addComponent(xSpinner));

        verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(yLabel).addComponent(ySpinner));

        verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(layerLabel).addComponent(layerSpinner));

        verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(eventLabel).addComponent(eventComboBox));
        
        verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(keyLabel).addComponent(keyComboBox));

        verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(eventProgramLabel).addComponent(eventProgramComboBox));

        verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(threadLabel).addComponent(threadComboBox));

        layout.setVerticalGroup(verticalGroup);
    }
	@Override
	public void modelMoved(BoardModelEvent e) {
		if (e.getSource() == model) {
			BoardSprite sprite = (BoardSprite) e.getSource();
			xSpinner.setValue(sprite.getX());
			ySpinner.setValue(sprite.getY());
			MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
		}
	}

}
