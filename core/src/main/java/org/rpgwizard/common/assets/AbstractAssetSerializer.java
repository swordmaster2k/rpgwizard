/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

/**
 * Abstract base class for asset serializers; provides some default
 * functionality.
 *
 * @author Chris Hutchinson
 */
public abstract class AbstractAssetSerializer implements AssetSerializer {

	public static final int DEFAULT_PRIORITY = 0;

	@Override
	public int priority() {
		return DEFAULT_PRIORITY; // return standard priority
	}

}
