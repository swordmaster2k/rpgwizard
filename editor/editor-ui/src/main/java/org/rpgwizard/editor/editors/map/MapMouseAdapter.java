/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map;

import org.rpgwizard.editor.editors.map.brush.AbstractBrush;
import org.rpgwizard.editor.editors.map.brush.ShapeBrush;
import org.rpgwizard.editor.editors.map.brush.BucketBrush;
import org.rpgwizard.editor.editors.map.brush.CustomBrush;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.assets.tileset.Tile;

import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.MainWindow;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class MapMouseAdapter extends MouseAdapter {

    private Point origin;
    private final MapEditor editor;

    private boolean draggingEntity;
    private boolean changedEntity;

    /**
     *
     *
     * @param mapEditor
     */
    public MapMouseAdapter(MapEditor mapEditor) {
        editor = mapEditor;
        draggingEntity = false;
        changedEntity = false;
    }

    /**
     *
     *
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (editor.getMapView().getCurrentSelectedLayer() != null) {
            AbstractBrush brush = MainWindow.getInstance().getCurrentBrush();
            if (!checkBrushValid(brush)) {
                return;
            }

            int button = e.getButton();
            int x = (int) (e.getX() / editor.getMapView().getZoom());
            int y = (int) (e.getY() / editor.getMapView().getZoom());

            switch (button) {
            case MouseEvent.BUTTON1:
                doMouseButton1Pressed(brush, x, y);
                break;
            case MouseEvent.BUTTON2:
                doMouseButton2Pressed(brush, x, y);
                break;
            case MouseEvent.BUTTON3:
                doMouseButton3Pressed(brush, x, y);
                break;
            default:
                break;
            }
        }
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        int button = e.getButton();
        switch (button) {
        case MouseEvent.BUTTON1:
        case MouseEvent.BUTTON3:
            if (draggingEntity) {
                draggingEntity = false;
            }
            if (changedEntity) {
                changedEntity = false;
                editor.getMap().fireMapChanged();
            }
        default:
            break;
        }
    }

    /**
     *
     *
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (editor.getMapView().getCurrentSelectedLayer() != null) {
            AbstractBrush brush = MainWindow.getInstance().getCurrentBrush();
            if (!checkBrushValid(brush)) {
                return;
            }

            int x = (int) (e.getX() / editor.getMapView().getZoom());
            int y = (int) (e.getY() / editor.getMapView().getZoom());

            if (SwingUtilities.isLeftMouseButton(e)) {
                doMouseButton1Dragged(brush, x, y);
            } else if (SwingUtilities.isMiddleMouseButton(e)) {

            } else if (SwingUtilities.isRightMouseButton(e)) {
                doMouseButton3Dragged(brush, x, y);
            }
        }
        editor.getMapView().repaint();
    }

    /**
     *
     *
     * @param e
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        int x = (int) (e.getX() / editor.getMapView().getZoom());
        int y = (int) (e.getY() / editor.getMapView().getZoom());
        editor.setCursorTileLocation(editor.getMapView().getTileCoordinates(x, y));
        editor.setCursorLocation(new Point(x, y));
        editor.getMapView().repaint();
    }

    /**
     * Deals with the creation of an object on a map layer.
     *
     * @param e
     * @param brush
     */
    private void doMouseButton1Pressed(AbstractBrush brush, int x, int y) {
        Rectangle selection = editor.getSelectionExpaned();

        Point point;
        if (brush.isPixelBased()) {
            point = new Point(x, y);
        } else {
            point = editor.getMapView().getTileCoordinates(x, y);
        }

        origin = point;
        brush.doMouseButton1Pressed(point, editor);
    }

    /**
     * Deals with the deletion of an object on a map layer.
     *
     * @param e
     * @param brush
     */
    private void doMouseButton2Pressed(AbstractBrush brush, int x, int y) {
        Point point;
        if (brush.isPixelBased()) {
            point = new Point(x, y);
        } else {
            point = editor.getMapView().getTileCoordinates(x, y);
        }

        brush.doMouseButton2Pressed(point, editor);
    }

    /**
     * Deals with the selection of an object on a map layer
     *
     * @param e
     * @param brush
     */
    private void doMouseButton3Pressed(AbstractBrush brush, int x, int y) {
        Point point;
        if (brush.isPixelBased()) {
            point = new Point(x, y);
        } else {
            point = editor.getMapView().getTileCoordinates(x, y);
        }

        brush.doMouseButton3Pressed(point, editor);
    }

    /**
     *
     *
     * @param e
     * @param brush
     */
    private void doMouseButton1Dragged(AbstractBrush brush, int x, int y) {
        // Ensure that the dragging remains within the bounds of the map.
        Point point = editor.getMapView().getTileCoordinates(x, y);
        if (!editor.getMapView().checkTileInBounds(point.x, point.y)) {
            return;
        }

        if (brush.isPixelBased()) {
            point = new Point(x, y);
        }

        editor.setCursorTileLocation(point);
        editor.setCursorLocation(new Point(x, y));

        changedEntity = brush.doMouseButton1Dragged(point, origin, editor) || changedEntity;
    }

    /**
     *
     *
     * @param e
     * @param brush
     */
    private void doMouseButton3Dragged(AbstractBrush brush, int x, int y) {
        // Ensure that the dragging remains within the bounds of the map.
        Point point = editor.getMapView().getTileCoordinates(x, y);
        if (!editor.getMapView().checkTileInBounds(point.x, point.y)) {
            return;
        }

        if (brush.isPixelBased()) {
            point = new Point(x, y);
        }

        editor.setCursorTileLocation(point);
        editor.setCursorLocation(new Point(x, y));

        changedEntity = brush.doMouseButton3Dragged(point, origin, editor) || changedEntity;

        // Set the dragging flag if required.
        if (!draggingEntity) {
            draggingEntity = true;
        }
    }

    private boolean checkBrushValid(AbstractBrush brush) {
        if (brush instanceof ShapeBrush) {
            ShapeBrush shapeBrush = (ShapeBrush) brush;
            if (shapeBrush.getTile() == null || shapeBrush.getTile().getTileSet() == null) {
                return false;
            }

            return isSameTileSize(editor.getMap(), shapeBrush.getTile());
        } else if (brush instanceof BucketBrush) {
            BucketBrush bucketBrush = (BucketBrush) brush;

            if (bucketBrush.getPourTile() == null || bucketBrush.getPourTile().getTileSet() == null) {
                return false;
            }

            return isSameTileSize(editor.getMap(), bucketBrush.getPourTile());
        } else if (brush instanceof CustomBrush) {
            CustomBrush customBrush = (CustomBrush) brush;

            if (customBrush.getTiles().length > 0) {
                if (customBrush.getTiles()[0].length > 0) {
                    if (customBrush.getTiles()[0][0] == null) {
                        return true; // Selection brush.
                    }

                    return isSameTileSize(editor.getMap(), customBrush.getTiles()[0][0]);
                }
            }
        }

        return true;
    }

    private boolean isSameTileSize(Map map, Tile tile) {
        return map.getTileWidth() == tile.getTileSet().getTileWidth()
                && map.getTileHeight() == tile.getTileSet().getTileHeight();
    }

}
