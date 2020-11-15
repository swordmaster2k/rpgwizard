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
import org.rpgwizard.common.Selectable;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.assets.Tile;
import org.rpgwizard.common.assets.Tileset;
import org.rpgwizard.common.assets.board.BoardLayer;
import org.rpgwizard.common.assets.board.BoardLayerImage;
import org.rpgwizard.common.assets.board.BoardSprite;
import org.rpgwizard.common.assets.board.BoardVector;
import org.rpgwizard.common.assets.events.BoardChangedEvent;
import org.rpgwizard.common.assets.listeners.BoardChangeListener;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.board.BoardMouseAdapter;
import org.rpgwizard.editor.editors.board.BoardView2D;
import org.rpgwizard.editor.editors.board.brush.AbstractBrush;
import org.rpgwizard.editor.editors.board.state.UndoRedoManager;
import org.rpgwizard.editor.editors.board.state.UndoRedoState;
import org.rpgwizard.editor.editors.board.state.UndoRedoType;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.actions.ActionHandler;
import org.rpgwizard.editor.ui.actions.CopyAction;
import org.rpgwizard.editor.ui.actions.CutAction;
import org.rpgwizard.editor.ui.actions.PasteAction;
import org.rpgwizard.editor.ui.actions.RedoAction;
import org.rpgwizard.editor.ui.actions.RemoveLayerImageAction;
import org.rpgwizard.editor.ui.actions.RemoveSpriteAction;
import org.rpgwizard.editor.ui.actions.RemoveVectorAction;
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
public final class BoardEditor extends AbstractAssetEditorWindow
        implements BoardChangeListener, KeyListener, ActionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoardEditor.class);

    private JScrollPane scrollPane;

    private BoardView2D boardView;
    private Board board;

    private BoardMouseAdapter boardMouseAdapter;

    private Point cursorTileLocation;
    private Point cursorLocation;
    private Rectangle selection;

    private Tile[][] selectedTiles;

    private Selectable selectedObject;

    private UndoRedoManager undoRedoManager;

    /**
     * Default Constructor.
     */
    public BoardEditor() {

    }

    public BoardEditor(Board board) {
        super("Untitled", true, true, true, true, Icons.getIcon("board"));

        boardMouseAdapter = new BoardMouseAdapter(this);
        this.board = board;
        this.board.addBoardChangeListener(this);
        if (board.getDescriptor() == null) {
            init(board, "Untitled");
        } else {
            init(board, new File(board.getDescriptor().getURI()).getName());
        }
    }

    @Override
    public AbstractAsset getAsset() {
        return board;
    }

    /**
     *
     * @return
     */
    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    /**
     *
     * @param scrollPane
     */
    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    /**
     *
     * @return
     */
    public BoardView2D getBoardView() {
        return boardView;
    }

    /**
     *
     * @param boardView
     */
    public void setBoardView(BoardView2D boardView) {
        this.boardView = boardView;
    }

    /**
     *
     * @return
     */
    public Board getBoard() {
        return board;
    }

    /**
     *
     * @param board
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     *
     * @return
     */
    public Point getCursorTileLocation() {
        return cursorTileLocation;
    }

    /**
     *
     * @param location
     */
    public void setCursorTileLocation(Point location) {
        cursorTileLocation = location;
    }

    /**
     *
     * @return
     */
    public Point getCursorLocation() {
        return cursorLocation;
    }

    /**
     *
     * @param location
     */
    public void setCursorLocation(Point location) {
        cursorLocation = location;
    }

    /**
     *
     * @return
     */
    public Rectangle getSelection() {
        return selection;
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
     * @return
     */
    public Tile[][] getSelectedTiles() {
        return selectedTiles;
    }

    /**
     *
     * @param tiles
     */
    public void setSelectedTiles(Tile[][] tiles) {
        selectedTiles = tiles;
    }

    /**
     *
     * @return
     */
    public Selectable getSelectedObject() {
        return selectedObject;
    }

    /**
     *
     * @param object
     */
    public void setSelectedObject(Selectable object) {
        if (object == null) {
            selectedObject = board;
        } else {
            selectedObject = object;
        }

        MainWindow.getInstance().getPropertiesPanel().setModel(selectedObject);
        boardView.repaint();
    }

    /**
     * Zoom in on the board view.
     */
    public void zoomIn() {
        boardView.zoomIn();
        scrollPane.getViewport().revalidate();
    }

    /**
     * Zoom out on the board view.
     */
    public void zoomOut() {
        boardView.zoomOut();
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
        for (BoardLayer boardLayer : board.getLayers()) {
            Tile[][] layerTiles = boardLayer.getTiles();

            int width = board.getWidth();
            int height = board.getHeight();
            int count = width * height;
            int x = 0;
            int y = 0;
            for (int j = 0; j < count; j++) {
                Tileset tileSet = layerTiles[x][y].getTileSet();

                if (tileSet != null) {
                    if (!board.getTileSets().containsKey(tileSet.getName())) {
                        board.getTileSets().put(tileSet.getName(), tileSet);
                    }
                }

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

        board.setName(getTitle().replace("*", ""));

        save(board);
    }

    /**
     *
     *
     * @param file
     * @throws java.lang.Exception
     */
    @Override
    public void saveAs(File file) throws Exception {
        board.setDescriptor(new AssetDescriptor(file.toURI()));
        setTitle(file.getName());
        save();
    }

    /**
     *
     * @param rectangle
     */
    public void setSelection(Rectangle rectangle) {
        selection = rectangle;
        boardView.repaint();
    }

    /**
     *
     */
    public static void toggleSelectedOnBoardEditor() {
        BoardEditor editor = MainWindow.getInstance().getCurrentBoardEditor();

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

            brush.startPaint(boardView, boardView.getCurrentSelectedLayer().getLayer().getNumber());
            brush.doPaint(point.x, point.y, selection);
            brush.endPaint();
        } catch (Exception ex) {
            LOGGER.error("Failed to paint on the board brush=[{}], point=[{}], selection=[{}]", brush, point, selection,
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

        BoardLayer boardLayer = boardView.getCurrentSelectedLayer().getLayer();
        for (int y = rectangle.y; y <= rectangle.y + rectangle.height; y++) {
            for (int x = rectangle.x; x <= rectangle.x + rectangle.width; x++) {
                tiles[x - rectangle.x][y - rectangle.y] = boardLayer.getTileAt(x, y);
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
        int tileWidth = board.getTileWidth();
        int tileHeight = board.getTileHeight();
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
    public void boardChanged(BoardChangedEvent e) {
        if (!e.isOpacityChanged() && !e.isLayerVisibilityToggled()) {
            undoRedoManager.push(new UndoRedoState(board, UndoRedoType.GENERAL));
            setNeedSave(true);
        }
    }

    @Override
    public void boardLayerAdded(BoardChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(board, UndoRedoType.LAYER));
        setNeedSave(true);
    }

    @Override
    public void boardLayerMovedUp(BoardChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(board, UndoRedoType.LAYER));
        setNeedSave(true);
    }

    @Override
    public void boardLayerMovedDown(BoardChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(board, UndoRedoType.LAYER));
        setNeedSave(true);
    }

    @Override
    public void boardLayerCloned(BoardChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(board, UndoRedoType.LAYER));
        setNeedSave(true);
    }

    @Override
    public void boardLayerDeleted(BoardChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(board, UndoRedoType.LAYER));
        setNeedSave(true);
    }

    @Override
    public void boardSpriteAdded(BoardChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(board, UndoRedoType.SPRITE));
        setNeedSave(true);
    }

    @Override
    public void boardSpriteRemoved(BoardChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(board, UndoRedoType.SPRITE));
        setNeedSave(true);
    }

    @Override
    public void boardLayerImageAdded(BoardChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(board, UndoRedoType.IMAGE));
        setNeedSave(true);
    }

    @Override
    public void boardLayerImageRemoved(BoardChangedEvent e) {
        undoRedoManager.push(new UndoRedoState(board, UndoRedoType.IMAGE));
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
            if (selectedObject instanceof BoardSprite) {
                RemoveSpriteAction action = new RemoveSpriteAction(this, (BoardSprite) selectedObject);
                action.actionPerformed(null);
            } else if (selectedObject instanceof BoardLayerImage) {
                RemoveLayerImageAction action = new RemoveLayerImageAction(this, (BoardLayerImage) selectedObject);
                action.actionPerformed(null);
            } else if (selectedObject instanceof BoardVector) {
                BoardVector boardVector = (BoardVector) selectedObject;
                Point point = boardVector.getPoints().get(0);
                RemoveVectorAction action = new RemoveVectorAction(this, point.x, point.y, true);
                action.actionPerformed(null);
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
            board = newState.getBoard();
            boardView.update(board);
            setSelectedObject(board);

            if (oldType == UndoRedoType.LAYER || newState.getType() == UndoRedoType.LAYER) {
                // Something layer based likely changed, tell the panel to update itself.
                MainWindow.getInstance().getLayerPanel().setBoardView(boardView);
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
            board = newState.getBoard();
            boardView.update(board);
            setSelectedObject(board);

            if (oldType == UndoRedoType.LAYER || newState.getType() == UndoRedoType.LAYER) {
                // Something layer based likely changed, tell the panel to update itself.
                MainWindow.getInstance().getLayerPanel().setBoardView(boardView);
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

    private void init(Board board, String fileName) {
        boardView = new BoardView2D(this, board);
        boardView.addMouseListener(boardMouseAdapter);
        boardView.addMouseMotionListener(boardMouseAdapter);
        boardView.addKeyListener(this);
        boardView.setFocusable(true);

        scrollPane = new JScrollPane(boardView);
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setFocusable(false);

        cursorTileLocation = new Point(0, 0);
        cursorLocation = new Point(0, 0);

        undoRedoManager = new UndoRedoManager(new UndoRedoState(new Board(board), UndoRedoType.GENERAL));

        setFocusable(false);
        setTitle(fileName);
        add(scrollPane);
        pack();
    }

}
