/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.menu;

import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.rpgwizard.editor.ui.actions.AboutAction;
import org.rpgwizard.editor.ui.actions.HelpAction;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 *
 * @author Joshua Michael Daly
 */
public final class HelpMenu extends JMenu {

    private JMenuItem helpMenuItem;
    private JMenuItem aboutMenuItem;

    public HelpMenu() {
        super("Help");

        this.setMnemonic(KeyEvent.VK_H);

        this.configureHelpMenuItem();
        this.configureAboutMenuItem();

        this.add(helpMenuItem);
        this.add(aboutMenuItem);
    }

    public void configureHelpMenuItem() {
        helpMenuItem = new JMenuItem("Docs");
        helpMenuItem.setIcon(Icons.getSmallIcon("help"));
        helpMenuItem.addActionListener(new HelpAction());
        helpMenuItem.setEnabled(true);
    }

    public void configureAboutMenuItem() {
        aboutMenuItem = new JMenuItem("About"); // About Menu
        aboutMenuItem.setIcon(Icons.getSmallIcon("information"));
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        aboutMenuItem.addActionListener(new AboutAction());
        aboutMenuItem.setEnabled(true);
    }

}
