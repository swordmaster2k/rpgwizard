/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.Utilities;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.ui.resources.Icons;
import org.rpgwizard.editor.utilities.EditorFileManager;

/**
 *
 * @author Joshua Michael Daly
 */
public final class GoToFileDialog extends JDialog {

    @Setter
    private File directory;

    private final JTextField searchField;

    private final DefaultListModel matchingFilesModel;
    private final JList matchingFilesList;

    private SwingWorker swingWorker;

    public GoToFileDialog(JFrame parent) {
        super(parent, "Go to File", true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                searchField.selectAll();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                if (swingWorker != null && !swingWorker.isCancelled()) {
                    swingWorker.cancel(true);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (swingWorker != null && !swingWorker.isCancelled()) {
                    swingWorker.cancel(true);
                }
            }
        });

        matchingFilesModel = new DefaultListModel();
        matchingFilesList = new JList(matchingFilesModel);
        matchingFilesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        matchingFilesList.setVisibleRowCount(15);
        matchingFilesList.setCellRenderer(new IconListRenderer());

        searchField = new JTextField();
        searchField.setColumns(60);
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);

        searchField.getInputMap().put(KeyStroke.getKeyStroke("UP"), "up");
        searchField.getActionMap().put("up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int idx = matchingFilesList.getSelectedIndex();
                if (idx == 0) {
                    return;
                }
                matchingFilesList.setSelectedIndex(idx - 1);
                matchingFilesList.ensureIndexIsVisible(idx - 1);
            }
        });

        searchField.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");
        searchField.getActionMap().put("down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int idx = matchingFilesList.getSelectedIndex();
                if (idx == matchingFilesModel.size() - 1) {
                    return;
                }
                matchingFilesList.setSelectedIndex(idx + 1);
                matchingFilesList.ensureIndexIsVisible(idx + 1);
            }
        });

        searchField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "enter");
        searchField.getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (matchingFilesModel.isEmpty()) {
                    return;
                }

                setVisible(false);
                MatchingFile matchingFile = (MatchingFile) matchingFilesModel.get(matchingFilesList.getSelectedIndex());
                MainWindow.getInstance().openAssetEditor(matchingFile.getFile());
                dispose();
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent arg0) {
                if (StringUtils.isNotBlank(searchField.getText())) {
                    findFiles(directory, searchField.getText());
                } else {
                    matchingFilesModel.clear();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent arg0) {
                if (StringUtils.isNotBlank(searchField.getText())) {
                    findFiles(directory, searchField.getText());
                } else {
                    matchingFilesModel.clear();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                if (StringUtils.isNotBlank(searchField.getText())) {
                    findFiles(directory, searchField.getText());
                } else {
                    matchingFilesModel.clear();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(matchingFilesList);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("File Name"));
        panel.add(searchField);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(new JLabel("Matching Files"));
        panel.add(scrollPane);

        getContentPane().add(panel, BorderLayout.CENTER);

        pack();
    }

    private void findFiles(File directory, String searchInput) {
        if (swingWorker != null && !swingWorker.isDone()) {
            swingWorker.cancel(true);
        }

        swingWorker = new SwingWorker<List<MatchingFile>, MatchingFile>() {
            @Override
            protected List<MatchingFile> doInBackground() throws Exception {
                return Files.walk(Paths.get(Utilities.toURI(directory))).filter(Files::isRegularFile).filter(p -> {
                    return FilenameUtils.getBaseName(p.toFile().getName()).toLowerCase()
                            .contains(searchInput.toLowerCase());
                }).map(p -> {
                    MatchingFile file = new MatchingFile(p);
                    publish(file);
                    return file;
                }).collect(Collectors.toCollection(ArrayList::new));
            }

            @Override
            protected void process(List<MatchingFile> chunks) {
                if (isCancelled()) {
                    return;
                }

                chunks.forEach(p -> {
                    matchingFilesModel.addElement(p);
                });
                matchingFilesList.setSelectedIndex(0);
            }
        };

        matchingFilesModel.clear();
        swingWorker.execute();
    }

    @Getter
    private class MatchingFile {

        private final File file;

        public MatchingFile(Path p) {
            file = p.toFile();
        }

        @Override
        public String toString() {
            return String.format("%s    (%s)", file.getName(),
                    FilenameUtils.separatorsToUnix(EditorFileManager.getRelativePath(file)));
        }

    }

    private class IconListRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            MatchingFile matchingFile = (MatchingFile) value;

            // Set the icon if possible.
            String extension = FilenameUtils.getExtension(matchingFile.file.getName());
            ImageIcon icon = Icons.getDefaultIcon(extension.toLowerCase());
            if (icon != null) {
                label.setIcon(icon);
            }

            return label;
        }

    }

    public static void main(String[] args) {
        File directory = new File(
                "D:\\Documents\\Software Development\\rpgwizard\\editor\\editor-ui\\target\\classes\\projects\\Sample");
        GoToFileDialog dialog = new GoToFileDialog(new JFrame());
        dialog.setDirectory(directory);
        dialog.setVisible(true);
    }

}
