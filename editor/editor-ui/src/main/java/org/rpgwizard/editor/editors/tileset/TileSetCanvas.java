/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.tileset;

import org.rpgwizard.editor.ui.listeners.TileSelectionListener;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;

import org.rpgwizard.common.assets.Tile;
import org.rpgwizard.common.assets.TileSet;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 * TODO: Deal with TileRegionSelectionEvents at some later date...
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public final class TileSetCanvas extends JPanel implements Scrollable {

    private int tilesPerRow;
    private final int maxTilesPerRow;

    private final LinkedList<TileSelectionListener> tileSelectionListeners = new LinkedList<>();

    private final TileSet tileSet;
    private BufferedImage bufferedImage;

    private Rectangle selection;

    private final TileSetMouseAdapter tileSetMouseAdapter;

    /**
     *
     * @param tileSet
     */
    public TileSetCanvas(TileSet tileSet) {
        super();

        this.tileSet = tileSet;

        int width = tileSet.getBufferedImage().getWidth();
        tilesPerRow = width / tileSet.getTileWidth();
        maxTilesPerRow = tileSet.getTiles().size();
        int height = tileSet.getTileHeight() * (int) (Math.ceil(tileSet.getTiles().size() / (double) tilesPerRow));

        init(width, height);

        Action increaseTilesAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!(tilesPerRow + 1 > maxTilesPerRow)) {
                    changeTilesPerRow(1);
                }
            }
        };
        this.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0),
                "increaseTilesAction");
        this.getActionMap().put("increaseTilesAction", increaseTilesAction);

        Action decreaseTilesAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (tilesPerRow - 1 > 0) {
                    changeTilesPerRow(-1);
                }
            }
        };
        this.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0),
                "decreaseTilesAction");
        this.getActionMap().put("decreaseTilesAction", decreaseTilesAction);

        tileSetMouseAdapter = new TileSetMouseAdapter();
        addMouseListener(tileSetMouseAdapter);
        addMouseMotionListener(tileSetMouseAdapter);
    }

    /**
     *
     * @return
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());
    }

    /**
     *
     * @param listener
     */
    public void addTileSelectionListener(TileSelectionListener listener) {
        tileSelectionListeners.add(listener);
    }

    /**
     *
     * @param listener
     */
    public void removeTileSelectionListener(TileSelectionListener listener) {
        tileSelectionListeners.remove(listener);
    }

    /**
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        paintBackground(g);

        Graphics2D g2d = bufferedImage.createGraphics();
        paintTileSet(g2d);
        paintGrid(g2d);

        if (selection != null) {
            paintSelection(g2d);
        }

        g.drawImage(bufferedImage, 0, 0, this);
    }

    /**
     *
     * @return
     */
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        if (tileSet != null) {
            int tileWidth = tileSet.getTileWidth() + 1;

            return new Dimension(tilesPerRow * tileWidth + 1, 200);
        } else {
            return new Dimension(0, 0);
        }
    }

    /**
     *
     * @param visibleRect
     * @param orientation
     * @param direction
     * @return
     */
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (tileSet != null) {
            return tileSet.getTileWidth();
        } else {
            return 0;
        }
    }

    /**
     *
     * @param visibleRect
     * @param orientation
     * @param direction
     * @return
     */
    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (tileSet != null) {
            return tileSet.getTileWidth();
        } else {
            return 0;
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return tileSet == null || tilesPerRow == 0;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public void changeTilesPerRow(int increment) {
        tilesPerRow += increment;
        int width = tileSet.getTileHeight() * tilesPerRow;
        int height = tileSet.getTileHeight() * (int) (Math.ceil(tileSet.getTiles().size() / (double) tilesPerRow));
        init(width, height);
        revalidate();
        repaint();
    }

    private void fireTileSelectionEvent(Tile selectedTile) {
        TileSelectionEvent event = new TileSelectionEvent(this, selectedTile);

        for (TileSelectionListener listener : tileSelectionListeners) {
            listener.tileSelected(event);
        }
    }

    private void fireTileRegionSelectionEvent(Rectangle selection) {
        Tile[][] region = createTileLayerFromRegion(selection);
        TileRegionSelectionEvent event = new TileRegionSelectionEvent(this, region);

        for (TileSelectionListener listener : tileSelectionListeners) {
            listener.tileRegionSelected(event);
        }
    }

    private Tile[][] createTileLayerFromRegion(Rectangle rectangle) {
        Tile[][] tiles = new Tile[rectangle.width + 1][rectangle.height + 1];

        for (int y = rectangle.y; y <= rectangle.y + rectangle.height; y++) {
            for (int x = rectangle.x; x <= rectangle.x + rectangle.width; x++) {
                tiles[x - rectangle.x][y - rectangle.y] = getTileAt(x, y);
            }
        }

        return tiles;
    }

    private void paintBackground(Graphics g) {
        Rectangle clipRectangle = g.getClipBounds();
        int side = tilesPerRow;

        int startX = clipRectangle.x / side;
        int startY = clipRectangle.y / side;
        int endX = (clipRectangle.x + clipRectangle.width) / side + 1;
        int endY = (clipRectangle.y + clipRectangle.height) / side + 1;

        // Fill with white background.
        g.setColor(Color.WHITE);
        g.fillRect(clipRectangle.x, clipRectangle.y, clipRectangle.width, clipRectangle.height);

        // Draw darker squares.
        g.setColor(Color.LIGHT_GRAY);

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                if ((y + x) % 2 == 1) {
                    g.fillRect(x * side, y * side, side, side);
                }
            }
        }
    }

    private void paintTileSet(Graphics2D g2d) {
        int x = 0;
        int y = 0;
        int i = 0;

        for (Tile tile : tileSet.getTiles()) {
            g2d.drawImage(tile.getTileAsImage(), x, y, this);

            // Update coordinates to draw at.
            x += tileSet.getTileWidth();
            i++;
            if (i % tilesPerRow == 0) {
                y += tileSet.getTileHeight();
                x = 0;
            }
        }
    }

    private void paintGrid(Graphics2D g2d) {
        GuiHelper.drawGrid(g2d, tileSet.getTileWidth(), tileSet.getTileHeight(),
                new Rectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
    }

    private void paintSelection(Graphics2D g2d) {
        g2d.setColor(new Color(100, 100, 255));
        g2d.draw3DRect(selection.x * tileSet.getTileWidth(), selection.y * tileSet.getTileHeight(),
                (selection.width + 1) * tileSet.getTileWidth(), (selection.height + 1) * tileSet.getTileHeight(),
                false);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.2f));
        g2d.fillRect(selection.x * tileSet.getTileWidth() + 1, selection.y * tileSet.getTileHeight() + 1,
                (selection.width + 1) * tileSet.getTileWidth() - 1,
                (selection.height + 1) * tileSet.getTileHeight() - 1);
    }

    private void scrollTileToVisible(Point tile) {
        int tileWidth = tileSet.getTileWidth() + 1;
        int tileHeight = tileSet.getTileHeight() + 1;

        scrollRectToVisible(new Rectangle(tile.x * tileWidth, tile.y * tileHeight, tileWidth + 1, tileHeight + 1));
    }

    /**
     * Converts pixel coordinates to tile coordinates. The returned coordinates are at least 0 and adjusted with respect
     * to the number of tiles per row and the number of rows.
     *
     * @param x
     *            x coordinate
     * @param y
     *            y coordinate
     * @return tile coordinates
     */
    private Point getTileCoordinates(int x, int y) {
        int tileWidth = tileSet.getTileWidth();
        int tileHeight = tileSet.getTileHeight();

        int tileX = Math.round(x / tileWidth);
        int tileY = Math.round(y / tileHeight);

        return new Point(tileX, tileY);
    }

    /**
     * Retrieves the tile at the given tile coordinates. It assumes the tile coordinates are adjusted to the number of
     * tiles per row.
     *
     * @param x
     *            x tile coordinate
     * @param y
     *            y tile coordinate
     * @return the tile at the given tile coordinates, or <code>null</code> if the index is out of range
     */
    private Tile getTileAt(int x, int y) {
        int tileAt = y * tilesPerRow + x;

        if (tileAt >= tileSet.getTiles().size()) {
            return null;
        } else {
            return tileSet.getTile(tileAt);
        }
    }

    private void setSelection(Rectangle rectangle) {
        selection = rectangle;
        repaint();
    }

    private void init(int width, int height) {
        if (height == 0) {
            height = 32;
        }
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    private class TileSetMouseAdapter extends MouseAdapter {

        private Point origin;

        public TileSetMouseAdapter() {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            requestFocus();

            origin = getTileCoordinates(e.getX(), e.getY());
            setSelection(new Rectangle(origin.x, origin.y, 0, 0));
            scrollTileToVisible(origin);

            Tile clickedTile = getTileAt(origin.x, origin.y);

            if (clickedTile != null) {
                fireTileSelectionEvent(clickedTile);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            requestFocus();

            Point point = getTileCoordinates(e.getX(), e.getY());

            if (point.x >= tilesPerRow) {
                return;
            }

            Rectangle select = new Rectangle(origin.x, origin.y, 0, 0);
            select.add(point);

            if (!select.equals(selection)) {
                setSelection(select);
                scrollTileToVisible(point);
            }

            if (selection.getWidth() > 0 || selection.getHeight() > 0) {
                fireTileRegionSelectionEvent(selection);
            }

        }

    }

}
