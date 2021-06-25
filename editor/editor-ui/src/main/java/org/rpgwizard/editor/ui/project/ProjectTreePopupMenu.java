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
import org.rpgwizard.common.assets.game.Game;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.ui.actions.DeleteFileAction;
import org.rpgwizard.editor.ui.actions.NewFolderAction;
import org.rpgwizard.editor.ui.actions.OpenFolderAction;
import org.rpgwizard.editor.utilities.EditorFileManager;

/**
 *
 * @author Joshua Michael Daly
 */
public final class ProjectTreePopupMenu extends JPopupMenu {

    private final JMenu newMenu;
    private final JMenuItem newFolderItem;

    private final JMenuItem openItem;
    private final JMenuItem deleteItem;

    private String filePath;

    public ProjectTreePopupMenu() {
        newFolderItem = new JMenuItem("Folder...");
        newFolderItem.addActionListener((ActionEvent ae) -> {
            new NewFolderAction(new File(filePath)).actionPerformed(null);
        });

        newMenu = new JMenu("New");
        newMenu.add(newFolderItem);

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
            if (canDelete()) {
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

        add(newMenu);
        add(new JSeparator());
        add(openItem);
        add(new JSeparator());
        add(deleteItem);
    }

    public void show(Component invoker, int x, int y, String filePath) {
        if (StringUtils.isBlank(filePath) || !Files.exists(Paths.get(filePath))) {
            return;
        }
        this.filePath = filePath;

        if (Files.isDirectory(Paths.get(filePath))) {
            newMenu.setVisible(true);
        } else {
            newMenu.setVisible(false);
        }
        if (canDelete()) {
            deleteItem.setVisible(true);
        } else {
            deleteItem.setVisible(false);
        }

        super.show(invoker, x, y);
    }

    private boolean canDelete() {
        if (Files.isRegularFile(Paths.get(filePath))) {
            List<String> exts = getDeletableExtensions();
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

    private List<String> getDeletableExtensions() {
        List<String> exts = new ArrayList<>();
        exts.addAll(Arrays.asList(EditorFileManager.getTKFileExtensions()));
        exts.addAll(Arrays.asList(EditorFileManager.getImageExtensions()));
        exts.remove(EditorFileManager.getTypeExtensions(Game.class)[0]);
        return exts;
    }

}
