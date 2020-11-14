/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;
import static javax.swing.JFileChooser.SAVE_DIALOG;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FileUtils;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.Animation;
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.assets.Character;
import org.rpgwizard.common.assets.Enemy;
import org.rpgwizard.common.assets.Item;
import org.rpgwizard.common.assets.NPC;
import org.rpgwizard.common.assets.Program;
import org.rpgwizard.common.assets.Game;
import org.rpgwizard.common.assets.SpecialMove;
import org.rpgwizard.common.assets.TileSet;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.ui.SingleRootFileSystemView;

/**
 *
 * @author Joshua Michael Daly
 */
public class EditorFileManager {

    private static JFileChooser FILE_CHOOSER = new JFileChooser(FileTools.getProjectsDirectory()) {
        @Override
        public void approveSelection() {
            File f = getSelectedFile();
            if (f.exists() && getDialogType() == SAVE_DIALOG) {
                int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                switch (result) {
                case JOptionPane.YES_OPTION:
                    super.approveSelection();
                    return;
                case JOptionPane.NO_OPTION:
                    return;
                case JOptionPane.CLOSED_OPTION:
                    return;
                case JOptionPane.CANCEL_OPTION:
                    cancelSelection();
                    return;
                }
            }
            super.approveSelection();
        }
    };

    public static JFileChooser getFileChooser() {
        return FILE_CHOOSER;
    }

    public static void setFileChooser(JFileChooser chooser) {
        FILE_CHOOSER = chooser;
    }

    public static File getPath(String relativePath) {
        return new File(System.getProperty("project.path") + File.separator + relativePath);
    }

    public static File getFullPath(Class<? extends AbstractAsset> type) {
        return getPath(getTypeSubdirectory(type) + File.separator);
    }

    public static String getRelativePath(File fullPath) {
        return getRelativePath(fullPath, new File(System.getProperty("project.path") + File.separator));
    }

    public static String getRelativePath(File fullPath, File relativeTo) {
        return fullPath.getPath().replace(relativeTo.getPath() + File.separator, "");
    }

    public static File getProjectPath() {
        return new File(System.getProperty("project.path"));
    }

    public static String getGraphicsPath() {
        return System.getProperty("project.path") + File.separator
                + CoreProperties.getProperty("toolkit.directory.graphics") + File.separator;
    }

    public static String getTypeSubdirectory(Class<? extends AbstractAsset> type) {
        if (type == Animation.class) {
            return CoreProperties.getProperty("toolkit.directory.animations");
        } else if (type == Board.class) {
            return CoreProperties.getProperty("toolkit.directory.board");
        } else if (type == Enemy.class) {
            return CoreProperties.getProperty("toolkit.directory.enemy");
        } else if (type == Item.class) {
            return CoreProperties.getProperty("toolkit.directory.item");
        } else if (type == NPC.class) {
            return CoreProperties.getProperty("toolkit.directory.npc");
        } else if (type == Character.class) {
            return CoreProperties.getProperty("toolkit.directory.character");
        } else if (type == Program.class) {
            return CoreProperties.getProperty("toolkit.directory.program");
        } else if (type == SpecialMove.class) {
            return CoreProperties.getProperty("toolkit.directory.specialmove");
        } else if (type == TileSet.class) {
            return CoreProperties.getProperty("toolkit.directory.tileset");
        } else {
            return "";
        }
    }

    public static String getGraphicsSubdirectory() {
        return CoreProperties.getProperty("toolkit.directory.graphics");
    }

    public static String getTypeFilterDescription(Class<? extends AbstractAsset> type) {
        if (type == Animation.class) {
            return "Animations";
        } else if (type == Board.class) {
            return "Boards";
        } else if (type == Enemy.class) {
            return "Enemies";
        } else if (type == Item.class) {
            return "Items";
        } else if (type == NPC.class) {
            return "NPCs";
        } else if (type == Character.class) {
            return "Characters";
        } else if (type == Program.class) {
            return "Programs";
        } else if (type == Game.class) {
            return "Projects";
        } else if (type == TileSet.class) {
            return "Tilesets";
        } else if (type == SpecialMove.class) {
            return "Special Moves";
        } else {
            return "Toolkit Files";
        }
    }

