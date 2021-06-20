/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rpgwizard.common.assets.listeners;

import org.rpgwizard.common.assets.events.MapModelEvent;

/**
 * Implementors of this interface will use the contained method definitions to inform their listeners of new event on a
 * <code>AbstractMapModel</code>.
 * 
 * @author Joshua Michael Daly
 */
public interface MapModelChangeListener {

    public void modelChanged(MapModelEvent e);

    public void modelMoved(MapModelEvent e);

}
