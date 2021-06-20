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
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.editors.map.brush.AbstractPolygonBrush;
import org.rpgwizard.editor.editors.map.brush.TriggerAreaBrush;
import org.rpgwizard.editor.editors.map.brush.TriggerBrush;

/**
 * CLEANUP: Clean me up
 * 
 * @author Joshua Michael Daly
 */
public class TriggerAction extends AbstractAction {

    private boolean useArea;

    public TriggerAction() {
        super("Trigger");
    }

    public TriggerAction(boolean useArea) {
        this();
        this.useArea = useArea;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MapEditor.toggleSelectedOnMapEditor();

        AbstractPolygonBrush brush;
        if (useArea) {
            brush = new TriggerAreaBrush();
        } else {
            brush = new TriggerBrush();
        }
        MainWindow.getInstance().setCurrentBrush(brush);

        if (MainWindow.getInstance().getMainMenuBar().getViewMenu().getShowVectorsMenuItem().isSelected() == false) {
            MainWindow.getInstance().getMainMenuBar().getViewMenu().getShowVectorsMenuItem().setSelected(true);
        }
    }

}
