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
import org.rpgwizard.editor.editors.ScriptEditor;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;

/**
 *
 * @author Joshua Michael Daly
 */
public class PasteAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        MainWindow instance = MainWindow.getInstance();
        JInternalFrame frame = instance.getCurrentFrame();
        if (frame instanceof AbstractAssetEditorWindow) {
            if (frame instanceof ScriptEditor) {
                ScriptEditor editor = (ScriptEditor) frame;
                editor.handle(this);
            }
        }
    }

}
