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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.InternalFrameListener;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.GraphicEnum;
import org.rpgwizard.common.assets.NPC;
import org.rpgwizard.common.assets.listeners.SpriteChangeListener;
import org.rpgwizard.editor.editors.sprite.AbstractSpriteEditor;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 *
 * @author Joshua Michael Daly
 */
public class NPCEditor extends AbstractSpriteEditor
		implements
			InternalFrameListener,
			SpriteChangeListener {

	private final NPC npc;

	private JTextField npcName;
	private JTextField npcDescription;

	public NPCEditor(NPC npc) {
		super("Untitled", npc, Icons.getIcon("npc"));

		this.npc = npc;
		this.npc.addSpriteChangeListener(this);

		if (this.npc.getDescriptor() == null) {
			setupNewNPC();
		} else {
			setTitle(new File(npc.getDescriptor().getURI()).getName());
		}

		constructWindow();
		setVisible(true);
		pack();
	}

	@Override
	public AbstractAsset getAsset() {
		return npc;
	}

	public NPC getNPC() {
		return npc;
	}

	@Override
	public void save() throws Exception {
		// Get the relative portrait path.
		checkProfileImagePath();

		npc.setName(npcName.getText());
		npc.setDescription(npcDescription.getText());

		save(npc);
	}

	@Override
	public void saveAs(File file) throws Exception {
		npc.setDescriptor(new AssetDescriptor((file.toURI())));
		setTitle(file.getName());
		save();
	}

	private void setupNewNPC() {
		String undefined = "Undefined";
		npc.setDescription(undefined);
	}

	private void constructWindow() {
		addInternalFrameListener(this);

		createStatsPanel();
		createAnimationsPanel();

		build();
	}

	private void createStatsPanel() {
        List<Component> labels = new ArrayList<>();
        labels.add(new JLabel("Name"));
        labels.add(new JLabel("Description"));

        npcName = new JTextField(npc.getName());
        npcName.setColumns(DEFAULT_INPUT_COLUMNS);

        npcDescription = new JTextField(npc.getDescription());
        npcDescription.setColumns(DEFAULT_INPUT_COLUMNS);

        List<Component> inputs = new ArrayList<>();
        inputs.add(npcName);
        inputs.add(npcDescription);

        profileImagePath = npc.getGraphics().get(GraphicEnum.PROFILE.toString());

        buildStatsPanel(labels, inputs);
    }
	private void createAnimationsPanel() {
		buildAnimationsPanel();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Test InternalJFrame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new NPCEditor(null));
		frame.setSize(440, 360);
		frame.setVisible(true);
	}

}
