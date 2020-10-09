/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import java.util.StringTokenizer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.ui.async.ProjectWatchServiceRunnable;
import org.rpgwizard.editor.ui.panel.ProjectTreeCellRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public final class ProjectPanel extends JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectPanel.class);

    private WatchService watchService;
    private Thread watchThread;

    private DefaultTreeModel model;
    private final JTree tree;

    public ProjectPanel() {
        tree = new JTree();
        tree.setCellRenderer(new ProjectTreeCellRenderer());
        tree.setVisible(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = tree.getRowForLocation(e.getX(), e.getY());
                TreePath treePath = tree.getPathForLocation(e.getX(), e.getY());
                if (row != -1) {
                    if (e.getClickCount() == 1) {

                    } else if (e.getClickCount() == 2) {
                        Object[] nodes = treePath.getPath();
                        if (nodes.length > 0) {
                            MainWindow.getInstance().openAssetEditor(new File(nodes[nodes.length - 1].toString()));
                        }
                    }
                }
            }
        };
        tree.addMouseListener(mouseListener);

        setLayout(new BorderLayout());
        add(new JScrollPane(tree));
    }

    public void setup(File file) {
        tearDown();
        model = new DefaultTreeModel(new DefaultMutableTreeNode(file));
        tree.setShowsRootHandles(true);
        tree.setRootVisible(true);
        tree.setModel(model);
        DefaultMutableTreeNode root = loadTree();
        if (root != null) {
            tree.expandPath(new TreePath(root.getPath()));
        }
        tree.setVisible(true);
        startWatchService(file.getAbsolutePath());
    }

    public void tearDown() {
        if (watchThread != null && watchThread.isAlive()) {
            try {
                if (watchService != null) {
                    watchService.close();
                }
                while (watchThread.isAlive()) {
                    LOGGER.info("Waiting for Watch Thread to die.");
                }
            } catch (IOException ex) {
                LOGGER.error("Could not stop watcher thread!", ex);
            }
        }
    }

    private void startWatchService(String watchPath) {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            watchThread = new Thread(new ProjectWatchServiceRunnable(this, watchService, watchPath));
            watchThread.start();
        } catch (IOException ex) {
            LOGGER.error("Could not start watcher thread!", ex);
        }
    }

    public DefaultMutableTreeNode loadTree() {
        String state = getExpansionState(tree, 0);
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        model.reload();
        addFiles((File) root.getUserObject(), model, root);
        restoreExpanstionState(tree, 0, state);

        return root;
    }

    private void addFiles(File rootFile, DefaultTreeModel model, DefaultMutableTreeNode root) {
        for (File file : rootFile.listFiles()) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(file);
            model.insertNodeInto(child, root, root.getChildCount());
            if (file.isDirectory()) {
                addFiles(file, model, child);
            }
        }
    }

    // is path1 descendant of path2
    private static boolean isDescendant(TreePath path1, TreePath path2) {
        int count1 = path1.getPathCount();
        int count2 = path2.getPathCount();
        if (count1 <= count2) {
            return false;
        }
        while (count1 != count2) {
            path1 = path1.getParentPath();
            count1--;
        }
        return path1.equals(path2);
    }

    private static String getExpansionState(JTree tree, int row) {
        TreePath rowPath = tree.getPathForRow(row);
        StringBuilder builder = new StringBuilder();
        int rowCount = tree.getRowCount();
        for (int i = row; i < rowCount; i++) {
            TreePath path = tree.getPathForRow(i);
            if (i == row || isDescendant(path, rowPath)) {
                if (tree.isExpanded(path)) {
                    builder.append(",").append(String.valueOf(i - row));
                }
            } else {
                break;
            }
        }
        return builder.toString();
    }

    private static void restoreExpanstionState(JTree tree, int row, String expansionState) {
        StringTokenizer tokenizer = new StringTokenizer(expansionState, ",");
        while (tokenizer.hasMoreTokens()) {
            int token = row + Integer.parseInt(tokenizer.nextToken());
            tree.expandRow(token);
        }
    }

}
