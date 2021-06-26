/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.resources;

import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.AssetHandleResolver;

/**
 * Resolves an asset handle for an asset reachable through the system class loader, and therefore any resource on the
 * classpath.
 *
 * @author Chris Hutchinson <chris@cshutchinson.com>
 */
public class ResourceAssetHandleResolver implements AssetHandleResolver {

    @Override
    public boolean resolvable(AssetDescriptor descriptor) {
        return descriptor.getUri().getScheme().equals("res");
    }

    @Override
    public AssetHandle resolve(AssetDescriptor descriptor) {
        return new ResourceAssetHandle(descriptor);
    }

}
