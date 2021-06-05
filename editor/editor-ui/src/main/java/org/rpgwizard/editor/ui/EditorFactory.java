/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui;

import org.rpgwizard.common.assets.animation.Animation;
import org.rpgwizard.common.assets.Asset;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.assets.Image;
import org.rpgwizard.common.assets.Script;
import org.rpgwizard.common.assets.game.Game;
import org.rpgwizard.common.assets.sprite.Sprite;
import org.rpgwizard.editor.editors.AnimationEditor;
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.editors.ScriptEditor;
import org.rpgwizard.editor.editors.GameEditor;
import org.rpgwizard.editor.editors.SpriteEditor;
import org.rpgwizard.editor.editors.image.ImageEditor;

/**
 * A factory for obtaining the corresponding file format editor based on the asset type.
 * 
 * @author Joshua Michael Daly
 */
public class EditorFactory {

    private EditorFactory() {

    }

    /**
     * Gets the matching editor for the asset type.
     * 
     * @param asset
     * @return editor for asset
     */
    public static AbstractAssetEditorWindow getEditor(Asset asset) {
        if (asset instanceof Animation) {
            return new AnimationEditor((Animation) asset);
        } else if (asset instanceof Map) {
            return new MapEditor((Map) asset);
        } else if (asset instanceof Sprite) {
            return new SpriteEditor((Sprite) asset);
        } else if (asset instanceof Script) {
            return new ScriptEditor((Script) asset);
        } else if (asset instanceof Game) {
            return new GameEditor((Game) asset);
        } else if (asset instanceof Image) {
            return new ImageEditor((Image) asset);
        }

        return null;
    }

}
