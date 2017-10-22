/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.assets.BoardSprite;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.BoardEditor;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;

/**
 *
 * @author Joshua Michael Daly
 */
public class BoardSpriteBrush extends AbstractBrush {

	private BoardSprite boardSprite;

	/**
     *
     */
	public BoardSpriteBrush() {
		boardSprite = new BoardSprite();
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
	public BoardSprite getBoardSprite() {
		return boardSprite;
	}

	/**
	 *
	 *
	 * @param boardSprite
	 */
	public void setBoardSprite(BoardSprite boardSprite) {
		this.boardSprite = boardSprite;
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
				x = Math.max(0, Math.min(x / board.getTileWidth(),
						board.getWidth() - 1))
						* board.getTileWidth() + (board.getTileWidth() / 2);
				y = Math.max(0, Math.min(y / board.getTileHeight(),
						board.getHeight() - 1))
						* board.getTileHeight() + (board.getTileHeight() / 2);
			}

			boardSprite = new BoardSprite();
			boardSprite.setX(x);
			boardSprite.setY(y);
			boardSprite.setLayer(currentLayer);

			board.addSprite(boardSprite);

			int centerX = x - shapeBounds.width / 2;
			int centerY = y - shapeBounds.height / 2;

			return new Rectangle(centerX, centerY, shapeBounds.width,
					shapeBounds.height);
		} else {
			return null;
		}
	}

	/**
	 *
	 *
	 * @param brush
	 * @return
	 */
	@Override
	public boolean equals(Brush brush) {
		return brush instanceof BoardSpriteBrush
				&& ((BoardSpriteBrush) brush).boardSprite.equals(boardSprite);
	}

	@Override
	public void doMouseButton1Pressed(Point point, AbstractAssetEditorWindow editor) {

	}

	@Override
	public void doMouseButton2Pressed(Point point, AbstractAssetEditorWindow editor) {
		if (editor instanceof BoardEditor) {
			BoardEditor boardEditor = (BoardEditor) editor;
			boardEditor.getBoard().removeSprite(boardSprite);

			if (boardSprite == boardEditor.getSelectedObject()) {
				boardEditor.getSelectedObject().setSelectedState(false);
				boardEditor.setSelectedObject(null);
			}
		}
	}

	@Override
	public void doMouseButton3Pressed(Point point, AbstractAssetEditorWindow editor) {
		if (editor instanceof BoardEditor) {
			BoardEditor boardEditor = (BoardEditor) editor;

			BoardSprite sprite = boardEditor.getBoardView()
					.getCurrentSelectedLayer().getLayer()
					.findSpriteAt(point.x, point.y);
			boardSprite = sprite;
			selectSprite(sprite, boardEditor);
		}
	}

	@Override
	public void doMouseButton1Dragged(Point point, Point origin,
			AbstractAssetEditorWindow editor) {

	}

	@Override
	public boolean isPixelBased() {
		return true;
	}

	/**
	 *
	 *
	 * @param sprite
	 */
	private void selectSprite(BoardSprite sprite, BoardEditor editor) {
		if (sprite != null) {

			if (editor.getSelectedObject() == sprite) {
				return;
			}

			sprite.setSelectedState(true);

			if (editor.getSelectedObject() != null) {
				editor.getSelectedObject().setSelectedState(false);
			}

			editor.setSelectedObject(sprite);
		} else if (editor.getSelectedObject() != null) {
			editor.getSelectedObject().setSelectedState(false);
			editor.setSelectedObject(null);
		}
	}

}
