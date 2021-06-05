/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.brush;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.common.assets.map.SelectablePair;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.editors.map.MapLayerView;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.actions.RemoveColliderAction;

/**
 * REFACTOR: FIX ME
 *
 * @author Joshua Michael Daly
 */
@Getter
@Setter
public class ColliderBrush extends AbstractPolygonBrush {

    public ColliderBrush() {
        polygon = new Collider();
    }

    @Override
    public boolean equals(Brush brush) {
        return brush instanceof ColliderBrush && ((ColliderBrush) brush).polygon.equals(polygon);
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

            if (MainWindow.getInstance().isSnapToGrid()) {
                MainWindow.getInstance().getCurrentMapEditor().calculateSnapCoordinates(x, y);
            }

            int[] coordinates = { x, y };

            if (MainWindow.getInstance().isSnapToGrid()) {
                coordinates = MainWindow.getInstance().getCurrentMapEditor().calculateSnapCoordinates(x, y);
            }

            if (polygon.addPoint(coordinates[0], coordinates[1])) {
                mapLayerView.getLayer().getMap().fireMapChanged();
            }
        }

        return null;
    }

    @Override
    public void finish() {
        if (polygon.getPointCount() < 2) {
            affectedContainer.getLayer(currentLayer).getLayer().getColliders().remove(polygonId);
        }
        polygonId = null;
        polygon = new Collider();
        drawing = false;
    }

    @Override
    public void doMouseButton2Pressed(Point point, AbstractAssetEditorWindow editor) {
        if (editor instanceof MapEditor) {
            MapEditor mapEditor = (MapEditor) editor;
            if (drawing) {
                finish();
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
                // We are drawing a polygon, so lets finish it.
                finish();
            } else {
                // We want to select a polygon.
                SelectablePair pair = mapEditor.getMapView().getCurrentSelectedLayer().getLayer()
                        .findColliderAt(point.x, point.y);
                selectPolygon(pair, mapEditor);
            }
        }
    }

}
