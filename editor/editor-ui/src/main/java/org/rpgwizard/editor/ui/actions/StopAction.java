/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.pluginsystem.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.fortsoft.pf4j.PluginManager;

/**
 *
 * @author Joshua Michael Daly
 */
public class StopAction extends AbstractAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(StopAction.class);

    private ProgressMonitor progressMonitor;
    private SwingWorker worker;

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            MainWindow instance = MainWindow.getInstance();
            instance.getMainToolBar().getStopButton().setEnabled(false);

            PluginManager pluginManager = MainWindow.getInstance().getPluginManager();
            List<Engine> engines = pluginManager.getExtensions(Engine.class);

            // Just use the first available engine for now.
            if (engines.size() > 0) {
                progressMonitor = new ProgressMonitor(MainWindow.getInstance(), "Stopping Engine...", "", 0, 100);
                progressMonitor.setProgress(0);

                worker = new SwingWorker<Integer, Integer>() {
                    @Override
                    protected Integer doInBackground() throws Exception {
                        engines.get(0).stop(progressMonitor);

                        return null;
                    }

                    @Override
                    public void done() {
                        Toolkit.getDefaultToolkit().beep();
                        instance.getMainToolBar().getRunButton().setEnabled(true);
                    }
                };
                worker.execute();
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to stop engine.", ex);
        }
    }

}
