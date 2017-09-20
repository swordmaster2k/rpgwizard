/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.serialization;

import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.Project;
import org.rpgwizard.common.io.Paths;
import org.rpgwizard.common.utilities.CoreProperties;

import org.json.JSONObject;

/**
 *
 * @author Joshua Michael Daly
 */
public class JsonProjectSerializer extends AbstractJsonSerializer {

	@Override
	public boolean serializable(AssetDescriptor descriptor) {
		final String ext = Paths.extension(descriptor.getURI().getPath());
		return (ext.contains(CoreProperties
				.getFullExtension("toolkit.project.extension.json")));
	}

	@Override
	public boolean deserializable(AssetDescriptor descriptor) {
		return serializable(descriptor);
	}

	@Override
	protected void load(AssetHandle handle, JSONObject json)
			throws AssetException {

		final Project project = new Project(handle.getDescriptor());

		project.setVersion(json.getDouble("version"));
		project.setName(json.getString("name"));
		project.setResolutionWidth(json.getInt("resolutionWidth"));
		project.setResolutionHeight(json.getInt("resolutionHeight"));
		project.setIsFullScreen(json.getBoolean("isFullScreen"));
		project.setInitialBoard(json.getString("initialBoard"));
		project.setInitialCharacter(json.getString("initialCharacter"));
		project.setStartupProgram(json.getString("startupProgram"));
		project.setGameOverProgram(json.getString("gameOverProgram"));

		handle.setAsset(project);
	}

	@Override
	protected void store(AssetHandle handle, JSONObject json)
			throws AssetException {
		super.store(handle, json);

		final Project project = (Project) handle.getAsset();

		json.put("name", project.getName());
		json.put("resolutionWidth", project.getResolutionWidth());
		json.put("resolutionHeight", project.getResolutionHeight());
		json.put("isFullScreen", project.isFullScreen());
		json.put("initialBoard", serializePath(project.getInitialBoard()));
		json.put("initialCharacter",
				serializePath(project.getInitialCharacter()));
		json.put("startupProgram", serializePath(project.getStartupProgram()));
		json.put("gameOverProgram", serializePath(project.getGameOverProgram()));
	}

}
