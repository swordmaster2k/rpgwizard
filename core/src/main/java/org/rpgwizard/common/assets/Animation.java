/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.rpgwizard.common.assets.events.AnimationChangedEvent;
import org.rpgwizard.common.assets.listeners.AnimationChangeListener;

/**
 * This class is responsible for reading and writing RPG Toolkit 3.1 compatible
 * Animation files.
 *
 * @author geoff wilson
 * @author Joshua Michael Daly
 */
public class Animation extends AbstractAsset {

    private final ConcurrentLinkedQueue<AnimationChangeListener> animationChangeListeners = new ConcurrentLinkedQueue<>();

    private int animationWidth;
    private int animationHeight;
    private int frameRate;
    private SpriteSheet spriteSheet;
    private String soundEffect;

    public Animation(AssetDescriptor descriptor) {
        super(descriptor);
        init();
    }

    public BufferedImage getFrame(int index) {
        return spriteSheet.getFrame(index, animationWidth, animationHeight);
    }

    public int getFrameCount() {
        return spriteSheet.getFrameCount(animationWidth, animationHeight);
    }

    /**
     * Gets the height (Y value) of the animation, this is necessary for both
     * the editor and the graphics uk.co.tkce.engine.
     *
     * @return Height value of the animation,
     */
    public int getAnimationHeight() {
        return animationHeight;
    }

    /**
     * Changes the height of the animation, it will attempt to preserve the
     * existing data
     *
     * @param newHeight New height value for the animation.
     */
    public void setAnimationHeight(int newHeight) {
        animationHeight = newHeight;
        fireAnimationChanged();
    }

    /**
     * Gets the width (X value) of the animation, this is necessary for both the
     * editor and the graphics uk.co.tkce.engine.
     *
     * @return Width value of the animation,
     */
    public int getAnimationWidth() {
        return animationWidth;
    }

    /**
     * Changes the width of the animation, it will attempt to preserve the
     * existing data
     *
     * @param newWidth New width value for the animation.
     */
    public void setAnimationWidth(int newWidth) {
        animationWidth = newWidth;
        fireAnimationChanged();
    }

    /**
     *
     * @return
     */
    public String getSoundEffect() {
        return soundEffect;
    }

    /**
     *
     * @param soundEffect
     */
    public void setSoundEffect(String soundEffect) {
        this.soundEffect = soundEffect;
        fireAnimationChanged();
    }

    /**
     * Gets the Frame Delay (seconds between each frame) of the animation, this
     * is required for the graphics uk.co.tkce.engine to correctly configure
     * animation timers.
     *
     * @return Frame delay value for the animation
     */
    public int getFrameRate() {
        return frameRate;
    }

    public void setFramRate(int rate) {
        frameRate = rate;
        fireAnimationChanged();
    }

    public SpriteSheet getSpriteSheet() {
        return spriteSheet;
    }

    public void setSpriteSheet(SpriteSheet spriteSheet) {
        this.spriteSheet = spriteSheet;
        fireAnimationChanged();
    }

    public void removeSpriteSheet() {
        spriteSheet = null;
        fireAnimationChanged();
    }

    /**
     * Add a new <code>AnimationChangeListener</code> for this board.
     *
     * @param listener new change listener
     */
    public void addAnimationChangeListener(AnimationChangeListener listener) {
        animationChangeListeners.add(listener);
    }

    /**
     * Remove an existing <code>AnimationChangeListener</code> for this
     * animation.
     *
     * @param listener change listener
     */
    public void removeAnimationChangeListener(AnimationChangeListener listener) {
        animationChangeListeners.remove(listener);
    }

    /**
     * Fires the <code>AnimationChangedEvent</code> informs all the listeners
     * that this animation has changed.
     */
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

    /**
     * Fires the <code>AnimationChangedEvent</code> informs all the listeners
     * that this animation has changed.
     */
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

    /**
     * Fires the <code>AnimationChangedEvent</code> informs all the listeners
     * that this animation has changed.
     */
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

    private void init() {
        animationWidth = 50;
        animationHeight = 50;
        frameRate = 2;
        spriteSheet = new SpriteSheet();
        soundEffect = "";
    }

}
