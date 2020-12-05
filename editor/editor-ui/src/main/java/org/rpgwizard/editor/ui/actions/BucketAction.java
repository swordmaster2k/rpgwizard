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
import org.rpgwizard.editor.editors.MapEditor;
import org.rpgwizard.editor.editors.map.brush.BucketBrush;
import org.rpgwizard.editor.MainWindow;

/**
 *
 * @author Joshua Michael Daly
 */
public class BucketAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        MapEditor.toggleSelectedOnMapEditor();

        BucketBrush brush = new BucketBrush();
        brush.setPourTile(MainWindow.getInstance().getLastSelectedTile());
        MainWindow.getInstance().setCurrentBrush(brush);
    }

}
