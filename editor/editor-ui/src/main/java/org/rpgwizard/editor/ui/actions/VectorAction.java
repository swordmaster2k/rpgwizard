/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
// REFACTOR: FIX ME
package org.rpgwizard.editor.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.editors.map.brush.ColliderBrush;

/**
 *
 * @author Joshua Michael Daly
 */
public class VectorAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        MapEditor.toggleSelectedOnMapEditor();

        ColliderBrush brush = new ColliderBrush();
        MainWindow.getInstance().setCurrentBrush(brush);

        if (MainWindow.getInstance().getMainMenuBar().getViewMenu().getShowVectorsMenuItem().isSelected() == false) {
            MainWindow.getInstance().getMainMenuBar().getViewMenu().getShowVectorsMenuItem().setSelected(true);
        }
    }

}
