/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.sprite;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.Character;
import org.rpgwizard.common.assets.listeners.SpriteChangeListener;
import org.rpgwizard.editor.editors.sprite.listener.AddAnimationActionListener;
import org.rpgwizard.editor.editors.sprite.listener.AnimationListSelectionListener;
import org.rpgwizard.editor.editors.sprite.listener.BrowseAnimationActionListener;
import org.rpgwizard.editor.editors.sprite.listener.RemoveAnimationActionListener;
import org.rpgwizard.editor.ui.AnimatedPanel;
import org.rpgwizard.editor.ui.resources.Icons;
import org.rpgwizard.editor.utilities.GuiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Character Character Editor
 *
 * @author Joshua Michael Daly
 */
public final class SpriteEditor extends AbstractSpriteEditor implements SpriteChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpriteEditor.class);

    private final Character player;

    private JTextField name;
    private JTextArea properties;

    private JSpinner originX;
    private JSpinner originY;

    public SpriteEditor(Character player) {
        super("Untitled", player, Icons.getIcon("character"));

        this.player = player;
        this.player.addSpriteChangeListener(this);
        if (this.player.getDescriptor() == null) {
            setupNewPlayer();
        } else {
            setTitle(new File(player.getDescriptor().getURI()).getName());
        }

        constructWindow();
        setVisible(true);
        pack();
    }

    @Override
    public AbstractAsset getAsset() {
        return player;
    }

    public Character getPlayer() {
        return player;
    }

    @Override
    public void save() throws Exception {
        // Update all player variables from stats panel.
        player.setName(name.getText());

        save(player);
    }

    /**
     *
     *
     * @param file
     * @throws java.lang.Exception
     */
    @Override
    public void saveAs(File file) throws Exception {
        player.setDescriptor(new AssetDescriptor(file.toURI()));
        this.setTitle(file.getName());
        save();
    }

    private void setupNewPlayer() {

    }

    /**
     * Builds the Swing interface
     */
    private void constructWindow() {
        createStatsPanel();

        JScrollPane scrollPane = new JScrollPane(statsPanel);
        add(scrollPane);
    }

    private void createStatsPanel() {
        List<Component> labels = new ArrayList<>();
        labels.add(new JLabel("Name"));
        labels.add(new JLabel("Properties"));

        name = new JTextField(player.getName());
        name.setColumns(DEFAULT_INPUT_COLUMNS);
        name.getDocument().addDocumentListener(saveDocumentListener);

        properties = new JTextArea();
        properties.setColumns(DEFAULT_INPUT_COLUMNS);
        properties.setRows(20);

        List<Component> inputs = new ArrayList<>();
        inputs.add(name);
        inputs.add(properties);

        buildPanels(labels, inputs);
    }

    private void buildPanels(List<Component> labels, List<Component> inputs) {
        // Create Layouts for second level panels
        GroupLayout statsLayout = GuiHelper.createGroupLayout(statsEditPanel);

        GroupLayout.ParallelGroup horizontalParallelGroup = statsLayout.createParallelGroup();
        GroupLayout.SequentialGroup sequentialGroup;
        int length = labels.size();
        for (int i = 0; i < length; i++) {
            sequentialGroup = statsLayout.createSequentialGroup();
            sequentialGroup.addComponent(labels.get(i));
            sequentialGroup.addComponent(inputs.get(i));

            statsLayout.setHorizontalGroup(horizontalParallelGroup.addGroup(sequentialGroup));
        }

        GroupLayout.SequentialGroup verticalSequentialGroup = statsLayout.createSequentialGroup();
        GroupLayout.ParallelGroup parallelGroup;
        for (int i = 0; i < length; i++) {
            parallelGroup = statsLayout.createParallelGroup();
            parallelGroup.addComponent(labels.get(i));
            parallelGroup.addComponent(inputs.get(i));
            statsLayout.setVerticalGroup(verticalSequentialGroup.addGroup(parallelGroup));
        }

        statsLayout.linkSize(SwingConstants.HORIZONTAL, labels.toArray(new Component[labels.size()]));

        statsLayout.linkSize(SwingConstants.VERTICAL, labels.toArray(new Component[inputs.size()]));

        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.add(statsEditPanel, BorderLayout.NORTH);
        westPanel.setPreferredSize(new Dimension(250, 500));
        westPanel.setBorder(BorderFactory.createTitledBorder(defaultEtchedBorder, "Starting Stats"));

        // DIVIDER

        // Configure Class scope components
        animationsTableModel = new AnimationsTableModel(sprite);
        sprite.addSpriteChangeListener(animationsTableModel);

        animationsTable = new JTable(animationsTableModel);
        animationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane animationScrollPane = new JScrollPane(animationsTable);

        animatedPanel = new AnimatedPanel(new Dimension(0, AnimatedPanel.DEFAULT_HEIGHT));

        addButton = new JButton();
        addButton.setIcon(Icons.getSmallIcon("new"));

        browseButton = new JButton();
        browseButton.setIcon(Icons.getSmallIcon("open"));
        browseButton.setEnabled(false);

        removeButton = new JButton();
        removeButton.setIcon(Icons.getSmallIcon("delete"));
        removeButton.setEnabled(false);

        animationsTable.getSelectionModel().addListSelectionListener(new AnimationListSelectionListener(this));

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(animationsTable.getModel());

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        animationsTable.setRowSorter(sorter);

        TableModelListener tableModelListener = (TableModelEvent e) -> {
            if (e.getType() == TableModelEvent.INSERT) {
                SwingUtilities.invokeLater(() -> {
                    int viewRow = animationsTable.convertRowIndexToView(e.getFirstRow());
                    animationsTable.scrollRectToVisible(animationsTable.getCellRect(viewRow, 0, true));
                });
            }
        };

        animationsTable.getModel().addTableModelListener(tableModelListener);

        // Enable double click on table rows
        animationsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    browseButton.doClick();
                }
            }
        });

        originX = GuiHelper.getJSpinner(0);
        ((JSpinner.DefaultEditor) originX.getEditor()).getTextField().setColumns(4);
        originY = GuiHelper.getJSpinner(0);
        ((JSpinner.DefaultEditor) originY.getEditor()).getTextField().setColumns(4);

        browseButton.addActionListener(new BrowseAnimationActionListener(this));
        addButton.addActionListener(new AddAnimationActionListener(this));
        removeButton.addActionListener(new RemoveAnimationActionListener(this));

        // Configure the necessary Panels
        JPanel configurationPanel = new JPanel();

        configurationPanel.add(new JLabel("Origin: "));
        configurationPanel.add(originX);
        configurationPanel.add(originY);
        configurationPanel.add(GuiHelper.getJSeparator());
        configurationPanel.add(addButton);
        configurationPanel.add(browseButton);
        configurationPanel.add(removeButton);

        // Fix the size of this panel to stop the JTable growing beyond the Window.
        AnimationsTablePanel northPanel = new AnimationsTablePanel(500);
        northPanel.add(animationScrollPane, BorderLayout.CENTER);
        northPanel.add(configurationPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(BorderLayout.NORTH, northPanel);
        centerPanel.add(BorderLayout.CENTER, animatedPanel);

        statsPanel.setLayout(new BorderLayout());
        statsPanel.add(BorderLayout.WEST, westPanel);
        statsPanel.add(BorderLayout.CENTER, centerPanel);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test InternalJFrame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new SpriteEditor(new Character(null)));
        frame.setSize(1200, 600);
        frame.setVisible(true);
    }

}
