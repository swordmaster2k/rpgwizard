/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor;

import javax.swing.JFrame;
import org.assertj.swing.core.GenericTypeMatcher;
import static org.assertj.swing.finder.WindowFinder.findFrame;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JMenuItemFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.launcher.ApplicationLauncher;
import org.junit.Test;

/**
 * The purpose of this class is to enable automated testing of the SWING user
 * interface to ensure there is no regression of features.
 *
 * It uses AssertJ:
 *
 * http://joel-costigliola.github.io/assertj/assertj-swing-getting-started.html
 *
 * @author Joshua Michael Daly
 */
public class UserInterfaceTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;

    @Override
    protected void onSetUp() {
        ApplicationLauncher.application(Driver.class).start();

        GenericTypeMatcher<JFrame> matcher = new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return frame.getTitle() != null && frame.getTitle().startsWith("RPGWizard") && frame.isShowing();
            }
        };
        window = findFrame(matcher).using(robot());
    }


    @Test
    public void openProject() {
        JMenuItemFixture fileMenu = window.menuItem("fileMenu");
        fileMenu.click();
    }

}
