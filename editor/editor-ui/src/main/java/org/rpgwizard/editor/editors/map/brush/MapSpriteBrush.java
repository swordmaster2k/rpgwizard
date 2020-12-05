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
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.rpgwizard.common.assets.Location;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.assets.map.MapSprite;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.editors.map.AbstractMapView;
import org.rpgwizard.editor.editors.map.MapLayerView;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.actions.RemoveSpriteAction;

/**
 *
 * @author Joshua Michael Daly
 */
public class MapSpriteBrush extends AbstractBrush {

    private Pair<String, MapSprite> pair;

    /**
     *
     */
    public MapSpriteBrush() {
        pair = new MutablePair<>();
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
     * @return
     */
    public MapSprite getMapSprite() {
        return pair.getValue();
    }

    /**
     *
     *
     * @param mapSprite
     */
    public void setMapSprite(MapSprite mapSprite) {
        this.pair.setValue(mapSprite);
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
            }

            String spriteId = UUID.randomUUID().toString();
            MapSprite mapSprite = new MapSprite();
            mapSprite.getStartLocation().setX(x);
            mapSprite.getStartLocation().setY(y);
            mapSprite.getStartLocation().setLayer(currentLayer);
            pair = new MutablePair<>(spriteId, mapSprite);

            map.addSprite(currentLayer, UUID.randomUUID().toString(), mapSprite);

            int centerX = x - shapeBounds.width / 2;
            int centerY = y - shapeBounds.height / 2;
            return new Rectangle(centerX, centerY, shapeBounds.width, shapeBounds.height);
        } else {
            return null;
        }
    }

    /**
     *
     *
     * @param brush
     * @return
     */
    @Override
    public boolean equals(Brush brush) {
        return brush instanceof MapSpriteBrush && ((MapSpriteBrush) brush).pair.getValue().equals(pair.getValue());
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
            RemoveSpriteAction action = new RemoveSpriteAction(mapEditor, currentLayer, pair.getKey());
            action.actionPerformed(null);
        }
    }

    @Override
    public void doMouseButton3Pressed(Point point, AbstractAssetEditorWindow editor) {
        if (editor instanceof MapEditor) {
            MapEditor mapEditor = (MapEditor) editor;
            BufferedImage defaultImage = MapLayerView.getPlaceHolderSprite();
            pair = mapEditor.getMapView().getCurrentSelectedLayer().getLayer().findSpriteAt(point.x, point.y,
                    defaultImage.getWidth(), defaultImage.getHeight());
            selectSprite(pair.getValue(), mapEditor);
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
            if (mapEditor.getSelectedObject() == pair.getValue()) {
                Dimension dimension = mapEditor.getMap().getMapPixelDimensions();
                if (checkDragBounds(point.x, point.y, dimension.width, dimension.height)) {
                    if (MainWindow.getInstance().isSnapToGrid()) {
                        point = getSnapPoint(mapEditor.getMap(), point.x, point.y);
                    }
                    final Location current = pair.getValue().getStartLocation();
                    if (!current.equals(new Location(point.x, point.y, currentLayer))) {
                        pair.getValue().setStartLocation(new Location(point.x, point.y, currentLayer));
                    }
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

    private Point getSnapPoint(Map map, int x, int y) {
        x = Math.max(0, Math.min(x / map.getTileWidth(), map.getWidth() - 1)) * map.getTileWidth()
                + (map.getTileWidth() / 2);
        y = Math.max(0, Math.min(y / map.getTileHeight(), map.getHeight() - 1)) * map.getTileHeight()
                + (map.getTileHeight() / 2);

        return new Point(x, y);
    }

    /**
     *
     *
     * @param sprite
     */
    private void selectSprite(MapSprite sprite, MapEditor editor) {
        if (sprite != null) {

            if (editor.getSelectedObject() == sprite) {
                return;
            }

            sprite.setSelectedState(true);

            if (editor.getSelectedObject() != null) {
                editor.getSelectedObject().setSelectedState(false);
            }

            editor.setSelectedObject(sprite);
        } else if (editor.getSelectedObject() != null) {
            editor.getSelectedObject().setSelectedState(false);
            editor.setSelectedObject(null);
        }
    }

}
