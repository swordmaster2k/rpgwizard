/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors;

import java.awt.BorderLayout;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.js.JavaScriptLanguageSupport;
import org.fife.rsta.ac.js.JsErrorParser;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.Script;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.script.RpgCodeCompletionProvider;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.actions.ActionHandler;
import org.rpgwizard.editor.ui.actions.CopyAction;
import org.rpgwizard.editor.ui.actions.CutAction;
import org.rpgwizard.editor.ui.actions.PasteAction;
import org.rpgwizard.editor.ui.actions.RedoAction;
import org.rpgwizard.editor.ui.actions.SelectAllAction;
import org.rpgwizard.editor.ui.actions.UndoAction;
import org.rpgwizard.editor.ui.resources.Icons;
import org.rpgwizard.editor.utilities.FileTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Built in program editor for JavaScript files used in the engine.
 *
 * @author Joshua Michael Daly
 */
@Getter
@Setter
public final class ScriptEditor extends AbstractAssetEditorWindow
        implements SearchListener, ActionHandler, PropertyChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptEditor.class);

    private static final String JSHINT_RC_FILE = "config" + File.separator + ".jshintrc";

    private final Script script;
    private RSyntaxTextArea textArea;

    public ScriptEditor(Script script) {
        super("Untitled", true, true, true, true, Icons.getIcon("program"));

        this.script = script;
        if (script.getDescriptor() == null) {
            try (InputStream in = ScriptEditor.class.getResourceAsStream("/script/templates/empty.js")) {
                script.update(IOUtils.toString(in, StandardCharsets.UTF_8));
            } catch (IOException ex) {
                // Ignore it
            }
            init(script, "Untitled");
        } else {
            init(script, new File(script.getDescriptor().getURI()).getName());
        }
    }

    @Override
    public AbstractAsset getAsset() {
        return script;
    }

    @Override
    public void save() throws Exception {
        script.update(textArea.getText());
        save(script);
    }

    @Override
    public void saveAs(File file) throws Exception {
        script.setDescriptor(new AssetDescriptor(file.toURI()));
        setTitle(file.getName());
        save();
    }

    public void forceReparsing() {
        textArea.forceReparsing(0);
    }

    private void init(Script script, String fileName) {
        String code = script.getStringBuffer().toString();
        textArea = new RSyntaxTextArea(code, 30, 90);
        LanguageSupportFactory.get().register(textArea);
        textArea.setCaretPosition(0);
        textArea.requestFocusInWindow();
        textArea.setMarkOccurrences(true);
        textArea.setCodeFoldingEnabled(true);
        textArea.setTabsEmulated(true);
        textArea.setTabSize(3);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        textArea.addPropertyChangeListener(RSyntaxTextArea.PARSER_NOTICES_PROPERTY, this);
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

        LanguageSupportFactory languageFactory = LanguageSupportFactory.get();
        JavaScriptLanguageSupport languageSupport = (JavaScriptLanguageSupport) languageFactory
                .getSupportFor(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        languageSupport.setLanguageVersion(200); // https://github.com/mozilla/rhino/blob/bee350c9c0559dcbce0715d506d41d9cd95994cd/src/org/mozilla/javascript/Context.java#L131
        languageSupport.setAutoActivationEnabled(true);
        try {
            // Attempt to configure JSHint for error parsing
            File jsHintRCFile = new File(
                    FileTools.getExecutionPath(ScriptEditor.class) + File.separator + JSHINT_RC_FILE);
            if (jsHintRCFile.exists()) {
                languageSupport.setErrorParser(JsErrorParser.JSHINT);
                languageSupport.setDefaultJsHintRCFile(jsHintRCFile);
            } else {
                LOGGER.warn("Could not find local jsHintRCFile=[{}]", jsHintRCFile);
            }
        } catch (URISyntaxException ex) {
            LOGGER.error("Failed to JSHint config.", ex);
        }
        languageSupport.install(textArea);

        AutoCompletion autoCompletion = new AutoCompletion(new RpgCodeCompletionProvider());
        autoCompletion.setDescriptionWindowSize(650, 325);
        autoCompletion.setAutoActivationEnabled(true);
        autoCompletion.setAutoCompleteSingleChoices(false);
        autoCompletion.setShowDescWindow(true);
        autoCompletion.install(textArea);

        try {
            Theme theme = Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
            theme.apply(textArea);
        } catch (IOException ex) { // Never happens
            LOGGER.error("Failed to set theme.", ex);
        }

        Font font = textArea.getFont();
        Font newFont = new Font(font.getName(), font.getStyle(), 14);
        textArea.setFont(newFont);

        RTextScrollPane scrollPane = new RTextScrollPane(textArea, true);
        // ErrorStrip errorStrip = new ErrorStrip(textArea);
        // errorStrip.setShowMarkedOccurrences(false);
        // errorStrip.setShowMarkAll(false);
        // errorStrip.setFollowCaret(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        // panel.add(errorStrip, BorderLayout.LINE_END); // Slows things down

        setContentPane(panel);
        setTitle(fileName);
        pack();
    }

    public void cleanUp() {
        LanguageSupportFactory.get().unregister(textArea);
    }

    @Override
    public void searchEvent(SearchEvent se) {
        SearchResult result;
        SearchEvent.Type type = se.getType();
        SearchContext context = se.getSearchContext();
        switch (type) {
        default: // Prevent FindBugs warning later
        case MARK_ALL:
            SearchEngine.markAll(textArea, context);
            break;
        case FIND:
            result = SearchEngine.find(textArea, context);
            if (!result.wasFound()) {
                UIManager.getLookAndFeel().provideErrorFeedback(textArea);
            }
            break;
        case REPLACE:
            result = SearchEngine.replace(textArea, context);
            if (!result.wasFound()) {
                UIManager.getLookAndFeel().provideErrorFeedback(textArea);
            }
            break;
        case REPLACE_ALL:
            result = SearchEngine.replaceAll(textArea, context);
            JOptionPane.showMessageDialog(null, result.getCount() + " occurrences replaced.");
            break;
        }
    }

    @Override
    public String getSelectedText() {
        return textArea.getSelectedText();
    }

    @Override
    public boolean canUndo() {
        return textArea.canUndo();
    }

    @Override
    public boolean canRedo() {
        return textArea.canRedo();
    }

    @Override
    public void handle(UndoAction action) {
        textArea.undoLastAction();
    }

    @Override
    public void handle(RedoAction action) {
        textArea.redoLastAction();
    }

    @Override
    public void handle(CutAction action) {
        textArea.cut();
    }

    @Override
    public void handle(CopyAction action) {
        textArea.copy();
    }

    @Override
    public void handle(PasteAction action) {
        textArea.paste();
    }

    @Override
    public void handle(SelectAllAction action) {
        textArea.selectAll();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!MainWindow.getInstance().getSouthPanel().isVisible()) {
            return;
        }

        List<ParserNotice> notices = textArea.getParserNotices();
        MainWindow.getInstance().getIssuesPanel().addNotices(notices);
    }

    public static void main(String[] args) {
        ScriptEditor editor = new ScriptEditor(new Script(null));
        editor.setVisible(true);

        JFrame frame = new JFrame("Test InternalJFrame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(editor);
        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
