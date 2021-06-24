/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import lombok.AllArgsConstructor;
import org.codehaus.plexus.util.StringUtils;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.utilities.EditorFileManager;

/**
 *
 * @author Joshua Michael Daly
 */
@AllArgsConstructor
public class NewFolderAction extends AbstractAction {

    private final File folder;

    @Override
    public void actionPerformed(ActionEvent e) {
        String folderName = (String) JOptionPane.showInputDialog(MainWindow.getInstance(), "Please provide a name:",
                "New Folder", JOptionPane.PLAIN_MESSAGE);
        if (StringUtils.isBlank(folderName)) {
            return;
        }

        if (EditorFileManager.getTypeDirectories().contains(folderName.toLowerCase())) {
            JOptionPane.showMessageDialog(MainWindow.getInstance(), "That folder name is reserved!", "Reserved Name",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        File newFolder = new File(folder, folderName);
        if (newFolder.exists()) {
            JOptionPane.showMessageDialog(MainWindow.getInstance(), "The folder already exists!", "Already Exists",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            newFolder.getCanonicalPath();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(MainWindow.getInstance(), "Invalid folder name!", "Invalid Name",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!newFolder.mkdirs()) {
            JOptionPane.showMessageDialog(MainWindow.getInstance(),
                    "Failed to create folder " + newFolder.getAbsolutePath(), "Failed to Create",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
