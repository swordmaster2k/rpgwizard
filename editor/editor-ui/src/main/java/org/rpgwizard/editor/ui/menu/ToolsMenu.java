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
import org.rpgwizard.editor.ui.actions.MapToImageAction;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 *
 * @author Joshua Michael Daly
 */
public final class ToolsMenu extends JMenu {

    private JMenuItem mapImageExportItem;

    public ToolsMenu() {
        super("Tools");
        configureMapImageExportItem();

        add(mapImageExportItem);
        setMnemonic(KeyEvent.VK_T);
    }

    /**
     *
     */
    public void configureMapImageExportItem() {
        mapImageExportItem = new JMenuItem("Export Map to Image");
        mapImageExportItem.setIcon(Icons.getSmallIcon("image-export"));
        mapImageExportItem.addActionListener(new MapToImageAction());
    }

}
