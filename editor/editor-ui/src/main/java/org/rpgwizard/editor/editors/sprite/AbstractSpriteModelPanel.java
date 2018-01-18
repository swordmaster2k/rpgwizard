/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.sprite;

import java.awt.Point;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.rpgwizard.common.assets.AbstractSprite;
import org.rpgwizard.common.assets.board.BoardVector;
import org.rpgwizard.editor.ui.AbstractModelPanel;

/**
 *
 * @author Joshua Michael Daly
 */
public abstract class AbstractSpriteModelPanel extends AbstractModelPanel {

	private final JSpinner baseVectorWidthSpinner;
	private final JLabel baseVectorWidthLabel;

	private final JSpinner baseVectorHeightSpinner;
	private final JLabel baseVectorHeightLabel;

	private final JSpinner baseVectorOffsetXSpinner;
	private final JLabel baseVectorOffsetXLabel;

	private final JSpinner baseVectorOffsetYSpinner;
	private final JLabel baseVectorOffsetYLabel;

	private final JSpinner activationVectorWidthSpinner;
	private final JLabel activationVectorWidthLabel;

	private final JSpinner activationVectorHeightSpinner;
	private final JLabel activationVectorHeightLabel;

	private final JSpinner activationVectorOffsetXSpinner;
	private final JLabel activationVectorOffsetXLabel;

	private final JSpinner activationVectorOffsetYSpinner;
	private final JLabel activationVectorOffsetYLabel;

	private final AbstractSprite sprite;

