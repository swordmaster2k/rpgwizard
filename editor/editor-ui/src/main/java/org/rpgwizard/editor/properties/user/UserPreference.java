/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.properties.user;

import org.rpgwizard.editor.ui.Theme;

/**
 *
 * 
 * @author Joshua Michael Daly
 */
public enum UserPreference {

    //@formatter:off
    USER_PREFERENCE_THEME("editor.user.preference.theme", Theme.DARK.toString()), 
    LAST_OPEN_PROJECT("editor.user.preference.project", "The Wizard's Tower"), 
    EAST_PANEL_ACTIVE("editor.user.preference.east.panel.active", "true"), 
    SOUTH_PANEL_ACTIVE("editor.user.preference.south.panel.active", "true");
    //@formatter:on

    private final String name;
    private final String defaultValue;

    private UserPreference(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return name;
    }

}
