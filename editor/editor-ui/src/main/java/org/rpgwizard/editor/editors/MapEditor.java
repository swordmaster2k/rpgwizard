/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import lombok.Getter;
import lombok.Setter;
import org.rpgwizard.common.Selectable;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.common.assets.Trigger;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.assets.tileset.Tile;
import org.rpgwizard.common.assets.tileset.Tileset;
import org.rpgwizard.common.assets.map.MapLayer;
import org.rpgwizard.common.assets.events.MapChangedEvent;
import org.rpgwizard.common.assets.listeners.MapChangeListener;
import org.rpgwizard.common.assets.map.MapImage;
import org.rpgwizard.common.assets.map.MapSprite;
import org.rpgwizard.common.assets.map.SelectablePair;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.map.MapMouseAdapter;
import org.rpgwizard.editor.editors.map.MapView2D;
import org.rpgwizard.editor.editors.map.brush.AbstractBrush;
import org.rpgwizard.editor.editors.map.state.UndoRedoManager;
import org.rpgwizard.editor.editors.map.state.UndoRedoState;
import org.rpgwizard.editor.editors.map.state.UndoRedoType;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.actions.ActionHandler;
import org.rpgwizard.editor.ui.actions.CopyAction;
import org.rpgwizard.editor.ui.actions.CutAction;
import org.rpgwizard.editor.ui.actions.PasteAction;
import org.rpgwizard.editor.ui.actions.RedoAction;
import org.rpgwizard.editor.ui.actions.RemoveColliderAction;
import org.rpgwizard.editor.ui.actions.RemoveMapImageAction;
import org.rpgwizard.editor.ui.actions.RemoveSpriteAction;
import org.rpgwizard.editor.ui.actions.RemoveTriggerAction;
import org.rpgwizard.editor.ui.actions.SelectAllAction;
import org.rpgwizard.editor.ui.actions.UndoAction;
import org.rpgwizard.editor.ui.resources.Icons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
@Getter
@Setter
public final class MapEditor extends AbstractAssetEditorWindow
        implements MapChangeListener, KeyListener, ActionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapEditor.class);

    private JScrollPane scrollPane;

    private MapView2D mapView;
    private Map map;

    private MapMouseAdapter mapMouseAdapter;

    private Point cursorTileLocation;
    private Point cursorLocation;
    private Rectangle selection;

    private Tile[][] selectedTiles;

    private Selectable selectedObject;

    private UndoRedoManager undoRedoManager;

    /**
     * Default Constructor.
     */
    public MapEditor() {

    }

    public MapEditor(Map map) {
        super("Untitled", true, true, true, true, Icons.getIcon("map"));

        mapMouseAdapter = new MapMouseAdapter(this);
        this.map = map;
        this.map.addMapChangeListener(this);
        if (map.getDescriptor() == null) {
            init(map, "Untitled");
        } else {
            init(map, new File(map.getDescriptor().getUri()).getName());
        }
    }

    public static void prepareMap(Map map) {

    }

    @Override
    public AbstractAsset getAsset() {
        return map;
    }

    /**
     *
     * @return
     */
    public Rectangle getSelectionExpaned() {
        if (selection == null) {
            return null;
        }

        // TODO: To compensate for the fact that the selection
        // is 1 size too small in both width and height.
        // Bit of a hack really.
        Rectangle cloned = (Rectangle) selection.clone();
        cloned.width++;
        cloned.height++;

        return cloned;
    }

    /**
     *
     * @param object
     */
    public void setSelectedObject(Selectable object) {
        if (object == null) {
            selectedObject = map;
        } else {
            selectedObject = object;
        }

        MainWindow.getInstance().getPropertiesPanel().setModel(selectedObject);
        mapView.repaint();
    }

    /**
     * Zoom in on the map view.
     */
    public void zoomIn() {
        mapView.zoomIn();
        scrollPane.getViewport().revalidate();
    }

    /**
     * Zoom out on the map view.
     */
    public void zoomOut() {
        mapView.zoomOut();
        scrollPane.getViewport().revalidate();
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @Override
    public void save() throws Exception {
        // TODO: Keep track of TileSets as they get added and removed
        // rather than performing this slow bulk update.
        for (MapLayer mapLayer : map.getLayers()) {
            Tile[][] layerTiles = mapLayer.getLoadedTiles();

            int width = map.getWidth();
            int height = map.getHeight();
            int count = width * height;
            int x = 0;
            int y = 0;
            for (int j = 0; j < count; j++) {
                // Default values for a blank tile.
                int tileSetIndex = -1;
                int tileIndex = -1;

                Tile tile = layerTiles[x][y];
                if (tile != null) {
                    tileIndex = tile.getIndex();
                    if (tile.getTileSet() != null) {
                        Tileset tileset = tile.getTileSet();
                        if (!map.getTilesets().contains(tileset.getName())) {
                            map.getTilesets().add(tileset.getName());
                        }
                        tileSetIndex = map.getTilesets().indexOf(tileset.getName());
                    }
                }

                mapLayer.getTiles().add(j, tileSetIndex + ":" + tileIndex);

                x++;
                if (x == width) {
                    x = 0;
                    y++;
                    if (y == height) {
                        break;
                    }
                }
            }
        }

        map.setName(getTitle().replace("*", ""));

        save(map);
    }

    /**
     *
     *
     * @param file
     * @throws java.lang.Exception
     */
    @Override
    public void saveAs(File file) throws Exception {
        map.setDescriptor(new AssetDescriptor(file.toURI()));
        setTitle(file.getName());
        save();
    }

    /**
     *
     * @param rectangle
     */
    public void setSelection(Rectangle rectangle) {
        selection = rectangle;
        mapView.repaint();
    }

    /**
     *
     */
    public static void toggleSelectedOnMapEditor() {
        MapEditor editor = MainWindow.getInstance().getCurrentMapEditor();

        if (editor != null) {
            if (editor.getSelectedObject() != null) {
                editor.getSelectedObject().setSelectedState(false);
            }

            editor.setSelectedObject(null);
        }
    }

    /**
     *
     * @param brush
     * @param point
     * @param selection
     */
    public void doPaint(AbstractBrush brush, Point point, Rectangle selection) {
        try {
            if (brush == null) {
                return;
            }

            brush.startPaint(mapView, mapView.getCurrentSelectedLayer().getLayer().getNumber());
            brush.doPaint(point.x, point.y, selection);
            brush.endPaint();
        } catch (Exception ex) {
            LOGGER.error("Failed to paint on the map brush=[{}], point=[{}], selection=[{}]", brush, point, selection,
                    ex);
        }
    }

    /**
     *
     * @param rectangle
     * @return
     */
    public Tile[][] createTileLayerFromRegion(Rectangle rectangle) {
        Tile[][] tiles = new Tile[rectangle.width + 1][rectangle.height + 1];

        MapLayer mapLayer = mapView.getCurrentSelectedLayer().getLayer();
        for (int y = rectangle.y; y <= rectangle.y + rectangle.height; y++) {
            for (int x = rectangle.x; x <= rectangle.x + rectangle.width; x++) {
                tiles[x - rectangle.x][y - rectangle.y] = mapLayer.getTileAt(x, y);
            }
        }

        return tiles;
    }

    /**
     *
     *
     * @param x
     * @param y
     * @return
     */
    public int[] calculateSnapCoordinates(int x, int y) {
        int tileWidth = map.getTileWidth();
        int tileHeight = map.getTileHeight();
        int[] coordinates = { 0, 0 };

        int mx = x % tileWidth;
        int my = y % tileHeight;

        if (mx < tileWidth / 2) {
            coordinates[0] = x - mx;
        } else {
            coordinates[0] = x + (tileWidth - mx);
        }

        if (my < tileHeight / 2) {
            coordinates[1] = y - my;
        } else {
            coordinates[1] = y + (tileHeight - my);
        }

        return coordinates;
    }

    @Override
    public void mapChanged(MapChangedEvent e) {
        if (!e.isOpacityChanged() && !e.isLayerVisibilityToggled()) {
            undoRedoManager.push(new UndoRedoState(map, UndoRedoType.GENERAL));
            setNeedSave(true);
        }
    }

    @Override
    public void mapLayerAdded(MapChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(map, UndoRedoType.LAYER));
        setNeedSave(true);
    }

    @Override
    public void mapLayerMovedUp(MapChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(map, UndoRedoType.LAYER));
        setNeedSave(true);
    }

    @Override
    public void mapLayerMovedDown(MapChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(map, UndoRedoType.LAYER));
        setNeedSave(true);
    }

    @Override
    public void mapLayerCloned(MapChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(map, UndoRedoType.LAYER));
        setNeedSave(true);
    }

    @Override
    public void mapLayerDeleted(MapChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(map, UndoRedoType.LAYER));
        setNeedSave(true);
    }

    @Override
    public void mapSpriteAdded(MapChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(map, UndoRedoType.SPRITE));
        setNeedSave(true);
    }

    @Override
    public void mapSpriteMoved(MapChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(map, UndoRedoType.SPRITE));
        setNeedSave(true);
    }

    @Override
    public void mapSpriteRemoved(MapChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(map, UndoRedoType.SPRITE));
        setNeedSave(true);
    }

    @Override
    public void mapImageAdded(MapChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(map, UndoRedoType.IMAGE));
        setNeedSave(true);
    }

    @Override
    public void mapImageMoved(MapChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(map, UndoRedoType.IMAGE));
        setNeedSave(true);
    }

    @Override
    public void mapImageRemoved(MapChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(map, UndoRedoType.IMAGE));
        setNeedSave(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            if (selectedObject == null) {
                return;
            }
            if (selectedObject instanceof SelectablePair) {
                SelectablePair pair = (SelectablePair) selectedObject;
                Selectable selectable = (Selectable) pair.getRight();
                int layerIndex = mapView.getCurrentSelectedLayer().getLayer().getNumber();
                if (selectable instanceof MapSprite) {
                    RemoveSpriteAction action = new RemoveSpriteAction(this, layerIndex, pair.getLeft());
                    action.actionPerformed(null);
                } else if (selectable instanceof MapImage) {
                    RemoveMapImageAction action = new RemoveMapImageAction(this, layerIndex, pair.getLeft());
                    action.actionPerformed(null);
                } else if (selectable instanceof Collider) {
                    RemoveColliderAction action = new RemoveColliderAction(this, 0, 0, true);
                    action.actionPerformed(null);
                } else if (selectable instanceof Trigger) {
                    RemoveTriggerAction action = new RemoveTriggerAction(this, 0, 0, true);
                    action.actionPerformed(null);
                }
            }
        }
    }

    @Override
    public boolean canUndo() {
        return undoRedoManager.canUndo();
    }

    @Override
    public boolean canRedo() {
        return undoRedoManager.canRedo();
    }

    @Override
    public void handle(UndoAction action) {
        try {
            UndoRedoType oldType = UndoRedoType.GENERAL;
            if (!undoRedoManager.isEmpty()) {
                oldType = undoRedoManager.peek().getType();
            }

            final UndoRedoState newState = undoRedoManager.undo();
            map = newState.getMap();
            mapView.update(map);
            setSelectedObject(map);

            if (oldType == UndoRedoType.LAYER || newState.getType() == UndoRedoType.LAYER) {
                // Something layer based likely changed, tell the panel to update itself.
                MainWindow.getInstance().getLayerPanel().setMapView(mapView);
            } else {
                MainWindow.getInstance().getLayerPanel().updateTable();
            }
        } catch (IllegalStateException ex) {
            // Ignore it
        }
    }

    @Override
    public void handle(RedoAction action) {
        try {
            UndoRedoType oldType = UndoRedoType.GENERAL;
            if (!undoRedoManager.isEmpty()) {
                oldType = undoRedoManager.peek().getType();
            }

            final UndoRedoState newState = undoRedoManager.redo();
            map = newState.getMap();
            mapView.update(map);
            setSelectedObject(map);

            if (oldType == UndoRedoType.LAYER || newState.getType() == UndoRedoType.LAYER) {
                // Something layer based likely changed, tell the panel to update itself.
                MainWindow.getInstance().getLayerPanel().setMapView(mapView);
            } else {
                MainWindow.getInstance().getLayerPanel().updateTable();
            }
        } catch (IllegalStateException ex) {
            // Ignore it
        }
    }

    @Override
    public void handle(CutAction action) {
        // Not implemented yet
    }

    @Override
    public void handle(CopyAction action) {
        // Not implemented yet
    }

    @Override
    public void handle(PasteAction action) {
        // Not implemented yet
    }

    @Override
    public void handle(SelectAllAction action) {
        // Not implemented yet
    }

    private void init(Map map, String fileName) {
        mapView = new MapView2D(this, map);
        mapView.addMouseListener(mapMouseAdapter);
        mapView.addMouseMotionListener(mapMouseAdapter);
        mapView.addKeyListener(this);
        mapView.setFocusable(true);

        scrollPane = new JScrollPane(mapView);
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setFocusable(false);

        cursorTileLocation = new Point(0, 0);
        cursorLocation = new Point(0, 0);

        undoRedoManager = new UndoRedoManager(new UndoRedoState(new Map(map), UndoRedoType.GENERAL));

        setFocusable(false);
        setTitle(fileName);
        add(scrollPane);
        pack();
    }

}
