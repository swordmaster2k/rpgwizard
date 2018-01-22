/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @author Chris Hutchinson
 */
public abstract class AssetHandle {

    public AssetHandle(AssetDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public abstract ReadableByteChannel read() throws IOException;

    public abstract WritableByteChannel write() throws IOException;

    public abstract long size() throws IOException;

    public Asset getAsset() {
        return this.asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public AssetDescriptor getDescriptor() {
        return this.descriptor;
    }

    protected final AssetDescriptor descriptor;
    protected Asset asset;

}
