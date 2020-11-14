/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.File;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class AbstractAsset implements Asset {

    protected String version;

    @JsonIgnore
    protected AssetDescriptor descriptor;

    /**
     * Copy constructor.
     * 
     * @param abstractAsset
     */
    public AbstractAsset(AbstractAsset abstractAsset) {
        version = abstractAsset.version;
        descriptor = abstractAsset.descriptor;
    }

    public AbstractAsset(AssetDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public void reset() {
    }

    @Override
    @JsonIgnore
    public File getFile() {
        if (descriptor == null) {
            return null;
        }

        return new File(descriptor.getURI());
    }

    public boolean exists() {
        return descriptor != null && new File(descriptor.getURI()).exists();
    }

}
