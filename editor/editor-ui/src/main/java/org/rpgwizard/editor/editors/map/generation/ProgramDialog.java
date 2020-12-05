/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.generation;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.files.FileAssetHandleResolver;
import org.rpgwizard.common.assets.serialization.TextProgramSerializer;
import org.rpgwizard.editor.editors.map.generation.panel.AbstractProgramPanel;
import org.rpgwizard.editor.editors.map.generation.panel.BoardLinkPanel;
import org.rpgwizard.editor.editors.map.generation.panel.CustomPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public final class ProgramDialog extends JDialog {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramDialog.class);

    private final JComboBox<String> programTypeCombo;

    private final JButton okButton;
    private final JButton cancelButton;

    private final JScrollPane centerPane;
    private AbstractProgramPanel programPanel;

    private final String oldValue;
    private String newValue;

    public ProgramDialog(Window owner, String program) {
        super(owner, "Configure Event", JDialog.ModalityType.APPLICATION_MODAL);
        this.oldValue = program;
        this.newValue = null;

        centerPane = new JScrollPane();
        centerPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        centerPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        List<String> types = Arrays.asList(ProgramType.values()).stream().map(ProgramType::name)
                .collect(Collectors.toList());
        programTypeCombo = new JComboBox<>(types.toArray(new String[0]));
        Pair<ProgramType, Map<String, Object>> pair = ProgramInterpreter.interpret(oldValue);
        switchPanel(pair.getKey(), pair.getValue());
        programTypeCombo.addActionListener((e) -> {
            String selected = (String) programTypeCombo.getSelectedItem();
            ProgramType type = ProgramType.valueOf(selected);
            switchPanel(type, new HashMap<>());
        });

        okButton = new JButton("OK");
        okButton.addActionListener((e) -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure? This will replace any previously auto-generated events.", "Generate event?",
                    JOptionPane.YES_NO_OPTION);
            if (result == 0) {
                newValue = collect();
                dispose();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((e) -> {
            dispose();
        });

        GridLayout gridLayout = new GridLayout(0, 2);
        gridLayout.setVgap(5);

        JPanel selectionPanel = new JPanel(gridLayout);
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        selectionPanel.add(new JLabel("Event Type", SwingConstants.LEFT));
        selectionPanel.add(programTypeCombo);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 2));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        JPanel northPanel = new JPanel(new GridLayout(1, 2));
        northPanel.add(selectionPanel);
        northPanel.add(buttonPanel);

        add(northPanel, BorderLayout.NORTH);
        add(centerPane, BorderLayout.CENTER);

        setResizable(false);
        pack();
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void display() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String collect() {
        try {
            ProgramType programType = programPanel.getProgramType();

            Map<String, Object> values = programPanel.collect();
            if (programType == ProgramType.CUSTOM) {
                return (String) values.get("program");
            }

            if (oldValue.contains(ProgramGenerator.AUTO_GENERATED_DIR)) {
                // Replace old auto-generated program
                String id = oldValue.replace(ProgramGenerator.AUTO_GENERATED_DIR + "/", "")
                        .replace(ProgramGenerator.TEMPLATE_EXT, "");
                return ProgramGenerator.generate(id, values, programType);
            } else {
                // Generate a new program.
                return ProgramGenerator.generate(values, programType);
            }
        } catch (IOException | AssetException | URISyntaxException ex) {
            LOGGER.error("Failed to collect new value!", ex);
            JOptionPane.showMessageDialog(this, "Failed to auto-generate event!", "Generate Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void switchPanel(ProgramType programType, Map<String, Object> parameters) {
        switch (programType) {
        case BOARD_LINK:
            if (parameters.isEmpty()) {
                programPanel = new BoardLinkPanel();
            } else {
                programPanel = new BoardLinkPanel(parameters);
            }
            break;
        default:
            if (parameters.isEmpty()) {
                programPanel = new CustomPanel();
            } else {
                programPanel = new CustomPanel(parameters);
            }
        }
        programTypeCombo.setSelectedItem(programType.name());
        centerPane.setViewportView(programPanel);
        centerPane.getViewport().revalidate();
        pack();
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("project.path",
                "D:\\Documents\\Software Development\\rpgwizard\\editor\\editor-ui\\target\\classes\\projects\\The Wizard's Tower");

        AssetManager.getInstance().registerResolver(new FileAssetHandleResolver());
        AssetManager.getInstance().registerSerializer(new TextProgramSerializer());

        ProgramDialog dialog = new ProgramDialog(null, "test.js");
        dialog.display();
        System.out.println(dialog.collect());
    }

}
