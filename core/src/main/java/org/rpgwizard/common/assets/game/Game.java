/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
public class Game extends AbstractAsset {

    private String name;
    private Viewport viewport;
    private boolean debug;

    public Game(AssetDescriptor descriptor) {
        super(descriptor);

        viewport = new Viewport();
        debug = false;
    }

    public Game(AssetDescriptor descriptor, String name) {
        this(descriptor);
        this.name = name;
    }

    @Override
    public void reset() {
        viewport = new Viewport();
        debug = false;
    }

}
