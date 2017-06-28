/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import org.rpgwizard.common.assets.Tile;
import org.rpgwizard.editor.editors.BoardEditor;
import org.rpgwizard.editor.ui.AssetEditorWindow;

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
		return brush instanceof EraserBrush
				&& ((EraserBrush) brush).shape.equals(shape);
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
	public Rectangle doPaint(int x, int y, Rectangle selection)
			throws Exception {
		super.doPaint(x, y, selection);

		if (selection != null && selection.contains(x, y)) {
			BoardLayerView layer = affectedContainer.getLayer(currentLayer);

			if (layer == null) {
				return null;
			}
			if (selection.contains(x, y)) {
				for (int y2 = selection.y; y2 < selection.height + selection.y; y2++) {
					for (int x2 = selection.x; x2 < selection.width
							+ selection.x; x2++) {
						layer.getLayer().setTileAt(x2, y2, paintTile);
					}
				}
			}
			return selection;
		} else {
			Rectangle shapeBounds = shape.getBounds();
			int centerX = x - shapeBounds.width / 2;
			int centerY = y - shapeBounds.height / 2;

			for (int layer = 0; layer < affectedLayers; layer++) {
				BoardLayerView boardLayer = affectedContainer
						.getLayer(currentLayer + layer);

				if (boardLayer != null) {
					for (int i = 0; i <= shapeBounds.height + 1; i++) {
						for (int j = 0; j <= shapeBounds.width + 1; j++) {
							if (shape.contains(i, j)) {
								boardLayer.getLayer().setTileAt(j + centerX,
										i + centerY, paintTile);
							}
						}
					}
				}
			}

			return new Rectangle(centerX, centerY, shapeBounds.width,
					shapeBounds.height);
		}
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
	public void doMouseButton1Dragged(Point point, Point origin,
			AssetEditorWindow editor) {

	}

}
