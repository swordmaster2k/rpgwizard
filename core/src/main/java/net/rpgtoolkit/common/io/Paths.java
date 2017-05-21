/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.io;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Chris Hutchinson <chris@cshutchinson.com>
 */
public final class Paths {

	private static final Pattern PATH_EXTENSION_PATTERN = Pattern
			.compile(".*/.*?(\\..*)");

	/**
	 * Extracts the file extension (if present) from a filesystem path.
	 *
	 * @param uri
	 *            file URI (e.g. file:\\\test.ext)
	 * @return file extension or empty string if not present
	 */
	public static String extension(final URI uri) {
		return Paths.extension(uri.getPath().toString());
	}

	/***
	 * Extracts the file extension (if present) from a filesystem path.
	 *
	 * @param path
	 *            path contents
	 * @return String if extension present, empty string otherwise
	 */
	public static String extension(String path) {
		final Matcher m = PATH_EXTENSION_PATTERN.matcher(path);
		if (m.matches()) {
			return m.group(1);
		}
		return "";
	}

	/***
	 * Extracts the filename from a URI, the name of the file with no path
	 * information.
	 *
	 * @param uri
	 *            file URI
	 * @return filename (with type extension)
	 */
	public static String filename(final URI uri) {
		final java.nio.file.Path path = java.nio.file.Paths.get(uri);
		final String filename = path.getFileName().toString();
		return filename;
	}

}
