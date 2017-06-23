/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

/**
 * Defines the target of a status effect invocation.
 */
public enum StatusEffectTarget {

	/**
	 * Effect can target any entity.
	 */
	ANY,

	/**
	 * Effect targets a player.
	 */
	SELF,

	/**
	 * Effect targets an enemy.
	 */
	ENEMY

}
