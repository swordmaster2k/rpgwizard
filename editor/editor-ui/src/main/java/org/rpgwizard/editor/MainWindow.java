/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import org.rpgwizard.common.assets.Animation;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.assets.Enemy;
import org.rpgwizard.common.assets.NPC;
import org.rpgwizard.common.assets.Character;
import org.rpgwizard.common.assets.Program;
import org.rpgwizard.common.assets.Project;
import org.rpgwizard.common.assets.SpecialMove;
import org.rpgwizard.common.assets.Tile;
import org.rpgwizard.common.assets.TileSet;
import org.rpgwizard.editor.editors.AnimationEditor;
import org.rpgwizard.editor.editors.BoardEditor;
import org.rpgwizard.editor.editors.board.AbstractBrush;
import org.rpgwizard.editor.editors.board.ShapeBrush;
import org.rpgwizard.editor.editors.board.BoardVectorBrush;
import org.rpgwizard.editor.editors.ProjectEditor;
import org.rpgwizard.editor.ui.listeners.TileSelectionListener;
import org.rpgwizard.editor.editors.board.NewBoardDialog;
import org.rpgwizard.editor.ui.resources.Icons;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.common.utilities.TileSetCache;
import org.rpgwizard.editor.editors.CharacterEditor;
import org.rpgwizard.editor.editors.EnemyEditor;
import org.rpgwizard.editor.editors.NPCEditor;
import org.rpgwizard.editor.editors.tileset.NewTilesetDialog;
import org.rpgwizard.editor.properties.EditorProperties;
import org.rpgwizard.editor.properties.EditorProperty;
import org.rpgwizard.editor.ui.EditorFactory;
import org.rpgwizard.editor.ui.LayerPanel;
import org.rpgwizard.editor.ui.menu.MainMenuBar;
import org.rpgwizard.editor.ui.toolbar.MainToolBar;
import org.rpgwizard.editor.ui.PropertiesPanel;
import org.rpgwizard.editor.ui.TileSetTabbedPane;
import org.rpgwizard.editor.ui.ToolkitDesktopManager;
import org.rpgwizard.editor.ui.AssetEditorWindow;
import org.rpgwizard.editor.ui.listeners.TileSetSelectionListener;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.FileTools;
import org.rpgwizard.editor.editors.tileset.TileSetUtil;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.fortsoft.pf4j.PluginManager;

