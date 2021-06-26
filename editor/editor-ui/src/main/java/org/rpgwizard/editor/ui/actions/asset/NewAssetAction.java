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
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openide.util.Utilities;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.Script;
import org.rpgwizard.common.assets.animation.Animation;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.assets.sprite.Sprite;
import org.rpgwizard.common.assets.tileset.Tileset;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.AnimationEditor;
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.editors.ScriptEditor;
import org.rpgwizard.editor.editors.SpriteEditor;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.FileTools;

/**
 *
 * @author Joshua Michael Daly
 */
@Slf4j
@AllArgsConstructor
public class NewAssetAction extends AbstractAction {

    private final Class<? extends AbstractAsset> type;
    private final File parent;

    @Override
    public void actionPerformed(ActionEvent ae) {
        File file = FileTools.promptForFile("New File", parent, EditorFileManager.getTypeExtensions(type)[0]);
        if (file == null) {
            return; // User cancelled
        }

        AbstractAsset asset = null;
        if (type == Animation.class) {
            asset = new Animation(new AssetDescriptor(Utilities.toURI(file)));
            AnimationEditor.prepareAnimation((Animation) asset);
        } else if (type == Map.class) {
            NewMapAction action = new NewMapAction();
            action.actionPerformed(null);
            asset = (Map) action.getValue("map");
            if (asset == null) {
                return;
            }
            asset.setDescriptor(new AssetDescriptor(Utilities.toURI(file)));
            MapEditor.prepareMap((Map) asset);
        } else if (type == Sprite.class) {
            NewSpriteAction action = new NewSpriteAction();
            action.actionPerformed(null);
            asset = (Sprite) action.getValue("sprite");
            if (asset == null) {
                return;
            }
            asset.setDescriptor(new AssetDescriptor(Utilities.toURI(file)));
            SpriteEditor.prepareNewSprite((Sprite) asset);
        } else if (type == Script.class) {
            asset = new Script(new AssetDescriptor(Utilities.toURI(file)));
            ScriptEditor.prepareNewScript((Script) asset);
        } else if (type == Tileset.class) {
            // Tilesets behave a little differently as they are created instantly
            NewTilesetAction action = new NewTilesetAction();
            action.putValue("file", file);
            action.actionPerformed(null);
            return;
        }

        if (asset != null) {
            try {
                file.createNewFile();
                FileTools.saveAsset(asset);
            } catch (Exception ex) {
                log.error("Failed to create new asset=[{}]", asset, ex);
                JOptionPane.showMessageDialog(MainWindow.getInstance(),
                        "Failed to create file " + file.getAbsolutePath(), "Failed to Create",
                        JOptionPane.ERROR_MESSAGE);
                FileUtils.deleteQuietly(file);
            }
        }
    }

}
