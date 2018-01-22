/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

/**
 * 
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public class RunTimeKey {
    private int key;
    private String program;

    public RunTimeKey(int key, String program) {
        this.key = key;
        this.program = program;
    }

    public int getKey() {
        return key;
    }

    public String getProgram() {
        return program;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setProgram(String program) {
        this.program = program;
    }

}
