/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board.generation;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.files.FileAssetHandleResolver;
import org.rpgwizard.common.assets.serialization.TextProgramSerializer;
import org.rpgwizard.editor.editors.board.generation.panel.AbstractProgramPanel;
import org.rpgwizard.editor.editors.board.generation.panel.BoardLinkPanel;
import org.rpgwizard.editor.editors.board.generation.panel.CustomPanel;

/**
 *
 * @author Joshua Michael Daly
 */
public final class ProgramDialog extends JDialog {

    private final JComboBox<String> programTypeCombo;

    private final JButton okButton;
    private final JButton cancelButton;

    private JScrollPane centerPane;
    private AbstractProgramPanel programPanel;

    private String value;

    public ProgramDialog(Window owner, String program) {
        super(owner, "Generate Program", JDialog.ModalityType.APPLICATION_MODAL);
        this.value = program;

        programPanel = new CustomPanel();
        centerPane = new JScrollPane(programPanel);
        centerPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        centerPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        List<String> types = Arrays.asList(ProgramType.values()).stream().map(ProgramType::name)
                .collect(Collectors.toList());
        programTypeCombo = new JComboBox<>(types.toArray(new String[0]));
        programTypeCombo.addActionListener((e) -> {
            String selected = (String) programTypeCombo.getSelectedItem();
            ProgramType type = ProgramType.valueOf(selected);

            switch (type) {
            case BOARD_LINK:
                programPanel = new BoardLinkPanel();
                break;
            default:
                programPanel = new CustomPanel();
            }

            centerPane.setViewportView(programPanel);
            centerPane.getViewport().revalidate();
            pack();
        });

        okButton = new JButton("OK");
        okButton.addActionListener((e) -> {
            dispose();
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((e) -> {
            dispose();
        });

        GridLayout gridLayout = new GridLayout(0, 2);
        gridLayout.setVgap(5);

        JPanel selectionPanel = new JPanel(gridLayout);
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        selectionPanel.add(new JLabel("Program Type", SwingConstants.LEFT));
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

    public void display() throws IOException {
        JPanel replacementPanel = new CustomPanel();
        centerPane.add(replacementPanel);
        revalidate();
        repaint();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String collect() throws IOException, AssetException, URISyntaxException {
        ProgramType programType = programPanel.getProgramType();

        Map<String, Object> values = programPanel.collect();
        if (programType == ProgramType.CUSTOM) {
            return (String) values.get("program");
        }

        return ProgramGenerator.generate(values, programType);
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
