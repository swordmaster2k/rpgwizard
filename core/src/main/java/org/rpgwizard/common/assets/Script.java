/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

public class Script extends AbstractAsset {

    private final StringBuffer stringBuffer;

    public Script(AssetDescriptor assetDescriptor) {
        super(assetDescriptor);
        stringBuffer = new StringBuffer();
    }

    public StringBuffer getStringBuffer() {
        return stringBuffer;
    }

    public void update(String code) {
        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer.insert(0, code);
    }

}
