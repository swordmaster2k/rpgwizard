/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.editors.board;

import java.awt.Point;
import java.awt.Rectangle;
import net.rpgtoolkit.common.assets.Tile;
import net.rpgtoolkit.editor.editors.BoardEditor;
import net.rpgtoolkit.editor.ui.AssetEditorWindow;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class SelectionBrush extends CustomBrush {

	/**
	 *
	 *
	 * @param tiles
	 */
	public SelectionBrush(Tile[][] tiles) {
		super(tiles);
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
		// Do nothing on paint. Perhaps collect the selected tiles here?
		return null;
	}

	@Override
	public void doMouseButton1Pressed(Point point, AssetEditorWindow editor) {
		if (editor instanceof BoardEditor) {
			BoardEditor boardEditor = (BoardEditor) editor;

			boardEditor.setSelection(new Rectangle(point.x, point.y, 0, 0));
			boardEditor.setSelectedTiles(boardEditor
					.createTileLayerFromRegion(boardEditor.getSelection()));
		}
	}

	@Override
	public void doMouseButton2Pressed(Point point, AssetEditorWindow editor) {
		// No implementation.
	}

	@Override
	public void doMouseButton3Pressed(Point point, AssetEditorWindow editor) {
		// No implementation.
	}

	@Override
	public void doMouseButton1Dragged(Point point, Point origin,
			AssetEditorWindow editor) {
		if (editor instanceof BoardEditor) {
			BoardEditor boardEditor = (BoardEditor) editor;

			Rectangle select = new Rectangle(origin.x, origin.y, 0, 0);
			select.add(point);

			if (!select.equals(boardEditor.getSelection())) {
				boardEditor.setSelection(select);
			}
			boardEditor.setSelectedTiles(boardEditor
					.createTileLayerFromRegion(boardEditor.getSelection()));
		}
	}

	@Override
	public boolean isPixelBased() {
		return false;
	}

}
