/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.toolbar;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.apache.commons.lang3.SystemUtils;
import org.rpgwizard.editor.properties.EditorProperties;
import org.rpgwizard.editor.properties.EditorProperty;
import org.rpgwizard.editor.ui.EditorButton;
import org.rpgwizard.editor.ui.actions.BucketAction;
import org.rpgwizard.editor.ui.actions.CompileAction;
import org.rpgwizard.editor.ui.actions.CopyAction;
import org.rpgwizard.editor.ui.actions.CutAction;
import org.rpgwizard.editor.ui.actions.EraserAction;
import org.rpgwizard.editor.ui.actions.HelpAction;
import org.rpgwizard.editor.ui.actions.LayerImageAction;
import org.rpgwizard.editor.ui.actions.LightAction;
import org.rpgwizard.editor.ui.actions.OpenFileAction;
import org.rpgwizard.editor.ui.actions.PasteAction;
import org.rpgwizard.editor.ui.actions.PencilAction;
import org.rpgwizard.editor.ui.actions.RedoAction;
import org.rpgwizard.editor.ui.actions.RunAction;
import org.rpgwizard.editor.ui.actions.SaveAction;
import org.rpgwizard.editor.ui.actions.SaveAllAction;
import org.rpgwizard.editor.ui.actions.SelectionAction;
import org.rpgwizard.editor.ui.actions.SpriteAction;
import org.rpgwizard.editor.ui.actions.StartPositionAction;
import org.rpgwizard.editor.ui.actions.StopAction;
import org.rpgwizard.editor.ui.actions.UndoAction;
import org.rpgwizard.editor.ui.actions.VectorAction;
import org.rpgwizard.editor.ui.actions.VectorAreaAction;
import org.rpgwizard.editor.ui.actions.ZoomInAction;
import org.rpgwizard.editor.ui.actions.ZoomOutAction;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 *
 * @author Joshua Michael Daly
 */
public final class MainToolBar extends JToolBar {

    private final JPopupMenu popupMenu;
    private final JMenuItem newAnimationMenu;
    private final JMenuItem newProjectMenu;

    private final EditorButton newButton;
    private final EditorButton openButton;
    private final EditorButton saveButton;
    private final EditorButton saveAllButton;

    private final EditorButton cutButton;
    private final EditorButton copyButton;
    private final EditorButton pasteButton;
    private final EditorButton deleteButton;

    private final EditorButton undoButton;
    private final EditorButton redoButton;

    private final ButtonGroup toolButtonGroup;
    private final JToggleButton pencilButton;
    private final JToggleButton selectionButton;
    private final JToggleButton bucketButton;
    private final JToggleButton eraserButton;

    private final JToggleButton vectorAreaButton;
    private final JToggleButton vectorButton;
    private final JToggleButton spriteButton;
    private final JToggleButton imageButton;
    private final JToggleButton lightButton;
    private final JToggleButton startPositionButton;

    private final EditorButton zoomInButton;
    private final EditorButton zoomOutButton;

    private final EditorButton runButton;
    private final EditorButton stopButton;
    private final EditorButton compileButton;

    private final EditorButton helpButton;

