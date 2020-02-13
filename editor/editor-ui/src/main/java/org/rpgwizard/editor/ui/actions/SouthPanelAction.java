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
import javax.swing.JPanel;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.properties.user.UserPreference;
import org.rpgwizard.editor.properties.user.UserPreferencesProperties;

/**
 *
 * @author Joshua Michael Daly
 */
public class SouthPanelAction extends AbstractAction {

    /**
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        MainWindow mainWindow = MainWindow.getInstance();
        JPanel panel = mainWindow.getSouthPanel();
        if (panel.isVisible()) {
            UserPreferencesProperties.setProperty(UserPreference.SOUTH_PANEL_ACTIVE, "false");
        } else {
            UserPreferencesProperties.setProperty(UserPreference.SOUTH_PANEL_ACTIVE, "true");
        }
        panel.setVisible(!panel.isVisible());
    }

}
