/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.editors.board;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import net.rpgtoolkit.common.assets.Tile;
import net.rpgtoolkit.editor.ui.AssetEditorWindow;

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
    public void drawPreview(Graphics2D g2d, AbstractBoardView view) {

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
        BoardLayerView layer = affectedContainer.getLayer(currentLayer);

        if (layer == null) {
            return null;
        }

        int layerWidth = layer.getLayer().getBoard().getWidth();
        int layerHeight = layer.getLayer().getBoard().getHeight();

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

                layer.getLayer().setTileAt(offsetX, offsetY,
                        tiles[offsetX - centerX][offsetY - centerY]);
            }
        }

        return new Rectangle(centerX, centerY,
                bounds.width, bounds.height);
    }

    /**
     *
     */
    public void resize() {
        bounds = new Rectangle(tiles.length, tiles[0].length);
    }

    @Override
    public void doMouseButton1Pressed(Point point, AssetEditorWindow editor) {
        
    }

    @Override
    public void doMouseButton2Pressed(Point point, AssetEditorWindow editor) {
        
    }

    @Override
    public void doMouseButton3Pressed(Point point, AssetEditorWindow editor) {
        
    }

    @Override
    public void doMouseButton1Dragged(Point point, Point origin, AssetEditorWindow editor) {
    }

    @Override
    public boolean isPixelBased() {
        return false;
    }

}
