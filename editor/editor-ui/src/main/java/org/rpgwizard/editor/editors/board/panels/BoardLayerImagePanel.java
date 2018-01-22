/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board.panels;

import java.awt.event.ActionEvent;
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
import org.rpgwizard.common.assets.board.BoardLayerImage;
import org.rpgwizard.common.assets.board.model.BoardModelEvent;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 * @author Joshua Michael Daly
 */
public class BoardLayerImagePanel extends BoardModelPanel {

    private final JComboBox fileComboBox;
    private final JLabel fileLabel;

    private final JTextField idField;
    private final JLabel idLabel;

    private final JSpinner xSpinner;
    private final JLabel xLabel;

    private final JSpinner ySpinner;
    private final JLabel yLabel;

    public BoardLayerImagePanel(final BoardLayerImage boardLayerImage) {
        ///
        /// super
        ///
        super(boardLayerImage);
        ///
        /// fileComboBox
        ///
        String[] exts = (String[]) ArrayUtils.addAll(EditorFileManager.getImageExtensions());
        fileComboBox = GuiHelper.getFileListJComboBox(new File[] { new File(EditorFileManager.getGraphicsPath()) },
                exts, true);
        fileComboBox.setSelectedItem(boardLayerImage.getSrc());
        fileComboBox.addActionListener((ActionEvent e) -> {
            String fileName = (String) fileComboBox.getSelectedItem();

            if (fileName == null) {
                return;
            }

            boardLayerImage.loadImage((String) fileComboBox.getSelectedItem());
            updateCurrentBoardView();

            MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
        });
        ///
        /// idField
        ///
        idField = new JTextField();
        idField.setText(((BoardLayerImage) model).getId());
        idField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateImageId();
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateImageId();
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateImageId();
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }

            private void updateImageId() {
                ((BoardLayerImage) model).setId(idField.getText());
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }
        });
        ///
        /// xSpinner
        ///
        xSpinner = new JSpinner();
        xSpinner.setValue(((BoardLayerImage) model).getX());
        xSpinner.addChangeListener((ChangeEvent e) -> {
            BoardLayerImage image = (BoardLayerImage) model;

            if (image.getX() != (int) xSpinner.getValue()) {
                image.setX((int) xSpinner.getValue());
                updateCurrentBoardView();
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }
        });
        ///
        /// ySpinner
        ///
        ySpinner = new JSpinner();
        ySpinner.setValue(((BoardLayerImage) model).getY());
        ySpinner.addChangeListener((ChangeEvent e) -> {
            BoardLayerImage image = (BoardLayerImage) model;

            if (image.getY() != (int) ySpinner.getValue()) {
                image.setY((int) ySpinner.getValue());
                updateCurrentBoardView();
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }
        });
        ///
        /// this
        ///
        horizontalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(fileLabel = getJLabel("Image")).addComponent(idLabel = getJLabel("ID"))
                .addComponent(xLabel = getJLabel("X")).addComponent(yLabel = getJLabel("Y")));

        horizontalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(fileComboBox)
                .addComponent(idField).addComponent(xSpinner).addComponent(ySpinner));

        layout.setHorizontalGroup(horizontalGroup);

        verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(fileLabel)
                .addComponent(fileComboBox));

        verticalGroup.addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(idLabel).addComponent(idField));

        verticalGroup.addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(xLabel).addComponent(xSpinner));

        verticalGroup.addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(yLabel).addComponent(ySpinner));

        layout.setVerticalGroup(verticalGroup);
    }

    @Override
    public void modelMoved(BoardModelEvent e) {
        if (e.getSource() == model) {
            BoardLayerImage sprite = (BoardLayerImage) e.getSource();
            xSpinner.setValue(sprite.getX());
            ySpinner.setValue(sprite.getY());
            MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
        }
    }
}
