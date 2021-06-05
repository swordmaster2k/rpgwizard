/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.script;

import javax.swing.Icon;
import javax.swing.JList;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.VariableCompletion;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 * The cell renderer used for the RPGCode programming language.
 *
 * Based on:
 * https://github.com/bobbylight/RSTALanguageSupport/blob/master/src/main/java/org/fife/rsta/ac/c/CCellRenderer.java
 *
 * @author Joshua Michael Daly
 */
class RpgCodeCellRenderer extends CompletionCellRenderer {

    private final Icon variableIcon;
    private final Icon functionIcon;

    /**
     * Constructor.
     */
    public RpgCodeCellRenderer() {
        variableIcon = Icons.getIcon("variable");
        functionIcon = Icons.getIcon("function");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareForOtherCompletion(JList list, Completion c, int index, boolean selected, boolean hasFocus) {
        super.prepareForOtherCompletion(list, c, index, selected, hasFocus);
        setIcon(getEmptyIcon());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareForVariableCompletion(JList list, VariableCompletion vc, int index, boolean selected,
            boolean hasFocus) {
        super.prepareForVariableCompletion(list, vc, index, selected, hasFocus);
        setIcon(variableIcon);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareForFunctionCompletion(JList list, FunctionCompletion fc, int index, boolean selected,
            boolean hasFocus) {
        super.prepareForFunctionCompletion(list, fc, index, selected, hasFocus);
        setIcon(functionIcon);
    }

}
