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
import org.rpgwizard.common.assets.board.Board;
import org.rpgwizard.common.assets.sprite.Character;
import org.rpgwizard.common.assets.sprite.Enemy;
import org.rpgwizard.common.assets.Image;
import org.rpgwizard.common.assets.Item;
import org.rpgwizard.common.assets.sprite.NPC;
import org.rpgwizard.common.assets.Program;
import org.rpgwizard.common.assets.game.Game;
import org.rpgwizard.editor.editors.AnimationEditor;
import org.rpgwizard.editor.editors.BoardEditor;
import org.rpgwizard.editor.editors.CharacterEditor;
import org.rpgwizard.editor.editors.EnemyEditor;
import org.rpgwizard.editor.editors.ItemEditor;
import org.rpgwizard.editor.editors.NPCEditor;
import org.rpgwizard.editor.editors.ProgramEditor;
import org.rpgwizard.editor.editors.GameEditor;
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
        } else if (asset instanceof Board) {
            return new BoardEditor((Board) asset);
        } else if (asset instanceof Enemy) {
            return new EnemyEditor((Enemy) asset);
        } else if (asset instanceof Item) {
            return new ItemEditor((Item) asset);
        } else if (asset instanceof NPC) {
            return new NPCEditor((NPC) asset);
        } else if (asset instanceof Character) {
            return new CharacterEditor((Character) asset);
        } else if (asset instanceof Program) {
            return new ProgramEditor((Program) asset);
        } else if (asset instanceof Game) {
            return new GameEditor((Game) asset);
        } else if (asset instanceof Image) {
            return new ImageEditor((Image) asset);
        }

        return null;
    }

}
