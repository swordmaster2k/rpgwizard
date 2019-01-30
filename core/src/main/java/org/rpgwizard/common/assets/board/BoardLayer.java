/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.board;

import com.google.common.collect.Lists;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.assets.Tile;

/**
 * Represents a layer on a board.
 *
 * @author Joshua Michael Daly
 */
public class BoardLayer implements Cloneable {

    /**
     * The name of the layer.
     */
    private String name;
    /**
     * What number is this layer on the board?
     */
    private int number;
    /**
     * A reference to the board this layer belongs to.
     */
    private Board board;
    /**
     * A list of all the tiles used on this layer.
     */
    private Tile[][] tiles;
    /**
     * A list of all the lights used on this layer.
     */
    private ArrayList<BoardLight> lights;
    /**
     * A list of all the vectors on this layer.
     */
    private ArrayList<BoardVector> vectors;
    /**
     * A list of all the sprites on this layer.
     */
    private ArrayList<BoardSprite> sprites;
    /**
     * A list of all the images on this layer.
     */
    private ArrayList<BoardLayerImage> images;

    /**
     * Creates a new layer with a parent board.
     *
     * @param parentBoard
     *            associated board
     */
    public BoardLayer(Board parentBoard) {
        board = parentBoard;
        tiles = new Tile[board.getWidth()][board.getHeight()];
        lights = new ArrayList<>();
        vectors = new ArrayList<>();
        sprites = new ArrayList<>();
        images = new ArrayList<>();

        clearTiles();
    }

    /**
     * Copy constructor.
     * 
     * @param boardLayer
     * @param board
     */
    public BoardLayer(BoardLayer boardLayer, Board board) {
        this(board);
        images = (ArrayList<BoardLayerImage>) boardLayer.images.clone();
        lights = (ArrayList<BoardLight>) boardLayer.lights.clone();
        name = boardLayer.name;
        number = boardLayer.number;
        sprites = (ArrayList<BoardSprite>) boardLayer.sprites.clone();
        tiles = (Tile[][]) boardLayer.tiles.clone();
        vectors = (ArrayList<BoardVector>) boardLayer.vectors.clone();
    }

    /**
     * Gets the layer name.
     *
     * @return layer name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the layer name.
     *
     * @param name
     *            layer name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the layer number.
     *
     * @return layer number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Sets the layer number.
     *
     * @param number
     *            layer number
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Gets the associated board with this layer.
     *
     * @return associated board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Sets the associated board with this layer
     *
     * @param board
     *            associated board
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Gets the tiles used on this layer.
     *
     * @return tiles used on this layer
     */
    public Tile[][] getTiles() {
        return tiles;
    }

    /**
     * Sets the tiles used on this layer.
     *
     * @param tiles
     *            tile used on this layer
     */
    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    /**
     * Gets the board lights used on this layer.
     *
     * @return board lights on this layer
     */
    public ArrayList<BoardLight> getLights() {
        return lights;
    }

    /**
     * Sets the board lights used on this layer.
     *
     * @param lights
     *            board lights on this layer
     */
    public void setLights(ArrayList<BoardLight> lights) {
        this.lights = lights;
    }

    /**
     * Gets the vectors used on this layer.
     *
     * @return vectors on this layer
     */
    public ArrayList<BoardVector> getVectors() {
        return vectors;
    }

    /**
     * Sets the vectors used on this layer.
     *
     * @param vectors
     *            vectors on this layer
     */
    public void setVectors(ArrayList<BoardVector> vectors) {
        this.vectors = vectors;
    }

    /**
     * Gets the sprites used on this layer.
     *
     * @return sprites used on this layer
     */
    public ArrayList<BoardSprite> getSprites() {
        return sprites;
    }

    /**
     * Sets the sprites used on this layer.
     *
     * @param sprites
     *            sprites used on this layer
     */
    public void setSprites(ArrayList<BoardSprite> sprites) {
        this.sprites = sprites;
    }

