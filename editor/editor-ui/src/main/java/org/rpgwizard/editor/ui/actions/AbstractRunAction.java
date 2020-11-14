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
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import org.apache.commons.io.FileUtils;
import org.pf4j.PluginManager;
import org.rpgwizard.common.assets.Game;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.pluginsystem.Engine;

/**
 *
 * @author Joshua Michael Daly
 */
public abstract class AbstractRunAction extends AbstractAction {

    private ProgressMonitor progressMonitor;
    private SwingWorker worker;

    protected void toggleButtons() {
        MainWindow instance = MainWindow.getInstance();
        instance.getMainToolBar().getRunButton().setEnabled(false);
        instance.getMainToolBar().getDebugButton().setEnabled(false);
        instance.getMainToolBar().getSaveAllButton().doClick();
    }

    protected File copyProject() throws IOException {
        // Make a temporary copy of the user's project for the engine to
        // use.
        File projectOriginal = new File(System.getProperty("project.path"));
        File projectCopy = Files.createTempDir();
        FileUtils.copyDirectory(projectOriginal, projectCopy);
        return projectCopy;
    }

    protected void startEngine(File projectCopy) {
        MainWindow instance = MainWindow.getInstance();
        Game project = instance.getActiveProject();
        int projectWidth = project.getViewport().getWidth();
        int projectHeight = project.getViewport().getHeight();
        boolean isFullScreen = project.getViewport().isFullScreen();

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
                    runEngine(engines.get(0), project, dimensions, isFullScreen, projectCopy, progressMonitor);
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
    }

    private void runEngine(Engine engine, Game project, Dimension dimensions, boolean isFullScreen, File projectCopy,
            ProgressMonitor progressMonitor) throws InterruptedException, InvocationTargetException, Exception {
        // Get the project icon if available.
        File projectIcon = null; // REFACTOR: Move to game.ico way
        engine.run(project.getName(), dimensions.width, dimensions.height, isFullScreen, projectCopy, progressMonitor,
                projectIcon);
    }

}
