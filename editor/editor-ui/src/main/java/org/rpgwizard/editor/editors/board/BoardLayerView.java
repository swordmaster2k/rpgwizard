/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.rpgwizard.common.assets.board.Board;
import org.rpgwizard.common.assets.tileset.Tile;
import org.rpgwizard.common.assets.tileset.TilePixelOutOfRangeException;
import org.rpgwizard.common.assets.board.BoardLayer;
import org.rpgwizard.common.assets.board.BoardLayerImage;
import org.rpgwizard.common.assets.board.BoardSprite;
import org.rpgwizard.common.assets.board.BoardVector;
import org.rpgwizard.common.assets.events.BoardChangedEvent;
import org.rpgwizard.editor.ui.resources.Icons;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class BoardLayerView {

    /**
     * Layer this view represents.
     */
    private BoardLayer layer;
    /**
     * A reference to to MultilayerContainer this layer belongs to.
     */
    private MultiLayerContainer parentContainer;
    /**
     * Bounds of the layer.
     */
    private Rectangle bounds;

    /**
     *
     *
     * @param layer
     */
    public BoardLayerView(BoardLayer layer) {
        this.layer = layer;
    }

    /**
     *
     * @return
     */
    public BoardLayer getLayer() {
        return layer;
    }

    /**
     *
     * @param layer
     */
    public void setLayer(BoardLayer layer) {
        this.layer = layer;
    }

    /**
     * Gets the layer width in tiles.
     *
     * @return Layer width in tiles.
     */
    public int getWidth() {
        return bounds.width;
    }

    /**
     * Sets the layer width in tiles.
     *
     * @param width
     *            New layer width in tiles.
     */
    public void setWidth(int width) {
        bounds.width = width;
    }

    /**
     * Gets the layer height in tiles.
     *
     * @return Layer height in tiles.
     */
    public int getHeight() {
        return bounds.height;
    }

    /**
     * Sets the layer height in tiles.
     *
     * @param height
     *            New layer height in tiles.
     */
    public void setHeight(int height) {
        bounds.height = height;
    }

    /**
     * Gets the layer bounds in tiles.
     *
     * @return The layer bounds in tiles
     */
    public Rectangle getBounds() {
        return new Rectangle(bounds);
    }

    /**
     * Gets the layer bounds in tiles to the given rectangle.
     *
     * @param rectangle
     *            The rectangle to which the layer bounds are assigned.
     */
    public void getBounds(Rectangle rectangle) {
        rectangle.setBounds(bounds);
    }

    /**
     * Sets the bounds (in tiles) to the specified Rectangle.
     *
     * @param bounds
     *            The bounds to set.
     */
    protected void setBounds(Rectangle bounds) {
        this.bounds = new Rectangle(bounds);
    }

    /**
     * Gets the layers current opacity which is a value between 0.0f and 1.0f.
     *
     * @return current layer opacity
     */
    public float getOpacity() {
        return this.layer.getOpacity();
    }

    /**
     * Sets layer opacity. If it is different from the previous value and the layer is visible, a BoardChangedEvent is
     * fired.
     *
     * @param opacity
     *            The new opacity for this layer.
     */
    public void setOpacity(float opacity) {
        if (this.layer.getOpacity() != opacity) {
            this.layer.setOpacity(opacity);

            if (isVisible() && layer != null) {
                final Board board = layer.getBoard();
                final BoardChangedEvent event = new BoardChangedEvent(board);
                event.setOpacityChanged(true);
                board.fireBoardChanged(event);
            }
        }
    }

    /**
     * Gets the current visibility of this layer.
     *
     * @return The visibility <code>true</code> or <code>false</code>.
     */
    public boolean isVisible() {
        return this.layer.isVisible();
    }

    /**
     * Sets the visibility of this board layer. If it changes from its current value, a BoardChangedEvent is fired
     * visibility.
     *
     * @param visible
     *            <code>true</code> to make the layer visible; <code>false</code> to make it invisible
     */
    public void setVisibility(boolean visible) {
        if (this.layer.isVisible() != visible) {
            this.layer.setVisible(visible);

            if (layer != null) {
                final Board board = layer.getBoard();
                final BoardChangedEvent event = new BoardChangedEvent(board);
                event.setLayerVisibilityToggled(true);
                board.fireBoardChanged(event);
            }
        }
    }

    /**
     * Sets the offset of this map layer. The offset is a distance by which to shift this layer from the origin of the
     * map.
     *
     * @param xOffset
     *            X offset in tiles.
     * @param yOffset
     *            Y offset in tiles.
     */
    public void setOffset(int xOffset, int yOffset) {
        bounds.x = xOffset;
        bounds.y = yOffset;
    }

    /**
     *
     *
     * @param locked
     */
    public void setLocked(boolean locked) {
        layer.setLocked(locked);
    }

    /**
     *
     *
     * @return
     */
    public MultiLayerContainer getParentContainer() {
        return parentContainer;
    }

    /**
     *
     *
     * @param parentContainer
     */
    public void setParentContainer(MultiLayerContainer parentContainer) {
        this.parentContainer = parentContainer;
    }

    public static BufferedImage getPlaceHolderImage() {
        return Icons.toBufferedImage(Icons.getLargeIcon("icons8-add-image-48"));
    }

    public static BufferedImage getPlaceHolderSprite() {
        return Icons.toBufferedImage(Icons.getLargeIcon("icons8-add-user-female-48"));
    }

    /**
     * Draws the tiles for this layer.
     *
     * @param g
     *            Graphics context to draw to.
     * @throws TilePixelOutOfRangeException
     *             Throws an exception if the tiles pixel value is out of the allowed range.
     */
    public void drawTiles(Graphics2D g) throws TilePixelOutOfRangeException {
        Board parentBoard = layer.getBoard();

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, layer.getOpacity()));

        for (int x = 0; x < parentBoard.getWidth(); x++) {
            for (int y = 0; y < parentBoard.getHeight(); y++) {
                if (layer.getTiles()[x][y] != null) {
                    Tile tile = layer.getTiles()[x][y];

                    g.drawImage(tile.getTileAsImage(), (x * layer.getBoard().getTileWidth()),
                            (y * layer.getBoard().getTileHeight()), null);
                } else {
                    g.setColor(Color.white);
                }
            }
        }
    }

    /**
     * Draws the vectors for this layer.
     *
     * @param g
     *            The graphics context to draw to.
     */
    public void drawVectors(Graphics2D g) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, layer.getOpacity()));

        // Draw Vectors
        ArrayList<BoardVector> vectors = layer.getVectors();

        for (BoardVector vector : vectors) {
            if (vector.isSelected()) {
                g.setStroke(new BasicStroke(3.0f)); // Draw it thicker.
            }

            switch (vector.getType()) {
            case PASSABLE:
                g.setColor(Color.YELLOW);
                break;
            case SOLID:
                g.setColor(Color.RED);
                break;
            default:
            }

            drawVectorLines(g, vector);

            if (vector.isSelected()) {
                g.setStroke(new BasicStroke(1.0f)); // Return to normal stroke.
            }
        }
    }

    /**
     * Draws the images for this layer.
     *
     * @param g
     *            Graphics context to draw to.
     */
    public void drawImages(Graphics2D g) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, layer.getOpacity()));
        List<BoardLayerImage> images = layer.getImages();
        images.forEach((layerImage) -> {
            BufferedImage image = layerImage.getImage();
            if (image == null) {
                image = getPlaceHolderImage();
            }
            int width = image.getWidth();
            int height = image.getHeight();
            int x = layerImage.getX();
            int y = layerImage.getY();
            g.drawImage(image, x, y, null);

            if (layerImage.isSelected()) {
                drawSelection(g, x, y, width, height);
            }
        });
    }

    /**
     *
     * @param g
     */
    public void drawSprites(Graphics2D g) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, layer.getOpacity()));

        List<BoardSprite> sprites = layer.getSprites();
        sprites.sort((BoardSprite a, BoardSprite b) -> a.getY() - b.getY());
        sprites.stream().forEach((sprite) -> {
            BufferedImage image = sprite.getSouthImage();
            if (image == null) {
                image = getPlaceHolderSprite();
            }

            int width = image.getWidth();
            int height = image.getHeight();
            int x = sprite.getX() - (width / 2);
            int y = sprite.getY() - (height / 2);
            g.drawImage(image, x, y, null);

            if (sprite.isSelected()) {
                drawSelection(g, x, y, width, height);
            }
        });
    }

    /**
     *
     *
     * @param g
     * @param vector
     */
    private void drawVectorLines(Graphics2D g, BoardVector vector) {
        // Draw lines from points 0 > 1 , 1 > 2, 2 > 3 etc..
        int count = vector.getPointCount();

        for (int i = 0; i < count - 1; i++) {
            int[] points = GuiHelper.ensureVectorVisible(layer.getBoard(), vector.getPointX(i), vector.getPointY(i),
                    vector.getPointX(i + 1), vector.getPointY(i + 1));
            g.drawLine(points[0], points[1], points[2], points[3]);
        }

        if (vector.isClosed()) {
            // Draw the final lines
            int[] points = GuiHelper.ensureVectorVisible(layer.getBoard(), vector.getPointX(count - 1),
                    vector.getPointY(count - 1), vector.getPointX(0), vector.getPointY(0));
            g.drawLine(points[0], points[1], points[2], points[3]);
        }
    }

    private void drawSelection(Graphics2D g, int x, int y, int width, int height) {
        Dimension dimensions = layer.getBoard().getBoardPixelDimensions();
        int boardWidth = dimensions.width;
        int boardHeight = dimensions.height;
        if (width >= boardWidth) {
            width = boardWidth - 1;
        }
        if (height >= boardHeight) {
            height = boardHeight - 1;
        }

        Composite resetComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.2f));
        g.setColor(new Color(100, 100, 255));
        g.fillRect(x, y, width, height);
        g.setComposite(resetComposite);
        g.setColor(Color.BLUE);
        g.drawRect(x, y, width, height);
    }

}
