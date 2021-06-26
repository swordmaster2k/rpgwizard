/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rpgwizard.common.assets.serialization;

import java.io.File;
import java.io.IOException;
import org.rpgwizard.common.assets.AbstractAssetSerializer;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.Script;
import org.rpgwizard.common.io.Paths;
import org.rpgwizard.common.utilities.CoreProperties;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Joshua Michael Daly
 */
public class ScriptSerializer extends AbstractAssetSerializer {

    @Override
    public boolean serializable(AssetDescriptor descriptor) {
        final String ext = Paths.extension(descriptor.getUri());
        return (ext.endsWith(CoreProperties.getFullExtension("rpgwizard.script.extension.default")));
    }

    @Override
    public boolean deserializable(AssetDescriptor descriptor) {
        return serializable(descriptor);
    }

    @Override
    public void serialize(AssetHandle handle) throws IOException, AssetException {
        final Script program = (Script) handle.getAsset();
        FileUtils.writeStringToFile(program.getFile(), program.getStringBuffer().toString());
    }

    @Override
    public void deserialize(AssetHandle handle) throws IOException, AssetException {
        final Script program = new Script(handle.getDescriptor());
        File path = new File(handle.getDescriptor().getUri().getPath());
        program.update(FileUtils.readFileToString(path, "UTF-8"));

        handle.setAsset(program);
    }

}
