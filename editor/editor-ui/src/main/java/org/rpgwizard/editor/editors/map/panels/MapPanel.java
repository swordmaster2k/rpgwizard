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
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.ui.AbstractModelPanel;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 *
 * @author Joshua Micahel Daly
 */
public final class MapPanel extends AbstractModelPanel {

    private final JSpinner widthSpinner;
    private final JSpinner heightSpinner;
    private final JComboBox musicFileComboBox;
    private final JComboBox entryProgramComboBox;

    public MapPanel(final Map map) {
        ///
        /// super
        ///
        super(map);
        ///
        /// widthSpinner
        ///
        widthSpinner = getJSpinner(map.getWidth());
        widthSpinner.setEnabled(false);
        widthSpinner.addChangeListener((ChangeEvent e) -> {
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// heightSpinner
        ///
        heightSpinner = getJSpinner(map.getHeight());
        heightSpinner.setEnabled(false);
        heightSpinner.addChangeListener((ChangeEvent e) -> {
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// musicTextField
        ///
        File directory = new File(System.getProperty("project.path") + File.separator
                + CoreProperties.getProperty("toolkit.directory.sounds") + File.separator);
        String[] exts = new String[] { "wav", "mp3", "ogg" };
        musicFileComboBox = GuiHelper.getFileListJComboBox(new File[] { directory }, exts, true);
        musicFileComboBox.setSelectedItem(map.getMusic());
        musicFileComboBox.addActionListener((ActionEvent e) -> {
            map.setMusic((String) musicFileComboBox.getSelectedItem());
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// entryProgramComboBox
        ///
        directory = new File(System.getProperty("project.path") + File.separator
                + CoreProperties.getProperty("toolkit.directory.program") + File.separator);
        exts = new String[] { "program", "js" };
        entryProgramComboBox = GuiHelper.getFileListJComboBox(new File[] { directory }, exts, true);
        entryProgramComboBox.setSelectedItem(map.getEntryScript());
        entryProgramComboBox.addActionListener((ActionEvent e) -> {
            map.setEntryScript((String) entryProgramComboBox.getSelectedItem());
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// this
        ///
        insert(getJLabel("Width"), widthSpinner);
        insert(getJLabel("Height"), heightSpinner);
        insert(getJLabel("Music"), musicFileComboBox);
        insert(getJLabel("Script"), entryProgramComboBox);
    }

}
