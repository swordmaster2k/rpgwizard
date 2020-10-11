/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import javax.swing.JFileChooser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.rpgwizard.migrator.asset.version1.OldAbstractAsset;
import org.rpgwizard.migrator.asset.version1.animation.OldAnimation;
import org.rpgwizard.migrator.asset.version1.board.OldBoard;
import org.rpgwizard.migrator.asset.version1.character.OldCharacter;
import org.rpgwizard.migrator.asset.version1.enemy.OldEnemy;
import org.rpgwizard.migrator.asset.version1.game.OldGame;
import org.rpgwizard.migrator.asset.version1.npc.OldNpc;
import org.rpgwizard.migrator.asset.version1.tileset.OldTileset;
import org.rpgwizard.migrator.asset.version2.AbstractAsset;
import org.rpgwizard.migrator.asset.version2.animation.Animation;
import org.rpgwizard.migrator.asset.version2.map.Map;
import org.rpgwizard.migrator.asset.version2.sprite.Sprite;
import org.rpgwizard.migrator.asset.version2.tileset.Tileset;

/**
 * Initial migration tool for RPGWizard 1.x to RPGWizard 2.x for testing.
 * 
 * This will be revisited closer to the release of 2.x for further improvements.
 * 
 * @author Joshua Michael Daly
 */
@Slf4j
public class Driver {
    
    public static void main(String[] args) {
        log.info("Starting migration tool, user.dir=[{}]", System.getProperty("user.dir"));
        
        var input = askForInput();
        if (input.isPresent()) {
            var gameDir = input.get();
            log.info("Got user input, gameDir=[{}]", gameDir);
            if (!validateInput(gameDir)) {
                log.error("Invalid input, not an RPGWizard game!");
                // TODO: Throw exception, and handle it
            } else {
                var outputDir = makeOutputDir(gameDir);
                if (outputDir.isPresent()) {
                    migrate(gameDir, outputDir.get());
                    showOutput(outputDir.get());
                }
            }
        } else {
            log.info("User cancelled the migration");
        }
        
        log.info("Stopping migraton tool");
    }
    
    private static Optional<File> askForInput() {
        var chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select Game to Migrate");
        chooser.setAcceptAllFileFilterUsed(false);
        
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return Optional.of(chooser.getSelectedFile());
        }
        
