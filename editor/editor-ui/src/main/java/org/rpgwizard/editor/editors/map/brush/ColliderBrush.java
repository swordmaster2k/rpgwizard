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
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.editors.map.AbstractMapView;
import org.rpgwizard.editor.editors.map.MapLayerView;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.actions.RemoveColliderAction;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 * REFACTOR: FIX ME
 *
 * @author Joshua Michael Daly
 */
@Getter
@Setter
public class ColliderBrush extends AbstractBrush {

    protected String colliderId;
    protected Collider collider;
    protected boolean drawing;
    protected Color previewColor;

    public ColliderBrush() {
        collider = new Collider();
        drawing = false;
        previewColor = Color.WHITE;
    }

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
        if (collider.getPoints().size() < 1) {
            return;
        }

        Point cursor = view.getMapEditor().getCursorLocation();

        org.rpgwizard.common.assets.Point pointAsset = collider.getPoints().get(collider.getPoints().size() - 1);
        Point lastVectorPoint = new Point(pointAsset.getX(), pointAsset.getY());

        int[] coordinates = { cursor.x, cursor.y };

        if (MainWindow.getInstance().isSnapToGrid()) {
            coordinates = MainWindow.getInstance().getCurrentMapEditor().calculateSnapCoordinates(cursor.x, cursor.y);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
        g2d.setColor(previewColor);

        int[] points = GuiHelper.ensureVisible(view.getMap(), lastVectorPoint.x, lastVectorPoint.y, coordinates[0],
                coordinates[1]);

        g2d.drawLine(points[0], points[1], points[2], points[3]);
    }

    @Override
    public boolean equals(Brush brush) {
        return brush instanceof ColliderBrush && ((ColliderBrush) brush).collider.equals(collider);
    }

    @Override
    public Rectangle doPaint(int x, int y, Rectangle selection) throws Exception {
        MapLayerView mapLayerView = affectedContainer.getLayer(currentLayer);

        super.doPaint(x, y, selection);

        if (mapLayerView != null) {
            if (!drawing) {
                drawing = true;
                colliderId = UUID.randomUUID().toString();
                collider = new Collider();

                affectedContainer.getLayer(currentLayer).getLayer().getColliders().put(colliderId, collider);
            }

            if (MainWindow.getInstance().isSnapToGrid()) {
                MainWindow.getInstance().getCurrentMapEditor().calculateSnapCoordinates(x, y);
            }

            int[] coordinates = { x, y };

            if (MainWindow.getInstance().isSnapToGrid()) {
                coordinates = MainWindow.getInstance().getCurrentMapEditor().calculateSnapCoordinates(x, y);
            }

            if (collider.addPoint(coordinates[0], coordinates[1])) {
                mapLayerView.getLayer().getMap().fireMapChanged();
            }
        }

        return null;
    }

    public void finish() {
        if (collider.getPointCount() < 2) {
            affectedContainer.getLayer(currentLayer).getLayer().getColliders().remove(colliderId);
        }
        colliderId = null;
        collider = new Collider();
        drawing = false;
    }

    public void abort() {
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
                // We are drawing a vector, so lets finish it.
                finish();
            } else {
                // We want to select a vector.
                Pair<String, Collider> pair = mapEditor.getMapView().getCurrentSelectedLayer().getLayer()
                        .findColliderAt(point.x, point.y);
                if (pair != null) {
                    selectCollider(pair.getValue(), mapEditor);
                }
            }
        }
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

    private void selectCollider(Collider collider, MapEditor editor) {
        if (collider != null) {
            collider.setSelectedState(true);
            if (editor.getSelectedObject() != null) {
                editor.getSelectedObject().setSelectedState(false);
            }
            editor.setSelectedObject(collider);
        } else if (editor.getSelectedObject() != null) {
            editor.getSelectedObject().setSelectedState(false);
            editor.setSelectedObject(null);
        }
    }

}
