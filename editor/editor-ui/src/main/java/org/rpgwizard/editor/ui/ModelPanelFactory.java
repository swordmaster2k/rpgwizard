/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui;

import org.rpgwizard.common.Selectable;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.common.assets.animation.Animation;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.assets.Image;
import org.rpgwizard.common.assets.Trigger;
import org.rpgwizard.common.assets.tileset.Tile;
import org.rpgwizard.common.assets.map.MapImage;
import org.rpgwizard.common.assets.map.MapSprite;
import org.rpgwizard.common.assets.map.SelectablePair;
import org.rpgwizard.common.assets.sprite.Sprite;
import org.rpgwizard.editor.editors.animation.AnimationModelPanel;
import org.rpgwizard.editor.editors.map.panels.MapImagePanel;
import org.rpgwizard.editor.editors.map.panels.MapPanel;
import org.rpgwizard.editor.editors.map.panels.MapSpritePanel;
import org.rpgwizard.editor.editors.sprite.SpriteModelPanel;
import org.rpgwizard.editor.editors.image.ImageModelPanel;
import org.rpgwizard.editor.editors.map.panels.ColliderPanel;
import org.rpgwizard.editor.editors.map.panels.TriggerPanel;
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
        if (model instanceof Map) {
            return new MapPanel((Map) model);
        } else if (model instanceof SelectablePair) {
            Selectable selectable = (Selectable) ((SelectablePair) model).getRight();
            if (selectable instanceof MapSprite) {
                return new MapSpritePanel((SelectablePair<String, MapSprite>) model);
            } else if (selectable instanceof MapImage) {
                return new MapImagePanel((SelectablePair<String, MapImage>) model);
            } else if (selectable instanceof Collider) {
                return new ColliderPanel((SelectablePair<String, Collider>) model);
            } else if (selectable instanceof Trigger) {
                return new TriggerPanel((SelectablePair<String, Trigger>) model);
            }
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
