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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.openide.util.Utilities;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.utilities.FileTools;

/**
 *
 * @author Joshua Michael Daly
 */
@Slf4j
@AllArgsConstructor
public class RenameAction extends AbstractAction {

    private final File file;

    @Override
    public void actionPerformed(ActionEvent ae) {
        File newFile = FileTools.promptForFile("Rename File", file.getParentFile(),
                FilenameUtils.getExtension(file.getAbsolutePath()));
        if (newFile == null) {
            return; // User cancelled
        }

        try {
            if (file.isDirectory()) {
                FileUtils.moveDirectory(file, newFile);
            } else if (file.isFile()) {
                FileUtils.moveFile(file, newFile);
            }

            updateAssetEditors(newFile);
        } catch (IOException ex) {
            log.error("Failed to rename file=[{}], newFile=[{}]", file, newFile, ex);
            JOptionPane.showMessageDialog(MainWindow.getInstance(), "Failed to rename file " + file.getAbsolutePath(),
                    "Failed to Rename", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAssetEditors(File newFile) {
        for (AbstractAssetEditorWindow window : MainWindow.getInstance().getOpenEditors()) {
            if (window.getAsset() == null || window.getAsset().getDescriptor() == null) {
                continue;
            }

            File windowFile = window.getAsset().getFile();
            if (newFile.isDirectory()) {
                if (windowFile.getAbsolutePath().startsWith(file.getAbsolutePath())) {
                    String newPath = windowFile.getAbsolutePath().replace(file.getAbsolutePath(),
                            newFile.getAbsolutePath());
                    window.getAsset().getDescriptor().setUri(Utilities.toURI(new File(newPath)));
                }
            } else {
                if (file.getAbsolutePath().equals(windowFile.getAbsolutePath())) {
                    log.info("match file=[{}], windowFile=[{}]", file, windowFile);
                    window.getAsset().getDescriptor().setUri(Utilities.toURI(newFile));
                    window.setTitle(newFile.getName());
                }
            }
        }
    }

}
