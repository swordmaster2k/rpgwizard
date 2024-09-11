/*
 * Copyright © 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions;

import java.awt.Dimension;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import javax.swing.AbstractAction;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import jodd.io.StreamGobbler;
import org.apache.commons.io.FileUtils;
import org.rpgwizard.common.assets.game.Game;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.utilities.FileTools;
import org.rpgwizard.engine.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public abstract class AbstractRunAction extends AbstractAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRunAction.class);

    private ProgressMonitor progressMonitor;

    private static StreamGobbler streamGobbler;

    protected void toggleButtons() {
        MainWindow instance = MainWindow.getInstance();
        instance.getMainToolBar().getRunButton().setEnabled(false);
        instance.getMainToolBar().getDebugButton().setEnabled(false);
        instance.getMainToolBar().getSaveAllButton().doClick();
    }

    protected File copyProject() throws IOException, URISyntaxException {
        File projectOriginal = new File(System.getProperty("project.path"));
        File projectCopy = new File(FileTools.getTempDirectory(),
                projectOriginal.getName() + "-" + System.currentTimeMillis());
        FileUtils.copyDirectory(projectOriginal, new File(projectCopy, "game"));
        return projectCopy;
    }

    protected void startEngine(File projectCopy) {
        MainWindow instance = MainWindow.getInstance();
        Game project = instance.getActiveProject();
        int projectWidth = project.getViewport().getWidth();
        int projectHeight = project.getViewport().getHeight();
        boolean isFullScreen = project.getViewport().isFullScreen();

        progressMonitor = new ProgressMonitor(instance, "Starting Engine...", "", 0, 100);
        progressMonitor.setProgress(0);
        Dimension dimensions = new Dimension(projectWidth, projectHeight);
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                runEngine(project, dimensions, isFullScreen, projectCopy, progressMonitor);
                return null;
            }

            @Override
            public void done() {
                instance.getMainToolBar().getStopButton().setEnabled(true);
            }
        };
        worker.execute();
    }

    private void runEngine(Game project, Dimension dimensions, boolean isFullScreen, File projectCopy,
            ProgressMonitor progressMonitor) throws InterruptedException, InvocationTargetException, Exception {
        File projectIcon = null; // REFACTOR: Move to game.ico way

        Engine engine = new Engine();
        engine.start(new File(projectCopy.getAbsolutePath() + File.separator + "game" + '"'));

        MainWindow.getInstance().setActiveEngine(engine);
    }

}
