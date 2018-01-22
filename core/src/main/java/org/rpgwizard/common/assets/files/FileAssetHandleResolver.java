/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.files;

import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.AssetHandleResolver;

/**
 * Resolves a handle for an asset located on a filesystem.
 *
 * @author Chris Hutchinson <chris@cshutchinson.com>
 */
public class FileAssetHandleResolver implements AssetHandleResolver {

    @Override
    public boolean resolvable(AssetDescriptor descriptor) {
        return descriptor.getURI().getScheme().equals("file");
    }

    @Override
    public AssetHandle resolve(AssetDescriptor descriptor) {
        return new FileAssetHandle(descriptor);
    }

}
