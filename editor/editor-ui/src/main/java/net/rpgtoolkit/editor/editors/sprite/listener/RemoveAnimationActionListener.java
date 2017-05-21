/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.editors.sprite.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTable;
import net.rpgtoolkit.common.assets.Animation;
import net.rpgtoolkit.common.assets.AbstractSprite;
import net.rpgtoolkit.common.assets.AnimationEnum;
import net.rpgtoolkit.editor.editors.sprite.AbstractSpriteEditor;
import net.rpgtoolkit.editor.editors.sprite.AnimationsTableModel;

/**
 *
 * @author Joshua Michael Daly
 */
public class RemoveAnimationActionListener implements ActionListener {

    private final AbstractSpriteEditor spriteEditor;

    private final AbstractSprite sprite;

    private final JTable animationsTable;
    private final AnimationsTableModel animationsTableModel;

    public RemoveAnimationActionListener(AbstractSpriteEditor editor) {
        spriteEditor = editor;
        sprite = spriteEditor.getSprite();
        animationsTable = spriteEditor.getAnimationsTable();
        animationsTableModel = (AnimationsTableModel) animationsTable.getModel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int rowIndex = animationsTable.getSelectedRow();
        if (rowIndex < sprite.getAnimations().size()) {
            String key = (String) animationsTable.getValueAt(rowIndex, 0);

            try {
                AnimationEnum.valueOf(key);
            } catch (IllegalArgumentException ex) {
                // Not a default can be removed.
                sprite.removeAnimation(key);

                if (rowIndex > 0) {
                    if (rowIndex == animationsTableModel.getRowCount()) {
                        rowIndex--;
                    }

                    animationsTable.scrollRectToVisible(animationsTable.getCellRect(rowIndex, 0, true));
                }

                spriteEditor.setSelectedAnim(null);
                spriteEditor.updateAnimatedPanel();
            }
        }

    }

}