/**
 * Main UI, holds all Asset editors as InternalFrames. This class deals with
 * opening existing assets and creating new ones.
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public class MainWindow extends JFrame implements InternalFrameListener {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MainWindow.class);

	// Singleton.
	private static final MainWindow INSTANCE = new MainWindow();

	private final JDesktopPane desktopPane;
	private final Map<File, AssetEditorWindow> editorMap;

	private final MainMenuBar menuBar;
	private final MainToolBar toolBar;

	private final JPanel toolboxPanel;
	private final JTabbedPane upperTabbedPane;
	private final JTabbedPane lowerTabbedPane;
	// private final ProjectPanel projectPanel;
	private final TileSetTabbedPane tileSetPanel;
	private final PropertiesPanel propertiesPanel;
	private final LayerPanel layerPanel;

	// Project Related.
	private Project activeProject;

	// Board Related.
	private boolean showGrid;
	private boolean showVectors;
	private boolean showCoordinates;
	private boolean snapToGrid;
	private AbstractBrush currentBrush;
	private Tile lastSelectedTile;

	// Listeners.
	private final TileSetSelectionListener tileSetSelectionListener;

	private PluginManager pluginManager;

	private MainWindow() {
		super(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TITLE));

		desktopPane = new JDesktopPane();
		desktopPane.setBackground(Color.LIGHT_GRAY);
		desktopPane.setDesktopManager(new ToolkitDesktopManager());

		editorMap = new HashMap();

		// this.projectPanel = new ProjectPanel();
		tileSetPanel = new TileSetTabbedPane();
		upperTabbedPane = new JTabbedPane();
		// this.upperTabbedPane.addTab("Project", this.projectPanel); // TODO:
		// TK 4.1.0
		upperTabbedPane.addTab("Tileset", tileSetPanel);

		propertiesPanel = new PropertiesPanel();
		layerPanel = new LayerPanel();
		lowerTabbedPane = new JTabbedPane();
		lowerTabbedPane.addTab("Properties", propertiesPanel);
		lowerTabbedPane.addTab("Layers", layerPanel);

		toolboxPanel = new JPanel(new GridLayout(2, 1));
		toolboxPanel.setPreferredSize(new Dimension(384, 0));
		toolboxPanel.add(upperTabbedPane);
		toolboxPanel.add(lowerTabbedPane);

		// Application icon.
		setIconImage(Icons.getLargeIcon("editor").getImage());

		menuBar = new MainMenuBar(this);
		toolBar = new MainToolBar();

		currentBrush = new ShapeBrush();
		((ShapeBrush) currentBrush)
				.makeRectangleBrush(new Rectangle(0, 0, 1, 1));

		lastSelectedTile = new Tile();

		tileSetSelectionListener = new TileSetSelectionListener();

		JPanel parent = new JPanel(new BorderLayout());
		parent.add(desktopPane, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(toolBar, BorderLayout.NORTH);
		add(parent, BorderLayout.CENTER);
		add(toolboxPanel, BorderLayout.WEST);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setSize(new Dimension(1024, 768));
		setLocationByPlatform(true);
		setJMenuBar(menuBar);
	}

	public static MainWindow getInstance() {
		return INSTANCE;
	}

	public JDesktopPane getDesktopPane() {
		return this.desktopPane;
	}

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean isShowGrid) {
		this.showGrid = isShowGrid;
	}

	public boolean isShowVectors() {
		return showVectors;
	}

	public void setShowVectors(boolean showVectors) {
		this.showVectors = showVectors;
	}

	public boolean isShowCoordinates() {
		return showCoordinates;
	}

	public void setShowCoordinates(boolean isShowCoordinates) {
		this.showCoordinates = isShowCoordinates;
	}

	public boolean isSnapToGrid() {
		return snapToGrid;
	}

	public void setSnapToGrid(boolean snapToGrid) {
		this.snapToGrid = snapToGrid;
	}

	public AbstractBrush getCurrentBrush() {
		return this.currentBrush;
	}

	public void setCurrentBrush(AbstractBrush brush) {
		this.currentBrush = brush;
	}

	public Tile getLastSelectedTile() {
		return this.lastSelectedTile;
	}

	public void setLastSelectedTile(Tile tile) {
		lastSelectedTile = tile;
	}

	public MainMenuBar getMainMenuBar() {
		return this.menuBar;
	}

	public MainToolBar getMainToolBar() {
		return this.toolBar;
	}

	public PropertiesPanel getPropertiesPanel() {
		return this.propertiesPanel;
	}

	public BoardEditor getCurrentBoardEditor() {
		if (this.desktopPane.getSelectedFrame() instanceof BoardEditor) {
			return (BoardEditor) this.desktopPane.getSelectedFrame();
		}

		return null;
	}

	public Project getActiveProject() {
		return activeProject;
	}

	public TileSelectionListener getTileSetSelectionListener() {
		return tileSetSelectionListener;
	}

	public void setLowerTabbedPane(Component component) {
		if (lowerTabbedPane.getSelectedComponent() != component) {
			lowerTabbedPane.setSelectedComponent(component);
		}
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}
        
        public Collection<AssetEditorWindow> getOpenEditors() {
            return editorMap.values();
        }

	public void updateEditorMap(File previous, File current,
			AssetEditorWindow editor) {
		if (current == null) {
			return;
		}

		if (previous != null) {
			editorMap.remove(previous);
		}

		editorMap.put(current, editor);
	}

	public void closeEditors() {
		if (editorMap.isEmpty()) {
			return;
		}

		// Because the collection is invalidated on frame close.
		int size = editorMap.size();
		AssetEditorWindow[] windows;
		windows = (AssetEditorWindow[]) editorMap.values().toArray(
				new AssetEditorWindow[size]);
		for (int i = 0; i < size; i++) {
			try {
				windows[i].setClosed(true);
			} catch (PropertyVetoException ex) {
				LOGGER.error("Failed to close internal frame.", ex);
			}
		}
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
		LOGGER.debug("Opened internal frame e=[{}].", e.getInternalFrame()
				.getClass());

		AssetEditorWindow window = (AssetEditorWindow) e.getInternalFrame();
		if (window instanceof AnimationEditor) {
			AnimationEditor editor = (AnimationEditor) window;
			propertiesPanel.setModel(editor.getAnimation());
		} else if (window instanceof BoardEditor) {
			BoardEditor editor = (BoardEditor) window;
			upperTabbedPane.setSelectedComponent(tileSetPanel);
			lowerTabbedPane.setSelectedComponent(layerPanel);
			propertiesPanel.setModel(editor.getBoard());
		} else if (window instanceof CharacterEditor) {
			CharacterEditor editor = (CharacterEditor) window;
			propertiesPanel.setModel(editor.getPlayer());
		}
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		LOGGER.debug("Closing internal frame e=[{}].", e.getInternalFrame()
				.getClass());

		AssetEditorWindow window = (AssetEditorWindow) e.getInternalFrame();
		if (window.doesNeedSave()) {
			String title = getTitle();
			String message = "Do you want to save changes to "
					+ window.getTitle() + "?";
			int result = JOptionPane.showConfirmDialog(this, message, title,
					JOptionPane.YES_NO_CANCEL_OPTION);

			try {
				switch (result) {
					case 0 :
						window.save();
					case 1 :
						window.dispose();
						break;
					case 2 :
				}
			} catch (Exception ex) {
				LOGGER.error("Failed to close window=[{}].", window, ex);
			}
		} else {
			window.dispose();
		}

		AssetManager.getInstance().removeAsset(window.getAsset());
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		LOGGER.debug("Closed internal frame e=[{}].", e.getInternalFrame()
				.getClass());

		AssetEditorWindow window = (AssetEditorWindow) e.getInternalFrame();
		if (window.getAsset().getFile() != null) {
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
		LOGGER.debug("Activated internal frame e=[{}].", e.getInternalFrame()
				.getClass());

		AssetEditorWindow window = (AssetEditorWindow) e.getInternalFrame();
		updateEditorMap(null, window.getAsset().getFile(), window);
		if (window instanceof AnimationEditor) {
			AnimationEditor editor = (AnimationEditor) window;
			propertiesPanel.setModel(editor.getAnimation());
		} else if (window instanceof BoardEditor) {
			BoardEditor editor = (BoardEditor) window;
			this.layerPanel.setBoardView(editor.getBoardView());

			if (editor.getSelectedObject() != null) {
				this.propertiesPanel.setModel(editor.getSelectedObject());
			} else {
				this.propertiesPanel.setModel(editor.getBoard());
			}
		} else if (window instanceof CharacterEditor) {
			CharacterEditor editor = (CharacterEditor) window;
			propertiesPanel.setModel(editor.getPlayer());
		} else if (window instanceof NPCEditor) {
			NPCEditor editor = (NPCEditor) window;
			propertiesPanel.setModel(editor.getNPC());
		}
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
		LOGGER.debug("Deactivated internal frame e=[{}].", e.getInternalFrame()
				.getClass());

		JInternalFrame frame = e.getInternalFrame();
		if (frame instanceof AnimationEditor) {
			AnimationEditor editor = (AnimationEditor) frame;

			if (propertiesPanel.getModel() == editor.getAnimation()) {
				propertiesPanel.setModel(null);
			}
		} else if (frame instanceof BoardEditor) {
			BoardEditor editor = (BoardEditor) frame;

			if (layerPanel.getBoardView().equals(editor.getBoardView())) {
				layerPanel.clearTable();
			}

			if (propertiesPanel.getModel() == editor.getSelectedObject()
					|| propertiesPanel.getModel() == editor.getBoard()) {
				propertiesPanel.setModel(null);
			}

			// So we do not end up drawing the vector on the other
			// board after it has been deactivated.
			if (currentBrush instanceof BoardVectorBrush) {
				BoardVectorBrush brush = (BoardVectorBrush) currentBrush;

				if (brush.isDrawing() && brush.getBoardVector() != null) {
					brush.finish();
				}
			}
		} else if (frame instanceof CharacterEditor) {
			CharacterEditor editor = (CharacterEditor) frame;

			if (propertiesPanel.getModel() == editor.getPlayer()) {
				propertiesPanel.setModel(null);
			}
		} else if (frame instanceof NPCEditor) {
			NPCEditor editor = (NPCEditor) frame;

			if (propertiesPanel.getModel() == editor.getNPC()) {
				propertiesPanel.setModel(null);
			}
		}
	}

	/**
	 * Adds a new file format editor to our desktop pane.
	 *
	 * @param editor
	 */
	public void addToolkitEditorWindow(AssetEditorWindow editor) {
		editor.addInternalFrameListener(this);
		editor.setVisible(true);
		editor.toFront();
		desktopPane.add(editor);
		selectToolkitWindow(editor);
	}

	public void openAssetEditor(File file) {
		if (editorMap.containsKey(file)) {
			return;
		}

		String fileName = file.getName().toLowerCase();
		if (fileName.endsWith(CoreProperties
				.getDefaultExtension(Animation.class))) {
			addToolkitEditorWindow(EditorFactory.getEditor(openAnimation(file)));
		} else if (fileName.endsWith(CoreProperties
				.getDefaultExtension(Board.class))) {
			addToolkitEditorWindow(EditorFactory.getEditor(openBoard(file)));
		} else if (fileName.endsWith(CoreProperties
				.getDefaultExtension(Enemy.class))) {
			addToolkitEditorWindow(EditorFactory.getEditor(openEnemy(file)));
		} else if (fileName.endsWith(CoreProperties
				.getDefaultExtension(NPC.class))) {
			addToolkitEditorWindow(EditorFactory.getEditor(openNPC(file)));
		} else if (fileName.endsWith(CoreProperties
				.getDefaultExtension(Character.class))) {
			addToolkitEditorWindow(EditorFactory.getEditor(openCharacter(file)));
		} else if (fileName.endsWith(CoreProperties
				.getDefaultExtension(TileSet.class))) {
			openTileset(file);
		} else if (fileName.endsWith(CoreProperties
				.getDefaultExtension(SpecialMove.class))) {
			addToolkitEditorWindow(EditorFactory
					.getEditor(openSpecialMove(file)));
		} else if (fileName.endsWith(CoreProperties
				.getDefaultExtension(Project.class))) {
			addToolkitEditorWindow(EditorFactory.getEditor(openProject(file)));
		} else if (fileName.endsWith(CoreProperties
				.getDefaultExtension(Program.class))) {
			addToolkitEditorWindow(EditorFactory.getEditor(openProgram(file)));
		}
	}

	public Project openProject(File file) {
        LOGGER.info("Opening {} file=[{}].", Project.class.getSimpleName(), file);

        try {
            AssetHandle handle = AssetManager.getInstance().deserialize(
                    new AssetDescriptor(file.toURI()));
            return (Project) handle.getAsset();
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to open {} file=[{}].", Project.class.getSimpleName(), file, ex);
        }

        return null;
    }
	public void createNewProject() {
        LOGGER.info("Creating new {}.", Project.class.getSimpleName());

        String projectName = JOptionPane.showInputDialog(this,
                "Project Name:",
                "Create Project",
                JOptionPane.QUESTION_MESSAGE);

        if (projectName != null) {
            // Remove any . extensions the user may have tried to add.
            projectName = FilenameUtils.removeExtension(projectName);

            boolean result = FileTools.createDirectoryStructure(
                    FileTools.getProjectsDirectory(), projectName);

            if (result) {
                String fileName = FileTools.getProjectsDirectory()
                        + File.separator
                        + projectName
                        + File.separator
                        + projectName
                        + CoreProperties.getDefaultExtension(Project.class);
                File file = new File(fileName);

                Project project = new Project(new AssetDescriptor(file.toURI()), projectName);
                try {
                    // Write out new project file.
                    AssetManager.getInstance().serialize(AssetManager.getInstance().getHandle(project));
                    setProjectPath(file.getParent());
                    setupProject(project);
                } catch (IOException | AssetException ex) {
                    LOGGER.error("Failed to create new {} projectName=[{}].", Project.class, projectName, ex);
                }
            } else {
                // TODO: clean up directory structure?
            }
        }
    }
	public void createNewProgram() {
		LOGGER.info("Creating new {}.", Program.class.getSimpleName());

		addToolkitEditorWindow(EditorFactory.getEditor(new Program(null)));
	}

	public Program openProgram(File file) {
        LOGGER.info("Opening {} file=[{}].", Program.class.getSimpleName(), file);

        try {
            if (file.canRead()) {
                Program program;

                AssetHandle handle = AssetManager.getInstance().deserialize(
                        new AssetDescriptor(file.toURI()));
                program = (Program) handle.getAsset();

                return program;
            }
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to open {} file=[{}].", Program.class.getSimpleName(), file, ex);
        }

        return null;
    }
	public void createNewAnimation() {
		LOGGER.info("Creating new {}.", Animation.class.getSimpleName());

		addToolkitEditorWindow(EditorFactory.getEditor(new Animation(null)));
	}

	/**
	 * Creates an animation editor window for modifying the specified animation
	 * file.
	 *
	 * @param file
	 * @return
	 */
	public Animation openAnimation(File file) {
        LOGGER.info("Opening {} file=[{}].", Animation.class.getSimpleName(), file);

        try {
            if (file.canRead()) {
                Animation animation;

                AssetHandle handle = AssetManager.getInstance().deserialize(
                        new AssetDescriptor(file.toURI()));
                animation = (Animation) handle.getAsset();

                return animation;
            }
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to open {} file=[{}].", Animation.class.getSimpleName(), file, ex);
        }

        return null;
    }
	public void createNewBoard() {
		LOGGER.info("Creating new {}.", Board.class.getSimpleName());

		NewBoardDialog dialog = new NewBoardDialog();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);

		if (dialog.getValue() != null) {
			Board board = new Board(null, dialog.getValue()[0],
					dialog.getValue()[1], dialog.getValue()[2],
					dialog.getValue()[3]);
			BoardEditor boardEditor = new BoardEditor(board);
			boardEditor.addInternalFrameListener(this);
			boardEditor.setVisible(true);
			boardEditor.toFront();

			this.desktopPane.add(boardEditor);
			this.selectToolkitWindow(boardEditor);
		}
	}

	public Board openBoard(File file) {
        LOGGER.info("Opening {} file=[{}].", Board.class.getSimpleName(), file);

        try {
            if (file.canRead()) {
                AssetHandle handle = AssetManager.getInstance().deserialize(
                        new AssetDescriptor(file.toURI()));
                Board board = (Board) handle.getAsset();

                // Setup the TileSets used on this Board.
                for (TileSet tileSet : board.getTileSets().values()) {
                    String path = System.getProperty("project.path")
                            + File.separator
                            + EditorFileManager.getTypeSubdirectory(TileSet.class)
                            + File.separator
                            + tileSet.getName();
                    openTileset(new File(path));
                }

                board.loadTiles();

                return board;
            }
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to open {} file=[{}].", Board.class.getSimpleName(), file, ex);
        }

        return null;
    }
	public void createNewEnemy() {
		LOGGER.info("Creating new {}.", Enemy.class.getSimpleName());

		Enemy enemy = new Enemy(null);
		enemy.setName("Untitled");

		EnemyEditor enemyEditor = new EnemyEditor(enemy);
		enemyEditor.addInternalFrameListener(this);
		enemyEditor.setVisible(true);
		enemyEditor.toFront();

		this.desktopPane.add(enemyEditor);
		this.selectToolkitWindow(enemyEditor);
	}

	/**
	 * Creates an animation editor window for modifying the specified animation
	 * file.
	 *
	 * @param file
	 * @return
	 */
	public Enemy openEnemy(File file) {
        LOGGER.info("Opening {} file=[{}].", Enemy.class.getSimpleName(), file);

        try {
            if (file.canRead()) {
                AssetHandle handle = AssetManager.getInstance().deserialize(
                        new AssetDescriptor(file.toURI()));
                Enemy enemy = (Enemy) handle.getAsset();

                return enemy;
            }
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to open {} file=[{}].", Enemy.class.getSimpleName(), file, ex);
        }

        return null;
    }
	public void createNewNPC() {
		LOGGER.info("Creating new {}.", NPC.class.getSimpleName());

		NPC npc = new NPC(null);
		npc.setName("Untitled");

		NPCEditor npcEditor = new NPCEditor(npc);
		npcEditor.addInternalFrameListener(this);
		npcEditor.setVisible(true);
		npcEditor.toFront();

		desktopPane.add(npcEditor);
		selectToolkitWindow(npcEditor);
	}

	public NPC openNPC(File file) {
        LOGGER.info("Opening {} file=[{}].", NPC.class.getSimpleName(), file);

        try {
            if (file.canRead()) {
                AssetHandle handle = AssetManager.getInstance().deserialize(
                        new AssetDescriptor(file.toURI()));
                NPC npc = (NPC) handle.getAsset();

                return npc;
            }
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to open {} file=[{}].", NPC.class.getSimpleName(), file, ex);
        }

        return null;
    }
	public void createNewCharacter() {
		LOGGER.info("Creating new {}.", Character.class.getSimpleName());

		Character player = new Character(null);
		player.setName("Untitled");

		CharacterEditor characterEditor = new CharacterEditor(player);
		characterEditor.addInternalFrameListener(this);
		characterEditor.setVisible(true);
		characterEditor.toFront();

		this.desktopPane.add(characterEditor);
		this.selectToolkitWindow(characterEditor);
	}

	/**
	 * Creates a character editor window for modifying the specified character
	 * file.
	 *
	 * @param file
	 * @return
	 */
	public Character openCharacter(File file) {
        LOGGER.info("Opening {} file=[{}].", Character.class.getSimpleName(), file);

        try {
            if (file.canRead()) {
                AssetHandle handle = AssetManager.getInstance().deserialize(
                        new AssetDescriptor(file.toURI()));
                Character player = (Character) handle.getAsset();

                return player;
            }
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to open {} file=[{}].", Character.class.getSimpleName(), file, ex);
        }

        return null;
    }
	public void createNewTileset() {
        LOGGER.info("Creating new {}.", TileSet.class.getSimpleName());

        NewTilesetDialog dialog = new NewTilesetDialog();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.getValue() != null) {
            int tileWidth = dialog.getValue()[0];
            int tileHeight = dialog.getValue()[1];

            String path = CoreProperties.getProperty("toolkit.directory.graphics");
            String description = "Image Files";
            String[] extensions = EditorFileManager.getImageExtensions();
            EditorFileManager.setFileChooserSubdirAndFilters(path, description, extensions);

            if (EditorFileManager.getFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = EditorFileManager.getFileChooser().getSelectedFile();

                try {
                    File tileSetFile = EditorFileManager.saveByType(TileSet.class);
                    
                    if (tileSetFile == null) {
                        return; // Cancelled by the user.
                    }

                    TileSet tileSet = new TileSet(
                            new AssetDescriptor(tileSetFile.toURI()),
                            tileWidth,
                            tileHeight
                    );
                    tileSet.setDescriptor(new AssetDescriptor(tileSetFile.toURI()));
                    tileSet.setName(tileSetFile.getName());

                    String remove = EditorFileManager.getGraphicsPath();
                    String imagePath = file.getAbsolutePath().replace(remove, "");
                    tileSet.setImage(imagePath);

                    AssetManager.getInstance().serialize(
                            AssetManager.getInstance().getHandle(tileSet));

                    openTileset(tileSetFile);
                } catch (IOException | AssetException ex) {
                    LOGGER.error("Failed to create new {} file=[{}].", TileSet.class.getSimpleName(), file, ex);
                }
            }
        }
    }
	public void openTileset(File file) {
		LOGGER.info("Opening {} file=[{}].", TileSet.class.getSimpleName(),
				file);

		try {
			TileSet tileSet;
			String key = file.getName();
			if (!TileSetCache.contains(key)) {
				tileSet = TileSetCache.addTileSet(key);
				tileSet = TileSetUtil.load(tileSet);
			} else {
				tileSet = TileSetCache.getTileSet(key);
			}

			tileSetPanel.addTileSet(tileSet);
			upperTabbedPane.setSelectedComponent(tileSetPanel);
		} catch (IOException ex) {
			LOGGER.error("Failed to open {} file=[{}].",
					TileSet.class.getSimpleName(), file, ex);
		}
	}

	public SpecialMove openSpecialMove(File file) {
        LOGGER.info("Opening {} file=[{}].", SpecialMove.class.getSimpleName(), file);

        try {
            if (file.canRead()) {
                AssetHandle handle = AssetManager.getInstance().deserialize(
                        new AssetDescriptor(file.toURI()));
                SpecialMove move = (SpecialMove) handle.getAsset();

                return move;
            }
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to open {} file=[{}].", SpecialMove.class.getSimpleName(), file, ex);
        }

        return null;
    }
	public void setProjectPath(String path) {
		LOGGER.info("Setting project path=[{}].", path);
		System.setProperty("project.path", path);
		LOGGER.info("Project path set to project.path=[{}].",
				System.getProperty("project.path"));
	}

	public void setupProject(Project project) {
		closeEditors();
		activeProject = project;

		ProjectEditor projectEditor = new ProjectEditor(this.activeProject);
		this.desktopPane.add(projectEditor, BorderLayout.CENTER);

		projectEditor.addInternalFrameListener(this);
		projectEditor.toFront();

		selectToolkitWindow(projectEditor);
		setTitle(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TITLE)
				+ " - " + activeProject.getName());

		menuBar.enableMenus(true);
		toolBar.toggleButtonStates(true);
	}

	private void selectToolkitWindow(AssetEditorWindow window) {
		try {
			window.setSelected(true);
		} catch (PropertyVetoException ex) {
			LOGGER.error("Failed to select {} window=[{}].",
					AssetEditorWindow.class.getSimpleName(), window, ex);
		}
	}

}
