/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Joshua Michael Daly
 */
public final class LogPanel extends JPanel {

    private final int referenceHeight;
    private final JTextArea textArea;

    public LogPanel(int height) {
        super(new BorderLayout());

        referenceHeight = height;

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        add(scroll);
    }

    public void append(ILoggingEvent e) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(e.getTimeStamp()), ZoneId.systemDefault());
        textArea.append(String.format("%s [%s] %s %s - %s\n", date.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
                e.getThreadName(), e.getLevel(), e.getLoggerName(), e.getFormattedMessage()));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    @Override
    public Dimension getPreferredSize() {
        super.getPreferredSize();
        return calculateDimensions(super.getPreferredSize().width);
    }

    @Override
    public Dimension getMaximumSize() {
        return calculateDimensions(super.getMaximumSize().width);
    }

    @Override
    public Dimension getMinimumSize() {
        return calculateDimensions(super.getMinimumSize().width);
    }

    private Dimension calculateDimensions(int width) {
        return new Dimension(width, referenceHeight);
    }

}
