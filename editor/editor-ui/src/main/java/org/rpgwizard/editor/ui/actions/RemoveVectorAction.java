/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
// REFACTOR: FIX ME
/// **
// * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
// *
// * This Source Code Form is subject to the terms of the Mozilla Public
// * License, v. 2.0. If a copy of the MPL was not distributed with this
// * file, You can obtain one at http://mozilla.org/MPL/2.0/.
// */
// package org.rpgwizard.editor.ui.actions;
//
// import java.awt.event.ActionEvent;
// import javax.swing.AbstractAction;
// import org.rpgwizard.common.assets.map.MapVector;
// import org.rpgwizard.editor.editors.MapEditor;
//
/// **
// *
// * @author Joshua Michael Daly
// */
// public class RemoveVectorAction extends AbstractAction {
//
// private final MapEditor mapEditor;
// private final int x;
// private final int y;
// private final boolean deleteKey;
//
// public RemoveVectorAction(MapEditor mapEditor, int x, int y, boolean deleteKey) {
// this.mapEditor = mapEditor;
// this.x = x;
// this.y = y;
// this.deleteKey = deleteKey;
// }
//
// @Override
// public void actionPerformed(ActionEvent e) {
// MapVector removed;
//
// if (deleteKey && mapEditor.getSelectedObject() instanceof MapVector) {
// MapVector selected = (MapVector) mapEditor.getSelectedObject();
// removed = mapEditor.getMapView().getCurrentSelectedLayer().getLayer().removeVector(selected);
// } else {
// removed = mapEditor.getMapView().getCurrentSelectedLayer().getLayer().removeVectorAt(x, y);
// }
//
// if (removed != null && removed == mapEditor.getSelectedObject()) {
// mapEditor.getSelectedObject().setSelectedState(false);
// mapEditor.setSelectedObject(null);
// }
// }
//
// }
