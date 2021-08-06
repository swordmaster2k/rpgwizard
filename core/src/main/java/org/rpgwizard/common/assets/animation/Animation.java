/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.animation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.events.AnimationChangedEvent;
import org.rpgwizard.common.assets.listeners.AnimationChangeListener;

/**
 * This class is responsible for reading and writing RPG Toolkit 3.1 compatible Animation files.
 *
 * @author geoff wilson
 * @author Joshua Michael Daly
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
public class Animation extends AbstractAsset {

    private int width;
    private int height;
    private int frameRate;
    private String soundEffect;
    private SpriteSheet spriteSheet;

    @JsonIgnore
    private final ConcurrentLinkedQueue<AnimationChangeListener> animationChangeListeners = new ConcurrentLinkedQueue<>();

    public Animation(AssetDescriptor descriptor) {
        super(descriptor);
        width = 50;
        height = 50;
        frameRate = 2;
        soundEffect = "";
        spriteSheet = new SpriteSheet("", 0, 0, 50, 50, 50, 50);
    }

    @JsonIgnore
    public BufferedImage getFrame(int index) {
        return spriteSheet.getFrame(index, spriteSheet.getTileWidth(), spriteSheet.getTileHeight());
    }

    @JsonIgnore
    public int getFrameCount() {
        return spriteSheet.getFrameCount(spriteSheet.getTileWidth(), spriteSheet.getTileHeight());
    }

    public void setHeight(int newHeight) {
        height = newHeight;
        fireAnimationChanged();
    }

    public void setWidth(int newWidth) {
        width = newWidth;
        fireAnimationChanged();
    }

    public void setSoundEffect(String soundEffect) {
        this.soundEffect = soundEffect;
        fireAnimationChanged();
    }

    public void setFramRate(int rate) {
        frameRate = rate;
        fireAnimationChanged();
    }

    public void setSpriteSheet(SpriteSheet spriteSheet) {
        this.spriteSheet = spriteSheet;
        fireAnimationChanged();
    }

    public void removeSpriteSheet() {
        spriteSheet = null;
        fireAnimationChanged();
    }

    public void addAnimationChangeListener(AnimationChangeListener listener) {
        animationChangeListeners.add(listener);
    }

    public void removeAnimationChangeListener(AnimationChangeListener listener) {
        animationChangeListeners.remove(listener);
    }

    public void fireAnimationChanged() {
        AnimationChangedEvent event = null;
        Iterator iterator = animationChangeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new AnimationChangedEvent(this);
            }

            ((AnimationChangeListener) iterator.next()).animationChanged(event);
        }
    }

    public void fireAnimationFrameAdded() {
        AnimationChangedEvent event = null;
        Iterator iterator = animationChangeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new AnimationChangedEvent(this);
            }

            ((AnimationChangeListener) iterator.next()).animationFrameAdded(event);
        }
    }

    public void fireAnimationFrameRemoved() {
        AnimationChangedEvent event = null;
        Iterator iterator = animationChangeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new AnimationChangedEvent(this);
            }

            ((AnimationChangeListener) iterator.next()).animationFrameRemoved(event);
        }
    }

}
