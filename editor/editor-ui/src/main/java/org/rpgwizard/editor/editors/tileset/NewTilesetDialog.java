/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rpgwizard.editor.editors.tileset;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import lombok.Getter;
import org.rpgwizard.editor.MainWindow;

/**
 *
 * @author Joshua Michael Daly
 */
public class NewTilesetDialog extends JDialog {

    private final JSpinner widthSpinner;
    private final JSpinner heightSpinner;

    private final JButton okButton;
    private final JButton cancelButton;

    @Getter
    private int[] value = null;

    /**
     *
     */
    public NewTilesetDialog() {
        super(MainWindow.getInstance(), "New Tileset", JDialog.ModalityType.APPLICATION_MODAL);

        widthSpinner = new JSpinner(new SpinnerNumberModel(32, 16, 128, 1));
        ((JSpinner.DefaultEditor) widthSpinner.getEditor()).getTextField().setColumns(7);

        heightSpinner = new JSpinner(new SpinnerNumberModel(32, 16, 128, 1));
        ((JSpinner.DefaultEditor) heightSpinner.getEditor()).getTextField().setColumns(7);

        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                value = new int[] { (int) widthSpinner.getValue(), (int) heightSpinner.getValue() };
                dispose();
            }

        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }

        });

        JPanel gridPanel = new JPanel(new GridLayout(0, 2));
        gridPanel.add(new JLabel("Tile Width", SwingConstants.CENTER));
        gridPanel.add(widthSpinner);
        gridPanel.add(new JLabel("Tile Height", SwingConstants.CENTER));
        gridPanel.add(heightSpinner);
        gridPanel.add(okButton);
        gridPanel.add(cancelButton);

        add(gridPanel);

        setResizable(false);
        pack();
    }

}
