/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.assets.serialization;

import java.util.ArrayList;
import java.util.List;
import net.rpgtoolkit.common.assets.AssetDescriptor;
import net.rpgtoolkit.common.assets.AssetException;
import net.rpgtoolkit.common.assets.AssetHandle;
import net.rpgtoolkit.common.assets.TileSet;
import net.rpgtoolkit.common.io.Paths;
import net.rpgtoolkit.common.utilities.CoreProperties;
import org.json.JSONObject;

/**
 *
 * @author Joshua Michael Daly
 */
public class JsonTileSetSerializer extends AbstractJsonSerializer {

    @Override
    public boolean serializable(AssetDescriptor descriptor) {
            final String ext = Paths.extension(descriptor.getURI());
            return (ext.equals(CoreProperties.getFullExtension("toolkit.tileset.extension.json")));
    }

    @Override
    public boolean deserializable(AssetDescriptor descriptor) {
        return serializable(descriptor);
    }
    
    @Override
    protected void load(AssetHandle handle, JSONObject json) throws AssetException {
        final TileSet tileSet = new TileSet(
                handle.getDescriptor(), 
                json.optInt("tileWidth"), 
                json.optInt("tileHeight")
        );
    
        tileSet.setName(json.optString("name"));
        tileSet.setImages(getStringArrayList(json.getJSONArray("images")));
        
        handle.setAsset(tileSet);
    }
    
    @Override
    protected void store(AssetHandle handle, JSONObject json) throws AssetException {
        super.store(handle, json);
        
        final TileSet tileSet = (TileSet) handle.getAsset();
        
        json.put("name", tileSet.getName());
        json.put("tileWidth", tileSet.getTileWidth());
        json.put("tileHeight", tileSet.getTileHeight());
        
        List<String> images = new ArrayList();
        for (String image : tileSet.getImages()) {
            images.add(serializePath(image));
        }
        json.put("images", images);
    }

}
