/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.Item;
import static org.rpgwizard.editor.editors.sprite.AbstractSpriteEditor.DEFAULT_INPUT_COLUMNS;
import org.rpgwizard.editor.ui.ImagePanel;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.DoubleField;
import org.rpgwizard.editor.ui.IntegerField;
import org.rpgwizard.editor.ui.listeners.ImagePanelChangeListener;
import org.rpgwizard.editor.ui.resources.Icons;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class ItemEditor extends AbstractAssetEditorWindow {

    private final int DESCRIPTION_INPUT_COLUMNS = 30;
    private final int DESCRIPTION_INPUT_ROWS = 5;

    private final Item item;

    private JTabbedPane tabbedPane;
    private Border defaultEtchedBorder;

    private JPanel generalTab;
    private JPanel generalEditPanel;

    private JTextField name;
    private ImagePanel icon;
    private JTextArea description;
    private JTextField type;
    private IntegerField price;

    private JPanel effectsTab;
    private JPanel effectsEditPanel;

    private DoubleField health;
    private DoubleField attack;
    private DoubleField defence;
    private DoubleField magic;

    public ItemEditor(Item item) {
        super("Untitled", true, true, true, true, Icons.getIcon("item"));
        this.item = item;

        if (item.getDescriptor() == null) {
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

    @Override
    public void save() throws Exception {
        save(item);
    }

    @Override
    public void saveAs(File file) throws Exception {
        item.setDescriptor(new AssetDescriptor(file.toURI()));
        setTitle(file.getName());
        save();
    }

    private void setupNewItem() {

    }

    /**
     * Builds the Swing interface
     */
    private void constructWindow() {
        defaultEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        generalTab = new JPanel();
        effectsTab = new JPanel();

        createGeneralEditPanel();
        createEffectsEditPanel();

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("General", generalTab);
        tabbedPane.addTab("Effects", effectsTab);

        add(tabbedPane);
    }

    private void createGeneralEditPanel() {
        List<Component> labels = new ArrayList<>();
        labels.add(new JLabel("Name"));
        labels.add(new JLabel("Icon (48x48)"));
        labels.add(new JLabel("Description"));
        labels.add(new JLabel("Type"));
        labels.add(new JLabel("Price"));

        name = new JTextField(item.getName());
        name.setColumns(DEFAULT_INPUT_COLUMNS);
        name.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                item.setName(name.getText());
                setNeedSave(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                item.setName(name.getText());
                setNeedSave(true);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                item.setName(name.getText());
                setNeedSave(true);
            }
        });

        icon = new ImagePanel(new Dimension(48, 48));
        if (!item.getIcon().isEmpty()) {
            icon.addImage(new File(EditorFileManager.getGraphicsPath() + item.getIcon()));
        }
        icon.addImageListener(() -> {
            item.setIcon(icon.getImagePath());
            setNeedSave(true);
        });

        description = new JTextArea(item.getDescription());
        description.setColumns(DESCRIPTION_INPUT_COLUMNS);
        description.setRows(DESCRIPTION_INPUT_ROWS);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                item.setDescription(description.getText());
                setNeedSave(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                item.setDescription(description.getText());
                setNeedSave(true);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                item.setDescription(description.getText());
                setNeedSave(true);
            }
        });

        type = new JTextField(item.getType());
        type.setColumns(DEFAULT_INPUT_COLUMNS);
        type.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                item.setType(type.getText());
                setNeedSave(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                item.setType(type.getText());
                setNeedSave(true);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                item.setType(type.getText());
                setNeedSave(true);
            }
        });

        price = new IntegerField(item.getPrice());
        price.setColumns(DEFAULT_INPUT_COLUMNS);
        price.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                item.setPrice(price.getValue());
                setNeedSave(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                item.setPrice(price.getValue());
                setNeedSave(true);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                item.setPrice(price.getValue());
                setNeedSave(true);
            }
        });

        List<Component> inputs = new ArrayList<>();
        inputs.add(name);
        inputs.add(icon);
        inputs.add(description);
        inputs.add(type);
        inputs.add(price);

        buildGeneralTab(labels, inputs);
    }

    private void buildGeneralTab(List<Component> labels, List<Component> inputs) {
        // Configure the necessary Panels
        generalEditPanel = new JPanel();
        generalEditPanel.setBorder(BorderFactory.createTitledBorder(defaultEtchedBorder, "General"));
        GuiHelper.buildEditPanelPair(generalTab, generalEditPanel, labels, inputs);
    }

    private void createEffectsEditPanel() {
        List<Component> labels = new ArrayList<>();
        labels.add(new JLabel("Health"));
        labels.add(new JLabel("Attack"));
        labels.add(new JLabel("Defence"));
        labels.add(new JLabel("Magic"));

        health = new DoubleField(item.getHealthEffect());
        health.setColumns(DEFAULT_INPUT_COLUMNS);
        health.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                item.setHealthEffect(health.getValue());
                setNeedSave(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                item.setHealthEffect(health.getValue());
                setNeedSave(true);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                item.setHealthEffect(health.getValue());
                setNeedSave(true);
            }
        });

        attack = new DoubleField(item.getAttackEffect());
        attack.setColumns(DEFAULT_INPUT_COLUMNS);
        attack.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                item.setAttackEffect(attack.getValue());
                setNeedSave(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                item.setAttackEffect(attack.getValue());
                setNeedSave(true);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                item.setAttackEffect(attack.getValue());
                setNeedSave(true);
            }
        });

        defence = new DoubleField(item.getDefenceEffect());
        defence.setColumns(DEFAULT_INPUT_COLUMNS);
        defence.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                item.setDefenceEffect(defence.getValue());
                setNeedSave(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                item.setDefenceEffect(defence.getValue());
                setNeedSave(true);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                item.setDefenceEffect(defence.getValue());
                setNeedSave(true);
            }
        });

        magic = new DoubleField(item.getMagicEffect());
        magic.setColumns(DEFAULT_INPUT_COLUMNS);
        defence.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                item.setMagicEffect(magic.getValue());
                setNeedSave(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                item.setMagicEffect(magic.getValue());
                setNeedSave(true);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                item.setMagicEffect(magic.getValue());
                setNeedSave(true);
            }
        });

        List<Component> inputs = new ArrayList<>();
        inputs.add(health);
        inputs.add(attack);
        inputs.add(defence);
        inputs.add(magic);

        buildEffectsPanel(labels, inputs);
    }

    private void buildEffectsPanel(List<Component> labels, List<Component> inputs) {
        // Configure the necessary Panels
        effectsEditPanel = new JPanel();
        effectsEditPanel.setBorder(BorderFactory.createTitledBorder(defaultEtchedBorder, "Effects (+/-)"));
        GuiHelper.buildEditPanelPair(effectsTab, effectsEditPanel, labels, inputs);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test InternalJFrame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ItemEditor(new Item(null)));
        frame.setSize(440, 360);
        frame.setVisible(true);
    }

}
