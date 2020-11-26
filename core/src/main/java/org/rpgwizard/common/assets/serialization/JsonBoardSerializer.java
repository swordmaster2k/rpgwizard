/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.serialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.board.Board;
import org.rpgwizard.common.assets.board.BoardLayer;
import org.rpgwizard.common.assets.board.BoardSprite;
import org.rpgwizard.common.assets.board.BoardVector;
import org.rpgwizard.common.assets.board.EventType;
import org.rpgwizard.common.io.Paths;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.common.assets.board.StartingPosition;
import org.rpgwizard.common.assets.tileset.Tile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rpgwizard.common.assets.tileset.Tileset;
import org.rpgwizard.common.assets.board.BoardLayerImage;

/**
 * @author Joshua Michael Daly
 * @author Chris Hutchinson
 */
public class JsonBoardSerializer extends AbstractJsonSerializer {

    @Override
    public boolean serializable(AssetDescriptor descriptor) {
        final String ext = Paths.extension(descriptor.getURI());
        return (ext.equals(CoreProperties.getFullExtension("toolkit.board.extension.json")));
    }

    @Override
    public boolean deserializable(AssetDescriptor descriptor) {
        return serializable(descriptor);
    }

    @Override
    protected void load(AssetHandle handle, JSONObject json) throws AssetException {

        final Board board = new Board(handle.getDescriptor());

        board.setVersion(String.valueOf(json.get("version"))); // REFACTOR: Fix this

        board.setName(json.getString("name"));

        // Version 1.3.0
        if (json.has("description")) {
            board.setDescription(json.getString("description"));
        }

        board.setWidth(json.getInt("width"));
        board.setHeight(json.getInt("height"));
        board.setTileWidth(json.getInt("tileWidth"));
        board.setTileHeight(json.getInt("tileHeight"));

        JSONArray tileSets = json.getJSONArray("tileSets");
        List<String> tileSetNames = getStringArrayList(tileSets);
        board.setTileSets(getTileSets(tileSets));

        List<BoardSprite> sprites = getSprites(json.getJSONArray("sprites"));

        board.setLayers(getBoardLayers(json.getJSONArray("layers"), board, tileSetNames, sprites));

        JSONObject startingPosition = json.getJSONObject("startingPosition");
        board.setStartingPosition(new StartingPosition(startingPosition.getInt("x"), startingPosition.getInt("y"),
                startingPosition.getInt("layer")));

        board.setFirstRunProgram(json.getString("firstRunProgram"));
        board.setBackgroundMusic(json.getString("backgroundMusic"));

        board.setBoardDimensions(new int[board.getWidth()][board.getHeight()][board.getLayers().size()]);

        handle.setAsset(board);

    }

    @Override
    protected void store(AssetHandle handle, JSONObject json) throws AssetException {
        super.store(handle, json);

        final Board board = (Board) handle.getAsset();

        json.put("name", board.getName());
        json.put("description", board.getDescription());
        json.put("width", board.getWidth());
        json.put("height", board.getHeight());
        json.put("tileWidth", board.getTileWidth());
        json.put("tileHeight", board.getTileHeight());

        // Serialize TileSets.
        // Stored in LinkedHashMap which the original insertion order.
        final JSONArray tileSets = new JSONArray();
        for (Tileset tileSet : board.getTileSets().values()) {
            tileSets.put(serializePath(tileSet.getName()));
        }
        json.put("tileSets", tileSets);

        List<BoardSprite> boardSprites = new ArrayList<>();
        for (BoardLayer layer : board.getLayers()) {
            boardSprites.addAll(layer.getSprites());
        }

        // Serialize sprites
        final JSONArray sprites = new JSONArray();
        for (final BoardSprite sprite : boardSprites) {
            final JSONObject s = new JSONObject();
            if (sprite.getFileName() == null || sprite.getFileName().isEmpty()) {
                continue;
            }
            s.put("name", serializePath(sprite.getFileName()));
            s.put("id", sprite.getId());
            JSONObject spritePosition = new JSONObject();
            spritePosition.put("x", sprite.getX());
            spritePosition.put("y", sprite.getY());
            spritePosition.put("layer", sprite.getLayer());
            s.put("startingPosition", spritePosition);

            // TODO: remove this once the editor supports adding multiple
            // events through the UI
            JSONArray events = new JSONArray();
            if (sprite.getEventProgram() != null && !sprite.getEventProgram().isEmpty()) {
                JSONObject event = new JSONObject();
                event.put("type", sprite.getEventType().name().toLowerCase());
                event.put("program", serializePath(sprite.getEventProgram()));
                event.put("key", sprite.getActivationKey());
                events.put(event);
            }

            s.put("events", events);

            s.put("thread", serializePath(sprite.getThread()));

            sprites.put(s);
        }
        json.put("sprites", sprites);

        // Serialize layers.
        final JSONArray layers = new JSONArray();
        for (BoardLayer boardLayer : board.getLayers()) {
            JSONObject layer = new JSONObject();
            layer.put("name", boardLayer.getName());

            // Tiles.
            int width = board.getWidth();
            int height = board.getHeight();
            JSONArray tiles = new JSONArray();
            Tile[][] layerTiles = boardLayer.getTiles();

            int count = width * height;
            int x = 0;
            int y = 0;
            for (int j = 0; j < count; j++) {
                // Default values for a blank tile.
                int tileSetIndex = -1;
                int tileIndex = -1;

                Tile tile = layerTiles[x][y];
                if (tile.getTileSet() != null) {
                    tileSetIndex = new ArrayList<>(board.getTileSets().keySet()).indexOf(tile.getTileSet().getName());
                    tileIndex = tile.getIndex();
                }

                String tileIndexer = tileSetIndex + ":" + tileIndex;
                tiles.put(tileIndexer);

                x++;
                if (x == width) {
                    x = 0;
                    y++;
                    if (y == height) {
                        break;
                    }
                }
            }
            layer.put("tiles", tiles);

            // Vectors.
            JSONArray vectors = serializeBoardVectors(boardLayer.getVectors());
            layer.put("vectors", vectors);

            // Images.
            JSONArray images = serializeBoardLayerImages(boardLayer.getImages());
            layer.put("images", images);

            layers.put(layer);
        }
        json.put("layers", layers);

        JSONObject startingPosition = new JSONObject();
        startingPosition.put("x", board.getStartingPositionX());
        startingPosition.put("y", board.getStartingPositionY());
        startingPosition.put("layer", board.getStartingLayer());
        json.put("startingPosition", startingPosition);

        json.put("firstRunProgram", serializePath(board.getFirstRunProgram()));
        json.put("backgroundMusic", serializePath(board.getBackgroundMusic()));

        handle.setAsset(board);
    }

