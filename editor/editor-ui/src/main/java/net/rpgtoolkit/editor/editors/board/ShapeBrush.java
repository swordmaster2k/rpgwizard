/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.editors.board;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import net.rpgtoolkit.common.assets.Tile;
import net.rpgtoolkit.editor.editors.BoardEditor;
import net.rpgtoolkit.editor.ui.AssetEditorWindow;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class ShapeBrush extends AbstractBrush {

    /**
     *
     */
    protected Area shape;

    /**
     *
     */
    protected Tile paintTile;

    /**
     *
     */
    public ShapeBrush() {

    }

    /**
     *
     *
     * @param shape
     */
    public ShapeBrush(Area shape) {
        this.shape = shape;
        paintTile = new Tile();
    }

    /**
     *
     *
     * @param abstractBrush
     */
    public ShapeBrush(AbstractBrush abstractBrush) {
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

    /**
     *
     *
     * @param rectangle
     */
    public void makeRectangleBrush(Rectangle rectangle) {
        shape = new Area(new Rectangle2D.Double(rectangle.x, rectangle.y,
                rectangle.width, rectangle.height));
    }

    /**
     *
     *
     * @param g2d
     * @param dimension
     * @param view
     */
    @Override
    public void drawPreview(Graphics2D g2d, Dimension dimension,
            AbstractBoardView view) {
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
        return brush instanceof ShapeBrush
                && ((ShapeBrush) brush).shape.equals(shape);
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
        Rectangle shapeBounds = shape.getBounds();
        int centerX = x - shapeBounds.width / 2;
        int centerY = y - shapeBounds.height / 2;

        super.doPaint(x, y, selection);

        for (int layer = 0; layer < affectedLayers; layer++) {
            BoardLayerView boardLayer = affectedContainer.getLayer(
                    currentLayer + layer);

            if (boardLayer != null) {
                for (int i = 0; i <= shapeBounds.height + 1; i++) {
                    for (int j = 0; j <= shapeBounds.width + 1; j++) {
                        if (shape.contains(i, j)) {
                            boardLayer.getLayer().setTileAt(j + centerX, i + centerY,
                                    paintTile);
                        }
                    }
                }
            }
        }

        return new Rectangle(
                centerX, centerY, shapeBounds.width, shapeBounds.height);
    }

    @Override
    public void doMouseButton1Pressed(Point point, AssetEditorWindow editor) {
        if (editor instanceof BoardEditor) {
            BoardEditor boardEditor = (BoardEditor) editor;
            boardEditor.setSelection(null);
        }
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
