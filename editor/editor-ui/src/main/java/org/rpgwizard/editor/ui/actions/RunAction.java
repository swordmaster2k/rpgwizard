/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public class RunAction extends AbstractRunAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunAction.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            toggleButtons();
            File projectCopy = copyProject();
            startEngine(projectCopy);
        } catch (IOException ex) {
            LOGGER.error("Failed to run engine.", ex);
        }
    }

}
