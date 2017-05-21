/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.assets.serialization;

import net.rpgtoolkit.common.assets.Animation;
import net.rpgtoolkit.common.assets.AssetDescriptor;
import net.rpgtoolkit.common.assets.AssetException;
import net.rpgtoolkit.common.assets.AssetHandle;
import net.rpgtoolkit.common.assets.SpriteSheet;
import net.rpgtoolkit.common.io.Paths;
import net.rpgtoolkit.common.utilities.CoreProperties;
import org.json.JSONObject;

/**
 *
 * @author Joshua Michael Daly
 */
public class JsonAnimationSerializer extends AbstractJsonSerializer {

    @Override
    public boolean serializable(AssetDescriptor descriptor) {
        final String ext = Paths.extension(descriptor.getURI());
        return (ext.endsWith(CoreProperties.getFullExtension("toolkit.animation.extension.json")));
    }

    @Override
    public boolean deserializable(AssetDescriptor descriptor) {
        return serializable(descriptor);
    }

    @Override
    protected void load(AssetHandle handle, JSONObject json) throws AssetException {
        final Animation animation = new Animation(handle.getDescriptor());

        animation.setAnimationWidth(json.optInt("width"));
        animation.setAnimationHeight(json.optInt("height"));
        animation.setFramRate(json.getInt("frameRate"));
        
        JSONObject object = json.getJSONObject("spriteSheet");
        animation.setSpriteSheet(
                new SpriteSheet(
                        object.getString("image"), 
                        object.getInt("x"), 
                        object.getInt("y"), 
                        object.getInt("width"), 
                        object.getInt("height")
                )
        );

        animation.setSoundEffect(json.getString("soundEffect"));

        handle.setAsset(animation);
    }

    @Override
    public void store(AssetHandle handle, JSONObject json) throws AssetException {
        super.store(handle, json);

        Animation animation = (Animation) handle.getAsset();

        json.put("width", animation.getAnimationWidth());
        json.put("height", animation.getAnimationHeight());
        json.put("frameRate", animation.getFrameRate());

        final SpriteSheet spriteSheet = animation.getSpriteSheet();
        final JSONObject object = new JSONObject();
        object.put("image", serializePath(spriteSheet.getFileName()));
        object.put("x", spriteSheet.getX());
        object.put("y", spriteSheet.getY());
        object.put("width", spriteSheet.getWidth());
        object.put("height", spriteSheet.getHeight());
        
        json.put("spriteSheet", object);

        json.put("soundEffect", serializePath(animation.getSoundEffect()));
    }

}
