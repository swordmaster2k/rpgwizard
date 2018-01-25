/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.GraphicEnum;
import org.rpgwizard.common.assets.Character;
import org.rpgwizard.editor.editors.sprite.AbstractSpriteEditor;
import org.rpgwizard.editor.ui.IntegerField;
import org.rpgwizard.common.assets.listeners.SpriteChangeListener;
import org.rpgwizard.editor.ui.DoubleField;
import org.rpgwizard.editor.ui.resources.Icons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Character Character Editor
 *
 * @author Joel Moore
 * @author Joshua Michael Daly
 */
public final class CharacterEditor extends AbstractSpriteEditor implements SpriteChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterEditor.class);

    private final Character player;

    private JTextField name;
    private IntegerField level;
    private IntegerField maxLevel;
    private DoubleField experience;
    private DoubleField maxExperience;
    private DoubleField health;
    private DoubleField maxHealth;
    private DoubleField attack;
    private DoubleField maxAttack;
    private DoubleField defence;
    private DoubleField maxDefence;
    private DoubleField magic;
    private DoubleField maxMagic;

    public CharacterEditor(Character player) {
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
        // Get the relative portrait path.
        checkProfileImagePath();

        // Update all player variables from stats panel.
        player.setName(name.getText());
        player.setLevel(level.getValue());
        player.setMaxLevel(maxLevel.getValue());
        player.setExperience(experience.getValue());
        player.setMaxExperience(maxExperience.getValue());
        player.setHealth(health.getValue());
        player.setMaxHealth(maxHealth.getValue());
        player.setAttack(attack.getValue());
        player.setMaxAttack(maxAttack.getValue());
        player.setDefence(defence.getValue());
        player.setMaxDefence(maxDefence.getValue());
        player.setMagic(magic.getValue());
        player.setMaxMagic(maxMagic.getValue());

        // Update all player variables from graphics panel.
        player.setIdleTimeBeforeStanding(idleTimeoutField.getValue());
        player.setFrameRate(stepRateField.getValue());

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
        createAnimationsPanel();
        build();
    }

    private void createStatsPanel() {
        List<Component> labels = new ArrayList<>();
        labels.add(new JLabel("Name"));
        labels.add(new JLabel("Level"));
        labels.add(new JLabel("Max Level"));
        labels.add(new JLabel("Experience"));
        labels.add(new JLabel("Max Experience"));
        labels.add(new JLabel("Health"));
        labels.add(new JLabel("Max Health"));
        labels.add(new JLabel("Attack"));
        labels.add(new JLabel("Max Attack"));
        labels.add(new JLabel("Defence"));
        labels.add(new JLabel("Max Defence"));
        labels.add(new JLabel("Magic"));
        labels.add(new JLabel("Max Magic"));

        name = new JTextField(player.getName());
        name.setColumns(DEFAULT_INPUT_COLUMNS);

        level = new IntegerField(player.getLevel());
        level.setColumns(DEFAULT_INPUT_COLUMNS);

        maxLevel = new IntegerField(player.getMaxLevel());
        maxLevel.setColumns(DEFAULT_INPUT_COLUMNS);

        experience = new DoubleField(player.getMaxExperience());
        experience.setColumns(DEFAULT_INPUT_COLUMNS);

        maxExperience = new DoubleField(player.getMaxExperience());
        maxExperience.setColumns(DEFAULT_INPUT_COLUMNS);

        health = new DoubleField(player.getHealth());
        health.setColumns(DEFAULT_INPUT_COLUMNS);

        maxHealth = new DoubleField(player.getMaxHealth());
        maxHealth.setColumns(DEFAULT_INPUT_COLUMNS);

        attack = new DoubleField(player.getAttack());
        attack.setColumns(DEFAULT_INPUT_COLUMNS);

        maxAttack = new DoubleField(player.getMaxAttack());
        maxAttack.setColumns(DEFAULT_INPUT_COLUMNS);

        defence = new DoubleField(player.getDefence());
        defence.setColumns(DEFAULT_INPUT_COLUMNS);

        maxDefence = new DoubleField(player.getMaxDefence());
        maxDefence.setColumns(DEFAULT_INPUT_COLUMNS);

        magic = new DoubleField(player.getMagic());
        magic.setColumns(DEFAULT_INPUT_COLUMNS);

        maxMagic = new DoubleField(player.getMaxMagic());
        maxMagic.setColumns(DEFAULT_INPUT_COLUMNS);

        List<Component> inputs = new ArrayList<>();
        inputs.add(name);
        inputs.add(level);
        inputs.add(maxLevel);
        inputs.add(experience);
        inputs.add(maxExperience);
        inputs.add(health);
        inputs.add(maxHealth);
        inputs.add(attack);
        inputs.add(maxAttack);
        inputs.add(defence);
        inputs.add(maxDefence);
        inputs.add(magic);
        inputs.add(maxMagic);

        profileImagePath = player.getGraphics().get(GraphicEnum.PROFILE.toString());

        buildStatsPanel(labels, inputs);
    }

    private void createAnimationsPanel() {
        buildAnimationsPanel();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test InternalJFrame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new CharacterEditor(null));
        frame.setSize(440, 360);
        frame.setVisible(true);
    }

}
