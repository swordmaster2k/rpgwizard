/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.properties.user.UserPreference;
import org.rpgwizard.editor.properties.user.UserPreferencesProperties;

/**
 *
 * @author Joshua Michael Daly
 */
public class EastPanelAction extends AbstractAction {

    /**
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        MainWindow mainWindow = MainWindow.getInstance();
        JPanel panel = mainWindow.getEastPanel();
        JTabbedPane eastUpperTabbedPane = mainWindow.getEastUpperTabbedPane();
        JTabbedPane eastLowerTabbedPane = mainWindow.getEastLowerTabbedPane();
        JTabbedPane westUpperTabbedPane = mainWindow.getWestUpperTabbedPane();
        JTabbedPane westLowerTabbedPane = mainWindow.getWestLowerTabbedPane();
        if (panel.isVisible()) {
            moveTab(eastUpperTabbedPane, westUpperTabbedPane, 0);
            moveTab(eastLowerTabbedPane, westLowerTabbedPane, 0);
            UserPreferencesProperties.setProperty(UserPreference.EAST_PANEL_ACTIVE, "false");
        } else {
            moveTab(westUpperTabbedPane, eastUpperTabbedPane, 1);
            moveTab(westLowerTabbedPane, eastLowerTabbedPane, 1);
            UserPreferencesProperties.setProperty(UserPreference.EAST_PANEL_ACTIVE, "true");
        }
        panel.setVisible(!panel.isVisible());
    }

    private void moveTab(JTabbedPane origin, JTabbedPane target, int index) {
        if (origin.getTabCount() > index) {
            String title = origin.getTitleAt(index);
            Component component = origin.getComponentAt(index);
            origin.removeTabAt(index);
            target.addTab(title, component);
            target.setSelectedIndex(target.getTabCount() - 1);
        }
    }

}
