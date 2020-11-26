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
import java.net.URISyntaxException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FileUtils;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.game.Game;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.properties.EditorProperties;
import org.rpgwizard.editor.properties.EditorProperty;
import org.rpgwizard.editor.ui.SingleRootFileSystemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public final class FileTools {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileTools.class);

    public static String getJreDirectory() throws URISyntaxException {
        return getExecutionPath(FileTools.class) + File.separator
                + EditorProperties.getProperty(EditorProperty.EDITOR_JRE_DIRECOTRY);
    }

    public static String getBuildsDirectory() throws URISyntaxException {
        return getExecutionPath(FileTools.class) + File.separator
                + EditorProperties.getProperty(EditorProperty.EDITOR_BUILDS_DIRECOTRY);
    }

    public static String getProjectsDirectory() {
        try {
            return getExecutionPath(FileTools.class) + File.separator
                    + CoreProperties.getProperty("toolkit.directory.projects");
        } catch (URISyntaxException ex) {
            LOGGER.error("Could not find projects file directory under exectuion path!");
            return System.getProperty("user.home");
        }
    }

    public static String getExecutionPath(Class clazz) throws URISyntaxException {
        String executionPath = System.getProperty("org.rpgwizard.execution.path");
        if (executionPath == null) {
            final String path = clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            if (path.endsWith(".jar")) {
                // It identified the JAR file, instead get its parent.
                System.setProperty("org.rpgwizard.execution.path",
                        new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
                                .getParentFile().getAbsolutePath());
            } else {
                System.setProperty("org.rpgwizard.execution.path",
                        new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
                                .getAbsolutePath());
            }
        }
        return System.getProperty("org.rpgwizard.execution.path");
    }

    public static boolean createBlankProject(String path, String projectName) {
        boolean result = true;

        result &= createDirectory(path + File.separator + projectName);
        result &= createAssetDirectories(path + File.separator + projectName);

        return result;
    }

    public static Game createProjectFromTemplate(String path, String projectName, String template)
            throws IOException, AssetException {
        // Copy the template into new directory
        File srcDir = new File(path + File.separator + template);
        File destDir = new File(path + File.separator + projectName);
        FileUtils.copyDirectory(srcDir, destDir);

        // Replace the .game file with new one
        final String projectsDir = FileTools.getProjectsDirectory();
        final String projectExt = CoreProperties.getDefaultExtension(Game.class);
        final String prefix = projectsDir + File.separator + projectName + File.separator;
        File srcFile = new File(prefix + template + projectExt);
        File destFile = new File(prefix + projectName + projectExt);
        FileUtils.moveFile(srcFile, destFile);
        FileUtils.deleteQuietly(srcFile);

        // Update the project name
        AssetHandle handle = AssetManager.getInstance().deserialize(new AssetDescriptor(destFile.toURI()));
        Game project = (Game) handle.getAsset();
        project.setName(projectName);
        AssetManager.getInstance().serialize(AssetManager.getInstance().getHandle(project));

        return project;
    }

    public static boolean createAssetDirectories(String path) {
        boolean result = true;

        for (String directory : CoreProperties.getDirectories()) {
            result &= createDirectory(path + File.separator + directory);
        }

        return result;
    }

    private static boolean createDirectory(String path) {
        File directory = new File(path);

        if (!directory.exists()) {
            return directory.mkdirs();
        } else {
            return true;
        }
    }

    public static File doChoosePath() {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(MainWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getCurrentDirectory();
        }

        return null;
    }

    public static File doChooseFile(String extension, String directory, String type) {
        if (MainWindow.getInstance().getActiveProject() != null) {
            File projectPath = new File(System.getProperty("project.path") + File.separator + directory);

            if (projectPath.exists()) {
                JFileChooser fileChooser = new JFileChooser(new SingleRootFileSystemView(projectPath));
                fileChooser.setAcceptAllFileFilterUsed(false);

                FileNameExtensionFilter filter = new FileNameExtensionFilter(type, extension);
                fileChooser.setFileFilter(filter);

                if (fileChooser.showOpenDialog(MainWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
                    return fileChooser.getSelectedFile();
                }
            }
        }

        return null;
    }

    public static void saveAsset(AbstractAsset asset) throws Exception {
        File original;
        File backup = EditorFileManager.backupFile(new File(asset.getDescriptor().getURI()));
        original = new File(asset.getDescriptor().getURI());
        try {
            AssetManager.getInstance().serialize(AssetManager.getInstance().getHandle(asset));
        } catch (IOException | AssetException ex) {
            LOGGER.error("Failed to save asset=[{}].", asset, ex);
            if (backup != null) {
                // Existing file that failed during save.
                FileUtils.copyFile(backup, original);
                FileUtils.deleteQuietly(backup);
            } else {
                // New file that failed during save.
                asset.setDescriptor(null);
                FileUtils.deleteQuietly(original);
            }
            throw new Exception("Failed to save asset.");
        } finally {
            if (backup != null) {
                FileUtils.deleteQuietly(backup);
            }
        }
    }
}