    /**
     * Gets the images used on this layer.
     *
     * @return images used on this layer
     */
    public ArrayList<BoardLayerImage> getImages() {
        return images;
    }

    /**
     * Sets the images used on this layer
     *
     * @param images
     *            images used on this layer
     */
    public void setImages(ArrayList<BoardLayerImage> images) {
        this.images = images;
    }

    /**
     * Gets the tile at the specified coordinates.
     *
     * @param x
     *            x position of tile
     * @param y
     *            y position of tile
     * @return the tile
     */
    public Tile getTileAt(int x, int y) {
        return tiles[x][y];
    }

    /**
     * Sets the tile at the specified coordinates
     *
     * @param x
     *            x position of tile
     * @param y
     *            y position of tile
     * @param tile
     *            the tile
     */
    public void setTileAt(int x, int y, Tile tile) {
        if (x >= 0 && x < tiles.length) {
            if (y >= 0 && y < tiles[x].length) {
                tiles[x][y] = tile;
                board.fireBoardChanged();
            }
        }
    }

    /**
     * 
     * @param sprite
     */
    public void addBoardSprite(BoardSprite sprite) {
        sprites.add(sprite);
    }

    public void removeBoardSprite(BoardSprite sprite) {
        sprites.remove(sprite);
    }

    public void addBoardLayerImage(BoardLayerImage image) {
        images.add(image);
    }

    public void removeBoardLayerImage(BoardLayerImage image) {
        images.remove(image);
    }

    /**
     * Does this layer contain the coordinates.
     *
     * @param x
     *            x position
     * @param y
     *            y position
     * @return true = yes, false = no
     */
    public boolean contains(int x, int y) {
        if (x < 0 || y < 0) {
            return false;
        }

        return x < tiles.length && y < tiles[0].length;
    }

    /**
     * Moves the layer up on the board.
     */
    public void moveLayerUp() {
        number++;

        for (BoardLight light : lights) {
            light.setLayer(number);
        }

        for (BoardVector vector : vectors) {
            vector.setLayer(number);
        }

        for (BoardSprite sprite : sprites) {
            sprite.setLayer(number);
        }

        for (BoardLayerImage image : images) {
            image.setLayer(number);
        }
    }

    /**
     * Moves the layer down on the board.
     */
    public void moveLayerDown() {
        number--;

        for (BoardLight light : lights) {
            light.setLayer(number);
        }

        for (BoardVector vector : vectors) {
            vector.setLayer(number);
        }

        for (BoardSprite sprite : sprites) {
            sprite.setLayer(number);
        }

        for (BoardLayerImage image : images) {
            image.setLayer(number);
        }
    }

    /**
     * Directly clones the layer.
     *
     * @return clone of layer
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();

        BoardLayer layer = new BoardLayer(board);
        layer.images = (ArrayList<BoardLayerImage>) images.clone();
        layer.lights = (ArrayList<BoardLight>) lights.clone();
        layer.name = name + "_clone";
        layer.number = number;
        layer.sprites = (ArrayList<BoardSprite>) sprites.clone();
        layer.tiles = (Tile[][]) tiles.clone();
        layer.vectors = (ArrayList<BoardVector>) vectors.clone();
        layer.moveLayerUp();

        return layer;
    }

    /**
     * Finds a vector at the coordinates based on a small bounding box around the mouse click.
     *
     * @param x
     *            mouse click x
     * @param y
     *            mouse click y
     * @return a vector or null
     */
    public BoardVector findVectorAt(int x, int y) {
        // Create a small rectangle to represent the bounds of the mouse.
        Rectangle2D mouse = new Rectangle2D.Double(x - 5, y - 5, 10, 10);

        for (BoardVector vector : vectors) {
            // There are no lines.
            if (vector.getPoints().size() < 2) {
                continue;
            }

            for (int i = 0; i < vector.getPoints().size() - 1; i++) {
                // Build a line from the points in the polygon.
                Line2D line2D = new Line2D.Double(vector.getPoints().get(i), vector.getPoints().get(i + 1));

                // See if the mouse intersects the line of the polygon.
                if (line2D.intersects(mouse)) {
                    return vector;
                }
            }
        }

        return null;
    }

