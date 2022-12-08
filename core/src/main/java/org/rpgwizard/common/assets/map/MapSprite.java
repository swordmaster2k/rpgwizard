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
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.rpgwizard.common.Selectable;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.Event;
import org.rpgwizard.common.assets.Location;
import org.rpgwizard.common.assets.animation.Animation;
import org.rpgwizard.common.assets.animation.AnimationEnum;
import org.rpgwizard.common.assets.sprite.Sprite;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.common.utilities.CoreUtil;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
public class MapSprite extends AbstractMapModel implements Selectable {

    private String asset;
    private String thread;
    private Location startLocation;
    private Event event;

    @JsonIgnore
    private boolean selected; // TODO: This is editor specific, move it!
    @JsonIgnore
    private BufferedImage southImage;

    public MapSprite() {
        startLocation = new Location();
        event = new Event();
    }

    /**
     * Copy constructor.
     *
     * @param mapSprite
     */
    public MapSprite(MapSprite mapSprite) {
        asset = mapSprite.asset;
        thread = mapSprite.thread;
        startLocation = new Location(mapSprite.startLocation);
        event = new Event(mapSprite.event);
        southImage = CoreUtil.copy(mapSprite.southImage);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Getters & Setters
    ////////////////////////////////////////////////////////////////////////////

    @JsonIgnore
    public void setScript(String script) {
        getEvent().setScript(script);
    }

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

    public void prepareSprite() {
        // TODO: This is should not be in here!
        BufferedImage image = null;
        if (!asset.isEmpty()) {
            File file;

            file = new File(System.getProperty("project.path") + File.separator
                    + CoreProperties.getProperty("rpgwizard.directory.sprites") + File.separator + asset);

            AssetHandle handle;
            try {
                handle = AssetManager.getInstance().deserialize(new AssetDescriptor(file.toURI()));

                Sprite sprite = (Sprite) handle.getAsset();

                String southAnimation = sprite.getAnimations().get(AnimationEnum.SOUTH.toString());
                if (!southAnimation.isEmpty()) {
                    file = new File(System.getProperty("project.path") + File.separator
                            + CoreProperties.getProperty("rpgwizard.directory.animations"), southAnimation);

                    handle = AssetManager.getInstance().deserialize(new AssetDescriptor(file.toURI()));
                    Animation animation = (Animation) handle.getAsset();

                    if (animation != null) {
                        if (!animation.getSpriteSheet().getImage().isEmpty()) {
                            animation.getSpriteSheet().loadSelection();
                            image = animation.getFrame(0);
                        }
                    }
                }
            } catch (IOException | AssetException ex) {
                Logger.getLogger(MapSprite.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        southImage = image;
    }

    public void updateLocation(Location startLocation) {
        this.startLocation = startLocation;
        fireModelMoved();
    }

}
