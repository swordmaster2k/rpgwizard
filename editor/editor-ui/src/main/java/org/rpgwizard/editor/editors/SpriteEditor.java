/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.Getter;
import lombok.Setter;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.animation.Animation;
import org.rpgwizard.common.assets.events.SpriteChangedEvent;
import org.rpgwizard.common.assets.listeners.SpriteChangeListener;
import org.rpgwizard.common.assets.sprite.Sprite;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.sprite.AnimationsTableModel;
import org.rpgwizard.editor.editors.sprite.AnimationsTablePanel;
import org.rpgwizard.editor.editors.sprite.listener.AddAnimationActionListener;
import org.rpgwizard.editor.editors.sprite.listener.AnimationListSelectionListener;
import org.rpgwizard.editor.editors.sprite.listener.BrowseAnimationActionListener;
import org.rpgwizard.editor.editors.sprite.listener.RemoveAnimationActionListener;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.AnimatedPanel;
import org.rpgwizard.editor.ui.resources.Icons;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sprite Editor
 *
 * @author Joshua Michael Daly
 */
@Getter
@Setter
public final class SpriteEditor extends AbstractAssetEditorWindow implements SpriteChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpriteEditor.class);

    public static final int DEFAULT_INPUT_COLUMNS = 12;
    protected static final List<String> STANDARD_PLACE_HOLDERS = Arrays.asList("SOUTH", "NORTH", "EAST", "WEST",
            "NORTH_WEST", "NORTH_EAST", "SOUTH_WEST", "SOUTH_EAST", "ATTACK", "DEFEND", "SPECIAL_MOVE", "DIE", "REST");
    protected static final List<String> STANDING_PLACE_HOLDERS = Arrays.asList("SOUTH_IDLE", "NORTH_IDLE", "EAST_IDLE",
            "WEST_IDLE", "NORTH_WEST_IDLE", "NORTH_EAST_IDLE", "SOUTH_WEST_IDLE", "SOUTH_EAST_IDLE");
    protected static final Dimension PROFILE_DIMENSION = new Dimension(100, 100);

    private final Sprite sprite;

    private JTextField nameField;
    private JTextArea properties;

    private JSpinner originX;
    private JSpinner originY;

    // Tabs
    protected JTabbedPane tabbedPane;

    // Stats
    private final JPanel statsPanel;
    private final JPanel statsEditPanel;

    // Animations
    private AnimatedPanel animatedPanel;
    private JTable animationsTable;
    private AnimationsTableModel animationsTableModel;
    private Animation selectedAnim;

    private final Border defaultEtchedBorder;

    private JButton browseButton;
    private JButton addButton;
    private JButton removeButton;

    public SpriteEditor(Sprite sprite) {
        super("Untitled", true, true, true, true, Icons.getIcon("character"));
        this.sprite = sprite;
        tabbedPane = new JTabbedPane();

        statsPanel = new JPanel();
        statsEditPanel = new JPanel();

        defaultEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        this.sprite.addSpriteChangeListener(this);
        if (this.sprite.getDescriptor() == null) {
            setupNewSprite();
        } else {
            setTitle(new File(sprite.getDescriptor().getURI()).getName());
        }

        constructWindow();
        setVisible(true);
        pack();
    }

    @Override
    public AbstractAsset getAsset() {
        return sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void save() throws Exception {
        // Update all player variables from stats panel.
        sprite.setName(nameField.getText());

        save(sprite);
    }

    /**
     *
     *
     * @param file
     * @throws java.lang.Exception
     */
    @Override
    public void saveAs(File file) throws Exception {
        sprite.setDescriptor(new AssetDescriptor(file.toURI()));
        this.setTitle(file.getName());
        save();
    }

    private void setupNewSprite() {

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

        nameField = new JTextField(sprite.getName());
        nameField.setColumns(DEFAULT_INPUT_COLUMNS);
        nameField.getDocument().addDocumentListener(saveDocumentListener);

        properties = new JTextArea();
        properties.setColumns(DEFAULT_INPUT_COLUMNS);
        properties.setRows(20);

        List<Component> inputs = new ArrayList<>();
        inputs.add(nameField);
        inputs.add(properties);

        buildPanels(labels, inputs);
    }

    private void buildPanels(List<Component> labels, List<Component> inputs) {
        // Create Layouts for second level panels
        GroupLayout statsLayout = GuiHelper.createGroupLayout(statsEditPanel);

        GroupLayout.ParallelGroup horizontalParallelGroup = statsLayout.createParallelGroup();
        GroupLayout.SequentialGroup sequentialGroup1;
        GroupLayout.SequentialGroup sequentialGroup2;
        int length = labels.size();
        for (int i = 0; i < length; i++) {
            sequentialGroup1 = statsLayout.createSequentialGroup();
            sequentialGroup1.addComponent(labels.get(i));
            statsLayout.setHorizontalGroup(horizontalParallelGroup.addGroup(sequentialGroup1));
            
            sequentialGroup2 = statsLayout.createSequentialGroup();
            sequentialGroup2.addComponent(inputs.get(i));
            statsLayout.setHorizontalGroup(horizontalParallelGroup.addGroup(sequentialGroup2));
        }

        GroupLayout.SequentialGroup verticalSequentialGroup = statsLayout.createSequentialGroup();
        GroupLayout.ParallelGroup parallelGroup1;
        GroupLayout.ParallelGroup parallelGroup2;
        for (int i = 0; i < length; i++) {
            parallelGroup1 = statsLayout.createParallelGroup();
            parallelGroup1.addComponent(labels.get(i));
            statsLayout.setVerticalGroup(verticalSequentialGroup.addGroup(parallelGroup1));
            
            parallelGroup2 = statsLayout.createParallelGroup();
            parallelGroup2.addComponent(inputs.get(i));
            statsLayout.setVerticalGroup(verticalSequentialGroup.addGroup(parallelGroup2));
        }

        statsLayout.linkSize(SwingConstants.HORIZONTAL, labels.toArray(new Component[labels.size()]));

        statsLayout.linkSize(SwingConstants.VERTICAL, labels.toArray(new Component[inputs.size()]));

        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.add(statsEditPanel, BorderLayout.NORTH);
        westPanel.setPreferredSize(new Dimension(200, 500));
        westPanel.setBorder(BorderFactory.createTitledBorder(defaultEtchedBorder, "Configuration"));

        // DIVIDER

        // Configure Class scope components
        animationsTableModel = new AnimationsTableModel(sprite);
        sprite.addSpriteChangeListener(animationsTableModel);

        animationsTable = new JTable(animationsTableModel);
        animationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane animationScrollPane = new JScrollPane(animationsTable);

        animatedPanel = new AnimatedPanel(new Dimension(700, AnimatedPanel.DEFAULT_HEIGHT));

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
        frame.add(new SpriteEditor(new Sprite()));
        frame.setSize(1200, 600);
        frame.setVisible(true);
    }

    // REFACTOR: Sort out

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
        sprite.removeSpriteChangeListener(this);
        sprite.removeSpriteChangeListener(animationsTableModel);
    }

    @Override
    public void spriteChanged(SpriteChangedEvent e) {
        updateAnimatedPanel();
        setNeedSave(true);
    }

    @Override
    public void spriteAnimationAdded(SpriteChangedEvent e) {
        setNeedSave(true);
    }

    @Override
    public void spriteAnimationUpdated(SpriteChangedEvent e) {
        setNeedSave(true);
    }

    @Override
    public void spriteAnimationRemoved(SpriteChangedEvent e) {
        setNeedSave(true);
    }

    public void updateAnimatedPanel() {
        if (animatedPanel == null) {
            return;
        }

        if (selectedAnim != null) {
            animatedPanel.setCollider(sprite.getCollider());
            animatedPanel.setTrigger(sprite.getTrigger());
        }

        try {
            animatedPanel.setAnimation(selectedAnim);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SpriteEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openAnimation(String path) {
        if (!path.isEmpty()) {
            File file = EditorFileManager
                    .getPath(EditorFileManager.getTypeSubdirectory(Animation.class) + File.separator + path);
            if (file.exists()) {
                selectedAnim = MainWindow.getInstance().openAnimation(file);
            } else {
                selectedAnim = null;
            }

            updateAnimatedPanel();
        }
    }

}
