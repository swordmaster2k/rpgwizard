/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.io.Paths;
import org.rpgwizard.common.utilities.CoreProperties;

/**
 *
 * @author Joshua Michael Daly
 */
public class JsonMapSerializer extends AbstractJsonSerializer {

    @Override
    public boolean serializable(AssetDescriptor descriptor) {
        final String ext = Paths.extension(descriptor.getURI().getPath());
        return (ext.contains(CoreProperties.getFullExtension("rpgwizard.map.extension.json")));
    }

    @Override
    public boolean deserializable(AssetDescriptor descriptor) {
        return serializable(descriptor);
    }

    @Override
    protected void load(AssetHandle handle, JSONObject json) throws AssetException {
        try {
            final Map asset = MAPPER.readValue(json.toString(), Map.class);
            asset.setDescriptor(handle.getDescriptor());
            handle.setAsset(asset);
        } catch (JsonProcessingException ex) {
            throw new AssetException(ex.getMessage());
        }
    }

    @Override
    protected JSONObject store(AssetHandle handle) throws AssetException {
        try {
            final Map asset = (Map) handle.getAsset();
            return new JSONObject(MAPPER.writeValueAsString(asset));
        } catch (JsonProcessingException ex) {
            throw new AssetException(ex.getMessage());
        }
    }

}
