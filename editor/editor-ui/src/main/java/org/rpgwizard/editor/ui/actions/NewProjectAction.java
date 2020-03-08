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
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.project.NewProjectDialog;
import org.rpgwizard.editor.utilities.FileTools;

/**
 *
 * @author Joshua Michael Daly
 */
public class NewProjectAction extends AbstractAction {

    public static final String BLANK_PROJECT = "Blank Project";

    @Override
    public void actionPerformed(ActionEvent e) {
        MainWindow mainWindow = MainWindow.getInstance();

        List<String> templates = findTemplateCandidates();
        templates.add(BLANK_PROJECT);

        NewProjectDialog dialog = new NewProjectDialog(mainWindow, templates.toArray(new String[templates.size()]));
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);

        if (dialog.getValue() != null) {
            String[] value = dialog.getValue();
            String projectName = value[0];
            String template = value[1];
            if (BLANK_PROJECT.equals(template)) {
                MainWindow.getInstance().createNewProject(projectName);
            } else {
                MainWindow.getInstance().createNewProject(projectName, template);
            }
        }
    }

    private List<String> findTemplateCandidates() {
        List<String> templates = new ArrayList<>();
        File mainFolder = new File(FileTools.getProjectsDirectory());
        File[] dirs = mainFolder.listFiles(File::isDirectory);
        for (File dir : dirs) {
            if (dir.listFiles((d, name) -> name.endsWith(".game")).length == 1) {
                templates.add(dir.getName());
            }
        }
        return templates;
    }

}
