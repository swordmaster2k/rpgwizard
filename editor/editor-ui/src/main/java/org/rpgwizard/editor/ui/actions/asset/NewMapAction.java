/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions.asset;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.map.NewMapDialog;

/**
 *
 * @author Joshua Michael Daly
 */
public class NewMapAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent ae) {
        Map map = null;

        NewMapDialog dialog = new NewMapDialog(MainWindow.getInstance());
        dialog.setLocationRelativeTo(MainWindow.getInstance());
        dialog.setVisible(true);
        if (dialog.getValue() != null) {
            map = new Map(null, dialog.getValue()[0], dialog.getValue()[1], dialog.getValue()[2], dialog.getValue()[3]);
            map.init();
        }

        putValue("map", map);
    }

}
