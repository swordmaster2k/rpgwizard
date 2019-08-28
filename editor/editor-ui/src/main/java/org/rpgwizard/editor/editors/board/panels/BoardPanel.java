/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board.panels;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.ui.AbstractModelPanel;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 *
 * @author Joshua Micahel Daly
 */
public final class BoardPanel extends AbstractModelPanel {

    private final JTextField descriptionField;
    private final JSpinner widthSpinner;
    private final JSpinner heightSpinner;
    private final JComboBox musicFileComboBox;
    private final JComboBox entryProgramComboBox;

    public BoardPanel(final Board board) {
        // /
        // / super
        // /
        super(board);
        // /
        // / descriptionField
        // /
        descriptionField = getJTextField(board.getDescription());
        descriptionField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                board.setDescription(descriptionField.getText());
                MainWindow.getInstance().markWindowForSaving();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                board.setDescription(descriptionField.getText());
                MainWindow.getInstance().markWindowForSaving();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                board.setDescription(descriptionField.getText());
                MainWindow.getInstance().markWindowForSaving();
            }
        });
        // /
        // / widthSpinner
        // /
        widthSpinner = getJSpinner(board.getWidth());
        widthSpinner.setEnabled(false);
        widthSpinner.addChangeListener((ChangeEvent e) -> {
            MainWindow.getInstance().markWindowForSaving();
        });
        // /
        // / heightSpinner
        // /
        heightSpinner = getJSpinner(board.getHeight());
        heightSpinner.setEnabled(false);
        heightSpinner.addChangeListener((ChangeEvent e) -> {
            MainWindow.getInstance().markWindowForSaving();
        });
        // /
        // / musicTextField
        // /
        File directory = new File(System.getProperty("project.path") + File.separator
                + CoreProperties.getProperty("toolkit.directory.sounds") + File.separator);
        String[] exts = new String[] { "wav", "mp3", "ogg" };
        musicFileComboBox = GuiHelper.getFileListJComboBox(new File[] { directory }, exts, true);
        musicFileComboBox.setSelectedItem(board.getBackgroundMusic());
        musicFileComboBox.addActionListener((ActionEvent e) -> {
            board.setBackgroundMusic((String) musicFileComboBox.getSelectedItem());
            MainWindow.getInstance().markWindowForSaving();
        });
        // /
        // / entryProgramComboBox
        // /
        directory = new File(System.getProperty("project.path") + File.separator
                + CoreProperties.getProperty("toolkit.directory.program") + File.separator);
        exts = new String[] { "program", "js" };
        entryProgramComboBox = GuiHelper.getFileListJComboBox(new File[] { directory }, exts, true);
        entryProgramComboBox.setSelectedItem(board.getFirstRunProgram());
        entryProgramComboBox.addActionListener((ActionEvent e) -> {
            board.setFirstRunProgram((String) entryProgramComboBox.getSelectedItem());
            MainWindow.getInstance().markWindowForSaving();
        });
        // /
        // / this
        // /
        setLayout(new GridBagLayout());

        insert(getJLabel("Description"), descriptionField);
        insert(getJLabel("Width"), widthSpinner);
        insert(getJLabel("Height"), heightSpinner);
        insert(getJLabel("Music"), musicFileComboBox);
        insert(getJLabel("Entry Program"), entryProgramComboBox);
    }

}
