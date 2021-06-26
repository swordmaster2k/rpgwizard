/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rtextarea.SearchContext;
import org.pf4j.PluginManager;
import org.rpgwizard.common.assets.animation.Animation;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.Image;
import org.rpgwizard.common.assets.Script;
import org.rpgwizard.common.assets.game.Game;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.assets.sprite.Sprite;
import org.rpgwizard.common.assets.tileset.Tile;
import org.rpgwizard.common.assets.tileset.Tileset;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.common.utilities.TileSetCache;
import org.rpgwizard.editor.editors.AnimationEditor;
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.editors.ScriptEditor;
import org.rpgwizard.editor.editors.GameEditor;
import org.rpgwizard.editor.editors.SpriteEditor;
import org.rpgwizard.editor.editors.map.brush.AbstractBrush;
import org.rpgwizard.editor.editors.map.brush.ShapeBrush;
import org.rpgwizard.editor.editors.image.ImageEditor;
import org.rpgwizard.editor.editors.map.brush.AbstractPolygonBrush;
import org.rpgwizard.editor.editors.map.panels.LayerPanel;
import org.rpgwizard.editor.editors.script.IssuesTablePanel;
import org.rpgwizard.editor.editors.tileset.TileSetUtil;
import org.rpgwizard.editor.properties.EditorProperties;
import org.rpgwizard.editor.properties.EditorProperty;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.EditorFactory;
import org.rpgwizard.editor.ui.GoToFileDialog;
import org.rpgwizard.editor.ui.project.ProjectPanel;
import org.rpgwizard.editor.ui.PropertiesPanel;
import org.rpgwizard.editor.ui.TileSetTabbedPane;
import org.rpgwizard.editor.ui.WizardDesktopManager;
import org.rpgwizard.editor.ui.actions.ActionHandler;
import org.rpgwizard.editor.ui.actions.asset.NewMapAction;
import org.rpgwizard.editor.ui.actions.asset.NewSpriteAction;
import org.rpgwizard.editor.ui.actions.asset.NewTilesetAction;
import org.rpgwizard.editor.ui.listeners.TileSetSelectionListener;
import org.rpgwizard.editor.ui.log.LogPanel;
import org.rpgwizard.editor.ui.menu.MainMenuBar;
import org.rpgwizard.editor.ui.resources.Icons;
import org.rpgwizard.editor.ui.toolbar.MainToolBar;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.FileTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main UI, holds all Asset editors as InternalFrames. This class deals with opening existing assets and creating new
 * ones.
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
@Getter
@Setter
public final class MainWindow extends JFrame implements InternalFrameListener, SearchListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainWindow.class);

    // Singleton.
    private static final MainWindow INSTANCE = new MainWindow();

    private final JDesktopPane desktopPane;
    private final java.util.Map<File, AbstractAssetEditorWindow> editorMap;

    private final MainMenuBar mainMenuBar;
    private final MainToolBar mainToolBar;

    private final JPanel westPanel;
    private final JTabbedPane westUpperTabbedPane;
    private final JTabbedPane westLowerTabbedPane;

    private final JPanel eastPanel;
    private final JTabbedPane eastUpperTabbedPane;
    private final JTabbedPane eastLowerTabbedPane;

    private final JPanel southPanel;
    private final JTabbedPane southTabbedPane;
    private final LogPanel logPanel;
    private final IssuesTablePanel issuesPanel;

    private final ProjectPanel projectPanel;
    private final TileSetTabbedPane tileSetPanel;
    private final PropertiesPanel propertiesPanel;
    private final LayerPanel layerPanel;

    // Game Related.
    private Game activeProject;

    // Map Related.
    private boolean showGrid;
    private boolean showVectors;
    private boolean showCoordinates;
    private boolean snapToGrid;
    private AbstractBrush currentBrush;
    private Tile lastSelectedTile;

    // Script Related.
    private final FindDialog findDialog;
    private final ReplaceDialog replaceDialog;
    private final GoToFileDialog goToFileDialog;

    // Listeners.
    private final TileSetSelectionListener tileSetSelectionListener;

    private PluginManager pluginManager;

    // Simple flag that can be set to cancel closing of application.
    private boolean cancelClose;

    private MainWindow() {
        ///
        /// super
        ///
        super(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TITLE));
        ///
        /// desktopPane
        ///
        desktopPane = new JDesktopPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (UIManager.getLookAndFeel().getClass().equals(FlatLightLaf.class)) {
                    g.setColor(new Color(217, 217, 217));
                } else {
                    g.setColor(new Color(48, 50, 52));
                }
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        desktopPane.setDesktopManager(new WizardDesktopManager());
        ///
        /// editorMap
        ///
        editorMap = new HashMap();
        ///
        /// tileSetPanel
        ///
        tileSetPanel = new TileSetTabbedPane();
        ///
        /// projectPanel
        ///
        projectPanel = new ProjectPanel();
        ///
        /// westUpperTabbedPane
        ///
        westUpperTabbedPane = new JTabbedPane();
        westUpperTabbedPane.addTab("Project", this.projectPanel);
        westUpperTabbedPane.addTab("Tileset", tileSetPanel);
        ///
        /// layerPanel
        ///
        layerPanel = new LayerPanel();
        ///
        /// propertiesPanel
        ///
        propertiesPanel = new PropertiesPanel();
        ///
        /// westLowerTabbedPane
        ///
        westLowerTabbedPane = new JTabbedPane();
        westLowerTabbedPane.addTab("Layers", layerPanel);
        westLowerTabbedPane.addTab("Properties", propertiesPanel);
        ///
        /// westPanel
        ///
        westPanel = new JPanel(new GridLayout(2, 1));
        westPanel.setPreferredSize(new Dimension(350, 0));
        westPanel.add(westUpperTabbedPane);
        westPanel.add(westLowerTabbedPane);
        ///
        /// eastUpperTabbedPane
        ///
        eastUpperTabbedPane = new JTabbedPane();
        ///
        /// eastLowerTabbedPane
        ///
        eastLowerTabbedPane = new JTabbedPane();
        ///
        /// eastPanel
        ///
        eastPanel = new JPanel(new GridLayout(2, 1));
        eastPanel.setPreferredSize(new Dimension(350, 0));
        eastPanel.add(eastUpperTabbedPane);
        eastPanel.add(eastLowerTabbedPane);
        eastPanel.setVisible(false);
        ///
        /// logPanel
        ///
        logPanel = new LogPanel(200);
        ///
        /// issuesPanel
        ///
        issuesPanel = new IssuesTablePanel(200);
        ///
        ///
        ///
        southTabbedPane = new JTabbedPane();
        southTabbedPane.addTab("Log", logPanel);
        southTabbedPane.addTab("Issues", issuesPanel);
        ///
        /// southPanel
        ///
        southPanel = new JPanel(new BorderLayout());
        southPanel.add(southTabbedPane);
        ///
        ///
        /// Misc
        ///
        setIconImage(Icons.getLargeIcon("editor").getImage());

        mainMenuBar = new MainMenuBar(this);
        mainToolBar = new MainToolBar();

        currentBrush = new ShapeBrush();
        ((ShapeBrush) currentBrush).makeRectangleBrush(new Rectangle(0, 0, 1, 1));

        lastSelectedTile = new Tile();

        tileSetSelectionListener = new TileSetSelectionListener();
        ///
        /// findDialog
        ///
        findDialog = new FindDialog(this, this);
        ///
        /// replaceDialog
        ///
        replaceDialog = new ReplaceDialog(this, this);
        SearchContext context = findDialog.getSearchContext();
        replaceDialog.setSearchContext(context);
        ///
        /// goToFileDialog
        goToFileDialog = new GoToFileDialog(this);
        ///
        ///
        /// statusBar
        ///
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY), new EmptyBorder(4, 4, 4, 4)));
        ///
        /// this
        ///
        JPanel parent = new JPanel(new BorderLayout());
        parent.add(desktopPane, BorderLayout.CENTER);
        parent.add(southPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(mainToolBar, BorderLayout.NORTH);
        add(parent, BorderLayout.CENTER);
        add(westPanel, BorderLayout.WEST);
        add(eastPanel, BorderLayout.EAST);
        add(statusBar, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(new Dimension(1024, 768));
        setLocationByPlatform(true);
        setJMenuBar(mainMenuBar);
    }

    public static MainWindow getInstance() {
        return INSTANCE;
    }

    public JInternalFrame getCurrentFrame() {
        return desktopPane.getSelectedFrame();
    }

    public MapEditor getCurrentMapEditor() {
        if (this.desktopPane.getSelectedFrame() instanceof MapEditor) {
            return (MapEditor) this.desktopPane.getSelectedFrame();
        }
        return null;
    }

    public Collection<AbstractAssetEditorWindow> getOpenEditors() {
        return editorMap.values();
    }

    public void updateEditorMap(File previous, File current, AbstractAssetEditorWindow editor) {
        if (current == null) {
            return;
        }

        if (previous != null) {
            editorMap.remove(previous);
        }

        editorMap.put(current, editor);
    }

    public boolean tearDown() {
        if (!closeAllFrames()) {
            return false;
        }
        projectPanel.tearDown();
        return true;
    }

    public boolean closeAllFrames() {
        cancelClose = false;
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            frame.doDefaultCloseAction();
            if (cancelClose) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void internalFrameOpened(InternalFrameEvent e) {
        LOGGER.debug("Opened internal frame e=[{}].", e.getInternalFrame().getClass());

        AbstractAssetEditorWindow window = (AbstractAssetEditorWindow) e.getInternalFrame();
        if (window instanceof AnimationEditor) {
            AnimationEditor editor = (AnimationEditor) window;
            propertiesPanel.setModel(editor.getAnimation());
        } else if (window instanceof MapEditor) {
            MapEditor editor = (MapEditor) window;
            if (westUpperTabbedPane.indexOfComponent(tileSetPanel) > -1) {
                westUpperTabbedPane.setSelectedComponent(tileSetPanel);
            }
            westLowerTabbedPane.setSelectedComponent(layerPanel);
            propertiesPanel.setModel(editor.getMap());
        } else if (window instanceof SpriteEditor) {
            SpriteEditor editor = (SpriteEditor) window;
            propertiesPanel.setModel(editor.getSprite());
        }
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
        LOGGER.debug("Closing internal frame e=[{}].", e.getInternalFrame().getClass());

        AbstractAssetEditorWindow window = (AbstractAssetEditorWindow) e.getInternalFrame();
        if (window.needsSave()) {
            String title = getTitle();
            String message = "Do you want to save changes to " + window.getTitle() + "?";
            int result = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_CANCEL_OPTION);

            try {
                switch (result) {
                case 0:
                    window.save();
                case 1:
                    window.dispose();
                    cancelClose = false;
                    break;
                case 2:
                default:
                    // User cancelled close.
                    cancelClose = true;
                    return;
                }
            } catch (Exception ex) {
                LOGGER.error("Failed to close window=[{}].", window, ex);
            }
        } else if (window instanceof ScriptEditor) {
            ((ScriptEditor) window).cleanUp();
            window.dispose();
        } else {
            window.dispose();
        }

        AssetManager.getInstance().removeAsset(window.getAsset());
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
        LOGGER.debug("Closed internal frame e=[{}].", e.getInternalFrame().getClass());

        AbstractAssetEditorWindow window = (AbstractAssetEditorWindow) e.getInternalFrame();
        if (window.getAsset() != null && window.getAsset().getFile() != null) {
            editorMap.remove(window.getAsset().getFile());
        }

        desktopPane.remove(window);
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e) {
        LOGGER.debug("Activated internal frame e=[{}].", e.getInternalFrame().getClass());

        AbstractAssetEditorWindow window = (AbstractAssetEditorWindow) e.getInternalFrame();
        if (window.getAsset() == null) {
            return;
        }

        updateEditorMap(null, window.getAsset().getFile(), window);

        // Enable or Disable undo/redo buttons.
        boolean canUndo = false;
        boolean canRedo = false;
        if (window instanceof ActionHandler) {
            final ActionHandler handler = (ActionHandler) window;
            canUndo = handler.canUndo();
            canRedo = handler.canRedo();
        }
        mainMenuBar.getEditMenu().getUndoMenuItem().setEnabled(canUndo);
        mainToolBar.getUndoButton().setEnabled(canUndo);
        mainMenuBar.getEditMenu().getRedoMenuItem().setEnabled(canRedo);
        mainToolBar.getRedoButton().setEnabled(canRedo);

        if (window instanceof AnimationEditor) {
            AnimationEditor editor = (AnimationEditor) window;
            propertiesPanel.setModel(editor.getAnimation());
        } else if (window instanceof MapEditor) {
            MapEditor editor = (MapEditor) window;
            this.layerPanel.setMapView(editor.getMapView());

            if (editor.getSelectedObject() != null) {
                this.propertiesPanel.setModel(editor.getSelectedObject());
            } else {
                this.propertiesPanel.setModel(editor.getMap());
            }
        } else if (window instanceof SpriteEditor) {
            SpriteEditor editor = (SpriteEditor) window;
            propertiesPanel.setModel(editor.getSprite());
        } else if (window instanceof ScriptEditor) {
            ScriptEditor editor = (ScriptEditor) window;
            editor.forceReparsing();
            if (!mainToolBar.getStopButton().isEnabled()) {
                // Engine isn't running at the moment
                mainToolBar.getDebugButton().setEnabled(true);
            }
        } else if (window instanceof ImageEditor) {
            ImageEditor editor = (ImageEditor) window;
            propertiesPanel.setModel(editor.getImage());
        }
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent e) {
        LOGGER.debug("Deactivated internal frame e=[{}].", e.getInternalFrame().getClass());

        JInternalFrame frame = e.getInternalFrame();
        if (frame instanceof AnimationEditor) {
            AnimationEditor editor = (AnimationEditor) frame;

            if (propertiesPanel.getModel() == editor.getAnimation()) {
                propertiesPanel.setModel(null);
            }
        } else if (frame instanceof MapEditor) {
            MapEditor editor = (MapEditor) frame;

            if (layerPanel.getMapView().equals(editor.getMapView())) {
                layerPanel.clearTable();
            }

            if (propertiesPanel.getModel() == editor.getSelectedObject()
                    || propertiesPanel.getModel() == editor.getMap()) {
                propertiesPanel.setModel(null);
            }

            // So we do not end up drawing the vector on the other
            // map after it has been deactivated.
            if (currentBrush instanceof AbstractPolygonBrush) {
                AbstractPolygonBrush brush = (AbstractPolygonBrush) currentBrush;
                if (brush.isDrawing() && brush.getPolygon() != null) {
                    brush.reset();
                }
            }
        } else if (frame instanceof SpriteEditor) {
            SpriteEditor editor = (SpriteEditor) frame;

            if (propertiesPanel.getModel() == editor.getSprite()) {
                propertiesPanel.setModel(null);
            }
        } else if (frame instanceof ScriptEditor) {
            issuesPanel.clearNotices();
            mainToolBar.getDebugButton().setEnabled(false);
        } else if (frame instanceof ImageEditor) {
            ImageEditor editor = (ImageEditor) frame;
            if (propertiesPanel.getModel() == editor.getImage()) {
                propertiesPanel.setModel(null);
            }
        }
    }

    @Override
    public void searchEvent(SearchEvent se) {
        if (getCurrentFrame() instanceof ScriptEditor) {
            ScriptEditor editor = (ScriptEditor) getCurrentFrame();
            editor.searchEvent(se);
        }
    }

    @Override
    public String getSelectedText() {
        if (getCurrentFrame() instanceof ScriptEditor) {
            ScriptEditor editor = (ScriptEditor) getCurrentFrame();
            return editor.getSelectedText();
        }
        return "";
    }

    /**
     * Adds a new file format editor to our desktop pane.
     *
     * @param editor
     */
    public void addToolkitEditorWindow(AbstractAssetEditorWindow editor) {
        // Inset 10 pixels (x, y)
        editor.setLocation(10, 10);
        editor.addInternalFrameListener(this);
        editor.setVisible(true);
        editor.toFront();
        desktopPane.add(editor);
        selectToolkitWindow(editor);
    }

    /**
     * If the current window is an AbstractAssetEditorWindow it will be marked as needing saving.
     */
    public void markWindowForSaving() {
        if (this.desktopPane.getSelectedFrame() instanceof AbstractAssetEditorWindow) {
            ((AbstractAssetEditorWindow) this.desktopPane.getSelectedFrame()).setNeedSave(true);
        }
    }

    public void openAssetEditor(File file) {
        if (editorMap.containsKey(file)) {
            try {
                editorMap.get(file).setSelected(true);
            } catch (PropertyVetoException ex) {
                LOGGER.error("Could not bring to front assetEditor=[{}], ex=[{}]", file, ex);
            }
            return;
        }

        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(CoreProperties.getDefaultExtension(Animation.class))) {
            addToolkitEditorWindow(EditorFactory.getEditor(openAnimation(file)));
        } else if (fileName.endsWith(CoreProperties.getDefaultExtension(Map.class))) {
            addToolkitEditorWindow(EditorFactory.getEditor(openMap(file)));
        } else if (fileName.endsWith(CoreProperties.getDefaultExtension(Sprite.class))) {
            addToolkitEditorWindow(EditorFactory.getEditor(openSprite(file)));
        } else if (fileName.endsWith(CoreProperties.getDefaultExtension(Tileset.class))) {
            openTileset(file);
        } else if (fileName.endsWith(CoreProperties.getDefaultExtension(Game.class))) {
            addToolkitEditorWindow(EditorFactory.getEditor(openProject(file)));
        } else if (fileName.endsWith(CoreProperties.getDefaultExtension(Script.class))) {
            addToolkitEditorWindow(EditorFactory.getEditor(openScript(file)));
        } else if (Arrays.asList(EditorFileManager.getImageExtensions())
                .contains(FilenameUtils.getExtension(fileName))) {
            addToolkitEditorWindow(EditorFactory.getEditor(openImage(file)));
        }
    }

    public Game openProject(File file) {
        LOGGER.info("Opening {} file=[{}].", Game.class.getSimpleName(), file);

        try {
            AssetHandle handle = AssetManager.getInstance().deserialize(new AssetDescriptor(file.toURI()));
            return (Game) handle.getAsset();
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to open {} file=[{}].", Game.class.getSimpleName(), file, ex);
        }

        return null;
    }

    public void createNewProject(String projectName) {
        LOGGER.info("Creating new {}.", Game.class.getSimpleName());

        // Remove any extensions the user may have tried to add.
        projectName = FilenameUtils.removeExtension(projectName);
        boolean result = FileTools.createBlankProject(FileTools.getProjectsDirectory(), projectName);
        if (result) {
            String fileName = FileTools.getProjectsDirectory() + File.separator + projectName + File.separator
                    + projectName + CoreProperties.getDefaultExtension(Game.class);
            File file = new File(fileName);

            Game project = new Game(new AssetDescriptor(file.toURI()), projectName);
            try {
                // Write out new project file.
                AssetManager.getInstance().serialize(AssetManager.getInstance().getHandle(project));
                setProjectPath(file.getParent());
                setupProject(project, true);
            } catch (IOException | AssetException ex) {
                LOGGER.error("Failed to create new {} projectName=[{}].", Game.class, projectName, ex);
            }
        } else {
            // TODO: clean up directory structure?
        }
    }

    public void createNewProject(String projectName, String template) {
        LOGGER.info("Creating new {}.", Game.class.getSimpleName());
        try {
            // Remove any extensions the user may have tried to add.
            projectName = FilenameUtils.removeExtension(projectName);
            Game project = FileTools.createProjectFromTemplate(FileTools.getProjectsDirectory(), projectName, template);
            setProjectPath(project.getFile().getParent());
            setupProject(project, true);
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to create new {} projectName=[{}].", Game.class, projectName, ex);
        }
    }

    public void createNewScript() {
        LOGGER.info("Creating new {}.", Script.class.getSimpleName());
        addToolkitEditorWindow(EditorFactory.getEditor(new Script(null)));
    }

    public Script openScript(File file) {
        LOGGER.info("Opening {} file=[{}].", Script.class.getSimpleName(), file);

        try {
            if (file.canRead()) {
                Script program;

                AssetHandle handle = AssetManager.getInstance().deserialize(new AssetDescriptor(file.toURI()));
                program = (Script) handle.getAsset();

                return program;
            }
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to open {} file=[{}].", Script.class.getSimpleName(), file, ex);
        }

        return null;
    }

    public void createNewAnimation() {
        LOGGER.info("Creating new {}.", Animation.class.getSimpleName());

        addToolkitEditorWindow(EditorFactory.getEditor(new Animation(null)));
    }

    /**
     * Creates an animation editor window for modifying the specified animation file.
     *
     * @param file
     * @return
     */
    public Animation openAnimation(File file) {
        LOGGER.info("Opening {} file=[{}].", Animation.class.getSimpleName(), file);

        try {
            if (file.canRead()) {
                Animation animation;

                AssetHandle handle = AssetManager.getInstance().deserialize(new AssetDescriptor(file.toURI()));
                animation = (Animation) handle.getAsset();

                return animation;
            }
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to open {} file=[{}].", Animation.class.getSimpleName(), file, ex);
        }

        return null;
    }

    public void createNewMap() {
        LOGGER.info("Creating new {}.", Map.class.getSimpleName());

        NewMapAction action = new NewMapAction();
        action.actionPerformed(null);
        Map map = (Map) action.getValue("map");

        if (map != null) {
            addToolkitEditorWindow(EditorFactory.getEditor(map));
        }
    }

    public Map openMap(File file) {
        LOGGER.info("Opening {} file=[{}].", Map.class.getSimpleName(), file);

        try {
            if (file.canRead()) {
                AssetHandle handle = AssetManager.getInstance().deserialize(new AssetDescriptor(file.toURI()));
                Map map = (Map) handle.getAsset();

                // Setup the TileSets used on this Map.
                for (String tileset : map.getTilesets()) {
                    String path = System.getProperty("project.path") + File.separator
                            + EditorFileManager.getTypeSubdirectory(Tileset.class) + File.separator + tileset;
                    openTileset(new File(path));
                }

                map.init();

                return map;
            }
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to open {} file=[{}].", Map.class.getSimpleName(), file, ex);
        }

        return null;
    }

    public void createNewSprite() {
        LOGGER.info("Creating new {}.", Sprite.class.getSimpleName());
        NewSpriteAction action = new NewSpriteAction();
        action.actionPerformed(null);
        Sprite sprite = (Sprite) action.getValue("sprite");
        addToolkitEditorWindow(EditorFactory.getEditor(sprite));
    }

    /**
     * Creates a character editor window for modifying the specified character file.
     *
     * @param file
     * @return
     */
    public Sprite openSprite(File file) {
        LOGGER.info("Opening {} file=[{}].", Sprite.class.getSimpleName(), file);

        try {
            if (file.canRead()) {
                AssetHandle handle = AssetManager.getInstance().deserialize(new AssetDescriptor(file.toURI()));
                Sprite sprite = (Sprite) handle.getAsset();

                return sprite;
            }
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to open {} file=[{}].", Character.class.getSimpleName(), file, ex);
        }

        return null;
    }

    public void createNewTileset() {
        LOGGER.info("Creating new {}.", Tileset.class.getSimpleName());
        NewTilesetAction action = new NewTilesetAction();
        action.actionPerformed(null);
        if (action.getValue("file") != null) {
            openTileset((File) action.getValue("file"));
        }
    }

    public void openTileset(File file) {
        LOGGER.info("Opening {} file=[{}].", Tileset.class.getSimpleName(), file);

        try {
            Tileset tileSet;
            String key = file.getName();
            if (!TileSetCache.contains(key)) {
                tileSet = TileSetCache.addTileSet(key);
                tileSet = TileSetUtil.load(tileSet);
            } else {
                tileSet = TileSetCache.getTileSet(key);
            }

            tileSetPanel.addTileSet(tileSet);
            if (westUpperTabbedPane.indexOfComponent(tileSetPanel) > -1) {
                westUpperTabbedPane.setSelectedComponent(tileSetPanel);
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to open {} file=[{}].", Tileset.class.getSimpleName(), file, ex);
        }
    }

    public Image openImage(File file) {
        LOGGER.info("Opening {} file=[{}].", Image.class.getSimpleName(), file);

        try {
            if (file.canRead()) {
                AssetHandle handle = AssetManager.getInstance().deserialize(new AssetDescriptor(file.toURI()));
                Image image = (Image) handle.getAsset();

                return image;
            }
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to open {} file=[{}].", Image.class.getSimpleName(), file, ex);
        }

        return null;
    }

    public void setProjectPath(String path) {
        LOGGER.info("Setting project path=[{}].", path);
        System.setProperty("project.path", path);
        LOGGER.info("Project path set to project.path=[{}].", System.getProperty("project.path"));
    }

    public void setupProject(Game project, boolean showProjectEditor) {
        // Clean up previous project.
        closeAllFrames();
        tileSetPanel.removeTileSets();
        TileSetCache.clear();

        activeProject = project;

        // Will create any missing directories.
        FileTools.createAssetDirectories(System.getProperty("project.path"));

        projectPanel.setup(EditorFileManager.getProjectPath());

        if (showProjectEditor) {
            GameEditor projectEditor = new GameEditor(activeProject);
            addToolkitEditorWindow(projectEditor);
        }

        setTitle(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TITLE) + " - " + activeProject.getName());

        mainMenuBar.enableMenus(true);
        mainToolBar.toggleButtonStates(true);
    }

    public void enableUndo(boolean enable) {
        mainMenuBar.getEditMenu().getUndoMenuItem().setEnabled(enable);
        mainToolBar.getUndoButton().setEnabled(enable);
    }

    public void enableRedo(boolean enable) {
        mainMenuBar.getEditMenu().getRedoMenuItem().setEnabled(enable);
        mainToolBar.getRedoButton().setEnabled(enable);
    }

    private void selectToolkitWindow(AbstractAssetEditorWindow window) {
        try {
            window.setSelected(true);
        } catch (PropertyVetoException ex) {
            LOGGER.error("Failed to select {} window=[{}].", AbstractAssetEditorWindow.class.getSimpleName(), window,
                    ex);
        }
    }

}
