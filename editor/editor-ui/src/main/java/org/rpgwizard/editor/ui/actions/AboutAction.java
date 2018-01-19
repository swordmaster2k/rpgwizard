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
