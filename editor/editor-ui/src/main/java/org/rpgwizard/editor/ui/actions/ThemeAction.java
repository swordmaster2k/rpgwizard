/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.rpgwizard.editor.properties.user.UserPreference;
import org.rpgwizard.editor.properties.user.UserPreferencesProperties;
import org.rpgwizard.editor.ui.Theme;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public class ThemeAction extends AbstractAction {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ThemeAction.class);

    private final Theme theme;

    public ThemeAction(Theme theme) {
        this.theme = theme;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (theme) {
        case LIGHT:
            changeTheme(new FlatLightLaf());
            break;
        case DARK:
            changeTheme(new FlatDarkLaf());
            break;
        default:
            // Do Nothing.
        }
    }

    private void changeTheme(LookAndFeel laf) {
        try {
            if (UIManager.getLookAndFeel().getClass().equals(laf.getClass())) {
                return;
            }
            UIManager.setLookAndFeel(laf);
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
            EditorFileManager.getFileChooser().updateUI();
            UserPreferencesProperties.setProperty(UserPreference.USER_PREFERENCE_THEME, theme.toString());
        } catch (UnsupportedLookAndFeelException ex) {
            LOGGER.error("Could not change theme laf=[{}]", ex, laf);
        }
    }

}
