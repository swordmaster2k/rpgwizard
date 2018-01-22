/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions;

import com.google.common.io.Files;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.pluginsystem.Engine;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.fortsoft.pf4j.PluginManager;

/**
 *
 * @author Joshua Michael Daly
 */
public class RunAction extends AbstractAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunAction.class);

    private ProgressMonitor progressMonitor;
    private SwingWorker worker;

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            MainWindow instance = MainWindow.getInstance();
            instance.getMainToolBar().getRunButton().setEnabled(false);
            instance.getMainToolBar().getSaveAllButton().doClick();

            String projectName = instance.getTitle();
            int projectWidth = instance.getActiveProject().getResolutionWidth();
            int projectHeight = instance.getActiveProject().getResolutionHeight();

            // Make a temporary copy of the user's project for the engine to
            // use.
            File projectOriginal = new File(System.getProperty("project.path"));
            File projectCopy = Files.createTempDir();
            FileUtils.copyDirectory(projectOriginal, projectCopy);

            PluginManager pluginManager = instance.getPluginManager();
            List<Engine> engines = pluginManager.getExtensions(Engine.class);

            // Just use the first available engine for now.
            if (engines.size() > 0) {
                progressMonitor = new ProgressMonitor(instance, "Starting Engine...", "", 0, 100);
                progressMonitor.setProgress(0);
                Dimension dimensions = new Dimension(projectWidth, projectHeight);
                worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        runEngine(engines.get(0), projectName, dimensions, projectCopy, progressMonitor);

                        return null;
                    }

                    @Override
                    public void done() {
                        Toolkit.getDefaultToolkit().beep();
                        instance.getMainToolBar().getStopButton().setEnabled(true);
                    }
                };
                worker.execute();
            }

        } catch (IOException ex) {
            LOGGER.error("Failed to run engine.", ex);
        }
    }

    private void runEngine(Engine engine, String projectName, Dimension dimensions, File projectCopy,
            ProgressMonitor progressMonitor) throws InterruptedException, InvocationTargetException, Exception {
        engine.run(projectName, dimensions.width, dimensions.height, projectCopy, progressMonitor);
    }

}
