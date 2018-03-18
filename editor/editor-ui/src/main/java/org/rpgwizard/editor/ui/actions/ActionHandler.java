/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions;

/**
 *
 * @author Joshua Michael Daly
 */
public interface ActionHandler {

    public void handle(UndoAction action);

    public void handle(RedoAction action);

    public void handle(CutAction action);

    public void handle(CopyAction action);

    public void handle(PasteAction action);

    public void handle(SelectAllAction action);

}