    private Map<String, Tileset> getTileSets(JSONArray array) {
        Map<String, Tileset> tileSets = new HashMap<>();

        Tileset tileSet;
        int length = array.length();
        for (int i = 0; i < length; i++) {
            AssetDescriptor descriptor = null;
            tileSet = new Tileset(descriptor);
            String name = array.getString(i);
            tileSet.setName(name);

            tileSets.put(name, tileSet);
        }

        return tileSets;
    }

    private LinkedList<BoardLayer> getBoardLayers(JSONArray array, Board board, List<String> tileSetNames,
            List<BoardSprite> sprites) {
        LinkedList<BoardLayer> layers = new LinkedList<>();

        int width = board.getWidth();
        int height = board.getHeight();

        BoardLayer layer;
        int length = array.length();
        for (int i = 0; i < length; i++) {
            JSONObject jsonLayer = array.getJSONObject(i);
            layer = new BoardLayer(board);
            layer.setName(jsonLayer.getString("name"));
            layer.setNumber(i);

            // Tiles.
            JSONArray tiles = jsonLayer.getJSONArray("tiles");
            int count = width * height;
            int x = 0;
            int y = 0;
            for (int j = 0; j < count; j++) {
                String[] tileIndexer = tiles.getString(j).split(":");
                int tileSetIndex = Integer.parseInt(tileIndexer[0]);
                int tileIndex = Integer.parseInt(tileIndexer[1]);

                Tile tile = new Tile();
                if (!(tileSetIndex == -1 && tileIndex == -1)) { // Check for blank tile.
                    Tileset tileSet = board.getTileSets().get(tileSetNames.get(tileSetIndex));
                    tile = new Tile(tileSet, tileIndex);
                }

                layer.setTileAt(x, y, tile);

                x++;
                if (x == width) {
                    x = 0;
                    y++;
                    if (y == height) {
                        break;
                    }
                }
            }

            // Vectors.
            JSONArray vectors = jsonLayer.getJSONArray("vectors");
            ArrayList<BoardVector> boardVectors = deserializeBoardVectors(vectors);
            for (BoardVector boardVector : boardVectors) {
                boardVector.setLayer(i);
            }
            layer.setVectors(boardVectors);

            // Sprites.
            for (BoardSprite sprite : sprites) {
                if (sprite.getLayer() == i) {
                    layer.getSprites().add(sprite);
                }
            }

            // Version 1.3.0 - Layer Image
            if (jsonLayer.has("images")) {
                JSONArray images = jsonLayer.getJSONArray("images");
                ArrayList<BoardLayerImage> boardLayerImages = deserializeBoardLayerImages(images);
                for (BoardLayerImage boardLayerImage : boardLayerImages) {
                    boardLayerImage.setLayer(i);
                }
                layer.setImages(boardLayerImages);
            }

            layers.add(layer);
        }

        return layers;
    }

    private ArrayList<BoardSprite> getSprites(JSONArray array) {
        ArrayList<BoardSprite> sprites = new ArrayList<>();

        BoardSprite sprite;
        int length = array.length();
        for (int i = 0; i < length; i++) {
            JSONObject object = array.getJSONObject(i);
            sprite = new BoardSprite();

            sprite.setFileName(object.getString("name"));

            sprite.setId(object.getString("id"));

            JSONObject startingPosition = object.getJSONObject("startingPosition");
            sprite.setX(startingPosition.getInt("x"));
            sprite.setY(startingPosition.getInt("y"));
            sprite.setLayer(startingPosition.getInt("layer"));

            // TODO: remove this once the editor supports adding multiple
            // events through the UI
            JSONArray events = object.getJSONArray("events");
            if (events.length() > 0) {
                JSONObject event = events.getJSONObject(0);
                sprite.setEventType(EventType.valueOf(event.getString("type").toUpperCase()));
                sprite.setEventProgram(event.getString("program"));

                if (sprite.getEventType() == EventType.KEYPRESS) {
                    sprite.setActivationKey(event.getString("key"));
                }
            }

            sprite.setThread(object.getString("thread"));
            sprites.add(sprite);
        }

        return sprites;
    }

    @Override
    protected JSONObject store(AssetHandle handle) throws AssetException {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }
}
