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
import javax.swing.AbstractAction;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.ui.GoToFileDialog;
import org.rpgwizard.editor.utilities.EditorFileManager;

/**
 *
 * @author Joshua Michael Daly
 */
public class GoToFileAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        MainWindow instance = MainWindow.getInstance();
        if (instance.getGoToFileDialog().isVisible()) {
            return;
        }
        File directory = EditorFileManager.getProjectPath();
        GoToFileDialog dialog = instance.getGoToFileDialog();
        dialog.setDirectory(directory);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

}
