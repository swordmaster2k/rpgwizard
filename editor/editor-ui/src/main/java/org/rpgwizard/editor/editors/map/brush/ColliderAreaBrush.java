/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
// REFACTOR: FIX ME
package org.rpgwizard.editor.editors.map.brush;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.UUID;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.common.assets.map.PolygonPair;
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.editors.map.MapLayerView;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.actions.RemoveColliderAction;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class ColliderAreaBrush extends AbstractPolygonAreaBrush {

    public ColliderAreaBrush() {
        polygon = new Collider();
    }

    @Override
    public boolean equals(Brush brush) {
        return brush instanceof ColliderAreaBrush && ((ColliderAreaBrush) brush).polygon.equals(polygon);
    }

    @Override
    public Rectangle doPaint(int x, int y, Rectangle selection) throws Exception {
        MapLayerView mapLayerView = affectedContainer.getLayer(currentLayer);

        super.doPaint(x, y, selection);

        if (mapLayerView != null) {
            if (!drawing) {
                drawing = true;
                polygonId = UUID.randomUUID().toString();
                polygon = new Collider();
                affectedContainer.getLayer(currentLayer).getLayer().getColliders().put(polygonId, (Collider) polygon);
            }

            int[] coordinates = calculateCoordinates(x, y);
            if (polygon.addPoint(coordinates[0], coordinates[1])) {
                mapLayerView.getLayer().getMap().fireMapChanged();
            }
        }

        return null;
    }

    @Override
    public void reset() {
        polygonId = null;
        polygon = new Collider();
        drawing = false;
    }

    @Override
    public void finish(int x, int y) {
        // Top-left
        org.rpgwizard.common.assets.Point assetPoint = polygon.getPoints().get(0);
        Point p1 = new Point(assetPoint.getX(), assetPoint.getY());

        // Bottom-right
        int[] coordinates = calculateCoordinates(x, y);
        Point p3 = new Point(coordinates[0], coordinates[1]);

        // Top-right
        Point p2 = new Point(p3.x, p1.y);

        // Bottom-left
        Point p4 = new Point(p1.x, p3.y);

        // Add the remaining points to the map polygon rectangle
        polygon.addPoint(p2.x, p2.y);
        polygon.addPoint(p3.x, p3.y);
        polygon.addPoint(p4.x, p4.y);

        polygonId = null;
        polygon = new Collider();
        drawing = false;
    }

    @Override
    public void doMouseButton2Pressed(Point point, AbstractAssetEditorWindow editor) {
        if (editor instanceof MapEditor) {
            MapEditor mapEditor = (MapEditor) editor;
            if (drawing) {
                finish(point.x, point.y);
            }
            RemoveColliderAction action = new RemoveColliderAction(mapEditor, point.x, point.y, false);
            action.actionPerformed(null);
        }
    }

    @Override
    public void doMouseButton3Pressed(Point point, AbstractAssetEditorWindow editor) {
        if (editor instanceof MapEditor) {
            MapEditor mapEditor = (MapEditor) editor;
            if (drawing) {
                // We are drawing a polygon, so lets reset it.
                reset();
            } else {
                // We want to select a polygon.
                PolygonPair pair = mapEditor.getMapView().getCurrentSelectedLayer().getLayer().findColliderAt(point.x,
                        point.y);
                selectPolygon(pair, mapEditor);
            }
        }
    }

}
