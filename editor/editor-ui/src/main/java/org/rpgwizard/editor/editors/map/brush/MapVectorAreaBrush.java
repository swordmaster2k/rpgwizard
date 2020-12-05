/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
// REFACTOR: FIX ME
/// **
// * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
// *
// * This Source Code Form is subject to the terms of the Mozilla Public
// * License, v. 2.0. If a copy of the MPL was not distributed with this
// * file, You can obtain one at http://mozilla.org/MPL/2.0/.
// */
// package org.rpgwizard.editor.editors.map.brush;
//
// import java.awt.AlphaComposite;
// import java.awt.Color;
// import java.awt.Graphics2D;
// import java.awt.Point;
// import java.awt.Rectangle;
// import java.awt.Shape;
// import org.rpgwizard.editor.MainWindow;
// import org.rpgwizard.editor.editors.MapEditor;
// import org.rpgwizard.editor.editors.map.AbstractMapView;
// import org.rpgwizard.editor.editors.map.MapLayerView;
// import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
// import org.rpgwizard.editor.ui.actions.RemoveVectorAction;
// import org.rpgwizard.editor.utilities.GuiHelper;
//
/// **
// *
// *
// * @author Joshua Michael Daly
// */
// public class MapVectorAreaBrush extends AbstractBrush {
//
// /**
// *
// */
// protected MapVector mapVector;
//
// /**
// *
// */
// protected boolean stillDrawing;
//
// /**
// *
// */
// protected Color previewColor;
//
// /**
// *
// */
// public MapVectorAreaBrush() {
// mapVector = new MapVector();
// stillDrawing = false;
// previewColor = Color.WHITE;
// }
//
// /**
// *
// *
// * @return
// */
// @Override
// public Shape getShape() {
// return getBounds();
// }
//
// /**
// *
// *
// * @return
// */
// @Override
// public Rectangle getBounds() {
// return new Rectangle(0, 0, 1, 1);
// }
//
// /**
// *
// *
// * @return
// */
// public MapVector getMapVector() {
// return mapVector;
// }
//
// /**
// *
// *
// * @param vector
// */
// public void setMapVector(MapVector vector) {
// mapVector = vector;
// }
//
// /**
// *
// *
// * @return
// */
// public boolean isDrawing() {
// return stillDrawing;
// }
//
// /**
// *
// *
// * @param isDrawing
// */
// public void setDrawing(boolean isDrawing) {
// stillDrawing = isDrawing;
// }
//
// /**
// *
// *
// * @param g2d
// * @param view
// */
// @Override
// public void drawPreview(Graphics2D g2d, AbstractMapView view) {
// if (mapVector.getPoints().size() < 1) {
// return;
// }
//
// Point cursor = view.getMapEditor().getCursorLocation();
// Point lastVectorPoint = mapVector.getPoints().get(mapVector.getPoints().size() - 1);
//
// int[] coordinates = { cursor.x, cursor.y };
//
// if (MainWindow.getInstance().isSnapToGrid()) {
// coordinates = MainWindow.getInstance().getCurrentMapEditor().calculateSnapCoordinates(cursor.x, cursor.y);
// }
//
// g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
// g2d.setColor(previewColor);
//
// int[] points = GuiHelper.ensureVectorVisible(view.getMap(), lastVectorPoint.x, lastVectorPoint.y,
// coordinates[0], coordinates[1]);
//
// int width = Math.abs(points[2] - points[0]);
// int height = Math.abs(points[3] - points[1]);
// g2d.drawRect(points[0], points[1], width, height);
// }
//
// /**
// *
// *
// * @param brush
// * @return
// */
// @Override
// public boolean equals(Brush brush) {
// return brush instanceof MapVectorAreaBrush && ((MapVectorAreaBrush) brush).mapVector.equals(mapVector);
// }
//
// /**
// *
// *
// * @param x
// * @param y
// * @param selection
// * @return
// * @throws Exception
// */
// @Override
// public Rectangle doPaint(int x, int y, Rectangle selection) throws Exception {
// MapLayerView mapLayerView = affectedContainer.getLayer(currentLayer);
//
// super.doPaint(x, y, selection);
//
// if (mapLayerView != null) {
// if (!stillDrawing) {
// stillDrawing = true;
// mapVector = new MapVector();
// mapVector.setLayer(currentLayer);
//
// affectedContainer.getLayer(currentLayer).getLayer().getVectors().add(mapVector);
// }
//
// int[] coordinates = calculateCoordinates(x, y);
// if (mapVector.addPoint(coordinates[0], coordinates[1])) {
// mapLayerView.getLayer().getMap().fireMapChanged();
// }
// }
//
// return null;
// }
//
// public void abort() {
// mapVector = new MapVector();
// stillDrawing = false;
// }
//
// public void finish(int x, int y) {
// // Top-left
// Point p1 = mapVector.getPoints().get(0);
// // Bottom-right
// int[] coordinates = calculateCoordinates(x, y);
// Point p3 = new Point(coordinates[0], coordinates[1]);
// // Top-right
// Point p2 = new Point(p3.x, p1.y);
// // Bottom-left
// Point p4 = new Point(p1.x, p3.y);
//
// // Add the remaining points to the map vector rectangle
// mapVector.addPoint(p2.x, p2.y);
// mapVector.addPoint(p3.x, p3.y);
// mapVector.addPoint(p4.x, p4.y);
// mapVector.setClosed(true);
//
// mapVector = new MapVector();
// stillDrawing = false;
// }
//
// /**
// *
// *
// * @param x
// * @param y
// * @param selection
// * @return
// * @throws Exception
// */
// protected Rectangle callRootPaint(int x, int y, Rectangle selection) throws Exception {
// // This is bad design, and is monkeying around with the inheritance
// // model by exposing access of parent class of this class to a child.
// // Should implement composition of inheritance in this case.
// return super.doPaint(x, y, selection);
// }
//
// @Override
// public void doMouseButton1Pressed(Point point, AbstractAssetEditorWindow editor) {
// if (stillDrawing) {
// finish(point.x, point.y);
// } else {
// MapEditor mapEditor = (MapEditor) editor;
// mapEditor.doPaint(this, point, null);
// }
// }
//
// @Override
// public void doMouseButton2Pressed(Point point, AbstractAssetEditorWindow editor) {
// if (editor instanceof MapEditor) {
// MapEditor mapEditor = (MapEditor) editor;
// if (stillDrawing) {
// finish(point.x, point.y);
// }
// RemoveVectorAction action = new RemoveVectorAction(mapEditor, point.x, point.y, false);
// action.actionPerformed(null);
// }
// }
//
// @Override
// public void doMouseButton3Pressed(Point point, AbstractAssetEditorWindow editor) {
// if (editor instanceof MapEditor) {
// MapEditor mapEditor = (MapEditor) editor;
// if (stillDrawing) {
// // We are drawing a vector, so lets abort it.
// abort();
// } else {
// // We want to select a vector.
// selectVector(
// mapEditor.getMapView().getCurrentSelectedLayer().getLayer().findVectorAt(point.x, point.y),
// mapEditor);
// }
// }
// }
//
// @Override
// public boolean doMouseButton1Dragged(Point point, Point origin, AbstractAssetEditorWindow editor) {
// return false;
// }
//
// @Override
// public boolean doMouseButton3Dragged(Point point, Point origin, AbstractAssetEditorWindow editor) {
// return false;
// }
//
// @Override
// public boolean isPixelBased() {
// return true;
// }
//
// /**
// *
// *
// * @param vector
// */
// private void selectVector(MapVector vector, MapEditor editor) {
// if (vector != null) {
// vector.setSelectedState(true);
// if (editor.getSelectedObject() != null) {
// editor.getSelectedObject().setSelectedState(false);
// }
// editor.setSelectedObject(vector);
// } else if (editor.getSelectedObject() != null) {
// editor.getSelectedObject().setSelectedState(false);
// editor.setSelectedObject(null);
// }
// }
//
// private int[] calculateCoordinates(int x, int y) {
// if (MainWindow.getInstance().isSnapToGrid()) {
// MainWindow.getInstance().getCurrentMapEditor().calculateSnapCoordinates(x, y);
// }
//
// int[] coordinates = { x, y };
//
// if (MainWindow.getInstance().isSnapToGrid()) {
// coordinates = MainWindow.getInstance().getCurrentMapEditor().calculateSnapCoordinates(x, y);
// }
//
// return coordinates;
// }
//
// }
