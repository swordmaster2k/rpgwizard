/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.ui;

import net.rpgtoolkit.common.assets.Animation;
import net.rpgtoolkit.common.assets.Asset;
import net.rpgtoolkit.common.assets.Board;
import net.rpgtoolkit.common.assets.Enemy;
import net.rpgtoolkit.common.assets.Item;
import net.rpgtoolkit.common.assets.Player;
import net.rpgtoolkit.common.assets.Project;
import net.rpgtoolkit.editor.editors.AnimationEditor;
import net.rpgtoolkit.editor.editors.BoardEditor;
import net.rpgtoolkit.editor.editors.CharacterEditor;
import net.rpgtoolkit.editor.editors.EnemyEditor;
import net.rpgtoolkit.editor.editors.ItemEditor;
import net.rpgtoolkit.editor.editors.ProjectEditor;

/**
 * A factory for obtaining the corresponding file format editor based on the
 * asset type.
 * 
 * @author Joshua Michael Daly
 */
public class EditorFactory {

	private EditorFactory() {

	}

	/**
	 * Gets the matching editor for the asset type.
	 * 
	 * @param asset
	 * @return editor for asset
	 */
	public static AssetEditorWindow getEditor(Asset asset) {
		if (asset instanceof Animation) {
			return new AnimationEditor((Animation) asset);
		} else if (asset instanceof Board) {
			return new BoardEditor((Board) asset);
		} else if (asset instanceof Enemy) {
			return new EnemyEditor((Enemy) asset);
		} else if (asset instanceof Item) {
			return new ItemEditor((Item) asset);
		} else if (asset instanceof Player) {
			return new CharacterEditor((Player) asset);
		} else if (asset instanceof Project) {
			return new ProjectEditor((Project) asset);
		}

		return null;
	}

}
