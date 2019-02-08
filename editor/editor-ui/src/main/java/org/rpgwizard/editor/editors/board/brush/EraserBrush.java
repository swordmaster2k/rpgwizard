/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board.brush;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import org.rpgwizard.common.assets.Tile;
import org.rpgwizard.common.assets.board.BoardLayer;
import org.rpgwizard.editor.editors.BoardEditor;
import org.rpgwizard.editor.editors.board.AbstractBoardView;
import org.rpgwizard.editor.editors.board.BoardLayerView;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class EraserBrush extends AbstractBrush {

    /**
     *
     */
    protected Area shape;

    /**
     *
     */
    protected Tile paintTile;

    public EraserBrush() {

    }

    public EraserBrush(Area shape) {
        this.shape = shape;
        paintTile = new Tile();
    }

    public EraserBrush(AbstractBrush abstractBrush) {
        super(abstractBrush);

        if (abstractBrush instanceof ShapeBrush) {
            shape = ((ShapeBrush) abstractBrush).shape;
            paintTile = ((ShapeBrush) abstractBrush).paintTile;
        }
    }

    /**
     *
     *
     * @return
     */
    public Tile getTile() {
        return paintTile;
    }

    /**
     *
     *
     * @param tile
     */
    public void setTile(Tile tile) {
        paintTile = tile;
    }

    /**
     *
     *
     * @return
     */
    @Override
    public Rectangle getBounds() {
        return shape.getBounds();
    }

    /**
     *
     *
     * @return
     */
    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public boolean isPixelBased() {
        return false;
    }

    /**
     *
     *
     * @param rectangle
     */
    public void makeRectangleBrush(Rectangle rectangle) {
        shape = new Area(new Rectangle2D.Double(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
    }

    /**
     *
     *
     * @param g2d
     * @param dimension
     * @param view
     */
    @Override
    public void drawPreview(Graphics2D g2d, Dimension dimension, AbstractBoardView view) {
        g2d.fill(shape);
    }

    /**
     *
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
        return brush instanceof EraserBrush && ((EraserBrush) brush).shape.equals(shape);
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
        BoardLayerView layerView = affectedContainer.getLayer(currentLayer);
        if (layerView == null || layerView.getLayer() == null) {
            return null;
        }

        final BoardLayer layer = layerView.getLayer();
        if (selection != null && selection.contains(x, y)) {
            return handleSelection(layer, selection, new Point(x, y));
        } else {
            return handleArea(layer, new Point(x, y));
        }
    }

    @Override
    public void doMouseButton1Pressed(Point point, AbstractAssetEditorWindow editor) {
        if (editor instanceof BoardEditor) {
            BoardEditor boardEditor = (BoardEditor) editor;
            boardEditor.setSelection(null);
        }
    }

    @Override
    public void doMouseButton2Pressed(Point point, AbstractAssetEditorWindow editor) {

    }

    @Override
    public void doMouseButton3Pressed(Point point, AbstractAssetEditorWindow editor) {

    }

    @Override
    public void doMouseButton1Dragged(Point point, Point origin, AbstractAssetEditorWindow editor) {
        if (editor instanceof BoardEditor) {
            ((BoardEditor) editor).doPaint(this, point, null);
        }
    }

    @Override
    public void doMouseButton3Dragged(Point point, Point origin, AbstractAssetEditorWindow editor) {

    }

    private Rectangle handleSelection(final BoardLayer layer, final Rectangle selection, final Point origin) {
        BoardLayerView layerView = affectedContainer.getLayer(currentLayer);
        if (layerView == null) {
            return null;
        }

        boolean changed = false;
        if (selection.contains(origin.x, origin.y)) {
            for (int y2 = selection.y; y2 < selection.height + selection.y; y2++) {
                for (int x2 = selection.x; x2 < selection.width + selection.x; x2++) {
                    boolean tileEffected = layer.pourTileAt(x2, y2, paintTile);
                    if (!changed && tileEffected) {
                        changed = true;
                    }
                }
            }
        }
        if (changed && layer != null) {
            layer.getBoard().fireBoardChanged();
        }

        return selection;
    }

    private Rectangle handleArea(final BoardLayer layer, final Point origin) {
        Rectangle shapeBounds = shape.getBounds();
        int centerX = origin.x - shapeBounds.width / 2;
        int centerY = origin.y - shapeBounds.height / 2;
        boolean changed = false;
        for (int i = 0; i <= shapeBounds.height + 1; i++) {
            for (int j = 0; j <= shapeBounds.width + 1; j++) {
                if (shape.contains(i, j)) {
                    boolean tileEffected = layer.pourTileAt(j + centerX, i + centerY, paintTile);
                    if (!changed && tileEffected) {
                        changed = true;
                    }
                }
            }
        }
        if (changed && layer != null) {
            layer.getBoard().fireBoardChanged();
        }

        return new Rectangle(centerX, centerY, shapeBounds.width, shapeBounds.height);
    }

}
