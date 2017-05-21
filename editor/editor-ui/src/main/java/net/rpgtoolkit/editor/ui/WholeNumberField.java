/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.ui;

import java.text.NumberFormat;
import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

/**
 * A field that consists of a JFormattedTextField with a default formatter that
 * ensures it contains whole numbers. (Non-negative integers. 0, 1, 2, etc.) Use
 * any time you want a text field in which the user should enter only integers 0
 * and above. May or may not enforce a maximum value, but enforces a minimum
 * value of 0.
 *
 * @author Joel Moore
 */
public class WholeNumberField extends JFormattedTextField {

    /**
     * Creates a WholeNumberField with the specified value. This will create and
     * use a Formatter that requires values that are whole numbers: non-negative
     * (long) integers.
     *
     * @param value Initial value for the WholeNumberField
     */
    public WholeNumberField(Object value) {
        super(getWholeNumFormatter());
        this.setValue(value);
    }

    public static NumberFormatter getWholeNumFormatter() {
        NumberFormatter wholeNumberFormatter = new NumberFormatter(
                NumberFormat.getIntegerInstance());
        wholeNumberFormatter.setValueClass(Long.class);
        wholeNumberFormatter.setMinimum(0L);
        wholeNumberFormatter.setCommitsOnValidEdit(true);
        return wholeNumberFormatter;
    }
    
    @Override
    public Long getValue() {
        Object val = super.getValue();
        if(val instanceof Integer) {
            return Long.valueOf((Integer)val);
        } else {
            return (Long)super.getValue();
        }
    }
    
}
