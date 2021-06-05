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
import java.util.Collection;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.Script;
import org.rpgwizard.common.assets.game.Game;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.ScriptEditor;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public class DebugProgramAction extends AbstractRunAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugProgramAction.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (!(MainWindow.getInstance().getCurrentFrame() instanceof ScriptEditor)) {
                return;
            }

            ScriptEditor programEditor = (ScriptEditor) MainWindow.getInstance().getCurrentFrame();
            Script program = programEditor.getProgram();
            if (program == null || program.getDescriptor() == null || programEditor.needsSave()) {
                JOptionPane.showMessageDialog(MainWindow.getInstance(), "Please save your program first.",
                        "Debug Program", JOptionPane.OK_OPTION);
                return;
            }

            String override = EditorFileManager.getRelativePath(new File(program.getDescriptor().getURI()));
            override = override.replace("Programs", "").replace("\\", "/");
            if (override.startsWith("/")) {
                override = override.substring(1);
            }

            toggleButtons();
            File projectCopy = copyProject();
            overrideStartupProgram(projectCopy, override);
            startEngine(projectCopy);
        } catch (AssetException | IOException ex) {
            LOGGER.error("Failed to run engine.", ex);
        }
    }

    private void overrideStartupProgram(File projectCopy, String override) throws IOException, AssetException {
        // Open the .game file an override the configured startup program
        Collection<File> files = FileUtils.listFiles(projectCopy, new String[] { "game" }, false);
        if (files.isEmpty()) {
            throw new IOException("No game file present in project directory!");
        }
        File gameFile = files.iterator().next();
        AssetHandle handle = AssetManager.getInstance().deserialize(new AssetDescriptor(gameFile.toURI()));
        Game tempProject = (Game) handle.getAsset();
        AssetManager.getInstance().serialize(AssetManager.getInstance().getHandle(tempProject));
    }

}
