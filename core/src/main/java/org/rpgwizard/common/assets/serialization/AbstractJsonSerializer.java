/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.rpgwizard.common.assets.AbstractAssetSerializer;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.game.Game;
import org.rpgwizard.common.assets.tileset.Tileset;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.assets.sprite.Sprite;

/**
 * Abstract base class for implementing asset serializers that load or store their contents using JSON encoding.
 *
 * @author Joel Moore
 * @author Chris Hutchinson
 * @author Joshua Micahel Daly
 */
public abstract class AbstractJsonSerializer extends AbstractAssetSerializer {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final String FILE_FORMAT_VERSION = "2.0.0";
    public static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void serialize(AssetHandle handle) throws IOException, AssetException {
        try (final WritableByteChannel channel = handle.write()) {
            // Store the asset contents into a JSON representation
            JSONObject obj = new JSONObject();
            if (handle.getAsset() instanceof Game || handle.getAsset() instanceof Tileset
                    || handle.getAsset() instanceof Sprite || handle.getAsset() instanceof Map) { // REFACTOR: Remove
                // this
                obj = store(handle);
            } else {
                store(handle, obj);
            }

            // Encode JSON representation with the specified character set encoding
            final String contents = obj.toString();
            final ByteBuffer encodedContents = DEFAULT_CHARSET.encode(contents);

            // Write encoded buffer to the channel
            channel.write(encodedContents);
        }
    }

    @Override
    public void deserialize(AssetHandle handle) throws IOException, AssetException {
        try (final ReadableByteChannel channel = handle.read()) {
            // Read the asset contents into a buffer
            final ByteBuffer buffer = ByteBuffer.allocateDirect((int) handle.size());
            channel.read(buffer);
            buffer.position(0);

            // Decode and parse the contents as JSON using the specified
            // character set encoding
            final CharBuffer source = DEFAULT_CHARSET.decode(buffer);
            final JSONObject obj = new JSONObject(source.toString());

            // Load asset from the decoded JSON
            load(handle, obj);
        }
    }

    protected abstract void load(AssetHandle handle, JSONObject json) throws AssetException;

    protected abstract JSONObject store(AssetHandle handle) throws AssetException;

    protected void store(AssetHandle handle, JSONObject json) throws AssetException {
        json.put("version", FILE_FORMAT_VERSION);
    }

    protected ArrayList<String> getStringArrayList(JSONArray array) {
        ArrayList<String> strings = new ArrayList<>();

        int length = array.length();
        for (int i = 0; i < length; i++) {
            strings.add(array.getString(i));
        }

        return strings;
    }

    protected ArrayList<Boolean> getBooleanArrayList(JSONArray array) {
        ArrayList<Boolean> booleans = new ArrayList<>();

        int length = array.length();
        for (int i = 0; i < length; i++) {
            booleans.add(array.getBoolean(i));
        }

        return booleans;
    }

    protected boolean[] getBooleanArray(JSONArray array) {
        boolean[] booleans = new boolean[array.length()];

        int length = array.length();
        for (int i = 0; i < length; i++) {
            booleans[i] = array.getBoolean(i);
        }

        return booleans;
    }

    protected static String serializePath(String path) {
        // Use *nix path separator everywhere, higher compatability.
        return FilenameUtils.separatorsToUnix(path);
    }

    protected LinkedHashMap<String, Integer> deserializeIntegerMap(JSONObject object) {
        LinkedHashMap<String, Integer> map = new LinkedHashMap();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, object.getInt(key));
        }

        return map;
    }

}
