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
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.ProjectEditor;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class SaveAsAction extends AbstractAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaveAsAction.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        MainWindow mainWindow = MainWindow.getInstance();
        JInternalFrame frame = mainWindow.getDesktopPane().getSelectedFrame();
        if (frame != null && frame instanceof AbstractAssetEditorWindow && !(frame instanceof ProjectEditor)) {
            AbstractAssetEditorWindow window = (AbstractAssetEditorWindow) frame;
            AbstractAsset asset = window.getAsset();
            if (asset != null) {
                File saveAs = EditorFileManager.saveByType(asset.getClass());
                if (saveAs != null) {
                    try {
                        final File current = window.getAsset().getFile();
                        AssetManager.getInstance().removeAsset(asset);
                        window.saveAs(saveAs);
                        mainWindow.updateEditorMap(current, saveAs, window);
                    } catch (Exception ex) {
                        LOGGER.error("Failed to invoke save as for asset frame=[{}]", frame, ex);
                        JOptionPane.showMessageDialog(MainWindow.getInstance(), "Error saving as file!",
                                "Error on Save As", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }

    }

}
