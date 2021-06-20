/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.brush;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.UUID;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.assets.map.MapImage;
import org.rpgwizard.common.assets.map.SelectablePair;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.editors.map.AbstractMapView;
import org.rpgwizard.editor.editors.map.MapLayerView;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.actions.RemoveMapImageAction;

/**
 * CLEANUP: Clean me up
 * 
 * @author Joshua Michael Daly
 */
public class MapImageBrush extends AbstractBrush {

    private SelectablePair<String, MapImage> pair;

    public MapImageBrush() {
        pair = new SelectablePair<>(null, null);
    }

    public MapImage getMapImage() {
        return pair.getValue();
    }

    public void setMapImage(MapImage mapLayerImage) {
        this.pair.setValue(mapLayerImage);
    }

    @Override
    public Shape getShape() {
        return getBounds();
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
        super.doPaint(x, y, selection);

        MapLayerView mapLayerView = affectedContainer.getLayer(currentLayer);

        if (mapLayerView != null) {
            boolean snap = MainWindow.getInstance().isSnapToGrid();
            Map map = mapLayerView.getLayer().getMap();
            Rectangle shapeBounds = getBounds();

            if (snap) {
                Point point = getSnapPoint(map, x, y);
                x = point.x;
                y = point.y;
            } else {
                x -= (map.getTileWidth() / 2);
                y -= (map.getTileHeight() / 2);
            }

            String newId = UUID.randomUUID().toString();
            MapImage mapImage = new MapImage("", x, y, false, null);
            pair = new SelectablePair<>(newId, mapImage);

            map.addLayerImage(currentLayer, UUID.randomUUID().toString(), mapImage);

            int centerX = x - shapeBounds.width / 2;
            int centerY = y - shapeBounds.height / 2;

            return new Rectangle(centerX, centerY, shapeBounds.width, shapeBounds.height);
        } else {
            return null;
        }
    }

    @Override
    public void doMouseButton1Pressed(Point point, AbstractAssetEditorWindow editor) {
        MapEditor mapEditor = (MapEditor) editor;
        mapEditor.doPaint(this, point, null);
    }

    @Override
    public void doMouseButton2Pressed(Point point, AbstractAssetEditorWindow editor) {
        if (editor instanceof MapEditor) {
            MapEditor mapEditor = (MapEditor) editor;
            RemoveMapImageAction action = new RemoveMapImageAction(mapEditor, currentLayer, pair.getKey());
            action.actionPerformed(null);
        }
    }

    @Override
    public void doMouseButton3Pressed(Point point, AbstractAssetEditorWindow editor) {
        if (editor instanceof MapEditor) {
            MapEditor mapEditor = (MapEditor) editor;
            BufferedImage defaultImage = MapLayerView.getPlaceHolderImage();
            pair = mapEditor.getMapView().getCurrentSelectedLayer().getLayer().findImageAt(point.x, point.y,
                    defaultImage.getWidth(), defaultImage.getHeight());
            selectImage(pair, mapEditor);
        }
    }

    @Override
    public boolean doMouseButton1Dragged(Point point, Point origin, AbstractAssetEditorWindow editor) {
        return false;
    }

    @Override
    public boolean doMouseButton3Dragged(Point point, Point origin, AbstractAssetEditorWindow editor) {
        if (editor instanceof MapEditor) {
            MapEditor mapEditor = (MapEditor) editor;
            if (mapEditor.getSelectedObject() == pair) {
                Dimension dimension = mapEditor.getMap().getMapPixelDimensions();
                Map map = mapEditor.getMap();
                if (checkDragBounds(point.x, point.y, dimension.width, dimension.height)) {
                    if (MainWindow.getInstance().isSnapToGrid()) {
                        point = getSnapPoint(mapEditor.getMap(), point.x, point.y);
                    } else {
                        point.x -= (map.getTileWidth() / 2);
                        point.y -= (map.getTileHeight() / 2);
                    }

                    pair.getValue().updateLocation(point.x, point.y);
                    mapEditor.getMapView().repaint();

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isPixelBased() {
        return true;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(0, 0, 1, 1);
    }

    @Override
    public void drawPreview(Graphics2D g2d, AbstractMapView view) {

    }

    @Override
    public boolean equals(Brush brush) {
        return brush instanceof MapImageBrush && ((MapImageBrush) brush).pair.getValue().equals(pair.getValue());
    }

    private Point getSnapPoint(Map map, int x, int y) {
        x = Math.max(0, Math.min(x / map.getTileWidth(), map.getWidth() - 1)) * map.getTileWidth();
        y = Math.max(0, Math.min(y / map.getTileHeight(), map.getHeight() - 1)) * map.getTileHeight();

        return new Point(x, y);
    }

    private void selectImage(SelectablePair pair, MapEditor editor) {
        if (pair != null) {
            pair.setSelectedState(true);
            if (editor.getSelectedObject() != null) {
                editor.getSelectedObject().setSelectedState(false);
            }
            editor.setSelectedObject(pair);
        } else if (editor.getSelectedObject() != null) {
            editor.getSelectedObject().setSelectedState(false);
            editor.setSelectedObject(null);
        }
    }

}
