/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetHandle;

/**
 * @author Chris Hutchinson <chris@cshutchinson.com>
 */
public class ResourceAssetHandle extends AssetHandle {

    private final ClassLoader loader;

    public ResourceAssetHandle(AssetDescriptor descriptor) {
        this(descriptor, ResourceAssetHandle.class.getClassLoader());
    }

    public ResourceAssetHandle(AssetDescriptor descriptor, ClassLoader loader) {
        super(descriptor);
        if (loader == null)
            throw new NullPointerException();
        this.loader = loader;
    }

    @Override
    public ReadableByteChannel read() throws IOException {
        final String part = this.descriptor.getUri().getSchemeSpecificPart();
        final String path = part.substring(1);
        final InputStream in = this.loader.getResourceAsStream(path);
        return Channels.newChannel(in);
    }

    @Override
    public WritableByteChannel write() throws IOException {
        throw new IOException("internal resource assets are read-only.");
    }

    @Override
    public long size() throws IOException {
        return 0;
    }

}
