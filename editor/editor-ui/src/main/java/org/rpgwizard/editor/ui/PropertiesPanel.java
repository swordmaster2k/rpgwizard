/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.rpgwizard.editor.MainWindow;

/**
 *
 * @author Joshua Michael Daly
 */
public class PropertiesPanel extends JPanel implements ListSelectionListener {

	private Object model;

	private JScrollPane propertiesScrollPane;

	public PropertiesPanel() {
		initialize();
	}

	public PropertiesPanel(Object model) {
		this.model = model;
		initialize();
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;

		AbstractModelPanel panel = ModelPanelFactory.getModelPanel(model);

		// To ensure that the internal controls are not streched.
		JPanel intermediate = new JPanel(new BorderLayout());

		if (panel != null) {
			intermediate.add(panel, BorderLayout.NORTH);
		}

		propertiesScrollPane.setViewportView(intermediate);
		propertiesScrollPane.getViewport().revalidate();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

	}

	private void initialize() {
		if (model != null) {

		} else {

		}

		propertiesScrollPane = new JScrollPane();
		propertiesScrollPane.getViewport().setScrollMode(
				JViewport.SIMPLE_SCROLL_MODE);
		propertiesScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		setLayout(new BorderLayout());
		add(propertiesScrollPane, BorderLayout.CENTER);
	}

}
