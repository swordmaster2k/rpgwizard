/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.assets;

public class Program extends AbstractAsset {

	private final StringBuffer programBuffer;

	public Program(AssetDescriptor assetDescriptor) {
		super(assetDescriptor);
		programBuffer = new StringBuffer();
	}

	public String getFileName() {
		return descriptor.getURI().toString();
	}

	public StringBuffer getProgramBuffer() {
		return programBuffer;
	}

	public void update(String code) {
		programBuffer.delete(0, programBuffer.length());
		programBuffer.insert(0, code);
	}

}
