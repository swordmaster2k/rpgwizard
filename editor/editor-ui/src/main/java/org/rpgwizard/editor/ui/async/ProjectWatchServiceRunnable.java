/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.async;

import java.awt.EventQueue;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import org.rpgwizard.editor.ui.ProjectPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public class ProjectWatchServiceRunnable implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectWatchServiceRunnable.class);

    private final ProjectPanel projectPanel;
    private final WatchService watchService;
    private final String watchPath;

    public ProjectWatchServiceRunnable(ProjectPanel pp, WatchService ws, String wp) {
        this.projectPanel = pp;
        this.watchService = ws;
        this.watchPath = wp;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Running WatchService watchPath=[{}]", watchPath);
            registerRecursive(Paths.get(watchPath));

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    LOGGER.debug("Event kind=[{}], File affected=[{}]", event.kind(), event.context());
                    EventQueue.invokeLater(() -> {
                        projectPanel.loadTree();
                    });
                }
                key.reset();
            }
        } catch (IOException | InterruptedException | ClosedWatchServiceException e) {
            LOGGER.info("Stopping WatchService watchPath=[{}]", watchPath);
        }
    }

    // Credit: https://stackoverflow.com/questions/18701242/how-to-watch-a-folder-and-subfolders-for-changes
    private void registerRecursive(final Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
                path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
