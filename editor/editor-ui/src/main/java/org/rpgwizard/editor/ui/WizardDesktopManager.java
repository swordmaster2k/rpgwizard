/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui;

import javax.swing.DefaultDesktopManager;
import javax.swing.JInternalFrame;

/**
 *
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public class WizardDesktopManager extends DefaultDesktopManager {

    public WizardDesktopManager() {
        super();
    }

    @Override
    public void openFrame(JInternalFrame f) {
        super.openFrame(f);
    }

    @Override
    public void closeFrame(JInternalFrame f) {
        super.closeFrame(f);
    }

    @Override
    public void activateFrame(JInternalFrame f) {
        super.activateFrame(f);
    }

    @Override
    public void deactivateFrame(JInternalFrame f) {
        super.deactivateFrame(f);
    }

    @Override
    public void maximizeFrame(JInternalFrame f) {
        super.maximizeFrame(f);
    }

    @Override
    public void minimizeFrame(JInternalFrame f) {
        super.minimizeFrame(f);
    }

}
