/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map;

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
import org.rpgwizard.common.assets.AbstractPolygon;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.assets.tileset.Tile;
import org.rpgwizard.common.assets.tileset.TilePixelOutOfRangeException;
import org.rpgwizard.common.assets.map.MapLayer;
import org.rpgwizard.common.assets.map.MapSprite;
import org.rpgwizard.common.assets.events.MapChangedEvent;
import org.rpgwizard.editor.ui.resources.Icons;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class MapLayerView {

    /**
     * Layer this view represents.
     */
    private MapLayer layer;
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
    public MapLayerView(MapLayer layer) {
        this.layer = layer;
    }

    /**
     *
     * @return
     */
    public MapLayer getLayer() {
        return layer;
    }

    /**
     *
     * @param layer
     */
    public void setLayer(MapLayer layer) {
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
     * Sets layer opacity. If it is different from the previous value and the layer is visible, a MapChangedEvent is
     * fired.
     *
     * @param opacity
     *            The new opacity for this layer.
     */
    public void setOpacity(float opacity) {
        if (this.layer.getOpacity() != opacity) {
            this.layer.setOpacity(opacity);

            if (isVisible() && layer != null) {
                final Map map = layer.getMap();
                final MapChangedEvent event = new MapChangedEvent(map);
                event.setOpacityChanged(true);
                map.fireMapChanged(event);
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
     * Sets the visibility of this map layer. If it changes from its current value, a MapChangedEvent is fired
     * visibility.
     *
     * @param visible
     *            <code>true</code> to make the layer visible; <code>false</code> to make it invisible
     */
    public void setVisibility(boolean visible) {
        if (this.layer.isVisible() != visible) {
            this.layer.setVisible(visible);

            if (layer != null) {
                final Map map = layer.getMap();
                final MapChangedEvent event = new MapChangedEvent(map);
                event.setLayerVisibilityToggled(true);
                map.fireMapChanged(event);
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
        Map parentMap = layer.getMap();

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, layer.getOpacity()));

        for (int x = 0; x < parentMap.getWidth(); x++) {
            for (int y = 0; y < parentMap.getHeight(); y++) {
                if (layer.getLoadedTiles()[x][y] != null) {
                    Tile tile = layer.getLoadedTiles()[x][y];

                    g.drawImage(tile.getTileAsImage(), (x * layer.getMap().getTileWidth()),
                            (y * layer.getMap().getTileHeight()), null);
                } else {
                    g.setColor(Color.white);
                }
            }
        }
    }

    public void drawPolygons(Graphics2D g) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, layer.getOpacity()));
        layer.getColliders().values().forEach(collider -> {
            drawPolygon(g, collider);
        });
        layer.getTriggers().values().forEach(trigger -> {
            drawPolygon(g, trigger);
        });
    }

    /**
     * Draws the images for this layer.
     *
     * @param g
     *            Graphics context to draw to.
     */
    public void drawImages(Graphics2D g) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, layer.getOpacity()));
        layer.getImages().values().forEach((layerImage) -> {
            BufferedImage image = layerImage.getBufferedImage();
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

        List<MapSprite> sprites = new ArrayList<>(layer.getSprites().values());
        sprites.sort((MapSprite a, MapSprite b) -> a.getStartLocation().getY() - b.getStartLocation().getY());
        sprites.stream().forEach((sprite) -> {
            BufferedImage image = sprite.getSouthImage();
            if (image == null) {
                image = getPlaceHolderSprite();
            }

            int width = image.getWidth();
            int height = image.getHeight();
            int x = sprite.getStartLocation().getX() - (width / 2);
            int y = sprite.getStartLocation().getY() - (height / 2);
            g.drawImage(image, x, y, null);

            if (sprite.isSelected()) {
                drawSelection(g, x, y, width, height);
            }
        });
    }

    private void drawPolygon(Graphics2D g, AbstractPolygon polygon) {
        if (polygon.isSelected()) {
            g.setStroke(new BasicStroke(3.0f)); // Draw it thicker.
        }
        g.setColor(polygon instanceof Collider ? Color.YELLOW : Color.GREEN);
        drawPolygonLines(g, polygon);
        if (polygon.isSelected()) {
            g.setStroke(new BasicStroke(1.0f)); // Return to normal stroke.
        }
    }

    // REFACTOR: FIX ME
    /**
     *
     *
     * @param g
     * @param vector
     */
    private void drawPolygonLines(Graphics2D g, AbstractPolygon polygon) {
        // Draw lines from points 0 > 1 , 1 > 2, 2 > 3 etc..
        int count = polygon.getPointCount();

        for (int i = 0; i < count - 1; i++) {
            int[] points = GuiHelper.ensureVisible(layer.getMap(), polygon.getPointX(i), polygon.getPointY(i),
                    polygon.getPointX(i + 1), polygon.getPointY(i + 1));
            g.drawLine(points[0], points[1], points[2], points[3]);
        }

        // Draw the final lines
        int[] points = GuiHelper.ensureVisible(layer.getMap(), polygon.getPointX(count - 1),
                polygon.getPointY(count - 1), polygon.getPointX(0), polygon.getPointY(0));
        g.drawLine(points[0], points[1], points[2], points[3]);
    }

    private void drawSelection(Graphics2D g, int x, int y, int width, int height) {
        Dimension dimensions = layer.getMap().getMapPixelDimensions();
        int mapWidth = dimensions.width;
        int mapHeight = dimensions.height;
        if (width >= mapWidth) {
            width = mapWidth - 1;
        }
        if (height >= mapHeight) {
            height = mapHeight - 1;
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
