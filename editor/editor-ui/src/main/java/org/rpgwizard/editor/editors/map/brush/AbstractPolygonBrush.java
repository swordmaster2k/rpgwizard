/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.brush;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import lombok.Getter;
import lombok.Setter;
import org.rpgwizard.common.assets.AbstractPolygon;
import org.rpgwizard.common.assets.map.PolygonPair;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.editors.map.AbstractMapView;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 * @author Joshua Michael Daly
 */
@Getter
@Setter
public abstract class AbstractPolygonBrush extends AbstractBrush {

    protected String polygonId;
    protected AbstractPolygon polygon;

    protected boolean drawing;
    protected Color previewColor = Color.WHITE;

    @Override
    public Shape getShape() {
        return getBounds();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(0, 0, 1, 1);
    }

    @Override
    public void drawPreview(Graphics2D g2d, AbstractMapView view) {
        if (polygon.getPoints().size() < 1) {
            return;
        }

        Point cursor = view.getMapEditor().getCursorLocation();

        org.rpgwizard.common.assets.Point pointAsset = polygon.getPoints().get(polygon.getPoints().size() - 1);
        Point lastPoint = new Point(pointAsset.getX(), pointAsset.getY());

        int[] coordinates = { cursor.x, cursor.y };

        if (MainWindow.getInstance().isSnapToGrid()) {
            coordinates = MainWindow.getInstance().getCurrentMapEditor().calculateSnapCoordinates(cursor.x, cursor.y);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
        g2d.setColor(previewColor);

        int[] points = GuiHelper.ensureVisible(view.getMap(), lastPoint.x, lastPoint.y, coordinates[0], coordinates[1]);

        g2d.drawLine(points[0], points[1], points[2], points[3]);
    }

    public void finish() {

    }

    public void reset() {
        finish();
    }

    protected Rectangle callRootPaint(int x, int y, Rectangle selection) throws Exception {
        // This is bad design, and is monkeying around with the inheritance
        // model by exposing access of parent class of this class to a child.
        // Should implement composition of inheritance in this case.
        return super.doPaint(x, y, selection);
    }

    @Override
    public void doMouseButton1Pressed(Point point, AbstractAssetEditorWindow editor) {
        MapEditor mapEditor = (MapEditor) editor;
        mapEditor.doPaint(this, point, null);
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
        return true;
    }

    protected void selectPolygon(PolygonPair pair, MapEditor editor) {
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
