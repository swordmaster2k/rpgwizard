/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board;

import org.rpgwizard.editor.editors.board.brush.AbstractBrush;
import org.rpgwizard.editor.editors.board.brush.ShapeBrush;
import org.rpgwizard.editor.editors.board.brush.BucketBrush;
import org.rpgwizard.editor.editors.board.brush.CustomBrush;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import org.rpgwizard.common.assets.board.Board;
import org.rpgwizard.common.assets.tileset.Tile;

import org.rpgwizard.editor.editors.BoardEditor;
import org.rpgwizard.editor.MainWindow;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class BoardMouseAdapter extends MouseAdapter {

    private Point origin;
    private final BoardEditor editor;

    private boolean draggingEntity;
    private boolean changedEntity;

    /**
     *
     *
     * @param boardEditor
     */
    public BoardMouseAdapter(BoardEditor boardEditor) {
        editor = boardEditor;
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
        if (editor.getBoardView().getCurrentSelectedLayer() != null) {
            AbstractBrush brush = MainWindow.getInstance().getCurrentBrush();
            if (!checkBrushValid(brush)) {
                return;
            }

            int button = e.getButton();
            int x = (int) (e.getX() / editor.getBoardView().getZoom());
            int y = (int) (e.getY() / editor.getBoardView().getZoom());

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
                editor.getBoard().fireBoardChanged();
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
        if (editor.getBoardView().getCurrentSelectedLayer() != null) {
            AbstractBrush brush = MainWindow.getInstance().getCurrentBrush();
            if (!checkBrushValid(brush)) {
                return;
            }

            int x = (int) (e.getX() / editor.getBoardView().getZoom());
            int y = (int) (e.getY() / editor.getBoardView().getZoom());

            if (SwingUtilities.isLeftMouseButton(e)) {
                doMouseButton1Dragged(brush, x, y);
            } else if (SwingUtilities.isMiddleMouseButton(e)) {

            } else if (SwingUtilities.isRightMouseButton(e)) {
                doMouseButton3Dragged(brush, x, y);
            }
        }
        editor.getBoardView().repaint();
    }

    /**
     *
     *
     * @param e
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        int x = (int) (e.getX() / editor.getBoardView().getZoom());
        int y = (int) (e.getY() / editor.getBoardView().getZoom());
        editor.setCursorTileLocation(editor.getBoardView().getTileCoordinates(x, y));
        editor.setCursorLocation(new Point(x, y));
        editor.getBoardView().repaint();
    }

    /**
     * Deals with the creation of an object on a board layer.
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
            point = editor.getBoardView().getTileCoordinates(x, y);
        }

        origin = point;
        brush.doMouseButton1Pressed(point, editor);
    }

    /**
     * Deals with the deletion of an object on a board layer.
     *
     * @param e
     * @param brush
     */
    private void doMouseButton2Pressed(AbstractBrush brush, int x, int y) {
        Point point;
        if (brush.isPixelBased()) {
            point = new Point(x, y);
        } else {
            point = editor.getBoardView().getTileCoordinates(x, y);
        }

        brush.doMouseButton2Pressed(point, editor);
    }

    /**
     * Deals with the selection of an object on a board layer
     *
     * @param e
     * @param brush
     */
    private void doMouseButton3Pressed(AbstractBrush brush, int x, int y) {
        Point point;
        if (brush.isPixelBased()) {
            point = new Point(x, y);
        } else {
            point = editor.getBoardView().getTileCoordinates(x, y);
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
        // Ensure that the dragging remains within the bounds of the board.
        Point point = editor.getBoardView().getTileCoordinates(x, y);
        if (!editor.getBoardView().checkTileInBounds(point.x, point.y)) {
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
        // Ensure that the dragging remains within the bounds of the board.
        Point point = editor.getBoardView().getTileCoordinates(x, y);
        if (!editor.getBoardView().checkTileInBounds(point.x, point.y)) {
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

            return isSameTileSize(editor.getBoard(), shapeBrush.getTile());
        } else if (brush instanceof BucketBrush) {
            BucketBrush bucketBrush = (BucketBrush) brush;

            if (bucketBrush.getPourTile() == null || bucketBrush.getPourTile().getTileSet() == null) {
                return false;
            }

            return isSameTileSize(editor.getBoard(), bucketBrush.getPourTile());
        } else if (brush instanceof CustomBrush) {
            CustomBrush customBrush = (CustomBrush) brush;

            if (customBrush.getTiles().length > 0) {
                if (customBrush.getTiles()[0].length > 0) {
                    if (customBrush.getTiles()[0][0] == null) {
                        return true; // Selection brush.
                    }

                    return isSameTileSize(editor.getBoard(), customBrush.getTiles()[0][0]);
                }
            }
        }

        return true;
    }

    private boolean isSameTileSize(Board board, Tile tile) {
        return board.getTileWidth() == tile.getTileSet().getTileWidth()
                && board.getTileHeight() == tile.getTileSet().getTileHeight();
    }

}
