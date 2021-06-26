/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions.asset;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import lombok.extern.slf4j.Slf4j;
import org.openide.util.Utilities;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.tileset.Tileset;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.tileset.NewTilesetDialog;
import org.rpgwizard.editor.utilities.EditorFileManager;

/**
 *
 * @author Joshua Michael Daly
 */
@Slf4j
public class NewTilesetAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent ae) {
        Tileset tileset = null;

        NewTilesetDialog dialog = new NewTilesetDialog();
        dialog.setLocationRelativeTo(MainWindow.getInstance());
        dialog.setVisible(true);

        if (dialog.getValue() != null) {
            int tileWidth = dialog.getValue()[0];
            int tileHeight = dialog.getValue()[1];

            String path = CoreProperties.getProperty("rpgwizard.directory.textures");
            String description = "Image Files";
            String[] extensions = EditorFileManager.getImageExtensions();
            EditorFileManager.setFileChooserSubdirAndFilters(path, description, extensions);

            if (EditorFileManager.getFileChooser()
                    .showOpenDialog(MainWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
                File imageFile = EditorFileManager.getFileChooser().getSelectedFile();

                try {
                    File tilesetFile = (File) getValue("file");
                    if (tilesetFile == null) {
                        tilesetFile = EditorFileManager.saveByType(Tileset.class);
                        if (tilesetFile == null) {
                            return; // Cancelled by the user.
                        }
                        putValue("file", tilesetFile);
                    }

                    Tileset tempTileset = new Tileset(new AssetDescriptor(Utilities.toURI(tilesetFile)), tileWidth,
                            tileHeight);
                    String remove = EditorFileManager.getGraphicsPath();
                    String imagePath = imageFile.getAbsolutePath().replace(remove, "");
                    tempTileset.setImage(imagePath);

                    AssetManager.getInstance().serialize(AssetManager.getInstance().getHandle(tempTileset));
                    tileset = tempTileset;
                } catch (IOException | AssetException ex) {
                    log.error("Failed to create new {} file=[{}].", Tileset.class.getSimpleName(), imageFile, ex);
                }
            }
        }

        putValue("tileset", tileset);
    }

}
