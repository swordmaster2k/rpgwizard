/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.events;

import java.util.EventObject;
import org.rpgwizard.common.assets.AbstractSprite;

/**
 * An <code>EventObject</code> used to contain information of a change that has
 * happened on a sprite.
 * 
 * @author Joshua Michael Daly
 */
public class SpriteChangedEvent extends EventObject {

	public SpriteChangedEvent(AbstractSprite sprite) {
		super(sprite);
	}

}
