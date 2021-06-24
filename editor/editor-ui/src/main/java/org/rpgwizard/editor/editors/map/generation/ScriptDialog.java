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
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.files.FileAssetHandleResolver;
import org.rpgwizard.common.assets.serialization.ScriptSerializer;
import org.rpgwizard.editor.editors.map.generation.panel.AbstractScriptPanel;
import org.rpgwizard.editor.editors.map.generation.panel.MapLinkPanel;
import org.rpgwizard.editor.editors.map.generation.panel.CustomPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public final class ScriptDialog extends JDialog {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptDialog.class);

    private final JComboBox<String> scriptTypeCombo;

    private final JButton okButton;
    private final JButton cancelButton;

    private final JScrollPane centerPane;
    private AbstractScriptPanel scriptPanel;

    @Getter
    private final String oldValue;
    @Getter
    private String newValue;

    public ScriptDialog(Window owner, String script) {
        super(owner, "Configure Event", JDialog.ModalityType.APPLICATION_MODAL);
        this.oldValue = script;
        this.newValue = null;

        centerPane = new JScrollPane();
        centerPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        centerPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        List<String> types = Arrays.asList(ScriptType.values()).stream().map(ScriptType::name)
                .collect(Collectors.toList());
        scriptTypeCombo = new JComboBox<>(types.toArray(new String[0]));
        Pair<ScriptType, Map<String, Object>> pair = ScriptInterpreter.interpret(oldValue);
        switchPanel(pair.getKey(), pair.getValue());
        scriptTypeCombo.addActionListener((e) -> {
            String selected = (String) scriptTypeCombo.getSelectedItem();
            ScriptType type = ScriptType.valueOf(selected);
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
        selectionPanel.add(scriptTypeCombo);

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

    public void display() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String collect() {
        try {
            ScriptType scriptType = scriptPanel.getScriptType();

            Map<String, Object> values = scriptPanel.collect();
            if (scriptType == ScriptType.CUSTOM) {
                return (String) values.get("script");
            }

            if (oldValue.contains(ScriptGenerator.AUTO_GENERATED_DIR)) {
                // Replace old auto-generated script
                String id = oldValue.replace(ScriptGenerator.AUTO_GENERATED_DIR + "/", "")
                        .replace(ScriptGenerator.TEMPLATE_EXT, "");
                return ScriptGenerator.generate(id, values, scriptType);
            } else {
                // Generate a new script.
                return ScriptGenerator.generate(values, scriptType);
            }
        } catch (IOException | AssetException | URISyntaxException ex) {
            LOGGER.error("Failed to collect new value!", ex);
            JOptionPane.showMessageDialog(this, "Failed to auto-generate event!", "Generate Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void switchPanel(ScriptType scriptType, Map<String, Object> parameters) {
        switch (scriptType) {
        case MAP_LINK:
            if (parameters.isEmpty()) {
                scriptPanel = new MapLinkPanel();
            } else {
                scriptPanel = new MapLinkPanel(parameters);
            }
            break;
        default:
            if (parameters.isEmpty()) {
                scriptPanel = new CustomPanel();
            } else {
                scriptPanel = new CustomPanel(parameters);
            }
        }
        scriptTypeCombo.setSelectedItem(scriptType.name());
        centerPane.setViewportView(scriptPanel);
        centerPane.getViewport().revalidate();
        pack();
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("project.path",
                "D:\\Documents\\Software Development\\rpgwizard\\editor\\editor-ui\\target\\classes\\projects\\The Wizard's Tower");

        AssetManager.getInstance().registerResolver(new FileAssetHandleResolver());
        AssetManager.getInstance().registerSerializer(new ScriptSerializer());

        ScriptDialog dialog = new ScriptDialog(null, "test.js");
        dialog.display();
        System.out.println(dialog.collect());
    }

}
