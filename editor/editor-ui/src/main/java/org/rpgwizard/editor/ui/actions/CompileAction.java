/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions;

import com.google.common.io.Files;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.pluginsystem.Engine;
import org.apache.commons.io.FileUtils;
import org.rpgwizard.common.assets.Game;
import org.rpgwizard.editor.utilities.FileTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pf4j.PluginManager;

/**
 *
 * @author Joshua Michael Daly
 */
public class CompileAction extends AbstractAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompileAction.class);

    private ProgressMonitor progressMonitor;
    private SwingWorker worker;

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            MainWindow instance = MainWindow.getInstance();
            instance.getMainToolBar().getCompileButton().setEnabled(false);
            instance.getMainToolBar().getSaveAllButton().doClick();

            Game project = instance.getActiveProject();

            // Make a temporary copy of the user's project for the engine to
            // use.
            File projectOriginal = new File(System.getProperty("project.path"));
            File projectCopy = Files.createTempDir();
            FileUtils.copyDirectory(projectOriginal, projectCopy);

            PluginManager pluginManager = MainWindow.getInstance().getPluginManager();
            List<Engine> engines = pluginManager.getExtensions(Engine.class);

            // Just use the first available engine for now.
            if (engines.size() > 0) {
                progressMonitor = new ProgressMonitor(MainWindow.getInstance(), "Compiling Game...", "", 0, 100);
                progressMonitor.setProgress(0);

                worker = new SwingWorker<File, File>() {
                    @Override
                    protected File doInBackground() {
                        try {
                            File executionPath = new File(FileTools.getExecutionPath(MainWindow.class));
                            return compileGame(engines.get(0), project, projectCopy, executionPath, progressMonitor);
                        } catch (InterruptedException | InvocationTargetException | URISyntaxException ex) {
                            LOGGER.error("Failed to compile game.", ex);
                            return null;
                        }
                    }

                    @Override
                    public void done() {
                        if (!isCancelled()) {
                            try {
                                File result = get();
                                if (result == null) {
                                    throw new Exception("Null result returned from compile.");
                                }
                                Desktop.getDesktop().open(result);
                            } catch (Exception ex) {
                                LOGGER.error("Failed to compile game.", ex);
                                progressMonitor.close();
                                JOptionPane.showMessageDialog(MainWindow.getInstance(), "Error compiling game!",
                                        "Error on Compile", JOptionPane.ERROR_MESSAGE);
                            }
                        }

                        Toolkit.getDefaultToolkit().beep();
                        instance.getMainToolBar().getCompileButton().setEnabled(true);
                    }
                };
                worker.execute();
            }

        } catch (IOException ex) {
            LOGGER.error("Failed to run engine.", ex);
        }
    }

    private File compileGame(Engine engine, Game project, File projectCopy, File executionPath,
            ProgressMonitor progressMonitor) throws InterruptedException, InvocationTargetException {
        try {
            File projectIcon; // REFACTOR: Move to game.ico way
            // Default to the editor icon instead.
            String path = FileTools.getExecutionPath(CompileAction.class);
            path += File.separator + "editor.ico";
            projectIcon = new File(path);
            return engine.compile(project.getName(), projectCopy, executionPath, progressMonitor, projectIcon);
        } catch (Exception ex) {
            LOGGER.error("Failed to run engine.", ex);
        }
        return null;
    }

}
