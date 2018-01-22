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
import javax.swing.JDialog;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.ui.AboutDialog;

/**
 *
 * @author Joshua Michael Daly
 */
public class AboutAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        JDialog dialog = new AboutDialog(MainWindow.getInstance());
        dialog.setModal(true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

}
