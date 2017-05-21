/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import net.rpgtoolkit.editor.MainWindow;
import net.rpgtoolkit.editor.ui.AssetEditorWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class SaveAction extends AbstractAction {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(SaveAction.class);

  @Override
  public void actionPerformed(ActionEvent e) {
    MainWindow w = MainWindow.getInstance();
    JInternalFrame frame = w.getDesktopPane().getSelectedFrame();
    
    if (frame != null) {
      if (frame instanceof AssetEditorWindow) {
        try {
          AssetEditorWindow window = (AssetEditorWindow) frame;
          window.save();
        } catch (Exception ex) {
          LOGGER.error("Failed to invoke save for asset frame=[{}]", frame, ex);
          
          JOptionPane.showMessageDialog(MainWindow.getInstance(),
                  "Error saving file!",
                  "Error on Save",
                  JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

}