    public static String getImageFilterDescription() {
        return "Supported Files";
    }

    public static String[] getTKFileExtensions() {
        return CoreProperties.getProperty("toolkit.supported.extensions").split(",");
    }

    public static String[] getTypeExtensions(Class<? extends AbstractAsset> type) {
        if (type == Animation.class) {
            return new String[] { CoreProperties.getProperty("toolkit.animation.extension.default") };
        } else if (type == Board.class) {
            return new String[] { CoreProperties.getProperty("toolkit.board.extension.default") };
        } else if (type == Enemy.class) {
            return new String[] { CoreProperties.getProperty("toolkit.enemy.extension.default") };
        } else if (type == Item.class) {
            return new String[] { CoreProperties.getProperty("toolkit.item.extension.default") };
        } else if (type == NPC.class) {
            return new String[] { CoreProperties.getProperty("toolkit.npc.extension.default") };
        } else if (type == Character.class) {
            return new String[] { CoreProperties.getProperty("toolkit.character.extension.default") };
        } else if (type == Program.class) {
            return new String[] { CoreProperties.getProperty("toolkit.program.extension.default") };
        } else if (type == Game.class) {
            return new String[] { CoreProperties.getProperty("toolkit.project.extension.default") };
        } else if (type == TileSet.class) {
            return new String[] { CoreProperties.getProperty("toolkit.tileset.extension.default") };
        } else if (type == SpecialMove.class) {
            return new String[] { CoreProperties.getProperty("toolkit.specialmove.extension.default") };
        } else {
            return getTKFileExtensions();
        }
    }

    public static String[] getImageExtensions() {
        return new String[] { "png", "gif", "jpg", "jpeg", "bmp" };
    }

