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
import org.apache.commons.io.FileUtils;
import org.rpgwizard.editor.MainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
@AllArgsConstructor
public class DeleteFileAction extends AbstractAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteFileAction.class);

    private final File file;

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to delete, file=[{}]", file, ex);
            JOptionPane.showMessageDialog(MainWindow.getInstance(), "Failed to delete " + file.getAbsolutePath(),
                    "Failed to Delete", JOptionPane.ERROR_MESSAGE);
        }
    }

}
