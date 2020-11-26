/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.sprite.listener;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.rpgwizard.common.assets.sprite.Sprite;
import org.rpgwizard.common.assets.animation.AnimationEnum;
import org.rpgwizard.editor.editors.SpriteEditor;
import org.rpgwizard.editor.editors.sprite.AbstractSpriteEditor;

/**
 *
 * @author Joshua Michael Daly
 */
public class AnimationListSelectionListener implements ListSelectionListener {

    private final Sprite sprite;

    private final AbstractSpriteEditor spriteEditor;

    private final JTable animationsTable;

    public AnimationListSelectionListener(SpriteEditor editor) {
        spriteEditor = editor;
        sprite = editor.getSprite();
        animationsTable = editor.getAnimationsTable();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int rowIndex = animationsTable.getSelectedRow();
            if (rowIndex == -1) {
                spriteEditor.updateAnimatedPanel();

                spriteEditor.getBrowseButton().setEnabled(false);
                spriteEditor.getRemoveButton().setEnabled(false);
            } else {
                String path = (String) animationsTable.getValueAt(rowIndex, 1);

                spriteEditor.openAnimation(path);
                spriteEditor.getBrowseButton().setEnabled(true);

                try {
                    AnimationEnum.valueOf((String) animationsTable.getValueAt(rowIndex, 0));
                    spriteEditor.getRemoveButton().setEnabled(false); // Cannot
                                                                      // remove
                                                                      // default
                                                                      // graphics.
                } catch (IllegalArgumentException ex) {
                    spriteEditor.getRemoveButton().setEnabled(true);
                }
            }
        }
    }

}
