/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui;

import org.rpgwizard.common.assets.Animation;
import org.rpgwizard.editor.editors.board.panels.BoardPanel;
import org.rpgwizard.editor.editors.board.panels.BoardVectorPanel;
import org.rpgwizard.editor.editors.board.panels.BoardSpritePanel;
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.assets.BoardLight;
import org.rpgwizard.common.assets.BoardSprite;
import org.rpgwizard.common.assets.BoardVector;
import org.rpgwizard.common.assets.NPC;
import org.rpgwizard.common.assets.Character;
import org.rpgwizard.common.assets.Enemy;
import org.rpgwizard.editor.editors.EnemyModelPanel;
import org.rpgwizard.editor.editors.NPCModelPanel;
import org.rpgwizard.editor.editors.animation.AnimationModelPanel;
import org.rpgwizard.editor.editors.character.CharacterModelPanel;

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
		} else if (model instanceof Character) {
			return new CharacterModelPanel((Character) model);
		} else if (model instanceof NPC) {
			return new NPCModelPanel((NPC) model);
		} else if (model instanceof Enemy) {
			return new EnemyModelPanel((Enemy) model);
		}

		return null;
	}

}
