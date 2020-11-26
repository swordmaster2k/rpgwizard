/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.sprite.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTable;
import org.rpgwizard.common.assets.animation.Animation;
import org.rpgwizard.common.assets.sprite.Sprite;
import org.rpgwizard.editor.editors.SpriteEditor;
import org.rpgwizard.editor.utilities.EditorFileManager;

/**
 *
 * @author Joshua Michael Daly
 */
public class BrowseAnimationActionListener implements ActionListener {

    private final Sprite sprite;

    private final SpriteEditor spriteEditor;

    private final JTable animationsTable;

    public BrowseAnimationActionListener(SpriteEditor editor) {
        spriteEditor = editor;
        sprite = editor.getSprite();
        animationsTable = editor.getAnimationsTable();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int rowIndex = animationsTable.getSelectedRow();
        if (rowIndex < 0) {
            return;
        }

        String path = EditorFileManager.browseByTypeRelative(Animation.class);
        if (path != null) {
            String key = (String) animationsTable.getValueAt(rowIndex, 0);
            sprite.updateAnimation(key, path);

            spriteEditor.openAnimation(path);
        }
    }

}
