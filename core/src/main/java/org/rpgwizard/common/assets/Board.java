/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.rpgwizard.common.Selectable;
import org.rpgwizard.common.assets.events.BoardChangedEvent;
import org.rpgwizard.common.assets.listeners.BoardChangeListener;
import org.rpgwizard.common.utilities.TileSetCache;

/**
 * A model that represents <code>Board</code> files in the RPGToolkit engine and
 * editor. Used during the serialization processes to various formats, at the
 * moment it contains the old binary routines for opening 3.x formats which
 * should be removed in 4.1.
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public final class Board extends AbstractAsset implements Selectable {

	/**
	 * Geometric perspective of a board for use with directional movement,
	 * rendering, raycasting, and other mathematical transformations.
	 *
	 * @author Chris Hutchinson
	 */
	public enum Perspective {

		ORTHOGONAL, ISOMETRIC_STACKED, ISOMETRIC_ROTATED;

	}

	// Non-IO
	private final LinkedList<BoardChangeListener> boardChangeListeners;
	private boolean selectedState; // TODO: This is editor specific, move it!
	private Perspective perspective;

	// Variables
	private String name;
	private int width;
	private int height;
	private int tileWidth;
	private int tileHeight;
	private LinkedHashMap<String, TileSet> tileSets;
	private LinkedList<BoardLayer> layers;
	private int[][][] boardDimensions; // x, y, z
	private StartingPosition startingPosition;
	private String backgroundMusic;
	private String firstRunProgram;

	/**
	 * Creates an empty board.
	 *
	 * @param descriptor
	 */
	public Board(AssetDescriptor descriptor) {
        super(descriptor);
        startingPosition = new StartingPosition();
        tileSets = new LinkedHashMap<>();
        layers = new LinkedList<>();
        boardChangeListeners = new LinkedList<>();
    }
	/**
	 * Creates a new board with the specified width and height.
	 *
	 * @param descriptor
	 * @param width
	 *            board width
	 * @param height
	 *            board height
	 * @param tileWidth
	 * @param tileHeight
	 */
	public Board(AssetDescriptor descriptor, int width, int height,
			int tileWidth, int tileHeight) {
		this(descriptor);
		reset();
		this.width = width;
		this.height = height;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		addLayer();
	}

	/**
	 * Gets this boards layers.
	 *
	 * @return the board layers
	 */
	public List<BoardLayer> getLayers() {
		return this.layers;
	}

	/**
	 * Sets this board's layers.
	 *
	 * @param layers
	 */
	public void setLayers(LinkedList<BoardLayer> layers) {
		this.layers = layers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the width of this board.
	 *
	 * @return width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the width of this board.
	 *
	 * @param width
	 *            new width
	 */
	public void setWidth(int width) {
		if (width <= 0) {
			throw new IllegalArgumentException("width must be > 0");
		}
		this.width = width;
	}

	/**
	 * Gets the height of this board.
	 *
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the height of this board.
	 *
	 * @param height
	 *            new height
	 */
	public void setHeight(int height) {
		if (height <= 0) {
			throw new IllegalArgumentException("height must be > 0");
		}
		this.height = height;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public void setTileWidth(int tileWidth) {
		this.tileWidth = tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public void setTileHeight(int tileHeight) {
		this.tileHeight = tileHeight;
	}

	public void addSprite(BoardSprite sprite) {
		BoardLayer boardLayer = layers.get(sprite.getLayer());
		boardLayer.addBoardSprite(sprite);
		fireBoardSpriteAdded(sprite);
	}

	public void removeSprite(BoardSprite sprite) {
		BoardLayer boardLayer = layers.get(sprite.getLayer());
		boardLayer.removeBoardSprite(sprite);
		fireBoardSpriteAdded(sprite);
	}

	public StartingPosition getStartingPosition() {
		return startingPosition;
	}

	public void setStartingPosition(StartingPosition startingPosition) {
		this.startingPosition = startingPosition;
	}

	/**
	 * Gets the players starting x position.
	 *
	 * @return starting x position
	 */
	public int getStartingPositionX() {
		return startingPosition.x;
	}

	/**
	 * Sets the players starting x position.
	 *
	 * @param startingPositionX
	 *            new starting x position
	 */
	public void setStartingPositionX(int startingPositionX) {
		startingPosition.x = startingPositionX;
	}

	/**
	 * Gets the players starting y position.
	 *
	 * @return starting y position
	 */
	public int getStartingPositionY() {
		return startingPosition.y;
	}

	/**
	 * Sets the players starting y position.
	 *
	 * @param startingPositionY
	 *            new starting y position
	 */
	public void setStartingPositionY(int startingPositionY) {
		this.startingPosition.y = startingPositionY;
	}

	/**
	 * Gets the players starting layer index.
	 *
	 * @return starting layer index.
	 */
	public int getStartingLayer() {
		return startingPosition.layer;
	}

	/**
	 * Sets the players starting layer index.
	 *
	 * @param startingLayer
	 *            new starting layer index
	 */
	public void setStartingLayer(int startingLayer) {
		this.startingPosition.layer = startingLayer;
	}

	/**
	 * Gets the tile index at the specified location.
	 *
	 * @param x
	 *            x location
	 * @param y
	 *            y location
	 * @param z
	 *            z layer index
	 * @return tile index
	 */
	public int getIndexAtLocation(int x, int y, int z) {
		return layers.get(z).getTiles()[x][y].getIndex();
	}

	/**
	 * Gets the board's geometric perspective.
	 *
	 * @return current geometric perspective
	 */
	public Perspective getPerspective() {
		return this.perspective;
	}

	/**
	 * Sets the board's geometric perspective.
	 *
	 * @param perspective
	 *            new geometric perspective
	 */
	public void setPerspective(Perspective perspective) {
		this.perspective = perspective;
	}

	/**
	 * Gets the board dimensions.
	 *
	 * @return x, y, z, where z is the number of layers
	 */
	public int[][][] getBoardDimensions() {
		return boardDimensions;
	}

	/**
	 * Sets the board dimensions.
	 *
	 * @param boardDimensions
	 *            new board dimensions x, y, z
	 */
	public void setBoardDimensions(int[][][] boardDimensions) {
		this.boardDimensions = boardDimensions;
	}

	/**
	 * Gets the board layer names.
	 *
	 * @return board layer names
	 */
	public List<String> getLayerNames() {
        List<String> layerNames = new ArrayList<>();
        for (BoardLayer layer : layers) {
            layerNames.add(layer.getName());
        }

        return layerNames;
    }
	/**
	 * Gets the layer name by index.
	 *
	 * @param index
	 *            layer index
	 * @return layer name
	 */
	public String getLayerName(int index) {
		return layers.get(index).getName();
	}

	/**
	 * Sets the layer name by index.
	 *
	 * @param index
	 *            layer index
	 * @param name
	 *            new layer name
	 */
	public void setLayerName(int index, String name) {
		layers.get(index).setName(name);
		fireBoardChanged();
	}

	/**
	 * Gets the board background music.
	 *
	 * @return background music filename
	 */
	public String getBackgroundMusic() {
		return backgroundMusic;
	}

	/**
	 * Sets the board background music.
	 *
	 * @param backgroundMusic
	 *            new background music filename
	 */
	public void setBackgroundMusic(String backgroundMusic) {
		this.backgroundMusic = backgroundMusic;
	}

	/**
	 * Gets the board first run program.
	 *
	 * @return first run program on enter
	 */
	public String getFirstRunProgram() {
		return firstRunProgram;
	}

	/**
	 * Sets the board first run program.
	 *
	 * @param firstRunProgram
	 *            new first run program on enter
	 */
	public void setFirstRunProgram(String firstRunProgram) {
		this.firstRunProgram = firstRunProgram;
	}

	/**
	 * Gets a hash of the board tile sets indexed by name.
	 *
	 * @return tile set hash
	 */
	public Map<String, TileSet> getTileSets() {
		return tileSets;
	}

	/**
	 * Sets the hash of board tile sets indexed by name
	 *
	 * @param tileSets
	 *            new tile set hash
	 */
	public void setTileSets(Map<String, TileSet> tileSets) {
		this.tileSets.clear();
		this.tileSets.putAll(tileSets);
	}

	/**
	 * Is this board selected in the editor?
	 *
	 * @return selected state
	 */
	@Override
	public boolean isSelected() {
		return selectedState;
	}

	/**
	 * Set the selected state of this board in the editor
	 *
	 * @param state
	 *            new state
	 */
	@Override
	public void setSelectedState(boolean state) {
		selectedState = state;
	}

	@Override
	public void reset() {
		tileSets.clear();

		width = 0;
		height = 0;
		perspective = Perspective.ORTHOGONAL;
		boardDimensions = new int[width][height][layers.size()];
		backgroundMusic = "";
		firstRunProgram = "";
		startingPosition = new StartingPosition();
	}

	/**
	 * Add a new <code>BoardChangeListener</code> for this board.
	 *
	 * @param listener
	 *            new change listener
	 */
	public void addBoardChangeListener(BoardChangeListener listener) {
		boardChangeListeners.add(listener);
	}

	/**
	 * Remove an existing <code>BoardChangeListener</code> for this board.
	 *
	 * @param listener
	 *            change listener
	 */
	public void removeBoardChangeListener(BoardChangeListener listener) {
		boardChangeListeners.remove(listener);
	}

	/**
	 * Fires the <code>BoardChangedEvent</code> informs all the listeners that
	 * this board has changed.
	 */
	public void fireBoardChanged() {
		BoardChangedEvent event = null;
		Iterator iterator = boardChangeListeners.iterator();

		while (iterator.hasNext()) {
			if (event == null) {
				event = new BoardChangedEvent(this);
			}

			((BoardChangeListener) iterator.next()).boardChanged(event);
		}
	}

	/**
	 * Fires the <code>BoardChangedEvent</code> informs all the listeners that
	 * this board has changed.
	 *
	 * @param layer
	 *            new layer
	 */
	public void fireBoardLayerAdded(BoardLayer layer) {
		BoardChangedEvent event = null;
		Iterator iterator = boardChangeListeners.iterator();

		while (iterator.hasNext()) {
			if (event == null) {
				event = new BoardChangedEvent(this);
				event.setLayer(layer);
			}

			((BoardChangeListener) iterator.next()).boardLayerAdded(event);
		}
	}

	/**
	 * Fires the <code>BoardChangedEvent</code> informs all the listeners that
	 * this board has changed.
	 *
	 * @param layer
	 *            effected layer
	 */
	public void fireBoardLayerMovedUp(BoardLayer layer) {
		BoardChangedEvent event = null;
		Iterator iterator = boardChangeListeners.iterator();

		while (iterator.hasNext()) {
			if (event == null) {
				event = new BoardChangedEvent(this);
				event.setLayer(layer);
			}

			((BoardChangeListener) iterator.next()).boardLayerMovedUp(event);
		}
	}

	/**
	 * Fires the <code>BoardChangedEvent</code> informs all the listeners that
	 * this board has changed.
	 *
	 * @param layer
	 *            effected layer
	 */
	public void fireBoardLayerMovedDown(BoardLayer layer) {
		BoardChangedEvent event = null;
		Iterator iterator = boardChangeListeners.iterator();

		while (iterator.hasNext()) {
			if (event == null) {
				event = new BoardChangedEvent(this);
				event.setLayer(layer);
			}

			((BoardChangeListener) iterator.next()).boardLayerMovedDown(event);
		}
	}

	/**
	 * Fires the <code>BoardChangedEvent</code> informs all the listeners that
	 * this board has changed.
	 *
	 * @param layer
	 *            cloned layer
	 */
	public void fireBoardLayerCloned(BoardLayer layer) {
		BoardChangedEvent event = null;
		Iterator iterator = boardChangeListeners.iterator();

		while (iterator.hasNext()) {
			if (event == null) {
				event = new BoardChangedEvent(this);
				event.setLayer(layer);
			}

			((BoardChangeListener) iterator.next()).boardLayerCloned(event);
		}
	}

	/**
	 * Fires the <code>BoardChangedEvent</code> informs all the listeners that
	 * this board has changed.
	 *
	 * @param layer
	 *            deleted layer
	 */
	public void fireBoardLayerDeleted(BoardLayer layer) {
		BoardChangedEvent event = null;
		Iterator iterator = boardChangeListeners.iterator();

		while (iterator.hasNext()) {
			if (event == null) {
				event = new BoardChangedEvent(this);
				event.setLayer(layer);
			}

			((BoardChangeListener) iterator.next()).boardLayerDeleted(event);
		}
	}

	/**
	 * Fires the <code>BoardChangedEvent</code> informs all the listeners that
	 * this board has changed.
	 *
	 * @param sprite
	 */
	public void fireBoardSpriteAdded(BoardSprite sprite) {
		BoardChangedEvent event = null;
		Iterator iterator = boardChangeListeners.iterator();

		while (iterator.hasNext()) {
			if (event == null) {
				event = new BoardChangedEvent(this);
				event.setBoardSprite(sprite);
			}

			((BoardChangeListener) iterator.next()).boardSpriteAdded(event);
		}
	}

	/**
	 * Fires the <code>BoardChangedEvent</code> informs all the listeners that
	 * this board has changed.
	 *
	 * @param sprite
	 */
	public void fireBoardSpriteRemoved(BoardSprite sprite) {
		BoardChangedEvent event = null;
		Iterator iterator = boardChangeListeners.iterator();

		while (iterator.hasNext()) {
			if (event == null) {
				event = new BoardChangedEvent(this);
				event.setBoardSprite(sprite);
			}

			((BoardChangeListener) iterator.next()).boardSpriteRemoved(event);
		}
	}

	/**
	 * Add a new blank layer to this board.
	 */
	public void addLayer() {
		int layerNumber = layers.size() + 1;

		BoardLayer layer = new BoardLayer(this);
		layer.setName("Untitled Layer " + layerNumber);
		layer.setNumber(layers.size());
		layers.add(layer);

		fireBoardLayerAdded(layer);
	}

	/**
	 * Moves the layer up to the specified index if possible.
	 *
	 * @param index
	 *            higher index
	 * @return was it moved
	 */
	public boolean moveLayerUp(int index) {
		// Highest possible index, can't be move up!
		if (index == layers.size()) {
			return false;
		}

		BoardLayer down = layers.get(index + 1);
		BoardLayer up = layers.get(index);
		layers.set(index + 1, up);
		layers.set(index, down);

		down.moveLayerDown();
		up.moveLayerUp();

		fireBoardLayerMovedUp(up);

		return true;
	}

	/**
	 * Moves the layer down to the specified index if possible.
	 *
	 * @param index
	 *            lower index
	 * @return was it moved
	 */
	public boolean moveLayerDown(int index) {
		// Lowest possible layer, can't be move down!
		if (index == 0) {
			return false;
		}

		BoardLayer down = layers.get(index);
		BoardLayer up = layers.get(index - 1);
		layers.set(index - 1, down);
		layers.set(index, up);

		down.moveLayerDown();
		up.moveLayerUp();

		fireBoardLayerMovedDown(down);

		return true;
	}

	/**
	 * Clones the layer at the specified index.
	 *
	 * @param index
	 *            clone layer at index
	 */
	public void cloneLayer(int index) {
		try {
			Iterator iterator = layers.listIterator(index + 1);

			while (iterator.hasNext()) {
				BoardLayer layer = (BoardLayer) iterator.next();
				layer.moveLayerUp();
			}

			BoardLayer clone = (BoardLayer) layers.get(index).clone();
			layers.add(index + 1, clone);

			fireBoardLayerCloned(clone);
		} catch (CloneNotSupportedException e) {
			System.out.println(e.toString());
		}

	}

	/**
	 * Deletes the layer at the specified index.
	 *
	 * @param index
	 *            delete layer at index
	 */
	public void deleteLayer(int index) {
		Iterator iterator = layers.listIterator(index + 1);

		while (iterator.hasNext()) {
			BoardLayer layer = (BoardLayer) iterator.next();
			layer.moveLayerDown();
		}

		BoardLayer removedLayer = layers.get(index);
		layers.remove(index);

		fireBoardLayerDeleted(removedLayer);
	}

	/**
	 * Invoked when the required TileSets exist in the Cache. When first loaded
	 * Boards will have blank Tile rasters.
	 */
	public void loadTiles() {
		for (BoardLayer boardLayer : layers) {
			int count = width * height;
			int x = 0;
			int y = 0;
			Tile[][] tiles = boardLayer.getTiles();
			for (int j = 0; j < count; j++) {
				Tile tile = tiles[x][y];

				if (tile.getTileSet() != null) {
					// When first loaded they'll only have the name.
					tile.setTileSet(TileSetCache.getTileSet(tile.getTileSet()
							.getName()));
					tiles[x][y] = tile.getTileSet().getTile(tile.getIndex());
				}

				x++;
				if (x == width) {
					x = 0;
					y++;
					if (y == height) {
						break;
					}
				}
			}
		}
	}

}
