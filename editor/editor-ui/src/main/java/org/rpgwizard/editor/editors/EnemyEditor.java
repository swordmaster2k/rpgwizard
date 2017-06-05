/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.InternalFrameListener;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.Enemy;
import org.rpgwizard.common.assets.GraphicEnum;
import org.rpgwizard.common.assets.listeners.SpriteChangeListener;
import org.rpgwizard.editor.editors.sprite.AbstractSpriteEditor;
import org.rpgwizard.editor.ui.DoubleField;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 * Enemy editor
 *
 * @author Joel Moore
 * @author Joshua Michael Daly
 */
public class EnemyEditor extends AbstractSpriteEditor
		implements
			InternalFrameListener,
			SpriteChangeListener {

	private final Enemy enemy;

	private JTextField enemyName;
	private DoubleField health;
	private DoubleField attack;
	private DoubleField defence;
	private DoubleField magic;
	private DoubleField experienceReward;
	private DoubleField goldReward;

	/*
	 * *************************************************************************
	 * Public Constructors
	 * *************************************************************************
	 */

	/**
	 * Opens an existing enemy
	 *
	 * @param theEnemy
	 *            Enemy to edit
	 */
	public EnemyEditor(Enemy theEnemy) {
		super("Untitled", theEnemy, Icons.getIcon("enemy"));

		enemy = theEnemy;
		enemy.addSpriteChangeListener(this);

		if (enemy.getDescriptor() == null) {
			setupNewEnemy();
		} else {
			setTitle(new File(enemy.getDescriptor().getURI()).getName());
		}

		this.constructWindow();
		this.setVisible(true);
		pack();
	}

	/*
	 * *************************************************************************
	 * Public Methods
	 * *************************************************************************
	 */

	@Override
	public AbstractAsset getAsset() {
		return enemy;
	}

	public Enemy getEnemy() {
		return enemy;
	}

	@Override
	public void save() throws Exception {
		// Get the relative portrait path.
		checkProfileImagePath();

		// Update all enemy variables from the stats panel
		enemy.setName(enemyName.getText());
		enemy.setHealth(health.getValue());
		enemy.setAttack(attack.getValue());
		enemy.setDefence(defence.getValue());
		enemy.setMagic(magic.getValue());
		enemy.setExperienceReward(experienceReward.getValue());
		enemy.setGoldReward(goldReward.getValue());

		// Update all enemy variables from graphics panel.
		enemy.setIdleTimeBeforeStanding(idleTimeoutField.getValue());
		enemy.setFrameRate(stepRateField.getValue());

		save(enemy);
	}

	/**
	 *
	 *
	 * @param file
	 * @throws java.lang.Exception
	 */
	@Override
	public void saveAs(File file) throws Exception {
		enemy.setDescriptor(new AssetDescriptor(file.toURI()));
		this.setTitle(file.getName());
		save();
	}

	/*
	 * *************************************************************************
	 * Private Methods
	 * *************************************************************************
	 */
	private void setupNewEnemy() {

	}

	/**
	 * Builds the Swing interface
	 */
	private void constructWindow() {
		this.addInternalFrameListener(this);

		this.createStatsPanel();
		this.createGraphicsPanel();

		build();
	}

	private void createStatsPanel() {
    List<Component> labels = new ArrayList<>();
    labels.add(new JLabel("Name"));
    labels.add(new JLabel("Health"));
    labels.add(new JLabel("Attack"));
    labels.add(new JLabel("Defence"));
    labels.add(new JLabel("Magic"));
    labels.add(new JLabel("Experience Reward"));
    labels.add(new JLabel("Gold Reward"));
    
    // Configure Class scope components
    enemyName = new JTextField(enemy.getName());
    enemyName.setColumns(DEFAULT_INPUT_COLUMNS);
    
    health = new DoubleField(enemy.getHealth());
    health.setColumns(DEFAULT_INPUT_COLUMNS);
    
    attack = new DoubleField(enemy.getAttack());
    attack.setColumns(DEFAULT_INPUT_COLUMNS);
    
    defence = new DoubleField(enemy.getDefence());
    defence.setColumns(DEFAULT_INPUT_COLUMNS);
    
    magic = new DoubleField(enemy.getMagic());
    magic.setColumns(DEFAULT_INPUT_COLUMNS);
    
    experienceReward = new DoubleField(enemy.getExperienceReward());
    experienceReward.setColumns(DEFAULT_INPUT_COLUMNS);
    
    goldReward = new DoubleField(enemy.getGoldReward());
    goldReward.setColumns(DEFAULT_INPUT_COLUMNS);
    
    List<Component> inputs = new ArrayList<>();
    inputs.add(enemyName);
    inputs.add(health);
    inputs.add(attack);
    inputs.add(defence);
    inputs.add(magic);
    inputs.add(experienceReward);
    inputs.add(goldReward);
    
    profileImagePath = enemy.getGraphics().get(GraphicEnum.PROFILE.toString());
    
    buildStatsPanel(labels, inputs);
  }
	private void createGraphicsPanel() {
		buildAnimationsPanel();
	}

}
