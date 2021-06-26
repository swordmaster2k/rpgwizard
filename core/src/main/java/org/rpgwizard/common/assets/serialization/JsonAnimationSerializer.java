/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.rpgwizard.common.assets.animation.Animation;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.io.Paths;
import org.rpgwizard.common.utilities.CoreProperties;
import org.json.JSONObject;
import static org.rpgwizard.common.assets.serialization.AbstractJsonSerializer.MAPPER;

/**
 *
 * @author Joshua Michael Daly
 */
public class JsonAnimationSerializer extends AbstractJsonSerializer {

    @Override
    public boolean serializable(AssetDescriptor descriptor) {
        final String ext = Paths.extension(descriptor.getUri());
        return (ext.endsWith(CoreProperties.getFullExtension("rpgwizard.animation.extension.json")));
    }

    @Override
    public boolean deserializable(AssetDescriptor descriptor) {
        return serializable(descriptor);
    }

    @Override
    protected void load(AssetHandle handle, JSONObject json) throws AssetException {
        try {
            final Animation asset = MAPPER.readValue(json.toString(), Animation.class);
            asset.getSpriteSheet().setTileWidth(asset.getWidth());
            asset.getSpriteSheet().setTileHeight(asset.getHeight());
            asset.setDescriptor(handle.getDescriptor());
            handle.setAsset(asset);
        } catch (JsonProcessingException ex) {
            throw new AssetException(ex.getMessage());
        }
    }

    @Override
    protected JSONObject store(AssetHandle handle) throws AssetException {
        try {
            final Animation asset = (Animation) handle.getAsset();
            return new JSONObject(MAPPER.writeValueAsString(asset));
        } catch (JsonProcessingException ex) {
            throw new AssetException(ex.getMessage());
        }
    }

}
