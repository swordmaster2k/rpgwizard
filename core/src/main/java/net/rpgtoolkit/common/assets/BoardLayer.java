/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.assets;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

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
  private ArrayList<BoardImage> images;

  /**
   * Creates a new layer with a parent board.
   *
   * @param parentBoard associated board
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
   * @param name layer name
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
   * @param number layer number
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
   * @param board associated board
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
   * @param tiles tile used on this layer
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
   * @param lights board lights on this layer
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
   * @param vectors vectors on this layer
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
   * @param sprites sprites used on this layer
   */
  public void setSprites(ArrayList<BoardSprite> sprites) {
    this.sprites = sprites;
  }

  /**
   * Gets the images used on this layer.
   *
   * @return images used on this layer
   */
  public ArrayList<BoardImage> getImages() {
    return images;
  }

  /**
   * Sets the images used on this layer
   *
   * @param images images used on this layer
   */
  public void setImages(ArrayList<BoardImage> images) {
    this.images = images;
  }

  /**
   * Gets the tile at the specified coordinates.
   *
   * @param x x position of tile
   * @param y y position of tile
   * @return the tile
   */
  public Tile getTileAt(int x, int y) {
    return tiles[x][y];
  }

  /**
   * Sets the tile at the specified coordinates
   *
   * @param x x position of tile
   * @param y y position of tile
   * @param tile the tile
   */
  public void setTileAt(int x, int y, Tile tile) {
    tiles[x][y] = tile;
    board.fireBoardChanged();
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

  /**
   * Does this layer contain the coordinates.
   *
   * @param x x position
   * @param y y position
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

    for (BoardImage image : images) {
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

    for (BoardImage image : images) {
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
    layer.images = (ArrayList<BoardImage>) images.clone();
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
   * @param x mouse click x
   * @param y mouse click y
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
        Line2D line2D = new Line2D.Double(vector.getPoints().get(i),
                vector.getPoints().get(i + 1));

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
   * @param x mouse click x
   * @param y mouse click y
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
   * @param x mouse click x
   * @param y mouse click y
   * @return a sprite or null
   */
  public BoardSprite findSpriteAt(int x, int y) {
    for (BoardSprite sprite : sprites) {
      int diffX = Math.abs(sprite.getX() - x);
      int diffY = Math.abs(sprite.getY() - y);
        
      if (diffX < 20 && diffY < 20) {
        return sprite;
      }
    }

    return null;
  }

  /**
   * Removes the sprite at the specified mouse click location.
   *
   * @param x mouse click x
   * @param y mouse click y
   * @return removed if any
   */
  public BoardSprite removeSpriteAt(int x, int y) {
    BoardSprite sprite = findSpriteAt(x, y);

    if (sprite != null) {
      sprites.remove(sprite);
      board.fireBoardChanged();
    }

    return sprite;
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
