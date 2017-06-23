/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import org.rpgwizard.common.assets.events.SpriteChangedEvent;
import org.rpgwizard.common.assets.listeners.SpriteChangeListener;

/**
 * A common abstract class for all sprite like assets to inherit
 * (Character, Enemy, NPC).
 *
 * @author Joshua Michael Daly
 */
public abstract class AbstractSprite extends AbstractAsset {

    // Non-IO
    protected final LinkedList<SpriteChangeListener> spriteChangeListeners = new LinkedList<>();

    protected String name;

    protected LinkedHashMap<String, String> graphics;

    // Graphics Variables
    protected LinkedHashMap<String, String> animations;
    protected ArrayList<Animation> standardGraphicsAnimations;

    protected double idleTimeBeforeStanding;
    protected double frameRate; //Seconds between each step
    protected int loopSpeed;

    protected BoardVector baseVector;
    protected BoardVector activationVector;

    protected Point baseVectorOffset;
    protected Point activationVectorOffset;

    public AbstractSprite(AssetDescriptor descriptor) {
        super(descriptor);

        name = "";
        idleTimeBeforeStanding = 3;

        // Insert the default animations.
        animations = new LinkedHashMap<>();
        for (AnimationEnum key : AnimationEnum.values()) {
            animations.put(key.toString(), "");
        }

        // Insert the default graphics.
        graphics = new LinkedHashMap<>();
        for (GraphicEnum key : GraphicEnum.values()) {
            graphics.put(key.toString(), "");
        }

        standardGraphicsAnimations = new ArrayList<>();

        name = "Undefined";

        baseVector = new BoardVector();
        baseVector.addPoint(0, 0);
        baseVector.addPoint(30, 0);
        baseVector.addPoint(30, 20);
        baseVector.addPoint(0, 20);
        baseVector.setClosed(true);

        activationVector = new BoardVector();
        activationVector.addPoint(0, 0);
        activationVector.addPoint(40, 0);
        activationVector.addPoint(40, 30);
        activationVector.addPoint(0, 30);
        activationVector.setClosed(true);

        baseVectorOffset = new Point(-15, 0);
        activationVectorOffset = new Point(-20, -5);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getGraphics() {
        return graphics;
    }

    public void setGraphics(LinkedHashMap<String, String> graphics) {
        this.graphics = graphics;
    }

    public Map<String, String> getAnimations() {
        return animations;
    }

    public void setAnimations(LinkedHashMap<String, String> animations) {
        this.animations = animations;
    }

    /**
     * @return the standardGraphicsAnimations
     */
    public ArrayList<Animation> getStandardGraphicsAnimations() {
        return standardGraphicsAnimations;
    }

    /**
     * @return the idleTimeBeforeStanding
     */
    public double getIdleTimeBeforeStanding() {
        return idleTimeBeforeStanding;
    }

    /**
     * @param idleTimeBeforeStanding the idleTimeBeforeStanding to set
     */
    public void setIdleTimeBeforeStanding(double idleTimeBeforeStanding) {
        this.idleTimeBeforeStanding = idleTimeBeforeStanding;
    }

    /**
     * @return the frameRate (seconds between each step)
     */
    public double getFrameRate() {
        return frameRate;
    }

    /**
     * @param frameRate the frameRate to set (seconds between each step)
     */
    public void setFrameRate(double frameRate) {
        this.frameRate = frameRate;
    }

    public BoardVector getBaseVector() {
        return baseVector;
    }

    /**
     * @param baseVector the baseVector to set
     * @param fire
     */
    public void setBaseVector(BoardVector baseVector, boolean fire) {
        this.baseVector = baseVector;

        if (fire) {
            fireSpriteChanged();
        }
    }

    public BoardVector getActivationVector() {
        return activationVector;
    }

    /**
     * @param activationVector the activationVector to set
     * @param fire
     */
    public void setActivationVector(BoardVector activationVector, boolean fire) {
        this.activationVector = activationVector;

        if (fire) {
            fireSpriteChanged();
        }
    }

    public Point getBaseVectorOffset() {
        return baseVectorOffset;
    }

    public void setBaseVectorOffset(Point baseVectorOffset, boolean fire) {
        this.baseVectorOffset = baseVectorOffset;

        if (fire) {
            fireSpriteChanged();
        }
    }

    public Point getActivationVectorOffset() {
        return activationVectorOffset;
    }

    public void setActivationVectorOffset(Point activationVectorOffset, boolean fire) {
        this.activationVectorOffset = activationVectorOffset;

        if (fire) {
            fireSpriteChanged();
        }
    }

    /**
     * Add a new <code>SpriteChangeListener</code> for this sprite.
     *
     * @param listener new change listener
     */
    public void addSpriteChangeListener(SpriteChangeListener listener) {
        spriteChangeListeners.add(listener);
    }

    /**
     * Remove an existing <code>PlayerChangeListener</code> for this player.
     *
     * @param listener change listener
     */
    public void removeSpriteChangeListener(SpriteChangeListener listener) {
        spriteChangeListeners.remove(listener);
    }

    public void addAnimation(String key, String value) {
        animations.put(key, value);
        fireSpriteAnimationAdded();
    }

    public void updateAnimation(String key, String value) {
        animations.put(key, value);
        fireSpriteAnimationUpdated();
    }

    public void removeAnimation(String key) {
        animations.remove(key);
        fireSpriteAnimationRemoved();
    }

    /**
     * Fires the <code>SpriteChangedEvent</code> informs all the listeners that
     * this sprite has changed.
     */
    public void fireSpriteChanged() {
        SpriteChangedEvent event = null;
        Iterator iterator = spriteChangeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new SpriteChangedEvent(this);
            }

            ((SpriteChangeListener) iterator.next()).spriteChanged(event);
        }
    }

    /**
     * Fires the <code>SpriteChangedEvent</code> informs all the listeners that
     * this sprite has had an animation added.
     */
    public void fireSpriteAnimationAdded() {
        SpriteChangedEvent event = null;
        Iterator iterator = spriteChangeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new SpriteChangedEvent(this);
            }

            ((SpriteChangeListener) iterator.next()).spriteAnimationAdded(event);
        }
    }

    /**
     * Fires the <code>SpriteChangedEvent</code> informs all the listeners that
     * this sprite has had an animation updated.
     */
    public void fireSpriteAnimationUpdated() {
        SpriteChangedEvent event = null;
        Iterator iterator = spriteChangeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new SpriteChangedEvent(this);
            }

            ((SpriteChangeListener) iterator.next()).spriteAnimationUpdated(event);
        }
    }

    /**
     * Fires the <code>SpriteChangedEvent</code> informs all the listeners that
     * this sprite has had an animation removed.
     */
    public void fireSpriteAnimationRemoved() {
        SpriteChangedEvent event = null;
        Iterator iterator = spriteChangeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new SpriteChangedEvent(this);
            }

            ((SpriteChangeListener) iterator.next()).spriteAnimationRemoved(event);
        }
    }

}
