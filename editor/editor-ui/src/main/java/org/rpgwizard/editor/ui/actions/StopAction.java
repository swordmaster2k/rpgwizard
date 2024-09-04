/*
 * Copyright © 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.rpgwizard.editor.MainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public class StopAction extends AbstractAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(StopAction.class);

    private ProgressMonitor progressMonitor;

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            MainWindow instance = MainWindow.getInstance();
            instance.getMainToolBar().getStopButton().setEnabled(false);

            progressMonitor = new ProgressMonitor(MainWindow.getInstance(), "Stopping Engine...", "", 0, 100);
            progressMonitor.setProgress(0);

            SwingWorker<Integer, Integer> worker = new SwingWorker<>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    // REFACTOR: Stop engine
                    return null;
                }

                @Override
                public void done() {
                    instance.getMainToolBar().getRunButton().setEnabled(true);
                    instance.getMainToolBar().getDebugButton().setEnabled(true);
                }
            };

            worker.execute();
        } catch (Exception ex) {
            LOGGER.error("Failed to stop engine.", ex);
        }
    }

}
