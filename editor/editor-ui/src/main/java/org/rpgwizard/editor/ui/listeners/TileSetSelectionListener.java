/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rpgwizard.editor.ui.listeners;

import java.awt.Rectangle;
import org.rpgwizard.common.assets.tileset.Tile;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.tileset.TileRegionSelectionEvent;
import org.rpgwizard.editor.editors.tileset.TileSelectionEvent;
import org.rpgwizard.editor.editors.board.brush.Brush;
import org.rpgwizard.editor.editors.board.brush.BucketBrush;
import org.rpgwizard.editor.editors.board.brush.CustomBrush;
import org.rpgwizard.editor.editors.board.brush.ShapeBrush;

/**
 *
 * @author Joshua Michael Daly
 */
public class TileSetSelectionListener implements TileSelectionListener {

    @Override
    public void tileSelected(TileSelectionEvent e) {
        MainWindow mainWindow = MainWindow.getInstance();

        Brush currentBrush = mainWindow.getCurrentBrush();
        Tile lastSelectedTile = mainWindow.getLastSelectedTile();

        if (currentBrush instanceof ShapeBrush) {
            ((ShapeBrush) currentBrush).setTile(e.getTile());
            mainWindow.getMainToolBar().getPencilButton().setSelected(true);
        } else if (currentBrush instanceof BucketBrush) {
            ((BucketBrush) currentBrush).setPourTile(e.getTile());
        } else {
            ShapeBrush shapeBrush = new ShapeBrush();
            shapeBrush.setTile(e.getTile());
            shapeBrush.makeRectangleBrush(new Rectangle(0, 0, 1, 1));
            mainWindow.setCurrentBrush(shapeBrush);
            mainWindow.getMainToolBar().getPencilButton().setSelected(true);
        }

        if (lastSelectedTile != e.getTile()) {
            mainWindow.setLastSelectedTile(e.getTile());
        }
    }

    @Override
    public void tileRegionSelected(TileRegionSelectionEvent e) {
        MainWindow mainWindow = MainWindow.getInstance();

        Brush currentBrush = MainWindow.getInstance().getCurrentBrush();

        if (!(currentBrush instanceof CustomBrush)) {
            mainWindow.setCurrentBrush(new CustomBrush(e.getTiles()));
        } else {
            ((CustomBrush) currentBrush).setTiles(e.getTiles());
        }

        mainWindow.getMainToolBar().getPencilButton().setSelected(true);
    }

}
