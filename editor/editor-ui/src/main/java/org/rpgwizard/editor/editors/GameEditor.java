/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.game.Game;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.ImagePanel;
import org.rpgwizard.editor.ui.resources.Icons;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 * Game File editor
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public final class GameEditor extends AbstractAssetEditorWindow implements InternalFrameListener {

    private final Game game; // Game file we are altering

    // Tabs required by the menu
    private JPanel settingsPanel;
    private JPanel graphicsPanel;

    // Components required for saving/loading data
    private final Border defaultEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

    // PROJECT SETTINGS
    private JTextField name;
    private ImagePanel icon;

    // STARTUP SETTINGS
    private JCheckBox debugCheckBox;

    // GRAPHICS SETTINGS
    private JCheckBox fullScreen;
    private JRadioButton sixByFour;
    private JRadioButton eightBySix;
    private JRadioButton tenBySeven;
    private JRadioButton customRes;

    private JSpinner customResWidthSpinner;
    private JSpinner customResHeightSpinner;

    /**
     * Opens an existing game
     *
     * @param game
     *            Game file to open (.gam)
     */
    public GameEditor(Game game) {
        super(game.getName(), true, true, true, true, Icons.getIcon("project"));

        this.game = game;

        setSize(555, 530);
        constructWindow();
        setVisible(true);
    }

    @Override
    public AbstractAsset getAsset() {
        return game;
    }

    @Override
    public void save() throws Exception {
        save(game);
        setTitle(game.getName());
        // Update current project instance with any new settings.
        MainWindow.getInstance().setActiveProject(game);
    }

    /**
     *
     *
     * @param file
     * @throws java.lang.Exception
     */
    @Override
    public void saveAs(File file) throws Exception {
        game.setDescriptor(new AssetDescriptor(file.toURI()));
        this.setTitle("Editing Game - " + file.getName());
        save();
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {

    }

    /**
     * Builds the Swing interface
     */
    private void constructWindow() {
        addInternalFrameListener(this);
        // Builds the components needed to display the Game status.
        JTabbedPane tabPane = new JTabbedPane();

        settingsPanel = new JPanel();
        graphicsPanel = new JPanel();

        createSettingsPanel();
        createGraphicsPanel();

        tabPane.addTab("Settings", settingsPanel);
        tabPane.addTab("Graphics", graphicsPanel);

        add(tabPane);
        pack();
    }

    private void createSettingsPanel() {
        // Configure Class scope components
        name = new JTextField(game.getName());
        name.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                game.setName(name.getText());
                setNeedSave(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                game.setName(name.getText());
                setNeedSave(true);
            }
        });

        icon = new ImagePanel(new Dimension(48, 48));
        if (!"".isEmpty()) { // REFACTOR: Look for icon in game directory
            icon.addImage(new File(EditorFileManager.getGraphicsPath() + ""));
        }
        // projectIcon.addImageListener(() -> {
        // project.setProjectIcon(projectIcon.getImagePath());
        // setNeedSave(true);
        // });

        // Configure function Scope Components
        JLabel nameLabel = new JLabel("Name");
        JLabel iconLabel = new JLabel("Icon (48x48)");

        // Configure the necessary Panels
        JPanel gameInfoPanel = new JPanel();
        gameInfoPanel.setBorder(BorderFactory.createTitledBorder(this.defaultEtchedBorder, "Information"));

        GroupLayout layout = GuiHelper.createGroupLayout(settingsPanel);
        GroupLayout projectInfoLayout = GuiHelper.createGroupLayout(gameInfoPanel);

        // Configure the PROJECT INFO PANEL layout
        projectInfoLayout.setHorizontalGroup(projectInfoLayout.createParallelGroup()
                .addGroup(projectInfoLayout.createSequentialGroup().addComponent(nameLabel).addComponent(name))
                .addGroup(projectInfoLayout.createSequentialGroup().addComponent(iconLabel).addComponent(icon)));

        projectInfoLayout.linkSize(SwingConstants.HORIZONTAL, nameLabel, iconLabel);

        projectInfoLayout.setVerticalGroup(projectInfoLayout.createSequentialGroup()
                .addGroup(projectInfoLayout.createParallelGroup().addComponent(nameLabel).addComponent(name,
                        GuiHelper.JTF_HEIGHT, GuiHelper.JTF_HEIGHT, GuiHelper.JTF_HEIGHT))
                .addGroup(projectInfoLayout.createParallelGroup().addComponent(iconLabel).addComponent(icon, 48, 48,
                        48)));

        debugCheckBox = new JCheckBox("Enable Debug");
        debugCheckBox.setSelected(game.isDebug());
        debugCheckBox.addActionListener((ActionEvent e) -> {
            game.setDebug(debugCheckBox.isSelected());
            setNeedSave(true);
        });

        // Configure the necessary Panels
        JPanel startupSettingsPanel = new JPanel();
        startupSettingsPanel.setBorder(BorderFactory.createTitledBorder(defaultEtchedBorder, "Startup Settings"));

        GroupLayout conditionsLayout = GuiHelper.createGroupLayout(startupSettingsPanel);

        conditionsLayout.setHorizontalGroup(conditionsLayout.createParallelGroup().addComponent(debugCheckBox));

        conditionsLayout.setVerticalGroup(conditionsLayout.createSequentialGroup().addComponent(debugCheckBox));

        // Configure PROJECT SETTINGS PANEL layout
        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(gameInfoPanel, 515, 515, 515)
                .addComponent(startupSettingsPanel, 515, 515, 515));

        layout.linkSize(SwingConstants.HORIZONTAL, gameInfoPanel, startupSettingsPanel);

        layout.setVerticalGroup(
                layout.createSequentialGroup().addComponent(gameInfoPanel).addComponent(startupSettingsPanel));
    }

    private void createGraphicsPanel() {
        fullScreen = new JCheckBox("Full Screen Mode");
        fullScreen.setSelected(game.getViewport().isFullScreen());
        fullScreen.addActionListener((ActionEvent e) -> {
            game.getViewport().setFullScreen(fullScreen.isSelected());
            setNeedSave(true);
        });

        sixByFour = new JRadioButton("640 x 480");
        sixByFour.addActionListener((e) -> {
            customResWidthSpinner.setEnabled(false);
            customResHeightSpinner.setEnabled(false);
            game.getViewport().setWidth(640);
            game.getViewport().setHeight(480);
            setNeedSave(true);
        });
        eightBySix = new JRadioButton("800 x 600");
        eightBySix.addActionListener((e) -> {
            customResWidthSpinner.setEnabled(false);
            customResHeightSpinner.setEnabled(false);
            game.getViewport().setWidth(800);
            game.getViewport().setHeight(600);
            setNeedSave(true);
        });
        tenBySeven = new JRadioButton("1024 x 768");
        tenBySeven.addActionListener((e) -> {
            customResWidthSpinner.setEnabled(false);
            customResHeightSpinner.setEnabled(false);
            game.getViewport().setWidth(1024);
            game.getViewport().setHeight(768);
            setNeedSave(true);
        });
        customRes = new JRadioButton("Custom");
        customRes.addActionListener((e) -> {
            customResWidthSpinner.setEnabled(true);
            customResHeightSpinner.setEnabled(true);
            setNeedSave(true);
        });

        customResWidthSpinner = GuiHelper.getJSpinner(game.getViewport().getWidth(), 320, 3200, 1);
        customResWidthSpinner.addChangeListener((ChangeEvent e) -> {
            int value = (Integer) customResWidthSpinner.getValue();
            game.getViewport().setWidth(value);
            setNeedSave(true);
        });
        customResWidthSpinner.setEnabled(false);

        customResHeightSpinner = GuiHelper.getJSpinner(game.getViewport().getHeight(), 240, 2400, 1);
        customResHeightSpinner.addChangeListener((ChangeEvent e) -> {
            int value = (Integer) customResHeightSpinner.getValue();
            game.getViewport().setHeight(value);
            setNeedSave(true);
        });
        customResHeightSpinner.setEnabled(false);

        ButtonGroup resolutionGroup = new ButtonGroup();
        resolutionGroup.add(this.sixByFour);
        resolutionGroup.add(this.eightBySix);
        resolutionGroup.add(this.tenBySeven);
        resolutionGroup.add(this.customRes);

        int width = game.getViewport().getWidth();
        int height = game.getViewport().getHeight();
        if (width == 640 && height == 480) {
            sixByFour.setSelected(true);
        } else if (width == 800 && height == 600) {
            eightBySix.setSelected(true);
        } else if (width == 1024 && height == 768) {
            tenBySeven.setSelected(true);
        } else {
            customRes.setSelected(true);
            customResWidthSpinner.setEnabled(true);
            customResHeightSpinner.setEnabled(true);
        }

        JLabel customResWarningLabel = new JLabel("Please note that not all video cards support all resolutions");
        JLabel customResX = new JLabel("x");
        JLabel customResY = new JLabel("y");

        JPanel screenPanel = new JPanel();
        screenPanel.setBorder(BorderFactory.createTitledBorder(this.defaultEtchedBorder, "Screen"));

        JPanel resolutionPanel = new JPanel();
        resolutionPanel.setBorder(BorderFactory.createTitledBorder(this.defaultEtchedBorder, "Resolution"));
        JPanel customResolutionPanel = new JPanel();
        customResolutionPanel
                .setBorder(BorderFactory.createTitledBorder(this.defaultEtchedBorder, "Custom Resolution"));

        // Create Layout for Top Level Panel
        GroupLayout layout = GuiHelper.createGroupLayout(this.graphicsPanel);

        // Configure Layouts for Second Level Panels
        GroupLayout resolutionLayout = GuiHelper.createGroupLayout(resolutionPanel);
        GroupLayout screenLayout = GuiHelper.createGroupLayout(screenPanel);
        GroupLayout customResLayout = GuiHelper.createGroupLayout(customResolutionPanel);

        resolutionLayout.setHorizontalGroup(
                resolutionLayout.createParallelGroup().addComponent(this.sixByFour).addComponent(this.eightBySix)
                        .addComponent(this.tenBySeven).addComponent(this.customRes).addComponent(this.fullScreen));

        resolutionLayout.setVerticalGroup(
                resolutionLayout.createSequentialGroup().addComponent(this.sixByFour).addComponent(this.eightBySix)
                        .addComponent(this.tenBySeven).addComponent(this.customRes).addComponent(this.fullScreen));

        screenLayout
                .setHorizontalGroup(screenLayout
                        .createParallelGroup().addGroup(screenLayout.createSequentialGroup()
                                .addComponent(resolutionPanel, 150, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(customResolutionPanel));

        screenLayout.linkSize(SwingConstants.VERTICAL, resolutionPanel);

        screenLayout.setVerticalGroup(screenLayout.createSequentialGroup()
                .addGroup(screenLayout.createParallelGroup().addComponent(resolutionPanel))
                .addComponent(customResolutionPanel).addGap(15));

        customResLayout.setHorizontalGroup(customResLayout.createParallelGroup().addComponent(customResWarningLabel)
                .addGroup(customResLayout.createSequentialGroup().addComponent(customResX)
                        .addComponent(this.customResWidthSpinner).addComponent(customResY)
                        .addComponent(this.customResHeightSpinner)));

        customResLayout.linkSize(SwingConstants.VERTICAL, this.customResWidthSpinner, this.customResHeightSpinner);

        customResLayout.setVerticalGroup(customResLayout.createSequentialGroup().addComponent(customResWarningLabel)
                .addGroup(customResLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(customResX)
                        .addComponent(this.customResWidthSpinner, GuiHelper.JTF_HEIGHT, GuiHelper.JTF_HEIGHT,
                                GuiHelper.JTF_HEIGHT)
                        .addComponent(customResY).addComponent(this.customResHeightSpinner)));

        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(screenPanel, 515, 515, 515));

        layout.linkSize(SwingConstants.HORIZONTAL, screenPanel);

        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(screenPanel));
    }

    public static void main(String[] args) {
        GameEditor editor = new GameEditor(new Game(null));
        editor.setVisible(true);

        JFrame frame = new JFrame("Test InternalJFrame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(editor);
        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
