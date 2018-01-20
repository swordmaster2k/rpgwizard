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
import java.awt.image.BufferedImage;
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.assets.board.BoardLayerImage;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.BoardEditor;
import org.rpgwizard.editor.editors.board.AbstractBoardView;
import org.rpgwizard.editor.editors.board.BoardLayerView;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.actions.RemoveLayerImageAction;

/**
 *
 * @author Joshua Michael Daly
 */
public class BoardLayerImageBrush extends AbstractBrush {

	private BoardLayerImage boardLayerImage;

	public BoardLayerImageBrush() {
		boardLayerImage = new BoardLayerImage();
	}

	public BoardLayerImage getBoardLayerImage() {
		return boardLayerImage;
	}

	public void setBoardLayerImage(BoardLayerImage boardLayerImage) {
		this.boardLayerImage = boardLayerImage;
	}

	@Override
	public Shape getShape() {
		return getBounds();
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

		BoardLayerView boardLayerView = affectedContainer
				.getLayer(currentLayer);

		if (boardLayerView != null) {
			boolean snap = MainWindow.getInstance().isSnapToGrid();
			Board board = boardLayerView.getLayer().getBoard();
			Rectangle shapeBounds = getBounds();

			if (snap) {
				Point point = getSnapPoint(board, x, y);
				x = point.x;
				y = point.y;
			} else {
				x -= (board.getTileWidth() / 2);
				y -= (board.getTileHeight() / 2);
			}

			boardLayerImage = new BoardLayerImage(x, y, currentLayer);
			board.addLayerImage(boardLayerImage);

			int centerX = x - shapeBounds.width / 2;
			int centerY = y - shapeBounds.height / 2;

			return new Rectangle(centerX, centerY, shapeBounds.width,
					shapeBounds.height);
		} else {
			return null;
		}
	}

	@Override
	public void doMouseButton1Pressed(Point point,
			AbstractAssetEditorWindow editor) {

	}

	@Override
	public void doMouseButton2Pressed(Point point,
			AbstractAssetEditorWindow editor) {
		if (editor instanceof BoardEditor) {
			BoardEditor boardEditor = (BoardEditor) editor;
			RemoveLayerImageAction action = new RemoveLayerImageAction(
					boardEditor, boardLayerImage);
			action.actionPerformed(null);
		}
	}

	@Override
	public void doMouseButton3Pressed(Point point,
			AbstractAssetEditorWindow editor) {
		if (editor instanceof BoardEditor) {
			BoardEditor boardEditor = (BoardEditor) editor;
			BufferedImage defaultImage = BoardLayerView.getPlaceHolderImage();
			BoardLayerImage image = boardEditor
					.getBoardView()
					.getCurrentSelectedLayer()
					.getLayer()
					.findImageAt(point.x, point.y, defaultImage.getWidth(),
							defaultImage.getHeight());
			boardLayerImage = image;
			selectImage(image, boardEditor);
		}
	}

	@Override
	public void doMouseButton1Dragged(Point point, Point origin,
			AbstractAssetEditorWindow editor) {

	}

	@Override
	public void doMouseButton3Dragged(Point point, Point origin,
			AbstractAssetEditorWindow editor) {
		if (editor instanceof BoardEditor) {
			BoardEditor boardEditor = (BoardEditor) editor;
			if (boardEditor.getSelectedObject() == boardLayerImage) {
				Dimension dimension = boardEditor.getBoard()
						.getBoardPixelDimensions();
				Board board = boardEditor.getBoard();
				if (checkDragBounds(point.x, point.y, dimension.width,
						dimension.height)) {
					if (MainWindow.getInstance().isSnapToGrid()) {
						point = getSnapPoint(boardEditor.getBoard(), point.x,
								point.y);
					} else {
						point.x -= (board.getTileWidth() / 2);
						point.y -= (board.getTileHeight() / 2);
					}
					boardLayerImage.setPosition(point.x, point.y);
					boardEditor.getBoardView().repaint();
				}
			}
		}
	}

	@Override
	public boolean isPixelBased() {
		return true;
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(0, 0, 1, 1);
	}

	@Override
	public void drawPreview(Graphics2D g2d, AbstractBoardView view) {

	}

	@Override
	public boolean equals(Brush brush) {
		return brush instanceof BoardLayerImageBrush
				&& ((BoardLayerImageBrush) brush).boardLayerImage
						.equals(boardLayerImage);
	}

	private Point getSnapPoint(Board board, int x, int y) {
		x = Math.max(0,
				Math.min(x / board.getTileWidth(), board.getWidth() - 1))
				* board.getTileWidth();
		y = Math.max(0,
				Math.min(y / board.getTileHeight(), board.getHeight() - 1))
				* board.getTileHeight();

		return new Point(x, y);
	}

	/**
	 *
	 *
	 * @param image
	 */
	private void selectImage(BoardLayerImage image, BoardEditor editor) {
		if (image != null) {
			if (editor.getSelectedObject() == image) {
				return;
			}

			image.setSelectedState(true);

			if (editor.getSelectedObject() != null) {
				editor.getSelectedObject().setSelectedState(false);
			}

			editor.setSelectedObject(image);
		} else if (editor.getSelectedObject() != null) {
			editor.getSelectedObject().setSelectedState(false);
			editor.setSelectedObject(null);
		}
	}

}
