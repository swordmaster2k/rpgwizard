/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions.asset;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.rpgwizard.common.assets.sprite.Sprite;

/**
 *
 * @author Joshua Michael Daly
 */
public class NewSpriteAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent ae) {
        Sprite sprite = new Sprite();
        sprite.setName("Untitled");
        putValue("sprite", sprite);
    }

}
