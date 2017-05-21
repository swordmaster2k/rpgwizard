/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.editors.board;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import net.rpgtoolkit.common.assets.BoardVector;
import net.rpgtoolkit.editor.MainWindow;
import net.rpgtoolkit.editor.editors.BoardEditor;
import net.rpgtoolkit.editor.ui.AssetEditorWindow;
import net.rpgtoolkit.editor.utilities.GuiHelper;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class BoardVectorBrush extends AbstractBrush {

    /**
     *
     */
    protected BoardVector boardVector;

    /**
     *
     */
    protected boolean stillDrawing;

    /**
     *
     */
    protected Color previewColor;

    /**
     *
     */
    public BoardVectorBrush() {
        boardVector = new BoardVector();
        stillDrawing = false;
        previewColor = Color.WHITE;
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
    public BoardVector getBoardVector() {
        return boardVector;
    }

    /**
     *
     *
     * @param vector
     */
    public void setBoardVector(BoardVector vector) {
        boardVector = vector;
    }

    /**
     *
     *
     * @return
     */
    public boolean isDrawing() {
        return stillDrawing;
    }

    /**
     *
     *
     * @param isDrawing
     */
    public void setDrawing(boolean isDrawing) {
        stillDrawing = isDrawing;
    }

    /**
     *
     *
     * @param g2d
     * @param view
     */
    @Override
    public void drawPreview(Graphics2D g2d, AbstractBoardView view) {
        if (boardVector.getPoints().size() < 1) {
            return;
        }

        Point cursor = view.getBoardEditor().getCursorLocation();
        Point lastVectorPoint = boardVector.getPoints()
                .get(boardVector.getPoints().size() - 1);

        int[] coordinates = {cursor.x, cursor.y};

        if (MainWindow.getInstance().isSnapToGrid()) {
            coordinates = MainWindow.getInstance().getCurrentBoardEditor().
                    calculateSnapCoordinates(cursor.x, cursor.y);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
        g2d.setColor(previewColor);

        int[] points = GuiHelper.ensureVectorVisible(
                view.getBoard(),
                lastVectorPoint.x,
                lastVectorPoint.y,
                coordinates[0],
                coordinates[1]
        );

        g2d.drawLine(points[0], points[1], points[2], points[3]);
    }

    /**
     *
     *
     * @param brush
     * @return
     */
    @Override
    public boolean equals(Brush brush) {
        return brush instanceof BoardVectorBrush
                && ((BoardVectorBrush) brush).boardVector.equals(boardVector);
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
        BoardLayerView boardLayerView = affectedContainer.getLayer(currentLayer);

        super.doPaint(x, y, selection);

        if (boardLayerView != null) {
            if (!stillDrawing) {
                stillDrawing = true;
                boardVector = new BoardVector();
                boardVector.setLayer(currentLayer);

                affectedContainer.getLayer(currentLayer).
                        getLayer().getVectors().add(boardVector);
            }

            if (MainWindow.getInstance().isSnapToGrid()) {
                MainWindow.getInstance().getCurrentBoardEditor().
                        calculateSnapCoordinates(x, y);
            }

            int[] coordinates = {x, y};

            if (MainWindow.getInstance().isSnapToGrid()) {
                coordinates = MainWindow.getInstance().getCurrentBoardEditor().
                        calculateSnapCoordinates(x, y);
            }

            boardVector.addPoint(coordinates[0], coordinates[1]);
            boardLayerView.getLayer().getBoard().fireBoardChanged();
        }

        return null;
    }

    /**
     *
     */
    public void finish() {
        if (boardVector.getPointCount() < 2) {
            affectedContainer.getLayer(currentLayer).getLayer()
                    .getVectors().remove(boardVector);
        }

        boardVector = new BoardVector();
        stillDrawing = false;
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
    protected Rectangle callRootPaint(int x, int y, Rectangle selection)
            throws Exception {
        // This is bad design, and is monkeying around with the inheritance
        // model by exposing access of parent class of this class to a child.
        // Should implement composition of inheritance in this case.
        return super.doPaint(x, y, selection);
    }

    @Override
    public void doMouseButton1Pressed(Point point, AssetEditorWindow editor) {

    }

    @Override
    public void doMouseButton2Pressed(Point point, AssetEditorWindow editor) {

        if (editor instanceof BoardEditor) {
            BoardEditor boardEditor = (BoardEditor) editor;

            if (stillDrawing) {
                finish();
            }

            Object result = boardEditor.getBoardView().getCurrentSelectedLayer()
                    .getLayer().removeVectorAt(point.x, point.y);

            if (result == boardEditor.getSelectedObject()) {
                boardEditor.getSelectedObject().setSelectedState(false);
                boardEditor.setSelectedObject(null);
            }
        }

    }

    @Override
    public void doMouseButton3Pressed(Point point, AssetEditorWindow editor) {

        if (editor instanceof BoardEditor) {
            BoardEditor boardEditor = (BoardEditor) editor;

            // We are drawing a vector, so lets finish it.
            if (stillDrawing) {
                finish();
            } else // We want to select a vector.
            {
                selectVector(boardEditor.getBoardView().getCurrentSelectedLayer()
                        .getLayer().findVectorAt(point.x, point.y), boardEditor);
            }
        }

    }

    @Override
    public void doMouseButton1Dragged(Point point, Point origin, AssetEditorWindow editor) {

    }

    @Override
    public boolean isPixelBased() {
        return true;
    }

    /**
     *
     *
     * @param vector
     */
    private void selectVector(BoardVector vector, BoardEditor editor) {
        if (vector != null) {
            vector.setSelectedState(true);

            if (editor.getSelectedObject() != null) {
                editor.getSelectedObject().setSelectedState(false);
            }

            editor.setSelectedObject(vector);
        } else if (editor.getSelectedObject() != null) {
            editor.getSelectedObject().setSelectedState(false);
            editor.setSelectedObject(null);
        }
    }

}
