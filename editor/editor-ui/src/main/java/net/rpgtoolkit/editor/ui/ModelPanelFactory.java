/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.ui;

import net.rpgtoolkit.common.assets.Animation;
import net.rpgtoolkit.editor.editors.board.panels.BoardPanel;
import net.rpgtoolkit.editor.editors.board.panels.BoardVectorPanel;
import net.rpgtoolkit.editor.editors.board.panels.BoardSpritePanel;
import net.rpgtoolkit.common.assets.Board;
import net.rpgtoolkit.common.assets.BoardLight;
import net.rpgtoolkit.common.assets.BoardSprite;
import net.rpgtoolkit.common.assets.BoardVector;
import net.rpgtoolkit.common.assets.Item;
import net.rpgtoolkit.common.assets.Player;
import net.rpgtoolkit.editor.editors.ItemModelPanel;
import net.rpgtoolkit.editor.editors.animation.AnimationModelPanel;
import net.rpgtoolkit.editor.editors.character.CharacterModelPanel;

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
		} else if (model instanceof BoardLight) {

		} else if (model instanceof Animation) {
			return new AnimationModelPanel((Animation) model);
		} else if (model instanceof Player) {
			return new CharacterModelPanel((Player) model);
		} else if (model instanceof Item) {
			return new ItemModelPanel((Item) model);
		}

		return null;
	}

}
