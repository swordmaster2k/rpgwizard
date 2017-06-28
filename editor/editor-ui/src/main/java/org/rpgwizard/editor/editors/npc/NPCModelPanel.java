/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.npc;

import org.rpgwizard.common.assets.NPC;
import org.rpgwizard.editor.editors.sprite.AbstractSpriteModelPanel;

/**
 *
 * @author Joshua Michael Daly
 */
public class NPCModelPanel extends AbstractSpriteModelPanel {

	private final NPC npc;

	public NPCModelPanel(NPC model) {
		super(model);
		npc = model;
	}

}
