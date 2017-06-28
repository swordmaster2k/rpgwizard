/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.character;

import org.rpgwizard.common.assets.Character;
import org.rpgwizard.editor.editors.sprite.AbstractSpriteModelPanel;

/**
 *
 * @author Joshua Michael Daly
 */
public class CharacterModelPanel extends AbstractSpriteModelPanel {

	private final Character player;

	public CharacterModelPanel(Character model) {
		super(model);
		player = model;
	}

}
