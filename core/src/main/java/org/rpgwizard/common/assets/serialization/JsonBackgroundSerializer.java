/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.serialization;

import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.Background;
import org.rpgwizard.common.io.Paths;
import org.rpgwizard.common.utilities.CoreProperties;

import org.json.JSONObject;

/**
 * @author Chris Hutchinson
 */
public class JsonBackgroundSerializer extends AbstractJsonSerializer {

	@Override
	public boolean serializable(AssetDescriptor descriptor) {
		final String ext = Paths.extension(descriptor.getURI());
		return (ext.endsWith(CoreProperties
				.getFullExtension("toolkit.background.extension.json")));
	}

	@Override
	public boolean deserializable(AssetDescriptor descriptor) {
		return serializable(descriptor);
	}

	@Override
	protected void load(AssetHandle handle, JSONObject json)
			throws AssetException {

		final Background bkg = new Background(handle.getDescriptor());

		bkg.setBackgroundImage(AssetDescriptor.parse(json.optString("image")));
		bkg.setBackgroundMusic(AssetDescriptor.parse(json.optString("music")));

		if (json.has("sounds")) {
			final JSONObject sounds = json.getJSONObject("sounds");
			bkg.setSelectingSound(AssetDescriptor.parse(sounds
					.optString("selecting")));
			bkg.setSelectionSound(AssetDescriptor.parse(sounds
					.optString("selection")));
			bkg.setReadySound(AssetDescriptor.parse(sounds.optString("ready")));
			bkg.setInvalidSelectionSound(AssetDescriptor.parse(sounds
					.optString("invalid")));
		}

		handle.setAsset(bkg);

	}

	@Override
	protected void store(AssetHandle handle, JSONObject json)
			throws AssetException {

		final Background bkg = (Background) handle.getAsset();

		json.put("image", bkg.getBackgroundImage());
		json.put("music", bkg.getBackgroundMusic());

		final JSONObject sounds = new JSONObject();

		sounds.put("selecting", bkg.getSelectingSound());
		sounds.put("selection", bkg.getSelectionSound());
		sounds.put("ready", bkg.getReadySound());
		sounds.put("invalid", bkg.getInvalidSelectionSound());

		json.put("sounds", sounds);

	}

}
