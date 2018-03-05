/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
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
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.assets.Character;
import org.rpgwizard.common.assets.Program;
import org.rpgwizard.common.assets.Project;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.resources.Icons;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 * Project File editor
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public final class ProjectEditor extends AbstractAssetEditorWindow implements InternalFrameListener {

    private final Project project; // Project file we are altering

    // Tabs required by the menu
    private JPanel projectSettingsPanel;
    private JPanel codePanel;
    private JPanel graphicsPanel;

    // Components required for saving/loading data
    private final Border defaultEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

    // PROJECT SETTINGS
    private JTextField projectName;

    // STARTUP SETTINGS
    private JComboBox initialBoard;
    private JComboBox initialCharacter;

    // CODE SETTINGS
    private JComboBox startupProgram;
    private JComboBox gameOverProgram;

    // GRAPHICS SETTINGS
    private JCheckBox fullScreen;
    private JRadioButton sixByFour;
    private JRadioButton eightBySix;
    private JRadioButton tenBySeven;
    private JRadioButton customRes;

    private JSpinner customResWidthSpinner;
    private JSpinner customResHeightSpinner;

    /**
     * Opens an existing project
     *
     * @param project
     *            Project file to open (.gam)
     */
    public ProjectEditor(Project project) {
        super(project.getName(), true, true, true, true, Icons.getIcon("project"));

        this.project = project;

        setSize(555, 530);
        constructWindow();
        setVisible(true);
    }

    @Override
    public AbstractAsset getAsset() {
        return project;
    }

    @Override
    public void save() throws Exception {
        save(project);
        setTitle(project.getName());
    }

    /**
     *
     *
     * @param file
     * @throws java.lang.Exception
     */
    @Override
    public void saveAs(File file) throws Exception {
        project.setDescriptor(new AssetDescriptor(file.toURI()));
        this.setTitle("Editing Project - " + file.getName());
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
        // Builds the components needed to display the Project status.
        JTabbedPane tabPane = new JTabbedPane();

        this.projectSettingsPanel = new JPanel();
        this.codePanel = new JPanel();
        this.graphicsPanel = new JPanel();

        this.createProjectSettingsPanel();
        this.createCodePanel();
        this.createGraphicsPanel();

        tabPane.addTab("Project Settings", this.projectSettingsPanel);
        tabPane.addTab("RPG Code", this.codePanel);
        tabPane.addTab("Graphics", this.graphicsPanel);

        add(tabPane);
        pack();
    }

    private void createProjectSettingsPanel() {
        // Configure Class scope components
        projectName = new JTextField(project.getName());
        projectName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                project.setName(projectName.getText());
                setNeedSave(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                project.setName(projectName.getText());
                setNeedSave(true);
            }
        });

        // Configure function Scope Components
        JLabel projectNameLabel = new JLabel("Project Name");

        // Configure the necessary Panels
        JPanel projectInfoPanel = new JPanel();
        projectInfoPanel.setBorder(BorderFactory.createTitledBorder(this.defaultEtchedBorder, "Project Information"));

        GroupLayout layout = GuiHelper.createGroupLayout(projectSettingsPanel);
        GroupLayout projectInfoLayout = GuiHelper.createGroupLayout(projectInfoPanel);

        // Configure the PROJECT INFO PANEL layout
        projectInfoLayout.setHorizontalGroup(projectInfoLayout.createSequentialGroup().addComponent(projectNameLabel)
                .addComponent(this.projectName));

        projectInfoLayout.setVerticalGroup(projectInfoLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(projectNameLabel)
                .addComponent(this.projectName, GuiHelper.JTF_HEIGHT, GuiHelper.JTF_HEIGHT, GuiHelper.JTF_HEIGHT));

        JLabel initialBoardLabel = new JLabel("Initial Board");
        String[] exts = EditorFileManager.getTypeExtensions(Board.class);
        File directory = EditorFileManager.getFullPath(Board.class);
        initialBoard = GuiHelper.getFileListJComboBox(new File[] { directory }, exts, true);
        initialBoard.setSelectedItem(project.getInitialBoard());
        initialBoard.addActionListener((ActionEvent e) -> {
            if (initialBoard.getSelectedItem() != null) {
                project.setInitialBoard((String) initialBoard.getSelectedItem());
                setNeedSave(true);
            }
        });

        JLabel initialCharLabel = new JLabel("Initial Character");
        exts = EditorFileManager.getTypeExtensions(Character.class);
        directory = EditorFileManager.getFullPath(Character.class);
        initialCharacter = GuiHelper.getFileListJComboBox(new File[] { directory }, exts, true);
        initialCharacter.setSelectedItem(project.getInitialCharacter());
        initialCharacter.addActionListener((ActionEvent e) -> {
            if (initialCharacter.getSelectedItem() != null) {
                project.setInitialCharacter((String) initialCharacter.getSelectedItem());
                setNeedSave(true);
            }
        });

        JLabel blankBoardNote = new JLabel("You may leave the initial board " + "blank if you wish");

        // Configure the necessary Panels
        JPanel conditionsPanel = new JPanel();
        conditionsPanel.setBorder(BorderFactory.createTitledBorder(defaultEtchedBorder, "Startup Settings"));

        GroupLayout conditionsLayout = GuiHelper.createGroupLayout(conditionsPanel);

        conditionsLayout.setHorizontalGroup(conditionsLayout.createParallelGroup()
                .addGroup(conditionsLayout.createSequentialGroup().addComponent(initialBoardLabel, 75, 75, 75)
                        .addComponent(initialBoard))
                .addGroup(conditionsLayout.createSequentialGroup().addComponent(initialCharLabel)
                        .addComponent(initialCharacter))
                .addComponent(blankBoardNote));

        conditionsLayout.linkSize(SwingConstants.HORIZONTAL, initialBoardLabel, initialCharLabel);
        conditionsLayout.linkSize(SwingConstants.VERTICAL, initialBoard, initialCharacter);

        conditionsLayout.setVerticalGroup(conditionsLayout.createSequentialGroup()
                .addGroup(conditionsLayout.createParallelGroup().addComponent(initialBoardLabel)
                        .addComponent(initialBoard, GuiHelper.JTF_HEIGHT, GuiHelper.JTF_HEIGHT, GuiHelper.JTF_HEIGHT))
                .addGroup(conditionsLayout.createParallelGroup().addComponent(initialCharLabel)
                        .addComponent(initialCharacter))
                .addComponent(blankBoardNote));

        // Configure PROJECT SETTINGS PANEL layout
        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(projectInfoPanel, 515, 515, 515)
                .addComponent(conditionsPanel, 515, 515, 515));

        layout.linkSize(SwingConstants.HORIZONTAL, projectInfoPanel, conditionsPanel);

        layout.setVerticalGroup(
                layout.createSequentialGroup().addComponent(projectInfoPanel).addComponent(conditionsPanel));
    }

    private void createCodePanel() {
        JLabel startupProgramLabel = new JLabel("Startup Program");
        String[] exts = EditorFileManager.getTypeExtensions(Program.class);
        File directory = EditorFileManager.getFullPath(Program.class);
        startupProgram = GuiHelper.getFileListJComboBox(new File[] { directory }, exts, true);
        startupProgram.setSelectedItem(project.getStartupProgram());
        startupProgram.addActionListener((ActionEvent e) -> {
            if (startupProgram.getSelectedItem() != null) {
                project.setStartupProgram((String) startupProgram.getSelectedItem());
                setNeedSave(true);
            }
        });

        JLabel gameOverProgramLabel = new JLabel("Game Over Program");
        gameOverProgram = GuiHelper.getFileListJComboBox(new File[] { directory }, exts, true);
        gameOverProgram.setSelectedItem(project.getGameOverProgram());
        gameOverProgram.addActionListener((ActionEvent e) -> {
            if (gameOverProgram.getSelectedItem() != null) {
                project.setGameOverProgram((String) gameOverProgram.getSelectedItem());
                setNeedSave(true);
            }
        });

        // Configure Panels
        JPanel programPanel = new JPanel();
        programPanel.setBorder(BorderFactory.createTitledBorder(this.defaultEtchedBorder, "Programs"));

        // Configure Layouts
        GroupLayout layout = GuiHelper.createGroupLayout(this.codePanel);
        GroupLayout programPanelLayout = GuiHelper.createGroupLayout(programPanel);

        programPanelLayout.setHorizontalGroup(programPanelLayout.createParallelGroup()
                .addGroup(programPanelLayout.createSequentialGroup().addComponent(startupProgramLabel)
                        .addComponent(this.startupProgram))
                .addGroup(programPanelLayout.createSequentialGroup().addComponent(gameOverProgramLabel)
                        .addComponent(this.gameOverProgram)));

        programPanelLayout.linkSize(SwingConstants.HORIZONTAL, startupProgramLabel, gameOverProgramLabel);

        programPanelLayout.linkSize(SwingConstants.VERTICAL, startupProgram, gameOverProgram);

        programPanelLayout.setVerticalGroup(programPanelLayout.createSequentialGroup()
                .addGroup(programPanelLayout.createParallelGroup().addComponent(startupProgramLabel)
                        .addComponent(this.startupProgram))
                .addGroup(programPanelLayout.createParallelGroup().addComponent(gameOverProgramLabel)
                        .addComponent(this.gameOverProgram)));

        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(programPanel, 515, 515, 515));

        layout.linkSize(SwingConstants.HORIZONTAL, programPanel);

        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(programPanel));

    }

    private void createGraphicsPanel() {
        fullScreen = new JCheckBox("Full Screen Mode?");
        fullScreen.setSelected(project.isFullScreen());
        fullScreen.addActionListener((ActionEvent e) -> {
            project.setIsFullScreen(fullScreen.isSelected());
            setNeedSave(true);
        });

        sixByFour = new JRadioButton("640 x 480");
        sixByFour.addActionListener((e) -> {
            customResWidthSpinner.setEnabled(false);
            customResHeightSpinner.setEnabled(false);
            project.setResolutionWidth(640);
            project.setResolutionHeight(480);
            setNeedSave(true);
        });
        eightBySix = new JRadioButton("800 x 600");
        eightBySix.addActionListener((e) -> {
            customResWidthSpinner.setEnabled(false);
            customResHeightSpinner.setEnabled(false);
            project.setResolutionWidth(800);
            project.setResolutionHeight(600);
            setNeedSave(true);
        });
        tenBySeven = new JRadioButton("1024 x 768");
        tenBySeven.addActionListener((e) -> {
            customResWidthSpinner.setEnabled(false);
            customResHeightSpinner.setEnabled(false);
            project.setResolutionWidth(1024);
            project.setResolutionHeight(768);
            setNeedSave(true);
        });
        customRes = new JRadioButton("Custom");
        customRes.addActionListener((e) -> {
            customResWidthSpinner.setEnabled(true);
            customResHeightSpinner.setEnabled(true);
            setNeedSave(true);
        });

        customResWidthSpinner = new JSpinner();
        customResWidthSpinner.setModel(new SpinnerNumberModel(this.project.getResolutionWidth(), 320, 3200, 1));
        customResWidthSpinner.addChangeListener((ChangeEvent e) -> {
            int value = (Integer) customResWidthSpinner.getValue();
            project.setResolutionWidth(value);
            setNeedSave(true);
        });
        customResWidthSpinner.setEnabled(false);

        customResHeightSpinner = new JSpinner();
        customResHeightSpinner.setModel(new SpinnerNumberModel(this.project.getResolutionHeight(), 240, 2400, 1));
        customResHeightSpinner.addChangeListener((ChangeEvent e) -> {
            int value = (Integer) customResHeightSpinner.getValue();
            project.setResolutionHeight(value);
            setNeedSave(true);
        });
        customResHeightSpinner.setEnabled(false);

        ButtonGroup resolutionGroup = new ButtonGroup();
        resolutionGroup.add(this.sixByFour);
        resolutionGroup.add(this.eightBySix);
        resolutionGroup.add(this.tenBySeven);
        resolutionGroup.add(this.customRes);

        int width = project.getResolutionWidth();
        int height = project.getResolutionHeight();
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

        JLabel customResWarningLabel = new JLabel("Please note that not all " + "video cards support all resolutions");
        JLabel customResX = new JLabel("x");
        JLabel customResY = new JLabel("y");
        JLabel showFPSLabel = new JLabel("Show FPS?");
        JLabel drawBoardVectorsLabel = new JLabel("Draw Board Vectors?");

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
}
