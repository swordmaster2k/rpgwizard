/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.project;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.apache.commons.lang3.StringUtils;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 * @author Joshua Michael Daly
 */
public final class NewProjectDialog extends JDialog {

    private final JTextField projectNameField;
    private final JComboBox templateComboBox;

    private final JButton createButton;

    private String[] value = null;

    /**
     *
     * @param owner
     * @param templates
     */
    public NewProjectDialog(Window owner, String[] templates) {
        super(owner, "New Project", JDialog.ModalityType.APPLICATION_MODAL);

        projectNameField = new JTextField(20);

        templateComboBox = new JComboBox(templates);

        createButton = new JButton("Create");
        createButton.addActionListener((ActionEvent e) -> {
            String projectName = projectNameField.getText();
            String template = (String) templateComboBox.getSelectedItem();
            if (!isInputValid(projectName, template)) {
                return;
            }
            value = new String[] { projectName.trim(), template };
            dispose();
        });

        GridBagConstraints constraints = new GridBagConstraints();
        GridBagLayout gridBagLayout = new GridBagLayout();
        JPanel controlsPanel = new JPanel(gridBagLayout);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.LINE_START;
        controlsPanel.add(new JLabel("Project Name", SwingConstants.LEFT), constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0.5;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        controlsPanel.add(projectNameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.LINE_START;
        controlsPanel.add(new JLabel("Template", SwingConstants.LEFT), constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 0.5;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        controlsPanel.add(templateComboBox, constraints);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(controlsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        setResizable(false);
        pack();
    }

    /**
     *
     * @return
     */
    public String[] getValue() {
        return value;
    }

    private boolean isInputValid(String projectName, String template) {
        if (StringUtils.isBlank(projectName)) {
            projectNameField.setBackground(GuiHelper.INVALID_INPUT_COLOR);
            return false;
        }
        if (projectName.equals(template)) {
            projectNameField.setBackground(GuiHelper.INVALID_INPUT_COLOR);
            templateComboBox.setBackground(GuiHelper.INVALID_INPUT_COLOR);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        NewProjectDialog dialog = new NewProjectDialog(null, new String[] { "Test-1", "Test-2" });
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        System.exit(0);
    }

}
