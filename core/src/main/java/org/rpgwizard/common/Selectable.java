/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common;

/**
 * Used to indicate whether an object in the editor is selected.
 *
 * @author Joshua Michael Daly
 */
public interface Selectable {

	/**
	 * Indicates if this object is selected.
	 * 
	 * @return is this object selected
	 */
	public boolean isSelected();

	/**
	 * Sets this objects selected state.
	 * 
	 * @param state
	 *            is it selected
	 */
	public void setSelectedState(boolean state);
}
