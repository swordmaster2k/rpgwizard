/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.assets.listeners;

import java.util.EventListener;
import net.rpgtoolkit.common.assets.events.SpriteChangedEvent;

/**
 * Implementors of this interface will use the contained method definitions to inform their 
 * listeners of new event on a <code>Sprite</code>.
 * 
 * @author Joshua Michael Daly
 */
public interface SpriteChangeListener extends EventListener  {
  
  /**
     * A general sprite changed event.
     * 
     * @param e
     */
    public void spriteChanged(SpriteChangedEvent e);
    
    /**
     * A new animation has been added to the sprite.
     * 
     * @param e
     */
    public void spriteAnimationAdded(SpriteChangedEvent e);
    
    /**
     * An animation has been updated.
     * 
     * @param e
     */
    public void spriteAnimationUpdated(SpriteChangedEvent e);
    
    /**
     * A animation has been removed.
     * 
     * @param e
     */
    public void spriteAnimationRemoved(SpriteChangedEvent e);

}
