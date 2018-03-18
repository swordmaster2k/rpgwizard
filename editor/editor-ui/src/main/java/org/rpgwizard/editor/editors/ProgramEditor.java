/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors;

import org.rpgwizard.editor.editors.program.RpgCodeCompletionProvider;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.js.JavaScriptLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.Program;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.resources.Icons;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Built in program editor for JavaScript files used in the engine.
 *
 * @author Joshua Michael Daly
 */
public final class ProgramEditor extends AbstractAssetEditorWindow {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramEditor.class);

    private final Program program;

    private RSyntaxTextArea textArea;

    public ProgramEditor(Program program) {
        super("Untitled", true, true, true, true, Icons.getIcon("program"));

        this.program = program;
        if (program.getDescriptor() == null) {
            init(program, "Untitled");
        } else {
            init(program, new File(program.getDescriptor().getURI()).getName());
        }
    }

    @Override
    public AbstractAsset getAsset() {
        return program;
    }

    @Override
    public void save() throws Exception {
        program.update(textArea.getText());
        save(program);
    }

    @Override
    public void saveAs(File file) throws Exception {
        program.setDescriptor(new AssetDescriptor(file.toURI()));
        setTitle(file.getName());
        save();
    }

    private void init(Program program, String fileName) {
        JPanel panel = new JPanel(new BorderLayout());

        LanguageSupportFactory languageFactory = LanguageSupportFactory.get();
        JavaScriptLanguageSupport languageSupport = (JavaScriptLanguageSupport) languageFactory
                .getSupportFor(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        languageSupport.setAutoActivationEnabled(true);
        try {
            languageSupport.getJarManager().addCurrentJreClassFileSource();
        } catch (IOException ex) {
            LOGGER.error("Failed to load language support!", ex);
        }

        String code = program.getProgramBuffer().toString();
        textArea = new RSyntaxTextArea(code, 30, 90);
        LanguageSupportFactory.get().register(textArea);
        textArea.setCaretPosition(0);
        textArea.requestFocusInWindow();
        textArea.setMarkOccurrences(true);
        textArea.setCodeFoldingEnabled(true);
        textArea.setTabsEmulated(true);
        textArea.setTabSize(3);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setNeedSave(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setNeedSave(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setNeedSave(true);
            }
        });
        ToolTipManager.sharedInstance().registerComponent(textArea);

        AutoCompletion autoCompletion = new AutoCompletion(new RpgCodeCompletionProvider());
        autoCompletion.setDescriptionWindowSize(650, 325);
        autoCompletion.setAutoActivationEnabled(true);
        autoCompletion.setShowDescWindow(true);
        autoCompletion.install(textArea);

        try {
            Theme theme = Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
            theme.apply(textArea);
        } catch (IOException ex) { // Never happens
            LOGGER.error("Failed to set theme.", ex);
        }

        RTextScrollPane scrollPane = new RTextScrollPane(textArea);
        panel.add(scrollPane);

        setContentPane(panel);
        setTitle(fileName);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }

    public void cleanUp() {
        LanguageSupportFactory.get().unregister(textArea);
    }

    public static void main(String[] args) {
        ProgramEditor editor = new ProgramEditor(new Program(null));
        editor.setVisible(true);

        JFrame frame = new JFrame("Test InternalJFrame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(editor);
        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