	public AbstractSpriteModelPanel(AbstractSprite model) {
		// /
		// / super
		// /
		super(model);
		// /
		// / sprite
		// /
		sprite = model;
		// /
		// / baseVectorWidthSpinner
		// /
		baseVectorWidthSpinner = getJSpinner(sprite.getBaseVector().getWidth());
		baseVectorWidthSpinner.setModel(new SpinnerNumberModel(sprite
				.getBaseVector().getWidth(), 1, 100, 1));
		baseVectorWidthSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int width = ((Double) baseVectorWidthSpinner.getValue())
						.intValue();
				int height = (int) sprite.getBaseVector().getHeight();

				// Assumes sprite base vector is rectangular in shape, will be a
				// limitiation.
				BoardVector boardVector = new BoardVector();
				boardVector.addPoint(0, 0);
				boardVector.addPoint(width, 0);
				boardVector.addPoint(width, height);
				boardVector.addPoint(0, height);
				boardVector.setClosed(true);

				sprite.setBaseVector(boardVector, true);
			}
		});
		// /
		// / baseVectorHeightSpinner
		// /
		baseVectorHeightSpinner = getJSpinner(sprite.getBaseVector()
				.getHeight());
		baseVectorHeightSpinner.setModel(new SpinnerNumberModel(sprite
				.getBaseVector().getHeight(), 1, 100, 1));
		baseVectorHeightSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int width = (int) sprite.getBaseVector().getWidth();
				int height = ((Double) baseVectorHeightSpinner.getValue())
						.intValue();

				// Assumes sprite base vector is rectangular in shape, will be a
				// limitiation.
				BoardVector boardVector = new BoardVector();
				boardVector.addPoint(0, 0);
				boardVector.addPoint(width, 0);
				boardVector.addPoint(width, height);
				boardVector.addPoint(0, height);
				boardVector.setClosed(true);

				sprite.setBaseVector(boardVector, true);
			}
		});
		// /
		// / baseVectorOffsetXSpinner
		// /
		baseVectorOffsetXSpinner = getJSpinner(sprite.getBaseVectorOffset()
				.getX());
		baseVectorOffsetXSpinner.setModel(new SpinnerNumberModel(sprite
				.getBaseVectorOffset().getX(), -1000, 1000, 1));
		baseVectorOffsetXSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int x = ((Double) baseVectorOffsetXSpinner.getValue())
						.intValue();
				int y = (int) sprite.getBaseVectorOffset().getY();

				sprite.setBaseVectorOffset(new Point(x, y), true);
			}
		});
		// /
		// / baseVectorOffsetYSpinner
		// /
		baseVectorOffsetYSpinner = getJSpinner(sprite.getBaseVectorOffset()
				.getY());
		baseVectorOffsetYSpinner.setModel(new SpinnerNumberModel(sprite
				.getBaseVectorOffset().getY(), -1000, 1000, 1));
		baseVectorOffsetYSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int x = (int) sprite.getBaseVectorOffset().getX();
				int y = ((Double) baseVectorOffsetYSpinner.getValue())
						.intValue();

				sprite.setBaseVectorOffset(new Point(x, y), true);
			}
		});
		// /
		// / activationVectorWidthSpinner
		// /
		activationVectorWidthSpinner = getJSpinner(sprite.getActivationVector()
				.getWidth());
		activationVectorWidthSpinner.setModel(new SpinnerNumberModel(sprite
				.getActivationVector().getWidth(), 1, 100, 1));
		activationVectorWidthSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int width = ((Double) activationVectorWidthSpinner.getValue())
						.intValue();
				int height = (int) sprite.getActivationVector().getHeight();

				// Assumes sprite base vector is rectangular in shape, will be a
				// limitiation.
				BoardVector boardVector = new BoardVector();
				boardVector.addPoint(0, 0);
				boardVector.addPoint(width, 0);
				boardVector.addPoint(width, height);
				boardVector.addPoint(0, height);
				boardVector.setClosed(true);

				sprite.setActivationVector(boardVector, true);
			}
		});
		// /
		// / baseVectorHeightSpinner
		// /
		activationVectorHeightSpinner = getJSpinner(sprite
				.getActivationVector().getHeight());
		activationVectorHeightSpinner.setModel(new SpinnerNumberModel(sprite
				.getActivationVector().getHeight(), 1, 100, 1));
		activationVectorHeightSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int width = (int) sprite.getActivationVector().getWidth();
				int height = ((Double) activationVectorHeightSpinner.getValue())
						.intValue();

				// Assumes sprite base vector is rectangular in shape, will be a
				// limitiation.
				BoardVector boardVector = new BoardVector();
				boardVector.addPoint(0, 0);
				boardVector.addPoint(width, 0);
				boardVector.addPoint(width, height);
				boardVector.addPoint(0, height);
				boardVector.setClosed(true);

				sprite.setActivationVector(boardVector, true);
			}
		});
		// /
		// / activationVectorOffsetXSpinner
		// /
		activationVectorOffsetXSpinner = getJSpinner(sprite
				.getActivationVectorOffset().getX());
		activationVectorOffsetXSpinner.setModel(new SpinnerNumberModel(sprite
				.getActivationVectorOffset().getX(), -1000, 1000, 1));
		activationVectorOffsetXSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int x = ((Double) activationVectorOffsetXSpinner.getValue())
						.intValue();
				int y = (int) sprite.getActivationVectorOffset().getY();

				sprite.setActivationVectorOffset(new Point(x, y), true);
			}
		});
		// /
		// / activationVectorOffsetYSpinner
		// /
		activationVectorOffsetYSpinner = getJSpinner(sprite
				.getActivationVectorOffset().getY());
		activationVectorOffsetYSpinner.setModel(new SpinnerNumberModel(sprite
				.getActivationVectorOffset().getY(), -1000, 1000, 1));
		activationVectorOffsetYSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int x = (int) sprite.getActivationVectorOffset().getX();
				int y = ((Double) activationVectorOffsetYSpinner.getValue())
						.intValue();

				sprite.setActivationVectorOffset(new Point(x, y), true);
			}
		});
		// /
		// / this
		// /
		horizontalGroup
				.addGroup(layout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(
								baseVectorWidthLabel = getJLabel("Base Vector Width"))
						.addComponent(
								baseVectorHeightLabel = getJLabel("Base Vector Height"))
						.addComponent(
								baseVectorOffsetXLabel = getJLabel("Base Vector Offset X"))
						.addComponent(
								baseVectorOffsetYLabel = getJLabel("Base Vector Offset Y"))
						.addComponent(
								activationVectorWidthLabel = getJLabel("Activation Vector Width"))
						.addComponent(
								activationVectorHeightLabel = getJLabel("Activation Vector Height"))
						.addComponent(
								activationVectorOffsetXLabel = getJLabel("Activation Vector Offset X"))
						.addComponent(
								activationVectorOffsetYLabel = getJLabel("Activation Vector Offset Y")));

		horizontalGroup.addGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(baseVectorWidthSpinner)
				.addComponent(baseVectorHeightSpinner)
				.addComponent(baseVectorOffsetXSpinner)
				.addComponent(baseVectorOffsetYSpinner)
				.addComponent(activationVectorWidthSpinner)
				.addComponent(activationVectorHeightSpinner)
				.addComponent(activationVectorOffsetXSpinner)
				.addComponent(activationVectorOffsetYSpinner));

		layout.setHorizontalGroup(horizontalGroup);

		verticalGroup.addGroup(layout
				.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(baseVectorWidthLabel)
				.addComponent(baseVectorWidthSpinner));

		verticalGroup.addGroup(layout
				.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(baseVectorHeightLabel)
				.addComponent(baseVectorHeightSpinner));

		verticalGroup.addGroup(layout
				.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(baseVectorOffsetXLabel)
				.addComponent(baseVectorOffsetXSpinner));

		verticalGroup.addGroup(layout
				.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(baseVectorOffsetYLabel)
				.addComponent(baseVectorOffsetYSpinner));

		verticalGroup.addGroup(layout
				.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(activationVectorWidthLabel)
				.addComponent(activationVectorWidthSpinner));

		verticalGroup.addGroup(layout
				.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(activationVectorHeightLabel)
				.addComponent(activationVectorHeightSpinner));

		verticalGroup.addGroup(layout
				.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(activationVectorOffsetXLabel)
				.addComponent(activationVectorOffsetXSpinner));

		verticalGroup.addGroup(layout
				.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(activationVectorOffsetYLabel)
				.addComponent(activationVectorOffsetYSpinner));

		layout.setVerticalGroup(verticalGroup);
	}

}
