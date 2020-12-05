/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBoxMenuItem;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.MapEditor;

/**
 *
 * @author Joshua Michael Daly
 */
public class ShowVectorsItemListener implements ItemListener {

    @Override
    public void itemStateChanged(ItemEvent e) {
        JCheckBoxMenuItem showVectorsMenuItem = (JCheckBoxMenuItem) e.getItem();
        MapEditor editor = MainWindow.getInstance().getCurrentMapEditor();
        if (editor != null) {
            MainWindow.getInstance().setShowVectors(showVectorsMenuItem.isSelected());
            editor.getMapView().repaint();
        }
    }

}
