/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import javax.swing.JFileChooser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.mapstruct.factory.Mappers;
import org.rpgwizard.migrator.asset.mapper.OldAnimationToAnimationMapper;
import org.rpgwizard.migrator.asset.mapper.OldBoardToMapMapper;
import org.rpgwizard.migrator.asset.version1.OldAbstractAsset;
import org.rpgwizard.migrator.asset.version1.animation.OldAnimation;
import org.rpgwizard.migrator.asset.version1.board.OldBoard;
import org.rpgwizard.migrator.asset.version2.AbstractAsset;
import org.rpgwizard.migrator.asset.version2.animation.Animation;
import org.rpgwizard.migrator.asset.version2.map.Map;

/**
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
            if (desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
                desktop.browseFileDirectory(outputDir);
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
                    break;
                case "Enemies":
                    break;
                case "Fonts":
                    break;
                case "Graphics":
                    break;
                case "Items":
                    break;
                case "NPCs":
                    break;
                case "Programs":
                    break;
                case "Sounds":
                    break;
                case "TileSets":
                    break;
                default:
                    log.warn("Unknown directory type, dirName=[{}], skipping...", dirName);
            }
        }
    }
    
    private static void migrateDir(File src, File dest, Class<? extends OldAbstractAsset> oldAssetType, Class<? extends AbstractAsset> newAssetType, String ext) {
        log.info("Migrating oldAssetType=[{}] to newAssetType=[{}], src=[{}], dest=[{}], ext=[{}]", oldAssetType, newAssetType, src, dest, ext);
        var srcAssets = FileUtils.listFiles(src, new String[]{ext}, true);
        
        for (var srcAsset : srcAssets) {
            try {
                var oldAsset = readAsset(srcAsset, oldAssetType);
                var newAsset = mapAsset(oldAsset);
                if (newAsset.isPresent()) {
                    var targetPath = srcAsset.getAbsolutePath().replace(src.getAbsolutePath(), dest.getAbsolutePath());
                    targetPath = ExtensionMapper.map(targetPath);
                    writeAsset(newAsset.get(), new File(targetPath));
                }
            } catch (IOException ex) {
                log.error("Could not read asset! srcAsset=[{}], oldAssetType=[{}], ex=[{}]", srcAsset, oldAssetType, ex);
            }
        }
    }
    
    private static OldAbstractAsset readAsset(File src, Class<? extends OldAbstractAsset> oldAssetType) throws IOException {
        log.info("Reading asset, src=[{}], oldAssetType=[{}]", src, oldAssetType);
        var inputJson = Files.readString(Paths.get(src.getAbsolutePath()));
        return new ObjectMapper().readValue(inputJson, oldAssetType);
    }
    
    private static void writeAsset(AbstractAsset newAsset, File dest) throws IOException {
        log.info("Writing asset, newAsset.class=[{}], dest=[{}]", newAsset.getClass(), dest);
        Files.createDirectories(Paths.get(dest.getParentFile().getAbsolutePath()));
        Files.createFile(Paths.get(dest.getAbsolutePath()));
        new ObjectMapper().writeValue(dest, newAsset);
    }
    
    private static Optional<AbstractAsset> mapAsset(OldAbstractAsset oldAsset) {
        if (oldAsset instanceof OldAnimation) {
            var mapper = Mappers.getMapper(OldAnimationToAnimationMapper.class);
            return Optional.of(mapper.map((OldAnimation) oldAsset));
        } else if (oldAsset instanceof OldBoard) {
            var mapper = Mappers.getMapper(OldBoardToMapMapper.class);
            return Optional.of(mapper.map((OldBoard) oldAsset));
        }
        
        return Optional.empty();
    }
    
}
