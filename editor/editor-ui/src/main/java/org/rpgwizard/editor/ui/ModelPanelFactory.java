/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui;

import org.rpgwizard.common.assets.animation.Animation;
import org.rpgwizard.common.assets.board.Board;
import org.rpgwizard.common.assets.Image;
import org.rpgwizard.common.assets.tileset.Tile;
import org.rpgwizard.common.assets.board.BoardLayerImage;
import org.rpgwizard.common.assets.board.BoardSprite;
import org.rpgwizard.common.assets.board.BoardVector;
import org.rpgwizard.common.assets.sprite.Sprite;
import org.rpgwizard.editor.editors.animation.AnimationModelPanel;
import org.rpgwizard.editor.editors.board.panels.BoardLayerImagePanel;
import org.rpgwizard.editor.editors.board.panels.BoardPanel;
import org.rpgwizard.editor.editors.board.panels.BoardSpritePanel;
import org.rpgwizard.editor.editors.board.panels.BoardVectorPanel;
import org.rpgwizard.editor.editors.sprite.SpriteModelPanel;
import org.rpgwizard.editor.editors.image.ImageModelPanel;
import org.rpgwizard.editor.editors.tileset.TileModelPanel;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public final class ModelPanelFactory {

    private ModelPanelFactory() {

    }

    public static AbstractModelPanel getModelPanel(Object model) {
        if (model instanceof Board) {
            return new BoardPanel((Board) model);
        } else if (model instanceof BoardVector) {
            return new BoardVectorPanel((BoardVector) model);
        } else if (model instanceof BoardSprite) {
            return new BoardSpritePanel((BoardSprite) model);
        } else if (model instanceof BoardLayerImage) {
            return new BoardLayerImagePanel((BoardLayerImage) model);
        } else if (model instanceof Animation) {
            return new AnimationModelPanel((Animation) model);
        } else if (model instanceof Sprite) {
            return new SpriteModelPanel((Sprite) model);
        } else if (model instanceof Tile) {
            return new TileModelPanel((Tile) model);
        } else if (model instanceof Image) {
            return new ImageModelPanel((Image) model);
        }

        return null;
    }

}
