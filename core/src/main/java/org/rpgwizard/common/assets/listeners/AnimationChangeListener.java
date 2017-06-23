/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.listeners;

import org.rpgwizard.common.assets.events.AnimationChangedEvent;
import java.util.EventListener;

/**
 * Implementors of this interface will use the contained method definitions to
 * inform their listeners of new event on a <code>Animation</code>.
 * 
 * @author Joshua Michael Daly
 */
public interface AnimationChangeListener extends EventListener {
	/**
	 * A general animation changed event.
	 * 
	 * @param e
	 */
	public void animationChanged(AnimationChangedEvent e);

	/**
	 * A new frame has been added to the animation.
	 * 
	 * @param e
	 */
	public void animationFrameAdded(AnimationChangedEvent e);

	/**
	 * A layer has been deleted on this animation.
	 * 
	 * @param e
	 */
	public void animationFrameRemoved(AnimationChangedEvent e);
}
