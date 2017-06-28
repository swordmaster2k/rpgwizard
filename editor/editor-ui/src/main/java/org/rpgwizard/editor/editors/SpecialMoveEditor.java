/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.text.ParseException;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.Animation;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.Program;
import org.rpgwizard.common.assets.SpecialMove;
import org.rpgwizard.common.assets.StatusEffect;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.ui.AssetEditorWindow;
import org.rpgwizard.editor.utilities.GuiHelper;
import org.rpgwizard.editor.ui.IntegerField;
import org.rpgwizard.editor.ui.resources.Icons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SpecialMove editor
 *
 * @author Joel Moore
 */
public class SpecialMoveEditor extends AssetEditorWindow
		implements
			InternalFrameListener {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SpecialMoveEditor.class);

	private final SpecialMove move; // SpecialMove file we are altering

	// Tabs required by the menu
	private JPanel specialMovePanel;

	private final Border defaultEtchedBorder = BorderFactory
			.createEtchedBorder(EtchedBorder.LOWERED);

	// BASIC SETTINGS
	private JTextField moveName;
	private JTextField description;
	private IntegerField mpCost;
	private IntegerField fightPower;
	private IntegerField mpRemovedTarget;
	private JTextField statusEffect;
	private JTextField animation;
	private JTextField program;
	private JCheckBox battleDriven;
	private JCheckBox boardDriven;

	/*
	 * *************************************************************************
	 * Public Constructors
	 * *************************************************************************
	 */
	/**
	 * Create a new blank SpecialMove
	 */
	public SpecialMoveEditor() {
		super("New Special Move", true, true, true, true, Icons
				.getIcon("new-specialmove"));

		this.move = new SpecialMove(null);

		this.setSize(555, 530);
		this.constructWindow();
		this.setVisible(true);
	}

	/**
	 * Opens an existing move
	 *
	 * @param theMove
	 *            SpecialMove to edit
	 */
	public SpecialMoveEditor(SpecialMove theMove) {
		super("Editing Special Move - " + theMove.toString(), true, true, true,
				true, Icons.getIcon("new-specialmove"));

		this.move = theMove;

		this.setSize(555, 530);
		this.constructWindow();
		this.setVisible(true);
	}

	/*
	 * *************************************************************************
	 * Public Methods
	 * *************************************************************************
	 */
	@Override
	public AbstractAsset getAsset() {
		return move;
	}

	@Override
	public void save() throws Exception {
		try {
			mpCost.commitEdit();
			fightPower.commitEdit();
			mpRemovedTarget.commitEdit();
		} catch (ParseException ex) {
			LOGGER.error("Failed to parse fields during save.", ex);
		}

		URI programUri = URI.create(program.getText());
		URI statusEffectUri = URI.create(statusEffect.getText());
		URI animationUri = URI.create(animation.getText());

		this.move.setName(moveName.getText());
		this.move.setDescription(description.getText());
		this.move.setMovePowerCost(mpCost.getValue());
		this.move.setFightPower(fightPower.getValue());
		this.move.setProgram(new AssetDescriptor(programUri));
		this.move.setMovePowerDrainedFromTarget(mpRemovedTarget.getValue());
		this.move.setStatusEffect(new AssetDescriptor(statusEffectUri));
		this.move.setAnimation(new AssetDescriptor(animationUri));
		this.move.isUsableInBattle(battleDriven.isSelected());
		this.move.isUsableInMenu(boardDriven.isSelected());

		save(move);
	}

	/**
	 * 
	 * 
	 * @param file
	 */
	@Override
	public void saveAs(File file) throws Exception {
		move.setDescriptor(new AssetDescriptor(file.toURI()));
		this.setTitle("Editing Special Move - " + file.getName());
		save();
	}

	public void gracefulClose() {

	}

	public void setWindowParent(MainWindow parent) {

	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {

	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {

	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		this.gracefulClose();
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

	/*
	 * *************************************************************************
	 * Private Methods
	 * *************************************************************************
	 */
	/**
	 * Builds the Swing interface
	 */
	private void constructWindow() {
		this.addInternalFrameListener(this);

		// Builds the components needed to display the SpecialMove status.
		this.specialMovePanel = new JPanel();

		this.createSpecialMovePanel();

		this.add(specialMovePanel);
	}

	private void createSpecialMovePanel() {
		// Configure Class scope components
		this.moveName = new JTextField(this.move.getName());
		this.description = new JTextField(this.move.getDescription());
		this.mpCost = new IntegerField(this.move.getMovePowerCost());
		this.fightPower = new IntegerField(this.move.getFightPower());
		this.mpRemovedTarget = new IntegerField(
				this.move.getMovePowerDrainedFromTarget());

		String statusEffectUri = this.move.getStatusEffect().getURI()
				.toString();
		String animationUri = this.move.getAnimation().getURI().toString();
		String programUri = this.move.getProgram().getURI().toString();
		this.statusEffect = new JTextField(statusEffectUri);
		this.animation = new JTextField(animationUri);
		this.program = new JTextField(programUri);

		this.battleDriven = new JCheckBox(
				"Battle-Driven (can be used during battle)");
		this.battleDriven.setSelected(this.move.isUsableInBattle());
		this.boardDriven = new JCheckBox(
				"Board-Driven (can be used outside of battle)");
		this.boardDriven.setSelected(this.move.isUsableInMenu());

		// Configure function scope components
		JLabel moveNameLabel = new JLabel("Name");
		JLabel descriptionLabel = new JLabel("Description");
		JLabel mpCostLabel = new JLabel("Special Move Power Consumption");
		JLabel fightPowerLabel = new JLabel("Fighting Power");
		JLabel mpRemovedTargetLabel = new JLabel(
				"Amount of SMP Removed from Target");
		final JLabel statusEffectLabel = new JLabel("Status Effect");
		final JButton statusEffectButton = new JButton("Browse");
		final JLabel animationLabel = new JLabel("Animation");
		final JButton animationButton = new JButton("Browse");
		final JLabel programLabel = new JLabel(
				"Program to run when the move is used");
		final JButton programButton = new JButton("Browse");

		// Configure listeners
		// browse status effect button
		statusEffectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String loc = EditorFileManager
						.browseByTypeRelative(StatusEffect.class);
				if (loc != null) {
					statusEffect.setText(loc);
				}
			}
		});

		// browse animation button
		animationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String loc = EditorFileManager
						.browseByTypeRelative(Animation.class);
				if (loc != null) {
					animation.setText(loc);
				}
			}
		});

		// browse program button
		programButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String loc = EditorFileManager
						.browseByTypeRelative(Program.class);
				if (loc != null) {
					program.setText(loc);
				}
			}
		});

		// Configure the necessary Panels
		JPanel editorPanel = new JPanel();
		editorPanel.setBorder(BorderFactory.createTitledBorder(
				this.defaultEtchedBorder, "Special Move Editor"));

		// Create Layout for top level panel
		GroupLayout layout = GuiHelper.createGroupLayout(this.specialMovePanel);

		// Create Layouts for second level panels
		GroupLayout editorLayout = GuiHelper.createGroupLayout(editorPanel);

		// Configure the BASIC INFO PANEL layout
		editorLayout
				.setHorizontalGroup(editorLayout
						.createParallelGroup()
						.addGroup(
								editorLayout.createSequentialGroup()
										.addComponent(moveNameLabel)
										.addComponent(this.moveName))
						.addGroup(
								editorLayout.createSequentialGroup()
										.addComponent(descriptionLabel)
										.addComponent(this.description))
						.addGroup(
								editorLayout.createSequentialGroup()
										.addComponent(mpCostLabel)
										.addComponent(this.mpCost))
						.addGroup(
								editorLayout.createSequentialGroup()
										.addComponent(fightPowerLabel)
										.addComponent(this.fightPower))
						.addGroup(
								editorLayout.createSequentialGroup()
										.addComponent(mpRemovedTargetLabel)
										.addComponent(this.mpRemovedTarget))
						.addGroup(
								editorLayout.createSequentialGroup()
										.addComponent(statusEffectLabel)
										.addComponent(this.statusEffect)
										.addComponent(statusEffectButton))
						.addGroup(
								editorLayout.createSequentialGroup()
										.addComponent(animationLabel)
										.addComponent(this.animation)
										.addComponent(animationButton))
						.addGroup(
								editorLayout.createSequentialGroup()
										.addComponent(programLabel)
										.addComponent(this.program)
										.addComponent(programButton))
						.addComponent(this.battleDriven)
						.addComponent(this.boardDriven));

		editorLayout.linkSize(SwingConstants.HORIZONTAL, moveNameLabel,
				descriptionLabel, mpCostLabel, fightPowerLabel,
				mpRemovedTargetLabel, statusEffectLabel, animationLabel,
				programLabel);

		editorLayout
				.setVerticalGroup(editorLayout
						.createSequentialGroup()
						.addGroup(
								editorLayout
										.createParallelGroup()
										.addComponent(moveNameLabel)
										.addComponent(this.moveName,
												GuiHelper.JTF_HEIGHT,
												GuiHelper.JTF_HEIGHT,
												GuiHelper.JTF_HEIGHT))
						.addGroup(
								editorLayout.createParallelGroup()
										.addComponent(descriptionLabel)
										.addComponent(this.description))
						.addGroup(
								editorLayout.createParallelGroup()
										.addComponent(mpCostLabel)
										.addComponent(this.mpCost))
						.addGroup(
								editorLayout.createParallelGroup()
										.addComponent(fightPowerLabel)
										.addComponent(this.fightPower))
						.addGroup(
								editorLayout.createParallelGroup()
										.addComponent(mpRemovedTargetLabel)
										.addComponent(this.mpRemovedTarget))
						.addGroup(
								editorLayout.createParallelGroup()
										.addComponent(statusEffectLabel)
										.addComponent(this.statusEffect)
										.addComponent(statusEffectButton))
						.addGroup(
								editorLayout.createParallelGroup()
										.addComponent(animationLabel)
										.addComponent(this.animation)
										.addComponent(animationButton))
						.addGroup(
								editorLayout.createParallelGroup()
										.addComponent(programLabel)
										.addComponent(this.program)
										.addComponent(programButton))
						.addComponent(this.battleDriven)
						.addComponent(this.boardDriven));

		editorLayout.linkSize(SwingConstants.VERTICAL, this.moveName,
				this.description, this.mpCost, this.fightPower,
				this.mpRemovedTarget, this.statusEffect, this.animation,
				this.program);

		// Configure BASIC SETTINGS PANEL layout
		layout.setHorizontalGroup(layout.createParallelGroup().addComponent(
				editorPanel, 515, 515, 515));

		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(
				editorPanel));
	}

}
