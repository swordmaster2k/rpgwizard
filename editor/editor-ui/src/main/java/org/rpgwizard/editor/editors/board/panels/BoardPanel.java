/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net
 * <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board.panels;

import org.rpgwizard.editor.ui.AbstractModelPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 *
 * @author Joshua Micahel Daly
 */
public class BoardPanel extends AbstractModelPanel {

    private final JTextField descriptionField;
    private final JLabel descriptionLabel;

    private final JSpinner widthSpinner;
    private final JLabel widthLabel;

    private final JSpinner heightSpinner;
    private final JLabel heightLabel;

    private final JComboBox musicFileComboBox;
    private final JLabel musicLabel;

    private final JComboBox entryProgramComboBox;
    private final JLabel entryProgramLabel;

    public BoardPanel(final Board board) {
        // /
        // / super
        // /
        super(board);
        // /
        // / descriptionField
        // /
        descriptionField = new JTextField();
        descriptionField.setText(board.getDescription());
        descriptionField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                board.setDescription(descriptionField.getText());
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                board.setDescription(descriptionField.getText());
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                board.setDescription(descriptionField.getText());
                MainWindow.getInstance().getCurrentBoardEditor().setNeedSave(true);
            }
        });
        // /
        // / widthSpinner
        // /
        widthSpinner = getJSpinner(board.getWidth());
        widthSpinner.setEnabled(false);
        widthSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                MainWindow.getInstance().getCurrentBoardEditor()
                        .setNeedSave(true);
            }

        });
        // /
        // / heightSpinner
        // /
        heightSpinner = getJSpinner(board.getHeight());
        heightSpinner.setEnabled(false);
        heightSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                MainWindow.getInstance().getCurrentBoardEditor()
                        .setNeedSave(true);
            }

        });
        // /
        // / musicTextField
        // /
        File directory = new File(System.getProperty("project.path")
                + File.separator
                + CoreProperties.getProperty("toolkit.directory.sounds")
                + File.separator);
        String[] exts = new String[]{"wav", "mp3", "ogg"};
        musicFileComboBox = GuiHelper.getFileListJComboBox(
                new File[]{directory}, exts, true);
        musicFileComboBox.setSelectedItem(board.getBackgroundMusic());
        musicFileComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                board.setBackgroundMusic((String) musicFileComboBox
                        .getSelectedItem());
                MainWindow.getInstance().getCurrentBoardEditor()
                        .setNeedSave(true);
            }

        });
        // /
        // / entryProgramComboBox
        // /
        directory = new File(System.getProperty("project.path")
                + File.separator
                + CoreProperties.getProperty("toolkit.directory.program")
                + File.separator);
        exts = new String[]{"program", "js"};
        entryProgramComboBox = GuiHelper.getFileListJComboBox(
                new File[]{directory}, exts, true);
        entryProgramComboBox.setSelectedItem(board.getFirstRunProgram());
        entryProgramComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                board.setFirstRunProgram((String) entryProgramComboBox
                        .getSelectedItem());
                MainWindow.getInstance().getCurrentBoardEditor()
                        .setNeedSave(true);
            }

        });
        // /
        // / this
        // /
        horizontalGroup.addGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(descriptionLabel = getJLabel("Description"))
                .addComponent(widthLabel = getJLabel("Width"))
                .addComponent(heightLabel = getJLabel("Height"))
                .addComponent(musicLabel = getJLabel("Music"))
                .addComponent(entryProgramLabel = getJLabel("Entry Program")));

        horizontalGroup.addGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(descriptionField)
                .addComponent(widthSpinner)
                .addComponent(heightSpinner)
                .addComponent(musicFileComboBox)
                .addComponent(entryProgramComboBox));

        layout.setHorizontalGroup(horizontalGroup);

        verticalGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
                .addComponent(descriptionLabel)
                .addComponent(descriptionField));

        verticalGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
                .addComponent(widthLabel)
                .addComponent(widthSpinner));

        verticalGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
                .addComponent(heightLabel)
                .addComponent(heightSpinner));

        verticalGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
                .addComponent(musicLabel)
                .addComponent(musicFileComboBox));

        verticalGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
                .addComponent(entryProgramLabel)
                .addComponent(entryProgramComboBox));

        layout.setVerticalGroup(verticalGroup);
    }

}
