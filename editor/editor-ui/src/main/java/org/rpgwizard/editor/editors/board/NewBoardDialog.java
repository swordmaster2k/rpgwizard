/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board;

import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Joshua Michael Daly
 */
public final class NewBoardDialog extends JDialog {

	private final JSpinner widthSpinner;
	private final JSpinner heightSpinner;
	private final JSpinner tileWidthSpinner;
	private final JSpinner tileHeightSpinner;
	private final JLabel dimensionsLabel;

	private final JButton okButton;
	private final JButton cancelButton;

	private int[] value = null;

	/**
	 *
	 * @param owner
	 */
	public NewBoardDialog(Window owner) {
        super(owner, "New Board",
                JDialog.ModalityType.APPLICATION_MODAL);

        widthSpinner = new JSpinner(new SpinnerNumberModel(10, 3, 50, 1));
        ((JSpinner.DefaultEditor) widthSpinner.getEditor()).getTextField()
                .setColumns(7);
        widthSpinner.addChangeListener((ChangeEvent e) -> {
            updateDimensionsLabel();
        });

        heightSpinner = new JSpinner(new SpinnerNumberModel(10, 3, 50, 1));
        ((JSpinner.DefaultEditor) heightSpinner.getEditor()).getTextField()
                .setColumns(7);
        heightSpinner.addChangeListener((ChangeEvent e) -> {
            updateDimensionsLabel();
        });

        tileWidthSpinner = new JSpinner(new SpinnerNumberModel(32, 3, 50, 1));
        ((JSpinner.DefaultEditor) tileWidthSpinner.getEditor()).getTextField()
                .setColumns(7);
        tileWidthSpinner.addChangeListener((ChangeEvent e) -> {
            updateDimensionsLabel();
        });

        tileHeightSpinner = new JSpinner(new SpinnerNumberModel(32, 3, 50, 1));
        ((JSpinner.DefaultEditor) tileHeightSpinner.getEditor()).getTextField()
                .setColumns(7);
        tileHeightSpinner.addChangeListener((ChangeEvent e) -> {
            updateDimensionsLabel();
        });
        
        dimensionsLabel = new JLabel("320x320px", SwingConstants.RIGHT);

        okButton = new JButton("OK");
        okButton.addActionListener((ActionEvent e) -> {
            value = new int[]{(int) widthSpinner.getValue(),
                (int) heightSpinner.getValue(),
                (int) tileWidthSpinner.getValue(),
                (int) tileHeightSpinner.getValue()};
            dispose();
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((ActionEvent e) -> {
            dispose();
        });

        GridLayout gridLayout = new GridLayout(0, 2);
        gridLayout.setVgap(5);

        JPanel gridPanel = new JPanel(gridLayout);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridPanel.add(new JLabel("Width", SwingConstants.LEFT));
        gridPanel.add(widthSpinner);
        gridPanel.add(new JLabel("Height", SwingConstants.LEFT));
        gridPanel.add(heightSpinner);
        gridPanel.add(new JLabel("Tile Width", SwingConstants.LEFT));
        gridPanel.add(tileWidthSpinner);
        gridPanel.add(new JLabel("Tile Height", SwingConstants.LEFT));
        gridPanel.add(tileHeightSpinner);
        gridPanel.add(new JLabel("Dimensions", SwingConstants.LEFT));
        gridPanel.add(dimensionsLabel);
        gridPanel.add(okButton);
        gridPanel.add(cancelButton);

        add(gridPanel);

        setResizable(false);
        pack();
    }
	/**
	 *
	 * @return
	 */
	public int[] getValue() {
		return value;
	}

	private void updateDimensionsLabel() {
		int width = (int) widthSpinner.getValue()
				* (int) tileWidthSpinner.getValue();
		int height = (int) heightSpinner.getValue()
				* (int) tileHeightSpinner.getValue();

		String text = width + "x" + height + "px";
		dimensionsLabel.setText(text);
	}

}
