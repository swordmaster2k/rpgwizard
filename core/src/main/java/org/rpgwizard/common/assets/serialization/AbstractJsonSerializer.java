/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Point;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rpgwizard.common.assets.AbstractAssetSerializer;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.board.Event;
import org.rpgwizard.common.assets.board.EventType;
import org.rpgwizard.common.assets.game.Game;
import org.rpgwizard.common.assets.board.KeyPressEvent;
import org.rpgwizard.common.assets.tileset.Tileset;
import org.rpgwizard.common.assets.board.BoardLayerImage;
import org.rpgwizard.common.assets.board.BoardVector;
import org.rpgwizard.common.assets.board.BoardVectorType;
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
            if (handle.getAsset() instanceof Game || handle.getAsset() instanceof Tileset || handle.getAsset() instanceof Sprite) { // REFACTOR: Remove this
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

    protected JSONArray serializeBoardVectors(List<BoardVector> vectors) {
        final JSONArray jsonVectors = new JSONArray();

        for (final BoardVector vector : vectors) {
            jsonVectors.put(serializeBoardVector(vector));
        }

        return jsonVectors;
    }

    protected JSONObject serializeBoardVector(BoardVector vector) {
        final JSONObject v = new JSONObject();
        final JSONArray points = new JSONArray();
        final JSONArray events = new JSONArray();

        for (final Point point : vector.getPoints()) {
            final JSONObject pt = new JSONObject();
            pt.put("x", point.x);
            pt.put("y", point.y);
            points.put(pt);
        }

        v.put("id", vector.getId());
        v.put("points", points);
        v.put("isClosed", vector.isClosed());
        v.put("type", vector.getType());

        for (final Event event : vector.getEvents()) {
            final JSONObject evt = new JSONObject();
            evt.put("type", event.getType().name().toLowerCase());
            evt.put("program", event.getProgram());
            if (event.getType() == EventType.KEYPRESS) {
                evt.put("key", ((KeyPressEvent) event).getKey());
            }
            events.put(evt);
        }
        v.put("events", events);

        return v;
    }

    protected ArrayList<BoardVector> deserializeBoardVectors(JSONArray array) {
        ArrayList<BoardVector> vectors = new ArrayList<>();

        int length = array.length();
        for (int i = 0; i < length; i++) {
            JSONObject object = array.getJSONObject(i);
            vectors.add(deserializeBoardVector(object));
        }

        return vectors;
    }

    protected BoardVector deserializeBoardVector(JSONObject object) {
        BoardVector vector = new BoardVector();
        vector.setId(object.getString("id"));
        vector.setPoints(getPoints(object.getJSONArray("points")));
        vector.setClosed(object.getBoolean("isClosed"));
        vector.setType(BoardVectorType.valueOf(object.getString("type")));
        vector.setEvents(getEvents(object.getJSONArray("events")));

        return vector;
    }

    protected JSONArray serializeBoardLayerImages(List<BoardLayerImage> images) {
        final JSONArray array = new JSONArray();
        images.stream().filter((image) -> !(image.getSrc() == null || image.getSrc().isEmpty()))
                .forEachOrdered((image) -> {
                    array.put(serializeBoardLayerImage(image));
                });
        return array;
    }

    protected JSONObject serializeBoardLayerImage(BoardLayerImage image) {
        final JSONObject object = new JSONObject();
        object.put("id", image.getId());
        object.put("src", image.getSrc());
        object.put("x", image.getX());
        object.put("y", image.getY());

        return object;
    }

    protected ArrayList<BoardLayerImage> deserializeBoardLayerImages(JSONArray array) {
        ArrayList<BoardLayerImage> images = new ArrayList<>();

        int length = array.length();
        for (int i = 0; i < length; i++) {
            JSONObject object = array.getJSONObject(i);
            images.add(deserializeBoardLayerImage(object));
        }

        return images;
    }

    protected BoardLayerImage deserializeBoardLayerImage(JSONObject object) {
        BoardLayerImage layerImage = new BoardLayerImage(object.getString("id"), object.getString("src"),
                object.getInt("x"), object.getInt("y"));
        return layerImage;
    }

    protected JSONArray serializeSpriteVectors(List<BoardVector> vectors) {
        final JSONArray jsonVectors = new JSONArray();

        for (final BoardVector vector : vectors) {
            jsonVectors.put(serializeSpriteVector(vector));
        }

        return jsonVectors;
    }

    protected JSONObject serializeSpriteVector(BoardVector vector) {
        final JSONObject v = new JSONObject();
        final JSONArray points = new JSONArray();
        final JSONArray events = new JSONArray();

        for (final Point point : vector.getPoints()) {
            final JSONObject pt = new JSONObject();
            pt.put("x", point.x);
            pt.put("y", point.y);
            points.put(pt);
        }
        v.put("points", points);

        for (final Event event : vector.getEvents()) {
            final JSONObject evt = new JSONObject();
            evt.put("type", event.getType().name().toLowerCase());
            evt.put("program", serializePath(event.getProgram()));
            events.put(evt);
        }
        v.put("events", events);

        return v;
    }

    protected ArrayList<BoardVector> deserializeSpriteVectors(JSONArray array) {
        ArrayList<BoardVector> vectors = new ArrayList<>();

        int length = array.length();
        for (int i = 0; i < length; i++) {
            JSONObject object = array.getJSONObject(i);
            vectors.add(deserializeSpriteVector(object));
        }

        return vectors;
    }

    protected BoardVector deserializeSpriteVector(JSONObject object) {
        BoardVector vector = new BoardVector();
        vector.setPoints(getPoints(object.getJSONArray("points")));
        vector.setEvents(getEvents(object.getJSONArray("events")));

        return vector;
    }

    protected JSONObject serializePoint(Point point) {
        JSONObject object = new JSONObject();
        object.put("x", point.getX());
        object.put("y", point.getY());
        return object;
    }

    protected Point deserializePoint(JSONObject object) {
        return new Point(object.getInt("x"), object.getInt("y"));
    }

    protected LinkedHashMap<String, String> deserializeStringMap(JSONObject object) {
        LinkedHashMap<String, String> map = new LinkedHashMap();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, object.getString(key));
        }

        return map;
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

    protected static JSONObject serializeMap(Map map) throws JSONException {
        JSONObject json = new JSONObject();
        for (Object key : map.keySet()) {
            json.put(key.toString(), serializePath(map.get(key).toString()));
        }
        return json;
    }

    protected static String serializePath(String path) {
        // Use *nix path separator everywhere, higher compatability.
        return FilenameUtils.separatorsToUnix(path);
    }

    private ArrayList<Point> getPoints(JSONArray array) {
        ArrayList<Point> points = new ArrayList<>();

        int length = array.length();
        for (int i = 0; i < length; i++) {
            JSONObject point = array.getJSONObject(i);

            points.add(new Point(point.getInt("x"), point.getInt("y")));
        }

        return points;
    }

    private ArrayList<Event> getEvents(JSONArray array) {
        ArrayList<Event> events = new ArrayList<>();

        int length = array.length();
        for (int i = 0; i < length; i++) {
            JSONObject event = array.getJSONObject(i);
            EventType type = EventType.valueOf(event.getString("type").toUpperCase());
            String program = event.getString("program");

            if (type == EventType.OVERLAP) {
                events.add(new Event(type, program));
            } else if (type == EventType.KEYPRESS) {
                String key = event.getString("key");
                events.add(new KeyPressEvent(key, program));
            }

        }

        return events;
    }
}
