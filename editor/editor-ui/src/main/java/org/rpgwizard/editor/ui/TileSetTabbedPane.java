/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Collection;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import org.rpgwizard.common.assets.TileSet;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.tileset.TileSetCanvas;
import org.rpgwizard.editor.editors.tileset.TileSetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public final class TileSetTabbedPane extends JClosableTabbedPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(TileSetTabbedPane.class);

    public TileSetTabbedPane() {
        setFont(new Font(getFont().getFontName(), Font.PLAIN, 10));
        setTabPlacement(JTabbedPane.BOTTOM);

        addMouseListener();
        addTabChangeListener();
    }

    public void addTileSet(TileSet tileSet) {
        String tabName = tileSet.getName().replace(CoreProperties.getDefaultExtension(TileSet.class), "");

        if (indexOfTab(tabName) < 0) {
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

            TileSetCanvas canvas = new TileSetCanvas(tileSet);
            canvas.addTileSelectionListener(MainWindow.getInstance().getTileSetSelectionListener());

            scrollPane.setViewportView(canvas);
            scrollPane.getViewport().revalidate();

            addTab(tabName, scrollPane);
            setSelectedIndex(indexOfTab(tabName));
        }
    }

    public void addTileSets(Collection<TileSet> tileSets) {
        for (TileSet tileSet : tileSets) {
            addTileSet(tileSet);
        }
    }

    /**
     * Provides a means for Tilesets to be reloaded, eventually this should be done through dynamic asset reloading on
     * change detection.
     * 
     * TODO: Replace this when dynamic asset reloading exists.
     */
    public void reloadTileSets() {
        for (int i = 0; i < getTabCount(); i++) {
            try {
                Component component = getComponentAt(i);
                JScrollPane scrollPane = (JScrollPane) component;
                TileSetCanvas canvas = (TileSetCanvas) scrollPane.getViewport().getComponent(0);
                TileSet tileset = canvas.getTileSet();
                tileset.clearTiles();
                tileset = TileSetUtil.load(tileset);
                canvas.setTileSet(tileset);
                canvas.revalidate();
                canvas.repaint();
            } catch (IOException ex) {
                LOGGER.error("Could not reload tileset!", ex);
            }
        }
    }

    public void removeTileSets() {
        removeAll();
    }

    private void addMouseListener() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                JTabbedPane tabs = (JTabbedPane) e.getSource();

                int index = tabs.indexAtLocation(e.getX(), e.getY());

                if (index > -1 && e.getButton() == MouseEvent.BUTTON2) {
                    tabs.remove(index);
                }
            }

        });
    }

    private void addTabChangeListener() {
        addChangeListener((ChangeEvent e) -> {
            JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
            int index = sourceTabbedPane.getSelectedIndex();

            if (index > -1) {

            }
        });
    }
}
