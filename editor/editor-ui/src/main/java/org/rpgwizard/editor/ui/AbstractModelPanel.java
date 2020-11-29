/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

/**
 *
 * @author Joshua Michael Daly
 */
public abstract class AbstractModelPanel extends JPanel {

    protected static final int COLUMNS = 10;

    protected Object model;
    protected Font font;

    protected GridBagLayout layout;

    protected static final Insets DEFAULT_INSETS = new Insets(10, 10, 10, 10);
    protected final GridBagConstraints constraints;
    protected int row;

    public AbstractModelPanel(Object model) {
        this.model = model;
        font = new JLabel().getFont();
        layout = new GridBagLayout();
        constraints = new GridBagConstraints();
        row = 0;
        setLayout(layout);
    }

    public Object getModel() {
        return model;
    }

    public final JLabel getJLabel(String text) {
        JLabel label = new JLabel(text);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setFont(font);
        return label;
    }

    public final JTextField getJTextField(String text) {
        JTextField textField = new JTextField(text);
        textField.setColumns(COLUMNS);
        textField.setFont(font);
        return textField;
    }

    public final JButton getJButton(String text) {
        JButton button = new JButton(text);
        button.setFont(font);
        return button;
    }

    public final JSpinner getJSpinner(Object value) {
        JSpinner spinner = new JSpinner();
        spinner.setValue(value);
        spinner.setFont(font);
        return spinner;
    }

    public final JSpinner getJSpinner(Integer value) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, Integer.valueOf(0),
                Integer.valueOf(Integer.MAX_VALUE), Integer.valueOf(1)));
        spinner.setFont(font);
        return spinner;
    }

    public final JSpinner getJSpinner(Integer value, Integer min, Integer max) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, Integer.valueOf(1)));
        spinner.setFont(font);
        return spinner;
    }

    public final JCheckBox getJCheckBox(boolean value) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(value);
        checkBox.setFont(font);
        return checkBox;
    }

    public final JComboBox getJComboBox(Object selected, String[] items) {
        JComboBox comboBox = new JComboBox(items);
        comboBox.setSelectedItem(selected);
        comboBox.setFont(font);
        return comboBox;
    }

    public final JSlider getJSlider(int min, int max, int value) {
        JSlider slider = new JSlider(min, max, value);
        slider.setMajorTickSpacing(1);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setFont(font);
        return slider;
    }

    public void tearDown() {
        // Do nothing by default, let children override it.
    }

    protected void insert(JComponent component) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;
        constraints.gridwidth = 2;
        constraints.ipady = 20;
        constraints.insets = new Insets(10, 100, 10, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(component, constraints);

        row++;
    }

    protected void insert(JLabel label, JComponent component) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.insets = DEFAULT_INSETS;
        constraints.anchor = GridBagConstraints.LINE_START;
        add(label, constraints);

        constraints.gridx = 1;
        constraints.gridy = row;
        constraints.weightx = 0.5;
        constraints.insets = DEFAULT_INSETS;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(component, constraints);

        row++;
    }

    protected void insert(JComponent component1, JComponent component2) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0.5;
        constraints.insets = DEFAULT_INSETS;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(component1, constraints);

        constraints.gridx = 1;
        constraints.gridy = row;
        constraints.weightx = 0.5;
        constraints.insets = DEFAULT_INSETS;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(component2, constraints);

        row++;
    }

}
