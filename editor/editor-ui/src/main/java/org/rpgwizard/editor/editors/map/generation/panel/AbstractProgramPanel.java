/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.generation.panel;

import java.util.Map;
import javax.swing.JPanel;
import org.rpgwizard.editor.editors.map.generation.ProgramType;

/**
 *
 * @author Joshua Michael Daly
 */
public abstract class AbstractProgramPanel extends JPanel {

    protected final ProgramType programType;

    public AbstractProgramPanel(ProgramType programType) {
        this.programType = programType;
    }

    public ProgramType getProgramType() {
        return programType;
    }

    public abstract Map<String, Object> collect();

}
