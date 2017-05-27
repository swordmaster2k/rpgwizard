/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui;

import org.rpgwizard.common.assets.Animation;
import org.rpgwizard.common.assets.Asset;
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.assets.Enemy;
import org.rpgwizard.common.assets.Item;
import org.rpgwizard.common.assets.Player;
import org.rpgwizard.common.assets.Program;
import org.rpgwizard.common.assets.Project;
import org.rpgwizard.editor.editors.AnimationEditor;
import org.rpgwizard.editor.editors.BoardEditor;
import org.rpgwizard.editor.editors.CharacterEditor;
import org.rpgwizard.editor.editors.EnemyEditor;
import org.rpgwizard.editor.editors.ItemEditor;
import org.rpgwizard.editor.editors.ProgramEditor;
import org.rpgwizard.editor.editors.ProjectEditor;

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
		} else if (asset instanceof Program) {
			return new ProgramEditor((Program) asset);
		} else if (asset instanceof Project) {
			return new ProjectEditor((Project) asset);
		}

		return null;
	}

}
