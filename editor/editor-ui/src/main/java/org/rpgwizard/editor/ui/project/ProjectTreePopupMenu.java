/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.project;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.rpgwizard.common.assets.Script;
import org.rpgwizard.common.assets.animation.Animation;
import org.rpgwizard.common.assets.game.Game;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.assets.sprite.Sprite;
import org.rpgwizard.common.assets.tileset.Tileset;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.ui.actions.DeleteFileAction;
import org.rpgwizard.editor.ui.actions.asset.NewAssetAction;
import org.rpgwizard.editor.ui.actions.NewFolderAction;
import org.rpgwizard.editor.ui.actions.OpenFolderAction;
import org.rpgwizard.editor.ui.actions.RenameAction;
import org.rpgwizard.editor.utilities.EditorFileManager;

/**
 *
 * @author Joshua Michael Daly
 */
public final class ProjectTreePopupMenu extends JPopupMenu {

    private final JMenu newMenu;
    private final JMenuItem newFolderItem;
    private final JMenuItem newAssetItem;

    private final JMenuItem openItem;
    private final JMenuItem deleteItem;
    private final JMenuItem renameItem;

    private String filePath;

    public ProjectTreePopupMenu() {
        newFolderItem = new JMenuItem("Folder...");
        newFolderItem.addActionListener((ActionEvent ae) -> {
            new NewFolderAction(new File(filePath)).actionPerformed(null);
        });

        newAssetItem = new JMenuItem();

        newMenu = new JMenu("New");
        newMenu.add(newFolderItem);
        newMenu.add(newAssetItem);

        openItem = new JMenuItem("Open");
        openItem.addActionListener((ActionEvent ae) -> {
            if (Files.isRegularFile(Paths.get(filePath))) {
                MainWindow.getInstance().openAssetEditor(new File(filePath));
            } else if (Files.isDirectory(Paths.get(filePath))) {
                new OpenFolderAction(new File(filePath)).actionPerformed(null);
            }
        });

        deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener((ActionEvent ae) -> {
            if (canModify()) {
                // Confirm user input
                String title = "Delete " + FilenameUtils.getName(filePath) + "?";
                String message = "Are sure you want to delete " + FilenameUtils.getName(filePath) + "?";
                int result = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);

                switch (result) {
                case 0:
                    new DeleteFileAction(new File(filePath)).actionPerformed(ae);
                case 1:
                case 2:
                default:
                }
            }
        });

        renameItem = new JMenuItem("Rename");
        renameItem.addActionListener((ActionEvent ae) -> {
            new RenameAction(new File(filePath)).actionPerformed(ae);
        });

        add(newMenu);
        add(new JSeparator());
        add(openItem);
        add(new JSeparator());
        add(renameItem);
        add(new JSeparator());
        add(deleteItem);
    }

    public void show(Component invoker, int x, int y, String filePath) {
        if (StringUtils.isBlank(filePath) || !Files.exists(Paths.get(filePath))) {
            return;
        }
        this.filePath = filePath;

        if (Files.isDirectory(Paths.get(filePath))) {
            prepareNewAssetItem();
            newMenu.setEnabled(true);
        } else {
            newMenu.setEnabled(false);
        }
        if (canModify()) {
            deleteItem.setEnabled(true);
        } else {
            deleteItem.setEnabled(false);
        }
        if (canModify()) {
            renameItem.setEnabled(true);
        } else {
            renameItem.setEnabled(false);
        }

        super.show(invoker, x, y);
    }

    private boolean canModify() {
        if (Files.isRegularFile(Paths.get(filePath))) {
            List<String> exts = getModifibleExtensions();
            return exts.contains(FilenameUtils.getExtension(filePath));
        } else if (Files.isDirectory(Paths.get(filePath))) {
            // Primitive checks on directory names
            if (FilenameUtils.getName(filePath).equalsIgnoreCase(EditorFileManager.getProjectPath().getName())) {
                return false;
            }
            return !EditorFileManager.getTypeDirectories().contains(FilenameUtils.getName(filePath).toLowerCase());
        }

        return false;
    }

    private List<String> getModifibleExtensions() {
        List<String> exts = new ArrayList<>();
        exts.addAll(Arrays.asList(EditorFileManager.getTKFileExtensions()));
        exts.addAll(Arrays.asList(EditorFileManager.getImageExtensions()));
        exts.remove(EditorFileManager.getTypeExtensions(Game.class)[0]);
        return exts;
    }

    private void prepareNewAssetItem() {
        if (filePath.contains(CoreProperties.getProperty("rpgwizard.directory.animations"))) {
            newAssetItem.setAction(new NewAssetAction(Animation.class, new File(filePath)));
            newAssetItem.setText("New Animation...");
            newAssetItem.setVisible(true);
        } else if (filePath.contains(CoreProperties.getProperty("rpgwizard.directory.maps"))) {
            newAssetItem.setAction(new NewAssetAction(Map.class, new File(filePath)));
            newAssetItem.setText("New Map...");
            newAssetItem.setVisible(true);
        } else if (filePath.contains(CoreProperties.getProperty("rpgwizard.directory.scripts"))) {
            newAssetItem.setAction(new NewAssetAction(Script.class, new File(filePath)));
            newAssetItem.setText("New Script...");
            newAssetItem.setVisible(true);
        } else if (filePath.contains(CoreProperties.getProperty("rpgwizard.directory.sprites"))) {
            newAssetItem.setAction(new NewAssetAction(Sprite.class, new File(filePath)));
            newAssetItem.setText("New Sprite...");
            newAssetItem.setVisible(true);
        } else if (filePath.contains(CoreProperties.getProperty("rpgwizard.directory.tilesets"))) {
            newAssetItem.setAction(new NewAssetAction(Tileset.class, new File(filePath)));
            newAssetItem.setText("New Tileset...");
            newAssetItem.setVisible(true);
        } else {
            newAssetItem.setVisible(false);
        }
    }

}
