/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.awt.image.BufferedImage;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.rpgwizard.common.Selectable;
import org.rpgwizard.common.utilities.CoreUtil;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapImage implements Selectable {

    private String image;
    private int x;
    private int y;

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean selected; // TODO: This is editor specific, move it!
    @JsonIgnore
    private BufferedImage bufferedImage;

    /**
     * Copy constructor.
     *
     * @param mapImage
     */
    public MapImage(MapImage mapImage) {
        this.image = mapImage.image;
        this.x = mapImage.x;
        this.y = mapImage.y;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Model Operations
    ////////////////////////////////////////////////////////////////////////////

    public final void loadBufferedImage() {
        if (image.isEmpty()) {
            return;
        }
        try {
            bufferedImage = CoreUtil.loadBufferedImage(image);
        } catch (IOException ex) {
            bufferedImage = null;
        }
    }

    public final void loadBufferedImage(String image) {
        this.image = image;
        try {
            bufferedImage = CoreUtil.loadBufferedImage(image);
        } catch (IOException ex) {
            bufferedImage = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Selection Listeners
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Is this selected in the editor?
     *
     * @return selected state
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set the selected state of this in the editor
     *
     * @param state
     *            new state
     */
    @Override
    public void setSelectedState(boolean state) {
        selected = state;
    }

}
