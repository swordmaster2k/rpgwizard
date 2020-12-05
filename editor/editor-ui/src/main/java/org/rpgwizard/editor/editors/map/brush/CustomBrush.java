/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.brush;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import org.rpgwizard.common.assets.tileset.Tile;
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.editors.map.AbstractMapView;
import org.rpgwizard.editor.editors.map.MapLayerView;
import org.rpgwizard.editor.editors.map.MultiLayerContainer;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class CustomBrush extends AbstractBrush {

    /**
     *
     */
    protected Rectangle bounds;

    /**
     *
     */
    protected Tile[][] tiles;

    /**
     *
     *
     * @param tiles
     */
    public CustomBrush(Tile[][] tiles) {
        this.tiles = tiles;
        bounds = new Rectangle(tiles.length, tiles[0].length);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public Shape getShape() {
        return getBounds();
    }

    /**
     *
     *
     * @return
     */
    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     *
     *
     * @param rectangle
     */
    public void setBounds(Rectangle rectangle) {
        bounds = rectangle;
    }

    /**
     *
     *
     * @return
     */
    public Tile[][] getTiles() {
        return tiles;
    }

    /**
     *
     *
     * @param tiles
     */
    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
        resize();
    }

    /**
     *
     * @param g2d
     * @param view
     */
    @Override
    public void drawPreview(Graphics2D g2d, AbstractMapView view) {

    }

    /**
     *
     *
     * @param brush
     * @return
     */
    @Override
    public boolean equals(Brush brush) {
        if (brush instanceof CustomBrush) {
            if (brush == this) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     *
     * @param container
     * @param layer
     */
    @Override
    public void startPaint(MultiLayerContainer container, int layer) {
        super.startPaint(container, layer);
    }

    /**
     *
     *
     * @param x
     * @param y
     * @param selection
     * @return
     * @throws Exception
     */
    @Override
    public Rectangle doPaint(int x, int y, Rectangle selection) throws Exception {
        MapLayerView layer = affectedContainer.getLayer(currentLayer);

        if (layer == null) {
            return null;
        }

        int layerWidth = layer.getLayer().getMap().getWidth();
        int layerHeight = layer.getLayer().getMap().getHeight();

        int centerX = x - bounds.width / 2;
        int centerY = y - bounds.height / 2;

        super.doPaint(x, y, selection);

        // TODO: Some small inefficiencies in this with regard to the ifs.
        for (int offsetY = centerY; offsetY < centerY + bounds.height; offsetY++) {
            if (offsetY < 0) {
                continue;
            } else if (offsetY == layerHeight) {
                break;
            }

            for (int offsetX = centerX; offsetX < centerX + bounds.width; offsetX++) {
                if (offsetX < 0) {
                    continue;
                } else if (offsetX == layerWidth) {
                    break;
                }

                boolean tileEffected = layer.getLayer().pourTileAt(offsetX, offsetY,
                        tiles[offsetX - centerX][offsetY - centerY]);
                if (!changedEntity && tileEffected) {
                    changedEntity = true;
                }
            }
        }
        return new Rectangle(centerX, centerY, bounds.width, bounds.height);
    }

    /**
     *
     */
    public void resize() {
        bounds = new Rectangle(tiles.length, tiles[0].length);
    }

    @Override
    public void doMouseButton1Pressed(Point point, AbstractAssetEditorWindow editor) {
        MapEditor mapEditor = (MapEditor) editor;
        mapEditor.doPaint(this, point, null);
        if (changedEntity) {
            mapEditor.getMap().fireMapChanged();
        }
    }

    @Override
    public void doMouseButton2Pressed(Point point, AbstractAssetEditorWindow editor) {

    }

    @Override
    public void doMouseButton3Pressed(Point point, AbstractAssetEditorWindow editor) {

    }

    @Override
    public boolean doMouseButton1Dragged(Point point, Point origin, AbstractAssetEditorWindow editor) {
        if (editor instanceof MapEditor) {
            ((MapEditor) editor).doPaint(this, point, null);
            return changedEntity;
        }
        return false;
    }

    @Override
    public boolean doMouseButton3Dragged(Point point, Point origin, AbstractAssetEditorWindow editor) {
        return false;
    }

    @Override
    public boolean isPixelBased() {
        return false;
    }

}