    /**
     * Removes the vector at the specified mouse click location.
     *
     * @param x
     *            mouse click x
     * @param y
     *            mouse click y
     * @return removed vector if any
     */
    public BoardVector removeVectorAt(int x, int y) {
        BoardVector vector = findVectorAt(x, y);

        if (vector != null) {
            vectors.remove(vector);
            board.fireBoardChanged();
        }

        return vector;
    }

    /**
     * Finds a sprite at the coordinates based on a small bounding box around the mouse click.
     *
     * @param x
     *            mouse click x
     * @param y
     *            mouse click y
     * @param width
     *            default image width
     * @param height
     *            default image height
     * @return a sprite or null
     */
    public BoardSprite findSpriteAt(int x, int y, int width, int height) {
        for (BoardSprite sprite : Lists.reverse(sprites)) {
            BufferedImage image = sprite.getSouthImage();
            if (image != null) {
                width = image.getWidth();
                height = image.getHeight();
            }
            int x1 = sprite.getX() - width / 2;
            int y1 = sprite.getY() - height / 2;
            int x2 = x1 + width;
            int y2 = y1 + height;
            if (x1 < x && x < x2 && y1 < y && y < y2) {
                return sprite;
            }
        }

        return null;
    }

    /**
     * Removes the sprite at the specified mouse click location.
     *
     * @param x
     *            mouse click x
     * @param y
     *            mouse click y
     * @param width
     *            default image width
     * @param height
     *            default image height
     * @return removed if any
     */
    public BoardSprite removeSpriteAt(int x, int y, int width, int height) {
        BoardSprite sprite = findSpriteAt(x, y, width, height);

        if (sprite != null) {
            sprites.remove(sprite);
            board.fireBoardChanged();
        }

        return sprite;
    }

    /**
     * Finds an image at the coordinates based on a small bounding box around the mouse click.
     *
     * @param x
     *            mouse click x
     * @param y
     *            mouse click y
     * @param width
     *            default image width
     * @param height
     *            default image height
     * @return a sprite or null
     */
    public BoardLayerImage findImageAt(int x, int y, int width, int height) {
        for (BoardLayerImage layerImage : Lists.reverse(images)) {
            BufferedImage image = layerImage.getImage();
            if (image != null) {
                width = image.getWidth();
                height = image.getHeight();
            }
            int x1 = layerImage.getX();
            int y1 = layerImage.getY();
            int x2 = x1 + width;
            int y2 = y1 + height;
            if (x1 < x && x < x2 && y1 < y && y < y2) {
                return layerImage;
            }
        }

        return null;
    }

    /**
     * Removes the image at the specified mouse click location.
     *
     * @param x
     *            mouse click x
     * @param y
     *            mouse click y
     * @param width
     *            default image width
     * @param height
     *            default image height
     * @return removed if any
     */
    public BoardLayerImage removeImageAt(int x, int y, int width, int height) {
        BoardLayerImage image = findImageAt(x, y, width, height);

        if (image != null) {
            images.remove(image);
            board.fireBoardChanged();
        }

        return image;
    }

    /**
     * Clears this layers tiles.
     */
    private void clearTiles() {
        int count = board.getWidth() * board.getHeight();
        Tile blankTile = new Tile();
        int x = 0;
        int y = 0;

        for (int i = 0; i < count; i++) {
            tiles[x][y] = blankTile;

            x++;
            if (x == board.getWidth()) {
                x = 0;
                y++;
                if (y == board.getHeight()) {
                    break;
                }
            }
        }
    }

}
