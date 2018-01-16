/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import javax.swing.JPanel;

import org.rpgwizard.common.assets.listeners.BoardChangeListener;
import org.rpgwizard.common.assets.events.BoardChangedEvent;
import org.rpgwizard.common.assets.BoardLayer;
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.assets.TilePixelOutOfRangeException;
import org.rpgwizard.editor.editors.BoardEditor;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 * This class is an Abstract model for the visual representation of a
 * RPG-Toolkit board file. It deals with initializing most of the members needed
 * by a concrete class, it also provides some of the core functionality such as
 * layer management. It does NOT perform the actual rendering of the board it
 * only defines abstract methods that a sub class can use, this is due to the
 * fact that the Toolkit supports both flat 2D boards and isometric boards.
 *
 * @author Joshua Michael Daly
 */
public abstract class AbstractBoardView extends JPanel
		implements
			MultiLayerContainer,
			BoardChangeListener {

	// Constants
	private static final int ZOOM_NORMALSIZE = 5;
	private static final Color DEFAULT_GRID_COLOR = Color.BLACK;
	private static final Color DEFAULT_BACKGROUND_COLOR = new Color(64, 64, 64);
	private static final double[] ZOOM_LEVELS = {0.0625, 0.125, 0.25, 0.5,
			0.75, 1.0, 1.5, 2.0, 3.0, 4.0};

	// Layer properties
	private BoardLayerView currentSelectedLayer;
	private ArrayList<BoardLayerView> layers;
	private Rectangle bounds; // in tiles

	// Zooming properties
	private int zoomLevel;
	private double zoom;

	/**
	 * Used to scale the board view.
	 */
	protected AffineTransform affineTransform;

	/**
	 * The board model that this view represents.
	 */
	protected Board board;

	/**
	 * A BufferedImage which this view can be drawn to.
	 */
	protected BufferedImage bufferedImage;

	/**
	 * The parent BoardEditor for this BoardView.
	 */
	protected BoardEditor boardEditor;

	// Grid properties.
	/**
	 * A boolean value that indicates whether the grid is visible or not.
	 */
	private boolean antialiasGrid;
	private Color gridColor;
	private int gridOpacity;

	protected Image startPositionImage;

	/**
	 * Default constructor.
	 */
	public AbstractBoardView() {

	}

	/**
	 * This constructor is used when creating a new board.
	 *
	 * @param boardEditor
	 *            The parent BoardEditor for this view.
	 */
	public AbstractBoardView(BoardEditor boardEditor) {
		board = new Board(null);
		this.boardEditor = boardEditor;
		init();
	}

	/**
	 * This constructor is used when opening an existing board.
	 *
	 * @param board
	 *            The Toolkit board that this view represents.
	 * @param boardEditor
	 *            The parent BoardEditor for this view.
	 */
	public AbstractBoardView(BoardEditor boardEditor, Board board) {
		this.board = board;
		this.boardEditor = boardEditor;
		init();
	}

	/**
	 * Gets the default color for the grid.
	 *
	 * @return The color.
	 */
	public Color getDefaultGridColor() {
		return DEFAULT_GRID_COLOR;
	}

	/**
	 * Gets the default background color.
	 *
	 * @return The color.
	 */
	public Color getDefaultBackgroudColor() {
		return DEFAULT_BACKGROUND_COLOR;
	}

	/**
	 * Sets the current BoardEditor for this board view.
	 *
	 * @param boardEditor
	 *            The parent BoardEditor.
	 */
	public void setBoardEditor(BoardEditor boardEditor) {
		this.boardEditor = boardEditor;
	}

	/**
	 * Gets the board associated with this view.
	 *
	 * @return The board model.
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Sets the board associated with this view.
	 *
	 * @param board
	 *            The board model.
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * Gets the grid color.
	 *
	 * @return The color of the grid.
	 */
	public Color getGridColor() {
		return gridColor;
	}

	/**
	 * Sets the grid color
	 *
	 * @param color
	 *            The color to use.
	 */
	public void setGridColor(Color color) {
		gridColor = color;
		repaint();
	}

	/**
	 * Gets the opacity of the grid.
	 *
	 * @return The opacity, a whole number 100%, 80%, 55% etc.
	 */
	public int getGridOpacity() {
		return gridOpacity;
	}

	/**
	 * Sets the opacity of the grid.
	 *
	 * @param opacity
	 *            The opacity to use, a whole number 100%, 80%, 55% etc.
	 */
	public void setGridOpacity(int opacity) {
		gridOpacity = opacity;
		repaint();
	}

	/**
	 * Is the grid anti-aliased?
	 *
	 * @return Is the grid being anti-aliased?
	 */
	public boolean isAntialiasGrid() {
		return antialiasGrid;
	}

	/**
	 * Sets the grid to be anti-aliased or not.
	 *
	 * @param isAntialias
	 *            Will the grid be anti-aliased?
	 */
	public void setAntialiasGrid(boolean isAntialias) {
		antialiasGrid = isAntialias;
		repaint();
	}

	/**
	 * Gets the current zoom.
	 *
	 * @return The zoom factor i.e. 0.5 = 50%, 2.0 = 200% etc.
	 */
	public double getZoom() {
		return zoom;
	}

	/**
	 * Sets the current zoom factor.
	 *
	 * @param zoom
	 *            The amount to zoom by.
	 */
	public void setZoom(double zoom) {
		if (zoom > 0) {
			this.zoom = zoom;
			rescale();
		}
	}

	/**
	 * Gets the current zoom level.
	 *
	 * @return The current zoom factor.
	 */
	public int getZoomLevel() {
		return zoomLevel;
	}

	/**
	 * Sets the zoom level.
	 *
	 * @param index
	 *            The zoom level to use, the index is used to access the zoom
	 *            level in an array.
	 */
	public void setZoomLevel(int index) {
		if (index >= 0 && index < ZOOM_LEVELS.length) {
			zoomLevel = index;
			setZoom(ZOOM_LEVELS[index]);
		}
	}

	/**
	 * Returns the total number of layers.
	 *
	 * @return The size of the layer ArrayList.
	 */
	@Override
	public int getTotalLayers() {
		return layers.size();
	}

	/**
	 * Returns a <code>Rectangle</code> representing the maximum bounds in
	 * tiles.
	 *
	 * @return A new rectangle containing the maximum bounds of this container.
	 */
	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * Returns a <code>Rectangle</code> representing the maximum bounds in
	 * pixels.
	 *
	 * @return A new rectangle containing the maximum bounds of this container.
	 */
	@Override
	public Rectangle getPixelBounds() {
		Rectangle pixelBounds = new Rectangle();
		pixelBounds.width = bounds.width * board.getTileWidth();
		pixelBounds.height = bounds.height * board.getTileHeight();

		return pixelBounds;
	}

	/**
	 * Returns the layer at the specified ArrayList index.
	 *
	 * @param index
	 *            The index of the layer to return.
	 * @return The layer at the specified index, or null if the index is out of
	 *         bounds.
	 */
	@Override
	public BoardLayerView getLayer(int index) {
		if (index >= layers.size() || index < 0) {
			return null;
		}

		return layers.get(index);
	}

	/**
	 * Sets a layer based on an index value.
	 *
	 * @param index
	 *            The position in the layers ArrayList to insert the new layer.
	 * @param layer
	 *            The layer we want to add.
	 */
	@Override
	public void setLayer(int index, BoardLayerView layer) {
		layers.set(index, layer);
	}

	/**
	 * Returns the layer ArrayList.
	 *
	 * @return The layer ArrayList.
	 */
	@Override
	public ArrayList<BoardLayerView> getLayerArrayList() {
		return layers;
	}

	/**
	 * Sets the layer ArrayList to the passed ArrayList.
	 *
	 * @param layers
	 *            The new set of layers.
	 */
	@Override
	public void setLayerArrayList(ArrayList<BoardLayerView> layers) {
		this.layers = layers;
	}

	/**
	 * Gets a listIterator of all layers.
	 *
	 * @return A listIterator.
	 */
	@Override
	public ListIterator<BoardLayerView> getLayers() {
		return layers.listIterator();
	}

	/**
	 *
	 * @return
	 */
	public BoardLayerView getCurrentSelectedLayer() {
		return currentSelectedLayer;
	}

	/**
	 *
	 * @param layer
	 */
	public void setCurrentSeletedLayer(BoardLayerView layer) {
		currentSelectedLayer = layer;
	}

	/**
	 * Converts pixel coordinates to tile coordinates. The returned coordinates
	 * are at least 0 and adjusted with respect to the number of tiles per row
	 * and the number of rows.
	 *
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return tile coordinates
	 */
	public Point getTileCoordinates(int x, int y) {
		int tileWidth = board.getTileWidth();
		int tileHeight = board.getTileHeight();

		int tileX = Math.round(x / tileWidth);
		int tileY = Math.round(y / tileHeight);

		return new Point(tileX, tileY);
	}

	/**
	 *
	 * @return
	 */
	public BoardEditor getBoardEditor() {
		return boardEditor;
	}

	/**
	 * Checks that the x and y tile based coordinates are within the bounds of
	 * the board.
	 *
	 * @param x
	 *            tile coordinate
	 * @param y
	 *            tile coordinate
	 * @return
	 */
	public boolean checkTileInBounds(int x, int y) {
		if (x >= board.getWidth() || y >= board.getHeight()) {
			return false;
		}
		return !(x < 0 || y < 0);
	}

	/**
	 * A concrete BoardView will implement its own layer drawing code here.
	 *
	 * @throws TilePixelOutOfRangeException
	 *             Thrown if a tiles pixel value is out of the allowed range.
	 */
	protected abstract void paintBoard() throws TilePixelOutOfRangeException;

	/**
	 * A concrete BoardView will implement its own layer drawing code here.
	 *
	 * @param g
	 *            The graphics context to draw on.
	 */
	protected abstract void paintImages(Graphics2D g);

	/**
	 * A concrete BoardView will implement its own layer drawing code here.
	 *
	 * @param g
	 *            The graphics context to draw on.
	 */
	protected abstract void paintLayers(Graphics2D g);

	/**
	 * A concrete BoardView will implement its own grid drawing code here.
	 *
	 * @param g
	 *            The graphics context to draw on.
	 */
	protected abstract void paintGrid(Graphics2D g);

	/**
	 * A concrete BoardView will implement its own vector drawing code here.
	 *
	 * @param g
	 *            The graphics context to draw on.
	 */
	protected abstract void paintVectors(Graphics2D g);

	/**
	 * A concrete BoardView will implement its own sprite drawing code here.
	 *
	 * @param g
	 *            The graphics context to draw on.
	 */
	protected abstract void paintSprites(Graphics2D g);

	/**
	 * A concrete BoardView will implement its own start location drawing code
	 * here.
	 *
	 * @param g
	 *            The graphics context to draw on.
	 */
	protected abstract void paintStartPostion(Graphics2D g);

	/**
	 * A concrete BoardView will implement its own coordinate drawing code here.
	 *
	 * @param g
	 *            The graphics context to draw on.
	 */
	protected abstract void paintCoordinates(Graphics2D g);

	/**
	 * A concrete BoardView will implement its own cursor drawing code here.
	 *
	 * @param g
	 *            The graphics context to draw on.
	 */
	protected abstract void paintCursor(Graphics2D g);

	/**
	 * A concrete BoardView will implement its own selection drawing code here.
	 *
	 * @param g
	 *            The graphics context to draw on.
	 */
	protected abstract void paintSelection(Graphics2D g);

	/**
	 * A concrete BoardView will implement its own brush preview code here.
	 *
	 * @param g
	 */
	protected abstract void paintBrushPreview(Graphics2D g);

	/**
	 * Zooms in on this board view.
	 *
	 * @return Is the zoom level less than the maximum zoom level?
	 */
	public boolean zoomIn() {
		if (zoomLevel < ZOOM_LEVELS.length - 1) {
			setZoomLevel(zoomLevel + 1);
			rescale();
		}

		return zoomLevel < ZOOM_LEVELS.length - 1;
	}

	/**
	 * Zooms out on this board view.
	 *
	 * @return Is the zoom greater than 0?
	 */
	public boolean zoomOut() {
		if (zoomLevel > 0) {
			setZoomLevel(zoomLevel - 1);
			rescale();
		}

		return zoomLevel > 0;
	}

	/**
	 * Changes the bounds of this container to include all layers completely.
	 */
	@Override
	public void fitBoundsToLayers() {
		int width = 0;
		int height = 0;

		Rectangle layerBounds = new Rectangle();

		for (int i = 0; i < layers.size(); i++) {
			getLayer(i).getBounds(layerBounds);

			if (width < layerBounds.width) {
				width = layerBounds.width;
			}

			if (height < layerBounds.height) {
				height = layerBounds.height;
			}
		}

		bounds.width = width;
		bounds.height = height;
	}

	/**
	 * Adds a layer to the board.
	 *
	 * @param layer
	 *            The {@link MapLayer} to add.
	 * @return The layer passed to the method.
	 */
	@Override
	public BoardLayerView addLayerView(BoardLayerView layer) {
		layers.add(layer);
		return layer;
	}

	/**
	 * Adds the BoardLayerView <code>l</code> after the MapLayer
	 * <code>after</code>.
	 *
	 * @param layer
	 *            The layer to add.
	 * @param after
	 *            Specifies the layer to add <code>l</code> after.
	 */
	@Override
	public void addLayerAfter(BoardLayerView layer, BoardLayerView after) {
		layers.add(layers.indexOf(after) + 1, layer);
	}

	/**
	 * Add a layer at the specified index, which should be within the valid
	 * range.
	 *
	 * @param index
	 *            The position at which to add the layer.
	 * @param layer
	 *            The layer to add.
	 */
	@Override
	public void addLayer(int index, BoardLayerView layer) {
		layers.add(index, layer);
	}

	/**
	 * Adds all the layers in a given java.util.Collection.
	 *
	 * @param layers
	 *            A collection of layers to add.
	 */
	@Override
	public void addAllLayers(Collection<BoardLayerView> layers) {
		layers.addAll(layers);
	}

	/**
	 * Removes the layer at the specified index. Layers above this layer will
	 * move down to fill the gap.
	 *
	 * @param index
	 *            The index of the layer to be removed.
	 * @return The layer that was removed from the list.
	 */
	@Override
	public BoardLayerView removeLayer(int index) {
		return layers.remove(index);
	}

	/**
	 * Moves the layer at <code>index</code> up one in the ArrayList.
	 *
	 * @param index
	 *            The index of the layer to swap up.
	 */
	@Override
	public void swapLayerUp(int index) {
		if (index + 1 == layers.size()) {
			throw new RuntimeException("Can't swap up when already at the top.");
		}

		BoardLayerView hold = layers.get(index + 1);
		BoardLayerView move = layers.get(index);
		layers.set(index + 1, move);
		layers.set(index, hold);
	}

	/**
	 * Moves the layer at <code>index</code> down one in the ArrayList.
	 *
	 * @param index
	 *            The index of the layer to swap down.
	 */
	@Override
	public void swapLayerDown(int index) {
		if (index - 1 < 0) {
			throw new RuntimeException(
					"Can't swap down when already at the bottom.");
		}

		BoardLayerView hold = layers.get(index - 1);
		BoardLayerView move = layers.get(index);
		layers.set(index - 1, move);
		layers.set(index, hold);
	}

	/**
	 * Determines whether the point (x,y) falls within the container.
	 *
	 * @param x
	 * @param y
	 * @return <code>true</code> if the point is within the plane,
	 *         <code>false</code> otherwise.
	 */
	@Override
	public boolean inBounds(int x, int y) {
		return x >= 0 && y >= 0 && x < bounds.width && y < bounds.height;
	}

	/**
	 * Return an iterator for the layers ArrayList.
	 *
	 * @return The iterator.
	 */
	@Override
	public Iterator<BoardLayerView> iterator() {
		return layers.iterator();
	}

	@Override
	public void boardChanged(BoardChangedEvent e) {
		repaint();
	}

	@Override
	public void boardLayerAdded(BoardChangedEvent e) {
		addLayerView(new BoardLayerView(e.getLayer()));
		repaint();
	}

	@Override
	public void boardLayerMovedUp(BoardChangedEvent e) {
		swapLayerUp(e.getLayer().getNumber() - 1);
		repaint();
	}

	@Override
	public void boardLayerMovedDown(BoardChangedEvent e) {
		swapLayerDown(e.getLayer().getNumber() + 1);
		repaint();
	}

	@Override
	public void boardLayerCloned(BoardChangedEvent e) {
		addLayer(e.getLayer().getNumber(), new BoardLayerView(e.getLayer()));
		repaint();
	}

	@Override
	public void boardLayerDeleted(BoardChangedEvent e) {
		removeLayer(e.getLayer().getNumber());
		repaint();
	}

	@Override
	public void boardSpriteAdded(BoardChangedEvent e) {
		repaint();
	}

	@Override
	public void boardSpriteRemoved(BoardChangedEvent e) {
		repaint();
	}

	@Override
	public void boardLayerImageAdded(BoardChangedEvent e) {
		repaint();
	}

	@Override
	public void boardLayerImageRemoved(BoardChangedEvent e) {
		repaint();
	}

	/**
	 * Initializes a BoardView, it sets most of the BoardView's members but it
	 * does not do anything with regard to the board model or parent editor.
	 */
	private void init() {
		board.addBoardChangeListener(this);

		layers = new ArrayList();
		bounds = new Rectangle();

		zoom = 1.0;
		zoomLevel = ZOOM_NORMALSIZE;
		affineTransform = new AffineTransform();

		loadTiles(board);
		setPreferredSize(new Dimension(
				(board.getWidth() * board.getTileWidth()),
				(board.getHeight() * board.getTileHeight())));

		bufferedImage = new BufferedImage(
				(board.getWidth() * board.getTileWidth()),
				(board.getHeight() * board.getTileHeight()),
				BufferedImage.TYPE_INT_ARGB);

		antialiasGrid = true;
		gridColor = DEFAULT_GRID_COLOR;
		gridOpacity = 100;

		startPositionImage = Icons.getSmallIcon("flag-checker").getImage();

		if (!layers.isEmpty()) {
			currentSelectedLayer = layers.get(0);
		}
	}

	/**
	 * Re-scales this board view based on the current zoom level.
	 */
	private void rescale() {
		affineTransform = AffineTransform.getScaleInstance(zoom, zoom);
		int width = (int) ((board.getWidth() * board.getTileWidth()) * zoom);
		int height = (int) ((board.getHeight() * board.getTileHeight()) * zoom);
		setPreferredSize(new Dimension(width, height));
		repaint();
	}

	/**
	 * Populates the TileSetCache for this board.
	 *
	 * @param board
	 *            The Toolkit board we want to load tiles for.
	 */
	private void loadTiles(Board board) {
		for (BoardLayer layer : board.getLayers()) {
			BoardLayerView layerView = new BoardLayerView(layer);
			addLayerView(layerView);
		}
	}

}