    public MainToolBar() {
        super();

        setFloatable(false);

        popupMenu = new JPopupMenu();
        newAnimationMenu = new JMenuItem("Animation");
        newProjectMenu = new JMenuItem("Project");

        popupMenu.add(newAnimationMenu);
        popupMenu.add(newProjectMenu);

        newButton = new EditorButton();
        newButton.setIcon(Icons.getSmallIcon("new"));
        newButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_NEW));
        newButton.setEnabled(false);

        openButton = new EditorButton();
        openButton.setAction(new OpenFileAction());
        openButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_OPEN));
        openButton.setIcon(Icons.getSmallIcon("open"));

        saveButton = new EditorButton();
        saveButton.setAction(new SaveAction());
        saveButton.setIcon(Icons.getSmallIcon("save"));
        saveButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_SAVE));

        saveAllButton = new EditorButton();
        saveAllButton.setAction(new SaveAllAction());
        saveAllButton.setIcon(Icons.getSmallIcon("save-all"));
        saveAllButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_SAVE_ALL));

        cutButton = new EditorButton();
        cutButton.setAction(new CutAction());
        cutButton.setIcon(Icons.getSmallIcon("cut"));
        cutButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_CUT));
        cutButton.setEnabled(false);

        copyButton = new EditorButton();
        copyButton.setAction(new CopyAction());
        copyButton.setIcon(Icons.getSmallIcon("copy"));
        copyButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_COPY));
        copyButton.setEnabled(false);

        pasteButton = new EditorButton();
        pasteButton.setAction(new PasteAction());
        pasteButton.setIcon(Icons.getSmallIcon("paste"));
        pasteButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_PASTE));
        pasteButton.setEnabled(false);

        deleteButton = new EditorButton();
        deleteButton.setIcon(Icons.getSmallIcon("delete"));
        deleteButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_DELETE));
        deleteButton.setEnabled(false);

        undoButton = new EditorButton();
        undoButton.setAction(new UndoAction());
        undoButton.setIcon(Icons.getSmallIcon("undo"));
        undoButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_UNDO));
        undoButton.setEnabled(false);

        redoButton = new EditorButton();
        redoButton.setAction(new RedoAction());
        redoButton.setIcon(Icons.getSmallIcon("redo"));
        redoButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_REDO));
        redoButton.setEnabled(false);

        pencilButton = new JToggleButton();
        pencilButton.setFocusable(false);
        pencilButton.setAction(new PencilAction());
        pencilButton.setIcon(Icons.getSmallIcon("pencil"));
        pencilButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_DRAW_TILE));

        selectionButton = new JToggleButton();
        selectionButton.setFocusable(false);
        selectionButton.setAction(new SelectionAction());
        selectionButton.setIcon(Icons.getSmallIcon("selection"));
        selectionButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_SELECT_REGION));

        bucketButton = new JToggleButton();
        bucketButton.setFocusable(false);
        bucketButton.setAction(new BucketAction());
        bucketButton.setIcon(Icons.getSmallIcon("bucket"));
        bucketButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_FILL_REGION));

        eraserButton = new JToggleButton();
        eraserButton.setFocusable(false);
        eraserButton.setAction(new EraserAction());
        eraserButton.setIcon(Icons.getSmallIcon("eraser"));
        eraserButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_ERASE_REGION));

        vectorAreaButton = new JToggleButton();
        vectorAreaButton.setFocusable(false);
        vectorAreaButton.setAction(new VectorAreaAction());
        vectorAreaButton.setIcon(Icons.getSmallIcon("layer-select"));
        vectorAreaButton
                .setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_DRAW_VECTOR_AREA));

        vectorButton = new JToggleButton();
        vectorButton.setFocusable(false);
        vectorButton.setAction(new VectorAction());
        vectorButton.setIcon(Icons.getSmallIcon("layer-shape-polyline"));
        vectorButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_DRAW_VECTOR));

        spriteButton = new JToggleButton();
        spriteButton.setFocusable(false);
        spriteButton.setAction(new SpriteAction());
        spriteButton.setIcon(Icons.getSmallIcon("npc"));
        spriteButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_SET_BOARD_SPRITE));

        imageButton = new JToggleButton();
        imageButton.setFocusable(false);
        imageButton.setAction(new LayerImageAction());
        imageButton.setIcon(Icons.getSmallIcon("image-select"));
        imageButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_SET_BOARD_IMAGE));

        lightButton = new JToggleButton();
        lightButton.setFocusable(false);
        lightButton.setAction(new LightAction());
        lightButton.setIcon(Icons.getSmallIcon("flashlight-shine"));
        lightButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_SET_BOARD_LIGHT));
        lightButton.setEnabled(false);

        startPositionButton = new JToggleButton();
        startPositionButton.setFocusable(false);
        startPositionButton.setAction(new StartPositionAction());
        startPositionButton.setIcon(Icons.getSmallIcon("flag-checker"));
        startPositionButton
                .setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_SET_START_POSITION));

        zoomInButton = new EditorButton();
        zoomInButton.setAction(new ZoomInAction());
        zoomInButton.setIcon(Icons.getSmallIcon("zoom-in"));
        zoomInButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_ZOOM_IN));

        zoomOutButton = new EditorButton();
        zoomOutButton.setAction(new ZoomOutAction());
        zoomOutButton.setIcon(Icons.getSmallIcon("zoom-out"));
        zoomOutButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_ZOOM_OUT));

        runButton = new EditorButton();
        runButton.setAction(new RunAction());
        runButton.setIcon(Icons.getSmallIcon("run"));
        runButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_RUN_GAME));
        runButton.setEnabled(false);

        stopButton = new EditorButton();
        stopButton.setAction(new StopAction());
        stopButton.setIcon(Icons.getSmallIcon("stop"));
        stopButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_STOP_GAME));
        stopButton.setEnabled(false);

        compileButton = new EditorButton();
        compileButton.setAction(new CompileAction());
        compileButton.setIcon(Icons.getSmallIcon("box"));
        compileButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_COMPILE_GAME));
        compileButton.setEnabled(false);

        helpButton = new EditorButton();
        helpButton.setAction(new HelpAction());
        helpButton.setIcon(Icons.getSmallIcon("help"));
        helpButton.setToolTipText(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TOOLTIP_HELP));
        helpButton.setEnabled(false);

        // Disable all the buttons for now
        toggleButtonStates(false);

        toolButtonGroup = new ButtonGroup();
        toolButtonGroup.add(pencilButton);
        toolButtonGroup.add(selectionButton);
        toolButtonGroup.add(bucketButton);
        toolButtonGroup.add(eraserButton);
        toolButtonGroup.add(vectorAreaButton);
        toolButtonGroup.add(vectorButton);
        toolButtonGroup.add(spriteButton);
        toolButtonGroup.add(imageButton);
        toolButtonGroup.add(lightButton);
        toolButtonGroup.add(startPositionButton);

        add(newButton);
        add(openButton);
        add(saveButton);
        add(saveAllButton);
        addSeparator();
        add(cutButton);
        add(copyButton);
        add(pasteButton);
        add(deleteButton);
        addSeparator();
        add(undoButton);
        add(redoButton);
        addSeparator();
        add(pencilButton);
        add(selectionButton);
        add(bucketButton);
        add(eraserButton);
        add(vectorAreaButton);
        add(vectorButton);
        add(spriteButton);
        add(imageButton);
        // add(lightButton);
        add(startPositionButton);
        addSeparator();
        add(zoomInButton);
        add(zoomOutButton);
        addSeparator();
        add(runButton);
        add(stopButton);
        addSeparator();
        add(compileButton);
        addSeparator();
        add(helpButton);
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public JMenuItem getNewAnimationMenu() {
        return newAnimationMenu;
    }

    public JMenuItem getNewProjectMenu() {
        return newProjectMenu;
    }

    public EditorButton getNewButton() {
        return newButton;
    }

    public EditorButton getOpenButton() {
        return openButton;
    }

    public EditorButton getSaveButton() {
        return saveButton;
    }

    public EditorButton getSaveAllButton() {
        return saveAllButton;
    }

    public EditorButton getCutButton() {
        return cutButton;
    }

    public EditorButton getCopyButton() {
        return copyButton;
    }

    public EditorButton getPasteButton() {
        return pasteButton;
    }

    public EditorButton getDeleteButton() {
        return deleteButton;
    }

    public EditorButton getUndoButton() {
        return undoButton;
    }

    public EditorButton getRedoButton() {
        return redoButton;
    }

    public ButtonGroup getToolButtonGroup() {
        return toolButtonGroup;
    }

    public JToggleButton getPencilButton() {
        return pencilButton;
    }

    public JToggleButton getSelectionButton() {
        return selectionButton;
    }

    public JToggleButton getBucketButton() {
        return bucketButton;
    }

    public JToggleButton getEraserButton() {
        return eraserButton;
    }

    public EditorButton getZoomInButton() {
        return zoomInButton;
    }

    public EditorButton getZoomOutButton() {
        return zoomOutButton;
    }

    public EditorButton getRunButton() {
        return runButton;
    }

    public EditorButton getStopButton() {
        return stopButton;
    }

    public EditorButton getCompileButton() {
        return compileButton;
    }

    public EditorButton getHelpButton() {
        return helpButton;
    }

    public final void toggleButtonStates(boolean enable) {
        // newButton.setEnabled(enable);
        openButton.setEnabled(enable);
        saveButton.setEnabled(enable);
        saveAllButton.setEnabled(enable);
        cutButton.setEnabled(enable);
        copyButton.setEnabled(enable);
        pasteButton.setEnabled(enable);
        deleteButton.setEnabled(enable);
        pencilButton.setEnabled(enable);
        selectionButton.setEnabled(enable);
        bucketButton.setEnabled(enable);
        eraserButton.setEnabled(enable);
        vectorAreaButton.setEnabled(enable);
        vectorButton.setEnabled(enable);
        spriteButton.setEnabled(enable);
        imageButton.setEnabled(enable);
        // lightButton.setEnabled(enable);
        startPositionButton.setEnabled(enable);
        zoomInButton.setEnabled(enable);
        zoomOutButton.setEnabled(enable);
        runButton.setEnabled(enable);
        // stopButton.setEnabled(enable);
        if (SystemUtils.IS_OS_WINDOWS && enable) {
            // Only enable this feature on Windows for now.
            compileButton.setEnabled(enable);
        }
        // helpButton.setEnabled(enable);
    }

}
