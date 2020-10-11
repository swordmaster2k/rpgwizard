/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.rpgwizard.migrator.asset.version1.OldAbstractAsset;
import org.rpgwizard.migrator.asset.version2.AbstractAsset;

/**
 *
 * @author Joshua Michael Daly
 */
@Slf4j
public class AssetIO {
    
    public static OldAbstractAsset readAsset(File src, Class<? extends OldAbstractAsset> oldAssetType) throws IOException {
        log.info("Reading asset, src=[{}], oldAssetType=[{}]", src, oldAssetType);
        var inputJson = Files.readString(Paths.get(src.getAbsolutePath()));
        return new ObjectMapper().readValue(inputJson, oldAssetType);
    }
    
    public static void writeAsset(AbstractAsset newAsset, File dest) throws IOException {
        log.info("Writing asset, newAsset.class=[{}], dest=[{}]", newAsset.getClass(), dest);
        Files.createDirectories(Paths.get(dest.getParentFile().getAbsolutePath()));
        Files.createFile(Paths.get(dest.getAbsolutePath()));
        new ObjectMapper().writeValue(dest, newAsset);
    }
    
}
