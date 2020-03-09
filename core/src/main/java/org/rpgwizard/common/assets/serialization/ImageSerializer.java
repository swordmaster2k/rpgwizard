/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.serialization;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.rpgwizard.common.assets.AbstractAssetSerializer;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.Image;
import org.rpgwizard.common.io.Paths;

/**
 *
 * @author Joshua Michael Daly
 */
public class ImageSerializer extends AbstractAssetSerializer {

    @Override
    public boolean serializable(AssetDescriptor descriptor) {
        final String ext = Paths.extension(descriptor.getURI());
        return (Arrays.asList(new String[] { ".png", ".gif", ".jpg", ".jpeg", ".bmp" }).contains(ext));
    }

    @Override
    public boolean deserializable(AssetDescriptor descriptor) {
        return serializable(descriptor);
    }

    @Override
    public void serialize(AssetHandle handle) throws IOException, AssetException {
        // Read-only do nothing
    }

    @Override
    public void deserialize(AssetHandle handle) throws IOException, AssetException {
        final Image image = new Image(handle.getDescriptor());
        File path = new File(handle.getDescriptor().getURI().getPath());
        image.setBufferedImage(ImageIO.read(path));

        handle.setAsset(image);
    }

}
