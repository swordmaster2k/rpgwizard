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
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.lang3.ArrayUtils;
import org.rpgwizard.common.assets.board.BoardLayerImage;
import org.rpgwizard.common.assets.board.model.BoardModelEvent;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.board.BoardLayerView;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 * @author Joshua Michael Daly
 */
public final class BoardLayerImagePanel extends BoardModelPanel {

    private final JComboBox fileComboBox;
    private final JTextField idField;
    private final JSpinner xSpinner;
    private final JSpinner ySpinner;
    private final JSpinner layerSpinner;

    private int lastSpinnerLayer; // Used to ensure that the selection is valid.

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
            updateCurrentBoardEditor();
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
                MainWindow.getInstance().markWindowForSaving();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateImageId();
                MainWindow.getInstance().markWindowForSaving();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateImageId();
                MainWindow.getInstance().markWindowForSaving();
            }

            private void updateImageId() {
                ((BoardLayerImage) model).setId(idField.getText());
                MainWindow.getInstance().markWindowForSaving();
            }
        });
        ///
        /// xSpinner
        ///
        xSpinner = getJSpinner(((BoardLayerImage) model).getX());
        xSpinner.addChangeListener((ChangeEvent e) -> {
            BoardLayerImage image = (BoardLayerImage) model;

            if (image.getX() != (int) xSpinner.getValue()) {
                image.setX((int) xSpinner.getValue());
                updateCurrentBoardEditor();
            }
        });
        ///
        /// ySpinner
        ///
        ySpinner = getJSpinner(((BoardLayerImage) model).getY());
        ySpinner.addChangeListener((ChangeEvent e) -> {
            BoardLayerImage image = (BoardLayerImage) model;

            if (image.getY() != (int) ySpinner.getValue()) {
                image.setY((int) ySpinner.getValue());
                updateCurrentBoardEditor();
            }
        });
        ///
        /// layerSpinner
        ///
        layerSpinner = getJSpinner(((BoardLayerImage) model).getLayer());
        layerSpinner.addChangeListener((ChangeEvent e) -> {
            BoardLayerImage image = (BoardLayerImage) model;

            BoardLayerView lastLayerView = getBoardEditor().getBoardView().getLayer((int) image.getLayer());

            BoardLayerView newLayerView = getBoardEditor().getBoardView().getLayer((int) layerSpinner.getValue());

            // Make sure this is a valid move.
            if (lastLayerView != null && newLayerView != null) {
                // Do the swap.
                image.setLayer((int) layerSpinner.getValue());
                newLayerView.getLayer().getImages().add(image);
                lastLayerView.getLayer().getImages().remove(image);
                updateCurrentBoardEditor();

                // Store new layer selection index.
                lastSpinnerLayer = (int) layerSpinner.getValue();
            } else {
                // Not a valid layer revert selection.
                layerSpinner.setValue(lastSpinnerLayer);
            }
        });
        ///
        /// this
        ///
        insert(getJLabel("Image"), fileComboBox);
        insert(getJLabel("ID"), idField);
        insert(getJLabel("X"), xSpinner);
        insert(getJLabel("Y"), ySpinner);
        insert(getJLabel("Layer"), layerSpinner);
    }

    @Override
    public void modelMoved(BoardModelEvent e) {
        if (e.getSource() == model) {
            BoardLayerImage sprite = (BoardLayerImage) e.getSource();
            xSpinner.setValue(sprite.getX());
            ySpinner.setValue(sprite.getY());
            MainWindow.getInstance().markWindowForSaving();
        }
    }
}
