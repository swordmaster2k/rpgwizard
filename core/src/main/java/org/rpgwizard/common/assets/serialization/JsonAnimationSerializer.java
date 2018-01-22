/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.serialization;

import org.rpgwizard.common.assets.Animation;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.SpriteSheet;
import org.rpgwizard.common.io.Paths;
import org.rpgwizard.common.utilities.CoreProperties;
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

        animation.setVersion(json.getDouble("version"));
        animation.setAnimationWidth(json.optInt("width"));
        animation.setAnimationHeight(json.optInt("height"));
        animation.setFramRate(json.getInt("frameRate"));

        JSONObject object = json.getJSONObject("spriteSheet");
        animation.setSpriteSheet(new SpriteSheet(object.getString("image"), object.getInt("x"), object.getInt("y"),
                object.getInt("width"), object.getInt("height")));

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
