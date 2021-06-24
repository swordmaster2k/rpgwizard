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
import java.util.Stack;
import lombok.Getter;
import lombok.Setter;
import org.rpgwizard.common.assets.tileset.Tile;
import org.rpgwizard.common.assets.map.MapLayer;
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.editors.map.AbstractMapView;
import org.rpgwizard.editor.editors.map.MapLayerView;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;

/**
 *
 *
 * @author Joshua Michael Daly
 */
@Getter
@Setter
public class BucketBrush extends AbstractBrush {

    /**
     *
     */
    protected Tile pourTile;

    /**
     *
     */
    protected Tile oldTile;

    /**
     *
     */
    public BucketBrush() {

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
        return new Rectangle(0, 0, 1, 1);
    }

    /**
     *
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
        return brush instanceof BucketBrush;
    }

    /**
     *
     *
     * @param x
     * @param y
     * @param selection
     * @return
     */
    @Override
    public Rectangle doPaint(int x, int y, Rectangle selection) {
        final MapLayerView layerView = affectedContainer.getLayer(currentLayer);
        if (layerView == null || pourTile == null || pourTile.getTileSet() == null) {
            return null;
        }
        final MapLayer layer = layerView.getLayer();
        if (layer == null || layer.getTileAt(x, y) == pourTile) {
            return null;
        }

        oldTile = layer.getTileAt(x, y);
        if (selection != null && selection.contains(x, y)) {
            return handleSelection(layer, selection, new Point(x, y));
        } else {
            return handleArea(layer, new Point(x, y));
        }
    }

    @Override
    public void doMouseButton1Pressed(Point point, AbstractAssetEditorWindow editor) {
        MapEditor mapEditor = (MapEditor) editor;
        mapEditor.doPaint(this, point, mapEditor.getSelectionExpaned());
    }

    @Override
    public void doMouseButton2Pressed(Point point, AbstractAssetEditorWindow editor) {

    }

    @Override
    public void doMouseButton3Pressed(Point point, AbstractAssetEditorWindow editor) {

    }

    @Override
    public boolean doMouseButton1Dragged(Point point, Point origin, AbstractAssetEditorWindow editor) {
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

    private Rectangle handleSelection(final MapLayer layer, final Rectangle selection, final Point origin) {
        if (selection.contains(origin.x, origin.y)) {
            for (int y = selection.y; y < selection.height + selection.y; y++) {
                for (int x = selection.x; x < selection.width + selection.x; x++) {
                    layer.pourTileAt(x, y, pourTile);
                }
            }
            layer.getMap().fireMapChanged();
        }
        return selection;
    }

    private Rectangle handleArea(final MapLayer layer, final Point origin) {
        final Rectangle area = new Rectangle(new Point(origin));
        final Stack<Point> stack = new Stack<>();
        stack.push(new Point(origin));
        boolean changed = false;
        while (!stack.empty()) {
            Point point = stack.pop(); // Remove the next tile from the stack.
            if (layer.contains(point.x, point.y) && layer.getTileAt(point.x, point.y).equals(oldTile)) {
                if (!changed) {
                    changed = true;
                }
                layer.pourTileAt(point.x, point.y, pourTile);
                area.add(point);
                stack.push(new Point(point.x, point.y - 1));
                stack.push(new Point(point.x, point.y + 1));
                stack.push(new Point(point.x + 1, point.y));
                stack.push(new Point(point.x - 1, point.y));
            }
        }
        if (changed) {
            layer.getMap().fireMapChanged();
        }
        return new Rectangle(area.x, area.y, area.width + 1, area.height + 1);
    }

}
