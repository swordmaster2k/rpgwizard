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
import javax.swing.JInternalFrame;
import org.rpgwizard.editor.MainWindow;

/**
 *
 * @author Joshua Michael Daly
 */
public class RedoAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        MainWindow instance = MainWindow.getInstance();
        JInternalFrame frame = instance.getCurrentFrame();
        if (frame instanceof ActionHandler) {
            final ActionHandler handler = (ActionHandler) frame;
            handler.handle(this);

            final MainWindow mainWindow = MainWindow.getInstance();
            mainWindow.enableUndo(handler.canUndo());
            mainWindow.enableRedo(handler.canRedo());
        }
    }

}
