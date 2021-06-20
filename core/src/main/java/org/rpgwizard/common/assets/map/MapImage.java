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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.rpgwizard.common.Selectable;
import org.rpgwizard.common.utilities.CoreUtil;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
public class MapImage extends AbstractMapModel implements Selectable {

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
        image = mapImage.image;
        x = mapImage.x;
        y = mapImage.y;
        bufferedImage = CoreUtil.copy(bufferedImage);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Getters & Setters
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
    // Model Operations
    ////////////////////////////////////////////////////////////////////////////

    public void updateLocation(int x, int y) {
        this.x = x;
        this.y = y;
        fireModelMoved();
    }

}
