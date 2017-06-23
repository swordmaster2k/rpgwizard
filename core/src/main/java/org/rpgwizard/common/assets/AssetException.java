/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

/**
 * Base class for all asset management exceptions.
 *
 * @author Chris Hutchinson
 */
public class AssetException extends Exception {

	// TODO: Consider reason codes for AssetException

	public AssetException(String message) {
		super(message);
	}

}
