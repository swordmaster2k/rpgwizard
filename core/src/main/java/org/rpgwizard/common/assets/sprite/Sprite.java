/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.sprite;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.common.assets.Trigger;
import org.rpgwizard.common.assets.events.SpriteChangedEvent;
import org.rpgwizard.common.assets.listeners.SpriteChangeListener;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
public class Sprite extends AbstractAsset {

    private String name;
    private String description;
    private Map<String, String> animations;
    private Collider collider;
    private Trigger trigger;
    private Map<String, String> data;

    @JsonIgnore
    protected LinkedList<SpriteChangeListener> spriteChangeListeners = new LinkedList<>();

    public Sprite() {
        animations = new HashMap<>();
        
        collider = new Collider();
        collider.addPoint(0, 0);
        collider.addPoint(0, 20);
        collider.addPoint(20, 20);
        collider.addPoint(20, 0);
        collider.addPoint(0, 0);
        collider.setX(-10);
        collider.setY(-10);
        
        trigger = new Trigger();
        trigger.addPoint(0, 0);
        trigger.addPoint(0, 30);
        trigger.addPoint(30, 30);
        trigger.addPoint(30, 0);
        trigger.addPoint(0, 0);
        trigger.setX(-15);
        trigger.setY(-15);
        
        data = new HashMap<>();
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
     * Add a new <code>SpriteChangeListener</code> for this sprite.
     *
     * @param listener
     *            new change listener
     */
    public void addSpriteChangeListener(SpriteChangeListener listener) {
        spriteChangeListeners.add(listener);
    }

    /**
     * Remove an existing <code>PlayerChangeListener</code> for this player.
     *
     * @param listener
     *            change listener
     */
    public void removeSpriteChangeListener(SpriteChangeListener listener) {
        spriteChangeListeners.remove(listener);
    }

    /**
     * Fires the <code>SpriteChangedEvent</code> informs all the listeners that this sprite has changed.
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
     * Fires the <code>SpriteChangedEvent</code> informs all the listeners that this sprite has had an animation added.
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
     * Fires the <code>SpriteChangedEvent</code> informs all the listeners that this sprite has had an animation
     * updated.
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
     * Fires the <code>SpriteChangedEvent</code> informs all the listeners that this sprite has had an animation
     * removed.
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
