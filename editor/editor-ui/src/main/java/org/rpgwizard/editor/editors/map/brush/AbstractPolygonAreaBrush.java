/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.brush;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import lombok.Getter;
import lombok.Setter;
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
public abstract class AbstractPolygonAreaBrush extends AbstractPolygonBrush {

    @Override
    public void drawPreview(Graphics2D g2d, AbstractMapView view) {
        if (polygon.getPoints().size() < 1) {
            return;
        }

        Point cursor = view.getMapEditor().getCursorLocation();

        org.rpgwizard.common.assets.Point assetPoint = polygon.getPoints().get(polygon.getPoints().size() - 1);
        Point lastVectorPoint = new Point(assetPoint.getX(), assetPoint.getY());

        int[] coordinates = { cursor.x, cursor.y };

        if (MainWindow.getInstance().isSnapToGrid()) {
            coordinates = MainWindow.getInstance().getCurrentMapEditor().calculateSnapCoordinates(cursor.x, cursor.y);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
        g2d.setColor(previewColor);

        int[] points = GuiHelper.ensureVisible(view.getMap(), lastVectorPoint.x, lastVectorPoint.y, coordinates[0],
                coordinates[1]);

        int width = Math.abs(points[2] - points[0]);
        int height = Math.abs(points[3] - points[1]);
        g2d.drawRect(points[0], points[1], width, height);
    }

    public abstract void finish(int x, int y);

    @Override
    public void doMouseButton1Pressed(Point point, AbstractAssetEditorWindow editor) {
        if (drawing) {
            finish(point.x, point.y);
        } else {
            MapEditor mapEditor = (MapEditor) editor;
            mapEditor.doPaint(this, point, null);
        }
    }

    protected int[] calculateCoordinates(int x, int y) {
        if (MainWindow.getInstance().isSnapToGrid()) {
            MainWindow.getInstance().getCurrentMapEditor().calculateSnapCoordinates(x, y);
        }

        int[] coordinates = { x, y };

        if (MainWindow.getInstance().isSnapToGrid()) {
            coordinates = MainWindow.getInstance().getCurrentMapEditor().calculateSnapCoordinates(x, y);
        }

        return coordinates;
    }

}
