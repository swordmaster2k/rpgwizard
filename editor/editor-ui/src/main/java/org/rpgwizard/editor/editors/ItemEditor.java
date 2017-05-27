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
import org.rpgwizard.common.assets.GraphicEnum;
import org.rpgwizard.common.assets.Item;
import org.rpgwizard.common.assets.listeners.SpriteChangeListener;
import org.rpgwizard.editor.editors.sprite.AbstractSpriteEditor;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 *
 * @author Joshua Michael Daly
 */
public class ItemEditor extends AbstractSpriteEditor
		implements
			InternalFrameListener,
			SpriteChangeListener {

	private final Item item;

	private JTextField itemName;
	private JTextField itemDescription;

	public ItemEditor(Item item) {
		super("Untitled", item, Icons.getIcon("item"));

		this.item = item;
		this.item.addSpriteChangeListener(this);

		if (this.item.getDescriptor() == null) {
			setupNewItem();
		} else {
			setTitle(new File(item.getDescriptor().getURI()).getName());
		}

		constructWindow();
		setVisible(true);
		pack();
	}

	@Override
	public AbstractAsset getAsset() {
		return item;
	}

	public Item getItem() {
		return item;
	}

	@Override
	public void save() throws Exception {
		// Get the relative portrait path.
		checkProfileImagePath();

		item.setName(itemName.getText());
		item.setDescription(itemDescription.getText());

		save(item);
	}

	@Override
	public void saveAs(File file) throws Exception {
		item.setDescriptor(new AssetDescriptor((file.toURI())));
		setTitle(file.getName());
		save();
	}

	private void setupNewItem() {
		String undefined = "Undefined";
		item.setDescription(undefined);
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

    itemName = new JTextField(item.getName());
    itemName.setColumns(DEFAULT_INPUT_COLUMNS);

    itemDescription = new JTextField(item.getDescription());
    itemDescription.setColumns(DEFAULT_INPUT_COLUMNS);

    List<Component> inputs = new ArrayList<>();
    inputs.add(itemName);
    inputs.add(itemDescription);

    profileImagePath = item.getGraphics().get(GraphicEnum.PROFILE.toString());

    buildStatsPanel(labels, inputs);
  }
	private void createAnimationsPanel() {
		buildAnimationsPanel();
	}

}