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
import org.codehaus.plexus.util.StringUtils;
import org.rpgwizard.editor.MainWindow;

/**
 *
 * @author Joshua Michael Daly
 */
public class NewFolderAction extends AbstractAction {

    private final File folder;

    public NewFolderAction(File folder) {
        this.folder = folder;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String input = (String) JOptionPane.showInputDialog(MainWindow.getInstance(), "Please provide a name:",
                "New Folder", JOptionPane.PLAIN_MESSAGE);
        if (StringUtils.isBlank(input)) {
            return;
        }

        File newFolder = new File(folder, input);
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
