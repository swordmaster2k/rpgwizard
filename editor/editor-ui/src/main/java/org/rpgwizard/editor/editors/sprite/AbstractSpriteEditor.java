/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.sprite;

import org.rpgwizard.editor.ui.ImagePanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.rpgwizard.common.assets.Animation;
import org.rpgwizard.common.assets.AbstractSprite;
import org.rpgwizard.common.assets.GraphicEnum;
import org.rpgwizard.common.assets.events.SpriteChangedEvent;
import org.rpgwizard.common.assets.listeners.SpriteChangeListener;
import org.rpgwizard.editor.editors.sprite.listener.AddAnimationActionListener;
import org.rpgwizard.editor.editors.sprite.listener.AnimationListSelectionListener;
import org.rpgwizard.editor.editors.sprite.listener.BrowseAnimationActionListener;
import org.rpgwizard.editor.editors.sprite.listener.RemoveAnimationActionListener;
import org.rpgwizard.editor.ui.AnimatedPanel;
import org.rpgwizard.editor.ui.DoubleField;
import org.rpgwizard.editor.utilities.GuiHelper;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 *
 * @author Joshua Michael Daly
 */
public abstract class AbstractSpriteEditor extends AbstractAssetEditorWindow
        implements InternalFrameListener, SpriteChangeListener {

    public static final int DEFAULT_INPUT_COLUMNS = 12;

    // Model for the animation tab.
    private final AbstractSprite sprite;

    protected static final List<String> STANDARD_PLACE_HOLDERS = Arrays.asList("SOUTH", "NORTH", "EAST", "WEST",
            "NORTH_WEST", "NORTH_EAST", "SOUTH_WEST", "SOUTH_EAST", "ATTACK", "DEFEND", "SPECIAL_MOVE", "DIE", "REST");

    protected static final List<String> STANDING_PLACE_HOLDERS = Arrays.asList("SOUTH_IDLE", "NORTH_IDLE", "EAST_IDLE",
            "WEST_IDLE", "NORTH_WEST_IDLE", "NORTH_EAST_IDLE", "SOUTH_WEST_IDLE", "SOUTH_EAST_IDLE");

    // Tabs.
    protected final JTabbedPane tabbedPane;

    // Stats Panel.
    protected final JPanel statsPanel;
    protected final ImagePanel profilePanel;
    protected final JPanel statsEditPanel;
    protected String profileImagePath;

    // Animations Panel.
    protected final JPanel animationsPanel;
    protected AnimatedPanel animatedPanel;
    protected JTable animationsTable;
    protected AnimationsTableModel animationsTableModel;
    protected Animation selectedAnim;
    protected DoubleField idleTimeoutField;
    protected DoubleField stepRateField;

    protected final Border defaultEtchedBorder;

    private JButton browseButton;
    private JButton addButton;
    private JButton removeButton;

    public AbstractSpriteEditor(String title, AbstractSprite model, ImageIcon icon) {
        super(title, true, true, true, true, icon);
        this.sprite = model;
        tabbedPane = new JTabbedPane();

        statsPanel = new JPanel();
        animationsPanel = new JPanel();

        profilePanel = new ImagePanel();
        statsEditPanel = new JPanel();

        defaultEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    }

    public AbstractSprite getSprite() {
        return sprite;
    }

    public ImagePanel getProfilePanel() {
        return profilePanel;
    }

    public JPanel getAnimationsPanel() {
        return animationsPanel;
    }

    public JButton getBrowseButton() {
        return browseButton;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getRemoveButton() {
        return removeButton;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public AnimatedPanel getAnimatedPanel() {
        return animatedPanel;
    }

    public void setAnimatedPanel(AnimatedPanel animatedPanel) {
        this.animatedPanel = animatedPanel;
    }

    public JTable getAnimationsTable() {
        return animationsTable;
    }

    public void setAnimationsTable(JTable animationsTable) {
        this.animationsTable = animationsTable;
    }

    public AnimationsTableModel getAnimationsTableModel() {
        return animationsTableModel;
    }

    public void setAnimationsTableModel(AnimationsTableModel animationsTableModel) {
        this.animationsTableModel = animationsTableModel;
    }

    public Animation getSelectedAnim() {
        return selectedAnim;
    }

    public void setSelectedAnim(Animation selectedAnim) {
        this.selectedAnim = selectedAnim;
    }

    public DoubleField getIdleTimeoutField() {
        return idleTimeoutField;
    }

    public void setIdleTimeoutField(DoubleField idleTimeoutField) {
        this.idleTimeoutField = idleTimeoutField;
    }

    public DoubleField getStepRateField() {
        return stepRateField;
    }

    public void setStepRateField(DoubleField stepRateField) {
        this.stepRateField = stepRateField;
    }

    @Override
    public void internalFrameOpened(InternalFrameEvent e) {

    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {

    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
        sprite.removeSpriteChangeListener(this);
        sprite.removeSpriteChangeListener(animationsTableModel);
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent e) {

    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent e) {

    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e) {

    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent e) {

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
            animatedPanel.setBaseVector(sprite.getBaseVector());
            animatedPanel.setActivationVector(sprite.getActivationVector());
            animatedPanel.setBaseVectorOffset(sprite.getBaseVectorOffset());
            animatedPanel.setActivationVectorOffset(sprite.getActivationVectorOffset());
        }

        try {
            animatedPanel.setAnimation(selectedAnim);
        } catch (IOException ex) {
            Logger.getLogger(AbstractSpriteEditor.class.getName()).log(Level.SEVERE, null, ex);
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

    protected void build() {
        tabbedPane.addTab("Stats", statsPanel);
        tabbedPane.addTab("Animations", animationsPanel);

        JScrollPane scrollPane = new JScrollPane(tabbedPane);
        add(scrollPane);
    }

    protected void buildStatsPanel(List<Component> labels, List<Component> inputs) {
        // Configure the necessary Panels
        statsEditPanel.setBorder(BorderFactory.createTitledBorder(defaultEtchedBorder, "Starting Stats"));

        // Create Layout for top level panel
        GroupLayout layout = GuiHelper.createGroupLayout(statsPanel);

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

        JPanel configPanel = new JPanel(new BorderLayout());
        configPanel.add(statsEditPanel, BorderLayout.NORTH);

        if (!profileImagePath.isEmpty()) {
            profilePanel.addImage(new File(EditorFileManager.getGraphicsPath() + profileImagePath));
        }

        // Configure STATS PANEL layout
        layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(profilePanel).addComponent(configPanel));

        layout.linkSize(SwingConstants.VERTICAL, profilePanel, configPanel);

        layout.setVerticalGroup(
                layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(profilePanel).addComponent(configPanel)));
    }

    protected void buildAnimationsPanel() {
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

        browseButton.addActionListener(new BrowseAnimationActionListener(this));
        addButton.addActionListener(new AddAnimationActionListener(this));
        removeButton.addActionListener(new RemoveAnimationActionListener(this));

        // Configure the necessary Panels
        JPanel configurationPanel = new JPanel();
        configurationPanel.add(addButton);
        configurationPanel.add(browseButton);
        configurationPanel.add(removeButton);

        configurationPanel.add(new JLabel("Idle Timeout: "));
        idleTimeoutField = new DoubleField(sprite.getIdleTimeBeforeStanding());
        configurationPanel.add(idleTimeoutField);

        configurationPanel.add(new JLabel("Step Rate: "));
        stepRateField = new DoubleField(sprite.getFrameRate());
        configurationPanel.add(stepRateField);

        // Fix the size of this panel to stop the JTable growing beyond the Window.
        AnimationsTablePanel southPanel = new AnimationsTablePanel(profilePanel);
        southPanel.add(animationScrollPane, BorderLayout.CENTER);
        southPanel.add(configurationPanel, BorderLayout.SOUTH);

        // Create Layout for Top Level Panel
        GroupLayout layout = GuiHelper.createGroupLayout(animationsPanel);

        // Configure the GRAPHICS PANEL layout
        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(animatedPanel).addComponent(southPanel));

        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(animatedPanel).addComponent(southPanel));
    }

    protected void checkProfileImagePath() {
        if (profilePanel.getFile() != null) {
            String remove = EditorFileManager.getGraphicsPath();
            String path = profilePanel.getFile().getAbsolutePath().replace(remove, "");
            sprite.getGraphics().put(GraphicEnum.PROFILE.toString(), path);
        }
    }

}