    public static void openFile() {
        setFileChooserSubdirAndFilters("", "Toolkit Files", getTKFileExtensions());
        if (FILE_CHOOSER.showOpenDialog(MainWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
            MainWindow.getInstance().openAssetEditor(FILE_CHOOSER.getSelectedFile());
        }
    }

    public static void openFile(String path, String description, String[] extensions) {
        setFileChooserSubdirAndFilters(path, description, extensions);
        if (FILE_CHOOSER.showOpenDialog(MainWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
            MainWindow.getInstance().openAssetEditor(FILE_CHOOSER.getSelectedFile());
        }
    }

    public static File backupFile(File original) throws IOException {
        File backup = new File(original.getAbsoluteFile() + ".tmp");
        FileUtils.copyFile(original, backup);

        return backup;
    }

    /**
     * Browse for aSystem.setProperty("project.path", this.fileChooser.getCurrentDirectory().getParent() +
     * File.separator + PropertiesSingleton.getProperty("toolkit.directory.game") + File.separator + fileName +
     * File.separator);
     *
     * this.activeProject = new Project(this.fileChooser.getSelectedFile(), System.getProperty("project.path"));
     *
     * ProjectEditor projectEditor = new ProjectEditor(this.activeProject); this.desktopPane.add(projectEditor,
     * BorderLayout.CENTER);
     *
     * projectEditor.addInternalFrameListener(this); projectEditor.setWindowParent(this); projectEditor.toFront();
     *
     * this.selectToolkitWindow(projectEditor); this.setTitle(this.getTitle() + " - " +
     * this.activeProject.getGameTitle());
     *
     * this.menuBar.enableMenus(true); this.toolBar.toggleButtonStates(true); file of the given type, starting in the
     * subdirectory for that type, and return its location relative to the subdirectory for that type. Filters by
     * extensions relevant to that type. This is a shortcut method for browseLocationBySubdir().
     *
     * @param type
     *            a BasicType class
     * @return the location of the file the user selects, relative to the subdirectory corresponding to that type; or
     *         null if no file or an invalid file is selected (see browseLocationBySubdir())
     */
    public static String browseByTypeRelative(Class<? extends AbstractAsset> type) {
        File path = browseByType(type);
        if (path == null) {
            return null;
        }
        return getRelativePath(path, getPath(getTypeSubdirectory(type)));
    }

    /**
     * Browse for a file of the given type, starting in the subdirectory for that type, and return its location. Filters
     * by extensions relevant to that type. This is a shortcut method for browseLocationBySubdir().
     *
     * @param type
     *            a BasicType class
     * @return the location of the file the user selects; or null if no file or an invalid file is selected (see
     *         browseLocationBySubdir())
     */
    public static File browseByType(Class<? extends AbstractAsset> type) {
        String subdir = getTypeSubdirectory(type);
        String desc = getTypeFilterDescription(type);
        String[] exts = getTypeExtensions(type);
        return browseLocationBySubdir(subdir, desc, exts);
    }

    /**
     * Browse for a file of the given type, starting in the subdirectory for that type, and return its location relative
     * to the subdirectory for that type. The file may not exist yet if the user types it in. Filters by extensions
     * relevant to that type. This is a shortcut method for saveLocationBySubdir().
     *
     * @param type
     *            a BasicType class
     * @return the location of the file the user selects, relative to the subdirectory corresponding to that type; or
     *         null if no file or an invalid file is selected (see saveLocationBySubdir())
     */
    public static String saveByTypeRelative(Class<? extends AbstractAsset> type) {
        File path = saveByType(type);
        if (path == null) {
            return null;
        }
        return getRelativePath(path, getPath(getTypeSubdirectory(type)));
    }

    /**
     * Browse for a file of the given type, starting in the subdirectory for that type, and return its location. The
     * file may not exist yet if the user types it in. Filters by extensions relevant to that type. This is a shortcut
     * method for saveLocationBySubdir().
     *
     * @param type
     *            a AbstractAsset class
     * @return the location of the file the user selects; or null if no file or an invalid file is selected (see
     *         saveLocationBySubdir())
     */
    public static File saveByType(Class<? extends AbstractAsset> type) {
        String subdir = getTypeSubdirectory(type);
        String desc = getTypeFilterDescription(type);
        String[] exts = getTypeExtensions(type);
        return saveLocationBySubdir(subdir, desc, exts);
    }

    /**
     * Browse for a file of the given image, starting in the subdirectory for that type, and return its location. The
     * file may not exist yet if the user types it in. Filters by extensions relevant to that type. This is a shortcut
     * method for saveLocationBySubdir().
     *
     * @return the location of the file the user selects; or null if no file or an invalid file is selected (see
     *         saveLocationBySubdir())
     */
    public static File saveImage() {
        String subdir = getGraphicsSubdirectory();
        String desc = getTypeFilterDescription(null);
        String[] exts = getImageExtensions();
        return saveLocationBySubdir(subdir, desc, exts);
    }

    /**
     * Browse for a file with one of the given extensions, starting in the given subdirectory of the project, and return
     * its location.
     *
     * @param subdirectory
     *            where within the project to start the file chooser
     * @param description
     *            what to name the filter (for example, "Program Files")
     * @param extensions
     *            the file extensions to filter by (the portion of the file name after the last ".")
     * @return the location of the file the user selects; or null if no file or an invalid file is selected
     */
    public static File browseLocationBySubdir(String subdirectory, String description, String... extensions) {
        File path = setFileChooserSubdirAndFilters(subdirectory, description, extensions);
        if (FILE_CHOOSER.showOpenDialog(MainWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
            if (validateFileChoice(path, extensions)) {
                return FILE_CHOOSER.getSelectedFile();
            }
        }
        return null;
    }

    /**
     * Browse for a file with one of the given extensions, starting in the given subdirectory of the project, and return
     * its location.
     *
     * @param subdirectory
     *            where within the project to start the file chooser
     * @param description
     *            what to name the filter (for example, "Program Files")
     * @param extensions
     *            the file extensions to filter by (the portion of the file name after the last ".")
     * @return the location of the file the user selects; or null if no file or an invalid file is selected
     */
    public static File[] browseLocationBySubdirMultiSelect(String subdirectory, String description,
            String... extensions) {
        File[] files = new File[0];
        FILE_CHOOSER.setMultiSelectionEnabled(true);
        File path = setFileChooserSubdirAndFilters(subdirectory, description, extensions);
        if (FILE_CHOOSER.showOpenDialog(MainWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
            if (validateFileChoice(path, extensions)) {
                files = FILE_CHOOSER.getSelectedFiles();
            }
        }
        FILE_CHOOSER.setMultiSelectionEnabled(false);

        return files;
    }

    /**
     * Browse for a file with one of the given extensions, starting in the given subdirectory of the project, and return
     * its location. May return a new filename if the user types one rather than selecting an existing file.
     *
     * @param subdirectory
     *            where within the project to start the file chooser
     * @param description
     *            what to name the filter (for example, "Program Files")
     * @param extensions
     *            the file extensions to filter by (the portion of the file name after the last ".")
     * @return the location of the file the user selects; or null if no file or an invalid file is selected
     */
    public static File saveLocationBySubdir(String subdirectory, String description, String... extensions) {
        File path = setFileChooserSubdirAndFilters(subdirectory, description, extensions);

        setSuggestedFile(extensions[0]);

        if (FILE_CHOOSER.showSaveDialog(MainWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
            if (validateFileChoice(path, extensions)) {
                return FILE_CHOOSER.getSelectedFile();
            } else {
                File file = FILE_CHOOSER.getSelectedFile();
                return new File(file.getAbsolutePath() + "." + extensions[0]);
            }
        }
        return null;
    }

    /**
     * Sets the file chooser's directory to the given subdirectory of the project, sets its filter to the given
     * description and extensions, and returns its new file path.
     *
     * @param subdirectory
     * @param description
     * @param extensions
     * @return
     */
    public static File setFileChooserSubdirAndFilters(String subdirectory, String description, String... extensions) {
        File path = getPath(subdirectory);
        if (path.exists()) {
            FILE_CHOOSER.setFileSystemView(new SingleRootFileSystemView(path));
            FILE_CHOOSER.setCurrentDirectory(path);
        }

        FILE_CHOOSER.resetChoosableFileFilters();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensions);
        FILE_CHOOSER.setFileFilter(filter);
        return path;
    }

    /**
     * Gets the location, relative to the given path, of the file chooser's currently selected file. Returns null if the
     * file does not end with one of the given extensions.
     *
     * @param extensions
     *            the file is required to end with one of these (do not include the dot that comes immediately before
     *            the extension)
     * @param path
     *            the location will be relative to this path
     * @return the location of the currently selected file of one of the given extensions, relative to the given path;
     *         or null if the extension does not match
     */
    public static boolean validateFileChoice(File path, String... extensions) {
        List<File> files = new ArrayList<>();
        if (FILE_CHOOSER.getSelectedFiles().length > 0) {
            files.addAll(Arrays.asList(FILE_CHOOSER.getSelectedFiles()));
        } else if (FILE_CHOOSER.getSelectedFile() != null) {
            files.add(FILE_CHOOSER.getSelectedFile());
        }

        if (files.isEmpty()) {
            return false;
        }

        boolean valid = true;
        for (File file : files) {
            String fileName = file.getName().toLowerCase();
            boolean extValid = false;
            for (String ext : extensions) {
                if (fileName.endsWith("." + ext)) {
                    extValid = true;
                    break;
                }
            }
            valid &= extValid;
        }

        return valid;
    }

    /**
     * Validates that the target path starts with the required path.
     * 
     * @param target
     * @param required
     * @return
     */
    public static boolean validatePathStartsWith(File target, File required) {
        Path child = Paths.get(target.toURI()).toAbsolutePath();
        Path parent = Paths.get(required.toURI()).toAbsolutePath();
        boolean valid = child.startsWith(parent);
        if (!valid) {
            String folder = required.getName();
            String message = "Please select an image file from within the project's \"" + folder + "\" folder!";
            JOptionPane.showMessageDialog(MainWindow.getInstance(), message, "Invalid File Choice",
                    JOptionPane.ERROR_MESSAGE);
        }
        return valid;
    }

    private static void setSuggestedFile(String ext) {
        // Set the suggested file name to use.
        String filePath = "untitled." + ext;
        FILE_CHOOSER.setSelectedFile(new File(filePath));
    }

}
