/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.utilities;

import java.io.File;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.Animation;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.assets.Character;
import org.rpgwizard.common.assets.Enemy;
import org.rpgwizard.common.assets.Item;
import org.rpgwizard.common.assets.NPC;
import org.rpgwizard.common.assets.Tileset;
import org.rpgwizard.common.assets.serialization.AbstractJsonSerializer;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.editor.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for upgrading projects between versions, deals with any file format changes that have been made.
 * 
 * @author Joshua Michael Daly
 */
public class ProjectUpgrader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectUpgrader.class);

    private static final String CURRENT_VERSION = AbstractJsonSerializer.FILE_FORMAT_VERSION;

    public static void upgrade(File path) {
        LOGGER.info("Upgrading project located at path=[{}]", path.getAbsolutePath());
        if (true == true) {
            return; // REFACTOR: Disable this for now
        }

        int filesUpgraded = 0;

        // Upgrade project file.
        String extension = CoreProperties.getFullExtension("toolkit.project.extension.json");
        File subPath = path;
        filesUpgraded += upgradeFiles(new String[] { extension.replace(".", "") }, subPath);

        // Upgrade board files.
        extension = CoreProperties.getFullExtension("toolkit.board.extension.json");
        subPath = EditorFileManager.getFullPath(Board.class);
        filesUpgraded += upgradeFiles(new String[] { extension.replace(".", "") }, subPath);

        // Upgrade animation files.
        extension = CoreProperties.getFullExtension("toolkit.animation.extension.json");
        subPath = EditorFileManager.getFullPath(Animation.class);
        filesUpgraded += upgradeFiles(new String[] { extension.replace(".", "") }, subPath);

        // Upgrade character files.
        extension = CoreProperties.getFullExtension("toolkit.character.extension.json");
        subPath = EditorFileManager.getFullPath(Character.class);
        filesUpgraded += upgradeFiles(new String[] { extension.replace(".", "") }, subPath);

        // Upgrade enemy files.
        extension = CoreProperties.getFullExtension("toolkit.enemy.extension.json");
        subPath = EditorFileManager.getFullPath(Enemy.class);
        filesUpgraded += upgradeFiles(new String[] { extension.replace(".", "") }, subPath);

        // Upgrade item files.
        extension = CoreProperties.getFullExtension("toolkit.item.extension.json");
        subPath = EditorFileManager.getFullPath(Item.class);
        filesUpgraded += upgradeFiles(new String[] { extension.replace(".", "") }, subPath);

        // Upgrade NPC files.
        extension = CoreProperties.getFullExtension("toolkit.npc.extension.json");
        subPath = EditorFileManager.getFullPath(NPC.class);
        filesUpgraded += upgradeFiles(new String[] { extension.replace(".", "") }, subPath);

        // Upgrade tileset files.
        extension = CoreProperties.getFullExtension("toolkit.tileset.extension.json");
        subPath = EditorFileManager.getFullPath(Tileset.class);
        filesUpgraded += upgradeFiles(new String[] { extension.replace(".", "") }, subPath);

        LOGGER.info("Upgrade report filesUpgraded=[{}]", filesUpgraded);
    }

    private static int upgradeFiles(String[] extensions, File path) {
        LOGGER.info("Upgrading files extensions=[{}], [path=[{}]", extensions, path);
        if (!path.exists()) {
            return 0;
        }

        int counter = 0;
        Collection<File> files = FileUtils.listFiles(path, extensions, true);
        for (File file : files) {
            AbstractAsset asset = null;
            try {
                AssetHandle handle = AssetManager.getInstance().deserialize(new AssetDescriptor(file.toURI()));
                asset = (AbstractAsset) handle.getAsset();

                if (checkVersion(asset)) {
                    LOGGER.info("Upgrading asset file=[{}]", file.getAbsolutePath());
                    FileTools.saveAsset(asset);
                    counter++;
                }
            } catch (Exception ex) {
                LOGGER.error("Failed to upgrade file=[{}]", file.getAbsolutePath(), ex);
            } finally {
                if (asset != null) {
                    AssetManager.getInstance().removeAsset(asset);
                }
            }
        }

        return counter;
    }

    private static boolean checkVersion(AbstractAsset asset) {
        if (asset == null) {
            return false;
        }
        return Double.valueOf(asset.getVersion()) < Double.valueOf(CURRENT_VERSION);
    }

    public static void main(String[] args) {
        System.setProperty("project.path", "D:\\Desktop\\The Wizard's Tower");
        Driver.registerResolvers();
        Driver.registerSerializers();

        File testPath = new File("D:\\Desktop\\The Wizard's Tower");
        ProjectUpgrader.upgrade(testPath);
    }

}
