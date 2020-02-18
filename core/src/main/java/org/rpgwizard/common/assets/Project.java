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
public class Project extends AbstractAsset {

    private String name;
    private int resolutionWidth;
    private int resolutionHeight;
    private boolean isFullScreen;
    private String initialBoard;
    private String initialCharacter;
    private String startupProgram;
    private String gameOverProgram;
    private String projectIcon;
    private boolean showVectors;

    public Project(AssetDescriptor descriptor) {
        super(descriptor);

        isFullScreen = false;
        startupProgram = "";
        initialBoard = "";
        initialCharacter = "";
        gameOverProgram = "";
        resolutionWidth = 640;
        resolutionHeight = 480;
        projectIcon = "";
        showVectors = false;
    }

    public Project(AssetDescriptor descriptor, String name) {
        this(descriptor);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public void setResolutionWidth(int resolutionWidth) {
        this.resolutionWidth = resolutionWidth;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    public void setResolutionHeight(int resolutionHeight) {
        this.resolutionHeight = resolutionHeight;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setIsFullScreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
    }

    public String getInitialBoard() {
        return initialBoard;
    }

    public void setInitialBoard(String initialBoard) {
        this.initialBoard = initialBoard;
    }

    public String getInitialCharacter() {
        return initialCharacter;
    }

    public void setInitialCharacter(String initialCharacter) {
        this.initialCharacter = initialCharacter;
    }

    public String getStartupProgram() {
        return startupProgram;
    }

    public void setStartupProgram(String startupProgram) {
        this.startupProgram = startupProgram;
    }

    public String getGameOverProgram() {
        return gameOverProgram;
    }

    public void setGameOverProgram(String gameOverProgram) {
        this.gameOverProgram = gameOverProgram;
    }

    public String getProjectIcon() {
        return projectIcon;
    }

    public void setProjectIcon(String projectIcon) {
        this.projectIcon = projectIcon;
    }

    public boolean isShowVectors() {
        return showVectors;
    }

    public void setShowVectors(boolean showVectors) {
        this.showVectors = showVectors;
    }

    @Override
    public void reset() {
        isFullScreen = false;
        startupProgram = "";
        initialBoard = "";
        initialCharacter = "";
        gameOverProgram = "";
        resolutionWidth = 0;
        resolutionHeight = 0;
        projectIcon = "";
        showVectors = false;
    }

}
