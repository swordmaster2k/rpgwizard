/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.properties;

/**
 * This Enum exits to make refactoring editor properties in the .properties file easier.
 *
 * @author Joshua Michael Daly
 */
public enum EditorProperty {

    EDITOR_UI_TITLE("editor.ui.title"),

    EDITOR_BUILDS_DIRECOTRY("editor.builds.directory"), EDITOR_JRE_DIRECOTRY(
            "editor.jre.directory"), EDITOR_PLUGINS_DIRECOTRY("editor.plugins.directory"),

    // Tooltips
    EDITOR_UI_TOOLTIP_NEW("editor.ui.tooltip.new"), EDITOR_UI_TOOLTIP_OPEN(
            "editor.ui.tooltip.open"), EDITOR_UI_TOOLTIP_SAVE("editor.ui.tooltip.save"), EDITOR_UI_TOOLTIP_SAVE_ALL(
                    "editor.ui.tooltip.saveall"), EDITOR_UI_TOOLTIP_CUT(
                            "editor.ui.tooltip.cut"), EDITOR_UI_TOOLTIP_COPY(
                                    "editor.ui.tooltip.copy"), EDITOR_UI_TOOLTIP_PASTE(
                                            "editor.ui.tooltip.paste"), EDITOR_UI_TOOLTIP_DELETE(
                                                    "editor.ui.tooltip.delete"), EDITOR_UI_TOOLTIP_UNDO(
                                                            "editor.ui.tooltip.undo"), EDITOR_UI_TOOLTIP_REDO(
                                                                    "editor.ui.tooltip.redo"), EDITOR_UI_TOOLTIP_DRAW_TILE(
                                                                            "editor.ui.tooltip.drawtile"), EDITOR_UI_TOOLTIP_SELECT_REGION(
                                                                                    "editor.ui.tooltip.selectregion"), EDITOR_UI_TOOLTIP_FILL_REGION(
                                                                                            "editor.ui.tooltip.fillregion"), EDITOR_UI_TOOLTIP_ERASE_REGION(
                                                                                                    "editor.ui.tooltip.eraseregion"), EDITOR_UI_TOOLTIP_DRAW_VECTOR(
                                                                                                            "editor.ui.tooltip.drawvector"), EDITOR_UI_TOOLTIP_DRAW_PROGRAM(
                                                                                                                    "editor.ui.tooltip.drawprogram"), EDITOR_UI_TOOLTIP_SET_BOARD_SPRITE(
                                                                                                                            "editor.ui.tooltip.setboardsprite"), EDITOR_UI_TOOLTIP_SET_BOARD_IMAGE(
                                                                                                                                    "editor.ui.tooltip.setboardimage"), EDITOR_UI_TOOLTIP_SET_BOARD_LIGHT(
                                                                                                                                            "editor.ui.tooltip.setboardlight"), EDITOR_UI_TOOLTIP_SET_START_POSITION(
                                                                                                                                                    "editor.ui.tooltip.setstartposition"), EDITOR_UI_TOOLTIP_ZOOM_IN(
                                                                                                                                                            "editor.ui.tooltip.zoomin"), EDITOR_UI_TOOLTIP_ZOOM_OUT(
                                                                                                                                                                    "editor.ui.tooltip.zoomout"), EDITOR_UI_TOOLTIP_RUN_GAME(
                                                                                                                                                                            "editor.ui.tooltip.rungame"), EDITOR_UI_TOOLTIP_STOP_GAME(
                                                                                                                                                                                    "editor.ui.tooltip.stopgame"), EDITOR_UI_TOOLTIP_COMPILE_GAME(
                                                                                                                                                                                            "editor.ui.tooltip.compilegame"), EDITOR_UI_TOOLTIP_HELP(
                                                                                                                                                                                                    "editor.ui.tooltip.help");

    private final String name;

    private EditorProperty(String s) {
        name = s;
    }

    @Override
    public String toString() {
        return name;
    }

}
