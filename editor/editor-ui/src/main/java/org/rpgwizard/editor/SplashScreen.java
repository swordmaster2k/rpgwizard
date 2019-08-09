/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 * Initial splash screen, originally based on:
 * https://www.java-tips.org/how-to-implement-a-splash-screen-for-an-application.html
 * 
 * @author Joshua Michael Daly
 */
public class SplashScreen extends JWindow {

    private static final String IMAGE = "splash-screen";

    public void display() {
        JPanel content = (JPanel) getContentPane();

        ImageIcon icon = Icons.getLargeIcon(IMAGE);
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, width, height);

        content.add(new JLabel(icon), BorderLayout.CENTER);

        setVisible(true);
    }

}
