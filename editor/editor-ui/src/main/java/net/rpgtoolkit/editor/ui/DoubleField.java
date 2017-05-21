/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.ui;

import java.text.DecimalFormat;
import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

/**
 * A field that consists of a JFormattedTextField with a default formatter that
 * ensures it contains doubles. (0.0, 1.0, 2.0, etc.) Use any time you want a
 * text field in which the user should enter only integers.
 *
 * @author Joshua Michael Daly
 */
public class DoubleField extends JFormattedTextField {

	/**
	 * Creates a IntegerField with the specified value. This will create and use
	 * a Formatter that requires values that are (long) integers.
	 *
	 * @param value
	 *            Initial value for the IntegerField
	 */
	public DoubleField(Object value) {
		super(getDoubleFormatter());
		setColumns(4);
		setValue(value);
	}

	public static NumberFormatter getDoubleFormatter() {
		NumberFormatter doubleFormatter = new NumberFormatter(
				new DecimalFormat("#0.00"));
		doubleFormatter.setValueClass(Double.class);
		doubleFormatter.setCommitsOnValidEdit(true);

		return doubleFormatter;
	}

	@Override
	public Double getValue() {
		Object val = super.getValue();
		if (val instanceof Double) {
			return (Double) val;
		} else {
			return (Double) super.getValue();
		}
	}

}
