/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.script;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Built in RPGCode auto-complete for JavaScript files used in the engine.
 *
 * @author Joshua Michael Daly
 */
public final class RpgCodeCompletionProvider extends LanguageAwareCompletionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpgCodeCompletionProvider.class);

    public RpgCodeCompletionProvider() {
        setDefaultCompletionProvider(createCodeCompletionProvider());
    }

    /**
     * Returns the provider to use when editing code.
     *
     * @return The provider.
     * @see #createCommentCompletionProvider()
     * @see #createStringCompletionProvider()
     * @see #loadCodeCompletionsFromXml(DefaultCompletionProvider)
     * @see #addShorthandCompletions(DefaultCompletionProvider)
     */
    protected CompletionProvider createCodeCompletionProvider() {
        DefaultCompletionProvider completionProvider = new DefaultCompletionProvider() {
            /**
             * Returns whether the specified character is valid in an auto-completion. The default implementation is
             * equivalent to "<code>Character.isLetterOrDigit(ch) || ch=='_'</code>". Subclasses can override this
             * method to change what characters are matched.
             *
             * @param ch
             *            The character.
             * @return Whether the character is valid.
             */
            @Override
            protected boolean isValidChar(char ch) {
                return Character.isLetterOrDigit(ch) || ch == '_' || ch == '.';
            }
        };
        completionProvider.setAutoActivationRules(true, null);
        completionProvider.setListCellRenderer(new RpgCodeCellRenderer());
        loadCodeCompletionsFromXml(completionProvider);
        return completionProvider;
    }

    /**
     * Returns the name of the XML resource to load (on classpath or a file).
     *
     * @return The resource to load.
     */
    protected String getXmlResource() {
        return "autocomplete/rpgcode.xml";
    }

    /**
     * Called from {@link #createCodeCompletionProvider()} to actually load the completions from XML. Subclasses that
     * override that method will want to call this one.
     *
     * @param completionProvider
     *            The code completion provider.
     */
    protected void loadCodeCompletionsFromXml(DefaultCompletionProvider completionProvider) {
        ClassLoader classLoader = getClass().getClassLoader();
        String resource = getXmlResource();
        if (resource != null) { // Subclasses may specify a null value
            try (InputStream in = classLoader.getResourceAsStream(resource)) {
                if (in != null) {
                    completionProvider.loadFromXML(in);
                } else {
                    completionProvider.loadFromXML(new File(resource));
                }
            } catch (IOException ex) {
                LOGGER.error("Failed to load rpgcode autocompletion.", ex);
            }
        }
    }

}
