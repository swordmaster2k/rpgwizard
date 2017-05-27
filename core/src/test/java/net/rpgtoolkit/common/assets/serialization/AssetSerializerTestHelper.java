/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.assets.serialization;

import org.rpgwizard.common.assets.Asset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.AssetSerializer;
import org.rpgwizard.common.assets.files.FileAssetHandle;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 *
 * @author Chris Hutchinson <chris@cshutchinson.com>
 */
public class AssetSerializerTestHelper {

    public static String getPath(String fileName) throws IOException, URISyntaxException {
        return AssetSerializerTestHelper.class.getClassLoader().getResource(fileName).toURI().toString();
    }

    public static String serialize(Asset asset, AssetSerializer serializer)
            throws AssetException, IOException {
        final File file = File.createTempFile(
                UUID.randomUUID().toString(), ".tmp");
        file.deleteOnExit();
        
        final AssetDescriptor descriptor = new AssetDescriptor(file.toURI());
        final AssetHandle handle = new FileAssetHandle(descriptor);

        handle.setAsset(asset);
        serializer.serialize(handle);

        return file.toURI().toString();
    }

    public static <T> T deserializeFile(String path, AssetSerializer serializer)
            throws AssetException, IOException {
        final AssetDescriptor descriptor = AssetDescriptor.parse(path);
        final AssetHandle handle = new FileAssetHandle(descriptor);

        serializer.deserialize(handle);

        return (T) handle.getAsset();
    }

}