        return Optional.empty();
    }
    
    private static boolean validateInput(File input) {
        var gameFiles = input.listFiles((File file) -> { 
            return file.getName().endsWith(".game");
        });
        return gameFiles.length == 1;
    }
    
    private static void showOutput(File outputDir) {
        if (Desktop.isDesktopSupported()) {
            var desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(outputDir.toURI());
                } catch (IOException ex) {
                    log.error("Could not browse outputDir=[{}]", outputDir, ex);
                }
            }
        }
    }
    
    private static Optional<File> makeOutputDir(File gameDir) {
        var outputDir = new File(System.getProperty("user.dir") + "/output/" + gameDir.getName());
        if (outputDir.exists()) {
            log.error("Outdir already exists aborting! outputDir=[{}]", outputDir);
            return Optional.empty();
        }
        
        try {
            Files.createDirectories(Paths.get(outputDir.getAbsolutePath()));
            return Optional.of(outputDir);
        } catch (IOException ex) {
            log.error("Could not create outputDir!", ex);
            return Optional.empty();
        }        
    }
    
    private static void migrate(File gameDir, File outputDir) {
        // Migrate the game file
        try {
            var gameFiles = gameDir.listFiles((File file) -> {
                return file.getName().endsWith(".game");
            });
            migrateAsset(gameDir, outputDir, gameFiles[0], OldGame.class);
        } catch (IOException ex) {
            log.error("Failed to migrate game file!", ex);
            // TODO: abort migration if this happens
        }
        
        // Migrate all the subdirectories
        var dirs = gameDir.listFiles(File::isDirectory);
        for (var dir : dirs) {
            var dirName = dir.getName();
            log.info("Migrating contents of, dir=[{}], dirName=[{}]", dir, dirName);
            switch (dirName) {
                case "Animations":
                    migrateDir(dir, new File(outputDir.getAbsoluteFile() + "/animations"), OldAnimation.class, Animation.class, "animation");
                    break;
                case "Boards":
                    migrateDir(dir, new File(outputDir.getAbsoluteFile() + "/maps"), OldBoard.class, Map.class, "board");
                    break;
                case "Characters":
                    migrateDir(dir, new File(outputDir.getAbsoluteFile() + "/sprites"), OldCharacter.class, Sprite.class, "character");
                    break;
                case "Enemies":
                    migrateDir(dir, new File(outputDir.getAbsoluteFile() + "/sprites"), OldEnemy.class, Sprite.class, "enemy");
                    break;
                case "Fonts":
                    migrateDir(dir, new File(outputDir.getAbsoluteFile() + "/fonts"));
                    break;
                case "Graphics":
                    migrateDir(dir, new File(outputDir.getAbsoluteFile() + "/textures"));
                    break;
                case "Items":
                    // No plan for these at the moment
                    break;
                case "NPCs":
                    migrateDir(dir, new File(outputDir.getAbsoluteFile() + "/sprites"), OldNpc.class, Sprite.class, "npc");
                    break;
                case "Programs":
                    migrateDir(dir, new File(outputDir.getAbsoluteFile() + "/scripts"));
                    break;
                case "Sounds":
                    migrateDir(dir, new File(outputDir.getAbsoluteFile() + "/sounds"));
                    break;
                case "TileSets":
                    migrateDir(dir, new File(outputDir.getAbsoluteFile() + "/tilesets"), OldTileset.class, Tileset.class, "tileset");
                    break;
                default:
                    log.warn("Unknown directory type, dirName=[{}], skipping...", dirName);
            }
        }
    }
    
    private static void migrateDir(File src, File dest) {
        log.info("Migrating src=[{}], dest=[{}]", src, dest);
        try {
            FileUtils.copyDirectory(src, dest);
        } catch (IOException ex) {
            log.error("Could not create asset dir, dest=[{}]", dest);
        }
    }
    
    private static void migrateDir(File src, File dest, Class<? extends OldAbstractAsset> oldAssetType, Class<? extends AbstractAsset> newAssetType, String ext) {
        log.info("Migrating dir, oldAssetType=[{}] to newAssetType=[{}], src=[{}], dest=[{}], ext=[{}]", oldAssetType, newAssetType, src, dest, ext);
        
        var srcAssets = FileUtils.listFiles(src, new String[]{ext}, true);
        try {
            Files.createDirectories(Paths.get(dest.getAbsolutePath()));
        } catch (IOException ex) {
            log.error("Could not create asset dir, dest=[{}]", dest);
            return;
        }
        
        for (var srcAsset : srcAssets) {
            try {
                migrateAsset(src, dest, srcAsset, oldAssetType);
            } catch (IOException ex) {
                log.error("Could not read asset! srcAsset=[{}], oldAssetType=[{}], ex=[{}]", srcAsset, oldAssetType, ex);
            }
        }
    }
    
    private static void migrateAsset(File src, File dest, File srcAsset, Class<? extends OldAbstractAsset> oldAssetType) throws IOException {
        log.info("Migrating asset, oldAssetType=[{}], src=[{}], dest=[{}]", oldAssetType, src, dest);
        
        var oldAsset = AssetIO.readAsset(srcAsset, oldAssetType);
        var newAsset = AssetMapperFactory.map(oldAsset);
        if (newAsset.isPresent()) {
            var targetPath = srcAsset.getAbsolutePath().replace(src.getAbsolutePath(), dest.getAbsolutePath());
            targetPath = ExtensionMapper.map(targetPath);
            AssetIO.writeAsset(newAsset.get(), new File(targetPath));
        }
    }
    
}
