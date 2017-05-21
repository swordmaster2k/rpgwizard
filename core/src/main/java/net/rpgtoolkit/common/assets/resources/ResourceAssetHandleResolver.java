/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.assets.resources;

import net.rpgtoolkit.common.assets.AssetDescriptor;
import net.rpgtoolkit.common.assets.AssetHandle;
import net.rpgtoolkit.common.assets.AssetHandleResolver;

/**
 * Resolves an asset handle for an asset reachable through the system class
 * loader, and therefore any resource on the classpath.
 *
 * @author Chris Hutchinson <chris@cshutchinson.com>
 */
public class ResourceAssetHandleResolver implements AssetHandleResolver {

	@Override
	public boolean resolvable(AssetDescriptor descriptor) {
		return descriptor.getURI().getScheme().equals("res");
	}

	@Override
	public AssetHandle resolve(AssetDescriptor descriptor) {
		return new ResourceAssetHandle(descriptor);
	}

}
