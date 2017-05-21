/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.assets.events;

import java.util.EventObject;
import net.rpgtoolkit.common.assets.Animation;

/**
 * An <code>EventObject</code> used to contain information of a change that has happened on a animation.
 *
 * @author Joshua Michael Daly
 */
public class AnimationChangedEvent extends EventObject {

  /**
   * Creates a new event.
   * 
   * @param animation animation the event happened on
   */
  public AnimationChangedEvent(Animation animation) {
    super(animation);
  }
}
