/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public class HelpAction extends AbstractAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelpAction.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.info("Running on os.name=[{}], trying default browser.", System.getProperty("os.name"));
        final String url = "https://rpgwiz.github.io/site/assets/docs/v1/index.html";
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            final Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException ex) {
                LOGGER.error("Could not start desktop browser!", ex);
            }
        }
    }

}
