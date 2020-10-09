/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.rpgwizard.editor.MainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public class ReloadTilesetsAction extends AbstractAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReloadTilesetsAction.class);

    @Override
    public void actionPerformed(ActionEvent arg0) {
        LOGGER.info("Starting reload of tileset cache.");
        MainWindow.getInstance().getTileSetPanel().reloadTileSets();
        LOGGER.info("Finished reload of tileset cache.");
    }

}
