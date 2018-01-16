/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board;

import org.rpgwizard.editor.editors.board.brush.AbstractBrush;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.assets.TilePixelOutOfRangeException;
import org.rpgwizard.editor.editors.BoardEditor;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.utilities.GuiHelper;
import org.rpgwizard.editor.utilities.TransparentDrawer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A concrete class for drawing 2D RPG-Toolkit Boards, this is the view
 * component. It defines the actual code behind its abstract super classes
 * drawing routines. It handles the drawing of individual layers, vectors, tile
 * based coordinates, and the grid.
 *
 * TBD: Create a grid drawing class and pull it out of here, a generic grid
 * drawer can then be used for this and the tile set viewer.
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 * @version 0.1
 */
public final class BoardView2D extends AbstractBoardView {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BoardView2D.class);

	/**
	 * Default constructor.
	 */
	public BoardView2D() {

	}

	/**
	 * This constructor is used when creating a new board.
	 *
	 * @param boardEditor
	 *            The parent BoardEditor for this view.
	 */
	public BoardView2D(BoardEditor boardEditor) {
		super(boardEditor);
	}

	/**
	 * This constructor is used when opening an existing board.
	 *
	 * @param board
	 *            The Toolkit board that this view represents.
	 * @param boardEditor
	 *            The parent BoardEditor for this view.
	 */
	public BoardView2D(BoardEditor boardEditor, Board board) {
		super(boardEditor, board);
	}

	/**
	 * Overrides the default paintComponent method by first making a call to its
	 * super class paintComponent method and then performs its own custom
	 * drawing routines.
	 *
	 * @param g
	 *            The graphics context to draw to.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.transform(affineTransform);

		try {
			paintBoard();
		} catch (TilePixelOutOfRangeException e) {

		}

		if (bufferedImage != null) {
			g.drawImage(bufferedImage, 0, 0, null);
		}

		if (MainWindow.getInstance().isShowCoordinates()) {
			paintCoordinates(g2d);
		}

		g.dispose();
		g2d.dispose();
	}

	/**
	 * Paints the board to the screen using a BufferedImage, it calls multiple
	 * sub methods which each draw part of the board (if they are set to).
	 *
	 * @throws TilePixelOutOfRangeException
	 *             Thrown if a tiles pixel value is out of the allowed range.
	 */
	@Override
	protected void paintBoard() throws TilePixelOutOfRangeException {
		Graphics2D g = bufferedImage.createGraphics();

		// Draw background first.
		TransparentDrawer.drawTransparentBackground(g,
				(board.getWidth() * board.getTileWidth()),
				(board.getHeight() * board.getTileHeight()));

		paintLayers(g);
		paintImages(g);
		paintSprites(g);
		paintStartPostion(g);

		// Reset an opcaity changes in the layers.
		g.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_ATOP, 1.0f));

		if (MainWindow.getInstance().isShowGrid()) {
			paintGrid(g);
		}

		if (boardEditor.getSelection() != null) {
			paintSelection(g);
		}

		paintCursor(g);

		if (MainWindow.getInstance().isShowVectors()) {
			paintVectors(g);
		}

		paintBrushPreview(g);
	}

	/**
	 * Handles the drawing of a layer's images to the graphics context. It
	 * cycles through each layer and calls that layers drawTiles(g) method.
	 *
	 * @param g
	 *            The graphics context to draw on.
	 */
	@Override
	protected void paintImages(Graphics2D g) {
		ArrayList<BoardLayerView> layers = getLayerArrayList();

		for (BoardLayerView layer : layers) {
			if (layer.isIsVisible()) {
				layer.drawImages(g);
			}
		}
	}

	/**
	 * Handles the drawing of a layer's tiles to the graphics context. It cycles
	 * through each layer and calls that layers drawTiles(g) method.
	 *
	 * @param g
	 *            The graphics context to draw on.
	 */
	@Override
	protected void paintLayers(Graphics2D g) {
		ArrayList<BoardLayerView> layers = getLayerArrayList();

		for (BoardLayerView layer : layers) {
			if (layer.isVisible()) {
				try {
					layer.drawTiles(g);
				} catch (TilePixelOutOfRangeException ex) {
					LOGGER.error("Failed to paint tiles on layer=[{}]", layer,
							ex);
				}
			}
		}
	}

	/**
	 * Handles the drawing of the grid on the graphics context. It draws a grid
	 * based on the boards width and height in tiles.
	 *
	 * IMPROVEMENT: Move this functionality to an external class named
	 * "GridDrawer" this would save repeating the code on the TileSet viewer and
	 * potentially elsewhere.
	 *
	 * @param g
	 *            The graphics context to draw too.
	 */
	@Override
	protected void paintGrid(Graphics2D g) {
		if (board.getTileWidth() <= 0 || board.getTileHeight() <= 0) {
			return;
		}

		// Determine lines to draw from clipping rectangle
		Rectangle clipRectangle = new Rectangle(bufferedImage.getWidth(),
				bufferedImage.getHeight());
		GuiHelper.drawGrid(g, board.getTileWidth(), board.getTileHeight(),
				clipRectangle);
	}

	/**
	 * Handles the drawing of each layers set of vectors and draws them to the
	 * graphics context. It cycles through each layer and calls that layers
	 * drawVectors(g) method.
	 *
	 * @param g
	 *            The graphics context to draw on.
	 */
	@Override
	protected void paintVectors(Graphics2D g) {
		ArrayList<BoardLayerView> layers = getLayerArrayList();

		for (BoardLayerView layer : layers) {
			if (layer.isVisible()) {
				layer.drawVectors(g);
			}
		}
	}

	/**
	 *
	 * @param g
	 */
	@Override
	protected void paintSprites(Graphics2D g) {
		ArrayList<BoardLayerView> layers = getLayerArrayList();

		for (BoardLayerView layer : layers) {
			if (layer.isIsVisible()) {
				layer.drawSprites(g);
			}
		}
	}

	/**
	 *
	 *
	 * @param g
	 */
	@Override
	protected void paintStartPostion(Graphics2D g) {
		int x = board.getStartingPositionX()
				- (startPositionImage.getWidth(this) / 2);
		int y = board.getStartingPositionY()
				- (startPositionImage.getHeight(this) / 2);

		int startingLayer = board.getStartingLayer();
		ArrayList<BoardLayerView> layers = getLayerArrayList();
		for (BoardLayerView layer : layers) {
			if (layer.isIsVisible()) {
				if (layer.getLayer().getNumber() == startingLayer) {
					g.drawImage(startPositionImage, x, y, this);
				}
			}
		}
	}

	/**
	 * Handles the drawing of the coordinates on the graphics context. It draws
	 * a coordinates based on the boards width and height in tiles.
	 *
	 * BUG: The coordinates are being effected by the boards scaling factors,
	 * correct
	 *
	 * IMPROVEMENT: Consider splitting this method up into smaller pieces to
	 * make IT more understandable.
	 *
	 * @param g
	 *            The graphics context to draw on.
	 */
	@Override
	protected void paintCoordinates(Graphics2D g) {
		Dimension tileSize = new Dimension(board.getTileWidth(),
				board.getTileHeight());

		if (tileSize.width <= 0 || tileSize.height <= 0) {
			return;
		}

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Determine tile size and offset
		Font font = new Font("SansSerif", Font.PLAIN, tileSize.height / 4);
		g.setFont(font);
		FontRenderContext fontRenderContext = g.getFontRenderContext();

		g.setColor(Color.WHITE);

		// Determine area to draw from clipping rectangle
		Rectangle clipRectangle = new Rectangle(bufferedImage.getWidth(),
				bufferedImage.getHeight());
		int startX = (clipRectangle.x / tileSize.width);
		int startY = clipRectangle.y / tileSize.height;
		int endX = (clipRectangle.x + clipRectangle.width) / tileSize.width;
		int endY = (clipRectangle.y + clipRectangle.height) / tileSize.height;

		// Draw the coordinates
		int gy = startY * tileSize.height;

		for (int y = startY; y < endY; y++) {
			int gx = startX * tileSize.width;

			for (int x = startX; x < endX; x++) {
				String coordinates = "(" + x + "," + y + ")";
				Rectangle2D textSize = font.getStringBounds(coordinates,
						fontRenderContext);

				int fx = gx
						+ (int) ((tileSize.width - textSize.getWidth()) / 2);
				int fy = gy
						+ (int) ((tileSize.height + textSize.getHeight()) / 2);

				g.drawString(coordinates, fx, fy);
				gx += tileSize.width;
			}

			gy += tileSize.height;
		}
	}

	/**
	 *
	 *
	 * @param g
	 */
	@Override
	protected void paintSelection(Graphics2D g) {
		int tileWidth = board.getTileWidth();
		int tileHeight = board.getTileHeight();

		Rectangle selection = boardEditor.getSelection();

		g.setColor(new Color(100, 100, 255));
		g.drawRect(selection.x * tileWidth, selection.y * tileHeight,
				(selection.width + 1) * tileWidth, (selection.height + 1)
						* tileHeight);
		g.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_ATOP, 0.2f));
		g.fillRect(selection.x * tileWidth + 1, selection.y * tileHeight + 1,
				(selection.width + 1) * tileWidth - 1, (selection.height + 1)
						* tileHeight - 1);
	}

	/**
	 *
	 *
	 * @param g
	 */
	@Override
	protected void paintCursor(Graphics2D g) {
		AbstractBrush brush = MainWindow.getInstance().getCurrentBrush();
		boolean snap = MainWindow.getInstance().isSnapToGrid();
		Rectangle cursor = brush.getBounds();

		Point selection;
		int widthMultiplier = board.getTileWidth();
		int heightMultiplier = board.getTileHeight();
		int centerX, centerY;
		if (brush.isPixelBased() && !snap) {
			selection = boardEditor.getCursorLocation();
			centerX = selection.x - widthMultiplier / 2;
			centerY = selection.y - heightMultiplier / 2;
		} else {
			selection = boardEditor.getCursorTileLocation();
			centerX = (selection.x * widthMultiplier)
					- (((int) cursor.getWidth() / 2) * widthMultiplier);
			centerY = (selection.y * heightMultiplier)
					- (((int) cursor.getHeight() / 2) * heightMultiplier);
		}

		g.setColor(new Color(100, 100, 255));
		g.drawRect(centerX, centerY, ((int) cursor.getWidth())
				* widthMultiplier, ((int) cursor.getHeight())
				* heightMultiplier);
		g.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_ATOP, 0.2f));
		g.fillRect(centerX, centerY, ((int) cursor.getWidth())
				* widthMultiplier, ((int) cursor.getHeight())
				* heightMultiplier);
	}

	@Override
	protected void paintBrushPreview(Graphics2D g) {
		MainWindow.getInstance().getCurrentBrush().drawPreview(g, this);
	}

}
